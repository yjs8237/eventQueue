package com.isi.utils;

import com.isi.vo.EmployeeVO;

public class Common {
	
	public static boolean compareEmployee (EmployeeVO empVO_before, EmployeeVO empVO_after) {
		// TODO Auto-generated method stub
		if(!empVO_before.getEmp_id().equals(empVO_after.getEmp_id())) {
			return false;
		}
		if(!empVO_before.getEmp_lno().equals(empVO_after.getEmp_lno())) {
			return false;
		}
		if(!empVO_before.getEmp_nm_kor().equals(empVO_after.getEmp_nm_kor())) {
			return false;
		}
		if(!empVO_before.getOrg_nm().equals(empVO_after.getOrg_nm())) {
			return false;
		}
		if(!empVO_before.getPos_nm().equals(empVO_after.getPos_nm())) {
			return false;
		}
		if(!empVO_before.getExtension().equals(empVO_after.getExtension())) {
			return false;
		}
		if(!empVO_before.getCell_no().equals(empVO_after.getCell_no())) {
			return false;
		}
		if(!empVO_before.getBuilding().equals(empVO_after.getBuilding())) {
			return false;
		}
		if(!empVO_before.getFloor().equals(empVO_after.getFloor())) {
			return false;
		}
		return true;
	}
}
