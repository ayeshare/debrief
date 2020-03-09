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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

/**
 * Test suite for the entire Java implementation. Runs all Java test cases in
 * the source tree that extend TestCase and are of a specified type. The
 * following system properties must be set:
 * <ul>
 * <li><code>class_root</code>: Directory containing classes. e.g
 * /product/classes
 * <li><code>test_type</code>: Type of test case to load. All test cases with
 * public static final String TEST_ALL_TEST_TYPE defined to match the value of
 * <code>test_type</code> will be loaded.
 * </ul>
 */
public class TestAll extends TestCase {
	public static void main(final String[] args) {
		System.setProperty("test_type", "UNIT");
		// System.setProperty("class_root", "c:\\temp\\d2001\\d2002\\mwc\\gui\\java3d");
		System.setProperty("class_root", "d:\\dev\\debrief\\source\\build\\debrief");
		try {
			final Test test = TestAll.suite();
			final TestResult tr = new TestResult();
			test.run(tr);
		} catch (final Throwable throwable) {
			throwable.printStackTrace(); // To change body of catch statement use Options | File Templates.
		}
	}

	public static Test suite() throws Throwable {
		try {
			final TestSuite suite = new TestSuite();
//            String classRootString = System.getProperty("class_root");
//          System.out.println("loading classes from:" + classRootString);
//            if (classRootString == null) throw new IllegalArgumentException ("System property class_root must be set.");
//            String testType = System.getProperty("test_type");
//            if (testType == null) throw new IllegalArgumentException ("System property test_type must be set.");
//            File classRoot = new File(classRootString);
//            ClassFinder classFinder = new ClassFinder (classRoot);
//            TestCaseLoader testCaseLoader = new TestCaseLoader (testType);
//            testCaseLoader.loadTestCases (classFinder.getClasses ());
//            int numberOfTests = addAllTests (suite, testCaseLoader.getClasses ());
//            System.out.println("Number of test classes found: " + numberOfTests);
			return suite;
		} catch (final Throwable t) {
			// This ensures we have extra information. Otherwise all we get is a "Could not
			// invoke the suite method." message.
			t.printStackTrace();
			throw t;
		}
	}

	/**
	 * Basic constructor - called by the test runners.
	 */

	public TestAll(final String s) {
		super(s);
	}

}
