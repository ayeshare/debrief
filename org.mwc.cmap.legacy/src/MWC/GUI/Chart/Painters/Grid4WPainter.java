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

package MWC.GUI.Chart.Painters;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: GridPainter.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.17 $
// $Log: GridPainter.java,v $
// Revision 1.17  2006/06/06 09:19:32  Ian.Mayo
// Give grids an editable name
//
// Revision 1.16  2006/05/25 14:10:38  Ian.Mayo
// Make plottables comparable
//
// Revision 1.15  2005/09/13 09:28:29  Ian.Mayo
// Eclipse tidying
//
// Revision 1.14  2005/09/13 09:16:07  Ian.Mayo
// Minor tidying
//
// Revision 1.13  2005/09/07 13:45:45  Ian.Mayo
// Minor tidying
//
// Revision 1.12  2005/05/19 14:46:48  Ian.Mayo
// Add more categories to editable bits
//
// Revision 1.11  2005/01/11 15:18:04  Ian.Mayo
// make the grid slightly thicker
//
// Revision 1.10  2004/10/25 09:02:48  Ian.Mayo
// Correctly label grid lines when in relative mode (non-angular units)
//
// Revision 1.9  2004/10/19 13:27:05  Ian.Mayo
// More refactoring to support override by local grid painter
//
// Revision 1.8  2004/10/19 11:13:16  Ian.Mayo
// Getting closer - our new origin is respected when using angular units
//
// Revision 1.7  2004/10/19 10:15:02  Ian.Mayo
// Add local grid support
//
// Revision 1.6  2004/10/19 08:28:20  Ian.Mayo
// Refactor to allow over-riding by LocalGridPainter
//
// Revision 1.5  2004/08/31 09:38:06  Ian.Mayo
// Rename inner static tests to match signature **Test to make automated testing more consistent
//
// Revision 1.4  2004/05/25 14:46:57  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:16  ian
// no message
//
// Revision 1.3  2003/10/21 08:15:36  Ian.Mayo
// Tidily format distance grid labels, correctly plot long as long
//
// Revision 1.2  2003/10/17 14:52:57  Ian.Mayo
// Better drawing of distance-related units
//
// Revision 1.1.1.1  2003/07/17 10:07:12  Ian.Mayo
// Initial import
//
// Revision 1.6  2003-07-04 11:00:55+01  ian_mayo
// Reflect name change of parent editor test
//
// Revision 1.5  2003-01-30 10:25:10+00  ian_mayo
// Check we only plot sensible grid lines
//
// Revision 1.4  2002-10-30 16:26:59+00  ian_mayo
// tidy (shorten) up display names for editables
//
// Revision 1.3  2002-07-12 15:46:53+01  ian_mayo
// Use constant to represent error value
//
// Revision 1.2  2002-05-28 09:25:39+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:15:07+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:02:14+01  ian_mayo
// Initial revision
//
// Revision 1.4  2002-01-17 20:40:26+00  administrator
// Reflect switch to Duration/WorldDistance
//
// Revision 1.3  2002-01-17 14:49:09+00  administrator
// Reflect fact that distance now in WorldDistance units
//
// Revision 1.2  2001-08-21 12:06:03+01  administrator
// Make grids visible by default
//
// Revision 1.1  2001-08-01 13:00:42+01  administrator
// Add code and properties necessary for plotting labels on grid lines
//
// Revision 1.0  2001-07-17 08:46:29+01  administrator
// Initial revision
//
// Revision 1.5  2001-06-04 09:37:37+01  novatech
// don't plot grid points if they're too close
//
// Revision 1.4  2001-01-22 19:40:37+00  novatech
// reflect optimised projection.toScreen plotting
//
// Revision 1.3  2001-01-22 14:49:00+00  novatech
// Projection.toWorld() always return the same object, so completely handle the TL corner before we start processing the BR corner
//
// Revision 1.2  2001-01-22 12:29:30+00  novatech
// added JUnit testing code
//
// Revision 1.1  2001-01-03 13:43:00+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:44:13  ianmayo
// initial version
//
// Revision 1.7  2000-09-21 09:06:46+01  ian_mayo
// make Editable.EditorType a transient parameter, to prevent it being written to file
//
// Revision 1.6  2000-08-18 13:36:07+01  ian_mayo
// implement singleton of Editable.EditorType
//
// Revision 1.5  2000-08-11 08:41:59+01  ian_mayo
// tidy beaninfo
//
// Revision 1.4  2000-08-09 16:03:13+01  ian_mayo
// remove stray semi-colons
//
// Revision 1.3  1999-11-26 15:45:32+00  ian_mayo
// adding toString methods
//
// Revision 1.2  1999-11-11 18:19:19+00  ian_mayo
// minor tidying up
//
// Revision 1.1  1999-10-12 15:37:01+01  ian_mayo
// Initial revision
//
// Revision 1.2  1999-09-14 14:17:55+01  administrator
// working with values other than whole degrees
//
// Revision 1.1  1999-08-17 10:02:08+01  administrator
// Initial revision
//

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.Serializable;

