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

import ASSET.Models.DecisionType;

/**
 * Created by IntelliJ IDEA. User: Ian.Mayo Date: 17-Aug-2004 Time: 14:47:13 To
 * change this template use File | Settings | File Templates.
 */
abstract public class CoreDecision implements DecisionType {
	//////////////////////////////////////////////////
	// member variables
	//////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////
	// embedded class, used for editing the object
	////////////////////////////////////////////////////////////////////////////
	/**
	 * the definition of what is editable about this object
	 */
	public class CoreDecisionInfo extends MWC.GUI.Editable.EditorType {

		/**
		 * constructor for editable details of a set of Layers
		 *
		 * @param data the Layers themselves
		 */
		public CoreDecisionInfo(final CoreDecision data) {
			super(data, data.getName(), "Edit", "images/icons/BehaviourModel.gif");
		}

		/**
		 * editable GUI properties for our participant
		 *
		 * @return property descriptions
		 */
		@Override
		public java.beans.PropertyDescriptor[] getPropertyDescriptors() {
			try {
				final java.beans.PropertyDescriptor[] res = {
						prop("Name", "the current location of this participant"), };

				return res;
			} catch (final java.beans.IntrospectionException e) {
				return super.getPropertyDescriptors();
			}
		}
	}

	/**
	 * the name of this decision model
	 */
	private String _myName;

	/**
	 * whether we're active or not
	 */
	private boolean _isActive = true;
	
	/** 
	 * whether to report decisions or not
	 */
	private boolean _reportDecisions = true;

	//////////////////////////////////////////////////
	// constructor
	//////////////////////////////////////////////////

	/**
	 * the last thing we were doing
	 */
	private String _lastActivity = null;

	//////////////////////////////////////////////////
	// accessors
	//////////////////////////////////////////////////

	/**
	 * use a local stringbuffer to create the activity string. We do it loads of
	 * times, this local buffer will reduce object creation
	 */
	private final StringBuffer _activityBuffer = new StringBuffer();

	/**
	 * constructor for the core decision type.
	 *
	 * @param name - the name for this activity
	 */
	protected CoreDecision(final String name) {
		setActive(true);
		setName(name);
	}

	/**
	 * return a string representing what we are currently up to
	 */
	@Override
	public final String getActivity() {
		// clear our buffer.
		_activityBuffer.setLength(0);
		_activityBuffer.append(getName());
		_activityBuffer.append(":");
		_activityBuffer.append(_lastActivity);
		return _activityBuffer.toString();
	}

	/**
	 * return the name of this detection model
	 *
	 * @return the name
	 */
	@Override
	public final String getName() {
		return _myName;
	}

	/**
	 * find out whether this behaviour is active or not.
	 *
	 * @return yes/no
	 */
	@Override
	public final boolean isActive() {
		return _isActive;
	}

	/**
	 * se twhether this behaviour is active or not.
	 *
	 * @param isActive yes/no
	 */
	@Override
	public final void setActive(final boolean isActive) {
		_isActive = isActive;
	}

	/**
	 * store the most recent activity
	 */
	protected void setLastActivity(final String activity) {
		_lastActivity = activity;
	}

	/**
	 * the name of this detection model
	 *
	 * @param val the name to use
	 */
	@Override
	public final void setName(final String val) {
		_myName = val;
	}

	/**
	 * toString
	 *
	 * @return the returned String
	 */
	@Override
	final public String toString() {
		return getName();
	}

	public void setReportDecisions(Boolean reportDecisions) {
		_reportDecisions = reportDecisions;
	}
	
	@Override
	public boolean isReportDecisions() {
		return _reportDecisions;
	}

}
