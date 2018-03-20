package com.isi.handler;

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
import com.isi.process.DBQueueMgr;
import com.isi.utils.Utils;
import com.isi.vo.PushResultVO;
import com.isi.vo.XmlVO;

/**
*
* @author greatyun
*/
public class PushHandler {
	
	
	private String authKey;
	private PropertyRead pr;
//	private ILog m_Log;
	private LogMgr m_Log;
	private int connTimeout;
	private int readTimeout;
	private String threadID;
	
	public PushHandler(String threadID){
		pr = PropertyRead.getInstance();
		m_Log = LogMgr.getInstance();
		this.threadID = threadID;
//		connTimeout = Integer.parseInt(pr.getValue(PROPERTIES.CONNECT_TIMEOUT));
		connTimeout = XmlInfoMgr.getInstance().getConnectTimeout();
		readTimeout = XmlInfoMgr.getInstance().getReadTimeout();
	}
	
	 public PushResultVO push(String xml, XmlVO xmlInfo, boolean getResult) {
		 
		 String phoneIP = xmlInfo.getTargetIP();
		 HttpURLConnection conn = null;
		 BufferedWriter bw = null;
		 
		 PushResultVO resultVO = new PushResultVO();
		 
		 int returnCode = RESULT.RTN_EXCEPTION;
		 
	        if (phoneIP == null || phoneIP.isEmpty()) {
	            //System.out.println("PushXML.push() Null IP address");
	        	resultVO.setReturnCode(RESULT.RTN_EXCEPTION);
	            return resultVO;
	        }
	        
	        m_Log.standLog(threadID, "push", "## Push !! CMUSER["+xmlInfo.getCmUser()+"]CMPW["+xmlInfo.getCmPassword()+"] DN ["+xmlInfo.getTargetdn()+"] phoneIP ["+phoneIP+"] xml["+xml+"]"); 
	        
//	        System.out.println("## Push !! phoneIP ["+phoneIP+"] xml["+xml+"]");
	        
	        StringBuffer response = new StringBuffer();
	        
	        try {
	          String httpData = "XML=" + URLEncoder.encode(xml, "utf-8");
	          //System.out.println(httpData+"---------");
	          URL url = new URL("http://" + phoneIP + "/CGI/Execute");
//	          URL url = new URL("http://xml:xml!@#$@" + phoneIP + "/CGI/Execute");
	          conn = (HttpURLConnection) url.openConnection();
	          conn.setDoInput(true);
	          conn.setDoOutput(true);
	          conn.setRequestMethod("POST");
	          conn.setFollowRedirects(getResult);
	          conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
	          authKey = Utils.getBase64(xmlInfo.getCmUser() + ":" + xmlInfo.getCmPassword());
	          conn.setRequestProperty("Authorization", "Basic " + authKey);
	          
	          conn.setConnectTimeout(connTimeout);
	          conn.setReadTimeout(readTimeout);
	          conn.connect();
	          
	          bw = new BufferedWriter(new PrintWriter(new OutputStreamWriter(conn.getOutputStream(), "utf-8"),true));
	          bw.write(httpData);
	          bw.flush();
	          
	          BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
	          
	          String line = "";
	          while( (line = br.readLine()) != null){
	        	  response.append(line);
	          }
	          
	          m_Log.standLog(threadID, "push", "## push response -> " + response.toString());
	          
	          resultVO.setResultMsg(response.toString());
	          
	          returnCode = conn.getResponseCode();
	          
	        } catch (Exception e) {
	        	e.printStackTrace(ExceptionUtil.getPrintWriter());
	        	m_Log.exceptionLog(threadID, "push", ExceptionUtil.getStringWriter().toString());
	        	returnCode = RESULT.RTN_EXCEPTION;
	        	resultVO.setResultMsg(e.toString());
	        } finally {
	        	conn.disconnect();
	        	try {
	        		if(bw != null){
	        			bw.close();
	        		}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace(ExceptionUtil.getPrintWriter());
					m_Log.exceptionLog(threadID, "push", ExceptionUtil.getStringWriter().toString());
					returnCode = RESULT.RTN_EXCEPTION;
					resultVO.setResultMsg(e.toString());
				}
	        }
	        
	        if(resultVO.getResultMsg().contains("Data=\"Success\"") || resultVO.getResultMsg().contains("Data=\"SUCCESS\"")) {
	        	resultVO.setPopup_yn("Y");
	        } else {
	        	resultVO.setPopup_yn("N");
	        	returnCode = RESULT.RTN_EXCEPTION;
	        }
	        
	        resultVO.setReturnCode(returnCode);
	        
	        return resultVO;
	        
	    }

	
}
