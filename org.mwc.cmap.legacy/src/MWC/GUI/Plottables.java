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

// $RCSfile: Plottables.java,v $
// @author $Author: ian.mayo $
// @version $Revision: 1.9 $
// $Log: Plottables.java,v $
// Revision 1.9  2007/06/01 10:45:03  ian.mayo
// Sort by name rather than hash value.  Tidy how we recalc bounds
//
// Revision 1.8  2007/05/29 08:31:08  ian.mayo
// Provide fallback for how plottables are compared
//
// Revision 1.7  2006/10/03 08:22:57  Ian.Mayo
// Use better compareTo methods
//
// Revision 1.6  2006/05/25 14:10:42  Ian.Mayo
// Make plottables comparable
//
// Revision 1.5  2004/10/07 14:23:22  Ian.Mayo
// Reflect fact that enum is now keyword - change our usage to enumer
//
// Revision 1.4  2004/09/27 10:52:32  Ian.Mayo
// Decide whether to plot data item by comparing against visible data area rather than the area the user has just dragged
//
// Revision 1.3  2004/09/06 14:04:43  Ian.Mayo
// Switch to supporting editables in Layer Manager, and showing icon for any editables which have one
//
// Revision 1.2  2004/05/25 15:45:45  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:14  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:04  Ian.Mayo
// Initial import
//
// Revision 1.6  2003-06-11 16:01:01+01  ian_mayo
// Tidy javadoc comments
//
// Revision 1.5  2003-02-07 09:49:05+00  ian_mayo
// rationalise unnecessary to da comments (that's do really)
//
// Revision 1.4  2002-11-25 11:11:26+00  ian_mayo
// Add concept of PlottablesType
//
// Revision 1.3  2002-07-12 15:46:56+01  ian_mayo
// Use constant to represent error value
//
// Revision 1.2  2002-05-28 09:25:35+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:15:14+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:02:31+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:46:34+01  administrator
// Initial revision
//
// Revision 1.2  2001-07-16 15:37:51+01  novatech
// only add the new plottable if it is non-null
//
// Revision 1.1  2001-01-03 13:43:08+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:43:00  ianmayo
// initial version
//
// Revision 1.10  2000-11-08 11:49:44+00  ian_mayo
// reflect change in status of TrackWrapper to Layer
//
// Revision 1.9  2000-04-19 11:37:37+01  ian_mayo
// optimise area calculation
//
// Revision 1.8  2000-03-07 10:11:38+00  ian_mayo
// optimisation, add myArea parameter to speed up getBounds operations
//
// Revision 1.7  2000-02-02 14:26:25+00  ian_mayo
// only bother with it if a projection has been set
//
// Revision 1.6  2000-01-20 10:14:16+00  ian_mayo
// after experiments/ d-lines
//
// Revision 1.5  1999-11-29 10:53:47+00  ian_mayo
// add getVector() method
//
// Revision 1.4  1999-11-26 15:45:04+00  ian_mayo
// adding toString method
//
// Revision 1.3  1999-11-25 13:33:47+00  ian_mayo
// inserted comment as reminder
//
// Revision 1.2  1999-11-15 15:42:37+00  ian_mayo
// checking whether shape & label is in the current data area
//
// Revision 1.1  1999-10-12 15:37:10+01  ian_mayo
// Initial revision
//
// Revision 1.5  1999-08-17 08:28:57+01  administrator
// added append function
//
// Revision 1.4  1999-08-17 08:15:44+01  administrator
// allow removal of elements
//
// Revision 1.2  1999-08-04 09:45:29+01  administrator
// minor mods, tidying up
//
// Revision 1.1  1999-07-27 10:50:51+01  administrator
// Initial revision
//
// Revision 1.3  1999-07-27 09:28:07+01  administrator
// tidying up
//
// Revision 1.2  1999-07-12 08:09:19+01  administrator
// Property editing added
//
// Revision 1.1  1999-07-07 11:10:09+01  administrator
// Initial revision
//
// Revision 1.1  1999-06-16 15:38:01+01  sm11td
// Initial revision
//
// Revision 1.4  1999-06-01 16:49:17+01  sm11td
// Reading in tracks aswell as fixes, commenting large portions of source code
//
// Revision 1.3  1999-02-04 08:02:30+00  sm11td
// Plotting to canvas, scaling canvas,
//
// Revision 1.2  1999-02-01 16:08:52+00  sm11td
// creating new sessions & panes, starting import management
//
// Revision 1.1  1999-01-31 13:33:15+00  sm11td
// Initial revision
//

