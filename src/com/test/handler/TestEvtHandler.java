package com.test.handler;

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
import com.test.vo.TestCallVO;

/**
*
* @author greatyun
*/
public class TestEvtHandler {
	
	private TestXMLHandler 		xmlHandler;
	private XmlVO 			xmlVO;
	private JDatabase		dataBase;
	private CallStateMgr	callMgr;
	
	public TestEvtHandler(JDatabase dataBase){
		this.dataBase = dataBase;
		xmlHandler = new TestXMLHandler(dataBase);
	}
	
	 
	public int callRingEvt (TestCallVO vo, String threadID)  throws Exception{	// Ring 이 울릴 경우
		
		boolean isRinging	=	false;
		
		
				
		// 내선 콜 상태 정보 SET
		// CallStateMgr.getInstance().addDeviceState(event.getCallingDn() ,
		// CALLSTATE.ALERTING_ING);
		CallStateMgr.getInstance().addDeviceState(vo.getCalledDN(), CALLSTATE.ALERTING_ING);

		dataBase.insertCallingHistory("", vo.getCallingDN(), vo.getCalledDN());

		// Target Device 정보
		EmployeeVO emp = Employees.getInstance().getEmployee(vo.getCalledDN() , threadID);
//		DeviceVO dev = DeviceMgr.getInstance().getDevice(vo.getCalledDN());
		if (emp != null) {
			xmlHandler.evtRing(makeAlertingXmlVO(vo, threadID), threadID);
		}
		
		isRinging = true;	
		
		return RESULT.RTN_SUCCESS;
	}
	

	public int callEstablishedEvt (Evt evt)  throws Exception{
		
		TermConnEvt event = (TermConnEvt) evt;
		
		String callingDN 	= event.getCallingDn(); 	// 전화를 건 사람의 DN
		String calledDN		= event.getCalledDn();		// 전화를 받은 사람의 DN
		String terminal 	= event.getTerminal();		// 이벤트 정보에 포함되어 있는 Termina(Mac address)
		String redirectDN 	= event.getRedirectDn();	// Redirect DN
		String DN			= event.getDn();			// DN
		String deviceDN		= event.getDevice();		// Device DN
		int ctlCause 		= event.getCtlCause();		// 콜 타입
		boolean isEastblish	= false;
		
		DeviceVO device = null;
		if (device == null){
			return RESULT.RTN_UNDEFINED_ERR;
		}
		
		switch (ctlCause) {
		

		case CALLSTATE.CONFERENCE:					// 전화회의 콜 
			int metaCode = event.getMetaCode();		// MetaCode
			if(metaCode == CALLSTATE.META_CALL_MERGING){
				
				if(redirectDN.equals(callingDN) && callingDN.equals(DN) && !DN.equals(deviceDN)){
					LogMgr.getInstance().write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, event.get_GCallID(), "callEstablishedEvt", ">>>>>>>>>>>>>>>>>>>>>>>> " + event.toString());
					DeviceVO dev = null;
					if(dev!=null){
						xmlHandler.evtEstablished(makeEstablishXmlVO(event , dev) , deviceDN);
					}
					isEastblish = true;
				} else if(!deviceDN.equals(DN) && deviceDN.equals(redirectDN)){
					LogMgr.getInstance().write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, event.get_GCallID(), "callEstablishedEvt", ">>>>>>>>>>>>>>>>>>>>>>>> " + event.toString());
					DeviceVO dev = null;
					if(dev != null){
						xmlHandler.evtEstablished(makeEstablishXmlVO(event , dev) , deviceDN);
					}
					isEastblish = true;
				}
				
			}
			break;
		
		case CALLSTATE.UNHOLD:	// 통화중 투콜 토글 시
//			if(!terminal.equals(device.getTerminal())){ 
//				if(event.getDevice().equals(event.getDn())){	// 이벤트 정보내에 Device 번호와 DN 번호가 같을 경우 push 처리
//					LogMgr.getInstance().write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, event.get_GCallID(), "callEstablishedEvt", ">>>>>>>>>>> UN HOLD >>>>>>>>>>>>> " + event.toString());
//					
//					callMgr = CallStateMgr.getInstance();
//					callMgr.addDeviceState(callingDN, CALLSTATE.ESTABLISHED_ING);
//					callMgr.addDeviceState(calledDN, CALLSTATE.ESTABLISHED_ING);
//					
//					DeviceVO dev = DeviceMgr.getInstance().getDevice(deviceDN);
//					if(dev != null) {
//						Thread.sleep(100);	// 토글 시 push 가 너무 빠르게 이루어지면 기본화면이 XML 화면을 뒤덮기 때문에.. sleep 0.1 초 살짝 줘볼까
//						xmlHandler.evtEstablished(makeEstablishXmlVO(event, dev) , callingDN);
//					}
////					isEastblish = true;	// 보류해제시에는 통화이력 정보가 남을 필요가 없다
//				}
//			}
			break;
			
