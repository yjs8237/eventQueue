package com.isi.data;

import com.isi.constans.PROPERTIES;
import com.isi.constans.PersonType;
import com.isi.db.JDatabase;
import com.isi.file.PropertyRead;
import com.isi.utils.Utils;
import com.isi.vo.*;
/**
*
* @author greatyun
*/
public class XMLData {
	
	private static final int CALL_RING = 0;
	private static final int CALL_CONNECT = 2;
	private static final int CALL_HANGUP = 3;
	
	private PropertyRead pr;
	
	public XMLData () {
		pr = PropertyRead.getInstance();
	}
	
	
	public String getCiscoIPPhoneImageFile(String strTitle, IPerson person, int nCallStep , String model , String caller_type , String pushPng) {
        StringBuffer xmlBuffer = new StringBuffer();
        
        // 폰 모델별 이미지 사이즈가 다르기 때문에 각기 다른 사이즈의 이미지 폴더  URL 을 구한다.
//        String path =     getPathByModel(model);
//        String path = pr.getValue(PROPERTIES.ISXMLWEB_URL) + ImageMgr.getInstance().getImageInfo(model) + "/"; 
        String path = "http://" + XmlInfoMgr.getInstance().getXmlPushUrl() + ImageMgr.getInstance().getImageInfo(model).getImageSize() + "/" + caller_type + "/";
        
        String aniNum = "";
        if(person instanceof EmployeeVO){
        	aniNum = ((EmployeeVO) person).getExtension();
        } else if(person instanceof CustomerVO) {
        	aniNum = ((CustomerVO) person).getPhoneNum();
        }
        
        {
            xmlBuffer.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
            xmlBuffer.append("<CiscoIPPhoneImageFile>");
            xmlBuffer.append("<Title>");
            xmlBuffer.append(strTitle);
            xmlBuffer.append("</Title>");
            xmlBuffer.append("<Prompt>");
            xmlBuffer.append("" + aniNum);
            xmlBuffer.append("</Prompt>");
            xmlBuffer.append("<LocationX>0</LocationX>");
            xmlBuffer.append("<LocationY>0</LocationY>");
            xmlBuffer.append("<URL>");
            xmlBuffer.append( path + pushPng + ".png");
            xmlBuffer.append("</URL>");
            
            if (nCallStep == CALL_HANGUP) {
//                xmlBuffer.append("<SoftKeyItem>");
//                xmlBuffer.append("<Name>검색</Name>");
//                xmlBuffer.append("<URL>"+ServiceParam.URL()+"/isiPhoneService/DirectoryService</URL>");
//                xmlBuffer.append("<Position>1</Position>");
//                xmlBuffer.append("</SoftKeyItem>");
            } else if (nCallStep == CALL_RING || nCallStep == CALL_CONNECT) {
                
            	xmlBuffer.append("<SoftKeyItem>");
                xmlBuffer.append("<Name>Close</Name>");
                xmlBuffer.append("<URL>SoftKey:Exit</URL>");
                xmlBuffer.append("<Position>1</Position>");
                xmlBuffer.append("</SoftKeyItem>");
//                xmlBuffer.append("<SoftKeyItem>");
//                xmlBuffer.append("<Name>Accept</Name>");
//                xmlBuffer.append("<URL>SoftKey:Select</URL>");
//                xmlBuffer.append("<Position>1</Position>");
//                xmlBuffer.append("</SoftKeyItem>");
            }
            
            xmlBuffer.append("</CiscoIPPhoneImageFile>");
        }
       // m_Log.warning(" XML > " + xmlBuffer.toString());
        return xmlBuffer.toString();
    }
	


	//
    // Phone Service XML 작성 관련 함수
    //
    public String getCiscoIPPhoneText(String strTitle, IPerson person) {
        StringBuffer xmlBuffer = new StringBuffer();
        
        String aniNum = "";
        String position = "";
        String name = "";
        String division = "";
        String orgNm = "";
        String custLevel = "";
        String isViewYN = ""; // 대표번호 설정여부
        
        int type = 0;
        if(person instanceof EmployeeVO){
        	type = PersonType.EMPLOYEE;
        	name = ((EmployeeVO) person).getEmp_nm_kor();
        	aniNum = ((EmployeeVO) person).getExtension();
        	position = ((EmployeeVO) person).getPos_nm();
        	division = ((EmployeeVO) person).getOrg_nm();
        } else if (person instanceof CustomerVO){
        	type = PersonType.CUSTOMER;
        	name = ((CustomerVO) person).getName();
        	aniNum = ((CustomerVO) person).getPhoneNum();
        	custLevel = ((CustomerVO) person).getCustLevel();
        }
        
//        System.out.println("isViewYN [" + isViewYN + "]");
        
        if(isViewYN != null && isViewYN.equalsIgnoreCase("Y")) {
        	JDatabase database = new JDatabase("XMLData getCiscoIPPhoneText");
    		database.connectDB(pr.getValue(PROPERTIES.DB_CLASS), pr.getValue(PROPERTIES.DB_URL), pr.getValue(PROPERTIES.DB_USER), pr.getValue(PROPERTIES.DB_PASSWORD));
    		division =  database.getViewNumber(aniNum);
    		name ="";
    		position="";
    		database.disconnectDB();
        }
        
        {   
            xmlBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            xmlBuffer.append("<CiscoIPPhoneText>");
            xmlBuffer.append("<Title>");
            xmlBuffer.append(strTitle);
            xmlBuffer.append("</Title>");
            xmlBuffer.append("<Prompt>");
            xmlBuffer.append("" + aniNum);
            xmlBuffer.append("</Prompt>");
            xmlBuffer.append("<Text>"); 
            if(type == PersonType.EMPLOYEE){
            	xmlBuffer.append(orgNm + " " + division + "\r\n"); // 직급 / 성명 / 부서 / 핸드폰번호
            	xmlBuffer.append(position + " " + name); // 직급 / 성명 / 부서 / 핸드폰번호
            } else {
            	xmlBuffer.append(custLevel+" "+name);
            }
            
            xmlBuffer.append("</Text>");
            xmlBuffer.append("<SoftKeyItem>");
            xmlBuffer.append("<Name>Close</Name>");
            xmlBuffer.append("<URL>SoftKey:Exit</URL>");
            xmlBuffer.append("<Position>4</Position>");
            xmlBuffer.append("</SoftKeyItem>");
            xmlBuffer.append("</CiscoIPPhoneText>");
            /////////////////////////////////////////////////
        }
        return xmlBuffer.toString();
    }
	
    public String getMenuInit(){
    	StringBuffer xmlBuffer = new StringBuffer();
    	
    	xmlBuffer.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
    	xmlBuffer.append("<CiscoIPPhoneExecute>");
    	xmlBuffer.append("<ExecuteItem URL=\"Init:Services\"/>");
    	xmlBuffer.append("</CiscoIPPhoneExecute>");
    	
    	return xmlBuffer.toString();
    }
    
    public String getInitMessages(){
    	StringBuffer xmlBuffer = new StringBuffer();
    	
    	//"<CiscoIPPhoneExecute><ExecuteItem Priority=\"0\" URL=\"Init:Directories\"/><ExecuteItem Priority=\"0\" URL=\"Init:Services\"/></CiscoIPPhoneExecute>";
    	
    	xmlBuffer.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
    	xmlBuffer.append("<CiscoIPPhoneExecute>");
    	xmlBuffer.append("<ExecuteItem URL=\"Init:Messages\"/>");
    	xmlBuffer.append("</CiscoIPPhoneExecute>");
    	
    	return xmlBuffer.toString();
    }
	
}
