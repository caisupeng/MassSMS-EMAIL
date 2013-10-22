package com.hanji.groupsending.ui.fragment;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hanji.groupsending.Constants;
import com.hanji.groupsending.ContactBean;
import com.hanji.groupsending.MyApplication;
import com.hanji.groupsending.R;
import com.hanji.groupsending.adapter.SelectedContactsAdapter;
import com.hanji.groupsending.ui.activity.SMSSendCounting;

/**
 * Send Grouping SMS Page
 * 
 * @date 2013-10-15
 */
public class SendSMSFragment extends Fragment {

	private TextView mCounting;
	private TextView mChooseNone;
	private GridView mSelectedNames;
	private SelectedContactsAdapter mNamesAdpater;
	private Button mButtonSend;
	private TextView mContentCounting;
	private EditText mSMSContent;
	private ArrayList<ContactBean> mChecked;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.sendsms_layout, null);
		mCounting = (TextView) view.findViewById(R.id.peopleCount);
		mChooseNone = (TextView) view.findViewById(R.id.tv_sms_choosenone);
		mSelectedNames = (GridView) view.findViewById(R.id.gv_sms_selected);
		mButtonSend = (Button) view.findViewById(R.id.bt_sms_bottom);
		mContentCounting = (TextView) view.findViewById(R.id.tv_smscontent_counting);
		mSMSContent = (EditText) view.findViewById(R.id.et_smscontent);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		ViewGroup.LayoutParams params = mButtonSend.getLayoutParams();
		ViewGroup.LayoutParams gridPrams = mSelectedNames.getLayoutParams();
		gridPrams.height = (int) (params.height * 3 + 6);
		mSelectedNames.setLayoutParams(gridPrams);
		mSelectedNames.setVisibility(View.GONE);

		mSMSContent.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// UNUSED
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// UNUSED
			}

			@Override
			public void afterTextChanged(Editable s) {

				String content = mSMSContent.getText().toString();
				// Nothing
				if (content.length() == 0) {
					mContentCounting.setText("");
					return;
				}
				// Judge Whether has Chinese Char
				boolean isHaveChinese = isHaveChinese(content);

				if (isHaveChinese) {

					mContentCounting.setText("(" + mSMSContent.getText().length() + "/"
							+ ((content.length() / 70 + 1) * 70) + ","
							+ (content.length() / 70 + 1) + "条)");

				} else {

					mContentCounting.setText("(" + mSMSContent.getText().length() + "/"
							+ ((content.length() / 160 + 1) * 160) + ","
							+ (content.length() / 160 + 1) + "条)");
				}
			}
		});

		mButtonSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mChecked.size() == 0) {
					Toast.makeText(getActivity(), "请选择收件人", Toast.LENGTH_SHORT).show();
					return;
				}
				if (mSMSContent.getText().toString().equals("")) {
					Toast.makeText(getActivity(), "请输入正文", Toast.LENGTH_SHORT).show();
					return;
				}

				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle("友情提示");
				builder.setMessage("你确定要群发短信?");
				builder.setNegativeButton("取消", null);
				builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(getActivity(), SMSSendCounting.class);
						intent.putExtra("checked", mChecked);
						intent.putExtra("content", mSMSContent.getText().toString());
						startActivity(intent);
					}
				});
				builder.show();

			}
		});

		mChecked = new ArrayList<ContactBean>();
	}

	private boolean isHaveChinese(String str) {
		for (int i = 0; i < str.length(); i++) {
			char ss = str.charAt(i);
			boolean s = String.valueOf(ss).matches("[\u4e00-\u9fa5]");
			if (s) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case Constants.REQUESTCODE_SMS_CONTACTS:
			if (resultCode == Constants.RESULTCODE_SMS_CONTACTS) {
				System.out.println("Selectet PhoneNum Address Success!");
				// TODO
				mChecked = new ArrayList<ContactBean>();
				for (ContactBean it : MyApplication.mSMSList) {
					if (it.isChecked()) {
						mChecked.add(it);
					}
				}
				showSelectedNames(mChecked);
			}
			break;

		default:
			break;
		}
	}

	public void showSelectedNames(ArrayList<ContactBean> list) {
		if (list.size() != 0) {
			mChooseNone.setVisibility(View.GONE);
			mCounting.setText("(" + list.size() + ")");
			mSelectedNames.setVisibility(View.VISIBLE);
		} else {
			mChooseNone.setVisibility(View.VISIBLE);
			mCounting.setText("(0)");
			mSelectedNames.setVisibility(View.GONE);
			return;
		}
		mNamesAdpater = new SelectedContactsAdapter(getActivity(), list);
		mSelectedNames.setAdapter(mNamesAdpater);
	}

	public void deleteThis(View view) {
		// TODO
		if (view instanceof ImageView) {
			ImageView iv = (ImageView) view;
			ContactBean contactBean = (ContactBean) iv.getTag();
			// For Recovering CheckBox State
			MyApplication.mSMSList.get(MyApplication.mSMSList.indexOf(contactBean)).setChecked(
					false);
			ArrayList<ContactBean> checked = new ArrayList<ContactBean>();
			for (ContactBean it : MyApplication.mSMSList) {
				if (it.isChecked()) {
					checked.add(it);
				}
			}
			showSelectedNames(checked);

			// Update About to Send Bean List
			mChecked = checked;
		}
	}
}
