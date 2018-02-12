package com.test.axl.soap;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import com.test.axl.model.CmAxlInfoModel;


public class AxlHandler {
	
	private static final String cmVer = "8.5";
	
	private CmAxlInfoModel cmAxlInfo;
	
	public AxlHandler(CmAxlInfoModel cmAxlInfo) {
		this.cmAxlInfo = cmAxlInfo;
	}
	
	public JSONArray testSoap (String query) {
		return sendSoapMessage(cmAxlInfo , getQuerySoapMessage(query) ); 
	}
	
	
	public JSONArray selectPickupGroupList () {
		
		StringBuffer queryBuffer = new StringBuffer();
		queryBuffer.append("SELECT").append("\n");
		queryBuffer.append("PICK.pkid AS pick_pkid, PICK.name AS pickup_grp_name, NUM.dnorpattern AS pickup_grp_num , NUM.pkid AS fknumplan_pickup , ROUTE.description").append("\n");
		queryBuffer.append("FROM (").append("\n");
		queryBuffer.append(" SELECT * FROM pickupgroup ").append("\n");
		queryBuffer.append(" ) PICK LEFT OUTER JOIN ( ").append("\n");
		queryBuffer.append(" SELECT pkid, fkroutepartition ,dnorpattern FROM numplan ").append("\n");
		queryBuffer.append(" ) NUM ON PICK.fknumplan_pickup = NUM.pkid ").append("\n");
		queryBuffer.append(" LEFT OUTER JOIN ( ").append("\n");
		queryBuffer.append(" SELECT pkid , description FROM routepartition ").append("\n");
		queryBuffer.append(" ) ROUTE ON NUM.fkroutepartition = ROUTE.pkid ").append("\n");
		
		return sendSoapMessage(cmAxlInfo , getQuerySoapMessage(queryBuffer.toString())) ;
	}
	
	public JSONArray deleteLine(String fkRoutePartition,String dn) {
		String query = (new StringBuilder("delete from numplan where fkroutepartition = '")).append(fkRoutePartition).append("' and dnorpattern = '").append(dn).append("'").toString();
		return sendSoapMessage(cmAxlInfo , getQuerySoapMessage(query)) ;
	}
	
	
	
	// Device pool 리스트 가져오기 
	public JSONArray selectDevicePoolList () {
		StringBuffer queryBuffer = new StringBuffer();
		queryBuffer.append("SELECT pkid AS devpool_pkid , name AS devpool_name  FROM devicepool");
		return sendSoapMessage(cmAxlInfo , getQuerySoapMessage(queryBuffer.toString())) ;
	}
	
	// Phone Button Template 리스트 가져오기
	public JSONArray selectPhoneBtnTemplateList() {
		StringBuffer queryBuffer = new StringBuffer();
		queryBuffer.append("SELECT pkid AS btn_pkid, name AS btn_tp_name FROM phonetemplate");
		return sendSoapMessage(cmAxlInfo, getQuerySoapMessage(queryBuffer.toString()));
	}
	
	// soft Key 리스트 가져오기
	public JSONArray selectSoftkeyTemplateList() {
		StringBuffer queryBuffer = new StringBuffer();
		queryBuffer.append("select pkid AS soft_pkid, name AS soft_tp_name , description  from softkeytemplate");
//		queryBuffer.append("select *  from softkeytemplate");
		return sendSoapMessage(cmAxlInfo, getQuerySoapMessage(queryBuffer.toString()));
	}
	
	// CSS 리스트 가져오기
	public JSONArray selectCSSList() {
		StringBuffer queryBuffer = new StringBuffer();
		queryBuffer.append("select pkid AS css_pkid , name AS css_name , description from callingsearchspace");
//		queryBuffer.append("select *  from callingsearchspace");
		return sendSoapMessage(cmAxlInfo, getQuerySoapMessage(queryBuffer.toString()));
	}
	
	// Route Partition 리스트 가져오기
	public JSONArray selectRoutePartitonList() {
		StringBuffer queryBuffer = new StringBuffer();
		queryBuffer.append("select pkid AS ptt_pkid, name AS partition_name , description from routepartition");
//		queryBuffer.append("select *  from routepartition");
		return sendSoapMessage(cmAxlInfo, getQuerySoapMessage(queryBuffer.toString()));
	}
	
	// Location 리스트 가져오기
	public JSONArray selectLocationList() {
		StringBuffer queryBuffer = new StringBuffer();
		queryBuffer.append("select pkid AS loc_pkid, name AS loc_name from location");
//		queryBuffer.append("select * from location");
		return sendSoapMessage(cmAxlInfo, getQuerySoapMessage(queryBuffer.toString()));
	}
	
	public JSONArray getDNInfo (String extension) {
		String query = (new StringBuilder("select pkid, dnorpattern, alertingname, description, fkroutepartition from numplan  where dnorpattern = '")).append(extension).append("'").toString();
		return sendSoapMessage(cmAxlInfo, getQuerySoapMessage(query));
	}
	public JSONArray selectDeviceList(String mac_address) {
		StringBuffer queryBuffer = new StringBuffer();
		queryBuffer.append("select name AS mac_address from device where name = 'SEP").append(mac_address).append("'");
//		queryBuffer.append("select * from location");
		return sendSoapMessage(cmAxlInfo, getQuerySoapMessage(queryBuffer.toString()));
		
	}
	public JSONArray updateDevice(String mac_address , String description , String fkDevicePool , String fkSoftkeyTemplate) {
		String query = (new StringBuilder("update device set description = '")).append(description).append("', fkdevicepool = '").append(fkDevicePool).append("', ").append("fksoftkeytemplate = '").append(fkSoftkeyTemplate).append("' where name = '").append(mac_address).append("'").toString();
		return sendSoapMessage(cmAxlInfo, getQuerySoapMessage(query));
	}
	
	
	
