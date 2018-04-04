package com.isi.axl.soap;

import java.io.BufferedReader;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.InetSocketAddress;
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

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.isi.file.GLogWriter;
import com.isi.file.ILog;

/**
 * @author skan
 */
public class SoapXML {

	SSLContext      m_ctx = null;
	String          m_auth = "";
	String          m_ip;
	int             m_port;
	String 			m_cmUser;
	String			m_cmPwd;
	
	private ILog logwrite;
	
	public SoapXML(String ip, int port, String id, String pwd, SSLContext ctx) {
		
		try {
			m_ctx = ctx;
			m_ip = ip;
			m_port = port;
			String auth = id + ":" + pwd;
			m_auth = Text2Base64.getBase64(auth);
			logwrite = new GLogWriter();
		} catch(Exception e) {
			
		}
	}
	
	public SoapXML(String ip, int port, String id, String pwd) {
		
		try {
			m_ip = ip;
			m_port = port;
			String auth = id + ":" + pwd;
			m_auth = Text2Base64.getBase64(auth);
			logwrite = new GLogWriter();
		} catch(Exception e) {
			
		}
	}
	
	public void setcmIP(String cmIP) {
		this.m_ip = cmIP;
	}
	public void setcmPort(int port) {
		this.m_port = port;
	}
	public void setcmUser(String cmUser) {
		this.m_cmUser = cmUser;
	}
	public void setcmPwd(String cmPwd) {
		this.m_cmPwd = cmPwd;
	}
	public void setcmAuth(String cmUser , String cmPwd) {
		String auth = cmUser + ":" + cmPwd;
		this.m_auth = Text2Base64.getBase64(auth);
	}
	
	public String getAuth() {
		return m_auth;
	}
	public String getIP() {
		return m_ip;
	}
	public int getPort() {
		return m_port;
	}
	
	/**
	 * 
	 * @param sb
	 * @return
	 */
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
	
	/*
	public String SendSoapMessageV2 (String ReqMsg, int nDefaultTimeOutMilliSecond) {
		
		Socket socket = null;
		String rcvMsg;
        try {
        	
            SSLSocketFactory sslFact = (SSLSocketFactory) m_ctx.getSocketFactory();
            socket = (SSLSocket) sslFact.createSocket(m_ip, m_port);
//
//            socket =  sslFact.createSocket();
//			socket.connect(new InetSocketAddress(m_ip, m_port), nDefaultTimeOutMilliSecond);
//			socket.setSoTimeout(nDefaultTimeOutMilliSecond);
            
            
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();
            
            StringBuffer sb = new StringBuffer(20000);
            byte[] bArray  = new byte[20000];
            int ch = 0;
            out.write(ReqMsg.getBytes("UTF-8"));
            
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
            
            RemoveSizeInfo(sb);
            
            in.close();
            out.close();
            
            if (sb.indexOf("<") < sb.length()) {
                rcvMsg = sb.substring(sb.indexOf("<"));
            } else {
            	return "Error Response is Not xml format!!";
            }
            
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();

			StringReader  rs        = new StringReader(rcvMsg);
			InputSource inputSource = new InputSource(rs);
			Document reply = db.parse(inputSource);
			
			if (reply != null) {
				// Success
				String strResult = DocumentToString(reply);
				return strResult;
			}
            
            

        } catch (UnknownHostException e) {
//            m_Log.warning("UnknownHostException", e);
        } catch (IOException ioe) {
        } catch (Exception ea) {
        } finally{
        }
		
		return "Error Unknwon!!";
	}
	*/
	
	/**
	 * SoapMessage
	 * @param ReqMsg
	 * @param nDefaultTimeOutMilliSecond
	 * @return
	 */
	
	
	public String SendSoapMessage(String ReqMsg, int nDefaultTimeOutMilliSecond ) {
		return SendSoapMessage(ReqMsg , nDefaultTimeOutMilliSecond , "unknown");
	}
	
