package com.tutor.viewModel;

import android.graphics.Bitmap;

public class FDItemInfModel {
	private int ID;

	private String Name;

	private boolean Application_Type_ID;

	private String Version;

	private String Size;

	private String Introudution;

	private String recommend_Decrible;

	private String DownLoadUrl;

	private Bitmap AppImage;

	private String AppImageUrl;

	public Bitmap getAppImage() {
		return AppImage;
	}

	public void setAppImage(Bitmap appImage) {
		AppImage = appImage;
	}

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

	public String getVersion() {
		return Version;
	}

	public void setVersion(String version) {
		Version = version;
	}

	public String getSize() {
		return Size;
	}

	public void setSize(String size) {
		Size = size;
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

	public String getDownLoadUrl() {
		return DownLoadUrl;
	}

	public void setDownLoadUrl(String downLoadUrl) {
		DownLoadUrl = downLoadUrl;
	}

	public String getAppImageUrl() {
		return AppImageUrl;
	}

	public void setAppImageUrl(String appImageUrl) {
		AppImageUrl = appImageUrl;
	}
}