		default:	//일반 콜
			// 이벤트정보에 포함된 terminal 정보와 Calling DN 으로 검색된 Device의 terminal 정보가 같으면
			// Establish 이벤트를 처리하지 않는다. (Establish 는 전화를 받는 사람이 전화를 받아야 발생한다)
//			if(!terminal.equals(device.getTerminal())){ 
//				if(event.getDevice().equals(event.getDn())){	// 이벤트 정보내에 Device 번호와 DN 번호가 같을 경우 push 처리
//					LogMgr.getInstance().write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, event.get_GCallID(), "callEstablishedEvt", ">>>>>>>>>>>>>>>>>>>>>>>> " + event.toString());
//					DeviceVO dev = DeviceMgr.getInstance().getDevice(deviceDN);
//					if(dev != null){
//						xmlHandler.evtEstablished(makeEstablishXmlVO(event, dev) , callingDN);
//					}
//					isEastblish = true;
//				}
//			}
			break;
		}
		
		
		if(isEastblish){	// 통화이력 관리 정보
			
			if(ctlCause != CALLSTATE.CONFERENCE){ // 전화회의 경우, 통화이력 정보를 새로 추가할 필요가 없다.
				
//				System.out.println("############ Established ############ calling["+callingDN+"] called["+calledDN+"]");
				callMgr = CallStateMgr.getInstance(); 
				callMgr.addDeviceState(callingDN, CALLSTATE.ESTABLISHED_ING);
				callMgr.addDeviceState(calledDN, CALLSTATE.ESTABLISHED_ING);
				
				dataBase.insertCalledHistory("",callingDN, calledDN);	// XML 통화 이력을 위한 DB INSERT
			}
		}
		
		return RESULT.RTN_SUCCESS;
		
	}

	public int callDisconnectEvt(TestCallVO vo)  throws Exception {

		String dn 			= vo.getCalledDN();
		String deviceDN 	= vo.getCalledDN();
		String calledDn 	= vo.getCalledDN();
		String callingDn 	= vo.getCallingDN();
		
		int disconType		=	-1;
		boolean isDisconnect	= false;
		
//		LogMgr.getInstance().write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, event.get_GCallID(), "callDisconnectEvt", "+++++++++++++++++++++++++++ " + event.toString());
		
//		DeviceVO dev = DeviceMgr.getInstance().getDevice(calledDn);
//		if(dev != null){
//			xmlHandler.evtDisconnect(makeDisconnectXmlVO(vo));
//		}
		
		xmlHandler.evtDisconnect(makeDisconnectXmlVO(vo));
		
		callMgr = CallStateMgr.getInstance();
		Integer calledState 	= callMgr.getDeviceState(calledDn);
		Integer callingState 	= callMgr.getDeviceState(callingDn);
		
		if(callMgr.getDeviceState(calledDn) == CALLSTATE.ALERTING_ING) {
			// DISCONNECT 가 발생했는데 전화기 상태가 ALERTING 이라면, 부재중 전화 종료 (Ex. 호전환 돌려주기 하다가 취소한 경우)
			dataBase.insertUACall("",callingDn, calledDn);			// 부재중전화 DB INSERT
			callMgr.addDeviceState(calledDn, CALLSTATE.IDLE);	// 전화기 상태 IDLE 로 변경
		} else {
//			dataBase.insertCallHistory(callingDn, calledDn);			// NORMAL CALL 수/발신 DB INSERT
			callMgr.addDeviceState(callingDn, CALLSTATE.IDLE);	// 전화기 상태 IDLE 로 변경
			callMgr.addDeviceState(calledDn, CALLSTATE.IDLE);	// 전화기 상태 IDLE 로 변경
		}
		
		
		return RESULT.RTN_SUCCESS;
	}
	
	
	private XmlVO makeDisconnectXmlVO(TestCallVO vo)  throws Exception{
		// TODO Auto-generated method stub
		
		xmlVO = new XmlVO();
		
		xmlVO.setDn(vo.getCalledDN()).setAlertingdn(vo.getCalledDN())
		.setCallingDn(vo.getCallingDN()).setTargetdn(vo.getCalledDN())
		.setTargetIP(vo.getTargetIP()).setTargetModel(vo.getTargetModel()).setCalledDn(vo.getCalledDN());
		
		LogMgr.getInstance().write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, "", "makeDisconnectXmlVO", xmlVO.toString());
		
		return xmlVO;
	}


	private XmlVO makeEstablishXmlVO(TermConnEvt event , DeviceVO dev)  throws Exception {
		// TODO Auto-generated method stub
		xmlVO = new XmlVO();
		
//		xmlVO.setDn(event.getDevice()).setCallid(event.getCallID()).setAlertingdn(event.getCalledDn())
//		.setCallingDn(event.getCallingDn()).setTargetdn(event.getDn()).setTerminal(event.getTerminal())
//		.setTargetIP(dev.getIp()).setTargetModel(dev.getModel()).setCalledDn(event.getCalledDn()).setCallidByString(event.getCallID().getGCallID());
//		
//		LogMgr.getInstance().write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, event.getCallID().getCCallID(), "makeEstablishXmlVO", xmlVO.toString());
		
		return xmlVO;
	}
	
	private XmlVO makeAlertingXmlVO(TestCallVO vo, String threadID)  throws Exception{
		// TODO Auto-generated method stub
		xmlVO = new XmlVO();
		
		xmlVO.setDn(vo.getCalledDN()).setAlertingdn(vo.getCalledDN())
		.setCallingDn(vo.getCallingDN()).setTargetdn(vo.getCalledDN())
		.setTargetIP(vo.getTargetIP()).setTargetModel(vo.getTargetModel()).setCalledDn(vo.getCalledDN());
		
		LogMgr.getInstance().write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, threadID, "makeAlertingXmlVO", xmlVO.toString());
		
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

