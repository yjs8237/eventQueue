package com.test.sync;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DBConnection {
	  private Connection              m_conn      = null;
	    private Statement               m_stmt      = null;
	    private ResultSet               m_rs        = null;
	    private String                  m_url       = null;
	    private String					m_driver	= null;
	    private String                  m_user      = null;
	    private String                  m_pwd       = null;
	    private boolean                 m_binit     = false;
	    private StringWriter			sw			= null;
	    private PrintWriter				pw			= null;
	    private String		invokeName;
	    CallableStatement cs;
	    
	    public DBConnection(String driver , String url , String user , String password) {
	    	this.m_driver = driver;
	    	this.m_url = url;
	    	this.m_user = user;
	    	this.m_pwd = password;
	    }
	    
	    public void connectDB() {
			try {

				Class.forName(m_driver);
				
				disconnectDB();

				m_conn = DriverManager.getConnection(m_url, m_user, m_pwd);

				System.out.println("connection : " + m_conn.toString());
				
			} catch (Exception e) {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);
			}
		}
	    public int disconnectDB() {
			try {
				if(m_rs != null) {
					m_rs.close();
					m_rs = null;
				}
				
				if(m_stmt != null) {
					m_stmt.close();
					m_stmt = null;
				}
				
				if (m_conn != null) {
					m_conn.close();
					m_conn = null;
				}
				
				return 0;
			} catch (Exception e) {
				m_conn = null;
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);
				disconnectDB();
				return -1;
			}
		}
	    public int executeQuery(String aQuery, boolean bClose) {
	        int rowcnt = 0;
	        try {

	            m_stmt = m_conn.createStatement();
	            
	            if (m_stmt.executeUpdate(aQuery) == 0) {
	                return -1;
	            }
	            
	            m_stmt.close();
	            
	            return 0;
	        } catch (Exception e) {
	            return -1;
	        }
	    }
	    
	    public ResultSet selectQuery(String aQuery, boolean bClose) {
	        
	        try {

	            m_stmt = m_conn.createStatement();
	            m_rs = m_stmt.executeQuery(aQuery);
	            
//	            m_stmt.close();
	            
	            return m_rs;
	        } catch (Exception e) {
	        	e.printStackTrace();
	            return null;
	        }
	      
	    }
}
