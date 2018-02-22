package com.isi.axl.soap;
import java.io.File;
import java.net.InetAddress;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
*
* @author greatyun
*/
public class Utils {
	private static final String HASH_ALGORITHM = "HmacSHA256"; 
	
 	// SHA256 으로 암호화 처리.
    public static String hashMac(String text, String secretKey)   throws SignatureException {
    	 try { 
			  Key sk = new SecretKeySpec(secretKey.getBytes(), HASH_ALGORITHM);
			  Mac mac = Mac.getInstance(sk.getAlgorithm());
			  mac.init(sk);
			  final byte[] hmac = mac.doFinal(text.getBytes());
			  return toHexString(hmac);
		 } catch (NoSuchAlgorithmException e1) {
		  // throw an exception or pick a different encryption method
		  throw new SignatureException(
		    "error building signature, no such algorithm in device "
		      + HASH_ALGORITHM);
		 } catch (InvalidKeyException e) {
		  throw new SignatureException(
		    "error building signature, invalid key " + HASH_ALGORITHM);
		 }
	}
    
    public static String toHexString(byte[] bytes) {  
        StringBuilder sb = new StringBuilder(bytes.length * 2);   
        Formatter formatter = new Formatter(sb);  
        for (byte b : bytes) {  
            formatter.format("%02x", b);  
        }   
        return sb.toString();  
    }  
	
	public String SHA256(String str){
		String SHA = ""; 
		try{
			MessageDigest sh = MessageDigest.getInstance("SHA-256"); 
			sh.update(str.getBytes()); 
			byte byteData[] = sh.digest();
			StringBuffer sb = new StringBuffer(); 
			for(int i = 0 ; i < byteData.length ; i++){
				sb.append(Integer.toString((byteData[i]&0xff) + 0x100, 16).substring(1));
			}
			SHA = sb.toString();
			
		}catch(NoSuchAlgorithmException e){
			e.printStackTrace(); 
			SHA = null; 
		}
		return SHA;
	}

	public  boolean isJumin(String jumin) {
		boolean isKorean = true;
		int check = 0;
		if (jumin == null || jumin.length() != 13) {
			return false;
		}
		if (Character.getNumericValue(jumin.charAt(6)) > 4
				&& Character.getNumericValue(jumin.charAt(6)) < 9) {
			isKorean = false;
		}
		for (int i = 0; i < 12; i++) {
			if (isKorean) {
				check += ((i % 8 + 2) * Character.getNumericValue(jumin.charAt(i)));
			} else {
				check += ((9 - i % 8) * Character.getNumericValue(jumin.charAt(i)));
			}
		}
		if (isKorean) {
			check = 11 - (check % 11);
			check %= 10;
		} else {
			int remainder = check % 11;
			if (remainder == 0)
				check = 1;
			else if (remainder == 10)
				check = 0;
			else
				check = remainder;

			int check2 = check + 2;
			if (check2 > 9)
				check = check2 - 10;
			else
				check = check2;
		}

		if (check == Character.getNumericValue(jumin.charAt(12)))
			return true;
		else
			return false;
	}
	
	
	
	public  boolean checkSIDForign(String sid) {
		int odd = 0;
		String sidTmp = "";
		sidTmp = sid;
		
		odd = Integer.parseInt(sid.charAt(7) + "") * 10 + (Integer.parseInt(sid.charAt(8) + ""));
		
		if(odd % 2 != 0) { 
			return false; 
		}
		
//		System.out.println(sid.charAt(11) + "," + sid.charAt(11) + "," + sid.charAt(11) + ", " + sid.charAt(11));
		if(sid.charAt(11) != '6' && sid.charAt(11) != '7' && sid.charAt(11) != '8' && sid.charAt(11) != '9') {
			return false;
		}
		
		
		int[] checkNum = {2,3,4,5,6,7,8,9,2,3,4,5};
		int sum = 0;
		int i = 0;
		for(sum = 0, i = 0; i < 12; i++) {
			int nNum = (Integer.parseInt(sid.charAt(i) + ""));
			sum += (nNum *= checkNum[i]);
		}
		sum = 11 - (sum % 11);
		if(sum >= 10) {
			sum -= 10;
		}
		sum += 2;
		
		if(sum >= 10) {
			sum -= 10;
		}
		
		int nCheckMagicNum = Integer.parseInt(sid.charAt(12) + "");
		if(sum != nCheckMagicNum) {
			return false;
		}
		
		return true;
		
	}

	
	
