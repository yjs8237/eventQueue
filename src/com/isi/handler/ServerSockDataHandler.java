package com.isi.handler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.isi.constans.CALLSTATE;
import com.isi.constans.RESULT;
import com.isi.data.CallStateMgr;
import com.isi.data.Employees;
import com.isi.data.ImageMgr;
import com.isi.data.MyAddressMgr;
import com.isi.db.DBConnMgr;
import com.isi.exception.ExceptionUtil;
import com.isi.file.GLogWriter;
import com.isi.file.ILog;
import com.isi.service.JtapiService;
import com.isi.thread.DeviceCheck;
import com.isi.thread.LoginProcess;
import com.isi.thread.MakeCall;
import com.isi.thread.StopCall;
import com.isi.vo.BaseVO;
import com.isi.vo.DeviceResetVO;
import com.isi.vo.DeviceStatusVO;
import com.isi.vo.EmployeeVO;
import com.isi.vo.ImageSyncVO;
import com.isi.vo.JTapiResultVO;
import com.isi.vo.MakeCallVO;
import com.isi.vo.PickupVO;
import com.isi.vo.XmlVO;

public class ServerSockDataHandler extends Thread {

	private Socket	sock;
	private ILog 			logwrite;
	private	String	requestID;
	
	private BufferedReader	buffer_reader;
	private PrintWriter		print_writer;
	
	public ServerSockDataHandler (Socket sock , String requestID) {
		this.sock = sock;
		this.logwrite = new GLogWriter();
		this.requestID = requestID;
	} 
	
	
	public void run () {
		
		try {
			
			buffer_reader 	= new BufferedReader(new InputStreamReader(sock.getInputStream(), "UTF-8"));
			print_writer	= new PrintWriter(new OutputStreamWriter(sock.getOutputStream() , "UTF-8"), true);
			
			String line = "";
			while( (line = buffer_reader.readLine()) != null) {
				logwrite.httpLog(requestID ,"run()", "recv data [" + line + "]");
				String responseData = reqDataHandle(line);
				logwrite.httpLog(requestID ,"run()", "responseData [" + responseData + "]");
				print_writer.println(responseData);
				print_writer.flush();
			}
			
			
		} catch (Exception e) {
			e.printStackTrace(ExceptionUtil.getPrintWriter());
			logwrite.httpLog(requestID ,"run()", ExceptionUtil.getStringWriter().toString());
			
		} finally {
			if(buffer_reader != null) {
				try { buffer_reader.close(); buffer_reader=null; } catch(Exception e) {}
			}
			if(print_writer != null) {
				try { print_writer.close(); print_writer = null;} catch(Exception e) {}
			}
			if(sock != null) {
				try { sock.close(); sock = null;} catch(Exception e) {}
			}
		}
		
	}
	
	private String reqDataHandle(String requestData) {
		
		if(requestData == null || requestData.isEmpty()) {
			logwrite.httpLog(requestID ,"run()", "recv data is null");
			return getErrorJson();
		}
		
		JSONObject jsonObj = new JSONObject(requestData);
		String type = jsonObj.get("type").toString().toUpperCase();
		String responseData = "";
		
		switch (type) {
		
		case "LOGINSYNC" :
			responseData = procCreateImage(jsonObj, requestID);
			break;
			
		case "IMAGESYNC" :
			responseData = procImageSync(jsonObj, requestID);
			break;
			
		case "LOGIN":
			responseData = procLogin(jsonObj , requestID);
			break;
		
		case "LOGOUT" :
			responseData = procLogout(jsonObj , requestID);
			break;
			
		case "RESETDEVICE" :
			responseData = procResetDevice(jsonObj , requestID);
			break;
			
		case "CALLSTATUS" :
			responseData = procCallStatus(jsonObj , requestID);
			break;
			
		case "PICKUP" :
			responseData = procCallPickup(jsonObj, requestID);
			break;
			
		case "MAKECALL" :
			responseData = procMakeCall(jsonObj, requestID);
			break;
			
		case "HANGUP" :
			responseData = procStopCall(jsonObj, requestID);
			break;

		default:
			logwrite.httpLog(requestID ,"run()", "request Type is Unknown TYPE[" + type + "]");
			responseData = getErrorJson();
			break;
		}
		
		return responseData;
	}
	
