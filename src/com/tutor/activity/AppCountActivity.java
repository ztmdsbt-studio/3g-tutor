package com.tutor.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

import com.totur.R;
import com.tutor.adapters.FDCountAdapter;
import com.tutor.adapters.JFCountAdapter;
import com.tutor.model.AppConfiguration;
import com.tutor.model.ApplicationData;
import com.tutor.utilities.GetDataFromWebService;
import com.tutor.utilities.OnGetDataListener;
import com.tutor.utilities.Utils;

public class AppCountActivity extends Activity {
	public static final String MIME_TYPE = "vnd.android.cursor.dir/vnd.exina.android.calendar.date";

	private ApplicationData applicationData;
	private List<Map<String, Object>> listItems; // 辅导统计
	private List<Map<String, Object>> plistItems; // 派单辅导
	private List<Map<String, Object>> jflistItems; // 营销积分
	TabHost tabHost;
	private int count = 1; // 辅导统计
	private int pcount = 2; // 派单辅导
	private int jfcount = 3; // 营销积分
	private ImageView countChooseDate;
	private ImageView countChoosepDate;
	private ImageView countChoosejfDate;

	private TextView countalluser; // 当月派单用户数
	private TextView countallmessage; // 短信辅导用户数

	JFCountAdapter jfcountAdapter;
	FDCountAdapter fdpcountAdapter;
	FDCountAdapter fdcountAdapter;

