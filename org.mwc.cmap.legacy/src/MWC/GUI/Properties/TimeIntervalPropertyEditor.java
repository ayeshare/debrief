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

// $RCSfile: TimeIntervalPropertyEditor.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.5 $
// $Log: TimeIntervalPropertyEditor.java,v $
// Revision 1.5  2006/05/15 15:18:58  Ian.Mayo
// Efficiency, efficiency, efficiency
//
// Revision 1.4  2005/10/24 09:53:25  Ian.Mayo
// Ease external access to parameter lists
//
// Revision 1.3  2004/09/29 14:21:49  Ian.Mayo
// Minor performance improvement
//
// Revision 1.2  2004/05/25 15:29:12  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:19  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:24  Ian.Mayo
// Initial import
//
// Revision 1.5  2002-07-08 11:47:49+01  ian_mayo
// <>
//
// Revision 1.4  2002-06-05 12:56:33+01  ian_mayo
// unnecessarily loaded
//
// Revision 1.3  2002-05-31 09:53:00+01  ian_mayo
// Make into a class which is more easy to over-ride
//
// Revision 1.2  2002-05-28 09:25:41+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:44+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:01:45+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:43:53+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:42:50+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:45:21  ianmayo
// initial version
//
// Revision 1.1  2000-09-26 10:53:06+01  ian_mayo
// Initial revision
//

package MWC.GUI.Properties;

import java.beans.PropertyEditorSupport;

/**
 * Class providing support for editing time intervals in the property editor
 */
public class TimeIntervalPropertyEditor extends PropertyEditorSupport {

	/**
	 * test labels for the available frequencies
	 */
	private static String _stringTags[] = null;

	/**
	 * values (in millis) for the selectable frequencies
	 */
	private static long _freqs[] = null;

	/**
	 * static version of return tag list (used for reference)
	 *
	 * @return
	 */
	public static String[] getTagList() {
		initialise();
		return _stringTags;
	}

	/**
	 * static version of return values
	 *
	 * @return
	 */
	public static long[] getValueList() {
		initialise();
		return _freqs;
	}

	private static void initialise() {
		if (_stringTags == null) {
			_stringTags = new String[] { "1/10 Sec", "1/5 Sec", "1/3 Sec", "1/2 Sec", "1 Sec", "5 Secs", "10 Secs",
					"30 Secs", "1 Minute", "2 Minutes", "5 Minutes", "10 Minutes", "15 Minutes", "30 Minutes",
					"60 Minutes" };

			_freqs = new long[] { 100, 200, 333, 500, 1000, 5000, 10 * 1000, 30 * 1000, 60 * 1000, 2 * 60 * 1000,
					5 * 60 * 1000, 10 * 60 * 1000, 15 * 60 * 1000, 30 * 60 * 1000, 60 * 60 * 1000 };
		}
	}

	/**
	 * currently selected interval
	 */
	protected Long _myFreq; // time interval in millis

	/**
	 * find the current value of this field
	 *
	 * @return current value in text form
	 */
	@Override
	public String getAsText() {
		final long[] list = getValues();
		final String[] tags = getTags();

		String res = null;
		final double current = _myFreq.longValue();
		for (int i = 0; i < list.length; i++) {
			final double v = list[i];
			if (v == current) {
				res = tags[i];
				break;
			}
		}

		// hmm, did we manage it?
		if (res == null) {
			res = tags[tags.length - 1];
		}

		return res;
	}

	/**
	 * return the labels to use
	 *
	 * @return list of labels
	 */
	@Override
	public String[] getTags() {
		return getTagList();
	}

	/**
	 * get the current value
	 *
	 * @return current interval in millis as asn Integer
	 */
	@Override
	public Object getValue() {
		System.out.println("getting Value:" + _myFreq);
		return _myFreq;
	}

	/**
	 * return the list of integer values
	 */
	public long[] getValues() {
		return getValueList();
	}

	/**
	 * User has selected an item from the list, update our internal value
	 *
	 * @param val user input
	 */
	@Override
	public void setAsText(final String val) {
		final long[] list = getValues();
		final String[] tags = getTags();
		for (int i = 0; i < tags.length; i++) {
			final String thisS = tags[i];
			if (thisS.equals(val)) {
				_myFreq = new Long(list[i]);
				break;
			}
		}

	}

	/**
	 * Initialise the value of this editable object (expecting to receive either an
	 * Integer or a String
	 *
	 * @param p1 the value to use
	 */
	@Override
	public void setValue(final Object p1) {
		if (p1 instanceof Long) {
			_myFreq = (Long) p1;
		} else if (p1 instanceof Integer) {
			_myFreq = new Long(((Integer) p1).intValue());
		} else if (p1 instanceof String) {
			final String val = (String) p1;
			setAsText(val);
		}
	}

}
