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

import java.util.Vector;

import ASSET.Util.RandomGenerator;

/**
 * PlanetMayo Ltd.  2003
 * User: Ian.Mayo
 * Date: 22-Sep-2003
 * Time: 14:58:51
 * Log:
 *  $Log: Choice.java,v $
 *  Revision 1.1  2006/08/08 14:22:19  Ian.Mayo
 *  Second import
 *
 *  Revision 1.1  2006/08/07 12:26:25  Ian.Mayo
 *  First versions
 *
 *  Revision 1.6  2004/08/31 09:37:19  Ian.Mayo
 *  Rename inner static tests to match signature **Test to make automated testing more consistent
 *
 *  Revision 1.5  2004/08/05 07:56:55  Ian.Mayo
 *  Use our own (seedable) random genny
 *
 *  Revision 1.4  2004/05/24 16:21:13  Ian.Mayo
 *  Commit updates from home
 *
 *  Revision 1.1.1.1  2004/03/04 20:30:56  ian
 *  no message
 *
 *  Revision 1.3  2004/02/16 13:48:55  Ian.Mayo
 *  make tests more specific
 *
 *  Revision 1.2  2003/09/23 14:54:07  Ian.Mayo
 *  New implementations
 *
 *  Revision 1.1  2003/09/22 15:50:34  Ian.Mayo
 *  New implementations
 *
 */

/**
 * class returning one of a number of choices
 */
public class Choice implements AttributeModifier {
	public static class ChoiceTest extends junit.framework.TestCase {
		public ChoiceTest(final String val) {
			super(val);
		}

		public void testDistribution() {
			final Choice choice = new Choice();
			choice.add("a");
			choice.add("b");
			choice.add("c");

			final int size = 1000000;
			int numA = 0;
			int numB = 0;
			int numC = 0;

			for (int i = 0; i < size; i++) {
				final String thisVal = choice.getNewValue();
				if (thisVal.equals("a")) {
					numA++;
				}
				if (thisVal.equals("b")) {
					numB++;
				}
				if (thisVal.equals("c")) {
					numC++;
				}
			}

			// check the dist
			assertEquals("correct num of a", size / 3, numA, 2000);
			assertEquals("correct num of b", size / 3, numB, 2000);
			assertEquals("correct num of c", size / 3, numC, 2000);

		}

		public void testGenerate() {
			final Choice choice = new Choice();
			choice.add("a");
			choice.add("b");
			choice.add("c");

			assertEquals("they got stored", 3, choice._myChoices.size(), 0);

			assertTrue("found a", choice._myChoices.contains("a"));
			assertTrue("found b", choice._myChoices.contains("b"));
			assertTrue("found c", choice._myChoices.contains("c"));
		}
	}

	public static void main(final String[] args) {
		final ChoiceTest ts = new ChoiceTest("trial");
		ts.testGenerate();
		ts.testDistribution();
	}

	/////////////////////////////////////////////////
	// member methods
	//////////////////////////////////////////////////

	//////////////////////////////////////////////////
	// member variables
	//////////////////////////////////////////////////
	Vector<String> _myChoices;

	//////////////////////////////////////////////////
	// constructor
	//////////////////////////////////////////////////
	public Choice() {
		_myChoices = new Vector<String>(0, 1);
	}

	public void add(final String thisChoice) {
		_myChoices.add(thisChoice);
	}

	/////////////////////////////////////////////////
	// member methods
	//////////////////////////////////////////////////
	@Override
	public String getNewValue() {
		final int len = _myChoices.size();
		String res = null;

		if (len > 0) {
			final int index = (int) (RandomGenerator.nextRandom() * len);
			res = _myChoices.elementAt(index);
		}

		return res;
	}
	//////////////////////////////////////////////////
	// testing
	//////////////////////////////////////////////////

}