	private String getErrorJson() {
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("code", String.valueOf(RESULT.HTTP_PARAM_ERROR));
		jsonObj.put("msg", "bad parameter data");
		jsonObj.put("param", "all");
		return jsonObj.toString();
	}
	
	private String procImageSync(JSONObject paramObject, String requestID2) {
		// TODO Auto-generated method stub
		JSONObject jsonObj = new JSONObject();
		
		if(paramObject == null) {
			jsonObj.put("code", String.valueOf(RESULT.HTTP_PARAM_ERROR));
			jsonObj.put("msg", "bad parameter data");
			jsonObj.put("param", "all");
			return jsonObj.toString();
		}
		
		ImageSyncVO imageSyncVO = getImageSyncInfo(paramObject);
		
		// 로그인 시도할때 이미지 삭제 -> 생성 
		ImageMgr imageMgr = ImageMgr.getInstance();
		
		
		// DB Device Type 정보 가져오고 IP 정보 업데이트
		Connection conn = DBConnMgr.getInstance().getConnection(requestID);
		MyAddressMgr myAddress = new MyAddressMgr(conn);
		ArrayList<EmployeeVO> empList = myAddress.getLoginUserList(imageSyncVO.getEmp_id(), requestID);
		// 커넥션 반납
		DBConnMgr.getInstance().returnConnection(conn, requestID);
		
		if(empList != null && empList.size() > 0) {
			imageSyncVO.setOrg_nm(empList.get(0).getOrg_nm());
			imageSyncVO.setPos_nm(empList.get(0).getPos_nm());
		}
		
		imageMgr.createImageSyncFiles( imageSyncVO , requestID);
		
		logwrite.httpLog(requestID, "procImageSync", "Create Image Sync Success!!");
		
		jsonObj.put("code", RESULT.HTTP_SUCCESS);
		jsonObj.put("msg", "success");
		
		return jsonObj.toString();
	}


	private ImageSyncVO getImageSyncInfo(JSONObject paramObject) {
		// TODO Auto-generated method stub
		ImageSyncVO imageSyncVO = new ImageSyncVO();
		imageSyncVO.setCaller_type(paramObject.get("caller_type").toString());
		imageSyncVO.setCallingNumber(paramObject.get("callingNumber").toString());
		imageSyncVO.setEmp_id(paramObject.get("emp_id").toString());
		imageSyncVO.setEmp_lno(paramObject.get("emp_lno").toString());
		imageSyncVO.setEmp_nm_kor(paramObject.get("emp_nm_kor").toString());
		imageSyncVO.setEmp_nm_eng(paramObject.get("emp_nm_eng").toString());
		imageSyncVO.setOrg_nm(paramObject.get("org_nm").toString());
		imageSyncVO.setPos_nm(paramObject.get("pos_nm").toString());
		imageSyncVO.setDuty_nm(paramObject.get("duty_nm").toString());
		imageSyncVO.setExtension(paramObject.get("extension").toString());
		imageSyncVO.setEmail(paramObject.get("email").toString());
		imageSyncVO.setCell_no(paramObject.get("cell_no").toString());
		imageSyncVO.setBuilding(paramObject.get("building").toString());
		imageSyncVO.setFloor(paramObject.get("floor").toString());
		imageSyncVO.setEmp_stat_nm(paramObject.get("emp_stat_nm").toString());
		imageSyncVO.setEmp_div_cd_nm(paramObject.get("emp_div_cd_nm").toString());
		imageSyncVO.setPopup_svc_yn(paramObject.get("popup_svc_yn").toString());
		imageSyncVO.setMac_address(paramObject.get("mac_address").toString());
		imageSyncVO.setDevice_ipaddr(paramObject.get("device_ipaddr").toString());
		imageSyncVO.setDevice_type(paramObject.get("device_type").toString());
		imageSyncVO.setCm_ver(paramObject.get("cm_ver").toString());
		imageSyncVO.setCm_ip(paramObject.get("cm_ip").toString());
		imageSyncVO.setCm_user(paramObject.get("cm_user").toString());
		imageSyncVO.setCm_pwd(paramObject.get("cm_pwd").toString());
		return imageSyncVO;
	}


