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

package MWC.Utilities.Errors.Testing;

public class TestEmpty extends EmptyTestCase {
	public static class InnerTestEmpty extends EmptyTestCase {
		public static final String TEST_ALL_TEST_TYPE1 = "UNIT";

		public InnerTestEmpty(final String s) {
			super(s);
		}
	}

	public static final String TEST_ALL_TEST_TYPE = "UNIT";

	/**
	 * Basic constructor - called by the test runners.
	 */
	public TestEmpty(final String s) {
		super(s);
	}
}