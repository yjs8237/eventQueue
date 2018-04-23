package com.isi.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.isi.axl.AdministrativeXML;
import com.isi.axl.CiscoPhoneInfo;
import com.isi.axl.SoapTrustManager;
import com.isi.constans.LOGLEVEL;
import com.isi.constans.LOGTYPE;
import com.isi.constans.PROPERTIES;
import com.isi.constans.SVCTYPE;
import com.isi.db.DBConnMgr;
import com.isi.handler.DeviceStatusHandler;
import com.isi.handler.PushHandler;
import com.isi.vo.EmployeeVO;
import com.isi.vo.XmlVO;
import com.test.axl.soap.Text2Base64;
import com.test.soap.AxlHandler;
import com.test.soap.Utils;
import com.test.vo.CmAxlInfoModel;

import sun.applet.Main;

public class TestMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String urlIP = "10.156.214.111";
		int urlPort = 8443;
		String ver = "8.5";
		String id = "SAC_IPT";
		String pwd = "dkdlvlxl123$";
		String auth = id + ":" + pwd;
		String m_auth = Text2Base64.getBase64(auth);
		 
		StringBuffer queryBuffer = new StringBuffer();
		queryBuffer.append("update numplan set iscallable = 't', cfurintvoicemailenabled = 't', cfurvoicemailenabled = 't' where dnorpattern = '");
		queryBuffer.append("0100").append("'");
		
		
//		queryBuffer.append("select * from numplan where dnorpattern = '1772'");
		
		String xmlBody = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"> <SOAP-ENV:Body> \r\n" + 
				"<axlapi:executeSQLQuery xmlns:axlapi=\"http://www.cisco.com/AXL/API/8.5\" sequence=\"1234\"> \r\n" + 
				"<sql>\r\n" + 
				queryBuffer.toString() + 
				"</sql> \r\n" + 
				"</axlapi:executeSQLQuery> \r\n" + 
				"</SOAP-ENV:Body> \r\n" + 
				"</SOAP-ENV:Envelope>";
		
		
		StringBuffer soapHeader = new StringBuffer();
		soapHeader.append("POST https://").append(urlIP).append(":").append(urlPort).append("/axl/ HTTP/1.1").append("\n");
		soapHeader.append("Accept-Encoding: gzip,deflate").append("\n");
		soapHeader.append("Content-Type: text/xml;charset=UTF-8").append("\n");
//		soapHeader.append("SOAPAction: \"CUCM:DB ver=").append(ver).append(" executeSQLQuery\"").append("\n");
		soapHeader.append("SOAPAction: \"CUCM:DB ver=").append(ver).append("\n");
		soapHeader.append("Content-Length: ").append(xmlBody.length()).append("\n");	
		soapHeader.append("Host: ").append(urlIP).append(":").append(urlPort).append("\n");
		soapHeader.append("Connection: Keep-Alive").append("\n");
		soapHeader.append("User-Agent: Apache-HttpClient/4.1.1 (java 1.5)").append("\n");
		soapHeader.append("Authorization: Basic ").append(m_auth).append("\n").append("\n");
		
		
		soapHeader.append(xmlBody);
		
		AxlTest axlTest = new AxlTest(urlIP, urlPort, id, pwd);
		String retMsg = axlTest.SendSoapMessageV2(soapHeader.toString(), 10000);
		
		System.out.println("--- return ---");
		System.out.println(retMsg);
		
		 
		 
