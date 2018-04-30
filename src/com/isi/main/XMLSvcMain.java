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
import com.isi.db.DBConnMgr;
import com.isi.db.JDatabase;
import com.isi.duplex.*;
import com.isi.file.GLogWriter;
import com.isi.file.ILog;
import com.isi.file.PropertyRead;
import com.isi.handler.HttpServerHandler;
import com.isi.handler.HttpSyncServer;
import com.isi.handler.HttpUrlHandler;
import com.isi.handler.ServerSocketHandler;
import com.isi.process.*;
import com.isi.process.IQueue;
import com.isi.process.JQueue;
import com.isi.service.JtapiService;
import com.isi.service.UDPThread;
import com.isi.test.AxlTest;
import com.isi.thread.DBService;
import com.isi.thread.ImageService;
import com.isi.thread.UDPService;
import com.isi.thread.XMLService;
import com.isi.utils.Utils;
import com.isi.vo.CMInfo;
import com.test.axl.soap.Text2Base64;
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
		
		// XML 팝업로그 과거 2주 데이터  삭제
		database.deletePopUpLog();
		
		
		// 오래된 로그파일 삭제
		XMLSvcMain svcMain = new XMLSvcMain();
		svcMain.delOldLogFiles();
		database.disconnectDB();
		
		
		// DB 커넥션 풀 생성
		DBConnMgr.getInstance().setDb_class(pr.getValue(PROPERTIES.DB_CLASS));
		DBConnMgr.getInstance().setDb_url(pr.getValue(PROPERTIES.DB_URL));
		DBConnMgr.getInstance().setDb_user(pr.getValue(PROPERTIES.DB_USER));
		DBConnMgr.getInstance().setDb_pwd(pr.getValue(PROPERTIES.DB_PASSWORD));
		DBConnMgr.getInstance().initialConnection();
		
		
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
		
		// 로그인시 동기화 상대 서버에게 Http Get Request 받는 서버 (이미지 생성을 위하여)
		
		
		 // Socket 수정 원복
		ServerSocketHandler syncServer = new ServerSocketHandler(XmlInfoMgr.getInstance().getHttp_sync_port());
		syncServer.startService();
		
		/*
		HttpSyncServer httpSyncServer = new HttpSyncServer();
		httpSyncServer.startService();
		*/
		//////////////////////////////////////////////////////////////////////////
		
		
		
		// 구동시 이미지 파일 전체 삭제 & 생성
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
						System.out.println("REMOTE XML SERVER TCP CONNECTION FAIL!!");
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
					logwrite.standLog("", "main", "Start Active Mode !! ");
					logwrite.duplexLog(duplexMgr.getActiveMode(), "XMLSvcMain main()",  "Start Active Mode !! ");
					main.singleMode(); // ISPS 없는 싱글모드
					
					
					// * Socket 수정 원복
					ServerSocketHandler serverHandler = new ServerSocketHandler(XmlInfoMgr.getInstance().getHttpPort());
					serverHandler.startService();
					serverHandler.start();
					
					/*
					HttpServerHandler httpHandler = new HttpServerHandler();
					httpHandler.startService();
					*/
					//////////////////////////////////////////////////////////////////////////////////
					logwrite.standLog("", "main", "Start HTTP Handler success !! ");
					
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
	
	
	
	private void test() {
		String urlIP = "10.156.214.111";
		int urlPort = 8443;
		String ver = "8.5";
		String id = "SAC_IPT";
		String pwd = "dkdlvlxl123$";
		String auth = id + ":" + pwd;
		String m_auth = Text2Base64.getBase64(auth);
		 
		StringBuffer queryBuffer = new StringBuffer();
		queryBuffer
		.append("select ")
		.append("	n.pkid fknumplan, n.dnorpattern, n.cfnaduration, n.cfnavoicemailenabled, n.cfnaintdestination, n.cfnaintvoicemailenabled, n.cfnadestination ")
		.append("	, cfd.cfavoicemailenabled, cfd.cfadestination ")
		.append("from ")
		.append("	numplan n, callforwarddynamic cfd ")
		.append("where ")
		.append("	n.tkpatternusage IN (1, 2) ")
		.append("	and n.pkid = cfd.fknumplan ");
		
		
//		queryBuffer
//		.append("select ")
//		.append("	d.pkid fkdevice, d.name, d.tkuserlocale, d.tkcountry, d.description ")
//		.append("	, dnm.busytrigger, dnm.e164mask, n.dnorpattern ")
//		.append("	, d.fkdevicepool, d.fksoftkeytemplate, n.fkroutepartition, n.fkcallingsearchspace_sharedlineappear ")
//		.append("	, PICK.pkid as pick_pkid ")
//		.append("from")
//		.append("	device d, devicenumplanmap dnm, numplan n , pickupgrouplinemap PM , pickupgroup PICK ")
//		.append("where ")
//		.append("	d.pkid = dnm.fkdevice and dnm.fknumplan = n.pkid and n.pkid = PM.fknumplan_line and PM.fkpickupgroup = PICK.pkid ");
		
//		queryBuffer.append("select * from numplan where dnorpattern = '1772'");
		
		String xmlBody = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"> <SOAP-ENV:Body> \r\n" + 
				"<axlapi:executeSQLQuery xmlns:axlapi=\"http://www.cisco.com/AXL/API/8.5\" sequence=\"1234\"> \r\n" + 
				"<sql>\r\n" + 
				queryBuffer.toString() + 
				"</sql> \r\n" + 
				"</axlapi:executeSQLQuery> \r\n" + 
				"</SOAP-ENV:Body> \r\n" + 
				"</SOAP-ENV:Envelope>";
		
		
		
		
		StringBuffer soapHeader = new StringBuffer();
		soapHeader.append("POST https://").append(urlIP).append(":").append(urlPort).append("/axl/ HTTP/1.1").append("\n");
		soapHeader.append("Accept-Encoding: gzip,deflate").append("\n");
		soapHeader.append("Content-Type: text/xml;charset=UTF-8").append("\n");
//		soapHeader.append("SOAPAction: \"CUCM:DB ver=").append(ver).append(" executeSQLQuery\"").append("\n");
		soapHeader.append("SOAPAction: \"CUCM:DB ver=").append(ver).append("\n");
		soapHeader.append("Content-Length: ").append(xmlBody.length()).append("\n");	
		soapHeader.append("Host: ").append(urlIP).append(":").append(urlPort).append("\n");
		soapHeader.append("Connection: Keep-Alive").append("\n");
		soapHeader.append("User-Agent: Apache-HttpClient/4.1.1 (java 1.5)").append("\n");
		soapHeader.append("Authorization: Basic ").append(m_auth).append("\n").append("\n");
		
		
		soapHeader.append(xmlBody);
		
//		
		AxlTest axlTest = new AxlTest(urlIP, urlPort, id, pwd);
		String retMsg = axlTest.SendSoapMessageV2(soapHeader.toString(), 10000);
	}
	
	
}
