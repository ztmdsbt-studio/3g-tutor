package com.tutor.activity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.totur.R;
import com.tutor.adapters.JFListAdapter;
import com.tutor.adapters.ProductListAdapter;
import com.tutor.model.AppConfiguration;
import com.tutor.model.ApplicationData;
import com.tutor.utilities.GetDataFromWebService;
import com.tutor.utilities.OnGetDataListener;
import com.tutor.viewModel.FDItemInfModel;
import com.tutor.viewModel.JFItemInfModel;

public class SearchResultActivity extends Activity {

	private String searchFilter = "";
	private String categoryId;
	private String flag;
	private ApplicationData applicationData;
	private ArrayList<Map<String, Object>> matchedResults;
	private GetDataFromWebService service;
	private ListView appList;
	private AlertDialog dialog;
	private String pageType;
	private static final ScheduledExecutorService executor = Executors
			.newSingleThreadScheduledExecutor();
	private AlertDialog alertDialog;
	private boolean isJumpFromSearchPage;
	private String categoryName = "搜索";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_serach_result);
		applicationData = (ApplicationData) getApplicationContext();
		isJumpFromSearchPage = true;
	}

	private void initial() {
		Bundle extras = getIntent().getExtras();
		if (extras.getString("Flag") == null) {
			return;
		}
		this.flag = extras.getString("Flag");
		this.pageType = extras.getString("PageType");
		if (this.pageType == null) {
			this.pageType = "fd";
		}
		if (extras.getString("Flag").equals("Category")) {
			this.categoryId = extras.getString("CategoryId");
			this.categoryName = extras.getString("CategoryName");
		} else if (extras.getString("Flag").equals("App")) {
			this.searchFilter = extras.getString("SearchFilter");
		}

		initialElement();
	}

	@Override
	protected void onResume() {
		super.onResume();
		applicationData.setCurrentActivity(this);
		initial();
		if (isJumpFromSearchPage) {
			searchFromServer();
		}
	}

	@Override
	protected void onPause() {
		clearReferences();
		super.onPause();
		this.isJumpFromSearchPage = false;
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

	private void searchFromServer() {
		if (service == null) {
			service = new GetDataFromWebService();
		}

		final String mFilter = searchFilter;
		service.setOnGetDataListener(new OnGetDataListener() {

			@Override
			public void onGetDataCompleted(String responseData) {
				if (SearchResultActivity.this.pageType.equals("fd")) {
					JsonToFdList(responseData);
				} else if (SearchResultActivity.this.pageType.equals("jf")) {
					JsonToJfList(responseData);
				}
				refreshElement(mFilter);
				if (dialog != null) {
					dialog.dismiss();
				}
			}
		});
		if (dialog != null) {
			dialog.dismiss();
		}
		dialog = new AlertDialog.Builder(
				(Context) (getParent() == null ? applicationData
						.getCurrentActivity() : getParent())).create();
		dialog.setMessage("正在查找...");
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
		String action = "";
		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("key", searchFilter);
		if (flag.equals("Category")) {
			params.put("cid", categoryId);
		}
		if (this.pageType.equals("fd")) {
			action = "searchApp";
			params.put("page", 1);
		} else if (this.pageType.equals("jf")) {
			action = "searchService";
		}

		service.GetDataFromWebAsync(action,
				AppConfiguration.GUEST_TUTOR_SERVICE, params);
	}

	protected void refreshElement(String mFilter) {
		if (this.pageType.equals("fd")) {
			appList.setAdapter(new ProductListAdapter(this, matchedResults));
		} else {
			appList.setAdapter(new JFListAdapter(this, matchedResults));
		}
		RelativeLayout rlNoResult = (RelativeLayout) findViewById(R.id.rl_noresult);
		if (matchedResults.size() == 0) {
			rlNoResult.setVisibility(View.VISIBLE);
			((TextView) findViewById(R.id.tv_search_noresult))
					.setText("没有找到与\"" + mFilter + "\"相关的结果");
			appList.setVisibility(View.GONE);
		} else {
			appList.setVisibility(View.VISIBLE);
			rlNoResult.setVisibility(View.GONE);
		}
	}

	public void JsonToFdList(String jsonstr) {
		try {
			if (matchedResults == null) {
				matchedResults = new ArrayList<Map<String, Object>>();
			}
			JSONArray array = new JSONArray(jsonstr);
			matchedResults.clear();
			for (int i = 0; i < array.length(); i++) {
				JSONObject json = (JSONObject) array.get(i);
				Map<String, Object> map = new LinkedHashMap<String, Object>();
				map.put("id", Integer.parseInt(json.get("id").toString()));
				map.put("imageUrl", json.get("ImageUrl").toString());
				map.put("image", BitmapFactory.decodeResource(getResources(),
						R.drawable.ic_launcher));
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
					introuduceTemp = introuduceTemp.substring(0, 12) + "....";
				}
				map.put("short_description", introuduceTemp);
				map.put("description", json.get("introudution").toString());
				map.put("recommend _Decrible", json.get("recommend_Decrible")
						.toString());
				map.put("DownloadUrl", json.get("DownloadUrl").toString());
				matchedResults.add(map);
			}
		} catch (Exception e) {
			showMessageBox("调用3G辅导列表发生错误.");
			e.printStackTrace();
		}
	}

	private void JsonToJfList(String result) {
		try {
			if (matchedResults == null) {
				matchedResults = new ArrayList<Map<String, Object>>();
			}
			matchedResults.clear();
			JSONArray jsonArray = new JSONArray(result);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject json = (JSONObject) jsonArray.get(i);
				Map<String, Object> map = new LinkedHashMap<String, Object>();
				map.put("id", Integer.parseInt(json.get("id").toString()));
				map.put("imageUrl", json.get("image").toString());
				map.put("image", BitmapFactory.decodeResource(getResources(),
						R.drawable.ic_launcher));
				String titletemp = json.get("name").toString();
				if (titletemp.length() > 8) {
					titletemp = titletemp.substring(0, 8) + "....";
				}
				map.put("short_title", titletemp);
				map.put("title", json.get("name").toString());
				map.put("point", json.get("point").toString());
				map.put("danwei", "分");
				String introuduceTemp = json.get("introudution").toString();
				if (introuduceTemp.length() > 12) {
					introuduceTemp = introuduceTemp.substring(0, 12) + "....";
				}
				map.put("short_description", introuduceTemp);
				map.put("description", json.get("introudution").toString());
				map.put("recommend_Decrible", json.get("recommend_Decrible")
						.toString());
				matchedResults.add(map);
			}
		} catch (Exception ex) {
			showMessageBox("调用积分业务列表发生错误.");
			ex.printStackTrace();
		}
	}

	private void initialElement() {
		appList = (ListView) findViewById(R.id.lv_applist);
		TextView tvTitle = (TextView)findViewById(R.id.tv_search_title);
		tvTitle.setText(this.categoryName);
		final EditText edCondition = (EditText) findViewById(R.id.et_search_condition);
		edCondition.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus
						&& edCondition.getText().toString()
								.equals("输入要查找的应用名称")) {
					edCondition.setText("");
				} else if (edCondition.getText().equals("")) {
					edCondition.setText("输入要查找的应用名称");
				}
			}
		});
		edCondition.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable editValue) {
				if (editValue.toString().equals("输入要查找的应用名称")) {
					searchFilter = "";
				} else {
					searchFilter = editValue.toString();
				}
			}
		});
		appList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (SearchResultActivity.this.pageType.equals("fd")) {
					Intent intent = new Intent(SearchResultActivity.this,
							AppDetailActivity.class);
					setCurrentFDItemInf(position);
					intent.putExtra("position", position);
					startActivity(intent);
				} else if (SearchResultActivity.this.pageType.equals("jf")) {
					Intent intent = new Intent(SearchResultActivity.this,
							JfAppDetailActivity.class);
					setCurrentJFItemInf(position);
					intent.putExtra("position", position);
					startActivity(intent);
				}
			}
		});

		findViewById(R.id.btn_search_app).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						searchFromServer();
					}
				});
	}

	private void setCurrentFDItemInf(int position) {
		FDItemInfModel temp = new FDItemInfModel();
		temp.setID(Integer.parseInt(matchedResults.get(position).get("id")
				.toString()));
		temp.setName(matchedResults.get(position).get("title").toString());
		temp.setApplication_Type_ID(true);
		temp.setVersion(matchedResults.get(position).get("version").toString());
		temp.setIntroudution(matchedResults.get(position).get("description")
				.toString());
		temp.setRecommend_Decrible(matchedResults.get(position)
				.get("recommend _Decrible").toString());
		temp.setSize(matchedResults.get(position).get("size").toString());
		temp.setDownLoadUrl(matchedResults.get(position).get("DownloadUrl")
				.toString());
		temp.setAppImage((Bitmap) matchedResults.get(position).get("image"));
		temp.setAppImageUrl(matchedResults.get(position).get("imageUrl").toString());
		applicationData.setcurrentFDItemInf(temp);
	}

	private void setCurrentJFItemInf(int position) {
		JFItemInfModel temp = new JFItemInfModel();
		temp.setID(Integer.parseInt(matchedResults.get(position).get("id")
				.toString()));
		temp.setName(matchedResults.get(position).get("title").toString());
		temp.setApplication_Type_ID(true);
		temp.setPoint(matchedResults.get(position).get("point").toString());
		temp.setIntroudution(matchedResults.get(position).get("description")
				.toString());
		temp.setRecommend_Decrible(matchedResults.get(position)
				.get("recommend_Decrible").toString());
		temp.setAppImage((Bitmap) matchedResults.get(position).get("image"));
		temp.setAppImageUrl(matchedResults.get(position).get("imageUrl")
				.toString());
		applicationData.setCurrentJFItemInf(temp);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.serach_result, menu);
		return true;
	}

	public void backToPrevious(View view) {
		super.onBackPressed();
	}
	
	public void refreshActivity(View view){
		initial();
		searchFromServer();
	}

	private void showMessageBox(String errorMsg) {
		try {
			if (alertDialog != null) {
				alertDialog.dismiss();
			}
			alertDialog = new AlertDialog.Builder(
					(Context) (getParent() == null ? applicationData
							.getCurrentActivity() : getParent())).create();
			alertDialog.setMessage(errorMsg);
			alertDialog.show();
			Runnable runnable = new Runnable() {

				@Override
				public void run() {
					alertDialog.dismiss();
				}
			};

			executor.schedule(runnable, 5, TimeUnit.SECONDS);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
