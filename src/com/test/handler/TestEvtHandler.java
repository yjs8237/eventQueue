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
/*
		// Target Device 정보
		EmployeeVO emp = Employees.getInstance().getEmployee(vo.getCalledDN() , threadID);
//		DeviceVO dev = DeviceMgr.getInstance().getDevice(vo.getCalledDN());
		if (emp != null) {
			xmlHandler.evtRing(makeAlertingXmlVO(vo, threadID), threadID);
		}
*/		
		isRinging = true;	
		
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

