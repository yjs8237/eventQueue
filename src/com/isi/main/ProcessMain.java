package com.isi.main;

import com.isi.constans.RESULT;
import com.isi.data.Employees;
import com.isi.duplex.DuplexMgr;
import com.isi.file.ILog;
import com.isi.file.LogMgr;
import com.isi.file.LogWriter;
import com.isi.process.IQueue;
import com.isi.process.JQueue;
import com.isi.service.JtapiService;
import com.isi.service.UDPThread;
import com.isi.thread.UDPService;
import com.isi.thread.XMLService;
import com.test.thread.TestService;
import com.test.thread.TestThread;
/**
*
* @author greatyun
*/
public class ProcessMain {

	

	
	public void ispsMode(){
		
		System.out.println("Start ISPS Mode!! Active Mode[" + DuplexMgr.getInstance().getActiveMode() +"]");
		
		IQueue queue = new JQueue(); // ISPS 로 부터 받는 UDP 패킷 처리 큐
		
//		System.out.println(DeviceMgr.getInstance().getAllDeviceInfo());
		 
		System.out.println("Start receiving UDP Packet from ISPS !!");
		
		// ISPS 로 부터 UDP 받는 소켓 스레드 시작
		UDPThread thread = new UDPThread(queue);
		thread.startService();
		
		/*
		 * ///////////////////////////////////////////////
		 * 
		 * 3. UDP 패킷 처리
		 */// ////////////////////////////////////////////
		
		UDPService udpService = new UDPService(queue);
		udpService.startService();
		
		
	}
	
	public void singleMode() {
		
		System.out.println("Start Single Mode!!  Active Mode[" + DuplexMgr.getInstance().getActiveMode() +"]");
		
			/*
			 * ///////////////////////////////////////////////
			 * 1. CM AXL Device 정보 가져오기 , Jtapi 연동
			 */// ////////////////////////////////////////////
			
			IQueue queue = new JQueue(); // 이벤트 데이터 처리 큐
			
			/* CM Jtapi 연동 (Device 정보 가져오기, Device 정보 DB Insert 하기) */
			JtapiService service = JtapiService.getInstance();
			service.startService(queue);
			
			/*
			TestThread[] thread = new TestThread[3];
			for (int i = 0; i < thread.length; i++) {
				thread[i] = new TestThread(queue);
				thread[i].start();
			}
			*/
			
			System.out.println("## Success!! Jtapi Service Started ##");
			
			Employees.getInstance().printAllEmployee();
			
			
			
			/*
			 * ///////////////////////////////////////////////
			 * 3. Jtapi 이벤트 처리
			 */// ////////////////////////////////////////////
			
			/* 70개의 스레드를 관리하는 스레드 풀 에서 CM 으로부터 받은 Jtapi 이벤트를 처리한다. */
			XMLService xmlservice = new XMLService(queue);
			xmlservice.startService();
			
			// xmlservice.stopService();
			
	}
	
	public void testMode() {
		
		System.out.println("Start Test Mode!!  Active Mode[" + DuplexMgr.getInstance().getActiveMode() +"]");
		/*
		 * ///////////////////////////////////////////////
		 * 1. CM AXL Device 정보 가져오기 , Jtapi 연동
		 */// ////////////////////////////////////////////
		
		IQueue queue = new JQueue(); // 이벤트 데이터 처리 큐
		/* CM Jtapi 연동 (Device 정보 가져오기, Device 정보 DB Insert 하기) */
		
		JtapiService service = JtapiService.getInstance();
		service.startService(queue);
		
		System.out.println("## Success!! Jtapi Service Started ##");
		
		/*
		 * ///////////////////////////////////////////////
		 * 3. Jtapi 이벤트 처리
		 */// ////////////////////////////////////////////
		
		/* 70개의 스레드를 관리하는 스레드 풀 에서 CM 으로부터 받은 Jtapi 이벤트를 처리한다. */
		TestService testService = new TestService(queue );
		testService.startService();
		
		
		for (int i = 0; i < 1200; i++) {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			TestThread thread = new TestThread(queue);
			thread.start();
			
		}
		
//		testService.putCallTestData();
		
		
	}

}
