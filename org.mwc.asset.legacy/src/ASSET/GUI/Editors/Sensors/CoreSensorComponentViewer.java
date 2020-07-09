
package ASSET.GUI.Editors.Sensors;

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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.WindowConstants;

import ASSET.Models.SensorType;
import ASSET.Models.Decision.Movement.Wander;
import ASSET.Models.Environment.EnvironmentType;
import ASSET.Models.Sensor.Initial.BroadbandSensor;
import ASSET.Models.Sensor.Initial.InitialSensor;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;

public class CoreSensorComponentViewer extends BaseSensorViewer {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	//////////////////////////////////////////////////////////////////////
	// GUI components
	//////////////////////////////////////////////////////////////////////

	public static void main(final String[] args) {

		// create a movement model
		final Wander wander = new Wander(new WorldLocation(1, 1, 0), new WorldDistance(3000, WorldDistance.YARDS));

		// set up the Ssk
		final ASSET.Models.Vessels.SSK ssk = new ASSET.Models.Vessels.SSK(12);
		final ASSET.Participants.Status sskStat = new ASSET.Participants.Status(12, 0);
		final WorldLocation origin = new WorldLocation(0, 0, 0);
		sskStat.setLocation(origin.add(new WorldVector(0, MWC.Algorithms.Conversions.Nm2Degs(350), 40)));
		sskStat.setSpeed(new WorldSpeed(12, WorldSpeed.M_sec));
		ssk.setStatus(sskStat);
		ssk.setDecisionModel(wander);
		ssk.setName("SSK");

		// ok, setup the ssk radiation
		final ASSET.Models.Mediums.BroadbandRadNoise brn = new ASSET.Models.Mediums.BroadbandRadNoise(134);
		final ASSET.Models.Vessels.Radiated.RadiatedCharacteristics rc = new ASSET.Models.Vessels.Radiated.RadiatedCharacteristics();
		rc.add(EnvironmentType.BROADBAND_PASSIVE, brn);
		ssk.setRadiatedChars(rc);

		// now setup the helo
		final ASSET.Models.Vessels.Helo merlin = new ASSET.Models.Vessels.Helo(33);
		final ASSET.Participants.Status merlinStat = new ASSET.Participants.Status(33, 0);
		merlinStat.setLocation(origin);
		merlin.setStatus(merlinStat);
		merlin.setDecisionModel(wander);
		merlin.setName("Merlin");

		// and it's sensor
		final ASSET.Models.Sensor.SensorList fit = new ASSET.Models.Sensor.SensorList();
		final BroadbandSensor bs = new BroadbandSensor(34);
		fit.add(bs);
		merlin.setSensorFit(fit);

		// now setup the su
		final ASSET.Models.Vessels.Surface ff = new ASSET.Models.Vessels.Surface(31);
		final ASSET.Participants.Status ffStat = new ASSET.Participants.Status(31, 0);
		final WorldLocation sskLocation = ssk.getStatus().getLocation();
		ffStat.setLocation(sskLocation.add(new WorldVector(0, MWC.Algorithms.Conversions.Nm2Degs(4), -40)));
		ff.setStatus(ffStat);

		final ASSET.Models.Sensor.SensorList fit2 = new ASSET.Models.Sensor.SensorList();
		final BroadbandSensor bs2 = new BroadbandSensor(34);
		fit2.add(bs2);
		ff.setSensorFit(fit2);
		final ASSET.Models.Mediums.BroadbandRadNoise ff_brn = new ASSET.Models.Mediums.BroadbandRadNoise(35);
		final ASSET.Models.Vessels.Radiated.RadiatedCharacteristics ff_rc = new ASSET.Models.Vessels.Radiated.RadiatedCharacteristics();
		ff_rc.add(EnvironmentType.BROADBAND_PASSIVE, ff_brn);
		ff.setSelfNoise(ff_rc);
		ff.setDecisionModel(wander);
		ff.setRadiatedChars(rc);
		ff.setName("FF");

		// setup the scenario
		final ASSET.Scenario.CoreScenario cs = new ASSET.Scenario.CoreScenario();
		cs.addParticipant(ff.getId(), ff);
		cs.addParticipant(merlin.getId(), merlin);
		cs.addParticipant(ssk.getId(), ssk);

		// and the viewer!!
		final JFrame viewer = new JFrame();
		viewer.setSize(400, 300);
		viewer.setVisible(true);

		final MWC.GUI.Properties.Swing.SwingPropertiesPanel props = new MWC.GUI.Properties.Swing.SwingPropertiesPanel(
				null, null, null, null);

		props.addEditor(bs.getInfo(), null);

		viewer.getContentPane().setLayout(new BorderLayout());
		viewer.getContentPane().add(props, BorderLayout.CENTER);

		final JButton stepper = new JButton("Step");
		stepper.addActionListener(new java.awt.event.ActionListener() {
			/**
			 * Invoked when an action occurs.
			 */
			@Override
			public void actionPerformed(final ActionEvent e) {
				// move the scenario forward
				cs.step();
			}
		});
		viewer.getContentPane().add(stepper, BorderLayout.SOUTH);

		viewer.doLayout();
		viewer.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

	}

