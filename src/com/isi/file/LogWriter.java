package com.isi.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import org.apache.commons.io.output.FileWriterWithEncoding;

import com.isi.constans.LOGLEVEL;
import com.isi.constans.LOGTYPE;
import com.isi.constans.PROPERTIES;
import com.isi.data.XmlInfoMgr;
import com.isi.utils.Utils;


/**
*
* @author greatyun
*/
public class LogWriter implements ILog {
	
	private Utils util;
    private PrintWriter pw = null;
    private String today = null;
    private String hour = null;
    private String time = null;
    private String logFile = null;
    private String logDirectory = "";
    private PropertyRead pr=null;
    
 
    public LogWriter( ) {
//    	pr = new PropertyRead();
    	pr = PropertyRead.getInstance();
    	logDirectory = XmlInfoMgr.getInstance().getLogPath();
    }
    
    
    /*
    public static LogWriter getInstance () {
		if(lw == null){
			lw = new LogWriter();
		}
		return lw;
    }
    */
    
    
    
	public synchronized void write(String level, String type, String callkey, String methodName,String msg) {
		
		String isConsoleDebug = XmlInfoMgr.getInstance().getConsoleDebugYN();
			
		try {
			util = new Utils();
			today = util.getCurrentDay().substring(0, 8);
	    	File dir = new File(logDirectory+today);
	        if(!dir.exists()){
	            dir.mkdirs();
	        }
	        
			hour = util.getCurrentDay().substring(8, 10);
			
			String fileName = today + hour ;
			
			if(!checkFile(logDirectory+today , fileName)){
				logFile = logDirectory + today +  File.separatorChar + today + hour + "-" + LogSequence.setSequence(0) + ".log";
			} else {
				logFile = logDirectory + today +  File.separatorChar + today + hour + "-" + LogSequence.getSequence() + ".log";
				File tempFile = new File(logFile);
				if(tempFile.exists()){
					long size = tempFile.length();
//					if(size > 30000000) {
					if(size > 100000) {
						logFile = logDirectory + today +  File.separatorChar + today + hour + "-" +LogSequence.setSeqIncreament() + ".log";
					}
				}
			}
			
			
			// PropertyRead pr = PropertyRead.getInstance();
			if (pr.isRead()) {
				if (Integer.parseInt(level) > XmlInfoMgr.getInstance().getLogLevel()) {
					return;
				}
			}
			long currentTime = Calendar.getInstance().getTimeInMillis();
			Date date = new Date(currentTime);
			SimpleDateFormat fommat = new SimpleDateFormat("HH:mm:ss.SSS");
			time = fommat.format(date);
			
			BufferedWriter br = new BufferedWriter(new FileWriterWithEncoding(logFile,"euc-kr" , true));
			
			/*
			 * // 로그깨짐방지(ASCII코드 20 이하의 값은 스페이스 처리) if (msg != null &&
			 * msg.length() > 0) { for (int i = 0; i < msg.getBytes().length;
			 * i++) { try { char c = msg.charAt(i); if ((int) c > 20) {
			 * sb.append(c); } else { sb.append(" "); } }catch(Exception e){
			 * //e.printStackTrace(); continue; } } }
			 */
			String strMsg= "";
			if(type.equals(LOGTYPE.ERR_LOG)){
				strMsg = String.format("[%s][CallKey:%10s][Method:%10s][%s]\r\n",
						time, callkey, methodName, "######################## EXCEPTION ########################");
				strMsg += String.format("[%s][CallKey:%10s][Method:%10s][%s]\r\n",
						time, callkey, methodName,  msg);
			} else {
				strMsg = String.format("[%s][CallKey:%10s][Method:%10s][%s]\r\n",
						time, callkey, methodName, msg);
			}
			
			// server.propertie 파일의 콘솔디버그 모드 값 (Y:콘솔출력, N:로그파일출력, B:둘다 출력)
			if(isConsoleDebug.equalsIgnoreCase("Y")){
				System.out.print(strMsg);
			} else if(isConsoleDebug.equalsIgnoreCase("N")){
				br.write(strMsg);
			} else {
				System.out.print(strMsg);
				br.write(strMsg);
			}
			
			br.flush();
			br.close();
			util = null;

		} catch (Exception e) {
			// e.printStackTrace();
		} finally {
		}
	}

    private boolean checkFile(String path, String fileName) {
		// TODO Auto-generated method stub
    	File dir = new File(path);
		File[] fileList = dir.listFiles();
		for (File file : fileList) {
			if(file.exists()){
				if(file.getName().indexOf(fileName) > -1){
					return true;
				}
			}
		}
		return false;
	}

	public static String uniEncode(String s) {
        StringBuffer uni_s = new StringBuffer();
        String temp_s = null;
        for (int i = 0; i < s.length(); i++) {
            temp_s = Integer.toHexString(s.charAt(i));
            for (int j = temp_s.length(); j < 4; j++) {
                temp_s = "0" + temp_s;
            }
            uni_s.append("\\u");
            uni_s.append(temp_s);
        }
        return uni_s.toString();
    }

    public static String uniDecode(String uni) {
        StringBuffer str = new StringBuffer();
        for (int i = uni.indexOf("\\u"); i > -1; i = uni.indexOf("\\u")) {
            str.append(uni.substring(0, i));
            str.append(String.valueOf((char) Integer.parseInt(uni.substring(i + 2, i + 6), 16)));
            uni = uni.substring(i + 6);
        }
        str.append(uni);
        return str.toString();
    }

	@Override
	public void config(String type, String methodName, String msg) {
		// TODO Auto-generated method stub
//		write(LOGLEVEL.LEVEL_3, type, callkey, methodName, msg);
	}

	@Override
	public void server(String type, String methodName, String msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void Jtapi(String type, String callid,String methodName, String msg) {
		// TODO Auto-generated method stub
	}

	@Override
	public void callLog(String type, String callId, String methodName,
			String msg) {
		// TODO Auto-generated method stub
		write(LOGLEVEL.LEVEL_3, type, callId, methodName, msg);
	}

	@Override
	public void standLog(String callId, String methodName, String msg) {
		// TODO Auto-generated method stub
		write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, callId, methodName, msg);
	}

	@Override
	public void exceptionLog(String callId, String methodName, String msg) {
		// TODO Auto-generated method stub
		write(LOGLEVEL.LEVEL_3, LOGTYPE.ERR_LOG, callId, methodName, msg);
	}

	@Override
	public void testLog(String msg) {
		// TODO Auto-generated method stub
		write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, "TEST", "TEST", msg);
	}

	@Override
	public void udpLog(String methodName, String msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void duplexLog(boolean isActive,String methodName, String msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void httpLog(String requestID, String methodName, String msg) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void imageLog(String callId, String methodName, String msg) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void logInOutLog(String requestID , String methodName, String msg) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void popupLog(String requestID, String methodName, String msg) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void databaseLog(String requestID, String methodName, String msg) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void server(String callID, String type, String methodName, String msg) {
		// TODO Auto-generated method stub
		
	}
	
	


  

}
