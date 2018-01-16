package com.test.handler;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.AttributedString;
import java.util.*;

import javax.imageio.ImageIO;

import com.isi.constans.IMAGESIZE;
import com.isi.constans.LOGLEVEL;
import com.isi.constans.LOGTYPE;
import com.isi.constans.PROPERTIES;
import com.isi.constans.PersonType;
import com.isi.data.*;
import com.isi.exception.ExceptionUtil;
import com.isi.file.*;
import com.isi.vo.CustomerVO;
import com.isi.vo.EmployeeVO;
import com.isi.vo.IPerson;
import com.isi.vo.ImageVO;

/**
*
* @author greatyun
*/
public class TestImageHandler {
	
	private PropertyRead pr;
	private ILog g_Log;
	
	
	public TestImageHandler(){
		pr = PropertyRead.getInstance();
		g_Log = new GLogWriter();
	}
	
	public boolean deleteImageFile() {
		boolean bResult = true;
		// 팝업 이미지 내선.png
		try{
			String dirPath = "";
			File files = new File(dirPath);
			bResult = deleteDirectory(files);
			
			/*
			File[] imageFiles = files.listFiles();
			
			for (int i = 0; i < imageFiles.length; i++) {
				
				if(imageFiles[i].delete()){
					g_Log.standLog("", "deleteImageFile", "DELETE SUCCESS : " + imageFiles[i].getAbsolutePath());
				} else {
					File[] filesSecond = imageFiles[i].listFiles();
					for (int j = 0; j < filesSecond.length; j++) {
						if(filesSecond[j].delete()){
							Thread.sleep(500);
							g_Log.standLog("", "deleteImageFile", "DELETE SUCCESS : " + filesSecond[j].getAbsolutePath());
						}else {
							g_Log.standLog("", "deleteImageFile", "DELETE FAIL : " + filesSecond[j].getAbsolutePath());
							return false;
						}
					}
					
					imageFiles[i].delete();
					
				}
			}
			
			*/
		} catch(Exception e){
			e.printStackTrace(ExceptionUtil.getPrintWriter());
			g_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.ERR_LOG, "" , "deleteImageFile", ExceptionUtil.getStringWriter().toString());
			bResult = false;
		}
		
