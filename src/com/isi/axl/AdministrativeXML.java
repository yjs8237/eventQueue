package com.isi.axl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;

import javax.net.ssl.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.*;
import org.xml.sax.InputSource;

import com.isi.constans.LOGLEVEL;
import com.isi.constans.LOGTYPE;
import com.isi.constans.SVCTYPE;
import com.test.soap.Utils;
import com.test.vo.CmAxlInfoModel;



/**
 *
 * @author jsyun
 */
public class AdministrativeXML extends SoapXML{

    public AdministrativeXML(String ip, int port, String id, String pwd, SSLContext ctx) {
        super(ip, port, id, pwd, ctx);
    }
    
    public int SoapTest(CmAxlInfoModel model) {
    	String ver  = "8.5";
    	
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
    	
    	
    	StringBuffer soapHeader = new StringBuffer();
		soapHeader.append("POST https://").append(model.getCmIP()).append(":").append(model.getCmPort()).append("/axl/ HTTP/1.1").append("\n");
		soapHeader.append("Accept-Encoding: gzip,deflate").append("\n");
		soapHeader.append("Content-Type: text/xml;charset=UTF-8").append("\n");
		soapHeader.append("SOAPAction: \"CUCM:DB ver=").append(ver).append(" executeSQLQuery\"").append("\n");
		soapHeader.append("Content-Length: ").append(queryBuffer.toString().length()).append("\n");	
		soapHeader.append("Host: ").append(model.getCmIP()).append(":").append(model.getCmPort()).append("\n");
		soapHeader.append("Connection: Keep-Alive").append("\n");
		soapHeader.append("User-Agent: Apache-HttpClient/4.1.1 (java 1.5)").append("\n");
		soapHeader.append("Authorization: Basic ").append(Utils.getBase64(model.getCmID()+":"+model.getCmPwd())).append("\n").append("\n");
    	
		soapHeader.append(queryBuffer.toString());
		Document [] xmldom      =  new Document [1];
		SendSoapMessage(soapHeader.toString() , xmldom);
		
    	return 0;
    }

    public int GetDeviceNameList(String aLike, Vector DeviceNames) {

        String strReqHttpHeader = "";
        String strSoapReqeust   = "";
        Document [] xmldom      =  new Document [1];
        //jylee (New SOAP)
        strSoapReqeust +="<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" ";
        strSoapReqeust +="xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ";
        strSoapReqeust +="xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"> ";
        strSoapReqeust +="<SOAP-ENV:Body> ";
        strSoapReqeust +="<axlapi:executeSQLQuery xmlns:axlapi=\"http://www.cisco.com/AXL/API/"+dbVer+"\" sequence=\"1234\"> ";
        strSoapReqeust +="<sql> SELECT name, tkClass FROM device WHERE name LIKE \'" + aLike + "%\' </sql> ";
        strSoapReqeust +="</axlapi:executeSQLQuery> ";
        strSoapReqeust +="</SOAP-ENV:Body> ";
        strSoapReqeust +="</SOAP-ENV:Envelope>";

        strReqHttpHeader = "POST /axl/ HTTP/1.0\r\n";
        strReqHttpHeader +="Content-type: text/xml\r\n";
        strReqHttpHeader +="SOAPAction: \"CUCM:DB ver="+dbVer+"\"\r\n";
        strReqHttpHeader +="Accept: text/*\r\n";
        strReqHttpHeader +="Content-Length: " + Integer.toString(strSoapReqeust.length()) + "\r\n";
        strReqHttpHeader +="Authorization: Basic " + getAuth() + "\r\n";
        strReqHttpHeader +="Host: " + getIP() + ":" + getPort() + "\r\n";
        strReqHttpHeader +="Connection: Keep-Alive\r\n";
        strReqHttpHeader +="\r\n";
        
//        strSoapReqeust +="<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" ";
//        strSoapReqeust +="xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">";
//        strSoapReqeust +="<soapenv:Body> ";
//        strSoapReqeust +="<axlapi:executeSQLQuery xmlns:axlapi=\"http://www.cisco.com/AXL/API/6.1\" sequence=\"1234\"> ";
//        strSoapReqeust +="<axlapi:sql>";
//        strSoapReqeust +="select name, tkClass from device where name LIKE \'" + aLike + "%\'";
//        strSoapReqeust +="</axlapi:sql>";
//        strSoapReqeust +="</axlapi:executeSQLQuery>";
//        strSoapReqeust +="</soapenv:Body>";
//
//        strReqHttpHeader = "POST /axl/ HTTP/1.0\r\n";
//        strReqHttpHeader +="Content-type: text/xml\r\n";
//        strReqHttpHeader +="SOAPAction: \"CUCM:DB ver=6.0\"\r\n";
//        strReqHttpHeader += "Accept: text/*\r\n";
//        strReqHttpHeader +="Content-Length: " + Integer.toString(strSoapReqeust.length()) + "\r\n";
//        strReqHttpHeader +="Authorization: Basic " + getAuth() + "\r\n";
//        strReqHttpHeader +="Host: " + getIP() + ":" + getPort() + "\r\n";
//        strReqHttpHeader +="Connection: Keep-Alive\r\n";
//        strReqHttpHeader +="\r\n";
        
        strReqHttpHeader += strSoapReqeust;

        if (SendSoapMessage(strReqHttpHeader, xmldom) != 0) {
            return -1;
        } else {

            NodeList itemlist = null;
            itemlist = xmldom[0].getElementsByTagName("row");
            //DeviceNames = new Vector();

            for (int i = 0 ; i < itemlist.getLength(); i++) {
                NodeList nodelist = itemlist.item(i).getChildNodes();
                for (int j = 0; j < nodelist.getLength(); j++) {
                    if ("name".equals(nodelist.item(j).getNodeName())) {
                        NodeList values = nodelist.item(j).getChildNodes();
                        DeviceNames.add(values.item(0).getNodeValue());
                        break;
                    }
                }
            }
        }
        return 0;
    }
    
