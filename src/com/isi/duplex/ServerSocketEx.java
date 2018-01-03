package com.isi.duplex;

import java.io.IOException;
import java.net.*;

import com.isi.exception.ExceptionUtil;
import com.isi.file.*;
/**
*
* @author greatyun
*/
public class ServerSocketEx extends Thread {
	
	protected 	int 			port;
	protected 	boolean			isStopReq;
	protected	ILog			logwrite;
	protected	ServerSocket	serverSocket;
	private DuplexMgr			duplexMgr;
	
	public ServerSocketEx (int port){
		this.port = port;
		logwrite = new GLogWriter();
		duplexMgr = DuplexMgr.getInstance();
	}
	
	public void startServer(){
		
		try {
			
			serverSocket = new ServerSocket(port);
			logwrite.duplexLog(duplexMgr.getActiveMode(),"ServerSocketEx startServer()", "It's ready to accept !!");
			
			this.start();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(ExceptionUtil.getPrintWriter());
			logwrite.duplexLog(duplexMgr.getActiveMode(),"ServerSocketEx startServer()", ExceptionUtil.getStringWriter().toString());
		}
	}
	
	public void requestStop(){
		this.isStopReq = true;
	}
	public boolean isStopReq(){
		return isStopReq;
	}
	
	public void close(){
		
		requestStop();
		
		try{
			
			if(serverSocket != null){
				serverSocket.close();
			}
			serverSocket = null;
			
		}catch (Exception e){
			e.printStackTrace(ExceptionUtil.getPrintWriter());
			logwrite.exceptionLog("", "close()", ExceptionUtil.getStringWriter().toString());
		}
		
		
	}
	
}
