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

package ASSET.Util.MonteCarlo;

import java.text.ParseException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public final class XMLAttribute implements XMLObject {
	/***************************************************************
	 * member variables
	 ***************************************************************/

	/**
	 * the name of the attribute we are editing
	 */
	private String _name = null;

	/**
	 * the operation we are performing
	 */
	private XMLOperation _myOperation = null;

	/**
	 * ************************************************************ constructor *
	 *
	 * @throws ParseException ************************************************************
	 */
	public XMLAttribute(final Element element) throws ParseException {
		// read ourselves in from this element

		// read in the attribute name
		_name = element.getAttribute("name");

		// name must be a single word phrase
		if (_name.contains(" ")) {
			throw new RuntimeException("XML Attribute 'name' field must be one word (" + _name + ")");
		}

		// read in the operation
		final NodeList list = element.getElementsByTagName("Range");

		if (list.getLength() == 0) {
			// nope, must be a choice

			// oh well, try for a choice
			final NodeList obj = element.getElementsByTagName("Choice");

			if (obj.getLength() > 0) {
				_myOperation = new XMLChoice((Element) obj.item(0));
			}
		} else {
			// oh well, try for a choice
			_myOperation = new XMLRange((Element) list.item(0));
		}
	}

	/**
	 * perform our update to the supplied element
	 */
	@Override
	public final String execute(final Element object, final Document parentDocument) {
		// create the new permutation
		_myOperation.newPermutation();

		// get a new value for the object
		final String newVal = _myOperation.getValue();

		// update our attribute
		object.setAttribute(_name, newVal);

		// and return the value we used
		return newVal;
	}

	/**
	 * return the last value used for this attribute
	 */
	@Override
	public final String getCurValue() {
		return _myOperation.getValue();
	}

	/**
	 * return the current value of the attribute for this operation in the supplied
	 * element
	 *
	 * @param object the node we are taking the attribute from
	 * @return the String representation of the attribute in this object
	 */
	@Override
	public final String getCurValueIn(final Element object) {
		final String res = object.getAttribute(_name);
		return res;
	}

	/**
	 * return the name of this variable
	 */
	@Override
	public final String getName() {
		return _name;
	}

	/***************************************************************
	 * member methods
	 ***************************************************************/
	/**
	 * accessor to get the operation (largely for testing)
	 */
	public final XMLOperation getOperation() {
		return _myOperation;
	}

	/**
	 * merge ourselves with the supplied object
	 */
	@Override
	public final void merge(final XMLObject other) {
		final XMLAttribute xr = (XMLAttribute) other;
		_myOperation.merge(xr._myOperation);
	}

	/**
	 * randomise ourselves
	 */
	@Override
	public final void randomise() {
		_myOperation.newPermutation();
	}

}
