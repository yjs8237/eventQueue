package com.isi.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.isi.vo.EmployeeVO;
import com.isi.vo.PushVO;

public class CallIDMgr {
	
	
	
	private static CallIDMgr callIDMgr = new CallIDMgr();
	
	private Map <String, List> callIDMap = new HashMap<String, List>();
	
	
	public synchronized static CallIDMgr getInstance() {
		
		if(callIDMgr == null) {
			callIDMgr = new CallIDMgr();
		}
		return callIDMgr;
	}
	
	public  void addCallIDObject(String callID , EmployeeVO empVO) {
		List list = null;
		if(callIDMap.containsKey(callID)) {
			list = callIDMap.get(callID);
			list.add(empVO);
		} else {
			list = new ArrayList<>();
			list.add(empVO);
		}
		
		synchronized (callIDMap) {
			callIDMap.put(callID, list);
		}
	}
	
	public synchronized List getCallIdObject (String callID) {
		return callIDMap.remove(callID);
	}
	
	
}
