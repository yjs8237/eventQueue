package com.isi.handler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.sql.Connection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import org.json.JSONObject;

import com.bestez.common.tr.HostWebtIO;
import com.bestez.common.vo.CustInfoVO;
import com.cisco.jtapi.extensions.CiscoTerminal;
import com.isi.constans.*;
import com.isi.data.*;
import com.isi.db.DBConnMgr;
import com.isi.db.JDatabase;
import com.isi.exception.ExceptionUtil;
import com.isi.file.ILog;
import com.isi.file.LogMgr;
import com.isi.file.LogWriter;
import com.isi.file.PropertyRead;
import com.isi.process.DBQueueMgr;
import com.isi.service.JtapiService;
import com.isi.thread.LoginProcess;
import com.isi.vo.*;

/**
*
* @author greatyun
*/
public class XMLHandler {
	
	private static final int CALL_RING = 0;
	private static final int CALL_CONNECT = 2;
	private static final int CALL_HANGUP = 3;
    
    
	private Employees 		employees;
	private PropertyRead 	pr;
	private LogMgr 			m_Log;
	private PushHandler 	pushHandler;
	private XMLData 		xmlData;
	private String 			custInfoPopupYN;
	private String			threadID;
	
	public XMLHandler() {
		employees = Employees.getInstance();	// 직원정보 관리 객체 - 싱글톤타입 
		pr = PropertyRead.getInstance();
		m_Log = LogMgr.getInstance();
		xmlData = new XMLData();
		custInfoPopupYN = XmlInfoMgr.getInstance().getCustinfoPopupYN();
	}
	
	public XMLHandler(JDatabase dataBase, String threadID) {
		employees = Employees.getInstance();	// 직원정보 관리 객체 - 싱글톤타입 
		pr = PropertyRead.getInstance();
		m_Log = LogMgr.getInstance();
		xmlData = new XMLData();
		this.threadID = threadID;
		custInfoPopupYN = XmlInfoMgr.getInstance().getCustinfoPopupYN();
	}
	
	public void pushToPhone(){
		
	}
	
	// Ring Push
	public int evtRing(XmlVO xmlInfo , String callID) {		// Ring 이 울릴 경우
		
		int returnCode = -1;	// Http Push 결과 리턴 코드
		
		EmployeeVO employee = null;
		
		if(xmlInfo.getTargetModel() == null || xmlInfo.getTargetModel().isEmpty() || xmlInfo.getTargetModel().equalsIgnoreCase("null")){
			// 팝업 띄우려는 타겟의 디바이스 타입이 없을 경우
			m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.ERR_LOG, callID, "evtRing", "## TARGET MODEL IS NULL ## ----------> 인사정보 DB 확인해주세요.");
			return RESULT.ERROR;
		}
		
		// 개인 주소록 번호 조회 (개인 주소록 팝업이 우선순위가 높다) 
		
		EmployeeVO myAddressVO = employees.getEmployeeByExtension(xmlInfo.getTargetdn(), callID);
		
		// 휴대전화번호 
		if(xmlInfo.getCallingDn().startsWith("#")) {
			xmlInfo.setCallingDn(xmlInfo.getCallingDn().replaceAll("#", ""));
		}
		xmlInfo.setCallingDn(xmlInfo.getCallingDn().replaceAll("-", ""));
		
		
		if(myAddressVO != null) {
			// 커넥션 획득
			Connection conn = DBConnMgr.getInstance().getConnection(callID);
			MyAddressMgr myAddress=  new MyAddressMgr(conn);
			employee = myAddress.getMyAddressInfo(myAddressVO.getEmp_id(), xmlInfo.getCallingDn(), callID);
			// 커넥션 반납
			DBConnMgr.getInstance().returnConnection(conn , callID);
			
		}
		
