package com.isi.data;

import java.io.File;
import java.util.*;

import com.isi.constans.RESULT;
import com.isi.handler.ImageHandler;
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
	
	public void createImageFiles(EmployeeVO empVO) {
		
		Set keySet = imageMap.keySet();
		Iterator iter = keySet.iterator();
		ImageHandler imgHandler = new ImageHandler();
		while(iter.hasNext()) {
			
			String key = (String) iter.next();
			ImageVO imageVO = imageMap.get(key);
			String extension = empVO.getExtension();
			String cell_num = empVO.getCell_no();
			
			String strDest = XmlInfoMgr.getInstance().getEmpImgPath() + imageVO.getImageSize() + "\\"+ extension + ".png";
			
//			System.out.println("strDest1 : " + strDest);
			File file = new File(strDest);
			if(file.exists()) {
				file.delete();
			}
			
			imgHandler.createImageFile(empVO , extension , imageVO , "");
			
			strDest = XmlInfoMgr.getInstance().getEmpImgPath() + imageVO.getImageSize() + "\\"+ cell_num + ".png";
//			System.out.println("strDest2 : " + strDest);
			file = new File(strDest);
			if(file.exists()) {
				file.delete();
			}
			imgHandler.createImageFile(empVO , cell_num , imageVO , "");
			
			addImgEmpInfo(cell_num, empVO);
			addImgEmpInfo(extension, empVO);
		}
	
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
