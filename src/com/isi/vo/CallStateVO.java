package com.isi.vo;

/**
 *
 * @author greatyun
 */
public class CallStateVO {

	private String callID;
	private String callingDN;
	private String calledDN;
	private int callstate;
	private String DN;
	private String targetDN;

	public String getDN() {
		return DN;
	}

	public CallStateVO setDN(String dN) {
		DN = dN;
		return this;
	}

	public String getTargetDN() {
		return targetDN;
	}

	public CallStateVO setTargetDN(String targetDN) {
		this.targetDN = targetDN;
		return this;
	}

	public String getCallID() {
		return callID;
	}

	public CallStateVO setCallID(String callID) {
		this.callID = callID;
		return this;
	}

	public String getCallingDN() {
		return callingDN;
	}

	public CallStateVO setCallingDN(String callingDN) {
		this.callingDN = callingDN;
		return this;
	}

	public String getCalledDN() {
		return calledDN;
	}

	public CallStateVO setCalledDN(String calledDN) {
		this.calledDN = calledDN;
		return this;
	}

	public int getCallstate() {
		return callstate;
	}

	public CallStateVO setCallstate(int callstate) {
		this.callstate = callstate;
		return this;
	}

	public String toString() {
		return "DN[" + DN + "]targetDN[" + targetDN + "]callID[" + callID + "]callingDN[" + callingDN + "]calledDN["
				+ calledDN + "]callstate[" + callstate + "]";
	}

}