	public String SendSoapMessage(String ReqMsg, int nDefaultTimeOutMilliSecond , String requestID) {
		Socket socket = null;
		OutputStream out = null;
		BufferedReader br = null;
		try {
			String rcvMsg;
			SSLSocketFactory sslFact = (SSLSocketFactory) m_ctx.getSocketFactory();
			socket = (SSLSocket) sslFact.createSocket(m_ip, m_port);
			
//			socket =  sslFact.createSocket();
//			socket.connect(new InetSocketAddress(m_ip, m_port), nDefaultTimeOutMilliSecond);
//			socket.setSoTimeout(nDefaultTimeOutMilliSecond);

			out = socket.getOutputStream();
			out.write(ReqMsg.getBytes("UTF-8"));

			br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			StringBuffer sb = new StringBuffer();

			int value = 0;
			while ((value = br.read()) != -1) {
				char c = (char) value;
				sb.append(c);
				if (sb.toString().lastIndexOf("</soapenv:Envelope>") != -1
						|| sb.toString().lastIndexOf("</SOAP-ENV:Envelope>") != -1) {
					break;
				}
			}
			RemoveSizeInfo(sb);
			
			br.close();
			out.close();
			br = null;
			out = null;
			
			if (sb.indexOf("<") < sb.length()) {
				rcvMsg = sb.substring(sb.indexOf("<"));
			} else {
				return "Error Response is Not xml format!!";
			}
			
//			System.out.println(rcvMsg);
			
			if(logwrite == null) {
				logwrite = new GLogWriter();
			}
			logwrite.httpLog(requestID, "SendSoapMessage", "recv message [" + rcvMsg + "]");
			
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();

			StringReader  rs        = new StringReader(rcvMsg);
			InputSource inputSource = new InputSource(rs);
			Document reply = db.parse(inputSource);
			
			if (reply != null) {
				// Success
				String strResult = DocumentToString(reply);
				return strResult;
			}
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
				if ( br != null) { br.close(); }
				if ( out != null) { out.close(); }
				if (socket != null) { socket.close(); }
			} catch (final Exception exc) {
				return "Error closing connection to server: "+ exc.getMessage();
			}
		}
		return "Error Unknwon!!";
	}
	
	public String SendSoapMessageTest(String ReqMsg, int nDefaultTimeOutMilliSecond) {
		Socket socket = null;
		InputStream in = null;
		OutputStream out = null;
		
		try {
			String rcvMsg;

			socket =  new Socket();	//sslFact.createSocket();
			socket.connect(new InetSocketAddress(m_ip,
					m_port), nDefaultTimeOutMilliSecond);
			socket.setSoTimeout(nDefaultTimeOutMilliSecond);
			
//			System.out.println(socket.isConnected());
			
			in = socket.getInputStream();
			out = socket.getOutputStream();
			
			StringBuffer sb = new StringBuffer(20000);
			byte[] bArray  = new byte[20000];
			int ch = 0;
			out.write(ReqMsg.getBytes("UTF-8"));

			while ((ch = in.read(bArray)) != -1) {
				String temp = new String(bArray, 0, ch);
				sb.append(temp);
				if (sb.lastIndexOf("</soapenv:Envelope>") != -1 || sb.lastIndexOf("</SOAP-ENV:Envelope>") != -1
						|| sb.lastIndexOf("</results>") != -1) {
					break;
				}
			}
			RemoveSizeInfo(sb);
			
			in.close();
			out.close();
			in = null;
			out = null;
			
			if (sb.indexOf("<") < sb.length()) {
				rcvMsg = sb.substring(sb.indexOf("<"));
			} else {
				return "Error Response is Not xml format!!";
			}

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();

			StringReader  rs        = new StringReader(rcvMsg);
			InputSource inputSource = new InputSource(rs);
			Document reply = db.parse(inputSource);

			
			if (reply != null) {
				// Success
				String strResult = DocumentToString(reply);
				return strResult;
			}
		} 
		catch (SocketException se) {
			return "Error socket timeout: " + se.toString();
		}
		catch (UnknownHostException ue) {
			return "Error connecting to host: " + ue.toString();
		} catch (IOException ioe) {
			return "Error sending/receiving from server: " + ioe.toString();
		} catch (Exception ea) {
			return "Unknown exception "  + ea.toString();
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
	
	public String SendSoapMessageUcce(String ReqMsg, int nDefaultTimeOutMilliSecond) {
		Socket socket = null;
		InputStream in = null;
		OutputStream out = null;
		
		try {
			String rcvMsg;

			socket =  new Socket();	//sslFact.createSocket();
			socket.connect(new InetSocketAddress(m_ip,
					m_port), nDefaultTimeOutMilliSecond);
			socket.setSoTimeout(nDefaultTimeOutMilliSecond);
			
//			System.out.println(socket.isConnected());
			
			in = socket.getInputStream();
			out = socket.getOutputStream();
			
			byte[] bArray  = new byte[20000];
			out.write(ReqMsg.getBytes("UTF-8"));
			
			rcvMsg = new String(bArray, 0, in.read(bArray));
			
			in.close();
			out.close();
			in = null;
			out = null;
			
			return rcvMsg;
			
		} 
		catch (SocketException se) {
			return "Error socket timeout: " + se.toString();
		}
		catch (UnknownHostException ue) {
			return "Error connecting to host: " + ue.toString();
		} catch (IOException ioe) {
			return "Error sending/receiving from server: " + ioe.toString();
		} catch (Exception ea) {
			return "Unknown exception "  + ea.toString();
		} finally{
			try {
				if ( in != null) { in.close(); }
				if ( out != null) { out.close(); }
				if (socket != null) { socket.close(); }
			} catch (final Exception exc) {
				return "Error closing connection to server: "+ exc.getMessage();
			}
		}
	}
}

