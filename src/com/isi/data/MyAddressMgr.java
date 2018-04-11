package com.isi.data;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.isi.constans.LOGLEVEL;
import com.isi.constans.LOGTYPE;
import com.isi.constans.PROPERTIES;
import com.isi.db.JDatabase;
import com.isi.file.LogMgr;
import com.isi.file.PropertyRead;
import com.isi.handler.HttpSyncServer;
import com.isi.vo.EmployeeVO;

public class MyAddressMgr {
	
	
	private LogMgr 			m_Log;
	
	private Connection conn;
	private Statement               m_stmt      = null;
    private ResultSet               m_rs        = null;
	private StringWriter sw;
	private PrintWriter pw;
	
	public MyAddressMgr(Connection conn){
		m_Log = LogMgr.getInstance();
		this.conn = conn; 
		PropertyRead pr = PropertyRead.getInstance();
	}
	
	public EmployeeVO getMyAddressInfo(String emp_id ,String number , String callID) {
		
		EmployeeVO empVO = null;
		
		StringBuffer query = new StringBuffer();
		query.append("SELECT * FROM tb_address_book WHERE emp_id = '").append(emp_id).append("'");
		query.append(" AND ( REPLACE(addr_tel , '-' , '') = '").append(number).append("' OR REPLACE(addr_cell, '-' , '') = '").append(number).append("')");
		
		m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, callID, "getMyAddressInfo", "Search My Address Book SQL [" + query.toString() + "]");
		try {
			
			m_stmt = conn.createStatement();
	        m_rs = m_stmt.executeQuery(query.toString());
	            
			
			if(m_rs != null) {
				String aniNum 	= "";
				String name		= "";
				String position	= "";
				String division = "";
				String floor	= "";
				while(m_rs.next()) {
					name = m_rs.getString("addr_name");
					position = m_rs.getString("addr_position");
					division = m_rs.getString("addr_company");
					floor = "";
					empVO = new EmployeeVO();
					empVO.setEmp_nm_kor(name);
					empVO.setPos_nm(position);
					empVO.setOrg_nm(division);
					empVO.setFloor(floor);
				}
				
				
				m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, callID, "getMyAddressInfo", "My Address Book Info name[" + name + "] position[" + position + "] division[" + division +"]");
				
			} else {
				m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, callID, "getMyAddressInfo", "## ResultSet is null ##");
			}
			
		} catch (Exception e) {
			sw = new StringWriter();
			pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.ERR_LOG, callID, "getMyAddressInfo",  sw.toString());
		}
		return empVO;
	}
	
	public int updateDeviceIpAddr(String mac_address, String device_ipaddr , String callID) {
		String device_type = "";
		StringBuffer query = new StringBuffer();
		
		query.append(" UPDATE tb_device_info SET device_ipaddr = '").append(device_ipaddr).append("' WHERE mac_address = '").append(mac_address).append("'");
		
		m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, callID, "updateDeviceIpAddr", "updateDeviceIpAddr SQL [" + query.toString() + "]");
		
		try {
			
			m_stmt = conn.createStatement();
	        m_stmt.executeUpdate(query.toString());
	        
		} catch (Exception e) {
			sw = new StringWriter();
			pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.ERR_LOG, callID, "updateDeviceIpAddr",  sw.toString());
		}
		
		
		return 0;
	}
	
	
	public String getDeviceType (String mac_address , String callID) {
		String device_type = "";
		StringBuffer query = new StringBuffer();
		
		query.append(" SELECT device_type FROM tb_device_info WHERE mac_address = '").append(mac_address).append("'");
		
		m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, callID, "getDeviceType", "getDeviceType SQL [" + query.toString() + "]");
		
	try {
			
			m_stmt = conn.createStatement();
	        m_rs = m_stmt.executeQuery(query.toString());
			
			if(m_rs != null) {
				while(m_rs.next()) {
					device_type = m_rs.getString("device_type");
				}
				m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, callID, "getDeviceType", "SELECT DEVICE TYPE " + device_type);
				
			} else {
				m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, callID, "getDeviceType", "## ResultSet is null ##");
			}
			
		} catch (Exception e) {
			sw = new StringWriter();
			pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.ERR_LOG, callID, "getDeviceType",  sw.toString());
		}
		return device_type;
		
	}

	public ArrayList<EmployeeVO> getLoginUserList(String emp_id , String callID) {
		
		ArrayList<EmployeeVO> loginUserList = new ArrayList<>();
		EmployeeVO empVO = null;
		
		StringBuffer query = new StringBuffer();
		
		query.append(" SELECT ");
		query.append(" SUB.extension , MST.cell_no , MST.emp_id , MST.emp_nm_kor, ");
		query.append(" MST.emp_nm_eng , MST.org_nm , MST.pos_nm , MST.duty_nm, ");
		query.append(" MST.email , MST.emp_stat_nm , MST.emp_div_cd_nm , MST.emp_lno, ");
		query.append(" MST.building , MST.floor , MST.cm_ver , MST.cm_ip, ");
		query.append(" MST.cm_user , MST.cm_pwd , MST.popup_svc_yn , SUB.mac_address, ");
		query.append(" SUB.device_type , SUB.device_ipaddr ");
		query.append(" FROM ");
		query.append(" ( SELECT * FROM tb_emp_info WHERE emp_id='").append(emp_id).append("' )");
		query.append(" MST LEFT OUTER JOIN ( ");
		query.append(" SELECT ED.emp_id , ED.extension , DD.mac_address , DI.device_type , DI.device_ipaddr ");
		query.append(" FROM tb_emp_dn_info ED , tb_dn_device DD , tb_device_info DI ");
		query.append(" WHERE ED.extension = DD.extension AND ED.mac_address = DD.mac_address AND DD.mac_address = DI.mac_address ");
		query.append(" ) SUB ON MST.emp_id = SUB.emp_id ");
		
		
		
		m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, callID, "getLoginUserList", "Search LoginUserList SQL [" + query.toString() + "]");
		try {
			
			m_stmt = conn.createStatement();
	        m_rs = m_stmt.executeQuery(query.toString());
			
			if(m_rs != null) {
				while(m_rs.next()) {
					empVO = new EmployeeVO();
					query.append(" SUB.extension , MST.cell_no , MST.emp_id , MST.emp_nm_kor, ");
					query.append(" MST.emp_nm_eng , MST.org_nm , MST.pos_nm , MST.duty_nm, ");
					query.append(" MST.email , MST.emp_stat_nm , MST.emp_div_cd_nm , MST.emp_lno, ");
					query.append(" MST.building , MST.floor , MST.cm_ver , MST.cm_ip, ");
					query.append(" MST.cm_user , MST.cm_pwd , MST.popup_svc_yn , SUB.mac_address, ");
					query.append(" SUB.device_type , SUB.device_ipaddr ");
					
					
					empVO.setEmp_id(m_rs.getString("emp_id"));
					empVO.setEmp_nm_kor(m_rs.getString("emp_nm_kor"));
					empVO.setEmp_nm_eng(m_rs.getString("emp_nm_eng"));
					empVO.setOrg_nm(m_rs.getString("org_nm"));
					empVO.setPos_nm(m_rs.getString("pos_nm"));
					empVO.setExtension(m_rs.getString("extension"));
					empVO.setCell_no(m_rs.getString("cell_no"));
					empVO.setDuty_nm(m_rs.getString("duty_nm"));
					empVO.setEmail(m_rs.getString("email"));
					empVO.setEmp_stat_nm(m_rs.getString("emp_stat_nm"));
					empVO.setEmp_div_cd_nm(m_rs.getString("emp_div_cd_nm"));
					empVO.setEmp_lno(m_rs.getString("emp_lno"));
					empVO.setBuilding(m_rs.getString("building"));
					empVO.setFloor(m_rs.getString("floor"));
					empVO.setCm_ver(m_rs.getString("cm_ver"));
					empVO.setCm_ip(m_rs.getString("cm_ip"));
					empVO.setCm_user(m_rs.getString("cm_user"));
					empVO.setCm_pwd(m_rs.getString("cm_pwd"));
					empVO.setPopup_svc_yn(m_rs.getString("popup_svc_yn"));
					empVO.setMac_address(m_rs.getString("mac_address"));
					empVO.setDevice_type(m_rs.getString("device_type"));
					empVO.setDevice_ipaddr(m_rs.getString("device_ipaddr"));
					loginUserList.add(empVO);
					
				}
				
				m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, callID, "getLoginUserList", "SELECT LOGIN USER INFO emp_id[" + emp_id + "] Login Size : " + loginUserList.size());
				
			} else {
				m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, callID, "getLoginUserList", "## ResultSet is null ##");
			}
			
		} catch (Exception e) {
			sw = new StringWriter();
			pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.ERR_LOG, callID, "getLoginUserList",  sw.toString());
		}
		return loginUserList;
	}
	

	
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		
		
		return sb.toString();
	}
	
	
	
}