		return bResult;
	}
	
	private boolean deleteDirectory(File path) throws Exception{
		// TODO Auto-generated method stub
		boolean result = true;
		if (!path.exists()) {
			path.mkdirs();
		}

		File[] files = path.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				deleteDirectory(file);
			} else {
				if(file.delete()){
					g_Log.standLog("", "deleteImageFile", "DELETE SUCCESS : " + file.getAbsolutePath());
//					System.out.println("DELETE SUCCESS : " + file.getAbsolutePath());
					Thread.sleep(100);
				} else {
					result = false;
					g_Log.standLog("", "deleteImageFile", "DELETE FAIL : " + file.getAbsolutePath());
//					System.out.println("DELETE FAIL : " + file.getAbsolutePath());
				}
			}
		}
		
		return result;
	}

	public boolean createImageFile(IPerson person , String model , String threadID) {

		boolean bResult = true;
		String ID = "";
		String aniNum = "";
		String contactNum = "";
		String division = "";
		String displayName = "";
		
		int type = -1;
		if(person instanceof EmployeeVO){
			type = PersonType.EMPLOYEE;
//			ID = ((EmployeeVO) person).getEm_ID();
			ID = getEmailID(ID);	// 이메일 주소의 ID 만 가져오기
			aniNum = ((EmployeeVO) person).getDN();
//			contactNum = ((EmployeeVO) person).getEm_cellNum();
			contactNum = aniNum;
			division = ((EmployeeVO) person).getGroupNm();
			displayName = ((EmployeeVO) person).getEm_position() + " " + ((EmployeeVO) person).getEm_name();
		} else if (person instanceof CustomerVO){
			type = PersonType.CUSTOMER;
			ID = ((CustomerVO) person).getPhoneNum();
			aniNum = ((CustomerVO) person).getPhoneNum();
			contactNum = aniNum;
			division = ((CustomerVO) person).getCustLevel();	// 고객 콜일 경우 고객등급을 세팅한다.
			displayName = ((CustomerVO) person).getName();
		}
		
		
		// 배경 이미지
		String basic_img_path = XmlInfoMgr.getInstance().getBaseImgPath() + "basic.jpg";
		// 직원 사진 사번.jpg
		String strCallerPicture = XmlInfoMgr.getInstance().getBaseImgPath() + ID + ".jpg";
        // 팝업 이미지 내선.png
//		String strDest = getPathByModel(model) + aniNum + ".png";
		
		String modelSize = ((ImageVO)ImageMgr.getInstance().getImageInfo(model)).getImageSize();
		if(modelSize == null){
			LogMgr.getInstance().write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, threadID , "CreateImageFile", model + " 모델 이미지 사이즈 정보 없음 !! ");
			return false;
		}
		
		String strDest = XmlInfoMgr.getInstance().getEmpImgPath() + modelSize + "\\"+ aniNum + ".png";
        // 고로 이미지
		String logoImagePath = XmlInfoMgr.getInstance().getBaseImgPath() + "logo.jpg";
		
        try {
        	
            File logdir = new File(strDest);
            
            if(type == PersonType.EMPLOYEE){
            	if (logdir.exists() ) {
                	// 이미 팝업 이미지가 있다면 이미지를 생성하지 않는다. (직원 콜 의 경우만 해당)
            		LogMgr.getInstance().write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, threadID , "CreateImageFile", ID + " / " + aniNum + " 이미지 존재");
                    return true;
                }
            }
            
            
            logdir = new File(strCallerPicture);
            if(!logdir.exists()){
            	// 직원 사진이 없다면 default 이미지로 대체한다.
            	strCallerPicture = XmlInfoMgr.getInstance().getBaseImgPath() + "default.jpg";
            }
            
            
            BufferedImage basic_img = ImageIO.read(new File(basic_img_path)); // 배경이미지
            
            // 얼굴 이미지
            BufferedImage faceImage = null;
            try {
                faceImage = ImageIO.read(new File(strCallerPicture)); // 띄우고자 하는 사진 
            } catch (IOException ex) {
            	ex.printStackTrace(ExceptionUtil.getPrintWriter());
            	LogMgr.getInstance().write(LOGLEVEL.LEVEL_3, LOGTYPE.ERR_LOG, threadID , "CreateImageFile", ExceptionUtil.getStringWriter().toString());
            }
            
            // 로고 이미지
            BufferedImage logoImage = null;
            try {
            	logoImage = ImageIO.read(new File(logoImagePath)); // 띄우고자 하는 사진 
            } catch (IOException ex) {
            	ex.printStackTrace(ExceptionUtil.getPrintWriter());
            	LogMgr.getInstance().write(LOGLEVEL.LEVEL_3, LOGTYPE.ERR_LOG, threadID , "CreateImageFile", ExceptionUtil.getStringWriter().toString());
            }
            
            
//            String imageSize = getImageSizeByModel(model);
            String imageSize = ((ImageVO)ImageMgr.getInstance().getImageInfo(model)).getImageSize();
            
            /*
            int width = basic_img.getWidth();
            int height = basic_img.getHeight();
            */
