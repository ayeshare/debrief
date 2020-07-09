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

package ASSET.Models.Decision;

import java.util.Vector;

/**
 * Created by IntelliJ IDEA. User: Ian.Mayo Date: Feb 8, 2003 Time: 9:03:26 AM
 * To change this template use Options | File Templates.
 */
public interface BehaviourList extends ASSET.Models.DecisionType, MWC.GUI.Editable {
	String UPDATED = "Updated";

	void addListener(String type, java.beans.PropertyChangeListener listener);

	/**
	 * get a description of what is happening in this behaviour
	 *
	 * @return
	 */
	@Override
	String getActivity();

	/**
	 * return the class name as a string (in particular this is used by the
	 * Waterfall Editor to indicate what is being edited
	 *
	 * @return the name
	 */
	public String getBehaviourName();

	/**
	 * get the list of models contained in this sequence
	 *
	 * @return
	 */
	Vector<ASSET.Models.DecisionType> getModels();

	void insertAtFoot(ASSET.Models.DecisionType decision);

	void insertAtHead(ASSET.Models.DecisionType decision);

	void removeListener(String type, java.beans.PropertyChangeListener listener);
}
