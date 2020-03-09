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

package MWC.GUI.Properties.Swing;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: SwingSteppingBoundedIntegerEditor.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: SwingSteppingBoundedIntegerEditor.java,v $
// Revision 1.2  2004/05/25 15:29:49  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:20  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:27  Ian.Mayo
// Initial import
//
// Revision 1.3  2003-01-09 16:18:54+00  ian_mayo
// Minor tidying
//
// Revision 1.2  2002-05-28 09:25:46+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:35+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:01:28+01  ian_mayo
// Initial revision
//
// Revision 1.2  2001-10-03 16:00:57+01  administrator
// Correct situation where value we are trying to set gets overwritten by setting Min and Max values
//
// Revision 1.1  2001-10-03 10:17:45+01  administrator
// Tidy up, force small steps to be one, so that we can use keyboard accelerators
//
// Revision 1.0  2001-07-17 08:43:33+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:42:40+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:45:46  ianmayo
// initial version
//
// Revision 1.1  2000-10-03 09:39:20+01  ian_mayo
// Initial revision
//
// Revision 1.1  2000-09-26 10:52:31+01  ian_mayo
// Initial revision
//
// Revision 1.3  2000-02-02 14:25:07+00  ian_mayo
// correct package naming
//
// Revision 1.2  1999-11-23 11:05:03+00  ian_mayo
// further introduction of SWING components
//
// Revision 1.1  1999-11-16 16:07:19+00  ian_mayo
// Initial revision
//
// Revision 1.1  1999-11-16 16:02:29+00  ian_mayo
// Initial revision
//
// Revision 1.2  1999-11-11 18:16:09+00  ian_mayo
// new class, now working
//
// Revision 1.1  1999-10-12 15:36:48+01  ian_mayo
// Initial revision
//
// Revision 1.1  1999-08-26 10:05:48+01  administrator
// Initial revision
//

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.WindowConstants;

import MWC.GUI.Properties.SteppingBoundedInteger;

public class SwingSteppingBoundedIntegerEditor extends MWC.GUI.Properties.SteppingBoundedIntegerEditor
		implements javax.swing.event.ChangeListener {
	/////////////////////////////////////////////////////////////
	// member variables
	////////////////////////////////////////////////////////////

	public static void main(final String[] args) {
		final JFrame tester = new JFrame("test stepping editor");
		tester.setSize(300, 300);
		final SwingSteppingBoundedIntegerEditor jr = new SwingSteppingBoundedIntegerEditor();
		final SteppingBoundedInteger sb = new SteppingBoundedInteger(120, 0, 180, 10);
		jr.setValue(sb);
		final java.awt.Component jc = jr.getCustomEditor();
		tester.getContentPane().add("Center", jc);
		tester.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		tester.setVisible(true);
	}

	/**
	 * slider to store the value
	 */
	JSlider _theSlider;

	/**
	 * panel to hold everything
	 */
	JPanel _theHolder;

	/////////////////////////////////////////////////////////////
	// constructor
	////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////
	// member functions
	////////////////////////////////////////////////////////////

	/**
	 * label to show current value
	 */
	JLabel _theCurrent;

	/**
	 * build the editor
	 */
	@Override
	public java.awt.Component getCustomEditor() {
		_theHolder = new JPanel();
		_theHolder.setLayout(new java.awt.GridLayout(1, 0));

		_theCurrent = new JLabel("000");
		_theHolder.add(_theCurrent);

		// create the slider (configure it in the resetData method)
		_theSlider = new JSlider();
		_theSlider.setSize(90, 10);
		_theHolder.add(_theSlider);

		_theSlider.setPaintTicks(true);
		_theSlider.setSnapToTicks(true);

		// and configure the slider
		_theSlider.putClientProperty("JSlider.isFilled", Boolean.FALSE);

		// listen to the slider
		_theSlider.addChangeListener(this);

		resetData();
		return _theHolder;
	}

	/**
	 * put the data into the text fields, if they have been created yet
	 */
	@Override
	public void resetData() {
		if (_theHolder != null) {
			// remember the new value to be set. As we set the sliders, there's a strong
			// chance that this current value will get over-written
			final int newValue = _myVal.getCurrent();

			// put the text into the fields
			_theSlider.setMinimum(_myVal.getMin());
			_theSlider.setMaximum(_myVal.getMax());
			_theSlider.setValue(newValue);
			_theSlider.setMinorTickSpacing(1);
			_theSlider.setMajorTickSpacing(10);

			_theCurrent.setText("" + _myVal.getCurrent());
		}
	}

	@Override
	public void stateChanged(final javax.swing.event.ChangeEvent p1) {
		_myVal.setCurrent(_theSlider.getValue());
		_theCurrent.setText("" + _myVal.getCurrent());
	}
}
