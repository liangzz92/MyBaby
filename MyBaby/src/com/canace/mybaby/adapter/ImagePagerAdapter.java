/**
 * ImagePagerAdapter.java
 * ImageViewPager的适配器，在remove页卡时释放资源
 * @author liangzz
 * 2014-12-2
 */
package com.canace.mybaby.adapter;

import java.util.List;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.canace.mybaby.activity.HomePageActivity;
import com.canace.mybaby.db.model.ImageItem;
import com.canace.mybaby.utils.BitmapHelper;
import com.canace.mybaby.utils.CommonsUtil;
import com.canace.mybaby.utils.FileHelper;
import com.canace.mybaby.utils.MyBabyConstants;
import com.canace.mybaby.view.PView;

public class ImagePagerAdapter extends PagerAdapter {
	private static final String TAG = "ImagePagerAdapter";
	private final List<PView> viewList;
	private ImageItem[] imageItems;

	public ImagePagerAdapter(List<PView> viewList) {

		this.viewList = viewList;
	}

	public void setImageItems(ImageItem[] imageItems) {
		this.imageItems = imageItems;
	}

	@Override
	public int getCount() {

		return viewList.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {

		return arg0 == arg1;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) { // 这个方法用来实例化页卡
		if (CommonsUtil.DEBUG) {
			Log.i(TAG, "instantiateItem" + position);
		}
		try {
			if (FileHelper.isFileExist(imageItems[position].getImagePath())) {
				Bitmap imageBitmap = BitmapHelper.getScaledBitmapFromSDCard(
						imageItems[position].getImagePath(), 600);

				viewList.get(position).setImageBitmap(imageBitmap);
			} else {
				HomePageActivity.showToast(MyBabyConstants.FILE_DELETED,
						Toast.LENGTH_SHORT);
			}

		} catch (Exception e) {
			if (CommonsUtil.DEBUG) {
				Log.i(TAG, "instantiateItem" + position + "getBitmapFailed");
				e.printStackTrace();
			}

		}

		container.addView(viewList.get(position), 0);// 添加页卡
		return viewList.get(position);
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		if (CommonsUtil.DEBUG) {
			Log.i(TAG, "destroyItem" + position);
		}
		viewList.get(position).recycle();
		container.removeView(viewList.get(position));// 删除页卡
	}

	public PView getViewFromIndex(int index) {
		return viewList.get(index);
	}

	public void recycle() {
		for (PView pView : viewList) {
			pView.recycle();
		}
	}

}
