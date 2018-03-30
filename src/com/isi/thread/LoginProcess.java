package com.isi.thread;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.isi.constans.PROPERTIES;
import com.isi.data.ImageMgr;
import com.isi.data.XmlInfoMgr;
import com.isi.file.ILog;
import com.isi.file.PropertyRead;
import com.isi.handler.HttpUrlHandler;
import com.isi.handler.ImageHandler;
import com.isi.vo.EmployeeVO;
import com.isi.vo.ImageVO;

public class LoginProcess extends Thread {
	
	private EmployeeVO empVO;
	private String parameter;
	private String requestID;
	private ILog logwrite;
	
	public LoginProcess(ILog logwrite , EmployeeVO empVO, String parameter , String requestID) {
		this.empVO = empVO;
		this.parameter = parameter;
		this.requestID = requestID;
		this.logwrite = logwrite;
	}
	
	public void run() {
		try {
			
//			createImageFiles(logwrite , empVO , requestID);
			//로그인 시도할때 이미지 삭제 -> 생성 
			ImageMgr.getInstance().createImageFiles(empVO, requestID);
			
			// 이중화 환경의 경우 remote 서버에게 로그인 시도 정보 전송 
			if(XmlInfoMgr.getInstance().getDuplexYN().equalsIgnoreCase("Y")) {
				HttpUrlHandler urlHandler = new HttpUrlHandler(logwrite , parameter , requestID);
				urlHandler.sendLoginUrl();
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
