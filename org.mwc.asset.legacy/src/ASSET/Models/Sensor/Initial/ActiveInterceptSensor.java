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

package ASSET.Models.Sensor.Initial;

import ASSET.ParticipantType;
import ASSET.Models.Environment.EnvironmentType;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;

public class ActiveInterceptSensor extends BroadbandSensor {
	////////////////////////////////////////////////////
	// member objects
	////////////////////////////////////////////////////

	////////////////////////////////////////////////////
	// member constructor
	////////////////////////////////////////////////////

	//////////////////////////////////////////////////
	// property testing
	//////////////////////////////////////////////////
	public static class ActiveInterceptTest extends SupportTesting.EditableTesting {
		/**
		 * get an object which we can test
		 *
		 * @return Editable object which we can check the properties for
		 */
		@Override
		public Editable getEditable() {
			return new ActiveInterceptSensor(12);
		}
	}

	////////////////////////////////////////////////////
	// the editor object
	////////////////////////////////////////////////////
	static public class InterceptInfo extends BaseSensorInfo {
		/**
		 * @param data the Layers themselves
		 */
		public InterceptInfo(final ActiveInterceptSensor data) {
			super(data);
		}

		/**
		 * editable GUI properties for our participant
		 *
		 * @return property descriptions
		 */
		@Override
		public java.beans.PropertyDescriptor[] getPropertyDescriptors() {
			try {
				final java.beans.PropertyDescriptor[] res = { prop("Name", "the name of this broadband sensor"),
						prop("Working", "whether this sensor is in use"), };
				return res;
			} catch (final java.beans.IntrospectionException e) {
				return super.getPropertyDescriptors();
			}
		}
	}

	////////////////////////////////////////////////////
	// member methods
	////////////////////////////////////////////////////

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public ActiveInterceptSensor(final int id) {
		super(id, "BB Intercept");
		super.setDetectionAperture(180);
	}

	/**
	 * get the editor for this item
	 *
	 * @return the BeanInfo data for this editable object
	 */
	@Override
	public MWC.GUI.Editable.EditorType getInfo() {
		if (_myEditor == null)
			_myEditor = new InterceptInfo(this);

		return _myEditor;
	}

	////////////////////////////////////////////////////////////
	// model support
	////////////////////////////////////////////////////////////

	@Override
	public int getMedium() {
		return EnvironmentType.BROADBAND_ACTIVE;
	}

	/**
	 *
	 * @param ownship
	 * @param absBearingDegs
	 * @return
	 */
	@Override
	protected double getOSNoise(final ParticipantType ownship, final double absBearingDegs) {
		return 0;
	}

	/**
	 * get the version details for this model.
	 *
	 * <pre>
	 * $Log: ActiveInterceptSensor.java,v $
	 * Revision 1.2  2006/09/21 12:20:40  Ian.Mayo
	 * Reflect introduction of default names
	 *
	 * Revision 1.1  2006/08/08 14:21:54  Ian.Mayo
	 * Second import
	 *
	 * Revision 1.1  2006/08/07 12:26:02  Ian.Mayo
	 * First versions
	 *
	 * Revision 1.7  2004/10/21 10:11:11  Ian.Mayo
	 * We don't use ownship noise in intercept sonar calcs, silly.
	 *
	 * Revision 1.6  2004/10/21 10:06:18  Ian.Mayo
	 * Correct how we manage detection aperture
	 *
	 * Revision 1.5  2004/09/06 14:20:04  Ian.Mayo
	 * Provide default icons & properties for sensors
	 *
	 * Revision 1.4  2004/08/31 09:36:53  Ian.Mayo
	 * Rename inner static tests to match signature **Test to make automated testing more consistent
	 * <p/>
	 * Revision 1.3  2004/08/26 16:27:21  Ian.Mayo
	 * Implement editable properties
	 * <p/>
	 * Revision 1.2  2004/05/24 15:06:17  Ian.Mayo
	 * Commit changes conducted at home
	 * <p/>
	 * Revision 1.1.1.1  2004/03/04 20:30:54  ian
	 * no message
	 * <p/>
	 * Revision 1.1  2004/02/16 13:41:38  Ian.Mayo
	 * Renamed class structure
	 * <p/>
	 * Revision 1.2  2003/11/05 09:19:06  Ian.Mayo
	 * Include MWC Model support
	 * <p/>
	 * </pre>
	 */
	@Override
	public String getVersion() {
		return "$Date$";
	}

}
