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
package com.tutor.views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import com.totur.R;

@SuppressLint("NewApi")
public class SlidingMenu extends RelativeLayout {
	private View mSlidingView;
	private View mMenuView;
	private RelativeLayout bgShade;
	private int screenWidth;
	private int screenHeight;
	private Context mContext;
	private Scroller mScroller;
	private int mTouchSlop;
	private float mLastMotionX;
	private float mLastMotionY;
	private boolean tCanSlideLeft = true;
	private boolean tCanSlideRight = false;
	private boolean hasClickLeft = false;

	public SlidingMenu(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {

		mContext = context;
		bgShade = new RelativeLayout(context);
		mScroller = new Scroller(getContext());
		mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
		WindowManager windowManager = ((Activity) context).getWindow()
				.getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		int sdk = android.os.Build.VERSION.SDK_INT;
		if (sdk < 13) {
			screenHeight =display.getHeight();
			screenWidth = display.getWidth();
		}else{
			Point point = new Point();
			display.getSize(point);
			screenHeight = point.y;
			screenWidth = point.x;
		}
		LayoutParams bgParams = new LayoutParams(screenWidth, screenHeight);
		bgParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		bgShade.setLayoutParams(bgParams);

	}

	public SlidingMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public SlidingMenu(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public void setLeftView(View view) {
		LayoutParams behindParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.MATCH_PARENT);
		addView(view, behindParams);
		mMenuView = view;
	}

	public void setCenterView(View view) {
		LayoutParams aboveParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);

		LayoutParams bgParams = new LayoutParams(screenWidth, screenHeight);
		bgParams.addRule(RelativeLayout.CENTER_IN_PARENT);

		View bgShadeContent = new View(mContext);
		bgShadeContent.setBackgroundResource(R.drawable.shade_bg);
		bgShade.addView(bgShadeContent, bgParams);

		addView(bgShade, bgParams);

		addView(view, aboveParams);
		mSlidingView = view;
		mSlidingView.bringToFront();
	}

	@Override
	public void scrollTo(int x, int y) {
		super.scrollTo(x, y);
		postInvalidate();
	}

	@Override
	public void computeScroll() {
		if (!mScroller.isFinished()) {
			if (mScroller.computeScrollOffset()) {
				int oldX = mSlidingView.getScrollX();
				int oldY = mSlidingView.getScrollY();
				int x = mScroller.getCurrX();
				int y = mScroller.getCurrY();
				if (oldX != x || oldY != y) {
					if (mSlidingView != null) {
						mSlidingView.scrollTo(x, y);
						if (x < 0)
							bgShade.scrollTo(x + 20, y);// 鑳屾櫙闃村奖鍙冲亸
						else
							bgShade.scrollTo(x - 20, y);// 鑳屾櫙闃村奖宸﹀亸
					}
				}
				invalidate();
			}
		}
	}

	private boolean canSlideLeft = true;
	private boolean canSlideRight = false;

	public void setCanSliding(boolean left, boolean right) {
		canSlideLeft = left;
		canSlideRight = right;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {

		final int action = ev.getAction();
		final float x = ev.getX();
		final float y = ev.getY();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mLastMotionX = x;
			mLastMotionY = y;
			if (canSlideLeft) {
				mMenuView.setVisibility(View.VISIBLE);
				// mDetailView.setVisibility(View.INVISIBLE);
			}
			if (canSlideRight) {
				mMenuView.setVisibility(View.INVISIBLE);
				// mDetailView.setVisibility(View.VISIBLE);
			}
			break;

		case MotionEvent.ACTION_MOVE:
			final float dx = x - mLastMotionX;
			final float xDiff = Math.abs(dx);
			final float yDiff = Math.abs(y - mLastMotionY);
			if (xDiff > mTouchSlop && xDiff > yDiff) {
				if (canSlideLeft) {
					float oldScrollX = mSlidingView.getScrollX();
					if (oldScrollX < 0) {
						mLastMotionX = x;
					} else {
						if (dx > 0) {
							mLastMotionX = x;
						}
					}

				} else if (canSlideRight) {
					float oldScrollX = mSlidingView.getScrollX();
					if (oldScrollX > 0) {
						mLastMotionX = x;
					} else {
						if (dx < 0) {
							mLastMotionX = x;
						}
					}
				}

			}
			break;

		}
		return false;
	}
	
	void smoothScrollTo(int dx) {
		int duration = 500;
		int oldScrollX = mSlidingView.getScrollX();
		mScroller.startScroll(oldScrollX, mSlidingView.getScrollY(), dx,
				mSlidingView.getScrollY(), duration);
		invalidate();
	}

	public boolean showLeftView() {
		int menuWidth = mMenuView.getWidth();
		int oldScrollX = mSlidingView.getScrollX();
		if (oldScrollX == 0) {
			mMenuView.setVisibility(View.VISIBLE);
			// mDetailView.setVisibility(View.INVISIBLE);
			smoothScrollTo(-menuWidth);
			tCanSlideLeft = canSlideLeft;
			tCanSlideRight = canSlideRight;
			hasClickLeft = true;
			setCanSliding(true, false);
		} else if (oldScrollX == -menuWidth) {
			smoothScrollTo(menuWidth);
			if (hasClickLeft) {
				hasClickLeft = false;
				setCanSliding(tCanSlideLeft, tCanSlideRight);
			}
		}
		return hasClickLeft;
	}
}
