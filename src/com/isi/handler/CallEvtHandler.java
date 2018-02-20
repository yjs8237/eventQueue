package com.isi.handler;

import java.util.List;

import com.cisco.jtapi.extensions.CiscoTermDeviceStateActiveEv;
import com.cisco.jtapi.extensions.CiscoTermDeviceStateAlertingEv;
import com.cisco.jtapi.extensions.CiscoTermDeviceStateHeldEv;
import com.cisco.jtapi.extensions.CiscoTermDeviceStateIdleEv;
import com.cisco.jtapi.extensions.CiscoTermInServiceEv;
import com.cisco.jtapi.extensions.CiscoTermOutOfServiceEv;
import com.cisco.jtapi.extensions.CiscoTerminal;
import com.isi.constans.*;
import com.isi.data.*;
import com.isi.db.JDatabase;
import com.isi.event.*;
import com.isi.file.*;
import com.isi.utils.CodeToString;
import com.isi.utils.Utils;
import com.isi.vo.*;

/**
*
* @author greatyun
*/
public class CallEvtHandler {
	
	private LogMgr 			m_Log;
	private XMLHandler 		xmlHandler;
	private XmlVO 			xmlVO;
//	private JDatabase		dataBase;
	private CallStateMgr	callMgr;
	private String			threadID;
	
