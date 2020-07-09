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

package ASSET.Util.XMLFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import ASSET.Util.RandomGenerator;

public class XMLChoice implements XMLOperation {
	/***************************************************************
	 * member variables
	 ***************************************************************/

	/**
	 * the list of values to choose from
	 *
	 */
	private final HashMap<List<Element>, String> _myList;

	/**
	 * the element we will use for this permutation
	 *
	 */
	private List<Element> _currentVal;

	private XMLChoice() {
		_myList = new HashMap<List<Element>, String>();
	}

	/***************************************************************
	 * constructor
	 ***************************************************************/
	public XMLChoice(final Element element) {
		this();

		// you know, get the stuff

		// have a fish around inside it

		final NodeList vars = element.getChildNodes();

		for (int i = 0; i < vars.getLength(); i++) {
			final Element thisE = (Element) vars.item(i);
			final String thisName = thisE.getAttribute("name");
			final NodeList children = thisE.getChildNodes();

			final List<Element> duplicates = new Vector<Element>(0, 1);

			if (children.getLength() > 0) {
				// keep track of the elements to be detached
				final Vector<Element> toBeDetached = new Vector<Element>(0, 1);

				// in this first pass, take copies of the children
				for (int j = 0; j < children.getLength(); j++) {
					final Element el = (Element) children.item(j);
					duplicates.add((Element) el.cloneNode(true));
					toBeDetached.add(el);
				}

				// now pass through again and detach all of the children
				final Iterator<Element> iter2 = toBeDetached.iterator();
				while (iter2.hasNext()) {
					final Element el = iter2.next();
					thisE.removeChild(el);
				}

				_myList.put(duplicates, thisName);
			}
		}

		// stick in a random variable to start us off
		newPermutation();
	}

	private XMLChoice(final XMLChoice other) {
		this();
	}

	/**
	 * clone this object
	 *
	 */
	@Override
	public Object clone() {
		final XMLChoice res = new XMLChoice(this);
		return res;
	}

	/**
	 * return our object as a list
	 *
	 */
	public List<Element> getList() {
		return _currentVal;
	}

	/**
	 * return the human legible current value of this permutation
	 *
	 */
	@Override
	public String getSimpleValue() {
		return _myList.get(_currentVal);
	}

	/**
	 * return the current value of this permutation
	 *
	 */
	@Override
	public String getValue() {
		String res = "";
		final List<Element> ls = _currentVal;
		final Iterator<Element> it = ls.iterator();
		while (it.hasNext()) {
			final Object o = it.next();
			res += o.toString();
		}

		return res;
	}

	/**
	 * merge ourselves with the supplied operation
	 *
	 */
	@Override
	public void merge(final XMLOperation other) {
	}

	/***************************************************************
	 * member methods
	 ***************************************************************/
	/**
	 * produce a new value for this operation
	 *
	 */
	@Override
	public void newPermutation() {
		final int index = (int) (RandomGenerator.nextRandom() * _myList.size());

		final Iterator<List<Element>> it = _myList.keySet().iterator();

		for (int i = 0; i <= index; i++) {
			_currentVal = it.next();
		}

		// done

	}

}
