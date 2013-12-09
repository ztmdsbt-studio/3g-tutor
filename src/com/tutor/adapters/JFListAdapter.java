package com.tutor.adapters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.totur.R;
import com.tutor.activity.AppTutorActivity;
import com.tutor.model.ApplicationData;
import com.tutor.utilities.ImageLoader;
import com.tutor.viewModel.JFItemInfModel;

public class JFListAdapter extends BaseAdapter {
	private Context context;
	private List<Map<String, Object>> listItems;
	private LayoutInflater listContainer;
	private ImageLoader imageLoader;
	private ApplicationData applicationData;

	public final class ListItemView {
		public ImageView image;
		public TextView titie;
		public TextView count;
		public TextView danwei;
		public TextView description;
		public Button doTutor;
	}

	public JFListAdapter(Context context, List<Map<String, Object>> listItems) {
		this.context = context;
		listContainer = LayoutInflater.from(context);
		this.listItems = listItems;
		if (this.listItems == null) {
			this.listItems = new ArrayList<Map<String, Object>>();
		}
		applicationData = (ApplicationData) context.getApplicationContext();
		imageLoader = applicationData.imageLoader;
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
			convertView = listContainer.inflate(R.layout.jifen_list_item_style,
					null);

			listItemView.doTutor = (Button) convertView
					.findViewById(R.id.tb_do_recommend);
			listItemView.titie = (TextView) convertView
					.findViewById(R.id.tv_jf_name);
			listItemView.count = (TextView) convertView
					.findViewById(R.id.tv_jf_count);
			listItemView.danwei = (TextView) convertView
					.findViewById(R.id.tv_jf_danwei);
			listItemView.description = (TextView) convertView
					.findViewById(R.id.tv_jf_description);
			listItemView.image = (ImageView) convertView
					.findViewById(R.id.iv_jf);
			convertView.setTag(listItemView);
		} else {
			listItemView = (ListItemView) convertView.getTag();
		}

		listItems.get(position).put(
				"image",
				imageLoader.DisplayImage(listItems.get(position)
						.get("imageUrl").toString(), listItemView.image));
		listItemView.titie.setText(listItems.get(position).get("short_title")
				.toString());
		listItemView.count.setText(listItems.get(position).get("point")
				.toString());
		listItemView.danwei.setText(listItems.get(position).get("danwei")
				.toString());
		listItemView.description.setText(listItems.get(position)
				.get("short_description").toString());

		listItemView.doTutor.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setCurrentJfItemInf(itemPosition);
				Intent intent = new Intent(context, AppTutorActivity.class);
				intent.putExtra("position", itemPosition);
				intent.putExtra("isTutorOrService",  "ÍÆ¼ö");
				context.startActivity(intent);
			}
		});

		return convertView;
	}

	protected void setCurrentJfItemInf(int itemPosition) {
		JFItemInfModel temp = new JFItemInfModel();
		temp.setID(Integer.parseInt(listItems.get(itemPosition).get("id")
				.toString()));
		temp.setName(listItems.get(itemPosition).get("title").toString());
		temp.setApplication_Type_ID(true);
		temp.setPoint(listItems.get(itemPosition).get("point").toString());
		temp.setIntroudution(listItems.get(itemPosition).get("description")
				.toString());
		temp.setRecommend_Decrible(listItems.get(itemPosition)
				.get("recommend_Decrible").toString());
		temp.setAppImage((Bitmap) listItems.get(itemPosition).get("image"));
		temp.setAppImageUrl(listItems.get(itemPosition).get("imageUrl").toString());
		applicationData.setCurrentJFItemInf(temp);
	}
}