import MWC.GUI.CanvasType;
import MWC.GUI.Defaults;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Plottable;
import MWC.GUI.Properties.LineStylePropertyEditor;
import MWC.GUI.Shapes.DraggableItem;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

public class Grid4WPainter implements Plottable, Serializable, DraggableItem {
	// ///////////////////////////////////////////////////////////
	// member variables
	// //////////////////////////////////////////////////////////

	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	static public class Grid4WTest extends junit.framework.TestCase {
		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		public Grid4WTest(final String val) {
			super(val);
		}

		public void testIndexMgt() {
			// first some valid ones
			assertEquals("index not calculated properly", 1, Grid4WPainter.indexOf("B"));
			assertEquals("index not calculated properly", 0, Grid4WPainter.indexOf("A"));
			assertEquals("index not calculated properly", 5, Grid4WPainter.indexOf("F"));
			assertEquals("index not calculated properly", 22, Grid4WPainter.indexOf("Y"));
			assertEquals("index not calculated properly", 24, Grid4WPainter.indexOf("AA"));
			assertEquals("index not calculated properly", 26, Grid4WPainter.indexOf("AC"));
			assertEquals("index not calculated properly", 47, Grid4WPainter.indexOf("AZ"));
			// and some invalid ones
			assertEquals("invalid index not rejected properly", INVALID_INDEX, Grid4WPainter.indexOf("3"));
			assertEquals("invalid index not rejected properly", INVALID_INDEX, Grid4WPainter.indexOf(""));
			assertEquals("invalid index not rejected properly", INVALID_INDEX, Grid4WPainter.indexOf("CC"));
			assertEquals("invalid index not rejected properly", INVALID_INDEX, Grid4WPainter.indexOf("?A"));
			// now some reverse checks
			// first some valid ones
			assertEquals("index not calculated properly", Grid4WPainter.labelFor(1), "B");
			assertEquals("index not calculated properly", Grid4WPainter.labelFor(0), "A");
			assertEquals("index not calculated properly", Grid4WPainter.labelFor(5), "F");
			assertEquals("index not calculated properly", Grid4WPainter.labelFor(22), "Y");
			assertEquals("index not calculated properly", Grid4WPainter.labelFor(24), "AA");
			assertEquals("index not calculated properly", Grid4WPainter.labelFor(26), "AC");
			assertEquals("index not calculated properly", Grid4WPainter.labelFor(47), "AZ");

		}

		public void testInit() {
			final WorldLocation origin = new WorldLocation(2, 3, 2);
			final Grid4WPainter pt = new Grid4WPainter(origin);
			assertEquals("wrong name", pt.getName(), DEFAULT_NAME);
			assertEquals("wrong x def", new WorldDistance(10, WorldDistance.NM), pt.getXDelta());
			assertEquals("wrong y def", new WorldDistance(10, WorldDistance.NM), pt.getYDelta());
			assertEquals("wrong init index", 1, pt.getYMin().intValue());
			assertEquals("wrong init index", 24, pt.getYMax().intValue());
			assertEquals("wrong init index", "A", pt.getXMin());
			assertEquals("wrong init index", "Z", pt.getXMax());
			assertEquals("wrong origin lat", 2d, pt._origin.getLat());
			assertEquals("wrong origin lat", 3d, pt._origin.getLong());
		}

		public void testMyParams() {
			MWC.GUI.Editable ed = new Grid4WPainter(null);
			MWC.GUI.Editable.editableTesterSupport.testParams(ed, this);
			ed = null;
		}

		public void testPosCalc() {
			// coords to get
			final int x = 1, y = 1;
			final WorldLocation origin = new WorldLocation(0, 0, 0);
			final double orientation = 0;
			final WorldDistance xDelta = new WorldDistance(1, WorldDistance.DEGS);
			final WorldDistance yDelta = new WorldDistance(1, WorldDistance.DEGS);

			final Grid4WPainter pt = new Grid4WPainter(origin);
			pt.setXDelta(xDelta);
			pt.setYDelta(yDelta);
			pt.setOrientation(orientation);
			WorldLocation newLoc = pt.calcLocationFor(1, 1);
			assertEquals("easy Lat wrong", 1d, newLoc.getLat(), 0.001);
			assertEquals("easy Long wrong", 1d, newLoc.getLong(), 0.001);
			newLoc = pt.calcLocationFor(2, 2);
			assertEquals("easy Lat wrong", 2d, newLoc.getLat(), 0.001);
			assertEquals("easy Long wrong", 2d, newLoc.getLong(), 0.001);

			// turn, baby
			pt.setOrientation(90d);
			newLoc = pt.calcLocationFor(x, y);
			assertEquals("easy Lat wrong", -1d, newLoc.getLat(), 0.001);
			assertEquals("easy Long wrong", 1d, newLoc.getLong(), 0.001);

			// have another go
			pt.setXDelta(new WorldDistance(2, WorldDistance.DEGS));
			pt.setYDelta(new WorldDistance(3, WorldDistance.DEGS));
			newLoc = pt.calcLocationFor(x, y);
			assertEquals("easy Lat wrong", -2d, newLoc.getLat(), 0.001);
			assertEquals("easy Long wrong", 3d, newLoc.getLong(), 0.001);

			// and turn back
			pt.setOrientation(0d);
			newLoc = pt.calcLocationFor(x, y);
			assertEquals("easy Lat wrong", 3d, newLoc.getLat());
			assertEquals("easy Long wrong", 2d, newLoc.getLong());

			// now try more complex orientations

		}

		public void testProperties() {
			final Grid4WPainter pt = new Grid4WPainter(null);
			pt.setName("new grid");
			pt.setXDelta(new WorldDistance(12, WorldDistance.DEGS));
			pt.setYDelta(new WorldDistance(5, WorldDistance.DEGS));
			pt.setXMin("C");
			pt.setXMax("E");
			pt.setYMin(7);
			pt.setYMax(12);
			final WorldLocation origin = new WorldLocation(2, 2, 0);
			pt.setOrigin(origin);
			assertEquals("wrong name", "new grid", pt.getName());
			assertEquals("wrong x val", new WorldDistance(12, WorldDistance.DEGS), pt.getXDelta());
			assertEquals("wrong y val", new WorldDistance(5, WorldDistance.DEGS), pt.getYDelta());
			assertEquals("wrong x index", "C", pt.getXMin());
			assertEquals("wrong x index", "E", pt.getXMax());
			assertEquals("wrong y index", 7, pt.getYMin().intValue());
			assertEquals("wrong y index", 12, pt.getYMax().intValue());
			assertEquals("wrong origin", origin, pt.getOrigin());
		}
	}

