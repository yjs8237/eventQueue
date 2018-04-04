package com.isi.test;

/**
 *-------------------------------------------------------------------------------
 *   Project      : Insight Manager
 *   Program ID   : CommonUtil.java
 *   Author       : JEUNGDEOK,SEO
 *   Date         : 2016.05
 *-------------------------------------------------------------------------------
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import java.util.StringTokenizer;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.html.HTMLEditorKit;

import org.json.JSONArray;


public class CommonUtil { 
	
	  public static boolean isNumber(String str) {
	        try {
	            Integer.parseInt(str);
	            return true;
	        } catch (Exception e) {
	            return false;
	        }
	    }
	
	public static int isValidJsonData (JSONArray jsonArr) {
		if(jsonArr == null || jsonArr.length() == 0) {
			return -1;
		}
		if(jsonArr.getJSONObject(0).has("axlcode")) {
			return 0; 
		}
		return 1;
	}
	
	public static String getLocalServerIp()
	{
		try
		{
		    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();)
		    {
		        NetworkInterface intf = en.nextElement();
		        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();)
		        {
		            InetAddress inetAddress = enumIpAddr.nextElement();
		            if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress() && inetAddress.isSiteLocalAddress())
		            {
		            	return inetAddress.getHostAddress().toString();
		            }
		        }
		    }
		}
		catch (SocketException ex) {}
		return null;
	}
	
	public static String getNowDate() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(new Date());
	}
	
	public static String getPhoneMask(String dn) {
		String inputDn = nullString(dn);
		String phoneMask = "";
		if (inputDn.length() == 4)
			if (inputDn.startsWith("00") || inputDn.startsWith("01") || inputDn.startsWith("14") || inputDn.startsWith("15")
					|| inputDn.startsWith("16") || inputDn.startsWith("17") || inputDn.startsWith("23") || inputDn.startsWith("25")
					|| inputDn.startsWith("30") || inputDn.startsWith("31") || inputDn.startsWith("32") || inputDn.startsWith("34")
					|| inputDn.startsWith("9"))
				phoneMask = "3781XXXX";
			else if (inputDn.startsWith("02") || inputDn.startsWith("03") || inputDn.startsWith("04") || inputDn.startsWith("05")
					|| inputDn.startsWith("06") || inputDn.startsWith("07") || inputDn.startsWith("08") || inputDn.startsWith("09")
					|| inputDn.startsWith("33") || inputDn.startsWith("40") || inputDn.startsWith("47") || inputDn.startsWith("64")
					|| inputDn.startsWith("70") || inputDn.startsWith("79") || inputDn.startsWith("80") || inputDn.startsWith("81")
					|| inputDn.startsWith("82") || inputDn.startsWith("83") || inputDn.startsWith("84") || inputDn.startsWith("85")
					|| inputDn.startsWith("87") || inputDn.startsWith("88") || inputDn.startsWith("89"))
				phoneMask = "709XXXX";
			//else
				//phoneMask = "4444XXXX";	//!WISEO! 지워야함
		
		if (inputDn.length() == 3 && inputDn.startsWith("4"))
			phoneMask = "6400XXX";
		else if (inputDn.length() == 3 && inputDn.startsWith("6"))
			phoneMask = "6309XXX";
		else
			phoneMask = "8282XXX";
		return phoneMask;
	}
	
	
	 public static String getInitial(String building) {
        if(building.equals("1"))
            return "K";
        if(building.equals("2"))
            return "S";
        else
            return "";
	 }
	
	
	/**
	 * Class의 이름명칭을 반환한다.
	 * @param  이름을 얻을 Class
	 * @return String Class 이름명칭
	 */
	public static String getClassName(Object obj) {
		return obj.getClass().getName()
				.substring(obj.getClass().getName().lastIndexOf('.') + 1);
	}

	
    // *************** 문자열 관련 함수 *************** //	
	/*** 8859_1 --> KSC5601. ***/
	public static String E2K( String english )	{
		String korean = null;
		if (english == null ) return null; 
		//if (english == null ) return "";
		try {
			korean = new String(english.getBytes("8859_1"), "KSC5601");
		}catch( UnsupportedEncodingException e ){
			korean = new String(english);
		}
		return korean;
	}

	/*** KSC5601 --> 8859_1. ***/
	public static String K2E( String korean ){
		String english;
		try {
			english = new String(korean.getBytes("8859_1"), "EUC-KR");
		}catch( UnsupportedEncodingException e ){
			english = new String(korean);
		}
		return english;
	}
		
	/**
	 * 문자열을 받아서 암호화
	 * @param String pwd
	 * @return 변환된 문자열
	 */
	
	
	
	/**
	 * 문자열을 받아서 null Check 
	 * @param strString
	 * @return boolean
	 */
	public static boolean isNull(String str) {
		if ((str == null) || (str.trim().equals(""))
				|| (str.trim().equals("null")))
			return true;
		else
			return false;
	}	
	
	/**
	 * 문자열이 같은지 비교 
	 * @param strString1, strString
	 * @return boolean
	 */
	public static boolean isEquals(String str1, String str2) {
		if (str1.equals(str2))
			return true;
		else
			return false;
	}	
	
	/**
	 * 문자열을 받아서 null이면 공백 문자열로 리턴 한다. null 일 경우 ""반환 
	 * @param str
	 * @return 변환된 문자열
	 */
	public static String nullString(String str) {
		if ((str == null) || (str.trim().equals(""))
				|| (str.trim().equals("null")))
			return "";
		else
			return str;
	}
	
	/**
	 * 문자열을 받아서 null이면 다른 문자열로 리턴 한다.
	 * @param str_in, str_out 변환될 문자열
	 * @return 변환된 문자열
	 */
	public static String nullChange(String str_in, String str_out) {
		if (str_in == null || str_in.equals("")|| str_in.equals("null")) {
			str_in = str_out;
		}
		return str_in;
	}

	/**
	 * 문자열을 받아서 strSearch 가 포함되어 있는 길이
	 * @param strTarget, strSearch 변환될 문자열
	 * @return 포함된 길이 
	 */
	public static int search(String strTarget, String strSearch) {
		int result = 0;
		String strCheck = new String(strTarget);
		for (int i = 0; i < strTarget.length();) {
			int loc = strCheck.indexOf(strSearch);
			if (loc == -1) {
				break;
			} else {
				result++;
				i = loc + strSearch.length();
				strCheck = strCheck.substring(i);
			}
		}
		return result;
	}
	
	/**
	 * 문자열이 길경우 적당한 길이에"..."를 붙여 줄인다. null 일 경우 ""반환
	 * @param strString
	 * @return 변환된 문자열
	 */
	public static String getStrCut(String strValue, int nlimit) {
		if (strValue == null || nlimit < 4)
			return strValue;
		int len = strValue.length();
		int cnt = 0;
		int index;
		for (index = 0; index < len && cnt < nlimit;)
			if (strValue.charAt(index++) < '\u0100')
				cnt++;
			else
				cnt += 2;

		if (index < len)
			strValue = strValue.substring(0, index) + "...";
		return strValue;
	}	

	/**
	 * 구분자로 구분되어있는 문자열을 구분자를 제거하고 리턴한다. null 일 경우 ""반환
	 * <p>	 * 
	 * @param str 변환될 문자열
	 * @param separator 구분자
	 * @return 변환된 문자열
	 */
	public static String getRemoveSplit(String str, String seperator) {
		String result = null;
		try {
			if (str != null) {
				result = "";
				String[] arrStr = str.trim().split(seperator);
				for (int i = 0; i < arrStr.length; i++) {
					result = result + arrStr[i];
				}
			}
		} catch (Exception e) {
			
		}

		return result;
	}	

	/**
	 * 구분자로 구분되어있는 문자열을 배열로 변환한다. null 일 경우 ""반환
	 * @param str	변환될 문자열
	 * @param separator		구분자
	 * @return 변환된 문자열 배열
	 */
	public static String[] getSplit(String str, String separator) {
		String[] TokStr = null;
		StringTokenizer st;
		int i = 0;

		try {
			if (!str.equals("") || str != null) {
				st = new StringTokenizer(str, separator);
				TokStr = new String[st.countTokens()];
				while (st.hasMoreTokens()) {
					TokStr[i] = st.nextToken();
					i++;
				}
			}
		} catch (Exception e) {
		}
		return TokStr;
	}	
	
	/**
	 * 입력받은 문자열을 first to end 까지 자른다
	 * @param str	변환될 문자열
	 * @param first, end 범위
	 * @return 변환된 문자열
	 */
	public static String subString(String target, int first, int end) { // (target,	0부터, end -1)
		try {
			target = target.substring(first, end);
		} catch (Exception e) {
			target = "";
		}
		return target;
	}

	/**	
	 * 123456-7890123라는 문자열 str을 1234567890123
	 * 형식으로 바꾸고 싶다면, replaceStr( str, "-", "") 로 호출한다.
	 */
	public static String replaceStr(String source, String keyStr, String toStr) {
		if (source == null)
			return null;
		int startIndex = 0;
		int curIndex = 0;
		StringBuffer result = new StringBuffer();

		while ((curIndex = source.indexOf(keyStr, startIndex)) >= 0) {
			result.append(source.substring(startIndex, curIndex)).append(toStr);
			startIndex = curIndex + keyStr.length();
		}

		if (startIndex <= source.length())
			result.append(source.substring(startIndex, source.length()));

		return result.toString();
	}
	
	/**
	 * 금액형식에 콤마(,) 추가 	 
	 * @param int st 금액(숫자)
	 * @return String 콤마(,)가 추가된 String, "" 일경우 "0" 리턴
	 */
	public static String addComma(int st) {
		String sj = Integer.toString(st);
		String str = Integer.toString(st).trim();
		if (str.equals(""))
			return "";
		if (sj.startsWith("0")) {
			for (int i = 0; i < str.length(); i++) {
				sj = str.substring(i);
				if (!sj.startsWith("0"))
					break;
			}
		}
		if (sj.equals("0"))
			return "0";
		double nu = Double.parseDouble(sj);
		NumberFormat nf = NumberFormat.getInstance();
		String no = nf.format(nu);
		if (no.equals("0"))
			no = "0";
		return no;
	}
	
	/**
	 * 금액형식에 콤마(,) 추가
	 * @param String  str 금액(문자)
	 * @return String 콤마(,)가 추가된 String, "" 일경우 "0" 리턴
	 */
	public static String addComma(String str) {
		String sj = str;
		if (str.trim().equals(""))
			return "";
		if (str.indexOf(",") != -1)
			return str;
		if (sj.startsWith("0")) {
			for (int i = 0; i < str.length(); i++) {
				sj = str.substring(i);
				if (!sj.startsWith("0"))
					break;
			}
		}
		if (str.equals("0"))
			return "0";
		else {
			double nu = Double.parseDouble(sj);
			NumberFormat nf = NumberFormat.getInstance();
			String no = nf.format(nu);
			return no;
		}
	}

	/**
	 * 금액 형식의 string에 포함되어 있는 콤마를 제거해 가져온다.
	 * @param str 금액 형식의 string.
	 * @return 콤마를 제거한 string.
	 */
	public static String removeComma(String str) {
		if (str == null)
			return null;

		if (str.indexOf(",") != -1) {
			StringBuffer buf = new StringBuffer();
			for (int i = 0; i < str.length(); i++) {
				char c = str.charAt(i);

				if (c != ',')
					buf.append(c);
			}
			return buf.toString();
		}

		return str;
	}	
	
	/**
	 * 실수를 를 받아서 소수점 n자리까지 자를는 함수 
	 * @param double, n, n.0
	 * @return 변환된 숫자(double)
	 */
	public static Double getDoubleReplace(Double d, int n1, double n2) {
		if (d==0)			
			return d;
		else
			return (int)(d*n1)/n2;
	}	
	
	/**
	 * 문자열에서 입력받은 문자를 끼우넣는다.
	 * @return the translated string.
	 * @param source	String 변환할 문자열
	 * @param keyStr
	 *            String 치환 대상 문자열 예를 들어, 123456 라는 문자열 str에 , 를 넣고
	 *            싶으며(1,2,3,4,....) 형식으로 바꾸고 싶다면, replaceStr( str, ",") 로 호출한다.
	 */
	public static String insertStr(String source, String keyStr) {
		String result = "";
		if (source == null)
			return null;

		for (int i = 0; i < source.length(); i++) {
			if (i != source.length() - 1) {
				result += source.substring(i, i + 1) + keyStr;
			} else {
				result += source.substring(i, i + 1);
			}
		}
		return result.toString();
	}	
	
	/**
	 * "0000000" 문자열에서 1을 요일로 변경
	 * @param String 변환할 문자열
	 * @return 월화수목금 String	  
	 */
	public static String getWeekOfDay(String str) {
		String chk1 = "1".equals(str.substring(0,1))?"월":"";
		String chk2 = "1".equals(str.substring(1,2))?"화":"";
		String chk3 = "1".equals(str.substring(2,3))?"수":"";
		String chk4 = "1".equals(str.substring(3,4))?"목":"";
		String chk5 = "1".equals(str.substring(4,5))?"금":"";
		String chk6 = "1".equals(str.substring(5,6))?"토":"";
		String chk7 = "1".equals(str.substring(6))?"일":"";	
		String dayStr = chk1+chk2+chk3+chk4+chk5+chk6+chk7;
		return dayStr;
	}

	
	/**
	 * 구분자로 구분되어있는 문자열을 배열로 변환한다. null 일 경우 ""반환
	 * @param str	변환될 문자열
	 * @param separator		구분자
	 * @return 변환된 문자열 배열
	 */
	
	/**
	 * 입력받은 글자 앞에 &nbsp; 입력받은 숫자만큼 추가
	 * @return String 
	 */
	public static String addSpace(String str, int st) {
		if(st!=0) str = "- "+str;
		for(int i=0; i<st; i++) {
			str = "\u00a0\u00a0\u00a0"+str;
		}
		return str;
	}
	
	
	
	

	// *************** 날짜 관련 함수 *************** //	
		
	/**
	 * 지정한 일자를 지정한 패턴으로 반환
	 * @param day 		0:현재일자, -6:일주일전일자
	 * @param pattern 	yyyy-MM-dd HH:mm:ss
	 * @return 일자 문자열
	 */
	public static String getSysDate(int day, String pattern) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, day);
		Date date = calendar.getTime();
		SimpleDateFormat formatter = new SimpleDateFormat(
				pattern, Locale.KOREA);
		String dateString = formatter.format(date);		
		return dateString;
	}
	
	/**
	 * 지정한 일자를  반환
	 * @param day 		0:현재일자, -6:일주일전일자
	 * @return Date
	 */
	public static Date getSysDate(int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, day);
		Date date = calendar.getTime();		
		return date;
	}
	
	/**
	 * 입력받은 날짜를 입력받은 패턴의 문자로 반환
	 * @param dt	입력받은 날짜
	 * @param pattern 패턴
	 * @return 변환된 날짜 문자열
	 */
	public static String getDateToString(Date dt, String pattern) {
		SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.KOREA);
		String dateString = formatter.format(dt);
		return dateString;
	}
	
	/**
	 * 문자열을 DataTime 로 변환
	 * @param 	str	문자열 	2015-02-09 09:10:10 
	 * @param 	pattern 	
	 * @return 		Date	Mon Feb 09 09:10:10 KST 2015
	 */
	public static Date getStringToDate(String str, String pattern) {
		Date date = new Date();
		try{
			SimpleDateFormat format = new SimpleDateFormat(pattern);
			date = format.parse(str); 			
		} catch(ParseException ex){
			 ex.printStackTrace();  
		}
		return date;	
	}	

	/**
	 * 문자열을 DataTime 로 변환
	 * @param 	str	문자열 	2015-02-09 09:10:10
	 * @return 		Date	Mon Feb 09 09:10:10 KST 2015
	 */
	public static Date getDateTime(String str) {
		Date date = new Date();
		try{
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			date = format.parse(str); 			
		} catch(ParseException ex){
			 ex.printStackTrace();  
		}
		return date;
		
	}
	
	/**
	 * 날짜 문자열을 구분자를 추가하여 반환한다.
	 * @param String  날짜, 구분자
	 * @return 변환된 문자열
	 */
	public static String getDaySlash(String str, String del) {
		if (str.equals("00000000"))
			return "";
		if (str.trim().equals(""))
			return "";
		if (str.indexOf(del) != -1)
			return str;
		String sm;
		if (str.length() == 8)
			sm = str.substring(0, 4) + del + str.substring(4, 6) + del
					+ str.substring(6);
		else if (str.length() == 6)
			sm = str.substring(0, 2) + del + str.substring(2, 4) + del
					+ str.substring(4);
		else
			return "";
		return sm;
	}

	/**
	 * 날짜 형식이 맞고, 존재하는 날짜일 때 2개 일자 사이의 나이(차이) 리턴 형식이 잘못 되었거나 존재하지 않는 날짜:
	 * ParseException 발생
	 */
	public static int ageBetween(String from, String to)
			throws ParseException {
		return ageBetween(from, to, "yyyyMMdd");
	}

	/**
	 * 날짜 형식이 맞고, 존재하는 날짜일 때 2개 일자 사이의 나이 리턴 형식이 잘못 되었거나 존재하지 않는 날짜:
	 * ParseException 발생
	 */
	public static int ageBetween(String from, String to, String format)	throws ParseException {
		//return ageBetween(from, to, "yyyy-MM-dd");
		return (int) (daysBetween(from, to, format) / 365);
	}

	public static int daysBetween(String from, String to, String format) throws ParseException {		
		Date d1 = check(from, format);
		Date d2 = check(to, format);
		long duration = d2.getTime() - d1.getTime();
		return (int) (duration / (1000 * 60 * 60 * 24));
	}
	
	public static int daysBetween(Date d1, Date d2) throws ParseException {	
		long duration = d2.getTime() - d1.getTime();
		return (int) (duration / (1000 * 60 * 60 * 24));
	}
	
	public static int daysBetween(String from, String to) throws ParseException {	
		Date d1 = getDateTime(from);
		Date d2 = getDateTime(to);		
		long duration = d2.getTime() - d1.getTime();
		return (int) (duration / (1000 * 60 * 60 * 24));
	}
	
	/**
	 * check date string validation with an user defined format.
	 * @param s	           date string you want to check.
	 * @param format	string representation of the date format. For example,	  "yyyy-MM-dd".
	 * @return Date
	 */
	public static Date check(String s, String format) throws ParseException {
		if (s == null)
			throw new ParseException("date string to check is null", 0);
		if (format == null)
			throw new ParseException("format string to check date is null", 0);

		SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.KOREA);
		Date date = null;
		try {
			date = formatter.parse(s);
		} catch (ParseException e) {
			throw new ParseException(" wrong date:\"" + s	+ "\" with format \"" + format + "\"", 0);
		}

		if (!formatter.format(date).equals(s))
			throw new ParseException("Out of bound date:\"" + s+ "\" with format \"" + format + "\"", 0);
		return date;
	}
	
	
	/**
	 * 시간 DateFormat 변환
	 * @param  boolean(true:yyyy-MM-dd-HH-mm-ss-SSS, false:yyyyMMddHHmmssSSS)
	 * @return 시간문자열
	 */
	public static String getDateTime(boolean boolHyphen){
		SimpleDateFormat simpleDateFormat;
		if(boolHyphen==true) simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");
		else simpleDateFormat=new SimpleDateFormat("yyyyMMddHHmmssSSS");
		Date date=new Date();
		return simpleDateFormat.format(date);
	}
	
	
	/**
	 * 입력받은 Date에 입력받은 기간을 추가하여 반환
	 * @param 	date 	입력받은 날짜
	 * @param 	a  		추가할 기간	
	 * @param 	str		대상(year, month, date, hour, minute)	
	 * @return 	반환할 문자열	
	 */
	public static String getAddDate(Date date, int a, String str)  {
		SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S"); 		
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		if(str.equals("year")) cal.add(Calendar.YEAR, a);
		else if(str.equals("month")) cal.add(Calendar.MONTH, a);
		else if(str.equals("date")) cal.add(Calendar.DATE, a); 
		else if(str.equals("hour")) cal.add(Calendar.HOUR, a);
		else if(str.equals("minute")) cal.add(Calendar.MINUTE, a);
		
		String dateStr = sdformat.format(cal.getTime());
		
		return dateStr;		
	}
	
	
	/**
	 * 입력받은 두개의 Date 중 어느 것이 큰지 비교
	 * @param 	a, b 	
	 * @return 	a가 크면 true, 작으면 false 리턴	
	 */
	public static boolean getCompareDate(Date a, Date b) throws Exception {
		boolean bool = false;
		if(a.compareTo(b)>0){
			bool=false;	//a < b
		}else if(a.compareTo(b)<0){
			bool=true;	//a > b
		}else{
			bool=true;
		}		
		return bool;		
	}

	/**
	 * 입력받은 두개의 String 가 같은지 비교
	 * @param 	a, b 	
	 * @return 	같으며 true, 다르면 false 리턴	
	 */
	public static boolean getCompareString(String a, String b){
		boolean bool = false;
		if(a.equals(b)){
			bool=true;	//a == b
		}	
		return bool;		
	}
	
	/**
	 * 날짜를 폼형식에 맞게 수정한다. ex) 19991212 --> 1999년 12월 12일 null 일 경우 "" 반환
	 * @param str
	 * @return
	 */
	public static String getDayStr(String str, int num) {
		if (str.equals("00000000"))
			return "";
		if (str.trim().equals(""))
			return "";
		String sm;
		if (str.length() == 8)
			sm = str.substring(0, 4) + "년 " + str.substring(4, 6) + "월 "
					+ str.substring(6) + "일";
		else if (str.length() == 6)
			sm = str.substring(0, 2) + "년 " + str.substring(2, 4) + "월 "
					+ str.substring(4) + "일";
		else
			return "";
		return sm;
	}

	/**
	 * 날짜에서 일을 더하고 뺌
	 * @param String    s 날짜(TYPE = yyyymmdd) int addDay addDay 증감값
	 * @return String 변환날짜문자열
	 */
	public static String getAddDay(String s, int addDay) {
		String retStr = "";
		try {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, Integer.parseInt(s.substring(0, 4)));
			cal.set(Calendar.MONTH, Integer.parseInt(s.substring(4, 6)) - 1);
			cal.set(Calendar.DATE, Integer.parseInt(s.substring(6, 8)));
			cal.add(Calendar.DATE, addDay);

			Date sDate = cal.getTime();
			SimpleDateFormat simpleDate = new SimpleDateFormat("yyyyMMdd");
			retStr = simpleDate.format(sDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return retStr;
	}

	/**
	 * 해당월의 마지막날을 구함
	 * @param int year 년도(TYPE = yyyy) int month 월(TYPE = mm)
	 * @return String 구해진 날짜 문자열
	 */ 
	public static int lastDay(int year, int month) throws ParseException {
		int day = 0;
		switch (month) {
		case 1: /* '\001' */
		case 3: /* '\003' */
		case 5: /* '\005' */
		case 7: /* '\007' */
		case 8: /* '\b' */
		case 10: /* '\n' */
		case 12: /* '\f' */
			day = 31;
			break;
		case 2: /* '\002' */
			if (year % 4 == 0) {
				if (year % 100 == 0 && year % 400 != 0)
					day = 28;
				else
					day = 29;
			} else {
				day = 28;
			}
			break;
		case 4: /* '\004' */
		case 6: /* '\006' */
		case 9: /* '\t' */
		case 11: /* '\013' */
		default:
			day = 30;
			break;
		}
		return day;
	}
	
	
	/**
	 * 시간차이를 구함
	 * @param HH:mm, HH:mm
	 * @return String 구해진시간문자열(HH:mm)
	 */
	public static String getDiffTime(String starttime, String startmin,
			String endtime, String endmin) {
		int s_time = Integer.parseInt(starttime);
		int s_min = Integer.parseInt(startmin);
		int e_time = Integer.parseInt(endtime);
		int e_min = Integer.parseInt(endmin);
		int h = 0;
		int m = 0;
		if (e_min < s_min) {
			m = e_min - s_min + 60;
			h = e_time - s_time - 1;
		} else {
			m = e_min - s_min;
			h = e_time - s_time;
		}

		String hour = new Integer(h).toString();
		String min = new Integer(m).toString();
		if (h < 10) {
			hour = "0" + new Integer(h).toString();
		}
		if (m < 10) {
			min = "0" + new Integer(m).toString();
		}
		String str = hour + ":" + min;
		return str;
	}
	
	/*
	 * 특정 날짜에 대하여 요일을 구함(일~토)
	 * @param date : 2015-02-09
	 * @param pattern : yyyy-MM-dd
	 * @return
	 * @throw Exception
	 */
	public static int getDateDay(String date, String pattern) throws Exception{
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.KOREA);
		Date nDate = dateFormat.parse(date);
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(nDate);
		
		int dayNum = cal.get(Calendar.DAY_OF_WEEK);
		
		return dayNum;
	}
	
	
	
	
	
	
	
	// *************** HTML 처리 관련 함수 *************** //
	
	/**
	 * 문자열에서 HTML테그 삭제
	 * @param String  문자열
	 * @return 변환된 문자열
	 */
	public static String removeTag(String str) throws Exception {
		EditorKit kit = new HTMLEditorKit();
		Document doc = kit.createDefaultDocument();
		Reader reader = new StringReader(str);
		kit.read(reader, doc, 0);
		return doc.getText(0, doc.getLength());
	}
	
	
	/**
	 * 문자열에서 개행문자대신 <br> 붙이기	
	 * @param String  문자열
	 * @return 변환된 문자열
	 */
	public static String appendHtmlBr(String comment) {
		int length = comment.length();
		StringBuffer buffer = new StringBuffer();

		for (int i = 0; i < length; ++i) {
			String comp = comment.substring(i, i + 1);
			if ("\r".compareTo(comp) == 0) {
				comp = comment.substring(++i, i + 1);

				if ("\n".compareTo(comp) == 0)
					buffer.append("<BR>\r");
				else
					buffer.append("\r");
			}

			buffer.append(comp);
		}
		return buffer.toString();
	}
	
	/**
	 * 문자열에서 개행문자대신 <br> 붙이기	
	 * @param String  문자열
	 * @return 변환된 문자열
	 */
	public static String replaceBr(String comment) {
		return comment.replaceAll("(\r\n|\n)", "<br/>");
	}

	
	public static String replaceBr(String mainString, String oldString,
			String newString) {
		if (mainString == null) {
			return null;
		}
		if (oldString == null || oldString.length() == 0) {
			return mainString;
		}
		if (newString == null) {
			newString = "";
		}

		int i = mainString.lastIndexOf(oldString);
		if (i < 0)
			return mainString;

		StringBuffer mainSb = new StringBuffer(mainString);

		while (i >= 0) {
			mainSb.replace(i, (i + oldString.length()), newString);
			i = mainString.lastIndexOf(oldString, i - 1);
		}
		return mainSb.toString();
	}

	
	/**
	 * 자료 등록시 스페이스 처리를 &nbsp; 태그로 변환한다.
	 * @param String  문자열
	 * @return 변환된 문자열
	 */
	public static String replaceSpace(String strOld, String strTok,	String strNewTok) {
		String strNew = "";

		for (int i = 0, j = 0; (j = strOld.indexOf(strTok, i)) > -1; i = j + 2) {
			strNew += strOld.substring(i, j) + strNewTok;
		}
		if (strOld.indexOf(strTok) > -1) {
			strNew += strOld.substring(
					strOld.lastIndexOf(strTok) + strTok.length(),
					strOld.length());
		} else
			strNew = strOld;
		return strNew;
	}

	
	/**
	 * 내용보기에서 <BR> 태그와 Space 처리
	 * @param String  문자열
	 * @return 변환된 문자열
	 */
	public static String convStr(String strOld) {
		String strNew = "";

		strNew = replaceSpace(replaceStr(strOld, "\n", "<BR>"), "  ",
				"&nbsp;&nbsp;");
		return strNew;
	}

	
	/**
	 * TextArea에서 입력받은 캐리지 리턴값을 <BR>태그로 변환
	 * @param String  문자열
	 * @return 변환된 문자열
	 */
	public static String nl2br(String comment) {
		int length = comment.length();
		StringBuffer buffer = new StringBuffer();

		for (int i = 0; i < length; ++i) {
			String comp = comment.substring(i, i + 1);
			if ("\r".compareTo(comp) == 0) {
				comp = comment.substring(++i, i + 1);
				if ("\n".compareTo(comp) == 0)
					buffer.append("<BR>\r");
				else
					buffer.append("\r");
			}
			buffer.append(comp);
		}
		return buffer.toString();
	}
	
	/**
	 * 파일의 MimeType 을 반환한다.	
	 * @param String  파일문자열
	 * @return MimeType 문자열
	 */
	public static String getMimeType(String filename) {
		String filePre = "";
		String fileExt = "";
		String type = "";
		int index = 0;
		try {

			index = filename.lastIndexOf(".");

			if (index != -1) {
				filePre = filename.substring(0, index);
				fileExt = filename.substring(index + 1);
			} else {
				filePre = filename;
				fileExt = "";
			}

			System.out.println("getMimeType filename :" + filename);
			System.out.println("getMimeType filePre :" + filePre);
			System.out.println("getMimeType fileExt :" + fileExt);
			fileExt = fileExt.toLowerCase();

			System.out.println("getMimeType fileExt.toLowerCase() :" + fileExt);
			if ("gif".equals(fileExt)) {
				type = "image/gif";

			} else if ("jpeg".equals(fileExt) || "jpg".equals(fileExt)
					|| "jpe".equals(fileExt)) {
				type = "image/jpeg";

			} else if ("png".equals(fileExt)) {
				type = "image/png";

			} else if ("bmp".equals(fileExt)) {
				type = "image/bmp";

			} else if ("mpeg".equals(fileExt) || "mpg".equals(fileExt)
					|| "mpe".equals(fileExt)) {
				type = "video/mpeg";

			} else if ("mpv2".equals(fileExt) || "mp2v".equals(fileExt)) {
				type = "video/x-mpeg2";

			} else if ("avi".equals(fileExt)) {
				type = "video/msvideo";

			} else if ("qt".equals(fileExt) || "mov".equals(fileExt)) {
				type = "video/quicktime";

			} else if ("viv".equals(fileExt) || "vivo".equals(fileExt)) {
				type = "video/vivo";

			} else if ("wmv".equals(fileExt)) {
				type = "video/wmv";

			} else if ("movie".equals(fileExt)) {
				type = "video/x-sgi-movie";
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return type;
	}
	

	/**
	 * TextArea에서 입력받은 따옴표 앞에 \ 추가
	 * @param String  파일문자열
	 * @return 변환된 문자열
	 */
	public String addslash(String comment) {
		if (comment == null) {
			return "";
		}
		int length = comment.length();
		StringBuffer buffer = new StringBuffer();

		for (int i = 0; i < length; ++i) {
			String comp = comment.substring(i, i + 1);
			if ("'".compareTo(comp) == 0) {
				buffer.append("\'");
			}
			buffer.append(comp);
		}
		return buffer.toString();
	}

	/**
	 * TextArea에서 입력받은 < or > 를 특수문자로 변환
	 * @param String  파일문자열
	 * @return 변환된 문자열
	 */
	public static String html2spec(String comment) {
		if (comment == null) {
			return "";
		}
		int length = comment.length();
		StringBuffer buffer = new StringBuffer();

		for (int i = 0; i < length; ++i) {
			String comp = comment.substring(i, i + 1);
			if ("<".compareTo(comp) == 0)
				buffer.append("&lt;");
			else if (">".compareTo(comp) == 0)
				buffer.append("&gt;");
			else
				buffer.append(comp);
		}
		return buffer.toString();
	}

	/**
	 * 입력받은 문자열의 공백을 &nbsp; 로 치환한다.
	 * @param String  파일문자열
	 * @return 변환된 문자열
	 */
	public String spaceadd(String addstr) {
		if (addstr == null) {
			return "";
		}
		int length = addstr.length();
		StringBuffer buffer = new StringBuffer();

		for (int k = 0; k < length; ++k) {
			String comp = addstr.substring(k, k + 1);
			if (" ".compareTo(comp) == 0) {
				buffer.append("&nbsp;");
			}
			buffer.append(comp);
		}
		return buffer.toString();
	}
	
	
	/**
	 * 입력받은 문자열의 <BR>태그를 공백으로 치환한다.
	 * @param String  파일문자열
	 * @return <BR> 이 제거된 문자열
	 */
	public static String removeHTML(String str) {
		String oldStr = "<BR>";
		String newStr = "";
		StringBuffer newString = new StringBuffer();
		int oldStrLen = oldStr.length();
		while (true) {
			int index = str.indexOf(oldStr);
			if (index != -1) {
				newString.append(str.substring(0, index) + newStr);
				str = str.substring(index + oldStrLen);
			} else {
				newString.append(str);
				break;
			}
		}
		return newString.toString();
	}
	
	
	/**
	 * 입력받은 문자열의 개행문자를 <P> <BR> 태그로 변환한다.
	 * @param String Html 문자열 \n\n --> <P>, \n --> <BR>
	 * @return 변환된 문자열
	 */
	public static String format(String inStr) {

		String outStr = inStr.toString();
		int i = -1;
		while ((i = outStr.indexOf("\n\n")) >= 0)
			outStr = outStr.substring(0, i) + "<P>" + outStr.substring(i + 1);
		i = -1;
		while ((i = outStr.indexOf("\n")) >= 0)
			outStr = outStr.substring(0, i) + "<BR>" + outStr.substring(i + 1);
		return outStr;
	}

	
	/**
	 * 입력받은 첨부파일명의 확장자를 반환한다.
	 * @param String  파일명
	 * @return String 확장자
	 */
	//첨부파일의 확장자명만 반환
	public static String getFiletype(String filename){
		return filename.substring(filename.lastIndexOf(".")+1,filename.length());
	}
	
	/**
	 * 입력받은 첨부파일명의 확장자명을 제거한 파일명만 반환한다.
	 * @param String  파일명
	 * @return String 확장자명를 제거한 파일명
	 */
	public static String getFilename(String filename){
		return filename.substring(0,filename.lastIndexOf("."));
	}
	
	
	
	
	
	
	// *************** Stream 처리 관련 함수 *************** //
	/**
	 * 입력받은 경로의 파일을 읽어 문자로 반환
	 * @param String  파일패스
	 * @return 읽어드린 문자열
	 */
	public static String readFile(String path_file) {
		//System.out.println("path_file : "+path_file);
		File myFile = new File(path_file);
		byte buf[] = new byte[(int) myFile.length()];
		FileInputStream i = null;

		try {
			i = new FileInputStream(myFile);
			i.read(buf);
			i.close();
		} catch (IOException e) {
			buf = ("Problems reading file :" + e.getMessage()).getBytes();
			e.printStackTrace();
		}

		return new String(buf);
	}

	
	/**
	 * 입력받은 경로, 파일을 읽어 문자로 반환
	 * @param String  파일패스, String 파일명
	 * @return 읽어드린 문자열
	 */
	public static String readFile(String path, String fileName) {
		String path_file = "";

		if (path.endsWith(System.getProperty("file.separator")))
			path_file = path + fileName;
		else
			path_file = path + System.getProperty("file.separator") + fileName;
		StringBuffer ta = new StringBuffer();

		try {
			FileReader fr = new FileReader(path_file);
			BufferedReader br = new BufferedReader(fr);
			String line;

			while ((line = br.readLine()) != null) {
				ta.append(line + "\n");
			}
			
			br.close();
		} catch (IOException e) {
			ta.append("Problems reading file" + e.getMessage());
		}

		return String.valueOf(ta).toString();
	}

	
	/**
	 * 입력받은 InputStream 을 읽어서 문자열로 반환한다.
	 * @param InputStream
	 * @return 읽어드린 문자열
	 */
	public static String readInputStream(InputStream in) throws IOException {
		BufferedReader rd = new BufferedReader(
				new InputStreamReader(in));
	
		StringBuffer sb = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			sb.append(line);
		}
			
		return sb.toString();
	}
		
	
	
	
	
	// *************** Character Set 변환 관련 함수 *************** //
	/**
	 * KSC5601 to 8859_1
	 */
	public static String toIso(String str) {
		if (str == null)
			return null;
		try {
			return new String(str.getBytes("KSC5601"), "8859_1");
		} catch (Exception e) {
			return str;
		}
	}

	/**
	 * 8859_1 to KSC5601
	 */
	public static String toKsc(String str) {
		if (str == null)
			return null;
		try {
			return new String(str.getBytes("8859_1"), "KSC5601");
		} catch (Exception e) {
			return str;
		}
	}
	
	/**
	 * 8859_1 to euc-kr
	 */
	public static String toEuc(String str) {
		if (str == null)
			return null;
		try {
			return new String(str.getBytes("8859_1"), "euc-kr");
		} catch (Exception e) {
			return str;
		}
	}
	
	/**
	 * 8859_1 to utf-8
	 */
	public static String toUtf(String str) {
		if (str == null)
			return null;
		try {
			return new String(str.getBytes("8859_1"), "utf-8");
		} catch (Exception e) {
			return str;
		}
	}

	
	

	// *************** 기타 함수 *************** //
	/**
	 * 입력받은 값에 따라   byte, Kbyte, Mbyte 를 붙힌 문자를 반환한다.
	 * @param size - long type 숫자
	 * @return 문자열
	 */
	public static String getChangeByte(long size) {
		if (size == 0)
			return "0 B";
		if (size < 1024) {
			return size + " B";
		} else if (size >= 1024 && size < 1024*1024) {
			size = size / 1024;
			return size + " KB";
		} else {
			size = size / (1024*1024);
			return size + " MB";
		}
	}
	
	
	/**
	 * 입력받은 값에 따라   byte, Kbyte, Mbyte 를 붙힌 문자를 반환한다.
	 * @param size - double type 숫자
	 * @return 문자열
	 */
	public static String getChangeByte(double size) {
		if (size == 0)
			return "0 B";
		if (size < 1024) {
			size = Math.round(size*100d) / 100d;
			return size + " B";
		} else if (size >= 1024 && size < 1024*1024) {
			size = size / 1024;			
			size = Math.round(size*100d) / 100d;
			return size + " KB";
		} else if (size >= 1024*1024 && size < 1024*1024*1024){
			size = size / (1024*1024);
			size = Math.round(size*100d) / 100d;
			return size + " MB";
		} else {
			size = size / (1024*1024*1024);
			size = Math.round(size*100d) / 100d;
			return size + " GB";
		}
	}
	
	
	
	
	/**
	 * CPU 용량 - Hz, KHz, MHz 를 붙힌 문자를 반환한다.
	 * @param size - double type 숫자
	 * @return 문자열
	 */
	public static String getChangeHz(double size, String form) {
		DecimalFormat df = new DecimalFormat(form);
		if(size == 0){
			return "0 Hz";		
		}else if(size < 1000) {			
			return df.format(size)+" Hz";
			
		}else if(size >= 1000 && size < 1000000) {
			size = size/1000;
			return df.format(size)+" KHz";
			
		}else if(size >= 1000000 && size < 1000000000){
			size = size/1000000;
			return df.format(size)+" MHz";
			
		}else{
			size = size/1000000000;
			return df.format(size)+" GHz";
		}
	}
	
	
	/**
	 * Memory, Disk 용량 - Byte, KByte, MByte, GByte 를 붙힌 문자를 반환한다.
	 * @param size - double type 숫자
	 * @return 문자열
	 */
	public static String getChangeByte(double size, String form) {
		DecimalFormat df = new DecimalFormat(form);
		if(size == 0){
			return "0 B";		
		}else if(size < 1024) {			 
			return df.format(size)+" B";
			
		}else if(size >= 1024 && size < 1024*1024) {
			size = size/1024;
			return df.format(size)+" KB";
			
		}else if(size >= 1024*1024 && size < 1024*1024*1024){
			size = size/(1024*1024);
			return df.format(size)+" MB";
			
		}else{ 
			size = size/1024;
			if(size >= 1024*1024 && size < 1024*1024*1024){
				size = size/(1024*1024);
				return df.format(size)+" GB";
			}else{
				size = size/(1024*1024*1024);
				return df.format(size)+" TB";
			}
		}
	}
	
	/**
	 * % 붙힌 문자를 반환한다.
	 * @param size - double type 숫자
	 * @return 문자열
	 */
	public static String getChangePercent(double total, double usage) {		
		double size = usage/total;		
		return Math.round(size*100d)+"";
	}

	
	
	

	
	/**
	 * CPU, Memory, Disk - B, KB, MB, GB 를 붙힌 문자를 반환한다.
	 * @param size - float type
	 * @return 문자열
	 */
	public static String getChangeUnit(float size, int unit, int core, String form, String strUnit) {
		if (size == 0)
			return "0 "+strUnit;
		if (size < 1000) {
			return String.format(form, size*core)+" "+strUnit;
			
		} else if (size > unit && size < unit*unit) {
			return String.format(form, size*core/1000)+" K"+strUnit;
			
		} else if (size > unit*unit && size < unit*unit*unit){
			return String.format(form, size*core/unit*unit)+" M"+strUnit;
			
		} else {
			size = size / (unit*unit*unit);
			return String.format(form, size*core)+" G"+strUnit;
		}
	}
	
	
	
	
	/**
	 * 주민번호로 나이를 구한다.
	 * @return the translated string.
	 * @param res_no String 변환할 문자열
	 * @param age  String 치환 대상 문자열 예를 들어, 123456-1234567을 나이로 바꾸고 싶다면,
	 * 
	 */
	public static String getAge(String res_no) {
		String result = "";

		if (res_no == null || res_no.equals(""))
			return "";

		String res_no1 = res_no.substring(0, 6);
		String res_no2 = res_no.substring(7, 14);

		String today = ""; // 시스템 날짜
		String birthday = ""; // 생일
		int myAge = 0; // 만 나이

		today = CommonUtil.getSysDate(0,"yyyy"); // 시스템 날짜를 가져와서 yyyy 형태로 변환

		if (res_no2.charAt(0) == '1' || res_no2.charAt(0) == '2') {
			birthday = "19" + res_no1.substring(0, 2); // 주민번호 7번째 자리수가 1 또는 2이면
														// 1900년대 출생
		} else {
			birthday = "20" + res_no1.substring(0, 2); // 주민번호 7번째 자리수가 1 또는 2가
														// 아니면 2000년대 출생
		}
		myAge = Integer.parseInt(today) - Integer.parseInt(birthday) + 1; // 현재년도 -생년+1
		result = "" + myAge;

		return result;

	}

	/**
	 * 휴대폰 번호를 000 0000 0000 배열로 반환한다.
	 * @return string 배열.
	 */
	public static String[] hpArray(String source) {
		String[] phones = new String[4];
		phones[0] = source.substring(0, 3);
		if (source.length() == 10) {
			phones[1] = source.substring(3, 6);
			phones[2] = source.substring(6, source.length());
		} else {
			phones[1] = source.substring(3, 7);
			phones[2] = source.substring(7, source.length());
		}
		return phones;

	}
	
	/**
	 * 입력받은 문자의 적당한 Y축 값을 반환
	 * @param str	변환될 문자열
	 * @return Y축 최대값
	 */
	public static String getMaxValue(String str, int limit) {
		if(str.length()<10){
			int max_value= Integer.parseInt(str)*2;
			if(max_value<limit){ 
				str = limit+"";
			}else{
				String tmp = String.valueOf(max_value);
				str = tmp.substring(0,1);
				for(int i=1;i<tmp.length();i++){
					str += "0";				
				}	
			}
		}
		
		return str;
	}	

	/**
	 * 메소드 수행 시간 체크
	 * @return 현재시간
	 */
	public static long getMethodSpeed() {
		long start = System.currentTimeMillis();
		return start;
	}
	
	/**
	 * VirtualMachine 호환성 반환
	 * @return 호환성
	 */
	public static String getCompatibility(String version){
		String compatibility = version;		
		if(version.equalsIgnoreCase("vmx-04")){
			compatibility = "ESX/ESXi 3.5 이상(VM 버전 4)";			
		}else if(version.equalsIgnoreCase("vmx-07")){
			compatibility = "ESX/ESXi 3.x 이상(VM 버전 7)";			
		}else if(version.equalsIgnoreCase("vmx-08")){
			compatibility = "ESXi 5.0 이상(VM 버전 8)";			
		}else if(version.equalsIgnoreCase("vmx-09")){
			compatibility = "ESXi 5.1 이상(VM 버전 9)";			
		}else if(version.equalsIgnoreCase("vmx-10")){
			compatibility = "ESXi 5.5 이상(VM 버전 10)";			
		}else if(version.equalsIgnoreCase("vmx-11")){
			compatibility = "ESXi 6.0 이상(VM 버전 11)";
		}
		
		return compatibility;	
	}
	
	
	/**
	 * SSL 인증서 패스
	 */
	public static void setPassCert() throws Exception {  
		TrustManager[] trustAllCerts = new TrustManager[] { 
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        X509Certificate[] myTrustedAnchors = new X509Certificate[0];  
                        return myTrustedAnchors;
                    }        						
                    @Override
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {}
        		  
                    @Override
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                }
        };
			
        SSLContext sc = SSLContext.getInstance("TLSv1.2");
        sc.init(null, trustAllCerts, new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String arg0, SSLSession arg1) {
                return true;
            }
        });
	}
}
