package com.isi.test;

import java.net.HttpURLConnection;
import java.net.URL;

import com.test.main.CmAxlInfoModel;
import com.test.soap.AxlHandler;

public class TestMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		/*
		EmployeeVO employeeVO = new EmployeeVO();
		employeeVO.setCmUser("xmluser");
		employeeVO.setCmPass("!Insung2018#");
		employeeVO.setCmIP("192.168.230.120");
		employeeVO.setDeviceType("119");
		employeeVO.setDN("1001");
		employeeVO.setEm_ID("s001");
		employeeVO.setEm_name("윤지상");
		employeeVO.setEm_position("대리");
		employeeVO.setGroupNm("인성");
		employeeVO.setIpAddr("192.168.20.242");
		
		PushHandler push = new PushHandler("1");
		
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?><CiscoIPPhoneImageFile><Title>Ringing</Title><Prompt>1002</Prompt><LocationX>0</LocationX><LocationY>0</LocationY><URL>http://192.168.20.248:8080/static/images/em/298168/1002.png</URL></CiscoIPPhoneImageFile>");
//		sb.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?><CiscoIPPhoneImageFile><Title>Ringing</Title><Prompt>1002</Prompt><LocationX>0</LocationX><LocationY>0</LocationY><URL>http://192.168.0.23:8080/static/images/back.png</URL></CiscoIPPhoneImageFile>");
		
		XmlVO xmlInfo = new XmlVO();
		xmlInfo.setAlertingdn("1001");
		xmlInfo.setCalledDn("1002");
		xmlInfo.setCmPassword("!Insung2018#");
		xmlInfo.setCmUser("xmluser");
		xmlInfo.setTargetIP("192.168.20.247");
		xmlInfo.setTargetModel("493");
		
		push.push(sb.toString(), xmlInfo, true);
		*/
		
//		com.test.soap.SOAPHandler handler = new com.test.soap.SOAPHandler();
//		try {
//			handler.sendSoapMessage("SELECT * FROM device");
//		} catch(Exception e) {
//			
//		}
		
		CmAxlInfoModel model = new CmAxlInfoModel();
		model.setCmID("xmluser");
		model.setCmIP("192.168.230.120");
		model.setCmPwd("!Insung2018#");
		
		AxlHandler.testSoap(model);
		
	}
	
	
	
	public static void test1() {
		
		String recv_msg = "C08^40006^40006";
		int idx = 0;
		int idx2 = 0;
		int idx3 = 0;
		idx = recv_msg.indexOf("^");
		idx2 = recv_msg.indexOf("^", idx + 1);
		// idx3 = recv_msg.indexOf("^", idx2 + 1);

		// C09을 제외하고 RCKD^RCK^TELNO만 추출
		// temp_1 = recv_msg.substring(idx+1, recv_msg.length());
		// C09을 제외하고 CallKey^TELNO만 추출
		String temp_1 = recv_msg.substring(idx + 1, recv_msg.length());
		System.out.println(temp_1);
		    
	}
	
	
	public static void test() {
		try {
			
			URL url = new URL("http://192.168.20.248:8080/static/images/em/298144/1001.png");
			
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			
			conn.setRequestMethod("GET");
			
			conn.setConnectTimeout(3000);
			conn.setReadTimeout(3000);
			
			
			
			
			int cnt = 0;
			while(true) {
				cnt++;
				Thread.sleep(1000);
				
				if(cnt > 160) {
					break;
				}
				
				if(conn == null) {
					System.out.println("cnt -> " +  cnt + " , null");
				} else {
					System.out.println("cnt -> " +  cnt + " , not");
				}
			}
			
			
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		
		
		
	}

}
