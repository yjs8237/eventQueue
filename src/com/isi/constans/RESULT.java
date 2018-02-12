package com.isi.constans;

/**
*
* @author greatyun
*/
public interface RESULT {
	public static final int RTN_SUCCESS 	= 		0;		// 성공 결과 코드
	public static final int ERROR 	= 		-1;		// 실패 결과 코드
	
	public static final int HTTP_URL_ERROR	=		404;	// 잘못된 URL 요청
	public static final int HTTP_PARAM_ERROR	=		405;	// 잘못된 URL 요청
	
	
	
	public static final int TCP_CONN_FAIL	=		301;	// TCP CONNECTION FAIL
    // 반환값    
    final static int RTN_BASIC                      = 100;
    final static int RTN_FOUND                      = RTN_BASIC + 1;
    final static int RTN_NOTFOUND                   = RTN_BASIC + 2;
    final static int RTN_ALREADY_LOGIN              = RTN_BASIC + 3;
    final static int RTN_ANOTHER_LOGIN              = RTN_BASIC + 4;
    final static int RTN_ISNOT_LOGIN                = RTN_BASIC + 5;
    final static int RTN_MAX_DEVICE_LOGIN           = RTN_BASIC + 6;
    final static int RTN_PASER_ERR                  = RTN_BASIC + 7;
    final static int RTN_OUT_OF_MEMORY              = RTN_BASIC + 8;
    final static int RTN_LOWER_LOGLEVEL             = RTN_BASIC + 9;
    final static int RTN_ALREADY_CONNECTED          = RTN_BASIC + 10;
    final static int RTN_ADD_NEW_GRP                = RTN_BASIC + 11;	
    // 상담원에대한 에러
    final static int ERR_AGENT                      = -100;
    final static int ERR_AGENT_UNREG                = ERR_AGENT - 1;	// 등록된 사용자가 아닙니다.
    final static int ERR_AGENT_ALREADY_LOGIN        = ERR_AGENT - 2;	// 사용자가 이미 로그인 중입니다.
    final static int ERR_AGENT_PWD_MISMATCH         = ERR_AGENT - 3;	// 사용자의 비밀번호가 정확하지 않습니다.
    final static int ERR_AGENT_NOT_LOGIN            = ERR_AGENT - 4;	// 사용자가 로그인되지 않았습니다.
    final static int ERR_AGENT_NOT_FOUND            = ERR_AGENT - 5;	// 등록된 사용자를 찾을 수 없습니다.
    final static int ERR_AGENT_ANONTHER_LOGIN       = ERR_AGENT - 6;	// 해당 번호로 다른 사용자가 이미 로그인 상태입니다.
    final static int ERR_AGENT_MAX_MONITER_DEVICE   = ERR_AGENT - 7;	// 사용자의 최대 모니터링 수를 초과했습니다.
    final static int ERR_AGENT_SAME_SESSION_DN      = ERR_AGENT - 8;	// 동일한 연결에서 같은 DN을 중복해 모니터링할 수 없습니다.
    final static int ERR_AGENT_SAME_ID              = ERR_AGENT - 9;	// 동일 ID로 같은 DN을 중복해서 모니터링 할 수 없습니다.
    final static int ERR_AGENT_SAME_STATE           = ERR_AGENT - 10;   // 변경하려는 상태가 기존의 상태와 동일합니다.
    final static int ERR_AGENT_ALREADY_EXIST        = ERR_AGENT - 11;   // 추가하려는 상담원이 이미 존재합니다.
    final static int ERR_AGENT_LICENSE_OVER         = ERR_AGENT - 12;   // 사용자 라이센스가 초과되었습니다.
    final static int ERR_AGENT_NOT_DELETE           = ERR_AGENT - 13;   // 로그인 중 사용자를 삭제할 수 없습니다.
    final static int ERR_AGENT_NOT_UPDATE           = ERR_AGENT - 14;   // 로그인 중 사용자를 업데이트할 수 없습니다.
    //호에 대한 에러
    final static int ERR_CALL                       = -200;
    final static int ERR_CALL_CREATE                = ERR_CALL - 1;	// 새로운 호를 만들 수 없습니다.
    final static int ERR_CALL_NO_RINGING_LINE       = ERR_CALL - 2;	// 벨이 울리는 회선이 존재하지 않습니다.
    final static int ERR_CALL_NO_BUSY_LINE          = ERR_CALL - 3;	// 통화중인 회선이 존재하지 않습니다.
    final static int ERR_CALL_NO_HOLD_LINE          = ERR_CALL - 4;	// 해당 DN에 Hold된 회선이 존재하지 않습니다.
    final static int ERR_CALL_WRONG_NUMBER          = ERR_CALL - 5;	// 잘못된 전화번호 입니다.
    final static int ERR_CALL_ITSELF_NUMBER         = ERR_CALL - 6;	// 자신의 번호로 발신할 수 없습니다.
    final static int ERR_CALL_NOT_FOUND             = ERR_CALL - 7;	// 해당 콜을 찾을 수 없습니다.
    final static int ERR_CALL_ALREADY_EXIST         = ERR_CALL - 8;	// 해당 콜이 이미 존재합니다.
    final static int ERR_CALL_UUI_INDEX_OUTOFBOUND  = ERR_CALL - 9;	// UUI 필드의 인덱스 범위가 아닙니다.
    final static int ERR_CALL_UUI_WRONG_FORMAT      = ERR_CALL - 10;    // 잘못된 UUI 형식입니다.
    //장치에 대한 에러
    final static int ERR_DEV                        = -300;
    final static int ERR_DEV_UNREG                  = ERR_DEV - 1;      // 등록된 전화번호가 아닙니다.
    final static int ERR_DEV_WRONG_STATE            = ERR_DEV - 2;	// 전화의 상태가 바르지 않습니다.
    final static int ERR_DEV_ISNOT_LOGIN            = ERR_DEV - 3;	// 로그인한 전화번호가 아닙니다.
    final static int ERR_DEV_ALREADY_LOGIN          = ERR_DEV - 4;	// 해당 전화번호를 다른 사용자가 사용중입니다.
    final static int ERR_DEV_NOT_FOUND              = ERR_DEV - 5;	// 장치를 찾을수 없습니다.
    final static int ERR_DEV_ALREADY_EXIST          = ERR_DEV - 6;	// 장치가 이미 있습니다.
    final static int ERR_DEV_ANONTHER_LOGIN         = ERR_DEV - 7;	// 해당 장치에 댜른 사람이 로그인되어 있습니다.
    final static int ERR_DEV_PHONEPADINFO_EXIST     = ERR_DEV - 8;	// 폰패드 요청 정보가 이미 존재합니다.
    final static int ERR_DEV_PHONEPADINFO_NOT_EXIST = ERR_DEV - 9;	// 폰패드 요청 정보가 존재하지 않습니다.
    final static int ERR_DEV_IS_LOGIN               = ERR_DEV - 10;	// 로그인 상태에서는 직원정보를 수정할 수 없습니다.(NewProj)
    final static int ERR_DEV_IS_HUNT                = ERR_DEV - 11;	// 헌트그룹 상태확인 조회실패 입니다. 
    final static int ERR_DEV_SET_HUNT               = ERR_DEV - 12;	// 헌트그룹 로그인 실패 입니다. 
    
