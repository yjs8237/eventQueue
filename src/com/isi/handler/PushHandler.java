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

import com.cisco.jtapi.extensions.CiscoTerminal;
import com.isi.constans.LOGLEVEL;
import com.isi.constans.LOGTYPE;
import com.isi.constans.PROPERTIES;
import com.isi.constans.RESULT;
import com.isi.data.*;
import com.isi.exception.ExceptionUtil;
import com.isi.file.ILog;
import com.isi.file.LogMgr;
import com.isi.file.LogWriter;
import com.isi.file.PropertyRead;
import com.isi.process.DBQueueMgr;
import com.isi.service.JtapiService;
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
	        	resultVO.setResultMsg("There is no device ip address");
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
	        	returnCode = RESULT.RTN_EXCEPTION;
	        	resultVO.setPopup_yn("N");
//	        	return sendTerminalPush(xml, xmlInfo);
	        }
	        
	        resultVO.setReturnCode(returnCode);
	        
	        return resultVO;
	        
	    }

	public PushResultVO sendTerminalPush(String xml, XmlVO xmlInfo) {
		// TODO Auto-generated method stub
		// Push 가 실패하면 터미널에 직접 XML 데이터 send

		PushResultVO resultVO = new PushResultVO();
		
		boolean isSuccess = false;
		
		try {
			CiscoTerminal terminal = JtapiService.getInstance().getTerminal(xmlInfo.getTerminal());
			if (terminal != null) {
				m_Log.standLog(threadID, "sendTerminalPush", "## push send -> " + xml);

				byte[] returnByte = terminal.sendData(xml.getBytes());
				String returnString = new String(returnByte).replaceAll("\n", "").replaceAll("\r", "");
				m_Log.standLog(threadID, "sendTerminalPush", "## push response -> " + returnString);
				resultVO.setResultMsg(returnString);
				if (resultVO.getResultMsg().contains("Data=\"Success\"")
						|| resultVO.getResultMsg().contains("Data=\"SUCCESS\"")) {
					resultVO.setPopup_yn("Y");
					resultVO.setReturnCode(RESULT.RTN_SUCCESS);
					isSuccess = true;	// Push 성공
				} else {
					resultVO.setPopup_yn("N");
					resultVO.setReturnCode(RESULT.RTN_EXCEPTION);
				}
			} else {
				m_Log.standLog(threadID, "sendTerminalPush", "## Terminal 정보 없음 로그인 여부 확인 필요 ##");
				resultVO.setPopup_yn("N");
				resultVO.setReturnCode(RESULT.RTN_EXCEPTION);
				resultVO.setResultMsg(xmlInfo.getTerminal() + " 정보 없음, 로그인 여부 확인 필요");
			}
		} catch (Exception e) {
			e.printStackTrace(ExceptionUtil.getPrintWriter());
			m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.ERR_LOG, threadID, "sendTerminalPush",
					ExceptionUtil.getStringWriter().toString());
			resultVO.setPopup_yn("N");
			resultVO.setReturnCode(RESULT.RTN_EXCEPTION);
			resultVO.setResultMsg(e.getLocalizedMessage());
		}
		
		if(!isSuccess) {
			// 팝업이 성공하지 못하면
			m_Log.standLog(threadID, "sendTerminalPush", "## Push 실패 Re-try ## ");
			resultVO = push(xml, xmlInfo, false);
		}

		return resultVO;

	}

	
}
