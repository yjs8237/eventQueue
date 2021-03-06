package com.isi.thread;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.isi.constans.RESULT;
import com.isi.data.Employees;
import com.isi.data.ImageMgr;

public class ImageService extends Thread {

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		Employees employees = Employees.getInstance();
		/*
		// 최초 직원정보 메모리 업로드
		if (employees.getEmployeeList() != RESULT.RTN_SUCCESS) {
			System.out.println("!! ERROR !! [getEmployeeList]");
//			System.exit(0);
		}
		*/
		// 최초 직원 이미지 파일 삭제
		if (employees.deleteAllImages() != RESULT.RTN_SUCCESS) {
			System.out.println("!! ERROR !! [deleteAllImages]");
//			System.exit(0);
		}
//		
		if(!ImageMgr.getInstance().createImageFolder()) {
			System.out.println("!! ERROR !! [createImageSizeFolder]");
		}
		
		// 최초 직원 이미지 파일 생성
		if (employees.createAllImages() != RESULT.RTN_SUCCESS) {
			System.out.println("!! ERROR !! [creatEmployeeImg]");
		}
		
	}
	

}
