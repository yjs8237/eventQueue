package com.test.soap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import com.test.vo.CmAxlInfoModel;


public class AxlHandler {
	
	private static final String cmVer = "8.5";
	
	public Object testSoap (CmAxlInfoModel cmAxlInfo , String query) {
		StringBuffer queryBuffer = new StringBuffer();
		return requestSelectSoap(cmAxlInfo , getQuerySoapMessage(query));
	}
	
	
	/*
	public  JSONObject insertPickupGroup (CmAxlInfoModel cmAxlInfo, PickupGroupModel model , PickupGroupConfigModel configModel) {
		StringBuffer queryBuffer = new StringBuffer();
		
//		String query = (new StringBuilder().append(pickupGroup.getId()).append("', '4', '").append(pickupGroup.getName()).append("', '").append(fkRoutePartition).toString();
		queryBuffer.append("insert into numplan(dnorpattern, tkpatternusage, description, fkRoutePartition)  values('");
		queryBuffer.append(model.getPickup_grp_num()).append("', '4', '");
		queryBuffer.append(model.getPickup_grp_name()).append("', '");
		queryBuffer.append(configModel.getRoute_partition_key()).append("')");
		
		// insert numplan
//		sendSoapMessage(cmAxlInfo , getSendSoapMessage(queryBuffer.toString()));
//		
//		new StringBuilder("select pkid, dnorpattern from numplan where tkpatternusage = '4' and dnorpattern = '")).append(pickupGroup.getId()).append("' and fkroutepartition = '").append(fkRoutePartition).append("'").toString());
//		queryBuffer = new StringBuffer();
		
		
		return sendSoapMessage(cmAxlInfo , getUpdateSoapMessage(queryBuffer.toString())) ;
	}
	
	*/
	
	
	private  String getQuerySoapMessage (String query) {
		
		StringBuffer soapReqMessage = new StringBuffer();
		soapReqMessage.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns=\"http://www.cisco.com/AXL/API/"+cmVer+"\">\n");
		soapReqMessage.append("<soapenv:Header/>\n");
		soapReqMessage.append("<soapenv:Body>\n");
		soapReqMessage.append("<ns:executeSQLQuery>\n");
		soapReqMessage.append("<sql>\n");
		soapReqMessage.append(query).append("\n");
		soapReqMessage.append("</sql>\n");
		soapReqMessage.append("</ns:executeSQLQuery>\n");
		soapReqMessage.append("</soapenv:Body>\n");
		soapReqMessage.append("</soapenv:Envelope>\n");
		
