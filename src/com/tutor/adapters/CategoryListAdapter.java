package com.tutor.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.totur.R;
import com.tutor.model.Category;

public class CategoryListAdapter extends BaseAdapter {

	private List<Category> listItems;
	private LayoutInflater listContainer;

	public final class ListItemView {
		public TextView categoryName;
	}

	public CategoryListAdapter(Context context,
			List<Category> listItems) {

		listContainer = LayoutInflater.from(context);
		this.listItems = listItems;
	}

	@Override
	public int getCount() {
		if (listItems != null) {
			return listItems.size();
		} else {
			return 0;
		}
	}

	@Override
	public Object getItem(int position) {
		if (listItems != null) {
			return listItems.get(position);
		}else {
			return null;
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ListItemView listItemView = null;
		if (convertView == null) {
			listItemView = new ListItemView();
			convertView = listContainer.inflate(
					R.layout.category_item_style, null);

			listItemView.categoryName = (TextView) convertView
					.findViewById(R.id.tv_category_name);
			convertView.setTag(listItemView);
		} else {
			listItemView = (ListItemView) convertView.getTag();  
		}

		listItemView.categoryName.setText(listItems.get(position).getname());

		return convertView;
	}

}
