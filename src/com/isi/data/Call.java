package com.isi.data;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.Calendar;
import java.util.GregorianCalendar;

import com.isi.constans.RESULT;
import com.isi.event.IStateEvt;
import com.isi.utils.CheckFunc;


public class Call implements IStateEvt {

    private static final String DeliMetar = "^";
    private static final String SubDeliMeter = "|"; //20121224 updated by yong                          
    public static final String NONE = "NC";
    public static final String INBOUND = "IB";
    public static final String OUTBOUND = "OB";
    public static final String INTERNAL = "IT";
    public static final String CONSULT = "CS";
    public static final String TRANSFER = "TR";
    public static final String CONFERENCE = "CF";
    public static final String CLEAR = "NC";
    public static final String PICKUP = "PU";

    final CallID m_callid;
    final String m_Uniqueid;

    int m_evtid = IStateEvt.CALL_STATE_REPORT;
    String m_dn;
    CallID m_curcallid;
    String m_calltype;

    String m_controlDn = "";
    String m_exttype = "";
    CallID m_extcallid = CallID.getNull();

    String[] m_agentid = new String[2];
    String[] m_groupid = new String[2];
    String m_orgCallingDn = "";
    String m_orgCalledDn = "";

    String m_callingDn = "";
    String m_calledDn = "";
    String m_connectedDn = "";
    String m_redirectDn = "";
    String m_custinfo = "";

    Calendar m_createTime = null;
    Calendar m_deliveredTime = null;
    Calendar m_establieshedTime = null;
    Calendar m_clearTime = null;

    boolean m_dialing = false;
    boolean m_connected = false;
    boolean m_pickup = false;

    public Call(String dn, CallID callid, String uniqueid, CallID consultid, String calltype) {
        m_Uniqueid = uniqueid;
        m_dn = dn;
        m_callid = callid;
        m_extcallid = consultid;
        m_curcallid = callid;
        m_calltype = calltype; //Consult일 경우

        m_createTime = new GregorianCalendar();
    }

    public boolean IsConnected() {
        return m_connected;
    }

    public boolean IsDialing() {
        return m_dialing;
    }

    public boolean IsPickup() {
        return m_pickup;
    }

    public String getDn() {
        return m_dn;
    }

    public CallID getExtCallID() {
        return m_extcallid;
    }

    public CallID getOriginalID() {
        return m_callid;
    }

    public CallID getCallID() {
        return m_curcallid;
    }

    public String getCallType() {
        return m_calltype;
    }

    public String getExtType() {
        return m_exttype;
    }

    public boolean getDialing() {
        return m_dialing;
    }

    public String getCallingDn() {
        return m_callingDn;
    }

    public String getCalledDn() {
        return m_calledDn;
    }

    public String getOrgCallingDn() {
        return m_orgCallingDn;
    }

    public String getOrgCalledDn() {
        return m_orgCalledDn;
    }

    public String getControlDn() {
        return m_controlDn;
    }

    public Calendar getDeliveredTime() {
        return m_deliveredTime;
    }

    public Calendar getEstablieshedTime() {
        return m_establieshedTime;
    }

    public void setCallingDn(String callingDn) {
        m_callingDn = callingDn;
    }

    public void setCalledDn(String calledDn) {
        m_calledDn = calledDn;
    }

    public void setCallID(CallID callid) {
        m_curcallid = callid;
    }

    public void setExtType(String calltype, String controlDn, CallID extcallid) {
        m_exttype = calltype;
        m_extcallid = extcallid;
        m_controlDn = controlDn;
    }

    public void setPickup(boolean pickup) {
        m_pickup = pickup;
    }

    public int getEventID() {
        return m_evtid;
    }

    public int dialing(String dialingDn) {
        if (m_dn.equals(dialingDn)) {
            m_dialing = true;
        }
        return RESULT.RTN_SUCCESS;
    }

    public int offered(String[] agentid, String[] groupid, String callingDn,
            String calledDn, String redirectDn, String custinfo,
            String calltype) {

        m_agentid = agentid;
        m_groupid = groupid;
        // CallType에 대한 처리
        m_calltype = calltype;

        m_callingDn = callingDn;
        m_orgCallingDn = callingDn;
        m_calledDn = calledDn;
        m_orgCalledDn = calledDn;
        m_redirectDn = redirectDn;
        m_deliveredTime = new GregorianCalendar();

        return RESULT.RTN_SUCCESS;
    }

    public int connected(String connectedDn) {
        if (m_establieshedTime == null) {
            m_establieshedTime = new GregorianCalendar();
            m_connected = true;
        }
        m_connectedDn = connectedDn;
        return RESULT.RTN_SUCCESS;
    }

    public int disconnected() {
        m_clearTime = new GregorianCalendar();
        return RESULT.RTN_SUCCESS;
    }

