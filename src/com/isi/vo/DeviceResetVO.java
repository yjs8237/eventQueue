package com.isi.vo;

public class DeviceResetVO extends BaseVO {
	
	private String extension;
	private String mac_address;
	private String device_ipaddr;
	private String cm_ip;
	private String cm_user;
	private String cm_pwd;
	public String getExtension() {
		return extension;
	}
	public void setExtension(String extension) {
		this.extension = extension;
	}
	public String getMac_address() {
		return mac_address;
	}
	public void setMac_address(String mac_address) {
		this.mac_address = mac_address;
	}
	public String getDevice_ipaddr() {
		return device_ipaddr;
	}
	public void setDevice_ipaddr(String device_ipaddr) {
		this.device_ipaddr = device_ipaddr;
	}
	public String getCm_ip() {
		return cm_ip;
	}
	public void setCm_ip(String cm_ip) {
		this.cm_ip = cm_ip;
	}
	public String getCm_user() {
		return cm_user;
	}
	public void setCm_user(String cm_user) {
		this.cm_user = cm_user;
	}
	public String getCm_pwd() {
		return cm_pwd;
	}
	public void setCm_pwd(String cm_pwd) {
		this.cm_pwd = cm_pwd;
	}
	
	
	

	
	
}
