package com.isi.handler;

import java.io.IOException;
import java.io.OutputStream;
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
			
			requestMethod = exchange.getRequestMethod().toUpperCase().trim();
			int retCode = RESULT.RTN_EXCEPTION;
			switch (requestMethod) {
			case "GET":
				retCode = procGet(exchange , requestID);
				break;
				
			case "POST":
				
				break;
			default:
				break;
			}
			
			String responseDATA = "";
			JsonHandler jsonHandler = new JsonHandler();

			URI uri = exchange.getRequestURI();
			String url = uri.toString().trim();
			url = getURL(url);
			if(url.equals("/select")) {
				//	
			} else {
				responseDATA = jsonHandler.getResponseJson(retCode).toString();
//				logwrite.httpLog(requestID, "handle", "[" + url + "]");
			}
			
			exchange.sendResponseHeaders(200, responseDATA.length());
			
			OutputStream responseBody = exchange.getResponseBody();
			logwrite.httpLog(requestID, "handle", "JSON RETURN DATA #[" + responseDATA + "]#");
			
			responseBody.write(responseDATA.getBytes());
			responseBody.close();
			
		}
	
		private int procGet(HttpExchange exchange, String requestID) {
			
			int returnCode = RESULT.RTN_EXCEPTION;
			
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
				
				
				
				
				
				// 인사정보 전체 일괄 업데이트
				case "/updateAll" :
					returnCode = procUpdateAll(parameter , requestID);
					break;
					
				// 특정 직원 인사정보 업데이트
				case "/update":
					returnCode = procUpdate(parameter , requestID);
					break;
					
				// 사용자 추가
				case "/register":
					returnCode = procRegister(parameter , requestID);
					break;
				
				case "/delete":
					returnCode = procDelete(parameter , requestID);
					break;
					
				case "pickup":
					returnCode = procPickup(parameter , requestID);
					break;
					
				case "login":
					returnCode = procLogin(parameter , requestID);
					break;
					
				case "logout":
					returnCode = procLogout(parameter , requestID);
					break;
					/*
				case "/select":
					returnCode = procSelect(parameter , requestID);
					break;
					
				case "/updateImg":
					returnCode = procUpdateImg(parameter , requestID);
					break;
				*/	
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
		private int procLogin  (String parameter, String requestID) {
			
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
				logwrite.httpLog(requestID , "procLogin()", "getEm_ID 정보 없음 !!");
				return RESULT.HTTP_PARAM_ERROR;
			}
			
			/*
			 * 로그인 구현 로직 개발
			 * 이미 직원 정보가 메모리에 올라가 있으면 -> 업데이트 , 없으면 -> 추가
			 * 
			 */
			
			return RESULT.RTN_SUCCESS;
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

		private int procRegister(String parameter , String requestID) {
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
			
			int retCode = Employees.getInstance().addEmployee(employee);
			
			if(retCode == RESULT.RTN_SUCCESS){
				logwrite.httpLog(requestID , "procRegister()", "REGISTER SUCCESS !! " + employee.toString());
			} else {
				logwrite.httpLog(requestID , "procRegister()", "REGISTER FAIL !! " + employee.toString());
			}
			return retCode;
		}
		
		private EmployeeVO getEmployeeInfo(Map<String, String> map) {
			// TODO Auto-generated method stub
			EmployeeVO employee = new EmployeeVO();
			Set keySet = map.keySet();
			Iterator iter = keySet.iterator();
			while(iter.hasNext()){
				String key = (String) iter.next();
				switch(key){
				case "empId" :
					employee.setEm_ID(getEmployeeInfo(map,key));
					break;
				case "mac" :
					employee.setMacaddress(getEmployeeInfo(map,key));
					break;
				case "extension" :
					employee.setDN(getEmployeeInfo(map,key));
					break;
				case "orgNm" :
					employee.setGroupNm(getEmployeeInfo(map,key));
					break;
				case "empNm" :
					employee.setEm_name(getEmployeeInfo(map,key));
					break;
				case "empGradeNm" :
					employee.setEm_position(getEmployeeInfo(map,key));
					break;
				case "cmIp" :
					employee.setCmIP(getEmployeeInfo(map,key));
					break;
				case "deviceType" :
					employee.setDeviceType(getEmployeeInfo(map,key));
					break;
				case "deviceIpaddr" :
					employee.setIpAddr(getEmployeeInfo(map,key));
					break;
				case "cmUser" :
					employee.setCmUser(getEmployeeInfo(map,key));
					break;
				case "cmPwd" :
					employee.setCmPass(getEmployeeInfo(map,key));
					break;
				case "popupSvcYn" :
					employee.setPopupYN(getEmployeeInfo(map,key));
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

