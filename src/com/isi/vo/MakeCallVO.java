package com.isi.vo;

public class MakeCallVO {
	
	private String myExtension;
	private String callingNumber;
	private String mac_address;
	
	public String getMac_address() {
		return mac_address;
	}
	public void setMac_address(String mac_address) {
		this.mac_address = mac_address;
	}
	public String getMyExtension() {
		return myExtension;
	}
	public void setMyExtension(String myExtension) {
		this.myExtension = myExtension;
	}
	public String getCallingNumber() {
		return callingNumber;
	}
	public void setCallingNumber(String callingNumber) {
		this.callingNumber = callingNumber;
	}
	
	
}