	// ///////////////////////////////////////////////////////////
	// info class
	// //////////////////////////////////////////////////////////
	public class GridPainterInfo extends Editable.EditorType implements Serializable {

		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

		public GridPainterInfo(final Grid4WPainter data) {
			super(data, data.getName(), "");
		}

		@Override
		public PropertyDescriptor[] getPropertyDescriptors() {
			try {
				final PropertyDescriptor[] res = { prop("Color", "the Color to draw the grid", FORMAT),
						prop("Visible", "whether this grid is visible", VISIBILITY),
						displayProp("PlotLabels", "Plot labels", "whether to plot grid labels", VISIBILITY),
						displayProp("PlotLines", "Plot lines", "whether to plot grid lines", VISIBILITY),
						displayProp("FillGrid", "Fill grid", "whether to fill the grid", VISIBILITY),
						prop("Name", "name of this grid", FORMAT), prop("Font", "font to use for labels", FORMAT),
						displayProp("FillColor", "Fill color", "color to use to fill the grid", FORMAT),
						displayProp("FontColor", "Label color", "color to use for grid labels", FORMAT),
						displayProp("XDelta", "X delta", "the x step size for the grid", SPATIAL),
						displayProp("YDelta", "Y delta", "the y step size for the grid", SPATIAL),
						prop("Orientation", "the orientation of the grid", SPATIAL),
						displayProp("XMin", "X min index", "the min index shown on the x-axis (A-AZ)", SPATIAL),
						displayProp("XMax", "X max index", "the max index shown on the x-axis (A-AZ)", SPATIAL),
						displayProp("YMin", "Y min index", "the min index shown on the y-axis (A-AZ)", SPATIAL),
						displayProp("YMax", "Y max index", "the max index shown on the y-axis (A-AZ)", SPATIAL),
						prop("Origin", "the bottom-left corner of the grid", SPATIAL),
						displayProp("OriginAtTopLeft", "Origin at top-left",
								"whether to put the origin at the top-left", FORMAT),
						displayLongProp("LineStyle", "Line style", "the dot-dash style to use for plotting this grid",
								LineStylePropertyEditor.class, FORMAT), };

				return res;
			} catch (final IntrospectionException e) {
				return super.getPropertyDescriptors();
			}
		}
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * flag for invalid 4w index
	 *
	 */
	private static final int INVALID_INDEX = 0;

