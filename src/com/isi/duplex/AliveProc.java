package com.isi.duplex;

import java.net.Socket;

import com.isi.constans.PROPERTIES;
import com.isi.constans.RESULT;
import com.isi.data.XmlInfoMgr;
import com.isi.exception.ExceptionUtil;
import com.isi.file.GLogWriter;
import com.isi.file.ILog;
import com.isi.file.PropertyRead;
/**
*
* @author greatyun
*/
public class AliveProc {
	
	
	private PropertyRead 	pr;
	private ILog			logwrite;
	private Socket			sock;
	private String 			ip;
	private int				port;
	private ClientSockThread cliendSock;
	

	
	public AliveProc(){
		pr = PropertyRead.getInstance();
		logwrite = new GLogWriter();
	}
	
	public int startAliveProc(){
		
		try{
			
			/*
			if(pr.getValue(PROPERTIES.SIDE_INFO).equals("A")) {
				ip = XmlInfoMgr.getInstance().getSideBIP();
			} else {
				ip = XmlInfoMgr.getInstance().getSideAIP();
			}
			*/
			ip = XmlInfoMgr.getInstance().getRemoteIP();
			
			port = Integer.parseInt(XmlInfoMgr.getInstance().getRemotePort());
			
//			System.out.println("서버 접속 시도");
			logwrite.duplexLog(DuplexMgr.getInstance().getActiveMode(), "AliveThread startAliveProc()", "서버 접속 시도 IP : " + ip + " , PORT : " + port);
			
			sock = new Socket(ip , port);
			
			cliendSock = new ClientSockThread(sock);
			cliendSock.startService();
			
		}catch (Exception e){
			e.printStackTrace(ExceptionUtil.getPrintWriter());
			logwrite.duplexLog(DuplexMgr.getInstance().getActiveMode(),"AliveThread startAliveProc()", ExceptionUtil.getStringWriter().toString());
			return RESULT.TCP_CONN_FAIL;
		}
		
		return RESULT.RTN_SUCCESS;
	}
	
	
}
