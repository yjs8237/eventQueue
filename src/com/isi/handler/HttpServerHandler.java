package com.isi.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.URI;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.isi.constans.APITYPE;
import com.isi.constans.CALLSTATE;
import com.isi.constans.RESULT;
import com.isi.data.CallStateMgr;
import com.isi.data.Employees;
import com.isi.data.ImageMgr;
import com.isi.data.MyAddressMgr;
import com.isi.data.XmlInfoMgr;
import com.isi.db.DBConnMgr;
import com.isi.duplex.AliveProc;
import com.isi.exception.ExceptionUtil;
import com.isi.file.GLogWriter;
import com.isi.file.ILog;
import com.isi.file.PropertyRead;
import com.isi.service.JtapiService;
import com.isi.thread.DeviceCheck;
import com.isi.thread.LoginProcess;
import com.isi.vo.BaseVO;
import com.isi.vo.DeviceResetVO;
import com.isi.vo.DeviceStatusVO;
import com.isi.vo.EmployeeVO;
import com.isi.vo.JTapiResultVO;
import com.isi.vo.MakeCallVO;
import com.isi.vo.PickupVO;
import com.isi.vo.XmlVO;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
*
* @author greatyun
*/
public class HttpServerHandler {
	
	private int port;
	private PropertyRead pr;
	private ILog logwrite;
	
	public HttpServerHandler(){
		pr = PropertyRead.getInstance();
		logwrite = new GLogWriter();
	}
	
	public int startService(){
		
		try {
			
			port = XmlInfoMgr.getInstance().getHttpPort();
			
			InetSocketAddress addr = new InetSocketAddress(port);
			HttpServer server = HttpServer.create(addr, 0);
			
			server.createContext("/", new HttpProcHandler());
			server.setExecutor(null);
			server.start();
			
			return RESULT.RTN_SUCCESS;
			
		} catch (Exception e){
			e.printStackTrace(ExceptionUtil.getPrintWriter());
			logwrite.httpLog("" ,"startService()", ExceptionUtil.getStringWriter().toString());
			return RESULT.RTN_EXCEPTION;
		}
	}
	
	
	private class HttpProcHandler implements HttpHandler{
		
		String requestMethod = "";
		
		@Override
		public void handle(HttpExchange exchange) throws IOException {
			// TODO Auto-generated method stub
			
			String requestID = String.valueOf(System.currentTimeMillis()) + "-" + String.valueOf(Thread.currentThread().getName()) + String.valueOf(Thread.currentThread().getId());
			String responseDATA = "";
			requestMethod = exchange.getRequestMethod().toUpperCase().trim();
			
			switch (requestMethod) {
			case "GET":
				responseDATA = procGet(exchange , requestID);
				
				break;
				
			case "POST":
				responseDATA = procPost(exchange , requestID);
				break;
			default:
				break;
			}
			
			exchange.sendResponseHeaders(200, responseDATA.length());
			
			OutputStream responseBody = exchange.getResponseBody();
			logwrite.httpLog(requestID, "handle", "JSON RETURN DATA #[" + responseDATA + "]#");
			
			responseBody.write(responseDATA.getBytes());
			responseBody.close();
			
			
		}
		
		private String procPost (HttpExchange exchange, String requestID) {
			
			String resultJSONData = "";
			
			try {
				
				Headers responseHeaders = exchange.getResponseHeaders();
				responseHeaders.set("Content-Type", "text/html");
				responseHeaders.set("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
				responseHeaders.set("Access-Control-Max-Age", "3600");
				responseHeaders.set("Access-Control-Allow-Headers", "x-requested-with");
				responseHeaders.set("Access-Control-Allow-Origin", "*");
				
				BufferedReader br = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), "UTF-8"));
				
				String line = "";
				while((line = br.readLine()) != null) {
					System.out.println(line);
				}
				
			} catch (Exception e) {
				
			}
			
