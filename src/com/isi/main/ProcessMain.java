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
		
		IQueue queue = new JQueue(); // ISPS 濡� 遺��꽣 諛쏅뒗 UDP �뙣�궥 泥섎━ �걧
		
//		System.out.println(DeviceMgr.getInstance().getAllDeviceInfo());
		 
		System.out.println("Start receiving UDP Packet from ISPS !!");
		
		// ISPS 濡� 遺��꽣 UDP 諛쏅뒗 �냼耳� �뒪�젅�뱶 �떆�옉
		UDPThread thread = new UDPThread(queue);
		thread.startService();
		  
		//asd sadf
		/*
		 * ///////////////////////////////////////////////
		 * 
		 * 3. UDP �뙣�궥 泥섎━
		 */// ////////////////////////////////////////////
		
		UDPService udpService = new UDPService(queue);
		udpService.startService();
		
		
	}
	
	public void singleMode() {
		
		System.out.println("Start Single Mode!!  Active Mode[" + DuplexMgr.getInstance().getActiveMode() +"]");
		
			/*
			 * ///////////////////////////////////////////////
			 * 1. CM AXL Device �젙蹂� 媛��졇�삤湲� , Jtapi �뿰�룞
			 */// ////////////////////////////////////////////
			
			IQueue queue = new JQueue(); // �씠踰ㅽ듃 �뜲�씠�꽣 泥섎━ �걧
			
			/* CM Jtapi �뿰�룞 (Device �젙蹂� 媛��졇�삤湲�, Device �젙蹂� DB Insert �븯湲�) */
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
			 * 3. Jtapi �씠踰ㅽ듃 泥섎━
			 */// ////////////////////////////////////////////
			
			/* 70媛쒖쓽 �뒪�젅�뱶瑜� 愿�由ы븯�뒗 �뒪�젅�뱶 �� �뿉�꽌 CM �쑝濡쒕��꽣 諛쏆� Jtapi �씠踰ㅽ듃瑜� 泥섎━�븳�떎. */
			XMLService xmlservice = new XMLService(queue);
			xmlservice.startService();
			
			// xmlservice.stopService();
			
	}
	
	public void testMode() {
		
		System.out.println("Start Test Mode!!  Active Mode[" + DuplexMgr.getInstance().getActiveMode() +"]");
		/*
		 * ///////////////////////////////////////////////
		 * 1. CM AXL Device �젙蹂� 媛��졇�삤湲� , Jtapi �뿰�룞
		 */// ////////////////////////////////////////////
		
		IQueue queue = new JQueue(); // �씠踰ㅽ듃 �뜲�씠�꽣 泥섎━ �걧
		/* CM Jtapi �뿰�룞 (Device �젙蹂� 媛��졇�삤湲�, Device �젙蹂� DB Insert �븯湲�) */
		
		JtapiService service = JtapiService.getInstance();
		service.startService(queue);
		
		System.out.println("## Success!! Jtapi Service Started ##");
		
		/*
		 * ///////////////////////////////////////////////
		 * 3. Jtapi �씠踰ㅽ듃 泥섎━
		 */// ////////////////////////////////////////////
		
		/* 70媛쒖쓽 �뒪�젅�뱶瑜� 愿�由ы븯�뒗 �뒪�젅�뱶 �� �뿉�꽌 CM �쑝濡쒕��꽣 諛쏆� Jtapi �씠踰ㅽ듃瑜� 泥섎━�븳�떎. */
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
