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

package ASSET.GUI.Workbench;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import ASSET.GUI.Core.CoreGUISwing;

public class LoadedWorkbench {
	////////////////////////////////////////////////////
	// member objects
	////////////////////////////////////////////////////
	// static private final String MY_SCENARIO =
	//////////////////////////////////////////////////// "D:\\Dev\\Asset\\src\\test_data\\force_prot_scenario.xml";
	static private final String MY_SCENARIO = "c:\\temp\\andy_tactic\\ssn_run1.xml";

	static private final String MY_OBSERVERS = "c:\\temp\\andy_tactic\\ssn_observers.xml";

	////////////////////////////////////////////////////
	// member constructor
	////////////////////////////////////////////////////

	////////////////////////////////////////////////////
	// member methods
	////////////////////////////////////////////////////

	public static void main(final String[] args) {
		// try{
		// UIManager.setLookAndFeel(new
		// com.incors.plaf.kunststoff.KunststoffLookAndFeel());
		// }catch(UnsupportedLookAndFeelException e)
		// {
		// e.printStackTrace();
		// }

		// create the interface
		final JFrame parent = new JFrame("Loaded session");

		parent.setSize(1100, 600);
		parent.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		// open the splash screen
		CoreGUISwing.showSplash(parent, "images/WorkBenchLogo.gif");

		// create the tool-parent
		final CoreGUISwing.ASSETParent pr = new CoreGUISwing.ASSETParent(parent);

		// create the workbench
		final WorkBenchGUI viewer = new WorkBenchGUI(pr);

		// collate the parent
		parent.getContentPane().setLayout(new BorderLayout());
		parent.getContentPane().add("Center", viewer.getPanel());
		parent.doLayout();

		parent.setVisible(true);

		// finally load the data

		// put the datafile into a vector
		final Vector<File> theScenarios = new Vector<File>();
		theScenarios.add(new File(MY_SCENARIO));

		final Vector<File> theControls = new Vector<File>();
		theControls.add(new File(MY_OBSERVERS));

		// load the data
		try {
			viewer.scenarioDropped(theScenarios);
			viewer.observerDropped(theControls);

		} catch (final FileNotFoundException e) {
			e.printStackTrace(); // To change body of catch statement use File | Settings | File
									// Templates.
		}

		// trigger a fit-to-win
		viewer.FitToWin();

	}

}