		String caller_type = "";
		if(employee != null) {
			// 개인주소록 인입콜 ( remote 서버 이미지 생성을 위해서 extension 컬럼에 calling  번호를 set 한다 )
			// 여기를 세팅안하면.. 이미지 Push 할때 URL 이미지 파일명에 null.png 로 Push 하기 때문에
			employee.setExtension(xmlInfo.getCallingDn());
			caller_type = CALLER_TYPE.MY_ADDRESS;
		} else {
			// 이외 콜
			if(xmlInfo.getCallingDn().length() > 6) {
				employee = employees.getEmployeeByCellNum(xmlInfo.getCallingDn(), callID);
				caller_type = CALLER_TYPE.CELL_PHONE;
			} else {
				employee = employees.getEmployeeByExtension(xmlInfo.getCallingDn() , callID);
				caller_type = CALLER_TYPE.EXTENSION;
			}
		}
        
		if(employee != null){
			
			String model = xmlInfo.getTargetModel().trim();
			
			switch (model) {
			
			
			case IPPhone.PHONE_7902:
				returnCode = pushText(employee, xmlInfo , callID);
				break;
			
			case IPPhone.PHONE_7911:
				returnCode = pushText(employee, xmlInfo , callID);
				break;
				
			case IPPhone.PHONE_7921:
				returnCode = pushImage(employee, xmlInfo , callID , caller_type);
				break;
			
			case IPPhone.PHONE_7925:
				returnCode = pushImage(employee, xmlInfo , callID , caller_type);
				break;
				
			case IPPhone.PHONE_7926:
				returnCode = pushImage(employee, xmlInfo , callID , caller_type);
				break;
				
			case IPPhone.PHONE_7931:
				returnCode = pushText(employee, xmlInfo , callID );
				break;
				
			case IPPhone.PHONE_7941:
				returnCode = pushImage(employee, xmlInfo , callID , caller_type);
				break;
			
			case IPPhone.PHONE_7942:
				returnCode = pushImage(employee, xmlInfo , callID , caller_type);
				break;
			
			case IPPhone.PHONE_7945:
				returnCode = pushImage(employee, xmlInfo , callID , caller_type);
				break;
				
			case IPPhone.PHONE_7961:
				returnCode = pushImage(employee, xmlInfo , callID , caller_type);
				break;
				
			case IPPhone.PHONE_7965:
				returnCode = pushImage(employee, xmlInfo , callID , caller_type);
				break;
				
			case IPPhone.PHONE_7970:
				returnCode = pushImage(employee, xmlInfo , callID , caller_type);
				break;
				
			case IPPhone.PHONE_7971:
				returnCode = pushImage(employee, xmlInfo , callID , caller_type);
				break;
				
			case IPPhone.PHONE_7975:
				returnCode = pushImage(employee, xmlInfo , callID , caller_type);
				break;
			
			case IPPhone.PHONE_8841:
				returnCode = pushImage(employee, xmlInfo , callID , caller_type);
				break;
				
			case IPPhone.PHONE_8851:
				returnCode = pushImage(employee, xmlInfo , callID , caller_type);
				break;
				
			case IPPhone.PHONE_IPCOMMUNICATOR:
				returnCode = pushImage(employee, xmlInfo , callID , caller_type);
				break;
				
			case IPPhone.PHONE_9971:
				returnCode = pushImage(employee, xmlInfo , callID , caller_type);
//				returnCode = pushText(person, xmlInfo , callID);
				break;
		
			default:
				m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.ERR_LOG, callID, "evtRing", "## THIS Phone Model is not supported !! ## [" + model + "]");
				break;
			}
			
		} else {
			m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, callID, "evtRing", "There is no employee information!! DN[" + xmlInfo.getCallingDn() +"]MAC["+xmlInfo.getTerminal()+"]");
		}
		
		return returnCode;
		
	}
	
	public CustomerVO getCustomerInfoV2(String callingDn, String callID) {
		// TODO Auto-generated method stub
		m_Log.standLog(callID, "getCustomerInfoV2", "고객정보 요청 !! " + callingDn);
		
		SocketAddress address = new InetSocketAddress("192.168.201.54", 9999);
		Socket sock = new Socket();
		
		CustomerVO customerVO = new CustomerVO();
		
		try{
			sock.setSoTimeout(3000);
			sock.connect(address, 3000);
			
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()));
//			System.out.println("sendData -> " + callingDn);
			pw.println(callingDn);
			pw.flush();
			
			BufferedReader br = new BufferedReader(new InputStreamReader(sock.getInputStream() , "utf-8"));
			String line = "";
			
			while((line = br.readLine()) != null) {
				if(line.length() < 5){
					break;
				}
				String [] arr = line.split("\\^");
				customerVO.setPhoneNum(callingDn);
				customerVO.setCustLevel(arr[0]);
				customerVO.setName(arr[1]);
				customerVO.setCustNo(arr[2]);
			}
			
			pw.close();
			br.close();
		}catch(Exception e) {
			
		} finally {
			try {
				sock.close();
			} catch(Exception e) {}
			
		}
		
		
		m_Log.standLog(callID, "getCustomerInfoV2", "고객정보 리턴 !! " + customerVO.toString());
