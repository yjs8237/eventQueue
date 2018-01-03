package com.isi.duplex;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.isi.constans.PROPERTIES;
import com.isi.file.PropertyRead;
/**
*
* @author greatyun
*/
public class DuplexMgr {
	
	private static DuplexMgr instance = new DuplexMgr();
	private String currentTime;
	private boolean isActiveMode	=	false;
	private boolean isDuplexMode	=	false;
	
	private DuplexMgr() {
		SimpleDateFormat format = new SimpleDateFormat("HHmmssSSS");
		currentTime = format.format(new Date());
	}
	public synchronized static DuplexMgr getInstance() {
		if(instance == null){
			instance = new DuplexMgr();
		}
		return instance;
	}
	
	public void setCurrentTime(String time){
		currentTime = time;
	}
	public String getCurrentTime(){
		return currentTime;
	}
	public void setActiveMode(){
		this.isActiveMode = true;
	}
	public void setStandByMode(){
		this.isActiveMode = false;
	}
	public boolean getActiveMode(){
		return isActiveMode;
	}
	public void setDuplexMode(boolean isDuplexMode){
		this.isDuplexMode = isDuplexMode;
	}
	public boolean getDuplexMode(){
		return isDuplexMode;
	}
	public void statusChange(){
		if(isActiveMode){
			isActiveMode = false;
		} else {
			isActiveMode = true;
		}
	}
	
	
}