	private String procCreateImage(JSONObject paramObject, String requestID2) {
		// TODO Auto-generated method stub
		JSONObject jsonObj = new JSONObject();
		
		if(paramObject == null) {
			jsonObj.put("code", String.valueOf(RESULT.HTTP_PARAM_ERROR));
			jsonObj.put("msg", "bad parameter data");
			jsonObj.put("param", "all");
			return jsonObj.toString();
		}
		BaseVO baseVO = getEmployeeInfo(paramObject);

		EmployeeVO empVO = (EmployeeVO) baseVO;
		empVO.setCell_no(empVO.getCell_no().replaceAll("-", ""));
		
		
		// 커넥션 획득
		Connection conn = DBConnMgr.getInstance().getConnection(requestID);
		MyAddressMgr myAddress=  new MyAddressMgr(conn);
		ArrayList<EmployeeVO> loginUserlist = myAddress.getLoginUserList(empVO.getEmp_id(), requestID);
		// 커넥션 반납
		DBConnMgr.getInstance().returnConnection(conn , requestID);
		
		if(loginUserlist == null || loginUserlist.size() == 0) {
			logwrite.httpLog(requestID, "procCreateImage", empVO.getEmp_id() + " 정보가 DB에 존재하지 않습니다.");
		} else {
			EmployeeVO tempVO = loginUserlist.get(0);
			empVO.setOrg_nm(tempVO.getOrg_nm());
			empVO.setPos_nm(tempVO.getPos_nm());
		}
		
		// 로그인 시도할때 이미지 삭제 -> 생성 
		ImageMgr imageMgr = ImageMgr.getInstance();
		imageMgr.createImageFiles( empVO , requestID);
		
		logwrite.httpLog(requestID, "procCreateImage", "Create Login Image Sync Success!!");
		
		jsonObj.put("code", RESULT.HTTP_SUCCESS);
		jsonObj.put("msg", "success");
		
		
		return jsonObj.toString();
	}


	private String procStopCall(JSONObject paramObject, String requestID2) {
		// TODO Auto-generated method stub
		
		JSONObject jsonObj = new JSONObject();

		if (paramObject == null) {
			jsonObj.put("code", String.valueOf(RESULT.HTTP_PARAM_ERROR));
			jsonObj.put("msg", "bad parameter data");
			jsonObj.put("param", "all");
			return jsonObj.toString();
		}

		MakeCallVO makeCallVO = getStopCallInfo(paramObject);
		
		StopCall stopCallProc = new StopCall(makeCallVO, requestID);
		stopCallProc.start();
		
		logwrite.httpLog(requestID, "procStopCall",
				"HANGUP CALL RESULT CODE [" + 0 + "] MESSAGE [" + "success" + "]");
		
		/*
		if(resultVO.getCode() == -900 && resultVO.getMessage().equalsIgnoreCase("Address is out of service")) {
			// Device 상태가 Out of Service 일 경우 Monitor Stop & Start
			JtapiService.getInstance().monitorStop(makeCallVO.getMyExtension());
			JtapiService.getInstance().monitorStart(makeCallVO.getMyExtension());
			resultVO = JtapiService.getInstance().stopCall(makeCallVO.getMyExtension()  , requestID);
		}
		*/
		
		//
		jsonObj.put("code", RESULT.HTTP_SUCCESS);
		jsonObj.put("msg", "success");
		return jsonObj.toString();
	}


	private MakeCallVO getStopCallInfo(JSONObject paramObject) {
		// TODO Auto-generated method stub
		MakeCallVO makeCallVO = new MakeCallVO();
		makeCallVO.setMyExtension(paramObject.get("myExtension").toString());
		return makeCallVO;
	}


