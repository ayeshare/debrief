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

package ASSET.GUI.CommandLine;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Vector;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;

import ASSET.Scenario.CoreScenario;
import ASSET.Scenario.Observers.CoreObserver;
import ASSET.Scenario.Observers.InterScenarioObserverType;
import ASSET.Scenario.Observers.RecordToFileObserverType;
import ASSET.Scenario.Observers.ScenarioObserver;
import ASSET.Util.SupportTesting;
import ASSET.Util.MonteCarlo.ScenarioGenerator;
import ASSET.Util.XML.ASSETReaderWriter;

/**
 * Created by IntelliJ IDEA. User: Ian.Mayo Date: 02-Jun-2003 Time: 15:05:23
 * Class providing multi scenario support to the command line class Log:
 */

public class MultiScenarioCommandLine {
	////////////////////////////////////////////////////////////
	// testing stuff
	////////////////////////////////////////////////////////////
	public static class MultiServerTest extends SupportTesting {
		public MultiServerTest(final String val) {
			super(val);
		}

		public void testCommandLineMainProcessing() {
			final String[] args = new String[2];
			args[0] = "../org.mwc.asset.legacy/src/ASSET/Util/MonteCarlo/test_variance_scenario.xml";
			args[1] = "../org.mwc.asset.legacy/src/ASSET/Util/MonteCarlo/test_variance_realistic.xml";

			CommandLine.main(args);
		}

		public void testInvalidStartups() {
			final ByteArrayOutputStream bos = new ByteArrayOutputStream();
			final ByteArrayOutputStream bes = new ByteArrayOutputStream();

			final PrintStream out = new PrintStream(bos);
			final PrintStream err = new PrintStream(bes);
			final InputStream in = new ByteArrayInputStream(new byte[] {});

			////////////////////////////////////////////////////////////
			// first with null args
			////////////////////////////////////////////////////////////
			bos.reset();
			bes.reset();
			String[] args = null;
			MultiScenarioCommandLine scen = new MultiScenarioCommandLine();
			int res = 0;
			try {
				res = scen.processThis(args, out, err, in);
			} catch (final XPathExpressionException e) {
				e.printStackTrace();
			}
			assertEquals("wrong num args error returned", WRONG_PARAMETERS, res);

			////////////////////////////////////////////////////////////
			// now with zero args
			////////////////////////////////////////////////////////////
			bos.reset();
			bes.reset();
			args = new String[0];
			scen = new MultiScenarioCommandLine();
			try {
				res = scen.processThis(args, out, err, in);
			} catch (final XPathExpressionException e) {
				e.printStackTrace();
			}
			assertEquals("wrong num args error returned", WRONG_PARAMETERS, res);

			////////////////////////////////////////////////////////////
			// now with only 1 args
			////////////////////////////////////////////////////////////
			bos.reset();
			bes.reset();
			args = new String[1];
			args[0] = "../org.mwc.asset.legacy/src/ASSET/Util/MonteCarlo/small_test_scenario.xml";

			scen = new MultiScenarioCommandLine();
			try {
				res = scen.processThis(args, out, err, in);
			} catch (final XPathExpressionException e) {
				e.printStackTrace();
			}

			assertEquals("wrong num args error returned", WRONG_PARAMETERS, res);

			final String errStr = bes.toString();
			assertTrue("correct error message",
					errStr.indexOf("Sorry, both a scenario file and command file are required") >= 0);

			////////////////////////////////////////////////////////////
			// and now with an invalid control file
			////////////////////////////////////////////////////////////
			bos.reset();
			bes.reset();
			args = new String[2];
			args[0] = "../org.mwc.asset.legacy/src/ASSET/Util/MonteCarlo/small_test_scenario.xml";
			args[1] = "../org.mwc.asset.legacy/src/ASSET/Util/MonteCarlo/test_variance_invalid.xml";
			scen = new MultiScenarioCommandLine();
			try {
				res = scen.processThis(args, out, err, in);
			} catch (final XPathExpressionException e) {
				e.printStackTrace();
			}

			assertEquals("wrong num args error returned", PROBLEM_LOADING, res);

			// check the contents of the error message
			final String str = bes.toString();
			assertTrue("contains some error message", str.indexOf("Problem loading multi-scenario generator:") >= 0);
		}

		public void testValidStartup() {
			final ByteArrayOutputStream bos = new ByteArrayOutputStream();
			final ByteArrayOutputStream bes = new ByteArrayOutputStream();

			final PrintStream out = new PrintStream(bos);
			final PrintStream err = new PrintStream(bes);
			final InputStream in = new ByteArrayInputStream(new byte[] {});

			bos.reset();
			bes.reset();
			final String[] args = new String[2];
			args[0] = "../org.mwc.asset.legacy/src/ASSET/Util/MonteCarlo/test_variance_scenario.xml";
			args[1] = "../org.mwc.asset.legacy/src/ASSET/Util/MonteCarlo/test_variance1.xml";
			// args[1] =
			// "..\\src\\java\\ASSET_SRC\\ASSET\\Util\\MonteCarlo\\test_variance1.xml";
			final MultiScenarioCommandLine scen = new MultiScenarioCommandLine();
			int res = 0;
			try {
				res = scen.processThis(args, out, err, in);
			} catch (final XPathExpressionException e) {
				e.printStackTrace();
			}
			assertEquals("ran ok", SUCCESS, res);

			// check the contents of the error message
			assertEquals("no error reported", 0, bes.size());

			// check the scenarios got created
			final Vector<Document> scenarios = scen._myScenarios;
			assertEquals("scenarios got created", 3, scenarios.size());
		}
	}

