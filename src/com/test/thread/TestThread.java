package com.test.thread;

import com.isi.event.TermConnEvt;
import com.isi.process.IQueue;
import com.test.vo.TestCallVO;

public class TestThread extends Thread{
	
	private IQueue queue;
	
	public TestThread(IQueue queue){
		this.queue = queue;
	}
	
	public void run(){
		while(true){
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			for (int i = 0; i < 100; i++) {
				
				TermConnEvt evt = new TermConnEvt();
				
				try {
					queue.put(evt);
				}catch(Exception e){
					
				}
			}
			
		}
	}
}
