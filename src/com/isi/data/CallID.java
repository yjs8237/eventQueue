package com.isi.data;

import com.isi.utils.CheckFunc;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



/**
 *
 * @author skan
 */
public class CallID {

	private int m_clusterid;
	private int m_cmid;
	private int m_seq;
	private String m_gcallid;
	private String m_ccallid;

	private static CallID m_null = null;

	public static CallID getNull() {
		if(m_null == null) {
			m_null = getInstance(0, 0, 0);
		}
		return m_null;
	}

	public static CallID getInstance(int cluster, int cm, int seq) {
		CallID callid = new CallID(cluster, cm, seq);
		return callid;
	}

	public static CallID getInstance(int cluster, int intvalue) {
		CallID callid = new CallID(cluster, intvalue);
		return callid;
	}

	public static CallID getInstance(String callid) {
		CallID gcallid = new CallID(callid);
		if(gcallid.IsSameGCall(getNull())) {
			return getNull();
		}
		return gcallid;
	}

	private CallID(int cluster, int cm, int seq) {
		m_clusterid = cluster;
		m_cmid = cm;
		m_seq = seq;
		m_gcallid = m_clusterid + "/" + m_cmid + "/" + m_seq;
	}

	private CallID(int cluster, int intvalue) {
		m_clusterid = cluster;
		m_cmid = intvalue >> 24;
		m_seq = intvalue & 0x00FFFFFF;
		m_gcallid = m_clusterid + "/" + m_cmid + "/" + m_seq;
	}

	private CallID(String callid) {
		try {
			String [] callinfo = CheckFunc.TokenizerString(callid, "/");

			if (callinfo.length != 3 ||
					!(CheckFunc.IsNumber(callinfo[0]) &&
					CheckFunc.IsNumber(callinfo[1]) &&
					CheckFunc.IsNumber(callinfo[2]))
					) {
				m_clusterid = 0;
				m_cmid = 0;
				m_seq = 0;
				m_gcallid = m_clusterid + "/" + m_cmid + "/" + m_seq;
			} else {
				m_clusterid = Integer.parseInt(callinfo[0]);
				m_cmid = Integer.parseInt(callinfo[1]);
				m_seq = Integer.parseInt(callinfo[2]);
				m_gcallid = m_clusterid + "/" + m_cmid + "/" + m_seq;
			}
		} catch(Exception e) {
			m_clusterid = 0;
			m_cmid = 0;
			m_seq = 0;
			m_gcallid = m_clusterid + "/" + m_cmid + "/" + m_seq;
		}

	}

	public int getintValue() {
		return m_seq + ( m_cmid << 24);
	}

	public int getClusterID() {
		return m_clusterid;
	}

	public int getCmID() {
		return m_cmid;
	}

	public int getCallSeq() {
		return m_seq;
	}

	public boolean IsSameGCall(CallID callid) {
		if(m_gcallid.equals(callid.getGCallID())) {
			return true;
		} else {
			return false;
		}
	}

	public boolean IsSameGCall(String callid) {
		if(m_gcallid.equals(callid)){
			return true;
		} else {
			return false;
		}
	}

	public boolean IsSameCCall(String callid) {
		int idx = callid.indexOf("/");
		if (idx == -1) return false;

		String ccallid = callid.substring(idx, callid.length());

		if(m_ccallid == null) {
			getCCallID();
		}
		if( m_ccallid.equals(ccallid)) {
			return true;
		} else {
			return false;
		}
	}

	public String getGCallID() {
		return  m_gcallid;
	}

	public String getCCallID() {
		if(m_ccallid == null) {
			m_ccallid = m_cmid + "/" + m_seq;
		}
		return m_ccallid;
	}
}
