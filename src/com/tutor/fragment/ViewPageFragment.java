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
package com.tutor.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.R.string;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

import com.totur.R;
import com.tutor.activity.AppDetailActivity;
import com.tutor.activity.JfAppDetailActivity;
import com.tutor.activity.MoreActivity;
import com.tutor.activity.MoreServiceActivity;
import com.tutor.activity.SlidingActivity;
import com.tutor.adapters.JFListAdapter;
import com.tutor.adapters.ProductListAdapter;
import com.tutor.model.AppConfiguration;
import com.tutor.model.ApplicationData;
import com.tutor.model.Category;
import com.tutor.utilities.GetDataFromWebService;
import com.tutor.utilities.OnGetDataListener;
import com.tutor.utilities.Utils;
import com.tutor.viewModel.FDItemInfModel;
import com.tutor.viewModel.FDItemTyeModel;
import com.tutor.viewModel.JFItemInfModel;

public class ViewPageFragment extends Fragment {

	private ImageView showLeft;
	private int offset = 0;// 动画图片偏移量
	private int bmpW;// 动画图片宽度
	private int currIndex = 0;// 当前页卡编号

	private List<Map<String, Object>> listItems;
	private List<Map<String, Object>> jf_listItems;
	private LinkedHashMap<String, Object> fdTypesMap;// 记录所有辅导类别
	private LinkedHashMap<String, Object> jfTypesMap;// 记录所有积分类别
	private ApplicationData applicationData;
	private TabHost tabHost;
	private int lastPage = 0;
	// just for debug
	private long timeCunsuming;

	private LayoutInflater inflater;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		applicationData = (ApplicationData) getActivity().getApplication();
		View mView = inflater.inflate(R.layout.tabmain, null);
		ViewPager vp = (ViewPager) mView.findViewById(R.id.vp_banner);
		ArrayList<View> listViews = new ArrayList<View>();
		View view1 = inflater.inflate(R.layout.layout_banner1, null);
		initialUserInfoPart1(view1);
		listViews.add(view1);
		View view2 = inflater.inflate(R.layout.layout_banner2, null);
		initialUserInfoPart2(view2);
		listViews.add(view2);
		vp.setOnPageChangeListener(new PageViewOnChangeListener());
		vp.setAdapter(new PageViewAdapter(listViews));

