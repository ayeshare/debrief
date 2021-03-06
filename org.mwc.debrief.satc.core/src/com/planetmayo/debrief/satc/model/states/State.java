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

import java.awt.Color;
import java.util.Date;
import java.util.HashMap;

import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.vividsolutions.jts.geom.Point;

/* the state of an object at a specific time
 *
 */
public class State implements Comparable<State> {
	private final Date _time;
	private final double _speed;
	private final double _course;
	private final Point _location;
	transient private final HashMap<BaseContribution, Double> _scores = new HashMap<BaseContribution, Double>();

	/**
	 * the (optional) color used to present this state
	 *
	 */
	private Color _color;

	public State(final Date time, final Point location, final double course, final double speed) {
		if (time == null) {
			throw new IllegalArgumentException("time can't be null");
		}
		_time = time;
		_location = location;
		_course = course;
		_speed = speed;
	}

	@Override
	public int compareTo(final State o) {
		return getTime().compareTo(o.getTime());
	}

	public Color getColor() {
		return _color;
	}

	public double getCourse() {
		return _course;
	}

	public Point getLocation() {
		return _location;
	}

	public HashMap<BaseContribution, Double> getScores() {
		return _scores;
	}

	public double getSpeed() {
		return _speed;
	}

	public Date getTime() {
		return _time;
	}

	public void setColor(final Color color) {
		_color = color;
	}

	public void setScore(final BaseContribution cont, final double thisError) {
		_scores.put(cont, thisError);
	}

}
