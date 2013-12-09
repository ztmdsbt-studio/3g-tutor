package com.tutor.adapters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.totur.R;
import com.tutor.model.ContactPersion;

public class ContactsListAdapter extends BaseAdapter {

	private List<ContactPersion> listItems;
	private LayoutInflater listContainer;

	public final class ListItemView {
		public TextView userNumber;
		public TextView count;
		public CheckBox isContactsChecked;
	}

	public ContactsListAdapter(Context context,
			List<ContactPersion> listItems) {

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
		final int currentPosition = position;
		ListItemView listItemView = null;
		if (convertView == null) {
			listItemView = new ListItemView();
			convertView = listContainer.inflate(
					R.layout.contacts_list_item_style, null);

			listItemView.count = (TextView) convertView
					.findViewById(R.id.tv_user_count);
			listItemView.userNumber = (TextView) convertView
					.findViewById(R.id.tv_user_number);
			listItemView.isContactsChecked = (CheckBox) convertView
					.findViewById(R.id.cb_contacts);
			convertView.setTag(listItemView);
		} else {
			listItemView = (ListItemView) convertView.getTag();  
		}
		listItemView.isContactsChecked.setChecked(listItems.get(currentPosition).getisChecked());
		listItemView.isContactsChecked.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				listItems.get(currentPosition).setisChecked(isChecked);
			}
		});

		listItemView.count.setText(listItems.get(position).getcount().toString());

		listItemView.userNumber.setText(listItems.get(position).getphone());

		return convertView;
	}

}