//            int width = getWidth(imageSize);
//            int height = getHeight(imageSize);
            
            int width = Integer.parseInt(imageSize.substring(0, 3));
            int height = Integer.parseInt(imageSize.substring(3));
            
            
            int picture_x = (int) (width / 16.5);
			int picture_y = (int) (height / 4.5);
			int pictureWidth = width / 4;
			int pictureHeight = height / 2;
			int fontsize = (int) (height / 14.5);
			int font_x	= (int) (width / 2.5);
			int font_y 	= (int) (height / 2.8);
			
			
			
			BufferedImage mergedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics2D graphics = (Graphics2D) mergedImage.getGraphics();
			
			graphics.setBackground(Color.WHITE);
			graphics.drawImage(basic_img, 0, 0, null);
			if (faceImage != null) {
				//graphics.drawImage(image2, 20, 60, null);
				// 127 x 150 Pixel Pictuer
				graphics.drawImage(faceImage, picture_x, picture_y, pictureWidth, pictureHeight, null);
				//System.out.println("[CreateImageFile] Picture is exist > " + strCallerPicture );
			} else {
				// Remote 원본 이미지를 가져오는 기능 추가 여부 판단 후 적용 예정
				// System.out.println("[CreateImageFile] Picture is NOT exist > " + objEmployee.getEMP_ID() );
			}
			
			if (logoImage != null) {
				//graphics.drawImage(image2, 20, 60, null);
				// 127 x 150 Pixel Pictuer
				graphics.drawImage(logoImage, (width / 10) * 8 , (height / 10) * 2, (width / 8), (height / 8), null);
				//System.out.println("[CreateImageFile] Picture is exist > " + strCallerPicture );
			} else {
				// Remote 원본 이미지를 가져오는 기능 추가 여부 판단 후 적용 예정
				// System.out.println("[CreateImageFile] Picture is NOT exist > " + objEmployee.getEMP_ID() );
			}
			
			Font font = new Font("맑은고딕", Font.BOLD, fontsize);
			graphics.setFont(font);
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			// 부서
			AttributedString as = new AttributedString( division );
			as.addAttribute(TextAttribute.FONT, font);
			as.addAttribute(TextAttribute.FOREGROUND, Color.red);
			graphics.drawString(as.getIterator(), font_x, font_y);
			
			// 직책 + 이름
			AttributedString attName = new AttributedString( displayName );
			attName.addAttribute(TextAttribute.FONT, font);
			attName.addAttribute(TextAttribute.FOREGROUND, Color.black);
			graphics.drawString(attName.getIterator(), font_x, font_y + 30);
			
			// 연락처
			AttributedString attPhone1 = new AttributedString(contactNum);
			attPhone1.addAttribute(TextAttribute.FONT, font);
			attPhone1.addAttribute(TextAttribute.FOREGROUND, Color.blue);
			graphics.drawString(attPhone1.getIterator(), font_x, font_y + 60);
			
			ImageIO.write(mergedImage, "png", new File(strDest));
			
			LogMgr.getInstance().write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, threadID , "CreateImageFile", "CREATE SUCCESS : " + strDest);
        } catch (IOException ioe) {
            bResult = false;
            ioe.printStackTrace(ExceptionUtil.getPrintWriter());
            LogMgr.getInstance().write(LOGLEVEL.LEVEL_3, LOGTYPE.ERR_LOG, threadID , "CreateImageFile", ExceptionUtil.getStringWriter().toString());
        }
        return bResult;
    }
	
	
