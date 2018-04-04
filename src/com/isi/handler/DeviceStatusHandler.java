package com.isi.handler;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.json.*;

import com.isi.axl.soap.SxmlHandler;
import com.isi.data.Employees;
import com.isi.data.XmlInfoMgr;
import com.isi.file.LogMgr;
import com.isi.vo.EmployeeVO;

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
		
	public boolean isRegisteredDevice(EmployeeVO empVO) {
		SxmlHandler soap = new SxmlHandler();
		
		xmlInfo = XmlInfoMgr.getInstance();
		
		Object obj = soap.selectDeviceStatusJSON(empVO.getCm_ip(), empVO.getCm_user(), empVO.getCm_pwd(), empVO.getMac_address() , empVO.getRequestID());
		JSONArray jsonArr = null;
		if(obj instanceof JSONObject) {
			JSONObject json = (JSONObject) obj;
			jsonArr = new JSONArray();
			jsonArr.put(json);
		} else if(obj instanceof JSONArray) {
			jsonArr = (JSONArray) obj;
		}
		
//		System.out.println(jsonArr.toString(4));
		
		if(jsonArr == null) {
			return false;
		}
		
		if(jsonArr.length() == 0) {
			return false;
		}
		
		JSONObject jsonObj = jsonArr.getJSONObject(0);
		
		//System.out.println(jsonObj.toString(4));
		
		JSONObject tempJson = (JSONObject) jsonObj.get("DirNumber");
		if(tempJson == null) {
			return false;
		}
		
		if(!tempJson.has("content")) {
			return false;
		}
		
		if(tempJson.get("content") == null) {
			return false;
		}
		
//		{"content":"8925-Registered,8999-Registered,7099-Registered","xsi:type":"xsd:string"}
		
		if(empVO.getExtension() == null) {
			return false;
		}
		
		boolean returnBoolean = false;
		
		Object tempObj = tempJson.get("content");
		if(tempObj == null) {
			return false;
		} else {
			String content =tempJson.get("content").toString(); 
			String []first_arr = content.split(",");
			for (int i = 0; i < first_arr.length; i++) {
				System.out.println(first_arr[i]);
				String [] arr = first_arr[i].split("-");
				String currentExtension = "";
				String deviceStatus = "";
				if(arr.length > 1) {
					currentExtension = arr[0];
					deviceStatus = arr[1];
				}
				if(empVO.getExtension().equals(currentExtension)) {
					
					if(deviceStatus.endsWith("Registered")) {
						returnBoolean = true;
					}					
				}
			}
		}
		
		
		return returnBoolean;
		
	}
	
	
}
