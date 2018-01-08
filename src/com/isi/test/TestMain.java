package com.isi.test;

import com.isi.handler.PushHandler;
import com.isi.vo.EmployeeVO;
import com.isi.vo.XmlVO;

public class TestMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
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
		sb.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?><CiscoIPPhoneImageFile><Title>Ringing</Title><Prompt>1002</Prompt><LocationX>0</LocationX><LocationY>0</LocationY><URL>http://192.168.0.24:8080/static/images/em/298168/1002.png</URL></CiscoIPPhoneImageFile>");
		
		XmlVO xmlInfo = new XmlVO();
		xmlInfo.setAlertingdn("1001");
		xmlInfo.setCalledDn("1002");
		xmlInfo.setCmPassword("!Insung2018#");
		xmlInfo.setCmUser("xmluser");
		xmlInfo.setTargetIP("192.168.20.242");
		xmlInfo.setTargetModel("119");
		
		push.push(sb.toString(), xmlInfo, true);
		
	}

}
