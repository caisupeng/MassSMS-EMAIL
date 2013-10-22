package com.hanji.groupsending;

import java.util.ArrayList;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Data;

/**
 * Custom Application exits in the whole life cycle
 * 
 * @date 2013-10-16
 */
public class MyApplication extends Application {

	private static Context mContext;
	public static ArrayList<ContactBean> mSMSList;
	public static ArrayList<ContactBean> mEmailList;

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = this.getApplicationContext();
		mSMSList = new ArrayList<ContactBean>();
		mEmailList = new ArrayList<ContactBean>();
	}

	public static ArrayList<ContactBean> queryContacts(boolean isPhone) {
		ArrayList<ContactBean> result = new ArrayList<ContactBean>();

		ContentResolver cr = mContext.getContentResolver();

		Uri uri = ContactsContract.Contacts.CONTENT_URI;
		String[] projection = new String[] { ContactsContract.Contacts._ID,
				ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.HAS_PHONE_NUMBER };
		String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
		Cursor cursor = cr.query(uri, projection, null, null, sortOrder);
		try {
			cursor.moveToFirst();
			if (cursor.getCount() > 0) {
				final int nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
				final int idIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID);
				final int numberIndex = cursor
						.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
				if (isPhone) {
					// Query Phone
					do {
						String name = cursor.getString(nameIndex);
						String id = "" + cursor.getLong(idIndex);
						String number = cursor.getString(numberIndex);
						if (number.equals("1")) {
							result.addAll(getPhone(name, id));
						}

					} while (cursor.moveToNext());

					mSMSList = result;

				} else {
					// Query Email
					do {
						String name = cursor.getString(nameIndex);
						String id = "" + cursor.getLong(idIndex);

						result.addAll(getEmail(name, id));

					} while (cursor.moveToNext());

					mEmailList = result;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return result;
	}

	private static ArrayList<ContactBean> getEmail(String name, String id) {
		ArrayList<ContactBean> result = new ArrayList<ContactBean>();

		ContentResolver cr = mContext.getContentResolver();
		Uri uri = Uri.parse("content://com.android.contacts/contacts/" + id + "/data");
		Cursor cursor = cr.query(uri, new String[] { Data.DATA1, Data.MIMETYPE }, null, null, null);
		try {
			cursor.moveToFirst();
			if (cursor.getCount() > 0) {
				do {
					String data = cursor.getString(0);
					String type = cursor.getString(1);

					// Add Email
					if ("vnd.android.cursor.item/email_v2".equals(type)) {
						ContactBean emailBean = new ContactBean();
						emailBean.setName(name);
						emailBean.setId(id);
						emailBean.setEmail(data);
						result.add(emailBean);
					}
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}

		return result;
	}

	private static ArrayList<ContactBean> getPhone(String name, String id) {
		ArrayList<ContactBean> result = new ArrayList<ContactBean>();

		ContentResolver cr = mContext.getContentResolver();
		Uri uri = Uri.parse("content://com.android.contacts/contacts/" + id + "/data");
		Cursor cursor = cr.query(uri, new String[] { Data.DATA1, Data.MIMETYPE }, null, null, null);
		try {
			cursor.moveToFirst();
			if (cursor.getCount() > 0) {
				do {
					String data = cursor.getString(0);
					String type = cursor.getString(1);

					if ("vnd.android.cursor.item/phone_v2".equals(type)) {
						// Add Phone Number

						ContactBean phoneContact = new ContactBean();
						phoneContact.setName(name);
						phoneContact.setId(id);
						phoneContact.setNumber(data);
						result.add(phoneContact);
					}
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}

		return result;
	}
}
