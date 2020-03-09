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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public final class XMLNode implements XMLObject {
	private XMLSnippets _myOperation = null;

	public XMLNode(final Element element) {
		// read ourselves in from this node

		// get the choice
		final NodeList nodes = element.getElementsByTagName("XMLChoice");
		if (nodes.getLength() > 0) {
			final Element choice = (Element) nodes.item(0);
			_myOperation = new XMLSnippets(choice);
		}
	}

	/**
	 * perform our update to the supplied element
	 */
	@Override
	public final String execute(final Element currentInstance, final Document parentDocument) {
		// ok, here we go..

		// we're going to replace the object with one of our permutations

		// first get the parent
		final Element parent = (Element) currentInstance.getParentNode();

		// now get a new permutation
		Element newPerm = _myOperation.getInstance();

		// created a cloned instance with the correct parent document
		newPerm = (Element) parentDocument.importNode(newPerm, true);

		// insert this before our current element
		parent.insertBefore(newPerm, currentInstance);

		// and remove the existing instance
		parent.removeChild(currentInstance);

		// convert the new value to a string, to be used in the hashing value
		final String asString = ScenarioGenerator.writeToString(newPerm);

		// and return the value we used
		return asString;
	}

	/**
	 * return the last value used for this attribute
	 */
	@Override
	public final String getCurValue() {
		return "Empty";
	}

	@Override
	public final String getCurValueIn(final Element object) {
		return "Empty";
	}

	/**
	 * return the name of this variable
	 */
	@Override
	public final String getName() {
		return "un-named";
	}

	/**
	 * return the operation we perform
	 */
	public final XMLSnippets getOperation() {
		return _myOperation;
	}

	/**
	 * merge ourselves with the supplied object
	 */
	@Override
	public final void merge(final XMLObject other) {
	}

	/**
	 * randomise ourselves
	 */
	@Override
	public final void randomise() {
		// _myOperation.newPermutation();
	}

}
