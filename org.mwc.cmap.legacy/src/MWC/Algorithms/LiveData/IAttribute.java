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

package MWC.Algorithms.LiveData;

import java.beans.PropertyChangeListener;
import java.util.Vector;

/**
 * a watchable attribute. Note, attributes are indexed - thus this class can
 * store the temporal variation of a number of indexed attributes
 *
 * @author ianmayo
 *
 */
public interface IAttribute {

	/**
	 * representation of an attribute and index couplet
	 *
	 * @author ianmayo
	 *
	 */
	public class IndexedAttribute {
		public final Object index;
		public final IAttribute attribute;

		public IndexedAttribute(final Object _index, final IAttribute _attribute) {
			index = _index;
			attribute = _attribute;
		}
	}

	/**
	 * start listening to this attribute
	 *
	 * @param listener
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener);

	/**
	 * current value of this attribute
	 *
	 * @param index what we're after the current value of
	 *
	 * @return value
	 */
	public DataDoublet getCurrent(Object index);

	/**
	 * retrieve past values for this attribute type
	 *
	 * @param index the index of the list for which we want current values
	 *
	 * @return collection of time-stamped observations
	 */
	public Vector<DataDoublet> getHistoricValues(Object index);

	/**
	 * name of this attribute
	 *
	 * @return name
	 */
	public String getName();

	/**
	 * the units of this attribute, typically used for when plotting this attribute
	 * graphically.
	 *
	 */
	public String getUnits();

	/**
	 * whether this is a significant attribute, displayed by default
	 *
	 * @return yes/no
	 */
	public boolean isSignificant();

	/**
	 * stop listening to this attribute
	 *
	 * @param listener
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener);

	/**
	 * record change in value, pass to listeners
	 *
	 * @param time
	 * @param newValue
	 */
//	public void fireUpdate(long time, Object newValue);

}
