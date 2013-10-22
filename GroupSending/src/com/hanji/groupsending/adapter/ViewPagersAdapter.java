package com.hanji.groupsending.adapter;

import com.hanji.groupsending.ui.fragment.SendEmailFragment;
import com.hanji.groupsending.ui.fragment.SendSMSFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ViewPagersAdapter extends FragmentPagerAdapter {

	private final int mCount = 2;

	public ViewPagersAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		switch (position) {
		case 0:
			return new SendSMSFragment();

		case 1:
			return new SendEmailFragment();

		default:
			break;
		}

		return null;
	}

	@Override
	public int getCount() {
		return mCount;
	}
}
