package com.isi.thread;

import com.isi.data.ImageMgr;
import com.isi.handler.HttpUrlHandler;
import com.isi.vo.EmployeeVO;

public class LoginProcess extends Thread{
	
	private EmployeeVO empVO;
	private String parameter;
	
	public LoginProcess(EmployeeVO empVO, String parameter) {
		this.empVO = empVO;
		this.parameter = parameter;
	}
	
	public void run() {
		try {
			
			// 로그인 시도할때 이미지 삭제 -> 생성 
			ImageMgr imageMgr = ImageMgr.getInstance();
			imageMgr.createImageFiles(empVO);
			
			HttpUrlHandler urlHandler = new HttpUrlHandler();
			urlHandler.sendLoginUrl(parameter);
			
		} catch (Exception e) {
			
		}

		
	}
	
}
