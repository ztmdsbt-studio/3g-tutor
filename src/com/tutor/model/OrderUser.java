package com.tutor.model;


public class OrderUser {
	private Integer id;
	private String phone;
	private Platform platform;
	private Integer count;
	private Boolean isChecked = true; 

	public Integer getid() {
		return this.id;
	}

	public void setid(Integer id) {
		this.id = id;
	}

	public String getphone() {
		return this.phone;
	}

	public void setphone(String phone) {
		this.phone = phone;
	}

	public Platform getplatform() {
		return this.platform;
	}

	public void setplatform(Platform platform) {
		this.platform = platform;
	}

	public Integer getcount() {
		return this.count;
	}

	public void setcount(Integer count) {
		this.count = count;
	}

	public Boolean getisChecked() {
		return this.isChecked;
	}

	public void setisChecked(Boolean isChecked) {
		this.isChecked = isChecked;
	}
}
