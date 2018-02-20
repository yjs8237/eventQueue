package com.isi.file;
/**
*
* @author greatyun
*/
public interface ILog {
	public void write(String level, String type, String callkey, String methodName,String msg);
	public void config(String type, String methodName, String msg);
	public void server(String type, String methodName, String msg);
	public void Jtapi(String type, String callid,String methodName, String msg);
	public void callLog(String type, String callId, String methodName, String msg);
	public void standLog(String callId, String methodName, String msg);
	public void imageLog(String callId, String methodName, String msg);
	public void exceptionLog(String callId, String methodName, String msg);
	public void testLog(String msg);
	public void udpLog(String methodName, String msg);
	public void httpLog(String requestID, String methodName, String msg);
	
	public void logInOutLog(String requestID , String methodName, String msg);
	
//	public void httpLog( String methodName, String msg);
	public void duplexLog(boolean isActive, String methodName, String msg);
	
}
