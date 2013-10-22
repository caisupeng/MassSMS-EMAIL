package com.hanji.groupsending.adapter;

import java.util.ArrayList;

import com.hanji.groupsending.ContactBean;
import com.hanji.groupsending.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * Adapter For ListView Showing Email Contacts
 * 
 * @date 2013-10-17
 */
public class EmailContactsAdapter extends BaseAdapter {
	// private Context mContext;
	private ArrayList<ContactBean> mList;
	private LayoutInflater mLayoutInflater;
	private ViewHolder mHolder;

	public EmailContactsAdapter(Context context, ArrayList<ContactBean> list) {
		// this.mContext = context;
		this.mList = list;
		mLayoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int positon) {
		return positon;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.contacts_item_layout, parent, false);
			mHolder = new ViewHolder();
			mHolder.nameTV = (TextView) convertView.findViewById(R.id.item_name);
			mHolder.emailTV = (TextView) convertView.findViewById(R.id.item_contact);
			mHolder.checkBox = (CheckBox) convertView.findViewById(R.id.item_checkbox);
			convertView.setTag(mHolder);
		} else {
			mHolder = (ViewHolder) convertView.getTag();
		}

		/* Fetch data-set of this line */
		ContactBean contact = (ContactBean) getItem(position);
		mHolder.checkBox.setTag(contact);
		/* Fill in parameters for this line */
		mHolder.checkBox.setChecked(contact.isChecked());
		mHolder.nameTV.setText(contact.getName());
		mHolder.emailTV.setText(contact.getEmail());

		return convertView;
	}

	private static class ViewHolder {
		public TextView nameTV;
		public TextView emailTV;
		public CheckBox checkBox;
	}

	public ArrayList<ContactBean> getList() {
		return mList;
	}
}
