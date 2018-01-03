package com.isi.data;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.sql.ResultSet;
import java.util.*;

import javax.imageio.ImageIO;

import com.isi.constans.LOGLEVEL;
import com.isi.constans.LOGTYPE;
import com.isi.constans.PROPERTIES;
import com.isi.constans.RESULT;
import com.isi.db.JDatabase;
import com.isi.exception.ExceptionUtil;
import com.isi.file.GLogWriter;
import com.isi.file.ILog;
import com.isi.file.LogMgr;
import com.isi.file.PropertyRead;
import com.isi.handler.ImageHandler;
import com.isi.vo.*;

/**
*
* @author greatyun
*/
public class Employees {
	
	
	private Map <String, Object> map = new HashMap<String, Object>();
	private Map <String, Object> customerMap = new HashMap<String, Object>();
	private JDatabase m_Conn = null;
	private ILog m_Log = new GLogWriter();
	private LogMgr logwrite;
	private StringWriter sw;
	private PrintWriter pw;
	private ImageHandler imgHandler;
	private PropertyRead pr;
	private ResultSet rs;
	
	private static Employees employees = new Employees();
	
	private Employees () {
		sw = new StringWriter();
		pw = new PrintWriter(sw);
		logwrite = LogMgr.getInstance();
	}
	
	public synchronized static Employees getInstance() {
		
		if(employees == null) {
			employees = new Employees();
		}
		return employees;
	}
	
	public int updateDeviceIpaddrInfo(String DN , String ipAddr) {
		List list =	(ArrayList) this.map.get(DN);
		
		if(list != null) {
			EmployeeVO empVO = null;
			List tempList = new ArrayList<>();
			
			for (int i = 0; i < list.size(); i++) {
				empVO = (EmployeeVO)list.get(i);
				if(empVO.getIpAddr() == null || empVO.getIpAddr().isEmpty()) {
					empVO.setIpAddr(ipAddr);
				}
				tempList.add(empVO);
			}
			
			if(tempList.size() > 0) {
				map.put(DN, tempList);
			}
			
		} 
		return 0;
	}
	
	
	public int addEmployee (EmployeeVO employee){
		if(employee == null) {
			return RESULT.RTN_EXCEPTION;
		}
		
		if(employee.getDN() == null){
			return RESULT.HTTP_PARAM_ERROR;
		}
		
		ArrayList list = new ArrayList();
		if(map.get(employee.getDN()) == null) {
			list.add(employee);
		} else {
			list = (ArrayList) map.get(employee.getDN());
			list.add(employee);
		}
		
		synchronized (map) {
			map.put(employee.getDN(), list);
		}
		
		return RESULT.RTN_SUCCESS;
	}
	
	public int updateEmpImage (EmployeeVO updateEmployee , String requestID) {
		
		Image image = null;
		String	strUrl="";
		URL url = null;
		BufferedImage img = null;
		
		try{
			
			imgHandler = new ImageHandler();
			// 직원 증명사진 삭제
			imgHandler.deleteFaceImageFile(updateEmployee);
			// 직원 증명사진 다운로드
			imgHandler.createFaceImage(updateEmployee);
			// 기존 팝업이미지 삭제
			imgHandler.deleteEmpImage(updateEmployee);
			// 신규 팝업이미지 생성
			imgHandler.createAllImageFile(updateEmployee);
			
			
		} catch (Exception e) {
			e.printStackTrace(ExceptionUtil.getPrintWriter());
			m_Log.httpLog(requestID , "updateEmpImage", "[" +updateEmployee.getEm_ID() + "] ## Exception ## ");
			m_Log.httpLog("", "updateEmpImage", ExceptionUtil.getStringWriter().toString());
			return RESULT.RTN_EXCEPTION;
		}
		
		
		return RESULT.RTN_SUCCESS;
	}
	
	
	public void printAllEmployee () {
		
		Set set = this.map.keySet();
		Iterator iter = set.iterator();
		while(iter.hasNext()) {
			String key = (String)iter.next();
			
			ArrayList list = (ArrayList) map.get(key);
			for (int i = 0; i < list.size(); i++) {
				EmployeeVO emp = (EmployeeVO) list.get(i);
				m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, "", "getMemberInfo", "[PRINT] " + emp.toString());
			}
			
			
		}
		
