package com.isi.axl.soap;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import com.isi.data.XmlInfoMgr;


public class AxlHandler {
	
	private static final String cmVer = "8.5";
	
	private CmAxlInfoModel cmAxlInfo;
	
	public AxlHandler(CmAxlInfoModel cmAxlInfo) {
		this.cmAxlInfo = cmAxlInfo;
	}
	
	public JSONArray selectUserLocale() {
		StringBuffer queryBuffer = new StringBuffer();
		queryBuffer.append("SELECT * FROM typeuserlocale");
		return sendSoapMessage(cmAxlInfo , getQuerySoapMessage(queryBuffer.toString()) );
	}
	
	
	public JSONArray selectAllDevice() {
		StringBuffer queryBuffer = new StringBuffer();
		queryBuffer.append("SELECT * FROM device");
		return sendSoapMessage(cmAxlInfo , getQuerySoapMessage(queryBuffer.toString()) );
	}
	
	public JSONArray insertEndUser(String userID , String lastName) {
		StringBuffer queryBuffer = new StringBuffer();
		queryBuffer.append("INSERT INTO enduser (userid , lastname) values ('" + userID + "' , '"+lastName+"')");
		return sendSoapMessage(cmAxlInfo , getQuerySoapMessage(queryBuffer.toString()) );
	}
	
	public JSONArray selectAllEndUser () {
		StringBuffer queryBuffer = new StringBuffer();
		queryBuffer.append("SELECT * FROM enduser");
		return sendSoapMessage(cmAxlInfo , getQuerySoapMessage(queryBuffer.toString()) ); 
	}
	
	public JSONArray testSoap (CmAxlInfoModel cmAxlInfo , String query) {
		StringBuffer queryBuffer = new StringBuffer();
		queryBuffer.append("SELECT * FROM pickupgroup");
		return sendSoapMessage(cmAxlInfo , getQuerySoapMessage(query) ); 
	}
	
	public JSONArray deleteLicense(String fkEndUser) {
		String query = (new StringBuilder("delete from enduserlicense where fkEndUser = '")).append(fkEndUser).append("'").toString();
		return sendSoapMessage(cmAxlInfo , getQuerySoapMessage(query) );
	}
	
