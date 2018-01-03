package com.isi.exception;

import java.io.PrintWriter;
import java.io.StringWriter;
/**
*
* @author greatyun
*/
public class ExceptionUtil {
	
	private static StringWriter sw = new StringWriter();
	private static PrintWriter pw = new PrintWriter(sw);
	
	
	public static PrintWriter getPrintWriter(){
		sw = new StringWriter();
		pw = new PrintWriter(sw);
		return pw;
	}
	
	public static StringWriter getStringWriter() {
		return sw;
	}
	
	
}
