package com.hanji.groupsending.ui.activity;

import java.util.ArrayList;

import com.hanji.groupsending.Constants;
import com.hanji.groupsending.ContactBean;
import com.hanji.groupsending.MyApplication;
import com.hanji.groupsending.R;
import com.hanji.groupsending.adapter.EmailContactsAdapter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.AdapterView.OnItemClickListener;

public class SelectEmialContacts extends ActionBarActivity {

	private ProgressBar mProgressBar;
	private ListView mListView;
	private Button mBtEnsure;
	private EmailContactsAdapter mAdapter;
	private boolean mLoaded = false;
	private ArrayList<ContactBean> list;
	private MyReceiver mReceiver;
	private int mCount;
	private ArrayList<ContactBean> mChecked;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_emailcontacts_layout);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		/* Load Views */
		mListView = (ListView) this.findViewById(R.id.lv_emailcontacts);
		mBtEnsure = (Button) this.findViewById(R.id.bt_emailcontacts_add);
		mProgressBar = (ProgressBar) this.findViewById(R.id.loadingbar_emailcontacts);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				CheckBox checkBox = (CheckBox) view.findViewById(R.id.item_checkbox);
				ContactBean contactBean = (ContactBean) checkBox.getTag();
				contactBean.setChecked(!contactBean.isChecked());
				// For Changing CheckBox State
				mAdapter.notifyDataSetChanged();

				if (checkBox.isChecked()) {
					// TODO
					mCount--;
					MyApplication.mEmailList.get(position).setChecked(false);
					sendBroadcast(new Intent(Constants.INTENT_FILTER_EMAIL_SELECTED));

				} else {
					// TODO
					mCount++;
					MyApplication.mEmailList.get(position).setChecked(true);
					sendBroadcast(new Intent(Constants.INTENT_FILTER_EMAIL_SELECTED));
				}
			}
		});
		mBtEnsure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setResult(Constants.RESULTCODE_EMAIL_CONTACTS);
				// TODO
				finish();
			}
		});
		mChecked = new ArrayList<ContactBean>();
		new LoadContacts().execute();

		/* Receive Broadcast */
		IntentFilter filter = new IntentFilter(Constants.INTENT_FILTER_EMAIL_SELECTED);
		mReceiver = new MyReceiver();
		registerReceiver(mReceiver, filter);
	}

	private class LoadContacts extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {

			if (MyApplication.mEmailList.size() != 0) {
				list = MyApplication.mEmailList;
			} else {
				list = MyApplication.queryContacts(false);
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			mAdapter = new EmailContactsAdapter(getApplicationContext(), list);

			mListView.setAdapter(mAdapter);
			mProgressBar.setVisibility(View.GONE);

			// TODO
			for (ContactBean it : MyApplication.mEmailList) {
				if (it.isChecked()) {
					mChecked.add(it);
					mCount++;
				}
			}

			/* Recover Counting */
			if (mCount != 0) {
				mBtEnsure.setText("添加联系人(" + mCount + ")");
			}

			mLoaded = true;
			super.onPostExecute(result);
		}
	}

	private class MyReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO
			if (mCount == 0) {
				mBtEnsure.setText("添加联系人");
			} else {
				mBtEnsure.setText("添加联系人(" + mCount + ")");
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == android.R.id.home) {
			recover();
			finish();
			return true;
		}

		if (item.getItemId() == R.id.action_select_all) {
			if (mLoaded) {

				mCount = 0;
				for (ContactBean it : MyApplication.mEmailList) {
					it.setChecked(true);
					mCount++;
				}
				mAdapter.notifyDataSetChanged();
				mBtEnsure.setText("添加联系人(" + mCount + ")");

			}
			return true;
		}

		if (item.getItemId() == R.id.action_select_clear) {
			if (mLoaded) {

				for (ContactBean it : MyApplication.mEmailList) {
					it.setChecked(false);
				}
				mAdapter.notifyDataSetChanged();
				mCount = 0;
				mBtEnsure.setText("添加联系人");
			}
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		recover();
		finish();
	}

	private void recover() {
		// Exclude not already checked
		for (ContactBean it : MyApplication.mEmailList) {
			if (!mChecked.contains(it)) {
				it.setChecked(false);
			}
		}
		// Recover checked
		for (ContactBean it : mChecked) {
			it.setChecked(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.select_contacts, menu);
		return true;
	}
}
