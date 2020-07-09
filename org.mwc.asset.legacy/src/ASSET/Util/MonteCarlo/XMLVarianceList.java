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
import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * list of items in a particular file which may be changed
 */

public class XMLVarianceList {
	private static final String VARIANCE = "Variance";
	/**
	 * the list of variances we manage
	 */
	private java.util.List<XMLVariance> _myVariables = null;

	public XMLVarianceList() {
		_myVariables = new java.util.Vector<XMLVariance>();
	}

	/***************************************************************
	 * constructor
	 ***************************************************************/
	/**
	 * constructor, received a stream containing the list of variances we are going
	 * to manage
	 */
	public XMLVarianceList(final Element myElement) {
		this();

		loadFrom(myElement);
	}

	/**
	 * add this variance to our list
	 */
	public final void add(final XMLVariance variance) {
		_myVariables.add(variance);
	}

	/**
	 * apply our list of operations to this object
	 *
	 * @return a hashing string representing the permutations we've used
	 */
	public final String apply(final String parentXPath, final Document target)
			throws XMLVariance.IllegalExpressionException, XMLVariance.MatchingException {
		String hashStr = "";

		for (int i = 0; i < _myVariables.size(); i++) {
			// get this variance
			final XMLVariance variance = _myVariables.get(i);

			// do this permutation
			final String thisPerm = variance.permutate(parentXPath, target);

			// insert a field separator if we need to
			if (hashStr.length() > 0)
				hashStr += " ";

			// collate the variance details
			hashStr += variance.getName() + ":" + thisPerm;
		}

		return hashStr;
	}

	/**
	 * ************************************************************ member methods
	 * *************************************************************
	 */

	@Override
	public Object clone() {
		System.err.println("SHOULD USE PROPER CLONE METHOD");
		// todo: IMPLEMENT PROPER CLONE METHYOD
		return this;
	}

	/**
	 * get the list of variances we contain
	 */
	public final Iterator<XMLVariance> getIterator() {
		return _myVariables.iterator();
	}

	public final XMLVariance itemAt(final int i) {
		return _myVariables.get(i);
	}

	public final void loadFrom(final Element myElement) {

		// now try to build up the list of vars
		// build up our list of variances from this document
		final NodeList lis = myElement.getElementsByTagName(VARIANCE);

		final int len = lis.getLength();

		for (int i = 0; i < len; i++) {
			final Element o = (Element) lis.item(i);

			try {
				// create this variance
				final XMLVariance xv = new XMLVariance(o);

				// and store it
				_myVariables.add(xv);
			} catch (final ParseException pe) {
				MWC.Utilities.Errors.Trace.trace(pe);
			}

		}
	}

	/**
	 * get the size of our list
	 */
	public final int size() {
		return _myVariables.size();
	}

}