			return resultJSONData;
		}
		
		
		private String procGet(HttpExchange exchange, String requestID) {

			int returnCode = RESULT.RTN_EXCEPTION;

			String resultJSONData = "";

			try {

				Headers responseHeaders = exchange.getResponseHeaders();
				responseHeaders.set("Content-Type", "text/html");
				responseHeaders.set("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
				responseHeaders.set("Access-Control-Max-Age", "3600");
				responseHeaders.set("Access-Control-Allow-Headers", "x-requested-with");
				responseHeaders.set("Access-Control-Allow-Origin", "*");

				URI uri = exchange.getRequestURI();
				String url = uri.toString().trim();
				String parameter = exchange.getRequestURI().getQuery();

				// System.out.println("url -> " + url);

				url = getURL(url);

				logwrite.httpLog(requestID, "procGet()", "REQUEST URL[" + url + "] PARAM[" + parameter + "]");

				switch (url) {

				case "/login":
					resultJSONData = procLogin(parameter, requestID);
					break;

				case "/logout":
					resultJSONData = procLogout(parameter, requestID);
					break;

				case "/resetdevice":
					resultJSONData = procResetDevice(parameter, requestID);
					break;

				case "/callstatus":
					resultJSONData = procCallStatus(parameter, requestID);
					break;
					
				case "/pickup":
					resultJSONData = procCallPickup(parameter, requestID);
					break;
					
				case "/employee":
					resultJSONData = procEmployee(parameter, requestID);
					break;
				
				case "/makecall":
					resultJSONData = procMakeCall(parameter, requestID);
					break;
					
				case "/stopcall":
					resultJSONData = procStopCall(parameter, requestID);
					break;
					
				case "/checkuser":
					resultJSONData = procCheckLoginUser(parameter, requestID);
					break;
				default:
					// returnCode = RESULT.HTTP_URL_ERROR;
					break;
				}

			} catch (Exception e) {
				e.printStackTrace(ExceptionUtil.getPrintWriter());
				logwrite.httpLog(requestID, "procGet()", ExceptionUtil.getStringWriter().toString());
				returnCode = RESULT.RTN_EXCEPTION;
			}

			return resultJSONData;

		}
		
		
		private String procCheckLoginUser(String parameter, String requestID) {
			// TODO Auto-generated method stub
			Map <String, String> map = new HashMap<String, String>();
			map = queryToMap(parameter);
			
			JSONObject jsonObj = new JSONObject();
			
			if(map == null || map.isEmpty()) {
				jsonObj.put("code", String.valueOf(RESULT.HTTP_PARAM_ERROR));
				jsonObj.put("msg", "bad parameter data");
				jsonObj.put("param", "all");
				return jsonObj.toString();
			}
			
			JSONArray jsonArr = new JSONArray();
			String cell_no = map.get("cell_no");
			String extension = map.get("extension");
			String mac_address = map.get("mac_address");
			
			List userList = Employees.getInstance().getEmployeeListByExtension(extension);
			if(userList == null) {
				
			} else {
				
				return getJsonReturnData (userList);
			}
			userList = Employees.getInstance().getEmployeeListByCellNum(cell_no, "");
			if(userList == null) {
				
			} else {
				return getJsonReturnData (userList);
			}
			return jsonArr.toString();
		}

		private String getJsonReturnData(List userList) {
			// TODO Auto-generated method stub
			JSONArray jsonArr = new JSONArray();
			for (int i = 0; i < userList.size(); i++) {
				JSONObject obj = new JSONObject();
				EmployeeVO empVO = (EmployeeVO) userList.get(i);
				obj.put("emp_lno", empVO.getEmp_lno() == null ? "" : empVO.getEmp_lno());
				obj.put("emp_id", empVO.getEmp_id() == null ? "" : empVO.getEmp_id());
				obj.put("extension", empVO.getExtension() == null ? "" : empVO.getExtension());
				obj.put("cell_num", empVO.getCell_no() == null ? "" : empVO.getCell_no());
				obj.put("mac_address", empVO.getMac_address() == null ? "" : empVO.getMac_address());
				obj.put("device_type", empVO.getDevice_type() == null ? "" : empVO.getDevice_type());
				obj.put("device_ipaddr", empVO.getDevice_ipaddr() == null ? "" : empVO.getDevice_ipaddr());
				obj.put("cm_ip", empVO.getCm_ip() == null ? "" : empVO.getCm_ip());
				obj.put("cm_user", empVO.getCm_user()== null ? "" : empVO.getCm_user());
				obj.put("emp_nm_kor", empVO.getEmp_nm_kor()== null ? "" : empVO.getEmp_nm_kor());
				obj.put("emp_nm_eng", empVO.getEmp_nm_eng()== null ? "" : empVO.getEmp_nm_eng());
				obj.put("org_nm", empVO.getOrg_nm()== null ? "" : empVO.getOrg_nm());
				obj.put("pos_nm", empVO.getPos_nm()== null ? "" : empVO.getPos_nm());
				obj.put("duty_nm", empVO.getDuty_nm()== null ? "" : empVO.getDuty_nm());
				obj.put("email", empVO.getEmail()== null ? "" : empVO.getEmail());
				obj.put("emp_stat_nm", empVO.getEmp_stat_nm()== null ? "" : empVO.getEmp_stat_nm());
				obj.put("emp_div_cd_nm", empVO.getEmp_div_cd_nm()== null ? "" : empVO.getEmp_div_cd_nm());
				obj.put("building", empVO.getBuilding()== null ? "" : empVO.getBuilding());
				obj.put("floor", empVO.getFloor()== null ? "" : empVO.getFloor());
				obj.put("cm_pwd", empVO.getCm_pwd()== null ? "" : empVO.getCm_pwd());
				obj.put("popup_svc_yn", empVO.getPopup_svc_yn()== null ? "" : empVO.getPopup_svc_yn());
				jsonArr.put(obj);
			}
			return jsonArr.toString();
		}

		private String procCreateImage (String parameter , String requestID) {
			Map <String, String> map = new HashMap<String, String>();
			map = queryToMap(parameter);
			
			JSONObject jsonObj = new JSONObject();
			
			if(map == null || map.isEmpty()) {
				jsonObj.put("code", String.valueOf(RESULT.HTTP_PARAM_ERROR));
				jsonObj.put("msg", "bad parameter data");
				jsonObj.put("param", "all");
				return jsonObj.toString();
			}
			BaseVO baseVO = getEmployeeInfo(map);
			/*
			String vaildParam = checkParameter(baseVO , APITYPE.API_LOGIN);
			if(!vaildParam.equals("OK")) {
				jsonObj.put("code", RESULT.HTTP_PARAM_ERROR);
				jsonObj.put("msg", "bad parameter data");
				jsonObj.put("param", vaildParam);
				return jsonObj.toString();
			}
			*/
			EmployeeVO empVO = (EmployeeVO) baseVO;
			empVO.setCell_no(empVO.getCell_no().replaceAll("-", ""));
			
			// 로그인 시도할때 이미지 삭제 -> 생성 
			ImageMgr imageMgr = ImageMgr.getInstance();
			imageMgr.createImageFiles(empVO , requestID);
			
			logwrite.httpLog(requestID, "procCreateImage", "Create Login Image Sync Success!!");
			
			jsonObj.put("code", RESULT.HTTP_SUCCESS);
			jsonObj.put("msg", "success");
			
			return jsonObj.toString();
			
		}
		
		private String procEmployee (String parameter , String requestID) {
			Map <String, String> map = new HashMap<String, String>();
			map = queryToMap(parameter);
			
			JSONObject jsonObj = new JSONObject();
			
			if(map == null || map.isEmpty()) {
				jsonObj.put("code", String.valueOf(RESULT.HTTP_PARAM_ERROR));
				jsonObj.put("msg", "bad parameter data");
				jsonObj.put("param", "all");
				return jsonObj.toString();
			}
			
			String extension = map.get("extension");
			
			List list = Employees.getInstance().getEmployeeListByExtension(extension, "");
			String retStr = "";
			for (int i = 0; i < list.size(); i++) {
				retStr += list.get(i).toString(); 
				retStr += "\n";
			}
			
			if(retStr.isEmpty()) {
				retStr = "NO DATA";
			}
			/*
			String vaildParam = checkParameter(baseVO , APITYPE.API_LOGIN);
			if(!vaildParam.equals("OK")) {
				jsonObj.put("code", RESULT.HTTP_PARAM_ERROR);
				jsonObj.put("msg", "bad parameter data");
				jsonObj.put("param", vaildParam);
				return jsonObj.toString();
			}
			*/
			
			
			return retStr;
		}
		
		private String procCallPickup(String parameter, String requestID) {
			Map<String, String> map = new HashMap<String, String>();
			map = queryToMap(parameter);
			
			JSONObject jsonObj = new JSONObject();

			if (map == null || map.isEmpty()) {
				jsonObj.put("code", String.valueOf(RESULT.HTTP_PARAM_ERROR));
				jsonObj.put("msg", "bad parameter data");
				jsonObj.put("param", "all");
				return jsonObj.toString();
			}

			BaseVO baseVO = getPickupInfo(map);
			/*
			 * String vaildParam = checkParameter(baseVO , APITYPE.API_PICKUP);
			 * if(!vaildParam.equals("OK")) { jsonObj.put("code", RESULT.HTTP_PARAM_ERROR);
			 * jsonObj.put("msg", "bad parameter data"); jsonObj.put("param", vaildParam);
			 * return jsonObj.toString(); }
			 */

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
		
		private String procMakeCall(String parameter, String requestID) {
			Map<String, String> map = new HashMap<String, String>();
			map = queryToMap(parameter);
			
			JSONObject jsonObj = new JSONObject();

			if (map == null || map.isEmpty()) {
				jsonObj.put("code", String.valueOf(RESULT.HTTP_PARAM_ERROR));
				jsonObj.put("msg", "bad parameter data");
				jsonObj.put("param", "all");
				return jsonObj.toString();
			}

			MakeCallVO makeCallVO = getMakeCallInfo(map);
			
//			EmployeeVO empVO = Employees.getInstance().getEmployeeByExtension(makeCallVO.getMyExtension(), "");
//			// JtapiService.getInstance().monitorStop(resetVO.getExtension());
//			
//			if(empVO == null) {
//				logwrite.httpLog(requestID, "procMakeCall",
//						"Extension " + makeCallVO.getMyExtension() + " not login");
//				jsonObj.put("code", String.valueOf(RESULT.HTTP_PARAM_ERROR));
//				jsonObj.put("msg", "Extension " + makeCallVO.getMyExtension() + " not login");
//				return jsonObj.toString();
//			}
			
			
			
			JTapiResultVO resultVO = JtapiService.getInstance().makeCall(makeCallVO.getMyExtension(),
					makeCallVO.getCallingNumber() , makeCallVO.getMac_address());
			
			logwrite.httpLog(requestID, "procMakeCall",
					"MAKE CALL RESULT CODE [" + resultVO.getCode() + "] MESSAGE [" + resultVO.getMessage() + "]");
			
			if(resultVO.getCode() == -900 && resultVO.getMessage().equalsIgnoreCase("Address is out of service")) {
				// Device 상태가 Out of Service 일 경우 Monitor Stop & Start
//				JtapiService.getInstance().monitorStop(makeCallVO.getMyExtension());
//				JtapiService.getInstance().monitorStart(makeCallVO.getMyExtension());
//				resultVO = JtapiService.getInstance().makeCall(makeCallVO.getMyExtension(), makeCallVO.getCallingNumber() ,makeCallVO.getMac_address());
			}
			
			//
			jsonObj.put("code", RESULT.HTTP_SUCCESS);
			jsonObj.put("msg", resultVO.getMessage());
			return jsonObj.toString();
		}
		
		private String procStopCall(String parameter, String requestID) {
			Map<String, String> map = new HashMap<String, String>();
			map = queryToMap(parameter);
			
			JSONObject jsonObj = new JSONObject();

			if (map == null || map.isEmpty()) {
				jsonObj.put("code", String.valueOf(RESULT.HTTP_PARAM_ERROR));
				jsonObj.put("msg", "bad parameter data");
				jsonObj.put("param", "all");
				return jsonObj.toString();
			}

			MakeCallVO makeCallVO = getStopCallInfo(map);
			
			JTapiResultVO resultVO = JtapiService.getInstance().stopCall(makeCallVO.getMyExtension());
			
			
			logwrite.httpLog(requestID, "procStopCall",
					"HANGUP CALL RESULT CODE [" + resultVO.getCode() + "] MESSAGE [" + resultVO.getMessage() + "]");

			if(resultVO.getCode() == -900 && resultVO.getMessage().equalsIgnoreCase("Address is out of service")) {
				// Device 상태가 Out of Service 일 경우 Monitor Stop & Start
				JtapiService.getInstance().monitorStop(makeCallVO.getMyExtension());
				JtapiService.getInstance().monitorStart(makeCallVO.getMyExtension());
				resultVO = JtapiService.getInstance().stopCall(makeCallVO.getMyExtension() );
			}
			
			//
			jsonObj.put("code", RESULT.HTTP_SUCCESS);
			jsonObj.put("msg", resultVO.getMessage());
			return jsonObj.toString();
		}
		
		
		private String procCallStatus (String parameter , String requestID) {
			Map <String, String> map = new HashMap<String, String>();
			map = queryToMap(parameter);
			
			JSONObject jsonObj = new JSONObject();
			
			if(map == null || map.isEmpty()) {
				jsonObj.put("code", String.valueOf(RESULT.HTTP_PARAM_ERROR));
				jsonObj.put("msg", "bad parameter data");
				jsonObj.put("param", "all");
				return jsonObj.toString();
			}
			
			BaseVO baseVO = getDeviceStatusInfo(map);
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
		
		private String procResetDevice (String parameter , String requestID) {
			Map <String, String> map = new HashMap<String, String>();
			map = queryToMap(parameter);
			
			JSONObject jsonObj = new JSONObject();
			
			if(map == null || map.isEmpty()) {
				jsonObj.put("code", String.valueOf(RESULT.HTTP_PARAM_ERROR));
				jsonObj.put("msg", "bad parameter data");
				jsonObj.put("param", "all");
				return jsonObj.toString();
			}
			
			BaseVO baseVO = getDeviceResetInfo(map);
			/*
			String vaildParam = checkParameter(baseVO , APITYPE.API_DEVICERESET);
			if(!vaildParam.equals("OK")) {
				jsonObj.put("code", RESULT.HTTP_PARAM_ERROR);
				jsonObj.put("msg", "bad parameter data");
				jsonObj.put("param", vaildParam);
				return jsonObj.toString();
			}
			*/
			
			DeviceResetVO resetVO = (DeviceResetVO) baseVO;
			JtapiService.getInstance().monitorStop(resetVO.getExtension());
			JTapiResultVO resultVO = JtapiService.getInstance().monitorStart(resetVO.getExtension());
			
			logwrite.httpLog(requestID, "procResetDevice", "DEVICE MONITOR RESULT CODE [" + resultVO.getCode() + "] MESSAGE [" + resultVO.getMessage() + "]");
			
			jsonObj.put("code", RESULT.HTTP_SUCCESS);
			jsonObj.put("msg", resultVO.getMessage());
			return jsonObj.toString();
		}
		
		private String procLogout  (String parameter, String requestID) {
			Map <String, String> map = new HashMap<String, String>();
			map = queryToMap(parameter);
			
			JSONObject jsonObj = new JSONObject();
			
			if(map == null || map.isEmpty()) {
				jsonObj.put("code", String.valueOf(RESULT.HTTP_PARAM_ERROR));
				jsonObj.put("msg", "bad parameter data");
				jsonObj.put("param", "all");
				return jsonObj.toString();
			}
			
			BaseVO baseVO = getEmployeeInfo(map);
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
		private String procLogin  (String parameter, String requestID) {
			
			Map <String, String> map = new HashMap<String, String>();
			map = queryToMap(parameter);
			
			JSONObject jsonObj = new JSONObject();
			
			if(map == null || map.isEmpty()) {
				jsonObj.put("code", String.valueOf(RESULT.HTTP_PARAM_ERROR));
				jsonObj.put("msg", "bad parameter data");
				jsonObj.put("param", "all");
				return jsonObj.toString();
			}
			BaseVO baseVO = getEmployeeInfo(map);
			/*
			String vaildParam = checkParameter(baseVO , APITYPE.API_LOGIN);
			if(!vaildParam.equals("OK")) {
				jsonObj.put("code", RESULT.HTTP_PARAM_ERROR);
				jsonObj.put("msg", "bad parameter data");
				jsonObj.put("param", vaildParam);
				return jsonObj.toString();
			}
			*/
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
			
			
//			EmployeeVO tempVO = Employees.getInstance().loginEmployee(empVO , requestID);
//			if(tempVO != null) {
//				empVO.setOrg_nm(tempVO.getOrg_nm());
//				empVO.setPos_nm(tempVO.getPos_nm());
//			}
			
			// 로그인 요청이 오면 이미지 삭제 -> 생성 작업을 실행하고,
			// Remote Side 서버에게 로그인 요청 동기화 실시
			// 비동기 처리를 위해 스레드 처리
			LoginProcess loginProc = new LoginProcess(empVO , parameter , requestID);
			loginProc.start();
			/*////////////////////////////////////////////////////////*/
			
			
			jsonObj.put("code", RESULT.HTTP_SUCCESS);
			jsonObj.put("msg", "success");
			return jsonObj.toString();
			
			
			//////////////////////////////////////////////////////////////////////////////////////////
			/*
			JTapiResultVO resultVO = JtapiService.getInstance().monitorStart(empVO.getExtension());
			int loginResult = Employees.getInstance().loginEmployee(empVO , requestID);
			
			logwrite.httpLog(requestID, "procLogin", "DEVICE MONITOR RESULT CODE [" + resultVO.getCode() + "] MESSAGE [" + resultVO.getMessage() + "]");
			logwrite.httpLog(requestID, "procLogin", "EMPLOYEE LOGIN RESULT CODE [" + loginResult + "]");
			
			jsonObj.put("code", String.valueOf(resultVO.getCode()));
			jsonObj.put("msg", resultVO.getMessage());
			
			return jsonObj.toString();
			*/
		}
		
		private String checkParameter(BaseVO baseVO, int apiType) {
			String result = "OK";
			String checkData = "";
			Object object = null;
			try {
				Class targetClass = null;
				if (apiType == APITYPE.API_LOGIN || apiType == APITYPE.API_LOGOUT) {
					
					targetClass = Class.forName("com.isi.vo.EmployeeVO");
					EmployeeVO empVO = (EmployeeVO) baseVO;
					object = empVO;
					
				} else if(apiType == APITYPE.API_DEVICERESET) {
					
					targetClass = Class.forName("com.isi.vo.DeviceResetVO");
					DeviceResetVO devResetVO = (DeviceResetVO) baseVO;
					object = devResetVO;
					
				} else if(apiType == APITYPE.API_CALLSTATUS){
					
					targetClass = Class.forName("com.isi.vo.DeviceStatusVO");
					DeviceStatusVO devStatusVO = (DeviceStatusVO) baseVO;
					object = devStatusVO;
					
				} else if (apiType == APITYPE.API_PICKUP){
					
					targetClass = Class.forName("com.isi.vo.PickupVO");
					PickupVO pickupVO = (PickupVO) baseVO;
					object = pickupVO;
					
				}
				Method methods[] = targetClass.getDeclaredMethods();

				for (int i = 0; i < methods.length; i++) {
					String methodName = methods[i].getName();
					if (methodName.startsWith("get")) {
						Object obj = methods[i].invoke(object);
						if (obj == null || obj.toString().isEmpty()) {
							return methodName.replaceAll("get", "").toLowerCase();
						}
					}
				}

			} catch (Exception e) {
				
			}

			return result;
		}

		private String getURL(String url) {
			// TODO Auto-generated method stub
			
			String retStr = "";
			int index = url.indexOf("?");
			
			if(index > -1) {
				retStr = url.substring(0, index);
			}
			
			return retStr;
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
		
		private PickupVO getPickupInfo (Map<String, String> map) {
			PickupVO pickupVO = new PickupVO();
			Set keySet = map.keySet();
			Iterator iter = keySet.iterator();
			while (iter.hasNext()) {
				String key = (String) iter.next();
				switch (key) {
					case "myExtension" :
						pickupVO.setMyExtension(map.get("myExtension").toString());
						break;
					case "pickupExtension" :
						pickupVO.setPickupExtension(map.get("pickupExtension").toString());
						break;
					default : 
						break;
				}
			}

			return pickupVO;
		}
		
		private MakeCallVO getMakeCallInfo (Map<String, String> map) {
			MakeCallVO makeCallVO = new MakeCallVO();
			Set keySet = map.keySet();
			Iterator iter = keySet.iterator();
			while (iter.hasNext()) {
				String key = (String) iter.next();
				switch (key) {
					case "myExtension" :
						makeCallVO.setMyExtension(map.get("myExtension").toString());
						break;
					case "callingNumber" :
						makeCallVO.setCallingNumber(map.get("callingNumber").toString());
						break;
					case "mac_address" :
						makeCallVO.setMac_address(map.get("mac_address").toString());
						break;
					default : 
						break;
				}
			}

			return makeCallVO;
		}
		
		private MakeCallVO getStopCallInfo (Map<String, String> map) {
			MakeCallVO makeCallVO = new MakeCallVO();
			Set keySet = map.keySet();
			Iterator iter = keySet.iterator();
			while (iter.hasNext()) {
				String key = (String) iter.next();
				switch (key) {
					case "myExtension" :
						makeCallVO.setMyExtension(map.get("myExtension").toString());
						break;
					default : 
						break;
				}
			}

			return makeCallVO;
		}
		
		private DeviceStatusVO getDeviceStatusInfo (Map<String, String> map) {
			// TODO Auto-generated method stub
			DeviceStatusVO deviceStatus = null;
			Set keySet = map.keySet();
			Iterator iter = keySet.iterator();
			while (iter.hasNext()) {
				String key = (String) iter.next();
				switch (key) {
					case "extension" :
						deviceStatus = splitExtension(map.get("extension").toString());
						break;
					default : 
						break;
				}
			}

			return deviceStatus;
		}
		
		
		private DeviceResetVO getDeviceResetInfo (Map<String, String> map) {
			// TODO Auto-generated method stub
			DeviceResetVO deviceReset = new DeviceResetVO();
			Set keySet = map.keySet();
			Iterator iter = keySet.iterator();
			while (iter.hasNext()) {
				String key = (String) iter.next();
				switch (key) {
					case "extension" : 
						deviceReset.setExtension(map.get("extension").toString());
						break;
					case "mac_address" :
						deviceReset.setMac_address(map.get("mac_address").toString());
						break;
					case "device_ipaddr" :
						deviceReset.setDevice_ipaddr(map.get("device_ipaddr").toString());
						break;
					case "cm_ip" : 
						deviceReset.setCm_ip(map.get("cm_ip").toString());
						break;
					case "cm_user" : 
						deviceReset.setCm_user(map.get("cm_user").toString());
						break;
					case "cm_pwd" : 
						deviceReset.setCm_pwd(map.get("cm_pwd").toString());
						break;
					default : 
						break;
				}
			}

			return deviceReset;
		}
		
		
		private EmployeeVO getEmployeeInfo(Map<String, String> map) {
			// TODO Auto-generated method stub
			EmployeeVO employee = new EmployeeVO();
			Set keySet = map.keySet();
			Iterator iter = keySet.iterator();
			while (iter.hasNext()) {
				String key = (String) iter.next();
				switch (key) {
				case "emp_id":
					employee.setEmp_id(getEmployeeInfo(map, key));
					break;
				case "emp_lno":
					employee.setEmp_lno(getEmployeeInfo(map, key));
					break;
				case "emp_nm_kor":
					employee.setEmp_nm_kor(getEmployeeInfo(map, key));
					break;
				case "emp_nm_eng":
					employee.setEmp_nm_eng(getEmployeeInfo(map, key));
					break;
				case "org_nm":
					employee.setOrg_nm(getEmployeeInfo(map, key));
					break;
				case "pos_nm":
					employee.setPos_nm(getEmployeeInfo(map, key));
					break;
				case "duty_nm":
					employee.setDuty_nm(getEmployeeInfo(map, key));
					break;
				case "extension":
					employee.setExtension(getEmployeeInfo(map, key));
					break;
				case "email":
					employee.setEmail(getEmployeeInfo(map, key));
					break;
				case "cell_no":
					employee.setCell_no(getEmployeeInfo(map, key));
					break;
				case "building":
					employee.setBuilding(getEmployeeInfo(map, key));
					break;
				case "floor":
					employee.setFloor(getEmployeeInfo(map, key));
					break;
				case "emp_stat_nm":
					employee.setEmp_stat_nm(getEmployeeInfo(map, key));
					break;
				case "emp_div_cd_nm":
					employee.setEmp_div_cd_nm(getEmployeeInfo(map, key));
					break;
				case "popup_svc_yn":
					employee.setPopup_svc_yn(getEmployeeInfo(map, key));
					break;
				case "mac_address":
					employee.setMac_address(getEmployeeInfo(map, key));
					break;
				case "device_ipaddr":
					employee.setDevice_ipaddr(getEmployeeInfo(map, key));
					break;
				case "device_type":
					employee.setDevice_type(getEmployeeInfo(map, key));
					break;
				case "cm_ver":
					employee.setCm_ver(getEmployeeInfo(map, key));
					break;
				case "cm_ip":
					employee.setCm_ip(getEmployeeInfo(map, key));
					break;
				case "cm_user":
					employee.setCm_user(getEmployeeInfo(map, key));
					break;
				case "cm_pwd":
					employee.setCm_pwd(getEmployeeInfo(map, key));
					break;
				}
			}

			return employee;
		}
		
		private String getEmployeeInfo (Map<String, String> map, String key) {
			if(map.get(key) == null){
				return "";
			} else {
				return map.get(key);
			}
		}

		public Map <String, String> queryToMap (String query){
		    Map<String, String> result = new HashMap<String, String>();
		    for (String param : query.split("&")) {
		        String pair[] = param.split("=");
		        if (pair.length>1) {
		            result.put(pair[0], pair[1]);
		        }else{
		            result.put(pair[0], "");
		        }
		    }
		    return result;
		}
		
		public Map <String, String> queryToMapExtension (String query){
		    Map<String, String> result = new HashMap<String, String>();
		    String pair[] = query.split("=");
	        if (pair.length>1) {
	            result.put(pair[0], pair[1]);
	        }else{
	            result.put(pair[0], "");
	        }
		    return result;
		}
		
	}

}

