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

package Debrief.Tools.FilterOperations;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: HideRevealObjects.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.3 $
// $Log: HideRevealObjects.java,v $
// Revision 1.3  2004/11/25 10:24:27  Ian.Mayo
// Switch to Hi Res dates
//
// Revision 1.2  2004/11/22 13:41:01  Ian.Mayo
// Replace old variable name used for stepping through enumeration, since it is now part of language (Jdk1.5)
//
// Revision 1.1.1.2  2003/07/21 14:48:23  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.4  2003-03-25 15:54:14+00  ian_mayo
// Implement "Reset me" buttons
//
// Revision 1.3  2003-03-19 15:37:18+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.2  2003-02-07 09:02:41+00  ian_mayo
// remove unnecessary toda comments
//
// Revision 1.1  2002-09-24 10:55:49+01  ian_mayo
// Initial revision
//

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JOptionPane;

import MWC.GUI.Layers;
import MWC.GUI.Plottable;
import MWC.GUI.Tools.Action;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WatchableList;

/**************************************************************
 * Class which lets user reformat a series of positions in the Track/Time
 * toolbox. Whilst the Toolbox may contain different types of object, this just
 * edits Fixes contained in Tracks
 **************************************************************/

public final class HideRevealObjects implements FilterOperation {

	/*********************************************************************************************
	 * member objects
	 **********************************************************************************************/

	///////////////////////////////////////////////////////
	// store action information
	///////////////////////////////////////////////////////
	final static class HideRevealAction implements Action {
		/**
		 * embedded class to store the changes we make
		 *
		 */
		public final class ItemEdit {
			public final Object _object;
			public final boolean _oldValue;

			public ItemEdit(final Object object, final boolean oldValue) {
				_object = object;
				_oldValue = oldValue;
			}
		} // end of ItemEdit class

		private final Vector<ItemEdit> _valuesChanged;
		private final boolean _hideIt;

		private final Layers _theLayers1;

		public HideRevealAction(final boolean hideIt, final Layers theLayers) {
			_hideIt = hideIt;
			_theLayers1 = theLayers;
			_valuesChanged = new Vector<ItemEdit>(0, 1);
		}

		/**
		 * add an update to a new object
		 *
		 */
		public final void changeThisObject(final Plottable val) {
			final boolean oldVal = val.getVisible();

			// remember the object and it's old value
			final ItemEdit ie = new ItemEdit(val, oldVal);

			// and store it
			_valuesChanged.add(ie);
		}

		/**
		 * make it so!
		 */
		@Override
		public final void execute() {
			final Iterator<ItemEdit> it = _valuesChanged.iterator();
			while (it.hasNext()) {
				final ItemEdit ie = it.next();
				final Plottable theO = (Plottable) ie._object;
				theO.setVisible(!_hideIt);
			}

			_theLayers1.fireReformatted(null);
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
			return "Reformat time values";
		}

		/**
		 * take the shape away from the layer
		 */
		@Override
		public final void undo() {
			final Iterator<ItemEdit> it = _valuesChanged.iterator();
			while (it.hasNext()) {
				final ItemEdit ie = it.next();
				final Plottable theO = (Plottable) ie._object;
				theO.setVisible(ie._oldValue);
			}

			_theLayers1.fireReformatted(null);
		}

	} // end of Action Class

	/**
	 * the selected objects
	 *
	 */
	private Vector<WatchableList> _theObjects = null;

	/**
	 * the set of layers which we will update
	 *
	 */
	private final Layers _theLayers;

	/*********************************************************************************************
	 * constructor
	 **********************************************************************************************/

	/**
	 * the separator we use in the operation description
	 *
	 */
	private final String _theSeparator = System.getProperties().getProperty("line.separator");

	/**
	 * constructor
	 *
	 * @param theLayers the layers object to be updated on our completion
	 */
	public HideRevealObjects(final Layers theLayers) {
		_theLayers = theLayers;
	}

	@Override
	public final void actionPerformed(final java.awt.event.ActionEvent p1) {
	}

	@Override
	public final void close() {
	}

	@Override
	public final void execute() {
	}

	@Override
	public final Action getData() {
		// produce the list of modifications to be made
		HideRevealAction res = null;

		// check we've got some tracks
		if (_theObjects == null) {
			MWC.GUI.Dialogs.DialogFactory.showMessage("Hide/Reveal objects",
					"Please select some objects prior to starting");
			return res;
		}

		// find out what we're doing
		final boolean hideIt = getHideReveal();

		// make our symbols and labels visible
		final Enumeration<WatchableList> iter = _theObjects.elements();
		while (iter.hasMoreElements()) {
			final WatchableList wl = iter.nextElement();

			if (wl instanceof Plottable) {
				final Plottable thisP = (Plottable) wl;

				if (res == null) {
					res = new HideRevealAction(hideIt, this._theLayers);
				}

				// and store it
				res.changeThisObject(thisP);
			}
		}

		// return the new action
		return res;
	}

	/*********************************************************************************************
	 * member methods
	 **********************************************************************************************/

	@Override
	public final String getDescription() {
		String res = "2. Select objects to be hidden/reveals";
		res += _theSeparator + "3. Press 'Apply' button";
		res += _theSeparator + "4. Select Hide/Reveal from the dialog box which appears";
		res += _theSeparator + "====================";
		res += _theSeparator + "This operations allows a group of objects to be hidden/revealed";
		return res;
	}

	/**
	 * get the property which is to be edited
	 *
	 */
	private boolean getHideReveal() {
		boolean res = false;

		// create the selections
		final String[] list = new String[] { "Hide", "Reveal" };

		// find out which one the user wants to edit
		final Object val = JOptionPane.showInputDialog(null, "Do you wish to hide or reveal the selected objects?",
				"Hide/Reveal objects", JOptionPane.QUESTION_MESSAGE, null, list, null);

		final String selected = (String) val;
		if (selected.equals("Hide")) {
			res = true;
		} else
			res = false;

		return res;
	}

	@Override
	public final String getImage() {
		return null;
	}

	@Override
	public final String getLabel() {
		return "Hide/Reveal objects";
	}

	/**
	 * the user has pressed RESET whilst this button is pressed
	 *
	 * @param startTime the new start time
	 * @param endTime   the new end time
	 */
	@Override
	public void resetMe(final HiResDate startTime, final HiResDate endTime) {
	}

	@Override
	public final void setPeriod(final HiResDate startDTG, final HiResDate finishDTG) {
		// ignore, since we don't mind
	}

	@Override
	public final void setTracks(final java.util.Vector<WatchableList> selectedTracks) {
		// store the objects
		_theObjects = selectedTracks;
	}

}
