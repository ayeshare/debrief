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

import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.builder.Input;
import org.xmlunit.diff.Diff;
import org.xmlunit.diff.Difference;

import Debrief.ReaderWriter.powerPoint.DebriefException;
import Debrief.ReaderWriter.powerPoint.PlotTracks;
import Debrief.ReaderWriter.powerPoint.TrackParser;
import Debrief.ReaderWriter.powerPoint.model.TrackData;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

public class JUnitTests {
	final private String[] donorFiles = new String[] { "master_template.pptx" };
	final private String[] trackFiles = new String[] { "long_tracks.txt", "multi_tracks.txt", "scenario_long_range.txt",
			"scenario_short_range.txt", "speed_change.txt" };
	final private String trackFolder = Utils.testFolder + File.separator + "track_data";
	final private String donorFolder = "../org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats";
	final private String resultsFolder = Utils.testFolder;
	final private String slide1Path = "ppt" + File.separator + "slides" + File.separator + "slide1.xml";

	@Test
	public void integrationTests() throws IOException, ZipException, DebriefException {
		for (final String donor : donorFiles) {
			for (final String track : trackFiles) {
				// System.out.println("Testing donor file " + donor + " with track file "
				// + trackFolder + "/" + track);

				final byte[] encoded = Files.readAllBytes(Paths.get(trackFolder + File.separator + track));
				final String trackXml = new String(encoded);

				final TrackData trackData = TrackParser.getInstance().parse(trackXml);

				final PlotTracks plotter = new PlotTracks();

				final String pptxGenerated = plotter.export(trackData, donorFolder + File.separator + donor,
						"output_test.pptx");

				final String cleanTrackName = track.substring(0, track.lastIndexOf('.'));
				final String expectedPptx = resultsFolder + File.separator + donor.substring(0, donor.lastIndexOf('.'))
						+ File.separator + cleanTrackName + File.separator + cleanTrackName + "_temp.pptx";

				final File generatedPptxTemporaryFolder = new File(
						pptxGenerated.substring(0, pptxGenerated.lastIndexOf('.')) + "generated");
				FileUtils.deleteDirectory(generatedPptxTemporaryFolder);
				generatedPptxTemporaryFolder.mkdir();
				final File expectedPptxTemporaryFolder = new File(
						expectedPptx.substring(0, expectedPptx.lastIndexOf('.')) + "expected");
				if (!expectedPptxTemporaryFolder.exists()) {
					expectedPptxTemporaryFolder.mkdir();
				}

				final ZipFile generatedPptxZip = new ZipFile(pptxGenerated);
				generatedPptxZip.extractAll(generatedPptxTemporaryFolder.getAbsolutePath());
				final ZipFile expectedPptxZip = new ZipFile(expectedPptx);
				expectedPptxZip.extractAll(expectedPptxTemporaryFolder.getAbsolutePath());

				// We compare the file structure first

				Assert.assertTrue("Directories Structures are diferent",
						Utils.compareDirectoriesStructures(generatedPptxTemporaryFolder, expectedPptxTemporaryFolder));

				// Now we compare the slide1.xml structure
				final String expectedXml = expectedPptxTemporaryFolder.getAbsolutePath() + File.separator + slide1Path;
				final String generatedXml = generatedPptxTemporaryFolder.getAbsolutePath() + File.separator
						+ slide1Path;

				final Diff delta = DiffBuilder.compare(Input.fromFile(expectedXml))
						.withTest(Input.fromFile(generatedXml)).build();
				final Iterator<Difference> iter = delta.getDifferences().iterator();
				int size = 0;
				while (iter.hasNext()) {
					final String diff = iter.next().toString();
					// System.out.println(diff);
					size++;
				}
				// System.out.println(size);
				assertFalse(delta.hasDifferences());

				FileUtils.deleteDirectory(generatedPptxTemporaryFolder);
				FileUtils.deleteDirectory(expectedPptxTemporaryFolder);
				new File(pptxGenerated).delete();
				// System.out.println("Success!!");
			}
		}
	}

}
