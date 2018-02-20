package com.isi.data;

import java.util.*;

import com.isi.constans.CALLSTATE;
import com.isi.vo.CallStateVO;
/**
*
* @author greatyun
*/
public class CallStateMgr {
	
	private static CallStateMgr callStateMgr = new CallStateMgr();
	final private Map deviceMap = Collections.synchronizedMap(new HashMap());
	
	private CallStateMgr() {}
	
	public synchronized static CallStateMgr getInstance() {
		if(callStateMgr == null){
			callStateMgr = new CallStateMgr();
		}
		return callStateMgr;
	}
	
	
	public synchronized void addDeviceState(String DN, int state) {
//		System.out.println("## 전화기 상태 변경 -> DN["+DN+"] 상태["+getCallStateString(state)+"]");
		deviceMap.put(DN, state);
		/*
		System.out.println("-------- 상태 리스트 --------");
		Set keySet = deviceMap.keySet();
		Iterator iter = keySet.iterator();
		while(iter.hasNext()){
			String key = (String) iter.next();
			System.out.println(key + " : " + getCallStateString((Integer)deviceMap.get(key)));
		}
		*/
	}
	
	public synchronized Integer getDeviceState(String DN){
		
		return (Integer) deviceMap.get(DN);
//		if(deviceMap.get(DN) == null) {
//			return null;
//		} else 
//		return (Integer)deviceMap.get(DN) == null ? CALLSTATE.IDLE : (Integer)deviceMap.get(DN);
	}
	
	private String getCallStateString(int state){
		
		String retStr = "";
		
		switch (state) {
		case CALLSTATE.ALERTING_ING:
			retStr = "ALERTING";
			break;
		case CALLSTATE.ESTABLISHED_ING:
			retStr = "ESTABLISHED";
			break;
		case CALLSTATE.IDLE:
			retStr = "IDLE";
			break;
			
		default:
			break;
		}
		
		return retStr;
		
	}
	
}