	public String trimNum(String num) {
		if(num == null) return "0";

		boolean isMinus = false;
		String result = "";

		if(num.startsWith("-")) {
			num = num.substring(1);
			isMinus = true;
		}

		int index = num.indexOf(".");

		if (index == -1) {
			for (int i = 0; i < num.length(); i++) {
				if (!num.substring(i, i + 1).equals("0")) {
					result = num.substring(i);
					break;
				}
			}

		} else {

			String temp1 = num.substring(0, index);
			String temp2 = num.substring(index + 1, num.length());

			for (int i = 0; i < temp1.length(); i++) {
				if (!temp1.substring(i, i + 1).equals("0")) {
					result = temp1.substring(i);
					break;
				}
			}

			String result2 = "";
			for (int i = temp2.length(); i > 0; i--) {
				if (!temp2.substring(i - 1, i).equals("0")) {
					result2 = temp2.substring(0, i);
					break;
				}
			}
			if (result2.length() != 0) {
				result = result + "." + result2;
			}

		}
		if(result.isEmpty()) {
			return "0";
		}

		if(result.startsWith("."))
			result  = "0" + result;

		if(isMinus){
			result = "-" + result;
		}

		return result;
	}

	public boolean isDoubleValue(double value){
		int index = String.valueOf(value).indexOf(".");
		if(String.valueOf(value).substring(index+1).equals("0"))
			return true;
		else 
			return false;
	}
	
	

	// ARS Password Check
	public boolean checkARSPwd(String stNum) {
		int[] tempNum = new int[stNum.length()];
		int cnt = 0;
		int conCnt = 0;
		int temp = 0;
		boolean flag = true;
		for (int i = 0; i < stNum.length(); i++) {
			if(i < stNum.length() - 1) {
				if(stNum.substring(i, i+1).equals(stNum.substring(i+1, i+2)))
					cnt++;
				if(i != 0) {
					if(temp == Integer.parseInt(stNum.substring(i, i+1)) - Integer.parseInt(stNum.substring(i+1, i+2)))
						conCnt++;
				}
				temp = Integer.parseInt(stNum.substring(i, i+1)) - Integer.parseInt(stNum.substring(i+1, i+2));

			}

		}
		if(cnt == (stNum.length() - 1) || conCnt == stNum.length() - 2){
			return true;
		} else 
			return false;

		//              
		//        for (int i = 0; i < stNum.length(); i++) {
			//            if (i < stNum.length() - 1) {
				//                tempNum[i] = Integer.parseInt(stNum.substring(i, i + 1));
				//                int temp = Integer.parseInt(stNum.substring(i + 1, i + 2));
		//                cnt += tempNum[i] - temp;
		//            }
		//        }
		//        if (Math.abs(cnt) == (stNum.length() - 1) || Math.abs(cnt) == 0) {
		//            return true;
		//        } else {
		//            return false;
		//        }
	}



	public String readString(String str, int start, int length) {
		try {
			byte[] data = str.getBytes("EUC-KR");
			byte[] b = new byte[length];
			int j = 0;

			for (int i = start; i < start + length; i++) {
				b[j] = data[i];
				j++;
			}
			String result = new String(b, 0, j, "EUC-KR");
			if (result == null || result.equals("")) {
				for (int i = 0; i < j; i++) {
					result += " ";
				}
			}
			return result;
		} catch (Exception e) {
			return null;
		}
	}

	public boolean isConsecutiveNum(String test) {
		boolean result = false;
		int[] num = new int[test.length()];
		int cnt = 0;
		for (int i = 0; i < num.length; i++) {
			if (i < num.length - 1) {
				num[i] = Integer.parseInt(test.substring(i, i + 1));
				int temp = Integer.parseInt(test.substring(i + 1, i + 2));
				if ((num[i] - temp) == -1 || (num[i] - temp) == 1) {
					cnt++;
					if (cnt - i == 1) {
						result = true;
					} else {
						result = false;
						break;
					}
				} else {
					result = false;
					break;
				}
			}
		}
		return result;
	}


	/**
	 * 전화번호 유효성 검사
	 *
	 * @param str "0212345678"
	 * @return
	 */
	public boolean isTelePhone(String str) {
		/*
         02  서울특별시
         031 경기도
         032 인천광역시
         033 강원도
         041 충청남도
         042 대전광역시
         043 충청북도
         051 부산광역시
         052 울산광역시
         053 대구광역시
         054 경상북도
         055 경상남도
         061 전라남도
         062 광주광역시
         063 전라북도
         064 제주특별자치도
		 */
		//return str.matches("(0(2|3(1|2|3)|4(1|2|3)|5(1|2|3|4|5)|6(1|2|3|4)))-(\\d{3,4})-(\\d{4})");
		return str.matches("(0(2|3(1|2|3)|4(1|2|3)|5(1|2|3|4|5)|6(1|2|3|4)))(\\d{3,4})(\\d{4})");
	}

