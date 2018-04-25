package com.isi.handler;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.isi.constans.RESULT;

public class ClientSocketHandler {
	
	private Socket sock;
	private String ip;
	private int port;
	
	private PrintWriter		print_writer;
	
	public ClientSocketHandler(String ip , int port) {
		this.ip = ip;
		this.port = port;
	}
	
	public int connect() {
		
		try {
			sock = new Socket();
            InetSocketAddress ipep = new InetSocketAddress(ip, port);
            sock.setTcpNoDelay(true);
            sock.setSoTimeout(2000);
            //접속
            sock.connect(ipep , 2000);
            print_writer = new PrintWriter(new OutputStreamWriter(sock.getOutputStream(), "UTF-8"));
            
		} catch (Exception e) {
			return RESULT.RTN_EXCEPTION;
		}
		
		return RESULT.RTN_SUCCESS;
	}
	
	public int disconnect() {
		try {
			
			if(print_writer != null) {
				print_writer.close(); print_writer = null;
			}
			
			if(sock !=null) {
				sock.close(); sock = null;
			}
			
		} catch (Exception e) {
			return RESULT.RTN_EXCEPTION;
		}
		return RESULT.RTN_SUCCESS;
	}
	
	public int send(String data) {
		
		try {
			
			print_writer.println(data);
			print_writer.flush();
			
		} catch (Exception e) {
			return RESULT.RTN_EXCEPTION;
		}
		return RESULT.RTN_SUCCESS;
	}
	
	
}
