package com.isi.command;

/*
 * 파일명 : ICmd.java
 * 프로그램 내용: Client 로부터 이벤트를 받는다. (예: MakeCall => C011)
 */

public abstract class ICmd {

    public static final String PACKET_VERSION   = "001";
    public static final int CMD_HEARTBEAT       = 00;
    public static final int CMD_CALL            = 10;
    public static final int CMD_MAKECALL        = CMD_CALL + 1;
    public static final int CMD_ANSWER          = CMD_CALL + 2;
    public static final int CMD_HOLD            = CMD_CALL + 3;
    public static final int CMD_RETRIEVE        = CMD_CALL + 4;
    public static final int CMD_CONSULT         = CMD_CALL + 5;
    public static final int CMD_ALTERNATE       = CMD_CALL + 6;
    public static final int CMD_RECONNECT       = CMD_CALL + 7;
    public static final int CMD_TRANSFER        = CMD_CALL + 8;
    public static final int CMD_CONFERENCE      = CMD_CALL + 9;
    public static final int CMD_SSTRANSFER      = CMD_CALL + 10;
    public static final int CMD_SSCONFERENCE    = CMD_CALL + 11;
    public static final int CMD_DISCONNECT      = CMD_CALL + 12;
    public static final int CMD_SENDDTMF        = CMD_CALL + 13;
    public static final int CMD_LOGIN           = CMD_CALL + 14;
    public static final int CMD_LOGOUT          = CMD_CALL + 15;
    public static final int CMD_MONITORSTART    = CMD_CALL + 16;
    public static final int CMD_MONITOREND      = CMD_CALL + 17;
    public static final int CMD_SETUSERINFO     = CMD_CALL + 18;
    public static final int CMD_GETUSERINFO     = CMD_CALL + 19;
    
    public static final int CMD_CHANGESTATE     = CMD_CALL + 20;
    public static final int CMD_SETDND          = CMD_CALL + 23;
    public static final int CMD_GETDND          = CMD_CALL + 24;
    public static final int CMD_SETFORWARD      = CMD_CALL + 25;
    public static final int CMD_GETFORWARD      = CMD_CALL + 26;
    public static final int CMD_GETSVRTIME      = CMD_CALL + 27;
    public static final int CMD_GETCUSTINFO     = CMD_CALL + 28;   
    public static final int CMD_CHAGESESSION    = CMD_CALL + 29;    
    public static final int CMD_SSTRANSEX       = CMD_CALL + 30;    
    public static final int CMD_SSCONFEX        = CMD_CALL + 31;    
    public static final int CMD_REQPHONEPAD     = CMD_CALL + 32;
    public static final int CMD_GETPHONEPADINFO = CMD_CALL + 33;    
    public static final int CMD_SETPHONEPADINFO = CMD_CALL + 34;  
    
    public static final int CMD_SETUSERINFOALL  = CMD_CALL + 35; 
    
    public static final int CMD_REQROUTING      = CMD_CALL + 36;    
    public static final int CMD_SENDMESSAGE     = CMD_CALL + 37;
    public static final int CMD_SETCUSTINFO     = CMD_CALL + 38;    
    public static final int CMD_GETLICENSEINFO  = CMD_CALL + 39;
   
    public static final int CMD_DELETEALL       = CMD_CALL + 43;  
    public static final int CMD_DEVICEINSERT    = CMD_CALL + 46;  
    public static final int CMD_DEVICEUPDATE    = CMD_CALL + 47; 
    public static final int CMD_DEVICEDELETE    = CMD_CALL + 48; 
    
    public static final int CMD_DEVICESTATUS    = CMD_CALL + 49;
    public static final int CMD_GROUPSELECTDN   = CMD_CALL + 50;
    
    public static final int CMD_AGENTSEARCH     = CMD_CALL + 60;    
    public static final int CMD_GROUPSEARCH     = CMD_CALL + 61; 
    public static final int CMD_AGENTNAMESEARCH = CMD_CALL + 62;    
    public static final int CMD_GROUPNAMESEARCH = CMD_CALL + 63; 
    
    public static final int CMD_AGENTINSERT     = CMD_CALL + 81;    
    public static final int CMD_AGENTDELETE     = CMD_CALL + 82;  
    public static final int CMD_AGENTUPDATE     = CMD_CALL + 83;
    public static final int CMD_GETHUNTLOGIN    = CMD_CALL + 85;    
    public static final int CMD_SETHUNTLOGIN    = CMD_CALL + 86;   
    public static final int CMD_SETDEVICERESET  = CMD_CALL + 87;      
    
    public static final int CMD_CALL_EX         = 300;
    public static final int CMD_ADDCALLINFO     = CMD_CALL_EX + 1;
    public static final int CMD_SETCALLINFO     = CMD_CALL_EX + 2;
    public static final int CMD_GETCALLINFO     = CMD_CALL_EX + 3;
    public static final int CMD_REMOVECALLINFO  = CMD_CALL_EX + 4;
    
    
    int m_Cmd;
    String m_Dn;
    String m_Passwd;

    public void setCmd(int aCmd) {
        m_Cmd = aCmd;
    }

    public void setDn(String aDn) {
        m_Dn = aDn;
    }

    public void setPasswd(String aPasswd) {
        m_Passwd = aPasswd;
    }

    public int getCmd() {
        return m_Cmd;
    }

    public String getDn() {
        return m_Dn;
    }

    public String getPasswd() {
        return m_Passwd;
    }
}