	public CallEvtHandler(JDatabase dataBase, String threadID){
//		this.dataBase = dataBase;
		this.threadID = threadID;
		m_Log = LogMgr.getInstance();
		xmlHandler = new XMLHandler(dataBase , threadID);
		
	}
	
	 
	public int callRingEvt (Evt evt, String callID)  throws Exception{	// Ring 이 울릴 경우
		
		if(evt == null){
			return RESULT.RTN_EXCEPTION;
		}
		
		TermConnEvt event = (TermConnEvt) evt;
		
//		m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, callID, "callRingEvt", "########################## " + event.toString());
		
		if(event.getDevice().equals(event.getCalledDn())) { // 전화를 받는 사람 측 이벤트만 push 한다
			
			if(event.getMetaCode() != CALLSTATE.META_CALL_STARTING){
				m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, callID, "callRingEvt", ">>>>>>>>>>>>>>>>>>>>>>>> " + event.toString());
				
				// 내선 콜 상태 정보 SET
//				CallStateMgr.getInstance().addDeviceState(event.getCallingDn() , CALLSTATE.ALERTING_ING);
				CallStateMgr.getInstance().addDeviceState(event.getCalledDn() , CALLSTATE.ALERTING_ING);
				
				List employeeList = Employees.getInstance().getEmployeeListByExtension(event.getDevice(), callID);
				if(employeeList != null && employeeList.size() > 0) {
					for (int i = 0; i < employeeList.size(); i++) {
						EmployeeVO employeeVO = (EmployeeVO) employeeList.get(i);
						
						if(checkVaildPush(employeeVO,callID) != RESULT.RTN_SUCCESS) {
							
							return RESULT.ERROR;
						}
						
						m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, callID, "callRingEvt", "########### 2 ##########");
						
						xmlHandler.evtRing(makeAlertingXmlVO(event , employeeVO , callID) , callID);
					}
				}
				
				/*
				// Target Device 정보
				EmployeeVO employeeVO = Employees.getInstance().getEmployee(event.getDevice() , callID);
				if(employeeVO != null) {
					if(!employeeVO.getPopupYN().equalsIgnoreCase("N")){
						xmlHandler.evtRing(makeAlertingXmlVO(event , employeeVO , callID) , callID);
					}
				}
				*/
			}
		} else {
			
		}
		return RESULT.RTN_SUCCESS;
	}
	

	private int checkVaildPush(EmployeeVO employeeVO, String callID) {
		// TODO Auto-generated method stub
		
		int result = 0;
		
		if(employeeVO.getCm_user() == null || employeeVO.getCm_user().isEmpty() || employeeVO.getCm_user().equalsIgnoreCase("null")) {
			result = -1;
		}
		
		if(employeeVO.getCm_pwd() == null || employeeVO.getCm_pwd().isEmpty() || employeeVO.getCm_pwd().equalsIgnoreCase("null")) {
			result = -1;
		}
		
		if(employeeVO.getDevice_type() == null || employeeVO.getDevice_type().isEmpty() || employeeVO.getDevice_type().equalsIgnoreCase("null")) {
			result = -1;
		}
		
		if(result != RESULT.RTN_SUCCESS) {
			m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.ERR_LOG, callID,  "checkVaildPush" , "### Invalid employee information ### " + employeeVO.toString());
		}
		
		return result;
	}


	public int callEstablishedEvt (Evt evt , String callID)  throws Exception{
		
		if(evt == null){
			return RESULT.RTN_EXCEPTION;
		}
		
//		m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, threadID, "callEstablishedEvt", "## >>>>>>>>>>>>>>>>>>>>>>>> " + evt.toString());
		
		TermConnEvt event = (TermConnEvt) evt;
		
		String callingDN 	= event.getCallingDn(); 	// 전화를 건 사람의 DN
		String calledDN		= event.getCalledDn();		// 전화를 받은 사람의 DN
		String terminal 	= event.getTerminal();		// 이벤트 정보에 포함되어 있는 Termina(Mac address)
		String redirectDN 	= event.getRedirectDn();	// Redirect DN
		String DN			= event.getDn();			// DN
		String deviceDN		= event.getDevice();		// Device DN
		
		int ctlCause 		= event.getCtlCause();		// 콜 타입
		boolean isEastblish	= false;
		
//		DeviceVO device = deviceMgr.getDevice(callingDN);
		Employees employeeMgr = Employees.getInstance();
		EmployeeVO employee = employeeMgr.getEmployeeByExtension(callingDN , callID);
		if (employee == null){
			return RESULT.RTN_UNDEFINED_ERR;
		}
		
		switch (ctlCause) {
		

		case CALLSTATE.CONFERENCE:					// 전화회의 콜 
			
			int metaCode = event.getMetaCode();		// MetaCode
			
			if(metaCode == CALLSTATE.META_CALL_MERGING) {
				
				if(redirectDN.equals(callingDN) && callingDN.equals(DN) && !DN.equals(deviceDN)){
					m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, threadID, "callEstablishedEvt", ">>>>>>>>>>>>>>>>>>>>>>>> " + event.toString());
					
					/* 전화기 현재 상태 업데이트 */
					CallStateMgr.getInstance().addDeviceState(deviceDN , CALLSTATE.ESTABLISHED_ING);
					CallStateMgr.getInstance().addDeviceState(event.getDn() , CALLSTATE.ESTABLISHED_ING);
					
					EmployeeVO emp = employeeMgr.getEmployeeByExtension(DN , callID);
//					DeviceVO dev = DeviceMgr.getInstance().getDevice(DN);
					if(emp!=null){
						xmlHandler.evtEstablished(makeEstablishXmlVO(event , emp, callID) , deviceDN , callID);
					}
					isEastblish = true;
				} else if(!deviceDN.equals(DN) && deviceDN.equals(redirectDN)){
					m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, threadID, "callEstablishedEvt", ">>>>>>>>>>>>>>>>>>>>>>>> " + event.toString());
					
					/* 전화기 현재 상태 업데이트 */
					CallStateMgr.getInstance().addDeviceState(deviceDN , CALLSTATE.ESTABLISHED_ING);
					CallStateMgr.getInstance().addDeviceState(event.getDn() , CALLSTATE.ESTABLISHED_ING);
					
					EmployeeVO emp = employeeMgr.getEmployeeByExtension(DN , callID);
//					DeviceVO dev = DeviceMgr.getInstance().getDevice(DN);
					if(emp != null){
						xmlHandler.evtEstablished(makeEstablishXmlVO(event , emp, callID) , deviceDN , callID);
					}
					isEastblish = true;
				}
				
			}
			break;
		
		case CALLSTATE.UNHOLD:	// 통화중 투콜 토글 시
			if(!terminal.equals(employee.getMac_address())){ 
				if(event.getDevice().equals(event.getDn())){	// 이벤트 정보내에 Device 번호와 DN 번호가 같을 경우 push 처리
					m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, threadID, "callEstablishedEvt", ">>>>>>>>>>> UN HOLD >>>>>>>>>>>>> " + event.toString());
					
					/* 전화기 현재 상태 업데이트 */
					CallStateMgr.getInstance().addDeviceState(callingDN , CALLSTATE.ESTABLISHED_ING);
					CallStateMgr.getInstance().addDeviceState(event.getDn() , CALLSTATE.ESTABLISHED_ING);
					
					EmployeeVO emp = employeeMgr.getEmployeeByExtension(deviceDN , callID);
//					DeviceVO dev = DeviceMgr.getInstance().getDevice(deviceDN);
					if(emp != null) {
						Thread.sleep(100);	// 토글 시 push 가 너무 빠르게 이루어지면 기본화면이 XML 화면을 뒤덮기 때문에.. sleep 0.1 초 살짝 줘볼까
						xmlHandler.evtEstablished(makeEstablishXmlVO(event, emp, callID) , callingDN , callID);
					}
//					isEastblish = true;	// 보류해제시에는 통화이력 정보가 남을 필요가 없다
				}
			}
			break;
			
		default:	//일반 콜
			// 이벤트정보에 포함된 terminal 정보와 Calling DN 으로 검색된 Device의 terminal 정보가 같으면
			// Establish 이벤트를 처리하지 않는다. (Establish 는 전화를 받는 사람이 전화를 받아야 발생한다)
			if(!terminal.equals(employee.getMac_address())){ 
				if(event.getDevice().equals(event.getDn())){	// 이벤트 정보내에 Device 번호와 DN 번호가 같을 경우 push 처리
					m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, threadID, "callEstablishedEvt", ">>>>>>>>>>>>>>>>>>>>>>>> " + event.toString());
					
					/* 전화기 현재 상태 업데이트 */
					CallStateMgr.getInstance().addDeviceState(callingDN , CALLSTATE.ESTABLISHED_ING);
					CallStateMgr.getInstance().addDeviceState(event.getDn() , CALLSTATE.ESTABLISHED_ING);
					
					EmployeeVO emp = employeeMgr.getEmployeeByExtension(deviceDN , callID);
//					DeviceVO dev = DeviceMgr.getInstance().getDevice(deviceDN);
					if(emp != null){
//						makeDisconnectXmlVO(event , emp , callID)
						xmlHandler.evtEstablished(makeEstablishXmlVO(event, emp, callID) , callingDN , callID);
					}
					isEastblish = true;
				}
			}
			break;
		}
		
		
		if(isEastblish){	// 통화이력 관리 정보
			
			if(ctlCause != CALLSTATE.CONFERENCE){ // 전화회의 경우, 통화이력 정보를 새로 추가할 필요가 없다.
				
			}
		}
		
		return RESULT.RTN_SUCCESS;
		
	}

	public int callDisconnectEvt(Evt evt , String callID)  throws Exception {
		
		if(evt == null){
			return RESULT.RTN_EXCEPTION;
		}
		
		TermConnEvt event = (TermConnEvt) evt;

		String dn 			= event.getDn();
		String deviceDN 	= event.getDevice();
		String calledDn 	= event.getCalledDn();
		String callingDn 	= event.getCallingDn();
		String redirectDn	= event.getRedirectDn(); 
		String macaddress	= event.getTerminal();
		int callType 		= event.getCtlCause();
		int metaCode		= event.getMetaCode();
		
		int disconType		=	-1;
		boolean isDisconnect	= false;
		
//		m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, event.get_GCallID(), "callDisconnectEvt", "+++++++++++++++++++++++++++ " + event.toString());
		
		Employees employees = Employees.getInstance();
		
		if(deviceDN.equals(dn)){
			
			switch(callType){
			
			case CALLSTATE.TRANSFER:	// 호전환 완료
				if(dn.equals(callingDn)){
					m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, callID, "callDisconnectEvt", ">>>>>>>>> TRANSFER >>>>>>>>>>>>>>> " + event.toString());
					
					disconType = DISCONNECTTYPE.TRANSFER_DISCONNECT;
					
					/* 전화기 현재 상태 업데이트 */
					CallStateMgr.getInstance().addDeviceState(callingDn , CALLSTATE.IDLE);
					CallStateMgr.getInstance().addDeviceState(event.getDn() , CALLSTATE.IDLE);
					
					
					List employeeList = employees.getEmployeeListByExtension(callingDn, callID);
					if(employeeList != null && employeeList.size() > 0){
						
						for (int i = 0; i < employeeList.size(); i++) {
							
							EmployeeVO employeeVO = (EmployeeVO) employeeList.get(i);
							if(employeeVO != null){
								
								if(checkVaildPush(employeeVO,callID) != RESULT.RTN_SUCCESS) {
									return RESULT.ERROR;
								}
								
								xmlHandler.evtDisconnect(makeDisconnectXmlVO(event , employeeVO , callID) , callID);
								// XML 팝업 화면이 닫히지 않아 Disconnect XML 을 한번 더 PUSH 한다.
								xmlHandler.evtDisconnectV2(makeDisconnectXmlVO(event , employeeVO , callID) , callID);
							}
							
						}
						
					}
					
					isDisconnect = true;
				}
				break;
				
			case CALLSTATE.PICKUP:		// 당겨받기 콜 종료
				if(dn.equals(event.getRedirectDn())){
					m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, callID, "callDisconnectEvt", ">>>>>>>>>> PICKUP >>>>>>>>>>>>>> " + event.toString());
					
					disconType = DISCONNECTTYPE.PICKUP_DISCONNECT;
					
					/* 전화기 현재 상태 업데이트 */
					CallStateMgr.getInstance().addDeviceState(deviceDN , CALLSTATE.IDLE);
					CallStateMgr.getInstance().addDeviceState(event.getDn() , CALLSTATE.IDLE);
					
					List employeeList = employees.getEmployeeListByExtension(deviceDN, callID);
					if(employeeList != null && employeeList.size() > 0){
						
						for (int i = 0; i < employeeList.size(); i++) {
							
							EmployeeVO employeeVO = (EmployeeVO) employeeList.get(i);
							if(employeeVO != null){
								
								if(checkVaildPush(employeeVO,callID) != RESULT.RTN_SUCCESS) {
									return RESULT.ERROR;
								}
								
								xmlHandler.evtDisconnect(makeDisconnectXmlVO(event , employeeVO, callID) , callID);
								// 당겨받기시 팝업되어있던 화면이 사라지지 않아 Disconnect push 한번 더 날린다.
								Thread.sleep(500);
								xmlHandler.evtDisconnectV2(makeDisconnectXmlVO(event , employeeVO, callID) , callID);
							}
							
						}
						
					}
					
					isDisconnect = true;
				}
				break;
				
			default:
					
				if(metaCode == CALLSTATE.META_CALL_ENDING && !Utils.isNumber(redirectDn)){
					m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, callID, "callDisconnectEvt", ">>>>>>>>>> NORMAL >>>>>>>>>>>>>> " + event.toString());

					disconType = DISCONNECTTYPE.NORMAL_DISCONNECT;
					
					/* 전화기 현재 상태 업데이트 */
					CallStateMgr.getInstance().addDeviceState(calledDn , CALLSTATE.IDLE);
					CallStateMgr.getInstance().addDeviceState(event.getDn() , CALLSTATE.IDLE);
					
					
					List employeeList = employees.getEmployeeListByExtension(calledDn, callID);
					if(employeeList != null && employeeList.size() > 0){
						
						for (int i = 0; i < employeeList.size(); i++) {
							
							EmployeeVO emp = (EmployeeVO) employeeList.get(i);
							if(emp != null) {
								
								if(checkVaildPush(emp,callID) != RESULT.RTN_SUCCESS) {
									return RESULT.ERROR;
								}
								xmlHandler.evtDisconnect(makeDisconnectXmlVO(event , emp, callID) , callID);
								// XML 팝업 화면이 닫히지 않아 Disconnect XML 을 한번 더 PUSH 한다.
								Thread.sleep(500);
								xmlHandler.evtDisconnectV2(makeDisconnectXmlVO(event , emp, callID) , callID);
							} else {
								EmployeeVO tempEmp = employees.getEmployeeByExtension(dn , callID);
								if(tempEmp != null) {
									
									if(checkVaildPush(tempEmp,callID) != RESULT.RTN_SUCCESS) {
										return RESULT.ERROR;
									}
									xmlHandler.evtDisconnect(makeDisconnectXmlVO(event , tempEmp, callID) , callID);
									// XML 팝업 화면이 닫히지 않아 Disconnect XML 을 한번 더 PUSH 한다.
									Thread.sleep(500);
									xmlHandler.evtDisconnectV2(makeDisconnectXmlVO(event , tempEmp, callID) , callID);
								}
							}
							
						}
						
					}
				
					isDisconnect = true;
				} else if(metaCode == CALLSTATE.META_CALL_REMOVING_PARTY && !Utils.isNumber(calledDn)){	// 전화회의 종료
					disconType = DISCONNECTTYPE.CONFERENCE_DISCONNECT;
					
					m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, callID, "callDisconnectEvt", ">>>>>>>>>> CONFERENCE >>>>>>>>>>>>>> " + event.toString());
					
					/* 전화기 현재 상태 업데이트 */
					CallStateMgr.getInstance().addDeviceState(dn , CALLSTATE.IDLE);
					CallStateMgr.getInstance().addDeviceState(event.getDn() , CALLSTATE.IDLE);
					
					EmployeeVO emp = employees.getEmployeeByExtension(dn , callID);
