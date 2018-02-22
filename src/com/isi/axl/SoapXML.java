package com.isi.axl;

import java.io.*;
import java.net.*;

import javax.net.ssl.*;
import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.InputSource;

import com.isi.constans.LOGLEVEL;
import com.isi.constans.LOGTYPE;
import com.isi.constans.SVCTYPE;
import com.isi.file.GLogWriter;
import com.isi.file.ILog;
import com.isi.utils.Utils;

public class SoapXML {

    private SSLContext      m_ctx = null;
    private String          m_auth = "";
    private String          m_ip;
    private int             m_port;
    private ILog            m_Log = new GLogWriter();
    private PrintWriter 	pw;
    private StringWriter	sw;
    protected static final String dbVer = "8.5";
    
    
    
    public SoapXML(String ip, int port, String id, String pwd, SSLContext ctx) {

        try {
            m_ctx = ctx;
            m_ip = ip;
            m_port = port;
            String auth = id + ":" + pwd;
            // m_auth = new sun.misc.BASE64Encoder().encode(auth.getBytes());
            m_auth = Utils.getBase64(auth);
            
            sw = new StringWriter();
            pw = new PrintWriter(sw);
            
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
    
    public int SendSoapMessage(String aReqMsg, Document [] aResDom) {

        Socket socket = null;
        
        try {
        	
            String rcvMsg;
            aResDom[0] = null;
            SSLSocketFactory sslFact = (SSLSocketFactory) m_ctx.getSocketFactory();
            socket = (SSLSocket) sslFact.createSocket(m_ip, m_port);

            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();
            
//            System.out.println("SEND SOAP MESSAGE");
//            System.out.println(aReqMsg);
            
            StringBuffer sb = new StringBuffer(8192);
            byte[] bArray  = new byte[8192];
            int ch = 0;
            out.write(aReqMsg.getBytes("UTF-8"));
            
            m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, SVCTYPE.GLOBAL, "SendSoapMessage", "SEND SOAP -> " + aReqMsg);
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
            m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, SVCTYPE.GLOBAL, "SendSoapMessage", "RECV SOAP -> " + sb.toString());
//            m_Log.fine(sb.toString());
            in.close();
            out.close();
            
            if (sb.indexOf("<") < sb.length()) {
                rcvMsg = sb.substring(sb.indexOf("<"));
            } else {
                return -1;
            }

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();

            StringReader  rs        = new StringReader(rcvMsg);
            InputSource inputSource = new InputSource(rs);
            Document reply = db.parse(inputSource);

            NodeList itemlist = null;

            if (reply != null) {

                itemlist = reply.getElementsByTagName("Fault");

                if (itemlist.getLength() > 0) {
                } else {
                    aResDom[0] = reply;
                }
            }
        } catch (UnknownHostException e) {
        	e.printStackTrace(pw);
        	m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.ERR_LOG, SVCTYPE.GLOBAL, "SendSoapMessage", sw.toString());
//            m_Log.warning("UnknownHostException", e);
            return -3;
        } catch (IOException ioe) {
        	ioe.printStackTrace(pw);
        	m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.ERR_LOG, SVCTYPE.GLOBAL, "SendSoapMessage", sw.toString());
            //m_Log.warning("IOException", ioe);
            // close the socket
        } catch (Exception ea) {
        	ea.printStackTrace(pw);
        	m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.ERR_LOG, SVCTYPE.GLOBAL, "SendSoapMessage", sw.toString());
            return -2 ;
        } finally{
            try {
                if (socket != null)
                socket.close();
            } catch (Exception exc) {
            	exc.printStackTrace(pw);
            	m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.ERR_LOG, SVCTYPE.GLOBAL, "SendSoapMessage", sw.toString());
//                m_Log.warning("IOException", exc);
            }
        }
        return 0;
    }
}
