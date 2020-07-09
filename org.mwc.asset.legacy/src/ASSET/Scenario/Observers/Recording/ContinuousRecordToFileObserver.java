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

package ASSET.Scenario.Observers.Recording;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import ASSET.ScenarioType;
import ASSET.Scenario.CoreScenario;
import ASSET.Scenario.Observers.RecordToFileObserverType;
import ASSET.Util.SupportTesting;

/**
 * Created by IntelliJ IDEA. User: Ian.Mayo Date: 12-Aug-2004 Time: 08:39:25 To
 * change this template use File | Settings | File Templates.
 */
public abstract class ContinuousRecordToFileObserver extends RecordToFileObserverType {
	//////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	//////////////////////////////////////////////////////////////////////////////////////////////////
	public static class RecToFileTest extends SupportTesting {
		boolean _listenersRemoved = false;
		boolean _listenersAdded = false;
		boolean _closeProcessingPerformed = false;
		boolean _setupProcessingPerformed = false;
		boolean _headerInfoSent = false;
		// private boolean _newNameCalled;
		// private boolean _getSuffixCalled;

		protected String dir_name = "test_reports";

		protected String file_name = "cont_observer";

		public RecToFileTest(final String val) {
			super(val);
		}

		//////////////////////////////////////////////////
		// utility method to create an observer - over-ridden in instantiated classes
		//////////////////////////////////////////////////
		protected ContinuousRecordToFileObserver getObserver(final boolean isActive) {
			return new ContinuousRecordToFileObserver(dir_name, file_name, "cont observer", isActive) {
				@Override
				protected void addListeners(final ScenarioType scenario) {
					_listenersAdded = true;
				}

				@Override
				protected EditorType createEditor() {
					return null; // To change body of implemented methods use File | Settings | File
									// Templates.
				}

				@Override
				protected String getMySuffix() {
					// _getSuffixCalled = true;
					return "tmp"; // To change body of implemented methods use File | Settings | File
									// Templates.
				}

				@Override
				protected String newName(final String name) {
					// _newNameCalled = true;
					return "new_name"; // To change body of implemented methods use File | Settings | File
										// Templates.
				}

				@Override
				protected void performCloseProcessing(final ScenarioType scenario) {
					super.performCloseProcessing(scenario); // To change body of overridden methods use File |
															// Settings | File Templates.

					_closeProcessingPerformed = true;
				}

				@Override
				protected void performSetupProcessing(final ScenarioType scenario) {
					super.performSetupProcessing(scenario); // To change body of overridden methods use File |
															// Settings | File Templates.

					_setupProcessingPerformed = true;
				}

				@Override
				protected void removeListeners(final ScenarioType scenario) {
					_listenersRemoved = true;
				}

				@Override
				protected void writeFileHeaderInformation(final FileWriter destination, final String fileName)
						throws IOException {

					_headerInfoSent = true;
				}
			};
		}

		public final void testActiveRecorder() {

			final ContinuousRecordToFileObserver observer = getObserver(true);
			assertNotNull("observer wasn't created", observer);

			// and the scenario
			final CoreScenario cs = new CoreScenario();
			cs.setName("testing scenario output");

			_listenersAdded = false;
			_listenersRemoved = false;
			_setupProcessingPerformed = false;
			_closeProcessingPerformed = false;
			_headerInfoSent = false;
			// _newNameCalled = false;
			// _getSuffixCalled = false;

			// and do the setup
			observer.setup(cs);

			// sort out the data file
			observer.createOutputFile();

			assertTrue("listeners weren't added", _listenersAdded);
			assertNotNull("output file wasn't created", observer._os);
			assertTrue("pre-processing conducetd", _setupProcessingPerformed);
			assertTrue("header info wasn't sent", _headerInfoSent);
			assertTrue("new name wasn't sent", _headerInfoSent);
			assertTrue("get suffix wasn't sent", _headerInfoSent);

			// and the close
			observer.tearDown(cs);

			assertTrue("listeners weren't removed", _listenersRemoved);
			assertTrue("post-processing conducetd", _closeProcessingPerformed);
			assertNull("output stream close", observer._os);

		}

