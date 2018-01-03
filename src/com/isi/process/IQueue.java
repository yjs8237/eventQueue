package com.isi.process;
/**
*
* @author greatyun
*/
public interface IQueue {
	  public void put(Object cmd)  throws InterruptedException ;
	  public Object get() throws InterruptedException ;
	  public int getCnt();
}
