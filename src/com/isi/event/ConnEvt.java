package com.isi.event;

import java.util.Calendar;

import com.isi.data.CallID;


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


// ConnEv의 최초 Interface
// 
public class ConnEvt extends Evt {

    private CallID m_CallID;
    private int m_CallState;
    private long m_ConnID;
    private int m_ConnState;
    private String m_CallingDn;
    private String m_CalledDn;
    private String m_ReDirectDn;
    private int m_CtlCause;
    private String m_DNIS;
    private String m_ANI;
    private StringBuffer msg;
    private String m_GCallID;
    
    
    
    
    public String get_GCallID() {
		return m_GCallID;
	}

	public void set_GCallID(String m_GCallID) {
		this.m_GCallID = m_GCallID;
	}

	public void setCallID(CallID aCallID) {
        m_CallID = aCallID;
    }

    public CallID getCallID() {
        return m_CallID;
    }

    public String getCallingDn() {
        return m_CallingDn;
    }

    public String getCalledDn() {
        return m_CalledDn;
    }

    public String getRedirectDn() {
        return m_ReDirectDn;
    }

    public String getDNIS() {
        return m_DNIS;
    }

    public String getANI() {
        return m_ANI;
    }

    public void setCallState(int aCallState) {
        m_CallState = aCallState;
    }

    public void setConnID(long aConnID) {
        m_ConnID = aConnID;
    }

    public void setConnState(int aConnState) {
        m_ConnState = aConnState;
    }

    public void setCallingDn(String aCallingDn) {
        m_CallingDn = aCallingDn;
    }

    public void setCalledDn(String aCalledDn) {
        m_CalledDn = aCalledDn;
    }

    public void setReDirectDn(String aReDirectDn) {
        m_ReDirectDn = aReDirectDn;
    }

    public void setCtlCause(int aCtlCause) {
        m_CtlCause = aCtlCause;
    }
    
    public int getCtlCause(){
    	return m_CtlCause;
    }

    public void setDNIS(String aDNIS) {
        m_DNIS = aDNIS;
    }

    public void setANI(String aANI) {
        m_ANI = aANI;
    }

     /**
     *
     * @return 
     * jylee 2012-10-15 콜이벤트 관련 서버시간 전송 추가, Converter 함수 신규 추가.
     */
    @Override
    public StringBuffer toMsg() {
        Calendar rightNow = Calendar.getInstance();
        String curTime = Integer.toString(rightNow.get(Calendar.YEAR)) + Converter(2, Integer.toString(rightNow.get(Calendar.MONTH)+1)) + Converter(2, Integer.toString(rightNow.get(Calendar.DAY_OF_MONTH))) 
                + Converter(2, Integer.toString(rightNow.get(Calendar.HOUR_OF_DAY))) + Converter(2, Integer.toString(rightNow.get(Calendar.MINUTE))) + Converter(2, Integer.toString(rightNow.get(Calendar.SECOND))) + Converter(3, Integer.toString(rightNow.get(Calendar.MILLISECOND)));
        
        if (msg != null) {
            return msg;
        }
        try {
            msg = super.toMsg();
            if (msg == null) {
                return msg;
            }
            msg.append(m_CallID.getGCallID());
            msg.append(IEvt.DeliMetar);
          msg.append(m_CallState);
//            msg.append(0);
            msg.append(IEvt.DeliMetar);
          msg.append(m_ConnID);
//            msg.append(0);
            msg.append(IEvt.DeliMetar);
          msg.append(m_ConnState);
//            msg.append(0);
            msg.append(IEvt.DeliMetar);
            msg.append(m_CallingDn);
            msg.append(IEvt.DeliMetar);
            msg.append(m_CalledDn);
            msg.append(IEvt.DeliMetar);
            msg.append(m_ReDirectDn);
            msg.append(IEvt.DeliMetar);
          msg.append(m_CtlCause);
//            msg.append(0);
            msg.append(IEvt.DeliMetar);
            msg.append(m_DNIS);
            msg.append(IEvt.DeliMetar);
            msg.append(m_ANI);
            // 콜이벤트에 서버시간 포함
            msg.append(IEvt.DeliMetar);
            msg.append(curTime);
        } catch (Exception e) {
            throw e;
        } finally {
            return msg;
        }
    }
    
    public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("EventID:").append(getEventID()).append(IEvt.DeliMetar);
		sb.append("Device:").append(getDevice()).append(IEvt.DeliMetar);
		sb.append("DN:").append(getDn()).append(IEvt.DeliMetar);
		sb.append("Cause:").append(getCause()).append(IEvt.DeliMetar);
		sb.append("MetaCode:").append(getMetaCode()).append(IEvt.DeliMetar);
		sb.append("m_GCallID:").append(m_GCallID).append(IEvt.DeliMetar);
		sb.append("m_CallID:").append(m_CallID.getGCallID()).append(IEvt.DeliMetar);
		sb.append("m_CallState:").append(m_CallState).append(IEvt.DeliMetar);
		sb.append("m_ConnID:").append(m_ConnID).append(IEvt.DeliMetar);
		sb.append("m_ConnState:").append(m_ConnState).append(IEvt.DeliMetar);
		sb.append("m_CallingDn:").append(m_CallingDn).append(IEvt.DeliMetar);
		sb.append("m_CalledDn:").append(m_CalledDn).append(IEvt.DeliMetar);
		sb.append("m_ReDirectDn:").append(m_ReDirectDn).append(IEvt.DeliMetar);
		sb.append("m_CtlCause:").append(m_CtlCause).append(IEvt.DeliMetar);
		
		return sb.toString();
	}
    
    public String Converter(int nLen, String stTemp) {
        if(stTemp.length()==1) {
            if(nLen==2){
                stTemp = "0" + stTemp;
            } else {
                stTemp = "00" + stTemp;
            }
        } else if(stTemp.length()==2) {
            if(nLen==3){
                stTemp = "0" + stTemp;
            }
        }
        return stTemp;
    }
}
