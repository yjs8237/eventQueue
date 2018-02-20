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
	
	final private Map <String, ImageVO> imageMap;
	private static ImageMgr imageMgr = new ImageMgr();
	
	
	private ImageMgr(){
		imageMap = Collections.synchronizedMap(new HashMap<String, ImageVO>());
	}
	
	public synchronized static ImageMgr getInstance(){
		if(imageMgr == null){
			imageMgr = new ImageMgr();
		}
		return imageMgr;
	}
	
	
	public void addImage(String modelID, ImageVO imageVo){
		synchronized (imageMap) {
			imageMap.put(modelID, imageVo);
		}
	}
	
	public Map getImageInfo() {
		return this.imageMap;
	}
	
	public void checkAllImageSize() {
		Set keySet = imageMap.keySet();
		Iterator iter = keySet.iterator();
		while(iter.hasNext()) {
			String key = (String)iter.next();
			System.out.println(key + " : " + imageMap.get(key));
		}
	}
	
	public ImageVO getImageInfo(String modelID){
		return imageMap.get(modelID);
	}
	
//	public Map getImageMap(){
//		return imageMap;
//	}
	
	
}
