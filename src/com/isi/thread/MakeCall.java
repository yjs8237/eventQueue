package com.isi.thread;

import com.isi.service.JtapiService;
import com.isi.vo.JTapiResultVO;
import com.isi.vo.MakeCallVO;

public class MakeCall extends Thread{

	private MakeCallVO makeCallVO;
	private String requestID;
	
	public MakeCall (MakeCallVO makeCallVO , String requestID) {
		this.makeCallVO = makeCallVO;
		this.requestID = requestID;
	}
	
	public void run () {
		
		JTapiResultVO resultVO = JtapiService.getInstance().makeCall(makeCallVO.getMyExtension(),
				makeCallVO.getCallingNumber() , makeCallVO.getMac_address() , requestID);
		
	}
}
