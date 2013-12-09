package com.tutor.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

import com.totur.R;
import com.tutor.adapters.ContactsListAdapter;
import com.tutor.adapters.OrderUsertListAdapter;
import com.tutor.model.AppRecommendation;
import com.tutor.model.ApplicationData;
import com.tutor.model.ContactPersion;
import com.tutor.model.OrderUser;
import com.tutor.model.Platform;
import com.tutor.model.ServeType;
import com.tutor.utilities.GetDataCompletedListener;
import com.tutor.utilities.ImageLoader;
import com.tutor.utilities.Utils;
import com.tutor.viewModel.AppTutorViewModel;

public class AppTutorActivity extends Activity {

	private AppTutorViewModel viewModel;

	private List<OrderUser> orderUsers;
	private static final ScheduledExecutorService executor = Executors
			.newSingleThreadScheduledExecutor();
	private AppRecommendation recommendation;
	private ApplicationData applicationData;
	private Button btnGetData;
	private Button btnGetContacts;
	private ListView lView;
	private ListView lvContacts;
	private List<ContactPersion> contacts;
	private String FREE_MESSAGE = "自由短信";
	private String ORDER_USERS = "派单用户";
	private String CONTACTS = "通讯录";
	private ImageLoader imageLoader;
	private String isTutorOrService;
	private Dialog recommendDialog;