/*
	private String getPathByModel(String model) {
		// TODO Auto-generated method stub
			// TODO Auto-generated method stub
			 String path = "";
			 
			 if(model.equals(IPPhone.PHONE_7970) || model.equals(IPPhone.PHONE_7971) 
					 || model.equals(IPPhone.PHONE_7975) || model.equals(IPPhone.PHONE_IPCOMMUNICATOR)){
				 
				 path = pr.getValue(PROPERTIES.IMAGE_298x168);
				 
			 } else if (model.equals(IPPhone.PHONE_8831)){

				 path = pr.getValue(PROPERTIES.IMAGE_396x162);
				 
			 } else if (model.equals(IPPhone.PHONE_8811) || model.equals(IPPhone.PHONE_8841) 
					 || model.equals(IPPhone.PHONE_8851) || model.equals(IPPhone.PHONE_8861)) {
				 
				 path = pr.getValue(PROPERTIES.IMAGE_559x313);
				 
			 } else if (model.equals(IPPhone.PHONE_8941) || model.equals(IPPhone.PHONE_8945) 
					 || model.equals(IPPhone.PHONE_8961) || model.equals(IPPhone.PHONE_9951) 
					 || model.equals(IPPhone.PHONE_9971)) {
				 
				 path = pr.getValue(PROPERTIES.IMAGE_498x289);
				 
			 } else if(model.equals(IPPhone.PHONE_7941)){
				 path = pr.getValue(PROPERTIES.IMAGE_298x144);
			 }
		return path;
	}
	*/
	/*
	private String getImageSizeByModel(String model) {
		// TODO Auto-generated method stub
			// TODO Auto-generated method stub
			 String size = "";
			 
			 if(model.equals(IPPhone.PHONE_7970) || model.equals(IPPhone.PHONE_7971) 
					 || model.equals(IPPhone.PHONE_7975) || model.equals(IPPhone.PHONE_IPCOMMUNICATOR)){
				 
				 size = IMAGESIZE.SIZE_298x168;
				 
			 } else if (model.equals(IPPhone.PHONE_8831)){

				 size = IMAGESIZE.SIZE_396x162;
				 
			 } else if (model.equals(IPPhone.PHONE_8811) || model.equals(IPPhone.PHONE_8841) 
					 || model.equals(IPPhone.PHONE_8851) || model.equals(IPPhone.PHONE_8861)) {
				 
				 size = IMAGESIZE.SIZE_559x313;
				 
			 } else if (model.equals(IPPhone.PHONE_8941) || model.equals(IPPhone.PHONE_8945) 
					 || model.equals(IPPhone.PHONE_8961) || model.equals(IPPhone.PHONE_9951) 
					 || model.equals(IPPhone.PHONE_9971)) {
				 
				 size = IMAGESIZE.SIZE_498x289;
				 
			 } else if (model.equals(IPPhone.PHONE_7941)) {
				 size = IMAGESIZE.SIZE_298x144;
			 }
		return size;
	}
	*/

	public boolean createAllImageFile(EmployeeVO objEmployee) {

		boolean bResult = true;
		// 배경 이미지
		String basic_img_path = XmlInfoMgr.getInstance().getBaseImgPath() + "basic.jpg";
		// 직원 사진 사번.jpg
//		String strCallerPicture = pr.getValue(PROPERTIES.BASE_IMAGE) + objEmployee.getEm_ID() + ".jpg";
		String strCallerPicture = XmlInfoMgr.getInstance().getBaseImgPath() + getEmailID(objEmployee.getCmIP()) + ".jpg";	// 사번 말고 이메일로 변경
        
		String logoImagePath = XmlInfoMgr.getInstance().getBaseImgPath() + "logo.jpg";
		
//		// 팝업 이미지 내선.png
//		String strDest = pr.getValue(PROPERTIES.EMPLOYEE_IMAGE) + objEmployee.getDN() + ".png";
        
        try {
        	
        	File dirPath = new File(XmlInfoMgr.getInstance().getEmpImgPath());
        	File [] dirFiles = dirPath.listFiles();
        	
        		
        	Map imageMap = IMAGESIZE.imageSizeMap;
    		
    		Set keySet = imageMap.keySet();
    		Iterator iter = keySet.iterator();
    		while(iter.hasNext()){
    			String key = (String)iter.next();
    			ImageVO imageVO = (ImageVO)imageMap.get(key);
    			File directory = new File(XmlInfoMgr.getInstance().getEmpImgPath() + "\\" + imageVO.getImageSize());
    			if(!directory.exists()){
    				directory.mkdirs();
    			}
        		
    		}
    		
        	
        	for (File file : dirFiles) {
        		
				String directtoryNm = file.getName();
				String strDest = dirPath + "\\" + directtoryNm + "\\" + objEmployee.getDN() + ".png";
				
				File logdir = new File(strDest);
				if (logdir.exists()) {
					// 이미 팝업 이미지가 있다면 이미지를 생성하지 않는다.
					g_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, "" , "createAllImageFile", objEmployee.getEm_ID() + " / " +objEmployee.getDN() + " 이미지 존재");
					return true;
				}
				
				logdir = new File(strCallerPicture);
				if(!logdir.exists()){
					// 직원 사진이 없다면 default 이미지로 대체한다.
					strCallerPicture = XmlInfoMgr.getInstance().getBaseImgPath() + "default.jpg";
				}
				
				
				BufferedImage basic_img = ImageIO.read(new File(basic_img_path)); // 배경이미지
				
				// 얼굴사진
				BufferedImage faceImage = null;
				try {
					faceImage = ImageIO.read(new File(strCallerPicture)); // 띄우고자 하는 사진 
				} catch (IOException ex) {
					ex.printStackTrace(ExceptionUtil.getPrintWriter());
					g_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.ERR_LOG, "" , "createAllImageFile", "There is no face image file !!");
					g_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.ERR_LOG, "" , "createAllImageFile", ExceptionUtil.getStringWriter().toString());
					return false;
				}
				
				// 회사 로고 사진
				BufferedImage logoImage = null;
				try {
					logoImage = ImageIO.read(new File(logoImagePath)); // 띄우고자 하는 사진 
				} catch (IOException ex) {
					ex.printStackTrace(ExceptionUtil.getPrintWriter());
					g_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.ERR_LOG, "" , "createAllImageFile", "There is no logo image file !!");
					g_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.ERR_LOG, "" , "createAllImageFile", ExceptionUtil.getStringWriter().toString());
					return false;
				}
				
				
