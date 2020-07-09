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

package ASSET.Models.Environment;

import MWC.GenericData.WorldLocation;

/**
 * Created by IntelliJ IDEA. User: Ian Date: 06-Feb-2004 Time: 12:46:27 To
 * change this template use File | Settings | File Templates.
 */
public class SimpleEnvironment extends CoreEnvironment {
	////////////////////////////////////////////////////////////
	// member variables
	////////////////////////////////////////////////////////////

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * our atmospheric attenuation
	 */
	private final int _atmosAtten;

	/**
	 * the sea state
	 */
	private final int _seaState;

	/**
	 * the light level
	 */
	private final int _lightLevel;

	////////////////////////////////////////////////////////////
	// constructor
	////////////////////////////////////////////////////////////

	/**
	 * constructor - setting hardcoded atmospheric settings
	 *
	 * @param atmosAtten the current atmospheric attenuation
	 * @param seaState   the current sea state
	 */
	public SimpleEnvironment(final int atmosAtten, final int seaState, final int lightLevel) {
		this._atmosAtten = atmosAtten;
		this._seaState = seaState;
		this._lightLevel = lightLevel;
	}

	////////////////////////////////////////////////////////////
	// member methods
	////////////////////////////////////////////////////////////

	/**
	 * get the atmospheric attenuation
	 *
	 * @param time     current time
	 * @param location place to get data for
	 * @return one of the atmospheric attenuation factors
	 */
	@Override
	public int getAtmosphericAttentuationFor(final long time, final WorldLocation location) {
		return _atmosAtten;
	}

	/**
	 * get the light level
	 *
	 * @param time     current time
	 * @param location place to get data for
	 * @return one of the atmospheric attenuation factors
	 */
	@Override
	public int getLightLevelFor(final long time, final WorldLocation location) {
		return _lightLevel;
	}

	/**
	 * get the sea state
	 *
	 * @param time     current time
	 * @param location place to get data for
	 * @return sea state, from 0 to 10
	 */
	@Override
	public int getSeaStateFor(final long time, final WorldLocation location) {
		return _seaState;
	}

	/**
	 * get the version details for this model.
	 *
	 * <pre>
	 * $Log: SimpleEnvironment.java,v $
	 * Revision 1.2  2006/09/11 15:14:42  Ian.Mayo
	 * Minor tidying
	 *
	 * Revision 1.1  2006/08/08 14:21:44  Ian.Mayo
	 * Second import
	 *
	 * Revision 1.1  2006/08/07 12:25:52  Ian.Mayo
	 * First versions
	 *
	 * Revision 1.5  2004/10/26 14:16:57  Ian.Mayo
	 * Ditch gash
	 *
	 * Revision 1.4  2004/10/25 15:30:21  Ian.Mayo
	 * Start incorporating lookup data tables in environment
	 *
	 * Revision 1.3  2004/05/24 16:01:09  Ian.Mayo
	 * Commit updates from home
	 *
	 * Revision 1.2  2004/03/25 22:48:03  ian
	 * Do light level aswell
	 *
	 * Revision 1.1.1.1  2004/03/04 20:30:53  ian
	 * no message
	 * <p/>
	 * Revision 1.2  2004/02/18 08:49:23  Ian.Mayo
	 * Sync from home
	 * <p/>
	 * Revision 1.2  2003/11/07 14:40:31  Ian.Mayo
	 * Include templated javadoc
	 * <p/>
	 * <p/>
	 * </pre>
	 */
	@Override
	public String getVersion() {
		return "$Date$";
	}

}