	public static final String DEFAULT_NAME = "4W Grid";

	private static String indices[] = { "A", "B", "C", "D", "E", "F", "G", "H", "J", "K", "L", "M", "N", "P", "Q", "R",
			"S", "T", "U", "V", "W", "X", "Y", "Z", "AA", "AB", "AC", "AD", "AE", "AF", "AG", "AH", "AJ", "AK", "AL",
			"AM", "AN", "AP", "AQ", "AR", "AS", "AT", "AU", "AV", "AW", "AX", "AY", "AZ", "BA", "BB", "BC", "BD", "BE",
			"BF", "BG", "BH", "BJ", "BK", "BL", "BM", "BN", "BP", "BQ", "BR", "BS", "BT", "BU", "BV", "BW", "BX", "BY",
			"BZ" };

	/**
	 * find the index of the supplied string
	 *
	 * @param theTarget
	 * @return
	 */
	public static int indexOf(final String target) {
		String theTarget = target;
		// trim it down
		theTarget = theTarget.trim();

		// move to upper case
		theTarget = theTarget.toUpperCase();

		// now find a match
		int res = 0;
		for (int i = 0; i < indices.length; i++) {
			if (indices[i].equals(theTarget)) {
				res = i;
				break;
			}
		}
		return res;
	}

	/**
	 * convert the integer index to a string
	 *
	 * @param index 4w grid horizontal index
	 * @return character representation of index
	 */
	protected static String labelFor(final int index) {
		return indices[index];
	}

	/**
	 * the colour for this grid
	 */
	protected Color _myColor;

	/**
	 * the horizontal grid separation (in degrees)
	 */
	protected WorldDistance _myXDelta;

	/**
	 * the vertical grid separation (in degrees)
	 */
	protected WorldDistance _myYDelta;

	/**
	 * whether this grid is visible
	 */
	protected boolean _isOn;

