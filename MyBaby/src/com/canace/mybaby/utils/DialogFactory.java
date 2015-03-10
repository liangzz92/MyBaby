/**
 * DialogFactory.java
 * 对话框工厂，使用单例模式
 * 目前有创建快捷方式、退出程序等对话框
 * @author liangzz
 * 2014-12-2
 */
package com.canace.mybaby.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.canace.mybaby.R;
import com.canace.mybaby.activity.LoginActivity;

public class DialogFactory {
	private static DialogFactory mInstance;
	private static Context mContext;

	public static DialogFactory getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new DialogFactory(context);
		}
		mContext = context;
		return mInstance;
	}

	private DialogFactory(Context context) {
		mContext = context;
	}

	public void reset() {
		mInstance = null;
	}

	/**
	 * 询问用户是否创建快捷方式
	 * 
	 * @return
	 */
	public Dialog createShortcutDialog() {
		Dialog dialog = new AlertDialog.Builder(mContext).setTitle("提示")
				.setMessage("是否创建桌面快捷方式?")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						CommonsUtil.createDeskShortCut(mContext);
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

					}
				}).create();
		return dialog;
	}

	/**
	 * 询问用户是否确定退出
	 * 
	 * @return
	 */
	public Dialog createQuitDialog() {
		Dialog dialog = new AlertDialog.Builder(mContext).setTitle("提示")
				.setMessage("确定退出?")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						((Activity) mContext).finish();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

					}
				}).create();
		return dialog;
	}

	/**
	 * 询问用户是否切换至登录界面
	 */
	public Dialog createLoginDialog() {

		Dialog dialog = new AlertDialog.Builder(mContext)
				.setTitle("提示")
				.setMessage(mContext.getResources().getText(R.string.login))
				.setCancelable(false)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(mContext,
								LoginActivity.class);
						mContext.startActivity(intent);
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				}).create();
		return dialog;

	}

}