package com.canace.mybaby.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.canace.mybaby.cache.loader.ImageLoader;

public class LoaderAdapter extends BaseAdapter {

	private static final String TAG = "LoaderAdapter";
	protected boolean mBusy = false;

	public void setFlagBusy(boolean busy) {
		this.mBusy = busy;
	}

	protected ImageLoader mImageLoader;

	public ImageLoader getImageLoader() {
		return mImageLoader;
	}

	@Override
	public int getCount() {
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		return convertView;
	}
}