	public JSONArray updateLocale (String name , String language) {
		String fkCountry = "31";
        String fkUserLocale = "21";
        if(language.equals("ENG"))
        {
            fkCountry = "64";
            fkUserLocale = "1";
        }
		String query = (new StringBuilder("update device set tkuserlocale = '")).append(fkUserLocale).append("', tkcountry = '").append(fkCountry).append("' ").append(" where name = '").append(name).append("'").toString();
		return sendSoapMessage(cmAxlInfo, getQuerySoapMessage(query));
	}
	
	public JSONArray getDeviceNumplanMap (String mac_address) {
		if(!mac_address.startsWith("SEP")) {
			mac_address = "SEP" + mac_address;
		}
		String query =(new StringBuilder("select c.pkid, c.dnorpattern from device a, devicenumplanmap b, numplan c where a.name = '")).append(mac_address).append("' and a.pkid = b.fkDevice and b.numplanindex = 1 and b.fkNumplan = c.pkid").toString();
		return sendSoapMessage(cmAxlInfo, getQuerySoapMessage(query));
	}
	
	public JSONArray insertNumplan (String dn, String fkRoutePartition , String description , String alertingName , String alertingNameAscii , String fkCSS) {
		String query = (new StringBuilder("insert into numplan(dnorpattern, fkroutepartition, tkpatternusage, description, alertingname, alertingnameascii,\tfkcallingsearchspace_sharedlineappear,  fkcallingsearchspace_cfna, fkcallingsearchspace_cfnaint, cfaptduration, cfbintvoicemailenabled, cfnaintvoicemailenabled, cfnavoicemailenabled, cfurintvoicemailenabled, cfbvoicemailenabled,  cfurvoicemailenabled)  values('")).append(dn).append("', '").append(fkRoutePartition).append("', '2', '").append(description).append("', '").append(alertingName).append("', '").append(alertingNameAscii).append("', ").append(" '").append(fkCSS).append("', '").append(fkCSS).append("', '").append(fkCSS).append("', 60,").append(" 'f', 't', 't', 't', 'f', 't')").toString();
		return sendSoapMessage(cmAxlInfo, getQuerySoapMessage(query));
	}
	
	public JSONArray getNumplanPkid (String fkRoutePartition , String dn) {
		String query = new StringBuilder("select pkid, dnorpattern from numplan where tkpatternusage = 2 and fkroutepartition = '").append(fkRoutePartition).append("' and dnorpattern = '").append(dn).append("'").toString();
		return sendSoapMessage(cmAxlInfo, getQuerySoapMessage(query));
	}
	
	public JSONArray getNumplanPkid (String extension) {
		String query = new StringBuilder("select pkid, dnorpattern from numplan where tkpatternusage = 2 and dnorpattern = '").append(extension).append("'").toString();
		return sendSoapMessage(cmAxlInfo, getQuerySoapMessage(query));
	}
	
	public JSONArray getDevicesFromNumplanPkid (String linePkid) {
		String query = (new StringBuilder("select a.pkid, a.name from device a, devicenumplanmap b where b.fknumplan = '")).append(linePkid).append("' and b.fkdevice = a.pkid").toString();
		return sendSoapMessage(cmAxlInfo, getQuerySoapMessage(query));
	}
	
	public JSONArray deletePickupGroupLineMap (String fkNumplan) {
		String query = (new StringBuilder("delete from pickupgrouplinemap where fknumplan_line = '")).append(fkNumplan).append("'").toString();
		return sendSoapMessage(cmAxlInfo, getQuerySoapMessage(query));
	}
	
	public JSONArray updateCallforwardDynamic(String fkNumplan, String fkCSS) {
		String query = (new StringBuilder("update callforwarddynamic set fkcallingsearchspace_cfa = '")).append(fkCSS).append("' ").append(" where fkNumplan = '").append(fkNumplan).append("'").toString();
		return sendSoapMessage(cmAxlInfo, getQuerySoapMessage(query));
	}
	
	public JSONArray insertPickupGroupLinenumMap (String fkNumplan,String fkPickupGroup) {
		String query = (new StringBuilder("insert into pickupgrouplinemap(fknumplan_line, fkpickupgroup)  values('")).append(fkNumplan).append("', '").append(fkPickupGroup).append("')").toString();
		return sendSoapMessage(cmAxlInfo, getQuerySoapMessage(query));
	}
	
	public JSONArray getFkDevice (String mac_address) {
		String query = new StringBuilder("select pkid, name from device where name = '").append(mac_address).append("'").toString();
		return sendSoapMessage(cmAxlInfo, getQuerySoapMessage(query));
	}
	
