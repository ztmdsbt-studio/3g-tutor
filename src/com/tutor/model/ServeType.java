package com.tutor.model;

public enum ServeType {
	//None(1),
	���ɶ���, ȫ���ɵ�, ��ҳ�ɵ�, ��ϵ��;

	private int value;

//	private Platform(int index) {
//		this.value = index;
//	}

	public static ServeType valueOf(Integer index) {
		
		switch (index) {
		case 0:
			return ���ɶ���;
		case 1:
			return ȫ���ɵ�;
		case 2:
			return ��ҳ�ɵ�;
		case 3:
			return ��ϵ��;
		}
		return null;
	}

	public int value() {
		return this.value;
	}
}
