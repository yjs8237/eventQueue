package com.isi.event;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author skan
 */
public class TermEvt extends Evt{
	private int m_State;
	private String m_terminal;
	private StringBuffer msg = null;
	private String m_GCallID;
	
	
	public String get_GCallID() {
		return m_GCallID;
	}

	public void set_GCallID(String m_GCallID) {
		this.m_GCallID = m_GCallID;
	}

	public void setState(int aState) {
		m_State = aState;
	}

	public int getState() {
		return m_State;
	}

	public void setTerminal(String aTerminal) {
		m_terminal = aTerminal;
	}

	public String getTerminal() {
		return m_terminal;
	}

	public StringBuffer toMsg() {
		if( msg != null) return msg;
		try {
			msg = new StringBuffer();
			msg.append("device state :");
			msg.append(m_State);
		} catch (Exception e) {
			throw e; //e.printStackTrace();
		} finally {
			return msg;
		}
	}
}
