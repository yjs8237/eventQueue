package com.isi.main;

import java.io.File;
import java.net.HttpURLConnection;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;

import com.isi.constans.PROPERTIES;
import com.isi.constans.RESULT;
import com.isi.data.Employees;
import com.isi.data.ImageMgr;
import com.isi.data.XmlInfoMgr;
import com.isi.db.JDatabase;
import com.isi.duplex.*;
import com.isi.file.GLogWriter;
import com.isi.file.ILog;
import com.isi.file.PropertyRead;
import com.isi.handler.HttpServerHandler;
import com.isi.handler.HttpUrlHandler;
import com.isi.process.IQueue;
import com.isi.process.JQueue;
import com.isi.service.JtapiService;
import com.isi.service.UDPThread;
import com.isi.thread.ImageService;
import com.isi.thread.UDPService;
import com.isi.thread.XMLService;
import com.isi.utils.Utils;
import com.isi.vo.CMInfo;
import com.test.thread.TestThread;

/**
*
* @author greatyun
*/
public class XMLSvcMain {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		PropertyRead pr = PropertyRead.getInstance();
		DuplexMgr duplexMgr = DuplexMgr.getInstance();
		
		JDatabase database = new JDatabase("XMLSvcMain");
		database.connectDB(pr.getValue(PROPERTIES.DB_CLASS), pr.getValue(PROPERTIES.DB_URL), pr.getValue(PROPERTIES.DB_USER), pr.getValue(PROPERTIES.DB_PASSWORD));
		// 전화기정보 SELECT
		database.selectImageInfoByModel(pr.getValue(PROPERTIES.QUERY_DEVICEINFO));
		// XML 환경설정  SELECT
		database.selectXMLInfo(pr.getValue(PROPERTIES.QUERY_XMLINFO));
		
		// 오래된 로그파일 삭제
		XMLSvcMain svcMain = new XMLSvcMain();
		svcMain.delOldLogFiles();
		
		database.disconnectDB();
		
		
		
		HttpServerHandler httpHandler = new HttpServerHandler();
		httpHandler.startService();
		
		
		/*
		CMInfo cmInfo = CMInfo.getInstance();
		cmInfo.setCmUser(pr.getValue(PROPERTIES.CM1_USER));
		cmInfo.setCmPassword(pr.getValue(PROPERTIES.CM1_PASSWORD));
		*/
		
		/*
		 * ///////////////////////////////////////////////
		 * 
		 * 2. DB 직원정보 가져오기
		 * 메인 스레드에서 실행하지 않고 스레드를 새로 생성하여 실행한다.
		 */// ////////////////////////////////////////////
		
		Employees employees = Employees.getInstance();
		// 최초 직원정보 메모리 업로드
		if (employees.getEmployeeList() != RESULT.RTN_SUCCESS) {
			System.out.println("!! ERROR !! [getEmployeeList]");
//			System.exit(0);
		}
		
		ImageService imgSvrThread = new ImageService();
		imgSvrThread.start();
		
		ILog logwrite = new GLogWriter();
		
		ProcessMain main = new ProcessMain();
		
		
		if(!XmlInfoMgr.getInstance().getXmlMode().equalsIgnoreCase("Y")){
			
			main.ispsMode(); // ISPS 에게 UDP 패킷을 받는 모드
		
		} else {
			
			if(XmlInfoMgr.getInstance().getDuplexYN().equalsIgnoreCase("Y")){
				
				duplexMgr.setDuplexMode(true);
				
				ServerSocketEx server = new DuplexServerSocket(Integer.parseInt(XmlInfoMgr.getInstance().getRemotePort()));
				server.startServer();
				
				AliveProc alive = new AliveProc();
				int result = alive.startAliveProc(); 
				if(result == RESULT.RTN_SUCCESS){
					logwrite.duplexLog(duplexMgr.getActiveMode(), "XMLSvcMain main()",  "START STANDBY MODE !!");
					duplexMgr.setStandByMode();
				} else { //
					if(result == RESULT.TCP_CONN_FAIL){
						logwrite.duplexLog(duplexMgr.getActiveMode(), "XMLSvcMain main()",  "REMOTE Server Connection Fail..");
						logwrite.duplexLog(duplexMgr.getActiveMode(), "XMLSvcMain main()",  "START ACTIVE MODE !!");
						System.out.println("TCP Connection FAIL!!");
						duplexMgr.setActiveMode();
					}
				}
				
			} else { // git test
				System.out.println("Stand Alone Mode");
				duplexMgr.setDuplexMode(false);
				duplexMgr.setActiveMode();
			}
			
			while (true) {
				
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				// 이중화모드 이고 Active 모드 || 이중화 모드가 아닌 경우 ( 결론은 Active )
				if (duplexMgr.getActiveMode()) {
					
					System.out.println("Active Mode 시작!!!!");
					
					logwrite.duplexLog(duplexMgr.getActiveMode(), "XMLSvcMain main()",  "Start Active Mode !! ");
//					main.testMode();	// 부하테스트 모드
					main.singleMode(); // ISPS 없는 싱글모드
					break;
				} else {
//					System.out.println("Standby Mode 시작!!!!");
				}
			}	// end while
			
		}
		
	}
	
	private void delOldLogFiles() {
		// TODO Auto-generated method stub
		
		String logPath = XmlInfoMgr.getInstance().getLogPath();
		
		int days = XmlInfoMgr.getInstance().getLogDelDays();
		
		try{
			
		deleteFiles(logPath, days);
//		deleteFiles(middleLogPath, days);	
			
		}catch(Exception e){
			
		}
		
	}

	private void deleteFiles(String logPath, int days) throws Exception{
		// TODO Auto-generated method stub
		
		File logfiles = new File(logPath);
		File[] files = logfiles.listFiles();
		
		Date fileDate;
		Calendar cal = Calendar.getInstance() ;
		long todayMil = cal.getTimeInMillis() ;     // 현재 시간(밀리 세컨드)
		long oneDayMil = 24*60*60*1000 ;            // 일 단위
		Calendar fileCal = Calendar.getInstance() ;
		Utils util = new Utils();
		
		for (File file : files) {
			
			fileDate = new Date(file.lastModified());
			fileCal.setTime(fileDate);
			long diffMil = todayMil - fileCal.getTimeInMillis() ;
			//날짜로 계산
			int diffDay = (int)(diffMil/oneDayMil) ;
			
			System.out.println(file.getName() + " " + fileDate.toString() + " " + String.valueOf(diffDay));
			
			// 3일이 지난 파일 삭제
			if(diffDay > days && file.exists()){
				util.deleteDirectory(file);
				System.out.println(days + "일 지난 로그파일을 삭제했습니다.");
			}
			
			file.delete();
			
		}
	}
	
}
