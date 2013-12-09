package com.tutor.activity;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.totur.R;
import com.tutor.model.AppConfiguration;
import com.tutor.model.ApplicationData;
import com.tutor.utilities.GetDataFromWebService;
import com.tutor.utilities.ImageLoader;
import com.tutor.utilities.OnGetDataListener;
import com.tutor.utilities.Utils;

public class AppDetailActivity extends Activity {

	public ImageView image;
	public TextView titie;
	public TextView size;
	public TextView version;
	public TextView description;
	public TextView introduce;
	private ApplicationData applicationData;
	private Button btnFavorite;
	private ImageLoader imageLoader;

	private boolean isFavorite;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_detail);
		applicationData = (ApplicationData) getApplicationContext();
		imageLoader = applicationData.imageLoader;
	}

	private void initial() {
		titie = (TextView) findViewById(R.id.app_detail_title);
		size = (TextView) findViewById(R.id.app_detail_size);
		version = (TextView) findViewById(R.id.app_detail_version);
		introduce = (TextView) findViewById(R.id.tvSoftwareIntorduction);
		description = (TextView) findViewById(R.id.tvMessageTutor);
		image = (ImageView) findViewById(R.id.app_detail_image);
		titie.setText(applicationData.getcurrentFDItemInf().getName());
		size.setText(applicationData.getcurrentFDItemInf().getSize());
		version.setText(applicationData.getcurrentFDItemInf().getVersion());
		introduce.setText(applicationData.getcurrentFDItemInf()
				.getIntroudution());
		description.setText(applicationData.getcurrentFDItemInf()
				.getRecommend_Decrible());
		imageLoader.DisplayImage(applicationData.getcurrentFDItemInf()
		.getAppImageUrl(), image);
		btnFavorite = (Button) this.findViewById(R.id.btn_favorite);
		isFavorite = false;
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.app_detail, menu);
		return true;
	}

	public void moveOutFromFavorite(View view1) {
		if (isFavorite) {
			showConfirmDialog(btnFavorite);
		} else {
			SaveFarivote(applicationData.getcurrentFDItemInf().getID(),
					applicationData.getcurrentUser().userid);
		}

	}

	private void showConfirmDialog(final Button btnFavorite) {
		// popup operate confirm.
		final Dialog dialog = new Dialog(this, R.style.Theme_dialog);
		dialog.setCanceledOnTouchOutside(true);
		View diaView = LayoutInflater.from(this).inflate(
				R.layout.dialog_yesno_layout, null);
		dialog.setContentView(diaView);
		TextView tvMessage = (TextView) dialog.findViewById(R.id.tvMessage);
		tvMessage.setText(getText(R.string.confirm_remove_app));
		TextView tv_dialog_title = (TextView) dialog
				.findViewById(R.id.tv_dialog_title);
		tv_dialog_title.setText(getText(R.string.cancel_favorite));

		Button confirmButton = (Button) dialog.findViewById(R.id.btnConfirm);
		confirmButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				btnFavorite
						.setBackgroundResource(R.drawable.selector_addto_favorite);
				// btnFavorite.setText(R.string.import_to_favorite);
				dialog.dismiss();
				HashMap<String, Object> para = new LinkedHashMap<String, Object>();
				para.put("Application_ID", applicationData
						.getcurrentFDItemInf().getID());
				para.put("User_ID", applicationData.getcurrentUser().userid);
				GetDataFromWebService service = new GetDataFromWebService();
				service.setOnGetDataListener(new OnGetDataListener() {

					@Override
					public void onGetDataCompleted(String responseData) {
						try {
							JSONObject json = new JSONObject(responseData);
							if (json.getString("flag") == "1") {
								isFavorite = false;
							}
						} catch (Exception e) {
							Utils.showMessageBox(AppDetailActivity.this,
									applicationData, "移除收藏夹失败!");
							if (e != null) {
								Log.d("调试", "移除收藏夹失败. response:\""
										+ responseData + "\"");
								e.printStackTrace();
							}
						}
					}
				});
				service.GetDataFromWebAsync("removeAppFavorite",
						AppConfiguration.GUEST_TUTOR_SERVICE, para);
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

	public void ToTutor(View view) {
		Intent intent = new Intent(this, AppTutorActivity.class);
		intent.putExtra("isTutorOrService", "辅导");
		startActivity(intent);
		finish();
	}

	private void SaveFarivote(int appID, String User_ID) {
		HashMap<String, Object> para = new LinkedHashMap<String, Object>();
		para.put("Application_ID", appID);
		para.put("User_ID", User_ID);
		GetDataFromWebService service = new GetDataFromWebService();
		service.setOnGetDataListener(new OnGetDataListener() {

			@Override
			public void onGetDataCompleted(String responseData) {
				showAddtoFavoriteMsg(responseData);
			}

		});
		service.GetDataFromWebAsync("addAppFavorite",
				AppConfiguration.GUEST_TUTOR_SERVICE, para);
	}

	private void showAddtoFavoriteMsg(String responseData) {
		boolean issucess = false;
		if (responseData != null && responseData != "") {
			String message = "";
			try {
				JSONObject json = new JSONObject(responseData);
				// JSONArray jsonArray = new JSONArray(result);
				// JSONObject json = (JSONObject) jsonArray.get(0);
				switch (Integer.parseInt(json.get("flag").toString())) {
				case 0:
					message = "加入收藏夹成功!";
					issucess = true;
					break;
				case 1:
					message = "加入收藏夹失败!";
					issucess = false;
					break;
				case 2:
					message = "重复加入收藏夹!";
					issucess = true;
					break;
				}
			} catch (Exception ex) {
				message = "加入收藏夹失败!";
				issucess = false;
			}

			Utils.showMessageBox(AppDetailActivity.this, applicationData,
					message);
		} else {
			Utils.showMessageBox(AppDetailActivity.this, applicationData,
					"加入收藏夹失败!");
			Log.d("调试", "加入收藏夹失败! response:\"" + responseData + "\"");
		}
		if (issucess) {
			btnFavorite
					.setBackgroundResource(R.drawable.selector_removeto_favorite);
			isFavorite = true;
		} else {
			isFavorite = false;
		}
	}

	public void backToPrevious(View view) {
		super.onBackPressed();
	}
}
