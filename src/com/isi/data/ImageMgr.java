package com.isi.data;

import java.util.*;

import com.isi.constans.RESULT;
import com.isi.vo.DeviceVO;
import com.isi.vo.ImageVO;


/**
*
* @author greatyun
*/
public class ImageMgr {
	
	final private Map <String, String> imageMap;
	private static ImageMgr imageMgr = new ImageMgr();
	
	
	private ImageMgr(){
		imageMap = Collections.synchronizedMap(new HashMap<String, String>());
	}
	
	public synchronized static ImageMgr getInstance(){
		if(imageMgr == null){
			imageMgr = new ImageMgr();
		}
		return imageMgr;
	}
	
	
	public void addImage(String modelID, String size){
		synchronized (imageMap) {
			imageMap.put(modelID, size);
		}
	}
	
	public String getImageInfo(String modelID){
		return imageMap.get(modelID);
	}
	
//	public Map getImageMap(){
//		return imageMap;
//	}
	
	
}