//					DeviceVO dev = DeviceMgr.getInstance().getDevice(dn);
					if(emp != null){
						
						if(checkVaildPush(emp,callID) != RESULT.RTN_SUCCESS) {
							return RESULT.ERROR;
						}
						
						xmlHandler.evtDisconnect(makeDisconnectXmlVO(event , emp,callID) , callID);
						// XML 팝업 화면이 닫히지 않아 Disconnect XML 을 한번 더 PUSH 한다.
						Thread.sleep(500);
						xmlHandler.evtDisconnectV2(makeDisconnectXmlVO(event , emp,callID) , callID);
					}
					isDisconnect = true;
				} else if(metaCode == CALLSTATE.META_CALL_ENDING && Utils.isNumber(calledDn)){	// 전화회의 최종 통화 종료
					
					disconType = DISCONNECTTYPE.CONFERENCEFINAL_DISCONNECT;
					
					m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, callID, "callDisconnectEvt", ">>>>>>>>>> CONFERENCE FINAL >>>>>>>>>>>>>> " + calledDn);
					
					/* 전화기 현재 상태 업데이트 */
					CallStateMgr.getInstance().addDeviceState(calledDn , CALLSTATE.IDLE);
					CallStateMgr.getInstance().addDeviceState(callingDn , CALLSTATE.IDLE);
					
					EmployeeVO emp = employees.getEmployeeByExtension(calledDn , callID);
