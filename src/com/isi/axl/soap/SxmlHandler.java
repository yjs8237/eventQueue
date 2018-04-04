package com.isi.axl.soap;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

public class SxmlHandler {
	
	

	
	
	
	
	
	
	/*public static List<DeviceStatusModel> selectListStateIntoMac(
			String cmIp, String cmId, String cmPwd, String mac, String ver){
		
		List<DeviceStatusModel> lstDeviceInfo = null;
		
		Object jsonDevice = selectDeviceStatusJSON(cmIp, cmId, cmPwd, mac);
		
		if(jsonDevice != null){
			lstDeviceInfo = selectListDevice(jsonDevice, cmIp, cmId, cmPwd, ver);
		}
		
		return lstDeviceInfo;
		
	}*/
//	
//	public static List<DeviceStatusModel> selectListStateIntoMac(DeviceStatusModel vo){
//		
//		List<DeviceStatusModel> lstDeviceInfo = null;
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
	public static Object selectDeviceStatusJSON(String cmIp, String cmId, String cmPwd, String mac , String requestID){
		
		int maxReturnedDevices = 10;
		String deviceClass = "Phone";
		String selectBy = "Name";
		String status = "Registered";
		int model = 255;
		
		Object rtnObject = null;
		
		String strXML = "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:soap=\"http://schemas.cisco.com/ast/soap/\" xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\">\n";
		strXML += "<soapenv:Header>\n";
		strXML += "<AstHeader xsi:type=\"soap:AstHeader\">\n";
		strXML += "<SessionId xsi:type=\"xsd:string\"></SessionId>\n";
		strXML += "</AstHeader>\n";
		strXML += "</soapenv:Header>\n";
		strXML += "<soapenv:Body>\n";
		strXML += "<soap:SelectCmDevice soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n";
		strXML += "<StateInfo xsi:type=\"xsd:string\"></StateInfo>\n";
		strXML += "<CmSelectionCriteria xsi:type=\"soap:CmSelectionCriteria\">\n";
		strXML += "<MaxReturnedDevices xsi:type=\"xsd:unsignedInt\">"+maxReturnedDevices+"</MaxReturnedDevices>\n";
		strXML += "<Class xsi:type=\"xsd:string\">"+deviceClass+"</Class>\n";
		strXML += "<Model xsi:type=\"xsd:unsignedInt\">"+model+"</Model>\n";
		strXML += "<Status xsi:type=\"xsd:string\">"+status+"</Status>\n";
		strXML += "<NodeName xsi:type=\"xsd:string\">"+cmIp+"</NodeName>\n";
		strXML += "<SelectBy xsi:type=\"xsd:string\">"+selectBy+"</SelectBy>\n";
		strXML += "<SelectItems xsi:type=\"soap:SelectItems\" soapenc:arrayType=\"soap:SelectItem[]\">\n";
		strXML += "<SelectItem><Item xsi:type=\"xsd:string\">"+mac+"</Item></SelectItem>\n";
		strXML += "</SelectItems>\n";
		strXML += "</CmSelectionCriteria>\n";
		strXML += "</soap:SelectCmDevice>\n";
		strXML += "</soapenv:Body>\n";
		strXML += "</soapenv:Envelope>\n";
		//System.out.println(strXML);
		
		String strResult = SoapHandler.RequestSoapSXML(cmId, cmPwd, cmIp, "8443", strXML , requestID);
		//System.out.println(strResult);
		try {
			JSONObject xmlJson = XML.toJSONObject(strResult);
//			String jsonP = xmlJson.toString(4);
//			System.out.println(jsonP);
			
			
			//if(jsonDeviceObject.optJSONObject("ns1:LinesStatus") == null){
			if(xmlJson.getJSONObject("soapenv:Envelope")
					.getJSONObject("soapenv:Body")
					.optJSONObject("ns1:SelectCmDeviceResponse") == null){
				return rtnObject;
			}
			
			JSONObject dfObj = xmlJson.getJSONObject("soapenv:Envelope")
					.getJSONObject("soapenv:Body")
					.getJSONObject("ns1:SelectCmDeviceResponse")
					.getJSONObject("SelectCmDeviceResult");
			
			//System.out.println("==>> "+dfObj.getInt("ns1:TotalDevicesFound"));
			if(dfObj.getJSONObject("TotalDevicesFound").getInt("content") == 0){
				System.out.println(cmIp+" search MAC("+mac+") -> TotalDevicesFound = "+dfObj.getJSONObject("TotalDevicesFound").getInt("content"));
				return rtnObject;
			}
			
			JSONObject chkJson = dfObj
					.getJSONObject("CmNodes")
					.getJSONObject("item")
					.getJSONObject("CmDevices");
			
			Object test = chkJson.get("item");
			
			if(test instanceof JSONObject){
				JSONObject jsonData = chkJson.getJSONObject("item");
				rtnObject = jsonData;
			}else if(test instanceof JSONArray){
				//System.out.println("44 = Array");
				JSONArray jsonArray = chkJson.getJSONArray("item");
				rtnObject = jsonArray;
			}
			
			//System.out.println(">>>> "+jsonArray.length());
		} catch (JSONException e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			System.out.println(sw.toString());
		}
		
		return rtnObject;
		
	}
//	
//	public static Object selectDeviceStatusJSON(DeviceStatusModel vo){
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
	
	
}
/*
 
{"soapenv:Envelope": {
    "xmlns:xsd": "http://www.w3.org/2001/XMLSchema",
    "xmlns:xsi": "http://www.w3.org/2001/XMLSchema-instance",
    "soapenv:Body": {"ns1:SelectCmDeviceResponse": {
        "StateInfo": {
            "content": "<StateInfo><Node Name=\"SAMILCUCM\" SubsystemStartTime=\"1516950694\" StateId=\"65\" TotalItemsFound=\"1\" TotalItemsReturned=\"1\"/><\/StateInfo>",
            "xsi:type": "xsd:string"
        },
        "SelectCmDeviceResult": {
            "TotalDevicesFound": {
                "content": 1,
                "xsi:type": "xsd:unsignedInt"
            },
            "xsi:type": "ns1:SelectCmDeviceResult",
            "CmNodes": {
                "item": {
                    "Name": {
                        "content": "SAMILCUCM",
                        "xsi:type": "xsd:string"
                    },
                    "ReturnCode": {
                        "content": "Ok",
                        "xsi:type": "ns1:RisReturnCode"
                    },
                    "CmDevices": {
                        "item": {
                            "BoxProduct": {
                                "content": 0,
                                "xsi:type": "xsd:unsignedInt"
                            },
                            "Description": {
                                "content": "DN 1003",
                                "xsi:type": "xsd:string"
                            },
                            "Model": {
                                "content": 119,
                                "xsi:type": "xsd:unsignedInt"
                            },
                            "Product": {
                                "content": 119,
                                "xsi:type": "xsd:unsignedInt"
                            },
                            "DirNumber": {
                                "content": "1003-Registered",
                                "xsi:type": "xsd:string"
                            },
                            "PerfMonObject": {
                                "content": 2,
                                "xsi:type": "xsd:unsignedInt"
                            },
                            "IpAddress": {
                                "content": "192.168.20.242",
                                "xsi:type": "xsd:string"
                            },
                            "H323Trunk": {
                                "CallSignalAddr": {
                                    "xsi:nil": true,
                                    "xsi:type": "xsd:string"
                                },
                                "RemoteCmServer3": {
                                    "xsi:nil": true,
                                    "xsi:type": "xsd:string"
                                },
                                "ActiveGk": {
                                    "xsi:nil": true,
                                    "xsi:type": "xsd:string"
                                },
                                "AltGkList": {
                                    "xsi:nil": true,
                                    "xsi:type": "xsd:string"
                                },
                                "TechPrefix": {
                                    "xsi:nil": true,
                                    "xsi:type": "xsd:string"
                                },
                                "ConfigName": {
                                    "xsi:nil": true,
                                    "xsi:type": "xsd:string"
                                },
                                "RasAddr": {
                                    "xsi:nil": true,
                                    "xsi:type": "xsd:string"
                                },
                                "RemoteCmServer1": {
                                    "xsi:nil": true,
                                    "xsi:type": "xsd:string"
                                },
                                "RemoteCmServer2": {
                                    "xsi:nil": true,
                                    "xsi:type": "xsd:string"
                                },
                                "Zone": {
                                    "xsi:nil": true,
                                    "xsi:type": "xsd:string"
                                },
                                "xsi:type": "ns1:H323Trunk"
                            },
                            "xsi:type": "ns1:CmDevice",
                            "IsCtiControllable": {
                                "content": true,
                                "xsi:type": "xsd:boolean"
                            },
                            "TimeStamp": {
                                "content": 1517286709,
                                "xsi:type": "xsd:unsignedInt"
                            },
                            "Name": {
                                "content": "SEP001AA2660E8A",
                                "xsi:type": "xsd:string"
                            },
                            "StatusReason": {
                                "content": 0,
                                "xsi:type": "xsd:unsignedInt"
                            },
                            "Status": {
                                "content": "Registered",
                                "xsi:type": "ns1:CmDevRegStat"
                            },
                            "Class": {
                                "content": "Phone",
                                "xsi:type": "ns1:DeviceClass"
                            },
                            "LoginUserId": {
                                "xsi:nil": true,
                                "xsi:type": "xsd:string"
                            },
                            "Httpd": {
                                "content": "Yes",
                                "xsi:type": "ns1:CmDevHttpd"
                            },
                            "DChannel": {
                                "content": 0,
                                "xsi:type": "xsd:unsignedInt"
                            },
                            "RegistrationAttempts": {
                                "content": 2,
                                "xsi:type": "xsd:unsignedInt"
                            }
                        },
                        "xsi:type": "soapenc:Array",
                        "soapenc:arrayType": "ns1:CmDevice[1]"
                    },
                    "NoChange": {
                        "content": false,
                        "xsi:type": "xsd:boolean"
                    },
                    "xsi:type": "ns1:CmNode"
                },
                "xmlns:soapenc": "http://schemas.xmlsoap.org/soap/encoding/",
                "xsi:type": "soapenc:Array",
                "soapenc:arrayType": "ns1:CmNode[1]"
            }
        },
        "xmlns:ns1": "http://schemas.cisco.com/ast/soap/",
        "soapenv:encodingStyle": "http://schemas.xmlsoap.org/soap/encoding/"
    }},
    "xmlns:soapenv": "http://schemas.xmlsoap.org/soap/envelope/"
}}
 
 */
