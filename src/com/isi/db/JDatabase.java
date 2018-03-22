package com.isi.db;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.isi.constans.*;
import com.isi.data.ImageMgr;
import com.isi.data.XmlInfoMgr;
import com.isi.file.GLogWriter;
import com.isi.file.ILog;
import com.isi.file.PropertyRead;
import com.isi.utils.Utils;
import com.isi.vo.*;
import com.test.vo.TestCallVO;
import com.test.vo.TestEmployeeVO;

/**
*
* @author greatyun
*/
public class JDatabase  {

    private Connection              m_conn      = null;
    private Statement               m_stmt      = null;
    private ResultSet               m_rs        = null;
    private ILog                    m_Log       = new GLogWriter();
    private String                  m_url       = null;
    private String					m_driver	= null;
    private String                  m_user      = null;
    private String                  m_pwd       = null;
    private boolean                 m_binit     = false;
    private StringWriter			sw			= null;
    private PrintWriter				pw			= null;
    private String		invokeName;
    
    public JDatabase () {
    	sw = new StringWriter();
    	pw = new PrintWriter(sw);
    }
    
    public JDatabase (String invokeName){
    	sw = new StringWriter();
    	pw = new PrintWriter(sw);
    	this.invokeName = invokeName;
    }
    
    
    
    public boolean IsConnected() {
        try {
            if (m_conn == null)
                return false;
            else{
                return !m_conn.isClosed();
            }
        } catch (Exception e){
        	e.printStackTrace(pw);
        	m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.ERR_LOG, "", "IsConnected", sw.toString());
            return false;
        }            
    }
    
    public int connectDB(String className, String aURL, String aID, String aPwd) {
       try {   
            if ( m_binit == false) {
                // Class.forName("com.microsoft.jdbc.sqlserver.SQLServerDriver");
                //Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            	Class.forName(className);
                m_binit = true;
            }
            
            disconnectDB();
            
            //m_Log.standLog(invokeName, "connectDB", "DB Connection Try URL["+aURL+"] ID["+aID+"] PW["+aPwd+"]");
            
            m_conn = DriverManager.getConnection(aURL, aID, aPwd);
            m_driver = className;
            m_url = aURL;
            m_user = aID;
            m_pwd = aPwd;
            return RESULT.RTN_SUCCESS;
       } catch (Exception e) {   
    	   e.printStackTrace(pw);
    	   System.out.println(sw.toString());
    	   //m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.ERR_LOG, "", "connectDB", sw.toString());
           return RESULT.RTN_EXCEPTION;            
       }
    }
    public int connectDB() {
        return connectDB(m_driver,m_url, m_user, m_pwd);
    }
    public int executeQuery(String aQuery, boolean bClose) {
        int rowcnt = 0;
        try {
            
            if (m_conn == null || m_conn.isClosed()) {            
                connectDB();
            }

            m_stmt = m_conn.createStatement();
            
            if (m_stmt.executeUpdate(aQuery) == 0) {
                return RESULT.RTN_NOTFOUND;
            }
            
            m_stmt.close();
            
            if (bClose == true) {
                disconnectDB();
            }            
            return RESULT.RTN_SUCCESS;
        } catch (Exception e) {
            return RESULT.RTN_EXCEPTION;
        }
    }
    
    public int EndSelect() {
        try {
            if(m_rs != null) {
                m_rs.close();
            }
            m_rs = null;

            if (m_stmt != null) {
                m_stmt.close();
            }
            m_stmt = null;
            return RESULT.RTN_SUCCESS;
        } catch (Exception e) {
            return RESULT.RTN_EXCEPTION;
        }       
    }
