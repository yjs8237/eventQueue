package com.isi.handler;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URI;
import java.sql.ResultSet;
import java.util.*;

import javax.management.relation.RelationTypeNotFoundException;

import org.json.JSONArray;
import org.json.JSONObject;

import com.isi.axl.soap.SxmlHandler;
import com.isi.constans.APITYPE;
import com.isi.constans.CALLSTATE;
import com.isi.constans.LOGLEVEL;
import com.isi.constans.LOGTYPE;
import com.isi.constans.PROPERTIES;
import com.isi.constans.RESULT;
import com.isi.data.CallStateMgr;
import com.isi.data.Employees;
import com.isi.data.XmlInfoMgr;
import com.isi.db.JDatabase;
import com.isi.exception.ExceptionUtil;
import com.isi.file.*;
import com.isi.service.JtapiService;
import com.isi.vo.BaseVO;
import com.isi.vo.CustomerVO;
import com.isi.vo.DeviceResetVO;
import com.isi.vo.DeviceStatusVO;
import com.isi.vo.EmployeeVO;
import com.isi.vo.JTapiResultVO;
import com.isi.vo.PickupVO;
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
	private EmployeeVO employee;
	private List employeeList;
//	private JDatabase m_Conn = null;
//	private ResultSet rs;
	
	public HttpServerHandler(){
		pr = PropertyRead.getInstance();
		logwrite = new GLogWriter();
//		m_Conn = new JDatabase();
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
		
		String requestURL = "";
		String requestMethod = "";
		
		@Override
		public void handle(HttpExchange exchange) throws IOException {
			// TODO Auto-generated method stub
			
			String requestID = String.valueOf(System.currentTimeMillis()) + "-" + String.valueOf(Thread.currentThread().getName()) + String.valueOf(Thread.currentThread().getId());
			String responseDATA = "";
			requestMethod = exchange.getRequestMethod().toUpperCase().trim();
			int retCode = RESULT.RTN_EXCEPTION;
			switch (requestMethod) {
			case "GET":
				responseDATA = procGet(exchange , requestID);
				break;
				
			case "POST":
				
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
				
//				System.out.println("url -> " + url);
				
				url = getURL(url);
				
				logwrite.httpLog(requestID,"procGet()", "REQUEST URL[" + url + "] PARAM["+parameter+"]");
				
				switch (url) {
				
					
				case "/login":
					resultJSONData = procLogin(parameter , requestID);
					break;
					
				case "/logout":
					resultJSONData = procLogout(parameter , requestID);
					break;
					
				case "/resetdevice":
					resultJSONData = procResetDevice(parameter , requestID);
					break;
					
				case "/callstatus":
					resultJSONData = procCallStatus(parameter , requestID);
					break;
					
				case "/pickup":
					resultJSONData = procCallPickup(parameter , requestID);
					break;
					
				default:
					returnCode = RESULT.HTTP_URL_ERROR;
					break;
				}
				
			} catch(Exception  e){
				e.printStackTrace(ExceptionUtil.getPrintWriter());
				logwrite.httpLog(requestID ,"procGet()", ExceptionUtil.getStringWriter().toString());
				returnCode = RESULT.RTN_EXCEPTION;
			}
			
			return resultJSONData;
			
		}
		
		private String procCallPickup (String parameter , String requestID) {
			Map <String, String> map = new HashMap<String, String>();
			map = queryToMap(parameter);
			
			JSONObject jsonObj = new JSONObject();
			
			if(map == null || map.isEmpty()) {
				jsonObj.put("code", RESULT.HTTP_PARAM_ERROR);
				jsonObj.put("msg", "bad parameter data");
				jsonObj.put("param", "all");
				return jsonObj.toString();
			}
			
			BaseVO baseVO = getPickupInfo(map);
			String vaildParam = checkParameter(baseVO , APITYPE.API_PICKUP);
			if(!vaildParam.equals("OK")) {
				jsonObj.put("code", RESULT.HTTP_PARAM_ERROR);
				jsonObj.put("msg", "bad parameter data");
				jsonObj.put("param", vaildParam);
				return jsonObj.toString();
			}
			
			PickupVO pickupVO = (PickupVO) baseVO;
//			JtapiService.getInstance().monitorStop(resetVO.getExtension());
//			JTapiResultVO resultVO = JtapiService.getInstance().monitorStart(resetVO.getExtension());
			
			logwrite.httpLog(requestID, "procCallPickup", "DEVICE MONITOR RESULT CODE [" + resultVO.getCode() + "] MESSAGE [" + resultVO.getMessage() + "]");
			
			jsonObj.put("code", resultVO.getCode());
			jsonObj.put("msg", resultVO.getMessage());
			return jsonObj.toString();
		}
		
		private String procCallStatus (String parameter , String requestID) {
			Map <String, String> map = new HashMap<String, String>();
			map = queryToMap(parameter);
			
			JSONObject jsonObj = new JSONObject();
			
			if(map == null || map.isEmpty()) {
				jsonObj.put("code", RESULT.HTTP_PARAM_ERROR);
				jsonObj.put("msg", "bad parameter data");
				jsonObj.put("param", "all");
				return jsonObj.toString();
			}
			
			BaseVO baseVO = getDeviceStatusInfo(map);
			String vaildParam = checkParameter(baseVO , APITYPE.API_CALLSTATUS);
			if(!vaildParam.equals("OK")) {
				jsonObj.put("code", RESULT.HTTP_PARAM_ERROR);
				jsonObj.put("msg", "bad parameter data");
				jsonObj.put("param", vaildParam);
				return jsonObj.toString();
			}
			
			String resultMsg = "success";
			DeviceStatusVO devStatusVO = (DeviceStatusVO) baseVO;
			
			JSONArray resultArr = new JSONArray();
			List<String> extensionList = devStatusVO.getExtensionList();
			for (int i = 0; i < extensionList.size(); i++) {
				String extension = extensionList.get(i);
				int deviceStatus = 0;
				if(CallStateMgr.getInstance().getDeviceState(extension) == null) {
					deviceStatus = CALLSTATE.IDLE;
				} else {
					deviceStatus = CallStateMgr.getInstance().getDeviceState(extension);
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
			
			jsonObj.put("code", 200);
			jsonObj.put("msg", "success");
			jsonObj.put("list", resultArr);
			return jsonObj.toString();
		}
		
		private String procResetDevice (String parameter , String requestID) {
			Map <String, String> map = new HashMap<String, String>();
			map = queryToMap(parameter);
			
			JSONObject jsonObj = new JSONObject();
			
			if(map == null || map.isEmpty()) {
				jsonObj.put("code", RESULT.HTTP_PARAM_ERROR);
				jsonObj.put("msg", "bad parameter data");
				jsonObj.put("param", "all");
				return jsonObj.toString();
			}
			
			BaseVO baseVO = getDeviceResetInfo(map);
			String vaildParam = checkParameter(baseVO , APITYPE.API_DEVICERESET);
			if(!vaildParam.equals("OK")) {
				jsonObj.put("code", RESULT.HTTP_PARAM_ERROR);
				jsonObj.put("msg", "bad parameter data");
				jsonObj.put("param", vaildParam);
				return jsonObj.toString();
			}
			
			DeviceResetVO resetVO = (DeviceResetVO) baseVO;
			JtapiService.getInstance().monitorStop(resetVO.getExtension());
			JTapiResultVO resultVO = JtapiService.getInstance().monitorStart(resetVO.getExtension());
			
			logwrite.httpLog(requestID, "procResetDevice", "DEVICE MONITOR RESULT CODE [" + resultVO.getCode() + "] MESSAGE [" + resultVO.getMessage() + "]");
			
			jsonObj.put("code", resultVO.getCode());
			jsonObj.put("msg", resultVO.getMessage());
			return jsonObj.toString();
		}
		
		private String procLogout  (String parameter, String requestID) {
			Map <String, String> map = new HashMap<String, String>();
			map = queryToMap(parameter);
			
			JSONObject jsonObj = new JSONObject();
			
			if(map == null || map.isEmpty()) {
				jsonObj.put("code", RESULT.HTTP_PARAM_ERROR);
				jsonObj.put("msg", "bad parameter data");
				jsonObj.put("param", "all");
				return jsonObj.toString();
			}
			
			BaseVO baseVO = getEmployeeInfo(map);
			String vaildParam = checkParameter(baseVO , APITYPE.API_LOGOUT);
			if(!vaildParam.equals("OK")) {
				jsonObj.put("code", RESULT.HTTP_PARAM_ERROR);
				jsonObj.put("msg", "bad parameter data");
				jsonObj.put("param", vaildParam);
				return jsonObj.toString();
			}
			
			EmployeeVO empVO = (EmployeeVO) baseVO;
			JTapiResultVO resultVO = JtapiService.getInstance().monitorStop(empVO.getExtension());
			int loginResult = Employees.getInstance().logoutEmployee(empVO , requestID);
			
			logwrite.httpLog(requestID, "procLogout", "DEVICE MONITOR RESULT CODE [" + resultVO.getCode() + "] MESSAGE [" + resultVO.getMessage() + "]");
			logwrite.httpLog(requestID, "procLogout", "EMPLOYEE LOGOUT RESULT CODE [" + loginResult + "]");
			
			
			jsonObj.put("code", resultVO.getCode());
			jsonObj.put("msg", resultVO.getMessage());
			return jsonObj.toString();
			
			
		}
		private String procLogin  (String parameter, String requestID) {
			
			Map <String, String> map = new HashMap<String, String>();
			map = queryToMap(parameter);
			
			JSONObject jsonObj = new JSONObject();
			
			if(map == null || map.isEmpty()) {
				jsonObj.put("code", RESULT.HTTP_PARAM_ERROR);
				jsonObj.put("msg", "bad parameter data");
				jsonObj.put("param", "all");
				return jsonObj.toString();
			}
			BaseVO baseVO = getEmployeeInfo(map);
			String vaildParam = checkParameter(baseVO , APITYPE.API_LOGIN);
			if(!vaildParam.equals("OK")) {
				jsonObj.put("code", RESULT.HTTP_PARAM_ERROR);
				jsonObj.put("msg", "bad parameter data");
				jsonObj.put("param", vaildParam);
				return jsonObj.toString();
			}
			
			EmployeeVO empVO = (EmployeeVO) baseVO;
			
			/*
			 * 전화기 상태체크
			 */
			if(!DeviceStatusHandler.getInstance().isRegisteredDevice(empVO.getMac_address())) {
				logwrite.httpLog(requestID, "procLogin", empVO.getMac_address() + " is not Registered!!");
			}
			//////////////////////////////////////////////////////////////////////////////////////////
			JTapiResultVO resultVO = JtapiService.getInstance().monitorStart(empVO.getExtension());
			int loginResult = Employees.getInstance().loginEmployee(empVO , requestID);
			
			logwrite.httpLog(requestID, "procLogin", "DEVICE MONITOR RESULT CODE [" + resultVO.getCode() + "] MESSAGE [" + resultVO.getMessage() + "]");
			logwrite.httpLog(requestID, "procLogin", "EMPLOYEE LOGIN RESULT CODE [" + loginResult + "]");
			
			
			jsonObj.put("code", resultVO.getCode());
			jsonObj.put("msg", resultVO.getMessage());
			return jsonObj.toString();
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
						pickupVO.setPickupExtension(map.get("setMyExtension").toString());
						break;
					default : 
						break;
				}
			}

			return pickupVO;
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

