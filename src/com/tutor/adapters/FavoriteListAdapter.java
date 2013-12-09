package com.tutor.adapters;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.totur.R;
import com.tutor.activity.AppTutorActivity;
import com.tutor.model.ApplicationData;
import com.tutor.utilities.ImageLoader;
import com.tutor.viewModel.FDItemInfModel;

public class FavoriteListAdapter extends BaseAdapter {
	private Context context;
	private List<Map<String, Object>> listItems;
	private LayoutInflater inflater;
	private ApplicationData applicationData;
	public Map<Integer, Boolean> map;   
	private ImageLoader imageLoader;

	public final class ListItemView {
//		public ImageView image;
		public TextView titie;
		public TextView size;
		public TextView version;
		public TextView description;
		public Button doTutor;
		public ImageView image;
	}

	public FavoriteListAdapter(Context context,
			List<Map<String, Object>> listItems) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		if (listItems == null) {
			this.listItems = new ArrayList<Map<String, Object>>();
			return;
		}
		this.listItems = listItems;
		applicationData = (ApplicationData) context.getApplicationContext();
		map = new LinkedHashMap<Integer, Boolean>();  
		for(int i = 0; i < listItems.size(); i++) {  
		              map.put(i, false);  
		}   

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
			convertView = inflater.inflate(R.layout.favorite_list_item_style,
					null);

			listItemView.doTutor = (Button) convertView
					.findViewById(R.id.tb_favorite_do_tutor);
			listItemView.titie = (TextView) convertView
					.findViewById(R.id.tv_favorite_name);
			listItemView.size = (TextView) convertView
					.findViewById(R.id.tv_favorite_size);
			listItemView.version = (TextView) convertView
					.findViewById(R.id.tv_favorite_version);
			listItemView.description = (TextView) convertView
					.findViewById(R.id.tv_favorite_description);
			listItemView.image = (ImageView) convertView
					.findViewById(R.id.iv_favorite);
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
		listItemView.size.setText(listItems.get(position).get("size")
				.toString());
		listItemView.version.setText(listItems.get(position)
				.get("short_version").toString());
		listItemView.description.setText(listItems.get(position)
				.get("short_description").toString());

		CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.favorite_isselected);  
		checkBox.setChecked(map.get(position));   
		
		listItemView.doTutor.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setCurrentFDItemInf(itemPosition);
				Intent intent = new Intent(context, AppTutorActivity.class);
				intent.putExtra("position", itemPosition);
				intent.putExtra("isTutorOrService", "¸¨µ¼");
				context.startActivity(intent);
			}
		});

		checkBox.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {        
				map.put(itemPosition, ((CheckBox)v).isChecked());  
			}
		});
		
		return convertView;
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
//		temp.setAppImage((Bitmap) listItems.get(position).get("image"));
		temp.setAppImageUrl(listItems.get(position).get("imageUrl").toString());
		applicationData.setcurrentFDItemInf(temp);
	}

	public void setMapValue(int position, boolean flag){
		 map.put(position, flag);  
	}
}
