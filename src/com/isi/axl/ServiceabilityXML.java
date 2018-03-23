package com.isi.axl;

import javax.net.ssl.*;
import org.w3c.dom.*;

import com.isi.constans.LOGLEVEL;
import com.isi.constans.LOGTYPE;
import com.isi.constans.SVCTYPE;
import com.isi.data.Employees;
import com.isi.file.GLogWriter;
import com.isi.file.ILog;

public class ServiceabilityXML extends SoapXML{

	 private ILog            m_Log = new GLogWriter();
	
    public ServiceabilityXML(String ip, int port, String id, String pwd, SSLContext ctx) {
        super(ip, port, id, pwd, ctx);
    }

    public int GetDeviceInfo(String aDeviceList, CiscoPhoneInfo PhoneInfo) {

        String strReqHttpHeader = "";
        String strSoapReqeust   = "";
        Document [] xmldom      =  new Document [1];
        
        // jylee (New SOAP)
        strSoapReqeust +="<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" ";
        strSoapReqeust +="xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" ";
        strSoapReqeust +="xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">";
        strSoapReqeust +="<soapenv:Body>";
        strSoapReqeust +="<ns1:SelectCmDevice soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\" ";
        strSoapReqeust +="xmlns:ns1=\"http://schemas.cisco.com/ast/soap/\">";
        strSoapReqeust +="<StateInfo xsi:type=\"xsd:string\"/>";
        strSoapReqeust +="<CmSelectionCriteria href=\"#id0\"/>";
        strSoapReqeust +="</ns1:SelectCmDevice>";
        strSoapReqeust +="<multiRef id=\"id0\" soapenc:root=\"0\" ";
        strSoapReqeust +="soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\" ";
        strSoapReqeust +="xsi:type=\"ns2:CmSelectionCriteria\" ";
        strSoapReqeust +="xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\" ";
        strSoapReqeust +="xmlns:ns2=\"http://schemas.cisco.com/ast/soap/\">";
        strSoapReqeust +="<MaxReturnedDevices xsi:type=\"xsd:unsignedInt\">200</MaxReturnedDevices>";
        strSoapReqeust +="<Class xsi:type=\"xsd:string\">Phone</Class>";
        strSoapReqeust +="<Model xsi:type=\"xsd:unsignedInt\">255</Model>";
        //strSoapReqeust +="<Status xsi:type=\"xsd:string\">Registered</Status>"; // CM의 Registrered된 값들만 가져올때
        strSoapReqeust +="<Status xsi:type=\"xsd:string\">Any</Status>"; // CM의 모든 전화기 정보 가져올때

        strSoapReqeust +="<NodeName xsi:type=\"xsd:string\" xsi:nil=\"true\"/>";
        strSoapReqeust +="<SelectBy xsi:type=\"xsd:string\">Name</SelectBy>";
        strSoapReqeust +="<SelectItems soapenc:arrayType=\"ns2:SelectItem[1]\" xsi:type=\"soapenc:Array\">";
        strSoapReqeust +="<item href=\"#id1\"/>";
        strSoapReqeust +="</SelectItems>";
        strSoapReqeust +="</multiRef>";
        strSoapReqeust +="<multiRef id=\"id1\" soapenc:root=\"0\" ";
        strSoapReqeust +="soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\" ";
        strSoapReqeust +="xsi:type=\"ns3:SelectItem\" xmlns:ns3=\"http://schemas.cisco.com/ast/soap/\" ";
        strSoapReqeust +="xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\">";
        strSoapReqeust +="<Item xsi:type=\"xsd:string\">" + aDeviceList + "</Item>";
        strSoapReqeust +="</multiRef>";
        strSoapReqeust +="</soapenv:Body>";
        strSoapReqeust +="</soapenv:Envelope>";

        strReqHttpHeader = "POST /realtimeservice/services/RisPort HTTP/1.1\r\n";
        strReqHttpHeader +="Content-Type: text/xml;charset=UTF-8\r\n";
        strReqHttpHeader +="SOAPAction: \"http://schemas.cisco.com/ast/soap/action/#RisPort#SelectCmDevice\"\r\n";
        strReqHttpHeader +="User-Agent: Jakarta Commons-HttpClient/3.1\r\n";
        strReqHttpHeader +="Content-Length: " + Integer.toString(strSoapReqeust.length()) + "\r\n";
        strReqHttpHeader +="Authorization: Basic " + getAuth() + "\r\n";
        strReqHttpHeader +="Host: " + getIP() + ":" + getPort() + "\r\n";
        strReqHttpHeader +="\r\n";

//        strSoapReqeust +="<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" ";
//        strSoapReqeust +="xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">";
//        strSoapReqeust +="<soapenv:Body>";
//        strSoapReqeust +="<ns1:SelectCmDevice soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\" ";
//        strSoapReqeust +="xmlns:ns1=\"http://schemas.cisco.com/ast/soap/\">";
//        strSoapReqeust +="<StateInfo xsi:type=\"xsd:string\"/>";
//        strSoapReqeust +="<CmSelectionCriteria href=\"#id0\"/>";
//        strSoapReqeust +="</ns1:SelectCmDevice>";
//        strSoapReqeust +="<multiRef id=\"id0\" soapenc:root=\"0\" soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\" ";
//        strSoapReqeust +="xsi:type=\"ns2:CmSelectionCriteria\" xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\" ";
//        strSoapReqeust +="xmlns:ns2=\"http://schemas.cisco.com/ast/soap/\">";
//        strSoapReqeust +="<MaxReturnedDevices xsi:type=\"xsd:unsignedInt\">200</MaxReturnedDevices>";
//        strSoapReqeust +="<Class xsi:type=\"xsd:string\">Any</Class>";
//        strSoapReqeust +="<Model xsi:type=\"xsd:unsignedInt\">255</Model>";
//        strSoapReqeust +="<Status xsi:type=\"xsd:string\">Any</Status>";
//        strSoapReqeust +="<NodeName xsi:type=\"xsd:string\" xsi:nil=\"true\"/>";
//        strSoapReqeust +="<SelectBy xsi:type=\"xsd:string\">Name</SelectBy>";
//        strSoapReqeust +="<SelectItems soapenc:arrayType=\"ns2:SelectItem[1]\" xsi:type=\"soapenc:Array\">";
//        strSoapReqeust +="<item href=\"#id1\"/>";
//        strSoapReqeust +="</SelectItems>";
//        strSoapReqeust +="</multiRef>";
//        strSoapReqeust +="<multiRef id=\"id1\" soapenc:root=\"0\" soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\" ";
//        strSoapReqeust +="xsi:type=\"ns3:SelectItem\" xmlns:ns3=\"http://schemas.cisco.com/ast/soap/\" ";
//        strSoapReqeust +="xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\">";
//        strSoapReqeust +="<Item xsi:type=\"xsd:string\">" + aDeviceList + "</Item>";
//        strSoapReqeust +="</multiRef>";
//        strSoapReqeust +="</soapenv:Body>";
//        strSoapReqeust +="</soapenv:Envelope>";
//
//        strReqHttpHeader = "POST /realtimeservice/services/RisPort HTTP/1.1\r\n";
//        strReqHttpHeader +="Content-Type: text/xml;charset=UTF-8\r\n";
//        strReqHttpHeader +="SOAPAction: \"http://schemas.cisco.com/ast/soap/action/#RisPort#SelectCmDevice\"\r\n";
//        strReqHttpHeader +="User-Agent: Jakarta Commons-HttpClient/3.1\r\n";
//        strReqHttpHeader +="Content-Length: " + Integer.toString(strSoapReqeust.length()) + "\r\n";
//        strReqHttpHeader +="Authorization: Basic " + getAuth() + "\r\n";
//        strReqHttpHeader +="Host: " + getIP() + ":" + getPort() + "\r\n";
//        strReqHttpHeader +="\r\n";

        strReqHttpHeader += strSoapReqeust;

        if (SendSoapMessage(strReqHttpHeader, xmldom) != 0) {
            return -1;
        } else {

            NodeList itemlist = null;
            itemlist = xmldom[0].getElementsByTagName("item");
            
            String Name, IpAddress, DirNumber, Model, Status;

            for (int i = 0 ; i < itemlist.getLength(); i++) {
            	
                NamedNodeMap map = itemlist.item(i).getAttributes();
                Node  node = map.getNamedItem("xsi:type");

                int allfill = 0;

                try {

                    if ("ns1:CmDevice".equals(node.getNodeValue())) {

                        Name = "";
                        IpAddress = "";
                        DirNumber = "";
                        Model = "";
                        Status = "";

                        NodeList nodelist = itemlist.item(i).getChildNodes();
                        for (int j = 0; j < nodelist.getLength(); j++) {
                            if ("Name".equals(nodelist.item(j).getNodeName())) {
                                NodeList values = nodelist.item(j).getChildNodes();
                                values.item(0).getNodeValue();
                                Name = values.item(0).getNodeValue();
                                allfill |= 0x01;    // 0001
                            }
                            if ("IpAddress".equals(nodelist.item(j).getNodeName())) {
                                NodeList values = nodelist.item(j).getChildNodes();
                                Node itemnode = values.item(0);
                                if (itemnode != null) {
                                    IpAddress = itemnode.getNodeValue();
                                } else { IpAddress = "";}
                                allfill |= 0x02;    // 0010
                            }
                            if ("Model".equals(nodelist.item(j).getNodeName())) {
                                NodeList values = nodelist.item(j).getChildNodes();
                                Node itemnode = values.item(0);
                                if (itemnode != null) {
                                    Model = itemnode.getNodeValue();
                                } else { Model = "";}

                                allfill |= 0x04;    // 0100
                            }
                            if ("DirNumber".equals(nodelist.item(j).getNodeName())) {
                                NodeList values = nodelist.item(j).getChildNodes();
                                Node itemnode = values.item(0);
                                if (itemnode != null) {
                                    DirNumber = itemnode.getNodeValue();
                                } else { DirNumber = "";}
                                allfill |= 0x08;    // 1000
                            }
/*
                            if ("Status".equals(nodelist.item(j).getNodeName())) {
                                NodeList values = nodelist.item(j).getChildNodes();
                                Node itemnode = values.item(0);
                                if (itemnode != null) {
                                    Status = itemnode.getNodeValue();
                                } else { Status = "UnRegistered";}
                                if ("Registered".equals(Status) == false) {
                                    continue;
                                }
                                 allfill |= 0x10;    // 16
                            }
*/
                            if (allfill == 0x0F) {  // 1111

                                if (!("".equals(DirNumber))) {

                                    PhoneInfo.setIPModel(IpAddress, Model);
                                    PhoneInfo.setModelTermName(Name, Model);
                                    
                                    String [] DevNum = DirNumber.split(",");

                                    for (int k = 0; k < DevNum.length; k++) {
                                        int pos = DevNum[k].indexOf("-");

                                        String Number="";

                                        if (pos == -1) {
                                            Number = DevNum[k];
                                        } else  {
                                            Number = DevNum[k].substring(0, pos);
                                        }
                                        
//                                        updateEmployeeDeviceInfo(Number, IpAddress);
                                        
                                        PhoneInfo.setIPDeviceNumber(Number, IpAddress);
                                    }
//                                    m_Log.write(LOGLEVEL.LEVEL_3, LOGTYPE.STAND_LOG, SVCTYPE.GLOBAL, "PHONE INFO", Name + " , " + IpAddress + " , " + DirNumber);
                                    PhoneInfo.setIPTermName(Name, IpAddress);
                                    PhoneInfo.setITermNameIP(IpAddress, Name);
                                }
                                break;
                            }
                        }
                    }
                } catch(Exception e) {
                    e.getStackTrace();
                }
            }
        }
        return 0;
    }
/*
	private void updateEmployeeDeviceInfo(String number, String ipAddress) {
		// TODO Auto-generated method stub
		Employees employees = Employees.getInstance();
		employees.updateDeviceIpaddrInfo(number , ipAddress);
	}
	*/
}
