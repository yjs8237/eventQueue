package com.isi.thread;

import java.util.concurrent.Callable;

import javax.telephony.callcontrol.events.CallCtlConnAlertingEv;
import javax.telephony.callcontrol.events.CallCtlConnEstablishedEv;
import javax.telephony.callcontrol.events.CallCtlConnNetworkAlertingEv;

import com.isi.constans.CALLSTATE;
import com.isi.constans.LOGLEVEL;
import com.isi.constans.LOGTYPE;
import com.isi.constans.RESULT;
import com.isi.constans.STATUS;
import com.isi.data.CallStateMgr;
import com.isi.db.JDatabase;
import com.isi.event.*;
import com.isi.exception.ExceptionUtil;
import com.isi.file.ILog;
import com.isi.file.LogMgr;
import com.isi.file.LogWriter;
import com.isi.handler.CallEvtHandler;
import com.isi.process.IQueue;
import com.isi.utils.CodeToString;
import com.isi.vo.CallStateVO;
import com.isi.vo.DeviceVO;
/**
*
* @author greatyun
*/
public class XMLSvcCallable implements Callable<Integer> {
	
	private IQueue 			queue;
	private CallEvtHandler 	evtHandler;
	private boolean 		stopThread		= false;
	private LogMgr			logwrite;
	private CallStateMgr	callMgr;
	private String			threadID;
	
	public XMLSvcCallable(IQueue queue, JDatabase dataBase, String threadID) {
		this.queue = queue;
		evtHandler = new CallEvtHandler(dataBase,threadID);
		logwrite = LogMgr.getInstance();
//		this.dataBase = dataBase;
		this.threadID = threadID;
		
	}
	
	@Override
	public Integer call()  {
		// TODO Auto-generated method stub
		
		while(!stopThread) {
			String callID = "";
			try{
				
				IEvt evt = (IEvt) queue.get();
//				logwrite.standLog("", "call()", evt.toString());
				callID = String.valueOf(System.currentTimeMillis()) + "-" + String.valueOf(Thread.currentThread().getName()) + String.valueOf(Thread.currentThread().getId())
				 + "_" + evt.getDevice();
				if(evt != null) {
					// 이벤트에 따라서 처리함.       
					if (evt instanceof TermConnEvt) {
						evtTermConnEvt((TermConnEvt)evt , callID);
						continue;
					}  else if (evt instanceof TermEvt) {
						evtTermEvt((TermEvt)evt , callID);
						continue;
					}  else if ( evt instanceof ConnEvt) {                        
						// 이벤트에 따른 추가정보 저장                 
						evtConnEvt((ConnEvt)evt , callID);
						continue;
						// Device의 상태를 변경한다.    
					}
					
				}
			}catch(Exception e) {
				e.printStackTrace(ExceptionUtil.getPrintWriter());
				logwrite.exceptionLog(callID, "call()", ExceptionUtil.getStringWriter().toString());
			}
			
		}
		
		return RESULT.RTN_SUCCESS;
	}

