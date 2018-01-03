package com.isi.service;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import com.isi.constans.PROPERTIES;
import com.isi.exception.ExceptionUtil;
import com.isi.file.*;
import com.isi.process.*;
/**
*
* @author greatyun
*/
public class UDPThread extends Thread{
	
	private LogMgr m_Log;
	private ILog u_Log;
	private JQueue queue;
	private int recvPort;
	private int sendPort;
	private boolean isShutDown;
	private DatagramSocket dataSock;
	private byte[] buffer = new byte[1024];
    public final static String HEARTBEAT_REQ = "STATUSCHK";
    public final static String HEARTBEAT_RES = "STATUSCHKED";
    
	public UDPThread(IQueue queue){
		this.queue = (JQueue) queue;
		m_Log = LogMgr.getInstance();
		u_Log = new GLogWriter();
		isShutDown = true;
	}
	
	public void startService(){
		
		m_Log.standLog("", "startService", "Start UDP Service!!");
		
		PropertyRead pr = PropertyRead.getInstance();
		
		this.recvPort = Integer.parseInt(pr.getValue(PROPERTIES.RECV_PORT));
		this.sendPort = Integer.parseInt(pr.getValue(PROPERTIES.SEND_PORT));
		
		try {
			
			dataSock = new DatagramSocket(recvPort);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(ExceptionUtil.getPrintWriter());
			m_Log.exceptionLog("", "startService", ExceptionUtil.getStringWriter().toString());
		}
		
		this.start();
		
	}
	
	public void run(){
		
		while (isShutDown) {

			try {

				DatagramPacket receivepacket = new DatagramPacket(buffer, buffer.length);
				dataSock.receive(receivepacket);
				
				InetAddress sockAddr = receivepacket.getAddress();
				String msg = new String(receivepacket.getData(), 0, receivepacket.getLength());

				String ip = "";
				int port = 0;
				if (sockAddr != null) {
					ip = receivepacket.getAddress().getHostAddress();
					
					if(msg.startsWith(HEARTBEAT_REQ)){
						u_Log.udpLog("run", "<-" + ip + ":" + recvPort+ " msg:" + msg);
						sendTo(ip, sendPort, HEARTBEAT_RES);
					} else {
						m_Log.standLog("", "run", msg);
						OnReceive(msg, ip, dataSock.getPort());
					}
					
				}
				
			} catch (Exception e) {
				e.printStackTrace(ExceptionUtil.getPrintWriter());
				m_Log.exceptionLog("", "run", ExceptionUtil.getStringWriter().toString());
			}

		}

	}

	private void OnReceive(String msg, String ip, int port) {
		// TODO Auto-generated method stub
		try {
			queue.put(msg);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void sendTo(String ip, int port, String msg) {
		try {
			u_Log.udpLog("sendTo", "->" + ip + ":" + port + " msg:" + msg);
			InetAddress addr = InetAddress.getByName(ip);
			DatagramPacket sendpacket = new DatagramPacket(msg.getBytes(), msg.getBytes().length, addr, port);
			dataSock.send(sendpacket);
		} catch (Exception e) {
			e.printStackTrace(ExceptionUtil.getPrintWriter());
			m_Log.exceptionLog("", "sendTo", ExceptionUtil.getStringWriter().toString());
		}
	}
	
}
