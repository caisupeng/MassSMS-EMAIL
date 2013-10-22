package com.hanji.groupsending.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.telephony.SmsManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hanji.groupsending.ContactBean;
import com.hanji.groupsending.R;

/**
 * Send SMS And Count Something In This Page
 * 
 * @date 2013-10-19
 */
public class SMSSendCounting extends ActionBarActivity {
	private ArrayList<ContactBean> mSMSContacts;
	private String mContent;
	private ProgressBar mLoadingBar;
	private ImageView mSmileFace;
	private TextView mTip;
	private TextView mSuccess;
	private TextView mFail;
	private TextView mTotal;
	private int mCountingSuccess = 0;
	private int mCountingFail = 0;
	private final String ACTION_SENDSMS = "sendsms";
	private final String ACTION_DELIVEREDSMS = "deliveredsms";
	private int mlength;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.smssend_counting_layout);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		/* Load Views */
		mLoadingBar = (ProgressBar) this.findViewById(R.id.loading_bar);
		mSmileFace = (ImageView) this.findViewById(R.id.iv_sendsms_smileface);
		mTip = (TextView) this.findViewById(R.id.tv_tip);
		mSuccess = (TextView) this.findViewById(R.id.tv_smssend_success);
		mFail = (TextView) this.findViewById(R.id.tv_smssend_fail);
		mTotal = (TextView) this.findViewById(R.id.tv_sms_sendtotal);

		/* Get Parameters */
		mSMSContacts = (ArrayList<ContactBean>) getIntent().getExtras().getSerializable("checked");
		mContent = getIntent().getStringExtra("content");

		for (ContactBean mContactBean : mSMSContacts) {
			System.out.println(mContactBean.toString());
		}
		System.out.println(mContent);

		registerReceiver(sendReceiver, new IntentFilter(ACTION_SENDSMS));
		registerReceiver(deliveryReceiver, new IntentFilter(ACTION_DELIVEREDSMS));

		new Thread() {
			@Override
			public void run() {
				mlength = mSMSContacts.size();

				for (int i = 0; i < mlength; i++) {
					String number = mSMSContacts.get(i).getNumber();
					sendSMSMessage(number, mContent);
				}
			}
		}.start();

	}

	private void sendSMSMessage(String number, String message) {

		try {
			SmsManager manager = SmsManager.getDefault();
			Intent send = new Intent(ACTION_SENDSMS);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0,
					send, 0);
			// Here just in case of forgotten, actually useless in this APP
			// deliveryIntent can be replace by null
			Intent delivery = new Intent(ACTION_DELIVEREDSMS);
			PendingIntent deliveryIntent = PendingIntent.getBroadcast(getApplicationContext(), 0,
					delivery, 0);
			List<String> texts = manager.divideMessage(message);
			for (String text : texts) {
				manager.sendTextMessage(number, null, text, pendingIntent, deliveryIntent);
			}
			// Thread.sleep(100);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Save into System message database
		try {
			ContentValues values = new ContentValues();
			values.put("address", number);
			values.put("body", mContent);
			getContentResolver().insert(Uri.parse("content://sms/sent"), values);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private BroadcastReceiver sendReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			switch (getResultCode()) {
			case Activity.RESULT_OK:
				mCountingSuccess++;
				break;
			default:
				mCountingFail++;
				break;
			}
			mSuccess.setText(String.valueOf(mCountingSuccess));
			mFail.setText(String.valueOf(mCountingFail));
			mTotal.setText(String.valueOf((mCountingSuccess + mCountingFail)));
			if (mlength == mCountingSuccess + mCountingFail) {
				mTip.setText("发送结束");
				mLoadingBar.setVisibility(View.GONE);
				mSmileFace.setVisibility(View.VISIBLE);
			}
		}
	};

	private BroadcastReceiver deliveryReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// UNUESD
			// System.out.println("对方成功收到短信");
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(sendReceiver);
		unregisterReceiver(deliveryReceiver);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

}
