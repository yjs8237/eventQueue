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
	/*
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
	*/
	
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
	
	public int updateEmpImage (EmployeeVO updateEmployee , String requestID) {
		
		Image image = null;
		String	strUrl="";
		URL url = null;
		BufferedImage img = null;
		
		try{
			/*
			imgHandler = new ImageHandler();
			// 직원 증명사진 삭제
			imgHandler.deleteFaceImageFile(updateEmployee);
			// 직원 증명사진 다운로드
			imgHandler.createFaceImage(updateEmployee);
			// 기존 팝업이미지 삭제
			imgHandler.deleteEmpImage(updateEmployee);
			// 신규 팝업이미지 생성
			imgHandler.createAllImageFile(updateEmployee);
			*/
			
		} catch (Exception e) {
			e.printStackTrace(ExceptionUtil.getPrintWriter());
			m_Log.httpLog(requestID , "updateEmpImage", "[" +updateEmployee.getEmp_id() + "] ## Exception ## ");
			m_Log.httpLog("", "updateEmpImage", ExceptionUtil.getStringWriter().toString());
			return RESULT.RTN_EXCEPTION;
		}
		
		
		return RESULT.RTN_SUCCESS;
	}
	
	
	public List getEmployeeListByExtension (String stExtension,String callID){
		return (ArrayList) empMapByExtension.get(stExtension);
	}
	
	public List getEmployeeListByCellNum (String cell_num,String callID){
		return (ArrayList) empMapByCellNum.get(cell_num);
	}
	
	public EmployeeVO getEmployeeByCellNum (String cell_num , String callID){
		ArrayList list = (ArrayList) empMapByCellNum.get(cell_num);
		
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
			logwrite.standLog(callID, "getEmployeeByCellNum", "GET Employee NULL !!! [" + cell_num + "]");
		}
		
		return employee;
	}
	
	
	public EmployeeVO getEmployeeByExtension (String stExtension , String callID){
		ArrayList list = (ArrayList) empMapByExtension.get(stExtension);
		
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
			logwrite.standLog(callID, "getEmployeeByExtension", "GET Employee Information RESULT [" + employee.toString() + "]");
		} else {
			logwrite.standLog(callID, "getEmployeeByExtension", "GET Employee NULL !!! [" + stExtension + "]");
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
                			
                			employeeInfo.setEmp_id(rs.getObject("emp_id").toString());
                			employeeInfo.setEmp_nm_kor(rs.getObject("emp_nm_kor").toString());
                			employeeInfo.setEmp_nm_eng(rs.getObject("emp_nm_eng").toString());
                			employeeInfo.setOrg_nm(rs.getObject("org_nm").toString());
                			employeeInfo.setPos_nm(rs.getObject("pos_nm").toString());
                			employeeInfo.setDuty_nm(rs.getObject("duty_nm").toString());
                			employeeInfo.setEmail(rs.getObject("email").toString());
                			employeeInfo.setTel_no(rs.getObject("tel_no").toString());
                			employeeInfo.setExtension(extension);
                			employeeInfo.setCell_no(cell_num);
                			employeeInfo.setEmp_stat_nm(rs.getObject("emp_stat_nm").toString());
                			employeeInfo.setEmp_div_cd_nm(rs.getObject("emp_div_cd_nm").toString());
                			employeeInfo.setEmp_lno(rs.getObject("emp_lno").toString());
                			employeeInfo.setBuilding(rs.getObject("building").toString());
                			employeeInfo.setFloor(rs.getObject("floor").toString());
                			employeeInfo.setCm_ver(rs.getObject("cm_ver").toString());
                			employeeInfo.setCm_ip(rs.getObject("cm_ip").toString());
                			employeeInfo.setCm_user(rs.getObject("cm_user").toString());
                			employeeInfo.setCm_pwd(rs.getObject("cm_pwd").toString());
                			employeeInfo.setPopup_svc_yn(rs.getObject("popup_svc_yn").toString());
                			
                			
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
	
	
	
}
