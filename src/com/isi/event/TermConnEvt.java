package com.isi.event;

import com.isi.data.CallID;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



public class TermConnEvt extends Evt {

	private CallID  m_CallID;
	private int	 m_CallState;
	private long	m_ConnID;
	private int	 m_ConnState;
	private int	 m_TermConnState;
	private String  m_CallingDn;
	private String  m_CalledDn;
	private String  m_ReDirectDn;
	private int	 m_CtlCause;
	private String  m_Terminal;
	private StringBuffer msg = null;
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
	public String getTerminal() {
		return m_Terminal;
	}

	public int getCallState(){
		return m_CallState;
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
	public void setTermConnState(int aTermState) {
		m_TermConnState = aTermState;
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
	public void setTerminal(String aTerminal) {
		m_Terminal = aTerminal;
	}
	public int getCtlCause(){
		return m_CtlCause;
	}
	
	

	public StringBuffer toMsg() {
		if(msg != null) return msg;
		try {
			msg = super.toMsg();
			if (msg == null)
				return msg;
			msg.append(m_CallID.getGCallID());
			msg.append(IEvt.DeliMetar);
			msg.append(m_CallState);
//			msg.append(0);
			msg.append(IEvt.DeliMetar);
			msg.append(m_ConnID);
//			msg.append(0);
			msg.append(IEvt.DeliMetar);
			msg.append(m_ConnState);
//			msg.append(0);
			msg.append(IEvt.DeliMetar);
			msg.append(m_TermConnState);
//			msg.append(0);
			msg.append(IEvt.DeliMetar);
			msg.append(m_CallingDn);
			msg.append(IEvt.DeliMetar);
			msg.append(m_CalledDn);
			msg.append(IEvt.DeliMetar);
			msg.append(m_ReDirectDn);
			msg.append(IEvt.DeliMetar);
			msg.append(m_CtlCause);
//			msg.append(0);
			msg.append(IEvt.DeliMetar);
			msg.append(m_Terminal);
		} catch (Exception e) {
			throw e; //e.printStackTrace();
		} finally {
			return msg;
		}
	}
//	private CallID  m_CallID;
//	private int	 m_CallState;
//	private long	m_ConnID;
//	private int	 m_ConnState;
//	private int	 m_TermConnState;
//	private String  m_CallingDn;
//	private String  m_CalledDn;
//	private String  m_ReDirectDn;
//	private int	 m_CtlCause;
//	private String  m_Terminal;
//	private StringBuffer msg = null;
//	private String m_GCallID;
	
	
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
		sb.append("m_TermConnState:").append(m_TermConnState).append(IEvt.DeliMetar);
		sb.append("m_CallingDn:").append(m_CallingDn).append(IEvt.DeliMetar);
		sb.append("m_CalledDn:").append(m_CalledDn).append(IEvt.DeliMetar);
		sb.append("m_ReDirectDn:").append(m_ReDirectDn).append(IEvt.DeliMetar);
		sb.append("m_CtlCause:").append(m_CtlCause).append(IEvt.DeliMetar);
		sb.append("m_Terminal:").append(m_Terminal).append(IEvt.DeliMetar);
		
		return sb.toString();
	}
	
	
}
