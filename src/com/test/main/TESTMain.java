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
import com.test.sync.Sync;

public class TESTMain {

	public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
		// TODO Auto-generated method stub
		
		
		
		
		Sync sync = new Sync();
		sync.startPickUpGroupSync();
		try {
			
			
			
			
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
