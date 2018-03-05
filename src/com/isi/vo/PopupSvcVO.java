package com.isi.vo;

public class PopupSvcVO extends EmployeeVO{
	
	private String calling_num;
	private String called_num;
	private String datetime;
	private String popup_yn;
	private String description;
	public String getCalling_num() {
		return calling_num;
	}
	public void setCalling_num(String calling_num) {
		this.calling_num = calling_num;
	}
	public String getCalled_num() {
		return called_num;
	}
	public void setCalled_num(String called_num) {
		this.called_num = called_num;
	}
	public String getDatetime() {
		return datetime;
	}
	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}
	public String getPopup_yn() {
		return popup_yn;
	}
	public void setPopup_yn(String popup_yn) {
		this.popup_yn = popup_yn;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
	
}
