package com.tutor.model;


public class AppConfiguration {
	public static final String NAMESPACE = "http://timeapi.snty.cc";
	public static final String LOGIN_SERVICE="http://timeapi.snty.cc/index.php?mod=member&ac=loginApi";
	public static final String APP_TUTOR_SERVICE="http://timeapi.snty.cc/index.php?mod=appApi&ac=appTutor";
	public static final String GUEST_TUTOR_SERVICE="http://timeapi.snty.cc/index.php?mod=appApi&ac=guestTutor";
	public boolean isSendFromLocal;
}