    public void setUserInfo(String uui) {
        m_custinfo = uui;
    }

    public String ToCRD() {
        String strAgentId = "";
        String strGroupId = "";

        for (int i = 0; i < m_agentid.length; i++) {
            if (i != m_agentid.length - 1) {
                strAgentId = strAgentId + m_agentid[i] + SubDeliMeter;
            } else {
                strAgentId = strAgentId + m_agentid[i];
            }
        }

        for (int i = 0; i < m_groupid.length; i++) {
            if (i != m_groupid.length - 1) {
                strGroupId = strGroupId + m_groupid[i] + SubDeliMeter;
            } else {
                strGroupId = strGroupId + m_groupid[i];
            }
        }

        StringBuffer buffer = new StringBuffer();
        buffer.append(m_dn).append(DeliMetar);
        buffer.append(m_callid.getGCallID()).append(DeliMetar);
        buffer.append(m_Uniqueid).append(DeliMetar);
        buffer.append(m_curcallid.getGCallID()).append(DeliMetar);
        buffer.append(m_calltype).append(DeliMetar);
        buffer.append(m_exttype).append(DeliMetar);
        buffer.append(m_extcallid.getGCallID()).append(DeliMetar);
        buffer.append(strAgentId).append(DeliMetar);
        buffer.append(strGroupId).append(DeliMetar);
        buffer.append(m_orgCallingDn).append(DeliMetar);
        buffer.append(m_orgCalledDn).append(DeliMetar);
        buffer.append(m_redirectDn).append(DeliMetar);
        buffer.append(m_controlDn).append(DeliMetar);
        buffer.append(m_custinfo).append(DeliMetar);
        buffer.append(CheckFunc.CurrentDate(m_createTime)).append(DeliMetar);
        buffer.append(CheckFunc.CurrentDate(m_deliveredTime)).append(DeliMetar);
        buffer.append(CheckFunc.CurrentDate(m_establieshedTime)).append(DeliMetar);
        buffer.append(CheckFunc.CurrentDate(m_clearTime)).append(DeliMetar);
        return buffer.toString();
    }

    public String ToPackCRD() {
        String strAgentId = "";
        String strGroupId = "";

        for (int i = 0; i < m_agentid.length; i++) {
            if (i != m_agentid.length - 1) {
                strAgentId = strAgentId + m_agentid[i] + SubDeliMeter;
            } else {
                strAgentId = strAgentId + m_agentid[i];
            }
        }

        for (int i = 0; i < m_groupid.length; i++) {
            if (i != m_groupid.length - 1) {
                strGroupId = strGroupId + m_groupid[i] + SubDeliMeter;
            } else {
                strGroupId = strGroupId + m_groupid[i];
            }
        }

        StringBuffer buffer = new StringBuffer();
        buffer.append("	Calll Record ").append("DN[").append(m_dn).append("] \r\n");
        buffer.append("CallID		  [").append(m_callid.getGCallID()).append("] \r\n");
        buffer.append("UniqueID		[").append(m_Uniqueid).append("] \r\n");
        buffer.append("Current CallID  [").append(m_curcallid.getGCallID()).append("] \r\n");
        buffer.append("CallType		[").append(m_calltype).append("] \r\n");
        buffer.append("ExcType		 [").append(m_exttype).append("] \r\n");
        buffer.append("ExcCallID	   [").append(m_extcallid.getGCallID()).append("] \r\n");
        buffer.append("AentID		  [").append(strAgentId).append("] \r\n");
        buffer.append("GroupID		 [").append(strGroupId).append("] \r\n");
        buffer.append("CallingDn	   [").append(m_orgCallingDn).append("] \r\n");
        buffer.append("CalledDn		[").append(m_orgCalledDn).append("] \r\n");
        buffer.append("RedirectDn	  [").append(m_redirectDn).append("] \r\n");
        buffer.append("ControlDn	   [").append(m_controlDn).append("] \r\n");
        buffer.append("CustInfo		[").append(m_custinfo).append("] \r\n");
        buffer.append("OriginateTime   [").append(CheckFunc.CurrentDate(m_createTime)).append("] \r\n");
        buffer.append("OfferedTime	 [").append(CheckFunc.CurrentDate(m_deliveredTime)).append("] \r\n");
        buffer.append("ConnectedTime   [").append(CheckFunc.CurrentDate(m_establieshedTime)).append("] \r\n");
        buffer.append("DisconnectedTime[").append(CheckFunc.CurrentDate(m_clearTime)).append("] \r\n");
        
        return buffer.toString();
    }

    public String getAlertingCallInfo(String custinfo) {
        String msg = "발신자 : " + m_callingDn + "\r\n"
                + "수신자 : " + m_calledDn + "\r\n"
                + "형태 : " + m_calltype + "\r\n"
                + "고객정보 : " + custinfo + "\r\n";
        return msg;
    }
}
