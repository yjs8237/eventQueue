package com.isi.constans;
/**
*
* @author greatyun
*/
public interface UDPMSG {
	
	public static final String SEND_TEXT_MESSAGE 			= "P001";
    public static final String SEND_IMAGE_MESSAGE 			= "P002";
    public static final String SEND_CLEAR_SERVICE 			= "P003";
    public static final String SEND_RING_SIGNAL 			= "P010";	// Ring 울렸을때 
    public static final String SEND_CALLSTART_SIGNAL 		= "P011";	// 전화받았을때
    public static final String SEND_CALLEND_SIGNAL 			= "P012";
    public static final String SEND_ALLCLEAR_SIGNAL 		= "P013";	// 전화끊었을때
    public static final String SEND_NETWORK_PICKUP_ESTABLISEHD_MESSAGE	= "P014";	// 외부인입콜을 다른사람이 당겨받았을때
    public static final String PACKET_VERSION 				= "001";
}