		this.inflater = inflater;
		// initialUserInfo(mView);
		showLeft = (ImageView) mView.findViewById(R.id.show_left);
		return mView;
	}

	private void initialUserInfoPart1(View view) {

		TextView tvCity = (TextView) view.findViewById(R.id.tvCity);
		tvCity.setText(applicationData.getcurrentUser().City);

		TextView tvCounty = (TextView) view.findViewById(R.id.tvCounty);
		tvCounty.setText(applicationData.getcurrentUser().County);

		TextView tvStation = (TextView) view.findViewById(R.id.tvTutorStation);
		tvStation.setText(applicationData.getcurrentUser().Tutor_Site);

		TextView tvStationType = (TextView) view
				.findViewById(R.id.tvTutorStationType);
		tvStationType.setText(applicationData.getcurrentUser().Tutor_Site_Type);
	}

	private void initialUserInfoPart2(View view) {
		TextView tvTutorName = (TextView) view.findViewById(R.id.tvTutorName);
		tvTutorName.setText(applicationData.getcurrentUser().Tutor_Name);

		TextView tvTutorCount = (TextView) view
				.findViewById(R.id.tv_tutorusers_count);
		tvTutorCount.setText(applicationData.getcurrentUser().Orders_Count);

		TextView tvTutorLastMonth = (TextView) view
				.findViewById(R.id.tv_tutorusers_lastmouth);
		tvTutorLastMonth
				.setText(applicationData.getcurrentUser().Current_Mouth_count);

		TextView tvTutorThisMonth = (TextView) view
				.findViewById(R.id.tv_tutorusers_thismouth);
		tvTutorThisMonth
				.setText(applicationData.getcurrentUser().Last_Mouth_Count);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initialMainTabHost(inflater, view);
		if (!initialfdTabHostFromWeb(inflater, view)) {
			Utils.showMessageBox(applicationData, "获取辅导列表失败.请重新登录");
		} else if (!initialjfTabHostFromWeb(inflater, view)) {
			Utils.showMessageBox(applicationData, "获取积分业务列表失败.请重新登录");
		}
	}

	private void initialMainTabHost(LayoutInflater inflater, View mView) {

		View sgTab = inflater.inflate(R.layout.tab_main_widebg, null);
		TextView sgtv = (TextView) sgTab.findViewById(R.id.mainTabTv);
		sgtv.setText("3G辅导");

		View yxTab = inflater.inflate(R.layout.tab_main_widebg, null);
		TextView yxtv = (TextView) yxTab.findViewById(R.id.mainTabTv);
		yxtv.setText("营销积分");
		TabHost yxTabHost = (TabHost) mView.findViewById(R.id.yxTabHost);
		yxTabHost.setup();
		yxTabHost.addTab(yxTabHost.newTabSpec("3GTab").setIndicator(sgTab)
				.setContent(R.id.tab_3gfudao));
		yxTabHost.addTab(yxTabHost.newTabSpec("YXJifen").setIndicator(yxTab)
				.setContent(R.id.tab_yxjifen));
		yxTabHost.setOnTabChangedListener(new OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				if (tabId.equals("3GTab")) {
					// ListView myTabLst = (ListView)
					// findViewById(R.id.myTabLst);

				} else {

				}
			}

		});
	}

	private boolean initialfdTabHostFromWeb(LayoutInflater inflater, View mView) {
		List<FDItemTyeModel> fdItems = GetfdItems();
		if (fdItems == null || fdItems.size() == 0) {
			return false;
		}
		tabHost = (TabHost) mView.findViewById(R.id.fd_tabhost);
		tabHost.setup();

		View tabItem = inflater.inflate(R.layout.tab_fd_widebg, null);

		TextView tbTabItemTitle = (TextView) tabItem
				.findViewById(R.id.tvTabItemTitle);

		for (int i = 0; i < 5 || i < fdItems.size() - 1; i++) {

			if (i == 4 || i == (fdItems.size() - 1)) {
				tabItem = inflater.inflate(R.layout.tab_fd_widebg, null);
				tbTabItemTitle = (TextView) tabItem
						.findViewById(R.id.tvTabItemTitle);
				tbTabItemTitle.setText("更多");
				tabHost.addTab(tabHost.newTabSpec("-1").setIndicator(tabItem)
						.setContent(R.id.tab5));

				break;
			}
			switch (i) {
			case 0:
				InitialFdItemInf(fdItems.get(i).getItem_ID());
				tbTabItemTitle.setText(fdItems.get(i).getItem_Name());
				tabHost.addTab(tabHost
						.newTabSpec(fdItems.get(i).getItem_Name())
						.setIndicator(tabItem).setContent(R.id.tab1));
				break;
			case 1:
				tabItem = inflater.inflate(R.layout.tab_fd_widebg, null);
				tbTabItemTitle = (TextView) tabItem
						.findViewById(R.id.tvTabItemTitle);
				tbTabItemTitle.setText(fdItems.get(i).getItem_Name());
				tabHost.addTab(tabHost
						.newTabSpec(fdItems.get(i).getItem_Name())
						.setIndicator(tabItem).setContent(R.id.tab2));
				break;
			case 2:
				tabItem = inflater.inflate(R.layout.tab_fd_widebg, null);
				tbTabItemTitle = (TextView) tabItem
						.findViewById(R.id.tvTabItemTitle);
				tbTabItemTitle.setText(fdItems.get(i).getItem_Name());
				tabHost.addTab(tabHost
						.newTabSpec(fdItems.get(i).getItem_Name())
						.setIndicator(tabItem).setContent(R.id.tab3));
				break;
			case 3:
				tabItem = inflater.inflate(R.layout.tab_fd_widebg, null);
				tbTabItemTitle = (TextView) tabItem
						.findViewById(R.id.tvTabItemTitle);
				tbTabItemTitle.setText(fdItems.get(i).getItem_Name());
				tabHost.addTab(tabHost
						.newTabSpec(fdItems.get(i).getItem_Name())
						.setIndicator(tabItem).setContent(R.id.tab4));
				break;
			}
		}

		tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				if (tabId != "-1") {
					int itemId = Integer.parseInt(fdTypesMap.get(tabId)
							.toString());
					InitialFdItemInf(itemId);
				} else {
					tabHost.setCurrentTab(0);
					Intent intent = new Intent(getActivity(),
							MoreActivity.class);
					intent.putExtra("Flag", "fd");
					startActivity(intent);
				}
			}
		});

		return true;
	}

	private boolean initialjfTabHostFromWeb(LayoutInflater inflater, View mView) {
		List<FDItemTyeModel> jfItems = GetjfItems();
		if (jfItems == null || jfItems.size() == 0) {
			return false;
		}
		final TabHost tabHost = (TabHost) mView.findViewById(R.id.jf_tabhost);
		tabHost.setup();
		View tabItem = inflater.inflate(R.layout.tab_fd_widebg, null);
		TextView tbTabItemTitle = (TextView) tabItem
				.findViewById(R.id.tvTabItemTitle);
		for (int i = 0; i < 3; i++) {

			if (i == 2) {
				tabItem = inflater.inflate(R.layout.tab_fd_widebg, null);
				tbTabItemTitle = (TextView) tabItem
						.findViewById(R.id.tvTabItemTitle);
				tbTabItemTitle.setText("更多");
				tabHost.addTab(tabHost.newTabSpec("-1").setIndicator(tabItem)
						.setContent(R.id.tab3));
				setJFListViewAdapter(tabHost);
				break;
			}
			switch (i) {
			case 0:
				InitialJfItemInf(jfItems.get(i).getItem_ID());
				tbTabItemTitle.setText(jfItems.get(i).getItem_Name());
				tabHost.addTab(tabHost
						.newTabSpec(jfItems.get(i).getItem_Name())
						.setIndicator(tabItem).setContent(R.id.tab1));
				break;
			case 1:
				tabItem = inflater.inflate(R.layout.tab_fd_widebg, null);
				tbTabItemTitle = (TextView) tabItem
						.findViewById(R.id.tvTabItemTitle);
				tbTabItemTitle.setText(jfItems.get(i).getItem_Name());
				tabHost.addTab(tabHost
						.newTabSpec(jfItems.get(i).getItem_Name())
						.setIndicator(tabItem).setContent(R.id.tab2));
				break;
			}
		}

		tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				if (tabId != "-1") {
					int itemId = Integer.parseInt(jfTypesMap.get(
							tabId.toString()).toString());
					InitialJfItemInf(itemId);
					setJFListViewAdapter(tabHost);
				} else {
					tabHost.setCurrentTab(0);
					Intent intent = new Intent(getActivity(),
							MoreActivity.class);

					intent.putExtra("Flag", "jf");
					startActivity(intent);
				}
			}
		});

		return true;
	}

	private List<FDItemTyeModel> GetfdItems() {
		List<FDItemTyeModel> items = new ArrayList<FDItemTyeModel>();
		String responseData = GetDataFromWebService.GetDataFromWeb(
				"getAppCategory", AppConfiguration.APP_TUTOR_SERVICE, null);
		fdTypesMap = new LinkedHashMap<String, Object>();
		try {
			JSONArray jsonArray = new JSONArray(responseData);
			applicationData.getappCategories().clear();
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject json = (JSONObject) jsonArray.get(i);
				// 记录辅导类别全局变量
				fdTypesMap.put(json.get("cname").toString(), json.get("cid")
						.toString());
				// 生成辅导类别对象
				FDItemTyeModel item = new FDItemTyeModel();
				item.setItem_ID(Integer.parseInt(json.get("cid").toString()));
				item.setItem_Name(json.get("cname").toString());
				items.add(item);
				applicationData.getappCategories().add(
						new Category("fd", item.getItem_Name(), String
								.valueOf(item.getItem_ID())));
			}
		} catch (Exception ex) {

			ex.printStackTrace();
		}
		return items;
	}

	private List<FDItemTyeModel> GetjfItems() {
		List<FDItemTyeModel> items = new ArrayList<FDItemTyeModel>();
		String result = GetDataFromWebService.GetDataFromWeb(
				"getServiceCategory", AppConfiguration.GUEST_TUTOR_SERVICE,
				null);
		jfTypesMap = new LinkedHashMap<String, Object>();
		try {
			applicationData.getserviceCategories().clear();
			JSONArray jsonArray = new JSONArray(result);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject json = (JSONObject) jsonArray.get(i);
				// 记录辅导类别全局变量
				jfTypesMap.put(json.get("name").toString(),
						json.get("Application_Type_ID").toString());
				// 生成辅导类别对象
				FDItemTyeModel item = new FDItemTyeModel();
				item.setItem_ID(Integer.parseInt(json
						.get("Application_Type_ID").toString()));
				item.setItem_Name(json.get("name").toString());
				items.add(item);
				applicationData.getserviceCategories().add(
						new Category("jf", item.getItem_Name(), String
								.valueOf(item.getItem_ID())));
			}
		} catch (Exception ex) {

		}
		return items;
	}

	private void InitialFdItemInf(int fdCategiry_id) {
		HashMap<String, Object> para = new LinkedHashMap<String, Object>();
		para.put("application_type_id", fdCategiry_id);
		String responseData = GetDataFromWebService.GetDataFromWeb("getApp",
				AppConfiguration.APP_TUTOR_SERVICE, para);
		if (responseData != null
				&& responseData != ""
				&& !responseData.toLowerCase(Locale.getDefault()).contains(
						"\"flag\":0")
				&& !responseData.toLowerCase(Locale.getDefault()).contains(
						"\"flag\":1")) {
			HashMap<String, Object> params = new LinkedHashMap<String, Object>();
			params.put("flag", "fd");
			params.put("result", responseData);
			DoAtBackground(params);
		} else {
			Log.d("调试", "获取辅导类别失败.responseData:\"" + responseData + "\"");
		}
	}

	private void InitialJfItemInf(int jfCategory_id) {
		HashMap<String, Object> para = new LinkedHashMap<String, Object>();
		para.put("application_type_id", jfCategory_id);
		String responseData = GetDataFromWebService.GetDataFromWeb(
				"getService", AppConfiguration.GUEST_TUTOR_SERVICE, para);
		if (responseData != null
				&& responseData != ""
				&& !responseData.toLowerCase(Locale.getDefault()).contains(
						"\"flag\":0")
				&& !responseData.toLowerCase(Locale.getDefault()).contains(
						"\"flag\":1")) {
			BuildjfItemsList(responseData);
		} else {
			Log.d("调试", "获取辅导类别失败.responseData:\"" + responseData + "\"");
		}
	}

	private void BuildjfItemsList(String result) {
		jf_listItems = new ArrayList<Map<String, Object>>();
		try {
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
				jf_listItems.add(map);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void setFDListViewAdapter(TabHost tabHost) {
		View view = (View) tabHost.getCurrentView();
		ListView listView = (ListView) view.findViewById(R.id.lv_products);
		listView.setAdapter(new ProductListAdapter(getActivity(), listItems));
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(getActivity(),
						AppDetailActivity.class);
				setCurrentFDItemInf(position);
				intent.putExtra("position", position);
				startActivity(intent);
			}
		});
	}

	private void setJFListViewAdapter(TabHost tabHost) {
		View view = (View) tabHost.getCurrentView();
		ListView listView = (ListView) view.findViewById(R.id.lv_products);
		listView.setAdapter(new JFListAdapter(getActivity(), jf_listItems));
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(getActivity(),
						JfAppDetailActivity.class);
				setCurrentJFItemInf(position);
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
				.get("recommend _Decrible").toString());
		temp.setSize(listItems.get(position).get("size").toString());
		temp.setDownLoadUrl(listItems.get(position).get("DownloadUrl")
				.toString());
		temp.setAppImage((Bitmap) listItems.get(position).get("image"));
		temp.setAppImageUrl(listItems.get(position).get("imageUrl").toString());
		applicationData.setcurrentFDItemInf(temp);
	}

	private void setCurrentJFItemInf(int position) {
		JFItemInfModel temp = new JFItemInfModel();
		temp.setID(Integer.parseInt(jf_listItems.get(position).get("id")
				.toString()));
		temp.setName(jf_listItems.get(position).get("title").toString());
		temp.setApplication_Type_ID(true);
		temp.setPoint(jf_listItems.get(position).get("point").toString());
		temp.setIntroudution(jf_listItems.get(position).get("description")
				.toString());
		temp.setRecommend_Decrible(jf_listItems.get(position)
				.get("recommend_Decrible").toString());
		temp.setAppImage((Bitmap) jf_listItems.get(position).get("image"));
		temp.setAppImageUrl(listItems.get(position).get("imageUrl").toString());
		temp.setAppImageUrl(jf_listItems.get(position).get("imageUrl")
				.toString());
		applicationData.setCurrentJFItemInf(temp);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		showLeft.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((SlidingActivity) getActivity()).showLeft();
			}
		});
	}

	private void DoAtBackground(HashMap<String, Object> params) {
		timeCunsuming = System.currentTimeMillis();

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
					Map<String, Object> map = new LinkedHashMap<String, Object>();
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
					String introuduceTemp = json.get("Introudution").toString();
					if (introuduceTemp.length() > 12) {
						introuduceTemp = introuduceTemp.substring(0, 12)
								+ "....";
					}
					map.put("short_description", introuduceTemp);
					map.put("description", json.get("Introudution").toString());
					map.put("recommend _Decrible",
							json.get("recommend _Decrible").toString());
					map.put("DownloadUrl", json.get("DownloadUrl").toString());
					listItems.add(map);
				}
			} catch (Exception ex) {
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
			setFDListViewAdapter(tabHost);
			Log.d("调试",
					"加载并显示应用集合"
							+ String.valueOf(System.currentTimeMillis()
									- timeCunsuming) + "毫秒");
		}
	}

	public class PageViewOnChangeListener implements OnPageChangeListener {

		int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量
		int two = one * 2;// 页卡1 -> 页卡3 偏移量

		@Override
		public void onPageSelected(int arg0) {
			if (lastPage > arg0) {// User Move to left

			} else if (lastPage < arg0) {// User Move to right

			}
			lastPage = arg0;
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {

			lastPage = arg0;
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

			lastPage = arg0;
		}
	}

	public class PageViewAdapter extends PagerAdapter {
		List<View> list = new ArrayList<View>();

		public PageViewAdapter(ArrayList<View> list) {
			this.list = list;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			ViewPager pViewPager = ((ViewPager) container);
			pViewPager.removeView(list.get(position));
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			ViewPager pViewPager = ((ViewPager) arg0);
			pViewPager.addView(list.get(arg1));
			return list.get(arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}
	}

}