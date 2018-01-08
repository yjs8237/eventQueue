package com.isi.handler;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import com.isi.constans.PROPERTIES;
import com.isi.constans.RESULT;
import com.isi.data.XmlInfoMgr;
import com.isi.exception.ExceptionUtil;
import com.isi.file.LogMgr;
import com.isi.file.PropertyRead;
import com.isi.utils.Utils;

/**
 * 
 * @author greatyun
 *
 */
public class HttpUrlHandler {
	
	private PropertyRead pr;
//	private ILog m_Log;
	private LogMgr m_Log;
	private int connTimeout;
	private int readTimeout;
	private String threadID;
	
	
	public HttpUrlHandler (){
		pr = PropertyRead.getInstance();
		m_Log = LogMgr.getInstance();
		connTimeout = XmlInfoMgr.getInstance().getConnectTimeout();
		readTimeout = XmlInfoMgr.getInstance().getReadTimeout();
	}
	
	
	public int invokeURL(String strurl){
		 
		HttpURLConnection conn = null;
//		strurl = "http://127.0.0.1:9000/register?test=10";
		try {
			 URL url = new URL(strurl);
			 
	         conn = (HttpURLConnection) url.openConnection();
	         conn.setDoInput(true);
	         conn.setDoOutput(true);
	         conn.setConnectTimeout(connTimeout);
	         conn.setReadTimeout(readTimeout);
	         conn.connect();
//	         
//	         BufferedWriter  bw = new BufferedWriter(new PrintWriter(new OutputStreamWriter(conn.getOutputStream(), "utf-8"),true));
//	          bw.flush();
	         
		} catch (Exception e){
			e.printStackTrace(ExceptionUtil.getPrintWriter());
			m_Log.httpLog("invokeURL", ExceptionUtil.getStringWriter().toString());
			return RESULT.RTN_EXCEPTION;
		}
		
		return RESULT.RTN_SUCCESS;
	}
	
	
	
}
