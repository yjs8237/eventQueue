package com.isi.duplex;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.isi.exception.ExceptionUtil;
import com.isi.file.GLogWriter;
import com.isi.file.ILog;
/**
*
* @author greatyun
*/
public class ClientSockThread extends Thread {
	
	protected Socket 			sock;
	protected ILog				logwrite;
	protected boolean			isStopReq;
	protected BufferedReader	buffer_reader;
	protected PrintWriter		print_writer;
	protected String 				ip;
	protected int 				port;
	protected DuplexMgr			duplexMgr;
	private String 				time;
	
	
	private static final String aliveCheckRequest	=	"ALIVEREQ";
	private static final String aliveCheckResponse	=	"ALIVERES";
	
	public ClientSockThread(Socket sock){
		this.sock = sock;
		isStopReq = false;
		logwrite = new GLogWriter();
		duplexMgr = DuplexMgr.getInstance();
	}
	
	
	public void channelInit(){
		
		try{
			sock.setTcpNoDelay(true);
			buffer_reader 	= new BufferedReader(new InputStreamReader(sock.getInputStream()));
			print_writer	= new PrintWriter(new OutputStreamWriter(sock.getOutputStream()), true);
			ip = sock.getInetAddress().getHostAddress();
			port = sock.getPort();
		} catch (Exception e){
			e.printStackTrace(ExceptionUtil.getPrintWriter());
			logwrite.duplexLog(duplexMgr.getActiveMode(),"ClientSockThread channelInit()", ExceptionUtil.getStringWriter().toString());
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
	
	
	public void run() {
		
		time = DuplexMgr.getInstance().getCurrentTime();
		String data = makeJsonData(aliveCheckRequest, time);
		send(data);
		
		try {
			
			while (!isStopReq()) {
				
				Thread.sleep(3000);
				
				if (sock == null) {
					close();
					break;
				}

				if (!sock.isBound()) {
					close();
					break;
				}

				String recv_msg = buffer_reader.readLine();
				logwrite.duplexLog(duplexMgr.getActiveMode(),"ClientSockThread run()", "(" + ip + "," + port+ ") RECV[" + recv_msg + "]");
				
				String time = DuplexMgr.getInstance().getCurrentTime();
				send(makeJsonData(aliveCheckRequest, time));
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(ExceptionUtil.getPrintWriter());
			logwrite.duplexLog(duplexMgr.getActiveMode(),
					"ClientSockThread run()", ExceptionUtil.getStringWriter()
							.toString());
			close();
			
			duplexMgr.statusChange();
			
		} 

	}
	
	private void receive(String msg) throws Exception {
		
		if(msg == null){
			return;
		}
		JSONParser parser = new JSONParser();
		JSONObject jsonData = (JSONObject) parser.parse(msg);
		
		String remoteTime = (String)jsonData.get("time");
		String localTime = DuplexMgr.getInstance().getCurrentTime();
		
		// remote 시간이 작으면.. 상대방이 Active
		if(Integer.parseInt(remoteTime) < Integer.parseInt(localTime)){
			DuplexMgr.getInstance().setStandByMode();
		} else {
			DuplexMgr.getInstance().setActiveMode();
		}
		
	}
	
	
	private void send(String msg){
		try {
			logwrite.duplexLog(DuplexMgr.getInstance().getActiveMode(), "ClientSockThread send()", msg);
			print_writer.println(msg);
			print_writer.flush();
		}catch (Exception e){
			e.printStackTrace(ExceptionUtil.getPrintWriter());
			logwrite.duplexLog(DuplexMgr.getInstance().getActiveMode(), "ClientSockThread send()", ExceptionUtil.getStringWriter().toString());
		}
	}
	
	public void close(){
		
		requestStop();
		
		try{
			
			if(sock != null){
				sock.close();
			}
			sock = null;
			
			if(buffer_reader != null){
				buffer_reader.close();
				buffer_reader = null;
			}
			
			if(print_writer != null){
				print_writer.close();
				print_writer = null;
			}
		
			logwrite.duplexLog(duplexMgr.getActiveMode(), "ClientSockThread close()", "Session Closed !!");
			
		}catch (Exception e){
			e.printStackTrace(ExceptionUtil.getPrintWriter());
			logwrite.duplexLog(duplexMgr.getActiveMode(), "ClientSockThread close()", ExceptionUtil.getStringWriter().toString());
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
