
package ASSET.Models.Mediums;

import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;

/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

public class SSKBroadband extends BroadbandRadNoise {
	//////////////////////////////////////////////////
	// testing
	//////////////////////////////////////////////////
	public static class SSKBBTest extends SupportTesting.EditableTesting {
		/**
		 * get an object which we can test
		 *
		 * @return Editable object which we can check the properties for
		 */
		@Override
		public Editable getEditable() {
			return new SSKBroadband(12, 12);
		}
	}

	/**
	 * ************************************************* the editor info
	 * *************************************************
	 */

	static public class SSKBroadbandInfo extends MWC.GUI.Editable.EditorType {

		/**
		 * constructor for editable details of a set of Layers
		 *
		 * @param data the Layers themselves
		 */
		public SSKBroadbandInfo(final SSKBroadband data) {
			super(data, data.getName(), "BroadbandSensor");
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
						prop("BaseNoiseLevel", "the base level of broadband radiated noise (dB)"),
						prop("SnortNoiseLevel", "the broadband noise level during snort(dB)"), };
				return res;
			} catch (final java.beans.IntrospectionException e) {
				return super.getPropertyDescriptors();
			}
		}
	}

	//////////////////////////////////////////////////////////////////////
	// constructor
	//////////////////////////////////////////////////////////////////////

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	//////////////////////////////////////////////////////////////////////
	// member variables
	//////////////////////////////////////////////////////////////////////
	private double _snortNoiseLevel;

	public SSKBroadband(final double baseNoiseLevel, final double snortNoiseLevel) {
		super(baseNoiseLevel);
		_snortNoiseLevel = snortNoiseLevel;
	}

	@Override
	public double getBaseNoiseLevelFor(final ASSET.Participants.Status status) {
		double res = 0;

		// has status been omitted?
		if (status == null) {
			// yes, this is the XML exporter interrogating us
			return super.getBaseNoiseLevelFor(null);
		}

		// are we snorting
		if (status.getLocation().getDepth() == ASSET.Models.Vessels.SSK.CHARGE_HEIGHT) {
			res = _snortNoiseLevel;
		} else
			res = super.getBaseNoiseLevelFor(status);

		return res;
	}

	/**
	 * get the editor for this item
	 *
	 * @return the BeanInfo data for this editable object
	 */
	@Override
	public Editable.EditorType getInfo() {
		if (_myEditor == null)
			_myEditor = new SSKBroadbandInfo(this);

		return _myEditor;
	}

	public double getSnortNoiseLevel() {
		return _snortNoiseLevel;
	}

	////////////////////////////////////////////////////////////
	// model support
	////////////////////////////////////////////////////////////

	/**
	 * get the version details for this model.
	 *
	 * <pre>
	 * $Log: SSKBroadband.java,v $
	 * Revision 1.1  2006/08/08 14:21:45  Ian.Mayo
	 * Second import
	 *
	 * Revision 1.1  2006/08/07 12:25:54  Ian.Mayo
	 * First versions
	 *
	 * Revision 1.8  2004/08/31 09:36:39  Ian.Mayo
	 * Rename inner static tests to match signature **Test to make automated testing more consistent
	 *
	 * Revision 1.7  2004/08/26 17:05:33  Ian.Mayo
	 * Implement more editable properties
	 * <p/>
	 * Revision 1.6  2004/08/26 16:47:23  Ian.Mayo
	 * Implement more editable properties, add Acceleration property editor
	 * <p/>
	 * Revision 1.5  2004/08/26 16:27:16  Ian.Mayo
	 * Implement editable properties
	 * <p/>
	 * Revision 1.4  2004/05/24 15:10:32  Ian.Mayo
	 * Commit changes conducted at home
	 * <p/>
	 * Revision 1.1.1.1  2004/03/04 20:30:53  ian
	 * no message
	 * <p/>
	 * Revision 1.3  2003/11/05 09:19:31  Ian.Mayo
	 * Include MWC Model support
	 * <p/>
	 * </pre>
	 */
	@Override
	public String getVersion() {
		return "$Date$";
	}

	/**
	 * whether there is any edit information for this item this is a convenience
	 * function to save creating the EditorType data first
	 *
	 * @return yes/no
	 */
	@Override
	public boolean hasEditor() {
		return true;
	}

	public void setSnortNoiseLevel(final double val) {
		_snortNoiseLevel = val;
	}

}