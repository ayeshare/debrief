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
package Debrief.ReaderWriter.powerPoint.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.junit.Test;

import Debrief.ReaderWriter.powerPoint.DebriefException;
import Debrief.ReaderWriter.powerPoint.PlotTracks;
import net.lingala.zip4j.exception.ZipException;

public class PlotTracksTest {

	private static final String path = Utils.testFolder + File.separator + "PlotTracks";

	@Test
	public void coordinateTransformationTest() {
		final double exilon = 1e-5;

		final PlotTracks plotter = new PlotTracks();
		float[] result = plotter.coordinateTransformation(0.07269773f, 0.33273053f, 9144000.0f, 6858000.0f, .0f, .0f,
				1.0f, 1.0f, 0);
		assertTrue(Math.abs(result[0] - 7.95032E-9f) < exilon && Math.abs(result[1] - 4.8517137E-8f) < exilon);

		result = plotter.coordinateTransformation(664748.0f, 2281866.0f, 9144000.0f, 6858000.0f, .0f, .0f, 1.0f, 1.0f,
				0);
		assertTrue(Math.abs(result[0] - 0.07269773f) < exilon && Math.abs(result[1] - 0.33273053f) < exilon);

		result = plotter.coordinateTransformation(250.0f, 178.0f, 867.0f, 803.0f, 0.07269773f, 0.33273053f, 0.6098697f,
				0.6098697f, 1);
		assertTrue(Math.abs(result[0] - 0.24855405f) < exilon && Math.abs(result[1] - 0.8074112f) < exilon);
	}

	@Test
	public void retrieveMapTest() throws IOException, ZipException, DebriefException {
		final String prefix = "retrieveMap";
		final PlotTracks plotter = new PlotTracks();
		final HashMap<String, String> result = plotter
				.retrieveMapProperties(path + File.separator + prefix + File.separator + "donor.pptx");
		final HashMap<String, String> expectedResult = new HashMap<String, String>() {
			/**
			 * Known Result
			 */
			private static final long serialVersionUID = -4264437335359313998L;

			{
				put("cx", "439");
				put("cy", "318");
				put("name", "map");
				put("x", "52");
				put("y", "180");
			}
		};
		assertEquals(expectedResult, result);
	}

	@Test
	public void validateDonorFileTest() {
		final String prefix = "validateDonor";
		final String[] donors = new String[] { "correct.pptx", "missingPresentation.pptx", "missingSlides.pptx" };
		final String[] expectedResults = new String[] { null, "Corrupted presentation file", "Corrupted File" };

		for (int i = 0; i < donors.length; i++) {
			final PlotTracks plotTracks = new PlotTracks();
			final String result = plotTracks
					.validateDonorFile(path + File.separator + prefix + File.separator + donors[i]);
			assertEquals(result, expectedResults[i]);
		}
	}
}
