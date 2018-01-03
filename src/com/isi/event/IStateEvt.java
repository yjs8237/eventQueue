package com.isi.event;

public interface IStateEvt {
	public static final int AGENT_STATE_CHANGE	= 1;
	public static final int CALL_STATE_CHANGE	= 2;
	public static final int AGENT_STATE_REPORT	= 3;
	public static final int CALL_STATE_REPORT	= 4;
	public int getEventID();
}