    // 통신에 대한 에러
    final static int ERR_COMM                       = -400;             // 통신에러 입니다.
    final static int ERR_COMM_NO_CREATE_PROVIDER    = ERR_COMM - 1;	// Provider를 만들 수 없습니다.
    final static int ERR_COMM_NO_GETADDRESS         = ERR_COMM - 2;	// 주소정보를 가져올 수 없습니다.
    final static int ERR_COMM_CANNOT_BIND           = ERR_COMM - 3;	// 서버 소켓이 포트에 바인딩할 수 없습니다.
    final static int ERR_COMM_ALREADY_OPEN          = ERR_COMM - 4;	// 서버 소켓이 이미 열려있습니다.
    final static int ERR_COMM_EXCEPTION             = ERR_COMM - 5;	// 소켓 예외 에러입니다.
    final static int ERR_COMM_SOCKET_ISNULL         = ERR_COMM - 6;	// 소켓이 초기화 되지 않았씁니다.
    final static int ERR_COMM_SOCKET_ISCLOSE        = ERR_COMM - 7;	// 소켓이 닫혀있습니다.
    final static int ERR_COMM_MSG_ISNULL            = ERR_COMM - 8;	// 전송문자열이 null입니다.
    final static int ERR_COMM_MSG_LENGTH0           = ERR_COMM - 9;	// 전송문자열의 길이가 0 입니다.
    final static int ERR_COMM_ONERRTOSEND           = ERR_COMM - 10;	// 전송중 에러가 발생했습니다.
    final static int ERR_COMM_MISMATCH_VERSION      = ERR_COMM - 11;	// 버젼정보가 맞지 않습니다.
    final static int ERR_COMM_CANNOT_CONNECT        = ERR_COMM - 12;	// 서버에 연결할 수 없습니다.
    final static int ERR_COMM_GROUP_ALERADY_MONITOR = ERR_COMM - 13;	// 그룹이 이미 모니터링중입니다.
    // 다바이스 모니터링에 대한 에러
    final static int ERR_MONI                       = -500;             // 디바이스 모니터링 에러입니다.
    final static int ERR_MONI_ALEADY_MONITORING     = ERR_MONI - 1;	// 이미 모니터링 중입니다.
    final static int ERR_MONI_NOTFOUND_GROUP        = ERR_MONI - 2;	// 해당 그룹이 존재하지 않습니다.
    final static int ERR_MONI_UNREG_SOCKET          = ERR_MONI - 3;	// 모니터링 요청되지 않았습니다.
    // 그룹에 대한 에러
    final static int ERR_GRP                        = -600;             // 그룹에러 입니다.
    final static int ERR_GRP_ALREADY_EXIST          = ERR_GRP - 1;	// 그룹이 이미 존재합니다.
    final static int ERR_GRP_NOT_FOUND              = ERR_GRP - 2;	// 그룹이 존재하지 않습니다.
    // 로그인에 대한 에러
    final static int RTN_LOGIN                      = -700;             // 로그인 에러입니다.
    final static int RTN_LOGIN_ISNOT_LOGIN          = RTN_LOGIN - 1;	// 로그인 하지 않았습니다.
    final static int RTN_LOGIN_SS_EXSIT_NOT_LOGIN   = RTN_LOGIN - 2;	// Session은 존재하나 로그인 하지 않았습니다.
    final static int RTN_LOGIN_SAME_SESSION_LOGIN   = RTN_LOGIN - 3;	// 같은 Session에 DN이 로그인 하였습니다.
    final static int RTN_LOGIN_DIFF_SESSION_LOGIN   = RTN_LOGIN - 4;	// 다른 Session에 DN이 로그인 하였습니다.
    // Common 에러
    final static int RTN_EXCEPTION                  = -900;             // 알수없는 에러입니다.
    final static int RTN_INVALIED_AGUMENT           = RTN_EXCEPTION - 1;// 함수의 인자가 바르지 않습니다.
    final static int RTN_UNDEFINED_ERR              = RTN_EXCEPTION - 2;// 정의되지 않은 에러가 발생했습니다.
    final static int RTN_TIMEOUT                    = RTN_EXCEPTION - 3;// 제한된 시간이 초과되었습니다.
    
    final static int HTTP_SUCCESS					= 200;
    
   
    
}
