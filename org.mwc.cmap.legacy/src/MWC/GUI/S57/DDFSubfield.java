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
// $Source: i:/mwc/coag/asset/cvsroot/util/MWC/GUI/S57/DDFSubfield.java,v $
// $RCSfile: DDFSubfield.java,v $
// $Revision: 1.2 $
// $Date: 2007/05/04 08:30:15 $
// $Author: ian.mayo $
//
// **********************************************************************

package MWC.GUI.S57;

import com.bbn.openmap.layer.vpf.MutableInt;

/**
 * Class containing subfield information for a DDFField object.
 */
public class DDFSubfield {

	/**
	 * A DDFSubfieldDefinition defining the admin part of the file that contains the
	 * subfield data.
	 */
	protected DDFSubfieldDefinition defn;
	/**
	 * The object containing the value of the field.
	 */
	protected Object value;
	/**
	 * The number of bytes the field took up in the data file.
	 */
	protected int byteSize;

	protected DDFSubfield() {
	}

	/**
	 * Create a subfield with a definition and the bytes containing the information
	 * for the value. The definition parameters will tell the DDFSubfield what kind
	 * of object to create for the data.
	 */
	public DDFSubfield(final DDFSubfieldDefinition poSFDefn, final byte[] pachFieldData, final int nBytesRemaining) {
		defn = poSFDefn;
		final MutableInt nBytesConsumed = new MutableInt();
		final DDFDataType ddfdt = poSFDefn.getType();

		if (ddfdt == DDFDataType.DDFInt) {
			setValue(new Integer(defn.extractIntData(pachFieldData, nBytesRemaining, nBytesConsumed)));
		} else if (ddfdt == DDFDataType.DDFFloat) {
			setValue(new Double(defn.extractFloatData(pachFieldData, nBytesRemaining, nBytesConsumed)));
		} else if (ddfdt == DDFDataType.DDFString || ddfdt == DDFDataType.DDFBinaryString) {
			setValue(defn.extractStringData(pachFieldData, nBytesRemaining, nBytesConsumed));
		}

		byteSize = nBytesConsumed.value;
	}

	/**
	 * Create a subfield with a definition and a value.
	 */
	public DDFSubfield(final DDFSubfieldDefinition ddfsd, final Object value) {
		setDefn(ddfsd);
		setValue(value);
	}

	/**
	 * Get the value of the subfield as a float. Returns 0f if the value is 0 or
	 * isn't a number.
	 */
	public float floatValue() {
		final Object obj = getValue();
		if (obj instanceof Number) {
			return ((Number) obj).floatValue();
		}
		return 0f;
	}

	public int getByteSize() {
		return byteSize;
	}

	public DDFSubfieldDefinition getDefn() {
		return defn;
	}

	/**
	 * Get the value of the subfield.
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Get the value of the subfield as an int. Returns 0 if the value is 0 or isn't
	 * a number.
	 */
	public int intValue() {
		final Object obj = getValue();
		if (obj instanceof Number) {
			return ((Number) obj).intValue();
		}
		return 0;
	}

	public void setDefn(final DDFSubfieldDefinition ddsfd) {
		defn = ddsfd;
	}

	/**
	 * Set the value of the subfield.
	 */
	public void setValue(final Object o) {
		value = o;
	}

	public String stringValue() {
		final Object obj = getValue();

		if (obj != null) {
			return obj.toString();
		}

		return "";
	}

	/**
	 * Return a string 'key = value', describing the field and its value.
	 */
	@Override
	public String toString() {
		if (defn != null) {
			return defn.getName() + " = " + value;
		}
		return "";
	}
}