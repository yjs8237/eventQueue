package com.test.thread;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.isi.constans.PROPERTIES;
import com.isi.db.JDatabase;
import com.isi.file.ILog;
import com.isi.file.LogMgr;
import com.isi.file.PropertyRead;
import com.isi.process.IQueue;
import com.test.vo.TESTVO;
import com.test.vo.TestCallVO;
/**
*
* @author greatyun
*/
public class TestService {

	private IQueue queue;
	private ExecutorService executorService;
	private JDatabase dataBase;
	
	private final static int MAX_COUNT 			= 70;		// 스레드풀 MAX 개수
	private LogMgr logwrite;
	
	public TestService(IQueue queue ){
		this.queue = queue;
		this.logwrite = LogMgr.getInstance();
		PropertyRead pr = PropertyRead.getInstance();
		dataBase = new JDatabase("TestService");
		dataBase.connectDB(pr.getValue(PROPERTIES.DB_CLASS), pr.getValue(PROPERTIES.DB_URL), pr.getValue(PROPERTIES.DB_USER), pr.getValue(PROPERTIES.DB_PASSWORD));
	}
	
	public void startService(){
		
		// 최대 70개의 스레드 풀 생성
		executorService = Executors.newFixedThreadPool(MAX_COUNT);
		
		// 70개 스레드 실행
		for (int i = 0; i < MAX_COUNT; i++) {
			
			Callable<Integer> callable = new TestSvcCallable(queue, dataBase , "Thread-" + String.valueOf(i));
			// Callable 객체를 사용함으로서,,, 스레드가 작업이 끝난 뒤 Future 객체를 통해 스레드 처리 결과값을 리턴받는다..
			// 하지만 필요없다.. 스레드는 무한 루프 상태에서 큐에 담긴 데이터를 처리하기 때문에..
			Future<Integer> future = executorService.submit(callable);
			
		}
		
	}
	
	public void stopService(){
		executorService.shutdownNow();
	}
	
	public void putCallTestData() {
		
		TESTVO vo0 = new TESTVO();
		vo0.setDn("7183");
		vo0.setIp("192.168.22.63");
		vo0.setModel("434");
		TESTVO vo1 = new TESTVO();
		vo1.setDn("7225");
		vo1.setIp("192.168.22.55");
		vo1.setModel("115");
		TESTVO vo2 = new TESTVO();
		vo2.setDn("7156");
		vo2.setIp("192.168.22.157");
		vo2.setModel("496");
		TESTVO vo3 = new TESTVO();
		vo3.setDn("7257");
		vo3.setIp("192.168.22.54");
		vo3.setModel("495");
		TESTVO vo4 = new TESTVO();
		vo4.setDn("7404");
		vo4.setIp("192.168.22.65");
		vo4.setModel("495");
		TESTVO vo5 = new TESTVO();
		vo5.setDn("7255");
		vo5.setIp("192.168.22.61");
		vo5.setModel("495");
		TESTVO vo6 = new TESTVO();
		vo6.setDn("7360");
		vo6.setIp("192.168.22.56");
		vo6.setModel("495");
		TESTVO vo7 = new TESTVO();
		vo7.setDn("7397");
		vo7.setIp("192.168.22.58");
		vo7.setModel("496");
		TESTVO vo8 = new TESTVO();
		vo8.setDn("7345");
		vo8.setIp("192.168.230.190");
		vo8.setModel("30006");
		TESTVO vo9 = new TESTVO();
		vo9.setDn("7194");
		vo9.setIp("192.168.32.131");
		vo9.setModel("493");
		TESTVO vo10 = new TESTVO();
		vo10.setDn("7251");
		vo10.setIp("192.168.22.53");
		vo10.setModel("496");
		
		
		ArrayList list = new ArrayList();
		list.add(vo0);
		list.add(vo1);
		list.add(vo2);
		list.add(vo3);
		list.add(vo4);
		list.add(vo5);
		list.add(vo6);
		list.add(vo7);
		list.add(vo8);
		list.add(vo9);
		list.add(vo10);
		
		
		
		
		
		for (int i = 0; i < list.size(); i++) {
			TestCallVO vo = new TestCallVO();
			TESTVO testVO = (TESTVO) list.get(i); 
			vo.setCalledDN(testVO.getDn());
			vo.setCallingDN("1000");
			vo.setCallType("ring");
			vo.setDivision("테스트부서");
			vo.setEmail("test@test.com");
			vo.setName("테스트01");
			vo.setPhoneNum("010-2525-2525");
			vo.setTargetIP(testVO.getIp());
			vo.setTargetModel(testVO.getModel());
			vo.setTeam("테스트팀");
			try {
				queue.put(vo);
			}catch(Exception e){
				
			}
		}
	
		
		
		/*
		List list = dataBase.selectTestData();
		
		for (int i = 0; i < list.size(); i++) {
			TestCallVO callVO = (TestCallVO) list.get(i);
			queue.put(callVO);
		}
		*/
	}
}
