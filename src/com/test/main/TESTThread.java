package com.test.main;

import java.util.Random;

import com.isi.constans.CALLSTATE;
import com.isi.data.CallStateMgr;
import com.isi.file.ILog;
import com.isi.file.LogMgr;
import com.isi.file.LogWriter;
import com.isi.vo.CallStateVO;

public class TESTThread extends Thread{
	
	private String id;
	private LogMgr log;
	public TESTThread(String ID){
		id = ID;
		log = LogMgr.getInstance();
	}
	
	public void run(){
		CallStateVO vo = new CallStateVO();
		vo.setCallID(id);
		vo.setCalledDN(String.valueOf(Integer.parseInt(id) + 1));
		
		long time = 0;
		for (int i = 0; i < 20; i++) {
			
			
			try {
				Thread.sleep(time);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		
		
	}
	
}
