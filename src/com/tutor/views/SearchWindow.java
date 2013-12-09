package com.tutor.views;

import java.util.List;

import com.totur.R;
import com.tutor.activity.AppDetailActivity;
import com.tutor.adapters.AppCategoriesAdapter;
import com.tutor.model.Category;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class SearchWindow extends RelativeLayout {

	private List<Category> categories;
	private ListView lvCategories;
	private ImageView btnHidden;
	private EditText edCondition;
	private Button btnSearch;

	public SearchWindow(Context context) {
		super(context);
	}

	public SearchWindow(Context context, List<Category> categories) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.layout_search_window, this);
		this.categories = categories;
		this.initialElement();
	}

	private void initialElement() {
		btnHidden = (ImageView) findViewById(R.id.iv_hide_window);
		edCondition = (EditText)findViewById(R.id.et_search_condition);
		btnSearch = (Button)findViewById(R.id.btn_search_app);
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
		lvCategories.setAdapter(new AppCategoriesAdapter(getContext(),
				this.categories));
	}

	public void setCategoryListOnItemClickListener(OnItemClickListener listener) {
		lvCategories.setOnItemClickListener(listener);
	}

	public void setOnHiddenClickListener(OnClickListener listener) {
		btnHidden.setOnClickListener(listener);
	}

	public void setOnSearchListener(OnClickListener listener){
		btnSearch.setOnClickListener(listener);
//		btnRefresh.setOnClickListener(listener);
	}

	public String getsearchFilter() {
		return edCondition.getText().toString();
	}
}
