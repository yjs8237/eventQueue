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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.isi.constans.LOGLEVEL;
import com.isi.constans.LOGTYPE;
import com.isi.constans.PROPERTIES;
import com.isi.constans.RESULT;
import com.isi.data.Employees;
import com.isi.data.XmlInfoMgr;
import com.isi.db.JDatabase;
import com.isi.exception.ExceptionUtil;
import com.isi.file.*;
import com.isi.vo.CustomerVO;
import com.isi.vo.EmployeeVO;
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
					
				default:
					returnCode = RESULT.HTTP_URL_ERROR;
					break;
				}
				
			} catch(Exception  e){
				e.printStackTrace(ExceptionUtil.getPrintWriter());
				logwrite.httpLog(requestID ,"procGet()", ExceptionUtil.getStringWriter().toString());
				returnCode = RESULT.RTN_EXCEPTION;
			}
			
			return returnCode == RESULT.RTN_SUCCESS ? RESULT.HTTP_SUCCESS : returnCode;
			
		}
		
		private int procLogout  (String parameter, String requestID) {
			Map <String, String> map = new HashMap<String, String>();
			map = queryToMap(parameter);
			
			if(map == null) {
				return RESULT.HTTP_PARAM_ERROR;
			}
			
			if(map.isEmpty()) {
				return RESULT.HTTP_PARAM_ERROR;
			}
			
			EmployeeVO employee = getEmployeeInfo(map);
			if(employee.getEm_ID() == null || employee.getEm_ID().isEmpty() || employee.getEm_ID().equals("null")){
				logwrite.httpLog(requestID , "procLogout()", "getEm_ID 정보 없음 !!");
				return RESULT.HTTP_PARAM_ERROR;
			}
			
			/*
			 * 로그아웃기능 개발 메모리에서 삭제
			 */
			
			return RESULT.RTN_SUCCESS;
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
			
			EmployeeVO employee = getEmployeeInfo(map);
			if(employee.getEmp_id() == null || employee.getEmp_id().isEmpty() || employee.getEmp_id().equals("null")){
				logwrite.httpLog(requestID , "procLogin()", "getEm_ID 정보 없음 !!");
				jsonObj.put("code", RESULT.HTTP_PARAM_ERROR);
				jsonObj.put("msg", "bad parameter data");
				jsonObj.put("param", "emp_id");
				return jsonObj.toString();
			}
			
			
			String vaildParam = checkParameter(employee);
			if(!vaildParam.equals("OK")) {
				jsonObj.put("code", RESULT.HTTP_PARAM_ERROR);
				jsonObj.put("msg", "bad parameter data");
				jsonObj.put("param", vaildParam);
				return jsonObj.toString();
			}
			
			Employees.getInstance().getEmployeeByCellNum(employee.getCell_no(), callID)
			
			
			/*
			 * 로그인 구현 로직 개발
			 * 이미 직원 정보가 메모리에 올라가 있으면 -> 업데이트 , 없으면 -> 추가
			 * 
			 */
			
			return RESULT.RTN_SUCCESS;
		}
		
		
		private String checkParameter (EmployeeVO employee) {
			String result = "OK";
			String checkData = "";
			try {
				
				Class targetClass = Class.forName("com.isi.vo.EmployeeVO");
				Method methods[] = targetClass.getDeclaredMethods();
				
				for (int i = 0; i < methods.length; i++) {
					String methodName = methods[i].getName();
					if(methodName.startsWith("get")) {
						Object obj = methods[i].invoke(employee);
						if(obj == null || obj.toString().isEmpty()) {
							return methodName.replaceAll("get", "").toLowerCase();
						}
					}
				}
				
			} catch (Exception e) {
			}
			
			return result;
		}
		
		
	
		private int procPickup (String parameter, String requestID) {
			
			Map <String, String> map = new HashMap<String, String>();
			map = queryToMap(parameter);
			
			if(map == null) {
				return RESULT.HTTP_PARAM_ERROR;
			}
			
			if(map.isEmpty()) {
				return RESULT.HTTP_PARAM_ERROR;
			}
			
			EmployeeVO employee = getEmployeeInfo(map);
			if(employee.getEm_ID() == null || employee.getEm_ID().isEmpty() || employee.getEm_ID().equals("null")){
				logwrite.httpLog(requestID , "procPickup()", "getEm_ID 정보 없음 !!");
				return RESULT.HTTP_PARAM_ERROR;
			}
			
			/*
			 * 픽업그룹 가져오는 로직 개발
			 */
			
			
			return RESULT.RTN_SUCCESS;
		}

		private int procUpdateAll(String parameter, String requestID) {
			// TODO Auto-generated method stub
			// 직원정보 갱신 ( DB 기준 )
			Employees.getInstance().getEmployeeList();
			return RESULT.RTN_SUCCESS;
		}

		private int procUpdateImg(String parameter, String requestID) {
			// TODO Auto-generated method stub
			Map <String, String> map = new HashMap<String, String>();
			map = queryToMap(parameter);
			
			if(map == null) {
				return RESULT.HTTP_PARAM_ERROR;
			}
			
			if(map.isEmpty()) {
				return RESULT.HTTP_PARAM_ERROR;
			}
			
			EmployeeVO employee = getEmployeeInfo(map);
			
			if(employee.getEm_ID() == null || employee.getEm_ID().isEmpty() || employee.getEm_ID().equals("null")){
				logwrite.httpLog(requestID , "procUpdateImg()", "getEm_ID 정보 없음 !! UPDATE 무시");
				return RESULT.HTTP_PARAM_ERROR;
			}
			
			int retCode = Employees.getInstance().updateEmpImage(employee , requestID);
			
			if(retCode == RESULT.RTN_SUCCESS){
				logwrite.httpLog(requestID , "procUpdateImg()", "UPDATE SUCCESS !! ");
			} else {
				logwrite.httpLog(requestID , "procUpdateImg()", "UPDATE FAIL !! ");
			}
			
			return retCode;
			
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

		private int procSelect(String parameter, String requestID) {
			// TODO Auto-generated method stub
			Map <String, String> map = new HashMap<String, String>();
			map = queryToMapExtension(parameter);
			String stExtension = map.get("extension");
			employeeList = Employees.getInstance().getAllEmployee(stExtension , "");
			logwrite.httpLog(requestID, "procSelect", "employeeList size -> " + employeeList.size());
			if(employeeList == null){
				return RESULT.RTN_EXCEPTION;
			} else {
				return RESULT.RTN_SUCCESS;
			}
		}
		
		private int procDelete(String parameter , String requestID) {
			// TODO Auto-generated method stub
			Map <String, String> map = new HashMap<String, String>();
			map = queryToMap(parameter);
			
			if(Employees.getInstance().deleteEmployee(map.get("")) == null){
				return RESULT.HTTP_PARAM_ERROR;
			} else {
				return RESULT.RTN_SUCCESS;
			}
			
		}
		private int procUpdate(String parameter , String requestID) {
			// TODO Auto-generated method stub
			Map <String, String> map = new HashMap<String, String>();
			map = queryToMap(parameter);
			
			if(map == null) {
				return RESULT.HTTP_PARAM_ERROR;
			}
			
			if(map.isEmpty()) {
				return RESULT.HTTP_PARAM_ERROR;
			}
			
			EmployeeVO employee = getEmployeeInfo(map);
			
			if(employee.getDN() == null || employee.getDN().isEmpty() || employee.getDN().equals("null")){
				logwrite.httpLog(requestID , "procUpdate()", "DN 정보 없음 !! UPDATE 무시");
				return RESULT.HTTP_PARAM_ERROR;
			}
			
			employee = nullCheckObj(employee);
			
			logwrite.httpLog(requestID , "procUpdate()", "UPDATE 요청 정보 -> " + employee.toString());
			
			int retCode = Employees.getInstance().updateEmployee(employee , requestID);
		
			if(retCode == RESULT.RTN_SUCCESS){
				logwrite.httpLog(requestID , "procUpdate()", "UPDATE SUCCESS !! ");
			} else {
				logwrite.httpLog(requestID , "procUpdate()", "UPDATE FAIL !! ");
			}
			
			return retCode;
		}

		private EmployeeVO nullCheckObj(EmployeeVO employee) {
			// TODO Auto-generated method stub
			if(employee.getCmIP() == null) { employee.setCmIP(""); }
			if(employee.getCmPass() == null) { employee.setCmPass(""); }
			if(employee.getCmUser() == null) { employee.setCmUser(""); }
			if(employee.getDeviceType() == null) { employee.setDeviceType(""); }
			if(employee.getDN() == null) { employee.setDN(""); }
			if(employee.getEm_ID() == null) { employee.setEm_ID(""); }
			if(employee.getEm_name() == null) { employee.setEm_name(""); }
			if(employee.getEm_position() == null) { employee.setEm_position(""); }
			if(employee.getGroupNm() == null) { employee.setGroupNm(""); }
			if(employee.getIpAddr() == null) { employee.setIpAddr(""); }
			if(employee.getMacaddress() == null) { employee.setMacaddress(""); }
			if(employee.getPopupYN() == null) { employee.setPopupYN(""); }
			return employee;
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

