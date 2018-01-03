package com.isi.process;

import java.util.LinkedList;
/**
*
* @author greatyun
*/
public class JQueue implements IQueue{
	
	private LinkedList list = null;
	private int count;
	
	// 큐 최초 생성시 List 객체 생성 및 큐 카운트 초기화
	public JQueue() {
		// TODO Auto-generated constructor stub
		list = new LinkedList();
		count = 0;
	}
	
	@Override
	public synchronized void put(Object obj) throws InterruptedException {
		// TODO Auto-generated method stub
		list.addLast(obj);
		count++;
//		System.out.println("[Q] put -> " + obj);
		notifyAll();
	}

	@Override
	public synchronized Object get() throws InterruptedException {
		// TODO Auto-generated method stub
		while( count <= 0 ){
			// Q  데이터가 없다면 스레드 wait
//			System.out.println("[Q] 데이터 없어서 wait");
			wait();
		}
		Object obj = list.removeFirst();
		count--;
//		System.out.println("[Q] get -> " + obj);
		notifyAll();
		return obj;
	}

	@Override
	public synchronized int getCnt() {
		// TODO Auto-generated method stub
		return this.count;
	}

}