	public JSONArray insertDeviceNumplanMap (String fkDevice, String label, String fkNumplan, String display,
			String el64mask, String displayAscii, String labelAscii) {
		String query = (new StringBuilder("delete from devicenumplanmap where fkDevice = '")).append(fkDevice).append("' and numplanindex = 1").toString();
		sendSoapMessage(cmAxlInfo, getQuerySoapMessage(query));
		query = (new StringBuilder("insert into devicenumplanmap(fkDevice, numplanindex, label, fkNumplan, display,  e164mask, labelAscii, displayAscii, maxnumcalls, busytrigger)  values('")).append(fkDevice).append("', 1, '").append(label).append("', '").append(fkNumplan).append("', '").append(display).append("', '").append(el64mask).append("', ").append(" '").append(labelAscii).append("', '").append(displayAscii).append("', 4, 2)").toString();
		return sendSoapMessage(cmAxlInfo, getQuerySoapMessage(query));
	}
	
	public JSONArray getFkEndUser(String userID) {
		String query = new StringBuilder("select pkid, userid from enduser where userid = '").append(userID).append("'").toString();
		return sendSoapMessage(cmAxlInfo, getQuerySoapMessage(query));
	}
	
	public JSONArray insertEndUserDeviceMap(String fkDevice , String fkEndUser) {
		String query = (new StringBuilder("insert into enduserdevicemap(fkdevice, fkenduser, tkuserassociation)  values('")).append(fkDevice).append("', '").append(fkEndUser).append("', 1)").toString();
		return sendSoapMessage(cmAxlInfo, getQuerySoapMessage(query));
	}
	
	public JSONArray insertEndUserDirGroupMap(String fkDirGroup ,String fkEndUser) {
		String query = (new StringBuilder("insert into enduserdirgroupmap(fkdirgroup, fkenduser)  values('")).append(fkDirGroup).append("', '").append(fkEndUser).append("')").toString();
		return sendSoapMessage(cmAxlInfo, getQuerySoapMessage(query));
	}
	
	public JSONArray getDirGroup () {
		String query = "select pkid, name from dirgroup";
		return sendSoapMessage(cmAxlInfo, getQuerySoapMessage(query));
	}
	
	public JSONArray getDeviceLineInfo(String mac_address) {
		String query = (new StringBuilder("select b.pkid, b.dnorpattern, b.alertingname, b.description, b.fkroutepartition from device a, numplan b, devicenumplanmap c  where a.name = '")).append(mac_address).append("' and a.pkid = c.fkdevice and c.fknumplan = b.pkid and c.numplanindex = 1").toString();
		return sendSoapMessage(cmAxlInfo, getQuerySoapMessage(query));
	}
	
	
	
	/* **************************************************************************************
	 * 
	 * phone xml 로그인시 사용
	 * 
	 */
	public JSONArray selectLoginDeviceInfo(String device_name) {
		StringBuffer queryBuffer = new StringBuffer();
		queryBuffer
			.append("SELECT a.pkid fkdevice, a.name, a.tkcountry, a.tkuserlocale, a.tkmodel ")
			.append(", b.pkid fknumplan, b.dnorpattern, b.cfnaduration, b.cfnavoicemailenabled, b.cfnaintdestination, b.iscallable ")
			.append(", b.cfurintvoicemailenabled, b.cfurvoicemailenabled, b.alertingname, b.description, b.fkroutepartition ")
			.append(", c.busytrigger, c.label, c.display, c.displayAscii, c.e164mask, c.displayAscii, c.labelAscii ")
			.append("FROM device a, numplan b, devicenumplanmap c WHERE a.name = '").append(device_name).append("' ")
			.append("AND a.pkid = c.fkdevice AND c.fknumplan = b.pkid AND c.numplanindex = 1");
		return sendSoapMessage(cmAxlInfo , getQuerySoapMessage(queryBuffer.toString())) ;
	}
	public JSONArray updateNumplanIsCallable(String extension) {
		StringBuffer queryBuffer = new StringBuffer();
		queryBuffer
			.append("update numplan set iscallable = 't', cfurintvoicemailenabled = 't', cfurvoicemailenabled = 't' where dnorpattern = '").append(extension).append("'");
		return sendSoapMessage(cmAxlInfo , getQuerySoapMessage(queryBuffer.toString())) ;
	}
	
	
	public JSONArray updaTetelecasterInit(String fkDevice) {
		StringBuffer queryBuffer = new StringBuffer();
		queryBuffer
			.append("UPDATE telecastersubscribedservice SET urlbuttonindex = 0 WHERE fkDevice = '")
			.append(fkDevice).append("'");
		return sendSoapMessage(cmAxlInfo , getQuerySoapMessage(queryBuffer.toString())) ;
	}
	public JSONArray updaTetelecasterLoginout(String fkDevice, String logInOut, String position) {
		StringBuffer queryBuffer = new StringBuffer();
		queryBuffer
			.append("UPDATE telecastersubscribedservice SET urlbuttonindex = ").append(position)
			.append(", urllabel = '").append(logInOut).append("' , urllabelascii = '").append(logInOut).append("' ")
			.append("WHERE fkDevice = '").append(fkDevice).append("' AND servicename = '").append(logInOut).append("'");
			
		return sendSoapMessage(cmAxlInfo , getQuerySoapMessage(queryBuffer.toString())) ;
	}
	
