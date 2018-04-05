package com.isi.data;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
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
	
	
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		
		
		return sb.toString();
	}
	
	
	
}
