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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Vector;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;

import ASSET.ScenarioType;
import ASSET.GUI.CommandLine.CommandLine.ASSETProgressMonitor;
import ASSET.Scenario.CoreScenario;
import ASSET.Scenario.LiveScenario.ISimulation;
import ASSET.Scenario.LiveScenario.ISimulationQue;
import ASSET.Scenario.Observers.CoreObserver;
import ASSET.Scenario.Observers.InterScenarioObserverType;
import ASSET.Scenario.Observers.RecordToFileObserverType;
import ASSET.Scenario.Observers.ScenarioObserver;
import ASSET.Scenario.Observers.ScenarioStatusObserver;
import ASSET.Scenario.Observers.TimeObserver;
import ASSET.Util.SupportTesting;
import ASSET.Util.MonteCarlo.ScenarioGenerator;
import ASSET.Util.XML.ASSETReaderWriter;
import ASSET.Util.XML.ASSETReaderWriter.ResultsContainer;
import MWC.Algorithms.LiveData.IAttribute;

/**
 * Created by IntelliJ IDEA. User: Ian.Mayo Date: 02-Jun-2003 Time: 15:05:23
 * Class providing multi scenario support to the command line class Log:
 */

public class MultiScenarioCore implements ISimulationQue {
	public static class InstanceWrapper {
		final ScenarioType scenario;
		final CommandLine commandLine;
		boolean _initialised = false;

		public InstanceWrapper(final ScenarioType theScenario, final CommandLine theCommandLine) {
			scenario = theScenario;
			commandLine = theCommandLine;
		}

		public ScenarioType getScenario() {
			return scenario;
		}

		public void initialise(final Vector<ScenarioObserver> allObservers, final File outputDirectory) {

			// ok, get the scenario, so we can set up our observers
			for (int i = 0; i < allObservers.size(); i++) {
				final CoreObserver thisObs = (CoreObserver) allObservers.elementAt(i);

				// ok, do we need to tell it about the directory?
				if (thisObs instanceof RecordToFileObserverType) {
					if (outputDirectory != null) {
						final RecordToFileObserverType rec = (RecordToFileObserverType) thisObs;
						rec.setDirectory(outputDirectory);
					}
				}

				// and set it up
				thisObs.setup(scenario);

				// and add to the runner
				commandLine.addObserver(thisObs);
			}

			_initialised = true;
		}

		public boolean isInitialised() {
			return _initialised;
		}

		public void terminate(final Vector<ScenarioObserver> allObservers) {

			// ok, get the scenario, so we can set up our observers
			for (int i = 0; i < allObservers.size(); i++) {
				final CoreObserver thisObs = (CoreObserver) allObservers.elementAt(i);

				// and tear it down
				thisObs.tearDown(scenario);
			}

			// and remove all the observers
			commandLine.clearObservers();

			_initialised = false;
		}
	}

	// //////////////////////////////////////////////////////////
	// testing stuff
	// //////////////////////////////////////////////////////////
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

