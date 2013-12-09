package com.tutor.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.totur.R;
import com.tutor.activity.AppCountActivity;
import com.tutor.activity.Appfavorite;
import com.tutor.activity.ChangePasswordActivity;
import com.tutor.activity.TodoExchangeActivity;
import com.tutor.activity.UserInfoActivity;
import com.tutor.model.ApplicationData;

public class LeftFragment extends Fragment {

	private View view;
	private ApplicationData applicationData;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		try {
			View view = inflater.inflate(R.layout.left_index, null);
			applicationData = (ApplicationData) getActivity()
					.getApplicationContext();
			this.view = view;
			InitialUserInfo();
			TextView tView = (TextView) view.findViewById(R.id.tv_user_info);

			tView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					Intent intent = new Intent(getActivity(),
							UserInfoActivity.class);
					startActivity(intent);
				}
			});

			TextView ChangePasswordView = (TextView) view
					.findViewById(R.id.tv_change_password);
			ChangePasswordView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					Intent intent = new Intent(getActivity(),
							ChangePasswordActivity.class);
					startActivity(intent);
				}
			});

			TextView todoExchangeTextView =(TextView)view.findViewById(R.id.tv_todo_exchange);
			todoExchangeTextView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					Intent intent = new Intent(getActivity(),
							TodoExchangeActivity.class);
					startActivity(intent);
				}
			});
			
			TextView CountView = (TextView) view.findViewById(R.id.tv_count);
			CountView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					Intent intent = new Intent(getActivity(),
							AppCountActivity.class);
					startActivity(intent);
				}
			});
			
			TextView favoriteView = (TextView) view.findViewById(R.id.tv_favorite);
			favoriteView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					Intent intent = new Intent(getActivity(),
							Appfavorite.class);
					startActivity(intent);
				}
			});
			
			TextView exitTextView = (TextView) view.findViewById(R.id.tv_exit);
			exitTextView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					final Dialog dialog = new Dialog(applicationData
							.getCurrentActivity(), R.style.Theme_dialog);
					dialog.setCanceledOnTouchOutside(true);
					View view = LayoutInflater.from(
							applicationData.getCurrentActivity()).inflate(
							R.layout.dialog_yesno_layout, null);
					dialog.setContentView(view);
					TextView tvMessage = (TextView) dialog
							.findViewById(R.id.tvMessage);
					tvMessage.setText("是否确定退出？");

					Button confirmButton = (Button) dialog
							.findViewById(R.id.btnConfirm);
					confirmButton.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							android.os.Process.killProcess(android.os.Process
									.myPid());
						}
					});

					Button cancelButton = (Button) dialog
							.findViewById(R.id.btnCancel);
					cancelButton.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});

					dialog.show();
				}
			});

		} catch (Exception e) {
			Log.e("unhandle", "something is null?", e);
		}

		return view;
	}

	private void InitialUserInfo() {
		try {
			TextView userName = (TextView) view.findViewById(R.id.tvUserId);
			userName.setText(applicationData.getcurrentUser().Username);

			TextView tutorName = (TextView) view.findViewById(R.id.tvUserName);
			tutorName.setText(applicationData.getcurrentUser().Tutor_Name);

			TextView point = (TextView) view.findViewById(R.id.tvUserScore);
			point.setText(applicationData.getcurrentUser().Point);
		} catch (Exception e) {
			Log.e("unhandle", "something is null?", e);
		}
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
}
