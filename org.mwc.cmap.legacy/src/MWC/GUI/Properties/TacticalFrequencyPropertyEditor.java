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

package MWC.GUI.Properties;

import java.beans.PropertyEditorSupport;

/**
 * class to provide list of time frequencies related to typical analysis periods
 */
public class TacticalFrequencyPropertyEditor extends PropertyEditorSupport {

	private static final long _60_MINS = 60 * 60 * 1000l;

	private static final long _30_MINS = 30 * 60 * 1000l;

	private static final long _10_MINS = 10 * 60 * 1000l;

	/**
	 * the currently selected frequency (in millis)
	 */
	protected Long _myFreq;

	/**
	 * the list of tags shown in the drop-down list
	 */
	private final String _stringTags[] = { "1 Min", "10 Mins", "30 Mins", "1 Hour", "6 Hours", "12 Hours", "24 Hours",
			"48 Hours", "72 Hours" };

	/**
	 * the values to use for the tags in the list
	 */
	private final long _freqs[] = { 1 * 60 * 1000l, _10_MINS, _30_MINS, _60_MINS, 6 * _60_MINS, 12 * _60_MINS,
			24 * _60_MINS, 48 * _60_MINS, 72 * _60_MINS };

	@Override
	public String getAsText() {
		String res = null;

		// check we have a freq
		if (_myFreq == null)
			return res;

		final long[] freqs = getFreqs();
		final String[] tags = getTags();
		final long current = _myFreq;
		for (int i = 0; i < freqs.length; i++) {
			final long v = freqs[i];
			if (v == current) {
				res = tags[i];
				break;
			}

		}
		return res;
	}

	protected long[] getFreqs() {
		return _freqs;
	}

	@Override
	public String[] getTags() {
		return _stringTags;
	}

	@Override
	public Object getValue() {
		return new Long(_myFreq);
	}

	@Override
	public void setAsText(final String val) {
		final long[] freqs = getFreqs();
		final String[] tags = getTags();
		for (int i = 0; i < tags.length; i++) {
			final String thisS = tags[i];
			if (thisS.equals(val)) {
				_myFreq = freqs[i];
				break;
			}
		}

	}

	@Override
	public void setValue(final Object p1) {
		if (p1 instanceof Long) {
			_myFreq = (Long) p1;
		} else if (p1 instanceof String) {
			final String val = (String) p1;
			setAsText(val);
		} else if (p1 == null) {
			_myFreq = getFreqs()[0];
		}
	}
}
