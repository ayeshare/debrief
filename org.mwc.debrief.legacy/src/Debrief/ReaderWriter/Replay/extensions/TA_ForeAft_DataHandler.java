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
package Debrief.ReaderWriter.Replay.extensions;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import MWC.GenericData.HiResDate;
import MWC.Utilities.ReaderWriter.AbstractPlainLineImporter;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;
import junit.framework.TestCase;

public class TA_ForeAft_DataHandler extends Core_TA_Handler {

	public static class TestMe extends TestCase {
		//
		private final List<String> _messages = new ArrayList<String>();

		public void testImport() throws ParseException {
			final String str = ";TA_FORE_AFT: 100112 120000 \"SENSOR ALPHA\" \"TA ARRAY\" 12.75 3.00\t14.75 10.00";
			final TA_ForeAft_DataHandler ff = new TA_ForeAft_DataHandler() {

				@Override
				protected void storeMeasurement(final String platform_name, final String sensor_name,
						final String folder, final String dataset, final String units, final HiResDate theDate,
						final double measurement) {
					super.storeMeasurement(platform_name, sensor_name, folder, dataset, units, theDate, measurement);
					final String outStr = "stored:" + dataset + " value of:" + measurement;
					_messages.add(outStr);

				}

			};
			ff.readThisLine(str);

			assertEquals("found items", 4, _messages.size());

		}
	}

	final private String _datasetName;

	public TA_ForeAft_DataHandler() {
		this("TA_FORE_AFT", "Fore & Aft");
	}

	public TA_ForeAft_DataHandler(final String title, final String datasetName) {
		super(title);

		_datasetName = datasetName;
	}

	protected boolean checkIfLong(final String[] tokens) {
		return false;
	}

	protected String nameForRow(final int ctr, final boolean isLong) {
		final String datasetName;

		if (ctr <= 1) {
			datasetName = "Fore";
		} else {
			datasetName = "Aft";
		}

		return datasetName;
	}

	@Override
	public Object readThisLine(final String theLine) throws ParseException {

		// should look like:
		// ;TA_FORE_AFT: 100112 120000 SENSOR TA_ARRAY [12.75 0.00] [12.75 0.00]

		// get a stream from the string
		final StringTokenizer st = new StringTokenizer(theLine);

		// declare local variables
		final HiResDate theDate;
		final String platform_name;
		final String sensor_name;

		// skip the comment identifier
		st.nextToken();

		// combine the date, a space, and the time
		final String dateToken = st.nextToken();
		final String timeToken = st.nextToken();

		// and extract the date
		theDate = DebriefFormatDateTime.parseThis(dateToken, timeToken);

		// trouble - the track name may have been quoted, in which case we will
		// pull
		// in the remaining fields aswell
		platform_name = AbstractPlainLineImporter.checkForQuotedName(st).trim();
		sensor_name = AbstractPlainLineImporter.checkForQuotedName(st).trim();

		// extract the measuremetns
		// ok, parse the rest of the line
		final String remainingText = st.nextToken("").trim();

		// now split this into tab separated tokens
		final String[] tokens = remainingText.split("\\s+");

		// SPECIAL CASE: for a modules entry, the line will either contain a set of
		// values, then zeros,
		// or a set of zeroes, then values.
		// Introduce test, to check
		final boolean isLong = checkIfLong(tokens);

		int ctr = 0;
		for (final String str : tokens) {
			// hmm, depth or heading value?
			final boolean isDepth = (ctr % 2) == 0;

			final String nextStr = str.trim();

			if (isNull(nextStr)) {
				// ok, skip this one. We haven't got the data
				continue;
			} else {

				// extract the double
				final double thisVal = Double.valueOf(nextStr);

				if (isLong && ctr > 7 || !isLong && ctr < 8) {

					// sort out if we're fore or aft
					final String datasetName = nameForRow(ctr, isLong);

					// ok, try to store the measurement
					if (isDepth) {
						storeMeasurement(platform_name, sensor_name, _datasetName + " / " + datasetName, "Depth", "m",
								theDate, thisVal);
					} else {
						storeMeasurement(platform_name, sensor_name, _datasetName + " / " + datasetName, "Heading",
								"\u00b0", theDate, thisVal);
					}
				}
			}

			// and increment
			ctr++;
		}

		return null;

	}

}