	/**
	 * the table we show our data in
	 *
	 */
	private javax.swing.JTable _myTable;

	/**
	 * the data model for the table
	 *
	 */
	private javax.swing.table.DefaultTableModel _myTableModel;

	////////////////////////////////////////////////////
	// constructor
	////////////////////////////////////////////////////

	/**
	 * the list of column names for our data
	 *
	 */
	private final Vector<String> _cols;

	public CoreSensorComponentViewer() {
		// ok, create the columns
		_cols = new Vector<String>(6, 1);
		_cols.add("Name");
		_cols.add("Loss");
		_cols.add("Bk Noise");
		_cols.add("OS Noise");
		_cols.add("Tgt Noise");
		_cols.add("RD");
		_cols.add("DI");
		_cols.add("SE");
	}

	/**
	 * build the form
	 *
	 */
	@Override
	public void initForm() {

		this.setLayout(new BorderLayout());
		// and the table
		_myTableModel = new javax.swing.table.DefaultTableModel();
		_myTable = new JTable(_myTableModel);

		// format the table
		_myTable.setAutoCreateColumnsFromModel(true);
		_myTable.setRowSelectionAllowed(false);
		_myTable.setColumnSelectionAllowed(false);

		// and store the table
		this.add(_myTable, BorderLayout.CENTER);

		// sort out the table header
		this.add(_myTable.getTableHeader(), BorderLayout.NORTH);
	}

	@Override
	protected void listenTo(final SensorType newSensor) {
		// ok, listen to property changes from this sensor
		_mySensor.addSensorCalculationListener(this);
	}

	////////////////////////////////////////////////////
	// TEST THIS GUI Class
	////////////////////////////////////////////////////

	/**
	 * we've received some new data, update the GUI
	 *
	 */
	@Override
	public void updateForm() {
		// step through the detections, collating them into the vector expected by the
		// table
		final Vector<Vector<String>> theData = new Vector<Vector<String>>(1, 1);
		final Iterator<Object> it = _sensorEvents.iterator();
		while (it.hasNext()) {
			final InitialSensor.InitialSensorComponentsEvent sc = (InitialSensor.InitialSensorComponentsEvent) it
					.next();
			final Vector<String> thisV = new Vector<String>(6, 1);
			thisV.add(sc.getTgtName());
			thisV.add("" + (int) sc.getLoss());
			thisV.add("" + (int) sc.getBkNoise());
			thisV.add("" + (int) sc.getOsNoise());
			thisV.add("" + (int) sc.getTgtNoise());
			thisV.add("" + (int) sc.getRd());
			thisV.add("" + (int) sc.getDi());
			thisV.add("" + (int) sc.getSE());
			theData.add(thisV);
		}

		// and update the model
		_myTableModel.setDataVector(theData, _cols);

	}

}