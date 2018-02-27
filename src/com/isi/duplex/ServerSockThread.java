package com.isi.duplex;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

import org.json.simple.JSONObject;

import com.isi.constans.RESULT;
import com.isi.exception.ExceptionUtil;
import com.isi.file.*;
/**
*
* @author greatyun
*/
public class ServerSockThread extends Thread{
	
	protected Socket 			sock;
	protected ILog				logwrite;
	protected boolean			isStopReq;
	protected PrintWriter		print_writer;
	protected BufferedReader	buffer_reader;
	private boolean				isFirstTime;
	private String				ip;
	private int					port;
	private DuplexMgr			duplexMgr;
	
	private static final String aliveCheckRequest	=	"ALIVEREQ";
	private static final String aliveCheckResponse	=	"ALIVERES";
	
	public ServerSockThread(Socket sock) {
		this.sock = sock;
		logwrite = new GLogWriter();
		isStopReq = false;
		duplexMgr = DuplexMgr.getInstance();
	}
	
	public void channelInit(){
		
		try{
			sock.setTcpNoDelay(true);
			buffer_reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			print_writer 	= new PrintWriter(new OutputStreamWriter(sock.getOutputStream()), true);
			ip = sock.getInetAddress().getHostAddress();
			port = sock.getPort();
			
		} catch (Exception e){
			e.printStackTrace(ExceptionUtil.getPrintWriter());
			logwrite.duplexLog(duplexMgr.getActiveMode(),"ServerSockThread channelInit()", ExceptionUtil.getStringWriter().toString());
		}
		
	}
	
	public void startService(){
		channelInit();
		this.start();
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
			
			if(sock != null){
				sock.close();
			}
			sock = null;
			
			if(print_writer != null){
				print_writer.close();
			}
			print_writer = null;
			
			if(buffer_reader != null){
				buffer_reader.close();
			}
			buffer_reader = null;
			
			logwrite.duplexLog(duplexMgr.getActiveMode(),"ServerSockThread close()", "Session Closed !!");
			
		}catch (Exception e){
			e.printStackTrace(ExceptionUtil.getPrintWriter());
			logwrite.duplexLog(duplexMgr.getActiveMode(),"ServerSockThread close()", ExceptionUtil.getStringWriter().toString());
		}
		
		
	}
	
	public void run(){
		
		try {
			
			while(!isStopReq()){
				
				if(sock == null){
					close();
					break;
				}
				
				if(!sock.isBound()){
					close();
					break;
				}
				
				String recv_msg = buffer_reader.readLine();
				logwrite.duplexLog(duplexMgr.getActiveMode(),"ServerSockThread run()", "RECV[" + recv_msg + "]");
				
				String time = DuplexMgr.getInstance().getCurrentTime();
				send(makeJsonData(aliveCheckResponse, time));
				
			} 
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(ExceptionUtil.getPrintWriter());
			logwrite.duplexLog(duplexMgr.getActiveMode(),"ServerSockThread run()", ExceptionUtil.getStringWriter().toString());
			close();
		}
		
	}
	
	private void send(String msg){
		try {
			logwrite.duplexLog(DuplexMgr.getInstance().getActiveMode(), "ServerSockThread send()", "SEND["+msg+"]");
			print_writer.println(msg);
			print_writer.flush();
		}catch (Exception e){
			e.printStackTrace(ExceptionUtil.getPrintWriter());
			logwrite.duplexLog(DuplexMgr.getInstance().getActiveMode(), "ServerSockThread send()", ExceptionUtil.getStringWriter().toString());
		}
	}
	
	private String makeJsonData(String str, String time) {
		// TODO Auto-generated method stub
		JSONObject jsonData = new JSONObject();
		jsonData.put("type", str);
		jsonData.put("time", time);
		if(DuplexMgr.getInstance().getActiveMode()){
			jsonData.put("status", "active");
		} else {
			jsonData.put("status", "standby");
		}

		return jsonData.toJSONString();
	}
	
}