	/**
	 * 핸드폰번호 유효성 검사
	 *
	 * @param str "01011112222"
	 * @return
	 */
	public boolean isCellphone(String str) {
		//010, 011, 016, 017, 018, 019
		return str.matches("(01[016789])(\\d{3,4})(\\d{4})");
	}

	/**
	 * 문자열이 영어로만 이루어졌는지 유효성 검사
	 *
	 * @param str "문자"
	 * @return
	 */
	public boolean isAlphabet(String str) {
		return str.matches("^[A-Za-z]*$");
	}

	/**
	 * 문자열이 대문자로만 이루어졌는지 유효성 검사
	 *
	 * @param str "문자"
	 * @return
	 */
	public boolean isUpper(String str) {
		return str.matches("^[A-Z]*$");
	}

	/**
	 * 문자열이 소문자로만 이루어졌는지 유효성 검사
	 *
	 * @param str "문자"
	 * @return
	 */
	public boolean isLower(String str) {
		return str.matches("^[a-z]*$");
	}

	/**
	 * 문자열이 숫자로만 이루어졌는지 유효성 검사
	 *
	 * @param str "문자"
	 * @return
	 */
	public static boolean isNumber(String str) {
		if(str.length()>0){
			return str.matches("^[0-9]*$");
		}else{
			return false;
		}
	}

	/**
	 * 두날짜의 차이를 구한다.
	 *
	 * @param startDate
	 * @param endDate
	 * @param pattern
	 * @return
	 */
	public long getDateDiff(String startDate, String endDate, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		long diff = 0l;
		try {
			Calendar startCal = new GregorianCalendar();
			Calendar endCal = new GregorianCalendar();
			startCal.setTime(sdf.parse(startDate));
			endCal.setTime(sdf.parse(endDate));

			long diffMillis = endCal.getTimeInMillis() - startCal.getTimeInMillis();
			diff = diffMillis / (24 * 60 * 60 * 1000);
		} catch (Exception e) {
		}
		return diff;
	}

	/**
	 * 문자열 날짜의 요일을 반환 (영문 한글 선택 가능)
	 *
	 * @param dateText 바꿀문자열
	 * @param pattern Date타입 ex) "yyyy-MM-dd" or "yyyyMMdd"
	 * @param korean
	 * @return
	 */
	public String getDayOfWeek(String dateText, String pattern, boolean korean) {
		Date date = null;
		String[][] week = {{"일", "Sun"}, {"월", "Mon"}, {"화", "Tue"}, {"수", "Wen"}, {"목", "Thu"}, {"금", "Fri"}, {"토", "Sat"}};

		try {
			SimpleDateFormat sdf = new SimpleDateFormat(pattern);
			date = sdf.parse(dateText);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			if (korean) {
				return week[cal.get(Calendar.DAY_OF_WEEK) - 1][0];
			} else {
				return week[cal.get(Calendar.DAY_OF_WEEK) - 1][1];
			}
		} catch (Exception e) {
			return "";
		}

	}

	/**
	 * 한글형변환
	 *
	 * @param str 한글
	 * @return
	 */
	public String ms9492uni(String str) {
		try {
			if (str.length() > 0) {
				return new String(str.getBytes("MS949"), "8859_1");
			} else {
				return "";
			}
		} catch (Exception e) {
			return "";
		}
	}

	public String uni2ms949(String str) {
		try {
			if (str.length() > 0) {
				return new String(str.getBytes("8859_1"), "MS949");
			} else {
				return "";
			}
		} catch (Exception e) {
			return "";
		}
	}

	public String ms9492ksc(String str) {
		try {
			if (str.length() > 0) {
				return new String(str.getBytes("MS949"), "KSC5601");
			} else {
				return "";
			}
		} catch (Exception e) {
			return "";
		}
	}

	public String ksc2ms949(String str) {
		try {
			if (str.length() > 0) {
				return new String(str.getBytes("KSC5601"), "MS949");
			} else {
				return "";
			}
		} catch (Exception e) {
			return "";
		}
	}

