package com.isi.jtapi;

public interface IDevCmd {

	public void setDn(String aDn);
	public String getDn();
	public int MakeCall(String aDestDn);
	public int Answer(String callid);
	public int Hold(String callid);
	public int Retrieve(String callid);
	public int Consult( String aDestDn, String callid);
	public int Alternate(String holdid, String activeid);
	public int Reconnect(String holdid, String activeid);
	public int Transfer(String holdid, String activeid);
	public int Conference(String holdid, String activeid);
	public int SingleStepTransfer(String aDestDn, String callid);
	public int SingleStepConference(String aDestDn, String callid);
	//public int AddParty(String aDestDn, long callid);
	public int Disconnect(String callid);
	public int SendDTMF(String dtmf, String callid);
	public int SetDTMFDetection(boolean bdtmf);
	public int SetForward(String aDest, boolean bforward);
	public int GetForward(String [] Dest, boolean [] bforward);
	public int SetDND(boolean bdnd);
	public int GetDND(boolean [] bdnd);
}
