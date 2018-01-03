package com.isi.constans;
/**
*
* @author greatyun
*/
public interface DISCONNECTTYPE {
	public static final int PICKUP_DISCONNECT				=	700;		// PICKUP 당겨받기로 인한 DISCONNECT
	public static final int NORMAL_DISCONNECT				=	701;		// 일반 NORMAL DISCONNECT
	public static final int CONFERENCE_DISCONNECT			=	702;		// 전화회의종료 DISCONNECT
	public static final int CONFERENCEFINAL_DISCONNECT		=	703;		// 전화회의 최종종료 DISCONNECT
	public static final int TRANSFER_DISCONNECT				=	704;		// 전화회의 최종종료 DISCONNECT
}
