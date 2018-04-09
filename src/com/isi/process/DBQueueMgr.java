package com.isi.process;

import java.util.Map;

import com.isi.constans.APITYPE;
import com.isi.file.PropertyRead;
import com.isi.vo.EmployeeVO;
import com.isi.vo.PopupSvcVO;

public class DBQueueMgr {
	
	private static DBQueueMgr instance = new DBQueueMgr(); 
	
	private DBQueueMgr() {}
    public static DBQueueMgr getInstance () {
		if(instance == null){
			instance = new DBQueueMgr();
		}
		
		return instance;
    }
	
    
    public void addHttpData(int API_TYPE , Map paramMap, String returnJson) {
    	
    	try {
    		StringBuffer query = new StringBuffer();
    		
    		query.append(" INSERT INTO tb_httpinf_log (");
    		
    		switch (API_TYPE) {
			case APITYPE.API_LOGIN:
				query.append("datetime , api_type , emp_id , emp_lno , emp_nm_kor , emp_nm_eng , org_nm ,");
				query.append("pos_nm , duty_nm , extension , email , cell_no , building , floor ,");
				query.append("emp_stat_nm , emp_div_cd_nm , popup_svc_yn , mac_address , device_ipaddr , device_type , cm_ver ,");
				query.append("cm_ip , cm_user , cm_pwd ");
				
				query.append(") VALUES (");
				query.append("GETDATE() , '"+paramMap.get("api_type")+"' , '" + paramMap.get("emp_id") + "' ,");
				query.append("'"+paramMap.get("emp_lno")+"',");
				query.append("'"+paramMap.get("emp_nm_kor")+"',");
				query.append("'"+paramMap.get("emp_nm_eng")+"',");
				query.append("'"+paramMap.get("org_nm")+"',");
				query.append("'"+paramMap.get("pos_nm")+"',");
				query.append("'"+paramMap.get("duty_nm")+"',");
				query.append("'"+paramMap.get("extension")+"',");
				query.append("'"+paramMap.get("email")+"',");
				query.append("'"+paramMap.get("cell_no")+"',");
				query.append("'"+paramMap.get("building")+"',");
				query.append("'"+paramMap.get("floor")+"',");
				query.append("'"+paramMap.get("emp_stat_nm")+"',");
				query.append("'"+paramMap.get("emp_div_cd_nm")+"',");
				query.append("'"+paramMap.get("popup_svc_yn")+"',");
				query.append("'"+paramMap.get("mac_address")+"',");
				query.append("'"+paramMap.get("device_ipaddr")+"',");
				query.append("'"+paramMap.get("device_type")+"',");
				query.append("'"+paramMap.get("cm_ver")+"',");
				query.append("'"+paramMap.get("cm_ip")+"',");
				query.append("'"+paramMap.get("cm_user")+"',");
				query.append("'"+paramMap.get("cm_pwd")+"'");
				query.append(")");
				break;
			
			case APITYPE.API_LOGOUT:
				
				break;
				
			case APITYPE.API_CALLSTATUS:
				
				break;
				
			case APITYPE.API_DEVICERESET:
				
				break;
				
			case APITYPE.API_PICKUP:
				
				break;
			default:
				break;
			}
    		
//    		DBHttpQueue.getInstance().put();
    		
    	} catch (Exception e) {
    		System.out.println(e.toString());
    		e.getStackTrace();
    	}
    }
    
    public void addPopUpData(String calling_num , String called_num , String popup_yn , EmployeeVO vo , String targetIP , String description)  {
    	try {
    		
    		//System.out.println("DBQueueMgr addQData  : " + vo.toString());
    		vo.setCalling_num(calling_num);
    		vo.setCalled_num(called_num);
    		vo.setPopup_yn(popup_yn);
    		vo.setDescription(description);
    		vo.setTargetIP(targetIP);
        	DBPopUpQueue.getInstance().put(vo);
    	} catch (Exception e) {
    		System.out.println(e.toString());
    		e.getStackTrace();
    	}
    	
    }
    
}
