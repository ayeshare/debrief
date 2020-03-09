
package MWC.GUI.TabPanel;

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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.util.Vector;

//	05/06/97	LAB	Created

/**
 * This is a utility class that can be used by beans that support constrained
 * properties. Your can either inherit from this class or you can use an
 * instance of this class as a member field of your bean and delegate various
 * work to it.
 * <p>
 * This extension of the java.beans.VetoableChangeSupport class adds
 * functionality to handle individual property changes.
 *
 * @author Symantec
 */
public class VetoableChangeSupport extends java.beans.VetoableChangeSupport {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * The listener table.
	 *
	 * @see #addVetoableChangeListener
	 * @see #removeVetoableChangeListener
	 */
	protected java.util.Hashtable<String, Vector<VetoableChangeListener>> listenerTable;

	private final Object source;

	/**
	 * Constructs a VetoableChangeSupport object.
	 *
	 * @param sourceBean the bean to be given as the source for any events
	 */

	public VetoableChangeSupport(final Object sourceBean) {
		super(sourceBean);
		source = sourceBean;
	}

	/**
	 * Adds a VetoableListener to the listener list.
	 *
	 * @param propertyName the name of the property to add a listener for
	 * @param listener     the VetoableChangeListener to be added
	 * @see #removeVetoableChangeListener
	 */
	@Override
	public synchronized void addVetoableChangeListener(final String propertyName,
			final VetoableChangeListener listener) {
		java.util.Vector<VetoableChangeListener> listenerList;

		if (listenerTable == null) {
			listenerTable = new java.util.Hashtable<String, Vector<VetoableChangeListener>>();
		}

		if (listenerTable.containsKey(propertyName)) {
			listenerList = listenerTable.get(propertyName);
		} else {
			listenerList = new java.util.Vector<VetoableChangeListener>();
		}

		listenerList.addElement(listener);
		listenerTable.put(propertyName, listenerList);
	}

	/*
	 * !!! LAB !!! 05/06/97 If we want to support non-serializable listeners we will
	 * have to implement the folowing functions and serialize out the listenerTable
	 * HashTable on our own.
	 *
	 * private void writeObject(ObjectOutputStream s) throws IOException private
	 * void readObject(ObjectInputStream s) throws ClassNotFoundException,
	 * IOException
	 */

	/**
	 * Reports a vetoable property update to any registered listeners. If anyone
	 * vetos the change, then a new event is fired reverting everyone to the old
	 * value, and then the PropertyVetoException is rethrown.
	 * <p>
	 * No event is fired if old and new are equal and non-null.
	 *
	 * @param propertyName the programmatic name of the property that was changed
	 * @param oldValue     the old value of the property
	 * @param newValue     the new value of the property
	 *
	 * @exception PropertyVetoException if the specified property value is
	 *                                  unacceptable
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void fireVetoableChange(final String propertyName, final Object oldValue, final Object newValue)
			throws PropertyVetoException {
		if (oldValue != null && oldValue.equals(newValue)) {
			return;
		}

		super.fireVetoableChange(propertyName, oldValue, newValue);

		java.util.Hashtable<String, Vector<VetoableChangeListener>> templistenerTable = null;

		synchronized (this) {
			if (listenerTable == null || !listenerTable.containsKey(propertyName)) {
				return;
			}
			templistenerTable = (java.util.Hashtable<String, Vector<VetoableChangeListener>>) listenerTable.clone();
		}

		java.util.Vector<VetoableChangeListener> listenerList;

		listenerList = templistenerTable.get(propertyName);

		PropertyChangeEvent evt = new PropertyChangeEvent(source, propertyName, oldValue, newValue);

		try {
			for (int i = 0; i < listenerList.size(); i++) {
				final VetoableChangeListener target = listenerList.elementAt(i);
				target.vetoableChange(evt);
			}
		} catch (final PropertyVetoException veto) {
			// Create an event to revert everyone to the old value.
			evt = new PropertyChangeEvent(source, propertyName, newValue, oldValue);
			for (int i = 0; i < listenerList.size(); i++) {
				try {
					final VetoableChangeListener target = listenerList.elementAt(i);
					target.vetoableChange(evt);
				} catch (final PropertyVetoException ex) {
					// We just ignore exceptions that occur during reversions.
				}
			}
			// And now rethrow the PropertyVetoException.
			throw veto;
		}
	}

	/**
	 * Removes a VetoableChangeListener from the listener list.
	 *
	 * @param propertyName the name of the property to remove a listener for.
	 * @param listener     the VetoableChangeListener to be removed
	 * @see #addVetoableChangeListener
	 */
	@Override
	public synchronized void removeVetoableChangeListener(final String propertyName,
			final VetoableChangeListener listener) {
		java.util.Vector<VetoableChangeListener> listenerList;

		if (listenerTable == null || !listenerTable.containsKey(propertyName)) {
			return;
		}
		listenerList = listenerTable.get(propertyName);
		listenerList.removeElement(listener);
	}
}
