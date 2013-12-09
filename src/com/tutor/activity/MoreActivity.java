package com.tutor.activity;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.totur.R;
import com.tutor.adapters.AppCategoriesAdapter;
import com.tutor.model.ApplicationData;
import com.tutor.model.Category;

public class MoreActivity extends Activity {
	private List<Category> categories;
	private ListView lvCategories;
	private EditText edCondition;
	private Button btnSearch;
	private ApplicationData applicationData;
	private String pageType;
	// just for debug
	private long timeCunsuming;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_more);
		applicationData = (ApplicationData) getApplicationContext();
	}

	private void initial() {
		Bundle extras = getIntent().getExtras();
		this.pageType = extras.getString("Flag");
		if (pageType.equals("fd")) {
			this.categories = applicationData.getappCategories();
		} else if (pageType.equals("jf")) {
			this.categories = applicationData.getserviceCategories();
		}
		this.initialElement();
	}

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

	private void initialElement() {
		edCondition = (EditText) findViewById(R.id.et_search_condition);
		btnSearch = (Button) findViewById(R.id.btn_search_app);
		edCondition.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus
						&& edCondition.getText().toString()
								.equals("输入要查找的应用名称")) {
					edCondition.setText("");
				} else if (edCondition.getText().toString().equals("")) {
					edCondition.setText("输入要查找的应用名称");
				}
			}
		});
		lvCategories = (ListView) findViewById(R.id.lv_app_categories);

		lvCategories
				.setAdapter(new AppCategoriesAdapter(this, this.categories));
		this.setCategoryListOnItemClickListener();
		this.setOnSearchListener();
	}

	public void setCategoryListOnItemClickListener() {
		lvCategories.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(MoreActivity.this,
						SearchResultActivity.class);
				intent.putExtra("PageType", MoreActivity.this.pageType);
				intent.putExtra("Flag", "Category");
				intent.putExtra("CategoryId", categories.get(position).getid());
				intent.putExtra("CategoryName", categories.get(position).getname());

				MoreActivity.this.startActivity(intent);
			}
		});
	}

	public void setOnSearchListener() {
		OnClickListener listener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MoreActivity.this,
						SearchResultActivity.class);
				intent.putExtra("PageType", MoreActivity.this.pageType);
				intent.putExtra("Flag", "App");
				intent.putExtra("SearchFilter",
						MoreActivity.this.getsearchFilter());
				startActivity(intent);
			}
		};
		btnSearch.setOnClickListener(listener);
		// btnRefresh.setOnClickListener(listener);
	}

	public void backToPrevious(View view) {
		super.onBackPressed();
	}

	public String getsearchFilter() {
		return edCondition.getText().toString();
	}
	
	public void refreshActivity(View view){
		initial();
	}
}
