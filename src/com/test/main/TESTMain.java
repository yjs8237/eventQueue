package com.test.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONObject;

import com.bestez.common.tr.HostWebtIO;
import com.bestez.common.vo.CustInfoVO;
import com.isi.axl.soap.SoapHandler;
import com.isi.axl.soap.SxmlHandler;
import com.isi.handler.PushHandler;
import com.isi.handler.XMLHandler;
import com.isi.vo.XmlVO;
import com.test.axl.model.CmAxlInfoModel;
import com.test.axl.soap.AxlHandler;
import com.test.axl.soap.SoapXML;

public class TESTMain {

	public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
		// TODO Auto-generated method stub
		
		CmAxlInfoModel cmVO = new CmAxlInfoModel();
		cmVO.setCmID("xmluser");
		cmVO.setCmPwd("!Insung2018#");
		cmVO.setCmIP("192.168.230.120");
		cmVO.setCmPort(8443);
		
//		AxlHandler axlHandler = new AxlHandler(cmVO);
//		String query = "SELECT * FROM numplan " ;
//		axlHandler.testSoap(query);
		
//		SxmlHandler soap = new SxmlHandler();
//		Object obj = soap.selectDeviceStatusJSON("10.156.114.203", "xmluser", "Samil@3131", "SEP00215554192A");
//		JSONObject jsonObj = (JSONObject) obj;
//		String jsonP = jsonObj.toString(4);
//		System.out.println(jsonP);
		
		int temp = new Integer(0x4000003);
		System.out.println(temp);
		
		System.out.println(getLocalServerIp());
		
		System.out.println(System.getProperty("user.dir"));
		
		/*
		String command = "C:\\Development\\SRC\\ISXML\\RUN_ISXML.bat";
		Process proc = Runtime.getRuntime().exec(command);

		int waitFor = proc.waitFor();
		int result = proc.exitValue();
		
		System.out.println(waitFor + " , " + result);
		*/
		try {
			
			
			// 실행시킬 jar 파일 존재 여부 체크
			
//			ProcessBuilder pb = new ProcessBuilder("C:\\Program Files\\Java\\jdk1.7.0_80\\bin\\ISXML.exe", "-jar", "ISXML1.jar");
//			pb.directory(new File("C:\\Development\\SRC\\ISXML\\"));
//			Process p = pb.start();
//			
//			Thread.sleep(30000);
//			
//			if(isProcessRunning("ISXML.exe")) {
//				killProcess("ISXML.exe");
//			}
			
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		
		
	}
	
	
	
	public static boolean isProcessRunning(String serviceName) throws Exception {

		 Process p = Runtime.getRuntime().exec("tasklist");
		 BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
		 String line;
		 while ((line = reader.readLine()) != null) {
		  //System.out.println(line);
		  if (line.contains(serviceName)) {
		   return true;
		  }
		 }

		 return false;

	}
	
	public static void killProcess(String serviceName) throws Exception {

		  Runtime.getRuntime().exec("taskkill /F /IM " + serviceName);

	}
	
	
	
	public static String getLocalServerIp()
	{
		try
		{
		    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();)
		    {
		        NetworkInterface intf = en.nextElement();
		        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();)
		        {
		            InetAddress inetAddress = enumIpAddr.nextElement();
		            if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress() && inetAddress.isSiteLocalAddress())
		            {
		            	return inetAddress.getHostAddress().toString();
		            }
		        }
		    }
		}
		catch (SocketException ex) {}
		return null;
	}

}
