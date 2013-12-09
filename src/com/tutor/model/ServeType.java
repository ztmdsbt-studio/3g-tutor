package com.tutor.model;

public enum ServeType {
	//None(1),
	自由短信, 全部派单, 本页派单, 联系人;

	private int value;

//	private Platform(int index) {
//		this.value = index;
//	}

	public static ServeType valueOf(Integer index) {
		
		switch (index) {
		case 0:
			return 自由短信;
		case 1:
			return 全部派单;
		case 2:
			return 本页派单;
		case 3:
			return 联系人;
		}
		return null;
	}

	public int value() {
		return this.value;
	}
}