	/**
	 * success code to prove it ran ok
	 */
	static int SUCCESS = 0;

	/**
	 * error code to return when we've rx the wrong parameters
	 */
	static int WRONG_PARAMETERS = 1;

	/**
	 * error code to return when we can't load our data
	 */
	static int PROBLEM_LOADING = 2;

	/**
	 * error code to indicate we couldn't find one of the files
	 */
	static int FILE_NOT_FOUND = 3;

	/**
	 * error code to indicate we couldn't create the output files
	 */
	static int TROUBLE_MAKING_FILES = 4;

	/**
	 * main method, of course - decides whether to handle this ourselves, or to pass
	 * it on to the command line
	 *
	 * @param args
	 */
	public static void main(final String[] args) {
		final MultiServerTest tm = new MultiServerTest("me");
		SupportTesting.callTestMethods(tm);
	}

	/**
	 * the scenario generator that does all the work
	 */
	private ScenarioGenerator _myGenny;

	/**
	 * the set of scenarios we're going to run through
	 */
	protected Vector<Document> _myScenarios;

	/**
	 * member method, effectively to handle "main" processing.
	 *
	 * @param args the arguments we received from the command line
	 * @param out  standard out
	 * @param err  error out
	 * @param in   input (to receive user input)
	 * @return success code (0) or failure codes
	 * @throws XPathExpressionException
	 */

	int processThis(final String[] args, final PrintStream out, final PrintStream err, final InputStream in)
			throws XPathExpressionException {
		int resCode = 0;

		if (args == null) {
			err.println("Usage:");
			err.println("    asset scenario_file control_file");
			resCode = WRONG_PARAMETERS;
		} else {
			if (args.length != 2) {
				if (args.length == 1) {
					err.println("Sorry, both a scenario file and command file are required");
					err.println("");
				}

				err.println("Usage:");
				err.println("    asset scenario_file control_file");
				resCode = WRONG_PARAMETERS;
			} else {
				System.out.println("about to generate scenarios");

				// and set it up (including generating the scenarios)
				final String res = setup(args[0].trim(), args[1].trim(), null);

				if (res != null) {
					// see what it was, file not found?
					if (res.indexOf("not found") >= 0) {
						err.println("Problem finding control file:" + res);
						resCode = FILE_NOT_FOUND;
					} else {
						err.println("Problem loading multi-scenario generator:" + res);
						resCode = PROBLEM_LOADING;
					}
				} else {
					System.out.println("about to write new scenarios to disk");

					// ok, now write the scenarios to disk
					resCode = writeToDisk(out, err, in);

					// remember the control file
					final Document controlFileSafeCopy = _myGenny.getControlFile();

					// and let our generator ditch some gash
					_myGenny = null;

					// there was lots of stuff read in by the scenario generator. Whilst we've
					// removed our
					// only reference to
					// it on the previous line, the system won't necessarily do a GC just yet - so
					// we'll
					// trigger an artificial one.
					System.gc();

					if (resCode == SUCCESS) {
						// all is well, get it to run..
						System.out.println("about to run through new scenarios");

						resCode = run(out, err, in, controlFileSafeCopy);
					} else {
						if (resCode == TROUBLE_MAKING_FILES) {
							System.err.println(
									"Failed to write new scenarios to disk.  Is an old copy of an output file currently open?");
							System.err.println(
									"  Alternately, is a file-browser currently looking at the output directory?");
						}
					}
				}
			}
		}

		return resCode;
	}

