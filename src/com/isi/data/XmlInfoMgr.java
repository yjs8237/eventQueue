package com.isi.data;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.isi.handler.HttpSyncServer;

public class XmlInfoMgr {
	
	private static XmlInfoMgr xmlInfoMgr = new XmlInfoMgr();
	
	private String xmlMode;
	private String duplexYN;
	private String sideAIP;
	private String sideBIP;
	private String remoteIP;
	private String remotePort;
	private String custinfoPopupYN;
	private int cmCnt;
	private String cm1User;
	private String cm1Pwd;
	private String cm2User;
	private String cm2Pwd;
	private String cm1IpAddr;
	private	String cm2IpAddr;
	private int	connectTimeout;
	private int readTimeout;
	private String xmlPushUrl;
	private int httpPort;
	private String consoleDebugYN;
	private int logLevel;
	private String logPath;
	private String baseImgPath;
	private String faceImgPath;
	private String empImgPath;
	private int logDelDays;
	private int http_sync_port;
	
	
	private XmlInfoMgr(){}
	public synchronized static XmlInfoMgr getInstance(){
		if(xmlInfoMgr == null){
			xmlInfoMgr = new XmlInfoMgr();
		}
		return xmlInfoMgr;
	}
	
	public static XmlInfoMgr getXmlInfoMgr() {
		return xmlInfoMgr;
	}
	public static void setXmlInfoMgr(XmlInfoMgr xmlInfoMgr) {
		XmlInfoMgr.xmlInfoMgr = xmlInfoMgr;
	}
	
	
	
	
	
	
	public int getHttp_sync_port() {
		return http_sync_port;
	}
	public void setHttp_sync_port(int http_sync_port) {
		this.http_sync_port = http_sync_port;
	}
	public int getHttpPort() {
		return httpPort;
	}
	public void setHttpPort(int httpPort) {
		this.httpPort = httpPort;
	}
	public String getRemoteIP() {
		return remoteIP;
	}
	public void setRemoteIP(String remoteIP) {
		this.remoteIP = remoteIP;
	}
	public String getXmlMode() {
		return xmlMode;
	}
	public void setXmlMode(String xmlMode) {
		this.xmlMode = xmlMode;
	}
	public String getDuplexYN() {
		return duplexYN;
	}
	public void setDuplexYN(String duplexYN) {
		this.duplexYN = duplexYN;
	}
	
