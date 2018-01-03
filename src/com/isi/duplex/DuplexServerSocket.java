package com.isi.duplex;

import java.io.IOException;
import java.net.*;

import com.isi.constans.PROPERTIES;
import com.isi.exception.ExceptionUtil;
import com.isi.file.PropertyRead;
/**
*
* @author greatyun
*/
public class DuplexServerSocket extends ServerSocketEx{
	
	private DuplexMgr		duplexMgr;
	private PropertyRead	pr;
	private String			ip;
	private int				port;
	
	public DuplexServerSocket(int port) {
		super(port);
		// TODO Auto-generated constructor stub
		duplexMgr = DuplexMgr.getInstance();
		pr			= PropertyRead.getInstance();
	}
	
	public void run(){
		
		while(!isStopReq()){
			
			try {

				logwrite.duplexLog(duplexMgr.getActiveMode(),"DuplexServerSocket run()", "waiting for client connection....");
				Socket sock = serverSocket.accept();
				
				ip = sock.getInetAddress().getHostAddress();
				port = sock.getLocalPort();
				
				if(ip.equals(pr.getValue(PROPERTIES.REMOTE_IP))){
					logwrite.duplexLog(duplexMgr.getActiveMode(),"DuplexServerSocket run()", "REMOTE SERVER CONNECTED !! IP["+ip+"]PORT["+port+"]");
					ServerSockThread senderSocket = new ServerSockThread(sock);
					senderSocket.startService();
				} else {
					logwrite.duplexLog(duplexMgr.getActiveMode(),"DuplexServerSocket run()", "NOT REMOTE SERVER !! IP["+ip+"]PORT["+port+"]");
					sock.close();
				}
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace(ExceptionUtil.getPrintWriter());
				logwrite.duplexLog(duplexMgr.getActiveMode(),"DuplexServerSocket run()", ExceptionUtil.getStringWriter().toString());
			}
			
			
		}
	}

}
