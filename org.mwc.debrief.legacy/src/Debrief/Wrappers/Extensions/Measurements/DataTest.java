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
package Debrief.Wrappers.Extensions.Measurements;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import junit.framework.TestCase;

@Deprecated
public class DataTest extends TestCase {

	public void testDataset() {
		final TimeSeriesTmpDouble timeD = new TimeSeriesTmpDouble("TimeDouble", "Some units");

		timeD.printAll();

		timeD.add(12L, 13D);

		timeD.add(13L, 12D);

		timeD.printAll();
	}

	public void testSerialise() {
		final TimeSeriesTmpDouble original = new TimeSeriesTmpDouble("Data", "Seconds");

		original.add(12L, 100d);
		original.add(15L, 200d);

		try {
			final java.io.ByteArrayOutputStream bas = new ByteArrayOutputStream();
			final java.io.ObjectOutputStream oos = new ObjectOutputStream(bas);
			oos.writeObject(original);
			// get closure
			oos.close();
			bas.close();

			// now get the item
			final byte[] bt = bas.toByteArray();

			// and read it back in as a new item
			final java.io.ByteArrayInputStream bis = new ByteArrayInputStream(bt);

			// create the reader
			final java.io.ObjectInputStream iis = new ObjectInputStream(bis);

			// and read it in
			final Object oj = iis.readObject();

			// get more closure
			bis.close();
			iis.close();

			final TimeSeriesCore clone = (TimeSeriesCore) oj;

			clone.printAll();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public void testStructure() {
		final DataFolder d0 = new DataFolder("l0");
		final DataFolder d0_1 = new DataFolder("l0_1");
		final DataFolder d0_1_1 = new DataFolder("l0_1_1");
		final DataFolder d0_1_2 = new DataFolder("l0_1_2");
		final DataFolder d0_2 = new DataFolder("l0_2");

		d0.add(d0_1);
		d0_1.add(d0_1_1);
		d0_1.add(d0_1_2);
		d0.add(d0_2);

		final TimeSeriesTmpDouble timeD1 = new TimeSeriesTmpDouble("TimeDouble", "Some units");
		timeD1.add(12L, 13D);
		timeD1.add(14L, 15D);

		final TimeSeriesTmpDouble timeD2 = new TimeSeriesTmpDouble("TimeDouble", "Some units");
		timeD2.add(22L, 23D);
		timeD2.add(34L, 25D);

		final TimeSeriesTmpDouble timeS1 = new TimeSeriesTmpDouble("TimeString", "Some units");
		timeS1.add(12L, 313d);
		timeS1.add(14L, 315D);

		d0_1.add(timeD1);
		d0_1.add(timeS1);
		d0_1_1.add(timeD2);

		d0.printAll();

	}

}
