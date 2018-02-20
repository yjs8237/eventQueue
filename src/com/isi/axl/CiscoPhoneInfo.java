package com.isi.axl;


import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.isi.constans.LOGLEVEL;
import com.isi.constans.LOGTYPE;
import com.isi.constans.PROPERTIES;
import com.isi.constans.SVCTYPE;
import com.isi.file.GLogWriter;
import com.isi.file.ILog;
import com.isi.file.PropertyRead;
import com.test.vo.CmAxlInfoModel;



public class CiscoPhoneInfo {

    private Map mapNametoIP = new HashMap();
    private Map mapDnToIP = new HashMap();
    private Map mapIPToModel = new HashMap();
    private Map mapNameToModel = new HashMap();
    private Map mapIPToName = new HashMap();
    private ILog m_Log ;
    private PropertyRead pr;
    private PrintWriter pw;
    private StringWriter sw;
    
    public CiscoPhoneInfo (){
    	pr = PropertyRead.getInstance();
    	m_Log = new GLogWriter();
    	sw = new StringWriter();
    	pw = new PrintWriter(sw);
    }
    
    public void setITermNameIP(String ip, String terminal) {
        mapIPToName.put(ip, terminal);
    }
    public void setIPTermName(String terminal, String ip) {
        mapNametoIP.put(terminal, ip);
    }
    public void setIPDeviceNumber(String dn, String ip) {
        mapDnToIP.put(dn, ip);
    }
    public void setIPModel(String ip, String model) {
        mapIPToModel.put(ip, model);
    }
    public void setModelTermName(String terminal, String model) {
        mapNameToModel.put(terminal, model);
    }
    public int getTotalCount(){
        return mapDnToIP.size();
    }
    public String getTermNamebyIP(String IP) {

        return (String)mapIPToName.get(IP);
    }
    public String getIPbyTermName(String terminal) {

        return (String)mapNametoIP.get(terminal);
    }
    public String getIPbyDeviceNumber(String dn) {

        return (String)mapDnToIP.get(dn);
    }
    public String getModelByIP(String ip) {
        return (String)mapIPToModel.get(ip);
    }

    public String getModelByTermName(String terminal) {
        return (String)mapNameToModel.get(terminal);
    }
    public String [] getNames() {
        String [] names = new String[mapDnToIP.size()];
        Set col = mapDnToIP.keySet();
        int idx = 0;
        Iterator i = col.iterator();
        
        while(i.hasNext()) {
            names[idx] = (String)i.next();
            idx++;
        }
        return names;
    }
    /*
    public int GetPhoneInfoText(CiscoPhoneInfo PhoneInfo) {

        FileReader fr = null;
        BufferedReader br = null;
        String readedline;
        String Name, IpAddress, DirNumber, Model;

        try {
            fr = new FileReader(m_DeviceFilePath);
            br = new BufferedReader(fr);
            readedline = br.readLine();

            while (readedline != null ) {

                    try {

                        String [] data = CheckFunc.TokenizerString(readedline, ",");

                        if (data.length == 3 ) {

                            DirNumber  = data[0].trim();
                            IpAddress  = data[1].trim();
                            Model      = data[2].trim();
                            Name       = "SEPXXXXXXXXXX";

                            PhoneInfo.setIPModel(IpAddress, Model);
                            PhoneInfo.setIPDeviceNumber(DirNumber, IpAddress);
                            PhoneInfo.setIPTermName(Name, IpAddress);
                            PhoneInfo.setITermNameIP(IpAddress, Name);
                        }
                        readedline = br.readLine();
                    } catch(Exception e) {
                    }
            }
            br.close();
            fr.close();
        } catch (Exception e) {

        }
        return 0;
    }
*/
    
    
    public static int axlTest (CmAxlInfoModel model ) {
    	
    	   X509TrustManager xtm = new SoapTrustManager();
           TrustManager[] mytm = { xtm };

           try {
               Vector DeviceNames = new Vector();
               SSLContext ctx = SSLContext.getInstance("SSL");
               ctx.init(null, mytm, null);
               
               AdministrativeXML AXL = new AdministrativeXML(model.getCmIP(), model.getCmPort(), model.getCmID(), model.getCmPwd(), ctx);
               AXL.SoapTest(model);
           } catch (Exception e) {
        	   
           }
    	
    	
    	
    	return 0;
    }
    