package MWC.GUI;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;

import MWC.GUI.Chart.Painters.Grid4WPainter;
import MWC.GUI.Chart.Painters.GridPainter;
import MWC.GUI.Chart.Painters.ScalePainter;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.Errors.Trace;

/**
 * a list of Plottables
 */
public class Plottables implements Plottable, Serializable, PlottablesType, PropertyChangeListener, Renamable {

	/**
	 * embedded class that knows how to compare two editables
	 *
	 * @author ian.mayo
	 */
	public static class CompareEditables implements Comparator<Editable>, Serializable {
		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

		@Override
		@SuppressWarnings("unchecked")
		public int compare(final Editable p1, final Editable p2) {
			int res;

			// just do our special check for items that should get plotted first
			if (p1 == p2)
				res = 0;
			else if (p1 instanceof PlotMeFirst) {
				res = -1;
			} else if (p2 instanceof PlotMeFirst) {
				res = 1;
			} else if (p1 instanceof Comparable) {
				// yup, let them go for it
				final Comparable<Editable> c1 = (Comparable<Editable>) p1;
				res = c1.compareTo(p2);
			} else
				res = p1.getName().compareTo(p2.getName());

			return res;
		}
	}

	/**
	 * marker interface. Plottables stores its data in a high performance
	 * time-sequenced dataset. When removing an item, it does a binary search to
	 * find the relevant item to delete. But, some elements lose their time marker
	 * as they're being deleted, so we should handle their removal with a
	 * traditional sequential search
	 *
	 */
	public static interface DeleteWithCare {
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	static public class Grid4WTest extends junit.framework.TestCase {
		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		public Grid4WTest(final String val) {
			super(val);
		}

		public void testAddRemove() {
			final Plottables pl = new Plottables();
			assertEquals("Empty list", pl.size(), 0);
			final GridPainter cp = new GridPainter();
			pl.add(cp);
			assertEquals("non-empty list", pl.size(), 1);
			pl.removeElement(cp);
			assertEquals("list", pl.size(), 0);

			final ScalePainter sp = new ScalePainter();
			pl.add(sp);
			assertEquals("non-empty list", pl.size(), 1);
			pl.removeElement(sp);
			assertEquals("list empty", pl.size(), 0);
			final Grid4WPainter c4p = new Grid4WPainter(null);
			pl.add(c4p);
			assertEquals("non-empty list", pl.size(), 1);
			pl.removeElement(c4p);
			assertEquals("list", pl.size(), 0);
		}

	}

	public static final class IteratorWrapper implements java.util.Enumeration<Editable> {
		private final Iterator<Editable> _val;

		public IteratorWrapper(final Iterator<Editable> iterator) {
			_val = iterator;
		}

		@Override
		public final boolean hasMoreElements() {
			return _val.hasNext();

		}

		@Override
		public final Editable nextElement() {
			return _val.next();
		}
	}

	/**
	 * marker interface to indicate that this plottable should get plotted before
	 * the others.
	 *
	 * @author ian
	 *
	 */
	public static interface PlotMeFirst {
	}

	static final long serialVersionUID = 4094060714021604632L;

	/**
	 * the actual list of plottables
	 */
	// private TreeSet<Editable> _thePlottables;
	private final ConcurrentSkipListSet<Editable> _thePlottables;

	// //////////////////////////////////////////////////////////////
	// member functions
	// //////////////////////////////////////////////////////////////

	// protected com.sun.java.util.collections.Hashset _thePlottables;
	/**
	 * the name of this list
	 */
	private String _theName;

	/**
	 * cache the value of toString()
	 *
	 */
	private String _cachedName = null;

	/**
	 * specify if this layer is currently visible or not
	 */
	private boolean _visible;

