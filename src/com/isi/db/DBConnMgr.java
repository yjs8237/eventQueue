package com.isi.db;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.isi.constans.PROPERTIES;
import com.isi.constans.RESULT;
import com.isi.data.MyAddressMgr;
import com.isi.file.GLogWriter;
import com.isi.file.ILog;
import com.isi.file.LogMgr;
import com.isi.file.PropertyRead;

public class DBConnMgr {
	
	private static DBConnMgr dbConMgr = new DBConnMgr();
	
	private static final int INITIAL_CNT = 10;
	private static final int ADD_CNT = 2;
	
	 private Connection              m_conn      = null;
	 private Statement               m_stmt      = null;
	 private ResultSet               m_rs        = null;
	
	private List<Connection> idle_conn_list = new ArrayList<>();
	private List<Connection> using_conn_list = new ArrayList<>();
	
	
	private ILog logwrite;
	private String db_class;
	private String db_url;
	private String db_user;
	private String db_pwd;
	
	private DBConnMgr(){
		logwrite = new GLogWriter();
		PropertyRead pr = PropertyRead.getInstance();
	}
	public synchronized static DBConnMgr getInstance(){
		if(dbConMgr == null){
			dbConMgr = new DBConnMgr();
		}
		return dbConMgr;
	}
	
	
	
	public void initialConnection() {
		
		for (int i = 0; i < INITIAL_CNT; i++) {
			idle_conn_list.add(makeConnection());
		}
		logwrite.databaseLog("", "initialConnection", "최초 Connection Size : " + idle_conn_list.size());
	}
	
	public  Connection getConnection(String callID) {
		Connection conn = null;
		synchronized (idle_conn_list) {
			if(idle_conn_list.size() == 0) {
				for (int i = 0; i < ADD_CNT; i++) {
					idle_conn_list.add(makeConnection());
					logwrite.databaseLog("", "initialConnection", "Make Connection");
//					System.out.println("Make Connection");
				}
				
			}
			conn = idle_conn_list.remove(0);
		}
		logwrite.databaseLog(callID, "getConnection", "Connection 획득 현재 Connection size : " + idle_conn_list.size());
//		System.out.println("Connection 획득 현재 Connection size : " + idle_conn_list.size());
		return  conn;
	}
	
	public void returnConnection(Connection conn , String callID) {
		
		try {
			if(conn == null || conn.isClosed()) {
				logwrite.databaseLog("", "returnConnection", "Connection 반납 객체 null 또는 closed");
//				System.out.println("Connection 반납 객체 null 또는 closed");
				return;
			}
			
			synchronized (idle_conn_list) {
				if(idle_conn_list.contains(conn)) {
//					System.out.println("이미 존재하는 객체");
					return;
				}
				idle_conn_list.add(conn);
			}
//			System.out.println("Connection 반납 현재 Connection size : " + idle_conn_list.size());
			logwrite.databaseLog(callID, "returnConnection", "Connection 반납 현재 Connection size : " + idle_conn_list.size());
		} catch(Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			logwrite.databaseLog(callID, "returnConnection", sw.toString());
		}
		
	}
	
	
	private Connection makeConnection() {
		
		Connection m_conn = null;
		
		try {
			Class.forName(db_class);
			m_conn = DriverManager.getConnection(db_url, db_user, db_pwd);
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			logwrite.databaseLog("", "makeConnection", sw.toString());
			System.out.println(sw.toString());
		}
		return m_conn;
	}
	
	
	public String getDb_class() {
		return db_class;
	}
	public void setDb_class(String db_class) {
		this.db_class = db_class;
	}
	public String getDb_url() {
		return db_url;
	}
	public void setDb_url(String db_url) {
		this.db_url = db_url;
	}
	public String getDb_user() {
		return db_user;
	}
	public void setDb_user(String db_user) {
		this.db_user = db_user;
	}
	public String getDb_pwd() {
		return db_pwd;
	}
	public void setDb_pwd(String db_pwd) {
		this.db_pwd = db_pwd;
	}
	
	
}