	private String procMakeCall(JSONObject paramObject, String requestID2) {
		// TODO Auto-generated method stub
		
		JSONObject jsonObj = new JSONObject();

		if (paramObject == null) {
			jsonObj.put("code", String.valueOf(RESULT.HTTP_PARAM_ERROR));
			jsonObj.put("msg", "bad parameter data");
			jsonObj.put("param", "all");
			return jsonObj.toString();
		}

		MakeCallVO makeCallVO = getMakeCallInfo(paramObject);
		
		MakeCall makeCallProc = new MakeCall(makeCallVO, requestID);
		makeCallProc.start();
		
		logwrite.httpLog(requestID, "procMakeCall",
				"MAKE CALL RESULT CODE [" + RESULT.RTN_SUCCESS + "] MESSAGE [" + "success" + "]");
		
		/*
		if(resultVO.getCode() == -900 && resultVO.getMessage().equalsIgnoreCase("Address is out of service")) {
			// Device 상태가 Out of Service 일 경우 Monitor Stop & Start
//			JtapiService.getInstance().monitorStop(makeCallVO.getMyExtension());
//			JtapiService.getInstance().monitorStart(makeCallVO.getMyExtension());
//			resultVO = JtapiService.getInstance().makeCall(makeCallVO.getMyExtension(), makeCallVO.getCallingNumber() ,makeCallVO.getMac_address());
		}
		*/
		//
		jsonObj.put("code", RESULT.HTTP_SUCCESS);
		jsonObj.put("msg", "success");
		return jsonObj.toString();
	}


	private MakeCallVO getMakeCallInfo(JSONObject paramObject) {
		// TODO Auto-generated method stub
		MakeCallVO makeCallVO = new MakeCallVO();
		makeCallVO.setMyExtension(paramObject.get("myExtension").toString());
		makeCallVO.setCallingNumber(paramObject.get("callingNumber").toString());
		makeCallVO.setMac_address(paramObject.get("mac_address").toString());
		return makeCallVO;
	}


	private String procCallPickup(JSONObject paramObject, String requestID2) {
		// TODO Auto-generated method stub
		
		JSONObject jsonObj = new JSONObject();

		if (paramObject == null) {
			jsonObj.put("code", String.valueOf(RESULT.HTTP_PARAM_ERROR));
			jsonObj.put("msg", "bad parameter data");
			jsonObj.put("param", "all");
			return jsonObj.toString();
		}

		BaseVO baseVO = getPickupInfo(paramObject);

		PickupVO pickupVO = (PickupVO) baseVO;
		// JtapiService.getInstance().monitorStop(resetVO.getExtension());
		JTapiResultVO resultVO = JtapiService.getInstance().pickup(pickupVO.getMyExtension(),
				pickupVO.getPickupExtension());
		
		if(resultVO.getCode() == -900 && resultVO.getMessage().equalsIgnoreCase("Address is out of service")) {
			// Device 상태가 Out of Service 일 경우 Monitor Stop & Start
			JtapiService.getInstance().monitorStop(pickupVO.getMyExtension());
			JtapiService.getInstance().monitorStart(pickupVO.getMyExtension());
			resultVO = JtapiService.getInstance().pickup(pickupVO.getMyExtension(),
					pickupVO.getPickupExtension());
		}
		
		EmployeeVO pickupEmp = Employees.getInstance().getEmployeeByExtension(pickupVO.getPickupExtension(), "");
		
		if(pickupEmp != null) {
			XmlVO xmlVO = new XmlVO();
			xmlVO.setTargetIP(pickupEmp.getDevice_ipaddr());
			xmlVO.setCmUser(pickupEmp.getCm_user());
			xmlVO.setCmPassword(pickupEmp.getCm_pwd());
			xmlVO.setTargetdn(pickupEmp.getExtension());
			
			XMLHandler 		xmlHandler = new XMLHandler();
			xmlHandler.evtDisconnect(xmlVO , "");
			// XML 팝업 화면이 닫히지 않아 Disconnect XML 을 한번 더 PUSH 한다.
			xmlHandler.evtDisconnectV2(xmlVO , "");
		} else {
			logwrite.httpLog(requestID, "procCallPickup",
					"Pickup Employee is null maybe not login");
		}
		
		logwrite.httpLog(requestID, "procCallPickup",
				"DEVICE MONITOR RESULT CODE [" + resultVO.getCode() + "] MESSAGE [" + resultVO.getMessage() + "]");
		//
		jsonObj.put("code", RESULT.HTTP_SUCCESS);
		jsonObj.put("msg", resultVO.getMessage());
		return jsonObj.toString();
	}