	public String getSideAIP() {
		return sideAIP;
	}
	public void setSideAIP(String sideAIP) {
		this.sideAIP = sideAIP;
	}
	public String getSideBIP() {
		return sideBIP;
	}
	public void setSideBIP(String sideBIP) {
		this.sideBIP = sideBIP;
	}
	public String getRemotePort() {
		return remotePort;
	}
	public void setRemotePort(String remotePort) {
		this.remotePort = remotePort;
	}
	public String getCustinfoPopupYN() {
		return custinfoPopupYN;
	}
	public void setCustinfoPopupYN(String custinfoPopupYN) {
		this.custinfoPopupYN = custinfoPopupYN;
	}
	public int getCmCnt() {
		return cmCnt;
	}
	public void setCmCnt(int cmCnt) {
		this.cmCnt = cmCnt;
	}
	public String getCm1User() {
		return cm1User;
	}
	public void setCm1User(String cm1User) {
		this.cm1User = cm1User;
	}
	public String getCm1Pwd() {
		return cm1Pwd;
	}
	public void setCm1Pwd(String cm1Pwd) {
		this.cm1Pwd = cm1Pwd;
	}
	public String getCm2User() {
		return cm2User;
	}
	public void setCm2User(String cm2User) {
		this.cm2User = cm2User;
	}
	public String getCm2Pwd() {
		return cm2Pwd;
	}
	public void setCm2Pwd(String cm2Pwd) {
		this.cm2Pwd = cm2Pwd;
	}
	public String getCm1IpAddr() {
		return cm1IpAddr;
	}
	public void setCm1IpAddr(String cm1IpAddr) {
		this.cm1IpAddr = cm1IpAddr;
	}
	public String getCm2IpAddr() {
		return cm2IpAddr;
	}
	public void setCm2IpAddr(String cm2IpAddr) {
		this.cm2IpAddr = cm2IpAddr;
	}
	public int getConnectTimeout() {
		return connectTimeout;
	}
	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}
	public int getReadTimeout() {
		return readTimeout;
	}
	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}
	public String getXmlPushUrl() {
		return xmlPushUrl;
	}
	public void setXmlPushUrl(String xmlPushUrl) {
		this.xmlPushUrl = xmlPushUrl;
	}
	public String getConsoleDebugYN() {
		return consoleDebugYN;
	}
	public void setConsoleDebugYN(String consoleDebugYN) {
		this.consoleDebugYN = consoleDebugYN;
	}
	public int getLogLevel() {
		return logLevel;
	}
	public void setLogLevel(int logLevel) {
		this.logLevel = logLevel;
	}
	public String getLogPath() {
		return logPath;
	}
	public void setLogPath(String logPath) {
		this.logPath = logPath;
	}
	public String getBaseImgPath() {
		return baseImgPath;
	}
	public void setBaseImgPath(String baseImgPath) {
		this.baseImgPath = baseImgPath;
	}
	public String getFaceImgPath() {
		return faceImgPath;
	}
	public void setFaceImgPath(String faceImgPath) {
		this.faceImgPath = faceImgPath;
	}
	public String getEmpImgPath() {
		return empImgPath;
	}
	public void setEmpImgPath(String empImgPath) {
		this.empImgPath = empImgPath;
	}
	public int getLogDelDays() {
		return logDelDays;
	}
	public void setLogDelDays(int logDelDays) {
		this.logDelDays = logDelDays;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("xmlMode : ").append(xmlMode).append("\n");
		sb.append("duplexYN : ").append(duplexYN).append("\n");
		sb.append("sideAIP : ").append(sideAIP).append("\n");
		sb.append("sideBIP : ").append(sideBIP).append("\n");
		sb.append("remoteIP : ").append(remoteIP).append("\n");
		sb.append("remotePort : ").append(remotePort).append("\n");
		sb.append("custinfoPopupYN : ").append(custinfoPopupYN).append("\n");
		sb.append("cmCnt : ").append(cmCnt).append("\n");
		sb.append("cm1User : ").append(cm1User).append("\n");
		sb.append("cm1Pwd : ").append(cm1Pwd).append("\n");
		sb.append("cm2User : ").append(cm2User).append("\n");
		sb.append("cm2Pwd : ").append(cm2Pwd).append("\n");
		sb.append("cm1IpAddr : ").append(cm1IpAddr).append("\n");
		sb.append("cm2IpAddr : ").append(cm2IpAddr).append("\n");
		sb.append("connectTimeout : ").append(connectTimeout).append("\n");
		sb.append("readTimeout : ").append(readTimeout).append("\n");
		sb.append("xmlPushUrl : ").append(xmlPushUrl).append("\n");
		sb.append("httpPort : ").append(httpPort).append("\n");
		sb.append("consoleDebugYN : ").append(consoleDebugYN).append("\n");
		sb.append("logLevel : ").append(logLevel).append("\n");
		sb.append("logPath : ").append(logPath).append("\n");
		sb.append("baseImgPath : ").append(baseImgPath).append("\n");
		sb.append("faceImgPath : ").append(faceImgPath).append("\n");
		sb.append("empImgPath : ").append(empImgPath).append("\n");
		sb.append("logDelDays : ").append(logDelDays).append("\n");
		sb.append("http_sync_port : ").append(http_sync_port).append("\n");
		
		return sb.toString();
	}
	
	
	
}