	/**
	 * are we plotting lines?
	 */
	protected boolean _plotLines = true;

	/**
	 * the style the lines of this shape are drawn in
	 */
	private int _lineStyle = LineStylePropertyEditor.SOLID;

	/**
	 * are we plotting lat/long labels?
	 */
	protected boolean _plotLabels = true;

	/**
	 * whether to fill the grid
	 */
	protected boolean _fillGrid = false;

	/**
	 * the color to fill in the grid
	 *
	 */
	protected Color _fillColor = Color.white;

	/**
	 * the font color
	 *
	 */
	protected Color _fontColor = null;

	/**
	 * whether to put the origin at the top-left
	 *
	 */
	private boolean _originTopLeft = false;

	/**
	 * the min x-axis square we're plotting
	 *
	 */
	protected int _xMin = 0;

	/**
	 * the max x-axis square we're plotting
	 *
	 */
	protected int _xMax = 23;

	/**
	 * the min y-axis square we're plotting
	 *
	 */
	protected int _yMin = 0;

	/**
	 * the max y-axis square we're plotting
	 *
	 */
	protected int _yMax = 23;

	/**
	 * the bottom-left corner of the grid
	 */
	protected WorldLocation _origin;

	// ///////////////////////////////////////////////////////////
	// member functions
	// //////////////////////////////////////////////////////////

	/**
	 * the orientation of the 4W grid
	 */
	private double _orientation;

	/**
	 * our editor
	 */
	transient protected Editable.EditorType _myEditor;

	/**
	 * the name of this grid
	 *
	 */
	private String _myName = DEFAULT_NAME;

	/**
	 * the font to use
	 */
	private Font _theFont = Defaults.getScaledFont(0.8f);

	// ///////////////////////////////////////////////////////////
	// constructor
	// //////////////////////////////////////////////////////////
	public Grid4WPainter(final WorldLocation origin) {
		_myColor = Color.darkGray;
		_fontColor = _myColor;

		_origin = origin;

		// give it some default deltas
		setXDelta(new WorldDistance(10, WorldDistance.NM));
		setYDelta(new WorldDistance(10, WorldDistance.NM));

		_orientation = 0;

		// make it visible to start with
		setVisible(true);
	}

	/**
	 * sort out the location of this point
	 *
	 * @param x how far across to go
	 * @param y how far down to go
	 * @return the location at this index
	 */
	protected WorldLocation calcLocationFor(final int x, final int y) {
		// convert the orientation to radians
		final double orient = MWC.Algorithms.Conversions.Degs2Rads(_orientation);

		// calculate the deltas
		final double xComponent = x * _myXDelta.getValueIn(WorldDistance.DEGS);
		final double yComponent = y * _myYDelta.getValueIn(WorldDistance.DEGS);
		final double xNew = xComponent * Math.cos(orient) + yComponent * Math.sin(orient);
		double yNew = -xComponent * Math.sin(orient) + yComponent * Math.cos(orient);

		// are we in US units?
		if (getOriginAtTopLeft()) {
			// switch the vertical axis
			yNew = -yNew;
		}

		final WorldLocation res = new WorldLocation(_origin.getLat() + yNew, _origin.getLong() + xNew, 0);
		return res;
	}

	@Override
	public int compareTo(final Plottable arg0) {
		final Plottable other = arg0;
		final String myName = this.getName() + this.hashCode();
		final String hisName = other.getName() + arg0.hashCode();
		return myName.compareTo(hisName);
	}

	@Override
	public void findNearestHotSpotIn(final Point cursorPos, final WorldLocation cursorLoc,
			final LocationConstruct currentNearest, final Layer parentLayer, final Layers theData) {

		// initialise thisDist, since we're going to be over-writing it
		final WorldDistance thisDist = new WorldDistance(calcLocationFor(_xMin, _yMin).rangeFrom(cursorLoc),
				WorldDistance.DEGS);

		// is this our first item?
		currentNearest.checkMe(this, thisDist, null, parentLayer);

	}

