package com.test.main;

import java.io.*;
import java.net.HttpRetryException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.xml.ws.http.HTTPBinding;

import com.sun.net.httpserver.*;


public class MyHandler implements HttpHandler{

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		// TODO Auto-generated method stub
		String requestMethod = exchange.getRequestMethod();
		
		if(requestMethod.equalsIgnoreCase("GET")){
			Headers responseHeaders = exchange.getResponseHeaders();
			responseHeaders.set("Content-Type", "text/html");
			URI uri = exchange.getRequestURI();
			System.out.println(uri.getPath());
			
			System.out.println(exchange.getRequestURI().getQuery());
			
			System.out.println(queryToMap(exchange.getRequestURI().getQuery()).toString());
			
			
			String responseDATA = "This is Good!!";
			exchange.sendResponseHeaders(200, responseDATA.length());
			OutputStream responseBody = exchange.getResponseBody();
			
			
			responseBody.write(responseDATA.getBytes());
			responseBody.close();
			
			
		}
		
	}
	
	public Map<String, String> queryToMap (String query){
	    Map<String, String> result = new HashMap<String, String>();
	    for (String param : query.split("&")) {
	        String pair[] = param.split("=");
	        if (pair.length>1) {
	            result.put(pair[0], pair[1]);
	        }else{
	            result.put(pair[0], "");
	        }
	    }
	    return result;
	}

}
