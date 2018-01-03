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
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import com.isi.constans.IMAGESIZE;
import com.isi.constans.LOGLEVEL;
import com.isi.constans.LOGTYPE;
import com.isi.constans.PROPERTIES;
import com.isi.constans.PersonType;
import com.isi.data.ImageMgr;
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
	
	
	
	public boolean createFaceImage(EmployeeVO objEmployee) {
		boolean bResult = true;
		Image image = null;
		String	strUrl="";
		
		if(objEmployee.getPic_path()==null || objEmployee.getPic_path().isEmpty() || objEmployee.getPic_path().equalsIgnoreCase("null")){
			// DB 정보에 증명사진 경로가 없을 경우
			strUrl = pr.getValue(PROPERTIES.FACE_IMAGE_URL) + objEmployee.getEm_ID() + ".jpg";
			g_Log.imageLog("", "createAllFaceImage", "[" +objEmployee.getEm_ID() + "] Property Read 증명사진 URL 경로 [" +strUrl+ "]");
		} else {
			strUrl = objEmployee.getPic_path();
			g_Log.imageLog("", "createAllFaceImage", "[" +objEmployee.getEm_ID() + "] 증명사진 URL 경로 [" +strUrl+ "]");
		}
		
		
		URL url = null;
		BufferedImage img = null;
		
		try{
			url = new URL(strUrl);
			img = ImageIO.read(url);
			
			File directory = new File(pr.getValue(PROPERTIES.FACE_IMAGE));
			// 폴더가 없을 경우 생성
			if(!directory.exists()){
				directory.mkdirs();
			}
			
			File file = new File(pr.getValue(PROPERTIES.FACE_IMAGE) + "\\" + objEmployee.getEm_ID() + ".jpg");
			
			
			if(img != null) {
				ImageIO.write(img, "jpg", file);
			} else {
				g_Log.imageLog("", "createAllFaceImage", "[" +objEmployee.getEm_ID() + "] ### 사진이 존재하지 않음 ###");
			}
			
		} catch (Exception e) {
			e.printStackTrace(ExceptionUtil.getPrintWriter());
			g_Log.imageLog("", "createAllFaceImage", "[" +objEmployee.getEm_ID() + "] ## Exception ## ");
			g_Log.imageLog("", "createAllFaceImage", ExceptionUtil.getStringWriter().toString());
//			g_Log.imageLog(LOGLEVEL.LEVEL_3, LOGTYPE.ERR_LOG, "" , "createAllFaceImage", ExceptionUtil.getStringWriter().toString());
			bResult = false;
		} finally {
			if(img!=null) {img = null;}
			if(url!=null) {url=null;}
		}
		
		return bResult;
	}
	
	
	public boolean deleteImageFile() {
		boolean bResult = true;
		// 팝업 이미지 내선.png
		try{
			String dirPath = pr.getValue(PROPERTIES.EMPLOYEE_IMAGE);
			File files = new File(dirPath);
			bResult = deleteDirectory(files);
			
		} catch(Exception e){
			e.printStackTrace(ExceptionUtil.getPrintWriter());
			g_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.ERR_LOG, "" , "deleteImageFile", ExceptionUtil.getStringWriter().toString());
			bResult = false;
		}
		
		return bResult;
	}
	
	public boolean deleteFaceImageFile(EmployeeVO employee) {
		boolean bResult = true;
		// 팝업 이미지 내선.png
		try{
			if(employee.getEm_ID()==null || employee.getEm_ID().equals("null") || employee.getEm_ID().isEmpty()){
				return false;
			}
			String dirPath = pr.getValue(PROPERTIES.FACE_IMAGE) + employee.getEm_ID() + ".jpg";
			File files = new File(dirPath);
			bResult = deleteDirectory(files);
			
		} catch(Exception e){
			e.printStackTrace(ExceptionUtil.getPrintWriter());
			g_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.ERR_LOG, "" , "deleteFaceImageFile", ExceptionUtil.getStringWriter().toString());
			bResult = false;
		}
		
		return bResult;
	}
	
	
	public boolean deleteFaceImageFile() {
		boolean bResult = true;
		// 팝업 이미지 내선.png
		try{
			String dirPath = pr.getValue(PROPERTIES.FACE_IMAGE);
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
	
	public boolean deleteEmpImage(EmployeeVO updateEmployee) {
		// TODO Auto-generated method stub
		boolean bResult = true;
		// 팝업 이미지 내선.png
		try{
			String dirPath = pr.getValue(PROPERTIES.EMPLOYEE_IMAGE);
			File path = new File(dirPath);
			File[] files = path.listFiles();
			
			for (File file : files) {
				String str_path = file.getAbsolutePath() + updateEmployee.getDN() + ".png";
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
	
	
	public boolean createImageFile(IPerson person , String model , String callID) {
		
		imageMgr = ImageMgr.getInstance();
		
		boolean bResult = true;
		String ID = "";
		String aniNum = "";
		String contactNum = "";
		String division = "";
		String displayName = "";
		String orgNm = "";
		
		int type = -1;
		if(person instanceof EmployeeVO){
		
			type = PersonType.EMPLOYEE;
			ID = ((EmployeeVO) person).getEm_ID();
			aniNum = ((EmployeeVO) person).getDN();
			contactNum = aniNum;
			division = ((EmployeeVO) person).getGroupNm();	// 부서명
			orgNm	 = ((EmployeeVO) person).getOrgNm();	// 사업소명
			displayName = ((EmployeeVO) person).getEm_position() + " " + ((EmployeeVO) person).getEm_name();
			
		} else if (person instanceof CustomerVO){
			
			type = PersonType.CUSTOMER;
			ID = ((CustomerVO) person).getPhoneNum();
			aniNum = ((CustomerVO) person).getPhoneNum();
			contactNum = aniNum;
			displayName = ((CustomerVO) person).getPosition() + " " + ((CustomerVO) person).getName(); 
			orgNm	 = ((CustomerVO) person).getCompany();	// 사업소명
			
		}
		
		// 직원 사진 사번.jpg
		String strCallerPicture = pr.getValue(PROPERTIES.FACE_IMAGE) + ID + ".jpg";
		
		String imageSize = imageMgr.getImageInfo(model);
		if(imageSize == null){
			m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, callID , "CreateImageFile", model + " 모델 이미지 사이즈 정보 없음 !! ");
			return false;
		}
		
		String strDest = pr.getValue(PROPERTIES.EMPLOYEE_IMAGE) + imageSize + "\\"+ aniNum + ".png";
        // 고로 이미지
//		String logoImagePath = pr.getValue(PROPERTIES.BASE_IMAGE) + "logo.jpg";
		
        try {
        	
            File logdir = new File(strDest);
            
            if(type == PersonType.EMPLOYEE){
            	if (logdir.exists() ) {
                	// 이미 팝업 이미지가 있다면 이미지를 생성하지 않는다. (직원 콜 의 경우만 해당)
            		m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, callID , "CreateImageFile", ID + " / " + aniNum + " 이미지 존재");
                    return true;
                }
            }
            
            logdir = new File(strCallerPicture);
            if(!logdir.exists()){
            	// 직원 사진이 없다면 default 이미지로 대체한다.
            	strCallerPicture = pr.getValue(PROPERTIES.BASE_IMAGE) + "default.jpg";
            }
            String basic_img_path = pr.getValue(PROPERTIES.BASE_IMAGE) + imageSize + "_basic.png";
            BufferedImage basic_img = ImageIO.read(new File(basic_img_path)); // 배경이미지
            
            
            /*
            // 얼굴 이미지
            BufferedImage faceImage = null;
            try {
                faceImage = ImageIO.read(new File(strCallerPicture)); // 띄우고자 하는 사진 
            } catch (IOException ex) {
            	ex.printStackTrace(ExceptionUtil.getPrintWriter());
            	m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.ERR_LOG, callID , "CreateImageFile", ExceptionUtil.getStringWriter().toString());
            }
            */
            
            /*
            // 로고 이미지
            BufferedImage logoImage = null;
            try {
            	logoImage = ImageIO.read(new File(logoImagePath)); // 띄우고자 하는 사진 
            } catch (IOException ex) {
            	ex.printStackTrace(ExceptionUtil.getPrintWriter());
            	m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.ERR_LOG, callID , "CreateImageFile", ExceptionUtil.getStringWriter().toString());
            }
            // 로고 이미지는 안띄울꺼니.. null 
            logoImage = null;
            */
            ImageVO imageVO = IMAGESIZE.imageSizeMap.get(imageSize);
            
            
//            System.out.println("imageSize : " + imageSize);
//            System.out.println("imageVO.getImageSize() : " + imageVO.getImageSize());
            
            int width = Integer.parseInt(imageVO.getImageSize().substring(0, 3));
            int height = Integer.parseInt(imageVO.getImageSize().substring(3));
            
            
            int picture_x = imageVO.getPicture_x1();
			int picture_y = imageVO.getPicture_y1();
			int pictureWidth = imageVO.getPicture_width();
			int pictureHeight = imageVO.getPicture_height();
			int fontsize = (int) (height / 8.5);
			
			BufferedImage mergedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics2D graphics = (Graphics2D) mergedImage.getGraphics();
			
			graphics.setBackground(Color.WHITE);
			graphics.drawImage(basic_img, 0, 0, null);
			
			/*
			if (faceImage != null) {
				Image imgTarget = faceImage.getScaledInstance(pictureWidth, pictureHeight, Image.SCALE_SMOOTH);
				int pixels[] = new int[pictureWidth * pictureHeight];
				PixelGrabber pg = new PixelGrabber(imgTarget, 0, 0, pictureWidth, pictureHeight, pixels,0 ,pictureWidth);
				pg.grabPixels();
				graphics.drawImage(imgTarget, picture_x, picture_y, pictureWidth, pictureHeight, null);
				
//				graphics.drawImage(faceImage, picture_x, picture_y, pictureWidth, pictureHeight, null);
				//System.out.println("[CreateImageFile] Picture is exist > " + strCallerPicture );
			} else {
				// Remote 원본 이미지를 가져오는 기능 추가 여부 판단 후 적용 예정
				// System.out.println("[CreateImageFile] Picture is NOT exist > " + objEmployee.getEMP_ID() );
			}
			*/
			
			/*
			if (logoImage != null) {
				//graphics.drawImage(image2, 20, 60, null);
				// 127 x 150 Pixel Pictuer
				graphics.drawImage(logoImage, ((width / 20) * 17) , (height / 10), (width / 8), (height / 8), null);
				//System.out.println("[CreateImageFile] Picture is exist > " + strCallerPicture );
			} else {
				// Remote 원본 이미지를 가져오는 기능 추가 여부 판단 후 적용 예정
				// System.out.println("[CreateImageFile] Picture is NOT exist > " + objEmployee.getEMP_ID() );
			}
			*/
			
			Font font = new Font("맑은고딕", Font.BOLD, fontsize);
			graphics.setFont(font);
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			if(!displayName.isEmpty()){
				// 직책 + 이름
				AttributedString attName = new AttributedString( displayName );
				attName.addAttribute(TextAttribute.FONT, font);
				attName.addAttribute(TextAttribute.FOREGROUND, Color.black);
				graphics.drawString(attName.getIterator(), imageVO.getName_x1(), imageVO.getName_y1());
				
			}
			
			if(!orgNm.isEmpty()) {
				if(orgNm.length() > 9) {
					Font newfont = new Font("맑은고딕", Font.BOLD, fontsize - 2);
					graphics.setFont(newfont);
					graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					// 사업소
					AttributedString as = new AttributedString( orgNm );
					as.addAttribute(TextAttribute.FONT, newfont);
					as.addAttribute(TextAttribute.FOREGROUND, Color.black);
					graphics.drawString(as.getIterator(), imageVO.getOrg_x1(), imageVO.getOrg_y1());
				} else {
					// 사업소
					AttributedString as = new AttributedString( orgNm );
					as.addAttribute(TextAttribute.FONT, font);
					as.addAttribute(TextAttribute.FOREGROUND, Color.black);
					graphics.drawString(as.getIterator(), imageVO.getOrg_x1(), imageVO.getOrg_y1());
				}
				
			}
			
			if(!division.isEmpty()) {
				// 부서
				AttributedString attPhone1 = new AttributedString(division);
				attPhone1.addAttribute(TextAttribute.FONT, font);
				attPhone1.addAttribute(TextAttribute.FOREGROUND, Color.black);
				graphics.drawString(attPhone1.getIterator(), imageVO.getDivision_x1(), imageVO.getDivision_y1());
				
			}
			
			if(!contactNum.isEmpty()){
				// 내선번호
				AttributedString attExtension = new AttributedString(contactNum);
				attExtension.addAttribute(TextAttribute.FONT, font);
				attExtension.addAttribute(TextAttribute.FOREGROUND, Color.black);
				graphics.drawString(attExtension.getIterator(), imageVO.getExtension_x1(), imageVO.getExtension_y1());
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
	

	public boolean createAllImageFile(EmployeeVO objEmployee) {

		boolean bResult = true;
		// 배경 이미지
//		String basic_img_path = pr.getValue(PROPERTIES.BASE_IMAGE) + "basic.jpg";
		// 직원 사진 사번.jpg
//		String strCallerPicture = pr.getValue(PROPERTIES.FACE_IMAGE) + objEmployee.getEm_ID() + ".jpg";
//		String strCallerPicture = pr.getValue(PROPERTIES.FACE_IMAGE) + getEmailID(objEmployee.getEmail()) + ".jpg";	// 사번 말고 이메일로 변경
        
//		String logoImagePath = pr.getValue(PROPERTIES.BASE_IMAGE) + "logo.jpg";
		
//		// 팝업 이미지 내선.png
//		String strDest = pr.getValue(PROPERTIES.EMPLOYEE_IMAGE) + objEmployee.getEm_extension() + ".png";
        
        try {
        	
        	File dirPath = new File(pr.getValue(PROPERTIES.EMPLOYEE_IMAGE));
        	File [] dirFiles = dirPath.listFiles();
        		
        		Map imageMap = IMAGESIZE.imageSizeMap;
        		
        		Set keySet = imageMap.keySet();
        		Iterator iter = keySet.iterator();
        		while(iter.hasNext()){
        			String key = (String)iter.next();
        			ImageVO imageVO = (ImageVO)imageMap.get(key);
        			File directory = new File(pr.getValue(PROPERTIES.EMPLOYEE_IMAGE) + "\\" + imageVO.getImageSize());
        			if(!directory.exists()){
        				directory.mkdirs();
        			}
	        		
        		}
        	
        		
        	for (File file : dirFiles) {
        		
				directtoryNm = file.getName();
				String basic_img_path = pr.getValue(PROPERTIES.BASE_IMAGE) +  directtoryNm + "_basic.png";
				String strDest = dirPath + "\\" + directtoryNm + "\\" + objEmployee.getDN() + ".png";
				
				File logdir = new File(strDest);
				if (logdir.exists()) {
					// 이미 팝업 이미지가 있다면 이미지를 생성하지 않는다.
					g_Log.imageLog( "" , "createAllImageFile", objEmployee.getEm_ID() + " / " +objEmployee.getDN() + " 이미지 존재");
					return true;
				}
				/*
				logdir = new File(strCallerPicture);
				if(!logdir.exists()){
					// 직원 사진이 없다면 default 이미지로 대체한다.
					strCallerPicture = pr.getValue(PROPERTIES.BASE_IMAGE) + "default.jpg";
				}
				*/
				BufferedImage basic_img = ImageIO.read(new File(basic_img_path)); // 배경이미지
				
				/*
				// 얼굴사진
				BufferedImage faceImage = null;
				try {
					faceImage = ImageIO.read(new File(strCallerPicture)); // 띄우고자 하는 사진 
					
				} catch (IOException ex) {
					ex.printStackTrace(ExceptionUtil.getPrintWriter());
					g_Log.imageLog( "" , "createAllImageFile", "There is no face image file !!");
					g_Log.imageLog( "" , "createAllImageFile", ExceptionUtil.getStringWriter().toString());
					return false;
				}
				*/
				/*
				// 회사 로고 사진
				BufferedImage logoImage = null;
				try {
					logoImage = ImageIO.read(new File(logoImagePath)); // 띄우고자 하는 사진 
				} catch (IOException ex) {
					ex.printStackTrace(ExceptionUtil.getPrintWriter());
					g_Log.imageLog( "" , "createAllImageFile", "There is no logo image file !!");
					g_Log.imageLog( "" , "createAllImageFile", ExceptionUtil.getStringWriter().toString());
					return false;
				}
				// 로고안띄울꺼니가.. null
				logoImage = null;
				*/
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
				
				/*
				if (faceImage != null) {
					Image imgTarget = faceImage.getScaledInstance(pictureWidth, pictureHeight, Image.SCALE_SMOOTH);
					int pixels[] = new int[pictureWidth * pictureHeight];
					PixelGrabber pg = new PixelGrabber(imgTarget, 0, 0, pictureWidth, pictureHeight, pixels,0 ,pictureWidth);
					pg.grabPixels();
					graphics.drawImage(imgTarget, picture_x, picture_y, pictureWidth, pictureHeight, null);
//					graphics.drawImage(faceImage, picture_x, picture_y, pictureWidth, pictureHeight, null);
					//System.out.println("[CreateImageFile] Picture is exist > " + strCallerPicture );
				} else {
					// Remote 원본 이미지를 가져오는 기능 추가 여부 판단 후 적용 예정
					// System.out.println("[CreateImageFile] Picture is NOT exist > " + objEmployee.getEMP_ID() );
				}
				*/
				/*
				if (logoImage != null) {
					//graphics.drawImage(image2, 20, 60, null);
					// 127 x 150 Pixel Pictuer
//					graphics.drawImage(logoImage, ((width / 20) * 17)  , (height / 10), (width / 8), (height / 8), null);
					//System.out.println("[CreateImageFile] Picture is exist > " + strCallerPicture );
				} else {
					// Remote 원본 이미지를 가져오는 기능 추가 여부 판단 후 적용 예정
					// System.out.println("[CreateImageFile] Picture is NOT exist > " + objEmployee.getEMP_ID() );
				}
				*/
				
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
				
//				System.out.println("CREATE SUCCESS : " + strDest);
				
				Thread.sleep(100);	// 이미지 처리 속도 텀을 0.3 초로 설정
				
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
