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

import MWC.GUI.Layer;

/**
 * Interface definition for classes which implement a properties panel
 */
public interface PropertiesPanel {
	/**
	 * add this component (which isn't an editor)
	 *
	 * @param thePanel the panel to add
	 * @return the component we've created to contain the panel
	 */
	public java.awt.Component add(java.awt.Component thePanel);

	/**
	 * add this constructor. A constructor contains a list of properties which are
	 * collated before a new object is constructed.
	 *
	 * @param info constructor to add
	 */
	public void addConstructor(MWC.GUI.Editable.EditorType info, Layer parentLayer);

	/**
	 * add this editor
	 *
	 * @param info        editor to add
	 * @param parentLayer the layer this object belongs to
	 */
	public void addEditor(MWC.GUI.Editable.EditorType info, Layer parentLayer);

	/**
	 * supply the undo buffer
	 *
	 * @return the undo buffer for this class
	 */
	public MWC.GUI.Undo.UndoBuffer getBuffer();

	/**
	 * remove this panel
	 *
	 * @param theComponent the panel to remove
	 */
	public void remove(java.awt.Component theComponent);

	/**
	 * remove the panel which represents this object
	 *
	 * @param theObject find the panel which shows this object and delete it
	 */
	public void remove(Object theObject);
}