		System.out.println("## SUCCESS getEmployeeList!! ## TOTAL EMPLOYEE COUNT : " + map.size());
		
	}
	
	public int updateEmployee (EmployeeVO updateEmployee , String requestID){
		try {

			if(updateEmployee == null) {
				return RESULT.RTN_EXCEPTION;
			}
			
			if(updateEmployee.getDN() == null || updateEmployee.getDN().isEmpty() || updateEmployee.getDN().equals("null")){
				return RESULT.HTTP_PARAM_ERROR;
			}
			
			ArrayList list = new ArrayList();
			if(map.get(updateEmployee.getDN()) == null) {
				m_Log.httpLog(requestID , "updateEmployee", "NO Employee in memory ADD[" + updateEmployee.toString() + "]");
				list.add(updateEmployee);
			} else {
				list = (ArrayList) map.get(updateEmployee.getDN());
				for (int i = 0; i < list.size(); i++) {
					
					// 이미 메모리에 있는 직원 정보를 가져온다.
					EmployeeVO beforeEmployee = (EmployeeVO) list.get(i);
					
					// DN 이 같아야 업데이트한다.
					if(beforeEmployee.getDN().equals(updateEmployee.getDN())){
						
						if(m_Conn == null){
							m_Conn = new JDatabase("Employees updateEmployee");
						}
						
						if(!m_Conn.IsConnected()){
							pr = PropertyRead.getInstance();
							int retCode = m_Conn.connectDB(pr.getValue(PROPERTIES.DB_CLASS) , pr.getValue(PROPERTIES.DB_URL), pr.getValue(PROPERTIES.DB_USER), pr.getValue(PROPERTIES.DB_PASSWORD));
							if(retCode != RESULT.RTN_SUCCESS){
								m_Log.httpLog(requestID, "updateEmployee" , "[DataBase] Connection failed!!");
							}
						}
						
						// 업데이트 DN 의 전화기 IP , MAC , 전화기 종류 값을 가져오기 위한 쿼리 (실시간으로 DN 만 변경하게 되면 변경된 DN 에 따른 전화기 IP , MAC, DeviceType 도 변경이 자동으로 이루어지게 하기위해
						String sql = "SELECT * FROM tb_device_status WHERE dn = '"+updateEmployee.getDN()+"'";
						
						m_Log.httpLog(requestID, "updateEmployee", "SELECT SQL [" + sql + "]");
						rs = m_Conn.selectQuery(sql, false);
						
						boolean isUpdate = false;
						
						if(rs == null) {
							m_Log.httpLog(requestID, "updateEmployee" , "NO DEVICE INFO IN tb_device_status TABLE DN ["+updateEmployee.getDN()+"]");
						} else {
							rs.next();
							
							// max , ipaddr , device_type 정보다 모두 존재하면 UPDATE 한다.
							if(!(rs.getString("mac")==null) && !rs.getString("mac").isEmpty() && !rs.getString("mac").equalsIgnoreCase("null")){
								if(!(rs.getString("ipaddr")==null) && !rs.getString("ipaddr").isEmpty() && !rs.getString("ipaddr").equalsIgnoreCase("null")){
									if(!(rs.getString("device_type")==null) && !rs.getString("device_type").isEmpty() && !rs.getString("device_type").equalsIgnoreCase("null")){
										
										// 업데이트 정보를 업데이트하고, 직원정보 테이블 tb_emp_info_md 에 해당 정보도 UPDATE 한다.
										updateEmployee.setMacaddress(rs.getString("mac"));
										updateEmployee.setIpAddr(rs.getString("ipaddr"));
										updateEmployee.setDeviceType(rs.getString("device_type"));
										
										sql = "UPDATE tb_emp_info_md SET "
												+ "extension='"+updateEmployee.getDN()+"',"
												+ "mac='"+updateEmployee.getMacaddress()+"',"
												+ "device_ipaddr='"+updateEmployee.getIpAddr()+"',"
												+ "device_type='"+updateEmployee.getDeviceType()+"' "
												+ " WHERE emp_id = '"+updateEmployee.getEm_ID()+"'";
												
										m_Log.httpLog(requestID, "updateEmployee", "UPDATE SQL [" + sql + "]");
										
										isUpdate = true;
										m_Conn.executeQuery(sql, false);
									}
								}
							}
						}
						
						if(isUpdate){
							m_Log.httpLog(requestID, "updateEmployee", "SUCCESS !! DB TABLE UPDATE !!" );
						} else {
							m_Log.httpLog(requestID, "updateEmployee", "FAIL !! DB TABLE UPDATE !! 업데이트 정보 부족 " );
						}
						
						m_Log.httpLog(requestID, "updateEmployee", "변경 전  VO 정보 [" + beforeEmployee.toString() + "]" );
						// UPDATE 요청 파라미터 데이터만 업데이트 한다.
						updateRealData(beforeEmployee, updateEmployee);
						m_Log.httpLog(requestID, "updateEmployee", "변경 후  VO 정보 [" + beforeEmployee.toString() + "]" );
						
						/*
						 * 10분 배치로 update 되는 URL 파라미터에 dev_deviceType , dev_ipAddr, dev_popupYN , eaiUseYN 가 넘어오지 않아
						 * null 로 SET 이 되어 팝업이 되지 않는 현상이 발생한다. 
						 * 이를 방지하기 위해  아래 처럼 데이터를 세팅한다.
						 * dev_deviceType , dev_ipAddr -> 이전 데이터 계속 사용
						 * dev_popupYN -> "Y" 세팅
						 * eaiUseYN -> "Y" 세팅
						 */
						
						/*
						// device_Type 설정
						if(updateEmployee.getDeviceType() == null || updateEmployee.getDeviceType().isEmpty()) {
							// NULL 이거나 값이 없을경우
							if(beforeEmployee.getDeviceType() == null || updateEmployee.getDeviceType().isEmpty()){
								// 수정하기 이전 데이터도 값이 없을 경우 623 으로 강제 세팅, 대부분 전화기가 623 이기 때문..
								m_Log.httpLog(requestID, "updateEmployee", "DEVICE TYPE 강제 세팅 [623]");
								updateEmployee.setDeviceType("623");
							} else {
								// 수정하기 이전 devicetype 데이터를 이어간다.
								m_Log.httpLog(requestID, "updateEmployee", "DEVICE TYPE 이전 세팅 유지 ["+beforeEmployee.getDeviceType()+"]");
								updateEmployee.setDeviceType(beforeEmployee.getDeviceType());
							}
						}
						
						// device_ipAddr 설정
						if(updateEmployee.getIpAddr() == null || updateEmployee.getIpAddr().isEmpty()) {
							// NULL 이거나 값이 없을경우 수정하기 이전  데이터를 이어간다.
							m_Log.httpLog(requestID, "updateEmployee", "DEVICE IPADDR 이전 세팅 유지 ["+beforeEmployee.getIpAddr()+"]");
							updateEmployee.setIpAddr(beforeEmployee.getIpAddr() != null ? beforeEmployee.getIpAddr() : "");
						}
						
						// dev_popupYN 설정
						if(updateEmployee.getPopupYN() == null || updateEmployee.getPopupYN().isEmpty()) {
							// NULL 이거나 값이 없을경우 수정하기 이전  데이터를 이어간다.
							m_Log.httpLog(requestID, "updateEmployee", "DEVICE POPUP YN 이전 세팅 유지 ["+beforeEmployee.getPopupYN()+"]");
							updateEmployee.setPopupYN(beforeEmployee.getPopupYN() != null ? beforeEmployee.getPopupYN() : "Y");
						}
						
						// eaiUseYN 설정
						if(updateEmployee.getEaiUseYN() == null || updateEmployee.getEaiUseYN().isEmpty()) {
							// NULL 이거나 값이 없을경우 수정하기 이전  데이터를 이어간다.
							m_Log.httpLog(requestID, "updateEmployee", "DEVICE EAIUSE YN 이전 세팅 유지 ["+beforeEmployee.getEaiUseYN()+"]");
							updateEmployee.setEaiUseYN(beforeEmployee.getEaiUseYN() != null ? beforeEmployee.getEaiUseYN() : "Y");
						}
						*/
						list.remove(i);
						list.add(beforeEmployee);
						m_Log.httpLog(requestID , "updateEmployee", "UPDATE Employee ["+ beforeEmployee.toString() + "]");
					}
				
				}
			}
			
			synchronized (map) {
				map.put(updateEmployee.getDN(), list);
			}
		} catch (Exception e) {
			m_Conn.disconnectDB();
			m_Conn = null;
			sw = new StringWriter();
			pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			m_Log.httpLog(requestID , "updateEmployee", sw.toString());
			return RESULT.RTN_EXCEPTION;
		}
		
		return RESULT.RTN_SUCCESS;
		
	}
	

	private void updateRealData(EmployeeVO beforeVO, EmployeeVO updateVO) {
		// TODO Auto-generated method stub
		
		if(updateVO.getDeviceType()!=null && !updateVO.getDeviceType().isEmpty() && !updateVO.getDeviceType().equalsIgnoreCase("null")){
			beforeVO.setDeviceType(updateVO.getDeviceType());
		}
		/*
		if(updateVO.getEaiUseYN()!=null && !updateVO.getEaiUseYN().isEmpty() && !updateVO.getEaiUseYN().equalsIgnoreCase("null")){
			beforeVO.setEaiUseYN(updateVO.getEaiUseYN());
		}
		
		if(updateVO.getEm_cellNum()!=null && !updateVO.getEm_cellNum().isEmpty() && !updateVO.getEm_cellNum().equalsIgnoreCase("null")){
			beforeVO.setEm_cellNum(updateVO.getEm_cellNum());
		}
		*/
		if(updateVO.getEm_name()!=null && !updateVO.getEm_name().isEmpty() && !updateVO.getEm_name().equalsIgnoreCase("null")){
			beforeVO.setEm_name(updateVO.getEm_name());
		}
		
		if(updateVO.getEm_position()!=null && !updateVO.getEm_position().isEmpty() && !updateVO.getEm_position().equalsIgnoreCase("null")){
			beforeVO.setEm_position(updateVO.getEm_position());
		}
		/*
		if(updateVO.getEmail()!=null && !updateVO.getEmail().isEmpty() && !updateVO.getEmail().equalsIgnoreCase("null")){
			beforeVO.setEmail(updateVO.getEmail());
		}
		*/
		if(updateVO.getGroupNm()!=null && !updateVO.getGroupNm().isEmpty() && !updateVO.getGroupNm().equalsIgnoreCase("null")){
			beforeVO.setGroupNm(updateVO.getGroupNm());
		}
		
		if(updateVO.getPopupYN()!=null && !updateVO.getPopupYN().isEmpty() && !updateVO.getPopupYN().equalsIgnoreCase("null")){
			beforeVO.setPopupYN(updateVO.getPopupYN());
		}
		/*
		if(updateVO.getViewYN()!=null && !updateVO.getViewYN().isEmpty() && !updateVO.getViewYN().equalsIgnoreCase("null")){
			beforeVO.setViewYN(updateVO.getViewYN());
		}
		*/
		if(updateVO.getIpAddr()!=null && !updateVO.getIpAddr().isEmpty() && !updateVO.getIpAddr().equalsIgnoreCase("null")){
			beforeVO.setIpAddr(updateVO.getIpAddr());
		}
		
		if(updateVO.getMacaddress()!=null && !updateVO.getMacaddress().isEmpty() && !updateVO.getMacaddress().equalsIgnoreCase("null")){
			beforeVO.setMacaddress(updateVO.getMacaddress());
		}
		
		// CM User , CM Password 는 변경 요청이 와도 무시한다.
		/*
		if(updateVO.getCmUser()!=null && !updateVO.getCmUser().isEmpty() && !updateVO.getCmUser().equalsIgnoreCase("null")){
			beforeVO.setCmUser(updateVO.getCmUser());
		}
		
		if(updateVO.getCmPass()!=null && !updateVO.getCmPass().isEmpty() && !updateVO.getCmPass().equalsIgnoreCase("null")){
			beforeVO.setCmPass(updateVO.getCmPass());
		}
		*/
	}

	public Object deleteEmployee (String DN){
		Object obj = null;
		if(DN == null) {
			return RESULT.RTN_EXCEPTION;
		}
		
		if(DN.length() == 0){
			return RESULT.HTTP_PARAM_ERROR;
		}
		
		synchronized (map) {
			obj = map.remove(DN);
		}
		
		return obj;
	}
	
