package com.isi.handler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

import com.isi.constans.PROPERTIES;
import com.isi.constans.RESULT;
import com.isi.data.XmlInfoMgr;
import com.isi.exception.ExceptionUtil;
import com.isi.file.GLogWriter;
import com.isi.file.ILog;
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
	private int connTimeout;
	private int readTimeout;
	private String threadID;
	private ILog logwrite;
	private String parameter;
	private String requestID;
	
	public HttpUrlHandler (ILog logwrite , String parameter , String requestID){
		pr = PropertyRead.getInstance();
		this.logwrite = logwrite;
		this.parameter = parameter;
		this.requestID = requestID;
//		connTimeout = XmlInfoMgr.getInstance().getConnectTimeout();
//		readTimeout = XmlInfoMgr.getInstance().getReadTimeout();
	}
	
	
	public int invokeURL(String strurl){
		 
		HttpURLConnection conn = null;
		
//		System.out.println("URL : " + strurl);
		
//		strurl = "http://127.0.0.1:9000/register?test=10";
		try {
			 URL url = new URL(strurl);
			 
	         conn = (HttpURLConnection) url.openConnection();
	         conn.setRequestMethod("GET");
	         conn.setDoInput(true);
	         conn.setDoOutput(true);
	         conn.setConnectTimeout(3000);
	         conn.setReadTimeout(3000);
	         conn.connect();
//	         
//	         System.out.println(conn.getResponseCode());
	         
//	         BufferedWriter  bw = new BufferedWriter(new PrintWriter(new OutputStreamWriter(conn.getOutputStream(), "utf-8"),true));
//	         bw.flush();
	         
	         BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
	         String line = "";
	         while((line = br.readLine()) != null) {
	        	 System.out.println(line);
	         }
	         
		} catch (Exception e){
			e.printStackTrace(ExceptionUtil.getPrintWriter());
			//m_Log.httpLog("invokeURL", ExceptionUtil.getStringWriter().toString());
			return RESULT.RTN_EXCEPTION;
		} finally {
			conn.disconnect();
		}
		
		return RESULT.RTN_SUCCESS;
	}
	
	
	public void sendLoginUrl() throws Exception{
		String url = "http://" + XmlInfoMgr.getInstance().getRemoteIP() + ":" + XmlInfoMgr.getInstance().getHttp_sync_port()  + "/loginsync";
		urlCallTypeOfGet(url , encode(parameter) , requestID);
		
	}
	
	
	 public  String urlCallTypeOfGet(String url, String param , String requestID) {
    	 int TIME_OUT = 1000;
    	 String rtn = "";
    	 try {
    		URL urlCon = new URL(url+"?"+param);
    		HttpURLConnection httpCon = (HttpURLConnection)urlCon.openConnection();
			
//    		System.out.println("## TEST " + url + param);
    		
    		httpCon.setConnectTimeout(TIME_OUT);
    		httpCon.setReadTimeout(TIME_OUT);
    		httpCon.setUseCaches(false);
    		httpCon.setRequestProperty("Accept", "application/json");
    		httpCon.setRequestProperty("Content-type", "text/plain; charset=utf-8");
    		httpCon.setDoInput(true);
    		httpCon.setRequestMethod("GET");
    		
            try {
            	InputStream is = httpCon.getInputStream();
    			Scanner scan = new Scanner(is);
    			while(scan.hasNext()) {
    				rtn += scan.nextLine();
    			}
    			scan.close();
    			
    			logwrite.httpLog(requestID, "urlCallTypeOfGet", "URL Call Success [" + url+"?"+param + "]");
            } catch (IOException e) {
            	
    			e.printStackTrace();
    			logwrite.httpLog(requestID, "urlCallTypeOfGet", "urlCallTypeOfGet IOException "+e.getMessage());
    			
    			System.out.println("urlCallTypeOfGet IOException "+e.getMessage());
            }finally {
            	httpCon.disconnect();
			}
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("The URL address is incorrect.");
			logwrite.httpLog(requestID, "urlCallTypeOfGet", "The URL address is incorrect.");
		} catch (IOException e) {
			e.printStackTrace();
			logwrite.httpLog(requestID, "urlCallTypeOfGet", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("It can't connect to the web page.");
			logwrite.httpLog(requestID, "urlCallTypeOfGet", e.getMessage());
		}
    	
    	//System.out.println("urlCallTypeOfGet DATA >>>>>>>> "+rtn);
    	return rtn;
     }
	 
		public String encode(String s) throws UnsupportedEncodingException {
			return java.net.URLEncoder.encode(s, "UTF-8");
		}
	
	
}
