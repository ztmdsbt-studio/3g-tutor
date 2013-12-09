package com.tutor.activity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.totur.R;
import com.tutor.adapters.FavoriteListAdapter;
import com.tutor.model.AppConfiguration;
import com.tutor.model.ApplicationData;
import com.tutor.utilities.GetDataFromWebService;
import com.tutor.utilities.OnGetDataListener;
import com.tutor.utilities.Utils;
import com.tutor.viewModel.FDItemInfModel;

public class Appfavorite extends Activity {
	private List<Map<String, Object>> listItems;
	private ApplicationData applicationData;
	private Intent intent;
	private ListView favoritelist;
	private FavoriteListAdapter favoriteListAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_favorites);
		applicationData = (ApplicationData) this.getApplication();
	}

	private void initial() {
		intent = new Intent(this, AppDetailActivity.class);
		InitialFdItemInf(applicationData.getcurrentUser().userid);
		setListViewAdapter();
		favoritelist = (ListView) findViewById(R.id.lv_favorite);
		CheckBox allcheck = (CheckBox) findViewById(R.id.favoriteisselected);

		allcheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				int count = favoritelist.getChildCount();

				if (isChecked) {
					for (int i = 0; i < count; i++) {
						final LinearLayout layout = (LinearLayout) favoritelist
								.getChildAt(i);
						final int c = layout.getChildCount();
						for (int j = 0; j < c; j++) {
							final View view = layout.getChildAt(j);
							if (view instanceof CheckBox) {
								((CheckBox) view).setChecked(true);
								favoriteListAdapter.setMapValue(i, true);
								break;
							}
						}
					}
				} else {
					for (int i = 0; i < count; i++) {
						final LinearLayout layout = (LinearLayout) favoritelist
								.getChildAt(i);
						final int c = layout.getChildCount();
						for (int j = 0; j < c; j++) {
							final View view = layout.getChildAt(j);
							if (view instanceof CheckBox) {
								((CheckBox) view).setChecked(false);
								favoriteListAdapter.setMapValue(i, false);
								break;
							}
						}
					}
				}
			}
		});

		TextView remove = (TextView) findViewById(R.id.removeFavoriteBtn);
		remove.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final Dialog succeed_dialog = new Dialog(Appfavorite.this,
						R.style.Theme_dialog);
				succeed_dialog.setCanceledOnTouchOutside(true);
				View dialogView = LayoutInflater.from(Appfavorite.this)
						.inflate(R.layout.dialog_remove_favorite_layout, null);
				succeed_dialog.setContentView(dialogView);
				Button confirmButton = (Button) succeed_dialog
						.findViewById(R.id.btnConfirm);
				confirmButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						succeed_dialog.dismiss();
						String appid = "";
						for (int i = 0; i < favoriteListAdapter.map.size(); i++) {
							if (favoriteListAdapter.map.get(i)) {
								appid += listItems.get(i).get("id") + ",";
							}
						}

						HashMap<String, Object> para = new LinkedHashMap<String, Object>();
						para.put("Application_ID", appid);
						para.put("User_ID",
								applicationData.getcurrentUser().userid);
						GetDataFromWebService service = new GetDataFromWebService();
						service.setOnGetDataListener(new OnGetDataListener() {

							@Override
							public void onGetDataCompleted(String responseData) {
								try {
									JSONObject json = new JSONObject(
											responseData);
									if (json.getString("flag") == "1") {
										InitialFdItemInf(applicationData
												.getcurrentUser().userid);
										setListViewAdapter();
									}
								} catch (Exception e) {
									Utils.showMessageBox(Appfavorite.this,
											applicationData, "移出收藏夹失败.");
									Log.d("调试", "移出收藏夹失败. response:\""
											+ responseData + "\"");
									e.printStackTrace();
								}
							}
						});
						service.GetDataFromWebAsync("removeAppFavorite",
								AppConfiguration.GUEST_TUTOR_SERVICE, para);
					}
				});

				Button cancelButton = (Button) succeed_dialog
						.findViewById(R.id.btnCancel);
				cancelButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						succeed_dialog.dismiss();
					}
				});

				succeed_dialog.show();
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.app_detail, menu);
		return true;
	}

	public void favoritebackToPrevious(View view) {
		super.onBackPressed();
	}

	private void InitialFdItemInf(String userID) {
		HashMap<String, Object> para = new LinkedHashMap<String, Object>();
		para.put("User_ID", userID);
		GetDataFromWebService service = new GetDataFromWebService();
		service.setOnGetDataListener(new OnGetDataListener() {

			@Override
			public void onGetDataCompleted(String responseData) {

				if (responseData != null
						&& responseData != ""
						&& !responseData.toLowerCase(Locale.getDefault())
								.contains("\"flag\":0")
						&& !responseData.toLowerCase(Locale.getDefault())
								.contains("\"flag\":1")) {
					HashMap<String, Object> params = new HashMap<String, Object>();
					params.put("flag", "fd");
					params.put("result", responseData);
					DoAtBackground(params);
				} else {
					Utils.showMessageBox(Appfavorite.this, applicationData,
							"获取收藏列表失败.");
					Log.d("调试", "获取收藏列表失败. response:\"" + responseData + "\"");
				}
			}
		});
		service.GetDataFromWebAsync("listAppFavorite",
				AppConfiguration.GUEST_TUTOR_SERVICE, para);
	}

	private void DoAtBackground(HashMap<String, Object> params) {
		InitDataAsyncTask task = new InitDataAsyncTask();
		task.execute(params);
	}

	private class InitDataAsyncTask extends
			AsyncTask<HashMap<String, Object>, Boolean, Void> {
		private void BuildfdItemsList(String result) {
			listItems = new ArrayList<Map<String, Object>>();
			try {
				JSONArray jsonArray = new JSONArray(result);
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject json = (JSONObject) jsonArray.get(i);
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("id", Integer.parseInt(json.get("id").toString()));
					map.put("imageUrl", json.get("ImageUrl").toString());
					map.put("image", BitmapFactory.decodeResource(
							getResources(), R.drawable.ic_launcher));
					String titletemp = json.get("name").toString();
					if (titletemp.length() > 8) {
						titletemp = titletemp.substring(0, 8) + "....";
					}
					map.put("short_title", titletemp);
					map.put("title", json.get("name").toString());
					map.put("size", json.get("size").toString() + "MB");
					String versiontemp = json.get("version").toString();
					if (versiontemp.length() > 8) {
						versiontemp = versiontemp.substring(0, 8) + "....";
					}
					map.put("short_version", "V" + versiontemp);
					map.put("version", "V" + json.get("version").toString());
					String introuduceTemp = json.get("introudution").toString();
					if (introuduceTemp.length() > 12) {
						introuduceTemp = introuduceTemp.substring(0, 12)
								+ "....";
					}
					map.put("short_description", introuduceTemp);
					map.put("description", json.get("introudution").toString());
					map.put("recommend_Decrible", json
							.get("recommend_Decrible").toString());
					map.put("DownloadUrl", json.get("DownloadUrl").toString());
					listItems.add(map);
				}
			} catch (Exception ex) {
				Utils.showMessageBox(Appfavorite.this, applicationData,
						"获取收藏夹列表出错.");
				ex.printStackTrace();
			}
		}

		@Override
		protected Void doInBackground(HashMap<String, Object>... params) {
			HashMap<String, Object> arguments = params[0];

			if (arguments.get("flag").toString() == "fd") {
				BuildfdItemsList(arguments.get("result").toString());
			} else if (arguments.get("flag").toString() == "fd") {
				// BuildfdItemsList(params[1]);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void void1) {
			setListViewAdapter();
		}
	}

	private void setListViewAdapter() {
		ListView listView = (ListView) findViewById(R.id.lv_favorite);
		favoriteListAdapter = new FavoriteListAdapter(this, listItems);
		listView.setAdapter(favoriteListAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// new Intent(this, AppDetailActivity.class);
				setCurrentFDItemInf(position);
				intent.putExtra("position", position);
				startActivity(intent);
			}
		});
	}

	private void setCurrentFDItemInf(int position) {
		FDItemInfModel temp = new FDItemInfModel();
		temp.setID(Integer.parseInt(listItems.get(position).get("id")
				.toString()));
		temp.setName(listItems.get(position).get("title").toString());
		temp.setApplication_Type_ID(true);
		temp.setVersion(listItems.get(position).get("version").toString());
		temp.setIntroudution(listItems.get(position).get("description")
				.toString());
		temp.setRecommend_Decrible(listItems.get(position)
				.get("recommend_Decrible").toString());
		temp.setSize(listItems.get(position).get("size").toString());
		temp.setDownLoadUrl(listItems.get(position).get("DownloadUrl")
				.toString());
		temp.setAppImage((Bitmap) listItems.get(position).get("image"));
		temp.setAppImageUrl(listItems.get(position).get("imageUrl").toString());
		applicationData.setcurrentFDItemInf(temp);
	}

	public Bitmap returnBitMap(String url) {
		URL myFileUrl = null;
		Bitmap bitmap = null;
		try {
			myFileUrl = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		try {
			HttpURLConnection conn = (HttpURLConnection) myFileUrl
					.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap;
	}
}