//		
		
		
//		System.out.println(CommonUtil.getPhoneMask(extension));
		
		
		
	}
	
	public void testReturn(EmployeeVO param) {
		
		param.setExtension("2");
		System.out.println(param.getExtension());
		
	}
	
	private String getHexToDec(String hex) {
		   long v = Long.parseLong(hex, 16);   
		   return String.valueOf(v);
		}
	
	
	public static void gogotest() {
		
		CmAxlInfoModel model = new CmAxlInfoModel();
		model.setCmID("xmluser");
		model.setCmIP("192.168.230.120");
		model.setCmPwd("!Insung2018#");
		model.setCmPort(8443);
		

        try {
            String ver  = "8.5";
        	
        	StringBuffer queryBuffer = new StringBuffer();
    		queryBuffer.append("SELECT").append("\n");
    		queryBuffer.append("PICK.pkid AS pick_pkid, PICK.name AS pickup_grp_name, NUM.dnorpattern AS pickup_grp_num , NUM.pkid AS fknumplan_pickup , ROUTE.description").append("\n");
    		queryBuffer.append("FROM (").append("\n");
    		queryBuffer.append(" SELECT * FROM pickupgroup ").append("\n");
    		queryBuffer.append(" ) PICK LEFT OUTER JOIN ( ").append("\n");
    		queryBuffer.append(" SELECT pkid, fkroutepartition ,dnorpattern FROM numplan ").append("\n");
    		queryBuffer.append(" ) NUM ON PICK.fknumplan_pickup = NUM.pkid ").append("\n");
    		queryBuffer.append(" LEFT OUTER JOIN ( ").append("\n");
    		queryBuffer.append(" SELECT pkid , description FROM routepartition ").append("\n");
    		queryBuffer.append(" ) ROUTE ON NUM.fkroutepartition = ROUTE.pkid ").append("\n");
        	
        	
        	StringBuffer soapHeader = new StringBuffer();
    		soapHeader.append("POST https://").append(model.getCmIP()).append(":").append(model.getCmPort()).append("/axl/ HTTP/1.1").append("\n");
    		soapHeader.append("Accept-Encoding: gzip,deflate").append("\n");
    		soapHeader.append("Content-Type: text/xml;charset=UTF-8").append("\n");
    		soapHeader.append("SOAPAction: \"CUCM:DB ver=").append(ver).append(" executeSQLQuery\"").append("\n");
    		soapHeader.append("Content-Length: ").append(queryBuffer.toString().length()).append("\n");	
    		soapHeader.append("Host: ").append(model.getCmIP()).append(":").append(model.getCmPort()).append("\n");
    		soapHeader.append("Connection: Keep-Alive").append("\n");
    		soapHeader.append("User-Agent: Apache-HttpClient/4.1.1 (java 1.5)").append("\n");
    		soapHeader.append("Authorization: Basic ").append(Utils.getBase64(model.getCmID()+":"+model.getCmPwd())).append("\n").append("\n");
    		soapHeader.append(queryBuffer.toString());
    		
    		
    		sendSoap(soapHeader.toString() , model);
        } catch (Exception e) {
        	
        }
	
	}
	
	public static void sendSoap(String aReqMsg , CmAxlInfoModel model ) {

        Socket socket = null;
        
        try {
        	
        	X509TrustManager xtm = new SoapTrustManager();
            TrustManager[] mytm = { xtm };

                SSLContext ctx = SSLContext.getInstance("SSL");
                ctx.init(null, mytm, null);
        	
            String rcvMsg;
            SSLSocketFactory sslFact = (SSLSocketFactory) ctx.getSocketFactory();
            socket = (SSLSocket) sslFact.createSocket(model.getCmIP(), model.getCmPort());

            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();
            
//            System.out.println("SEND SOAP MESSAGE");
//            System.out.println(aReqMsg);
            
            StringBuffer sb = new StringBuffer(8192);
            byte[] bArray  = new byte[8192];
            int ch = 0;
            out.write(aReqMsg.getBytes("UTF-8"));
            
//            m_Log.fine(aReqMsg);
            while ((ch = in.read(bArray)) != -1) {
                String temp = new String(bArray, 0, ch);
                sb.append(temp);
              // 종료시점에 바로 소켓을 종료함.
                if (sb.lastIndexOf("</soapenv:Envelope>") != -1 || sb.lastIndexOf("</SOAP-ENV:Envelope>") != -1) {
                    break;
                }
            }
            
//            System.out.println("RECV SOAP MSG : " + sb.toString());
            
//            RemoveSizeInfo(sb);
//            m_Log.fine(sb.toString());
            in.close();
            out.close();
            /*
            if (sb.indexOf("<") < sb.length()) {
                rcvMsg = sb.substring(sb.indexOf("<"));
            } else {
                return -1;
            }
*/
        } catch (UnknownHostException e) {
//            m_Log.warning("UnknownHostException", e);
        } catch (IOException ioe) {
        } catch (Exception ea) {
        } finally{
        }
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
//		System.out.println(temp_1);
		    
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
//					System.out.println("cnt -> " +  cnt + " , null");
				} else {
//					System.out.println("cnt -> " +  cnt + " , not");
				}
			}
			
			
		} catch (Exception e) {
//			System.out.println(e.toString());
		}
		
		
		
	}

}
