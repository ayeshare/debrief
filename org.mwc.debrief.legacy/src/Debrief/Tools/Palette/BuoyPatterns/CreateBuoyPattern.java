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

// $RCSfile: CreateBuoyPattern.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.1.1.2 $
// $Log: CreateBuoyPattern.java,v $
// Revision 1.1.1.2  2003/07/21 14:48:50  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.5  2003-03-19 15:37:18+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.4  2003-02-12 16:18:42+00  ian_mayo
// Remove unused imports
//
// Revision 1.3  2003-02-11 08:37:37+00  ian_mayo
// remove unnecessary toda statement
//
// Revision 1.2  2002-05-28 09:25:09+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:11:46+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:28:44+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-01-24 14:23:36+00  administrator
// Reflect change in Layers reformat and modified events which take an indication of which layer has been modified - a step towards per-layer graphics repaints
//
// Revision 1.0  2001-07-17 08:41:15+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-17 13:20:31+00  novatech
// Initial revision
//
// Revision 1.2  2001-01-05 10:32:04+00  novatech
// Finishing off, adding undo functionality
//
// Revision 1.1  2001-01-03 16:02:38+00  novatech
// Initial revision
//

package Debrief.Tools.Palette.BuoyPatterns;

import MWC.GUI.Layers;
import MWC.GUI.ToolParent;
import MWC.GUI.Properties.PropertiesPanel;
import MWC.GUI.Tools.Action;
import MWC.GUI.Tools.PlainTool;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

public final class CreateBuoyPattern extends PlainTool {

	/**
	 * get the actual instance of the shape we are creating
	 *
	 * @return LabelWrapper containing an instance of the new shape
	 * @param centre the current centre of the screen, where the shape should be
	 *               centred
	 */
	// abstract protected LabelWrapper getShape(WorldLocation centre);

	///////////////////////////////////////////////////////
	// store action information
	///////////////////////////////////////////////////////
	protected final class CreateBuoyPatternAction implements Action {
		/**
		 * the panel we are going to show the initial editor in
		 */
		final PropertiesPanel _thePanel1;
		final Layers _theLayers;
		final MWC.GUI.PlainChart _theChart1;
		final BuoyPatternDirector _theDirector1;

		PatternBuilderType _myBuilder;

		public CreateBuoyPatternAction(final PropertiesPanel thePanel, final Layers theLayers,
				final MWC.GUI.PlainChart theChart, final BuoyPatternDirector theDirector) {
			_thePanel1 = thePanel;
			_theChart1 = theChart;
			_theDirector1 = theDirector;
			_theLayers = theLayers;
		}

		/**
		 * make it so!
		 */
		@Override
		public final void execute() {
			// find the centre of the plot
			final WorldArea wa = _theChart1.getDataArea();

			// have we actually got data?
			if (wa == null) {
				// drop out
				return;
			}

			// retrieve the centre of this area
			final WorldLocation centre = wa.getCentre();

			// find out which type of shape we want
			final String selection = getChoice();

			// check that the user has entered something
			if (selection == null) {
				// oh well, just drop out
				return;
			}

			// create the pattern builder, informing it of the Layers object which
			// it is to insert itself into
			_myBuilder = _theDirector1.createBuilder(centre, selection, _thePanel1, _theLayers);

			// pass the pattern builder to the property editor
			_thePanel1.addConstructor(_myBuilder.getInfo(), null);

			// finished.
		}

		/**
		 * specify is this is an operation which can be redone
		 */
		@Override
		public final boolean isRedoable() {
			return true;
		}

		/**
		 * specify is this is an operation which can be undone
		 */
		@Override
		public final boolean isUndoable() {
			return true;
		}

		/**
		 * return string describing this operation
		 *
		 * @return String describing this operation
		 */
		@Override
		public final String toString() {
			return "New BuoyPattern";
		}

		/**
		 * take the shape away from the layer
		 */
		@Override
		public final void undo() {
			// check that we got as far as creating a builder
			if (_myBuilder != null) {
				// get the builder to undo the work
				_myBuilder.undo();
			}
		}
	}

	/**
		 *
		 */
	private static final long serialVersionUID = 1L;

	/////////////////////////////////////////////////////////////
	// member variables
	////////////////////////////////////////////////////////////
	/**
	 * the properties panel
	 */
	private final PropertiesPanel _thePanel;

	/**
	 * the layers we are going to drop this shape into
	 */
	private final Layers _theData;

	/**
	 * the chart we are using (since want our 'duff' item to appear in the middle)
	 */
	private final MWC.GUI.PlainChart _theChart;

	/**
	 * the buoypattern director I work with
	 */
	private final BuoyPatternDirector _theDirector;

	/////////////////////////////////////////////////////////////
	// member functions
	////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////
	// constructor
	////////////////////////////////////////////////////////////
	/**
	 * constructor for label
	 *
	 * @param theParent parent where we can change cursor
	 * @param thePanel  panel
	 */
	public CreateBuoyPattern(final ToolParent theParent, final PropertiesPanel thePanel, final Layers theData,
			final MWC.GUI.PlainChart theChart, final String theName, final String theImage) {
		super(theParent, theName, theImage);

		_thePanel = thePanel;
		_theData = theData;
		_theChart = theChart;

		_theDirector = new BuoyPatternDirector();
	}

	String getChoice() {
		final Object[] opts = _theDirector.getPatterns();
		final String res = (String) javax.swing.JOptionPane.showInputDialog(null, "Which pattern?",
				"Create Buoy Pattern", javax.swing.JOptionPane.QUESTION_MESSAGE, null, opts, null);
		return res;
	}

	@Override
	public final Action getData() {
		return new CreateBuoyPatternAction(_thePanel, _theData, _theChart, _theDirector);
	}

}
