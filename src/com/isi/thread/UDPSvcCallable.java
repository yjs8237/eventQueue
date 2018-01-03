package com.isi.thread;

import java.util.concurrent.Callable;

import com.isi.constans.LOGLEVEL;
import com.isi.constans.LOGTYPE;
import com.isi.constans.RESULT;
import com.isi.constans.STATUS;
import com.isi.constans.UDPMSG;
import com.isi.db.JDatabase;
import com.isi.event.*;
import com.isi.exception.ExceptionUtil;
import com.isi.file.ILog;
import com.isi.file.LogMgr;
import com.isi.file.LogWriter;
import com.isi.handler.CallEvtHandler;
import com.isi.handler.UDPHandler;
import com.isi.process.IQueue;
import com.isi.vo.DeviceVO;
/**
*
* @author greatyun
*/
public class UDPSvcCallable implements Callable<Integer>{
	
	private IQueue 			queue;
	private boolean 		stopThread		= false;
	private LogMgr			logwrite;
	private UDPHandler		udpHandler;
	private String			threadID;
	
	public UDPSvcCallable(IQueue queue, JDatabase dataBase, String threadID) {
		this.queue = queue;
		this.threadID = threadID;
		logwrite = LogMgr.getInstance();
		udpHandler = new UDPHandler(dataBase, threadID);
	}
	
	@Override
	public Integer call()  {
		// TODO Auto-generated method stub
		
		while(!stopThread) {
			try{
				String received = (String) queue.get();
//               System.out.println(received);
				
				// 0x02 -> 
				char token = 0x02;
					
				String[] Tokens = received.split(""+token, -1);
				
				int iRtn = Parsing(received, Tokens);
				int recvtoken_count = Tokens.length;
				
				
				if (iRtn == RESULT.RTN_SUCCESS) {
					if (UDPMSG.SEND_TEXT_MESSAGE.equals(Tokens[0])) {
//                       cmdSendTextMessage(Tokens, recvtoken_count);
					} else if (UDPMSG.SEND_IMAGE_MESSAGE.equals(Tokens[0])) {
//                       cmdSendImageMessage(Tokens, recvtoken_count);
					} else if (UDPMSG.SEND_CLEAR_SERVICE.equals(Tokens[0])) {
//                       cmdClearService(Tokens, recvtoken_count);
					} else if (UDPMSG.SEND_RING_SIGNAL.equals(Tokens[0])) {
						udpHandler.callRingUDP(Tokens, recvtoken_count);
					} else if (UDPMSG.SEND_CALLSTART_SIGNAL.equals(Tokens[0])) {
						udpHandler.callEstablishUDP(Tokens, recvtoken_count);
					} else if (UDPMSG.SEND_CALLEND_SIGNAL.equals(Tokens[0])) {
						
						udpHandler.callAllDisconnectUDP(Tokens, recvtoken_count);
						
					} else if (UDPMSG.SEND_ALLCLEAR_SIGNAL.equals(Tokens[0])) {
						
//						udpHandler.callDisconnectUDP(Tokens, recvtoken_count);
						
					} else if(UDPMSG.SEND_NETWORK_PICKUP_ESTABLISEHD_MESSAGE.equals(Tokens[0])){
						udpHandler.callNetworkPickUpEstablishUDP(Tokens, recvtoken_count);
					}
				}
			} catch (Exception e){
				e.printStackTrace(ExceptionUtil.getPrintWriter());
				logwrite.exceptionLog(threadID, "call()", ExceptionUtil.getStringWriter().toString());
			}
			
		}
		return RESULT.RTN_SUCCESS;
	}

	
	
	   private int Parsing(String aRecv_msg, String[] aTokens) {
		   
	        try {

	            if (aTokens == null) {
	                return RESULT.RTN_PASER_ERR;
	            }
	            if (aTokens.length < 3) {
	                return RESULT.RTN_INVALIED_AGUMENT;
	            }

	            if (!UDPMSG.PACKET_VERSION.equals(aTokens[1])) {
	                return RESULT.ERR_COMM_MISMATCH_VERSION;
	            }

	            return RESULT.RTN_SUCCESS;
	        } catch (Exception e) {
	            return RESULT.RTN_EXCEPTION;
	        }
	    }
	
	
	
	

}
