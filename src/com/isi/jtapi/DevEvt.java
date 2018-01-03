package com.isi.jtapi;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author skan
 */
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.telephony.Address;
import javax.telephony.Call;
import javax.telephony.CallObserver;
import javax.telephony.Terminal;
import javax.telephony.callcontrol.CallControlAddressObserver;
import javax.telephony.callcontrol.CallControlCallObserver;
import javax.telephony.callcontrol.CallControlConnection;
import javax.telephony.callcontrol.CallControlTerminalConnection;
import javax.telephony.callcontrol.CallControlTerminalObserver;
import javax.telephony.callcontrol.events.CallCtlAddrEv;
import javax.telephony.callcontrol.events.CallCtlCallEv;
import javax.telephony.callcontrol.events.CallCtlConnEv;
import javax.telephony.callcontrol.events.CallCtlTermConnEv;
import javax.telephony.callcontrol.events.CallCtlTermEv;
import javax.telephony.events.*;
import javax.telephony.media.MediaCallObserver;
import javax.telephony.media.events.MediaTermConnDtmfEv;

import com.cisco.jtapi.extensions.CiscoCall;
import com.cisco.jtapi.extensions.CiscoCallEv;
import com.cisco.jtapi.extensions.CiscoConferenceEndEv;
import com.cisco.jtapi.extensions.CiscoConferenceStartEv;
import com.cisco.jtapi.extensions.CiscoConnection;
import com.cisco.jtapi.extensions.CiscoConsultCallActiveEv;
import com.cisco.jtapi.extensions.CiscoSynchronousObserver;
import com.cisco.jtapi.extensions.CiscoTermDeviceStateActiveEv;
import com.cisco.jtapi.extensions.CiscoTermDeviceStateAlertingEv;
import com.cisco.jtapi.extensions.CiscoTermDeviceStateHeldEv;
import com.cisco.jtapi.extensions.CiscoTermDeviceStateIdleEv;
import com.cisco.jtapi.extensions.CiscoTermEv;
import com.cisco.jtapi.extensions.CiscoTermInServiceEv;
import com.cisco.jtapi.extensions.CiscoTermOutOfServiceEv;
import com.cisco.jtapi.extensions.CiscoTerminal;
import com.cisco.jtapi.extensions.CiscoTransferEndEv;
import com.cisco.jtapi.extensions.CiscoTransferStartEv;
import com.isi.constans.LOGLEVEL;
import com.isi.constans.LOGTYPE;
import com.isi.constans.SVCTYPE;
import com.isi.data.*;
import com.isi.event.*;
import com.isi.file.*;
import com.isi.utils.CheckFunc;
import com.isi.utils.CodeToString;

