package com.test.axl.soap;

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
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.Name;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.test.main.TrustAllCertificates;
import com.test.main.TrustAllHosts;

/**
 * @author skan
 */
public class SoapXML {

	SSLContext      m_ctx = null;
	String          m_auth = "";
	String          m_ip;
	int             m_port;
	
	public SoapXML(String ip, int port, String id, String pwd, SSLContext ctx) {
		
		try {
			m_ctx = ctx;
			m_ip = ip;
			m_port = port;
			String auth = id + ":" + pwd;
			m_auth = Text2Base64.getBase64(auth);
		} catch(Exception e) {
			
		}
	}
	
	public SoapXML(String ip, int port, String id, String pwd) {
		
		try {
			m_ip = ip;
			m_port = port;
			String auth = id + ":" + pwd;
			m_auth = Text2Base64.getBase64(auth);
		} catch(Exception e) {
			
		}
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
            
            System.out.println("RECV SOAP MSG : " + sb.toString());
            
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
	
	
	/**
	 * SoapMessage
	 * @param ReqMsg
	 * @param nDefaultTimeOutMilliSecond
	 * @return
	 */
	public String SendSoapMessage(String ReqMsg, int nDefaultTimeOutMilliSecond) {
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

			//in = socket.getInputStream();
			out = socket.getOutputStream();
			
			
			StringBuffer sb = new StringBuffer(20000);
			byte[] bArray  = new byte[20000];
			int ch = 0;
			out.write(ReqMsg.getBytes("UTF-8"));
			
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			String line = "";
			while((line = br.readLine()) != null ) {
				System.out.println(line);
			}
			
			System.out.println("end");
			
			/*
			while ((ch = in.read(bArray)) != -1) {
				String temp = new String(bArray, 0, ch);
				sb.append(temp);
				if (sb.lastIndexOf("</soapenv:Envelope>") != -1 || sb.lastIndexOf("</SOAP-ENV:Envelope>") != -1) {
					break;
				}
			}
			RemoveSizeInfo(sb);
			*/
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
	
	
	
	public String testSoapRequestV2 () {
		
		String endpointUrl ="https://192.168.230.120:8443/axl/";
		
		 try {
		        final boolean isHttps = endpointUrl.toLowerCase().startsWith("https");
		        HttpsURLConnection httpsConnection = null;
		        // Open HTTPS connection
		        if (isHttps) {
		            // Create SSL context and trust all certificates
		            SSLContext sslContext = SSLContext.getInstance("SSL");
		            TrustManager[] trustAll = new TrustManager[] {new TrustAllCertificates()};
		            sslContext.init(null, trustAll, new java.security.SecureRandom());
		            // Set trust all certificates context to HttpsURLConnection
		            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
		            // Open HTTPS connection
		            URL url = new URL(endpointUrl);
		            httpsConnection = (HttpsURLConnection) url.openConnection();
		            // Trust all hosts
		            httpsConnection.setHostnameVerifier(new TrustAllHosts());
		            // Connect
		            httpsConnection.connect();
		        }
		        
		        
		        
		        // Send HTTP SOAP request and get response
		        SOAPConnection soapConnection = SOAPConnectionFactory.newInstance().createConnection();
		        SOAPMessage response = soapConnection.call(createSOAPRequest(), endpointUrl);
		        // Close connection
		        soapConnection.close();
		        // Close HTTPS connection
		        if (isHttps) {
		            httpsConnection.disconnect();
		        }
		        
		        System.out.println("23222");
//		        response.writeTo(System.out);
		        
		        SOAPBody soapBody = response.getSOAPBody();
		        
		        NodeList list = soapBody.getElementsByTagName("row");
//		        DeviceLineInfo info = null;
		        for(int i = 0; i < list.getLength(); i++)
		        {
//		            info = new DeviceLineInfo();
//		            Node node = list.item(i);
//		            NodeList children = node.getChildNodes();
//		            if(children.item(0).getFirstChild() != null)
//		                info.setPkid(children.item(0).getFirstChild().getNodeValue());
//		            if(children.item(1).getFirstChild() != null)
//		                info.setDn(children.item(1).getFirstChild().getNodeValue());
//		            if(children.item(2).getFirstChild() != null)
//		                info.setAlertingName(children.item(2).getFirstChild().getNodeValue());
//		            if(children.item(3).getFirstChild() != null)
//		                info.setDescription(children.item(3).getFirstChild().getNodeValue());
//		            if(children.item(4).getFirstChild() != null)
//		                info.setFkRoutePartition(children.item(4).getFirstChild().getNodeValue());
		        }
		        
		        
		        
		        return "";
		    } catch (Exception ex) {
		        // Do Something
		    	ex.printStackTrace();
		    }
		    return null;
	}
	
	
	public String testSoapRequest (String ReqMsg) {
		
		try {
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
		    SOAPConnection soapConnection = soapConnectionFactory.createConnection();

		    
		    // Send SOAP Message to SOAP Server
		    String url = "https://192.168.230.120:8443/axl/";
		    SOAPMessage soapResponse = soapConnection.call(createSOAPRequest(), url);
		    soapResponse.writeTo(System.out);
		    SOAPPart soapPart = soapResponse.getSOAPPart();
		    SOAPEnvelope envelope = soapPart.getEnvelope();
		    SOAPBody soapBody = envelope.getBody();
		    
		    
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getLocalizedMessage());
		}
		
		return "";
	}
	
	private SOAPMessage createSOAPRequest() throws Exception {
		
		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPMessage soapMessage = messageFactory.createMessage();
		SOAPPart soapPart = soapMessage.getSOAPPart();


		// SOAP Envelope
		SOAPEnvelope envelope = soapPart.getEnvelope();
		envelope.addNamespaceDeclaration("ns", "http://www.cisco.com/AXL/API/8.5");
		
		/*
		 * Constructed SOAP Request Message: 
		 * <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/" xmlns:example="http://ws.cdyne.com/"> <SOAP-ENV:Header/> <SOAP-ENV:Body>
		 * <example:VerifyEmail> <example:email>mutantninja@gmail.com</example:email>
		 * <example:LicenseKey>123</example:LicenseKey> </example:VerifyEmail>
		 * </SOAP-ENV:Body> </SOAP-ENV:Envelope>
		 */

		// SOAP Body
		SOAPBody soapBody = envelope.getBody();
		SOAPElement soapBodyFirst = soapBody.addChildElement("executeSQLQuery", "ns");
		QName name = new QName("sequence");
		soapBodyFirst.addAttribute(name, "?");
		SOAPElement soapBodySecond = soapBodyFirst.addChildElement("sql");
		soapBodySecond.addTextNode("select pkid, name from device where name = 'SEP001AA2660E8A'");
		
		
		/*
		SOAPElement soapBodyElem = soapBody.addChildElement("VerifyEmail", "axlapi");
		
		SOAPElement soapBodyElem1 = soapBodyElem.addChildElement("email", "axlapi");
		soapBodyElem1.addTextNode("mutantninja@gmail.com");
		SOAPElement soapBodyElem2 = soapBodyElem.addChildElement("LicenseKey", "axlapi");
		soapBodyElem2.addTextNode("123");
		 */
		MimeHeaders headers = soapMessage.getMimeHeaders();
		headers.addHeader("Accept-Encoding", "gzip,deflate");
		headers.addHeader("Content-Type", "text/xml;charset=UTF-8");
		headers.addHeader("Content-Length", String.valueOf(soapBody.toString().length()));
		headers.addHeader("SOAPAction", "CUCM:DB ver=8.5");
		headers.addHeader("Connection", "Keep-Alive");
		headers.addHeader("User-Agent", "Apache-HttpClient/4.1.1 (java 1.5)");
		headers.addHeader("Authorization", "Basic eG1sdXNlcjohSW5zdW5nMjAxOCM=");
		

		soapMessage.saveChanges();
		
		
		/* Print the request message */
		System.out.println("Request SOAP Message:");
		soapMessage.writeTo(System.out);
		System.out.println("");
		System.out.println("------");

		return soapMessage;
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
			
			System.out.println(socket.isConnected());
			
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
			
			System.out.println(socket.isConnected());
			
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

