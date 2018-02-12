package com.isi.handler;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.AttributedString;
import java.util.*;

import javax.imageio.ImageIO;

import com.cisco.cti.util.ArrayList;
import com.isi.constans.IMAGESIZE;
import com.isi.constans.LOGLEVEL;
import com.isi.constans.LOGTYPE;
import com.isi.constans.PROPERTIES;
import com.isi.constans.PersonType;
import com.isi.data.ImageMgr;
import com.isi.data.XmlInfoMgr;
import com.isi.exception.ExceptionUtil;
import com.isi.file.GLogWriter;
import com.isi.file.ILog;
import com.isi.file.LogMgr;
import com.isi.file.PropertyRead;
import com.isi.vo.CustomerVO;
import com.isi.vo.EmployeeVO;
import com.isi.vo.IPerson;
import com.isi.vo.ImageVO;

/**
*
* @author greatyun
*/
public class ImageHandler {
	
	private PropertyRead pr;
	private LogMgr m_Log;
	private ILog g_Log;
	private ImageMgr imageMgr;
	private String directtoryNm;
	
	public ImageHandler(){
		pr = PropertyRead.getInstance();
		m_Log = LogMgr.getInstance();
		g_Log = new GLogWriter();
	}
	
	
	
	
	public boolean deleteImageFile() {
		boolean bResult = true;
		// 팝업 이미지 내선.png
		try{
			String dirPath = XmlInfoMgr.getInstance().getEmpImgPath();
			File files = new File(dirPath);
			bResult = deleteDirectory(files);
		} catch(Exception e){
			e.printStackTrace(ExceptionUtil.getPrintWriter());
			g_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.ERR_LOG, "" , "deleteImageFile", ExceptionUtil.getStringWriter().toString());
			bResult = false;
		}
		