	private BaseVO getPickupInfo(JSONObject paramObject) {
		// TODO Auto-generated method stub
		PickupVO pickupVO = new PickupVO();
		pickupVO.setMyExtension(paramObject.get("myExtension").toString());
		pickupVO.setPickupExtension(paramObject.get("pickupExtension").toString());
		return pickupVO;
	}


	private String procCallStatus (JSONObject paramObject , String requestID) {
			
			JSONObject jsonObj = new JSONObject();
			
			if(paramObject == null) {
				jsonObj.put("code", String.valueOf(RESULT.HTTP_PARAM_ERROR));
				jsonObj.put("msg", "bad parameter data");
				jsonObj.put("param", "all");
				return jsonObj.toString();
			}
			
			BaseVO baseVO = getDeviceStatusInfo(paramObject);
			/*
			String vaildParam = checkParameter(baseVO , APITYPE.API_CALLSTATUS);
			if(!vaildParam.equals("OK")) {
				jsonObj.put("code", RESULT.HTTP_PARAM_ERROR);
				jsonObj.put("msg", "bad parameter data");
				jsonObj.put("param", vaildParam);
				return jsonObj.toString();
			}
			*/
			String resultMsg = "success";
			DeviceStatusVO devStatusVO = (DeviceStatusVO) baseVO;
			
			JSONArray resultArr = new JSONArray();
			List<String> extensionList = devStatusVO.getExtensionList();
			for (int i = 0; i < extensionList.size(); i++) {
				String extension = extensionList.get(i);
				int deviceStatus = 0;
				
				if(!CallStateMgr.getInstance().hasDeviceState(extension)) {
					logwrite.httpLog(requestID, "procCallStatus", extension + " NULL");
					deviceStatus = CALLSTATE.IDLE;
				} else {
					deviceStatus = CallStateMgr.getInstance().getDeviceState(extension);
					logwrite.httpLog(requestID, "procCallStatus", extension + " " + deviceStatus);
				}
				String message = "";
				if(deviceStatus == CALLSTATE.ALERTING_ING) {
					message = "ALERTING";
				} else if(deviceStatus == CALLSTATE.ESTABLISHED_ING) {
					message = "ESTABLISHED";
				} else if (deviceStatus == CALLSTATE.IDLE) {
					message = "IDLE";
				}
				JSONObject tempJson = new JSONObject();
				tempJson.put("extension", extension);
				tempJson.put("statusCode", deviceStatus);
				tempJson.put("statusMessage", message);
				
				resultArr.put(tempJson);
				
				logwrite.httpLog(requestID, "procCallStatus", "DEVICE STATUS RESULT EXTENSION ["+extension+"] CODE [" + deviceStatus + "] MESSAGE [" + message + "]");
			}
			/*
			 * deviceStatus
			 * 	ALERTING_ING		=		900;		// Ring (발신 주체)
				ESTABLISHED_ING		=		902;		// 통화연결 (발신 주체)
				IDLE				=		904;		// 통화종료 
			 */
			// 
			
			jsonObj.put("code", RESULT.HTTP_SUCCESS);
			jsonObj.put("msg", "success");
			jsonObj.put("list", resultArr);
			return jsonObj.toString();
		}


	private BaseVO getDeviceStatusInfo(JSONObject paramObject) {
		// TODO Auto-generated method stub
		DeviceStatusVO deviceStatus = null;
		deviceStatus = splitExtension(paramObject.get("extension").toString());
		
		return deviceStatus;
	}
	private DeviceStatusVO splitExtension(String extension) {
		DeviceStatusVO deviceStatus = new DeviceStatusVO();
		if(extension == null) {
			return null;
		}
		
		String [] extArr = extension.split(",");
		for (int i = 0; i < extArr.length; i++) {
			deviceStatus.setExtension(extArr[i]);
		}
		return deviceStatus;
	}


