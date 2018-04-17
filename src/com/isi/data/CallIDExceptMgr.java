package com.isi.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.isi.vo.EmployeeVO;
import com.isi.vo.PushVO;

public class CallIDExceptMgr {
	
	
	
	private static CallIDExceptMgr callIDMgr = new CallIDExceptMgr();
	
	private Map <String, String> callIDMap = new HashMap<String, String>();
	
	
	public synchronized static CallIDExceptMgr getInstance() {
		
		if(callIDMgr == null) {
			callIDMgr = new CallIDExceptMgr();
		}
		return callIDMgr;
	}
	
	public  void addCallIDObject(String callID , String mac_address) {
		synchronized (callIDMap) {
			callIDMap.put(callID, mac_address);
		}
	}
	
	public synchronized String getCallIdObject (String callID) {
		return callIDMap.remove(callID);
	}
	
	
}