		public final void testInActiveRecorder() {

			// File theOutputFile = null;

			final ContinuousRecordToFileObserver observer = getObserver(false);
			assertNotNull("observer wasn't created", observer);

			// and the scenario
			final CoreScenario cs = new CoreScenario();
			cs.setName("testing scenario output");

			// initialise our results monitors
			_listenersAdded = false;
			_listenersRemoved = false;
			_setupProcessingPerformed = false;
			_closeProcessingPerformed = false;
			_headerInfoSent = false;
			// _newNameCalled = false;
			// _getSuffixCalled = false;

			// and do the setup
			observer.setup(cs);

			assertFalse("listeners weren't added", _listenersAdded);
			assertNull("output file wasn't created", observer._os);
			assertFalse("pre-processing conducetd", _setupProcessingPerformed);
			assertFalse("header info wasn't sent", _headerInfoSent);
			assertFalse("new name wasn't sent", _headerInfoSent);
			assertFalse("get suffix wasn't sent", _headerInfoSent);

			// and the close
			observer.tearDown(cs);

			assertFalse("listeners weren't removed", _listenersRemoved);
			assertFalse("post-processing conducetd", _closeProcessingPerformed);
			assertNull("output stream close", observer._os);

		}
	}

	/**
	 * the stream we are currently outputting to
	 */
	protected java.io.FileWriter _os;

	//////////////////////////////////////////////////
	// setup/close processing
	//////////////////////////////////////////////////

	//////////////////////////////////////////////////
	// constructor
	//////////////////////////////////////////////////
	public ContinuousRecordToFileObserver(final String directoryName, final String fileName, final String observerName,
			final boolean isActive) {
		super(directoryName, fileName, observerName, isActive);
	}

	/**
	 * ok, close the stream
	 */
	protected void closeTheFile() {
		writeTheFooter();

		if (_os != null) {
			try {
				// close the stream
				_os.close();
			} catch (final IOException e) {
				MWC.Utilities.Errors.Trace.trace(e, "Closing logging file");
			}
		}

		// reset data
		_os = null;
	}

	//////////////////////////////////////////////////
	// file management
	//////////////////////////////////////////////////

	/**
	 * ok, create the output file
	 */
	protected void createOutputFile() {
		// create a new output file
		try {
			_os = createOutputFileWriter(_myScenario);

			writeFileHeaderInformation(_os, _myScenario.getName());

			// don't forget to flush!
			_os.flush();

		} catch (final FileNotFoundException fe) {
			MWC.GUI.Dialogs.DialogFactory.showMessage("Record to File",
					"Sorry, unable to create file at:" + getDirectory() + "\\" + getFileName());
			fe.printStackTrace();
		} catch (final IOException e) {
			MWC.GUI.Dialogs.DialogFactory.showMessage("Record to File",
					"Sorry, unable to create file at:" + getDirectory() + "\\" + getFileName());
			e.printStackTrace();
		}
	}

	/**
	 * right, the scenario is about to close. We haven't removed the listeners or
	 * forgotten the scenario (yet).
	 *
	 * @param scenario the scenario we're closing from
	 */
	@Override
	protected void performCloseProcessing(final ScenarioType scenario) {
		// close the file, we've finished writing to it.
		closeTheFile();
	}

	/**
	 * we're getting up and running. The observers have been created and we've
	 * remembered the scenario
	 *
	 * @param scenario the new scenario we're looking at
	 */
	@Override
	protected void performSetupProcessing(final ScenarioType scenario) {
		// ok, we don't create the output file here,
		// we defer output file creation until the first step
	}

	/**
	 * the file has been opened, write the header details to it
	 *
	 * @param destination the file to write to
	 * @param fileName    the filename of the file
	 * @throws IOException
	 */
	abstract protected void writeFileHeaderInformation(FileWriter destination, String fileName) throws IOException;

	/**
	 * write any extra detail to the file
	 *
	 */
	protected void writeTheFooter() {

	}

}
