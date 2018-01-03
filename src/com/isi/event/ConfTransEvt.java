package com.isi.event;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import com.cisco.jtapi.extensions.CiscoConferenceEndEv;
import com.cisco.jtapi.extensions.CiscoConferenceStartEv;
import com.cisco.jtapi.extensions.CiscoTransferEndEv;
import com.cisco.jtapi.extensions.CiscoTransferStartEv;
import com.isi.data.CallID;


public  class ConfTransEvt  extends Evt {

	private String  m_ControlDn;
	private CallID  m_FirstCallID;
	private int	 m_FirstCallState;
	private CallID  m_SecondCallID;
	private int	 m_SecondCallState;
	private int	 m_isSuccess;
	StringBuffer	msg = null;
	private String m_GCallID;
	
	

	public void setEventID(int aEvt) {
		if (CiscoTransferStartEv.ID == aEvt) {
			m_Evt = IEvt.TransferStartEv;
		} else if (CiscoTransferEndEv.ID == aEvt) {
			m_Evt = IEvt.TransferEndEv;
		} else if (CiscoConferenceStartEv.ID == aEvt) {
			m_Evt = IEvt.ConferenceStartEv;
		} else if (CiscoConferenceEndEv.ID == aEvt) {
			m_Evt = IEvt.ConferenceEndEv;
		}
	}
	

	public String get_GCallID() {
		return m_GCallID;
	}

	public void set_GCallID(String m_GCallID) {
		this.m_GCallID = m_GCallID;
	}

	public CallID getCallID() {
		return m_SecondCallID;
	}

	public void setControlDn(String aControlDn) {
		m_ControlDn = aControlDn;
	}
	public String getControlDn() {
		return m_ControlDn;
	}
	public void setFirstCallID(CallID aFirstCallID) {
		m_FirstCallID = aFirstCallID;
	}
	public CallID getFirstCallID() {
		return m_FirstCallID;
	}
	public CallID getSecondCallID() {
		return m_SecondCallID;
	}

	public void setFirstCallState(int aFirstCallState) {
		m_FirstCallState = aFirstCallState;
	}
	public void setSecondCallID(CallID aSecondCallID) {
		m_SecondCallID = aSecondCallID;
	}
	public void setSecondCallState(int aSecondCallState) {
		m_SecondCallState = aSecondCallState;
	}
	public void setIsSuccess(boolean aIsSuccess) {
		if (aIsSuccess) {
			m_isSuccess = 1;
		} else {
			m_isSuccess = 0;
		}
	}

	public StringBuffer toMsg() {
		if  (msg != null) return msg;
		try {
			msg = super.toMsg();
			if (msg == null)
				return msg;
			msg.append(m_ControlDn);
			msg.append(IEvt.DeliMetar);
			msg.append(m_FirstCallID.getGCallID());
			msg.append(IEvt.DeliMetar);
//			msg.append(m_FirstCallState);
			msg.append(0);
			msg.append(IEvt.DeliMetar);
			msg.append(m_SecondCallID.getGCallID());
			msg.append(IEvt.DeliMetar);
//			msg.append(m_SecondCallState);
			msg.append(0);
			msg.append(IEvt.DeliMetar);
			msg.append(m_isSuccess);
		} catch (Exception e) {
			throw e;
		} finally {
			return msg;
		}
	}
}
