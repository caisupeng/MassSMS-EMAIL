package com.hanji.groupsending.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBar.TabListener;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.hanji.groupsending.Constants;
import com.hanji.groupsending.R;
import com.hanji.groupsending.adapter.ViewPagersAdapter;
import com.hanji.groupsending.ui.fragment.SendEmailFragment;
import com.hanji.groupsending.ui.fragment.SendSMSFragment;

/**
 * Main Page
 * 
 * @date 2013-10-14
 */
public class MainActivity extends ActionBarActivity implements TabListener {

	private long exitTime = 0;
	private ViewPager mViewPager;
	private static final String TAB_INDEX = "tab_index";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final ActionBar actionBar = getSupportActionBar();

		/* ViewPager Related */
		mViewPager = (ViewPager) this.findViewById(R.id.viewpagers);
		mViewPager.setAdapter(new ViewPagersAdapter(getSupportFragmentManager()));
		ViewPager.SimpleOnPageChangeListener pageChangeListener = new SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				super.onPageSelected(position);
				actionBar.setSelectedNavigationItem(position);
			}
		};
		mViewPager.setOnPageChangeListener(pageChangeListener);
		mViewPager.setPageMarginDrawable(R.drawable.grey_border_inset_lr);
		mViewPager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.page_margin_width));

		/* ActionBar TABS Related */
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.addTab(actionBar.newTab().setText(getString(R.string.send_sms))
				.setTabListener(this));
		actionBar.addTab(actionBar.newTab().setText(getString(R.string.send_email))
				.setTabListener(this));

		/* Recover */
		if (savedInstanceState != null) {
			actionBar.setSelectedNavigationItem(savedInstanceState.getInt(TAB_INDEX, 0));
		}
	}

	@Override
	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
		// UNUSED
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction arg1) {
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
		// UNUSED
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(TAB_INDEX, getSupportActionBar().getSelectedNavigationIndex());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// System.out.println("onCreateOptionsMenu");
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == R.id.action_contacts) {
			if (mViewPager.getCurrentItem() == 0) {
				Intent intent = new Intent(getApplicationContext(), SelectSMSContacts.class);
				startActivityForResult(intent, Constants.REQUESTCODE_SMS_CONTACTS);
			} else {
				// TODO
				Intent intent = new Intent(getApplicationContext(), SelectEmialContacts.class);
				startActivityForResult(intent, Constants.REQUESTCODE_EMAIL_CONTACTS);
			}
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * Relay
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case Constants.REQUESTCODE_SMS_CONTACTS:
			if (resultCode == Constants.RESULTCODE_SMS_CONTACTS) {
				getSupportFragmentManager().findFragmentByTag(
						"android:switcher:" + R.id.viewpagers + ":" + mViewPager.getCurrentItem())
						.onActivityResult(requestCode, resultCode, data);
			}
			break;
		case Constants.REQUESTCODE_EMAIL_CONTACTS:
			if (resultCode == Constants.RESULTCODE_EMAIL_CONTACTS) {
				getSupportFragmentManager().findFragmentByTag(
						"android:switcher:" + R.id.viewpagers + ":" + mViewPager.getCurrentItem())
						.onActivityResult(requestCode, resultCode, data);
			}
			break;

		default:
			break;
		}
	}

	/**
	 * Relay:dispatch onClick method of delete image in SMS selected names
	 */
	public void deleteThis(View view) {
		SendSMSFragment fragment = (SendSMSFragment) getSupportFragmentManager().findFragmentByTag(
				"android:switcher:" + R.id.viewpagers + ":" + mViewPager.getCurrentItem());
		fragment.deleteThis(view);
	}

	/**
	 * Relay:dispatch onClick method of delete image in EMAIL selected names
	 */
	public void emailDeleteThis(View view) {
		SendEmailFragment fragment = (SendEmailFragment) getSupportFragmentManager()
				.findFragmentByTag(
						"android:switcher:" + R.id.viewpagers + ":" + mViewPager.getCurrentItem());
		fragment.emailDeleteThis(view);
	}

	/**
	 * Push again to exit
	 */
	@Override
	public void onBackPressed() {
		if ((System.currentTimeMillis() - exitTime) > 2000) {
			Toast.makeText(getApplicationContext(), getString(R.string.push_again_exit),
					Toast.LENGTH_SHORT).show();
			exitTime = System.currentTimeMillis();
		} else {
			finish();
			System.exit(0);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		supportInvalidateOptionsMenu();
	}
}
