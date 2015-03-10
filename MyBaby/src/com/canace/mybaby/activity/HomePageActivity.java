/**
 * HomePageActivity.java
 * 图片列表主界面，展示人脸检测后符合“宝贝”条件的图片，提供按时间与笑脸排序的功能
 * 设置页面、查看大图页面入口
 * @author liangzz
 * 2014-12-2
 */
package com.canace.mybaby.activity;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.TreeSet;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.canace.mybaby.R;
import com.canace.mybaby.adapter.ImageItemAdapter;
import com.canace.mybaby.cache.model.LocalDataSource;
import com.canace.mybaby.cache.model.MediaFeed;
import com.canace.mybaby.cache.scanner.CacheService;
import com.canace.mybaby.cache.scanner.LocalDetectService;
import com.canace.mybaby.cache.scanner.OnlineDetectService;
import com.canace.mybaby.db.DBFacade;
import com.canace.mybaby.db.model.ImageItem;
import com.canace.mybaby.utils.CommonsUtil;
import com.canace.mybaby.utils.PreferenceUtils;
import com.canace.mybaby.utils.SortByQuality;
import com.canace.mybaby.utils.SortBySmiling;
import com.canace.mybaby.utils.SortByTime;
import com.canace.mybaby.view.DragGridView;
import com.canace.mybaby.view.SortTypeLayout;
import com.umeng.analytics.MobclickAgent;

