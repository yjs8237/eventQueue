package com.isi.utils;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.File;
import java.util.*;


public class CheckFunc {

	public static boolean deleteDir(String dirpath) {
		File dir = new File(dirpath);
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i=0; i<children.length; i++) {

				File subdir = new File(dir, children[i]);
				subdir.delete();
			}
		}
		return dir.delete();
	}


	public static String FormatIPAddr(String ip, int port, int mng) {
		String strTemp = "";
		try {
			String [] Tokens = TokenizerString(ip, ".");
			if( Tokens.length != 4) {
				strTemp = ip + ":" + port + "@" + mng;
			} else {
				int i = 0;
				for( i = 0; i < 3; i++) {
					strTemp = strTemp + FixLenString(Tokens[i], 1, " ") + ".";
				}
				strTemp = strTemp + FixLenString(Tokens[i], 1, " ") + ":";
				strTemp = strTemp + FixLenString(port, 1, " ") + "@";
				strTemp = strTemp + FixLenString(mng, 1, " ");
			}
		} finally {
			return strTemp;
		}
	}

	public static String FormatIPAddr(String ip, int port) {
		String strTemp = "";
		try {
			String [] Tokens = TokenizerString(ip, ".");
			if( Tokens.length != 4) {
				strTemp = ip + ":" + port;
			} else {
				int i = 0;
				for( i = 0; i < 3; i++) {
					strTemp = strTemp + FixLenString(Tokens[i], 1, " ") + ".";
				}
				strTemp = strTemp + FixLenString(Tokens[i], 1, " ") + ":";
				strTemp = strTemp + FixLenString(port, 1, " ");
			}
		} finally {
			return strTemp;
		}
	}

	public static boolean IsNumber(String str) {
		boolean result = true;
		try {
			if (str == null) {
			   return false;
			}
			if (str.equals("")) {
			   return  false;
			}
			for (int i = 0; i < str.length(); i++) {
				char c = str.charAt(i);
				if (c == 45) {
					continue;
				} else if (c < 48 || c > 57 ) {
					result = false;
					break;
				}
			}
			return result;
		} catch (Exception e) {
			return result;
		}
	}

	public static String CurrentDate(Calendar curTime) {
		StringBuffer buffer = null;
		try {
			buffer = new StringBuffer();
			buffer.append(curTime.get(Calendar.YEAR)).append("-");
			buffer.append((curTime.get(Calendar.MONTH) + 1)).append("-");
			buffer.append(curTime.get(Calendar.DAY_OF_MONTH)).append(" ");
			buffer.append(curTime.get(Calendar.HOUR_OF_DAY)).append(":");
			buffer.append(curTime.get(Calendar.MINUTE)).append(":");
			buffer.append(curTime.get(Calendar.SECOND)).append(".");
			buffer.append(curTime.get(Calendar.MILLISECOND));
			return buffer.toString();
		} catch (Exception e) {
			return "";
		}
	}

	public static int LEFT_POS = 1;
	public static int CENTER_POS = 2;
	public static int RIGHT_POS = 3;

	public static String FixLenString(String str, int imax, String space, int pos) {
		String strTemp = str;
		int ispacesize = 0;
		if ( strTemp.length() >= imax)
			return strTemp;
		ispacesize = imax - strTemp.length();

		for (int i = 0 ; i < ispacesize; i++) {
			if (pos == RIGHT_POS) {
				strTemp = space + strTemp;
			} else if (pos == LEFT_POS) {
				strTemp =  strTemp + space;
			} else {
				strTemp = space + strTemp;
			}
		}
		return strTemp;
	}

	public static String FixLenString(int value, int imax, String space, int pos) {
		String strTemp = String.valueOf(value);
		return FixLenString(strTemp, imax, space, pos);
	}

	public static String FixLenString(String str, int imax, String space) {
		String strTemp = str;
		int ispacesize = 0;
		if ( strTemp.length() >= imax)
			return strTemp;
		ispacesize = imax - strTemp.length();
		for (int i = 0 ; i < ispacesize; i++) {
			strTemp = space + strTemp;
		}
		return strTemp;
	}

	public static String FixLenString(int value, int imax, String space) {
		String strTemp = String.valueOf(value);
		return FixLenString(strTemp, imax, space);
	}

	public static String getTimeString(int year, int month, int day, int hour, int min, int sec) {
		String szYear, szMonth, szDay, szHour, szMin, szSec, szTemp;
		Calendar curTime = new GregorianCalendar();
		curTime.set(curTime.get(Calendar.YEAR) + year, curTime.get(Calendar.MONTH) + 1 + month,
					curTime.get(Calendar.DAY_OF_MONTH) + day, curTime.get(Calendar.HOUR_OF_DAY) + hour,
					curTime.get(Calendar.MINUTE) + min, curTime.get(Calendar.SECOND) + sec);
		szYear = Integer.toString(curTime.get(Calendar.YEAR));
		szMonth = FixLenString(curTime.get(Calendar.MONTH) + 1, 2, "0");
		szDay = FixLenString(curTime.get(Calendar.DAY_OF_MONTH), 2, "0");
		szHour = FixLenString(curTime.get(Calendar.HOUR_OF_DAY), 2, "0");
		szMin = FixLenString(curTime.get(Calendar.MINUTE), 2, "0");	  // 0..59
		szSec = FixLenString(curTime.get(Calendar.SECOND), 2, "0");	   // 0..59

		szTemp = szYear + "-" + szMonth + "-" + szDay + " " + szHour + ":" + szMin +  ":" + szSec;
		return szTemp;
	}

	public static String getCurrentTime() {
		String szYear, szMonth, szDay, szHour, szMin, szSec, szTemp;
		Calendar curTime = new GregorianCalendar();
		szYear = Integer.toString(curTime.get(Calendar.YEAR));
		szMonth = FixLenString(curTime.get(Calendar.MONTH) + 1, 2, "0");
		szDay = FixLenString(curTime.get(Calendar.DAY_OF_MONTH), 2, "0");
		szHour = FixLenString(curTime.get(Calendar.HOUR_OF_DAY), 2, "0");
		szMin = FixLenString(curTime.get(Calendar.MINUTE), 2, "0");	  // 0..59
		szSec = FixLenString(curTime.get(Calendar.SECOND), 2, "0");	   // 0..59

		szTemp = szYear + "-" + szMonth + "-" + szDay + " " + szHour + ":" + szMin +  ":" + szSec;
		return szTemp;
	}
        public static long getCurrentTimeMilliSecond() {
		Calendar curTime = new GregorianCalendar();
		return curTime.getTimeInMillis();
	}
	public static String getCurrentDate() {
		String szYear, szMonth, szDay, szHour, szMin, szSec, szTemp;
		Calendar curTime = new GregorianCalendar();

		szYear = Integer.toString(curTime.get(Calendar.YEAR));
		szMonth = FixLenString(curTime.get(Calendar.MONTH) + 1, 2, "0");
		szDay = FixLenString(curTime.get(Calendar.DAY_OF_MONTH), 2, "0");

		szTemp = szYear + szMonth + szDay;
		return szTemp;
	}

	public static String getDateString(int year, int month, int day) {

		String szYear, szMonth, szDay, szTemp;
		Calendar curTime = new GregorianCalendar();
		curTime.set(curTime.get(Calendar.YEAR) + year, curTime.get(Calendar.MONTH) + month,
					curTime.get(Calendar.DAY_OF_MONTH) + day, curTime.get(Calendar.HOUR_OF_DAY),
					curTime.get(Calendar.MINUTE), curTime.get(Calendar.SECOND));
		szYear = Integer.toString(curTime.get(Calendar.YEAR));
		szMonth = FixLenString(curTime.get(Calendar.MONTH) + 1, 2, "0");
		szDay = FixLenString(curTime.get(Calendar.DAY_OF_MONTH), 2, "0");

		szTemp = szYear + "-" + szMonth + "-" + szDay;
		return szTemp;
	}

	public static final char TAB  = '\t';
	public static final char CR  = '\n';
	public static final char LF  = '\f';
	public static final char SP  = ' ';

	public static String Trim(String str){
		int prepos = 0, postpos = str.length();
		int i = 0;
		for ( i =0 ; i < str.length(); i++) {
			if (str.charAt(i) == TAB ||
				str.charAt(i) == CR ||
				str.charAt(i) == LF ||
				str.charAt(i) == SP) {
				continue;
			} else {
				prepos = i;
				break;
			}
		}
		for ( i = str.length()-1 ; i >= 0 ; i--) {
			if (str.charAt(i) == TAB ||
				str.charAt(i) == CR ||
				str.charAt(i) == LF ||
				str.charAt(i) == SP) {
				continue;
			} else {
				postpos = i + 1;
				break;
			}
		}
		return str.substring(prepos, postpos);
	}

	public static String[]  TokenizerString(String str, String key) throws Exception{
		StringTokenizer token = null;
		String result[] = null;
		Vector vecStr = new Vector();
		int prepos = 0, postpos = 0;
		try {
			if ( str == null) {
				return null;
			}

			str = Trim(str);

			if (str.equals("")) {
				return null;
			} else {

				postpos = str.indexOf(key);
				while(postpos != -1) {
					vecStr.add(str.substring(prepos, postpos));
					prepos = postpos + 1;
					postpos = str.indexOf(key, prepos);
				}
				vecStr.add(str.substring(prepos, str.length()));

				result = new String[vecStr.size()];

				for(int i=0; i < vecStr.size(); i++)
				{
				 result[i] = (String) vecStr.get(i);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
		return result;
	 }
}
