/**
 * ImageItemAdapter.java
 * DragGridView的适配器，根据父控件大小计算item高宽
 * @author liangzz
 * 2014-12-2
 */
package com.canace.mybaby.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;

import com.canace.mybaby.R;
import com.canace.mybaby.cache.loader.ImageLoader;
import com.canace.mybaby.db.model.ImageItem;
import com.canace.mybaby.utils.CommonsUtil;
import com.canace.mybaby.utils.PreferenceUtils;

public class ImageItemAdapter extends LoaderAdapter {
	private ImageItem[] myImageItems;
	private final Context mContext;
	private final int parentHeight;
	private final int parentWidth;
	private final LayoutInflater mInflater;
	public static int mHidePosition = -1;
	private final AbsListView.LayoutParams params;

	public ImageItemAdapter(Context context, int width, int height,
			ImageItem[] myImageItems) {

		this.myImageItems = myImageItems;
		mContext = context;
		parentHeight = height;
		parentWidth = width;
		if (PreferenceUtils.getIntValue(PreferenceUtils.GRID_WIDTH) < 1) {
			PreferenceUtils.saveIntValue(PreferenceUtils.GRID_WIDTH,
					parentWidth / 3);
			if (CommonsUtil.getStatusBarHeight(mContext) > 0) {

				PreferenceUtils.saveIntValue(PreferenceUtils.GRID_HEIGHT,
						(parentHeight - 80) / 5);
			} else {
				PreferenceUtils.saveIntValue(PreferenceUtils.GRID_HEIGHT,
						(parentHeight - 50 - 80) / 5);
			}
		}
		params = new AbsListView.LayoutParams(
				PreferenceUtils.getIntValue(PreferenceUtils.GRID_WIDTH),
				PreferenceUtils.getIntValue(PreferenceUtils.GRID_HEIGHT));
		mInflater = (LayoutInflater) mContext
				.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
		mImageLoader = new ImageLoader(context);
	}

	@Override
	public int getCount() {

		return myImageItems.length;
	}

	@Override
	public Object getItem(int position) {

		return myImageItems[position];
	}

	@Override
	public long getItemId(int position) {

		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ItemViewHolder itemViewHolder = null;
		if (null == convertView) {
			convertView = mInflater.inflate(R.layout.image_item, null);
			convertView.setLayoutParams(params);
			itemViewHolder = new ItemViewHolder();
			itemViewHolder.image = (ImageView) convertView
					.findViewById(R.id.category_image);
			convertView.setTag(R.id.tag_category_item, itemViewHolder);
		} else {
			itemViewHolder = (ItemViewHolder) convertView
					.getTag(R.id.tag_category_item);
		}

		itemViewHolder.image.setImageResource(R.drawable.no_pic);
		if (myImageItems[position] != null) {
			if (myImageItems[position].getImagePath() != null) {

				mImageLoader.displayImage(
						myImageItems[position].getImagePath(),
						itemViewHolder.image, false);
			}

		}

		if (position == mHidePosition) {
			convertView.setVisibility(View.INVISIBLE);
		}

		return convertView;
	}

	private class ItemViewHolder {
		ImageView image;
	}

	/**
	 * 设置某项隐藏
	 * 
	 * @param position
	 */
	public void setItemHide(int position) {
		ImageItemAdapter.mHidePosition = position;
		notifyDataSetChanged();
	}

	public void setDataList(ImageItem[] myImageItems) {

		this.myImageItems = myImageItems;
	}

}
