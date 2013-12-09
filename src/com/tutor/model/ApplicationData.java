package com.tutor.model;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.tutor.utilities.ImageLoader;
import com.tutor.viewModel.FDItemInfModel;
import com.tutor.viewModel.JFItemInfModel;

//import com.tutor.viewModel.JFItemInfModel;

public class ApplicationData extends Application {

	private LoginUser currentUser;

	private AppConfiguration appConfiguration;

	private boolean isLogin;

	private FDItemInfModel currentFDItemInf;

	private JFItemInfModel currentJFItemInf;

	public static Context context = null;

	public ImageLoader imageLoader;

	private Activity mCurrentActivity = null;

	private List<Category> appCategories;
	private List<Category> serviceCategories;

	public JFItemInfModel getCurrentJFItemInf() {
		return currentJFItemInf;
	}

	public void setCurrentJFItemInf(JFItemInfModel currentJFItemInf) {
		this.currentJFItemInf = currentJFItemInf;
	}

	public LoginUser getcurrentUser() {
		return currentUser;
	}

	public void setcurrentUser(LoginUser user) {
		currentUser = user;
	}

	public AppConfiguration getappConfiguration() {
		return appConfiguration;
	}

	public void setappConfiguration(AppConfiguration appConfiguration) {
		this.appConfiguration = appConfiguration;
	}

	public boolean getisLogin() {
		return isLogin;
	}

	public void setisLogin(boolean isLogin) {
		this.isLogin = isLogin;
	}

	public FDItemInfModel getcurrentFDItemInf() {
		return currentFDItemInf;
	}

	public void setcurrentFDItemInf(FDItemInfModel model) {
		currentFDItemInf = model;
	}

	public Activity getCurrentActivity() {
		return mCurrentActivity;
	}

	public void setCurrentActivity(Activity mCurrentActivity) {
		this.mCurrentActivity = mCurrentActivity;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		context = getApplicationContext();
		imageLoader = new ImageLoader(context);
	}

	public List<Category> getappCategories() {
		if (appCategories == null) {
			appCategories = new ArrayList<Category>();
		}
		return this.appCategories;
	}

	public List<Category> getserviceCategories() {
		if (serviceCategories == null) {
			serviceCategories = new ArrayList<Category>();
		}
		return this.serviceCategories;
	}
}