	private String procResetDevice (JSONObject paramObject , String requestID) {
		
		JSONObject jsonObj = new JSONObject();
		
		if(paramObject == null) {
			jsonObj.put("code", String.valueOf(RESULT.HTTP_PARAM_ERROR));
			jsonObj.put("msg", "bad parameter data");
			jsonObj.put("param", "all");
			return jsonObj.toString();
		}
		
		BaseVO baseVO = getDeviceResetInfo(paramObject);
		
		DeviceResetVO resetVO = (DeviceResetVO) baseVO;
		JtapiService.getInstance().monitorStop(resetVO.getExtension());
		JTapiResultVO resultVO = JtapiService.getInstance().monitorStart(resetVO.getExtension());
		
		logwrite.httpLog(requestID, "procResetDevice", "DEVICE MONITOR RESULT CODE [" + resultVO.getCode() + "] MESSAGE [" + resultVO.getMessage() + "]");
		
		jsonObj.put("code", RESULT.HTTP_SUCCESS);
		jsonObj.put("msg", resultVO.getMessage());
		return jsonObj.toString();
	}
	
	
	private String procLogin  (JSONObject paramObject, String requestID) {
		
		JSONObject jsonObj = new JSONObject();
		
		if(paramObject == null) {
			jsonObj.put("code", String.valueOf(RESULT.HTTP_PARAM_ERROR));
			jsonObj.put("msg", "bad parameter data");
			jsonObj.put("param", "all");
			return jsonObj.toString();
		}
		BaseVO baseVO = getEmployeeInfo(paramObject);
		
		EmployeeVO empVO = (EmployeeVO) baseVO;
		empVO.setCell_no(empVO.getCell_no().replaceAll("-", ""));
		
		/*
		 * 전화기 상태체크 (로그인 시도한 전화기와 내선번호가 정확하게 Regi 되었나 확인)
		 */
		empVO.setRequestID(requestID);
		
		if(empVO.getCm_ip() == null || empVO.getCm_ip().isEmpty()) {
			logwrite.httpLog(requestID, "procLogin", empVO.getEmp_id() + " 교환기 IP 정보가 없습니다!! 교환기 정보 강제 세팅 IP [10.156.214.111]");
			empVO.setCm_ip("10.156.214.111");
		}
		
		if(empVO.getCm_user() == null || empVO.getCm_user().isEmpty()) {
			logwrite.httpLog(requestID, "procLogin", empVO.getEmp_id() + " 교환기 USER 정보가 없습니다!! 교환기 정보 강제 세팅 USER [SAC_IPT]");
			empVO.setCm_user("SAC_IPT");
		}
		
		if(empVO.getCm_pwd() == null || empVO.getCm_pwd().isEmpty()) {
			logwrite.httpLog(requestID, "procLogin", empVO.getEmp_id() + " 교환기 PASSWORD 정보가 없습니다!! 교환기 정보 강제 세팅 PW [dkdlvlxl123$]");
			empVO.setCm_pwd("dkdlvlxl123$");
		}
		
		// 전화기가 깜박거리는 시간동안 백그라운드 스레드에서 일정시간 기다리고 Monitor 시작한다
		DeviceCheck deviceCheck = new DeviceCheck(requestID, empVO);
		deviceCheck.start();
		
		
		// 로그인 요청이 오면 이미지 삭제 -> 생성 작업을 실행하고,
		// Remote Side 서버에게 로그인 요청 동기화 실시
		// 비동기 처리를 위해 스레드 처리
		LoginProcess loginProc = new LoginProcess(paramObject ,  "LOGINSYNC" , requestID);
		loginProc.start();
		/*////////////////////////////////////////////////////////*/
		
		jsonObj.put("code", RESULT.HTTP_SUCCESS);
		jsonObj.put("msg", "success");
		return jsonObj.toString();
		
	}
	
