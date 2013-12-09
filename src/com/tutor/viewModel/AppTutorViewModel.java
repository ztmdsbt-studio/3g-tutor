package com.tutor.viewModel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;

import com.tutor.model.AppConfiguration;
import com.tutor.model.AppRecommendation;
import com.tutor.model.ApplicationData;
import com.tutor.model.ContactPersion;
import com.tutor.model.OrderUser;
import com.tutor.model.Platform;
import com.tutor.utilities.GetDataCompletedListener;
import com.tutor.utilities.GetDataFromWebService;
import com.tutor.utilities.OnGetDataListener;

public class AppTutorViewModel {
	private FDItemInfModel currentFdItemInfModel;
	private JFItemInfModel currentJfItemInfModel;
	private ApplicationData applicationData;
	private ArrayList<ContactPersion> contacts;

	private boolean isSendMessageOK;

	public boolean getisSendMessageOk() {
		return this.isSendMessageOK;
	}

	public FDItemInfModel getcurrentFdItemInfModel() {
		return currentFdItemInfModel;
	}

	public JFItemInfModel getcurrentJFItemInfModel() {
		return currentJfItemInfModel;
	}

	public AppTutorViewModel(ApplicationData applicationData) {
		this.applicationData = applicationData;
		this.currentFdItemInfModel = applicationData.getcurrentFDItemInf();
		this.currentJfItemInfModel = applicationData.getCurrentJFItemInf();
	}

	public void doTutorDiy(AppRecommendation recommendation,
			final GetDataCompletedListener getDataCompletedListener) {
		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("User_id", recommendation.userId);
		params.put("Tname", recommendation.Tname);
		params.put("City_id", recommendation.cityId);
		params.put("Tel_Phone", recommendation.telPhones);
		params.put("Application_ID", recommendation.ApplicationId);
		params.put("Application_name", recommendation.applicationName);
		params.put("Tutor_Decrible", recommendation.TutorDecrible);
		params.put("Push_Date", recommendation.PushDate);
		sendTutorMessage(recommendation, getDataCompletedListener,
				"sendMessageDiy",params);
	}

	public void doTutorPage(AppRecommendation recommendation,
			final GetDataCompletedListener getDataCompletedListener) {
		if(recommendation.telPhones.equals("All")){
			Map<String, Object> params = new LinkedHashMap<String, Object>();
			params.put("User_id", recommendation.userId);
			params.put("Tname", recommendation.Tname);
			params.put("Application_ID", recommendation.ApplicationId);
			params.put("City_id", recommendation.cityId);
			sendTutorMessage(recommendation, getDataCompletedListener,
					"sendMessageAll",params);
			
		}else{
			Map<String, Object> params = new LinkedHashMap<String, Object>();
			params.put("User_id", recommendation.userId);
			params.put("Tname", recommendation.Tname);
			params.put("City_id", recommendation.cityId);
			params.put("Application_ID", recommendation.ApplicationId);
			params.put("Tel_Phone", recommendation.telPhones);
			sendTutorMessage(recommendation, getDataCompletedListener,
					"sendMessagePage",params);
		}
	}

	private void sendTutorMessage(AppRecommendation recommendation,
			final GetDataCompletedListener getDataCompletedListener,
			String action,Map<String, Object> params) {
		callSendMessageServiceAsync(getDataCompletedListener, params, action);
	}