    //doDeviceReset
    public int SetDeviceReset(String strMAC) {
        String strReqHttpHeader = "";
        String strSoapReqeust = "";
        Document[] xmldom = new Document[1];

        strSoapReqeust += "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" ";
        strSoapReqeust += "xmlns:ns1=\"http://www.cisco.com/AXL/API/1.0\"> ";
        strSoapReqeust += "<SOAP-ENV:Body> ";
        strSoapReqeust += "<ns1:doDeviceReset><deviceName>" + strMAC + "</deviceName> ";
        strSoapReqeust += "<isHardReset>false</isHardReset> ";
        strSoapReqeust += "</ns1:doDeviceReset> ";
        strSoapReqeust += "</SOAP-ENV:Body> ";
        strSoapReqeust += "</SOAP-ENV:Envelope>";
        
        strReqHttpHeader = "POST /axl/ HTTP/1.0\r\n";
        strReqHttpHeader += "Content-type: text/xml\r\n";
        strReqHttpHeader += "SOAPAction: \"CUCM:DB ver="+dbVer+"\"\r\n";
        strReqHttpHeader += "Accept: text/*\r\n";
        strReqHttpHeader += "Content-Length: " + Integer.toString(strSoapReqeust.length()) + "\r\n";
        strReqHttpHeader += "Authorization: Basic " + getAuth() + "\r\n";
        strReqHttpHeader += "Host: " + getIP() + ":" + getPort() + "\r\n";
        strReqHttpHeader += "Connection: Keep-Alive\r\n";
        strReqHttpHeader += "\r\n";

        strReqHttpHeader += strSoapReqeust;

        if (SendSoapMessage(strReqHttpHeader, xmldom) != 0) {
            return -1;
        } else {
            return 0;
        }
    }

