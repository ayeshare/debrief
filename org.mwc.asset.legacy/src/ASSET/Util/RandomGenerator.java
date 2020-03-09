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

package ASSET.Util;

import java.util.Random;

/**
 * Class providing seed-able random number generation Log: $Log:
 * RandomGenerator.java,v $ Revision 1.1 2006/08/08 14:23:15 Ian.Mayo Second
 * import
 *
 * Revision 1.1 2006/08/07 12:27:24 Ian.Mayo First versions
 *
 * Revision 1.9 2004/09/27 10:07:55 Ian.Mayo Store string representations of
 * number models
 *
 * Revision 1.8 2004/09/27 08:12:06 Ian.Mayo Complete tests
 *
 * Revision 1.7 2004/09/24 11:10:22 Ian.Mayo Get gaussian working
 *
 * Revision 1.6 2004/09/23 14:32:56 Ian.Mayo Muddling through gaussian number
 * generation
 *
 * Revision 1.5 2004/09/08 15:58:46 ian Provide support for generating number
 * within supplied range
 *
 * Revision 1.4 2004/08/05 09:10:08 Ian.Mayo Add normalised random number
 * generator
 * <p/>
 * Revision 1.3 2004/08/05 07:53:27 Ian.Mayo Initialise random Genny
 * <p/>
 * Revision 1.2 2004/05/24 16:18:56 Ian.Mayo Commit updates from home
 * <p/>
 * Revision 1.1.1.1 2004/03/04 20:30:56 ian no message
 * <p/>
 * Revision 1.1 2003/11/05 09:10:31 Ian.Mayo Initial version
 */

public class RandomGenerator {

	//////////////////////////////////////////////////
	// property testing
	//////////////////////////////////////////////////
	public static class RandomGennyTest extends SupportTesting {
		public RandomGennyTest(final String s) {
			super(s);
		}

		public void testConstrainedGaussian() {
			final double min = 60;
			final double max = 140;
			final int num = 10000;
			for (int i = 0; i < num; i++) {
				final double nextV = RandomGenerator.generateRandomNumber(min, max, NORMAL_CONSTRAINED);
				assertTrue("slipped outside lower bounds at num:" + i, nextV >= min);
				assertTrue("slipped outside upper bounds at num:" + i, nextV <= max);
			}
		}

		public void testGaussian() {
			final double min = 60;
			final double max = 140;
			final int num = 10000;
			boolean lowerExceeded = false;
			boolean upperExceeded = false;
			for (int i = 0; i < num; i++) {
				final double nextV = RandomGenerator.generateRandomNumber(min, max, NORMAL);
				if (nextV < min)
					lowerExceeded = true;
				if (nextV > max)
					upperExceeded = true;
			}

			assertTrue("didn't go off bottom of range", lowerExceeded);
			assertTrue("didn't go off top of range", upperExceeded);
		}

		public void testUniform() {
			final double min = 60;
			final double max = 140;
			final int num = 10000;
			for (int i = 0; i < num; i++) {
				final double nextV = RandomGenerator.generateRandomNumber(min, max, UNIFORM);
				assertTrue("slipped outside lower bounds at num:" + i, nextV >= min);
				assertTrue("slipped outside upper bounds at num:" + i, nextV <= max);
			}
		}
	}

	/**
	 * property name indicating that random numbers should be uniformly distributed
	 */
	public static final int UNIFORM = 0;

	/**
	 * property name indicating that random numbers should be normally (Gaussian)
	 * distributed
	 */
	public static final int NORMAL = 1;

	/**
	 * property name indicating that random numbers should be normally (Gaussian)
	 * distributed but constrained to the outer limits
	 */
	public static final int NORMAL_CONSTRAINED = 2;
	//////////////////////////////////////////////////
	// and the text version of the number models
	//////////////////////////////////////////////////
	public static final String UNIFORM_STR = "Uniform";
	public static final String NORMAL_STR = "Normal";

	public static final String NORMAL_CONSTRAINED_STR = "NormalConstrained";
	/**
	 * our random number generator
	 */
	private static Random _myGenny = new Random(12);

	private static final int STANDARD_DEVIATION = 3;

	/**
	 * generate a gaussian number in the set range
	 *
	 * @param min       lower limit
	 * @param max       upper limit
	 * @param constrain - whether another sample should be produced if the
	 *                  calculated value is outside the suppled range
	 * @return gaussian random number within the set range
	 */
	private static double generateGaussian(final double min, final double max, final boolean constrain) {
		final double semi_range = (max - min) / 2;
		double nextGauss = RandomGenerator.generateNormalValue(semi_range / STANDARD_DEVIATION);

		while (constrain && ((nextGauss < -semi_range) || (nextGauss > semi_range))) {
			nextGauss = RandomGenerator.generateNormalValue(semi_range / STANDARD_DEVIATION);
		}

		final double res = min + semi_range + nextGauss;
		return res;
	}

	/**
	 * generate a normally distributed double value, with standard deviation as
	 * supplied
	 *
	 * @param sd standard deviation to apply
	 * @return normally distributed value
	 */
	public static double generateNormalValue(final double sd) {
		final double res = _myGenny.nextGaussian() * sd;
		return res;
	}

	/**
	 * generate a gaussian number in the set range
	 *
	 * @param min         lower limit
	 * @param max         upper limit
	 * @param numberModel whether the number is uniform or gaussian
	 * @return gaussian random number within the set range
	 */
	public static double generateRandomNumber(final double min, final double max, final int numberModel) {
		double res;
		switch (numberModel) {
		case (UNIFORM):
			final double range = max - min;
			final double newVal = ASSET.Util.RandomGenerator.nextRandom() * range;
			res = min + newVal;
			break;
		case (NORMAL_CONSTRAINED):
			res = generateGaussian(min, max, true);
			break;
		default:
			res = generateGaussian(min, max, false);
			break;
		}
		return res;
	}

	/**
	 * get the next random number (in the 0..1 range)
	 *
	 * @return next random double
	 */
	public static double nextRandom() {
		return _myGenny.nextDouble();
	}

	/**
	 * seed the random number generator using the supplied seed
	 *
	 * @param val the seed to use
	 */
	public static void seed(final int val) {
		_myGenny.setSeed(val);

	}
}
