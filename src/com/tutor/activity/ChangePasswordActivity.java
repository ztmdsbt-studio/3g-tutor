package com.tutor.activity;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.totur.R;
import com.tutor.dialogs.ConfirmDialog;
import com.tutor.model.AppConfiguration;
import com.tutor.model.ApplicationData;
import com.tutor.utilities.GetDataFromWebService;
import com.tutor.utilities.OnGetDataListener;
import com.tutor.utilities.Utils;

public class ChangePasswordActivity extends Activity {

	private ApplicationData applicationData;
	private AlertDialog alertDialog;
	TextView oldPassword;
	TextView newPassword;
	private TextView newPasswordRepet;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_password);
		applicationData = (ApplicationData) getApplicationContext();
	}

	private void initial() {
		oldPassword = (TextView) findViewById(R.id.old_password);
		newPassword = (TextView) findViewById(R.id.tvNewPassword);
		newPasswordRepet = (TextView) findViewById(R.id.tvNewPassword_repet);
	}

	@Override
	protected void onResume() {
		super.onResume();
		applicationData.setCurrentActivity(this);
		initial();
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
		getMenuInflater().inflate(R.menu.change_password, menu);
		return true;
	}

	public void backToPrevious(View view) {
		super.onBackPressed();
	}

	public void confirmExchange(View view) {
		HashMap<String, Object> para = new LinkedHashMap<String, Object>();
		final AlertDialog dialog = new AlertDialog.Builder(
				(Context) (getParent() == null ? applicationData
						.getCurrentActivity() : getParent())).create();
		para.put("tid", applicationData.getcurrentUser().userid);
		String oldPasswordtemp = oldPassword.getText().toString();
		String newPasswordtemp = newPassword.getText().toString();
		String newPasswordtempRep = newPasswordRepet.getText().toString();
		if (newPasswordtemp.equals("") || newPasswordtempRep.equals("")) {
			dialog.setMessage("新密码不能为空.");
			newPassword.setText("");
			newPasswordRepet.setText("");
			dialog.show();
			return;
		}
		if (!newPasswordtemp.equals(newPasswordtempRep)) {
			dialog.setMessage("两次输入密码不一致,请重新输入");
			newPassword.setText("");
			newPasswordRepet.setText("");
			dialog.show();
			return;
		}
		para.put("OldPassword", oldPasswordtemp);
		para.put("NewPassword", newPasswordtemp);
		dialog.setMessage("请稍后...");
		dialog.show();
		GetDataFromWebService service = new GetDataFromWebService();
		service.setOnGetDataListener(new OnGetDataListener() {

			@Override
			public void onGetDataCompleted(String responseData) {
				dialog.dismiss();
				try {
					JSONObject json = (JSONObject) new JSONTokener(responseData)
							.nextValue();
					if (json.getString("flag").equals("1")) {
						final ConfirmDialog cDialog = new ConfirmDialog(
								ChangePasswordActivity.this,
								R.layout.dialog_confirm_layout,
								R.style.Theme_dialog);
						TextView tvMessage = (TextView) cDialog
								.findViewById(R.id.tvMessage);
						tvMessage.setText("密码修改成功！");
						findViewById(R.id.btn_conform_dialog)
								.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View v) {
										cDialog.dismiss();
									}
								});
						cDialog.show();
					}
				} catch (Exception e) {
					Utils.showMessageBox(ChangePasswordActivity.this,
							applicationData, "修改密码失败.");
					Log.d("调试", "修改密码失败. response:\"" + responseData + "\"");
					if (e != null) {
						e.printStackTrace();
					}
				}
			}
		});

		service.GetDataFromWebAsync("editPasswd",
				AppConfiguration.LOGIN_SERVICE, para);

	}
}