    public static int GetAllPhoneInfo (String cmip, int cmport, String cmid, String cmpwd, CiscoPhoneInfo PhoneInfo) {

        String 	cmIp    = cmip;
        int 	cmPort  = 8443;
        String	cmID    = cmid;
        String 	cmPwd   = cmpwd;

        X509TrustManager xtm = new SoapTrustManager();
        TrustManager[] mytm = { xtm };

        try {
            Vector DeviceNames = new Vector();
            SSLContext ctx = SSLContext.getInstance("SSL");
            ctx.init(null, mytm, null);

            AdministrativeXML AXL = new AdministrativeXML(cmIp, cmPort, cmID, cmPwd, ctx);
            AXL.GetDeviceNameList("SEP", DeviceNames);

            ServiceabilityXML SXL = new ServiceabilityXML(cmIp, cmPort, cmID, cmPwd, ctx);
            int repeat = DeviceNames.size();

            String DeviceList = "";
            int i = 0;

             // 한번에 전송하는 크기르르 100으로 낮춤
            // CM에서 최대 200개의 Device의 정보만 담아서 전송해주기 때문에 문제 발생.
            // 1분에 15회만 응답함. 요청간격을 4초로 두어 500 Internal 에러 방지.

            // Himart의 경우 2500 대면 25 * 4 = 100초의 시간이 IP를 가져오는데 소요됨.
            
            
            for (; i < repeat; i++) {

                if ((i + 1) % 100 == 0) {
                    DeviceList += DeviceNames.elementAt(i);
                    SXL.GetDeviceInfo(DeviceList, PhoneInfo);
//                    m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, SVCTYPE.GLOBAL, "GetAllPhoneInfo", DeviceList);
                    DeviceList = "";
                    Thread.sleep(4000);
                } else {
                    DeviceList += DeviceNames.elementAt(i) + ", ";
                }
            }
            if (i % 10 != 100 && DeviceList.length() > 3 ) {
                DeviceList = DeviceList.substring(0, DeviceList.lastIndexOf(","));
                SXL.GetDeviceInfo(DeviceList, PhoneInfo);
                
//                m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, SVCTYPE.GLOBAL, "GetAllPhoneInfo", DeviceList);
            }
            PhoneInfo.getNames();
        } catch (Exception e) {
//        	e.printStackTrace(pw);
//        	m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.ERR_LOG, SVCTYPE.GLOBAL, "GetAllPhoneInfo", sw.toString());
            return -1;
        }
        return 0;
    }
//    
//    // 헌트그룹 로그인 유무 확인 (2013-10-20 jylee)
//    public static String GetHuntGroupYN(String cmip, int cmport, String cmid, String cmpwd, String dn) {
//        String CMIP = "192.168.230.11";
//        int CMPORT = 8443;
//        String CMID = "ccmadmin";
//        String CMPWD = "lhy623123";
//        String strRtn = "";
//
//        CMIP = cmip;
//        CMPORT = 8443;
//        CMID = cmid;
//        CMPWD = cmpwd;
//
//        X509TrustManager xtm = new SoapTrustManager();
//        TrustManager[] mytm = {xtm};
//
//        try {
//            SSLContext ctx = SSLContext.getInstance("SSL");
//            ctx.init(null, mytm, null);
//            AdministrativeXML AXL = new AdministrativeXML(CMIP, CMPORT, CMID, CMPWD, ctx);
//            strRtn = AXL.GetHuntGroupYN(dn);
//            m_Log.server("[GetHuntGroupYN] strRtn: " + strRtn);
//        } catch (Exception e) {
//            strRtn = e.getMessage();
//            m_Log.server("[GetHuntGroupYN] Exception: " + strRtn);
//        }
//        return strRtn;
//    }
    
    // 헌트그룹 로그인 (2013-10-20 jylee)
//    public static int SetHuntGroupYN(String cmip, int cmport, String cmid, String cmpwd, String dn, String hlog) {
//        String CMIP = "192.168.230.11";
//        int CMPORT = 8443;
//        String CMID = "ccmadmin";
//        String CMPWD = "lhy623123";
//        int iRtn = IResult.ERR_DEV_SET_HUNT;        // 헌트그룹 로그인 실패 입니다.
//
//        CMIP = cmip;
//        CMPORT = 8443;
//        CMID = cmid;
//        CMPWD = cmpwd;
//
//        X509TrustManager xtm = new SoapTrustManager();
//        TrustManager[] mytm = {xtm};
//
//        try {
//            SSLContext ctx = SSLContext.getInstance("SSL");
//            ctx.init(null, mytm, null);
//            AdministrativeXML AXL = new AdministrativeXML(CMIP, CMPORT, CMID, CMPWD, ctx);
//            iRtn = AXL.SetHuntGroupYN(hlog,dn);
//            m_Log.server("[SetHuntGroupYN] iRtn(0:Success): " + iRtn);
//            if(iRtn==IResult.RTN_SUCCESS) {
//                String strMAC = (String)mapIPToName.get(mapDnToIP.get(dn));
//                m_Log.server("[SetHuntGroupYN] strMAC: " + strMAC);
//                iRtn = AXL.SetDeviceReset(strMAC);
//            }
//        } catch (Exception e) {
//            m_Log.server("[SetHuntGroupYN] Exception: " + e.getMessage());
//        }
//        return iRtn;
//    }
    
    // IP폰 Reset (2013-10-20 jylee)
//    public static int SetDeviceReset(String cmip, int cmport, String cmid, String cmpwd, String dn) {
//        String CMIP = "192.168.230.11";
//        int CMPORT = 8443;
//        String CMID = "ccmadmin";
//        String CMPWD = "lhy623123";
//        int iRtn = IResult.ERR_DEV_WRONG_STATE;     // 전화의 상태가 바르지 않습니다.
//        
//
//        CMIP = cmip;
//        CMPORT = 8443;
//        CMID = cmid;
//        CMPWD = cmpwd;
//
//        X509TrustManager xtm = new SoapTrustManager();
//        TrustManager[] mytm = {xtm};
//
//        try {
//            SSLContext ctx = SSLContext.getInstance("SSL");
//            ctx.init(null, mytm, null);
//            AdministrativeXML AXL = new AdministrativeXML(CMIP, CMPORT, CMID, CMPWD, ctx);
//            //strIP = PhoneInfo.getIPbyDeviceNumber(dn);
//            //System.out.println(">>>strIP:" + strIP);
//            //strMAC = PhoneInfo.getTermNamebyIP(strIP);
//            //System.out.println(">>>strMAC:" + strMAC);
//            String strMAC = (String)mapIPToName.get(mapDnToIP.get(dn));
//            m_Log.server("[SetDeviceReset] strMAC: " + strMAC);
//            iRtn = AXL.SetDeviceReset(strMAC);
//        } catch (Exception e) {
//            m_Log.server("[SetDeviceReset] Exception: " + e.getMessage());
//            System.out.println(e.getMessage());
//        }
//        return iRtn;
//    }
}
