package com.isi.vo;


/**
*
* @author greatyun
*/
public class CustomerVO implements IPerson {
	
	private String custLevel;
	private String name;
	private String phoneNum;
	private String position;
	private String company;
	
	private String custNo;
	
	
	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	
	

	public String getCustNo() {
		return custNo;
	}

	public CustomerVO setCustNo(String custNo) {
		this.custNo = custNo;
		return this;
	}

	public String getName() {
		return name;
	}

	public CustomerVO setName(String name) {
		this.name = name;
		return this;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public CustomerVO setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
		return this;
	}

	public String getCustLevel() {
		return custLevel;
	}

	public CustomerVO setCustLevel(String custLevel) {
		this.custLevel = custLevel;
		return this;
	}

	public String toString() {
		return this.name + " : " + 
	this.phoneNum + " : " + 
	this.company + " : " +
	this.position ;
	
	}
	
	
	
}
