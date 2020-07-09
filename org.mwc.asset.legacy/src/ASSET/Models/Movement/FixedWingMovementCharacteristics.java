/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package ASSET.Models.Movement;

import MWC.GUI.Editable;
import MWC.GenericData.WorldAcceleration;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldSpeed;

/**
 * PlanetMayo Ltd.  2003
 * User: Ian.Mayo
 * Date: 03-Sep-2003
 * Time: 09:55:35
 * Log:
 *  $Log: FixedWingMovementCharacteristics.java,v $
 *  Revision 1.1  2006/08/08 14:21:47  Ian.Mayo
 *  Second import
 *
 *  Revision 1.1  2006/08/07 12:25:56  Ian.Mayo
 *  First versions
 *
 *  Revision 1.1  2004/11/09 09:27:44  Ian.Mayo
 *  Add fixed wing support
 *

 */

/**
 * the maneuvering characteristics particular to a helo
 */
public class FixedWingMovementCharacteristics extends ThreeDimMovementCharacteristics
		implements ClimbRateCharacteristics {
	//////////////////////////////////////////////////
	// member objects
	//////////////////////////////////////////////////

	//////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	//////////////////////////////////////////////////////////////////////////////////////////////////
	static public class FixedWingMoveCharsTest extends ASSET.Util.SupportTesting.EditableTesting {
		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		public FixedWingMoveCharsTest(final String val) {
			super(val);
		}

		/**
		 * get an object which we can test
		 *
		 * @return Editable object which we can check the properties for
		 */
		@Override
		public Editable getEditable() {
			return FixedWingMovementCharacteristics.getSampleChars();
		}

		public void testTurningCircle() {
			double rate = 3;
			double speed = 60;
			double circle = FixedWingMovementCharacteristics._calcTurnCircle(rate, speed);
			assertEquals("correct turning circle calculated", 2291.831, circle, 0.01);

			rate = 3;
			speed = 0;
			circle = FixedWingMovementCharacteristics._calcTurnCircle(rate, speed);
			assertEquals("correct turning circle calculated", 2 * _hoverTurnCircle, circle, 0.01);

			rate = 0;
			speed = 60;
			circle = FixedWingMovementCharacteristics._calcTurnCircle(rate, speed);
			assertEquals("correct turning circle calculated", 2 * _hoverTurnCircle, circle, 0.01);

		}

	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * the turn circle we travel through at zero speed (hover)
	 */
	final static private double _hoverTurnCircle = 1;

	/**
	 * static method used to calculate turning circle diameter from angular velocity
	 * and speed
	 */
	protected static double _calcTurnCircle(final double turn_rate_degs_sec, final double speed_m_sec) {
		// so, the equation for the radius of a turning circle using the turn rate and
		// speed is
		//
		// speed (m/sec) = radius (m) * angular velocity (radians/sec)
		//
		// radius = speed / angular velocity

		double radius;

		// just check that we have an angular velocity
		if (turn_rate_degs_sec == 0) {
			// MWC.Utilities.Errors.Trace.trace("Invalid turn rate for fixed wing movement",
			// false);
			radius = _hoverTurnCircle;
		} else {
			if (speed_m_sec != 0d) {
				// convert to angular velocity in radians
				final double angular_velocity = MWC.Algorithms.Conversions.Degs2Rads(turn_rate_degs_sec);

				// ok, calc radius of turning circle at this speed
				radius = speed_m_sec / angular_velocity;
			} else {
				// stationary - just return our hover turning circle
				radius = _hoverTurnCircle;
			}

		}
		return radius * 2;
	}

	public static MovementCharacteristics getSampleChars() {
		final MovementCharacteristics moves = new FixedWingMovementCharacteristics("merlin",
				new WorldAcceleration(Math.PI / 40, WorldAcceleration.M_sec_sec),
				new WorldAcceleration(Math.PI / 40, WorldAcceleration.M_sec_sec), 0,
				new WorldSpeed(200, WorldSpeed.Kts), new WorldSpeed(20, WorldSpeed.Kts),
				new WorldSpeed(20, WorldSpeed.Kts), new WorldSpeed(20, WorldSpeed.Kts),
				new WorldDistance(3000, WorldDistance.YARDS), new WorldDistance(30, WorldDistance.YARDS), 3,
				new WorldSpeed(20, WorldSpeed.Kts), new WorldSpeed(60, WorldSpeed.Kts));
		return moves;

	}

	//////////////////////////////////////////////////
	// constructor
	//////////////////////////////////////////////////

	/**
	 * the turn rate for this helo (degs/sec)
	 */
	private double _myTurnRate;

	/**
	 * the normal climb speed for this helo (m/sec)
	 */
	private WorldSpeed _defaultClimbSpeed;

	/**
	 * the normal dive speed for this helo (m/sec)
	 */
	private WorldSpeed _defaultDiveSpeed;

	/**
	 * @deprecated
	 */
	@Deprecated
	public FixedWingMovementCharacteristics(final String myName, final double accelRate, final double decelRate,
			final double fuel_usage_rate, final double maxSpeed, final double minSpeed, final double defaultClimbRate,
			final double defaultDiveRate, final double maxHeight, final double minHeight, final double myTurnRate,
			final double defaultClimbSpeed, final double defaultDiveSpeed) {
		this(myName, new WorldAcceleration(accelRate, WorldAcceleration.M_sec_sec),
				new WorldAcceleration(decelRate, WorldAcceleration.M_sec_sec), fuel_usage_rate,
				new WorldSpeed(maxSpeed, WorldSpeed.M_sec), new WorldSpeed(minSpeed, WorldSpeed.M_sec),
				new WorldSpeed(defaultClimbRate, WorldSpeed.M_sec), new WorldSpeed(defaultDiveRate, WorldSpeed.M_sec),
				new WorldDistance(maxHeight, WorldDistance.METRES), new WorldDistance(minHeight, WorldDistance.METRES),
				myTurnRate, new WorldSpeed(defaultClimbSpeed, WorldSpeed.M_sec),
				new WorldSpeed(defaultDiveSpeed, WorldSpeed.M_sec));
	}

	public FixedWingMovementCharacteristics(final String myName, final WorldAcceleration accelRate,
			final WorldAcceleration decelRate, final double fuel_usage_rate, final WorldSpeed maxSpeed,
			final WorldSpeed minSpeed, final WorldSpeed defaultClimbRate, final WorldSpeed defaultDiveRate,
			final WorldDistance maxHeight, final WorldDistance minHeight, final double myTurnRate,
			final WorldSpeed defaultClimbSpeed, final WorldSpeed defaultDiveSpeed) {
		super(myName, accelRate, decelRate, fuel_usage_rate, maxSpeed, minSpeed, defaultClimbRate, defaultDiveRate,
				maxHeight, minHeight);
		this._myTurnRate = myTurnRate;
		_defaultClimbSpeed = defaultClimbSpeed;
		_defaultDiveSpeed = defaultDiveSpeed;
	}

	/**
	 * calculate the turn rate at this speed/turning circle
	 *
	 * @param curSpeed_m_sec
	 * @return turn rate (degs/sec)
	 */
	@Override
	public double calculateTurnRate(final double curSpeed_m_sec) {
		return _myTurnRate;
	}

	/**
	 * calculate how long it takes to move to make the specified course change
	 *
	 * @param mean_speed         the mean speed through the turn (m/sec)
	 * @param course_change_degs the change in course required (degs)
	 * @return the time taken (seconds)
	 */
	@Override
	public double calculateTurnTime(final double mean_speed, final double course_change_degs) {
		// hey, we can confidently over-ride this method - since we fundamentally work
		// with turn rates
		double turn_time;

		// calculate the turn time
		if (_myTurnRate == 0)
			turn_time = 0;
		else
			turn_time = Math.abs(course_change_degs) / _myTurnRate;

		return turn_time;
	}

	@Override
	public WorldSpeed getDefaultClimbSpeed() {
		return _defaultClimbSpeed;
	}

	@Override
	public WorldSpeed getDefaultDiveSpeed() {
		return _defaultDiveSpeed;
	}

	//////////////////////////////////////////////////
	// member methods
	//////////////////////////////////////////////////
	/**
	 * get the turning circle diameter (m) at this speed (in m/sec)
	 */
	@Override
	public double getTurningCircleDiameter(final double m_sec) {
		return _calcTurnCircle(_myTurnRate, m_sec);
	}

	public double getTurnRate() {
		return _myTurnRate;
	}

	@Override
	public void setDefaultClimbSpeed(final WorldSpeed _defaultClimbSpeed_m_sec) {
		this._defaultClimbSpeed = _defaultClimbSpeed_m_sec;
	}

	@Override
	public void setDefaultDiveSpeed(final WorldSpeed _defaultDiveSpeed_m_sec) {
		this._defaultDiveSpeed = _defaultDiveSpeed_m_sec;
	}

	public void setTurnRate(final double myTurnRate_deg_sec) {
		this._myTurnRate = myTurnRate_deg_sec;
	}

}
