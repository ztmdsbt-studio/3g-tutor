package com.tutor.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.totur.R;
import com.tutor.dialogs.ConfirmDialog;
import com.tutor.model.ApplicationData;

public class TodoExchangeActivity extends Activity {

	private ApplicationData applicationData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		applicationData = (ApplicationData) getApplicationContext();
		setContentView(R.layout.activity_todo_exchange);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		applicationData.setCurrentActivity(this);
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
		getMenuInflater().inflate(R.menu.todo_exchange, menu);
		return true;
	}

	public void confirmExchange(View view)
	{
		ConfirmDialog cDialog =new ConfirmDialog(this,R.layout.dialog_confirm_layout,R.style.Theme_dialog);
		cDialog.show();
		TextView tvMessage = (TextView)cDialog.findViewById(R.id.tvMessage);
		tvMessage.setText("¹§Ï²Äú¶Ò»»³É¹¦£¡¼ÌÐø¶Ò»»£¡");				
	}
	
	public void cancelExchange(View view)
	{
		super.onBackPressed();
	}
}
