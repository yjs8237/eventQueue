package com.isi.jtapi;

import com.isi.axl.CiscoPhoneInfo;
import com.isi.event.IEventObserver;
import com.isi.event.IEvt;
import com.isi.vo.JTapiResultVO;

public interface IJTAPI  {

	public int serviceStart(String cmIP, String cmID, String cmPwd);
	public int ServiceStop();
	public int MonitorAllStart(CiscoPhoneInfo address);
	public int MonitorAllStop();
	public JTapiResultVO MonitorStart(String aDn);
	public JTapiResultVO MonitorStop(String aDn);
	public JTapiResultVO pickupCall(String myExtension , String pickupExtension);
	public JTapiResultVO makeCall(String myExtension , String callingNumber , String mac_address);
	public JTapiResultVO stopCall(String myExtension);
	
//	Provider getProvider();
//	public Log getLog();
	/*
	public int MakeCall(String aDn, String aDestDn);
	public int Answer(String aDn, String callid);
	public int Hold(String aDn, String callid);
	public int Retrieve(String aDn, String callid);
	public int Consult(String aDn, String aDestDn, String callid);
	public int Alternate(String aDn, String holdid, String activeid);
	public int Reconnect(String aDn, String holdid, String activeid);
	public int Transfer(String aDn, String holdid, String activeid);
	public int Conference(String aDn, String holdid, String activeid);
	public int SingleStepTransfer(String aDn, String aDestDn, String callid);
	public int SingleStepConference(String aDn,String aDestDn, String callid);
	public int Disconnect(String aDn, String callid);
	public int SendDTMF(String aDn, String aDtmfDn, String callid);
	public int SetDTMFDetection(String aDn, boolean bdtmf);
	public int SetDND(String aDn, boolean bdnd);
	public int GetDND(String aDn, boolean [] bdnd);
	public int SetForward(String aDn, String aDest, boolean bforward);
	public int GetForward(String aDn, String [] aDest, boolean [] bforward);
	*/
	public int getCMID();
	void ReceiveEvent(IEvt evt);
}
