package com.isi.event;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.lang.*;

import com.cisco.jtapi.extensions.*;
import com.isi.data.CallID;

/*
 * author : greatyun
 * 
 */
public  class CallEvt  extends Evt {
	private CallID	 m_CallID;
	private int		m_CallState;
	private CallID	 m_ConsultCallID;
	private StringBuffer	msg = null;
	
	private String 		m_GCallID;
	
	
	public String get_GCallID() {
		return m_GCallID;
	}

	public void set_GCallID(String m_GCallID) {
		this.m_GCallID = m_GCallID;
	}

	public void setEventID(int aEvt) {
		if (CiscoConsultCallActiveEv.ID == aEvt)
		{
			m_Evt = Call_ConsultCallActiveEv;
		} else  {
			m_Evt = aEvt;
		}
	}

	public CallID getCallID() {
		return m_CallID;
	}

	public void setCallID(CallID aCallID) {
		m_CallID = aCallID;
	}

	public CallID getConsultCallID() {
		return m_ConsultCallID;
	}

	public void setConsultCallID(CallID aConsultCallID) {
		m_ConsultCallID = aConsultCallID;
	}

	public void setCallState(int aCallState) {
		m_CallState = aCallState;
	}

	public StringBuffer toMsg() {
		if  (msg != null) return msg;
		try {
			msg = super.toMsg();
			if (msg == null)
				return msg;
			msg.append(m_CallID.getGCallID());
			msg.append(IEvt.DeliMetar);
//			msg.append(m_CallState);
			msg.append(0);
			msg.append(IEvt.DeliMetar);
			msg.append(m_ConsultCallID.getGCallID());
		} catch (Exception e) {
			throw e;
		} finally {
			return msg;
		}
	}
}