	public JSONArray deleteSipDevice(String mac_address) {
		String query = (new StringBuilder("delete from device where name= '")).append(mac_address).append("'").toString();
		return sendSoapMessage(cmAxlInfo , getQuerySoapMessage(query) ); 
	}
	
	
	
	
	
	
	/*
	 ************************************************************************************** */
	
	
	private  String getQuerySoapMessage (String query) {
		
		
		StringBuffer soapReqMessage = new StringBuffer();
		
		query = query.trim();
		
		if(query.startsWith("select") || query.startsWith("SELECT")) {
			soapReqMessage.append("<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" ");
			soapReqMessage.append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ");
			soapReqMessage.append("xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"> ");
			soapReqMessage.append("<SOAP-ENV:Body> ").append("\n");
			soapReqMessage.append("<axlapi:executeSQLQuery xmlns:axlapi=\"http://www.cisco.com/AXL/API/"+cmVer+"\" sequence=\"1234\"> ").append("\n");
			soapReqMessage.append("<sql>").append("\n").append(query).append("\n").append("</sql> ").append("\n");
			soapReqMessage.append("</axlapi:executeSQLQuery> ").append("\n");
			soapReqMessage.append("</SOAP-ENV:Body> ").append("\n");
			soapReqMessage.append("</SOAP-ENV:Envelope>");
		} else {
			soapReqMessage.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns=\"http://www.cisco.com/AXL/API/"+cmVer+"\">").append("\n");
			soapReqMessage.append("<soapenv:Header/>").append("\n");
			soapReqMessage.append("<soapenv:Body>").append("\n");
			soapReqMessage.append("<ns:executeSQLUpdate sequence=\"?\">").append("\n");
			soapReqMessage.append("<sql>").append(query).append("</sql>").append("\n");
			soapReqMessage.append("</ns:executeSQLUpdate>").append("\n");
			soapReqMessage.append("</soapenv:Body>").append("\n");
			soapReqMessage.append("</soapenv:Envelope>").append("\n");
		}
		
		
		return soapReqMessage.toString();
	}
	
	
	private  JSONArray sendSoapMessage(CmAxlInfoModel  cmAxlInfo , String soapReqMessage ) {
		
		String strResult = SoapHandler.RequestSoap(cmVer, cmAxlInfo.getCmID(), cmAxlInfo.getCmPwd(), cmAxlInfo.getCmIP(), "8443", soapReqMessage.toString(), "executeSQLQuery");
		
		try {
			if(strResult.startsWith("Error")) {
				
				XmlInfoMgr xmlInfo = XmlInfoMgr.getInstance();
				
				strResult = SoapHandler.RequestSoap(cmVer, xmlInfo.getCm2User(), xmlInfo.getCm2Pwd(), xmlInfo.getCm2IpAddr(), "8443", soapReqMessage.toString(), "executeSQLQuery");
				
			}
		} catch (Exception e) {
			
		}
		
//		System.out.println("Send Soap Return : " + strResult);
		
		if(soapReqMessage.indexOf("axlapi:executeSQLQuery") > 0) {
			// SELECT Request
			return jsonQueryResParsing(strResult);
		} else {
			// UPDATE Request
			return jsonUpdateResParsing(strResult);
		}
		
	}
	
	
	private JSONArray jsonUpdateResParsing(String strResult) {
		// TODO Auto-generated method stub
		JSONArray resultObj = new JSONArray();
		JSONObject xmlJson = null;
		
		String responseKey = "ns:executeSQLUpdateResponse";
		
		try {
			
			xmlJson = XML.toJSONObject(strResult);
//			System.out.println(xmlJson.toString(4));

			if (xmlJson.getJSONObject("soapenv:Envelope").getJSONObject("soapenv:Body")
					.getJSONObject(responseKey).get("return").equals("")) {
				return resultObj;
			} 

			JSONObject chkJson = xmlJson.getJSONObject("soapenv:Envelope").getJSONObject("soapenv:Body")
					.getJSONObject(responseKey).getJSONObject("return");
			
			resultObj.put(chkJson);

		} catch (Exception e) {
//			System.out.println(e.toString());
			JSONObject chkJson = xmlJson.getJSONObject("soapenv:Envelope").getJSONObject("soapenv:Body")
					.getJSONObject("soapenv:Fault").getJSONObject("detail").getJSONObject("axlError");
			resultObj.put(chkJson);
			return resultObj;
		} finally {
			
		}
		return resultObj;
	}

	private JSONArray jsonQueryResParsing(String strResult) {
		// TODO Auto-generated method stub
		JSONArray resultObj = new JSONArray();
		JSONObject xmlJson = null;
		
		String responseKey = "ns:executeSQLQueryResponse";
		try {
			
			xmlJson = XML.toJSONObject(strResult);
//			System.out.println(xmlJson.toString(4));

			if (xmlJson.getJSONObject("soapenv:Envelope").getJSONObject("soapenv:Body")
					.getJSONObject(responseKey).get("return").equals("")) {
				return resultObj;
			} 

			JSONObject chkJson = xmlJson.getJSONObject("soapenv:Envelope").getJSONObject("soapenv:Body")
					.getJSONObject(responseKey).getJSONObject("return");

			Object test = chkJson.get("row");

			if (test instanceof JSONObject) {
				JSONObject jsonData = chkJson.getJSONObject("row");
				resultObj.put(jsonData);
			} else if (test instanceof JSONArray) {
				resultObj = chkJson.getJSONArray("row");
			}

		} catch (Exception e) {
//			System.out.println(e.toString());
			JSONObject chkJson = xmlJson.getJSONObject("soapenv:Envelope").getJSONObject("soapenv:Body")
					.getJSONObject("soapenv:Fault").getJSONObject("detail").getJSONObject("axlError");
			resultObj.put(chkJson);
			return resultObj;
		} finally {
			
		}
		return resultObj;
	}
	
	public int doDeviceReset(String pkid, String mac_address) {
		int rtn = 0;
		StringBuffer soapReqMessage = new StringBuffer();
		
		soapReqMessage.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns=\"http://www.cisco.com/AXL/API/8.5\"> ").append("\n");
		soapReqMessage.append("<soapenv:Header/> ").append("\n");
		soapReqMessage.append("<soapenv:Body> ").append("\n");
		soapReqMessage.append("<ns:doDeviceReset sequence=\"\" isMGCP=\"false\"> ").append("\n");
		soapReqMessage.append("<deviceName uuid=\"").append(pkid).append("\">").append(mac_address).append("</deviceName> ").append("\n");
		soapReqMessage.append("<isHardReset>false</isHardReset> ").append("\n");
		soapReqMessage.append("</ns:doDeviceReset> ").append("\n");
		soapReqMessage.append("</soapenv:Body> ").append("\n");
		soapReqMessage.append("</soapenv:Envelope>");
		
		String strResult = SoapHandler.RequestSoap(cmVer, cmAxlInfo.getCmID(), cmAxlInfo.getCmPwd(), cmAxlInfo.getCmIP(), "8443", soapReqMessage.toString(), "doDeviceReset");
		
		//JSONArray resultObj = new JSONArray();
		JSONObject xmlJson = null;
		
		String responseKey = "ns:doDeviceResetResponse";
		try {
			
			xmlJson = XML.toJSONObject(strResult);
//			System.out.println("ns:doDeviceResetResponse >>>> "+xmlJson.toString(4));

			if (xmlJson.getJSONObject("soapenv:Envelope").getJSONObject("soapenv:Body")
					.getJSONObject(responseKey).get("return").equals("")) {
				rtn = -1;
			}else{
				rtn = 1;
			}

		} catch (Exception e) {
//			System.out.println(e.toString());
			/*JSONObject chkJson = xmlJson.getJSONObject("soapenv:Envelope").getJSONObject("soapenv:Body")
					.getJSONObject("soapenv:Fault").getJSONObject("detail").getJSONObject("axlError");*/
			return rtn -2;
		} finally {
			
		}
		
		return rtn;
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
//		System.out.println("json return");
//		System.out.println(strResult);
		
		try {
			JSONObject xmlJson = XML.toJSONObject(strResult);
			String jsonP = xmlJson.toString(4);
//			System.out.println(jsonP);
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
//			System.out.println(e.toString());
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