public class DevEvt  implements CallControlAddressObserver,
        CallControlTerminalObserver, CallControlCallObserver,
        MediaCallObserver /*, CiscoSynchronousObserver */{

    private GLogWriter m_Log = null;
    private String m_Dn = "";
    private IJTAPI m_Jtapi = null;
    private int m_State = CiscoTerminal.DEVICESTATE_UNKNOWN;
    private PrintWriter pw;
    private StringWriter sw;
    
    public DevEvt(String aDn, IJTAPI aJTAPI) {
        setDn(aDn);
        m_Jtapi = aJTAPI;
        m_Log = new GLogWriter();
        sw = new StringWriter();
        pw = new PrintWriter(sw);
    }

    public void setDn(String aDn) {
        m_Dn = aDn;
    }

    public String getDn() {
        return m_Dn;
    }

    public String getDisDn() {
        return "[" + m_Dn + "]";
    }

    public void ChangeState(int aState) {
        m_State = aState;
    }

    public int getState() {
        return m_State;
    }

    public final void callChangedEvent(CallEv[] events) {
        try {
            for (int i = 0; i < events.length; i++) {

            	if (events[i] instanceof CallCtlTermConnEv) {
                    EvtCallCtlTermConnEv((CallCtlTermConnEv) events[i]);
                } else if (events[i] instanceof CallCtlConnEv) {
                    EvtCallCtlConnEv((CallCtlConnEv) events[i]);
                } else if (events[i] instanceof CiscoTransferStartEv
                        || events[i] instanceof CiscoTransferEndEv
                        || events[i] instanceof CiscoConferenceStartEv
                        || events[i] instanceof CiscoConferenceEndEv) {
                    EvtCiscoTransConfEv((CiscoCallEv) events[i]);
                }
                /*else if (events[i] instanceof CallCtlTermConnEv) {
                    EvtCallCtlTermConnEv((CallCtlTermConnEv) events[i]);
                } else if (events[i] instanceof CiscoTransferStartEv
                        || events[i] instanceof CiscoTransferEndEv
                        || events[i] instanceof CiscoConferenceStartEv
                        || events[i] instanceof CiscoConferenceEndEv) {
                    EvtCiscoTransConfEv((CiscoCallEv) events[i]);
                } else if (events[i] instanceof MediaTermConnDtmfEv) {
                    EvtDTMFEv((MediaTermConnDtmfEv) events[i]);
                } else if (events[i] instanceof CallCtlCallEv) {
                    EvtCallCtlCallEv((CallCtlCallEv)events[i]);
                } else if (events[i] instanceof CallActiveEv
                        || events[i] instanceof CallInvalidEv
                        || events[i] instanceof CallObservationEndedEv
                        || events[i] instanceof CiscoConsultCallActiveEv) {
                    EvtCallEv((CallEv) events[i]);
                } else if (events[i] instanceof ConnEv) {
                    EvtConnEv((ConnEv)events[i]);
                } else if (events[i] instanceof TermConnEv) {
                    EvtTermConnEv((TermConnEv)events[i]);
                } else {
                	m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, SVCTYPE.JTAPI, getCallId(events[i]), "callChangedEvent", "Unchecked Event[Call Change] [EventID] " + events[i]);
                }
                */
            }
        } catch (Exception e) {
        	e.printStackTrace(pw);
        	m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.ERR_LOG, SVCTYPE.JTAPI, "", "callChangedEvent", sw.toString());
        }
    }

    public final void addressChangedEvent(AddrEv[] events) {

        try {
            for (int i = 0; i < events.length; i++) {

                if (events[i] instanceof CallCtlAddrEv) {
                    //EvtCallCtlAddrEv((CallCtlAddrEv)events[i]);
                } else if (events[i] instanceof AddrEv) {
                    EvtAddrEv((AddrEv) events[i]);
                } else {
                	m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, SVCTYPE.JTAPI, "", "addressChangedEvent", "Unchecked Event[Address Change] [EventID] " + events[i]);
                }
            }
        } catch (Exception e) {
        	e.printStackTrace(pw);
        	m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.ERR_LOG, SVCTYPE.JTAPI, "", "addressChangedEvent", sw.toString());
        }
    }
    
    
    // Terminal 상태 변경 이벤트 ? 
    public final void terminalChangedEvent(TermEv[] events) {

        try {
            for (int i = 0; i < events.length; i++) {

                if (events[i] instanceof CiscoTermEv) {
                    EvtCiscoTermEv((CiscoTermEv) events[i]);
                } else if (events[i] instanceof CallCtlTermEv) {
                    //			   EvtCallCtlTermEv((CallCtlTermEv)events[i]);
                } else if (events[i] instanceof TermEv) {
                    EvtTermEv((TermEv) events[i]);
                } else {
                	m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, SVCTYPE.JTAPI, "", "terminalChangedEvent", "Unchecked Event[Address Change] [EventID] " + events[i]);
                }
            }
        } catch (Exception e) {
        	e.printStackTrace(pw);
        	m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.ERR_LOG, SVCTYPE.JTAPI, "", "terminalChangedEvent", sw.toString());
        }
    }
    
    
    
    
    
    
    // DTMF Event
    private void EvtDTMFEv(MediaTermConnDtmfEv dtmfEv) {
    	m_Log.Jtapi(LOGTYPE.STAND_LOG, getCallId(dtmfEv) ,"EvtDTMFEv", "EvtDTMFEv");
//        DTMFEvt evt = null;
//
//        try {
//            // Ev
//            int id = dtmfEv.getID();
//            int cause = dtmfEv.getCause();
//            int metacode = dtmfEv.getMetaCode();
//
//            // CallEv
//            Call call = dtmfEv.getCall();
//            char digit = dtmfEv.getDtmfDigit();
//
//            evt = new DTMFEvt();
//
//            // IEvt
//            evt.setDevice(getDn());
//            evt.setEventID(id);
//            evt.setDn(getDn());
//            evt.setCause(cause);
//            evt.setMetaCode(metacode);
//            evt.setDigit(digit);
//
//            m_Log.info(getDisDn() + " " + evt.toMsg().toString());
//            m_Jtapi.ReceiveEvent(evt);
//
//            if (m_PackLog.isLoggable(JLog.FINEST)) {
//                StringBuffer msg = new StringBuffer();
//                
//                msg.append("[").append(CheckFunc.FixLenString(m_Dn, 10, " ", 1)).append("]"); //Device
//                msg.append(CheckFunc.FixLenString(CodeToString.EvtToString(id), 30, " ", 1)); //Event
//                msg.append(" Digit:").append(digit);
//                msg.append(" Cause:").append(cause);
//                msg.append(" Metacode:").append(metacode);
//                m_PackLog.finest(msg.toString());
//            }
//        } catch (Exception e) {
//            m_Log.server(getDn() + " EvtCallCtlCallEv Error ", e);
//            evt = null;
//        }
    }

    // CallEvent
    private void EvtCallEv(CallEv callEv) {
    	
    	m_Log.Jtapi(LOGTYPE.STAND_LOG, getCallId(callEv), "EvtCallEv", "EvtCallEv");
        CallEvt evt = null;
        CallID gcallid = null;
        try {
            // Ev
            int id = callEv.getID();
            int cause = callEv.getCause();
            int metacode = callEv.getMetaCode();
            int transid = 0;
            

            Call call = null;
            // Cisco Spec
            int callseq = 0;
            int system = 0;
            gcallid = CallID.getNull();
            // Data Convert
            int callstate = Call.INVALID;

            int consultid = 0;  // CiscoConsultCallActiveEv???留????
            int consultsystem = 0;
            CallID gconsultid = CallID.getNull();

            // Shotdown일 경우 CallID는 null로 들어옮			
            if (callEv.getCall() != null) {
                // CallEv
                call = callEv.getCall();

                // Data Convert
                callstate = call.getState();

                if (call instanceof CiscoCall) {
                    callseq = ((CiscoCall) call).getCallID().getGlobalCallID();
                    system = ((CiscoCall) call).getCallID().getCallManagerID();
                    gcallid = CallID.getInstance(m_Jtapi.getCMID(), system, callseq);
                }

                try {
                    // Consult일 경우 Hold된 호의ID 회수 UUI 정보 처리
                    if (CiscoConsultCallActiveEv.ID == id) {
                        CiscoConsultCallActiveEv consultevt = (CiscoConsultCallActiveEv) callEv;
                        consultid = ((CiscoCall) consultevt.getHeldTerminalConnection().getConnection().getCall()).getCallID().getGlobalCallID();
                        consultsystem = ((CiscoCall) consultevt.getHeldTerminalConnection().getConnection().getCall()).getCallID().getCallManagerID();
                        gconsultid = CallID.getInstance(m_Jtapi.getCMID(), consultsystem, consultid);
                    }
                } catch (Exception ex) {
                	ex.printStackTrace(pw);
                	m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.ERR_LOG, SVCTYPE.JTAPI,gcallid.getCCallID(),  "EvtCallEv", sw.toString());
                }
            }

            evt = new CallEvt();

            // IEvt
            evt.setDevice(getDn());

            evt.setEventID(id);
            evt.setDn(getDn());
            evt.setCause(cause);
            evt.setMetaCode(metacode);

            evt.setCallID(gcallid);
            evt.setCallState(callstate);
            evt.setConsultCallID(gconsultid);
            // 이벤트를 전송한다.
            evt.set_GCallID(getCallId(callEv));
            
            
            m_Jtapi.ReceiveEvent((IEvt) evt);

            	
                StringBuffer msg = new StringBuffer();
                msg.append("[").append(CheckFunc.FixLenString(m_Dn, 10, " ", 1)).append("]");				  // Device
                msg.append(CheckFunc.FixLenString(CodeToString.EvtToString(id), 30, " ", 1));   //Event
                msg.append(" CallID:").append(CheckFunc.FixLenString(gcallid.getGCallID(), 13, " ", 1));   // CallID
                msg.append(" Dn:").append(CheckFunc.FixLenString(m_Dn, 10, " ", 1));							  // Dn
                msg.append(" ConsultID:").append(CheckFunc.FixLenString(gconsultid.getGCallID(), 13, " ", 1));		// ConsutlCallID
                msg.append(" Cause:").append(cause);
                msg.append(" Metacode:").append(metacode);
                m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, SVCTYPE.JTAPI, evt.get_GCallID(), "EvtCallEv",  msg.toString());
        } catch (Exception e) {
        	e.printStackTrace(pw);
        	m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.ERR_LOG, SVCTYPE.JTAPI, evt.get_GCallID(), "EvtCallEv",  sw.toString());
            evt = null;
        }
    }

    private void EvtConnEv(ConnEv connEv) {
    	m_Log.Jtapi(LOGTYPE.STAND_LOG,getCallId(connEv), "EvtConnEv", "EvtConnEv");
    }

    private void EvtTermConnEv(TermConnEv termconnEv) {
    	m_Log.Jtapi(LOGTYPE.STAND_LOG,getCallId(termconnEv), "EvtTermConnEv", "EvtTermConnEv");
    }

    private void EvtTermEv(TermEv termEv) {

        try {
            // Ev
            int id = termEv.getID();
            int cause = termEv.getCause();
            int metacode = termEv.getMetaCode();
            Terminal term = termEv.getTerminal();
            
			StringBuffer msg = new StringBuffer();
			msg.append("[").append(CheckFunc.FixLenString(m_Dn, 10, " ", 1)).append("]"); // Device
			msg.append(CheckFunc.FixLenString(CodeToString.EvtToString(id), 30," ", 1)); // Event
			msg.append(" Cause:").append(cause);
			msg.append(" Metacode:").append(metacode);
			m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, SVCTYPE.JTAPI, "",
					"EvtTermEv", msg.toString());

        } catch (Exception e) {
        	e.printStackTrace(pw);
        	m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.ERR_LOG, SVCTYPE.JTAPI, "", "EvtTermEv",  sw.toString());
        }
    }

    private void EvtAddrEv(AddrEv addrEv) {

        try {
            // Ev
            int id = addrEv.getID();
            int cause = addrEv.getCause();
            int metacode = addrEv.getMetaCode();
            Address addr = addrEv.getAddress();
            
			StringBuffer msg = new StringBuffer();
			msg.append("[").append(CheckFunc.FixLenString(m_Dn, 10, " ", 1))
					.append("]"); // Device
			msg.append(CheckFunc.FixLenString(CodeToString.EvtToString(id), 30,
					" ", 1)); // Event
			msg.append(" Cause:").append(cause);
			msg.append(" Metacode:").append(metacode);
			m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, SVCTYPE.JTAPI, "",
					"EvtAddrEv", msg.toString());

        } catch (Exception e) {
        	e.printStackTrace(pw);
        	 m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.ERR_LOG, SVCTYPE.JTAPI, "",  "EvtAddrEv", sw.toString());
        }
    }

    // CallCtlEvent
    private void EvtCallCtlCallEv(CallCtlCallEv callEv) {
    	m_Log.Jtapi(LOGTYPE.STAND_LOG,getCallId(callEv), "EvtCallCtlCallEv", "EvtCallCtlCallEv");
    }

    /*
     * yjyoon 20130114 Event EvtCallCtlConnEv
     *  203 : CallCtl_Conn_AlertingEv
     *  205 : CallCtl_Conn_DisconnectedEv
     *  206 : CallCtl_Conn_EstablishedEv
     *  211 : CallCtl_Conn_OfferedEv
     *  208 : CallCtl_Conn_InitiatedEv
     *  204 : CallCtl_Conn_DialingEv
     */
    private void EvtCallCtlConnEv(CallCtlConnEv connEv) {
    	m_Log.Jtapi(LOGTYPE.STAND_LOG,getCallId(connEv), "EvtCallCtlConnEv", "EvtCallCtlConnEv");
    	
        ConnEvt evt = null;
        try {

            // Ev
            int id = connEv.getID();
            int cause = connEv.getCause();
            int metacode = connEv.getMetaCode();

            // CallEv
            Call call = connEv.getCall();
            // ConnEv
            CallControlConnection conn = (CallControlConnection) connEv.getConnection();
            // CallCtlEv
            int ctlcause = connEv.getCallControlCause();

            // CallCtlCallEv
            Address called = connEv.getCalledAddress();
            Address calling = connEv.getCallingAddress();
            Terminal term = connEv.getCallingTerminal();
            Address redirect = connEv.getLastRedirectedAddress();

            // Data Convert
            int callstate = call.getState();
            int connstate = conn.getCallControlState();

            Address addr = conn.getAddress();
            String strdn = addr.getName();
            String strcalled = "";
            String strcalling = "";
            String strterm = "";
            String strredirect = "";

            // Cisco Spec
            int callid = 0;
            int system = 0;
            int connid = 0;
            CallID gcallid = CallID.getNull();

            if (called != null) {
                strcalled = called.getName();
            }
            if (called != null) {
                strcalling = calling.getName();
            }
            if (term != null) {
                strterm = term.getName();
            }
            if (redirect != null) {
                strredirect = redirect.getName();
            }

            if (call instanceof CiscoCall) {
                callid = ((CiscoCall) call).getCallID().getGlobalCallID();
                //callid   = ((CiscoCall)call).getCallID().intValue();
                system = ((CiscoCall) call).getCallID().getCallManagerID();
                gcallid = CallID.getInstance(m_Jtapi.getCMID(), system, callid);
            }

            if (conn instanceof CiscoConnection) {
                connid = ((CiscoConnection) conn).getConnectionID().intValue();
            }

            evt = new ConnEvt();

            // IEvt
            evt.setDevice(getDn());

            evt.setEventID(id);
            evt.setDn(strdn);
            evt.setCause(cause);
            evt.setMetaCode(metacode);

            // ConnEvt
            evt.setCallID(gcallid);
            evt.setCallState(callstate);
            evt.setConnID(connid);
            evt.setConnState(connstate);

            evt.setCallingDn(strcalling);
            evt.setCalledDn(strcalled);
            evt.setReDirectDn(strredirect);
            evt.setCtlCause(ctlcause);
            evt.setDNIS("");
            evt.setANI("");
            evt.set_GCallID(gcallid.getGCallID());

            // 이벤트를 전송한다.
//            m_Log.info(getDisDn() + " " + evt.toMsg().toString());
            m_Jtapi.ReceiveEvent((Evt) evt);

			StringBuffer msg = new StringBuffer();

			msg.append("[").append(CheckFunc.FixLenString(getDn(), 10, " ", 1)).append("]"); // Device
			msg.append(CheckFunc.FixLenString(CodeToString.EvtToString(id), 30," ", 1)); // Event
			msg.append(" CallID:").append(CheckFunc.FixLenString(gcallid.getGCallID(), 13, " ", 1)); // CallID
			msg.append(" Dn:").append(CheckFunc.FixLenString(strdn, 10, " ", 1)); // Dn
			msg.append(" Calling:").append(strcalling);
			msg.append(" Called:").append(strcalled);
			msg.append(" Redirect:").append(strredirect);
			msg.append(" Cause:").append(cause);
			msg.append(" Metacode:").append(metacode);
			msg.append(" CtlCause:").append(ctlcause);

			m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, SVCTYPE.JTAPI,evt.get_GCallID(), "EvtCallCtlConnEv", msg.toString());

        } catch (Exception e) {
        	e.printStackTrace(pw);
        	m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.ERR_LOG, SVCTYPE.JTAPI, evt.get_GCallID(), "EvtCallCtlConnEv",  sw.toString());
            evt = null;
        }
        
    }

    /*
     * yjyoon EvtCallCtlTermConnEv  Event 
     * 215 : CallCtl_TermConn_DroppedEv
     * 218 : CallCtl_TermConn_RingingEv
     * 219 : CallCtl_TermConn_TalkingEv
     */
    private void EvtCallCtlTermConnEv(CallCtlTermConnEv termconnEv) {
    	
    	m_Log.Jtapi(LOGTYPE.STAND_LOG, getCallId(termconnEv), "EvtCallCtlTermConnEv", "EvtCallCtlTermConnEv");
        TermConnEvt evt = null;
        CallID gcallid = null;
        try {
            // Ev
            int id = termconnEv.getID();
            int cause = termconnEv.getCause();
            int metacode = termconnEv.getMetaCode();

            // CallEv
            Call call = termconnEv.getCall();
            
            CiscoCall ciscoCall = (CiscoCall) termconnEv.getCall();
//            CiscoCall ciscoCall = (CiscoCall) call;
            
            // TermConnEv
            CallControlTerminalConnection termConn = (CallControlTerminalConnection) termconnEv.getTerminalConnection();

            // CallCtlEv
            int ctlcause = termconnEv.getCallControlCause();
            

            // CallCtlCallEv
            Address called = termconnEv.getCalledAddress();
            Address calling = termconnEv.getCallingAddress();
            Terminal callingterm = termconnEv.getCallingTerminal();
            Address redirect = termconnEv.getLastRedirectedAddress();

            // Data Convert
            Terminal term = termConn.getTerminal();
            CallControlConnection conn = (CallControlConnection) termConn.getConnection();
            Address dn = conn.getAddress();
            int callstate = call.getState();
            int connstate = conn.getCallControlState();
            int termstate = termConn.getCallControlState();

            String strdn = "";
            String strterm = "";
            String strcalled = "";
            String strcalling = "";
            String strcallingterm = "";
            String strredirect = "";
            String strCiscoCalling = "";
            String strCiscoCalled = "";
            
            if (dn != null) {
                strdn = dn.getName();
            }
            if (term != null) {
                strterm = term.getName();
            }
            
            // Calling 정보 추가 2016.04.25
            if(ciscoCall.getCurrentCallingPartyInfo() != null){
            	strCiscoCalling = ciscoCall.getCurrentCallingPartyInfo().getAddress().getName();
            }
            // Called 정보 추가 2016.04.25
            if(ciscoCall.getCurrentCalledPartyInfo() != null){
            	strCiscoCalled = ciscoCall.getCurrentCalledPartyInfo().getAddress().getName();
            }
            
            
            if (called != null) {
                strcalled = called.getName();
            }
            if (called != null) {
                strcalling = calling.getName();
            }
            
            if (callingterm != null) {
                strcallingterm = callingterm.getName();
            }
            if (redirect != null) {
                strredirect = redirect.getName();
            }

            // Cisco Spec
            int callid = 0;
            int system = 0;
            gcallid = CallID.getNull();
            if (call instanceof CiscoCall) {
                callid = ((CiscoCall) call).getCallID().getGlobalCallID();
                //			callid   = ((CiscoCall)call).getCallID().intValue();
                system = ((CiscoCall) call).getCallID().getCallManagerID();
                gcallid = CallID.getInstance(m_Jtapi.getCMID(), system, callid);
            }

            int connid = 0;
            if (conn instanceof CiscoConnection) {
                connid = ((CiscoConnection) conn).getConnectionID().intValue();
            }

            evt = new TermConnEvt();

            // IEvt
            evt.setDevice(getDn());

            evt.setEventID(id);
            evt.setDn(strdn);
            evt.setCause(cause);
            evt.setMetaCode(metacode);

            // ConnEvt
            evt.setCallID(gcallid);
            evt.setCallState(callstate);
            evt.setConnID(connid);
            evt.setConnState(connstate);
            evt.setTermConnState(termstate);
            
            // CM 클러스터가 나눠져 있는경우 Called 와 Calling 정보가 안넘어 오는 경우가 있음.
            /*
            evt.setCallingDn(strcalling);
            evt.setCalledDn(strcalled);
            */
            evt.setCallingDn(strCiscoCalling);
            evt.setCalledDn(strCiscoCalled);
            
            evt.setReDirectDn(strredirect);

            evt.setCtlCause(ctlcause);
            evt.setTerminal(strterm);
//            evt.set_GCallID(getCallId(termconnEv));
            evt.set_GCallID(gcallid.getGCallID());
            
//            setDeviceState(evt);		// 전화기 상태 정보 저장
             
            m_Jtapi.ReceiveEvent(evt);

            
			StringBuffer msg = new StringBuffer();

			msg.append("[").append(CheckFunc.FixLenString(getDn(), 10, " ", 1)).append("]"); // Device
			msg.append(CheckFunc.FixLenString(CodeToString.EvtToString(id), 30," ", 1)); // Event
			msg.append(" CallID:").append(CheckFunc.FixLenString(gcallid.getGCallID(), 13, " ", 1)); // CallID
			msg.append(" Dn:").append(CheckFunc.FixLenString(strdn, 10, " ", 1)); // Dn
			msg.append(" Calling:").append(strcalling);
			msg.append(" Called:").append(strcalled);
			msg.append(" CallStatus:").append(callstate);
			msg.append(" Redirect:").append(strredirect);
			msg.append(" Terminal:").append(strterm);
			msg.append(" Cause:").append(cause);
			msg.append(" Metacode:").append(metacode);
			msg.append(" CtlCause:").append(ctlcause);
			
			m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, SVCTYPE.JTAPI,evt.get_GCallID(), "EvtCallCtlTermConnEv", msg.toString());
			
        } catch (Exception e) {
        	e.printStackTrace(pw);
        	m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.ERR_LOG, SVCTYPE.JTAPI, evt.get_GCallID(), "EvtCallCtlTermConnEv",  sw.toString());
        }
    }

    private void EvtCallCtlAddrEv(CallCtlAddrEv addrEv) {
    }

    private void EvtCallCtlTermEv(CallCtlTermEv termEv) {
    }

    private void EvtCiscoTransConfEv(CiscoCallEv callEv) {
    	m_Log.Jtapi(LOGTYPE.STAND_LOG, getCallId(callEv), "EvtCiscoTransConfEv", "EvtCiscoTransConfEv");
        ConfTransEvt evt = null;
        CallID gorigcallid = null;
        try {
            // Ev
            int id = callEv.getID();
            int cause = callEv.getCause();
            int metacode = callEv.getMetaCode();

            Address controladdr = null;

            String strcontrol = "";

            Call finalcall = null;
            Call origcall = null;

            boolean isSuccess = false;

            if (callEv instanceof CiscoTransferStartEv) {

                CiscoTransferStartEv TransferStartEv = (CiscoTransferStartEv) callEv;
                finalcall = TransferStartEv.getFinalCall();
                origcall = TransferStartEv.getTransferredCall();
                controladdr = TransferStartEv.getTransferControllerAddress();
                
            } else if (callEv instanceof CiscoTransferEndEv) {

                CiscoTransferEndEv TransferEndEv = (CiscoTransferEndEv) callEv;
                finalcall = TransferEndEv.getFinalCall();
                origcall = TransferEndEv.getTransferredCall();
                controladdr = TransferEndEv.getTransferControllerAddress();
                isSuccess = TransferEndEv.isSuccess();
            } else if (callEv instanceof CiscoConferenceStartEv) {

                CiscoConferenceStartEv ConferencerStartEv = (CiscoConferenceStartEv) callEv;
                finalcall = ConferencerStartEv.getFinalCall();
                origcall = ConferencerStartEv.getConferencedCall();
                controladdr = ConferencerStartEv.getConferenceControllerAddress();
            } else if (callEv instanceof CiscoConferenceEndEv) {

                CiscoConferenceEndEv ConferencerEndEv = (CiscoConferenceEndEv) callEv;
                finalcall = ConferencerEndEv.getFinalCall();
                origcall = ConferencerEndEv.getConferencedCall();
                controladdr = ConferencerEndEv.getConferenceControllerAddress();
                isSuccess = ConferencerEndEv.isSuccess();
            }

            if (controladdr != null) {
                strcontrol = controladdr.getName();
            }

            // Cisco Spec
            int callid = 0;
            int finalcallid = 0;
            int finalsystem = 0;
            CallID gfinalcallid = CallID.getNull();
            int origcallid = 0;
            int origsystem = 0;
            gorigcallid = CallID.getNull();

            int system = 0;

            int finalcallstate = -1;
            int origcallstate = -1;

            if (finalcall instanceof CiscoCall) {
                finalcallid = ((CiscoCall) finalcall).getCallID().getGlobalCallID();
//				finalcallid   = ((CiscoCall)finalcall).getCallID().intValue();
                finalsystem = ((CiscoCall) finalcall).getCallID().getCallManagerID();
                finalcallstate = finalcall.getState();
                gfinalcallid = CallID.getInstance(m_Jtapi.getCMID(), finalsystem, finalcallid);

            }
            if (origcall instanceof CiscoCall) {
                origcallid = ((CiscoCall) origcall).getCallID().getGlobalCallID();
//				origcallid   = ((CiscoCall)origcall).getCallID().intValue();
                origsystem = ((CiscoCall) origcall).getCallID().getCallManagerID();
                origcallstate = origcall.getState();
                gorigcallid = CallID.getInstance(m_Jtapi.getCMID(), origsystem, origcallid);
            }

            evt = new ConfTransEvt();

            evt.setDevice(m_Dn);
            evt.setEventID(id);
            evt.setMetaCode(metacode);
            evt.setCause(cause);
            evt.setControlDn(strcontrol);
            evt.setFirstCallID(gfinalcallid);
            evt.setFirstCallState(finalcallstate);
            evt.setSecondCallID(gorigcallid);
            evt.setSecondCallState(origcallstate);
            evt.setIsSuccess(isSuccess);
            evt.set_GCallID(getCallId(callEv));
            // 이벤트를 전송한다.
            m_Jtapi.ReceiveEvent((Evt) evt);

            StringBuffer msg = new StringBuffer();
            msg.append("[").append(CheckFunc.FixLenString(getDn(), 10, " ", 1)).append("]");					   // Device
            msg.append(CheckFunc.FixLenString(CodeToString.EvtToString(id), 30, " ", 1));   //Event
            msg.append(" ControlDn:").append(CheckFunc.FixLenString(strcontrol, 10, " ", 1));							 // ControlDn
            msg.append(" OrignalCall:").append(CheckFunc.FixLenString(gfinalcallid.getGCallID(), 13, " ", 1)); // Final CallI
            msg.append(" ConsultCall:").append(CheckFunc.FixLenString(gorigcallid.getGCallID(), 13, " ", 1));   // Original CallI
            msg.append(" IsSuccess:").append(isSuccess);
            msg.append(" Cause:").append(cause);
            msg.append(" Metacode:").append(metacode);
            m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, SVCTYPE.JTAPI, evt.get_GCallID(), "EvtCiscoTransConfEv",  msg.toString());
            
        } catch (Exception e) {
        	e.printStackTrace(pw);
        	m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.ERR_LOG, SVCTYPE.JTAPI, evt.get_GCallID(), "EvtCiscoTransConfEv",  sw.toString());
            evt = null;
        }
    }

    /*
     * yjyoon EvtCiscoTermEv  20130114
     * 1073745926 : CiscoTermDeviceStateActiveEv
     * 1073745927 : CiscoTermDeviceStateAlertingEv
     * 1073745929 : CiscoTermDeviceStateIdleEv
     * 
     */
    private void EvtCiscoTermEv(CiscoTermEv ciscotermEv) {
        // Ev
        TermEvt evt = null;

        try {

            int id = ciscotermEv.getID();
            int cause = ciscotermEv.getCause();
            int metacode = ciscotermEv.getMetaCode();
            String terminal = ciscotermEv.getTerminal().getName();

            switch (ciscotermEv.getID()) {
                case CiscoTermDeviceStateActiveEv.ID:
                    ChangeState(CiscoTerminal.DEVICESTATE_ACTIVE);
                    break;
                case CiscoTermDeviceStateAlertingEv.ID:
                    ChangeState(CiscoTerminal.DEVICESTATE_ALERTING);
                    break;
                case CiscoTermDeviceStateHeldEv.ID:
                    ChangeState(CiscoTerminal.DEVICESTATE_HELD);
                    break;
                case CiscoTermDeviceStateIdleEv.ID:
                    ChangeState(CiscoTerminal.DEVICESTATE_IDLE);
                    break;
                case CiscoTermInServiceEv.ID:
                    ChangeState(CiscoTerminal.DEVICESTATE_UNKNOWN);
                    break;
                case CiscoTermOutOfServiceEv.ID:
                    ChangeState(CiscoTerminal.DEVICESTATE_UNKNOWN);
                    break;
                default:
                    break;
            }

            evt = new TermEvt();

            evt.setDevice(m_Dn);
            evt.setDn(m_Dn);
            evt.setEventID(id);
            evt.setTerminal(terminal);
            evt.setMetaCode(metacode);
            evt.setCause(cause);
            evt.setState(getState());
//            evt.set_GCallID(getCallId(ciscotermEv));
            
//            setDeviceState(evt);		// 전화기 상태 정보 저장 
            
            m_Jtapi.ReceiveEvent((Evt) evt);

            StringBuffer msg = new StringBuffer();
            msg.append("[").append(CheckFunc.FixLenString(getDn(), 10, " ", 1)).append("]");					   // Device
            msg.append(CheckFunc.FixLenString(CodeToString.EvtToString(id), 30, " ", 1));   //Event
            msg.append(" EventID:").append(id).append(",").append(CodeToString.EvtToString(id));
            msg.append(" State:").append(CheckFunc.FixLenString(CodeToString.TermStateToString(getState()), 10, " ", 1));
            msg.append(" Terminal:").append(terminal);
            msg.append(" Cause:").append(cause);
            msg.append(" Metacode:").append(metacode);
            m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, SVCTYPE.JTAPI, "", "EvtCiscoTermEv",  msg.toString());
            
        } catch (Exception e) {
        	e.printStackTrace(pw);
        	m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.ERR_LOG, SVCTYPE.JTAPI, "", "EvtCiscoTermEv",  sw.toString());
            evt = null;
        }
    }
    
//    private void setDeviceState(TermEvt evt) {
//		// TODO Auto-generated method stub
//    	logwrite.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, "", "setDeviceState", "set Device State !! DN:"+evt.getDn()+" State:" + evt.getState()+","+CodeToString.TermStateToString(evt.getState()));
//    	DeviceMgr deviceMgr = DeviceMgr.getInstance();
//    	deviceMgr.putDeviceState(evt.getDn(), evt.getState());
//	} 

	private String getCallId(CallEv callEv){
    	CiscoCall ciscoCall = (CiscoCall)callEv.getCall();
    	String callid = String.valueOf(m_Jtapi.getCMID()) + "/" + String.valueOf(ciscoCall.getCallID().getGlobalCallID());
    	return callid;
    }
	
	
}
