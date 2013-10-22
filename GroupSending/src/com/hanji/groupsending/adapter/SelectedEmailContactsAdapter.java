package com.hanji.groupsending.adapter;

import java.util.ArrayList;

import com.hanji.groupsending.ContactBean;
import com.hanji.groupsending.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;

public class SelectedEmailContactsAdapter extends BaseAdapter {

	private ArrayList<ContactBean> names;
	private LayoutInflater mInflater;

	public SelectedEmailContactsAdapter(Context ct, ArrayList<ContactBean> names) {
		mInflater = LayoutInflater.from(ct);
		this.names = names;
	}

	@Override
	public int getCount() {
		return names.size();
	}

	@Override
	public Object getItem(int position) {
		return names.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ContactBean itemContactBean = names.get(position);
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.selected_emailnames_item, null);
			viewHolder = new ViewHolder();
			viewHolder.pickPeople = (Button) convertView
					.findViewById(R.id.bt_selectedemailname_item);
			viewHolder.deleteImg = (ImageView) convertView
					.findViewById(R.id.iv_selectedemailname_item);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.deleteImg.setTag(itemContactBean);
		viewHolder.pickPeople.setTag(itemContactBean);
		viewHolder.pickPeople.setText(itemContactBean.getName());

		return convertView;
	}

	private static class ViewHolder {
		public Button pickPeople;
		public ImageView deleteImg;
	}

}
