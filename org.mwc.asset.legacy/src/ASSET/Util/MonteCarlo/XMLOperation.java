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

public interface XMLOperation {
	/**
	 * clone operation, to produce an identical copy
	 *
	 */
	public Object clone();

	/**
	 * return the current value of this permutation
	 *
	 */
	public String getValue();

	/**
	 * merge ourselves with the supplied operation
	 *
	 */
	public void merge(XMLOperation other);

	/**
	 * produce a new value for this operation
	 *
	 */
	public void newPermutation();

}
