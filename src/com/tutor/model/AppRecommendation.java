package com.tutor.model;

import java.util.Date;

public class AppRecommendation {
	public Integer userId;
	public String Tname;
	public Integer cityId;
	public String telPhones;
	public Integer ApplicationId;
	public String applicationName;
	public String TutorDecrible;
	public String PushDate;
	
	public void setCurrentTime(){
		PushDate=String.valueOf(new Date().getTime()/1000);
	}
}
