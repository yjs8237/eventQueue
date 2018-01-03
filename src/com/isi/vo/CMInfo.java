package com.isi.vo;

public class CMInfo {
	
	private static CMInfo cmInfo = new CMInfo(); 
	private String cmUser;
	private String cmPassword;
	
	 public static CMInfo getInstance () {
			if(cmInfo == null){
				cmInfo = new CMInfo();
			}
			return cmInfo;
	 }
	 
	 
	 public void setCmUser(String cmUser) {
		 this.cmUser = cmUser;
	 }
	 
	 public String getCmUser() {
		 return this.cmUser;
	 }
	 
	 public void setCmPassword(String cmPassword) {
		 this.cmPassword = cmPassword;
	 }
	 
	 public String getCmPassword() {
		 return this.cmPassword;
	 }
	 
	    
}
