package com.tutor.adapters;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.totur.R;

public class JFCountAdapter extends BaseAdapter{
	private Context context;
	private List<Map<String, Object>> listItems;
	private LayoutInflater listContainer;
	
	public final class ListItemView {
		public TextView count_date;
		public TextView count_user;
		public TextView count_download;
		public TextView count_order;
	}
	
	public JFCountAdapter(Context context,
			List<Map<String, Object>> listItems) {
		this.context = context;
		listContainer = LayoutInflater.from(context);
		this.listItems = listItems;
	}

	@Override
	public int getCount() {
		return listItems.size();
	}

	@Override
	public Object getItem(int position) {
		return listItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ListItemView listItemView = null;
		final int itemPosition = position;

		if (convertView == null) {
			listItemView = new ListItemView();
			convertView = listContainer.inflate(
					R.layout.jfcount_list_item_style, null);

			listItemView.count_date = (TextView) convertView
					.findViewById(R.id.tv_count_date);
			listItemView.count_user = (TextView) convertView
			.findViewById(R.id.tv_count_user);
			listItemView.count_download = (TextView) convertView
			.findViewById(R.id.tv_count_down);
			listItemView.count_order = (TextView) convertView
			.findViewById(R.id.tv_count_order);
			convertView.setTag(listItemView);
		} else {
			listItemView = (ListItemView) convertView.getTag();
		}

		listItemView.count_date.setText(listItems.get(position).get("count_date")
				.toString());
		listItemView.count_user.setText(listItems.get(position).get("count_user")
				.toString());
		listItemView.count_download.setText(listItems.get(position).get("count_download")
				.toString());
		listItemView.count_order.setText(listItems.get(position).get("count_order")
				.toString());
//		listItemView.doTutor.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				
//			}
//		});

		return convertView;
	}
}