	public JSONArray deleteDeviceNumplanMap(String fkDevice) {
		StringBuffer queryBuffer = new StringBuffer();
		queryBuffer
			.append("DELETE FROM devicenumplanmap WHERE fkDevice = '").append(fkDevice).append("' AND numplanindex = 1");
		return sendSoapMessage(cmAxlInfo , getQuerySoapMessage(queryBuffer.toString())) ;
	}
	public JSONArray insertDeviceNumplanMap01(String fkDevice, String label, String fkNumplan
			, String display, String e164mask, String labelAscii, String displayAscii) {
		StringBuffer queryBuffer = new StringBuffer();
		queryBuffer
			.append("INSERT INTO devicenumplanmap(fkDevice, numplanindex, label, fkNumplan, display, e164mask, labelAscii, displayAscii, maxnumcalls, busytrigger) ")
			.append("VALUES('").append(fkDevice).append("',1,'").append(label).append("','").append(fkNumplan).append("','").append(display).append("','")
			.append(e164mask).append("','").append(labelAscii).append("','").append(displayAscii).append("', 4, 2)");
		return sendSoapMessage(cmAxlInfo , getQuerySoapMessage(queryBuffer.toString())) ;
	}
	public JSONArray updateDeviceNumplanMap(String fkDevice, String label, String fkNumplan
			, String display, String e164mask, String labelAscii, String displayAscii) {
		StringBuffer queryBuffer = new StringBuffer();
		queryBuffer
			.append("UPDATE devicenumplanmap SET label = '").append(label).append("', fknumplan = '").append(fkNumplan).append("', display = '").append(display)
			.append("', e164mask = '").append(e164mask).append("', displayAscii = '").append(displayAscii).append("', labelAscii = '").append(labelAscii)
			.append("', maxnumcalls = 4, busytrigger = 2 ")
			.append("WHERE fkDevice = '").append(fkDevice).append("' AND numplanIndex = 1");
		return sendSoapMessage(cmAxlInfo , getQuerySoapMessage(queryBuffer.toString())) ;
	}
	
	public JSONArray callForwardAll(String fknumplan, String cfavoicemailenabled, String cfadestination) {
		StringBuffer queryBuffer = new StringBuffer();
		queryBuffer
			.append("update callforwarddynamic set cfavoicemailenabled = '").append(cfavoicemailenabled)
			.append("', cfadestination = '").append(cfadestination)
			.append("' where fknumplan = '").append(fknumplan).append("'");
		return sendSoapMessage(cmAxlInfo , getQuerySoapMessage(queryBuffer.toString())) ;
	}
	
	public JSONArray callForwardNA(String cfnaduration, String cfnavoicemailenabled, String cfnaintdestination
			, String cfnaintvoicemailenabled, String cfnadestination, String pkid) {
		StringBuffer queryBuffer = new StringBuffer();
		queryBuffer
			.append("update numplan set cfnaduration = ").append(cfnaduration)
			.append(", cfnavoicemailenabled = '").append(cfnavoicemailenabled)
			.append("', cfnaintdestination = '").append(cfnaintdestination)
			.append("', cfnaintvoicemailenabled = '").append(cfnaintvoicemailenabled)
			.append("', cfnadestination = '").append(cfnadestination)
			.append("' where pkid = '").append(pkid).append("'");
		return sendSoapMessage(cmAxlInfo , getQuerySoapMessage(queryBuffer.toString())) ;
	}
	
	public JSONArray setBusyTrigger(String busyTrigger, String fkdevice) {
		StringBuffer queryBuffer = new StringBuffer();
		queryBuffer
			.append("update devicenumplanmap set busytrigger = '").append(busyTrigger)
			.append("' where fkdevice = '").append(fkdevice).append("'");
		return sendSoapMessage(cmAxlInfo , getQuerySoapMessage(queryBuffer.toString())) ;
	}
	
	public JSONArray setLineTextLabel(String fkdevice, String fknumplan, String label) {
		StringBuffer queryBuffer = new StringBuffer();
		queryBuffer
			.append("update devicenumplanmap set label= '").append(label).append("'")
			.append(", display = '").append(label).append("'")
			.append(" where fknumplan = '").append(fknumplan).append("'")
			.append(" and  fkdevice = '").append(fkdevice).append("'");
		return sendSoapMessage(cmAxlInfo , getQuerySoapMessage(queryBuffer.toString())) ;
	}
	
	public JSONArray deleteSpeeddial(String fkdevice) {
		StringBuffer queryBuffer = new StringBuffer();
		queryBuffer
			.append("delete from speeddial where fkDevice = '").append(fkdevice).append("'");
		return sendSoapMessage(cmAxlInfo , getQuerySoapMessage(queryBuffer.toString())) ;
	}
	public JSONArray insertSpeedDial(String fkdevice, String label, long speeddialIndex, String speeddialNumber) {
		StringBuffer queryBuffer = new StringBuffer();
		queryBuffer
			.append("insert into speeddial(fkDevice, label, speeddialindex, speeddialNumber) values(")
			.append("'").append(fkdevice).append("', '").append(label).append("', ").append(speeddialIndex).append(", '").append(speeddialNumber).append("')");
		return sendSoapMessage(cmAxlInfo , getQuerySoapMessage(queryBuffer.toString())) ;
	}
	
	
	
