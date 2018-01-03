/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.isi.vo;

/**
*
* @author greatyun
*/
public class EmployeeVO  implements IPerson{
	
	
	private String em_ID;				// 사번
	private String em_name;				// 이름
	private String em_position;			// 직급
	private String em_orgNm;			// 사업부
	


	private String em_groupNm;			// 부서
	private String dev_macaddress;		// 전화기 Macaddress
	private String dev_dn;				// 내선번호
	private String dev_CmIP;			// CM IP
	private String dev_deviceType;		// 전화기 모델 코드
	private String dev_ipAddr;			// 전화기 IP주소
	private String dev_popupYN;			// pop 유무
	private String dev_cmUser;			// CM Application User
	private String dev_cmPass;			// CM Application Password
	private String pic_path;			// 직원 사진 URL
	

	public EmployeeVO (){
		
	}
	
	
	
	public String getPic_path() {
		return pic_path;
	}



	public void setPic_path(String pic_path) {
		this.pic_path = pic_path;
	}


	public String getOrgNm() {
		return em_orgNm;
	}


	public EmployeeVO setOrgNm(String em_orgNm) {
		this.em_orgNm = em_orgNm;
		return this;
	}
	

	public String getCmUser() {
		return dev_cmUser;
	}

	public EmployeeVO setCmUser(String dev_cmUser) {
		this.dev_cmUser = dev_cmUser;
		return this;
	}

	public String getCmPass() {
		return dev_cmPass;
	}

	public EmployeeVO setCmPass(String dev_cmPass) {
		this.dev_cmPass = dev_cmPass;
		return this;
	}

	public String getPopupYN() {
		return dev_popupYN;
	}

	public EmployeeVO setPopupYN(String dev_popupYN) {
		this.dev_popupYN = dev_popupYN;
		return this;
	}

	public String getIpAddr() {
		return dev_ipAddr;
	}


	public EmployeeVO setIpAddr(String dev_ipAddr) {
		this.dev_ipAddr = dev_ipAddr;
		return this;
	}


	public String getDeviceType() {
		return dev_deviceType;
	}


	public EmployeeVO setDeviceType(String dev_deviceType) {
		this.dev_deviceType = dev_deviceType;
		return this;
	}

	public String getCmIP() {
		return dev_CmIP;
	}

	public EmployeeVO setCmIP(String dev_CmIP) {
		this.dev_CmIP = dev_CmIP;
		return this;
	}

	public String getMacaddress() {
		return dev_macaddress;
	}

	public EmployeeVO setMacaddress(String dev_macaddress) {
		this.dev_macaddress = dev_macaddress;
		return this;
	}

//	public String getEm_cellnum() {
//		return em_cellnum;
//	}

//	public void setEm_cellnum(String em_cellnum) {
//		this.em_cellnum = em_cellnum;
//	}



	public String getEm_ID() {
		return em_ID;
	}

	public EmployeeVO setEm_ID(String em_ID) {
		this.em_ID = em_ID;
		return this;
	}

	
	public String getEm_name() {
		return em_name;
	}

	public EmployeeVO setEm_name(String em_name) {
		this.em_name = em_name;
		return this;
	}
	

	public String getDN() {
		return dev_dn;
	}

	public EmployeeVO setDN(String dev_dn) {
		this.dev_dn = dev_dn;
		return this;
	}

	public String getEm_position() {
		return em_position;
	}

	public EmployeeVO setEm_position(String em_position) {
		this.em_position = em_position;
		return this;
	}

	public String getGroupNm() {
		return em_groupNm;
	}

	public EmployeeVO setGroupNm(String em_groupNm) {
		this.em_groupNm = em_groupNm;
		return this;
	}

	
	
	public String toString() {
		
		return "em_ID["+em_ID+"]"
				+ "em_name["+em_name+"]"
				+ "em_position["+em_position+"]"
				+ "em_groupNm["+em_groupNm+"]"
				+ "dev_macaddress["+dev_macaddress+"]"
				+ "dev_dn["+dev_dn+"]"
				+ "dev_CmIP["+dev_CmIP+"]"
				+ "dev_deviceType["+dev_deviceType+"]"
				+ "dev_ipAddr["+dev_ipAddr+"]"
				+ "dev_popupYN["+dev_popupYN+"]"
				+ "dev_cmUser["+dev_cmUser+"]"
				+ "dev_cmPass["+dev_cmPass+"]";
		
	}


}
