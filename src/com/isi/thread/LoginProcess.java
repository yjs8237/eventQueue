package com.isi.thread;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

import com.isi.constans.PROPERTIES;
import com.isi.constans.RESULT;
import com.isi.data.ImageMgr;
import com.isi.data.XmlInfoMgr;
import com.isi.file.GLogWriter;
import com.isi.file.ILog;
import com.isi.file.PropertyRead;
import com.isi.handler.ClientSocketHandler;
import com.isi.handler.HttpUrlHandler;
import com.isi.handler.ImageHandler;
import com.isi.vo.EmployeeVO;
import com.isi.vo.ImageVO;

public class LoginProcess extends Thread {
	
	private String requestID;
	private JSONObject jsonObj;
	private String type;
	private String parameter;
	private ILog 			logwrite;
	
	public LoginProcess( JSONObject jsonObj ,  String type, String requestID) {
		this.jsonObj = jsonObj;
		this.requestID = requestID;
		this.type = type;
		this.logwrite = new GLogWriter();
	}
	
	public LoginProcess( String parameter, String requestID) {
		this.requestID = requestID;
		this.parameter = parameter;
		this.logwrite = new GLogWriter();
	}
	
	public void run() {
		try {
			
//			createImageFiles(logwrite , empVO , requestID);
			//로그인 시도할때 이미지 삭제 -> 생성 
//			ImageMgr.getInstance().createImageFiles(empVO, requestID);
			
			// 이중화 환경의 경우 remote 서버에게 로그인 시도 정보 전송 
			if(XmlInfoMgr.getInstance().getDuplexYN().equalsIgnoreCase("Y")) {
				
				// * Socket 수정 원복
				// $$ IP 변경
				ClientSocketHandler clientSock = new ClientSocketHandler(XmlInfoMgr.getInstance().getRemoteIP(), XmlInfoMgr.getInstance().getHttp_sync_port());
				if(clientSock.connect() == RESULT.RTN_SUCCESS) {
					jsonObj.put("type", type);
					if(clientSock.send(jsonObj.toString()) == RESULT.RTN_SUCCESS) {
						logwrite.httpLog(requestID, "run", "remote Logint Request Success !! " + jsonObj.toString());
					} else {
						logwrite.httpLog(requestID, "run", "remote Logint Request Fail !! " + jsonObj.toString());
					}
					clientSock.disconnect();
				} else {
					
				}
				/*
				HttpUrlHandler urlHandler = new HttpUrlHandler(parameter , requestID);
				urlHandler.sendLoginUrl();
				*/
			}
			
			
		} catch (Exception e) {
			
		}

		
	}
	
	/*
	public void createImageFiles (ILog logwrite ,EmployeeVO empVO , String requestID) {
		ImageMgr imageMgr = ImageMgr.getInstance();
		Map imageMap = imageMgr.getImageInfo();
		
		Set keySet = imageMap.keySet();
		Iterator iter = keySet.iterator();
		ImageHandler imgHandler = new ImageHandler();
		while(iter.hasNext()) {
			
			String key = (String) iter.next();
			ImageVO imageVO = (ImageVO) imageMap.get(key);
			String extension = empVO.getExtension();
			String cell_num = empVO.getCell_no();
			
			String strDest = "";
			if (PropertyRead.getInstance().getValue(PROPERTIES.SIDE_INFO).equals("A")) {
				strDest = XmlInfoMgr.getInstance().getEmp_img_path_A() + imageVO.getImageSize() + "\\"+ extension + ".png";
			} else {
				strDest = XmlInfoMgr.getInstance().getEmp_img_path_B() + imageVO.getImageSize() + "\\"+ extension + ".png";
			}
			
			
			File file = new File(strDest);
			if(file.exists()) {
				file.delete();
			}
			
			imgHandler.createImageFile(empVO , extension , imageVO , "");
			
			logwrite.httpLog(requestID, "createImageFiles", "Create Image Success!! ["+ strDest + "]");
			
			strDest = "";
			if (PropertyRead.getInstance().getValue(PROPERTIES.SIDE_INFO).equals("A")) {
				strDest = XmlInfoMgr.getInstance().getEmp_img_path_A() + imageVO.getImageSize() + "\\"+ cell_num + ".png";
			} else {
				strDest = XmlInfoMgr.getInstance().getEmp_img_path_B() + imageVO.getImageSize() + "\\"+ cell_num + ".png";
			}
			
			
			file = new File(strDest);
			if(file.exists()) {
				file.delete();
			}
			imgHandler.createImageFile(empVO , cell_num , imageVO , "");
			
			logwrite.httpLog(requestID, "createImageFiles", "Create Image Success!! ["+ strDest + "]");
			
			imageMgr.addImgEmpInfo(cell_num, empVO);
			imageMgr.addImgEmpInfo(extension, empVO);
			
			
		}
	
	}
	*/
	
}