	// //////////////////////////////////////////////////////////////
	// constructor
	// //////////////////////////////////////////////////////////////
	public Plottables() {
		_thePlottables = new ConcurrentSkipListSet<>(new CompareEditables());
//		Collections.synchronizedSortedSet(new TreeSet<Editable>(
//				new CompareEditables()));
		_visible = true;
	}

	/**
	 * add the plottable to this list
	 */
	public void add(final Editable thePlottable) {
		// check the creation worked
		if (thePlottable == null)
			return;

		clearCachedName();

		// right, add it.
		boolean added = _thePlottables.add(thePlottable);
		System.out.println("Added successfully " + added);

		// does it have info?
		if (thePlottable.getInfo() != null) {
			// try to listen out for if the plottable moves
			thePlottable.getInfo().addPropertyChangeListener(PlainWrapper.LOCATION_CHANGED, this);
		}
	}

	/**
	 * append the other list of plottables to this one
	 */
	public void append(final PlottablesType other) {
		final Enumeration<Editable> enumer = other.elements();
		while (enumer.hasMoreElements()) {
			final Plottable p = (Plottable) enumer.nextElement();
			add(p);
		}
	}

	/**
	 * clear the cached name, if the name changes, or the number of children changes
	 */
	private void clearCachedName() {
		_cachedName = null;
	}

	/**
	 * the collective name for items of this type (used when viewing in Outline)
	 *
	 * @return collective name for this type
	 */
	protected String collectiveName() {
		return "items";
	}

	@Override
	public int compareTo(final Plottable arg0) {
		final Plottable other = arg0;

		// start by comparing name
		int res = getName().compareTo(other.getName());

		// use hashcode as fallback
		if (res == 0) {
			res = Integer.valueOf(this.hashCode()).compareTo(Integer.valueOf(other.hashCode()));
		}

		return res;
	}

	/**
	 * do we already contain this object?
	 *
	 * @param other
	 * @return
	 */
	public boolean contains(final Editable other) {
		return _thePlottables.contains(other);
	}

	/**
	 * return the list of elements in this plottable
	 */
	@Override
	public Enumeration<Editable> elements() {
		return new IteratorWrapper(_thePlottables.iterator());
	}

	/**
	 * @return the first item in our list
	 */
	public Plottable first() {
		return (Plottable) _thePlottables.first();
	}

	/**
	 * get the area covered by this list
	 */
	@Override
	public MWC.GenericData.WorldArea getBounds() {
		// yup, get on with it...
		return recalculateAreaCovered();
	}

	/**
	 * return our data, expressed as a collection
	 *
	 * @return
	 */
	public Collection<Editable> getData() {
		return _thePlottables;
	}

	/**
	 * get the editing information for this type
	 */
	@Override
	public Editable.EditorType getInfo() {
		return null;
	}

	/**
	 * return the name of the plottable
	 */
	@Override
	public String getName() {
		return _theName;
	}

	/**
	 * it this item currently visible?
	 */
	@Override
	public boolean getVisible() {
		return _visible;
	}

	/**
	 * does this item have an editor?
	 */
	@Override
	public boolean hasEditor() {
		return false;
	}

	/**
	 * @return all items in our list less than toElement
	 */
	public SortedSet<Editable> headSet(final Editable toElement) {
		return _thePlottables.headSet(toElement);
	}

	/**
	 * whether this list contains elements
	 *
	 * @return
	 */
	public boolean isEmpty() {
		return _thePlottables.isEmpty();
	}

	/**
	 * @return the last item in our list
	 */
	public Plottable last() {
		return (Plottable) _thePlottables.last();
	}

	/**
	 * paint this list to the canvas
	 */
	@Override
	public synchronized void paint(final CanvasType dest) {
		// see if I am visible
		if (!getVisible())
			return;

		// note, we used to only test it the subject was in the data area,
		// but that left some items outside the user-dragged area not being visible.
		// - instead we calculate the visible data-area from the current screen
		// area, and
		// compare against that
		WorldArea wa = dest.getProjection().getVisibleDataArea();

		// drop out if we don't have a data area for the projection
		if (wa == null) {
			dest.getProjection().zoom(0.0);
			wa = dest.getProjection().getVisibleDataArea();
		}

		synchronized (_thePlottables) {
			final Iterator<Editable> enumer = _thePlottables.iterator();

			while (enumer.hasNext()) {
				final Object next = enumer.next();
				if (next instanceof Plottable) {
					final Plottable thisP = (Plottable) next;

					// is this plottable visible
					if (thisP.getVisible()) {

						// see if this plottable is within the data area
						final WorldArea wp = thisP.getBounds();

						if (wp != null) {
							// it has an area, see if it is in view
							if (wp.overlaps(wa))
								thisP.paint(dest);
						} else {
							// it doesn't have an area, so plot it anyway
							thisP.paint(dest);
						}
					}
				}
			}
		}
	}

