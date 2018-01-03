package com.isi.vo;


/**
*
* @author greatyun
*/
public class DeviceVO {
	
	private String dn;
	private String status;
	private String ip;
	private String model;
	private String terminal;
	private String cmIP;
	private String cmUser;
	private String cmPassword;
	
	/*
	public String getCmIP() {
		return cmIP;
	}
	public DeviceVO setCmIP(String cmIP) {
		this.cmIP = cmIP;
		return this;
	}
	public String getCmUser() {
		return cmUser;
	}
	public DeviceVO setCmUser(String cmUser) {
		this.cmUser = cmUser;
		return this;
	}
	public String getCmPassword() {
		return cmPassword;
	}
	public DeviceVO setCmPassword(String cmPassword) {
		this.cmPassword = cmPassword;
		return this;
	}
	public String getTerminal() {
		return terminal;
	}
	public DeviceVO setTerminal(String terminal) {
		this.terminal = terminal;
		return this;
	}
	public String getIp() {
		return ip;
	}
	public DeviceVO setIp(String ip) {
		this.ip = ip;
		return this;
	}
	public String getModel() {
		return model;
	}
	public DeviceVO setModel(String model) {
		this.model = model;
		return this;
	}
	public String getDn() {
		return dn;
	}
	public DeviceVO setDn(String dn) {
		this.dn = dn;
		return this;
	}
	public String getStatus() {
		return status;
	}
	public DeviceVO setStatus(String status) {
		this.status = status;
		return this;
	}
	*/
	public String toString() {
		return "dn["+dn+"]ip["+ip+"]model["+model+"]status["+status+"]terminal["+terminal+"]";
	}
	
	
}