	Random random = new Random();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_count_detail);
		applicationData = (ApplicationData) getApplicationContext();		
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
		getMenuInflater().inflate(R.menu.user_info, menu);
		return true;
	}

	public void backToPrevious(View view) {
		super.onBackPressed();
	}

	private void initialMainTabHost() {
		tabHost = (TabHost) this.findViewById(R.id.app_count_tabhost);
		tabHost.setup();
		View tabItem = getLayoutInflater().inflate(R.layout.tab_count_widebg,
				null);
		TextView tbTabItemTitle = (TextView) tabItem
				.findViewById(R.id.mainTabTv);

		InitialCountItemInf("");
		InitialPCountItemInf("");
		InitialJFCountItemInf("");

		tbTabItemTitle.setText("辅导统计");
		tabHost.addTab(tabHost.newTabSpec("辅导统计").setIndicator(tabItem)
				.setContent(R.id.count_tab1));

		tabItem = getLayoutInflater().inflate(R.layout.tab_count_widebg, null);
		tbTabItemTitle = (TextView) tabItem.findViewById(R.id.mainTabTv);
		tbTabItemTitle.setText("派单辅导结果统计");
		tabHost.addTab(tabHost.newTabSpec("派单辅导结果统计").setIndicator(tabItem)
				.setContent(R.id.count_tab2));

		tabItem = getLayoutInflater().inflate(R.layout.tab_count_widebg, null);
		tbTabItemTitle = (TextView) tabItem.findViewById(R.id.mainTabTv);
		tbTabItemTitle.setText("营销积分统计");
		tabHost.addTab(tabHost.newTabSpec("营销积分统计").setIndicator(tabItem)
				.setContent(R.id.count_tab3));
		setCountListViewAdapter(tabHost);

		tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				if (tabId != "-1") {
					if (tabId == "辅导统计") {
						setCountListViewAdapter(tabHost);
					} else if (tabId == "派单辅导结果统计") {
						setPCountListViewAdapter(tabHost);
					} else if (tabId == "营销积分统计") {
						setJFCountListViewAdapter(tabHost);
					}
					// InitialFdItemInf(itemId);
				} else {
					// TODO: GetAllApplications.
				}
			}
		});

	}

	// 统计辅导
	private void InitialCountItemInf(String searchDate) {
		HashMap<String, Object> para = new LinkedHashMap<String, Object>();
		// para.put("tid", "9674");
		para.put("tid", applicationData.getcurrentUser().userid);
		para.put("time", searchDate);
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
					BuildCountItemsList(responseData);
				} else {
					Utils.showMessageBox(AppCountActivity.this,
							applicationData, "获取收藏列表失败.");
					Log.d("调试", "获取收藏列表失败. response:\"" + responseData + "\"");
				}
			}
		});
		service.GetDataFromWebAsync("tutorStatistics",
				AppConfiguration.GUEST_TUTOR_SERVICE, para);
	}

	// 派单辅导结果统计
	private void InitialPCountItemInf(String searchDate) {
		HashMap<String, Object> para = new LinkedHashMap<String, Object>();
		// para.put("tid", "9674");
		// para.put("City_id", "40");
		para.put("tid", applicationData.getcurrentUser().userid);
		para.put("City_id", applicationData.getcurrentUser().City_id);
		para.put("Time", searchDate);
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
					BuildpCountItemsList(responseData);
				} else {
					Utils.showMessageBox(AppCountActivity.this,
							applicationData, "获取收藏列表失败.");
					Log.d("调试", "获取收藏列表失败. response:\"" + responseData + "\"");
				}
			}
		});

		service.GetDataFromWebAsync("tutorResultStatistics",
				AppConfiguration.GUEST_TUTOR_SERVICE, para);
	}

	// 营销积分统计
	private void InitialJFCountItemInf(String searchDate) {
		HashMap<String, Object> para = new LinkedHashMap<String, Object>();
		// para.put("tid", "9674");
		para.put("tid", applicationData.getcurrentUser().userid);
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
					BuildJFCountItemsList(responseData);
				} else {
					Utils.showMessageBox(AppCountActivity.this,
							applicationData, "获取收藏列表失败.");
					Log.d("调试", "获取收藏列表失败. response:\"" + responseData + "\"");
				}
			}
		});

		service.GetDataFromWebAsync("serviceStatistics",
				AppConfiguration.GUEST_TUTOR_SERVICE, para);
	}

	private void BuildCountItemsList(String result) {
		listItems = new ArrayList<Map<String, Object>>();
		try {
			JSONArray jsonArray = new JSONArray(result);
			int count_user = 0;
			int count_download = 0;
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject json = (JSONObject) jsonArray.get(i);
				Map<String, Object> map = new LinkedHashMap<String, Object>();
				String strdate = json.get("time").toString();
				if (strdate.length() > 8) {
					strdate = strdate.substring(0, 4) + "-"
							+ strdate.substring(4, 6) + "-"
							+ strdate.substring(6);
				}
				map.put("count_date", strdate);
				count_user += Integer.parseInt(json
						.get("tutor_Customter_count").toString());
				map.put("count_user", json.get("tutor_Customter_count")
						.toString());
				count_download += Integer.parseInt(json.get(
						"tutor_Customter_count").toString());
				map.put("count_download", json.get("dowload_count").toString());
				listItems.add(map);
			}
			countalluser.setText(String.valueOf(count_user));
			countallmessage.setText(String.valueOf(count_download));
		} catch (Exception ex) {
			System.out.print(ex.toString());
		}
	}

	private void BuildpCountItemsList(String result) {
		plistItems = new ArrayList<Map<String, Object>>();
		try {
			JSONArray jsonArray = new JSONArray(result);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject json = (JSONObject) jsonArray.get(i);
				Map<String, Object> map = new LinkedHashMap<String, Object>();
				String strdate = json.get("time").toString();
				if (strdate.length() > 8) {
					strdate = strdate.substring(0, 4) + "-"
							+ strdate.substring(4, 6) + "-"
							+ strdate.substring(6);
				}
				map.put("count_date", strdate);
				map.put("count_user", json.get("tutor_Customter_count")
						.toString());
				map.put("count_download", json.get("message_count").toString());
				plistItems.add(map);
			}
		} catch (Exception ex) {
			System.out.print(ex.toString());
		}
	}

	private void BuildJFCountItemsList(String result) {
		jflistItems = new ArrayList<Map<String, Object>>();
		try {
			JSONArray jsonArray = new JSONArray(result);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject json = (JSONObject) jsonArray.get(i);
				Map<String, Object> map = new LinkedHashMap<String, Object>();
				String strdate = json.get("time").toString();
				if (strdate.length() > 8) {
					strdate = strdate.substring(0, 4) + "-"
							+ strdate.substring(4, 6) + "-"
							+ strdate.substring(6);
				}
				map.put("count_date", strdate);
				map.put("count_user", json.get("type_name").toString());
				map.put("count_download", json.get("recommend_count")
						.toString());
				map.put("count_order", json.get("type_id").toString());
				jflistItems.add(map);
			}
		} catch (Exception ex) {
			System.out.print(ex.toString());
		}
	}

	private void setCountListViewAdapter(TabHost tabHost) {
		View view = (View) tabHost.getCurrentView();
		ListView listView = (ListView) findViewById(R.id.lv_count);
		fdcountAdapter = new FDCountAdapter(this, listItems);
		listView.setAdapter(fdcountAdapter);
	}

	private void setPCountListViewAdapter(TabHost tabHost) {
		View view = (View) tabHost.getCurrentView();
		ListView listView = (ListView) findViewById(R.id.lv_counta);
		fdpcountAdapter = new FDCountAdapter(this, plistItems);
		listView.setAdapter(fdpcountAdapter);
	}

	private void setJFCountListViewAdapter(TabHost tabHost) {
		View view = (View) tabHost.getCurrentView();
		ListView listView = (ListView) findViewById(R.id.lv_countjf);
		jfcountAdapter = new JFCountAdapter(this, jflistItems);
		listView.setAdapter(jfcountAdapter);
	}

	/*
	 * 初始化日历时间
	 */
	private void InitalDate() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM");//
		java.util.Date currTime = new java.util.Date();
		String strDate = df.format(currTime);
		showDate.setText(strDate);
		showpDate.setText(strDate);
		showjfDate.setText(strDate);

		Calendar cal = Calendar.getInstance();
		mYear = cal.get(Calendar.YEAR);
		mMonth = cal.get(Calendar.MONTH) + 1;
		mDay = cal.get(Calendar.DATE) + 1;
		pmYear = cal.get(Calendar.YEAR);
		pmMonth = cal.get(Calendar.MONTH) + 1;
		pmDay = cal.get(Calendar.DATE) + 1;
		jfmYear = cal.get(Calendar.YEAR);
		jfmMonth = cal.get(Calendar.MONTH) + 1;
		jfmDay = cal.get(Calendar.DATE) + 1;
	}

	private TextView showDate;
	private int mYear;
	private int mMonth;
	private int mDay;
	private TextView showpDate;
	private int pmYear;
	private int pmMonth;
	private int pmDay;
	private TextView showjfDate;
	private int jfmYear;
	private int jfmMonth;
	private int jfmDay;

	/**
	 * 设置辅导统计日期
	 */
	private void setDateTime() {
		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 设置派单辅导日期
	 */
	private void setpDateTime() {
		final Calendar c = Calendar.getInstance();
		pmYear = c.get(Calendar.YEAR);
		pmMonth = c.get(Calendar.MONTH);
		pmDay = c.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 设置营销积分日期
	 */
	private void setjfDateTime() {
		final Calendar c = Calendar.getInstance();
		jfmYear = c.get(Calendar.YEAR);
		jfmMonth = c.get(Calendar.MONTH);
		jfmDay = c.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * /** 更新日期显示
	 */
	private void updateDateDisplay(int id) {
		switch (id) {
		case 1:
			showDate.setText(new StringBuilder()
					.append(mYear)
					.append("-")
					.append((mMonth + 1) < 10 ? "0" + (mMonth + 1)
							: (mMonth + 1)).append(""));
			InitialCountItemInf(String.valueOf(mYear) + String.valueOf(mMonth)
					+ String.valueOf(mDay));
			// testCountItemsList(random.nextInt(50));
			setCountListViewAdapter(tabHost);
			break;
		case 2:
			showpDate.setText(new StringBuilder()
					.append(pmYear)
					.append("-")
					.append((pmMonth + 1) < 10 ? "0" + (pmMonth + 1)
							: (pmMonth + 1)).append(""));
			InitialPCountItemInf(String.valueOf(pmYear)
					+ String.valueOf(pmMonth) + String.valueOf(pmDay));
			// testpCountItemsList(random.nextInt(50));
			setPCountListViewAdapter(tabHost);
			break;
		case 3:
			showjfDate.setText(new StringBuilder()
					.append(jfmYear)
					.append("-")
					.append((jfmMonth + 1) < 10 ? "0" + (jfmMonth + 1)
							: (jfmMonth + 1)).append(""));
			InitialJFCountItemInf(String.valueOf(jfmYear)
					+ String.valueOf(jfmMonth) + String.valueOf(jfmDay));
			// testJFCountItemsList(random.nextInt(50));
			setJFCountListViewAdapter(tabHost);
			break;
		}
	}

	/**
	 * 日期控件的事件
	 */
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			updateDateDisplay(count);
		}
	};

	private DatePickerDialog.OnDateSetListener pmDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			pmYear = year;
			pmMonth = monthOfYear;
			pmDay = dayOfMonth;
			updateDateDisplay(pcount);
		}
	};

	private DatePickerDialog.OnDateSetListener jfmDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			jfmYear = year;
			jfmMonth = monthOfYear;
			jfmDay = dayOfMonth;
			updateDateDisplay(jfcount);
		}
	};

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = new DatePickerDialog(this, pmDateSetListener, pmYear,
				pmMonth, pmDay);
		switch (id) {
		case 1:
			// startActivityForResult(new
			// Intent(Intent.ACTION_VIEW).setDataAndType(null, MIME_TYPE), 100);
			dialog = new DatePickerDialog(this, mDateSetListener, mYear,
					mMonth, mDay);
			break;
		case 2:
			dialog = new DatePickerDialog(this, pmDateSetListener, pmYear,
					pmMonth, pmDay);
			break;
		case 3:
			dialog = new DatePickerDialog(this, jfmDateSetListener, jfmYear,
					jfmMonth, jfmDay);
			break;
		}
		return dialog;
	}
	private void initial() {
		countalluser = (TextView) findViewById(R.id.tv_count_all_user);
		countallmessage = (TextView) findViewById(R.id.tv_count_all_message);
		initialMainTabHost();

		showDate = (TextView) findViewById(R.id.tv_count_date);
		countChooseDate = (ImageView) findViewById(R.id.tv_choose_count_date);
		countChooseDate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Message msg = new Message();
				msg.what = count;
				dateandtimeHandler.sendMessage(msg);
			}
		});

		showpDate = (TextView) findViewById(R.id.tv_count_p_date);
		countChoosepDate = (ImageView) findViewById(R.id.tv_choose_p_count_date);
		countChoosepDate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Message msg = new Message();
				msg.what = pcount;
				dateandtimeHandler.sendMessage(msg);
			}
		});

		showjfDate = (TextView) findViewById(R.id.tv_count_jf_date);
		countChoosejfDate = (ImageView) findViewById(R.id.tv_choose_jf_count_date);
		countChoosejfDate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Message msg = new Message();
				msg.what = jfcount;
				dateandtimeHandler.sendMessage(msg);
			}
		});

		InitalDate();
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case 1:
			((DatePickerDialog) dialog).updateDate(mYear, mMonth, mDay);
			break;
		case 2:
			((DatePickerDialog) dialog).updateDate(pmYear, pmMonth, pmDay);
			break;
		case 3:
			((DatePickerDialog) dialog).updateDate(jfmYear, jfmMonth, jfmDay);
			break;
		}
	}

	/**
	 * 处理日期和时间控件的Handler
	 */
	Handler dateandtimeHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			showDialog(msg.what);
		}
	};

	// mockup
	private void testCountItemsList(int a) {
		listItems = new ArrayList<Map<String, Object>>();
		for (int i = 6; i < a; i++) {
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			map.put("count_date", "11-" + i);
			map.put("count_user", String.valueOf(i));
			map.put("count_download", String.valueOf(i));
			listItems.add(map);
		}
	}

	private void testpCountItemsList(int a) {
		plistItems = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < a; i++) {
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			map.put("count_date", "11-" + i);
			map.put("count_user", String.valueOf(i));
			map.put("count_download", String.valueOf(i));
			plistItems.add(map);
		}
	}

	private void testJFCountItemsList(int a) {
		jflistItems = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < a; i++) {
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			map.put("count_date", "11-" + i);
			map.put("count_user", String.valueOf(i));
			map.put("count_download", String.valueOf(i));
			map.put("count_order", String.valueOf(i));
			jflistItems.add(map);
		}
	}
}
