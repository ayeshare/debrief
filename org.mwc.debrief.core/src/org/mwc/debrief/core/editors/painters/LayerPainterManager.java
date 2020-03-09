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

package org.mwc.debrief.core.editors.painters;

import java.beans.PropertyChangeSupport;
import java.util.Iterator;
import java.util.Vector;

import org.mwc.debrief.core.editors.painters.highlighters.NullHighlighter;
import org.mwc.debrief.core.editors.painters.highlighters.SWTPlotHighlighter;
import org.mwc.debrief.core.editors.painters.highlighters.SWTRangeHighlighter;
import org.mwc.debrief.core.editors.painters.highlighters.SWTSymbolHighlighter;

import MWC.TacticalData.TrackDataProvider;

/**
 * @author ian.mayo
 */
public class LayerPainterManager extends PropertyChangeSupport {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * our list of painters
	 */
	private final Vector<TemporalLayerPainter> _myPainterList;

	/**
	 * our list of highlighters
	 *
	 */
	private final Vector<SWTPlotHighlighter> _myHighlighterList;

	/**
	 * the current one
	 *
	 * @param dataProvider
	 */
	private TemporalLayerPainter _currentPainter = null;

	/**
	 * and the highlighter
	 *
	 */
	private SWTPlotHighlighter _currentHighlighter = null;

	/**
	 * constructor - to collate the list
	 *
	 * @param dataProvider
	 */
	public LayerPainterManager(final TrackDataProvider dataProvider) {
		super(dataProvider);

		// and now build the painters
		_myPainterList = new Vector<TemporalLayerPainter>(0, 1);
		_myPainterList.add(new PlainHighlighter());
		_myPainterList.add(new SnailHighlighter(dataProvider));

		setCurrentPainter(_myPainterList.firstElement());

		// and the plot highlighters
		_myHighlighterList = new Vector<SWTPlotHighlighter>(0, 1);
		_myHighlighterList.add(new SWTPlotHighlighter.RectangleHighlight());
		_myHighlighterList.add(new SWTSymbolHighlighter());
		_myHighlighterList.add(new SWTRangeHighlighter());
		_myHighlighterList.add(new NullHighlighter());

		// and sort out the defaults
		_currentPainter = _myPainterList.firstElement();
		_currentHighlighter = _myHighlighterList.firstElement();
	}

	/**
	 * ditch ourselves
	 *
	 */
	public void close() {
		_myHighlighterList.clear();
		_myPainterList.clear();
		_currentHighlighter = null;
		_currentPainter = null;
	}

	/**
	 * find out which is the currently selected painter
	 *
	 * @return
	 */
	public SWTPlotHighlighter getCurrentHighlighter() {
		return _currentHighlighter;
	}

	/**
	 * find out which is the currently selected painter
	 *
	 * @return
	 */
	public TemporalLayerPainter getCurrentPainter() {
		return _currentPainter;
	}

	/**
	 * get the list of painters
	 */
	public SWTPlotHighlighter[] getHighlighterList() {
		final SWTPlotHighlighter[] res = new SWTPlotHighlighter[] { null };
		return _myHighlighterList.toArray(res);
	}

	/**
	 * get the list of painters
	 */
	public TemporalLayerPainter[] getPainterList() {
		final TemporalLayerPainter[] res = new TemporalLayerPainter[] { null };
		return _myPainterList.toArray(res);
	}

	/**
	 * decide which cursor to use (based on text string)
	 *
	 * @param highlighterName
	 */
	public void setCurrentHighlighter(final String highlighterName) {
		SWTPlotHighlighter newCursor = null;
		for (final Iterator<SWTPlotHighlighter> thisHighlighter = _myHighlighterList.iterator(); thisHighlighter
				.hasNext();) {
			final SWTPlotHighlighter thisP = thisHighlighter.next();
			if (thisP.getName().equals(highlighterName)) {
				newCursor = thisP;
				break;
			}
		}

		// cool. did we find one?
		if (newCursor != null)
			setCurrentHighlighter(newCursor);
	}

	/**
	 * allow changing of the current painter
	 *
	 * @param current the new one being selected
	 */
	public void setCurrentHighlighter(final SWTPlotHighlighter current) {
		// store the old one
		final SWTPlotHighlighter old = _currentHighlighter;

		// assign to the new one
		_currentHighlighter = current;

		// inform anybody who wants to know
		firePropertyChange("Changed", old, current);
	}

	/**
	 * decide which cursor to use (based on text string)
	 *
	 * @param cursorName
	 */
	public void setCurrentPainter(final String cursorName) {
		TemporalLayerPainter newCursor = null;
		for (final Iterator<TemporalLayerPainter> thisPainter = _myPainterList.iterator(); thisPainter.hasNext();) {
			final TemporalLayerPainter thisP = thisPainter.next();
			if (thisP.getName().equals(cursorName)) {
				newCursor = thisP;
				break;
			}
		}

		// cool. did we find one?
		if (newCursor != null)
			setCurrentPainter(newCursor);
	}

	/**
	 * allow changing of the current painter
	 *
	 * @param current the new one being selected
	 */
	public void setCurrentPainter(final TemporalLayerPainter current) {
		// store the old one
		final TemporalLayerPainter old = _currentPainter;

		// assign to the new one
		_currentPainter = current;

		// inform anybody who wants to know
		firePropertyChange("Changed", old, current);
	}
}