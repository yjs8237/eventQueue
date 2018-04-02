package com.isi.constans;


/**
*
* @author greatyun
*/
public interface PROPERTIES {
	
	
	public static final String SIDE_INFO				= "SIDE_INFO";			// 이중화  side 정보
	
	public static final String DB_CLASS				= "DB_CLASS";			// DB Class 정보
	public static final String DB_URL				= "DB_URL";				// DB URL
	public static final String DB_USER				= "DB_USER";			// DB USER
	public static final String DB_PASSWORD			= "DB_PASSWORD";		// DB PASSWORD
	
	/* DB 쿼리 정보 */
	public static final String QUERY_DEVICEINFO			= "DEVICEINFO";			// 전화기 정보 가져오는 쿼리 (디바이스타입 , 전화기 사이즈 등등..)
	public static final String QUERY_XMLINFO			= "XMLINFO";			// XML 환경정보 가져오는 쿼리 (교환기정보, URL 등등)
	public static final String QUERY_EMPINFO			= "SELECT_EMP_INFO";			// XML 환경정보 가져오는 쿼리 (교환기정보, URL 등등)
	public static final String QUERY_MY_ADDRESS			= "SELECT_MY_ADDRESS";			// 개인주소록 정보 최초 로딩 쿼리
	
	/* ISXML WEB 서버 정보 */
//	public static final String ISXMLWEB_URL			= "ISXMLWEB_URL";		// ISXML WEB 서버 정보
	
	/* 이미지 파일 저장 경로 정보 */
//	public static final String BASE_IMAGE			= "BASE_IMAGE";			// 기본 바탕 이미지
//	public static final String EMPLOYEE_IMAGE		= "EMPLOYEE_IMAGE";			// 직원 팝업 이미지 부모 경로
//	public static final String IMAGE_298x168		= "IMAGE_298x168";			// 298x168 해상도 의 직원 팝업 이미지
//	public static final String IMAGE_396x162		= "IMAGE_396x162";			// 396x162 해상도 의 직원 팝업 이미지
//	public static final String IMAGE_498x289		= "IMAGE_498x289";			// 498x289 해상도 의 직원 팝업 이미지
//	public static final String IMAGE_559x313		= "IMAGE_559x313";			// 559x313 해상도 의 직원 팝업 이미지
//	public static final String IMAGE_298x144		= "IMAGE_298x144";			// 298x144 해상도 의 직원 팝업 이미지
//	public static final String IMAGE_298x156		= "IMAGE_298x156";			// 298x156 해상도 의 직원 팝업 이미지
//	public static final String FACE_IMAGE			= "FACE_IMAGE";			// 직원 증명사진
//	public static final String FACE_IMAGE_URL		= "FACE_IMAGE_URL";			// 직원 증명사진 다운 URL
	
	
	/* n 일 지난 로그파일 삭제 */
//	public static final String LOG_DEL_DAYS			= "LOG_DEL_DAYS";			// n 일 지난 로그파일 삭제
}