	public String uni2ksc(String Unicodestr) {
		try {
			if (Unicodestr.length() > 0) {
				return new String(Unicodestr.getBytes("8859_1"), "KSC5601");
			} else {
				return "";
			}
		} catch (Exception e) {
			return "";
		}
	}

	public String ksc2uni(String str) {
		try {
			if (str.length() > 0) {    
				return new String(str.getBytes("KSC5601"), "8859_1");
			} else {
				return "";
			}
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 날짜 유효성을 체크하는 함수
	 *
	 * @param str "20140523"
	 * @return
	 */
	public boolean isRightDate(String sYYYYMMDD) {
		try {
			String textDate = sYYYYMMDD;
			java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyyMMdd");
			java.util.Date date = format.parse(textDate);
			java.text.SimpleDateFormat format1 = new java.text.SimpleDateFormat("yyyyMMdd");
			String dateString = format1.format(date);
			format = null;
			format1 = null;
			if (sYYYYMMDD.equals(dateString)) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}
	

	/**
	 * 서버 IP 리턴
	 *
	 * @param
	 * @return str ex)"192.168.230.1"
	 */
	public String getLocalIP() {
		try {
			InetAddress Address = InetAddress.getLocalHost();
			String sRtn = Address.getHostAddress();
			return sRtn;
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 서버 날짜시간값 리턴
	 *
	 * @param
	 * @return str ex)"20140525101528"
	 */
	public String getCurrentDay() {
		try {
			Date today = Calendar.getInstance().getTime();
			SimpleDateFormat fomatter = new SimpleDateFormat("yyyyMMddHHmmss", java.util.Locale.KOREA);
			String current = fomatter.format(today);
			return current;
		} catch (Exception e) {
			return "";
		}
	}

	public String getCurrentDayLong() {
		Calendar cal = Calendar.getInstance();
		long i = cal.getTimeInMillis();
		return String.valueOf(i);
	}

	/**
	 * 서버 시간값 리턴
	 *
	 * @param
	 * @return str ex)"101627211"
	 */
	public String getCurrentTime() {
		try {
			Date today = Calendar.getInstance().getTime();
			SimpleDateFormat fomatter = new SimpleDateFormat("HHmmssSSS", java.util.Locale.KOREA);
			String current = fomatter.format(today);
			return current;
		} catch (Exception e) {
			return "";
		}
	}
	
	/**
	 * 두 시간의 차이값 리턴
	 *
	 * @param
	 * @return str ex)"20" 초
	 */
	public long getTimeDiff(long beforeTime) {
		try {
			long currentTime = System.currentTimeMillis();
			if(currentTime > beforeTime){
				long diff = currentTime - beforeTime;
				return diff;
			} else {
				return -1;
			}
		} catch (Exception e) {
			return -1;
		}
	}
	
	

	/**
	 * 어제의 날짜값 리턴
	 *
	 * @param
	 * @return str ex)"20140425"
	 */
	public String getBeforeDay(int day) {
		try {
			SimpleDateFormat fomatter = new SimpleDateFormat("yyyyMMdd", java.util.Locale.KOREA);
			GregorianCalendar cal = new GregorianCalendar();
			cal.add(Calendar.DATE, -day);
			Date d = cal.getTime();
			String s = fomatter.format(d);
			return s;
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 오늘의 날짜값 리턴
	 *
	 * @param
	 * @return str ex)"20140425"
	 */
	public String getToday() {
		try {
			Date today = Calendar.getInstance().getTime();
			SimpleDateFormat fomatter = new SimpleDateFormat("yyyyMMdd", java.util.Locale.KOREA);
			String current = fomatter.format(today);
			return current;
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 입력받은 문자를 변환하여 리턴
	 *
	 * @param 원본값
	 * @param 기존값
	 * @param 바꿀값
	 * @return str ex)"010-1234-5678","-","" -> "01012345678"
	 */
	public String replace(String src, String oldstr, String newstr) {
		try {
			if (src == null) {
				return null;
			}
			StringBuffer dest = new StringBuffer("");
			int len = oldstr.length();
			int srclen = src.length();
			int pos = 0;
			int oldpos = 0;

			while ((pos = src.indexOf(oldstr, oldpos)) >= 0) {
				dest.append(src.substring(oldpos, pos));
				dest.append(newstr);
				oldpos = pos + len;
			}

			if (oldpos < srclen) {
				dest.append(src.substring(oldpos, srclen));
			}

			return dest.toString();
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 입력받은 길이만큼 왼쪽에 대체문자로 채워줌
	 *
	 * @param str 대상문자열, len 길이, addStr 대체문자
	 * @return 문자열 ex)"TEST1234",10,"*" -> "**TEST1234"
	 */
	public String lpad(String str, int len, String addStr) {
		try {
			String result = str;
			if(result.length() > len){
				result = result.substring(0,len);
			}
			int templen = len - result.length();
			for (int i = 0; i < templen; i++) {
				result = addStr + result;
			}
			return result;
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 입력받은 길이만큼 오른쪽에 대체문자로 채워줌
	 *
	 * @param str 대상문자열, len 길이, addStr 대체문자
	 * @return 문자열 ex)"TEST1234",10,"*" -> "TEST1234**"
	 */
	public String rpad(String str, int len, String addStr) {
		try {
			String result = str;
			if(result.length() > len){
				result = result.substring(0,len);
			}
			int templen = len - result.getBytes("euc-kr").length;
			for (int i = 0; i < templen; i++) {
				result = result + addStr;
			}
			return result;
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 대상문자열을 입력받은 대체문자로 오른쪽부터 길이만큼 채워줌(로그암호화에서 사용)
	 *
	 * @param str 대상문자열, len 길이, addStr 대체문자
	 * @return 문자열 ex)"7705161753341",7,"*" -> "770516*******"
	 */
	public String rpadSecret(String str, int len, String addStr) {
		try {
			int templen = str.getBytes().length - len;
			String result = str.substring(0, templen);
			for (int i = 0; i < len; i++) {
				result = result + addStr;
			}
			return result;
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 대상문자열을 입력받은 대체문자로 왼쪽부터 길이만큼 채워줌(로그암호화에서 사용)
	 *
	 * @param str 대상문자열, len 길이, addStr 대체문자
	 * @return 문자열 ex)"7705161753341",6,"*" -> "******1753341"
	 */
	public String lpadSecret(String str, int len, String addStr) {
		try {
			String result = str.substring(len, str.getBytes().length);
			for (int i = 0; i < len; i++) {
				result = addStr + result;
			}
			return result;
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 입력받은 숫자 전화번호를 전화번호 규격에 맞도록 리턴
	 *
	 * @param str 전화번호문자열
	 * @return 문자열 ex)"03123456789" -> "031-2345-6789"
	 */
	public String makeTelForm(String stTelNum) {
		try {
			String stLocal = stTelNum.substring(0, 2);
			String stTelNum1 = null;
			String stTelNum2 = null;
			String stTelForm = null;
			int nLength = stTelNum.length();

			if (nLength < 4) {
				return null;
			} else if (nLength >= 4 && nLength <= 8) {
				stTelNum1 = stTelNum.substring(0, nLength - 4);
				stTelNum2 = stTelNum.substring(nLength - 4, nLength);
				if (stTelNum1.length() <= 0) {
					stTelForm = stTelNum2;
				} else {
					stTelForm = stTelNum1 + "-" + stTelNum2;
				}
				return stTelForm;
			} else if (stLocal.equals("02")) {
				stTelNum1 = stTelNum.substring(2, nLength - 4);
				stTelNum2 = stTelNum.substring(nLength - 4, nLength);
				stTelForm = stLocal + "-" + stTelNum1 + "-" + stTelNum2;
				return stTelForm;
			} else {
				stLocal = stTelNum.substring(0, 3);
				stTelNum1 = stTelNum.substring(3, nLength - 4);
				stTelNum2 = stTelNum.substring(nLength - 4, nLength);
				stTelForm = stLocal + "-" + stTelNum1 + "-" + stTelNum2;
				return stTelForm;
			}
		} catch (Exception e) {
			return "";
		}
	}



	/**
	 * 입력받은 주민등록번호의 유효성 체크
	 *
	 * @param str 주민등록번호
	 * @return true/false
	 * 외국인주민번호도 한번더 체크한다
	 */
	public boolean checkSID(String sid) {
		int[] jumin = new int[13];
		char ctemp;
		int hap = 0, cre = 2;
		float temp = 0.0f, temp1 = 0.0f;
		boolean result = false;
		
		if (sid.length() != 13 || sid.equals("0000000000001")) {
			return false;
//			result = false;
		}

		for (int i = 0; i < 13; i++) {
			ctemp = sid.charAt(i);
			if (ctemp < '0' || ctemp > '9') {
				result = false;
			} else {
				jumin[i] = Character.getNumericValue(ctemp);
				if (i == 6) {
					if (jumin[2] * 10 + jumin[3] > 12) {
//						return false;    // 태어난 달은 12월보다 클 수 없다 
						result = false;
					} else if (jumin[4] * 10 + jumin[5] > 31) {
//						return false;   // 태어난 일은 31일보다 클 수 없다
						result = false;
					} else if (jumin[6] != 9 && jumin[6] != 0 && jumin[6] != 1 && jumin[6] != 2 && jumin[6] != 3 && jumin[6] != 4) {
//						return false;   // 성별을 나타내는 숫자는 9,0,1,2,3,4 중 하나여야 한다
						result = false;
					} else if (jumin[0] >= 0 && jumin[0] < 3) {
						if (jumin[6] < 3 || jumin[6] > 5) {
//							return false;
							result = false;
						}
					} else if (jumin[2] == 0 && jumin[3] == 2) {
						if (jumin[4] >= 3) {
//							return false;
							result = false;
						}
					}
				}
				if (cre == 10) {
					cre = 2;
				}
				if (i != 12) {
					hap += jumin[i] * cre;
					cre++;
				}
			}
		}
		temp = (int) (hap / 11.0f) * 11.0f + 11.0f - hap;
		temp1 = temp - (int) (temp / 10.0f) * 10.0f;
		if (temp1 != jumin[12]) {
//			return false;   // 주민번호 검증 수가 올바르지 않습니다
			result = false;
		} else {

			return true;
		}

		return checkSIDForign(sid);
		
		
	}

	/**
	 * 입력받은 연도월 값의 마지막날 숫자 리턴
	 *
	 * @param str 연도월 예) "201405"
	 * @return 마지막날짜 숫자리턴 예) "31"
	 */
	public String getLastDate(String s_date) {
		try {
			int Year = Integer.parseInt(s_date.substring(0, 4));
			int Month = Integer.parseInt(s_date.substring(4, 6));
			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("JST"));
			cal.set(Year, Month, 1);
			cal.setTime(new java.util.Date(cal.getTime().getTime() - 86400000));
			return String.valueOf(cal.get(Calendar.DATE));
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 오늘이 주말(토,일요일) 인지 유효성 체크
	 *
	 * @param
	 * @return true/false
	 */
	public boolean getWeekYN() {
		try {
			Calendar cal = Calendar.getInstance();
			cal.get(Calendar.DAY_OF_WEEK);
			if (cal.get(Calendar.DAY_OF_WEEK) == 1 || cal.get(Calendar.DAY_OF_WEEK) == 7) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}
	

	  public static String getBase64(String text)
	  {
	    int i = 0;
	    int j = 0;

	    String base64key = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
	    StringBuffer base64string = new StringBuffer();
	    char text0, text1, text2;

	    for (i = 0; i < text.length();)
	    {
	      text0 = text.charAt(i);

	      base64string.setLength(j+4);
	      base64string.setCharAt(j, base64key.charAt((text0 & 252) >> 2));
	      if ((i+1)<text.length())
	      {
	        text1 = text.charAt(i+1);
	        base64string.setCharAt(j+1, base64key.charAt(((text0 & 3) << 4) | ((text1 & 240) >> 4)));
	        if ((i+2)<text.length())
	        {
	          text2 = text.charAt(i+2);
	          base64string.setCharAt(j+2, base64key.charAt(((text1 & 15) << 2) | ((text2 & 192) >> 6)));
	          base64string.setCharAt(j+3, base64key.charAt((text2 & 63)));
	        }
	        else
	        {
	          base64string.setCharAt(j+2, base64key.charAt(((text1 & 15) << 2)));
	          base64string.setCharAt(j+3, (char)61);
	        }
	      }
	      else
	      {
	        base64string.setCharAt(j+1, base64key.charAt(((text0 & 3) << 4)));
	        base64string.setCharAt(j+2, (char)61);
	        base64string.setCharAt(j+3, (char)61);
	      }

	      i += 3;
	      j += 4;

	    }

	    return (base64string.toString());
	  }
	
	  public boolean deleteDirectory(File path) throws Exception{
			// TODO Auto-generated method stub
			boolean result = true;
			if (!path.exists()) {
				path.mkdirs();
			}

			File[] files = path.listFiles();
			for (File file : files) {
				if (file.isDirectory()) {
					deleteDirectory(file);
				} else {
					if(file.delete()){
//						System.out.println("DELETE SUCCESS : " + file.getAbsolutePath());
						Thread.sleep(100);
					} else {
						result = false;
//						System.out.println("DELETE FAIL : " + file.getAbsolutePath());
					}
				}
				file.delete();
			}
			
			return result;
		}
	
}