	public String getPhoneMask(String dn)
    {
        String phoneMask = "";
        if(dn.length() == 4)
            if(dn.startsWith("00") || dn.startsWith("01") || dn.startsWith("14") || dn.startsWith("15") || dn.startsWith("16") || dn.startsWith("17") || dn.startsWith("23") || dn.startsWith("25") || dn.startsWith("30") || dn.startsWith("31") || dn.startsWith("32") || dn.startsWith("34") || dn.startsWith("9"))
                phoneMask = "3781XXXX";
            else
            if(dn.startsWith("02") || dn.startsWith("03") || dn.startsWith("04") || dn.startsWith("05") || dn.startsWith("06") || dn.startsWith("07") || dn.startsWith("08") || dn.startsWith("09") || dn.startsWith("33") || dn.startsWith("40") || dn.startsWith("47") || dn.startsWith("64") || dn.startsWith("70") || dn.startsWith("79") || dn.startsWith("80") || dn.startsWith("81") || dn.startsWith("82") || dn.startsWith("83") || dn.startsWith("84") || dn.startsWith("85") || dn.startsWith("87") || dn.startsWith("88") || dn.startsWith("89"))
                phoneMask = "709XXXX";
            else
            	phoneMask = "444XXXX";	// TEST wiseo : dn.startsWith("10")
        
        if(dn.length() == 3 && dn.startsWith("4"))
            phoneMask = "6400XXX";
        else
        if(dn.length() == 3 && dn.startsWith("6"))
            phoneMask = "6309XXX";
        
        return phoneMask;
    }
	/*
	 ************************************************************************************** */
	
	
	private  String getQuerySoapMessage (String query) {
		
		
		StringBuffer soapReqMessage = new StringBuffer();
		
		query = query.trim();
		
		if(query.startsWith("select") || query.startsWith("SELECT")) {
			soapReqMessage.append("<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" ");
			soapReqMessage.append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ");
			soapReqMessage.append("xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"> ");
			soapReqMessage.append("<SOAP-ENV:Body> ").append("\n");
			soapReqMessage.append("<axlapi:executeSQLQuery xmlns:axlapi=\"http://www.cisco.com/AXL/API/"+cmVer+"\" sequence=\"1234\"> ").append("\n");
			soapReqMessage.append("<sql>").append("\n").append(query).append("\n").append("</sql> ").append("\n");
			soapReqMessage.append("</axlapi:executeSQLQuery> ").append("\n");
			soapReqMessage.append("</SOAP-ENV:Body> ").append("\n");
			soapReqMessage.append("</SOAP-ENV:Envelope>");
		} else {
			soapReqMessage.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns=\"http://www.cisco.com/AXL/API/"+cmVer+"\">").append("\n");
			soapReqMessage.append("<soapenv:Header/>").append("\n");
			soapReqMessage.append("<soapenv:Body>").append("\n");
			soapReqMessage.append("<ns:executeSQLUpdate sequence=\"?\">").append("\n");
			soapReqMessage.append("<sql>").append(query).append("</sql>").append("\n");
			soapReqMessage.append("</ns:executeSQLUpdate>").append("\n");
			soapReqMessage.append("</soapenv:Body>").append("\n");
			soapReqMessage.append("</soapenv:Envelope>").append("\n");
		}
		
		
		return soapReqMessage.toString();
	}
	
	
	private  JSONArray sendSoapMessage(CmAxlInfoModel cmAxlInfo , String soapReqMessage ) {
		
		
		JSONObject resultJsonObj = new JSONObject();
		
		String strResult = SoapHandler.RequestSoap(cmVer, cmAxlInfo.getCmID(), cmAxlInfo.getCmPwd(), cmAxlInfo.getCmIP(), "8443", soapReqMessage.toString(), "executeSQLQuery");
		
		String responseKey = "";
		if(soapReqMessage.indexOf("axlapi:executeSQLQuery") > 0) {
			// SELECT Request
			return jsonQueryResParsing(strResult);
		} else {
			// UPDATE Request
			return jsonUpdateResParsing(strResult);
		}
		
	}
	
	
	private JSONArray jsonUpdateResParsing(String strResult) {
		// TODO Auto-generated method stub
		JSONArray resultObj = new JSONArray();
		JSONObject xmlJson = null;
		
		String responseKey = "ns:executeSQLUpdateResponse";
		
		try {
			
			xmlJson = XML.toJSONObject(strResult);
			System.out.println(xmlJson.toString(4));

			if (xmlJson.getJSONObject("soapenv:Envelope").getJSONObject("soapenv:Body")
					.getJSONObject(responseKey).get("return").equals("")) {
				return resultObj;
			} 

			JSONObject chkJson = xmlJson.getJSONObject("soapenv:Envelope").getJSONObject("soapenv:Body")
					.getJSONObject(responseKey).getJSONObject("return");
			
			resultObj.put(chkJson);

		} catch (Exception e) {
			System.out.println(e.toString());
			JSONObject chkJson = xmlJson.getJSONObject("soapenv:Envelope").getJSONObject("soapenv:Body")
					.getJSONObject("soapenv:Fault").getJSONObject("detail").getJSONObject("axlError");
			resultObj.put(chkJson);
			return resultObj;
		} finally {
			
		}
		return resultObj;
	}

	private JSONArray jsonQueryResParsing(String strResult) {
		// TODO Auto-generated method stub
		JSONArray resultObj = new JSONArray();
		JSONObject xmlJson = null;
		
		String responseKey = "ns:executeSQLQueryResponse";
		try {
			
			xmlJson = XML.toJSONObject(strResult);
			System.out.println(xmlJson.toString(4));

			if (xmlJson.getJSONObject("soapenv:Envelope").getJSONObject("soapenv:Body")
					.getJSONObject(responseKey).get("return").equals("")) {
				return resultObj;
			} 

			JSONObject chkJson = xmlJson.getJSONObject("soapenv:Envelope").getJSONObject("soapenv:Body")
					.getJSONObject(responseKey).getJSONObject("return");

			Object test = chkJson.get("row");

			if (test instanceof JSONObject) {
				JSONObject jsonData = chkJson.getJSONObject("row");
				resultObj.put(jsonData);
			} else if (test instanceof JSONArray) {
				resultObj = chkJson.getJSONArray("row");
			}

		} catch (Exception e) {
			System.out.println(e.toString());
			JSONObject chkJson = xmlJson.getJSONObject("soapenv:Envelope").getJSONObject("soapenv:Body")
					.getJSONObject("soapenv:Fault").getJSONObject("detail").getJSONObject("axlError");
			resultObj.put(chkJson);
			return resultObj;
		} finally {
			
		}
		return resultObj;
	}
	
	public int doDeviceReset(String pkid, String mac_address) {
		int rtn = 0;
		StringBuffer soapReqMessage = new StringBuffer();
		
		soapReqMessage.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns=\"http://www.cisco.com/AXL/API/8.5\"> ").append("\n");
		soapReqMessage.append("<soapenv:Header/> ").append("\n");
		soapReqMessage.append("<soapenv:Body> ").append("\n");
		soapReqMessage.append("<ns:doDeviceReset sequence=\"\" isMGCP=\"false\"> ").append("\n");
		soapReqMessage.append("<deviceName uuid=\"").append(pkid).append("\">").append(mac_address).append("</deviceName> ").append("\n");
		soapReqMessage.append("<isHardReset>false</isHardReset> ").append("\n");
		soapReqMessage.append("</ns:doDeviceReset> ").append("\n");
		soapReqMessage.append("</soapenv:Body> ").append("\n");
		soapReqMessage.append("</soapenv:Envelope>");
		
		String strResult = SoapHandler.RequestSoap(cmVer, cmAxlInfo.getCmID(), cmAxlInfo.getCmPwd(), cmAxlInfo.getCmIP(), "8443", soapReqMessage.toString(), "doDeviceReset");
		
		//JSONArray resultObj = new JSONArray();
		JSONObject xmlJson = null;
		
		String responseKey = "ns:doDeviceResetResponse";
		try {
			
			xmlJson = XML.toJSONObject(strResult);
			System.out.println("ns:doDeviceResetResponse >>>> "+xmlJson.toString(4));

			if (xmlJson.getJSONObject("soapenv:Envelope").getJSONObject("soapenv:Body")
					.getJSONObject(responseKey).get("return").equals("")) {
				rtn = -1;
			}else{
				rtn = 1;
			}

		} catch (Exception e) {
			System.out.println(e.toString());
			/*JSONObject chkJson = xmlJson.getJSONObject("soapenv:Envelope").getJSONObject("soapenv:Body")
					.getJSONObject("soapenv:Fault").getJSONObject("detail").getJSONObject("axlError");*/
			return rtn -2;
		} finally {
			
		}
		
		return rtn;
	}
		
	

