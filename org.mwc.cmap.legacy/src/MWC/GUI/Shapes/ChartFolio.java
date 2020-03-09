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

package MWC.GUI.Shapes;

import java.awt.Color;
import java.util.Enumeration;

import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;

public class ChartFolio extends BaseLayer {

	// //////////////////////////////////////////////////////////////////////////
	// embedded class, used for editing the plotter
	// //////////////////////////////////////////////////////////////////////////
	/**
	 * the definition of what is editable about this object
	 */
	public class ChartFolioInfo extends Editable.EditorType {

		/**
		 * constructor for editable details of a set of Layers
		 *
		 * @param data the Layers themselves
		 */
		public ChartFolioInfo(final ChartFolio data) {
			super(data, data.getName(), "Edit");
		}

		/**
		 * editable GUI properties for our participant
		 *
		 * @return property descriptions
		 */
		@Override
		public java.beans.PropertyDescriptor[] getPropertyDescriptors() {
			try {
				final java.beans.PropertyDescriptor[] res = {
						displayProp(SHOW_NAMES, "Show names", "show names for charts"),
						displayProp(LINE_COLOR, "Line color", "color for chart rectangles") };
				return res;
			} catch (final java.beans.IntrospectionException e) {
				return super.getPropertyDescriptors();
			}
		}

	}

	public static final String SHOW_NAMES = "showNames";

	public static final String LINE_COLOR = "lineColor";
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private boolean _showNames = false;

	private Color _lineColor = Color.red;

	public ChartFolio(final boolean showNames, final Color lineColor) {
		_showNames = showNames;
		_lineColor = lineColor;
	}

	@Override
	public void add(final Editable thePlottable) {
		if (thePlottable instanceof ChartBoundsWrapper)
			super.add(thePlottable);
		else
			throw new RuntimeException("Can only hold chart wrappers!");
	}

	/**
	 * whether this type of BaseLayer is able to have shapes added to it
	 *
	 * @return
	 */
	@Override
	public boolean canTakeShapes() {
		return false;
	}

	@Override
	public EditorType getInfo() {
		if (_myEditor == null)
			_myEditor = new ChartFolioInfo(this);

		return super.getInfo();
	}

	public Color getLineColor() {
		return _lineColor;
	}

	public boolean isShowNames() {
		return _showNames;
	}

	public void setLineColor(final Color lineColor) {
		this._lineColor = lineColor;
		// go through kiddies setting name vis
		final Enumeration<Editable> iter = super.elements();
		while (iter.hasMoreElements()) {
			final Editable editable = iter.nextElement();
			final ChartBoundsWrapper chart = (ChartBoundsWrapper) editable;
			chart.setColor(lineColor);
		}
	}

	public void setShowNames(final boolean showNames) {
		this._showNames = showNames;

		// go through kiddies setting name vis
		final Enumeration<Editable> iter = super.elements();
		while (iter.hasMoreElements()) {
			final Editable editable = iter.nextElement();
			final ChartBoundsWrapper chart = (ChartBoundsWrapper) editable;
			chart.setLabelVisible(showNames);
		}
	}

}
