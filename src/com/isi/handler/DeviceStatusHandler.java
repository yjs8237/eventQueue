package com.isi.handler;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.json.*;

import com.isi.axl.soap.SxmlHandler;
import com.isi.data.Employees;
import com.isi.data.XmlInfoMgr;
import com.isi.file.LogMgr;

public class DeviceStatusHandler {
	

	private LogMgr logwrite;
	private StringWriter sw;
	private PrintWriter pw;
	private XmlInfoMgr xmlInfo;
	
	
	private static DeviceStatusHandler object = new DeviceStatusHandler();

	private DeviceStatusHandler() {
		sw = new StringWriter();
		pw = new PrintWriter(sw);
		logwrite = LogMgr.getInstance();
	}

	public synchronized static DeviceStatusHandler getInstance() {

		if (object == null) {
			object = new DeviceStatusHandler();
		}
		return object;
	}
		
	public boolean isRegisteredDevice(String mac_address) {
		SxmlHandler soap = new SxmlHandler();
		
		xmlInfo = XmlInfoMgr.getInstance();
		
		Object obj = soap.selectDeviceStatusJSON(xmlInfo.getCm1IpAddr(), xmlInfo.getCm1User(), xmlInfo.getCm1Pwd(), mac_address);
		JSONArray jsonArr = null;
		if(obj instanceof JSONObject) {
			JSONObject json = (JSONObject) obj;
			jsonArr = new JSONArray();
			jsonArr.put(json);
		} else if(obj instanceof JSONArray) {
			jsonArr = (JSONArray) obj;
		}
		
		
		if(jsonArr == null) {
			return false;
		}
		
		if(jsonArr.length() == 0) {
			return false;
		}
		
		JSONObject jsonObj = jsonArr.getJSONObject(0);
		
		JSONObject tempJson = (JSONObject) jsonObj.get("DirNumber");
		if(tempJson == null) {
			return false;
		}
		
		String deviceStatus = tempJson.get("content").toString();
		
//		System.out.println("deviceStatus : " + deviceStatus);
		
		if(!deviceStatus.endsWith("Registered")) {
			return false;
		}
		
		return true;
		
	}
	
	
	
}