		return soapReqMessage.toString();
	}
	
	
	private  String getUpdateSoapMessage (String query) {
		
		StringBuffer soapReqMessage = new StringBuffer();
		soapReqMessage.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns=\"http://www.cisco.com/AXL/API/"+cmVer+"\">\n");
		soapReqMessage.append("<soapenv:Header/>\n");
		soapReqMessage.append("<soapenv:Body>\n");
		soapReqMessage.append("<ns:executeSQLUpdate>\n");
		soapReqMessage.append("<sql>\n");
		soapReqMessage.append(query).append("\n");
		soapReqMessage.append("</sql>\n");
		soapReqMessage.append("</ns:executeSQLUpdate>\n");
		soapReqMessage.append("</soapenv:Body>\n");
		soapReqMessage.append("</soapenv:Envelope>\n");
		
		return soapReqMessage.toString();
	}
	
	
	private Object requestSelectSoap(CmAxlInfoModel cmAxlInfo , String soapReqMessage) {
		
		Object resultObj = null;
		
		JSONObject xmlJson = sendSoapMessage(cmAxlInfo, soapReqMessage);
		
		if(xmlJson.getJSONObject("soapenv:Envelope")
				.getJSONObject("soapenv:Body")
				.getJSONObject("ns:executeSQLQueryResponse").get("return").equals("")){
			
			return resultObj;
		}else{
		}
		
		JSONObject chkJson = xmlJson.getJSONObject("soapenv:Envelope")
				.getJSONObject("soapenv:Body")
				.getJSONObject("ns:executeSQLQueryResponse")
				.getJSONObject("return");
		
		Object test = chkJson.get("row");
		
		if(test instanceof JSONObject){
			JSONObject jsonData = chkJson.getJSONObject("row");
			resultObj = jsonData;
		}else if(test instanceof JSONArray){
			JSONArray jsonArray = chkJson.getJSONArray("row");
			resultObj = jsonArray;
		}
		
		return resultObj;
	}
	
	private  JSONObject sendSoapMessage(CmAxlInfoModel cmAxlInfo , String soapReqMessage) {
		
//		System.out.println(" :::: Soap Send Message :::: ");
//		System.out.println(soapReqMessage.toString());
		
		String strResult = SoapHandler.RequestSoap(cmVer, cmAxlInfo.getCmID(), cmAxlInfo.getCmPwd(), cmAxlInfo.getCmIP(), "8443", soapReqMessage.toString(), "executeSQLQuery");
		
		JSONObject xmlJson = null;
		try {
			xmlJson = XML.toJSONObject(strResult);
			System.out.println(xmlJson.toString(4));
		
		} catch (JSONException e) {
			System.out.println(e.toString());
		}
		
		return xmlJson;
	}
	
	private static Object selectDeviceInfoJSON01(CmAxlInfoModel cmAxlInfo) {
		
		Object rtnObject = null;
		
		StringBuffer soapReqMessage = new StringBuffer();
		
//	     strSoapReqeust +="<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" ";
//	        strSoapReqeust +="xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ";
//	        strSoapReqeust +="xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"> ";
//	        strSoapReqeust +="<SOAP-ENV:Body> ";
//	        strSoapReqeust +="<axlapi:executeSQLQuery xmlns:axlapi=\"http://www.cisco.com/AXL/API/"+dbVer+"\" sequence=\"1234\"> ";
//	        strSoapReqeust +="<sql> SELECT name, tkClass FROM device WHERE name LIKE \'" + aLike + "%\' </sql> ";
//	        strSoapReqeust +="</axlapi:executeSQLQuery> ";
//	        strSoapReqeust +="</SOAP-ENV:Body> ";
//	        strSoapReqeust +="</SOAP-ENV:Envelope>";
//		
		 //
		soapReqMessage.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns=\"http://www.cisco.com/AXL/API/"+cmVer+"\">\n");
		soapReqMessage.append("<soapenv:Header/>\n");
		soapReqMessage.append("<soapenv:Body>\n");
		soapReqMessage.append("<ns:executeSQLQuery>\n");
		soapReqMessage.append("<sql>\n");
		soapReqMessage.append("SELECT name, tkClass FROM device \n");
		soapReqMessage.append("</sql>\n");
		soapReqMessage.append("</ns:executeSQLQuery>\n");
		soapReqMessage.append("</soapenv:Body>\n");
		soapReqMessage.append("</soapenv:Envelope>\n");
		
		//System.out.println("aa = "+strXML);
		
		String strResult = SoapHandler.RequestSoap(cmVer, cmAxlInfo.getCmID(), cmAxlInfo.getCmPwd(), cmAxlInfo.getCmIP(), "8443", soapReqMessage.toString(), "executeSQLQuery");
		System.out.println("json return");
		System.out.println(strResult);
		
		try {
			JSONObject xmlJson = XML.toJSONObject(strResult);
			String jsonP = xmlJson.toString(4);
			System.out.println(jsonP);
			if(jsonP.equals("{}")){
				return rtnObject;
			}
			
//			Iterator iter = xmlJson.keys();
//			while(iter.hasNext()) {
//				
//				if(iter.next() instanceof JSONObject) {
//					System.out.println("** JSONObject **");
//				}
//				
//				String key =  iter.next().toString();
//				System.out.println(key + " : " + xmlJson.getString(key));
//			}
			
			if(xmlJson.getJSONObject("soapenv:Envelope")
					.getJSONObject("soapenv:Body")
					.getJSONObject("ns:executeSQLQueryResponse").get("return").equals("")){
				
				return rtnObject;
			}else{
			}
			
			JSONObject chkJson = xmlJson.getJSONObject("soapenv:Envelope")
					.getJSONObject("soapenv:Body")
					.getJSONObject("ns:executeSQLQueryResponse")
					.getJSONObject("return");
			
			Object test = chkJson.get("row");
			
			if(test instanceof JSONObject){
				JSONObject jsonData = chkJson.getJSONObject("row");
				rtnObject = jsonData;
			}else if(test instanceof JSONArray){
				JSONArray jsonArray = chkJson.getJSONArray("row");
				rtnObject = jsonArray;
			}
			//System.out.println(">>>> "+jsonArray.length());
		} catch (JSONException e) {
			System.out.println(e.toString());
		}
		
		return rtnObject;
	}
	/*
	 * AXL API 이용하여 정보 조회
	 * 	DN으로 mac, dn, dn index를 조회
	 */
	/*
	public static List<SxmlDeviceStatusVO> selectListMacIntoDn(
			String cmVer, String cmIp, String cmId, String cmPwd, String dn){
		
		List<SxmlDeviceStatusVO> lstDeviceInfo = null;
		
		Object jsonDevice = selectDeviceInfoJSON01(cmVer, cmIp, cmId, cmPwd, dn);
		//System.out.println("jsonDevice.length() = "+jsonDevice.length());
		
		if(jsonDevice != null){
			lstDeviceInfo = selectListDevice(jsonDevice, cmIp, cmId, cmPwd);
		}
		
		return lstDeviceInfo;
	}
	*/
	/*
	 * AXL 조회 RequestSoap
	 */
	/*
	private static Object selectDeviceInfoJSON01(
			String cmVer, String cmIp, String cmId, String cmPwd, String dn) {
		
		Object rtnObject = null;
		
		String strXML = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns=\"http://www.cisco.com/AXL/API/"+cmVer+"\">\n";
		strXML += "<soapenv:Header/>\n";
		strXML += "<soapenv:Body>\n";
		strXML += "<ns:executeSQLQuery>\n";
		strXML += "<sql>\n";
		strXML += "SELECT \n";
		strXML += "		d.name, dn.numplanindex, n.dnorpattern \n";
		strXML += "FROM \n";
		strXML += "		DEVICE d, DEVICENUMPLANMAP dn, NUMPLAN n \n";
		strXML += "WHERE \n";
		strXML += "		n.tkpatternusage = '2' \n";
		strXML += "		AND d.name like 'SEP%' \n";
		strXML += "		AND d.pkid = dn.fkdevice \n";
		strXML += "		AND dn.fknumplan = n.pkid \n";
		if(dn != null && !dn.equals("")){
			strXML += "		AND n.dnorpattern = '"+dn+"' \n";
		}
		strXML += "ORDER BY \n";
		strXML += "		dn.numplanindex ASC \n";
		strXML += "</sql>\n";
		strXML += "</ns:executeSQLQuery>\n";
		strXML += "</soapenv:Body>\n";
		strXML += "</soapenv:Envelope>\n";
		
		//System.out.println("aa = "+strXML);
		
		String strResult = SoapHandler.RequestSoap(cmId, cmPwd, cmIp, "8443", strXML, "executeSQLQuery");
		//System.out.println(dn + " = bb = "+strResult);
		
		try {
			JSONObject xmlJson = XML.toJSONObject(strResult);
			String jsonP = xmlJson.toString(4);
			//System.out.println(cmIp+" / "+dn+" = "+jsonP);
			if(jsonP.equals("{}")){
				return rtnObject;
			}
			
			if(xmlJson.getJSONObject("soapenv:Envelope")
					.getJSONObject("soapenv:Body")
					.getJSONObject("ns:executeSQLQueryResponse").get("return").equals("")){
				
				return rtnObject;
			}else{
			}
			
			JSONObject chkJson = xmlJson.getJSONObject("soapenv:Envelope")
					.getJSONObject("soapenv:Body")
					.getJSONObject("ns:executeSQLQueryResponse")
					.getJSONObject("return");
			
			Object test = chkJson.get("row");
			
			if(test instanceof JSONObject){
				JSONObject jsonData = chkJson.getJSONObject("row");
				rtnObject = jsonData;
			}else if(test instanceof JSONArray){
				JSONArray jsonArray = chkJson.getJSONArray("row");
				rtnObject = jsonArray;
			}
			
			//System.out.println(">>>> "+jsonArray.length());
		} catch (JSONException e) {
			System.out.println(e.toString());
		}
		
		return rtnObject;
	}
	*/
	/*
	@SuppressWarnings("unused")
	private static Object selectDeviceInfoJSON(
			String cmVer, String cmIp, String cmId, String cmPwd, String dn) {
		
		Object rtnObject = null;
		
		String strXML = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns=\"http://www.cisco.com/AXL/API/"+cmVer+"\">\n";
		strXML += "<soapenv:Header/>\n";
		strXML += "<soapenv:Body>\n";
		strXML += "<ns:executeSQLQuery>\n";
		strXML += "<sql>\n";
		strXML += "SELECT \n";
		strXML += "		d.name, dn.numplanindex, n.dnorpattern \n";
		strXML += "FROM \n";
		strXML += "		DEVICE d, DEVICENUMPLANMAP dn, NUMPLAN n \n";
		strXML += "WHERE \n";
		strXML += "		n.tkpatternusage = '2' \n";
		strXML += "		AND d.name like 'SEP%' \n";
		strXML += "		AND d.pkid = dn.fkdevice \n";
		strXML += "		AND dn.fknumplan = n.pkid \n";
		if(dn != null && !dn.equals("")){
			strXML += "		AND n.dnorpattern = '"+dn+"' \n";
		}
		strXML += "ORDER BY \n";
		strXML += "		dn.numplanindex ASC \n";
		strXML += "</sql>\n";
		strXML += "</ns:executeSQLQuery>\n";
		strXML += "</soapenv:Body>\n";
		strXML += "</soapenv:Envelope>\n";
		
		//System.out.println(dn+" = "+strXML);
		
		String strResult = SoapHandler.RequestSoap(cmVer, cmId, cmPwd, cmIp, "8443", strXML, "executeSQLQuery");
		//System.out.println("bb = "+strResult);
		
		try {
			JSONObject xmlJson = XML.toJSONObject(strResult);
			String jsonP = xmlJson.toString(4);
			//System.out.println(jsonP);
			
			JSONObject chkJson = xmlJson.getJSONObject("soapenv:Envelope")
					.getJSONObject("soapenv:Body")
					.getJSONObject("ns:executeSQLQueryResponse")
					.getJSONObject("return");
			
			Object test = chkJson.get("row");
			
			if(test instanceof JSONObject){
				System.out.println("11 = Object");
				JSONObject jsonData = chkJson.getJSONObject("row");
				rtnObject = jsonData;
			}else if(test instanceof JSONArray){
				System.out.println("22 = Array");
				JSONArray jsonArray = chkJson.getJSONArray("row");
				rtnObject = jsonArray;
			}
			
			//System.out.println(">>>> "+jsonArray.length());
		} catch (JSONException e) {
			System.out.println(e.toString());
		}
		
		return rtnObject;
	}
	*/
	/*
	 * Device List
	 */
	/*
	private static List<SxmlDeviceStatusVO> selectListDevice(Object test, String cmIp, String cmId, String cmPwd){
		List<SxmlDeviceStatusVO> deviceList = new ArrayList<SxmlDeviceStatusVO>();
    	
		if(test instanceof JSONObject){
			JSONObject jsonDeviceObject = (JSONObject) test;
			SxmlDeviceStatusVO insertVO = new SxmlDeviceStatusVO();
			
			System.out.println("dnorpattern = "+jsonDeviceObject.getInt("dnorpattern"));
			System.out.println("name = "+jsonDeviceObject.getString("name"));
			System.out.println("numplanindex = "+jsonDeviceObject.getInt("numplanindex"));
			
			insertVO.setCmIp(cmIp);
			insertVO.setCmUser(cmId);
			insertVO.setCmPwd(cmPwd);
			insertVO.setDn(String.valueOf(jsonDeviceObject.getInt("dnorpattern")));
			insertVO.setMac(jsonDeviceObject.getString("name"));
			insertVO.setNumplanindex(jsonDeviceObject.getInt("numplanindex"));
			
			deviceList.add(insertVO);
		}else if(test instanceof JSONArray){
			JSONArray jsonDeviceArray = (JSONArray) test;
			//JSON 배열 데이터를 자바 List에 넣기 
			for(int i=0; i<jsonDeviceArray.length(); i++){
				SxmlDeviceStatusVO insertVO = new SxmlDeviceStatusVO();
				insertVO.setCmIp(cmIp);
				insertVO.setCmUser(cmId);
				insertVO.setCmPwd(cmPwd);
				insertVO.setDn(String.valueOf(jsonDeviceArray.getJSONObject(i).getInt("dnorpattern")));
				insertVO.setMac(jsonDeviceArray.getJSONObject(i).getString("name"));
				insertVO.setNumplanindex(jsonDeviceArray.getJSONObject(i).getInt("numplanindex"));
				
				deviceList.add(insertVO);
			}
		}
		
		return deviceList;
	}
	*/
}