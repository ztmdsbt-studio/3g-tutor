package com.tutor.model;

public class Category {
	private String flag;
	private String name;
	private String id;

	public Category(String flag, String name, String id) {
		this.flag = flag;
		this.name = name;
		this.id = id;
	}
	
	public Category(){
		
	}

	public String getflag() {
		return this.flag;
	}

	public void setflag(String flag) {
		this.flag = flag;
	}

	public String getname() {
		return this.name;
	}

	public void setname(String name) {
		this.name = name;
	}

	public String getid() {
		return this.id;
	}

	public void setid(String id) {
		this.id = id;
	}
}
