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

//
// <copyright>
//
//  BBN Technologies
//  10 Moulton Street
//  Cambridge, MA 02138
//  (617) 873-8000
//
//  Copyright (C) BBNT Solutions LLC. All rights reserved.
//
// </copyright>
// **********************************************************************
//
// $Source:
// /cvs/distapps/openmap/src/openmap/com/bbn/openmap/dataAccess/iso8211/DDFDataType.java,v
// $
// $RCSfile: DDFDataType.java,v $
// $Revision: 1.2 $
// $Date: 2007/05/04 08:30:14 $
// $Author: ian.mayo $
//
// **********************************************************************

package MWC.GUI.S57;

public class DDFDataType {

	public final static DDFDataType DDFInt = new DDFDataType();
	public final static DDFDataType DDFFloat = new DDFDataType();
	public final static DDFDataType DDFString = new DDFDataType();
	public final static DDFDataType DDFBinaryString = new DDFDataType();

	protected DDFDataType() {
	}
}