package com.tutor.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.totur.R;
import com.tutor.dialogs.ConfirmDialog;
import com.tutor.model.ApplicationData;

public class ForgetPwdActivity extends Activity {

	private ApplicationData applicationData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forget_pwd);
		applicationData = (ApplicationData) getApplicationContext();
	}

	@Override
	protected void onResume() {
		super.onResume();
		applicationData.setCurrentActivity(this);
		Bundle bundle = getIntent().getExtras();
		TextView currentPhone = (TextView) findViewById(R.id.tv_current_phone);
		currentPhone.setText(bundle.getString("currentPhone"));
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.forget_pwd, menu);
		return true;
	}

	public void backToPrevious(View view) {
		super.onBackPressed();
	}

	public void confirmPwd(View view) {
		ConfirmDialog cDialog = new ConfirmDialog(this,
				R.layout.dialog_confirm_layout, R.style.Theme_dialog);
		TextView tvMessage = (TextView) cDialog.findViewById(R.id.tvMessage);
		tvMessage.setText("≤‚ ‘£∫–ﬁ∏ƒ√‹¬Î°£");
		cDialog.show();
	}
}
