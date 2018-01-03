package com.isi.file;
/**
*
* @author greatyun
*/
public class LogSequence {
	
	private static int sequence = 0;
	
	
	public static int getSequence(){
		return sequence;
	}
	
	public static int setSequence(int seq){
		sequence = seq;
		return sequence;
	}
	
	public static int setSeqIncreament(){
		sequence++;
		return sequence;
	}
	
}
