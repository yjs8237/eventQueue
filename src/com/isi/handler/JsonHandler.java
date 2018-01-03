package com.isi.handler;

import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.isi.constans.RESULT;
import com.isi.vo.EmployeeVO;

public class JsonHandler {
	
	private JSONObject jsonData;
	
	public JsonHandler() {
		
	}
	
	public JSONObject getResponseJson(int returnCode) {
		
		switch (returnCode) {
		
		case RESULT.RTN_SUCCESS :
			setJsonReturnCode(RESULT.HTTP_SUCCESS);
			break;
		
		default : 
			setJsonReturnCode(RESULT.HTTP_URL_ERROR);
			break;
		}
		
		return jsonData;
		
	}
	
	
	public JSONObject getEmployeeInfo(List employeeList) {
		
		if(employeeList == null) {
			setJsonReturnCode(RESULT.HTTP_PARAM_ERROR);
			return jsonData;
		}
		
		JSONArray jsonArr = new JSONArray();
		if(employeeList != null) {
			for (int i = 0; i < employeeList.size(); i++) {
				EmployeeVO employeeVO = (EmployeeVO) employeeList.get(i);
				JSONObject obj = new JSONObject();
				obj.put("empId", employeeVO.getEm_ID());
				obj.put("mac", employeeVO.getMacaddress());
				obj.put("extension", employeeVO.getDN());
//				obj.put("orgNm", employeeVO.getGroupNm());
//				obj.put("empNm", employeeVO.getEm_name());
//				obj.put("empGradeNm", employeeVO.getEm_position());
				obj.put("cmIp", employeeVO.getCmIP());
				obj.put("deviceType", employeeVO.getDeviceType());
				obj.put("deviceIpaddr", employeeVO.getIpAddr());
				obj.put("cmUser", employeeVO.getCmUser());
//				obj.put("cmPwd", employeeVO.getCmPass());
				obj.put("popupSvcYn", employeeVO.getPopupYN());
					
				if(obj != null && obj.size() != 0) {
					jsonArr.add(obj);
				}
			}
		} else {

		}
		
		jsonData.put("empInfo", jsonArr);
		setJsonReturnCode(RESULT.HTTP_SUCCESS);
		return jsonData;
		
	}
	
	
	
	
	private void setJsonReturnCode(int returnCode) {
		jsonData.put("code", returnCode);
		jsonData.put("msg", setHttpResponseData(returnCode));
	}
	
	private String setHttpResponseData(int retCode) {
		// TODO Auto-generated method stub
		String str = "";
		
		switch (retCode) {
		case RESULT.RTN_SUCCESS:
			str = "SUCCESS";
			break;
			
		case RESULT.HTTP_SUCCESS:
			str = "SUCCESS";
			break;
			
		case RESULT.HTTP_URL_ERROR:
			str = "잘못된 URL 요청";
			break;
			
		case RESULT.HTTP_PARAM_ERROR:
			str = "Parameter 데이터 확인해주세요";
			break;
		
		
		default:
			break;
		}
		
		return str;
	}
	
}
