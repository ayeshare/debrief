
package ASSET.GUI.Editors;

import java.awt.BorderLayout;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.SwingConstants;

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

import ASSET.ParticipantType;
import ASSET.ScenarioType;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Sensor.SensorDataProvider;
import ASSET.Models.Sensor.Initial.BroadbandSensor;
import ASSET.Participants.CoreParticipant;
import ASSET.Participants.ParticipantDetectedListener;

class DetectionViewer extends MWC.GUI.Properties.Swing.SwingCustomEditor
		implements MWC.GUI.Properties.NoEditorButtons, ASSET.Participants.ParticipantDetectedListener {
	////////////////////////////////////////////////////
	// member objects
	////////////////////////////////////////////////////

	//////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	//////////////////////////////////////////////////////////////////////////////////////////////////
	public static class ViewerTest extends junit.framework.TestCase {
		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		boolean set = false;

		public ViewerTest(final String val) {
			super(val);
		}

		public void testMe() {
			// create the object
			final DetectionViewer dv = new DetectionViewer();

			set = false;
			final ParticipantType cp = new CoreParticipant(12) {
				/**
				 *
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void addParticipantDetectedListener(final ParticipantDetectedListener list) {
					super.addParticipantDetectedListener(list);
					if (list instanceof DetectionViewer) {
						set = true;
					}
				}
			};

			dv.setObject(cp);

			assertTrue("has been set as detected listener", set == true);

			assertEquals("list starts off being empty", dv.detList.getModel().getSize(), 0);

			// try setting the list
			dv.newDetections(null);
			assertEquals("we can set null data", dv.detList.getModel().getSize(), 0);

			final DetectionList dl = new DetectionList();
			dv.newDetections(dl);
			assertEquals("we can set zero length data", dv.detList.getModel().getSize(), 0);

			final BroadbandSensor bb = new BroadbandSensor(122);
			final ParticipantType target = new ASSET.Models.Vessels.Surface(12);
			target.setName("surfare target");
			dl.add(new DetectionEvent(0, cp.getId(), null, bb, null, null, null, null, null, null, null, null, target));
			dv.newDetections(dl);
			assertEquals("we can set  data", dv.detList.getModel().getSize(), 1);

			// check restart
			dv.restart(null);
			assertEquals("we can do restart", dv.detList.getModel().getSize(), 0);

			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			System.out.println("POSSIBLE JUNIT PROBLEM - THIS THREAD (DetectionViewer) SHOULD DIE");

			// close down
			dv.doClose();

		}
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * GUI components
	 */
	private final BorderLayout mainBorder = new BorderLayout();
	private final JLabel detectionLabel = new JLabel();

	JList detList = new JList();

	/**
	 * the vessel we listen to
	 */
	private ASSET.Models.Sensor.SensorDataProvider _sensorProvider;

	/**
	 * any pending detections we have made - this allows us to retrieve any existing
	 * detections when we first open, then plot them the first time we do a redraw
	 */
	DetectionList _pendingDetections = null;

	//////////////////////////////////////////////////
	// CONSTRUCTOR
	//////////////////////////////////////////////////

	/**
	 * keep our own list of indices used to represent target held
	 */
	private Vector<String> _datasetIndices;

	/**
	 * constructor
	 */
	public DetectionViewer() {
	}

	/**
	 * build the interface
	 */
	void buildGUI() {
		detectionLabel.setHorizontalAlignment(SwingConstants.CENTER);
		detectionLabel.setText("Detections");
		this.setLayout(mainBorder);
		this.add(detectionLabel, BorderLayout.NORTH);
		this.add(detList, BorderLayout.CENTER);
	}

	/**
	 * the scenario has moved forward, clear the GUI
	 */
	void clearGUI() {
		detList.removeAll();
	}

	/**
	 * we are closing, ditch the components
	 */
	void closeGUI() {
		detList.removeAll();
	}

	/**
	 * handle close event
	 */
	@Override
	public void doClose() {
		// do our bit
		_sensorProvider.removeParticipantDetectedListener(this);

		closeGUI();

		// do the parent bit
		super.doClose();
	}

	/**
	 * for each combination of dataset & target we want to create a unique dataset
	 * number so they are nicely colour coded. Create a hashing code of the two
	 * components, put it into a Vector, and use the vector index as the dataset id
	 *
	 * @param sensorId the id of the sensor for this detection
	 * @param targetId the id of the subject target of this detection
	 * @return unique index for this permutation
	 */
	protected int getDataSetIndexFor(final int sensorId, final int targetId) {
		int res = 0;

		// check we've got our list
		if (_datasetIndices == null)
			_datasetIndices = new Vector<String>(0, 1);

		// create hash-code representing this data item
		final String code = "" + sensorId + " " + targetId;

		// do we hold it?
		if (!_datasetIndices.contains(code)) {
			// nope, better add it
			_datasetIndices.add(code);
		}

		// cool, it must be there now
		res = _datasetIndices.indexOf(code);

		return res;
	}

	/**
	 * pass on the list of new detections
	 */
	@Override
	public void newDetections(final DetectionList detections) {
		// and update the data
		updateList(detections);
	}

	/**
	 * the scenario has restarted
	 */
	@Override
	public void restart(final ScenarioType scenario) {
		newDetections(null);
	}

	/**
	 * the editor is telling us what we are viewing
	 *
	 * @param data the vessel to monitor
	 */
	@Override
	public void setObject(final Object data) {
		// this is our vessel, start listening to it
		if (data instanceof SensorDataProvider) {
			_sensorProvider = (SensorDataProvider) data;
			_sensorProvider.addParticipantDetectedListener(this);

			// also get a copy of the vessel's existing detections, and view them
			_pendingDetections = _sensorProvider.getAllDetections();
		}
	}

	/**
	 * the detections are updated, update our list
	 *
	 * @param vec the new detections
	 */
	void updateGUI(final Vector<DetectionEvent> vec) {
		// set the data, whether it's empty or not
		detList.setListData(vec);
	}

	/**
	 * have a look for new detections
	 *
	 * @param list the list of new detections from the vessel
	 */
	protected final void updateList(final ASSET.Models.Detection.DetectionList list) {
		// clear the existing list
		clearGUI();

		// my working list
		final java.util.Vector<DetectionEvent> vec = new java.util.Vector<DetectionEvent>();

		// do we have any pending detections?
		if (_pendingDetections != null) {
			for (int i = 0; i < _pendingDetections.size(); i++) {
				final DetectionEvent event = _pendingDetections.elementAt(i);
				vec.add(event);
			}

			// finally clear the list
			_pendingDetections = null;
		}

		// now check we have received some data
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				final DetectionEvent event = list.elementAt(i);
				vec.add(event);
			}
		}

		// and inform the GUI about this new data
		updateGUI(vec);
	}

}