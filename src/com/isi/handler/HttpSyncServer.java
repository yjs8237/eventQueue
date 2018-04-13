package com.isi.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.URI;
import java.sql.Connection;
import java.util.ArrayList;
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
import com.isi.vo.ImageSyncVO;
import com.isi.vo.JTapiResultVO;
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
public class HttpSyncServer {
	
	private int port;
	private PropertyRead pr;
	private ILog logwrite;
	
	public HttpSyncServer(){
		pr = PropertyRead.getInstance();
		logwrite = new GLogWriter();
	}
	
	public int startService(){
		
		try {
			
			port = XmlInfoMgr.getInstance().getHttp_sync_port();
			
			InetSocketAddress addr = new InetSocketAddress(port);
			HttpServer server = HttpServer.create(addr, 0);
			
			server.createContext("/", new HttpSyncHandler());
			server.setExecutor(null);
			server.start();
			
			return RESULT.RTN_SUCCESS;
			
		} catch (Exception e){
			e.printStackTrace(ExceptionUtil.getPrintWriter());
			logwrite.httpLog("" ,"startService()", ExceptionUtil.getStringWriter().toString());
			return RESULT.RTN_EXCEPTION;
		}
	}
	
	
	private class HttpSyncHandler implements HttpHandler{
		
		String requestMethod = "";
		
		@Override
		public void handle(HttpExchange exchange) throws IOException {
			// TODO Auto-generated method stub
			
			String requestID = String.valueOf(System.currentTimeMillis()) + "-" + String.valueOf(Thread.currentThread().getName()) + String.valueOf(Thread.currentThread().getId());
			String responseDATA = "";
			requestMethod = exchange.getRequestMethod().toUpperCase().trim();
			
			System.out.println("TEST requestMethod : " + requestMethod);
			
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

//				System.out.println("url -> " + url);

				url = getURL(url);

				logwrite.httpLog(requestID, "procGet()", "REQUEST URL[" + url + "] PARAM[" + parameter + "]");

				switch (url) {
				
				case "/loginsync":
					resultJSONData = procCreateImage(parameter, requestID);
					break;
					
				case "/imagesync":
					resultJSONData = procImageSync(parameter, requestID);
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
		
		
		private String procImageSync (String parameter , String requestID) {
			Map <String, String> map = new HashMap<String, String>();
			map = queryToMap(parameter);
			
			JSONObject jsonObj = new JSONObject();
			
			if(map == null || map.isEmpty()) {
				jsonObj.put("code", String.valueOf(RESULT.HTTP_PARAM_ERROR));
				jsonObj.put("msg", "bad parameter data");
				jsonObj.put("param", "all");
				return jsonObj.toString();
			}
			ImageSyncVO imageSyncVO = getImageSyncInfo(map);
			
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
		
		
		private ImageSyncVO getImageSyncInfo (Map<String, String> map) {
			// TODO Auto-generated method stub
			ImageSyncVO imageSyncVO = new ImageSyncVO();
			
			Set keySet = map.keySet();
			Iterator iter = keySet.iterator();
			while (iter.hasNext()) {
				String key = (String) iter.next();
				switch (key) {
					case "caller_type" :
						imageSyncVO.setCaller_type(map.get("caller_type").toString());
						break;
					case "callingNumber" :
						imageSyncVO.setCallingNumber(map.get("callingNumber").toString());
						break;
					case "emp_id":
						imageSyncVO.setEmp_id(getEmployeeInfo(map, key));
						break;
					case "emp_lno":
						imageSyncVO.setEmp_lno(getEmployeeInfo(map, key));
						break;
					case "emp_nm_kor":
						imageSyncVO.setEmp_nm_kor(getEmployeeInfo(map, key));
						break;
					case "emp_nm_eng":
						imageSyncVO.setEmp_nm_eng(getEmployeeInfo(map, key));
						break;
					case "org_nm":
						imageSyncVO.setOrg_nm(getEmployeeInfo(map, key));
						break;
					case "pos_nm":
						imageSyncVO.setPos_nm(getEmployeeInfo(map, key));
						break;
					case "duty_nm":
						imageSyncVO.setDuty_nm(getEmployeeInfo(map, key));
						break;
					case "extension":
						imageSyncVO.setExtension(getEmployeeInfo(map, key));
						break;
					case "email":
						imageSyncVO.setEmail(getEmployeeInfo(map, key));
						break;
					case "cell_no":
						imageSyncVO.setCell_no(getEmployeeInfo(map, key));
						break;
					case "building":
						imageSyncVO.setBuilding(getEmployeeInfo(map, key));
						break;
					case "floor":
						imageSyncVO.setFloor(getEmployeeInfo(map, key));
						break;
					case "emp_stat_nm":
						imageSyncVO.setEmp_stat_nm(getEmployeeInfo(map, key));
						break;
					case "emp_div_cd_nm":
						imageSyncVO.setEmp_div_cd_nm(getEmployeeInfo(map, key));
						break;
					case "popup_svc_yn":
						imageSyncVO.setPopup_svc_yn(getEmployeeInfo(map, key));
						break;
					case "mac_address":
						imageSyncVO.setMac_address(getEmployeeInfo(map, key));
						break;
					case "device_ipaddr":
						imageSyncVO.setDevice_ipaddr(getEmployeeInfo(map, key));
						break;
					case "device_type":
						imageSyncVO.setDevice_type(getEmployeeInfo(map, key));
						break;
					case "cm_ver":
						imageSyncVO.setCm_ver(getEmployeeInfo(map, key));
						break;
					case "cm_ip":
						imageSyncVO.setCm_ip(getEmployeeInfo(map, key));
						break;
					case "cm_user":
						imageSyncVO.setCm_user(getEmployeeInfo(map, key));
						break;
					case "cm_pwd":
						imageSyncVO.setCm_pwd(getEmployeeInfo(map, key));
						break;
						
					default : 
						break;
				}
			}
			return imageSyncVO;
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