	@Override
	public void propertyChange(final PropertyChangeEvent arg0) {

	}

	/**
	 * Determine how far away we are from this point. or return INVALID_RANGE if it
	 * can't be calculated
	 */
	@Override
	public double rangeFrom(final WorldLocation other) {
		return INVALID_RANGE;
	}

	/**
	 * method to recalculate the area covered by the plottables
	 */
	private WorldArea recalculateAreaCovered() {
		// so, step through the array, and calculate the area
		WorldArea res = null;

		final Iterator<Editable> enumer = _thePlottables.iterator();
		while (enumer.hasNext()) {
			final Object nextOne = enumer.next();
			if (nextOne instanceof Plottable) {
				final Plottable thisOne = (Plottable) nextOne;

				// is this item visible?
				if (thisOne.getVisible()) {
					res = WorldArea.extend(res, thisOne.getBounds());
				}
			}
		}

		return res;

	}

	/**
	 * clera the list
	 */
	public void removeAllElements() {
		clearCachedName();

		// undo all the moved listeners
		final Iterator<Editable> iter = _thePlottables.iterator();
		while (iter.hasNext()) {
			final Editable editable = iter.next();
			stopListeningTo(editable);
		}

		_thePlottables.clear();
	}

	/**
	 * remove this area
	 */
	public void removeElement(final Editable p) {
		// stop listening to it
		stopListeningTo(p);

		clearCachedName();

		// double check we've got it.
		final boolean worked;
		if (p instanceof DeleteWithCare) {
			// create simple list, that uses sequential find (remove)
			final ArrayList<Editable> items = new ArrayList<Editable>();

			// add all the plottables
			items.addAll(_thePlottables);

			// remove our one
			worked = items.remove(p);

			// did it work?
			if (worked) {
				// yes. Replace our editables with the trimmed list
				_thePlottables.clear();
				_thePlottables.addAll(items);
			}
		} else {
			worked = _thePlottables.remove(p);
		}

		if (!worked) {
			Trace.trace("Failed trying to remove " + p + " from " + this, false);
		}
	}

	/**
	 * set the name of the plottable
	 */
	@Override
	@FireReformatted
	public void setName(final String theName) {
		_theName = theName;

		clearCachedName();
	}

	/**
	 * set the visible flag for this layer
	 */
	@Override
	public void setVisible(final boolean visible) {
		_visible = visible;
	}

	/**
	 * return the current size of this list
	 */
	@Override
	public int size() {
		return _thePlottables.size();
	}

	/**
	 * remove location change listener from the supplied item
	 *
	 * @param p
	 */
	private void stopListeningTo(final Editable p) {
		final EditorType info = p.getInfo();
		if (info != null)
			info.removePropertyChangeListener(PlainWrapper.LOCATION_CHANGED, this);
	}

	/**
	 * @return all items in our list greater or equal to fromElement and less than
	 *         toElement
	 */
	public SortedSet<Editable> subSet(final Editable fromElement, final Editable toElement) {
		return _thePlottables.subSet(fromElement, toElement);
	}

	/**
	 * @return all items in our list greater or equal to fromElement
	 */
	public SortedSet<Editable> tailSet(final Editable fromElement) {
		return _thePlottables.tailSet(fromElement);
	}

	/**
	 * convenience function, to describe this plottable as a string
	 */
	@Override
	public String toString() {
		// do we have a cached value?
		if (_cachedName == null) {
			// nope, better create one
			_cachedName = getName() + " (" + size() + " " + collectiveName() + ")";
		}
		return _cachedName;
	}

}
