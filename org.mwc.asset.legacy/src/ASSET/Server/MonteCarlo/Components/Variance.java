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

package ASSET.Server.MonteCarlo.Components;

import org.w3c.dom.Document;

/**
 * PlanetMayo Ltd.  2003
 * User: Ian.Mayo
 * Date: 22-Sep-2003
 * Time: 14:57:59
 * Log:
 *  $Log: Variance.java,v $
 *  Revision 1.1  2006/08/08 14:22:20  Ian.Mayo
 *  Second import
 *
 *  Revision 1.1  2006/08/07 12:26:26  Ian.Mayo
 *  First versions
 *
 *  Revision 1.2  2004/05/24 16:21:17  Ian.Mayo
 *  Commit updates from home
 *
 *  Revision 1.1.1.1  2004/03/04 20:30:56  ian
 *  no message
 *
 *  Revision 1.1  2003/09/22 15:50:37  Ian.Mayo
 *  New implementations
 *
 *
 */

/**
 * an individual variance to apply
 *
 */
abstract public class Variance {

	//////////////////////////////////////////////////
	// member variables
	//////////////////////////////////////////////////

	/**
	 * the description of this attribute variance
	 *
	 */
	private String _myName;

	//////////////////////////////////////////////////
	// constructor
	//////////////////////////////////////////////////

	//////////////////////////////////////////////////
	// member methods
	//////////////////////////////////////////////////

	/**
	 * return a description of this variance
	 *
	 */
	public String getDescription() {
		return "Attribute variance:" + _myName;
	}

	public String getName() {
		return _myName;
	}

	/**
	 * create a new permutation of the supplied scenario
	 *
	 * @param existingScenario
	 * @return the new scenario
	 */
	abstract public String getNewPermutation(Document existingScenario);

	//////////////////////////////////////////////////
	// getter/setters
	//////////////////////////////////////////////////

	/**
	 * create a new permutation of the indicated participant within the supplied
	 * scenario
	 *
	 * @param participantName
	 * @param existingScenario
	 * @return the new scenaroio
	 */
	abstract public String getNewPermutation(String participantName, Document existingScenario);

	public void setName(final String myName) {
		this._myName = myName;
	}

}
