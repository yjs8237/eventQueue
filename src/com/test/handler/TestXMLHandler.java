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
		custInfoPopupYN = pr.getValue(PROPERTIES.CUSTINFO_POPUP);
	}
	
	public void pushToPhone(){
		
	}
	
	// Ring Push
	public int evtRing(XmlVO xmlInfo, String  threadID) {		// Ring 이 울릴 경우
		
		int returnCode = -1;	// Http Push 결과 리턴 코드
		
		IPerson person;
		
		this.threadID = threadID;
		
		// 고객정보 팝업 여부 설정이 Y 인 경우에만 고객정보 테이블 Select 한다. 쓸데없는 트랜잭션 유발 금지
		if(custInfoPopupYN.equals("Y")){
			if(xmlInfo.getCallingDn().length() > 6){
				person = dataBase.getCustInfo(xmlInfo.getCallingDn());
			} else {
				person = employees.getEmployee(xmlInfo.getCallingDn() , threadID);
			}
		} else {
			person = employees.getEmployee(xmlInfo.getCallingDn() , threadID);
		}
		
        
		if(person != null){
			
			String model = xmlInfo.getTargetModel().trim();
			
			switch (model) {
			
			case IPPhone.PHONE_6921:
				returnCode = pushText(person, xmlInfo);
				break;
				
			case IPPhone.PHONE_6941:
				returnCode = pushText(person, xmlInfo);
				break;
				
			case IPPhone.PHONE_6961:
				returnCode = pushText(person, xmlInfo);
				break;
				
				
			case IPPhone.PHONE_7821:
				returnCode = pushText(person, xmlInfo);
				break;
			
			case IPPhone.PHONE_7911:
				returnCode = pushText(person, xmlInfo);
				break;
				
			case IPPhone.PHONE_7912:
				returnCode = pushText(person, xmlInfo);
				break;
				
			case IPPhone.PHONE_7931:
				returnCode = pushText(person, xmlInfo);
				break;
				
			case IPPhone.PHONE_7941:
				returnCode = pushImage(person, xmlInfo);
				break;
				
			case IPPhone.PHONE_7942:
				returnCode = pushImage(person, xmlInfo);
				break;
				
			case IPPhone.PHONE_7945:
				returnCode = pushImage(person, xmlInfo);
				break;
				
			case IPPhone.PHONE_7962:
				returnCode = pushImage(person, xmlInfo);
				break;
				
			case IPPhone.PHONE_7965:
				returnCode = pushImage(person, xmlInfo);
				break;
				
			case IPPhone.PHONE_7970:
				returnCode = pushImage(person, xmlInfo);
				break;
				
			case IPPhone.PHONE_7975:
				returnCode = pushImage(person, xmlInfo);
				break;
				
			case IPPhone.PHONE_8841:
				returnCode = pushImage(person, xmlInfo);
				break;
				
			case IPPhone.PHONE_9951:
				returnCode = pushImage(person, xmlInfo);
				break;
				
			case IPPhone.PHONE_9971:
				returnCode = pushImage(person, xmlInfo);
				break;
				
			case IPPhone.PHONE_IPCOMMUNICATOR:
				returnCode = pushImage(person, xmlInfo);
				break;
		
			default:
				break;
			}
			
		} else {
			LogMgr.getInstance().write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, xmlInfo.getCallidByString(), "evtRing", "There is no employee information!! DN[" + xmlInfo.getCallingDn() +"]");
		}
		
		return returnCode;
		
	}
	
	public int evtEstablished(XmlVO xmlInfo , String callingDN){	// 전화를 받았을 경우
		
		int returnCode = -1;	// Http Push 결과 리턴 코드
		
		IPerson person;
		
		
		// 고객정보 팝업 여부 설정이 Y 인 경우에만 고객정보 테이블 Select 한다. 쓸데없는 트랜잭션 유발 금지
		if (custInfoPopupYN.equals("Y")) {
			if (xmlInfo.getCallingDn().length() > 6) {
				person = dataBase.getCustInfo(callingDN);
			} else {
				person = employees.getEmployee(callingDN , threadID);
			}
		} else {
			person = employees.getEmployee(callingDN , threadID);
		}
				
		
//		EmployeeVO employee = employees.getEmployee(callingDN);
		
		if(person != null){
			
//			pushHandler.push(xmlData.getMenuInit(), xmlInfo, false);
			
			String model = xmlInfo.getTargetModel().trim();
			
			switch (model) {
			
			case IPPhone.PHONE_6921:
				returnCode = pushText(person, xmlInfo);
				break;
				
			case IPPhone.PHONE_6941:
				returnCode = pushText(person, xmlInfo);
				break;
				
			case IPPhone.PHONE_6961:
				returnCode = pushText(person, xmlInfo);
				break;
				
			case IPPhone.PHONE_7821:
				pushHandler = new TestPushHandler();
				// 7821 전화기는 Establish 일때 XML 창을 덮어버린다.. 그래서 .. MENUINIT push 한번 해주고
				pushHandler.push(xmlData.getMenuInit(), xmlInfo, false);
				returnCode = pushText(person, xmlInfo);
				break;
				
			case IPPhone.PHONE_7911:
				returnCode = pushText(person, xmlInfo);
				break;
				
			case IPPhone.PHONE_7912:
				returnCode = pushText(person, xmlInfo);
				break;
				
			case IPPhone.PHONE_7931:
				returnCode = pushText(person, xmlInfo);
				break;
				
			case IPPhone.PHONE_7941:
				returnCode = pushImage(person, xmlInfo);
				break;
				
			case IPPhone.PHONE_7942:
				returnCode = pushImage(person, xmlInfo);
				break;
				
			case IPPhone.PHONE_7945:
				returnCode = pushImage(person, xmlInfo);
				break;
				
			case IPPhone.PHONE_7962:
				returnCode = pushImage(person, xmlInfo);
				break;
				
			case IPPhone.PHONE_7965:
				returnCode = pushImage(person, xmlInfo);
				break;
				
			case IPPhone.PHONE_7970:
				returnCode = pushImage(person, xmlInfo);
				break;
				
			case IPPhone.PHONE_7975:
				returnCode = pushImage(person, xmlInfo);
				break;
				
			case IPPhone.PHONE_8841: 
				pushHandler = new TestPushHandler();
				// 8841 전화기는 Establish 일때 이미지 팝업을 통화상태 표시창이 덮어버린다.. 그래서 .. MENUINIT push 한번 해주고 이미지 팝업 한다. 
				pushHandler.push(xmlData.getMenuInit(), xmlInfo, false);
				returnCode = pushImage(person, xmlInfo);
				break;
				
			case IPPhone.PHONE_9951:
				returnCode = pushImage(person, xmlInfo);
				break;
			
			case IPPhone.PHONE_9971:
				returnCode = pushImage(person, xmlInfo);
				break;
				
				
			case IPPhone.PHONE_IPCOMMUNICATOR:
				returnCode = pushImage(person, xmlInfo);
				break;
				
			default:
				break;
			} 
			
//			// push 가 성공하면 상태체크를 위하여 푸쉬 결과 상태를  SET 한다.
//			if(returnCode == RESULT.HTTP_SUCCESS){
//				deviceMgr = DeviceMgr.getInstance();
//				DeviceVO dev = new DeviceVO().setDn(xmlInfo.getDn()).setStatus(STATUS.POPUP);
//				deviceMgr.putDevice(dev);
//			} 
			
		} else {
			LogMgr.getInstance().write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, xmlInfo.getCallidByString(), "evtEstablished", "There is no employee information!!");
		}
		
		return returnCode;
		
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
