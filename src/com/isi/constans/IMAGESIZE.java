package com.isi.constans;

import java.util.ArrayList;
import java.util.HashMap;

import com.isi.vo.ImageVO;
/**
*
* @author greatyun
*/
public abstract class IMAGESIZE {
	
	public static HashMap <String,ImageVO> imageSizeMap = new HashMap<String, ImageVO>();
	
	static {
		
		ImageVO imageVO = new ImageVO();
		imageVO.setImageSize("559265").setPicture_x1(37).setPicture_y1(29).setPicture_width(200).setPicture_height(209)
		.setName_x1(100).setName_y1(45).
		setOrg_x1(100).setOrg_y1(110).
		setDivision_x1(100).setDivision_y1(175)
		.setExtension_x1(100).setExtension_y1(240);
		imageSizeMap.put("559265", imageVO);
		
	}
	
}
