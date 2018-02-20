package com.test.handler;

import com.isi.constans.*;
import com.isi.data.*;
import com.isi.db.JDatabase;
import com.isi.file.ILog;
import com.isi.file.LogMgr;
import com.isi.file.LogWriter;
import com.isi.file.PropertyRead;
import com.isi.handler.ImageHandler;
import com.isi.handler.PushHandler;
import com.isi.vo.*;

/**
*
* @author greatyun
*/
public class TestXMLHandler {
	
	private static final int CALL_RING = 0;
	private static final int CALL_CONNECT = 2;
	private static final int CALL_HANGUP = 3;
    
    
	private Employees 		employees;
	private PropertyRead 	pr;
	private TestPushHandler 	pushHandler;
	private XMLData 		xmlData;
	private JDatabase		dataBase;
	private String 			custInfoPopupYN;
	private String 			threadID;
	
	public TestXMLHandler(JDatabase dataBase) {
		employees = Employees.getInstance();	// 직원정보 관리 객체 - 싱글톤타입 
		pr = PropertyRead.getInstance();
		xmlData = new XMLData();
		this.dataBase = dataBase;
		custInfoPopupYN = XmlInfoMgr.getInstance().getCustinfoPopupYN();
	}
	
	public void pushToPhone(){
		
	}
	
	
	
	
	public int evtDisconnect(XmlVO xmlInfo){	// 전화를 끊었을 경우
		pushHandler = new TestPushHandler();
		
		int returnCode = pushHandler.push(xmlData.getMenuInit(), xmlInfo, false);
		
		return returnCode;
	}
	
	private int pushImage(IPerson person, XmlVO xmlInfo) {
		// TODO Auto-generated method stub
		int returnCode = -1;
		TestImageHandler imgHandler = new TestImageHandler();
		if(imgHandler.createImageFile(person , xmlInfo.getTargetModel() , threadID)) {
			// 이미지 생성
			pushHandler = new TestPushHandler();
			returnCode = pushHandler.push(xmlData.getCiscoIPPhoneImageFile("Calling !! " , person , CALL_RING , xmlInfo.getTargetModel()), xmlInfo, false);
		} else {
			LogMgr.getInstance().write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, xmlInfo.getCallidByString(), "pushRing", "Cannot make Image !!");
		}
		
		return returnCode;
	}
	
	
	private int pushText(IPerson person, XmlVO xmlInfo){
		pushHandler = new TestPushHandler();
		return pushHandler.push(xmlData.getCiscoIPPhoneText("Calling !!", person), xmlInfo, false);
		
	}
	

	
}
