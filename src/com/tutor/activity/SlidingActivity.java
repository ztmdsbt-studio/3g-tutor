/*
 * Copyright (C) 2012 yueyueniao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tutor.activity;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.totur.R;
import com.tutor.fragment.LeftFragment;
import com.tutor.fragment.ViewPageFragment;
import com.tutor.model.ApplicationData;
import com.tutor.views.SearchWindow;
import com.tutor.views.SlidingMenu;

public class SlidingActivity extends FragmentActivity {
	private SlidingMenu mSlidingMenu;
	private LeftFragment leftFragment;
	private ViewPageFragment viewPageFragment;
	private ApplicationData applicationData;
	private PopupWindow popupWindow;
	private boolean isShowLeft;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		applicationData = (ApplicationData) getApplicationContext();
		setContentView(R.layout.main);
		initial();
	}

	@Override
	protected void onResume() {
		super.onResume();
		applicationData.setCurrentActivity(this);
		View search = this.findViewById(R.id.iv_search);
		search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showPopupWindow();
			}
		});
	}

	@Override
	protected void onPause() {
		clearReferences();
		super.onPause();
	}

	protected void onDestroy() {
		clearReferences();
		super.onDestroy();
	}

	private void clearReferences() {
		Activity currActivity = applicationData.getCurrentActivity();
		if (currActivity != null && currActivity.equals(this))
			applicationData.setCurrentActivity(null);
	}

	private void initial() {
		mSlidingMenu = (SlidingMenu) findViewById(R.id.slidingMenu);
		mSlidingMenu.setLeftView(getLayoutInflater().inflate(
				R.layout.left_frame, null));
		mSlidingMenu.setCenterView(getLayoutInflater().inflate(
				R.layout.center_frame, null));

		FragmentTransaction t = this.getSupportFragmentManager()
				.beginTransaction();
		leftFragment = new LeftFragment();
		t.replace(R.id.left_frame, leftFragment);

		viewPageFragment = new ViewPageFragment();
		t.replace(R.id.center_frame, viewPageFragment);

		t.commit();
		getSupportFragmentManager().executePendingTransactions();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			if (isShowLeft) {
				this.isShowLeft = mSlidingMenu.showLeftView();
				return false;
			}
			final Dialog dialog = new Dialog(this, R.style.Theme_dialog);
			dialog.setCanceledOnTouchOutside(true);
			View view = LayoutInflater.from(this).inflate(
					R.layout.dialog_yesno_layout, null);
			dialog.setContentView(view);
			TextView tvMessage = (TextView) dialog.findViewById(R.id.tvMessage);
			tvMessage.setText("是否确定退出？");

			Button confirmButton = (Button) dialog
					.findViewById(R.id.btnConfirm);
			confirmButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					finish();
					android.os.Process.killProcess(android.os.Process.myPid());
				}
			});

			Button cancelButton = (Button) dialog.findViewById(R.id.btnCancel);
			cancelButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});

			dialog.show();
		}

		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public void showLeft() {
		this.isShowLeft = mSlidingMenu.showLeftView();
	}

	private void showPopupWindow() {

		if (popupWindow == null) {
			final SearchWindow window = new SearchWindow(this,
					applicationData.getappCategories());

			popupWindow = new PopupWindow(window, LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT, true);
			window.setCategoryListOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					Intent intent = new Intent(SlidingActivity.this,
							SearchResultActivity.class);
					intent.putExtra("Flag", "Category");

					intent.putExtra("CategoryId", applicationData
							.getappCategories().get(position).getid());
					intent.putExtra("CategoryName", applicationData
							.getappCategories().get(position).getname());

					SlidingActivity.this.startActivity(intent);
				}
			});
			window.setOnHiddenClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					hidePopupWindow();
				}
			});
			window.setOnSearchListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(SlidingActivity.this,
							SearchResultActivity.class);
					intent.putExtra("Flag", "App");
					intent.putExtra("SearchFilter", window.getsearchFilter());
					startActivity(intent);
				}
			});
		}
		popupWindow.setTouchable(true);
		popupWindow.setOutsideTouchable(false);
		popupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(),
				Bitmap.createBitmap(480, 800, Bitmap.Config.ARGB_8888)));
		popupWindow.showAtLocation(findViewById(R.id.slidingMenu), Gravity.TOP,
				0, 0);
	}

	private void hidePopupWindow() {
		if (popupWindow == null) {
			return;
		}

		popupWindow.dismiss();
	}
}
