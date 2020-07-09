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

package org.mwc.debrief.timebar.model;

import java.awt.Color;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.eclipse.nebula.widgets.ganttchart.GanttCheckpoint;

import MWC.GUI.Plottable;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.TacticalData.NarrativeEntry;

public class TimeSpot implements IEventEntry {
	final Object _source;
	final String _name;
	Calendar _time = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.UK);
	GanttCheckpoint _presentation;
	final Color _color;

	public TimeSpot(final NarrativeEntry entry) {
		_source = entry;
		_name = "";// entry.getName();
		_time.setTime(entry.getDTG().getDate());
		_color = Color.red;
	}

	public TimeSpot(final Watchable spot) {
		_source = spot;
		_name = spot.getName();
		_time.setTime(spot.getTime().getDate());
		_color = spot.getColor();
	}

	public TimeSpot(final WatchableList spot) {
		_source = spot;
		_name = spot.getName();
		_time.setTime(spot.getStartDTG().getDate());
		_color = spot.getColor();
	}

	@Override
	public List<IEventEntry> getChildren() {
		return null;
	}

	@Override
	public org.eclipse.swt.graphics.Color getColor() {
		if (_color != null) {
			return ColorUtils.convertAWTtoSWTColor(_color);
		}
		return null;
	}

	@Override
	public Calendar getEnd() {
		final Calendar end = _time;
		end.add(Calendar.HOUR, 1);
		return end;
	}

	@Override
	public String getName() {
		return _name;
	}

	@Override
	public Object getSource() {
		return _source;
	}

	@Override
	public Calendar getStart() {
		return _time;
	}

	@Override
	public String getToolTipText() {
		if (_source instanceof NarrativeEntry) {
			final StringBuffer res = new StringBuffer();
			final NarrativeEntry entry = (NarrativeEntry) _source;
			res.append(entry.getDTGString());
			res.append("\n");
			if (entry.getSource() != null) {
				res.append(entry.getSource());
				res.append("\n");
			}
			if (entry.getType() != null) {
				res.append(entry.getType());
				res.append("\n");
			}
			res.append(entry.getEntry());
			return res.toString();
		}
		return "";
	}

	@Override
	public boolean isVisible() {
		if (getSource() instanceof Plottable) {
			return ((Plottable) getSource()).getVisible();
		}
		return true;
	}
}
