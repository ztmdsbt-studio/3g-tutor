package com.tutor.model;

public enum Platform {
	//None(1),
	ȫ��, Windows, Android, Linux, Bada, ��ݮ, Iphone;

	private int value;

//	private Platform(int index) {
//		this.value = index;
//	}

	public static Platform valueOf(Integer index) {
		
		switch (index) {
		case 0:
			return ȫ��;
		case 1:
			return Windows;
		case 2:
			return Android;
		case 3:
			return Linux;
		case 4:
			return Bada;
		case 5:
			return ��ݮ;
		case 6:
			return Iphone;
		}
		return null;
	}

	public int value() {
		return this.value;
	}
}
