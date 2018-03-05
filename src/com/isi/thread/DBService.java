package com.isi.thread;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.isi.db.JDatabase;
import com.isi.file.PropertyRead;
import com.isi.process.IQueue;

public class DBService {
	
	private ExecutorService executorService;
	private String threadID;
	
	private final static int MAX_COUNT 			= 3;		// 스레드풀 MAX 개수
	
	public DBService(){
	}
	
	public void startService(){
		
		// 최대 70개의 스레드 풀 생성
		executorService = Executors.newFixedThreadPool(MAX_COUNT);
		
		// 70개 스레드 실행
		for (int i = 0; i < MAX_COUNT; i++) {
			
			threadID = "thread-" + String.valueOf(i+1);
			
			Callable<Integer> callable = new DBSvcCallable(threadID);
			// Callable 객체를 사용함으로서,,, 스레드가 작업이 끝난 뒤 Future 객체를 통해 스레드 처리 결과값을 리턴받는다..
			// 하지만 필요없다.. 스레드는 무한 루프 상태에서 큐에 담긴 데이터를 처리하기 때문에..
			Future<Integer> future = executorService.submit(callable);
			
		}
		
	}
	
	public void stopService(){
		executorService.shutdownNow();
	}
}
