package com.isi.handler;

import java.net.HttpURLConnection;

import com.isi.constans.CALLSTATE;
import com.isi.constans.LOGLEVEL;
import com.isi.constans.LOGTYPE;
import com.isi.constans.RESULT;
import com.isi.data.CallStateMgr;
import com.isi.data.Employees;
import com.isi.db.JDatabase;
import com.isi.event.TermConnEvt;
import com.isi.file.ILog;
import com.isi.file.LogMgr;
import com.isi.file.LogWriter;
import com.isi.vo.*;
/**
*
* @author greatyun
*/
public class UDPHandler {
	
//	private ILog 			m_Log;
	private LogMgr 			m_Log;
	private XmlVO 			xmlVO;
	private XMLHandler 		xmlHandler;
	private JDatabase		dataBase;
	private CallStateMgr	callMgr;
	private String 			threadID;
	
	public UDPHandler (JDatabase dataBase, String threadID) {
		this.dataBase = dataBase;
		this.threadID = threadID;
		xmlHandler = new XMLHandler(dataBase,threadID);
		m_Log = LogMgr.getInstance();
	}
	

	public int callRingUDP(String[] aParam, int aCount) throws Exception { // Ring이
																			// 울렸을
																			// 경우

		int retCode = RESULT.RTN_EXCEPTION;

		if (aParam[11].equals("1")) {
			// P0100011302192.168.22.110493130413021/2/2811800/0/01304^1
//			   P010001130413049010322282371/2/2940840/0/0^1]

			CallStateVO callVO = new CallStateVO().
					setCallingDN(aParam[5]).
					setCalledDN(aParam[6]).
					setTargetDN(aParam[2]).
					setCallID(aParam[7]).
					setDN(aParam[10].replace("^", ""));
			
			/*
			String ani = aParam[5];
			String targetDN = aParam[6];
			
			// Target Device 정보
			EmployeeVO emp = Employees.getInstance().getEmployee(targetDN , threadID);
//			DeviceVO dev = DeviceMgr.getInstance().getDevice(targetDN);
			
			if (emp != null) {
				retCode = xmlHandler.evtRing(makeAlertingXmlVO(callVO, emp) , threadID);
			} else {
				m_Log.standLog(threadID, "callRingUDP", "dev null");
			}
			
			CallStateMgr.getInstance().addDeviceState(aParam[6] , CALLSTATE.ALERTING_ING);
			
			dataBase.insertCallingHistory(aParam[7],aParam[5], aParam[6]);
			*/
		}
		return retCode == HttpURLConnection.HTTP_OK ? retCode : RESULT.RTN_EXCEPTION;

	}
	
	    
	public int callEstablishUDP(String[] aParam, int aCount) throws Exception { // 전화받았을 경우
		//P0110011302130413021/2/2840870/0/01304^1  일반콜
		//P0110011303130413031/2/2841730/0/01304^1	당겨받기
//		P0110011302130413021/2/2934710/0/01304^PICKUP1]
		int retCode = RESULT.RTN_EXCEPTION;
		
		if (aParam[12].equals("1")) {

			String targetDN 	= aParam[2];
			String callingDN 	= aParam[5];
			String calledDN		= aParam[6];
			String callType		= aParam[11];
			String callID		= aParam[7];
			String isTransfer	= aParam[9];
			
			if(isTransfer.equalsIgnoreCase("TR")){
				if(callingDN.isEmpty()){
					return RESULT.RTN_EXCEPTION;
				}
			}
			
			CallStateVO callVO = new CallStateVO()
					.setCalledDN(aParam[6])
					.setCallID(aParam[7])
					.setCallingDN(aParam[5])
					.setDN(aParam[10].replace("^", ""))
					.setTargetDN(aParam[2]);
			/*
			// Target Device 정보
			EmployeeVO emp = Employees.getInstance().getEmployee(targetDN , threadID);
//			DeviceVO dev = DeviceMgr.getInstance().getDevice(targetDN);

			if (emp != null) {
				retCode = xmlHandler.evtEstablished(makeEstablishXmlVO(callVO, emp), callingDN , threadID);
			} else {
				m_Log.standLog(threadID, "callEstablishUDP", "There is no Device ["+targetDN+"]");
			}

			callMgr = CallStateMgr.getInstance(); 
			if(callType.equalsIgnoreCase("PICKUP")){
				// 당겨받기
				callMgr.addDeviceState(callingDN, CALLSTATE.ESTABLISHED_ING);
				callMgr.addDeviceState(calledDN, CALLSTATE.ESTABLISHED_ING);
				if(callingDN.length() > 7){
					// 외부 인입콜인데 당겨받았을 경우
					
					dataBase.updateUAHistory(callID, callingDN, calledDN);
				} else {
					dataBase.insertCalledHistory(callID,callingDN, calledDN);	// XML 통화 이력을 위한 DB INSERT
				}
				
			} else if(isTransfer.equalsIgnoreCase("TR")){
				callMgr.addDeviceState(callingDN, CALLSTATE.ESTABLISHED_ING);
				callMgr.addDeviceState(calledDN, CALLSTATE.ESTABLISHED_ING);
			} else if(callMgr.getDeviceState(calledDN) == CALLSTATE.ESTABLISHED_ING
					|| callMgr.getDeviceState(calledDN) == CALLSTATE.IDLE) {
				callMgr.addDeviceState(callingDN, CALLSTATE.ESTABLISHED_ING);
				callMgr.addDeviceState(calledDN, CALLSTATE.ESTABLISHED_ING);
			} else {
				callMgr.addDeviceState(callingDN, CALLSTATE.ESTABLISHED_ING);
				callMgr.addDeviceState(calledDN, CALLSTATE.ESTABLISHED_ING);
				dataBase.insertCalledHistory(callID,callingDN, calledDN);	// XML 통화 이력을 위한 DB INSERT
			}
		*/	
		}
		
		return retCode == HttpURLConnection.HTTP_OK ? retCode :  RESULT.RTN_EXCEPTION;

	}
	
