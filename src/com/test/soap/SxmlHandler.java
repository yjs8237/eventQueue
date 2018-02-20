package com.test.soap;

public class SxmlHandler {
	
//	
//	public static List<SxmlDeviceStatusVO> selectListStateIntoMac(
//			String cmIp, String cmId, String cmPwd, String mac, String ver){
//		
//		List<SxmlDeviceStatusVO> lstDeviceInfo = null;
//		
//		Object jsonDevice = selectDeviceStatusJSON(cmIp, cmId, cmPwd, mac);
//		
//		if(jsonDevice != null){
//			lstDeviceInfo = selectListDevice(jsonDevice, cmIp, cmId, cmPwd, ver);
//		}
//		
//		return lstDeviceInfo;
//		
//	}
//	
//	public static List<SxmlDeviceStatusVO> selectListStateIntoMac(SxmlDeviceStatusVO vo){
//		
//		List<SxmlDeviceStatusVO> lstDeviceInfo = null;
//		
//		Object jsonDevice = selectDeviceStatusJSON(vo);
//		//System.out.println("jsonDevice.length() = "+jsonDevice.length());
//		
//		if(jsonDevice != null){
//			lstDeviceInfo = selectListDevice(jsonDevice, vo.getCmIp(), vo.getCmUser(), vo.getCmPwd(), vo.getCmVer());
//		}
//		
//		return lstDeviceInfo;
//		
//	}
//	
//	
//	public static Object selectDeviceStatusJSON(
//			String cmIp, String cmId, String cmPwd, String mac){
//		
//		int maxReturnedDevices = 1000;
//		String deviceClass = "Phone";
//		String selectBy = "Name";
//		
//		Object rtnObject = null;
//		
//		String strXML = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:soap=\"http://schemas.cisco.com/ast/soap\">\n";
//		strXML += "<soapenv:Header/>\n";
//		strXML += "<soapenv:Body>\n";
//		strXML += "<soap:selectCmDeviceExt>\n";
//		strXML += "<soap:StateInfo></soap:StateInfo>\n";
//		strXML += "<soap:CmSelectionCriteria>\n";
//		strXML += "<soap:MaxReturnedDevices>"+maxReturnedDevices+"</soap:MaxReturnedDevices>\n";
//		strXML += "<soap:DeviceClass>"+deviceClass+"</soap:DeviceClass>\n";
//		strXML += "<soap:Model></soap:Model>\n";
//		strXML += "<soap:Status>Any</soap:Status>\n";
//		strXML += "<soap:NodeName>"+cmIp+"</soap:NodeName>\n";
//		strXML += "<soap:SelectBy>"+selectBy+"</soap:SelectBy>\n";
//		strXML += "<soap:SelectItems>\n";
//		strXML += "<soap:item>\n";
//		strXML += "<soap:Item>"+mac+"</soap:Item>\n";
//		strXML += "</soap:item>\n";
//		strXML += "</soap:SelectItems>\n";
//		strXML += "<soap:Protocol>Any</soap:Protocol>\n";
//		strXML += "<soap:DownloadStatus>Any</soap:DownloadStatus>\n";
//		strXML += "</soap:CmSelectionCriteria>\n";
//		strXML += "</soap:selectCmDeviceExt>\n";
//		strXML += "</soapenv:Body>\n";
//		strXML += "</soapenv:Envelope>\n";
//		//System.out.println(strXML);
//		
//		String strResult = SoapHandler.RequestSoapSXML(cmId, cmPwd, cmIp, "8443", strXML);
//		//System.out.println(strResult);
//		try {
//			JSONObject xmlJson = XML.toJSONObject(strResult);
//			//String jsonP = xmlJson.toString(4);
//			//System.out.println(jsonP);
//			
//			JSONObject dfObj = xmlJson.getJSONObject("soapenv:Envelope")
//					.getJSONObject("soapenv:Body")
//					.getJSONObject("ns1:selectCmDeviceResponse")
//					.getJSONObject("ns1:selectCmDeviceReturn")
//					.getJSONObject("ns1:SelectCmDeviceResult");
//			
//			//System.out.println("==>> "+dfObj.getInt("ns1:TotalDevicesFound"));
//			if(dfObj.getInt("ns1:TotalDevicesFound") == 0){
//				System.out.println("TotalDevicesFound = "+dfObj.getInt("ns1:TotalDevicesFound"));
//				return rtnObject;
//			}
//			
//			JSONObject chkJson = xmlJson.getJSONObject("soapenv:Envelope")
//					.getJSONObject("soapenv:Body")
//					.getJSONObject("ns1:selectCmDeviceResponse")
//					.getJSONObject("ns1:selectCmDeviceReturn")
//					.getJSONObject("ns1:SelectCmDeviceResult")
//					.getJSONObject("ns1:CmNodes")
//					.getJSONObject("ns1:item")
//					.getJSONObject("ns1:CmDevices");
//			
//			Object test = chkJson.get("ns1:item");
//			
//			if(test instanceof JSONObject){
//				//System.out.println("33 = Object");
//				JSONObject jsonData = chkJson.getJSONObject("ns1:item");
//				rtnObject = jsonData;
//			}else if(test instanceof JSONArray){
//				//System.out.println("44 = Array");
//				JSONArray jsonArray = chkJson.getJSONArray("ns1:item");
//				rtnObject = jsonArray;
//			}
//			
//			//System.out.println(">>>> "+jsonArray.length());
//		} catch (JSONException e) {
//			System.out.println(e.toString());
//		}
//		
//		return rtnObject;
//		
//	}
//	
//	public static Object selectDeviceStatusJSON(SxmlDeviceStatusVO vo){
//		
//		int maxReturnedDevices = vo.getMaxReturnedDevices();
//		String deviceClass = "Phone";
//		String selectBy = "Name";
//		
//		Object rtnObject = null;
//		
//		String strXML = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:soap=\"http://schemas.cisco.com/ast/soap\">\n";
//		strXML += "<soapenv:Header/>\n";
//		strXML += "<soapenv:Body>\n";
//		strXML += "<soap:selectCmDeviceExt>\n";
//		strXML += "<soap:StateInfo></soap:StateInfo>\n";
//		strXML += "<soap:CmSelectionCriteria>\n";
//		strXML += "<soap:MaxReturnedDevices>"+maxReturnedDevices+"</soap:MaxReturnedDevices>\n";
//		strXML += "<soap:DeviceClass>"+deviceClass+"</soap:DeviceClass>\n";
//		strXML += "<soap:Model></soap:Model>\n";
//		strXML += "<soap:Status>Any</soap:Status>\n";
//		strXML += "<soap:NodeName>"+vo.getCmIp()+"</soap:NodeName>\n";
//		strXML += "<soap:SelectBy>"+selectBy+"</soap:SelectBy>\n";
//		strXML += "<soap:SelectItems>\n";
//		
//		for(int i=0; i<vo.getDeviceList().size(); i++){
//			strXML += "<soap:item><soap:Item>"+vo.getDeviceList().get(i).getMac()+"</soap:Item></soap:item>\n";
//		}
//		
//		strXML += "</soap:SelectItems>\n";
//		strXML += "<soap:Protocol>Any</soap:Protocol>\n";
//		strXML += "<soap:DownloadStatus>Any</soap:DownloadStatus>\n";
//		strXML += "</soap:CmSelectionCriteria>\n";
//		strXML += "</soap:selectCmDeviceExt>\n";
//		strXML += "</soapenv:Body>\n";
//		strXML += "</soapenv:Envelope>\n";
//		//System.out.println(strXML);
//		
//		String strResult = SoapHandler.RequestSoapSXML(vo.getCmUser(), vo.getCmPwd(), vo.getCmIp(), "8443", strXML);
//		//System.out.println(strResult);
//		try {
//			JSONObject xmlJson = XML.toJSONObject(strResult);
//			String jsonP = xmlJson.toString(4);
//			//System.out.println("wiseo = "+jsonP);
//			
//			JSONObject dfObj = xmlJson.getJSONObject("soapenv:Envelope")
//					.getJSONObject("soapenv:Body")
//					.getJSONObject("ns1:selectCmDeviceResponse")
//					.getJSONObject("ns1:selectCmDeviceReturn")
//					.getJSONObject("ns1:SelectCmDeviceResult");
//			
//			//System.out.println("==>> "+dfObj.getInt("ns1:TotalDevicesFound"));
//			if(dfObj.getInt("ns1:TotalDevicesFound") == 0){
//				System.out.println("TotalDevicesFound = "+dfObj.getInt("ns1:TotalDevicesFound"));
//				return rtnObject;
//			}
//			
//			JSONObject chkJson = xmlJson.getJSONObject("soapenv:Envelope")
//					.getJSONObject("soapenv:Body")
//					.getJSONObject("ns1:selectCmDeviceResponse")
//					.getJSONObject("ns1:selectCmDeviceReturn")
//					.getJSONObject("ns1:SelectCmDeviceResult")
//					.getJSONObject("ns1:CmNodes")
//					.getJSONObject("ns1:item")
//					.getJSONObject("ns1:CmDevices");
//			
//			Object test = chkJson.get("ns1:item");
//			
//			if(test instanceof JSONObject){
//				//System.out.println("1 = Object");
//				JSONObject jsonData = chkJson.getJSONObject("ns1:item");
//				rtnObject = jsonData;
//			}else if(test instanceof JSONArray){
//				//System.out.println("2 = Array");
//				JSONArray jsonArray = chkJson.getJSONArray("ns1:item");
//				rtnObject = jsonArray;
//			}
//			
//			//System.out.println(">>>> "+jsonArray.length());
//		} catch (JSONException e) {
//			System.out.println(e.toString());
//		}
//		
//		return rtnObject;
//		
//	}
//	
//	public static List<SxmlDeviceStatusVO> selectListDevice(
//			Object test, String cmIp, String cmId, String cmPwd, String cmVer){
//		
//		List<SxmlDeviceStatusVO> deviceList = new ArrayList<SxmlDeviceStatusVO>();
//    	
//		if(test instanceof JSONObject){
//			JSONObject jsonDeviceObject = (JSONObject) test;	// ns1:CmDevices > ns1:item
//			
//			if(jsonDeviceObject.optJSONObject("ns1:LinesStatus") == null){
//				return deviceList;
//			}
//			
//			Object dnTest = jsonDeviceObject.getJSONObject("ns1:LinesStatus").get("ns1:item");
//			
//			if(dnTest instanceof JSONObject){
//				//System.out.println("1 = dn Object");
//				SxmlDeviceStatusVO insertVO = new SxmlDeviceStatusVO();
//				
//				insertVO.setCmIp(cmIp);
//				insertVO.setCmUser(cmId);
//				insertVO.setCmPwd(cmPwd);
//				insertVO.setCmVer(cmVer);
//				insertVO.setMac(jsonDeviceObject.getString("ns1:Name"));
//				insertVO.setDeviceStatus(jsonDeviceObject.getString("ns1:Status"));
//				insertVO.setDeviceType(String.valueOf(jsonDeviceObject.getInt("ns1:Model")));
//				
//				if(jsonDeviceObject.optJSONObject("ns1:IPAddress") != null){
//					if(jsonDeviceObject.getJSONObject("ns1:IPAddress").optJSONObject("ns1:item") != null ){
//						insertVO.setIpaddr(jsonDeviceObject
//								.getJSONObject("ns1:IPAddress").getJSONObject("ns1:item").getString("ns1:IP"));
//					}
//				}
//				
//				insertVO.setNumplanindex(1);
//				insertVO.setDn(String.valueOf(jsonDeviceObject.getJSONObject("ns1:LinesStatus").getJSONObject("ns1:item")
//						.getInt("ns1:DirectoryNumber")));
//				insertVO.setDnStatus(jsonDeviceObject.getJSONObject("ns1:LinesStatus").getJSONObject("ns1:item")
//						.getString("ns1:Status"));
//				
//				deviceList.add(insertVO);
//			}else if(dnTest instanceof JSONArray){
//				//System.out.println("2 = dn Array");
//				JSONArray dnArray = new JSONArray();
//				dnArray = jsonDeviceObject.getJSONObject("ns1:LinesStatus").getJSONArray("ns1:item");
//				
//				for(int j=0; j<dnArray.length(); j++){
//					SxmlDeviceStatusVO insertVO = new SxmlDeviceStatusVO();
//					
//					insertVO.setCmIp(cmIp);
//					insertVO.setCmUser(cmId);
//					insertVO.setCmPwd(cmPwd);
//					insertVO.setCmVer(cmVer);
//					insertVO.setMac(jsonDeviceObject.getString("ns1:Name"));
//					insertVO.setDeviceStatus(jsonDeviceObject.getString("ns1:Status"));
//					insertVO.setDeviceType(String.valueOf(jsonDeviceObject.getInt("ns1:Model")));
//					
//					if(jsonDeviceObject.optJSONObject("ns1:IPAddress") != null){
//						if(jsonDeviceObject.getJSONObject("ns1:IPAddress").optJSONObject("ns1:item") != null ){
//							insertVO.setIpaddr(jsonDeviceObject
//									.getJSONObject("ns1:IPAddress").getJSONObject("ns1:item").getString("ns1:IP"));
//						}
//					}
//					
//					insertVO.setDn(String.valueOf(dnArray.getJSONObject(j).getInt("ns1:DirectoryNumber")));
//					insertVO.setNumplanindex(j+1);
//					insertVO.setDnStatus(dnArray.getJSONObject(j).getString("ns1:Status"));
//					
//					deviceList.add(insertVO);
//				}
//				
//			}
//			
//		}else if(test instanceof JSONArray){
//			JSONArray jsonDeviceArray = (JSONArray) test;
//			
//			//JSON 배열 데이터를 자바 List에 넣기 
//			for(int i=0; i<jsonDeviceArray.length(); i++){
//				//SxmlDeviceStatusVO insertVO = new SxmlDeviceStatusVO();
//				//insertVO.setNumplanindex(jsonDeviceArray.getJSONObject(i).getInt("numplanindex"));
//				//deviceList.add(insertVO);
//				if(jsonDeviceArray.getJSONObject(i).optJSONObject("ns1:LinesStatus") != null){
//					Object dnTest = jsonDeviceArray.getJSONObject(i).getJSONObject("ns1:LinesStatus").get("ns1:item");
//					
//					if(dnTest instanceof JSONObject){
//						//System.out.println("1 = dn Object");
//						SxmlDeviceStatusVO insertVO = new SxmlDeviceStatusVO();
//						
//						insertVO.setCmIp(cmIp);
//						insertVO.setCmUser(cmId);
//						insertVO.setCmPwd(cmPwd);
//						insertVO.setCmVer(cmVer);
//						insertVO.setMac(jsonDeviceArray.getJSONObject(i).getString("ns1:Name"));
//						insertVO.setDeviceStatus(jsonDeviceArray.getJSONObject(i).getString("ns1:Status"));
//						insertVO.setDeviceType(String.valueOf(jsonDeviceArray.getJSONObject(i).getInt("ns1:Model")));
//						
//						if(jsonDeviceArray.getJSONObject(i).optJSONObject("ns1:IPAddress") != null){
//							if(jsonDeviceArray.getJSONObject(i).getJSONObject("ns1:IPAddress").optJSONObject("ns1:item") != null ){
//								insertVO.setIpaddr(jsonDeviceArray.getJSONObject(i)
//										.getJSONObject("ns1:IPAddress").getJSONObject("ns1:item").getString("ns1:IP"));
//							}
//						}
//						
//						insertVO.setNumplanindex(1);
//						insertVO.setDn(String.valueOf(jsonDeviceArray.getJSONObject(i).getJSONObject("ns1:LinesStatus").getJSONObject("ns1:item")
//								.getInt("ns1:DirectoryNumber")));
//						insertVO.setDnStatus(jsonDeviceArray.getJSONObject(i).getJSONObject("ns1:LinesStatus").getJSONObject("ns1:item")
//								.getString("ns1:Status"));
//						
//						deviceList.add(insertVO);
//					}else if(dnTest instanceof JSONArray){
//						//System.out.println("2 = dn Array");
//						JSONArray dnArray = new JSONArray();
//						dnArray = jsonDeviceArray.getJSONObject(i).getJSONObject("ns1:LinesStatus").getJSONArray("ns1:item");
//						
//						for(int j=0; j<dnArray.length(); j++){
//							SxmlDeviceStatusVO insertVO = new SxmlDeviceStatusVO();
//							
//							insertVO.setCmIp(cmIp);
//							insertVO.setCmUser(cmId);
//							insertVO.setCmPwd(cmPwd);
//							insertVO.setCmVer(cmVer);
//							insertVO.setMac(jsonDeviceArray.getJSONObject(i).getString("ns1:Name"));
//							insertVO.setDeviceStatus(jsonDeviceArray.getJSONObject(i).getString("ns1:Status"));
//							insertVO.setDeviceType(String.valueOf(jsonDeviceArray.getJSONObject(i).getInt("ns1:Model")));
//							
//							if(jsonDeviceArray.getJSONObject(i).optJSONObject("ns1:IPAddress") != null){
//								if(jsonDeviceArray.getJSONObject(i).getJSONObject("ns1:IPAddress").optJSONObject("ns1:item") != null ){
//									insertVO.setIpaddr(jsonDeviceArray.getJSONObject(i)
//											.getJSONObject("ns1:IPAddress").getJSONObject("ns1:item").getString("ns1:IP"));
//								}
//							}
//							
//							insertVO.setDn(String.valueOf(dnArray.getJSONObject(j).getInt("ns1:DirectoryNumber")));
//							insertVO.setNumplanindex(j+1);
//							insertVO.setDnStatus(dnArray.getJSONObject(j).getString("ns1:Status"));
//							
//							deviceList.add(insertVO);
//						}
//					}
//				}
//			}
//			
//		}
//		
//		
//		return deviceList;
//	}
	
	
	
