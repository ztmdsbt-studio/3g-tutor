package com.tutor.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.totur.R;
import com.tutor.model.ApplicationData;

public class UserInfoActivity extends Activity {

	private ApplicationData applicationData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_info);
		applicationData = (ApplicationData) getApplicationContext();
	}

	@Override
	protected void onResume() {
		super.onResume();
		applicationData.setCurrentActivity(this);
		InitialUserInfo();
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

	private void InitialUserInfo() {
		TextView userName = (TextView) findViewById(R.id.tv_userId);
		userName.setText(applicationData.getcurrentUser().Username);

		TextView tutorName = (TextView) findViewById(R.id.tvUserName);
		tutorName.setText(applicationData.getcurrentUser().Tutor_Name);

		TextView point = (TextView) findViewById(R.id.tvUserScore);
		point.setText(applicationData.getcurrentUser().Point);

		TextView phone = (TextView) findViewById(R.id.tvPhone);
		phone.setText(applicationData.getcurrentUser().Tel);

		TextView mail = (TextView) findViewById(R.id.tvMail);
		mail.setText(applicationData.getcurrentUser().Email);

		TextView city = (TextView) findViewById(R.id.tvCity);
		city.setText(applicationData.getcurrentUser().City);

		TextView county = (TextView) findViewById(R.id.tvCounty);
		county.setText(applicationData.getcurrentUser().County);

		TextView station = (TextView) findViewById(R.id.tvTutorStation);
		station.setText(applicationData.getcurrentUser().Tutor_Site);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user_info, menu);
		return true;
	}

	public void backToPrevious(View view) {
		super.onBackPressed();
	}

	public void goback(View v) {
		finish();
	}
}
