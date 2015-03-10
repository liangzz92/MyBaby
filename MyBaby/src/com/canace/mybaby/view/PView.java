/**
 * PView.java
 * 重写View控件以实现可根据手势按比例缩放的ImageView
 * 根据两个触碰屏幕的手指间距计算缩放比例大小，但所生成Bitmap会占用较多内存空间
 * 应注意显式调用recycle()函数，及时释放内存并通知系统回收资源
 * @author liangzz
 * 2014-12-2
 */
package com.canace.mybaby.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class PView extends View {
	private Bitmap mBitmap;
	private final static String TAG = "PView";
	private float RATE_MAX = 3;
	private float RATE_MIN = 1f;
	private final static int MOVE_THRESHLOD = 4;

	public final static int TYPE_SELECT_TARGET = 0;
	public final static int TYPE_SELECT_MIDDLE = 1;
	public final static int TYPE_CORRECT_POSITION = 2;

	private float mScaleRate;
	private float mPrevScaleRate;
	private float mTempLength;
	private float mLeft;
	private float mTop;
	private final PointF[] mPrePointF = { new PointF(), new PointF() };
	private int mPointNo;
	private int mMoveCount;
	private boolean mIsDoubleTouched;

	public PView(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	public PView(Context context) {
		super(context);
	}

	public void reset() {
		mLeft = 0;
		mTop = (getHeight() - mBitmap.getHeight()) / 2;
		mScaleRate = 1;
	}

	public void setImageBitmap(final Bitmap bitmap) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (getWidth() == 0) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				if (bitmap.getWidth() != getWidth()) {
					float scale = (float) getWidth() / bitmap.getWidth();
					RATE_MIN = scale;
					RATE_MAX = RATE_MIN * 3;
					setScaleRate(scale);
					mBitmap = bitmap;
				} else {
					mBitmap = bitmap;
				}
				setLeft(0.f);
				setTop(0.f);

				postInvalidate();

			}
		}).start();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.translate(mLeft, mTop);
		canvas.scale(mScaleRate, mScaleRate);
		if (mBitmap != null)
			canvas.drawBitmap(mBitmap, 0, 0, null);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getActionMasked()) {
		case MotionEvent.ACTION_DOWN:
			mIsDoubleTouched = false;
			mPointNo = 1;
			mMoveCount = 0;
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			mIsDoubleTouched = true;
			mMoveCount = 0;
			mPointNo = 2;
			try {
				mPrePointF[1].set(event.getX(1), event.getY(1));
				mTempLength = new PointF(mPrePointF[1].x - mPrePointF[0].x,
						mPrePointF[1].y - mPrePointF[0].y).length();
				// Log.e("length", "fir " +mTempLength);
			} catch (IllegalArgumentException e) {

			}
			break;
		case MotionEvent.ACTION_MOVE:
			mMoveCount++;
			if (mPointNo == 1) {
				setLeft(mLeft + event.getX() - mPrePointF[0].x);
				setTop(mTop + event.getY() - mPrePointF[0].y);
			} else if (mPointNo == 2 && event.getPointerCount() == 2) {
				if (Math.abs(mTempLength) > 0.000000001)
					setScaleRate(mScaleRate
							* new PointF(mPrePointF[1].x - mPrePointF[0].x,
									mPrePointF[1].y - mPrePointF[0].y).length()
							/ mTempLength);
				setScaleRate(mScaleRate);
				mTempLength = new PointF(mPrePointF[1].x - mPrePointF[0].x,
						mPrePointF[1].y - mPrePointF[0].y).length();
				// Log.e("length", "mov " +mTempLength);
				try {
					mPrePointF[1].set(event.getX(1), event.getY(1));
				} catch (IllegalArgumentException e) {

				}
			}
			postInvalidate();
			break;
		case MotionEvent.ACTION_POINTER_UP:
			mPointNo = 0;
			break;
		case MotionEvent.ACTION_UP:
			if (mMoveCount <= MOVE_THRESHLOD) {
				postInvalidate();
			}
			mIsDoubleTouched = false;
			mPointNo = 0;
			break;
		default:
			break;
		}
		mPrePointF[0].set(event.getX(), event.getY());
		return true;
	}

	private void setLeft(float left) {

		if (mBitmap != null) {
			mLeft = left;
			int threshold = 2;
			if (mLeft > threshold) {
				mLeft = threshold;
			}

			if (mLeft < -(mBitmap.getWidth() * mScaleRate - getWidth() + threshold)) {
				mLeft = -(mBitmap.getWidth() * mScaleRate - getWidth() + threshold);
			}
		}

	}

	private void setTop(float top) {
		if (mBitmap != null) {
			mTop = top;
			int threshold = 5;
			float offset = (getHeight() - mBitmap.getHeight() * mScaleRate) / 2;
			if (mBitmap.getHeight() * mScaleRate < getHeight()) {
				mTop = offset;
			} else {
				if (mTop + mBitmap.getHeight() * mScaleRate < getHeight()) {
					mTop = getHeight() - mBitmap.getHeight() * mScaleRate;
				}

				if (mTop > threshold) {
					mTop = threshold;
				}
			}
		}

	}

	private float correctScaleRate(float scaleRate) {
		if (Float.isNaN(scaleRate) || Float.isInfinite(scaleRate))
			scaleRate = 1;
		scaleRate = scaleRate > RATE_MAX ? RATE_MAX : scaleRate;
		scaleRate = scaleRate < RATE_MIN ? RATE_MIN : scaleRate;

		return scaleRate;
	}

	public void setScaleRate(float rate) {
		float scaleRate = correctScaleRate(rate);
		if (mPrevScaleRate == 0 || mScaleRate == 0)
			mPrevScaleRate = mScaleRate = scaleRate;
		else {
			setLeft(-((-mLeft + getWidth() / 2) * mScaleRate / mPrevScaleRate - getWidth() / 2));
			setTop(-((-mTop + getHeight() / 2) * mScaleRate / mPrevScaleRate - getHeight() / 2));
			mPrevScaleRate = mScaleRate;
			mScaleRate = scaleRate;
		}

	}

	public boolean isMoveOut() {
		if (mBitmap != null) {

			if (mIsDoubleTouched)
				return false;
			if (mLeft < 0
					&& mLeft > -(mBitmap.getWidth() * mScaleRate - getWidth())) {
				return false;
			}
		}

		return true;
	}

	public void recycle() {
		if (mBitmap != null) {
			mBitmap.recycle();
			mBitmap = null;
			System.gc();
		}

	}

}