//					DeviceVO dev = DeviceMgr.getInstance().getDevice(calledDn);
					if(emp != null){
						
						if(checkVaildPush(emp,callID) != RESULT.RTN_SUCCESS) {
							return RESULT.ERROR;
						}
						
						xmlHandler.evtDisconnect(makeDisconnectXmlVO(event , emp,callID) , callID);
						// XML 팝업 화면이 닫히지 않아 Disconnect XML 을 한번 더 PUSH 한다.
						Thread.sleep(500);
						xmlHandler.evtDisconnectV2(makeDisconnectXmlVO(event , emp,callID) , callID);
					}
					
					m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, callID, "callDisconnectEvt", ">>>>>>>>>> CONFERENCE FINAL >>>>>>>>>>>>>> " + callingDn);
					emp = employees.getEmployeeByExtension(callingDn , callID);
//					dev = DeviceMgr.getInstance().getDevice(callingDn);
					if(emp != null) {
						
						if(checkVaildPush(emp,callID) != RESULT.RTN_SUCCESS) {
							return RESULT.ERROR;
						}
						
						xmlHandler.evtDisconnect(makeDisconnectXmlVO(event , emp, callID) , callID);
						// XML 팝업 화면이 닫히지 않아 Disconnect XML 을 한번 더 PUSH 한다.
						Thread.sleep(500);
						xmlHandler.evtDisconnectV2(makeDisconnectXmlVO(event , emp, callID) , callID);
					}
					isDisconnect = true;
				} 
				
				break;
			}
			
		} 
		
		
		/*
		if(isDisconnect) {	// 통화이력저장 ( 전화기 XML 서비스 )
//			System.out.println("#### Call History Data #### -> " + event.get_GCallID());
//			m_Log.standLog(event.get_GCallID(), "callDisconnectEvt", "######## Disconnect ######### callID["+event.get_GCallID()+"]callingDN["+callingDn+"]calledDN["+calledDn+"]");
			
			callMgr = CallStateMgr.getInstance();
			Integer calledState 	= callMgr.getDeviceState(calledDn);
			Integer callingState 	= callMgr.getDeviceState(callingDn);
			
			if(calledState != null && callingState != null){
//				System.out.println("######## Disconnect ######### calledDn["+calledDn+"]상태["+changeState(calledState)+"]callingDn["+callingDn+"]상태["+changeState(callingState)+"]");
				
				switch(disconType){
				
				case DISCONNECTTYPE.NORMAL_DISCONNECT:
					
					if(callMgr.getDeviceState(calledDn) == CALLSTATE.ALERTING_ING) {
						// DISCONNECT 가 발생했는데 전화기 상태가 ALERTING 이라면, 부재중 전화 종료 (Ex. 호전환 돌려주기 하다가 취소한 경우)
//						dataBase.insertUACall(event.get_GCallID(),callingDn, calledDn);			// 부재중전화 DB INSERT
						callMgr.addDeviceState(calledDn, CALLSTATE.IDLE);	// 전화기 상태 IDLE 로 변경
					} else {
//						dataBase.insertCallHistory(callingDn, calledDn);			// NORMAL CALL 수/발신 DB INSERT
						callMgr.addDeviceState(callingDn, CALLSTATE.IDLE);	// 전화기 상태 IDLE 로 변경
						callMgr.addDeviceState(calledDn, CALLSTATE.IDLE);	// 전화기 상태 IDLE 로 변경
					}
					
					break;
				
				case DISCONNECTTYPE.TRANSFER_DISCONNECT:
					callMgr.addDeviceState(callingDn, CALLSTATE.IDLE);
					break;
					
				case DISCONNECTTYPE.CONFERENCE_DISCONNECT:
					callMgr.addDeviceState(dn, CALLSTATE.IDLE);
					break;
					
				case DISCONNECTTYPE.CONFERENCEFINAL_DISCONNECT:
					callMgr.addDeviceState(callingDn, CALLSTATE.IDLE);	// 전화기 상태 IDLE 로 변경
					callMgr.addDeviceState(calledDn, CALLSTATE.IDLE);	// 전화기 상태 IDLE 로 변경
					break;
					
				case DISCONNECTTYPE.PICKUP_DISCONNECT:
//					dataBase.insertUACall(event.get_GCallID(),callingDn, dn);			// 부재중전화 DB INSERT
					callMgr.addDeviceState(dn, CALLSTATE.IDLE);	// 전화기 상태 IDLE 로 변경
					break;
				}
			}
		}
		*/
		return RESULT.RTN_SUCCESS;
	}
	
	
	private XmlVO makeDisconnectXmlVO(TermConnEvt event , EmployeeVO employee, String callID)  throws Exception{
		// TODO Auto-generated method stub
		
		xmlVO = new XmlVO();
		
		xmlVO.setDn(event.getDevice()).setCallid(event.getCallID()).setAlertingdn(event.getCalledDn())
		.setCallingDn(event.getCallingDn()).setTargetdn(event.getDn()).setTerminal(event.getTerminal())
		.setTargetIP(employee.getDevice_ipaddr()).setTargetModel(employee.getDevice_type()).setCalledDn(event.getCalledDn()).setCallidByString(event.getCallID().getGCallID())
//		.setCmUser(employee.getCmUser()).setCmPassword(employee.getCmPass());
		.setCmUser(employee.getCm_user()).setCmPassword(employee.getCm_pwd());
		m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, callID, "makeDisconnectXmlVO", xmlVO.toString());
		
		return xmlVO;
	}


	private XmlVO makeEstablishXmlVO(TermConnEvt event , EmployeeVO employee, String callID)  throws Exception {
		// TODO Auto-generated method stub
		xmlVO = new XmlVO();
		
		xmlVO.setDn(event.getDevice()).setCallid(event.getCallID()).setAlertingdn(event.getCalledDn())
		.setCallingDn(event.getCallingDn()).setTargetdn(event.getDn()).setTerminal(event.getTerminal())
		.setTargetIP(employee.getDevice_ipaddr()).setTargetModel(employee.getDevice_type()).setCalledDn(event.getCalledDn()).setCallidByString(event.getCallID().getGCallID())
		.setCmUser(employee.getCm_user()).setCmPassword(employee.getCm_pwd());
		m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, callID, "makeEstablishXmlVO", xmlVO.toString());
		
		return xmlVO;
	}
	
	private XmlVO makeAlertingXmlVO(TermConnEvt event, EmployeeVO employee , String callID)  throws Exception{
		// TODO Auto-generated method stub
		xmlVO = new XmlVO();
		
		xmlVO.setDn(event.getDevice()).setCallid(event.getCallID()).setAlertingdn(event.getCalledDn())
		.setCallingDn(event.getCallingDn()).setTargetdn(event.getDn()).setTerminal(event.getTerminal())
		.setTargetIP(employee.getDevice_ipaddr()).setTargetModel(employee.getDevice_type()).setCalledDn(event.getCalledDn()).setCallidByString(event.getCallID().getGCallID())
		.setCmUser(employee.getCm_user()).setCmPassword(employee.getCm_pwd());
		m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, callID, "makeAlertingXmlVO", xmlVO.toString());
		
		return xmlVO;
	}
	
	private XmlVO makeAlertingXmlVO(TermConnEvt event, EmployeeVO employee , EmployeeVO callingEmployee ,String callID)  throws Exception{
		// TODO Auto-generated method stub
		xmlVO = new XmlVO();
		
		xmlVO.setDn(event.getDevice()).setCallid(event.getCallID()).setAlertingdn(event.getCalledDn())
		.setCallingDn(event.getCallingDn()).setTargetdn(event.getDn()).setTerminal(event.getTerminal())
		.setTargetIP(employee.getDevice_ipaddr()).setTargetModel(employee.getDevice_type()).setCalledDn(event.getCalledDn()).setCallidByString(event.getCallID().getGCallID())
		.setCmUser(employee.getCm_user()).setCmPassword(employee.getCm_pwd());
		m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, callID, "makeAlertingXmlVO", xmlVO.toString());
		
		return xmlVO;
	}
	

	private String changeState(int state){
		String str = "";
		switch (state) {
		case CALLSTATE.ALERTING_ING:
			str = "ALERTING";
			break;
		case CALLSTATE.ESTABLISHED_ING:
			str = "ESTABLISHED";
			break;
		default:
			break;
		}
		return str;
	}
	
	
		
}

