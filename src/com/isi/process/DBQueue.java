package com.isi.process;

import java.util.LinkedList;

import com.isi.file.PropertyRead;

public class DBQueue implements IQueue{

	private LinkedList list = null;
	private int count;
	
	private static DBQueue pr = new DBQueue(); 
	private DBQueue () {
		list = new LinkedList<>();
		count = 0;
	}
    public static DBQueue getInstance () {
		if(pr == null){
			pr = new DBQueue();
		}
		return pr;
    }
	
	
	@Override
	public synchronized void put(Object obj) throws InterruptedException {
		// TODO Auto-generated method stub
		
		//System.out.println("db Queue insert " + obj.toString());
		
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
		notifyAll();
		return obj;
	}

	@Override
	public synchronized int getCnt() {
		// TODO Auto-generated method stub
		return this.count;
	}
}
