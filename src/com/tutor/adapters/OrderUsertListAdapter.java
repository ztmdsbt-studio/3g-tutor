package com.tutor.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.totur.R;
import com.tutor.model.OrderUser;

public class OrderUsertListAdapter extends BaseAdapter {
	private Context context;
	private List<OrderUser> orderUsers;
	private LayoutInflater listContainer;

	public final class OrderUserView {
		public TextView orderUserTel;
		public TextView orderUserPlatform;
		public TextView orderUserCount;
		public CheckBox isOrderUserChecked;
	}

	public OrderUsertListAdapter(Context context, List<OrderUser> orderUsers) {
		this.context = context;
		listContainer = LayoutInflater.from(context);
		this.orderUsers = orderUsers;
		if (this.orderUsers == null) {
			this.orderUsers = new ArrayList<OrderUser>();
		}
	}

	@Override
	public int getCount() {
		if (orderUsers != null) {
			return orderUsers.size();
		} else {
			return 0;
		}
	}

	@Override
	public Object getItem(int position) {
		if (orderUsers != null) {
			return orderUsers.get(position);
		} else {
			return null;
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		OrderUserView orderUserView = null;
		final int currentPosition = position;
		// if (convertView == null) {
		orderUserView = new OrderUserView();
		convertView = listContainer.inflate(R.layout.orderuser_list_item_style,
				null);

		orderUserView.orderUserTel = (TextView) convertView
				.findViewById(R.id.tv_orderuser_tel);
		orderUserView.orderUserPlatform = (TextView) convertView
				.findViewById(R.id.tv_orderuser_platform);
		orderUserView.orderUserCount = (TextView) convertView
				.findViewById(R.id.tv_orderuser_count);
		orderUserView.isOrderUserChecked = (CheckBox) convertView
				.findViewById(R.id.cb_orderUsers);
		convertView.setTag(orderUserView);
		// } else {
		// orderUserView = (OrderUserView) convertView.getTag();
		// }
		orderUserView.isOrderUserChecked.setChecked(orderUsers.get(currentPosition).getisChecked());
		orderUserView.isOrderUserChecked.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				orderUsers.get(currentPosition).setisChecked(isChecked);
			}
		});
		orderUserView.orderUserTel.setText(orderUsers.get(position).getphone());
		orderUserView.orderUserPlatform.setText(orderUsers.get(position)
				.getplatform().toString());
		orderUserView.orderUserCount.setText(orderUsers.get(position)
				.getcount().toString()
				+ "´Î");
		

		return convertView;
	}
}