	// life cycle
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_tutor);
		applicationData = (ApplicationData) getApplicationContext();
		viewModel = new AppTutorViewModel(applicationData);
		imageLoader = applicationData.imageLoader;
	}

	private void initial() {
		try {
			Bundle extras = getIntent().getExtras();
			isTutorOrService = extras.getString("isTutorOrService");
		} catch (NullPointerException e) {
			Log.e("调试", "isTutorOrService is null.");
			isTutorOrService = "辅导";
		}
		this.FREE_MESSAGE = this.FREE_MESSAGE + isTutorOrService;
		this.ORDER_USERS = this.ORDER_USERS + isTutorOrService;
		this.CONTACTS = this.CONTACTS + isTutorOrService;
		initialActivityElements();

		// Just for test.
		EditText userPhones = (EditText) findViewById(R.id.et_users_phone);
		userPhones.setText("18509282442");
	}

	@Override
	protected void onResume() {
		super.onResume();
		applicationData.setCurrentActivity(this);
		initial();
		initialMainTabHost();
		// contacts =viewModel.getContacts();
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
		getMenuInflater().inflate(R.menu.app_tutor, menu);
		return true;
	}

	// end life cycle

	// initial page
	private void initialActivityElements() {
		LayoutInflater layoutInflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		RelativeLayout title = (RelativeLayout) findViewById(R.id.rl_title);
		if (isTutorOrService.equals("辅导")) {
			View titleView = layoutInflater.inflate(R.layout.layout_app_title,
					null);
			((ViewGroup) title).addView(titleView, 0,
					new ViewGroup.LayoutParams(
							ViewGroup.LayoutParams.MATCH_PARENT,
							ViewGroup.LayoutParams.WRAP_CONTENT));
			initialTutorElements();
		} else if (isTutorOrService.equals("推荐")) {
			View titleView = layoutInflater.inflate(
					R.layout.layout_service_title, null);
			((ViewGroup) title).addView(titleView, 0,
					new ViewGroup.LayoutParams(
							ViewGroup.LayoutParams.MATCH_PARENT,
							ViewGroup.LayoutParams.WRAP_CONTENT));
			initialServiceElements();
		}
		TextView tvTitle = (TextView) findViewById(R.id.tv_apptutor_title);
		tvTitle.setText(isTutorOrService);
	}

	private void initialTutorElements() {
		ImageView appImage = (ImageView) findViewById(R.id.iv_app_image);
		imageLoader.DisplayImage(this.viewModel.getcurrentFdItemInfModel()
				.getAppImageUrl(), appImage);

		TextView appName = (TextView) findViewById(R.id.tv_app_name);
		appName.setText(this.viewModel.getcurrentFdItemInfModel().getName());

		TextView appSize = (TextView) findViewById(R.id.tv_app_size);
		appSize.setText(this.viewModel.getcurrentFdItemInfModel().getSize());

		TextView appVersion = (TextView) findViewById(R.id.tv_app_version);
		appVersion.setText(this.viewModel.getcurrentFdItemInfModel()
				.getVersion());

		Button btnFreeMessasge = (Button) findViewById(R.id.btn_freemessage);
		btnFreeMessasge.setBackgroundResource(R.drawable.selector_coach);

		Button btnOrderUsers = (Button) findViewById(R.id.btn_dotutor_orderusers);
		btnOrderUsers
				.setBackgroundResource(R.drawable.selector_filtrate_recommend);

		findViewById(R.id.btn_dotutor_orderusers).setBackgroundResource(
				R.drawable.selector_filtrate_coach);

		findViewById(R.id.btn_dotutor_contacts).setBackgroundResource(
				R.drawable.selector_filtrate_coach);

		initialOrderUsersListElements();
		initialContactsElement();
	}

	private void initialServiceElements() {
		ImageView appImage = (ImageView) findViewById(R.id.iv_app_image);
		imageLoader.DisplayImage(this.viewModel.getcurrentJFItemInfModel()
				.getAppImageUrl(), appImage);

		TextView appName = (TextView) findViewById(R.id.tv_service_name);
		appName.setText(this.viewModel.getcurrentJFItemInfModel().getName());

		TextView appPoint = (TextView) findViewById(R.id.tv_service_cost);
		appPoint.setText(this.viewModel.getcurrentJFItemInfModel().getPoint());

		Button btnFreeMessasge = (Button) findViewById(R.id.btn_freemessage);
		btnFreeMessasge.setBackgroundResource(R.drawable.selector_recommend);

		View btnTutor = findViewById(R.id.btn_dotutor_orderusers);
		btnTutor.setBackgroundResource(R.drawable.selector_filtrate_recommend);

		findViewById(R.id.btn_dotutor_orderusers).setBackgroundResource(
				R.drawable.selector_filtrate_recommend);

		findViewById(R.id.btn_dotutor_contacts).setBackgroundResource(
				R.drawable.selector_filtrate_recommend);

		initialOrderUsersListElements();
		initialContactsElement();
	}

	private void initialOrderUsersListElements() {
		btnGetData = (Button) this.findViewById(R.id.btn_get_data);
		lView = (ListView) this.findViewById(R.id.lv_orderusers);
		btnGetData.setVisibility(View.VISIBLE);
		lView.setVisibility(View.GONE);
	}

	private void initialContactsElement() {
		// btnGetContacts = (Button) this.findViewById(R.id.btn_get_contacts);
		lvContacts = (ListView) this.findViewById(R.id.lv_contacts_tutors);
		btnGetData.setVisibility(View.VISIBLE);
		lView.setVisibility(View.GONE);
	}

	private void initialMainTabHost() {

		final TabHost yxTabHost = (TabHost) this
				.findViewById(R.id.do_tutor_tabhost);
		yxTabHost.setup();
		if (yxTabHost.getTabWidget().getTabCount() > 0) {
			return;
		}

		LayoutInflater inflater = LayoutInflater.from(this);
		View msgTutorTab = inflater.inflate(R.layout.tab_main_widebg, null);
		TextView sgtv = (TextView) msgTutorTab.findViewById(R.id.mainTabTv);
		sgtv.setText(FREE_MESSAGE);

		View orderUserTab = inflater.inflate(R.layout.tab_main_widebg, null);
		TextView yxtv = (TextView) orderUserTab.findViewById(R.id.mainTabTv);
		yxtv.setText(ORDER_USERS);

		View contactTab = inflater.inflate(R.layout.tab_main_widebg, null);
		TextView contacttv = (TextView) contactTab.findViewById(R.id.mainTabTv);
		contacttv.setText(CONTACTS);

		yxTabHost.addTab(yxTabHost.newTabSpec("msgTutorTab")
				.setIndicator(msgTutorTab).setContent(R.id.tab1));
		yxTabHost.addTab(yxTabHost.newTabSpec("orderUserTab")
				.setIndicator(orderUserTab).setContent(R.id.tab2));
		yxTabHost.addTab(yxTabHost.newTabSpec("contactsTab")
				.setIndicator(contactTab).setContent(R.id.tab3));

		yxTabHost.setOnTabChangedListener(new OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				if (tabId.equals("orderUserTab")) {
					Spinner spinner = (Spinner) AppTutorActivity.this
							.findViewById(R.id.sp_orderusers_selelct_users);
					ArrayAdapter<CharSequence> adapter = ArrayAdapter
							.createFromResource(AppTutorActivity.this,
									R.array.search_scope_type,
									R.layout.spinner_item);
					adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
					spinner.setAdapter(adapter);

					spinner = (Spinner) AppTutorActivity.this
							.findViewById(R.id.sp_select_platform);
					adapter = ArrayAdapter.createFromResource(
							AppTutorActivity.this, R.array.search_platform,
							R.layout.spinner_item);
					adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
					spinner.setAdapter(adapter);

				} else if (tabId.equals("contactsTab")) {
					contacts = viewModel.getContacts();
					TextView resoutCount = (TextView) findViewById(R.id.tv_contacts_count);
					resoutCount.setText(String.valueOf(contacts.size()));
					View view = (View) yxTabHost.getCurrentView();

					Spinner spinner = (Spinner) AppTutorActivity.this
							.findViewById(R.id.sp_contacts_selelct_users);
					ArrayAdapter<CharSequence> adapter = ArrayAdapter
							.createFromResource(AppTutorActivity.this,
									R.array.search_scope_type,
									R.layout.spinner_item);
					adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
					spinner.setAdapter(adapter);
					ListView listView = (ListView) view
							.findViewById(R.id.lv_contacts_tutors);
					listView.setAdapter(new ContactsListAdapter(
							AppTutorActivity.this, contacts));
				}
			}
		});
	}

	// end initial page

	// call service and get data from server.
	public void sendMessageDiy(final View view) {
		if (isTutorOrService.equals("辅导")) {
			combieArgumentForTutor(view,
					((EditText) findViewById(R.id.et_users_phone)).getText()
							.toString());
			initialDialogLayout(ServeType.自由短信);
		} else if (isTutorOrService.equals("推荐")) {

			final AlertDialog dialog;
			dialog = showWaitDialog();
			viewModel.doRecommendDiy(
					((EditText) findViewById(R.id.et_users_phone)).getText()
							.toString(), new GetDataCompletedListener() {
						@Override
						public void GetDataCompleted(Object data) {
							onSendMessageCompleted();
							dialog.dismiss();
						}
					});
		}
	}

	public void sendMessageOrderUsers(View view) {
		if (isTutorOrService.equals("辅导")) {
			Spinner spPage = (Spinner) this
					.findViewById(R.id.sp_orderusers_selelct_users);
			String scope = spPage.getSelectedItem().toString();
			if (scope.equals("全部用户")) {
				combieArgumentForTutor(view, "All");
				initialDialogLayout(ServeType.全部派单);
			} else if (scope.equals("本页用户")) {
				MessageBoolean message = verifyAndAppendTels(view);
				if (message.returnValue) {
					combieArgumentForTutor(view, message.Message);
					initialDialogLayout(ServeType.本页派单);
				}
			}
		} else if (isTutorOrService.equals("推荐")) {
			MessageBoolean message = verifyAndAppendTels(view);
			if (message.returnValue) {
				final AlertDialog dialog;
				dialog = showWaitDialog();
				viewModel.doRecommendPage(message.Message,
						new GetDataCompletedListener() {
							@Override
							public void GetDataCompleted(Object data) {
								onSendMessageCompleted();
								dialog.dismiss();
							}
						});
			}
		}
	}

	private MessageBoolean verifyAndAppendTels(View view) {
		MessageBoolean result = new MessageBoolean();
		result.returnValue = true;
		result.Message = "";
		if (orderUsers == null) {
			Builder builder = new AlertDialog.Builder(
					(Context) (getParent() == null ? applicationData
							.getCurrentActivity() : getParent()));
			builder.setMessage("没有派单用户.点击获取按钮或尝试修改筛选条件.");

			AlertDialog dialog = builder.show();

			TextView messageView = (TextView) dialog
					.findViewById(android.R.id.message);
			messageView.setGravity(Gravity.CENTER);
			result.returnValue = false;
		} else if (orderUsers.size() == 0) {
			Builder builder = new AlertDialog.Builder(
					(Context) (getParent() == null ? applicationData
							.getCurrentActivity() : getParent()));
			builder.setMessage("没有派单用户.点击获取按钮或尝试修改筛选条件.");

			AlertDialog dialog = builder.show();

			TextView messageView = (TextView) dialog
					.findViewById(android.R.id.message);
			messageView.setGravity(Gravity.CENTER);
			result.returnValue = false;
		} else {

			for (OrderUser orderUser : this.orderUsers) {
				if (orderUser.getisChecked()) {
					result.Message += orderUser.getphone() + ";";
				}
			}
			if (result.Message.length() == 0) {
				Builder builder = new AlertDialog.Builder(
						(Context) (getParent() == null ? applicationData
								.getCurrentActivity() : getParent()));
				builder.setMessage("没有勾选任何派单用户.");

				AlertDialog dialog = builder.show();

				TextView messageView = (TextView) dialog
						.findViewById(android.R.id.message);
				messageView.setGravity(Gravity.CENTER);
				result.returnValue = false;
			}
		}
		return result;
	}

	public void doToturContacats(View view) {
		Spinner spPage = (Spinner) this
				.findViewById(R.id.sp_contacts_selelct_users);
		String scope = spPage.getSelectedItem().toString();
		MessageBoolean message = new MessageBoolean();
		message.returnValue = true;
		message.Message = "";
		if (scope.equals("本页用户")) {
			message = verifyAndAppendContactsTels(view);
			if (!message.returnValue) {
				return;
			}
		}
		if (isTutorOrService.equals("辅导")) {
			combieArgumentForTutor(view, message.Message);
			initialDialogLayout(ServeType.联系人);
		} else if (isTutorOrService.equals("推荐")) {
			final AlertDialog dialog;
			dialog = showWaitDialog();
			viewModel.doRecommendPage(message.Message,
					new GetDataCompletedListener() {
						@Override
						public void GetDataCompleted(Object data) {
							onSendMessageCompleted();
							dialog.dismiss();
						}
					});
		}
	}

	private MessageBoolean verifyAndAppendContactsTels(View view) {
		MessageBoolean result = new MessageBoolean();
		result.returnValue = true;
		result.Message = "";
		if (contacts == null) {
			contacts = new ArrayList<ContactPersion>();
		}
		for (ContactPersion contact : this.contacts) {
			if (contact.getisChecked()) {
				result.Message += contact.getphone() + ";";
			}
		}
		if (result.Message.length() == 0) {
			Builder builder = new AlertDialog.Builder(
					(Context) (getParent() == null ? applicationData
							.getCurrentActivity() : getParent()));
			builder.setMessage("没有勾选任何联系人.");

			AlertDialog dialog = builder.show();

			TextView messageView = (TextView) dialog
					.findViewById(android.R.id.message);
			messageView.setGravity(Gravity.CENTER);
			result.returnValue = false;
		}
		return result;
	}

	public void getTutorUsers(final View view) {
		// TextView currentPage =
		// (TextView)view.findViewById(R.id.tv_current_page);
		// Integer pageNumber=Integer.valueOf(currentPage.getText().toString());
		btnGetData.setEnabled(false);
		Spinner spPlatform = (Spinner) findViewById(R.id.sp_select_platform);
		Platform platform = Platform.valueOf(spPlatform.getSelectedItem()
				.toString());
		this.orderUsers = new ArrayList<OrderUser>();
		EditText etMessageCount = (EditText) findViewById(R.id.et_message_count);
		String messageCountStr = etMessageCount.getText().toString().equals("") ? "0"
				: etMessageCount.getText().toString();
		int messageCount = Integer.parseInt(messageCountStr);

		if (Utils.isInteger(etMessageCount.getText().toString(), true))
			messageCount = Integer.valueOf(etMessageCount.getText().toString());
		viewModel.getTutorUsers(1, platform, messageCount, this.orderUsers,
				new GetDataCompletedListener() {

					@Override
					public void GetDataCompleted(Object list) {
						lView.setVisibility(View.VISIBLE);
						btnGetData.setVisibility(View.GONE);
						lView.setAdapter(new OrderUsertListAdapter(
								AppTutorActivity.this, orderUsers));

						TextView resoutCount = (TextView) findViewById(R.id.tv_resout_count);
						resoutCount.setText(String.valueOf(orderUsers.size()));
					}
				});
	}

	public void filtrateOrderUsers(final View view) {
		this.getTutorUsers(view);
	}

	public void backToPrevious(View view) {
		super.onBackPressed();
	}

	// end call service and get data from server.

	// call service helper
	private void combieArgumentForTutor(final View view, String tels) {
		recommendation = new AppRecommendation();
		recommendation.ApplicationId = applicationData.getcurrentFDItemInf()
				.getID();
		recommendation.applicationName = applicationData.getcurrentFDItemInf()
				.getName();
		recommendation.telPhones = tels;
		recommendation.Tname = applicationData.getcurrentUser().Tel;
		recommendation.TutorDecrible = applicationData.getcurrentFDItemInf()
				.getRecommend_Decrible();
		recommendation.userId = Integer.valueOf(applicationData
				.getcurrentUser().userid);
		recommendation.cityId = Integer.valueOf(applicationData
				.getcurrentUser().City_id);
		recommendation.PushDate = String.valueOf(new Date().getTime() / 1000);
	}

	private void initialDialogLayout(final ServeType flag) {
		recommendDialog = new Dialog(this, R.style.Theme_dialog);
		Button confirmButton = initialRecommandDialog();
		confirmButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final AlertDialog dialog;
				dialog = showWaitDialog();
				GetDataCompletedListener listener = new GetDataCompletedListener() {
					@Override
					public void GetDataCompleted(Object data) {
						onSendMessageCompleted();
						dialog.dismiss();
					}
				};
				switch (flag) {
				case 自由短信:
					viewModel.doTutorDiy(recommendation, listener);
					return;
				case 全部派单:
					viewModel.doTutorPage(recommendation, listener);
					return;
				case 本页派单:
					viewModel.doTutorPage(recommendation, listener);
					return;
				case 联系人:
					viewModel.doTutorPage(recommendation, listener);
					return;
				default:
					return;
				}
			}
		});

		Button cancelButton = (Button) recommendDialog
				.findViewById(R.id.recommend_btnCancel);
		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (recommendDialog != null)
					recommendDialog.dismiss();
			}
		});
		recommendDialog.show();
	}

	private Button initialRecommandDialog() {
		recommendDialog.setCanceledOnTouchOutside(true);
		View dialogView = LayoutInflater.from(this).inflate(
				R.layout.dialog_recommend_layout, null);
		recommendDialog.setContentView(dialogView);
		TextView usersCount = (TextView) recommendDialog
				.findViewById(R.id.tv_users_count);
		usersCount
				.setText(String.valueOf(recommendation.telPhones.split(";").length));

		ImageView productImg = (ImageView) recommendDialog
				.findViewById(R.id.iv_product);
		productImg.setImageBitmap(applicationData.getcurrentFDItemInf()
				.getAppImage());

		TextView productname = (TextView) recommendDialog
				.findViewById(R.id.tv_product_name);
		productname.setText(applicationData.getcurrentFDItemInf().getName());

		TextView productSize = (TextView) recommendDialog
				.findViewById(R.id.tv_product_size);
		productSize.setText(applicationData.getcurrentFDItemInf().getSize());

		TextView productVersion = (TextView) recommendDialog
				.findViewById(R.id.tv_product_version);
		productVersion.setText(applicationData.getcurrentFDItemInf().getSize());

		TextView porductRecommendContent = (TextView) recommendDialog
				.findViewById(R.id.ed_product_recommend_content);
		porductRecommendContent.setText(applicationData.getcurrentFDItemInf()
				.getRecommend_Decrible());

		Button confirmButton = (Button) recommendDialog
				.findViewById(R.id.recommend_btnConfirm);
		return confirmButton;
	}

	private AlertDialog showWaitDialog() {
		final AlertDialog dialog;
		dialog = new AlertDialog.Builder(
				(Context) (getParent() == null ? applicationData
						.getCurrentActivity() : getParent())).create();
		dialog.setMessage("正在发送");
		dialog.setCanceledOnTouchOutside(false);
		dialog.setCancelable(false);
		dialog.show();
		return dialog;
	}

	private void onSendMessageCompleted() {
		if (viewModel.getisSendMessageOk()) {
			initialSucceedDialog();
		} else {
			initialFailedDialog();
		}
	}

	private void initialSucceedDialog() {
		final Dialog succeed_dialog = new Dialog(AppTutorActivity.this,
				R.style.Theme_dialog);
		succeed_dialog.setCanceledOnTouchOutside(true);
		View dialogView = LayoutInflater.from(AppTutorActivity.this).inflate(
				R.layout.dialog_recommand_success_layout, null);
		succeed_dialog.setContentView(dialogView);
		Button confirmButton = (Button) succeed_dialog
				.findViewById(R.id.btnConfirm);
		confirmButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (succeed_dialog != null)
					succeed_dialog.dismiss();
				if (recommendDialog != null)
					recommendDialog.dismiss();
				Intent intent = new Intent(AppTutorActivity.this,
						SlidingActivity.class);
				startActivity(intent);
				finish();
			}
		});

		Button cancelButton = (Button) succeed_dialog
				.findViewById(R.id.btnCancel);
		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (succeed_dialog != null)
					succeed_dialog.dismiss();
				if (recommendDialog != null)
					recommendDialog.dismiss();
			}
		});

		succeed_dialog.show();
	}

	private void initialFailedDialog() {
		final AlertDialog failedDialog = new AlertDialog.Builder(
				(Context) (getParent() == null ? applicationData
						.getCurrentActivity() : getParent())).create();
		failedDialog.setMessage("辅导失败,请联系管理员.");
		failedDialog.show();
		Runnable hideDialog = new Runnable() {

			@Override
			public void run() {
				failedDialog.dismiss();
			}
		};
		executor.schedule(hideDialog, 5, TimeUnit.SECONDS);
	}

	// end call service helper

	public class MessageBoolean {
		public Boolean returnValue;
		public String Message;
	}
}