//				int width = getWidth(directtoryNm);
//				int height = getHeight(directtoryNm);
				
				int width = Integer.parseInt(directtoryNm.substring(0, 3));
				int height = Integer.parseInt(directtoryNm.substring(3));
				
				int picture_x = (int) (width / 16.5);
				int picture_y = (int) (height / 4.5);
				int pictureWidth = width / 4;
				int pictureHeight = height / 2;
				int fontsize = (int) (height / 14.5);
				int font_x	= (int) (width / 2.5);
				int font_y 	= (int) (height / 2.8);
				
				
				BufferedImage mergedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
				Graphics2D graphics = (Graphics2D) mergedImage.getGraphics();
				
				graphics.setBackground(Color.WHITE);
				graphics.drawImage(basic_img, 0, 0, null);
				
				if (faceImage != null) {
					//graphics.drawImage(image2, 20, 60, null);
					// 127 x 150 Pixel Pictuer
					graphics.drawImage(faceImage, picture_x, picture_y, pictureWidth, pictureHeight, null);
					//System.out.println("[CreateImageFile] Picture is exist > " + strCallerPicture );
				} else {
					// Remote 원본 이미지를 가져오는 기능 추가 여부 판단 후 적용 예정
					// System.out.println("[CreateImageFile] Picture is NOT exist > " + objEmployee.getEMP_ID() );
				}
				
				if (logoImage != null) {
					//graphics.drawImage(image2, 20, 60, null);
					// 127 x 150 Pixel Pictuer
					graphics.drawImage(logoImage, (width / 10) * 8 , (height / 10) * 2, (width / 8), (height / 8), null);
					//System.out.println("[CreateImageFile] Picture is exist > " + strCallerPicture );
				} else {
					// Remote 원본 이미지를 가져오는 기능 추가 여부 판단 후 적용 예정
					// System.out.println("[CreateImageFile] Picture is NOT exist > " + objEmployee.getEMP_ID() );
				}
				
				
				
				
				Font font = new Font("맑은고딕", Font.BOLD, fontsize);
				graphics.setFont(font);
				graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				
				// 부서
				AttributedString as = new AttributedString( objEmployee.getGroupNm() );
				as.addAttribute(TextAttribute.FONT, font);
				as.addAttribute(TextAttribute.FOREGROUND, Color.red);
				graphics.drawString(as.getIterator(), font_x, font_y);
				
				// 직책 + 이름
				AttributedString attName = new AttributedString( objEmployee.getEm_position() + " " + objEmployee.getEm_name());
				attName.addAttribute(TextAttribute.FONT, font);
				attName.addAttribute(TextAttribute.FOREGROUND, Color.black);
				graphics.drawString(attName.getIterator(), font_x, font_y + 30);
				
				// 연락처
				AttributedString attPhone1 = new AttributedString(objEmployee.getDN());
				attPhone1.addAttribute(TextAttribute.FONT, font);
				attPhone1.addAttribute(TextAttribute.FOREGROUND, Color.blue);
				graphics.drawString(attPhone1.getIterator(), font_x, font_y + 60);
				
				ImageIO.write(mergedImage, "png", new File(strDest));
				
				g_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, "" , "createAllImageFile", "CREATE SUCCESS : " + strDest);
				
//				System.out.println("CREATE SUCCESS : " + strDest);
				
				Thread.sleep(100);	// 이미지 처리 속도 텀을 0.3 초로 설정
			}
        	
        	
        	
        } catch (Exception ioe) {
            bResult = false;
            ioe.printStackTrace(ExceptionUtil.getPrintWriter());
            g_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.ERR_LOG, "" , "createAllImageFile", ExceptionUtil.getStringWriter().toString());
            return false;
        }
        return bResult;
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
