package com.isi.thread;

import com.isi.constans.RESULT;
import com.isi.data.Employees;
import com.isi.data.ImageMgr;
import com.isi.exception.ExceptionUtil;
import com.isi.file.GLogWriter;
import com.isi.file.ILog;
import com.isi.handler.DeviceStatusHandler;
import com.isi.service.JtapiService;
import com.isi.vo.EmployeeVO;
import com.isi.vo.JTapiResultVO;

public class DeviceCheck extends Thread{
	
	private ILog logwrite;
	private EmployeeVO empVO;
	private String requestID;
	
	public DeviceCheck ( String requestID , EmployeeVO emp) {
		this.requestID = requestID;
		this.empVO = emp;
		this.logwrite = new GLogWriter();
	}
	
	
	public void run () {
		
		boolean isSuccess = false;
		
		try {
			
			logwrite.httpLog(requestID, "run", "--> Start Device Regi Status Check");
			
			int cnt = 0;
			
			while(true) {
				
				if(cnt > 100) {
					logwrite.httpLog(requestID, "run", "--> Check Device Count [" + cnt + "] FAIL!! STOP CHEKING ");
					break;
				}
				
				if(cnt > 1) {
					JTapiResultVO resultVO = JtapiService.getInstance().monitorStart(empVO.getExtension());
					if(resultVO == null || resultVO.getCode() < 0) {
						// 실패의 경우
						logwrite.httpLog(requestID, "run", empVO.getExtension() + " --> Login 실패 3초후 재시도..");
					} else if(resultVO.getCode() == RESULT.RTN_SUCCESS || resultVO.getCode() == 200){
						// 성공의 경우
						EmployeeVO tempVO = Employees.getInstance().loginEmployee(empVO , requestID);
						if(tempVO != null) {
							empVO.setOrg_nm(tempVO.getOrg_nm());
							empVO.setPos_nm(tempVO.getPos_nm());
						}
						ImageMgr.getInstance().createImageFiles(empVO, requestID);
						logwrite.httpLog(requestID, "run", empVO.getExtension() + " --> Login 성공");
						
						
//						JtapiService.getInstance().monitorStart(empVO.getExtension());
						
						break;
					}
				}
				
				cnt++;
				Thread.sleep(3000);
				
				/*
				if(cnt == 0) {
					// 최초 시도는 6초 정도 이후에 ... 쓸데없는 네트워크 부하 없애기 위함
					logwrite.httpLog(requestID, "run", "--> Check Device Count [" + cnt + "] Will try after 6 Seconds...");
					Thread.sleep(6000);
				}
				
				if(!DeviceStatusHandler.getInstance().isRegisteredDevice(empVO)) {
					logwrite.httpLog(requestID, "run", "--> Check Device Count [" + cnt + "] WAIT...");
				} else {
					logwrite.httpLog(requestID, "run", "--> Check Device Count [" + cnt + "] SUCCESS!! ");
					isSuccess = true;
					break;
				}
				*/
			}
			
			/*
			if(isSuccess) {
				JtapiService.getInstance().monitorStart(empVO.getExtension());
				Employees.getInstance().loginEmployee(empVO , requestID);
				logwrite.httpLog(requestID, "run", "--> Login 성공");
			}
			*/
			logwrite.httpLog(requestID, "run", "--> Cheking device thread stop...");
			
		} catch (Exception e) {
			e.printStackTrace(ExceptionUtil.getPrintWriter());
			logwrite.httpLog(requestID, "run", ExceptionUtil.getStringWriter().toString());
		}
		
	}
	
}