	@Override
	public MWC.GenericData.WorldArea getBounds() {
		return null;
	}

	public Color getColor() {
		return _myColor;
	}

	/**
	 * @return the fillColor
	 */
	public Color getFillColor() {
		return _fillColor;
	}

	/**
	 * @return the fillGrid
	 */
	public boolean getFillGrid() {
		return _fillGrid;
	}

	public Font getFont() {
		return _theFont;
	}

	/**
	 * @return the fontColor
	 */
	public Color getFontColor() {
		return _fontColor;
	}

	/**
	 * determine where to start counting our grid labels from
	 *
	 * @param bounds
	 * @return
	 */
	protected WorldLocation getGridLabelOrigin(final WorldArea bounds) {
		return bounds.getBottomLeft();
	}

	@Override
	public Editable.EditorType getInfo() {
		if (_myEditor == null)
			_myEditor = new GridPainterInfo(this);

		return _myEditor;
	}

	public int getLineStyle() {
		return _lineStyle;
	}

	@Override
	public String getName() {
		return _myName;
	}

	/**
	 * @return the orientation
	 */
	public double getOrientation() {
		return _orientation;
	}

	/**
	 * @return the origin
	 */
	public WorldLocation getOrigin() {
		return _origin;
	}

	/**
	 * whether to put the origin at the top-left
	 *
	 * @return
	 */
	public boolean getOriginAtTopLeft() {
		return _originTopLeft;
	}

	/**
	 * find the top, bottom, left and right limits to plot. We've refactored it to a
	 * child class so that it can be overwritten
	 *
	 * @param g          the plotting converter
	 * @param screenArea the visible screen area
	 * @param deltaDegs  the grid separation requested
	 * @return an area providing the coverage requested
	 */
	protected WorldArea getOuterBounds(final CanvasType g, final Dimension screenArea, final double deltaDegs) {
		// create data coordinates from the current corners of the screen
		final WorldLocation topLeft = g.toWorld(new Point(0, 0));

		// create new corners just outside the current plot area, and clip
		// them to the nearest 'delta' value
		final double maxLat1 = Math.ceil(topLeft.getLat() / deltaDegs) * deltaDegs;
		final double minLong1 = (int) Math.floor(topLeft.getLong() / deltaDegs) * deltaDegs;

		// now for the bottom right
		final WorldLocation bottomRight = g.toWorld(new Point(screenArea.width, screenArea.height));

		// create new corners just outside the current plot area, and clip
		// them to the nearest 'delta' value
		final double maxLong1 = Math.ceil(bottomRight.getLong() / deltaDegs) * deltaDegs;
		final double minLat1 = Math.floor(bottomRight.getLat() / deltaDegs) * deltaDegs;

		final WorldArea bounds = new WorldArea(new WorldLocation(maxLat1, minLong1, 0),
				new WorldLocation(minLat1, maxLong1, 0));
		return bounds;
	}

	/**
	 * whether to plot the labels or not
	 */
	public boolean getPlotLabels() {
		return _plotLabels;
	}

	/**
	 * @return the plotLines
	 */
	public boolean getPlotLines() {
		return _plotLines;
	}

	@Override
	public boolean getVisible() {
		return _isOn;
	}

	/**
	 * get the x delta for the grid
	 *
	 * @return the size
	 */
	public WorldDistance getXDelta() {
		return _myXDelta;
	}

	/**
	 * @return the xMax
	 */
	public String getXMax() {
		return labelFor(_xMax);
	}

	/**
	 * @return the xMin
	 */
	public String getXMin() {
		return labelFor(_xMin);
	}

	/**
	 * get the y delta for the grid
	 *
	 * @return the size
	 */
	public WorldDistance getYDelta() {
		return _myYDelta;
	}

	/**
	 * @return the yMax
	 */
	public Integer getYMax() {
		return _yMax + 1;
	}

	/**
	 * @return the yMin
	 */
	public Integer getYMin() {
		return _yMin + 1;
	}

