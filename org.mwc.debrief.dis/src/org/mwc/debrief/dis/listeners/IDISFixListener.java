package org.mwc.debrief.dis.listeners;

public interface IDISFixListener {

	/**
	 * enums used for FORCE in the Entity State PDUs
	 *
	 */
	final short OTHER = 0;
	final short BLUE = 1;
	final short RED = 2;
	final short GREEN = 3;

	/**
	 * KIND marker for OSAT track
	 *
	 */
	final int OSAT_TRACK = 102;
	final int NON_OSAT_TRACK = 101;

	void add(long time, short exerciseId, long id, String eName, short force, short kind, short domain, short category,
			boolean isOSAT, double dLat, double dLong, double depth, double courseDegs, double speedMS, int damage);
}