	/*
	 * SXML API 이용하여 정보 조회
	 */
//	public static List<SxmlDeviceStatusVO> selectListDeviceStatus(){
//		
//		String cmIp = Config.get("axl.cm1.ip");
//		String cmId = Config.get("axl.cm1.id");
//		String cmPwd = Config.get("axl.cm1.pwd");
//		List<SxmlDeviceStatusVO> lstDeviceStatus = null;
//		
//		JSONArray jsonDevice = selectDeviceStatusJSON(cmIp, cmId, cmPwd);
//		if(jsonDevice.length() > 0){
//			lstDeviceStatus = selectListDevice(jsonDevice, cmIp, cmId, cmPwd);
//		}
//		
//		return lstDeviceStatus;
//	}
//	
//	/*
//	 * SXML 조회 RequestSoapSXML
//	 */
//	private static JSONArray selectDeviceStatusJSON(String cmIp, String cmId, String cmPwd) {
//		int maxReturnedDevices = 1000;
//		String deviceClass = "Phone";
//		String selectBy = "Name";
//		
//		JSONArray jsonArray = new JSONArray();
//		
//		String strXML = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:soap=\"http://schemas.cisco.com/ast/soap\">\n";
//		strXML += "<soapenv:Header/>\n";
//		strXML += "<soapenv:Body>\n";
//		strXML += "<soap:selectCmDeviceExt>\n";
//		strXML += "<soap:StateInfo></soap:StateInfo>\n";
//		strXML += "<soap:CmSelectionCriteria>\n";
//		strXML += "<soap:MaxReturnedDevices>"+maxReturnedDevices+"</soap:MaxReturnedDevices>\n";
//		strXML += "<soap:DeviceClass>"+deviceClass+"</soap:DeviceClass>\n";
//		strXML += "<soap:Model></soap:Model>\n";
//		strXML += "<soap:Status>Any</soap:Status>\n";
//		strXML += "<soap:NodeName>"+cmIp+"</soap:NodeName>\n";
//		strXML += "<soap:SelectBy>"+selectBy+"</soap:SelectBy>\n";
//		strXML += "<soap:SelectItems>\n";
//		strXML += "<soap:item>\n";
//		strXML += "<soap:Item></soap:Item>\n";
//		strXML += "</soap:item>\n";
//		strXML += "</soap:SelectItems>\n";
//		strXML += "<soap:Protocol>Any</soap:Protocol>\n";
//		strXML += "<soap:DownloadStatus>Any</soap:DownloadStatus>\n";
//		strXML += "</soap:CmSelectionCriteria>\n";
//		strXML += "</soap:selectCmDeviceExt>\n";
//		strXML += "</soapenv:Body>\n";
//		strXML += "</soapenv:Envelope>\n";
//		
//		String strResult = SoapHandler.RequestSoapSXML(cmId, cmPwd, cmIp, "8443", strXML);
//		//System.out.println(strResult);
//		try {
//			JSONObject xmlJson = XML.toJSONObject(strResult);
//			//String jsonP = xmlJson.toString(4);
//			//System.out.println(jsonP);
//			jsonArray = xmlJson.getJSONObject("soapenv:Envelope")
//					.getJSONObject("soapenv:Body")
//					.getJSONObject("ns1:selectCmDeviceResponse")
//					.getJSONObject("ns1:selectCmDeviceReturn")
//					.getJSONObject("ns1:SelectCmDeviceResult")
//					.getJSONObject("ns1:CmNodes")
//					.getJSONObject("ns1:item")
//					.getJSONObject("ns1:CmDevices")
//					.getJSONArray("ns1:item");
//			//System.out.println(">>>> "+jsonArray.length());
//		} catch (JSONException e) {
//			System.out.println(e.toString());
//		}
//		
//		return jsonArray;
//	}
//
//	/*
//	 * Device 상태 List
//	 */
//	private static List<SxmlDeviceStatusVO> selectListDevice(JSONArray jsonDeviceArray, String cmIp, String cmId, String cmPwd){
//		SxmlDeviceStatusVO paramVO = new SxmlDeviceStatusVO();
//		List<SxmlDeviceStatusVO> deviceList = new ArrayList<SxmlDeviceStatusVO>();
//		int addIdx = 0;
//    	
//		//System.out.println(jsonDeviceArray.length());
//    	//JSON 배열 데이터를 자바 List에 넣기 
//		for(int i=0; i<jsonDeviceArray.length(); i++){
//			if(jsonDeviceArray.getJSONObject(i).optJSONObject("ns1:LinesStatus") != null){
//				Object test = jsonDeviceArray.getJSONObject(i).getJSONObject("ns1:LinesStatus").get("ns1:item");
//				if(test instanceof JSONObject){
//					SxmlDeviceStatusVO insertVO = new SxmlDeviceStatusVO();
//					
//					insertVO.setMac(jsonDeviceArray.getJSONObject(i).getString("ns1:Name"));
//					//System.out.println(insertVO.getMac());
//					if(jsonDeviceArray.getJSONObject(i).optJSONObject("ns1:IPAddress") != null){
//						if(jsonDeviceArray.getJSONObject(i).getJSONObject("ns1:IPAddress").optJSONObject("ns1:item") != null ){
//							insertVO.setIpaddr(jsonDeviceArray.getJSONObject(i)
//									.getJSONObject("ns1:IPAddress").getJSONObject("ns1:item").getString("ns1:IP"));
//						}
//					}
//					
//					insertVO.setCmIp(cmIp);
//					insertVO.setDeviceStatus(jsonDeviceArray.getJSONObject(i).getString("ns1:Status"));
//					insertVO.setDeviceType(String.valueOf(jsonDeviceArray.getJSONObject(i).getInt("ns1:Model")));
//					
//					insertVO.setDn(String.valueOf(jsonDeviceArray.getJSONObject(i)
//							.getJSONObject("ns1:LinesStatus").getJSONObject("ns1:item").getInt("ns1:DirectoryNumber")));
//					insertVO.setNumplanindex(1);
//					insertVO.setDnStatus(jsonDeviceArray.getJSONObject(i)
//							.getJSONObject("ns1:LinesStatus").getJSONObject("ns1:item").getString("ns1:Status"));
//					insertVO.setCmUser(cmId);
//					insertVO.setCmPwd(cmPwd);
//					
//					deviceList.add(addIdx, insertVO);
//					addIdx++;
//				}else if(test instanceof JSONArray){
//					JSONArray dnArray = new JSONArray();
//					dnArray = jsonDeviceArray.getJSONObject(i).getJSONObject("ns1:LinesStatus").getJSONArray("ns1:item");
//					for(int j=0; j<dnArray.length(); j++){
//						SxmlDeviceStatusVO insertVO = new SxmlDeviceStatusVO();
//						
//						insertVO.setMac(jsonDeviceArray.getJSONObject(i).getString("ns1:Name"));
//						if(jsonDeviceArray.getJSONObject(i).optJSONObject("ns1:IPAddress") != null){
//							if(jsonDeviceArray.getJSONObject(i).getJSONObject("ns1:IPAddress").optJSONObject("ns1:item") != null ){
//								insertVO.setIpaddr(jsonDeviceArray.getJSONObject(i)
//										.getJSONObject("ns1:IPAddress").getJSONObject("ns1:item").getString("ns1:IP"));
//							}
//						}
//						
//						insertVO.setCmIp(cmIp);
//						insertVO.setDeviceStatus(jsonDeviceArray.getJSONObject(i).getString("ns1:Status"));
//						insertVO.setDeviceType(String.valueOf(jsonDeviceArray.getJSONObject(i).getInt("ns1:Model")));
//						
//						insertVO.setDn(String.valueOf(dnArray.getJSONObject(j).getInt("ns1:DirectoryNumber")));
//						insertVO.setNumplanindex(j+1);
//						insertVO.setDnStatus(dnArray.getJSONObject(j).getString("ns1:Status"));
//						insertVO.setCmUser(cmId);
//						insertVO.setCmPwd(cmPwd);
//						
//						deviceList.add(addIdx, insertVO);
//						addIdx++;
	
//					}
//				}else{
//					//System.out.println(insertVO.getMac()+" -- WHAT IS JSON TYPE???");
//				}
//			}
//		}
//		paramVO.setDeviceList(deviceList);
//		
//		return deviceList;
//	}
	
	
}