@SuppressLint("NewApi")
public class HomePageActivity extends MyBabyActivity implements
		OnClickListener, MediaFeed.Listener {

	private static Context mContext;
	private final static Handler mHandler = new Handler();

	public final static int SORT_BY_TIME = 0;
	public final static int SORT_BY_SMILE = 1;
	public final static int SORT_BY_QUALITY = 2;
	public final static int SORT_ASCENT = 0;
	public final static int SORT_DESCENT = 1;
	private final Map<Integer, SortTypeLayout> sortTypeMap = new HashMap<Integer, SortTypeLayout>();
	private SortTypeLayout sortByTimeLayout;
	private SortTypeLayout sortBySmileLayout;
	private SortTypeLayout sortByQualityLayout;
	public static final TimeZone CURRENT_TIME_ZONE = TimeZone.getDefault();

	private static final String TAG = "HomePageActivity";

	protected static final int DIALOG_CANCEL = 0;
	private ImageView settingButton;
	private ImageView facePlayButton;
	private ImageView sortBarButton;
	private TextView statusTextView;
	private RelativeLayout sortBarRelativeLayout;

	private TextView refreshTextView;
	private ImageView refreshImageView;
	private ImageItemAdapter imageItemAdapter;
	private DragGridView imagesDragGridView;
	private static ImageItem[] imageItems = {};

	/**
	 * @return the imageItems
	 */
	public static ImageItem[] getImageItems() {
		return imageItems;
	}

	private boolean isSortingBarVisible = false;

	/**
	 * @return the isSortingBarVisible
	 */
	public boolean isSortingBarVisible() {
		return isSortingBarVisible;
	}

	/**
	 * @param isSortingBarVisible
	 *            the isSortingBarVisible to set
	 */
	public void setSortingBarVisible(boolean isSortingBarVisible) {
		this.isSortingBarVisible = isSortingBarVisible;
	}

	private int currentSortType;

	/**
	 * @return the currentSortType
	 */
	public int getCurrentSortType() {
		return currentSortType;
	}

	/**
	 * @param currentSortType
	 *            the currentSortType to set
	 */
	public void setCurrentSortType(int currentSortType) {
		this.currentSortType = currentSortType;
	}

	private static int currentSortOrder;

	/**
	 * @return the currentSortOrder
	 */
	public static int getCurrentSortOrder() {
		return currentSortOrder;
	}

	/**
	 * @param currentSortOrder
	 *            the currentSortOrder to set
	 */
	public static void setCurrentSortOrder(int currentSortOrder) {
		HomePageActivity.currentSortOrder = currentSortOrder;
	}

	private int firstVisiblePosition = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		initComponents();
		setListener();

	}

	/**
	 * 为各元素设置点击事件监听器
	 */
	private void setListener() {
		sortBarButton.setOnClickListener(this);
		settingButton.setOnClickListener(this);
		facePlayButton.setOnClickListener(this);
		sortByTimeLayout.sortTypeTextView.setOnClickListener(this);
		sortByTimeLayout.sortTypeImageView.setOnClickListener(this);
		sortBySmileLayout.sortTypeTextView.setOnClickListener(this);
		sortBySmileLayout.sortTypeImageView.setOnClickListener(this);
		sortByQualityLayout.sortTypeTextView.setOnClickListener(this);
		sortByQualityLayout.sortTypeImageView.setOnClickListener(this);
		refreshImageView.setOnClickListener(this);
		refreshTextView.setOnClickListener(this);
		imagesDragGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					final int arg2, final long arg3) {

				firstVisiblePosition = imagesDragGridView
						.getFirstVisiblePosition();
				Intent intent = new Intent(HomePageActivity.this,
						SlidesActivity.class);
				intent.putExtra("fromItem", arg2);
				intent.putExtra("firstVisiblePosition", firstVisiblePosition);
				startActivityForResult(intent,
						SlidesActivity.RESULT_SLIDESACTIVITY);

			}
		});
		imagesDragGridView
				.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> arg0,
							View arg1, final int arg2, long arg3) {
						firstVisiblePosition = imagesDragGridView
								.getFirstVisiblePosition();
						final ImageItem imageItem = imageItems[arg2];
						Dialog dialog = new AlertDialog.Builder(
								HomePageActivity.this)
								.setTitle("提示")
								.setMessage("将此照片从宝贝相册移除？（不可恢复）")
								.setPositiveButton("确定",
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {

												DBFacade.delete(imageItem);
												refreshImageItemsList(true,
														true);
											}
										})
								.setNegativeButton("取消",
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												// 不需要刷新数据，需重新设置adapter
												refreshImageItemsList(false,
														true);
											}
										}).create();
						dialog.show();
						return true;
					}
				});
	}

	private void initComponents() {
		mContext = this;

		statusTextView = (TextView) findViewById(R.id.status);
		sortBarButton = (ImageView) findViewById(R.id.buttonsort);
		sortBarRelativeLayout = (RelativeLayout) findViewById(R.id.sort_bar);

		initSortTypeAndOrder(PreferenceUtils
				.getIntValue(PreferenceUtils.SORT_TYPE));
		initSortBar();
		refreshTextView = (TextView) findViewById(R.id.refresh_text);
		refreshImageView = (ImageView) findViewById(R.id.refresh_image);
		settingButton = (ImageView) findViewById(R.id.buttonmenu);
		facePlayButton = (ImageView)findViewById(R.id.buttonfaceplay);
		imagesDragGridView = (DragGridView) findViewById(R.id.dragGridView);
		imagesDragGridView.post(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(300);
					imageItemAdapter = new ImageItemAdapter(mContext,
							imagesDragGridView.getWidth(), imagesDragGridView
									.getHeight(), imageItems);
					imagesDragGridView.setAdapter(imageItemAdapter);
					startRefreshImages();
				} catch (InterruptedException e) {
					if (CommonsUtil.DEBUG) {
						e.printStackTrace();
					}

				}

			}
		});

	}

	/**
	 * 初始化排序功能控件
	 * 
	 * @param intValue
	 */
	private void initSortBar() {
		TextView sortByTimeTextView = (TextView) findViewById(R.id.sort_time);
		ImageView sortByTimeImageView = (ImageView) findViewById(R.id.sort_time_image);
		sortByTimeLayout = new SortTypeLayout(sortByTimeImageView,
				sortByTimeTextView);
		sortTypeMap.put(SORT_BY_TIME, sortByTimeLayout);
		TextView sortBySmileTextView = (TextView) findViewById(R.id.sort_smile);
		ImageView sortBySmileImageView = (ImageView) findViewById(R.id.sort_smile_image);
		sortBySmileLayout = new SortTypeLayout(sortBySmileImageView,
				sortBySmileTextView);
		sortTypeMap.put(SORT_BY_SMILE, sortBySmileLayout);
		TextView sortByQualityTextView = (TextView) findViewById(R.id.sort_quality);
		ImageView sortByQualityImageView = (ImageView) findViewById(R.id.sort_quality_image);
		sortByQualityLayout = new SortTypeLayout(sortByQualityImageView,
				sortByQualityTextView);
		sortTypeMap.put(SORT_BY_QUALITY, sortByQualityLayout);

		setSortType();

	}

	/**
	 * 高亮的当前排序方式，还原其他控件显示
	 */
	private void setSortType() {
		for (Entry<Integer, SortTypeLayout> entry : sortTypeMap.entrySet()) {
			if (entry.getKey().equals(currentSortType)) {
				entry.getValue().select(
						getResources().getColor(R.color.mybaby_default_color),
						currentSortOrder);
			} else {
				entry.getValue().initLayout();
			}
		}
	}

	/**
	 * 开启缓存服务
	 */
	private void startCacheService() {
		CacheService.computeDirtySets(HomePageActivity.this);
		CacheService.startCache(HomePageActivity.this, false);
		// Creating the DataSource objects.
		final LocalDataSource localDataSource = new LocalDataSource(
				HomePageActivity.this);

		MediaFeed feed = new MediaFeed(mContext, localDataSource,
				HomePageActivity.this);
		feed.start();
	}

	/**
	 * 每隔1.5s刷新一次图片列表，直至CacheService完成扫描与检测任务
	 */
	private void startRefreshImages() {

		startCacheService();
		refreshButtonAnimation();

	}

	/**
	 * 排序按钮和检测情况提示控件的刷新
	 */
	private void refreshButtonAnimation() {
		// TODO Auto-generated method stub
		startRefreshButtonAnimation();
		new Thread(new Runnable() {

			@Override
			public void run() {

				while (!CacheService.isCacheReady(false)) {

					try {

						Thread.sleep(1500);
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								if (LocalDetectService.getTotalCount() == 0) {
									statusTextView.setText(""
											+ OnlineDetectService
													.getDetectedCount()
											+ "/"
											+ OnlineDetectService
													.getTotalCount());
								} else {
									statusTextView.setText(""
											+ LocalDetectService
													.getDetectedCount()
											+ "/"
											+ LocalDetectService
													.getTotalCount());
								}

								refreshImageItemsList(true, false);
							}
						});

					} catch (InterruptedException e) {
						if (CommonsUtil.DEBUG) {
							e.printStackTrace();
						}
					}
				}
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						statusTextView.setText("");
						stopRefreshButtonAnimation();
					}
				});

			}

		}).start();
	}

	/**
	 * 刷新图片列表
	 * 
	 * @param needRefreshData
	 *            是否刷新列表数据
	 * @param needRefreshAdapter
	 *            是否为列表新建adapter
	 */
	private void refreshImageItemsList(boolean needRefreshData,
			boolean needRefreshAdapter) {
		if (needRefreshData || currentSortType == SORT_BY_TIME) {
			imageItems = initImageItems();
		}
		if (needRefreshAdapter) {
			imageItemAdapter = new ImageItemAdapter(mContext,
					imagesDragGridView.getWidth(),
					imagesDragGridView.getHeight(), imageItems);
			imagesDragGridView.setAdapter(imageItemAdapter);
			imagesDragGridView.setSelection(firstVisiblePosition);
		}
		sortImageItems();
		imageItemAdapter.setDataList(imageItems);
		imageItemAdapter.notifyDataSetChanged();
	}

	/**
	 * 从数据库获取图片信息 检测图片是否存在，不存在时更新数据库，删除相应记录
	 * 
	 * @return
	 */
	public static ImageItem[] initImageItems() {
		ImageItem[] imageItems = (ImageItem[]) DBFacade.findByFieldName(
				ImageItem.class, null, "*");
		boolean changedFlag = false;
		for (int i = 0; i < imageItems.length; i++) {
			if (imageItems[i] != null) {
				if (imageItems[i].getImagePath() != null) {
					File file = new File(imageItems[i].getImagePath());
					if (!file.exists()) {
						DBFacade.delete(imageItems[i]);
						changedFlag = true;
					}
				} else {
					DBFacade.delete(imageItems[i]);
					changedFlag = true;
				}
			}

		}

		if (changedFlag) {
			imageItems = (ImageItem[]) DBFacade.findByFieldName(
					ImageItem.class, null, "*");
		}
		return imageItems;
	}

	/**
	 * 根据当前选中的排序方式对列表数据使用TreeSet去重及排序
	 * 
	 * @param imageItems2
	 * @return
	 */
	private void sortImageItems() {
		SortedSet<ImageItem> sortedImageItems = new TreeSet<ImageItem>(
				new SortByTime());
		switch (currentSortType) {

		case SORT_BY_SMILE:
			sortedImageItems = new TreeSet<ImageItem>(new SortBySmiling());
			break;
		case SORT_BY_QUALITY:
			sortedImageItems = new TreeSet<ImageItem>(new SortByQuality());
			break;
		default:
			break;
		}
		if (imageItems != null && sortedImageItems != null) {
			for (int i = 0; i < imageItems.length; i++) {
				sortedImageItems.add(imageItems[i]);
			}
			Iterator<ImageItem> iterator = sortedImageItems.iterator();
			if (currentSortOrder == SORT_ASCENT) {
				int j = 0;
				while (iterator.hasNext()) {
					imageItems[j++] = iterator.next();
					if (CommonsUtil.DEBUG) {
						//Log.i("ascent", imageItems[j - 1].getTimeScore() + "");
					}
				}
			} else {

				int j = imageItems.length - 1;
				while (iterator.hasNext()) {
					imageItems[j--] = iterator.next();
					if (CommonsUtil.DEBUG) {
						//Log.i("descent", imageItems[j + 1].getTimeScore() + "");
					}
				}
			}

		}

	}

	@Override
	public void onClick(View v) {
		if (v == sortBarButton) {
			if (CacheService.isCacheReady(false)) {
				showOrHideSortingBar();
			}

		} else if (v == sortByTimeLayout.sortTypeTextView
				|| v == sortByTimeLayout.sortTypeImageView) {
			if (currentSortType != SORT_BY_TIME) {
				initSortTypeAndOrder(SORT_BY_TIME);
			}
			refreshImageItemsSortType();

		} else if (v == sortBySmileLayout.sortTypeTextView
				|| v == sortBySmileLayout.sortTypeImageView) {
			if (currentSortType != SORT_BY_SMILE) {
				initSortTypeAndOrder(SORT_BY_SMILE);
			}
			refreshImageItemsSortType();

		} else if (v == sortByQualityLayout.sortTypeTextView
				|| v == sortByQualityLayout.sortTypeImageView) {
			Toast.makeText(mContext, "还在路上(*^__^*)~", Toast.LENGTH_SHORT)
					.show();
		} else if (v == refreshTextView || v == refreshImageView) {
			showOrHideSortingBar();
			startRefreshImages();
		} else if (v == settingButton) {

			Intent intent = new Intent(HomePageActivity.this,
					SettingActivity.class);
			startActivity(intent);
			finish();
		} else if( v == facePlayButton){
			Intent intent = new Intent(HomePageActivity.this, FacePlayActivity2.class);
			startActivity(intent);
		}

	}

	/**
	 * 根据选中排序方法更改排序控件外观 重新对列表数据排序（不重新从数据库获取），刷新列表
	 */
	private void refreshImageItemsSortType() {
		setSortType();
		refreshImageItemsList(false, false);

	}

	/**
	 * 更换排序方法时，默认以降序排序，先设当前排序顺序为升序，以便在实际排序时反转
	 * 
	 * @param sort_type
	 */
	private void initSortTypeAndOrder(int sort_type) {
		currentSortOrder = SORT_ASCENT;
		currentSortType = sort_type;
		PreferenceUtils
				.saveIntValue(PreferenceUtils.SORT_TYPE, currentSortType);
	}

	/**
	 * 根据用户点击事件，显示或隐藏排序功能控件
	 */
	private void showOrHideSortingBar() {
		if (!isSortingBarVisible) {
			sortBarRelativeLayout.setVisibility(View.VISIBLE);
			isSortingBarVisible = true;
		} else {
			sortBarRelativeLayout.setVisibility(View.GONE);
			isSortingBarVisible = false;
		}
	}

	private void startRefreshButtonAnimation() {
		sortBarButton.setImageResource(R.drawable.refreshing);
		Animation animRoute = new RotateAnimation(0.0f, +360.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);// 设置位移动画
		animRoute.setDuration(1000);// 持续1秒
		animRoute.setRepeatMode(Animation.RESTART);// 重复
		animRoute.setRepeatCount(Animation.INFINITE);// 无限次

		// 旋转不停顿
		LinearInterpolator lir = new LinearInterpolator();
		animRoute.setInterpolator(lir);
		sortBarButton.startAnimation(animRoute);
	}

	private void stopRefreshButtonAnimation() {

		if (sortBarButton != null) {
			sortBarButton.clearAnimation();
			sortBarButton.setImageResource(R.drawable.homebutton_red);
		}

	}

	@Override
	public void onFeedAboutToChange(MediaFeed feed) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				refreshImageItemsList(true, false);
			}
		});
	}

	@Override
	public void onFeedChanged(MediaFeed feed, boolean needsLayout) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				refreshImageItemsList(true, false);
			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// 从查看大图页面返回时，如果最后一张查看的图片不在之前屏幕内，GridView就滚动到指定位置
		if (resultCode == SlidesActivity.RESULT_SLIDESACTIVITY) {
			firstVisiblePosition = data.getIntExtra("currentItem", -1);
			if (firstVisiblePosition != -1) {

				imagesDragGridView.setSelection(firstVisiblePosition);
			} else {
				firstVisiblePosition = 0;
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	public Handler getHandler() {
		return mHandler;
	}

	public static void showToast(final String string, final int duration) {
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(mContext, string, duration).show();
			}
		});
	}
}
