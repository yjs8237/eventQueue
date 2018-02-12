package com.test.main;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import com.bestez.common.tr.HostWebtIO;
import com.bestez.common.vo.CustInfoVO;
import com.isi.handler.PushHandler;
import com.isi.handler.XMLHandler;
import com.isi.vo.XmlVO;
import com.test.axl.model.CmAxlInfoModel;
import com.test.axl.soap.AxlHandler;

public class TESTMain {

	public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
		// TODO Auto-generated method stub
		
		CmAxlInfoModel cmVO = new CmAxlInfoModel();
		cmVO.setCmID("xmluser");
		cmVO.setCmPwd("!Insung2018#");
		cmVO.setCmIP("192.168.230.120");
		cmVO.setCmPort(8443);
		
		AxlHandler axlHandler = new AxlHandler(cmVO);
		String query = "SELECT * FROM numplan " ;
		axlHandler.testSoap(query);
		
		
	}

}
