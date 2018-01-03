package com.test.vo;

public class TestCallVO {
	
	private String callingDN;
	private String calledDN;
	private String name;
	private String division;
	private String team;
	private String email;
	private String phoneNum;
	private String targetIP;
	private String targetModel;
	private String callType;
	
	
	public String getCallType() {
		return callType;
	}
	public TestCallVO setCallType(String callType) {
		this.callType = callType;
		return this;
	}
	public String getTargetModel() {
		return targetModel;
	}
	public TestCallVO setTargetModel(String targetModel) {
		this.targetModel = targetModel;
		return this;
	}
	public String getTargetIP() {
		return targetIP;
	}
	public TestCallVO setTargetIP(String targetIP) {
		this.targetIP = targetIP;
		return this;
	}
	public String getCallingDN() {
		return callingDN;
	}
	public TestCallVO setCallingDN(String callingDN) {
		this.callingDN = callingDN;
		return this;
	}
	public String getCalledDN() {
		return calledDN;
	}
	public TestCallVO setCalledDN(String calledDN) {
		this.calledDN = calledDN;
		return this;
	}
	public String getName() {
		return name;
	}
	public TestCallVO setName(String name) {
		this.name = name;
		return this;
	}
	public String getDivision() {
		return division;
	}
	public TestCallVO setDivision(String division) {
		this.division = division;
		return this;
	}
	public String getTeam() {
		return team;
	}
	public TestCallVO setTeam(String team) {
		this.team = team;
		return this;
	}
	public String getEmail() {
		return email;
	}
	public TestCallVO setEmail(String email) {
		this.email = email;
		return this;
	}
	public String getPhoneNum() {
		return phoneNum;
	}
	public TestCallVO setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
		return this;
	}
	
	public String toString(){
		return this.calledDN + " , " + this.callingDN;
	}
	}