		public void testValidStartup() {
			final ByteArrayOutputStream bos = new ByteArrayOutputStream();
			final ByteArrayOutputStream bes = new ByteArrayOutputStream();

			final PrintStream out = new PrintStream(bos);
			final PrintStream err = new PrintStream(bes);
			final InputStream in = new ByteArrayInputStream(new byte[] {});

			bos.reset();
			bes.reset();
			final String[] args = new String[2];
			args[1] = "../org.mwc.asset.legacy/src/ASSET/Util/MonteCarlo/test_variance_scenario.xml";
			args[0] = "../org.mwc.asset.legacy/src/ASSET/Util/MonteCarlo/test_variance1.xml";
			// args[1] =
			// "..\\src\\java\\ASSET_SRC\\ASSET\\Util\\MonteCarlo\\test_variance1.xml";
			final MultiScenarioCore scen = new MultiScenarioCore();
			final ASSETProgressMonitor pMon = new ASSETProgressMonitor() {
				@Override
				public void beginTask(final String name, final int totalWork) {
				}

				@Override
				public void worked(final int work) {
				}
			};
			int res = 0;
			try {
				res = scen.prepareFiles(args[0], args[1], out, err, in, pMon, null);
			} catch (final XPathExpressionException e) {
				e.printStackTrace();
			}
			assertEquals("ran ok", SUCCESS, res);

			// check the contents of the error message
			assertEquals("no error reported", 0, bes.size());

			// check the scenarios got created
			final Vector<Document> scenarios = scen._myScenarioDocuments;
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
	protected Vector<Document> _myScenarioDocuments;

	private Vector<InterScenarioObserverType> _theInterObservers;

	private Vector<ScenarioObserver> _thePlainObservers;

	private Vector<ScenarioObserver> _allObservers;

	private ResultsContainer _resultsStore;

	private Vector<InstanceWrapper> _theScenarios;

	private Vector<IAttribute> _myAttributes;

	private ScenarioStatusObserver _stateObserver;

	@Override
	public Vector<IAttribute> getAttributes() {
		if (_myAttributes == null) {
			// look at our observers, find any attributes
			_myAttributes = new Vector<IAttribute>();

			// start off with the single-scenario observers
			for (final Iterator<ScenarioObserver> iterator = _thePlainObservers.iterator(); iterator.hasNext();) {
				final ScenarioObserver thisS = iterator.next();
				if (thisS instanceof IAttribute)
					_myAttributes.add((IAttribute) thisS);
			}

			// now the multi-scenario observers
			for (final Iterator<InterScenarioObserverType> iterator = _theInterObservers.iterator(); iterator
					.hasNext();) {
				final InterScenarioObserverType thisS = iterator.next();
				if (thisS instanceof IAttribute)
					_myAttributes.add((IAttribute) thisS);
			}

		}
		// done.
		return _myAttributes;
	}

	public Vector<ScenarioObserver> getObservers() {
		return _allObservers;
	}

	public Vector<InstanceWrapper> getScenarios() {
		return _theScenarios;
	}

	@Override
	public Vector<ISimulation> getSimulations() {
		final Vector<ISimulation> res = new Vector<ISimulation>();
		for (final Iterator<InstanceWrapper> iter = _theScenarios.iterator(); iter.hasNext();)
			res.add((ISimulation) iter.next().scenario);
		// return my list of simulations
		return res;
	}

	@Override
	public IAttribute getState() {
		return _stateObserver;
	}

	public InstanceWrapper getWrapperFor(final ScenarioType scenario) {
		InstanceWrapper res = null;
		final Iterator<InstanceWrapper> sList = _theScenarios.iterator();
		while (sList.hasNext()) {
			final InstanceWrapper thisWrap = sList.next();
			if (thisWrap.scenario == scenario) {
				res = thisWrap;
				break;
			}
		}
		return res;
	}

	// //////////////////////////////////////////////////////////
	// and now the main method
	// //////////////////////////////////////////////////////////

	public boolean isMultiScenario(final String controlFile) throws FileNotFoundException {
		return CommandLine.isMultiScenarioFile(controlFile);
	}

	@Override
	public boolean isRunning() {
		return false;
	}

	@Override
	public int nowRun(final PrintStream out, final PrintStream err, final InputStream in,
			final NewScenarioListener scenarioListener) {
		return runAll(out, err, in, _myGenny.getControlFile(), scenarioListener);
	}

	public int prepareControllers(final ResultsContainer multiRunResultsStore, final ASSETProgressMonitor pMon,
			final NewScenarioListener newScenarioListener) {
		final int resCode = 0;

		_resultsStore = multiRunResultsStore;

		// sort out observers (inter & intra)
		_theInterObservers = new Vector<InterScenarioObserverType>(0, 1);
		_thePlainObservers = new Vector<ScenarioObserver>();

		// start off by generating the time/state observers that we create for
		// everybody
		_stateObserver = new ScenarioStatusObserver();
		_thePlainObservers.add(_stateObserver);
		_thePlainObservers.add(new TimeObserver());

		// also add those from the file
		final Vector<ScenarioObserver> theObservers = _resultsStore.observerList;
		for (int i = 0; i < theObservers.size(); i++) {
			final ScenarioObserver observer = theObservers.elementAt(i);
			if (observer instanceof InterScenarioObserverType) {
				_theInterObservers.add((InterScenarioObserverType) observer);
			} else
				_thePlainObservers.add(observer);
		}

		// also collate the collected set of observers
		// combine the two sets of observers
		_allObservers = new Vector<ScenarioObserver>();
		_allObservers.addAll(_theInterObservers);
		_allObservers.addAll(_thePlainObservers);

		// also read in the collection of scenarios
		_theScenarios = new Vector<InstanceWrapper>(0, 1);

		pMon.beginTask("Reading in block of scenarios", _myScenarioDocuments.size());

		for (final Iterator<Document> iterator = _myScenarioDocuments.iterator(); iterator.hasNext();) {
			final Document thisD = iterator.next();
			final String scenarioStr = ScenarioGenerator.writeToString(thisD);
			final InputStream scenarioStream = new ByteArrayInputStream(scenarioStr.getBytes());
			final CoreScenario newS = new CoreScenario();
			ASSETReaderWriter.importThis(newS, null, scenarioStream);
			// wrap the scenario
			final CommandLine runner = new CommandLine(newS);

			final InstanceWrapper wrapper = new InstanceWrapper(newS, runner);

			_theScenarios.add(wrapper);
			pMon.worked(1);
		}

		// ok, everything's loaded. Just have a pass through to
		// initialise any intra-scenario observers
		for (int thisObs = 0; thisObs < _theInterObservers.size(); thisObs++) {
			final ScenarioObserver scen = _theInterObservers.elementAt(thisObs);
			if (scen.isActive()) {
				final InterScenarioObserverType obs = (InterScenarioObserverType) scen;
				// is it active?
				obs.initialise(_resultsStore.outputDirectory);
			}
		}

		// tell the parent we've got a new scenario
		if (newScenarioListener != null)
			newScenarioListener.newScenario(null, _theScenarios.firstElement().scenario);

		return resCode;
	}

	/**
	 * member method, effectively to handle "main" processing.
	 *
	 * @param args            the arguments we received from the command line
	 * @param out             standard out
	 * @param err             error out
	 * @param in              input (to receive user input)
	 * @param pMon
	 * @param outputDirectory - where to put the working files
	 * @return success code (0) or failure codes
	 * @throws XPathExpressionException
	 */

	public int prepareFiles(final String controlFile, final String scenarioFile, final PrintStream out,
			final PrintStream err, final InputStream in, final ASSETProgressMonitor pMon, final File outputDirectory)
			throws XPathExpressionException {
		int resCode = 0;

		// do a little tidying
		_myAttributes = null;
		_theInterObservers = null;
		_thePlainObservers = null;

		System.out.println("about to generate scenarios");

		// and set it up (including generating the scenarios)
		final String res = setup(scenarioFile, controlFile, pMon, outputDirectory);

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
			out.println("about to write new scenarios to disk");

			pMon.beginTask("Writing generated scenarios to disk", 1);

			// ok, now write the scenarios to disk
			resCode = writeToDisk(out, err, in);

			pMon.worked(1);

			// and let our generator ditch some gash
			// _myGenny = null;

			// there was lots of stuff read in by the scenario generator. Whilst
			// we've removed our only reference to
			// it on the previous line, the system won't necessarily do a GC just
			// yet - so we'll trigger an artificial one.
			System.gc();

			if (resCode != SUCCESS) {
				if (resCode == TROUBLE_MAKING_FILES) {
					err.println(
							"Failed to write new scenarios to disk.  Is an old copy of an output file currently open?");
					err.println("  Alternately, is a file-browser currently looking at the output directory?");
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
	 * @param scenarioRunningListener
	 */
	private int runAll(final OutputStream out, final OutputStream err, final InputStream in, final Document controlFile,
			final NewScenarioListener listener) {
		final int result = SUCCESS;

		final int scenarioLen = _myScenarioDocuments.size();

		// get the data we're after
		final String controlStr = ScenarioGenerator.writeToString(_myGenny.getControlFile());
		final InputStream controlStream = new ByteArrayInputStream(controlStr.getBytes());

		// ok, we've got our scenarios up and running, might as well run through
		// them
		int ctr = 0;
		ScenarioType oldScenario = null;
		for (final Iterator<InstanceWrapper> iterator = _theScenarios.iterator(); iterator.hasNext();) {
			final InstanceWrapper wrapper = iterator.next();
			final ScenarioType thisS = wrapper.scenario;

			// tell the listener what's up
			if (listener != null)
				listener.newScenario(oldScenario, thisS);

			// get the observers sorted
			wrapper.initialise(_allObservers, null);

			// now run this one
			final CommandLine runner = wrapper.commandLine;

			System.out.print("Run " + (ctr + 1) + " of " + scenarioLen + " ");

			// now set the seed
			thisS.setSeed(_resultsStore.randomSeed);

			// and get going....
			runner.run();

			// and ditch the observers
			wrapper.terminate(_allObservers);

			try {
				// and reset the control stream
				controlStream.reset();
			} catch (final IOException e) {
				e.printStackTrace(); // To change body of catch statement use Options |
				// File Templates.
			}

			// and remember the scenario
			oldScenario = thisS;

			ctr++;
		}

		// ok, everything's finished running. Just have a pass through to
		// close any i-scenario observers
		for (int thisObs = 0; thisObs < _theInterObservers.size(); thisObs++) {
			final ScenarioObserver scen = _theInterObservers.elementAt(thisObs);
			if (scen.isActive()) {
				final InterScenarioObserverType obs = _theInterObservers.elementAt(thisObs);
				obs.finish();
			}
		}

		return result;
	}

	/**
	 * ok, get things up and running. Load the data-files
	 *
	 * @param scenario        the scenario file
	 * @param control         the control file
	 * @param pMon            who tell what we're up to
	 * @param outputDirectory where to write to
	 * @return null for success, message for failure
	 * @throws XPathExpressionException
	 */
	private String setup(final String scenario, final String control, final ASSETProgressMonitor pMon,
			final File outputDirectory) throws XPathExpressionException {
		// ok, create our genny
		_myGenny = new ScenarioGenerator();

		// now create somewhere for the scenarios to go
		_myScenarioDocuments = new Vector<Document>(0, 1);

		// and now create the list of scenarios
		final String res = _myGenny.createScenarios(scenario, control, _myScenarioDocuments, pMon, outputDirectory);

		return res;
	}

	@Override
	public void startQue(final NewScenarioListener listener) {
		// ok, go for it
		nowRun(System.out, System.err, System.in, listener);
	}

	@Override
	public void stopQue() {
	}

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
			final String failure = _myGenny.writeTheseToFile(_myScenarioDocuments, false);
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