	private static Object selectDeviceInfoJSON01(CmAxlInfoModel cmAxlInfo) {
		
		Object rtnObject = null;
		
		StringBuffer soapReqMessage = new StringBuffer();
		
//	     strSoapReqeust +="<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" ";
//	        strSoapReqeust +="xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ";
//	        strSoapReqeust +="xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"> ";
//	        strSoapReqeust +="<SOAP-ENV:Body> ";
//	        strSoapReqeust +="<axlapi:executeSQLQuery xmlns:axlapi=\"http://www.cisco.com/AXL/API/"+dbVer+"\" sequence=\"1234\"> ";
//	        strSoapReqeust +="<sql> SELECT name, tkClass FROM device WHERE name LIKE \'" + aLike + "%\' </sql> ";
//	        strSoapReqeust +="</axlapi:executeSQLQuery> ";
//	        strSoapReqeust +="</SOAP-ENV:Body> ";
//	        strSoapReqeust +="</SOAP-ENV:Envelope>";
//		
		 //
		soapReqMessage.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns=\"http://www.cisco.com/AXL/API/"+cmVer+"\">\n");
		soapReqMessage.append("<soapenv:Header/>\n");
		soapReqMessage.append("<soapenv:Body>\n");
		soapReqMessage.append("<ns:executeSQLQuery>\n");
		soapReqMessage.append("<sql>\n");
		soapReqMessage.append("SELECT name, tkClass FROM device \n");
		soapReqMessage.append("</sql>\n");
		soapReqMessage.append("</ns:executeSQLQuery>\n");
		soapReqMessage.append("</soapenv:Body>\n");
		soapReqMessage.append("</soapenv:Envelope>\n");
		
