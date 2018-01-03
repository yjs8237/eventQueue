package com.test.main;

import java.util.concurrent.Callable;

public class TestCallable implements Callable<String>{

	@Override
	public String call() throws Exception {
		// TODO Auto-generated method stub
		int test = 0;
		
		for (int i = 0; i < 3; i++) {
			test += i;
			Thread.sleep(1000);
			System.out.println(Thread.currentThread().getName() + " : " + i);
		}
		return Thread.currentThread().getName();
	}

}
