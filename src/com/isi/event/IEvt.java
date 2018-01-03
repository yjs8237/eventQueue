package com.isi.event;

public interface IEvt {
	public static final String DeliMetar				= "";
    public static final String DeliMetar2				= "|";
// --javax.events -----------------------------------------
// [Ev]
		
		// [AddrEv]
		public static final int Addr_Observation_EndedEv	= 100;
		
		// [ProvEv]
		public static final int Prov_InServiceEv			= 111;
		public static final int Prov_Observation_EndedEv	= 112;
		public static final int Prov_OutOfServiceEv			= 113;
		public static final int Prov_ShutdownEv				= 114;
		
		// [CallEv]
		public static final int Call_ActiveEv				= 101;
		public static final int Call_InvalidEv				= 102;
		public static final int Call_ObservationEndedEv		= 103;
		
		// [ConnEv]
		public static final int Conn_AlertingEv				= 104;
		public static final int Conn_ConnectedEv			= 105;
		public static final int Conn_CreatedEv				= 106;
		public static final int Conn_DisconnectedEv			= 107;
		public static final int Conn_FailedEv				= 108;
		public static final int Conn_InProgressEv			= 109;
		public static final int Conn_UnknownEv				= 110;
		
		// [TermConnEv]
		public static final int Term_ConnActiveEv			= 115;
		public static final int Term_ConnCreatedEv			= 116;
		public static final int Term_ConnDroppedEv			= 117;
		public static final int Term_ConnPassiveEv			= 118;
		public static final int Term_ConnRingingEv			= 119;
		public static final int Term_ConnUnknownEv			= 120;
		
		// [TermEv]
		public static final int Term_ObservationEndedEv		= 121;
		
		// --javax.callcontrol.events------------------------------
		// [CallCtlAddrEv]
		public static final int CallCtl_Addr_DoNotDisturbEv		= 200;
		public static final int CallCtl_Addr_ForwardEv			= 201;
		public static final int CallCtl_Addr_MessageWaitingEv	= 202;
		
		// [CallCtlCallEv]
		
		// [CallCtlConnEv]
		public static final int CallCtl_Conn_AlertingEv			= 203;
		public static final int CallCtl_Conn_DialingEv			= 204;   // 발신되지 않아도 다이얼이 되면 떨어짐.
		public static final int CallCtl_Conn_DisconnectedEv		= 205;   // 모든 발생호 종료시 떨어짐	
		    public static final int CallCtl_Conn_EstablishedEv		= 206;
		public static final int CallCtl_Conn_FailedEv			= 207;   // 발신에러시 떨어짐. cause로 원인 알수 있음.
		public static final int CallCtl_Conn_InitiatedEv		= 208;   // 수화기들면 발생함.
		public static final int CallCtl_Conn_NetworkAlertingEv	= 209;
		public static final int CallCtl_Conn_NetworkReachedEv	= 210;
		public static final int CallCtl_Conn_OfferedEv			= 211;
		public static final int CallCtl_Conn_QueuedEv			= 212;
		public static final int CallCtl_Conn_UnknownEv			= 213;
		
		// [CallCtlEv]
		
		// [CallCtlTermConnEv]
		public static final int CallCtl_TermConn_BridgedEv	= 214;
		public static final int CallCtl_TermConn_DroppedEv	= 215; // 전화기 놓으면 발생함.
		public static final int CallCtl_TermConn_HeldEv		= 216;
		public static final int CallCtl_TermConn_InUseEv	= 217;
		public static final int CallCtl_TermConn_RingingEv	= 218;
		public static final int CallCtl_TermConn_TalkingEv	= 219; // 전화기 들면 발생함.
		public static final int CallCtl_TermConn_UnknownEv	= 220;
		
		// [CallCtlTermEv]
		public static final int CallCtl_Term_DoNotDisturbEv = 221;
		
		// [Cisco ???]
		public static final int MediaTermConnDtmfEv			= 401;
		
		public static final int CiscoTermDeviceStateActiveEv	= 1073745926;
		public static final int CiscoTermDeviceStateAlertingEv  = 1073745927;
		public static final int CiscoTermDeviceStateHeldEv		= 1073745928; //?
		public static final int CiscoTermDeviceStateIdleEv		= 1073745929;
		public static final int CiscoTermOutOfServiceEv			= 1073745924;
		public static final int CiscoTermInServiceEv			= 1073745923;
		
		public static final int TransferStartEv				= 500;
		public static final int TransferEndEv				= 501;
		public static final int ConferenceStartEv			= 502;
		public static final int ConferenceEndEv				= 503;
		public static final int Call_ConsultCallActiveEv	= 504;
		public static final int SingleStepEv				= 505;
		
		// 구분자	
		    public void setEventID(int aEvt);		   // 이벤트의 종류
		public void setDn(String aDn);		 // 어느 장치의 모니터링에서 발생했는지
		public void setDevice(String aDevice);
		public String getDevice();
}