	/**
	 * ok, let's get going...
	 *
	 * @param out
	 * @param err
	 */
	private int run(final OutputStream out, final OutputStream err, final InputStream in, final Document controlFile) {
		final int result = SUCCESS;

		// convert the control file to a stream
		final String controlStr = ScenarioGenerator.writeToString(controlFile);
		final InputStream controlStream = new ByteArrayInputStream(controlStr.getBytes());

		System.out.println("about to import Control file");

		final ASSETReaderWriter.ResultsContainer results = ASSETReaderWriter.importThisControlFile(null, controlStream);

		// find out if we have any intra-scenario observers
		final Vector<InterScenarioObserverType> theInterObservers = new Vector<InterScenarioObserverType>(0, 1);

		final Vector<ScenarioObserver> theObservers = results.observerList;
		for (int i = 0; i < theObservers.size(); i++) {
			final ScenarioObserver observer = theObservers.elementAt(i);
			if (observer instanceof InterScenarioObserverType) {
				theInterObservers.add((InterScenarioObserverType) observer);
			}
		}

		// ok, everything's loaded. Just have a pass through to
		// initialise any intra-scenario observers
		for (int thisObs = 0; thisObs < theInterObservers.size(); thisObs++) {
			final ScenarioObserver scen = theInterObservers.elementAt(thisObs);
			if (scen.isActive()) {
				final InterScenarioObserverType obs = (InterScenarioObserverType) scen;
				// is it active?
				obs.initialise(results.outputDirectory);
			}
		}

		final int scenarioLen = _myScenarios.size();

		// ok, we've got our scenarios up and running, might as well run through them
		for (int i = 0; i < scenarioLen; i++) {
			// get the scenario
			final Document document = _myScenarios.elementAt(i);

			// ok, put it into a stream
			final String scenarioStr = ScenarioGenerator.writeToString(document);
			final InputStream scenarioStream = new ByteArrayInputStream(scenarioStr.getBytes());

			final File newOutputSubDirectory = new File(results.outputDirectory, "" + (i + 1) + File.separator);

			// and run through this one
			runThisOne(controlStream, scenarioStream, results.observerList, newOutputSubDirectory, results.randomSeed,
					i, scenarioLen);

			try {
				// and reset the control stream
				controlStream.reset();
			} catch (final IOException e) {
				e.printStackTrace(); // To change body of catch statement use Options | File Templates.
			}
		}

		// ok, everything's loaded. Just have a pass through to
		// close any intra-scenario observers
		for (int thisObs = 0; thisObs < theInterObservers.size(); thisObs++) {
			final ScenarioObserver scen = theInterObservers.elementAt(thisObs);
			if (scen.isActive()) {
				final InterScenarioObserverType obs = theInterObservers.elementAt(thisObs);
				obs.finish();
			}
		}

		return result;
	}

	/**
	 * run through a single scenario - using the ASSET command line runner
	 *
	 * @param controlStream   the stream containing the control file
	 * @param scenarioStream  the stream containing the scenario
	 * @param theObservers    and observers to setup
	 * @param outputDirectory the output directory to dump into
	 * @param theSeed         a seed for this scenario
	 * @param thisIndex       a counter running through the scenarios
	 * @param numScenarios    the total number of scenarios
	 */
	private void runThisOne(final InputStream controlStream, final InputStream scenarioStream,
			final Vector<ScenarioObserver> theObservers, final File outputDirectory, final Integer theSeed,
			final int thisIndex, final int numScenarios) {
		// load the data
		final CommandLine runner = new CommandLine();

		// load the scenario
		// ok, read in the scenario
		final CoreScenario theScenario = runner.getScenario();
		ASSET.Util.XML.ASSETReaderWriter.importThis(theScenario, null, scenarioStream);

		// now set the seed
		theScenario.setSeed(theSeed);

		// ok, get the scenario, so we can set up our observers
		for (int i = 0; i < theObservers.size(); i++) {
			final CoreObserver thisObs = (CoreObserver) theObservers.elementAt(i);

			// is it file-related?
			if (thisObs instanceof RecordToFileObserverType) {
				final RecordToFileObserverType rec = (RecordToFileObserverType) thisObs;
				rec.setDirectory(outputDirectory);
			}

			// and set it up
			thisObs.setup(runner.getScenario());

			// and add to the runner
			runner.addObserver(thisObs);
		}

		System.out.print("Run " + (thisIndex + 1) + " of " + numScenarios + " ");

		// and get going....
		runner.run();

		// and remove the observers
		runner.clearObservers();
	}

	/**
	 * ok, get things up and running. Load the data-files
	 *
	 * @param scenario        the scenario file
	 * @param control         the control file
	 * @param outputDirectory TODO
	 * @return null for success, message for failure
	 * @throws XPathExpressionException
	 */
	private String setup(final String scenario, final String control, final File outputDirectory)
			throws XPathExpressionException {
		// ok, create our genny
		_myGenny = new ScenarioGenerator();

		// now create somewhere for the scenarios to go
		_myScenarios = new Vector<Document>(0, 1);

		// and now create the list of scenarios
		final String res = _myGenny.createScenarios(scenario, control, _myScenarios, null, outputDirectory);

		return res;
	}

	////////////////////////////////////////////////////////////
	// and now the main method
	////////////////////////////////////////////////////////////

	/**
	 * write this set of scenarios to disk, for later examination
	 *
	 * @param out standard out
	 * @param err error out
	 * @param in  input (to receive user input)
	 * @return success code (0) or failure codes
	 */
	private int writeToDisk(final PrintStream out, final PrintStream err, final InputStream in) {
		int res = 0;
		// so,
		try {
			final String failure = _myGenny.writeTheseToFile(_myScenarios, false);
			// just check for any other probs
			if (failure != null) {
				res = TROUBLE_MAKING_FILES;
			}
		} catch (final Exception e) {
			res = TROUBLE_MAKING_FILES;
		}

		return res;
	}

}
