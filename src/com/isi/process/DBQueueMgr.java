package com.isi.process;

import com.isi.file.PropertyRead;
import com.isi.vo.EmployeeVO;
import com.isi.vo.PopupSvcVO;

public class DBQueueMgr {
	
	private static DBQueueMgr instance = new DBQueueMgr(); 
	
	private DBQueueMgr() {}
    public static DBQueueMgr getInstance () {
		if(instance == null){
			instance = new DBQueueMgr();
		}
		
		return instance;
    }
	
    
    public void addQData(String calling_num , String called_num , String popup_yn , EmployeeVO vo , String description)  {
    	try {
    		
    		//System.out.println("DBQueueMgr addQData  : " + vo.toString());
    		vo.setCalling_num(calling_num);
    		vo.setCalled_num(called_num);
    		vo.setPopup_yn(popup_yn);
    		vo.setDescription(description);
        	DBQueue.getInstance().put(vo);
    	} catch (Exception e) {
    		System.out.println(e.toString());
    		e.getStackTrace();
    	}
    	
    }
    
}
