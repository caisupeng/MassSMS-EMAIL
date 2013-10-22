package com.hanji.groupsending.ui.fragment;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.hanji.groupsending.adapter.SelectedEmailContactsAdapter;

/**
 * Send Email Page
 * 
 * @date 2013-10-20
 */
public class SendEmailFragment extends Fragment {

	private TextView mSelectedCounting;
	private TextView mChooseNone;
	private GridView mSelectedNames;
	private SelectedEmailContactsAdapter mNamesAdpater;
	private Button mButtonSend;
	// private TextView mContentCounting;
	private EditText mEmailContent;
	private ArrayList<ContactBean> mChecked;
	private EditText mEmailSubject;
	private String mAttachmentPath = "";
	private TextView mTVAttach;
	private ImageView mIVAttach;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.attach, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_attach:
			System.out.println("附件");
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("*/*");
			intent.addCategory(Intent.CATEGORY_OPENABLE);
			try {
				startActivityForResult(Intent.createChooser(intent, "选择附件"),
						Constants.REQUESTCODE_SELECT_ATTACH);
			} catch (ActivityNotFoundException ex) {
				Toast.makeText(getActivity(), "对不起,你还没有文件管理应用,快去下载安装后再试吧 !", Toast.LENGTH_SHORT)
						.show();
				// Here we can give user a link
			}

			return true;

		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		// super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void onDestroyOptionsMenu() {
		// super.onDestroyOptionsMenu();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.sendemail_layout, null);
		mSelectedCounting = (TextView) view.findViewById(R.id.tv_email_selectedcounting);
		mChooseNone = (TextView) view.findViewById(R.id.tv_email_choosenone);
		mSelectedNames = (GridView) view.findViewById(R.id.gv_email_selected);
		mButtonSend = (Button) view.findViewById(R.id.bt_email_bottom);
		mEmailContent = (EditText) view.findViewById(R.id.et_emailcontent);
		mEmailSubject = (EditText) view.findViewById(R.id.et_emailsubject);
		mTVAttach = (TextView) view.findViewById(R.id.tv_email_attach);
		mIVAttach = (ImageView) view.findViewById(R.id.iv_email_attachment);

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

		mChecked = new ArrayList<ContactBean>();

		mButtonSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mChecked.size() == 0) {
					Toast.makeText(getActivity(), "请选择收件人!", Toast.LENGTH_SHORT).show();
					return;
				}
				if (mEmailSubject.getText().toString().equals("")) {
					Toast.makeText(getActivity(), "请输入邮件主题!", Toast.LENGTH_SHORT).show();
					return;
				}
				if (mEmailContent.getText().toString().equals("")) {
					Toast.makeText(getActivity(), "请输入邮件正文!", Toast.LENGTH_SHORT).show();
					return;
				}

				String message = "";
				if (mAttachmentPath.equals("")) {
					message = "确定发送邮件 ?";
				} else {
					message = "确定发送带附件的邮件 ?";
				}

				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle("友情提示");
				builder.setMessage(message);
				builder.setNegativeButton("取消", null);
				builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						int length = mChecked.size();
						String[] address = new String[length];
						for (int i = 0; i < length; i++) {
							address[i] = mChecked.get(i).getEmail();
						}

						/*
						 * The easiest way is to use an Intent to accomplish
						 * sending email. Otherwise, we can use SMTP method and
						 * that's too complex and fallibility(Other Reference:
						 * https://code.google.com/p/javamail-android/)
						 */
						Intent i = new Intent(Intent.ACTION_SEND);
						i.setType("message/rfc822");
						i.putExtra(Intent.EXTRA_EMAIL, address);
						i.putExtra(Intent.EXTRA_SUBJECT, mEmailSubject.getText().toString());
						i.putExtra(Intent.EXTRA_TEXT, mEmailContent.getText().toString());
						if (!mAttachmentPath.equals("")) {
							// File file = new
							// File(mAttachmentPath.split("mnt")[1]);
							File file = new File(mAttachmentPath);
							i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
						}

						try {
							startActivity(Intent.createChooser(i, "发送邮件"));
						} catch (android.content.ActivityNotFoundException ex) {
							Toast.makeText(getActivity(), "抱歉,您的手机没有邮件客户端,快去安装一个再试吧...",
									Toast.LENGTH_SHORT).show();
							// Here we can give user a link to download
						}

					}
				});
				builder.show();
			}
		});
		mIVAttach.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Click to clear attachment
				mAttachmentPath = "";
				mTVAttach.setVisibility(View.INVISIBLE);
				mIVAttach.setVisibility(View.INVISIBLE);
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case Constants.REQUESTCODE_EMAIL_CONTACTS:
			if (resultCode == Constants.RESULTCODE_EMAIL_CONTACTS) {

				// System.out.println("Selectet Email Address Success!");

				mChecked = new ArrayList<ContactBean>();
				for (ContactBean it : MyApplication.mEmailList) {
					if (it.isChecked()) {
						mChecked.add(it);
					}
				}
				showSelectedNames(mChecked);
			}
			break;

		case Constants.REQUESTCODE_SELECT_ATTACH:
			if (resultCode == Activity.RESULT_OK) {
				if (data != null) {
					if (data.getData() != null) {
						// Here get the path of the file selected
						Uri uri = data.getData();
						try {
							mAttachmentPath = getPath(getActivity(), uri);

						} catch (URISyntaxException e) {
							e.printStackTrace();
						}

						// System.out.println(mAttachmentPath);
					}
				} else {
					// System.out.println("data null");
				}

				if (!mAttachmentPath.equals("")) {
					mTVAttach.setVisibility(View.VISIBLE);
					mIVAttach.setVisibility(View.VISIBLE);
				}

			}

		default:
			break;
		}
	}

	private String getPath(Context context, Uri uri) throws URISyntaxException {

		if ("content".equalsIgnoreCase(uri.getScheme())) {
			String[] projection = { "_data" };
			Cursor cursor = null;

			try {
				cursor = context.getContentResolver().query(uri, projection, null, null, null);
				int column_index = cursor.getColumnIndexOrThrow("_data");
				if (cursor.moveToFirst()) {
					return cursor.getString(column_index);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
	}

	public void showSelectedNames(ArrayList<ContactBean> list) {
		if (list.size() != 0) {
			mChooseNone.setVisibility(View.GONE);
			mSelectedCounting.setText("(" + list.size() + ")");
			mSelectedNames.setVisibility(View.VISIBLE);
		} else {
			mChooseNone.setVisibility(View.VISIBLE);
			mSelectedCounting.setText("(0)");
			mSelectedNames.setVisibility(View.GONE);
			return;
		}
		mNamesAdpater = new SelectedEmailContactsAdapter(getActivity(), list);
		mSelectedNames.setAdapter(mNamesAdpater);
	}

	public void emailDeleteThis(View view) {
		if (view instanceof ImageView) {
			ImageView iv = (ImageView) view;
			ContactBean contactBean = (ContactBean) iv.getTag();
			// For Recovering CheckBox State
			MyApplication.mEmailList.get(MyApplication.mEmailList.indexOf(contactBean)).setChecked(
					false);
			ArrayList<ContactBean> checked = new ArrayList<ContactBean>();
			for (ContactBean it : MyApplication.mEmailList) {
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
