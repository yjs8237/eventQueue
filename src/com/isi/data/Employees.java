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
	
	
	private Map <String, Object> empMapByExtension = new HashMap<String, Object>();
	private Map <String, Object> empMapByCellNum = new HashMap<String, Object>();
	private Map <String, Object> empMapByMac = new HashMap<String, Object>();
	private Map <String, Object> customerMap = new HashMap<String, Object>();
	
	private List<String> initLoginExtlist;
	
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
		initLoginExtlist = new ArrayList<>();
	}
	
	public synchronized static Employees getInstance() {
		
		if(employees == null) {
			employees = new Employees();
		}
		return employees;
	}
	
	public List getInitLoginExtlist () {
		return this.initLoginExtlist;
	}
	
	
	public int loginEmployee ( EmployeeVO employee , String requestID) {
		if(employee == null) {
			return RESULT.RTN_EXCEPTION;
		}
		
		if(employee.getMac_address() == null || employee.getMac_address().isEmpty()) {
			return RESULT.RTN_EXCEPTION;
		}
		
		String cell_no = employee.getCell_no();
		cell_no = cell_no.replaceAll("-", "");
		cell_no = cell_no.replaceAll("#", "");
		employee.setCell_no(cell_no);
		
		logoutEmployee(employee , requestID);
		
		List list = new ArrayList<>();
		list.add(employee);
		
		empMapByCellNum.put(employee.getCell_no() , list);
		empMapByExtension.put(employee.getExtension() , list);
		empMapByMac.put(employee.getMac_address() , list);
		
		m_Log.httpLog(requestID , "loginEmployee", ">> LOGIN << SUCCESS extension [" + employee.getExtension() + "] cell_no [" + employee.getCell_no() + "] mac_address [" + employee.getMac_address() + "]");
		
		return RESULT.RTN_SUCCESS;
	}
	
	
	public int logoutEmployee (EmployeeVO employee , String requestID) {
		if(employee == null) {
			return RESULT.RTN_EXCEPTION;
		}
		
		if(employee.getMac_address() == null || employee.getMac_address().isEmpty()) {
			return RESULT.RTN_EXCEPTION;
		}
		
		empMapByCellNum.remove(employee.getCell_no());
		empMapByExtension.remove(employee.getExtension());
		empMapByMac.remove(employee.getMac_address());
		
		m_Log.httpLog(requestID , "logoutEmployee", ">> LOGOUT << SUCCESS extension [" + employee.getExtension() + "] cell_no [" + employee.getCell_no() + "] mac_address [" + employee.getMac_address() + "]");
		
		return RESULT.RTN_SUCCESS;
	}
	
	/*
	public int addEmployee (EmployeeVO employee){
		if(employee == null) {
			return RESULT.RTN_EXCEPTION;
		}
		
		if(employee.getExtension() == null){
			return RESULT.HTTP_PARAM_ERROR;
		}
		
		ArrayList listExtension = new ArrayList();
		if(empMapByExtension.get(employee.getExtension()) == null) {
			listExtension.add(employee);
		} else {
			listExtension = (ArrayList) empMapByExtension.get(employee.getExtension());
			listExtension.add(employee);
		}
		
		ArrayList listCellNum = new ArrayList();
		if(empMapByCellNum.get(employee.getCell_no()) == null) {
			listCellNum.add(employee);
		} else {
			listCellNum = (ArrayList) empMapByCellNum.get(employee.getCell_no());
			listCellNum.add(employee);
		}
		
		empMapByExtension.put(employee.getExtension(), listExtension);
		empMapByCellNum.put(employee.getCell_no(), listCellNum);
		
		return RESULT.RTN_SUCCESS;
	}
	*/
	
	
	public List getEmployeeListByExtension (String... strs){
		return (ArrayList) empMapByExtension.get(strs[0]);
	}
	
	public List getEmployeeListByCellNum (String cell_num,String callID){
		return (ArrayList) empMapByCellNum.get(cell_num);
	}
	
	public EmployeeVO getEmployeeByCellNum (String cell_no , String callID){
		ArrayList list = (ArrayList) empMapByCellNum.get(cell_no);
		
		EmployeeVO employee = null;
		
		if(list != null) {
			if(list != null && list.size() > 1){
				for (int i = 0; i < list.size(); i++) {
					employee = (EmployeeVO) list.get(i);
					if(employee.getPopup_svc_yn().equalsIgnoreCase("Y")){
						break;
					}
				}
			} else {
				employee = (EmployeeVO) list.get(0);
			}
		}
		
		if(employee != null){
			logwrite.standLog(callID, "getEmployeeByCellNum", "GET Employee Information RESULT [" + employee.toString() + "]");
		} else {
			logwrite.standLog(callID, "getEmployeeByCellNum", "GET Employee NULL !!! [" + cell_no + "]");
		}
		
		return employee;
	}
	
	public boolean checkEmployee(String extension) {
		return empMapByExtension.containsKey(extension);
	}
	
	
	public EmployeeVO getEmployeeByExtension (String stExtension , String callID){
		ArrayList list = (ArrayList) empMapByExtension.get(stExtension);
		
		EmployeeVO employee = null;
		
		if(list != null) {
			if(list != null && list.size() > 1){
				for (int i = 0; i < list.size(); i++) {
					employee = (EmployeeVO) list.get(i);
					if(employee.getPopup_svc_yn() != null && employee.getPopup_svc_yn().equalsIgnoreCase("Y")) {
						break;
					}
				}
			} else {
				employee = (EmployeeVO) list.get(0);
			}
		}
		
		if(employee != null){
			logwrite.standLog(callID, "getEmployeeByExtension", "GET Employee Information RESULT [" + employee.toString() + "]");
		} else {
			logwrite.standLog(callID, "getEmployeeByExtension", "GET Employee NULL !!! [" + stExtension + "]");
		}
		
		return employee;
	}
	
	public EmployeeVO getEmployeeByMacAddress (String mac_address , String callID){
		ArrayList list = (ArrayList) empMapByMac.get(mac_address);
		
		EmployeeVO employee = null;
		
		if(list != null) {
			if(list != null && list.size() > 1){
				for (int i = 0; i < list.size(); i++) {
					employee = (EmployeeVO) list.get(i);
					if(employee.getPopup_svc_yn().equalsIgnoreCase("Y")){
						break;
					}
				}
			} else {
				employee = (EmployeeVO) list.get(0);
			}
		}
		
		if(employee != null){
			logwrite.standLog(callID, "getEmployeeByMacAddress", "GET Employee Information RESULT [" + employee.toString() + "]");
		} else {
			logwrite.standLog(callID, "getEmployeeByMacAddress", "GET Employee NULL !!! [" + mac_address + "]");
		}
		
		return employee;
	}
	
	public int getEmployeeList(){
		
		if(this.empMapByCellNum.size() > 0) {
			this.empMapByCellNum.clear();
		}
		
		if(this.empMapByExtension.size() > 0) {
			this.empMapByExtension.clear();
		}
		
		if(this.empMapByMac.size() > 0) {
			this.empMapByMac.clear();
		}
		
		if(getMemberInfo() != RESULT.RTN_SUCCESS) {
			return RESULT.RTN_EXCEPTION;
		}
		
		return RESULT.RTN_SUCCESS;
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
	
	
	private int getMemberInfo() {
		
		m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, "", "getMemberInfo", "Start !! getMemberInfo !!");
		
    	int retCode = RESULT.RTN_SUCCESS;
    	EmployeeVO employeeInfo = null;
    	PropertyRead pr = PropertyRead.getInstance();
    	Map <String, Object> employeeMap = new HashMap<String, Object>();
    	
    	try {
			if(m_Conn == null){
				m_Conn = new JDatabase("Employees getMemberInfo");
			}
			
			if(!m_Conn.IsConnected()){
				retCode = m_Conn.connectDB(pr.getValue(PROPERTIES.DB_CLASS) , pr.getValue(PROPERTIES.DB_URL), pr.getValue(PROPERTIES.DB_USER), pr.getValue(PROPERTIES.DB_PASSWORD));
				if(retCode != RESULT.RTN_SUCCESS){
					m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, "", "getMemberInfo", "[DataBase] Connection failed!!");
				}
			}
			
			if (retCode == RESULT.RTN_SUCCESS) {
				
				String strSelect = pr.getValue(PROPERTIES.QUERY_EMPINFO);
                m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, "", "getMemberInfo", "[SELECT] " + strSelect);
                
                ResultSet rs = m_Conn.selectQuery(strSelect, false);
                if (rs == null) {
                	m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, "", "getMemberInfo" ,"[DB] AgentInfo not exist:0");
                } else {
                	m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, "", "getMemberInfo" ,"[DB] Select Success");
                	while(rs.next()){
                		
                		if(rs.getString("extension") != null && rs.getString("extension").length() > 0){
                			
                			employeeInfo = new EmployeeVO();
                			
                			String extension = rs.getString("extension");
                			String cell_num = rs.getString("cell_no");
                			if(cell_num != null) {
                				cell_num = cell_num.replaceAll("-", "");
                			} 
                			
                			employeeInfo.setEmp_id(rs.getString("emp_id"));
                			employeeInfo.setEmp_nm_kor(rs.getString("emp_nm_kor"));
                			employeeInfo.setEmp_nm_eng(rs.getString("emp_nm_eng"));
                			employeeInfo.setOrg_nm(rs.getString("org_nm"));
                			employeeInfo.setPos_nm(rs.getString("pos_nm"));
                			employeeInfo.setDuty_nm(rs.getString("duty_nm"));
                			employeeInfo.setEmail(rs.getString("email"));
                			
                			// 프로세스 기동 시점 기준 현재 로그인상태 유저는 로그인되어 있는 내선번호를 메모리에 올린다
                			if(rs.getString("loginExtension") != null) {
                				extension = rs.getString("loginExtension");
                				employeeInfo.setExtension(extension);
                				initLoginExtlist.add(extension);
                			} else {
                				employeeInfo.setExtension(extension);
                			}
                			
                			employeeInfo.setCell_no(cell_num);
                			employeeInfo.setEmp_stat_nm(rs.getString("emp_stat_nm"));
                			employeeInfo.setEmp_div_cd_nm(rs.getString("emp_div_cd_nm"));
                			employeeInfo.setEmp_lno(rs.getString("emp_lno"));
                			employeeInfo.setBuilding(rs.getString("building"));
                			employeeInfo.setFloor(rs.getString("floor"));
                			employeeInfo.setCm_ver(rs.getString("cm_ver"));
                			employeeInfo.setCm_ip(rs.getString("cm_ip"));
                			employeeInfo.setCm_user(rs.getString("cm_user"));
                			employeeInfo.setCm_pwd(rs.getString("cm_pwd"));
                			employeeInfo.setPopup_svc_yn(rs.getString("popup_svc_yn"));
                			employeeInfo.setMac_address(rs.getString("mac_address"));
                			employeeInfo.setDevice_type(rs.getString("device_type"));
                			employeeInfo.setDevice_ipaddr(rs.getString("device_ipaddr"));
                			
                			m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, "", "getMemberInfo" ,employeeInfo.toString());
                			// key : 내선번호 , value : Employee VO 객체
                			
                			ArrayList list = new ArrayList();
                			if(empMapByExtension.containsKey(extension)){
                				list = (ArrayList) empMapByExtension.get(extension);
                				list.add(employeeInfo);
                				empMapByExtension.put(extension, list);
                			} else {
                				list.add(employeeInfo);
                				empMapByExtension.put(extension, list);
                			}
                			
                			list = new ArrayList();
                			if(empMapByCellNum.containsKey(cell_num)){
                				list = (ArrayList) empMapByCellNum.get(cell_num);
                				list.add(employeeInfo);
                				empMapByCellNum.put(cell_num, list);
                			} else {
                				list.add(employeeInfo);
                				empMapByCellNum.put(cell_num, list);
                			}
//                			System.out.println("## SUCCESS getEmployeeList!! ## TOTAL EMPLOYEE COUNT EXTENSION : " + empMapByExtension.size());
//                	    	System.out.println("## SUCCESS getEmployeeList!! ## TOTAL EMPLOYEE COUNT CELLNUM : " + empMapByCellNum.size());
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
            retCode = RESULT.RTN_EXCEPTION;
        }
        
    	System.out.println("## SUCCESS getEmployeeList!! ## TOTAL EMPLOYEE COUNT EXTENSION : " + empMapByExtension.size());
    	System.out.println("## SUCCESS getEmployeeList!! ## TOTAL EMPLOYEE COUNT CELLNUM : " + empMapByCellNum.size());
    	
        if(m_Conn.disconnectDB() == RESULT.RTN_SUCCESS){
        	m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, "", "getMemberInfo", "## SUCCESS DISCONNECT DATABASE ##");
        } else {
        	m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, "", "getMemberInfo", "## FAIL DISCONNECT DATABASE ##");
        }
        
        return retCode;
        
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
	
	public int createAllImages() {
		imgHandler = new ImageHandler();
		
		// 구동시 로그인 되어 있는 사람 이미지만 생성
		for (int i = 0; i < initLoginExtlist.size(); i++) {
			String extension = initLoginExtlist.get(i);
			List list = (List) empMapByExtension.get(extension);
			
			for (int j = 0; j < list.size(); j++) {
				EmployeeVO empVO = (EmployeeVO) list.get(j);
				ImageMgr.getInstance().createImageFiles(empVO, "initial");
			}
		}
		
		
		/*
		Set keySet = empMapByExtension.keySet();
		Iterator iter = keySet.iterator();
		while(iter.hasNext()) {
			String key = (String) iter.next();
			List list = (List) empMapByExtension.get(key);
			
			for (int i = 0; i < list.size(); i++) {
				EmployeeVO empVO = (EmployeeVO) list.get(i);
				if(empVO.getExtension().equals("1764")) {
//					ImageMgr.getInstance().createImageFiles(empVO, "initial");
				} else {
//					ImageMgr.getInstance().createImageFiles(empVO, "initial");
				}
				
//				imgHandler.createImageFile(employee, callingNum, model, callID);
//				imgHandler.createImageFile(empVO, "010-3222-8237", "119", "");
			}
		}
		*/
		return 0;
	}
	
	
}