//	public void setEmployeesMap (Map employeeMap) {
//		this.map = employeeMap;
//	}

	public Map<String, Object> getMap() {
		return map;
	}
	
	public int getEmployeeCnt (){
		return this.map.size();
	}
	
	public boolean isEmptyMap (){
		return this.map.isEmpty();
	}
	
	public List getAllEmployee (String stExtension,String callID){
		return (ArrayList) map.get(stExtension);
	}
	public void addAllEmployee (String DN , ArrayList list) {
		this.map.put(DN, list);
	}
	
	public CustomerVO getCustomer (String cellNum , String callID) {
		return (CustomerVO) this.customerMap.get(cellNum);
	}
	
	public EmployeeVO getEmployee (String stExtension , String callID){
		ArrayList list = (ArrayList) map.get(stExtension);
		
		EmployeeVO employee = null;
		
		if(list != null) {
			if(list != null && list.size() > 1){
				for (int i = 0; i < list.size(); i++) {
					employee = (EmployeeVO) list.get(i);
					if(employee.getPopupYN().equalsIgnoreCase("Y")){
						break;
					}
				}
			} else {
				employee = (EmployeeVO) list.get(0);
			}
		}
		
		if(employee != null){
			logwrite.standLog(callID, "getEmployee", "GET Employee Information RESULT [" + employee.toString() + "]");
		} else {
			logwrite.standLog(callID, "getEmployee", "GET Employee NULL !!! [" + stExtension + "]");
		}
		
		return employee;
	}
	
	public int getEmployeeList(){
		
		if(this.map.size() > 0) {
			this.map.clear();
		}
		
		this.map = getMemberInfo();
		if(map == null || map.size() < 1){
			return RESULT.RTN_EXCEPTION;
		} else {
			System.out.println("## SUCCESS getEmployeeList!! ## TOTAL EMPLOYEE COUNT : " + map.size());
			/*
			Set keySet = map.keySet();
			Iterator iter = keySet.iterator();
			while(iter.hasNext()){
				String key = (String)iter.next();
				ArrayList list = (ArrayList) map.get(key);
				for (int i = 0; i < list.size(); i++) {
					Object obj = list.get(i);
					m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, "", "getEmployeeList", "key["+key+"][" +obj.toString()+"]");
				}
			}
			*/
			return RESULT.RTN_SUCCESS;
		}
	}
	
	public int getCustomerList() {
		this.customerMap = getCustomerInfo();
		if(customerMap == null || customerMap.size() < 1) {
			return RESULT.RTN_EXCEPTION;
		} else {
			System.out.println("## SUCCESS getCustomerList!! ## TOTAL CUSTOMER COUNT : " + customerMap.size());
			return RESULT.RTN_SUCCESS;
		}
	}
	
	
	public String getMapInfo(){
		return this.map.toString();
	}
	
	// Deamon 구동시 최초 직원 이미지 생성 한다.
		public int creatAllFaceImg(){
			
			imgHandler = new ImageHandler();
			
			// DB 에서 불러들인 직원 정보 기준으로 이미지 생성
			Set keyset = map.keySet();
			Iterator iter = keyset.iterator();
			while(iter.hasNext()){
				String key = (String) iter.next();
				ArrayList list = (ArrayList) map.get(key);
				for (int i = 0; i < list.size(); i++) {
					EmployeeVO employee = (EmployeeVO) list.get(i);
					if(!imgHandler.createFaceImage(employee)){
						m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, ""
								, "MakeLogFaile", "[creatAllFaceImg] Exception >> EMP:" + employee.toString());
						System.out.println("[creatAllFaceImg] Exception >> EMP:" + employee.toString());
						//return RESULT.RTN_EXCEPTION;
					}
				}
			}
			return RESULT.RTN_SUCCESS;
		}
	
	// Deamon 구동시 최초 직원 이미지 생성 한다.
	public int creatEmployeeImg(){
		
		imgHandler = new ImageHandler();
		
		// DB 에서 불러들인 직원 정보 기준으로 이미지 생성
		Set keyset = map.keySet();
		Iterator iter = keyset.iterator();
		
		while(iter.hasNext()){
			
			String key = (String) iter.next();
			ArrayList list = (ArrayList) map.get(key);
			
			for (int i = 0; i < list.size(); i++) {
				EmployeeVO employee = (EmployeeVO) list.get(i);
				if(!imgHandler.createAllImageFile(employee)){
					
					m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, ""
							, "MakeLogFaile", "[creatEmployeeImg] Exception >> EMP:" + employee.toString());
					System.out.println("[creatEmployeeImg] Exception >> EMP:" + employee.toString());
					//return RESULT.RTN_EXCEPTION;
				}
			}
		}
		return RESULT.RTN_SUCCESS;
	}
	
	private Map getCustomerInfo() {
		m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, "", "getCustomerInfo", "Start !! getCustomerInfo !!");
		int retCode = 0;
    	CustomerVO customerVO = null;
    	PropertyRead pr = PropertyRead.getInstance();
    	Map <String, Object> customerMap = new HashMap<String, Object>();
    	
    	try {
    		if(m_Conn == null){
				m_Conn = new JDatabase("Customer getCustomerInfo");
			}
    		
    		if(!m_Conn.IsConnected()){
//				RESULT = m_Conn.connectDB(ServiceParam.getDatabaseURL(), ServiceParam.getDatabaseID(), ServiceParam.getDatabasePWD());
				retCode = m_Conn.connectDB(pr.getValue(PROPERTIES.DB_CLASS) , pr.getValue(PROPERTIES.DB_URL), pr.getValue(PROPERTIES.DB_USER), pr.getValue(PROPERTIES.DB_PASSWORD));
				if(retCode != RESULT.RTN_SUCCESS){
					m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, "", "getCustomerInfo", "[DataBase] Connection failed!!");
				}
			}
			
			if (retCode == RESULT.RTN_SUCCESS) {
				
				String strSelect = "select * from tb_cust_info order by cust_tel desc";
				
                m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, "", "getCustomerInfo", "[SELECT] " + strSelect);
                
                ResultSet rs = m_Conn.selectQuery(strSelect, false);
                if (rs == null) {
                	m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, "", "getCustomerInfo" ,"[DB] AgentInfo not exist:0");
                } else {
                	m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, "", "getCustomerInfo" ,"[DB] Select Success");
                	
                	while(rs.next()){
                		
                		if(rs.getString("cust_tel") != null && rs.getString("cust_tel").length() > 0){
                			
                			customerVO = new CustomerVO();
                			
                			customerVO.setName(rs.getString("cust_nm"));
                			customerVO.setCompany(rs.getString("office_nm"));
                			customerVO.setPosition(rs.getString("duty_nm"));
                			customerVO.setPhoneNum(rs.getString("cust_tel"));
                			
                			customerMap.put(rs.getString("cust_tel"), customerVO);
                			m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, "", "getCustomerInfo" ,customerVO.toString());
                		
                		}
                	}
                    m_Conn.EndSelect();
                }
            }
    		
    	}catch (Exception e) {
    		sw = new StringWriter();
        	pw = new PrintWriter(sw);
        	e.printStackTrace(pw);
        	m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.ERR_LOG, "", "getCustomerInfo" , sw.toString());
            m_Conn.EndSelect();
            m_Conn.disconnectDB();
    	}
    	
    	 if(m_Conn.disconnectDB() == RESULT.RTN_SUCCESS){
         	m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, "", "getCustomerInfo", "## SUCCESS DISCONNECT DATABASE ##");
         } else {
         	m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, "", "getCustomerInfo", "## FAIL DISCONNECT DATABASE ##");
         }
		
    	return customerMap;
	}
	
	
	private Map getMemberInfo() {
		
		m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, "", "getMemberInfo", "Start !! getMemberInfo !!");
		
    	int retCode = 0;
    	EmployeeVO employeeInfo = null;
    	PropertyRead pr = PropertyRead.getInstance();
    	Map <String, Object> employeeMap = new HashMap<String, Object>();
    	
    	try {
			if(m_Conn == null){
				m_Conn = new JDatabase("Employees getMemberInfo");
			}
			
			if(!m_Conn.IsConnected()){
//				RESULT = m_Conn.connectDB(ServiceParam.getDatabaseURL(), ServiceParam.getDatabaseID(), ServiceParam.getDatabasePWD());
				retCode = m_Conn.connectDB(pr.getValue(PROPERTIES.DB_CLASS) , pr.getValue(PROPERTIES.DB_URL), pr.getValue(PROPERTIES.DB_USER), pr.getValue(PROPERTIES.DB_PASSWORD));
				if(retCode != RESULT.RTN_SUCCESS){
					m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, "", "getMemberInfo", "[DataBase] Connection failed!!");
				}
			}
			
			if (retCode == RESULT.RTN_SUCCESS) {
				
				String strSelect = "select * from tb_emp_info_md order by comtel_no1_4 desc";
                m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, "", "getMemberInfo", "[SELECT] " + strSelect);
                
                ResultSet rs = m_Conn.selectQuery(strSelect, false);
                if (rs == null) {
                	m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, "", "getMemberInfo" ,"[DB] AgentInfo not exist:0");
                } else {
                	m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, "", "getMemberInfo" ,"[DB] Select Success");
                	while(rs.next()){
                		
//                		String sql = "update itss_userinfo set dn = '"+ getDN(rs.getString("phone"))+"' where userid = '" +rs.getString("userid")+"'";
//                		m_Conn.executeQuery(sql, false);
                		
                		if(rs.getString("comtel_no1_4") != null && rs.getString("comtel_no1_4").length() > 0){
                			
                			employeeInfo = new EmployeeVO();
                			
//                			String cellNum = rs.getString("mobile_no_1") + "-" + rs.getString("mobile_no_2") + "-"+rs.getString("mobile_no_3");
                			String extension = rs.getString("comtel_no1_4");
                			String mac = rs.getString("mac");
                			employeeInfo.
                			setEm_ID(rs.getString("emp_no")).
                			setMacaddress(rs.getString("mac")).
                			setDN(rs.getString("comtel_no1_4")).
                			setOrgNm(rs.getString("org_nm")).
                			setGroupNm(rs.getString("dup_org_nm")).
                			setEm_position(rs.getString("duty_nm")).
                			setEm_name(rs.getString("emp_nm")).
                			setPopupYN(rs.getString("popup_svc_yn")).
                			setCmIP(rs.getString("cm_ip")).
                			setDeviceType(rs.getString("device_type")).
                			setIpAddr(rs.getString("device_ipaddr")).
                			setCmUser(rs.getString("cm_user")).
                			setCmPass(rs.getString("cm_pwd"));
                			
                			m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, "", "getMemberInfo" ,employeeInfo.toString());
                			// key : 내선번호 , value : Employee VO 객체
                			
                			ArrayList list = new ArrayList();
                			if(employeeMap.containsKey(extension)){
                				list = (ArrayList) employeeMap.get(extension);
                				list.add(employeeInfo);
                				employeeMap.put(extension, list);
                			} else {
                				list.add(employeeInfo);
                				employeeMap.put(extension, list);
                			}
                			
//                			employeeMap.put(rs.getString("extension"), employeeInfo);
                		
                		}
                			
                	}
                	
                    m_Conn.EndSelect();
                }
            }
        } catch (Exception e) {
        	sw = new StringWriter();
        	pw = new PrintWriter(sw);
        	e.printStackTrace(pw);
        	m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.ERR_LOG, "", "getMemberInfo" , sw.toString());
            m_Conn.EndSelect();
            m_Conn.disconnectDB();
        }
        
        if(m_Conn.disconnectDB() == RESULT.RTN_SUCCESS){
        	m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, "", "getMemberInfo", "## SUCCESS DISCONNECT DATABASE ##");
        } else {
        	m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, "", "getMemberInfo", "## FAIL DISCONNECT DATABASE ##");
        }
        
        return employeeMap;
        
    }

	public int deleteAllImages() {
		// TODO Auto-generated method stub
		imgHandler = new ImageHandler();
		if(imgHandler.deleteImageFile()){
			return RESULT.RTN_SUCCESS; 
		} else {
			return RESULT.RTN_EXCEPTION;
		}
	}
	
	public int deleteAllFaceImages() {
		// TODO Auto-generated method stub
		imgHandler = new ImageHandler();
		if(imgHandler.deleteFaceImageFile()){
			return RESULT.RTN_SUCCESS; 
		} else {
			return RESULT.RTN_EXCEPTION;
		}
	}
	
	
	
}
