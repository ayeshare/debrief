package org.mwc.debrief.dis.listeners;

public interface IDISStopListener {

	public static final short PDU_FREEZE = 1;
	public static final short PDU_STOP = 2;
	public static final short PDU_ITERATION_COMPLETE = 7;

	/**
	 * the scenario has stopped
	 *
	 * @param time
	 * @param appId   TODO
	 * @param eid
	 * @param reason
	 * @param numRuns TODO
	 */
	void stop(long time, int appId, short eid, short reason, long numRuns);

}