    //hlog select
    public String GetHuntGroupYN(String dnorpattern) {
        String strReqHttpHeader = "";
        String strSoapReqeust = "";
        String strRtn = null;
        Document[] xmldom = new Document[1];

        strSoapReqeust += "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" ";
        strSoapReqeust += "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ";
        strSoapReqeust += "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"> ";
        strSoapReqeust += "<SOAP-ENV:Body> ";
        strSoapReqeust += "<axlapi:executeSQLQuery xmlns:axlapi=\"http://www.cisco.com/AXL/API/"+dbVer+"\" sequence=\"1234\"> ";
        strSoapReqeust += "<sql> select hlog from devicehlogdynamic where fkdevice = ";
        strSoapReqeust += "(select fkdevice from devicenumplanmap where fknumplan = ";
        strSoapReqeust += "(select pkid from numplan where dnorpattern = \'" + dnorpattern + "\'))</sql> ";
        strSoapReqeust += "</axlapi:executeSQLQuery> ";
        strSoapReqeust += "</SOAP-ENV:Body> ";
        strSoapReqeust += "</SOAP-ENV:Envelope>";

        strReqHttpHeader = "POST /axl/ HTTP/1.0\r\n";
        strReqHttpHeader += "Content-type: text/xml\r\n";
        strReqHttpHeader += "SOAPAction: \"CUCM:DB ver="+dbVer+"\"\r\n";
        strReqHttpHeader += "Accept: text/*\r\n";
        strReqHttpHeader += "Content-Length: " + Integer.toString(strSoapReqeust.length()) + "\r\n";
        strReqHttpHeader += "Authorization: Basic " + getAuth() + "\r\n";
        strReqHttpHeader += "Host: " + getIP() + ":" + getPort() + "\r\n";
        strReqHttpHeader += "Connection: Keep-Alive\r\n";
        strReqHttpHeader += "\r\n";

        strReqHttpHeader += strSoapReqeust;

        if (SendSoapMessage(strReqHttpHeader, xmldom) != 0) {
            return "-1";
        } else {
            NodeList itemlist = null;
            NodeList nodelist = null;
            NodeList values = null;
            itemlist = xmldom[0].getElementsByTagName("row");
            for (int i = 0; i < itemlist.getLength(); i++) {
                nodelist = itemlist.item(i).getChildNodes();
                for (int j = 0; j < nodelist.getLength(); j++) {
                    if ("hlog".equals(nodelist.item(j).getNodeName())) {
                        values = nodelist.item(j).getChildNodes();
                        strRtn = values.item(0).getNodeValue();
                        break;
                    }
                }
            }
            return strRtn;
        }
    }

    //hlog update
    public int SetHuntGroupYN(String hlog, String dnorpattern) {
        String strReqHttpHeader = "";
        String strSoapReqeust = "";
        Document[] xmldom = new Document[1];

        strSoapReqeust += "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"> ";
        strSoapReqeust += "<SOAP-ENV:Body> ";
        strSoapReqeust += "<axlapi:executeSQLUpdate sequence=\"1\" ";
        strSoapReqeust += "xmlns:axlapi=\"http://www.cisco.com/AXL/API/"+dbVer+"\" xmlns:axl=\"http://www.cisco.com/AXL/"+dbVer+"\" ";
        strSoapReqeust += "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ";
        strSoapReqeust += "xsi:schemaLocation=\"http://www.cisco.com/AXL/API/"+dbVer+" axlsoap.xsd\"> ";
        strSoapReqeust += "<sql>update devicehlogdynamic set hlog = \'" + hlog + "\' where fkdevice = ";
        strSoapReqeust += "(select fkdevice from devicenumplanmap where fknumplan = ";
        strSoapReqeust += "(select pkid from numplan where dnorpattern = \'" + dnorpattern + "\'))</sql> ";
        strSoapReqeust += "</axlapi:executeSQLUpdate> ";
        strSoapReqeust += "</SOAP-ENV:Body> ";
        strSoapReqeust += "</SOAP-ENV:Envelope>";

        strReqHttpHeader = "POST /axl/ HTTP/1.0\r\n";
        strReqHttpHeader += "Content-type: text/xml\r\n";
        strReqHttpHeader += "SOAPAction: \"CUCM:DB ver="+dbVer+"\"\r\n";
        strReqHttpHeader += "Accept: text/*\r\n";
        strReqHttpHeader += "Content-Length: " + Integer.toString(strSoapReqeust.length()) + "\r\n";
        strReqHttpHeader += "Authorization: Basic " + getAuth() + "\r\n";
        strReqHttpHeader += "Host: " + getIP() + ":" + getPort() + "\r\n";
        strReqHttpHeader += "Connection: Keep-Alive\r\n";
        strReqHttpHeader += "\r\n";

        strReqHttpHeader += strSoapReqeust;

        if (SendSoapMessage(strReqHttpHeader, xmldom) != 0) {
            return -1;
        } else {
            return 0;
        }
    }
}
