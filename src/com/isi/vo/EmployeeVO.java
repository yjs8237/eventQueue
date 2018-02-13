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
	
	private String emp_id;
	private String emp_nm_kor;
	private String emp_nm_eng;
	
	private String org_nm;
	private String pos_nm;
	private String duty_nm;
	private String email;
	private String cell_no;
	private String emp_stat_nm;
	private String emp_div_cd_nm;
	private String emp_lno;
	private String building;
	private String floor;
	private String popup_svc_yn;
	private String cm_ver;
	private String cm_ip;
	private String cm_user;
	private String cm_pwd;
	private String mac_address;
	private String device_ipaddr;
	private String extension;
	private String device_type;
	

	public EmployeeVO (){
		
	}


	public String getEmp_id() {
		return emp_id;
	}



	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}



	public String getEmp_nm_kor() {
		return emp_nm_kor;
	}



	public void setEmp_nm_kor(String emp_nm_kor) {
		this.emp_nm_kor = emp_nm_kor;
	}



	public String getEmp_nm_eng() {
		return emp_nm_eng;
	}



	public void setEmp_nm_eng(String emp_nm_eng) {
		this.emp_nm_eng = emp_nm_eng;
	}


	public String getOrg_nm() {
		return org_nm;
	}



	public void setOrg_nm(String org_nm) {
		this.org_nm = org_nm;
	}



	public String getPos_nm() {
		return pos_nm;
	}



	public void setPos_nm(String pos_nm) {
		this.pos_nm = pos_nm;
	}



	public String getDuty_nm() {
		return duty_nm;
	}



	public void setDuty_nm(String duty_nm) {
		this.duty_nm = duty_nm;
	}



	public String getEmail() {
		return email;
	}



	public void setEmail(String email) {
		this.email = email;
	}



	public String getCell_no() {
		return cell_no;
	}



	public void setCell_no(String cell_no) {
		this.cell_no = cell_no;
	}



	public String getEmp_stat_nm() {
		return emp_stat_nm;
	}



	public void setEmp_stat_nm(String emp_stat_nm) {
		this.emp_stat_nm = emp_stat_nm;
	}



	public String getEmp_div_cd_nm() {
		return emp_div_cd_nm;
	}



	public void setEmp_div_cd_nm(String emp_div_cd_nm) {
		this.emp_div_cd_nm = emp_div_cd_nm;
	}






	public String getEmp_lno() {
		return emp_lno;
	}



	public void setEmp_lno(String emp_lno) {
		this.emp_lno = emp_lno;
	}



	public String getBuilding() {
		return building;
	}



	public void setBuilding(String building) {
		this.building = building;
	}



	public String getFloor() {
		return floor;
	}



	public void setFloor(String floor) {
		this.floor = floor;
	}



	public String getPopup_svc_yn() {
		return popup_svc_yn;
	}



	public void setPopup_svc_yn(String popup_svc_yn) {
		this.popup_svc_yn = popup_svc_yn;
	}



	public String getCm_ver() {
		return cm_ver;
	}



	public void setCm_ver(String cm_ver) {
		this.cm_ver = cm_ver;
	}



	public String getCm_ip() {
		return cm_ip;
	}



	public void setCm_ip(String cm_ip) {
		this.cm_ip = cm_ip;
	}



	public String getCm_user() {
		return cm_user;
	}



	public void setCm_user(String cm_user) {
		this.cm_user = cm_user;
	}



	public String getCm_pwd() {
		return cm_pwd;
	}



	public void setCm_pwd(String cm_pwd) {
		this.cm_pwd = cm_pwd;
	}



	public String getMac_address() {
		return mac_address;
	}



	public void setMac_address(String mac_address) {
		this.mac_address = mac_address;
	}



	public String getDevice_ipaddr() {
		return device_ipaddr;
	}



	public void setDevice_ipaddr(String device_ipaddr) {
		this.device_ipaddr = device_ipaddr;
	}



	public String getExtension() {
		return extension;
	}



	public void setExtension(String extension) {
		this.extension = extension;
	}



	public String getDevice_type() {
		return device_type;
	}



	public void setDevice_type(String device_type) {
		this.device_type = device_type;
	}





	public String toString() {
		
		StringBuffer sb = new StringBuffer();
		sb.append("emp_id [").append(emp_id).append("] ");
		sb.append("emp_nm_kor [").append(emp_nm_kor).append("] ");
		sb.append("emp_nm_eng [").append(emp_nm_eng).append("] ");
		
		sb.append("org_nm [").append(org_nm).append("] ");
		sb.append("pos_nm [").append(pos_nm).append("] ");
		sb.append("duty_nm [").append(duty_nm).append("] ");
		sb.append("email [").append(email).append("] ");
		
		sb.append("cell_no [").append(cell_no).append("] ");
		sb.append("emp_stat_nm [").append(emp_stat_nm).append("] ");
		sb.append("emp_div_cd_nm [").append(emp_div_cd_nm).append("] ");
		
		sb.append("emp_lno [").append(emp_lno).append("] ");
		sb.append("building [").append(building).append("] ");
		sb.append("floor [").append(floor).append("] ");
		sb.append("popup_svc_yn [").append(popup_svc_yn).append("] ");
		sb.append("cm_ver [").append(cm_ver).append("] ");
		sb.append("cm_ip [").append(cm_ip).append("] ");
		sb.append("cm_user [").append(cm_user).append("] ");
		sb.append("cm_pwd [").append(cm_pwd).append("] ");
		sb.append("mac_address [").append(mac_address).append("] ");
		sb.append("device_type [").append(device_type).append("] ");
		sb.append("device_ipaddr [").append(device_ipaddr).append("] ");
		sb.append("extension [").append(extension).append("] ");
		
		return sb.toString();
		
	}


}
