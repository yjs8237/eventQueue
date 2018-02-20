package com.test.soap;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class SoapHandler {
	////////////////////////////////////////////////////////////////
	// AXL
	public static String RequestSoap(String ver, String id, String pw, String urlIP, String urlPort, String xmlBody, String apiFlag) {
		// TimeOut Default 3 second
		return RequestSoap(ver, id, pw, urlIP, urlPort, xmlBody, apiFlag, 3000);
	}
	public static String RequestSoap(String id, String pw, String urlIP, String urlPort, String xmlBody, String apiFlag) {
		// TimeOut Default 3 second
		return RequestSoap(id, pw, urlIP, urlPort, xmlBody, apiFlag,3000);
	}
	public static String RequestSoap(String id, String pw, String urlIP, String urlPort, String xmlBody, String apiFlag, int timeOutMilliSecond) {
		SSLContext ctx = null;
		X509TrustManager xtm = new SoapTrustManager();
		TrustManager[] mytm = { xtm };
		try {
			ctx = SSLContext.getInstance("SSL");
			ctx.init(null, mytm, null);
		} catch (NoSuchAlgorithmException e) {
			return e.toString();
		} catch (KeyManagementException ekey) {
			return ekey.toString();
		}

		SoapXML xml = new SoapXML(urlIP, Integer.parseInt(urlPort), id, pw, ctx);
		
		String strHttpHeader = "POST https://"+xml.getIP()+":"+xml.getPort()+"/axl/ HTTP/1.1\r\n";
		strHttpHeader +="Accept-Encoding: gzip,deflate \r\n";
		strHttpHeader +="Content-type: text/xml; charset=UTF-8 \r\n";
		strHttpHeader +="SOAPAction: \"CUCM:DB ver=10.5 " + apiFlag + "\"" + "\r\n";
		strHttpHeader +="Content-Length: " + xmlBody.length() + "\r\n";
		strHttpHeader +="Host: " + xml.getIP() + ":" + xml.getPort() + "\r\n";
		strHttpHeader +="Connection: Keep-Alive\r\n";
		strHttpHeader +="Authorization: Basic " + xml.getAuth() + "\r\n";
		//strHttpHeader +="User-Agent: Apache-HttpClient/4.1.1 (java 1.5)"; 
		strHttpHeader +="\r\n";
		

		String strXml = strHttpHeader + xmlBody;
		String strResult = xml.SendSoapMessage(strXml, timeOutMilliSecond);
		
		return strResult;
	}
	public static String RequestSoap(String ver, String id, String pw, String urlIP, String urlPort, String xmlBody, String apiFlag, int timeOutMilliSecond) {
		SSLContext ctx = null;
		X509TrustManager xtm = new SoapTrustManager();
		TrustManager[] mytm = { xtm };
		try {
			ctx = SSLContext.getInstance("SSL");
			ctx.init(null, mytm, null);
		} catch (NoSuchAlgorithmException e) {
			return e.toString();
		} catch (KeyManagementException ekey) {
			return ekey.toString();
		}

		SoapXML xml = new SoapXML(urlIP, Integer.parseInt(urlPort), id, pw, ctx);
		
		String auth = Utils.getBase64(id+":"+pw);
		
//		String strHttpHeader = "POST /axl/ HTTP/1.0\r\n";
//		strHttpHeader +="Content-type: text/xml\r\n";
//		strHttpHeader +="SOAPAction: \"CUCM:DB ver="+ver+"\"\r\n";
//		strHttpHeader +="Accept: text/*\r\n";
//		strHttpHeader +="Content-Length: " + xmlBody.length() + "\r\n";
//		strHttpHeader +="Authorization: Basic " + auth + "\r\n";
//		strHttpHeader +="Host: " + urlIP + ":" + urlPort + "\r\n";
//		strHttpHeader +="Connection: Keep-Alive\r\n";
//		strHttpHeader +="\r\n";
		
		
		String strHttpHeader = "POST https://"+xml.getIP()+":"+xml.getPort()+"/axl/ HTTP/1.1\r\n";
		strHttpHeader +="Accept-Encoding: gzip,deflate \r\n";
		strHttpHeader +="Content-type: text/xml; charset=UTF-8 \r\n";
		strHttpHeader +="SOAPAction: \"CUCM:DB ver=" + ver + " " + apiFlag + "\"" + "\r\n";
		strHttpHeader +="Content-Length: " + xmlBody.length() + "\r\n";
		strHttpHeader +="Host: " + xml.getIP() + ":" + xml.getPort() + "\r\n";
		strHttpHeader +="Connection: Keep-Alive\r\n";
		strHttpHeader +="Authorization: Basic " + xml.getAuth() + "\r\n";
		//strHttpHeader +="User-Agent: Apache-HttpClient/4.1.1 (java 1.5)"; 
		strHttpHeader +="\r\n";
		

		String strXml = strHttpHeader + xmlBody;
		
		System.out.println(" :::: Soap Send Message :::: ");
		System.out.println(strXml);
		
		String strResult = xml.SendSoapMessage(strXml, timeOutMilliSecond);
		
		System.out.println(" :::: Soap Return Message :::: ");
		System.out.println(strResult);
		
		return strResult;
	}
	//
	////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////
	// SXML
	public static String RequestSoapSXML(String id, String pw, String urlIP, String urlPort, String xmlBody) {
		// TimeOut Default 3 second
		return RequestSoapSXML(id, pw, urlIP, urlPort, xmlBody, 3000);
	}
	public static String RequestSoapSXML(String id, String pw, String urlIP, String urlPort, String xmlBody, int timeOutMilliSecond) {
		SSLContext ctx = null;
		X509TrustManager xtm = new SoapTrustManager();
		TrustManager[] mytm = { xtm };
		try {
			ctx = SSLContext.getInstance("SSL");
			ctx.init(null, mytm, null);
		} catch (NoSuchAlgorithmException e) {
			return e.toString();
		} catch (KeyManagementException ekey) {
			return ekey.toString();
		}
		
		SoapXML xml = new SoapXML(urlIP, Integer.parseInt(urlPort), id, pw, ctx);

		String strHttpHeader = "POST https://"+xml.getIP()+":"+xml.getPort()+"/realtimeservice2/services/RISService70 HTTP/1.1\r\n";
		strHttpHeader +="Accept-Encoding: gzip,deflate\r\n";
		strHttpHeader +="Content-Type: text/xml;charset=UTF-8\r\n";
		strHttpHeader +="SOAPAction: \"selectCmDeviceExt\"\r\n";
		//strHttpHeader +="Accept: text/*\r\n";
		strHttpHeader +="Content-Length: " + xmlBody.length() + "\r\n";
		strHttpHeader +="Authorization: Basic " + xml.getAuth() + "\r\n";
		strHttpHeader +="Host: " + xml.getIP() + ":" + xml.getPort() + "\r\n";
		strHttpHeader +="Connection: Keep-Alive\r\n";
		strHttpHeader +="\r\n";
		
		String strXml = strHttpHeader + xmlBody;
		//System.out.println("Request >>>>>> \n" + strXml);
		
		String strResult = xml.SendSoapMessage(strXml, timeOutMilliSecond);
		
		return strResult;
	}
	////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////
	// UCCE
	public static String RequestSoapUcce(String reqType, String id, String pw, String urlIP, String urlPort, String xmlBody) {
	// TimeOut Default 3 second
	String rtnStr = "";
	rtnStr = RequestSoapUcce(id, pw, urlIP, urlPort, xmlBody, 30000);
	
	return rtnStr;
	}
	
	public static String RequestSoapUcce(String id, String pw, String urlIP, String urlPort, String xmlBody, int timeOutMilliSecond) {
	SoapXML xml = new SoapXML(urlIP, Integer.parseInt(urlPort), id, pw);
	
	String strHttpHeader = "GET /unifiedconfig/config/agent HTTP/1.1\r\n";
	strHttpHeader +="Accept-Encoding: gzip,deflate\r\n";
	strHttpHeader +="Host: " + xml.getIP() +":"+xml.getPort() + "\r\n";
	strHttpHeader +="Connection: Keep-Alive\r\n";
	strHttpHeader +="User-Agent: Apache-HttpClient/4.1.1 (java 1.5)\r\n";
	strHttpHeader +="Authorization: Basic " + xml.getAuth() + "\r\n";
	//strHttpHeader +="Content-Type: text/xml;charset=UTF-8\r\n";
	//strHttpHeader +="SOAPAction: \"selectCmDeviceExt\"\r\n";
	//strHttpHeader +="Accept: text/*\r\n";
	//strHttpHeader +="Content-Length: " + xmlBody.length() + "\r\n";
	
	strHttpHeader +="\r\n";
	if(xmlBody == null) 
	xmlBody = "";
	
	String strXml = strHttpHeader + xmlBody;
	
	String strResult = xml.SendSoapMessageTest(strXml, timeOutMilliSecond);
	
	return strResult;
	}
	
	public static String RequestSoapUccePUT(String id, String pw, String urlIP, String urlPort
	, String xmlBody, int timeOutMilliSecond, String agentTid) {
	SoapXML xml = new SoapXML(urlIP, Integer.parseInt(urlPort), id, pw);
	
	String strHttpHeader = "PUT /unifiedconfig/config/agent/"+agentTid+" HTTP/1.1\r\n";
	strHttpHeader +="Accept-Encoding: gzip,deflate\r\n";
	strHttpHeader +="Content-Type: application/xml;charset=UTF-8\r\n";
	//strHttpHeader +="Accept: text/*\r\n";
	strHttpHeader +="Content-Length: " + xmlBody.length() + "\r\n";
	strHttpHeader +="Authorization: Basic " + xml.getAuth() + "\r\n";
	strHttpHeader +="Host: " + xml.getIP() + ":" + xml.getPort() + "\r\n";
	strHttpHeader +="Connection: Keep-Alive\r\n";
	strHttpHeader +="\r\n";
	
	String strXml = strHttpHeader + xmlBody;
	//System.out.println("Request >>>>>> \n" + strXml);
	
	String strResult = xml.SendSoapMessageUcce(strXml, timeOutMilliSecond);
	
	return strResult;
	}
	////////////////////////////////////////////////////////////////
}
