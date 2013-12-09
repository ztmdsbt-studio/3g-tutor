package com.tutor.viewModel;

import android.graphics.Bitmap;

public class JFItemInfModel {

	private int ID;

	private String Name;

	private boolean Application_Type_ID;

	private String Point;

	private String Introudution;

	private String recommend_Decrible;

	private Bitmap AppImage;

	private String AppImageUrl;

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public boolean isApplication_Type_ID() {
		return Application_Type_ID;
	}

	public void setApplication_Type_ID(boolean application_Type_ID) {
		Application_Type_ID = application_Type_ID;
	}

	public String getPoint() {
		return Point;
	}

	public void setPoint(String point) {
		Point = point;
	}

	public String getIntroudution() {
		return Introudution;
	}

	public void setIntroudution(String introudution) {
		Introudution = introudution;
	}

	public String getRecommend_Decrible() {
		return recommend_Decrible;
	}

	public void setRecommend_Decrible(String recommend_Decrible) {
		this.recommend_Decrible = recommend_Decrible;
	}

	public Bitmap getAppImage() {
		return AppImage;
	}

	public void setAppImage(Bitmap appImage) {
		AppImage = appImage;
	}

	public String getAppImageUrl() {
		return AppImageUrl;
	}

	public void setAppImageUrl(String appImageUrl) {
		AppImageUrl = appImageUrl;
	}
}