/*    
    public ResultSet PrepareQuery(String aQuery, Object [] aObj) {
        try {
            if (m_connected == false) {            
                connectDB();
            }

            m_stmt = m_conn.prepareStatement(aQuery);
            
            for ( int i = 0; i < aObj.length; i++)
                if (Integer.TYPE == aObj[i].getClass()) {
                    
                } else if (Long.TYPE = ) {
                    
                } else if (Boolean.TYPE) {
                    
                } else if (Float.TYPE) {
                    
                } else if (Double.TYPE) {
                    
                } else if ()
                    
                        
            pstmt.setString(1, "홍길동"); pstmt.setInt(2, 20);

            pstmt.executeUpdate();

        
            m_rs = m_stmt.executeQuery(aQuery);
            
            m_stmt.close();
            
            if (bClose == true) {
                disconnectDB();
            }            
            return m_rs;
        } catch (Exception e) {
            return null;
        }
    }
 */ 
    public ResultSet selectQuery(String aQuery, boolean bClose) {
        
        try {
            if (m_conn == null || m_conn.isClosed()) {            
                connectDB();
            }

            m_stmt = m_conn.createStatement();
            m_rs = m_stmt.executeQuery(aQuery);
            
//            m_stmt.close();
            
            if (bClose == true) {
                disconnectDB();
            }            
            return m_rs;
        } catch (Exception e) {
        	e.printStackTrace();
            return null;
        }
      
    }
    
    
    public String getViewNumber(String aniNum) {
    	String sql = "select code_nm from tb_code_dtl where cd_class = '40000' and code = '" + aniNum + "'";
    	String result="";
    	ResultSet rs = null;
    	try{
    		// TODO: TR 넣기!!!!
    		rs = selectQuery(sql, false);
        	if(rs != null){
        		while(rs.next()){
        			result = rs.getString("code_nm");
            	}
        	}
    	}catch (Exception e){
    		
    	} finally {
    		if(rs != null) {
    			try{rs.close();} catch (Exception e){}
    		}
    	}
    	
    	return result;
    }
    
    public CustomerVO getCustInfo(String ani){
    	
    	System.out.println("************** DB Select *****************");
    	CustomerVO customer = null;
    	String sql = "select * from tb_cust_info where hp_num = '"+ani+"'";
    	ResultSet rs = null;
    	try{
    		// TODO: TR 넣기!!!!
    		rs = selectQuery(sql, false);
        	if(rs != null){
        		while(rs.next()){
        			customer = new CustomerVO();
        			customer.setName(rs.getString("name")).setPhoneNum(rs.getString("hp_num")).setCustLevel(rs.getString("cust_level"));
            	}
        	}
    	}catch(Exception e){
    		
    	} finally {
    		if(rs != null) {
    			try{rs.close();} catch (Exception e){}
    		}
    	}
    	
    	return customer;
    }
    
    public int disconnectDB() {
        try {
            if (m_conn != null) {
                m_conn.close();
                m_conn = null;        
            }
            return RESULT.RTN_SUCCESS;
        } catch (Exception e) {
            m_conn = null;
            return RESULT.RTN_EXCEPTION;
        }
    } 
    
  public int selectXMLInfo(String query){
    	
    	System.out.println("************** DB Select XML Info *****************");
    	
    	query = query.trim();
    	
    	XmlInfoMgr xmlInfoMgr = XmlInfoMgr.getInstance();
    	
    	System.out.println("QUERY -> " + query);
    	
    	try{
    		ResultSet rs = selectQuery(query, false);
    		
        	if(rs != null){
        		
        		while(rs.next()) {
        			
        			String logStr = "";
        			
        			if(rs.getString("config_name").equals("xml_mode") ) {
        				xmlInfoMgr.setXmlMode(rs.getString("config_value"));
        				logStr += "xml_mode : " + rs.getString("config_value") + " ";
        			} else if(rs.getString("config_name").equals("duplex_yn") ) {
        				xmlInfoMgr.setDuplexYN(rs.getString("config_value"));
        				logStr += "duplex_yn : " + rs.getString("config_value") + " ";
        			} else if(rs.getString("config_name").equals("side_a_ip")) {
        				xmlInfoMgr.setSideAIP(rs.getString("config_value"));
        				logStr += "side_a_ip : " + rs.getString("config_value") + " ";
        				if(!PropertyRead.getInstance().getValue(PROPERTIES.SIDE_INFO).equals("A")) {
        					xmlInfoMgr.setRemoteIP(rs.getString("config_value"));
        					logStr += "remote_ip : " + rs.getString("config_value") + " ";
        				}
        			} else if(rs.getString("config_name").equals("side_b_ip")) {
        				xmlInfoMgr.setSideBIP(rs.getString("config_value"));
        				logStr += "side_b_ip : " + rs.getString("config_value") + " ";
        				if(PropertyRead.getInstance().getValue(PROPERTIES.SIDE_INFO).equals("A")) {
        					xmlInfoMgr.setRemoteIP(rs.getString("config_value"));
        					logStr += "remote_ip : " + rs.getString("config_value") + " ";
        				}
        			} else if(rs.getString("config_name").equals("remote_port") ) {
        				
        				xmlInfoMgr.setRemotePort(rs.getString("config_value"));
        				logStr += "remote_port : " + rs.getString("config_value") + " ";
        			} else if(rs.getString("config_name").equals("custinfo_pupup") ) {
        				
        				xmlInfoMgr.setCustinfoPopupYN(rs.getString("config_value"));
        				logStr += "custinfo_pupup : " + rs.getString("config_value") + " ";
        			} else if(rs.getString("config_name").equals("cm_cnt") ) {
        				
        				xmlInfoMgr.setCmCnt(Integer.parseInt(rs.getString("config_value")));
        				logStr += "cm_cnt : " + rs.getString("config_value") + " ";
        			} else if(rs.getString("config_name").equals("cm_ip01") ) {
        				
        				xmlInfoMgr.setCm1IpAddr(rs.getString("config_value"));
        				logStr += "cm_ip01 : " + rs.getString("config_value") + " ";
        				xmlInfoMgr.setCm1User(rs.getString("config_desc1"));
        				logStr += "cm_user1 : " + rs.getString("config_desc1") + " ";
        				xmlInfoMgr.setCm1Pwd(rs.getString("config_desc2"));
        				logStr += "cm_pwd1 : " + rs.getString("config_desc2") + " ";
        			} else if(rs.getString("config_name").equals("cm_ip03") ) {
        				
        				xmlInfoMgr.setCm2IpAddr(rs.getString("config_value"));
        				logStr += "cm_ip03 : " + rs.getString("config_value") + " ";
        				xmlInfoMgr.setCm2User(rs.getString("config_desc1"));
        				logStr += "cm_user2 : " + rs.getString("config_desc1") + " ";
        				xmlInfoMgr.setCm2Pwd(rs.getString("config_desc2"));
        				logStr += "cm_pwd2 : " + rs.getString("config_desc2") + " ";
        			} else if(rs.getString("config_name").equals("conn_timeout") ) {
        				
        				xmlInfoMgr.setConnectTimeout(rs.getInt("config_value"));
        				logStr += "conn_timeout : " + rs.getString("config_value") + " ";
        			} else if(rs.getString("config_name").equals("read_timeout") ) {
        				
        				xmlInfoMgr.setReadTimeout(rs.getInt("config_value"));
        				logStr += "read_timeout : " + rs.getString("config_value") + " ";
        			} else if(rs.getString("config_name").equals("xml_push_url") ) {
        				
        				xmlInfoMgr.setXmlPushUrl(rs.getString("config_value"));
        				logStr += "xml_push_url : " + rs.getString("config_value") + " ";
        			} else if(rs.getString("config_name").equals("http_port") ) {
        				
        				xmlInfoMgr.setHttpPort(rs.getInt("config_value"));
        				logStr += "http_port : " + rs.getString("config_value") + " ";
        			} else if(rs.getString("config_name").equals("console_debug") ) {
        				
        				xmlInfoMgr.setConsoleDebugYN(rs.getString("config_value"));
        				logStr += "console_debug : " + rs.getString("config_value") + " ";
        			} else if(rs.getString("config_name").equals("log_level") ) {
        				
        				xmlInfoMgr.setLogLevel(rs.getInt("config_value"));
        				logStr += "log_level : " + rs.getString("config_value") + " ";
        			} else if(rs.getString("config_name").equals("log_path") ) {
        				
        				xmlInfoMgr.setLogPath(rs.getString("config_value"));
        				logStr += "log_path : " + rs.getString("config_value") + " ";
        			} else if(rs.getString("config_name").equals("base_img_path") ) {
        				
        				xmlInfoMgr.setBaseImgPath(rs.getString("config_value"));
        				logStr += "base_img_path : " + rs.getString("config_value") + " ";
        			} else if(rs.getString("config_name").equals("face_img_path") ) {
        				
        				xmlInfoMgr.setFaceImgPath(rs.getString("config_value"));
        				logStr += "face_img_path : " + rs.getString("config_value") + " ";
        			} else if(rs.getString("config_name").equals("emp_img_path") ) {
        				
        				xmlInfoMgr.setEmpImgPath(rs.getString("config_value"));
        				logStr += "emp_img_path : " + rs.getString("config_value") + " ";
        			} else if(rs.getString("config_name").equals("log_del_day") ) {
        				
        				xmlInfoMgr.setLogDelDays(rs.getInt("config_value"));
        				logStr += "log_del_day : " + rs.getString("config_value") + " ";
        			} else if(rs.getString("config_name").equals("http_sync_port") ) {
        				
        				xmlInfoMgr.setHttp_sync_port(rs.getInt("config_value"));
        				logStr += "http_sync_port : " + rs.getString("config_value") + " ";
        			}
        			
            	}
        	}
    	}catch(Exception e){
    		
    	}
    	
    	System.out.println(xmlInfoMgr.toString());
    	return RESULT.RTN_SUCCESS;
    }
    
    
    public int selectImageInfoByModel(String query){
    	
    	System.out.println("************** DB Select ImageInfo *****************");
    	
    	query = query.trim();
    	
    	System.out.println("SQL : " + query);
    	
    	try{
    		ResultSet rs = selectQuery(query, false);
        	if(rs != null){
        		while(rs.next()) {
        			ImageVO imageVO = new ImageVO();
        			imageVO.setModel(rs.getString("device_model"));
        			imageVO.setImageSize(rs.getString("display_size"));
        			imageVO.setPicture_x1(rs.getInt("pic_x"));
        			imageVO.setPicture_y1(rs.getInt("pic_y"));
        			imageVO.setPicture_width(rs.getInt("pic_width"));
        			imageVO.setPicture_height(rs.getInt("pic_height"));
        			imageVO.setAninum_x(rs.getInt("aninum_x"));
        			imageVO.setAninum_y(rs.getInt("aninum_y"));
        			imageVO.setName_x(rs.getInt("name_x"));
        			imageVO.setName_y(rs.getInt("name_y"));
        			imageVO.setPosition_x(rs.getInt("position_x"));
        			imageVO.setPosition_y(rs.getInt("position_y"));
        			imageVO.setDivision_x(rs.getInt("division_x"));
        			imageVO.setDivision_y(rs.getInt("division_y"));
        			imageVO.setFloor_x(rs.getInt("floor_x"));
        			imageVO.setFloor_y(rs.getInt("floor_y"));
        			
        			m_Log.standLog("", "selectImageInfoByModel", 
        					imageVO.getModel() + " " + 
        					imageVO.getImageSize() + " " + 
        					imageVO.getPicture_x1()+ " " +
        					imageVO.getPicture_y1()+ " " +
        					imageVO.getPicture_width()+ " " +
        					imageVO.getPicture_height()+ " " +
        					imageVO.getAninum_x()+ " " +
        					imageVO.getAninum_y()+ " " +
        					imageVO.getPosition_x()+ " " +
        					imageVO.getPosition_y()+ " " +
        					imageVO.getDivision_x()+ " " +
        					imageVO.getDivision_y()+ " " +
        					imageVO.getFloor_x()+ " " +
        					imageVO.getFloor_y());
        			
        			ImageMgr.getInstance().addImage(rs.getString("device_model"), imageVO);
        			
            	}
        	} else {
        		System.out.println("NO DB DATA");
        	}
    	}catch(Exception e){
    		
    	}
    	return RESULT.RTN_SUCCESS;
    }
    
    /*
    public int insertDeviceInfo(DeviceVO device) {
    	
    	String sql = "insert into tb_device_info values('" + device.getDn() + "' , '" +device.getIp()+ "' , '"+device.getModel()+"')";
    	return executeQuery(sql, false);
    	
    }
    */
    
    public int deleteDeviceInfo(){
    	String sql = "delete from tb_device_info";
    	return executeQuery(sql, false);
    }
    
    public int insertUACall (String callid, String callingDN, String calledDN) {
    	
    	Utils util = new Utils();
    	String date = util.getCurrentDay().substring(0, 8);
    	String time = util.getCurrentDay().substring(8);
    	
    	int result = RESULT.RTN_EXCEPTION;
    	String sql = "insert into tb_call_hist (call_ymd, call_hms, call_type, my_dn, ani, bigo, call_id) "
    			+ "values('" + date + "' , '" +time+ "' , '"+"UA"+"' , '" +calledDN+ "' , '" + callingDN + "' , '" +""+"' , '"+callid+"')";
    	
    	if(executeQuery(sql, false) == RESULT.RTN_SUCCESS){
    		m_Log.standLog("", "insertCallHistory", "SUCCESS!! Call insertUACall Information !! Query[" + sql + "]");
//    		System.out.println("## DB insertUACall SUCCESS ## -> " + sql);
    	} else {
    		m_Log.standLog("", "insertCallHistory", "FAIL!! Call insertUACall Information !! Query[" + sql + "]");
//    		System.out.println("## DB insertUACall FAIL ## -> " + sql);
    	}
    	return result;
    }
    
   public int insertPickUpUACall (String callid, String callingDN, String calledDN, String pickUpDN) {
    	
    	Utils util = new Utils();
    	String date = util.getCurrentDay().substring(0, 8);
    	String time = util.getCurrentDay().substring(8);
    	
    	int result = RESULT.RTN_EXCEPTION;
    	
    	String strMsg = "";
    	if(!pickUpDN.isEmpty()){
    		strMsg = "부재중 통화 ("+pickUpDN+") 당겨받음";
    	}
    	
    	String sql = "insert into tb_call_hist (call_ymd, call_hms, call_type, my_dn, ani, bigo, call_id) "
    			+ "values('" + date + "' , '" +time+ "' , '"+"UA"+"' , '" +calledDN+ "' , '" + callingDN + "' , '" + strMsg +"' , '"+callid+"')";
    	
    	
    	if(executeQuery(sql, false) == RESULT.RTN_SUCCESS){
    		m_Log.standLog("", "insertCallHistory", "SUCCESS!! Call insertPickUpUACall Information !! Query[" + sql + "]");
//    		System.out.println("## DB insertPickUpUACall SUCCESS ## -> " + sql);
    	} else {
    		m_Log.standLog("", "insertCallHistory", "FAIL!! Call insertPickUpUACall Information !! Query[" + sql + "]");
//    		System.out.println("## DB insertPickUpUACall FAIL ## -> " + sql);
    	}
    	return result;
    }
    
    
    public int insertCallHistory (String callid, String callingDN , String calledDN){
    	Utils util = new Utils();
    	String date = util.getCurrentDay().substring(0, 8);
    	String time = util.getCurrentDay().substring(8);
    	
    	int result = RESULT.RTN_EXCEPTION;
    	String sql = "insert into tb_call_hist (call_ymd, call_hms, call_type, my_dn, ani, bigo, call_id) "
    			+ "values('" + date + "' , '" +time+ "' , '"+"IN"+"' , '" +calledDN+ "' , '" + callingDN + "' , '" +""+"'  , '"+callid+"')";
    	if(executeQuery(sql, false) == RESULT.RTN_SUCCESS){
    		m_Log.standLog("", "insertCallHistory", "SUCCESS!! Call History Information !! Query[" + sql + "]");

//    		System.out.println("## SUCCESS Call History -> " + sql);
    		sql = "insert into tb_call_hist (call_ymd, call_hms, call_type, my_dn, ani, bigo, call_id) "
    				+ "values('" + date + "' , '" +time+ "' , 'OB' , '" +callingDN+ "' , '" + calledDN + "' , '" +""+"'  , '"+callid+"')";
    		result = executeQuery(sql, false);
    		if(result == RESULT.RTN_SUCCESS){
//    			System.out.println("## SUCCESS Call History -> " + sql);
    			m_Log.standLog("", "insertCallHistory", "SUCCESS!! Call History Information !! Query[" + sql + "]");
    		}
    	}
    	
    	
    	if(result != RESULT.RTN_SUCCESS) {
    		m_Log.standLog("", "insertCallHistory", "FAIL!! Call History Information !! Query[" + sql + "]");
    	}
    	return result;
    }
    

    public int insertCallingHistory (String callid, String callingDN , String calledDN){
    	Utils util = new Utils();
    	String date = util.getCurrentDay().substring(0, 8);
    	String time = util.getCurrentDay().substring(8);
    	
    	int result = RESULT.RTN_EXCEPTION;
    	String sql = "insert into tb_call_hist (call_ymd, call_hms, call_type, my_dn, ani, bigo, call_id) "
    			+ "values('" + date + "' , '" +time+ "' , '"+"OB"+"' , '" +callingDN+ "' , '" + calledDN + "' , '" +""+"' , '"+callid+"')";
    	
    	
    	if(executeQuery(sql, false) == RESULT.RTN_SUCCESS){
    		m_Log.standLog("", "insertCallHistory", "SUCCESS!! Call insertCallingHistory Information !! Query[" + sql + "]");
//    		System.out.println("## DB insertCallingHistory SUCCESS ## -> " + sql);
    	} else {
    		m_Log.standLog("", "insertCallHistory", "FAIL!! Call insertCallingHistory Information !! Query[" + sql + "]");
//    		System.out.println("## DB insertCallingHistory FAIL ## -> " + sql);
    	}
    	return result;
    }
    
    public int insertCalledHistory (String callid, String callingDN , String calledDN){
    	Utils util = new Utils();
    	String date = util.getCurrentDay().substring(0, 8);
    	String time = util.getCurrentDay().substring(8);
    	
    	int result = RESULT.RTN_EXCEPTION;
    	String sql = "insert into tb_call_hist (call_ymd, call_hms, call_type, my_dn, ani, bigo, call_id) "
    			+ "values('" + date + "' , '" +time+ "' , '"+"IN"+"' , '" +calledDN+ "' , '" + callingDN + "' , '" +""+"'  , '"+callid+"')";

    	
    	if(executeQuery(sql, false) == RESULT.RTN_SUCCESS){
    		m_Log.standLog("", "insertCalledHistory", "SUCCESS!! Call insertCalledHistory Information !! Query[" + sql + "]");
//        	System.out.println("## DB insertCalledHistory  SUCCESS ## -> " + sql);
    	} else {
    		m_Log.standLog("", "insertCalledHistory", "FAIL!! Call insertCalledHistory Information !! Query[" + sql + "]");
//    		System.out.println("## DB insertCalledHistory  FAIL ## -> " + sql);
    	}
    	return result;
    }
    
    public int updateUAHistory (String callid, String callingDN , String calledDN){
    	Utils util = new Utils();
    	String date = util.getCurrentDay().substring(0, 8);
    	String time = util.getCurrentDay().substring(8);
    	
    	int result = RESULT.RTN_EXCEPTION;
    	
    	String strMsg = "";
    	if(calledDN != null && !calledDN.isEmpty()){
    		strMsg = "부재중 통화 ("+calledDN+") 당겨받음";
    	}
    	
    	String sql = "UPDATE tb_call_hist SET bigo='"+strMsg+"' WHERE call_id='"+callid+"' AND call_type='UA'";
    	
    	if(executeQuery(sql, false) == RESULT.RTN_SUCCESS){
    		m_Log.standLog("", "updateUAHistory", "SUCCESS!! Call updateUAHistory Information !! Query[" + sql + "]");
//    		System.out.println("## DB updateUAHistory SUCCESS ## -> " + sql);
    	} else {
    		m_Log.standLog("", "updateUAHistory", "FAIL!! Call updateUAHistory Information !! Query[" + sql + "]");
//    		System.out.println("## DB updateUAHistory FAIL ## -> " + sql);
    	}
    	return result;
    }
    
    
    public int insertPickUPCalledHistory (String callid, String callingDN , String calledDN, String pickUPDN){
    	Utils util = new Utils();
    	String date = util.getCurrentDay().substring(0, 8);
    	String time = util.getCurrentDay().substring(8);
    	
    	int result = RESULT.RTN_EXCEPTION;
    	
    	String msg = "";
    	if(pickUPDN != null && !pickUPDN.isEmpty()){
    		msg = "당겨받은 콜 ("+pickUPDN+")";
    	}
    	String sql = "insert into tb_call_hist (call_ymd, call_hms, call_type, my_dn, ani, bigo,call_id) "
    			+ "values('" + date + "' , '" +time+ "' , '"+"IN"+"' , '" +calledDN+ "' , '" + callingDN + "' , '" + msg + "' , '"+callid+"')";

    	
    	if(executeQuery(sql, false) == RESULT.RTN_SUCCESS){
    		m_Log.standLog("", "insertPickUPCalledHistory", "SUCCESS!! Call insertPickUPCalledHistory Information !! Query[" + sql + "]");
//        	System.out.println("## DB insertPickUPCalledHistory SUCCESS ## -> " + sql);
    	} else {
    		m_Log.standLog("", "insertPickUPCalledHistory", "FAIL!! Call insertPickUPCalledHistory Information !! Query[" + sql + "]");
//    		System.out.println("## DB insertPickUPCalledHistory FAIL ## -> " + sql);
    	}
    	return result;
    }
    
    
    public int insertCallHistory (CallStateVO call){
    	
    	Utils util = new Utils();
    	String date = util.getCurrentDay().substring(0, 8);
    	String time = util.getCurrentDay().substring(8);
    	String [] callType = new String[2];
    	String [] myDn = new String [2];
    	String [] ani = new String[2];
    	
    	switch (call.getCallstate()) {
    	
		case CALLSTATE.ALERTING_ING:
			callType[0] = "UA";
			myDn[0] = call.getCalledDN();
			ani[0] = call.getCallingDN();
			callType[1] = "OB";
			myDn[1] = ani[0];
			ani[1] = myDn[0];
			break;
			
		case CALLSTATE.ESTABLISHED_ING:
			callType[0] = "IN";
			myDn[0] = call.getCalledDN();
			ani[0] = call.getCallingDN();
			callType[1] = "OB";
			myDn[1] = ani[0];
			ani[1] = myDn[0];
			break;
			
		default:
			break;
		}
    	
    	int result = RESULT.RTN_EXCEPTION;
    	String sql = "insert into tb_call_hist (call_ymd, call_hms, call_type, my_dn, ani, bigo) "
    			+ "values('" + date + "' , '" +time+ "' , '"+callType[0]+"' , '" +myDn[0]+ "' , '" + ani[0] + "')";
    	if(executeQuery(sql, false) == RESULT.RTN_SUCCESS){
    		m_Log.standLog("", "insertCallHistory", "SUCCESS!! Call History Information !! Query[" + sql + "]");
//    		System.out.println("## SUCCESS Call History -> " + sql);
    		sql = "insert into tb_call_hist (call_ymd, call_hms, call_type, my_dn, ani, bigo) "
    				+ "values('" + date + "' , '" +time+ "' , '"+callType[1]+"' , '" +myDn[1]+ "' , '" + ani[1] + "')";
    		result = executeQuery(sql, false);
    		if(result == RESULT.RTN_SUCCESS){
//    			System.out.println("## SUCCESS Call History -> " + sql);
    			m_Log.standLog("", "insertCallHistory", "SUCCESS!! Call History Information !! Query[" + sql + "]");
    		}
    	}
    	if(result != RESULT.RTN_SUCCESS) {
    		m_Log.standLog("", "insertCallHistory", "FAIL!! Call History Information !! Query[" + sql + "]");
    	}
    	return result;
    }
    
    
    public int insertTestData (TestEmployeeVO vo){
    	Utils util = new Utils();
    	String date = util.getCurrentDay().substring(0, 8);
    	String time = util.getCurrentDay().substring(8);
    	
    	int result = RESULT.RTN_EXCEPTION;
    	String sql = "insert into itss_userinfo "
    			+ "(userid, username, maindeptname, deptname, userrank, hp, email, dn, maindeptcode, deptcode, userrole) values('"
    			+ vo.getUserid() + "' , '"
    			+ vo.getUsername() + "' , '"
    			+ vo.getMaindeptname() + "' , '"
    			+ vo.getDeptname() + "' , '"
    			+ vo.getUserrank() + "' , '"
    			+ vo.getHp() + "' , '"
    			+ vo.getEmail() + "' , '"
    			+ vo.getDn() + "' , '"
    			+ vo.getTargetModel() + "' , '"
    			+ vo.getTargetIP() + "' , '"
    			+ vo.getTartgetDN() + "')";
    			
    	if(executeQuery(sql, false) == RESULT.RTN_SUCCESS){
    		m_Log.standLog("", "insertCallHistory", "SUCCESS!! Call History Information !! Query[" + sql + "]");
//    		System.out.println("## SUCCESS Call History -> " + sql);
    	} else {
    		m_Log.standLog("", "insertCallHistory", "FAIL!! Call History Information !! Query[" + sql + "]");
    	}
    	return result;
    }
    
    public List selectTestData(){
    	
    	List list = new ArrayList();
    	String sql = "select * from itss_userinfo where userid like 'test%'";
    	try {
    		
    		ResultSet rs = selectQuery(sql, true);
    		
    		while(rs.next()){
    			TestCallVO vo = new TestCallVO();
    			vo.setCalledDN(rs.getString("userrole"));
    			vo.setCallingDN(rs.getString("dn"));
    			vo.setDivision(rs.getString("maindeptname"));
    			vo.setEmail(rs.getString("email"));
    			vo.setName(rs.getString("username"));
    			vo.setPhoneNum(rs.getString("hp"));
    			vo.setTargetIP(rs.getString("deptcode"));
    			vo.setTargetModel(rs.getString("maindeptcode"));
    			vo.setTeam(rs.getString("deptname"));
    			
    			list.add(vo);
    		}
    	}catch(Exception e){
    		
    	}
		return list;
    	
    }
    
}
