package com.isi.data;

import java.io.File;
import java.util.*;

import com.isi.constans.RESULT;
import com.isi.vo.DeviceVO;
import com.isi.vo.EmployeeVO;
import com.isi.vo.ImageVO;


/**
*
* @author greatyun
*/
public class ImageMgr {
	
	final private Map <String, ImageVO> imageMap;
	private Map <String, EmployeeVO> imageEmpMap = new HashMap<String, EmployeeVO>();
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
	
	public void addImgEmpInfo (String callingNum , EmployeeVO employee) {
		this.imageEmpMap.put(callingNum, employee);
	}
	
	public synchronized EmployeeVO getImgEmpInfo (String callingNum) {
		return imageEmpMap.get(callingNum);
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
	
	public boolean createImageFolder() {
		Set keySet = imageMap.keySet();
		Iterator iter = keySet.iterator();
		while(iter.hasNext()) {
			String modelID =  (String)iter.next();
			ImageVO imageVO = imageMap.get(modelID);
			
			String imageSize = imageVO.getImageSize();
			String path = XmlInfoMgr.getInstance().getEmpImgPath();
			
			String targetPath = path + "\\" + imageSize;
			
			File file = new File(targetPath);
			if(!file.exists()) {
				file.mkdirs();
			}
		}
		
		return true;
	}
	
//	public Map getImageMap(){
//		return imageMap;
//	}
	
	
}
