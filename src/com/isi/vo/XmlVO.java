package com.isi.vo;

import com.isi.data.CallID;
/**
*
* @author greatyun
*/
public class XmlVO {
	
	private String dn;
	private String callingDn;
	private String calledDn;
	private String alertingdn;
	private String targetdn;
	private String terminal;
	private String targetIP;
	private String targetModel;
	private CallID callid;
	private String strCallID;
	private String cmUser;
	private String cmPassword;
	
	
	public String getCmUser() {
		return cmUser;
	}
	public XmlVO setCmUser(String cmUser) {
		this.cmUser = cmUser;
		return this;
	}
	public String getCmPassword() {
		return cmPassword;
	}
	public XmlVO setCmPassword(String cmPassword) {
		this.cmPassword = cmPassword;
		return this;
	}
	public XmlVO setCallidByString(String callID){
		this.strCallID = callID;
		return this;
	}
	public String getCallidByString(){
		return strCallID;
	}
	public String getTargetModel() {
		return targetModel;
	}
	public XmlVO setTargetModel(String targetModel) {
		this.targetModel = targetModel;
		return this;
	}
	public String getTargetIP() {
		return targetIP;
	}
	public XmlVO setTargetIP(String targetIP) {
		this.targetIP = targetIP;
		return this;
	}
	public String getDn() {
		return dn;
	}
	public XmlVO setDn(String dn) {
		this.dn = dn;
		return this;
	}
	public String getCallingDn() {
		return callingDn;
	}
	public XmlVO setCallingDn(String callingDn) {
		this.callingDn = callingDn;
		return this;
	}
	public String getCalledDn() {
		return calledDn;
	}
	public XmlVO setCalledDn(String calledDn) {
		this.calledDn = calledDn;
		return this;
	}
	public String getAlertingdn() {
		return alertingdn;
	}
	public XmlVO setAlertingdn(String alertingdn) {
		this.alertingdn = alertingdn;
		return this;
	}
	public String getTargetdn() {
		return targetdn;
	}
	public XmlVO setTargetdn(String targetdn) {
		this.targetdn = targetdn;
		return this;
	}
	public String getTerminal() {
		return terminal;
	}
	public XmlVO setTerminal(String terminal) {
		this.terminal = terminal;
		return this;
	}
	public CallID getCallid() {
		return callid;
	}
	public XmlVO setCallid(CallID callid) {
		this.callid = callid;
		this.strCallID = callid.getGCallID();
		return this;
	}
	
	public String toString(){
		return "dn["+dn+"]callingDn["+callingDn+"]calledDn["+calledDn+"]alertingdn["+alertingdn+"]targetdn["+targetdn+"]terminal["+terminal+"]"
				+ "targetIP["+targetIP+"]targetModel["+targetModel+"]";
				
	}
	
}
