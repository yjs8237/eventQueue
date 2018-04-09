package com.isi.thread;

import java.util.concurrent.Callable;

import com.isi.constans.PROPERTIES;
import com.isi.constans.RESULT;
import com.isi.db.JDatabase;
import com.isi.exception.ExceptionUtil;
import com.isi.file.GLogWriter;
import com.isi.file.PropertyRead;
import com.isi.process.DBPopUpQueue;
import com.isi.process.IQueue;
import com.isi.vo.EmployeeVO;

public class DBSvcCallable implements Callable<Integer>{

	private IQueue 			queue;
	private boolean 		stopThread		= false;
	private GLogWriter		logwrite;
	private String			threadID;
	private JDatabase		dbService;
	private PropertyRead	pr;
	
	public DBSvcCallable(String threadID) {
		this.queue = DBPopUpQueue.getInstance();
		logwrite = new GLogWriter();
		dbService = new JDatabase();
		pr = PropertyRead.getInstance();
		dbService.connectDB(pr.getValue(PROPERTIES.DB_CLASS), pr.getValue(PROPERTIES.DB_URL), pr.getValue(PROPERTIES.DB_USER), pr.getValue(PROPERTIES.DB_PASSWORD));
		this.threadID = threadID;
	}
	
	@Override
	public Integer call()  {
		// TODO Auto-generated method stub
		
		while(!stopThread) {
			String callID = "";
			try{
				
				//System.out.println("start db service callable  " + threadID);
				
				EmployeeVO popupSvcVO = (EmployeeVO) queue.get();
				
				//System.out.println("callable get object  " + popupSvcVO.toString());
				
				callID = threadID + "-" + String.valueOf(System.currentTimeMillis()) + "-" + String.valueOf(Thread.currentThread().getName()) + String.valueOf(Thread.currentThread().getId())
				 + "_" + popupSvcVO.getCalling_num() + "_" + popupSvcVO.getCalled_num();
				
				String query = makeInsertQuery(popupSvcVO);
				logwrite.popupLog(callID, "call()", "QUERY : " + query);
				
				dbService.executeQuery(query, false);
				
				
			}catch(Exception e) {
				e.printStackTrace(ExceptionUtil.getPrintWriter());
				logwrite.exceptionLog(callID, "call()", ExceptionUtil.getStringWriter().toString());
			}
			
		}
		
		return RESULT.RTN_SUCCESS;
	}
	
	private String makeInsertQuery(EmployeeVO popupSvcVO) {
		StringBuffer sb = new StringBuffer();
		
		sb.append(" INSERT INTO tb_popup_log (");
		sb.append(" datetime, ");
		sb.append(" calling_num, ");
		sb.append(" called_num, ");
		sb.append(" popup_yn, ");
		sb.append(" emp_id, ");
		sb.append(" emp_lno, ");
		sb.append(" emp_nm_kor, ");
		sb.append(" org_nm, ");
		sb.append(" pos_nm, ");
		sb.append(" duty_nm, ");
		sb.append(" email, ");
		sb.append(" cell_no, ");
		sb.append(" building, ");
		sb.append(" floor, ");
		sb.append(" cm_ip, ");
		sb.append(" cm_user, ");
		sb.append(" cm_pwd, ");
		sb.append(" popup_svc_yn, ");
		sb.append(" device_ipaddr, ");
		sb.append(" description )");
		sb.append(" VALUES ( ");
		sb.append("getdate()").append(",");
		sb.append("'").append(popupSvcVO.getCalling_num() == null ? "" : popupSvcVO.getCalling_num()).append("'").append(",");
		sb.append("'").append(popupSvcVO.getCalled_num() == null ? "" : popupSvcVO.getCalled_num()).append("'").append(",");
		sb.append("'").append(popupSvcVO.getPopup_yn() == null ? "" : popupSvcVO.getPopup_yn()).append("'").append(",");
		sb.append("'").append(popupSvcVO.getEmp_id() == null ? "" : popupSvcVO.getEmp_id()).append("'").append(",");
		sb.append("'").append(popupSvcVO.getEmp_lno() == null ? "" : popupSvcVO.getEmp_lno()).append("'").append(",");
		sb.append("'").append(popupSvcVO.getEmp_nm_kor() == null ? "" : popupSvcVO.getEmp_nm_kor()).append("'").append(",");
		sb.append("'").append(popupSvcVO.getOrg_nm() == null ? "" : popupSvcVO.getOrg_nm()).append("'").append(",");
		sb.append("'").append(popupSvcVO.getPos_nm() == null ? "" : popupSvcVO.getPos_nm()).append("'").append(",");
		sb.append("'").append(popupSvcVO.getDuty_nm() == null ? "" : popupSvcVO.getDuty_nm()).append("'").append(",");
		sb.append("'").append(popupSvcVO.getEmail() == null ? "" : popupSvcVO.getEmail()).append("'").append(",");
		sb.append("'").append(popupSvcVO.getCell_no() == null ? "" : popupSvcVO.getCell_no()).append("'").append(",");
		sb.append("'").append(popupSvcVO.getBuilding() == null ? "" : popupSvcVO.getBuilding()).append("'").append(",");
		sb.append("'").append(popupSvcVO.getFloor() == null ? "" : popupSvcVO.getFloor()).append("'").append(",");
		sb.append("'").append(popupSvcVO.getCm_ip() == null ? "" : popupSvcVO.getCm_ip()).append("'").append(",");
		sb.append("'").append(popupSvcVO.getCm_user() == null ? "" : popupSvcVO.getCm_user()).append("'").append(",");
		sb.append("'").append(popupSvcVO.getCm_pwd() == null ? "" : popupSvcVO.getCm_pwd()).append("'").append(",");
		sb.append("'").append(popupSvcVO.getPopup_svc_yn() == null ? "" : popupSvcVO.getPopup_svc_yn()).append("'").append(",");
		sb.append("'").append(popupSvcVO.getDevice_ipaddr() == null ? "" : popupSvcVO.getTargetIP()).append("'").append(",");
		sb.append("'").append(popupSvcVO.getDescription() == null ? "" : popupSvcVO.getDescription()).append("'").append(")");
		
		return sb.toString();
	}

}