	public int callNetworkPickUpEstablishUDP(String[] aParam, int aCount) throws Exception { // 

		int retCode = RESULT.RTN_EXCEPTION;
		
		if (aParam[12].equals("1")) {

			String targetDN 	= aParam[2];
			String callingDN 	= aParam[5];
			String calledDN		= aParam[6];
			String callType		= aParam[11];
			String callID		= aParam[7];
			String isTransfer	= aParam[9];
			
			CallStateVO callVO = new CallStateVO()
					.setCalledDN(aParam[6])
					.setCallID(aParam[7])
					.setCallingDN(aParam[5])
					.setDN(aParam[10].replace("^", ""))
					.setTargetDN(aParam[2]);
			/*
			// Target Device 정보
			EmployeeVO emp = Employees.getInstance().getEmployee(targetDN , threadID);
//			DeviceVO dev = DeviceMgr.getInstance().getDevice(targetDN);

			if (emp != null) {
				retCode = xmlHandler.evtEstablished(makeEstablishXmlVO(callVO, emp), callingDN , threadID);
			} else {
				m_Log.standLog(threadID, "callEstablishUDP", "There is no Device ["+targetDN+"]");
			}

			callMgr = CallStateMgr.getInstance(); 
			
			
			
			
			dataBase.insertCalledHistory(callID,callingDN, calledDN);	// XML 통화 이력을 위한 DB INSERT
			
			*/
		}
		
		return retCode == HttpURLConnection.HTTP_OK ? retCode :  RESULT.RTN_EXCEPTION;

	}
	