		//System.out.println("aa = "+strXML);
		
		String strResult = SoapHandler.RequestSoap(cmVer, cmAxlInfo.getCmID(), cmAxlInfo.getCmPwd(), cmAxlInfo.getCmIP(), "8443", soapReqMessage.toString(), "executeSQLQuery");
		System.out.println("json return");
		System.out.println(strResult);
		
		try {
			JSONObject xmlJson = XML.toJSONObject(strResult);
			String jsonP = xmlJson.toString(4);
			System.out.println(jsonP);
			if(jsonP.equals("{}")){
				return rtnObject;
			}
			
//			Iterator iter = xmlJson.keys();
//			while(iter.hasNext()) {
//				
//				if(iter.next() instanceof JSONObject) {
//					System.out.println("** JSONObject **");
//				}
//				
//				String key =  iter.next().toString();
//				System.out.println(key + " : " + xmlJson.getString(key));
//			}
			
			if(xmlJson.getJSONObject("soapenv:Envelope")
					.getJSONObject("soapenv:Body")
					.getJSONObject("ns:executeSQLQueryResponse").get("return").equals("")){
				
				return rtnObject;
			}else{
			}
			
			JSONObject chkJson = xmlJson.getJSONObject("soapenv:Envelope")
					.getJSONObject("soapenv:Body")
					.getJSONObject("ns:executeSQLQueryResponse")
					.getJSONObject("return");
			
			Object test = chkJson.get("row");
			
			if(test instanceof JSONObject){
				JSONObject jsonData = chkJson.getJSONObject("row");
				rtnObject = jsonData;
			}else if(test instanceof JSONArray){
				JSONArray jsonArray = chkJson.getJSONArray("row");
				rtnObject = jsonArray;
			}
			//System.out.println(">>>> "+jsonArray.length());
		} catch (JSONException e) {
			System.out.println(e.toString());
		}
		
