package com.test.main;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
		
		
		String temp = "00000001";
		String [] arr = temp.split(",");
		
		for (int i = 0; i < arr.length; i++) {
			System.out.println(arr[i]);
		}
		
	}

}