	private String procLogout  (JSONObject paramObject, String requestID) {
		
		JSONObject jsonObj = new JSONObject();
		
		if(paramObject == null) {
			jsonObj.put("code", String.valueOf(RESULT.HTTP_PARAM_ERROR));
			jsonObj.put("msg", "bad parameter data");
			jsonObj.put("param", "all");
			return jsonObj.toString();
		}
		
		BaseVO baseVO = getEmployeeInfo(paramObject);
		/*
		String vaildParam = checkParameter(baseVO , APITYPE.API_LOGOUT);
		if(!vaildParam.equals("OK")) {
			jsonObj.put("code", RESULT.HTTP_PARAM_ERROR);
			jsonObj.put("msg", "bad parameter data");
			jsonObj.put("param", vaildParam);
			return jsonObj.toString();
		}
		*/
		
		EmployeeVO empVO = (EmployeeVO) baseVO;
		JTapiResultVO resultVO = JtapiService.getInstance().monitorStop(empVO.getExtension());
		int loginResult = Employees.getInstance().logoutEmployee(empVO , requestID);
		
		logwrite.httpLog(requestID, "procLogout", "DEVICE MONITOR RESULT CODE [" + resultVO.getCode() + "] MESSAGE [" + resultVO.getMessage() + "]");
		logwrite.httpLog(requestID, "procLogout", "EMPLOYEE LOGOUT RESULT CODE [" + loginResult + "]");
		
		
		jsonObj.put("code", RESULT.HTTP_SUCCESS);
		jsonObj.put("msg", resultVO.getMessage());
		return jsonObj.toString();
		
		
	}
	
	
	
	
	private EmployeeVO getEmployeeInfo(JSONObject paramObject) {
		// TODO Auto-generated method stub
		EmployeeVO employee = new EmployeeVO();
		employee.setEmp_id(paramObject.get("emp_id").toString());
		employee.setEmp_lno(paramObject.get("emp_lno").toString());
		employee.setEmp_nm_kor(paramObject.get("emp_nm_kor").toString());
		employee.setEmp_nm_eng(paramObject.get("emp_nm_eng").toString());
		employee.setOrg_nm(paramObject.get("org_nm").toString());
		employee.setPos_nm(paramObject.get("pos_nm").toString());
		employee.setDuty_nm(paramObject.get("duty_nm").toString());
		employee.setExtension(paramObject.get("extension").toString());
		employee.setEmail(paramObject.get("email").toString());
		employee.setCell_no(paramObject.get("cell_no").toString());
		employee.setBuilding(paramObject.get("building").toString());
		employee.setFloor(paramObject.get("floor").toString());
		employee.setEmp_stat_nm(paramObject.get("emp_stat_nm").toString());
		employee.setEmp_div_cd_nm(paramObject.get("emp_div_cd_nm").toString());
		employee.setPopup_svc_yn(paramObject.get("popup_svc_yn").toString());
		employee.setMac_address(paramObject.get("mac_address").toString());
		employee.setDevice_ipaddr(paramObject.get("device_ipaddr").toString());
		employee.setDevice_type(paramObject.get("device_type").toString());
		employee.setCm_ver(paramObject.get("cm_ver").toString());
		employee.setCm_ip(paramObject.get("cm_ip").toString());
		employee.setCm_user(paramObject.get("cm_user").toString());
		employee.setCm_pwd(paramObject.get("cm_pwd").toString());

		return employee;
	}
	
	
	
	
	
	private DeviceResetVO getDeviceResetInfo (JSONObject paramObject) {
		// TODO Auto-generated method stub
		DeviceResetVO deviceReset = new DeviceResetVO();
		deviceReset.setExtension(paramObject.get("extension").toString());
		deviceReset.setMac_address(paramObject.get("mac_address").toString());
		deviceReset.setDevice_ipaddr(paramObject.get("device_ipaddr").toString());
		deviceReset.setCm_ip(paramObject.get("cm_ip").toString());
		deviceReset.setCm_user(paramObject.get("cm_user").toString());
		deviceReset.setCm_pwd(paramObject.get("cm_pwd").toString());
		return deviceReset;
	}
	
	
	
}
