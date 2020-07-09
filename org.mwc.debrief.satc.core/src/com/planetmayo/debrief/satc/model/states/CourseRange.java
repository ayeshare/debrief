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

package com.planetmayo.debrief.satc.model.states;

import com.planetmayo.debrief.satc.util.MathUtils;

/**
 * class representing a set of Course bounds
 *
 * @author ian
 *
 */
public class CourseRange extends BaseRange<CourseRange> {
	private volatile double _min;
	private volatile double _max;

	/**
	 * copy constructor
	 *
	 * @param range
	 */
	public CourseRange(final CourseRange range) {
		this(range.getMin(), range.getMax());
	}

	public CourseRange(final double minCourse, double maxCourse) {
		if (minCourse > maxCourse) {
			maxCourse += 2 * Math.PI;
		}
		this._min = minCourse;
		this._max = maxCourse;
	}

	/**
	 * does the supplied course fit in my range?
	 *
	 * @param speed the value to test
	 * @return yes/no
	 */
	public boolean allows(double course) {
		// put the coursre into my domain
		while (course < _min)
			course += 2 * Math.PI;

		// and just check we're not too high
		while (course > _max)
			course -= 2 * Math.PI;

		// and test
		return (course >= _min) && (course <= _max);
	}

	public double calcErrorFor(double estimate, double state) {
		double delta = 0;

		// put us in the correct domain
		while (state < _min)
			state += Math.PI * 2;
		while (state > _max)
			state -= Math.PI * 2;

		while (estimate < _min)
			estimate += Math.PI * 2;
		while (estimate > _max)
			estimate -= Math.PI * 2;

		// ok, what's the offset?
		delta = estimate - state;

		// ok - we 'normalise' this speed according to the max/min
		Double limit;

		// is the state lower than the estimate?
		if (delta > 0) {
			// ok, do we have a min value
			limit = _min;
		} else {
			// higher, use max
			limit = _max;
		}

		// do we have a relevant limit?
		if (limit != null) {
			// what's the range from the estimate to the limit
			final double allowable = estimate - limit;

			// ok, and how far through this are we
			delta = delta / allowable;
		}

		// ok, make it absolute
		delta = Math.abs(delta);

		return delta;
	}

	@Override
	public void constrainTo(final CourseRange sTwo) throws IncompatibleStateException {
		// right, there's a chance that the two ranges are in different cycles (one
		// is -5 to +5, the other is 355 to 365) , so put them in the same domain
		final double newUpper, newLower;
		if (sTwo._max < _min) {
			newLower = sTwo._min + Math.PI * 2;
			newUpper = sTwo._max + Math.PI * 2;
		} else {
			newLower = sTwo._min;
			newUpper = sTwo._max;
		}

		// note: we're using _min and _max because our getter mangles the value to
		// make it human readable
		_min = Math.max(_min, newLower);
		_max = Math.min(_max, newUpper);

		// aah, but are are we now in the wrong domain?
		if (_min > Math.PI * 2) {
			// yes, put us back into 0..360
			_min -= Math.PI * 2;
			_max -= Math.PI * 2;
		}

		// aah, but what if we're now impossible?
		if (_max < _min)
			throw new IncompatibleStateException("Incompatible states", this, sTwo);
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		final CourseRange other = (CourseRange) obj;

		if (_max != other._max)
			return false;
		if (_min != other._min)
			return false;

		return true;
	}

	/**
	 * generate a course object that is the reverse of this one
	 *
	 * @return
	 */
	public CourseRange generateInverse() {
		// special case. if it covers the full circle keep a full circle
		final CourseRange res;
		if (_max - _min == 2 * Math.PI)
			res = new CourseRange(this._min, this._max);
		else {
			// generate the inverse angles
			final double newMin = MathUtils.normalizeAngle(this._min + Math.PI);
			final double newMax = MathUtils.normalizeAngle(this._max + Math.PI);

			res = new CourseRange(newMin, newMax);
		}
		return res;
	}

	public double getMax() {
		return _max > 2 * Math.PI ? _max - 2 * Math.PI : _max;
	}

	public double getMin() {
		return _min < 0 ? _min + 2 * Math.PI : _min;
	}

	@Override
	public int hashCode() {
		int result = (int) _min;
		result = 31 * result + (int) _max;
		return result;
	}

	public String toDebugString() {
		return (int) Math.toDegrees(_min) + " - " + (int) Math.toDegrees(_max);
	}
}