	@Override
	public boolean hasEditor() {
		return true;
	}

	/**
	 * unfortunately we need to do some plotting tricks when we're doing a
	 * locally-origined grid. This method is over-ridden by the LocalGrid to allow
	 * this
	 *
	 * @return
	 */
	protected boolean isLocalPlotting() {
		return false;
	}

	@Override
	public void paint(final CanvasType g) {

		// check we are visible
		if (!_isOn)
			return;

		// create a transparent colour
		g.setColor(new Color(_myColor.getRed(), _myColor.getGreen(), _myColor.getBlue(), 160));

		final float oldLineWidth = g.getLineWidth();

		// get the screen dimensions
		final Dimension dim = g.getSize();

		g.setLineWidth(1.0f);

		// is it filled?
		if (_fillGrid) {
			// sort out the bounds
			final int[] xPoints = new int[4];
			final int[] yPoints = new int[4];
			int ctr = 0;

			Point thisP = g.toScreen(calcLocationFor(_xMin, _yMin));

			// handle unable to gen screen coords (if off visible area)
			if (thisP == null)
				return;

			xPoints[ctr] = thisP.x;
			yPoints[ctr++] = thisP.y;

			thisP = g.toScreen(calcLocationFor(_xMin, _yMax + 1));
			xPoints[ctr] = thisP.x;
			yPoints[ctr++] = thisP.y;

			thisP = g.toScreen(calcLocationFor(_xMax + 1, _yMax + 1));
			xPoints[ctr] = thisP.x;
			yPoints[ctr++] = thisP.y;

			thisP = g.toScreen(calcLocationFor(_xMax + 1, _yMin));
			xPoints[ctr] = thisP.x;
			yPoints[ctr++] = thisP.y;

			g.setColor(_fillColor);
			g.fillPolygon(xPoints, yPoints, xPoints.length);
		}

		// ok, draw the vertical lines
		for (int x = _xMin; x <= _xMax + 1; x++) {
			// set the normal color
			g.setColor(_myColor);

			if (_plotLines) {
				// sort out the line style
				g.setLineStyle(_lineStyle);

				final Point start = new Point(g.toScreen(calcLocationFor(x, _yMin)));
				final Point end = new Point(g.toScreen(calcLocationFor(x, _yMax + 1)));
				g.drawLine(start.x, start.y, end.x, end.y);
			}

			if ((x <= _xMax) && _plotLabels) {
				// find the centre-point for the label
				final Point start = new Point(g.toScreen(calcLocationFor(x, _yMin)));
				final Point end = new Point(g.toScreen(calcLocationFor(x + 1, _yMin)));

				final Point centre = new Point(start.x + (end.x - start.x) / 2, start.y);

				// what's this label
				final String thisLbl = labelFor(x);

				// sort out the dimensions of the font
				int ht = g.getStringHeight(_theFont);
				final int wid = g.getStringWidth(_theFont, thisLbl);

				if (dim != null) {
					// sometimes we don't have a dimension - such as when the grid is
					// being dragged
					centre.y = Math.min(centre.y, dim.height - 2 * ht);
				}

				if (getOriginAtTopLeft())
					ht = -ht;

				// set the font color
				g.setColor(_fontColor);

				// and draw it
				g.drawText(_theFont, thisLbl, centre.x - wid / 2, centre.y + ht);
			}

		}

		// ok, now the horizontal lines
		for (int y = _yMin; y <= _yMax + 1; y++) {
			// set the normal color
			g.setColor(_myColor);

			if (_plotLines) {
				// sort out the line style
				g.setLineStyle(_lineStyle);

				final Point start = new Point(g.toScreen(calcLocationFor(_xMin, y)));
				final Point end = new Point(g.toScreen(calcLocationFor(_xMax + 1, y)));
				g.drawLine(start.x, start.y, end.x, end.y);
			}

			if ((y <= _yMax) && _plotLabels) {
				// find the centre-point for the label
				final Point start = new Point(g.toScreen(calcLocationFor(_xMin, y)));
				final Point end = new Point(g.toScreen(calcLocationFor(_xMin, y + 1)));

				final Point centre = new Point(start.x, start.y + (end.y - start.y) / 2);

				// move this into the visible area if it's outside.
				if (dim != null) {
					// sometimes we don't have a dimension - such as when the grid is
					// being dragged
					centre.x = Math.max(centre.x, (int) (dim.getWidth() * 0.03));
				}

				// what's this label
				final String thisLbl = "" + (y + 1);

				// sort out the dimensions of the font
				final int ht = g.getStringHeight(_theFont);
				final int wid = g.getStringWidth(_theFont, thisLbl);

				// set the font color
				g.setColor(_fontColor);

				// and draw it
				g.drawText(_theFont, thisLbl, centre.x - (wid + 2), centre.y + ht / 2);
			}
		}

		// and restore the line width
		g.setLineWidth(oldLineWidth);

	}

