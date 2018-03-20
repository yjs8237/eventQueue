package com.isi.service;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.isi.axl.CiscoPhoneInfo;
import com.isi.constans.LOGLEVEL;
import com.isi.constans.LOGTYPE;
import com.isi.constans.PROPERTIES;
import com.isi.constans.RESULT;
import com.isi.constans.SVCTYPE;
import com.isi.data.XmlInfoMgr;
import com.isi.exception.ExceptionUtil;
import com.isi.file.GLogWriter;
import com.isi.file.ILog;
import com.isi.file.PropertyRead;
import com.isi.jtapi.IJTAPI;
import com.isi.jtapi.JTAPI2;
import com.isi.process.IQueue;
import com.isi.process.JQueue;
import com.isi.vo.JTapiResultVO;
/**
*
* @author greatyun
*/
public class JtapiService {
	
	private PropertyRead proRead;
	private ILog logWriter;
	
	private String cmIP;
	private String cmUser;
	private String cmPassword;
	private boolean isJtapiRun;
	
	private int cmCnt;
	
	private IJTAPI[] m_jtapi;
	private JQueue m_EvtQue;
	 
	private static JtapiService jtapiService = new JtapiService();
	
	private JtapiService(){}
	public synchronized static JtapiService getInstance(){
		if(jtapiService == null){
			jtapiService = new JtapiService();
		}
		return jtapiService;
	}
	
	
	public int startService(IQueue queue){
		
		try {
			
			proRead = PropertyRead.getInstance();
			logWriter = new GLogWriter();
			m_EvtQue = (JQueue) queue;
			
//			cmCnt = Integer.parseInt(proRead.getValue(PROPERTIES.CM_CNT));
			cmCnt = XmlInfoMgr.getInstance().getCmCnt();
			
			m_jtapi = new IJTAPI[cmCnt];
			
			 for (int i = 0; i < m_jtapi.length; i++) {
				 
				 if( i == 0 ){
					 cmIP = XmlInfoMgr.getInstance().getCm1IpAddr();
					 cmUser = XmlInfoMgr.getInstance().getCm1User();
					 cmPassword = XmlInfoMgr.getInstance().getCm1Pwd();
				 } else if(i == 1){
					 cmIP = XmlInfoMgr.getInstance().getCm2IpAddr();
					 cmUser = XmlInfoMgr.getInstance().getCm2User();
					 cmPassword = XmlInfoMgr.getInstance().getCm2Pwd();
				 }
				 
	                m_jtapi[i] = new JTAPI2(i, m_EvtQue);
	                
	                if(m_jtapi[i].serviceStart(cmIP, cmUser, cmPassword) != RESULT.RTN_SUCCESS) {
	                	isJtapiRun = false;
	                	logWriter.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, SVCTYPE.JTAPI, "startService", "Cannot start JTAPI Service!");
	                } else {
	                	isJtapiRun = true;
	                	logWriter.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, SVCTYPE.JTAPI, "startService", "Start JTAPI Service!");
	                	
	                	CiscoPhoneInfo phoneinfo = new CiscoPhoneInfo();
	                	CiscoPhoneInfo.GetAllPhoneInfo(cmIP, 8443, cmUser, cmPassword, phoneinfo);
	                	// ## 주석
//	                	JCtiData.getData().updateDeviceIP(phoneinfo);
	                	
//	                	m_jtapi[i].MonitorAllStart(phoneinfo);
	                }
			 }
			 
			 if(!isJtapiRun) {
				 System.exit(0);
			 }
			 
			
		} catch( Exception e){
			e.printStackTrace(ExceptionUtil.getPrintWriter());
			logWriter.write(LOGLEVEL.LEVEL_3, LOGTYPE.ERR_LOG, SVCTYPE.JTAPI, "startService", ExceptionUtil.getStringWriter().toString());
		}
		
		
		return RESULT.RTN_SUCCESS;
	}
	
	
	
	public JTapiResultVO pickup(String myExtension , String pickupExtension) {
		
		JTapiResultVO resultVO = null;
		if(myExtension == null || myExtension.isEmpty()) {
			return resultVO;
		}
		
		if(pickupExtension == null || pickupExtension.isEmpty()) {
			return resultVO;
		}
		
		resultVO = m_jtapi[0].pickupCall(myExtension, pickupExtension);
		if(resultVO.getCode() != RESULT.RTN_SUCCESS) {
			return resultVO;
		}
		
		return resultVO;
	}
	
	public JTapiResultVO monitorStart(String extension) {
		
		JTapiResultVO resultVO = null;
		if(extension == null || extension.isEmpty()) {
			return resultVO;
		}
		
		for (int i = 0; i < m_jtapi.length; i++) {
			resultVO = m_jtapi[i].MonitorStart(extension);
			if(resultVO.getCode() != RESULT.RTN_SUCCESS) {
				return resultVO;
			}
		}
		
		if(resultVO != null) {
			if(resultVO.getCode() == RESULT.RTN_SUCCESS) {
				resultVO.setCode(200);
			}
		}
		return resultVO;
	}
	
	public JTapiResultVO monitorStop(String extension) {
		
		JTapiResultVO resultVO = null;
		if(extension == null || extension.isEmpty()) {
			return null;
		}
		
		for (int i = 0; i < m_jtapi.length; i++) {
			resultVO = m_jtapi[i].MonitorStop(extension);
			if(resultVO.getCode() != RESULT.RTN_SUCCESS) {
				return resultVO;
			}
		}
		
		return resultVO;
	}
	
}
