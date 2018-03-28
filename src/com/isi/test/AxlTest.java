package com.isi.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.test.axl.soap.Text2Base64;




public class AxlTest {
	SSLContext      m_ctx = null;
	String 			m_auth = "";
	String 			m_ip = "";
	int			m_port;
	
	public AxlTest(String ip, int port, String id, String pwd) {
		try {
			X509TrustManager xtm = new SoapTrustManager();
			TrustManager[] mytm = { xtm };
			m_ctx = SSLContext.getInstance("SSL");
			m_ctx.init(null, mytm, null);
			String auth = id + ":" + pwd;
			m_auth = Text2Base64.getBase64(auth);
			m_ip = ip;
			m_port = port;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	public String SendSoapMessage(String ReqMsg, int nDefaultTimeOutMilliSecond) {
		
//		System.out.println(this.getClass().getClassLoader().getResource("."));
		
		Socket socket = null;
		InputStream in = null;
		OutputStream out = null;
		
		try {
			String rcvMsg;
			SSLSocketFactory sslFact = (SSLSocketFactory) m_ctx.getSocketFactory();
			socket = (SSLSocket) sslFact.createSocket(m_ip, m_port);
			
//			socket =  sslFact.createSocket();
//			socket.connect(new InetSocketAddress(m_ip, m_port), nDefaultTimeOutMilliSecond);
//			socket.setSoTimeout(nDefaultTimeOutMilliSecond);
			
			
			out = socket.getOutputStream();
			out.write(ReqMsg.getBytes("UTF-8"));
			
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "ks_c_5601-1987"));
			StringBuffer sb = new StringBuffer();
			
			int value = 0;
			while((value = br.read()) != -1) {
				char c = (char)value;
//				System.out.println(c);
				sb.append(c);
				if (sb.toString().lastIndexOf("</soapenv:Envelope>") != -1 || sb.toString().lastIndexOf("</SOAP-ENV:Envelope>") != -1) {
					break;
				}
			}
			
			RemoveSizeInfo(sb);
			
			System.out.println(sb.toString());
			
				br.close();
			out.close();
			br = null;
			out = null;
			
			
			
			
			/*
			in = socket.getInputStream();
			out = socket.getOutputStream();
			
			StringBuffer sb = new StringBuffer(20000);
			byte[] bArray  = new byte[20000];
			int ch = 0;
			out.write(ReqMsg.getBytes("UTF-8"));
			
			while ((ch = in.read(bArray)) != -1) {
				String temp = new String(bArray, 0, ch);
				sb.append(temp);
				if (sb.lastIndexOf("</soapenv:Envelope>") != -1 || sb.lastIndexOf("</SOAP-ENV:Envelope>") != -1) {
					break;
				}
			}
			RemoveSizeInfo(sb);
			
			//logger.debug("StringBuffer [" + sb.toString() + "]");
			
			in.close();
			out.close();
			in = null;
			out = null;
			
			*/
			
			
			if (sb.indexOf("<") < sb.length()) {
				rcvMsg = sb.substring(sb.indexOf("<"));
			} else {
				return "Error Response is Not xml format!!";
			}
			
//			System.out.println(rcvMsg);
			
			//logger.debug("rcvMsg [" + rcvMsg + "]");
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();

			StringReader  rs        = new StringReader(rcvMsg);
			InputSource inputSource = new InputSource(rs);
			Document reply = db.parse(inputSource);
			
			if (reply != null) {
				// Success
				String strResult = DocumentToString(reply);
				//logger.debug("strResult [" + strResult + "]");
				JSONArray array = jsonQueryResParsing(strResult);
				return array.toString();
			}
			
			
			
			/*
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();

			StringReader  rs        = new StringReader(rcvMsg);
			InputSource inputSource = new InputSource(rs);
			Document reply = db.parse(inputSource);
			
			if (reply != null) {
				// Success
				String strResult = DocumentToString(reply);
				//logger.debug("strResult [" + strResult + "]");
				return strResult;
			}
			*/
		} 
		catch (SocketException se) {
			return "Error socket timeout: " + se.toString();
		}
		catch (UnknownHostException ue) {
			return "Error connecting to host: " + ue.toString();
		} catch (IOException ioe) {
			return "Error sending/receiving from server: " + ioe.toString();
		} catch (Exception ea) {
			return "Error exception "  + ea.toString();
		} finally{
			try {
				if ( in != null) { in.close(); }
				if ( out != null) { out.close(); }
				if (socket != null) { socket.close(); }
			} catch (final Exception exc) {
				return "Error closing connection to server: "+ exc.getMessage();
			}
		}
		return "Error Unknwon!!";
	}
	
	public int RemoveSizeInfo(StringBuffer sb) {


		int hpos, tpos, curpos = 0, size;
		size = sb.length();
		do {
			hpos = sb.indexOf("\r\n", curpos);
			tpos = sb.indexOf("\r\n", hpos + 2);
			if (tpos > hpos &&  tpos - hpos > 1) {

				String value = sb.substring(hpos + 2, tpos);

				try {
					value.trim();
					Long.decode("0x" + value);
					if (tpos + 2 < size && ' ' == sb.charAt(tpos + 2)) {
						// tpos++;
					}
					sb.delete(hpos, tpos + 2);
					curpos = tpos;
				}catch (NumberFormatException e) {
					curpos = tpos;
				}
			} else {
				curpos = tpos;
			}
		} while (curpos != -1);
		return 0;
	}
	
	
	private JSONArray jsonQueryResParsing(String strResult) {
		// TODO Auto-generated method stub
		JSONArray resultObj = new JSONArray();
		JSONObject xmlJson = null;
		
		String retJsonMessage = "";
		String responseKey = "ns:executeSQLQueryResponse";
		try {
			
			xmlJson = XML.toJSONObject(strResult);
			if(xmlJson != null) {
				retJsonMessage = xmlJson.toString(4);
			} else {
//				logger.debug("## xmlJson is null ##");
			}
			//System.out.println(xmlJson.toString(4));
			
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
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			JSONObject chkJson = xmlJson.getJSONObject("soapenv:Envelope").getJSONObject("soapenv:Body")
					.getJSONObject("soapenv:Fault").getJSONObject("detail").getJSONObject("axlError");
			resultObj.put(chkJson);
			return resultObj;
		} finally {
			
		}
		return resultObj;
	}
	
	
	public static String DocumentToString(Document doc) {
		try {
			StringWriter sw = new StringWriter();
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

			transformer.transform(new DOMSource(doc), new StreamResult(sw));
			return sw.toString();
		} catch (Exception ex) {
			throw new RuntimeException("Error converting to String", ex);
		}
	}
}
