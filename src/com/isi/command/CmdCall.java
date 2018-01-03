package com.isi.command;


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

public class CmdCall extends ICmd { 
	String  m_DestDn;
	String  m_DTMF;
	String  m_UUI;
	String  m_firstID;
	String	m_secondID;

	public void setDestDn(String aDeskDn) {
		m_DestDn = aDeskDn;
	}
	public void setSendDTMF(String aDTMF) {
		m_DTMF = aDTMF;
	}
	public void setUUI(String aUUI) {
		m_UUI = aUUI;
	}
	public void setFirstID(String aCallID) {
		m_firstID = aCallID;
	}
	public void setSecondID(String aCallID) {
		m_secondID = aCallID;
	}

	public String getDestDn() {
		return m_DestDn;
	}
	public String getSendDTMF() {
		return m_DTMF;
	}
	public String getUUI() {
		return m_UUI;
	}
	public String getFisrtID () {
		return m_firstID;
	}
	public String getSecondID () {
		return m_secondID;
	}
}
