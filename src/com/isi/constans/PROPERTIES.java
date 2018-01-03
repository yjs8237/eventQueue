package com.isi.constans;


/**
*
* @author greatyun
*/
public interface PROPERTIES {
	
	/* 싱글 모드 설정 */
	public static final String SINGLE_MODE			= "SINGLE_MODE";	// 싱글모드 설정 ISPS 와 SET 여부
	
	/* 이중화 설정 */
	public static final String DUPLEX_YN			= "DUPLEX_YN";			// 이중화 설정 유무
	public static final String REMOTE_IP			= "REMOTE_IP";			// 이중화 상대 서버 IP
	public static final String REMOTE_PORT			= "REMOTE_PORT";		// 이중화 상대 서버 포트
	
	/* Timeout 설정 */
	public static final String CONNECT_TIMEOUT		= "CONNECT_TIMEOUT";	// Connection Timeout
	public static final String READ_TIMEOUT			= "READ_TIMEOUT";		// Data Read Timeout
	
	/* HTTP PORT */
	public static final String HTTP_PORT			= "HTTP_PORT";			// HTTP PORT 정보
	
	/* 고객 정보 팝업 여부 */
	public static final String CUSTINFO_POPUP		= "CUSTINFO_POPUP";	// 고객정보 팝업 여부
	
	/* ISPS 설정 */
	public static final String ISPS_IP				= "ISPS_IP";		// ISPS 모드일 경우 사용된다
	public static final String RECV_PORT			= "RECV_PORT";		// ISPS UDP RECV 포트
	public static final String SEND_PORT			= "SEND_PORT";		// ISPS UDP SEND 포트
	
	/* CM 연동 설정 */
	public static final String CM_CNT				= "CM_CNT";			// CM 서버 개수 정보
	public static final String CM1_USER				= "CM1_USER";		// CM Application 유저 정보 for Jtapi 연동
	public static final String CM1_PASSWORD			= "CM1_PASSWORD";	// CM Application 유저 정보 for Jtapi 연동
	public static final String CM1_IP				= "CM1_IP";			// CM IP
	
	public static final String CM2_USER				= "CM2_USER";		// CM Application 유저 정보 for Jtapi 연동
	public static final String CM2_PASSWORD			= "CM2_PASSWORD";	// CM Application 유저 정보 for Jtapi 연동
	public static final String CM2_IP				= "CM2_IP";			// CM IP
	
	
	/* 로그 파일 설정 */
	public static final String CONSOLE_DEBUG_MODE	= "CONSOLE_DEBUG_MODE";	// Debug 모드 설정 (Y:콘솔만 표시 B:로그파일 표시 B:콘솔,로그파일 모두표시)
	public static final String LOG_LEVEL			= "LOG_LEVEL";			// 로그레벨 (0:로그없음,1:Exception 로그,2:Normal 로그,3: All(권장)
	public static final String LOG_PATH				= "LOG_PATH";			// 로그파일 경로
	public static final String MIDDLE_LOG_PATH		= "MIDDLE_LOG_PATH";	// 미들웨어로그파일 경로
	
	
	public static final String DB_CLASS				= "DB_CLASS";			// DB Class 정보
	public static final String DB_URL				= "DB_URL";				// DB URL
	public static final String DB_USER				= "DB_USER";			// DB USER
	public static final String DB_PASSWORD			= "DB_PASSWORD";		// DB PASSWORD
	
	/* ISXML WEB 서버 정보 */
	public static final String ISXMLWEB_URL			= "ISXMLWEB_URL";		// ISXML WEB 서버 정보
	
	/* 이미지 파일 저장 경로 정보 */
	public static final String BASE_IMAGE			= "BASE_IMAGE";			// 기본 바탕 이미지
	public static final String EMPLOYEE_IMAGE		= "EMPLOYEE_IMAGE";			// 직원 팝업 이미지 부모 경로
	public static final String IMAGE_298x168		= "IMAGE_298x168";			// 298x168 해상도 의 직원 팝업 이미지
	public static final String IMAGE_396x162		= "IMAGE_396x162";			// 396x162 해상도 의 직원 팝업 이미지
	public static final String IMAGE_498x289		= "IMAGE_498x289";			// 498x289 해상도 의 직원 팝업 이미지
	public static final String IMAGE_559x313		= "IMAGE_559x313";			// 559x313 해상도 의 직원 팝업 이미지
	public static final String IMAGE_298x144		= "IMAGE_298x144";			// 298x144 해상도 의 직원 팝업 이미지
	public static final String IMAGE_298x156		= "IMAGE_298x156";			// 298x156 해상도 의 직원 팝업 이미지
	public static final String FACE_IMAGE			= "FACE_IMAGE";			// 직원 증명사진
	public static final String FACE_IMAGE_URL		= "FACE_IMAGE_URL";			// 직원 증명사진 다운 URL
	
	
	/* n 일 지난 로그파일 삭제 */
	public static final String LOG_DEL_DAYS			= "LOG_DEL_DAYS";			// n 일 지난 로그파일 삭제
}
