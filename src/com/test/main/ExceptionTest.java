package com.test.main;

import com.isi.exception.ExceptionUtil;

public class ExceptionTest {
	
	public void test(){
		String temp = "ddd";
		try{
			Integer.parseInt(temp);
		} catch(Exception e){
			e.printStackTrace(ExceptionUtil.getPrintWriter());
			System.out.println(ExceptionUtil.getStringWriter().toString());
		}
		
		
	}
}