	@Override
	public double rangeFrom(final MWC.GenericData.WorldLocation other) {
		final MWC.GenericData.WorldArea wa = new WorldArea(calcLocationFor(_xMin, _yMin),
				calcLocationFor(_xMax + 1, _yMax + 1));
		// doesn't return a sensible distance;
		return wa.rangeFrom(other);
	}

	public void setColor(final Color val) {
		_myColor = val;
	}

	/**
	 * @param fillColor the fillColor to set
	 */
	public void setFillColor(final Color fillColor) {
		_fillColor = fillColor;
	}

	/**
	 * @param fillGrid the fillGrid to set
	 */
	public void setFillGrid(final boolean fillGrid) {
		_fillGrid = fillGrid;
	}

	public void setFont(final Font theFont) {
		_theFont = theFont;
	}

	/**
	 * @param fillColor the fillColor to set
	 */
	public void setFontColor(final Color fontColor) {
		_fontColor = fontColor;
	}

	public void setLineStyle(final int lineStyle) {
		_lineStyle = lineStyle;
	}

	/**
	 * whether to plot the labels or not
	 */
	public void setName(final String name) {
		_myName = name;
	}

	/**
	 * @param orientation the orientation to set
	 */
	public void setOrientation(final double orientation) {
		_orientation = orientation;
	}

	/**
	 * @param origin the origin to set
	 */
	public void setOrigin(final WorldLocation origin) {
		_origin = origin;
	}

	/**
	 * whether to put the origin at the top-left
	 *
	 * @return
	 */
	public void setOriginAtTopLeft(final boolean uSStandard) {
		_originTopLeft = uSStandard;
	}

	/**
	 * whether to plot the labels or not
	 */
	public void setPlotLabels(final boolean val) {
		_plotLabels = val;
	}

	/**
	 * @param plotLines the plotLines to set
	 */
	public void setPlotLines(final boolean plotLines) {
		_plotLines = plotLines;
	}

	@Override
	public void setVisible(final boolean val) {
		_isOn = val;
	}

	/**
	 * set the x delta for this grid
	 *
	 * @param val the size
	 */
	public void setXDelta(final WorldDistance val) {
		_myXDelta = val;
	}

	/**
	 * @param max the xMax to set
	 */
	public void setXMax(final String max) {
		_xMax = indexOf(max);
	}

	/**
	 * @param min the xMin to set
	 */
	public void setXMin(final String min) {
		_xMin = indexOf(min);
	}

	/**
	 * set the y delta for this grid
	 *
	 * @param val the size
	 */
	public void setYDelta(final WorldDistance val) {
		_myYDelta = val;
	}

	/**
	 * @param max the yMax to set
	 */
	public void setYMax(final Integer max) {
		_yMax = max - 1;
	}

	/**
	 * @param min the yMin to set
	 */
	public void setYMin(final Integer min) {
		_yMin = min - 1;
	}

	@Override
	public void shift(final WorldVector vector) {
		_origin.addToMe(vector);
	}

	/**
	 * return this item as a string
	 */
	@Override
	public String toString() {
		return getName();
	}

}