		return bResult;
	}
	
	
	public boolean deleteFaceImageFile() {
		boolean bResult = true;
		// 팝업 이미지 내선.png
		try{
//			String dirPath = pr.getValue(PROPERTIES.FACE_IMAGE);
			String dirPath = XmlInfoMgr.getInstance().getFaceImgPath();
			File files = new File(dirPath);
			bResult = deleteDirectory(files);
			
		} catch(Exception e){
			e.printStackTrace(ExceptionUtil.getPrintWriter());
			g_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.ERR_LOG, "" , "deleteFaceImageFile", ExceptionUtil.getStringWriter().toString());
			bResult = false;
		}
		
		return bResult;
	}
	
	private boolean deleteDirectory(File path) throws Exception{
		// TODO Auto-generated method stub
		boolean result = true;
		if (!path.exists()) {
			g_Log.imageLog("", "deleteImageFile", "삭제 이미지 경로 미존재!! path["+path+"]");
			path.mkdirs();
		}

		File[] files = path.listFiles();
		
		g_Log.imageLog("", "deleteImageFile", "삭제 이미지 경로["+path+"] 삭제할 이미지 개수[" + files.length + "]");
		
		for (File file : files) {
			if (file.isDirectory()) {
				deleteDirectory(file);
			} else {
				if(file.delete()){
					g_Log.imageLog("", "deleteImageFile", "DELETE SUCCESS : " + file.getAbsolutePath());
//					System.out.println("DELETE SUCCESS : " + file.getAbsolutePath());
					Thread.sleep(100);
				} else {
					result = false;
					g_Log.imageLog("", "deleteImageFile", "DELETE FAIL : " + file.getAbsolutePath());
//					System.out.println("DELETE FAIL : " + file.getAbsolutePath());
				}
			}
		}
		
		return result;
	}
	
	public boolean deleteEmpImage(String fileName) {
		// TODO Auto-generated method stub
		boolean bResult = true;
		// 팝업 이미지 내선.png
		try{
//			String dirPath = pr.getValue(PROPERTIES.EMPLOYEE_IMAGE);
			String dirPath = XmlInfoMgr.getInstance().getEmpImgPath();
			File path = new File(dirPath);
			File[] files = path.listFiles();
			
			for (File file : files) {
				String str_path = file.getAbsolutePath() + fileName + ".png";
				File tmpFile = new File(str_path);
				bResult = deleteDirectory(tmpFile);
			}
			
		} catch(Exception e){
			e.printStackTrace(ExceptionUtil.getPrintWriter());
			g_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.ERR_LOG, "" , "deleteEmpImage", ExceptionUtil.getStringWriter().toString());
			bResult = false;
		}
		return bResult;
	}
	
	
	public boolean createImageFile(EmployeeVO employee ,  String callingNum,  String model , String callID) {
		
		imageMgr = ImageMgr.getInstance();
		
		boolean bResult = true;
		
		String aniNum 	= "발신 : " + callingNum;
		String name		= "이름 : " + employee.getEmp_nm_kor();
		String position	= "직급 : " + employee.getPos_nm();
		String division = "부서 : " + employee.getOrg_nm();
		String floor	= "위치 : " + employee.getFloor();
		
		int type = -1;
		
		
		ImageVO imageVO = ImageMgr.getInstance().getImageInfo(model);
		
		String imageSize = imageVO.getImageSize();
		if(imageSize == null) {
			m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, callID , "CreateImageFile", imageSize + " 모델 이미지 사이즈 정보 없음 !! ");
			return false;
		}
		
		String strDest = XmlInfoMgr.getInstance().getEmpImgPath() + imageSize + "\\"+ callingNum + ".png";
		
        try {
        	
            File logdir = new File(strDest);
            
            if(type == PersonType.EMPLOYEE){
            	if (logdir.exists() ) {
                	// 이미 팝업 이미지가 있다면 이미지를 생성하지 않는다. (직원 콜 의 경우만 해당)
            		m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, callID , "CreateImageFile", callingNum + " 이미지 존재");
                    return true;
                }
            }
            
            String basic_img_path = XmlInfoMgr.getInstance().getBaseImgPath() + imageSize + "_basic.png";
            BufferedImage basic_img = ImageIO.read(new File(basic_img_path)); // 배경이미지
            
            
            int width = Integer.parseInt(imageVO.getImageSize().substring(0, 3));
            int height = Integer.parseInt(imageVO.getImageSize().substring(3));
            
            
			int fontsize = (int) (height / 10);
			
			BufferedImage mergedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics2D graphics = (Graphics2D) mergedImage.getGraphics();
			
			graphics.setBackground(Color.WHITE);
			graphics.drawImage(basic_img, 0, 0, null);
			
			
			Font font = new Font("맑은고딕", Font.BOLD, fontsize);
			graphics.setFont(font);
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			if(aniNum != null && !aniNum.isEmpty()){
				// 발신번호 표시
				AttributedString attName = new AttributedString( aniNum );
				attName.addAttribute(TextAttribute.FONT, font);
				attName.addAttribute(TextAttribute.FOREGROUND, Color.black);
				graphics.drawString(attName.getIterator(), imageVO.getAninum_x(), imageVO.getAninum_y());
				
			}
			
			if(name != null && !name.isEmpty()) {
				// 이름 표시
				AttributedString attName = new AttributedString( name );
				attName.addAttribute(TextAttribute.FONT, font);
				attName.addAttribute(TextAttribute.FOREGROUND, Color.black);
				graphics.drawString(attName.getIterator(), imageVO.getName_x(), imageVO.getName_y());
			}
			
			if(position != null && !position.isEmpty()) {
				// 직급
				AttributedString attName = new AttributedString(position);
				attName.addAttribute(TextAttribute.FONT, font);
				attName.addAttribute(TextAttribute.FOREGROUND, Color.black);
				graphics.drawString(attName.getIterator(), imageVO.getPosition_x(), imageVO.getPosition_y());
			}
			
			if(division != null && !division.isEmpty()) {
				// 부서
				if(division.length() > 9) {
					Font newfont = new Font("맑은고딕", Font.BOLD, fontsize - 2);
					graphics.setFont(newfont);
					graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					// 사업소
					AttributedString as = new AttributedString( division );
					as.addAttribute(TextAttribute.FONT, newfont);
					as.addAttribute(TextAttribute.FOREGROUND, Color.black);
					graphics.drawString(as.getIterator(), imageVO.getDivision_x(), imageVO.getDivision_y());
				} else {
					// 사업소
					AttributedString as = new AttributedString( division );
					as.addAttribute(TextAttribute.FONT, font);
					as.addAttribute(TextAttribute.FOREGROUND, Color.black);
					graphics.drawString(as.getIterator(), imageVO.getDivision_x(), imageVO.getDivision_y());
				}
			}
			
			if(floor != null && !floor.isEmpty()) {
				// 위치
				AttributedString attName = new AttributedString(floor);
				attName.addAttribute(TextAttribute.FONT, font);
				attName.addAttribute(TextAttribute.FOREGROUND, Color.black);
				graphics.drawString(attName.getIterator(), imageVO.getFloor_x(), imageVO.getFloor_y());
			}
			
			ImageIO.write(mergedImage, "png", new File(strDest));
			
			m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, callID , "CreateImageFile", "CREATE SUCCESS : " + strDest);
        } catch (Exception ioe) {
            bResult = false;
            ioe.printStackTrace(ExceptionUtil.getPrintWriter());
            m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.ERR_LOG, callID , "CreateImageFile", ExceptionUtil.getStringWriter().toString());
        }
        return bResult;
    }
	
	/*
	public boolean createAllImageFile(java.util.ArrayList<EmployeeVO> employeeList) {

		boolean bResult = true;
		
		EmployeeVO objEmployee = null;
		
        try {
        	
//        	File dirPath = new File(pr.getValue(PROPERTIES.EMPLOYEE_IMAGE));
        	File dirPath = new File(XmlInfoMgr.getInstance().getEmpImgPath());
        	File [] dirFiles = dirPath.listFiles();
        	
			Map imageMap = ImageMgr.getInstance().getImageInfo();
			
			Set keySet = imageMap.keySet();
			Iterator iter = keySet.iterator();
			while (iter.hasNext()) {
				String key = (String) iter.next();
				ImageVO imageVO = (ImageVO) imageMap.get(key);
				File directory = new File(XmlInfoMgr.getInstance().getEmpImgPath() + "\\" + imageVO.getImageSize());
				if (!directory.exists()) {
					directory.mkdirs();
				}
				
				for (int i = 0; i < employeeList.size(); i++) {
					objEmployee = employeeList.get(i);
					createImageFile(objEmployee, imageVO.getModel(), "createAllImage");
					
					Thread.sleep(100);	// 이미지 처리 속도 텀을 0.3 초로 설정
				}
				
			}
        	
        	for (File file : dirFiles) {
        		
				directtoryNm = file.getName();
			
				
				/*
			
				BufferedImage basic_img = ImageIO.read(new File(basic_img_path)); // 배경이미지
				
			
				ImageVO imageVO = IMAGESIZE.imageSizeMap.get(directtoryNm);
				
				int width = Integer.parseInt(directtoryNm.substring(0, 3));
				int height = Integer.parseInt(directtoryNm.substring(3));
				
				int picture_x = imageVO.getPicture_x1();
				int picture_y = imageVO.getPicture_y1();
				int pictureWidth = imageVO.getPicture_width();
				int pictureHeight = imageVO.getPicture_height();
				
//				int fontsize = (int) (height / 14.5);
				int fontsize = (int) (height / 8.5);
//				int font_x	= (int) (width / 2.5);
//				int font_y 	= (int) (height / 2.8);
				
				BufferedImage mergedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
				Graphics2D graphics = (Graphics2D) mergedImage.getGraphics();
				
				graphics.setBackground(Color.WHITE);
				graphics.drawImage(basic_img, 0, 0, null);
				
		
				
				Font font = new Font("맑은고딕", Font.BOLD, fontsize);
				graphics.setFont(font);
				graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				
				// 직책 + 이름
				String position = objEmployee.getEm_position();
				String name = objEmployee.getEm_name();
				String displayContent = "";
				
				if(position!=null && !position.isEmpty() && name!=null && !name.isEmpty()){
					displayContent = position + " " + name;
				} else if(position==null || position.isEmpty()) {
					displayContent = name;
				} else if(name==null || name.isEmpty()){
					displayContent = position;
				}
				
				if(displayContent==null){
					displayContent = "";
				}
			
				if(!displayContent.isEmpty()) {
					AttributedString attName = new AttributedString(displayContent);
					attName.addAttribute(TextAttribute.FONT, font);
					attName.addAttribute(TextAttribute.FOREGROUND, Color.black);
					graphics.drawString(attName.getIterator(), imageVO.getName_x1(), imageVO.getName_y1());
				}
				
				
				// 사업소
				if(objEmployee.getOrgNm()!=null && !objEmployee.getOrgNm().isEmpty()){
					
					if(objEmployee.getOrgNm().length() > 9) {
						Font newfont = new Font("맑은고딕", Font.BOLD, fontsize - 2);
						graphics.setFont(newfont);
						graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
						AttributedString as = new AttributedString( objEmployee.getOrgNm() );
						as.addAttribute(TextAttribute.FONT, newfont);
						as.addAttribute(TextAttribute.FOREGROUND, Color.black);
						graphics.drawString(as.getIterator(), imageVO.getOrg_x1(), imageVO.getOrg_y1());
					} else {
						AttributedString as = new AttributedString( objEmployee.getOrgNm() );
						as.addAttribute(TextAttribute.FONT, font);
						as.addAttribute(TextAttribute.FOREGROUND, Color.black);
						graphics.drawString(as.getIterator(), imageVO.getOrg_x1(), imageVO.getOrg_y1());
					}
					
				}
				
				// 부서
				if(objEmployee.getGroupNm()!=null && !objEmployee.getGroupNm().isEmpty()){
					AttributedString attPhone1 = new AttributedString(objEmployee.getGroupNm());
					attPhone1.addAttribute(TextAttribute.FONT, font);
					attPhone1.addAttribute(TextAttribute.FOREGROUND, Color.black);
					graphics.drawString(attPhone1.getIterator(), imageVO.getDivision_x1(), imageVO.getDivision_y1());
				}
				
				// 내선번호
				if(objEmployee.getDN()!=null && !objEmployee.getDN().isEmpty()){
					AttributedString attExtension = new AttributedString(objEmployee.getDN());
					attExtension.addAttribute(TextAttribute.FONT, font);
					attExtension.addAttribute(TextAttribute.FOREGROUND, Color.black);
					graphics.drawString(attExtension.getIterator(), imageVO.getExtension_x1(), imageVO.getExtension_y1());
				}
				
				
				ImageIO.write(mergedImage, "png", new File(strDest));
				
				g_Log.imageLog( "" , "createAllImageFile", "CREATE SUCCESS : " + strDest);
				
				
				
				
				
				
			}
        	
        	
        	
        } catch (Exception ioe) {
            bResult = false;
            ioe.printStackTrace(ExceptionUtil.getPrintWriter());
            g_Log.imageLog( "" , "createAllImageFile", "directtoryNm -> " + directtoryNm + " , objEmployee -> " + objEmployee.toString());
            g_Log.imageLog( "" , "createAllImageFile", ExceptionUtil.getStringWriter().toString());
            return false;
        }
        return bResult;
    }
    */
	
	private String get_X(String size){
		String retStr = "";
		
		switch (size) {
		case "298144":
			retStr = "20";
			break;
		case "298156":
			retStr = "20";
			break;
		case "298168":
			retStr = "20";
			break;
		case "396162":
			retStr = "26";
			break;
		case "498289":
			retStr = "33";
			break;
		case "559313":
			retStr = "37";
			break;
		default:
			break;
		}
		
		return retStr;
	}
	
	private String get_Y(String size){
		String retStr = "";
		
		switch (size) {
		case "298144":
			retStr = "129";
			break;
		case "298156":
			retStr = "139";
			break;
		case "298168":
			retStr = "149";
			break;
		case "396162":
			retStr = "145";
			break;
		case "498289":
			retStr = "257";
			break;
		case "559313":
			retStr = "279";
			break;
		default:
			break;
		}
		
		return retStr;
	}
	
	/*
	private int getHeight(String size) {
		// TODO Auto-generated method stub
		int height = -1;
		switch (size) {
		
		case IMAGESIZE.SIZE_298x168:
			height = 168;
			break;
			
		case IMAGESIZE.SIZE_396x162:
			height = 162;
			break;
			
		case IMAGESIZE.SIZE_498x289:
			height = 289;
			break;
		case IMAGESIZE.SIZE_559x313:
			height = 313;
			break;
			
		default:
			break;
		}
		
		return height;
	}
	*/
/*
	private int getWidth(String directtoryNm) {
		// TODO Auto-generated method stub
		
		int width = -1;
		switch (directtoryNm) {
		
		case IMAGESIZE.SIZE_298x168:
			width = Integer.parseInt(directtoryNm.substring(0, 3));
			break;
			
		case IMAGESIZE.SIZE_396x162:
			width = 396;
			break;
			
		case IMAGESIZE.SIZE_498x289:
			width = 498;
			break;
		case IMAGESIZE.SIZE_559x313:
			width = 559;
			break;
			
		default:
			break;
		}
		
		return width;
	}
	*/
	private String getEmailID(String email){
		// 이메일 주소의 @ 기준으로 앞 부분만 리턴한다.
		int index = email.indexOf("@");
		String retStr;
		if(index > 0){
			retStr = email.substring(0, index);
		} else {
			retStr = "";
		}
		return retStr;
	}




	
	
	
}
