package com.isi.event;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

public abstract class Evt implements IEvt {
	
	private 	String  	m_Device;   // 모니터링 하는 Dn
	private 	String 		m_GCallID;
	private 	String  	m_Dn;	   // 이벤트와 관련된 Dn
	protected 	int   		m_Evt;	  // 모니터링 하는 Dn
	private 	int	 		m_Cause;
	private 	int	 		m_MetaCode;
	
	
	
	
	public String getM_GCallID() {
		return m_GCallID;
	}
	public void setM_GCallID(String m_GCallID) {
		this.m_GCallID = m_GCallID;
	}
	public void setDevice(String aDevice) {
		m_Device = aDevice;
	}
	public void setEventID(int aEvt) {
		m_Evt = aEvt;
	}
	public void setDn(String aDn) {
		m_Dn = aDn;
	}
	public void setCause(int aCause) {
		m_Cause = aCause;
	}
	public void setMetaCode(int aMetaCode) {
		m_MetaCode = aMetaCode;
	}

	public String getDevice() {
		return m_Device;
	}
	public int getEventID() {
		return m_Evt;
	}
	public String getDn() {
		return m_Dn;
	}
	public int getCause() {
		return m_Cause;
	}
	public int getMetaCode() {
		return m_MetaCode;
	}

	public StringBuffer toMsg() {
		StringBuffer msg = null;
		try {
			msg = new StringBuffer();
			msg.append("E");
//			msg.append(CodeToString.EvtToString(getEventID()));
			msg.append(getEventID());
			msg.append(IEvt.DeliMetar);
			msg.append(getDevice());
			msg.append(IEvt.DeliMetar);
			msg.append(getDn());
			msg.append(IEvt.DeliMetar);
			msg.append(getCause());
			msg.append(IEvt.DeliMetar);
			msg.append(getMetaCode());
			msg.append(IEvt.DeliMetar);
		} catch (Exception e) {
			throw e;
		} finally {
			return msg;
		}
	}
}