		return rtnObject;
	}
	/*
	 * AXL API 이용하여 정보 조회
	 * 	DN으로 mac, dn, dn index를 조회
	 */
	/*
	public static List<SxmlDeviceStatusVO> selectListMacIntoDn(
			String cmVer, String cmIp, String cmId, String cmPwd, String dn){
		
		List<SxmlDeviceStatusVO> lstDeviceInfo = null;
		
		Object jsonDevice = selectDeviceInfoJSON01(cmVer, cmIp, cmId, cmPwd, dn);
		//System.out.println("jsonDevice.length() = "+jsonDevice.length());
		
		if(jsonDevice != null){
			lstDeviceInfo = selectListDevice(jsonDevice, cmIp, cmId, cmPwd);
		}
		
		return lstDeviceInfo;
	}
	*/
	/*
	 * AXL 조회 RequestSoap
	 */
	/*
	private static Object selectDeviceInfoJSON01(
			String cmVer, String cmIp, String cmId, String cmPwd, String dn) {
		
		Object rtnObject = null;
		
		String strXML = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns=\"http://www.cisco.com/AXL/API/"+cmVer+"\">\n";
		strXML += "<soapenv:Header/>\n";
		strXML += "<soapenv:Body>\n";
		strXML += "<ns:executeSQLQuery>\n";
		strXML += "<sql>\n";
		strXML += "SELECT \n";
		strXML += "		d.name, dn.numplanindex, n.dnorpattern \n";
		strXML += "FROM \n";
		strXML += "		DEVICE d, DEVICENUMPLANMAP dn, NUMPLAN n \n";
		strXML += "WHERE \n";
		strXML += "		n.tkpatternusage = '2' \n";
		strXML += "		AND d.name like 'SEP%' \n";
		strXML += "		AND d.pkid = dn.fkdevice \n";
		strXML += "		AND dn.fknumplan = n.pkid \n";
		if(dn != null && !dn.equals("")){
			strXML += "		AND n.dnorpattern = '"+dn+"' \n";
		}
		strXML += "ORDER BY \n";
		strXML += "		dn.numplanindex ASC \n";
		strXML += "</sql>\n";
		strXML += "</ns:executeSQLQuery>\n";
		strXML += "</soapenv:Body>\n";
		strXML += "</soapenv:Envelope>\n";
		
		//System.out.println("aa = "+strXML);
		
		String strResult = SoapHandler.RequestSoap(cmId, cmPwd, cmIp, "8443", strXML, "executeSQLQuery");
		//System.out.println(dn + " = bb = "+strResult);
		
		try {
			JSONObject xmlJson = XML.toJSONObject(strResult);
			String jsonP = xmlJson.toString(4);
			//System.out.println(cmIp+" / "+dn+" = "+jsonP);
			if(jsonP.equals("{}")){
				return rtnObject;
			}
			
			if(xmlJson.getJSONObject("soapenv:Envelope")
					.getJSONObject("soapenv:Body")
					.getJSONObject("ns:executeSQLQueryResponse").get("return").equals("")){
				
				return rtnObject;
			}else{
			}
			
			JSONObject chkJson = xmlJson.getJSONObject("soapenv:Envelope")
					.getJSONObject("soapenv:Body")
					.getJSONObject("ns:executeSQLQueryResponse")
					.getJSONObject("return");
			
			Object test = chkJson.get("row");
			
			if(test instanceof JSONObject){
				JSONObject jsonData = chkJson.getJSONObject("row");
				rtnObject = jsonData;
			}else if(test instanceof JSONArray){
				JSONArray jsonArray = chkJson.getJSONArray("row");
				rtnObject = jsonArray;
			}
			
			//System.out.println(">>>> "+jsonArray.length());
		} catch (JSONException e) {
			System.out.println(e.toString());
		}
		
		return rtnObject;
	}
	*/
	/*
	@SuppressWarnings("unused")
	private static Object selectDeviceInfoJSON(
			String cmVer, String cmIp, String cmId, String cmPwd, String dn) {
		
		Object rtnObject = null;
		
		String strXML = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns=\"http://www.cisco.com/AXL/API/"+cmVer+"\">\n";
		strXML += "<soapenv:Header/>\n";
		strXML += "<soapenv:Body>\n";
		strXML += "<ns:executeSQLQuery>\n";
		strXML += "<sql>\n";
		strXML += "SELECT \n";
		strXML += "		d.name, dn.numplanindex, n.dnorpattern \n";
		strXML += "FROM \n";
		strXML += "		DEVICE d, DEVICENUMPLANMAP dn, NUMPLAN n \n";
		strXML += "WHERE \n";
		strXML += "		n.tkpatternusage = '2' \n";
		strXML += "		AND d.name like 'SEP%' \n";
		strXML += "		AND d.pkid = dn.fkdevice \n";
		strXML += "		AND dn.fknumplan = n.pkid \n";
		if(dn != null && !dn.equals("")){
			strXML += "		AND n.dnorpattern = '"+dn+"' \n";
		}
		strXML += "ORDER BY \n";
		strXML += "		dn.numplanindex ASC \n";
		strXML += "</sql>\n";
		strXML += "</ns:executeSQLQuery>\n";
		strXML += "</soapenv:Body>\n";
		strXML += "</soapenv:Envelope>\n";
		
		//System.out.println(dn+" = "+strXML);
		
		String strResult = SoapHandler.RequestSoap(cmVer, cmId, cmPwd, cmIp, "8443", strXML, "executeSQLQuery");
		//System.out.println("bb = "+strResult);
		
		try {
			JSONObject xmlJson = XML.toJSONObject(strResult);
			String jsonP = xmlJson.toString(4);
			//System.out.println(jsonP);
			
			JSONObject chkJson = xmlJson.getJSONObject("soapenv:Envelope")
					.getJSONObject("soapenv:Body")
					.getJSONObject("ns:executeSQLQueryResponse")
					.getJSONObject("return");
			
			Object test = chkJson.get("row");
			
			if(test instanceof JSONObject){
				System.out.println("11 = Object");
				JSONObject jsonData = chkJson.getJSONObject("row");
				rtnObject = jsonData;
			}else if(test instanceof JSONArray){
				System.out.println("22 = Array");
				JSONArray jsonArray = chkJson.getJSONArray("row");
				rtnObject = jsonArray;
			}
			
			//System.out.println(">>>> "+jsonArray.length());
		} catch (JSONException e) {
			System.out.println(e.toString());
		}
		
		return rtnObject;
	}
	*/
	/*
	 * Device List
	 */
	/*
	private static List<SxmlDeviceStatusVO> selectListDevice(Object test, String cmIp, String cmId, String cmPwd){
		List<SxmlDeviceStatusVO> deviceList = new ArrayList<SxmlDeviceStatusVO>();
    	
		if(test instanceof JSONObject){
			JSONObject jsonDeviceObject = (JSONObject) test;
			SxmlDeviceStatusVO insertVO = new SxmlDeviceStatusVO();
			
			System.out.println("dnorpattern = "+jsonDeviceObject.getInt("dnorpattern"));
			System.out.println("name = "+jsonDeviceObject.getString("name"));
			System.out.println("numplanindex = "+jsonDeviceObject.getInt("numplanindex"));
			
			insertVO.setCmIp(cmIp);
			insertVO.setCmUser(cmId);
			insertVO.setCmPwd(cmPwd);
			insertVO.setDn(String.valueOf(jsonDeviceObject.getInt("dnorpattern")));
			insertVO.setMac(jsonDeviceObject.getString("name"));
			insertVO.setNumplanindex(jsonDeviceObject.getInt("numplanindex"));
			
			deviceList.add(insertVO);
		}else if(test instanceof JSONArray){
			JSONArray jsonDeviceArray = (JSONArray) test;
			//JSON 배열 데이터를 자바 List에 넣기 
			for(int i=0; i<jsonDeviceArray.length(); i++){
				SxmlDeviceStatusVO insertVO = new SxmlDeviceStatusVO();
				insertVO.setCmIp(cmIp);
				insertVO.setCmUser(cmId);
				insertVO.setCmPwd(cmPwd);
				insertVO.setDn(String.valueOf(jsonDeviceArray.getJSONObject(i).getInt("dnorpattern")));
				insertVO.setMac(jsonDeviceArray.getJSONObject(i).getString("name"));
				insertVO.setNumplanindex(jsonDeviceArray.getJSONObject(i).getInt("numplanindex"));
				
				deviceList.add(insertVO);
			}
		}
		
		return deviceList;
	}
	*/
}