//		System.out.println(customerVO.toString());
//		customerVO.setCustLevel((String)retTable.get("A0_CustGradNm"));
//		customerVO.setName((String)retTable.get("A0_CustNm"));
//		customerVO.setCustNo((String)retTable.get("A0_CustNo"));
		
		if(customerVO.getName() == null || customerVO.getName().isEmpty()) {
			customerVO = null;
			return customerVO;
		}
		
		if(customerVO.getName().equalsIgnoreCase("NULL") || customerVO.getCustLevel().equalsIgnoreCase("NULL")
				|| customerVO.getCustNo().equalsIgnoreCase("NULL")) {
			customerVO = null;
		}
		return customerVO;
	}
	
	private CustomerVO getCustomerInfo(String callingDN) {
		// TODO Auto-generated method stub
		HostWebtIO hostIO = new HostWebtIO();
		
		CustInfoVO custInfo = new CustInfoVO();
		custInfo.setAccountNum("");
		custInfo.setAccountPwd("");
		custInfo.setCustID("");
		
		String guBn = "";
		
		if(callingDN.startsWith("9")) {
			callingDN = callingDN.substring(1);
		}
		
		if(callingDN.startsWith("01")) {
			guBn = "1";
		} else {
			guBn = "2";
		}
		
		Hashtable body = new Hashtable();
		body.put("0", guBn);
		body.put("1", getDivideANI(callingDN, 1));
		body.put("2", getDivideANI(callingDN, 2));
		body.put("3", getDivideANI(callingDN, 3));
		
		CustomerVO customerVO = new CustomerVO();
		
		Hashtable retTable = hostIO.hostIOSend("A90OI01", "", "", "", custInfo, body);
		
		m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, threadID, "getCustomerInfo", "GET CUMSTOMER INFO[" + retTable.toString()+"]");
		
		if(retTable == null || !retTable.get("RET").equals("0")){
			return null;
		}
		
		if(retTable.get("A0_CustNm") == null || retTable.get("A0_CustNo") == null 
				|| retTable.get("A0_CustGradNm") == null) {
			return null;
		}
		
		customerVO.setPhoneNum(callingDN);
		customerVO.setCustLevel((String)retTable.get("A0_CustGradNm"));
		customerVO.setName((String)retTable.get("A0_CustNm"));
		customerVO.setCustNo((String)retTable.get("A0_CustNo"));
		return customerVO;
	}

	private String getDivideANI(String callingDN, int index) {
		// TODO Auto-generated method stub
		if(callingDN == null || callingDN.isEmpty() || callingDN.length() < 9) {
			return "";
		}
		
		String firstNum = "";
		String secondNum = "";
		String lastNum = "";
		
		if(callingDN.startsWith("9")) {
			callingDN = callingDN.substring(1);
		}
		
		if(callingDN.startsWith("01")) {
			// 핸드폰 번호
			firstNum = callingDN.substring(0, 3);
			callingDN = callingDN.substring(3);
			if(callingDN.length() >= 8) {
				secondNum = callingDN.substring(0, 4);
				lastNum = callingDN.substring(4);
			} else {
				secondNum = callingDN.substring(0, 3);
				lastNum = callingDN.substring(3);
			}
					
		} else {
			// 일반 번호의 경우
			if(callingDN.startsWith("02")) {
				// 서울전화번호
				firstNum = callingDN.substring(0, 2);
				callingDN = callingDN.substring(2);
				if(callingDN.length() >= 8){
					secondNum = callingDN.substring(0, 4);
					lastNum = callingDN.substring(4);
				} else {
					secondNum = callingDN.substring(0, 3);
					lastNum = callingDN.substring(3);
				}
			} else {
				firstNum = callingDN.substring(0, 3);
				callingDN = callingDN.substring(3);
				if(callingDN.length() >= 8){
					secondNum = callingDN.substring(0, 4);
					lastNum = callingDN.substring(4);
				} else {
					secondNum = callingDN.substring(0, 3);
					lastNum = callingDN.substring(3);
				}
			}
		}
		
		switch (index) {
		case 1:
			return firstNum;
		case 2:
			return secondNum;
		case 3:
			return lastNum;
		default:
			break;
		}
		
		return "";
	}

	public int evtEstablished(XmlVO xmlInfo , String callingDN , String callID){	// 전화를 받았을 경우
		
		int returnCode = -1;	// Http Push 결과 리턴 코드
		
		EmployeeVO person;
		
		
		// 고객정보 팝업 여부 설정이 Y 인 경우에만 고객정보 테이블 Select 한다. 쓸데없는 트랜잭션 유발 금지
		if (custInfoPopupYN.equals("Y")) {
			if (xmlInfo.getCallingDn().length() > 6) {
				// 고객정보는 전문을 통해서 가져온다.
//				person = dataBase.getCustInfo(callingDN);
				person = null;
			} else {
				person = employees.getEmployeeByExtension(callingDN , callID);
			}
		} else {
			person = employees.getEmployeeByExtension(callingDN , callID);
		}
				
		
//		EmployeeVO employee = employees.getEmployee(callingDN);
		
		if(person != null){
			
//			pushHandler.push(xmlData.getMenuInit(), xmlInfo, false);
			
			String model = xmlInfo.getTargetModel().trim();
			
			switch (model) {
			
			case IPPhone.PHONE_6921:
				returnCode = pushText(person, xmlInfo , callID);
				break;
				
			case IPPhone.PHONE_6941:
				returnCode = pushText(person, xmlInfo , callID);
				break;
				
			case IPPhone.PHONE_6961:
				returnCode = pushText(person, xmlInfo , callID);
				break;
				
			case IPPhone.PHONE_7821:
				pushHandler = new PushHandler(threadID);
				// 7821 전화기는 Establish 일때 XML 창을 덮어버린다.. 그래서 .. MENUINIT push 한번 해주고
//				pushHandler.push(xmlData.getMenuInit(), xmlInfo, false);
				pushHandler.sendTerminalPush(xmlData.getMenuInit(), xmlInfo);
				returnCode = pushText(person, xmlInfo , callID);
				break;
				
			case IPPhone.PHONE_7911:
				returnCode = pushText(person, xmlInfo , callID);
				break;
				
			case IPPhone.PHONE_7912:
				returnCode = pushText(person, xmlInfo , callID);
				break;
				
			case IPPhone.PHONE_7931:
				returnCode = pushText(person, xmlInfo , callID);
				break;
				
			case IPPhone.PHONE_7941:
//				returnCode = pushImage(person, xmlInfo , callID);
				break;
				
			case IPPhone.PHONE_7942:
//				returnCode = pushImage(person, xmlInfo , callID);
				break;
				
			case IPPhone.PHONE_7945:
//				returnCode = pushImage(person, xmlInfo , callID);
				break;
				
			case IPPhone.PHONE_7962:
//				returnCode = pushImage(person, xmlInfo , callID);
				break;
				
			case IPPhone.PHONE_7965:
//				returnCode = pushImage(person, xmlInfo , callID);
				break;
				
			case IPPhone.PHONE_7970:
//				returnCode = pushImage(person, xmlInfo , callID);
				break;
				
			case IPPhone.PHONE_7975:
//				returnCode = pushImage(person, xmlInfo , callID);
				break;
				
			case IPPhone.PHONE_8841: 
				pushHandler = new PushHandler(callID);
				// 8841 전화기는 Establish 일때 이미지 팝업을 통화상태 표시창이 덮어버린다.. 그래서 .. MENUINIT push 한번 해주고 이미지 팝업 한다. 
//				pushHandler.push(xmlData.getMenuInit(), xmlInfo, false);
				pushHandler.sendTerminalPush(xmlData.getMenuInit(), xmlInfo);
				
//				returnCode = pushImage(person, xmlInfo , callID);
				break;
				
			case IPPhone.PHONE_9951:
//				returnCode = pushImage(person, xmlInfo , callID);
				break;
			
			case IPPhone.PHONE_9971:
//				returnCode = pushImage(person, xmlInfo , callID);
				break;
				
				
			case IPPhone.PHONE_IPCOMMUNICATOR:
//				returnCode = pushImage(person, xmlInfo , callID);
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
			m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, threadID, "evtEstablished", "There is no employee information!!");
		}
		
		return returnCode;
		
	}
	
	
	
	
	
	public int evtDisconnect(XmlVO xmlInfo , String callID){	// 전화를 끊었을 경우
		pushHandler = new PushHandler(callID);
		PushResultVO resultVO = new PushResultVO();
//		resultVO = pushHandler.push(xmlData.getMenuInit(), xmlInfo, false);
		resultVO = pushHandler.sendTerminalPush(xmlData.getMenuInit(), xmlInfo);
		return resultVO.getReturnCode();
	}
	
	public int evtDisconnectV2(XmlVO xmlInfo , String callID){	// 전화를 끊었을 경우
		pushHandler = new PushHandler(callID);
		PushResultVO resultVO = new PushResultVO();
//		resultVO = pushHandler.push(xmlData.getInitMessages(), xmlInfo, false);
		resultVO = pushHandler.sendTerminalPush(xmlData.getMenuInit(), xmlInfo);
		return resultVO.getReturnCode();
	}
	
	private int pushImage(EmployeeVO employee, XmlVO xmlInfo , String callID , String caller_type)  { 
		// TODO Auto-generated method stub
		
		PushResultVO resultVO = new PushResultVO();
		ImageHandler imgHandler = new ImageHandler();
		
		ImageVO imageVO = ImageMgr.getInstance().getImageInfo(xmlInfo.getTargetModel());
		
		String folderPath = "";
		String filename = "";
		if(PropertyRead.getInstance().getValue(PROPERTIES.SIDE_INFO).equals("A")) {
			folderPath = XmlInfoMgr.getInstance().getEmp_img_path_A() + imageVO.getImageSize() + "\\"+ caller_type +  "\\";
			filename = xmlInfo.getCallingDn() + ".png";
		} else {
			folderPath = XmlInfoMgr.getInstance().getEmp_img_path_B() + imageVO.getImageSize() + "\\"+ caller_type +  "\\";
			filename = xmlInfo.getCallingDn() + ".png";
		}
		
		
		int returnCode = imgHandler.createImageFile(employee ,xmlInfo.getCallingDn(),  imageVO, callID , folderPath , filename , caller_type);
		if (returnCode == 1) {
			// 생성
			m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, threadID, "pushImage", "이미지 생성 이후 이미지 생성 동기화 URL 호출");
			// 이중화 환경의 경우 Push 하기전에 remote 서버에게 로그인 시도 정보 전송 
			if (XmlInfoMgr.getInstance().getDuplexYN().equalsIgnoreCase("Y")) {
				
				 //* Socket 수정 원복
				LoginProcess loginProc = new LoginProcess(makeJsonData(employee , caller_type, xmlInfo.getCallingDn()) ,  "IMAGESYNC" , threadID);
				loginProc.start();
				
				/*
				HttpUrlHandler urlHandler = new HttpUrlHandler(employee, callID , xmlInfo.getCallingDn() , caller_type );
				urlHandler.setImageParameter(employee , caller_type, xmlInfo.getCallingDn());
				try {
					urlHandler.sendImageUrl();
				} catch (Exception e) {
			            e.printStackTrace(ExceptionUtil.getPrintWriter());
			            m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.ERR_LOG, threadID, "pushImage", ExceptionUtil.getStringWriter().toString());
				}
				*/
				
			}
			
			// Push
			pushHandler = new PushHandler(callID);
//			resultVO = pushHandler.push(xmlData.getCiscoIPPhoneImageFile("Ringing", employee, CALL_RING, xmlInfo.getTargetModel() ,  caller_type ,   xmlInfo.getCallingDn()), xmlInfo,false);
			resultVO = pushHandler.sendTerminalPush(xmlData.getCiscoIPPhoneImageFile("Ringing", employee, CALL_RING, xmlInfo.getTargetModel() ,  caller_type ,   xmlInfo.getCallingDn()), xmlInfo);
			
			DBQueueMgr.getInstance().addPopUpData(xmlInfo.getCallingDn(), xmlInfo.getCalledDn(), resultVO.getPopup_yn(),
					employee, xmlInfo.getTargetIP(),  resultVO.getResultMsg());
		} else if (returnCode == 0) {
			// 이미존재
			pushHandler = new PushHandler(callID);
//			resultVO = pushHandler.push(xmlData.getCiscoIPPhoneImageFile("Ringing", employee, CALL_RING, xmlInfo.getTargetModel() ,caller_type, xmlInfo.getCallingDn()), xmlInfo,false);
			resultVO = pushHandler.sendTerminalPush(xmlData.getCiscoIPPhoneImageFile("Ringing", employee, CALL_RING, xmlInfo.getTargetModel() ,  caller_type ,   xmlInfo.getCallingDn()), xmlInfo);
			DBQueueMgr.getInstance().addPopUpData(xmlInfo.getCallingDn(), xmlInfo.getCalledDn(), resultVO.getPopup_yn(),
					employee, xmlInfo.getTargetIP(),resultVO.getResultMsg());
		} else {
			// 실패
			DBQueueMgr.getInstance().addPopUpData(xmlInfo.getCallingDn(), xmlInfo.getCalledDn(), "N", employee,
					xmlInfo.getTargetIP(), "cannot make image file of calling employee info");
			m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, threadID, "pushImage", "Cannot make Image !!");
			return -1;
		}
		
		return resultVO.getReturnCode(); 
	}
	
	
	
	private int pushText(EmployeeVO employee, XmlVO xmlInfo , String callID ){
		pushHandler = new PushHandler(callID);
		PushResultVO resultVO = new PushResultVO();
//		resultVO = pushHandler.push(xmlData.getCiscoIPPhoneText("Calling !!", employee), xmlInfo, false);
		resultVO = pushHandler.sendTerminalPush(xmlData.getCiscoIPPhoneText("Calling !!", employee), xmlInfo);
		DBQueueMgr.getInstance().addPopUpData(xmlInfo.getCallingDn(), xmlInfo.getCalledDn(), resultVO.getPopup_yn(), employee ,xmlInfo.getTargetIP(), resultVO.getResultMsg());
		return resultVO.getReturnCode();
	}
	private JSONObject makeJsonData(EmployeeVO employee, String caller_type, String callingDn) {
		JSONObject jsonObject = new JSONObject();
		
		jsonObject.put("caller_type", caller_type);
		jsonObject.put("callingNumber", callingDn);
		jsonObject.put("emp_id", employee.getEmp_id());
		jsonObject.put("emp_lno", employee.getEmp_lno());
		jsonObject.put("emp_nm_kor", employee.getEmp_nm_kor());
		jsonObject.put("emp_nm_eng", employee.getEmp_nm_eng());
		jsonObject.put("org_nm", employee.getOrg_nm());
		jsonObject.put("pos_nm", employee.getPos_nm());
		jsonObject.put("duty_nm", employee.getDuty_nm());
		jsonObject.put("extension", employee.getExtension());
		jsonObject.put("email", employee.getEmail());
		jsonObject.put("cell_no", employee.getCell_no());
		jsonObject.put("building", employee.getBuilding());
		jsonObject.put("floor", employee.getFloor());
		jsonObject.put("emp_stat_nm", employee.getEmp_stat_nm());
		jsonObject.put("emp_div_cd_nm", employee.getEmp_div_cd_nm());
		jsonObject.put("popup_svc_yn", employee.getPopup_svc_yn());
		jsonObject.put("mac_address", employee.getMac_address());
		jsonObject.put("device_ipaddr", employee.getDevice_ipaddr());
		jsonObject.put("device_type", employee.getDevice_type());
		jsonObject.put("cm_ver", employee.getCm_ver());
		jsonObject.put("cm_ip", employee.getCm_ip());
		jsonObject.put("cm_user", employee.getCm_user());
		jsonObject.put("cm_pwd", employee.getCm_pwd());
		return jsonObject;
	}

	
}