	private void evtConnEvt(ConnEvt evt , String callID)  throws Exception {
		// TODO Auto-generated method stub
//		logwrite.testLog("#### evtConnEvt #### " + evt.toString());
		
		String callingDN = "";
		String calledDN = "";
//		String callID = "";
		String deviceDN = "";
		String DN = "";
		boolean isNetworkCall = false;
		
		
		/*
		 * 통화이력 저장을 위한 이벤트 Catch
		 * Alerting 과 Establish는 evtConnEvt 이벤트에서 필터한다.
		 * 외부 수/발신 콜의 경우 evtTermConnEvt 이벤트에는 외부 (핸드폰 등등) 번호의 정보가 이벤트에 없기때문에..
		 * Disconnect (통화종료) Catch 는 evtTermConnEvt 에서 처리한다.
		 */
		
		switch(evt.getEventID()){
		
		case CALLSTATE.CallCtlConnAlertingEv:
			callingDN = evt.getCallingDn();
			calledDN =  evt.getCalledDn();
			callID = evt.get_GCallID();
//			System.out.println("######## evtConnEvt Alerting ######### callID["+callID+"]callingDN["+callingDN+"]calledDN["+calledDN+"]");
			break;
			
		case CALLSTATE.CallCtlConnNetworkAlertingEv:
			callingDN = evt.getCallingDn();
			calledDN = evt.getCalledDn();
			callID = evt.get_GCallID();
			/*
			 *  외부 수/발신시 9 로 시작하는 번호는 9를 제외한다..
			 *  evtTermConnEvt 에는 9를 제외한 번호가 들어오기 때문에.. 이벤트마다 9가 있고, 없고,, 멋대로..
			 */
			
			if(calledDN.startsWith("9")){calledDN = calledDN.substring(1);}	
			CallStateMgr.getInstance().addDeviceState(calledDN , CALLSTATE.ALERTING_ING);
//			dataBase.insertCallingHistory(callID,callingDN, calledDN);
			break;
			
		case CALLSTATE.CallCtlConnEstablishedEv:
			
//			logwrite.standLog("", "evtConnEvt()", "CallCtlConnEstablishedEv -> " + evt.toString());
			callingDN = evt.getCallingDn();
			calledDN =  evt.getCalledDn();
			callID = evt.get_GCallID();
			deviceDN = evt.getDevice();
			DN = evt.getDn();
			
			/*
			 *  외부 수/발신시 9 로 시작하는 번호는 9를 제외한다..
			 *  evtTermConnEvt 에는 9를 제외한 번호가 들어오기 때문에.. 이벤트마다 9가 있고, 없고,, 멋대로..
			 */
			if(calledDN.length() > 7 || callingDN.length() > 7){
				isNetworkCall = true;
				if(calledDN.startsWith("9")){
					calledDN = calledDN.substring(1);
				}
				if(callingDN.startsWith("9")){
					callingDN = callingDN.substring(1);
				}
			}
			
			// 외부번호와 연결된 콜의 경우에만 통화이력을 남긴다.. evtTermConnEvt 이벤트도 Establish 가 오기때문에.. Insert 중복을 피하기 위해
			// evtTermConnEvt 에는 외부번호의 이벤트는 오지 않는다.
			
			if(isNetworkCall && evt.getCtlCause() == CALLSTATE.NORMAL){
				callMgr = CallStateMgr.getInstance(); 
				Integer calledDNStatus = callMgr.getDeviceState(calledDN);
				if(calledDNStatus != null) {
					if(calledDNStatus == CALLSTATE.ALERTING_ING) {
						callMgr.addDeviceState(callingDN, CALLSTATE.ESTABLISHED_ING);
						callMgr.addDeviceState(calledDN, CALLSTATE.ESTABLISHED_ING);
//						dataBase.insertCalledHistory(callID,callingDN, calledDN);
					}
				}
			} else if(isNetworkCall && evt.getCtlCause() == CALLSTATE.TRANSFER){
				if(!DN.equals(deviceDN)){
					callMgr = CallStateMgr.getInstance(); 
					callMgr.addDeviceState(DN, CALLSTATE.ESTABLISHED_ING);
					callMgr.addDeviceState(deviceDN, CALLSTATE.ESTABLISHED_ING);
//					dataBase.insertCalledHistory(callID,DN, deviceDN);
				}
			}
			
			break;
			
		case CALLSTATE.CallCtl_Conn_DisconnectedEv:
			
			break;
		}
		
	}

	private void evtTermConnEvt(TermConnEvt evt, String callID) throws Exception {
		// TODO Auto-generated method stub
		
		switch(evt.getEventID()) {
			// Ring 이 울릴경우
			case IEvt.CallCtl_TermConn_RingingEv:	
				evtHandler.callRingEvt(evt, callID);
				break;
			// 
			case IEvt.CallCtl_TermConn_BridgedEv:
				break;
			
			// Established 전화 받았을 경우
			case IEvt.CallCtl_TermConn_TalkingEv:
				// 삼일회계법인은 Established 이벤트에 팝업창을 닫는다..
//				evtHandler.callEstablishedEvt(evt , callID);
				evtHandler.callEstablishedEvtForPwc(evt , callID);
				break;

			// Disconnect 전화 끊었을 경우
			case IEvt.CallCtl_TermConn_DroppedEv:
				evtHandler.callDisconnectEvt(evt, callID);
				break;
		}
		
	}

	private void evtCallEvt(CallEvt evt , String callID)  throws Exception {
		// TODO Auto-generated method stub
	}
	
	private void evtTermEvt(TermEvt evt , String callID)  throws Exception{
		
	}
	
	
	
	

}
