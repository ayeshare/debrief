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

package MWC.Utilities.ReaderWriter.XML.Features;

import org.w3c.dom.Element;

import MWC.GUI.Chart.Painters.GridPainter;
import MWC.GUI.Chart.Painters.LocalGridPainter;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.ReaderWriter.XML.Util.LocationHandler;

/**
 * Created by IntelliJ IDEA. User: Ian.Mayo Date: 19-Oct-2004 Time: 09:34:43 To
 * change this template use File | Settings | File Templates.
 */
abstract public class LocalGridHandler extends GridHandler {
	private final static String ORIGIN = "Origin";
	private final static String MY_TYPE = "LocalGrid";
	private static final String PLOT_ORIGIN = "PlotOrigin";

	WorldLocation _myOrigin = null;
	boolean _plotOrigin = true;

	public LocalGridHandler() {
		super(MY_TYPE);

		addAttributeHandler(new HandleBooleanAttribute(PLOT_ORIGIN) {
			@Override
			public void setValue(final String name, final boolean value) {
				_plotOrigin = value;
			}
		});

		addHandler(new LocationHandler(ORIGIN) {
			@Override
			public void setLocation(final WorldLocation res) {
				// To change body of implemented methods use File | Settings | File Templates.
				_myOrigin = res;
			}
		});

	}

	/**
	 * export this grid
	 *
	 * @param plottable the grid we're going to export
	 * @param parent
	 * @param doc
	 */
	@Override
	public void exportThisPlottable(final MWC.GUI.Plottable plottable, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {

		final MWC.GUI.Chart.Painters.LocalGridPainter theGrid = (MWC.GUI.Chart.Painters.LocalGridPainter) plottable;
		final Element gridElement = doc.createElement(MY_TYPE);

		// get the parent to export itself
		exportGridAttributes(gridElement, theGrid, doc);

		// and the location
		LocationHandler.exportLocation(theGrid.getOrigin(), ORIGIN, gridElement, doc);

		// and whether to plot the origin
		gridElement.setAttribute(PLOT_ORIGIN, writeThis(theGrid.getPlotOrigin()));

		// done.
		parent.appendChild(gridElement);
	}

	/**
	 * get the grid object itself (we supply this method so that it can be
	 * overwritten, by the LocalGrid painter for example
	 *
	 * @return
	 */
	@Override
	protected GridPainter getGrid() {
		final LocalGridPainter local = new LocalGridPainter();
		local.setOrigin(_myOrigin);
		local.setPlotOrigin(_plotOrigin);
		_myOrigin = null;
		_plotOrigin = true;
		return local;
	}

}