	public void getTutorUsers(int currentPageNumber, final Platform platform,
			final int messageCount, final List<OrderUser> orderUsers,
			final GetDataCompletedListener onGetListListener) {

		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("uid", applicationData.getcurrentUser().userid);
		params.put("page", 1);
		params.put("messageCount", messageCount);
		params.put("platform", platform.ordinal());
		GetDataFromWebService service = new GetDataFromWebService();
		service.setOnGetDataListener(new OnGetDataListener() {

			@Override
			public void onGetDataCompleted(String responseData) {
				JSONArray jsonArray;
				try {
					jsonArray = new JSONArray(responseData);
					OrderUser orderUser = null;
					for (int i = 0; i < jsonArray.length(); i++) {
						orderUser = new OrderUser();
						JSONObject json = (JSONObject) jsonArray.get(i);
						orderUser.setid(json.get("id").equals("") ? 0
								: Integer.valueOf(json.get("id").toString()));
						orderUser.setphone(json.getString("tel"));
						orderUser.setplatform(Platform.valueOf(json.get(
								"Machine_type").equals("") ? 0 : Integer
								.valueOf(json.get("Machine_type").toString())));
						orderUser
								.setcount(json.get("count").equals("") ? 0
										: Integer.valueOf(json.get("count")
												.toString()));
						orderUser.setisChecked(true);
						orderUsers.add(orderUser);
					}
					onGetListListener.GetDataCompleted(orderUsers);
				} catch (Exception e) {
					onGetListListener.GetDataCompleted(null);
					e.printStackTrace();
				}
			}
		});
		service.GetDataFromWebAsync("getGuest",
				AppConfiguration.GUEST_TUTOR_SERVICE, params);
	}

	public ArrayList<ContactPersion> getContacts() {
		contacts = new ArrayList<ContactPersion>();
		ContentResolver resolver = applicationData.getCurrentActivity()
				.getContentResolver();
		Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI,
				null, null, null, null);

		int counter = 0;
		while (cursor.moveToNext() && counter < 20) {
			// int personcol = cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME);
			// sb.append(cursor.getString(personcol));
			String id = cursor.getString(cursor
					.getColumnIndex(ContactsContract.Contacts._ID));
			Cursor c = resolver.query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "="
							+ id, null, null);
			while (c.moveToNext()) {
				String number = c
						.getString(c
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				number = number.replace("-", "").replace(" ", "");
				ContactPersion contactPersion = new ContactPersion();
				contactPersion.setcount(0);
				contactPersion.setid(counter);
				contactPersion.setphone(number);
				contactPersion.setisChecked(true);
				// contactPersion.setplatform(Platform.Android);
				// contactPersion.setservicePackageType("ÌìÒíÌ×²Í");
				contacts.add(contactPersion);
				counter++;
			}
		}
		return contacts;
	}

	public void doRecommendDiy(String telPhones,
			final GetDataCompletedListener getDataCompletedListener) {
		sendRecommendMessage(telPhones, getDataCompletedListener,
				"sendMessageServiceDiy");
	}

	public void doRecommendPage(String telPhones,
			final GetDataCompletedListener getDataCompletedListener) {
		sendRecommendMessage(telPhones, getDataCompletedListener,
				"sendMessageServicePage");
	}

	private void sendRecommendMessage(String telPhones,
			final GetDataCompletedListener getDataCompletedListener,
			String action) {
		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("uid", applicationData.getcurrentUser().userid);
		params.put("sid", applicationData.getCurrentJFItemInf().getID());
		params.put("cellphone", telPhones);
		callSendMessageServiceAsync(getDataCompletedListener, params, action);
	}

	private void callSendMessageServiceAsync(
			final GetDataCompletedListener getDataCompletedListener,
			Map<String, Object> params, String action) {
		GetDataFromWebService service = new GetDataFromWebService();

		service.setOnGetDataListener(new OnGetDataListener() {

			@Override
			public void onGetDataCompleted(String responseData) {
				AppTutorViewModel.this.onGetDataCompleted(
						getDataCompletedListener, responseData);
			}
		});
		service.GetDataFromWebAsync(action,
				AppConfiguration.GUEST_TUTOR_SERVICE, params);
	}

	private void onGetDataCompleted(
			final GetDataCompletedListener getDataCompletedListener,
			String responseData) {
		JSONObject response;
		try {
			response = (JSONObject) new JSONTokener(responseData).nextValue();
			if ((Integer) response.get("flag") == 0) {
				isSendMessageOK = true;
			} else {
				isSendMessageOK = false;
			}
			getDataCompletedListener.GetDataCompleted(isSendMessageOK);
		} catch (Exception e) {
			getDataCompletedListener.GetDataCompleted(null);
			e.printStackTrace();
		}
	}
}
