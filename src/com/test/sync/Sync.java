package com.test.sync;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Sync {
	
	
	    
	    
	    public Sync() {
	    	
	    }
	    
	    public void startPickUpGroupSync () {
	    	
	    	String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	    	String url = "jdbc:sqlserver://10.156.115.205:1433;DatabaseName=SamilCafe";
	    	String user = "cafe";
	    	String password = "cafe2413";
	    	DBConnection cafe_database = new DBConnection(driver, url, user, password);
	    	cafe_database.connectDB();
	    	
	    	StringBuffer buffer = new StringBuffer();
	    	
	    	ArrayList<PickupGroupVO> cafe_list = new ArrayList<>();
	    	ArrayList<PickupGroupVO> isup_list = new ArrayList<>();
	    	
	    	Map <String,PickupGroupVO> cafe_map = new HashMap<>();
	    	Map <String,PickupGroupVO> isup_map = new HashMap<>();
	    	
	    	try {
	    		
	    		buffer.append("select MST.USERID , MST.NAME , MST.PICKUP_GROUP_ID , SUB.PKID , SUB.NAME , SUB.DESCRIPTION");
		    	buffer.append(" FROM ");
		    	buffer.append("( select * from UCUSER ) MST LEFT OUTER JOIN ( select * from PICKUP_GROUP )");
		    	buffer.append(" SUB ON MST.PICKUP_GROUP_ID = SUB.ID ");
		    	buffer.append("WHERE SUB.PKID is not null");
		    	
		    	ResultSet result = cafe_database.selectQuery(buffer.toString(), true);
		    	if(result != null) {
		    		
		    		while(result.next()) {
		    			PickupGroupVO cafe_vo = new PickupGroupVO();
		    			cafe_vo.setEmp_id(result.getString("USERID"));
		    			cafe_vo.setPkid(result.getString("PKID"));
		    			cafe_vo.setName(result.getString("NAME"));
		    			cafe_map.put(cafe_vo.getEmp_id(), cafe_vo);
		    			
//		    			System.out.println(cafe_vo.getEmp_id() + " , " + cafe_vo.getName() + " , " + cafe_vo.getPkid());
		    			
		    		}
		    	}
		    	
		    	cafe_database.disconnectDB();
		    	driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
		    	url = "jdbc:sqlserver://10.156.114.205:1433;DatabaseName=PHONE";
		    	user = "SAC_IPT";
		    	password = "dkdlvlxl123$";
		    	cafe_database = new DBConnection(driver, url, user, password);
		    	cafe_database.connectDB();
		    	
		    	buffer = new StringBuffer();
		    	buffer.append("SELECT emp_id , pick_pkid   FROM tb_emp_info");
		    	
		    	result = cafe_database.selectQuery(buffer.toString(), false);
		    	
		    	if(result != null) {
		    		while(result.next()) {
		    			PickupGroupVO cafe_vo = new PickupGroupVO();
		    			cafe_vo.setEmp_id(result.getString("emp_id"));
		    			cafe_vo.setPkid(result.getString("pick_pkid"));
		    			isup_map.put(cafe_vo.getEmp_id(), cafe_vo);
		    			
		    		}
		    	}
		    	
		    	Set keySet = cafe_map.keySet();
		    	Iterator iter = keySet.iterator();
		    	
		    	
		    	
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    	}
	    	
	    	
	    }
	    
	
}
