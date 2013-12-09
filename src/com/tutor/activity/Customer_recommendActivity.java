package com.tutor.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.totur.R;
import com.tutor.fragment.ViewPageFragment;
import com.tutor.model.ApplicationData;

public class Customer_recommendActivity extends Activity {
	private TextView recommend;
	private ApplicationData applicationData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		applicationData = (ApplicationData) getApplicationContext();
		setContentView(R.layout.customer_recommend);
	}

	private void initial() {
		recommend = (TextView) findViewById(R.id.customer_recommend);
		recommend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				final Dialog dialog = new Dialog(
						Customer_recommendActivity.this, R.style.Theme_dialog);
				dialog.setCanceledOnTouchOutside(true);
				LayoutInflater inflater = getLayoutInflater();
				View layout = inflater.inflate(
						R.layout.dialog_recommend_layout,
						(ViewGroup) findViewById(R.id.dialog_recommend));
				dialog.setContentView(layout);
				// TextView tvMessage = (TextView) dialog
				// .findViewById(R.id.tvMessage);
				// tvMessage.setText("是否确定退出？");
				Button confirmButton = (Button) dialog
						.findViewById(R.id.recommend_btnConfirm);
				confirmButton.setText("发送");
				confirmButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
						dialog();
					}
				});
				Button cancelButton = (Button) dialog
						.findViewById(R.id.recommend_btnCancel);
				cancelButton.setText("取消");
				cancelButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				dialog.show();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		applicationData.setCurrentActivity(this);
		initial();
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

	protected void dialog() {
		new AlertDialog.Builder(
				(Context) (getParent() == null ? applicationData
						.getCurrentActivity() : getParent()))
				.setTitle("提示")
				.setMessage("辅导成功，是否继续辅导？")
				.setPositiveButton("返回首页",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								Intent intent = new Intent(
										Customer_recommendActivity.this,
										ViewPageFragment.class);
								Customer_recommendActivity.this
										.startActivity(intent);
							}
						})
				.setNegativeButton("继续辅导",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						}).show();
	}

}
