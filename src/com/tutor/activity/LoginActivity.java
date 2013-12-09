package com.tutor.activity;

//import Call;
//import Service;
import java.net.SocketTimeoutException;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.totur.R;
import com.tutor.model.AppConfiguration;
import com.tutor.model.ApplicationData;
import com.tutor.model.LoginUser;
import com.tutor.utilities.Utils;
import com.tutor.views.GuideHelper;

public class LoginActivity extends Activity {

	private EditText loginUserName;
	private EditText loginUserPwd;
	private Intent intent;
	private ApplicationData applicationData;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		applicationData = (ApplicationData) getApplicationContext();
	}

	private void initial() {
		loginUserName = (EditText) findViewById(R.id.et_login_username);
		loginUserPwd = (EditText) findViewById(R.id.et_login_pwd);

		GuideHelper guideHelper = new GuideHelper(this);
		guideHelper.openGuide();
		intent = new Intent(this, SlidingActivity.class);

		TextView tvFindPwd = (TextView) findViewById(R.id.tv_find_wpd);
		tvFindPwd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				findPwd(v);
			}
		});

		// just for test :default user
		loginUserName.setText("13359290311");
		loginUserPwd.setText("111111");
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

	public void gotoMainPage(View v) {
		try {

			Utils.showMessageBox(LoginActivity.this,
					applicationData,"登录中...");
			LoginTask task = new LoginTask();
			String params[] = { loginUserName.getText().toString(),
					loginUserPwd.getText().toString() };
			task.execute(params);

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void findPwd(View view) {
		intent = new Intent(applicationData.getCurrentActivity(),
				ForgetPwdActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("currentPhone", loginUserName.getText().toString());
		intent.putExtras(bundle);
		startActivity(intent);
	}

	private class LoginTask extends AsyncTask<String, String, Integer> {
		@Override
		protected Integer doInBackground(String... params) {
			String userName = (String) params[0];
			String pwd = (String) params[1];
			return Login(userName, pwd);
		}

		@Override
		protected void onPostExecute(final Integer success) {
			if (success == 0) {
				Utils.closeMessageBox();
				intent = new Intent(applicationData.getCurrentActivity(),
						SlidingActivity.class);
				startActivity(intent);
				LoginActivity.this.finish();
			} else if (success == 1) {

				Utils.showMessageBox(LoginActivity.this,
						applicationData,"登录失败，用户名或密码错误。");
			} else if (success == 2) {

				Utils.showMessageBox(LoginActivity.this,
						applicationData,"连接服务器超时，请重试。");
			} else {

				Utils.showMessageBox(LoginActivity.this,
						applicationData,"登录失败，请重试。");
			}
		}
		
		public int Login(String userName, String password) {
			SoapObject request = new SoapObject(AppConfiguration.NAMESPACE,
					"login");
			request.addProperty("Username", userName);
			request.addProperty("Password", password);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.bodyOut = request;
			HttpTransportSE httpTransportSE = new HttpTransportSE(
					AppConfiguration.LOGIN_SERVICE, 30000);
			try {
				httpTransportSE.call(null, envelope);
				if (envelope.getResponse() != null) {
					String response = envelope.getResponse().toString();
					if (response == null || response.equals("1")) {
						applicationData.setisLogin(false);
						return 1;
					} else {
						getCurrentUser(response);
						applicationData.setisLogin(true);
						return 0;
					}
				}
			} catch (SocketTimeoutException timeoutEexception) {
				timeoutEexception.printStackTrace();
				// just for debug!!!
				// getMockupUser();
				// return 0;
				return 2;
			} catch (Exception e) {
				e.getCause();
				e.printStackTrace();
				return 3;
			}
			return 3;
		}

		// Just for Test!!
		private void getMockupUser() {
			LoginUser user = new LoginUser();
			user.City = "西安";
			user.City_id = "40";
			user.County = "中国";
			user.County_id = "01";
			user.Current_Mouth_count = "20";
			user.Deparment = "流量经营组";
			user.Email = "";
			user.Last_Mouth_Count = "2";
			user.Orders_Count = "0";
			user.Point = "11000";
			user.Tel = "13389223303";
			user.Tutor_Name = "安凯航";
			user.Tutor_Site = "流量经营组";
			user.Tutor_Site_City = "西安";
			user.Tutor_Site_Name = "哈哈";
			user.Tutor_Site_Type = "呵呵";
			user.userid = "9674";
			user.Username = "13389223303";
			applicationData.setcurrentUser(user);
		}

		private void getCurrentUser(String response) throws Exception {
			JSONObject json = (JSONObject) new JSONTokener(response)
					.nextValue();
			applicationData.setcurrentUser(new LoginUser());
			applicationData.getcurrentUser().Username = (String) (json
					.isNull("Username") ? "" : json.get("Username"));
			applicationData.getcurrentUser().userid = (String) (json
					.isNull("userid") ? "" : json.get("userid"));
			applicationData.getcurrentUser().Tel = (String) (json.isNull("Tel") ? ""
					: json.get("Tel"));
			applicationData.getcurrentUser().Point = (String) (json
					.isNull("Point") ? "" : json.get("Point"));
			applicationData.getcurrentUser().Email = (String) (json
					.isNull("Email") ? "" : json.get("Email"));
			applicationData.getcurrentUser().City = (String) (json
					.isNull("City") ? "" : json.get("City"));
			applicationData.getcurrentUser().City_id = (String) (json
					.isNull("City_id") ? "" : json.get("City_id"));
			applicationData.getcurrentUser().County = (String) (json
					.isNull("County") ? "" : json.get("County"));
			applicationData.getcurrentUser().County_id = (String) (json
					.isNull("County_id") ? "" : json.get("County_id"));
			applicationData.getcurrentUser().Tutor_Site = (String) (json
					.isNull("Tutor_Site") ? "" : json.get("Tutor_Site"));
			applicationData.getcurrentUser().Deparment = (String) (json
					.isNull("Deparment") ? "" : json.get("Deparment"));
			applicationData.getcurrentUser().Tutor_Site_City = (String) (json
					.isNull("Tutor_Site_City") ? "" : json
					.get("Tutor_Site_City"));
			applicationData.getcurrentUser().Tutor_Site_Name = (String) (json
					.isNull("Tutor_Site_Name") ? "" : json
					.get("Tutor_Site_Name"));
			applicationData.getcurrentUser().Tutor_Site_Type = (String) (json
					.isNull("Tutor_Site_Type") ? "" : json
					.get("Tutor_Site_Type"));
			applicationData.getcurrentUser().Tutor_Name = (String) (json
					.isNull("Tutor_Name") ? "" : json.get("Tutor_Name"));
			applicationData.getcurrentUser().Orders_Count = (String) (json
					.isNull("Orders_Count") ? "" : json.get("Orders_Count"));
			applicationData.getcurrentUser().Last_Mouth_Count = (String) (json
					.isNull("Last_Mouth_Count") ? "" : json
					.get("Last_Mouth_Count"));
			applicationData.getcurrentUser().Current_Mouth_count = (String) (json
					.isNull("Current_Mouth_count") ? "" : json
					.get("Current_Mouth_count"));
		}
	}
}