	public int callDisconnectUDP(String[] aParam, int aCount) throws Exception{
		//P013
		int retCode = RESULT.RTN_EXCEPTION;
		
		if (aParam[10].equals("1")) {
			
			String targetDN 	= aParam[2];
			String callid		= aParam[7];
			String callingDN 	= aParam[5];
			String calledDN		= aParam[6];
			
			CallStateVO callVO = new CallStateVO()
			.setCalledDN(aParam[6])
			.setCallID(aParam[7])
			.setCallingDN(aParam[5])
			.setDN(aParam[10].replace("^", ""))
			.setTargetDN(aParam[2]);
			
			/*
			callMgr = CallStateMgr.getInstance();
			Integer targetState 	= callMgr.getDeviceState(targetDN);
			if(targetState == CALLSTATE.ALERTING_ING){
				// Target Device 정보
				EmployeeVO emp = Employees.getInstance().getEmployee(targetDN , threadID);
//				DeviceVO dev = DeviceMgr.getInstance().getDevice(targetDN);
				if (emp != null) {
					retCode = xmlHandler.evtDisconnect(makeDisconnectXmlVO(callVO, emp) , threadID);
				} else {
					m_Log.standLog(threadID, "callDisconnectUDP", "There is no Device ["+targetDN+"]");
				}
				
				dataBase.insertUACall(callid,callingDN, targetDN);
				
				callMgr.addDeviceState(targetDN, CALLSTATE.IDLE);	// 전화기 상태 IDLE 로 변경
			}
			*/
			/*
			if(!targetDN.equals(calledDN) && !calledDN.equals(callingDN)){
				callMgr.addDeviceState(targetDN, CALLSTATE.IDLE);	// 전화기 상태 IDLE 로 변경
			} else {
				callMgr.addDeviceState(callingDN, CALLSTATE.IDLE);	// 전화기 상태 IDLE 로 변경
				callMgr.addDeviceState(targetDN, CALLSTATE.IDLE);	// 전화기 상태 IDLE 로 변경
			}
			*/
			
		}
		
		return retCode == HttpURLConnection.HTTP_OK ? retCode :  RESULT.RTN_EXCEPTION;
	}
	
	
	public int callAllDisconnectUDP(String[] aParam, int aCount) throws Exception{
		//P012
		int retCode = RESULT.RTN_EXCEPTION;
		
		if (aParam[11].equals("1")) {

			String targetDN 	= aParam[2];
			String callingDN 	= aParam[5];
			String calledDN		= aParam[6];
			String secondCall	= aParam[8];
			String callid		= aParam[7];
			String callType		= aParam[9];

			CallStateVO callVO = new CallStateVO()
			.setCalledDN(calledDN)
			.setCallID(callid)
			.setCallingDN(callingDN)
			.setDN(targetDN)
			.setTargetDN(targetDN);
			/*
			// Target Device 정보
			EmployeeVO emp = Employees.getInstance().getEmployee(targetDN , threadID);
//			DeviceVO dev = DeviceMgr.getInstance().getDevice(targetDN);

			if (emp != null) {
				retCode = xmlHandler.evtDisconnect(makeDisconnectXmlVO(callVO, emp) , threadID);
			} else {
				m_Log.standLog(threadID, "callDisconnectUDP", "There is no Device ["+targetDN+"]");
			}

			callMgr = CallStateMgr.getInstance();
			Integer targetState 	= callMgr.getDeviceState(targetDN);
			
			if(targetState == CALLSTATE.ALERTING_ING){
				
//				if(isNullCallID(secondCall) && !targetDN.equals(calledDN) && !calledDN.equals(callingDN)){
				if(!targetDN.equals(calledDN) && !calledDN.equals(callingDN)){
					// 당겨받기로 인한 Disconnect
					dataBase.insertPickUpUACall(callid,callingDN, targetDN, calledDN);
				} else {
					// 일반 부재중 통화
					dataBase.insertUACall(callid,callingDN, targetDN);
				}
			}
			
			switch (callType) {
			case "CF":
				callMgr.addDeviceState(targetDN, CALLSTATE.IDLE);	// 전화기 상태 IDLE 로 변경
				break;

			default:
				if(!targetDN.equals(calledDN) && !calledDN.equals(callingDN)){
					callMgr.addDeviceState(targetDN, CALLSTATE.IDLE);	// 전화기 상태 IDLE 로 변경
				} else {
					callMgr.addDeviceState(callingDN, CALLSTATE.IDLE);	// 전화기 상태 IDLE 로 변경
					callMgr.addDeviceState(targetDN, CALLSTATE.IDLE);	// 전화기 상태 IDLE 로 변경
				}
				break;
			}
			*/
		}
		
		return retCode == HttpURLConnection.HTTP_OK ? retCode :  RESULT.RTN_EXCEPTION;
	}
	
	  
	private XmlVO makeAlertingXmlVO(CallStateVO callVO, EmployeeVO employee) throws Exception {
		// TODO Auto-generated method stub
		
		// P0100011302192.168.22.110493130413021/2/2811800/0/01304^1
		xmlVO = new XmlVO();

		xmlVO.setDn(callVO.getDN()).setCallidByString(callVO.getCallID())
				.setAlertingdn(callVO.getCalledDN())
				.setCallingDn(callVO.getCallingDN())
				.setTargetdn(callVO.getTargetDN())
				.setTargetIP(employee.getDevice_ipaddr())
				.setTargetModel(employee.getDevice_type())
				.setCalledDn(callVO.getCalledDN())
				.setCmUser(employee.getCm_user())
				.setCmPassword(employee.getCm_pwd());

		m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, threadID, "makeEstablishXmlVO", xmlVO.toString());

		return xmlVO;
	}
	
	private XmlVO makeEstablishXmlVO(CallStateVO callVO, EmployeeVO employee) throws Exception {
		// TODO Auto-generated method stub
//		P0110011302  130413021/2/2840870/0/01304^1]
		xmlVO = new XmlVO();
		
		xmlVO.setDn(callVO.getDN()).setCallidByString(callVO.getCallID())
		.setAlertingdn(callVO.getCalledDN())
		.setCallingDn(callVO.getCallingDN()).setTargetdn(callVO.getTargetDN())
//		.setTerminal(event.getTerminal())
		.setTargetIP(employee.getDevice_ipaddr())
		.setTargetModel(employee.getDevice_type())
		.setCalledDn(callVO.getCalledDN())
		.setCmUser(employee.getCm_user())
		.setCmPassword(employee.getCm_pwd());
		
		m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG,threadID, "makeEstablishXmlVO", xmlVO.toString());
		
		return xmlVO;
	}
	
	private XmlVO makeDisconnectXmlVO(CallStateVO callVO , EmployeeVO employee)throws Exception {
		// TODO Auto-generated method stub
		
		xmlVO = new XmlVO();
		
		xmlVO.setDn(callVO.getDN()).setCallidByString(callVO.getCallID())
		.setAlertingdn(callVO.getCalledDN())
		.setCallingDn(callVO.getCallingDN()).setTargetdn(callVO.getTargetDN())
//		.setTerminal(event.getTerminal())
		.setTargetIP(employee.getDevice_ipaddr())
		.setTargetModel(employee.getDevice_type())
		.setCalledDn(callVO.getCalledDN())
		.setCmUser(employee.getCm_user())
		.setCmPassword(employee.getCm_pwd());
		
		m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, threadID, "makeDisconnectXmlVO", xmlVO.toString());
		
		return xmlVO;
	}
	
	private boolean isNullCallID(String callID){
		
		if(callID == null || callID.isEmpty()){
			return true;
		}
		
		callID = callID.replaceAll("/", "");
		callID = callID.replaceAll("0", "");
		
		return callID.isEmpty();
		
	}
	 
}
