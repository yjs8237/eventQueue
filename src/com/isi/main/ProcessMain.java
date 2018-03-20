package com.isi.main;

import java.util.List;

import com.isi.constans.RESULT;
import com.isi.data.Employees;
import com.isi.duplex.DuplexMgr;
import com.isi.file.ILog;
import com.isi.file.LogMgr;
import com.isi.file.LogWriter;
import com.isi.process.*;
import com.isi.process.IQueue;
import com.isi.process.JQueue;
import com.isi.service.JtapiService;
import com.isi.service.UDPThread;
import com.isi.thread.DBService;
import com.isi.thread.UDPService;
import com.isi.thread.XMLService;
import com.test.thread.TestService;
import com.test.thread.TestThread;

import kr.co.insunginfo.EventHandlerTest;
import kr.co.insunginfo.jtapi.EventGroup;
import kr.co.insunginfo.jtapi.JTapiManager;
import kr.co.insunginfo.jtapi.JtapiConnInfo;
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
		
		UDPThread thread = new UDPThread(queue); //asdasd
		thread.startService();
		
		UDPService udpService = new UDPService(queue);
		udpService.startService();
		
		
	}
	
	public void singleMode() {

		System.out.println("Start Single Mode!!  Active Mode[" + DuplexMgr.getInstance().getActiveMode() + "]");
		
		
		/*
		 * XML 팝업 결과 데이터 적재를 위한 DataBase Thread Pool (Queue) 구현
		 */
		DBService dbService = new DBService();
		dbService.startService();
		
		/*********************************************************************/
		
		
		IQueue queue = new JQueue();

		JtapiService service = JtapiService.getInstance();
		service.startService(queue);
		
		System.out.println("## Success!! Jtapi Service Started ##");

		XMLService xmlservice = new XMLService(queue);
		xmlservice.startService();
		
		
		// Daemon 구동시 로그인상태였던 내선은 바로 모니터링 시작한다.
		List <String>loginList = Employees.getInstance().getInitLoginExtlist();
		for (int i = 0; i < loginList.size(); i++) {
			String extenion = loginList.get(i);
			service.monitorStart(extenion);
		}
		 
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
