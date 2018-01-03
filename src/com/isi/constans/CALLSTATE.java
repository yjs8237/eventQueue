package com.isi.constans;



/**
 *
 * @author greatyun
 */
public interface CALLSTATE {
	
	
	public static final int NORMAL			=		100;	// NORMAL 콜
	public static final int PICKUP			=		206;	// 당겨받기
	public static final int TRANSFER		=		212;	// 호전환
	public static final int CONFERENCE		=		207;	// 전화회의
	public static final int UNHOLD			=		214;	// 보류해제
	
	public static final int META_CALL_STARTING			=	128;	// Meta Call Starting
	public static final int META_CALL_MERGING			=	133;	// Meta Call Merging	
	public static final int META_CALL_REMOVING_PARTY	=	131;	// Meta Call Removing Party
	public static final int META_CALL_ENDING			=	132;	// Meta Call Ending
	
	
	
	public static final int ALERTING_ING		=		900;		// Ring (발신 주체)
	public static final int ESTABLISHED_ING		=		902;		// 통화연결 (발신 주체)
	public static final int IDLE				=		904;		// 통화종료 
	
	
	public static final int CallCtlConnAlertingEv			=	203;		// CTI Alerting
	public static final int CallCtlConnNetworkAlertingEv	=	209;		// CTI Network Alerting
	public static final int CallCtlConnEstablishedEv	=	206;		// CTI ESTABLISHED
	public static final int CallCtl_Conn_DisconnectedEv	=	205;		// CTI DISCONNECTED
	
}
