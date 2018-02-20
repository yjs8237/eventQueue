package com.test.handler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import com.isi.constans.PROPERTIES;
import com.isi.constans.RESULT;
import com.isi.data.*;
import com.isi.exception.ExceptionUtil;
import com.isi.file.ILog;
import com.isi.file.LogMgr;
import com.isi.file.LogWriter;
import com.isi.file.PropertyRead;
import com.isi.utils.Utils;
import com.isi.vo.XmlVO;

/**
*
* @author greatyun
*/
public class TestPushHandler {
	
	
	private String authKey;
	private PropertyRead pr;
	private LogMgr m_Log;
	private String cmUser;
	private String cmPassword;
	
	public TestPushHandler(){
		pr = PropertyRead.getInstance();
		m_Log = LogMgr.getInstance();
		
		cmUser = XmlInfoMgr.getInstance().getCm1User();
		cmPassword = XmlInfoMgr.getInstance().getCm1Pwd();
	}
	
	 public int push(String xml, XmlVO xmlInfo, boolean getResult) {
		 return 0;
		 /*
		 String phoneIP = xmlInfo.getTargetIP();
		 HttpURLConnection conn = null;
		 BufferedWriter bw = null;
		 int returnCode = RESULT.RTN_EXCEPTION;
		 
	        if (phoneIP == null || phoneIP.isEmpty()) {
	            //System.out.println("PushXML.push() Null IP address");
	            return RESULT.RTN_EXCEPTION;
	        }
	        
	        m_Log.standLog(xmlInfo.getCallidByString(), "push", "## Push !! phoneIP ["+phoneIP+"] xml["+xml+"]"); 
	        
	        
	        StringBuffer response = new StringBuffer();
	        
	        try {
	          String httpData = "XML=" + URLEncoder.encode(xml, "utf-8");
	          //System.out.println(httpData+"---------");
	          URL url = new URL("http://" + phoneIP + "/CGI/Execute");
	          conn = (HttpURLConnection) url.openConnection();
	          conn.setDoInput(true);
	          conn.setDoOutput(true);
	          conn.setRequestMethod("POST");
	          conn.setFollowRedirects(getResult);
	          conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
	          authKey = Utils.getBase64(cmUser + ":" + cmPassword);
	          conn.setRequestProperty("Authorization", "Basic " + authKey);
	          
	          conn.setConnectTimeout(1000);
	          conn.setReadTimeout(3000);
	          conn.connect();
	          
	          bw = new BufferedWriter(new PrintWriter(new OutputStreamWriter(conn.getOutputStream(), "utf-8"),true));
	          bw.write(httpData);
	          bw.flush();
	          
	          BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
	          
	          String line = "";
	          while( (line = br.readLine()) != null){
	        	  response.append(line);
	          }
	          
	          m_Log.standLog(xmlInfo.getCallidByString(), "push", "## push response -> " + response.toString());
	          
	          returnCode = conn.getResponseCode();
	          
	        } catch (Exception e) {
	        	e.printStackTrace(ExceptionUtil.getPrintWriter());
	        	m_Log.exceptionLog(xmlInfo.getCallidByString(), "push", ExceptionUtil.getStringWriter().toString());
	        	returnCode = RESULT.RTN_EXCEPTION;
	        } finally {
	        	conn.disconnect();
	        	try {
	        		if(bw != null){
	        			bw.close();
	        		}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace(ExceptionUtil.getPrintWriter());
					m_Log.exceptionLog(xmlInfo.getCallidByString(), "push", ExceptionUtil.getStringWriter().toString());
					returnCode = RESULT.RTN_EXCEPTION;
				}
	        }
	        
	        return returnCode == HttpURLConnection.HTTP_OK ? returnCode : RESULT.RTN_EXCEPTION;
	        */
	    }

	
}
