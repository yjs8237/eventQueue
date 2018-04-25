package com.isi.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.isi.constans.APITYPE;
import com.isi.constans.CALLSTATE;
import com.isi.constans.RESULT;
import com.isi.data.CallStateMgr;
import com.isi.data.Employees;
import com.isi.data.ImageMgr;
import com.isi.data.MyAddressMgr;
import com.isi.data.XmlInfoMgr;
import com.isi.db.DBConnMgr;
import com.isi.duplex.AliveProc;
import com.isi.exception.ExceptionUtil;
import com.isi.file.GLogWriter;
import com.isi.file.ILog;
import com.isi.file.PropertyRead;
import com.isi.service.JtapiService;
import com.isi.thread.DeviceCheck;
import com.isi.thread.LoginProcess;
import com.isi.thread.MakeCall;
import com.isi.thread.StopCall;
import com.isi.vo.BaseVO;
import com.isi.vo.DeviceResetVO;
import com.isi.vo.DeviceStatusVO;
import com.isi.vo.EmployeeVO;
import com.isi.vo.JTapiResultVO;
import com.isi.vo.MakeCallVO;
import com.isi.vo.PickupVO;
import com.isi.vo.XmlVO;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
*
* @author greatyun
*/
public class ServerSocketHandler extends Thread{
	
	private int 			port;
	private PropertyRead 	pr;
	private ILog 			logwrite;
	private ServerSocket	serverSocket;
	
	private String			clientIP;
	private boolean			isStopReq;
	
	
	
	public ServerSocketHandler(int port){
		this.port = port;
		pr = PropertyRead.getInstance();
		logwrite = new GLogWriter();
		isStopReq = false;
	}
	
	public int startService(){
		
		try {
			serverSocket = new ServerSocket(port);
			return RESULT.RTN_SUCCESS;
			
		} catch (Exception e){
			e.printStackTrace(ExceptionUtil.getPrintWriter());
			logwrite.httpLog("" ,"startService()", ExceptionUtil.getStringWriter().toString());
			return RESULT.RTN_EXCEPTION;
		}
	}
	
	
	public void run() {
		
		while(!isStopReq()){
			
			try {
				logwrite.httpLog("" ,"run()", "Server Socket Handler is waiting for client...");
				
				Socket sock = serverSocket.accept();
				
				clientIP = sock.getInetAddress().getHostAddress();
				port = sock.getLocalPort();
				String requestID = String.valueOf(System.currentTimeMillis()) + "-" + clientIP + "-" + String.valueOf(Thread.currentThread().getId());
				
				ServerSockDataHandler dataHandler = new ServerSockDataHandler(sock, requestID);
				dataHandler.start();
				
			} catch (Exception e) {
				
			}
			
		}
		
	}
	
	public void requestStop(){
		this.isStopReq = true;
	}
	public boolean isStopReq(){
		return isStopReq;
	}
	
	
}

