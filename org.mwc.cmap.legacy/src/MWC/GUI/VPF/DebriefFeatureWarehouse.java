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

package MWC.GUI.VPF;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: DebriefFeatureWarehouse.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.4 $
// $Log: DebriefFeatureWarehouse.java,v $
// Revision 1.4  2006/01/13 15:27:26  Ian.Mayo
// Eclipse tidying, minor mods to improve serialization
//
// Revision 1.3  2004/10/07 14:23:15  Ian.Mayo
// Reflect fact that enum is now keyword - change our usage to enumer
//
// Revision 1.2  2004/05/25 15:37:22  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:27  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:48  Ian.Mayo
// Initial import
//
// Revision 1.2  2002-05-28 09:26:04+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:13:59+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 13:03:29+01  ian_mayo
// Initial revision
//
// Revision 1.2  2001-07-20 09:27:45+01  administrator
// more tidying up, together with rationalising new methods we're using for performance enhancement
//
// Revision 1.1  2001-07-18 16:01:33+01  administrator
// still plodding along
//
// Revision 1.0  2001-07-17 08:42:50+01  administrator
// Initial revision
//
// Revision 1.1  2001-07-16 14:59:15+01  novatech
// Initial revision
//

import java.awt.Polygon;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import com.bbn.openmap.LatLonPoint;
import com.bbn.openmap.layer.vpf.AreaTable;
import com.bbn.openmap.layer.vpf.CoordFloatString;
import com.bbn.openmap.layer.vpf.CoverageTable;
import com.bbn.openmap.layer.vpf.EdgeTable;
import com.bbn.openmap.layer.vpf.LibrarySelectionTable;
import com.bbn.openmap.layer.vpf.TextTable;
import com.bbn.openmap.layer.vpf.VPFFeatureGraphicWarehouse;

import MWC.GenericData.WorldArea;

/**
 * MWC.GUI.VPF.DebriefFeatureWarehouse
 */
public class DebriefFeatureWarehouse extends VPFFeatureGraphicWarehouse implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * keep track of how many graphic objects we create, to monitor performance
	 */
	public static int counter = 0;

	/**
	 * the canvas we are currently plotting to
	 */
	private MWC.GUI.CanvasType _myCanvas;

	/**
	 * the set of features which we are currently plotting to
	 */
	private Hashtable<String, FeaturePainter> _currentFeatures;

	/**
	 * the vector of features names we create for each new coverage
	 */
	private Vector<String> _currentFeatureList = null;

	/**
	 * a working variable to stop us excessively creating new objects
	 */
	private final MWC.GenericData.WorldLocation _workingLocation = new MWC.GenericData.WorldLocation(0, 0, 0);

	/**
	 * another variable to stop us excessively creating new objects
	 */
	private transient MWC.GenericData.WorldArea _workingArea = null;

	/**
	 * another variable to stop us excessively creating new objects
	 */
	private transient MWC.GenericData.WorldArea _otherWorkingArea = null;

	// ///////////////////////////////////////////////////
	// pair of flags which indicate whether the coastline painters should draw
	// text and/or lines
	// ///////////////////////////////////////////////////
	/**
	 * whether coastline painters should draw text
	 */
	private Boolean _drawText = null;

	/**
	 * whether coastline painters should draw lines
	 */
	private Boolean _drawLines = null;

	// /////////////////////////////////////////////////
	// constructor
	// ///////////////////////////////////////////////////
	/**
	 * constructor, initialise the working variables we use
	 */
	public DebriefFeatureWarehouse() {
		_workingArea = new WorldArea(_workingLocation, _workingLocation);
		_otherWorkingArea = new WorldArea(_workingArea);
	}

	/**
	*
	*/
	@Override
	@SuppressWarnings("rawtypes")
	public void createArea(final CoverageTable covtable, final AreaTable areatable, final Vector facevec,
			final LatLonPoint ll1, final LatLonPoint ll2, final float dpplat, final float dpplon,
			final boolean doAntarcticaWorkaround) {
		this.createArea(covtable, areatable, facevec, ll1, ll2, dpplat, dpplon, doAntarcticaWorkaround, null);
	}

	/**
	 * createArea
	 *
	 * @param covtable    parameter for createArea
	 * @param facevec     parameter for createArea
	 * @param ll2         parameter for createArea
	 * @param dpplon      parameter for createArea
	 * @param featureType the type of feature we are plotting
	 */
	@Override
	@SuppressWarnings({ "rawtypes" })
	public void createArea(final CoverageTable covtable, final AreaTable areatable, final Vector facevec,
			final LatLonPoint ll1, final LatLonPoint ll2, final float dpplat, final float dpplon,
			final boolean doAntarcticaWorkaround, final String featureType) {

		final Vector ipts = new Vector();

		// get the algorithm to collate the edge points for the polygon
		int totalSize = 0;
		try {
			totalSize = areatable.computeEdgePoints(facevec, ipts);
		} catch (final com.bbn.openmap.io.FormatException f) {
			f.printStackTrace();
			return;
		}

		// find out if any of the edges are in our area
		boolean worth_it = false;

		// area any of these shapes within our area?
		final Enumeration theEdges = ipts.elements();

		//
		while (theEdges.hasMoreElements()) {
			final CoordFloatString cfs = (CoordFloatString) theEdges.nextElement();

			if (overlaps(ll1, ll2, cfs)) {
				worth_it = true;
				continue;
			}

		}

		// do we have any valid data?
		if (!worth_it) {
			// no, so drop out
			return;
		}

		// get the colour
		final java.awt.Color res = getColor(featureType);

		// set the colour
		_myCanvas.setColor(res);

		// convert the group of lines into a single polygon
		final java.awt.Polygon poly = extendArea(ipts, totalSize, ll1, ll2, dpplat, dpplon, false,
				_myCanvas.getProjection());

		// increment the shape counter
		counter++;

		// end of stepping through the areas
		_myCanvas.fillPolygon(poly.xpoints, poly.ypoints, poly.xpoints.length);

	}

	/**
	 * edge plotter for when we're not plotting by features this is really only used
	 * for coastlines - which aren't tiled. We can't build up the coastline into a
	 * single large polyline, since it jumps around a little!
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public void createEdge(final CoverageTable c, final EdgeTable edgetable, final Vector edgevec,
			final LatLonPoint ll1, final LatLonPoint ll2, final float dpplat, final float dpplon,
			final CoordFloatString coords) {

		// is the current painter interested in text?
		if (_drawLines != null) {
			final boolean res = _drawLines.booleanValue();
			if (!res)
				return;
		} else {
			// hey the client hasn't set a preference, so lets just paint anyway
		}

		// is this line currently visible?
		if (!isVisible(ll1, ll2, coords))
			return;

		// get the colour
		final java.awt.Color res = getColor(null);

		// set the colour
		_myCanvas.setColor(res);

		// now plot the polygon
		final int len = coords.maxIndex();
		java.awt.Point _lastPoint = null;

		// create working location
		try {
			for (int i = 0; i < len; i++) {
				final float x = coords.getXasFloat(i);
				final float y = coords.getYasFloat(i);

				_workingLocation.setLat(y);
				_workingLocation.setLong(x);
				final java.awt.Point pt = _myCanvas.toScreen(_workingLocation);

				// handle unable to gen screen coords (if off visible area)
				if (pt == null)
					return;

				// xpoints[i] = pt.x;
				// ypoints[i] = pt.y;
				if (_lastPoint != null) {
					_myCanvas.drawLine(_lastPoint.x, _lastPoint.y, pt.x, pt.y);
				}

				_lastPoint = new java.awt.Point(pt);

			}

			// finally plot the polygon
			// _myCanvas.drawPolygon(xpoints, ypoints, len);
		} catch (final Exception E) {
			E.printStackTrace();
		}

	}

	/**
	 * Edge painter. In this implementation we build the edges up into a polygon
	 * which we then plot - this works much more quickly and is an option because we
	 * get the edges in the correctly tiled order.
	 *
	 * @param c           parameter for createEdge
	 * @param edgevec     parameter for createEdge
	 * @param ll2         parameter for createEdge
	 * @param dpplon      parameter for createEdge
	 * @param coords      list of coordinates which make up this edge
	 * @param featureType the type for this feature
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public void createEdge(final CoverageTable c, final EdgeTable edgetable, final Vector edgevec,
			final LatLonPoint ll1, final LatLonPoint ll2, final float dpplat, final float dpplon,
			final CoordFloatString coords, final String featureType) {

		// get this feature painter
		final FeaturePainter fp = _currentFeatures.get(featureType);

		// is this feature currently visible?
		if (!fp.getVisible())
			return;

		// is this line currently visible?
		if (!isVisible(ll1, ll2, coords))
			return;

		// get the colour
		final java.awt.Color res = fp.getColor();

		if (res == null) {
			System.out.println(" not painting!");
			return;
		}

		// set the colour
		_myCanvas.setColor(res);

		// now plot the polygon
		final int len = coords.maxIndex();
		final int[] points = new int[len * 2];

		counter++;

		try {
			for (int i = 0; i < len; i++) {
				final float x = coords.getXasFloat(i);
				final float y = coords.getYasFloat(i);

				_workingLocation.setLat(y);
				_workingLocation.setLong(x);
				final java.awt.Point pt = _myCanvas.toScreen(_workingLocation);

				// handle unable to gen screen coords (if off visible area)
				if (pt == null)
					return;

				points[i * 2] = pt.x;
				points[i * 2 + 1] = pt.y;

			}

			// finally plot the polygon
			_myCanvas.drawPolyline(points);
		} catch (final Exception E) {
			E.printStackTrace();
		}

	}

	@Override
	@SuppressWarnings("rawtypes")
	public void createText(final CoverageTable c, final TextTable texttable, final Vector textvec, final float latitude,
			final float longitude, final String text) {

		// is the current painter interested in text?
		if (_drawText != null) {
			final boolean res = _drawText.booleanValue();
			if (!res)
				return;
		} else {
			// hey the client hasn't set a preference, so lets just paint anyway
		}

		// set the colour
		_myCanvas.setColor(getColor(null));

		// find the screen location
		_workingLocation.setLat(latitude);
		_workingLocation.setLong(longitude);

		counter++;

		// convert to screen coordinates
		final java.awt.Point pt = _myCanvas.toScreen(_workingLocation);

		// handle unable to gen screen coords (if off visible area)
		if (pt == null)
			return;

		// and plot it
		_myCanvas.drawText(text, pt.x, pt.y);

	}

	@Override
	@SuppressWarnings("rawtypes")
	public void createText(final CoverageTable c, final TextTable texttable, final Vector textvec, final float latitude,
			final float longitude, final String text, final String featureType) {
		counter++;

		// get the colour
		final java.awt.Color res = getColor(featureType);

		// set the colour
		_myCanvas.setColor(res);

		// find the screen location
		_workingLocation.setLat(latitude);
		_workingLocation.setLong(longitude);
		final java.awt.Point pt = _myCanvas.toScreen(_workingLocation);

		// handle unable to gen screen coords (if off visible area)
		if (pt == null)
			return;

		// and plot it
		_myCanvas.drawText(text, pt.x, pt.y);
	}

	/**
	 * Return true if we may draw some area features.
	 */
	@Override
	public boolean drawAreaFeatures() {
		return true;
	}

	/**
	 * Return true if we may draw some edge features.
	 */
	@Override
	public boolean drawEdgeFeatures() {
		return true;
	}

	/**
	 * Return true if we may draw some point features.
	 */
	@Override
	public boolean drawPointFeatures() {
		return true;
	}

	/**
	 * Return true if we may draw some text features.
	 */
	@Override
	public boolean drawTextFeatures() {
		return true;
	}

	/**
	 * method to convert multiple polylines into a single area
	 */
	@SuppressWarnings("rawtypes")
	private java.awt.Polygon extendArea(final Vector ipts, final int totalSize, final LatLonPoint ll1,
			final LatLonPoint ll2, final float dpplat, final float dpplon, final boolean doAntarcticaWorkaround,
			final MWC.Algorithms.PlainProjection proj) {
		Polygon res = null;

		int i, j;
		final int size = ipts.size();
		int npts = 0;

		// create the output arrays
		final int[] xlpts = new int[totalSize];
		final int[] ylpts = new int[totalSize];

		boolean antarcticaWorkaround = doAntarcticaWorkaround;
		// only do it if we're in the vicinity
		if (antarcticaWorkaround) {
			antarcticaWorkaround = (ll2.getLatitude() < -62f);
		}

		// step through the areas we've been provided
		for (j = 0; j < size; j++) {
			final CoordFloatString cfs = (CoordFloatString) ipts.elementAt(j);
			int cfscnt = cfs.tcount;
			final int cfssz = cfs.tsize;
			final float cfsvals[] = cfs.vals;
			// see if this line is going clockwise or anti-clockwise
			if (cfscnt > 0) { // normal
				for (i = 0; i < cfscnt; i++) {
					_workingLocation.setLat(cfsvals[i * cfssz + 1]);
					_workingLocation.setLong(cfsvals[i * cfssz]);
					final java.awt.Point pt = proj.toScreen(_workingLocation);
					xlpts[npts] = pt.x;
					ylpts[npts++] = pt.y;
					// res.addPoint(pt.x, pt.y);
				}
			} else { // reverse
				cfscnt *= -1;
				for (i = cfscnt - 1; i >= 0; i--) {
					_workingLocation.setLat(cfsvals[i * cfssz + 1]);
					_workingLocation.setLong(cfsvals[i * cfssz]);
					final java.awt.Point pt = proj.toScreen(_workingLocation);
					xlpts[npts] = pt.x;
					ylpts[npts++] = pt.y;
					// res.addPoint(pt.x, pt.y);
				}
			}
		}

		// pop the data into a polygon (just for tidy storage really)
		res = new java.awt.Polygon(xlpts, ylpts, npts);
		return res;
	}

	/**
	 * get the colour
	 */
	protected java.awt.Color getColor(final String featureType) {
		java.awt.Color res = null;
		final Hashtable<String, FeaturePainter> hash = this._currentFeatures;
		FeaturePainter thisFeature = null;
		if (hash != null) {
			if (featureType == null) {
				final int len = hash.size();
				if (len == 1) {
					if (hash.elements().hasMoreElements()) {
						final Object val = hash.elements().nextElement();
						thisFeature = (FeaturePainter) val;
					}
				}
			} else {
				thisFeature = hash.get(featureType);
			}
		}

		if (thisFeature != null)
			res = thisFeature.getColor();

		return res;
	}

	/**
	 * Get a Vector of Strings listing all the feature types wanted. Returned with
	 * the area features first, then text features, then line features.
	 */
	@Override
	public Vector<String> getFeatures() {
		if (_currentFeatureList == null) {
			_currentFeatureList = new Vector<String>();

			if (_currentFeatures != null) {
				// right, first pass through doing the area items (the ones that end in 'a')
				Enumeration<FeaturePainter> enumer = _currentFeatures.elements();
				while (enumer.hasMoreElements()) {
					final FeaturePainter fp = enumer.nextElement();
					if (fp.getVisible()) {
						final String nm = fp.getFeatureType();
						if (nm.endsWith("a"))
							_currentFeatureList.add(fp.getFeatureType());
					}
				}

				// now pass through doing the others
				enumer = _currentFeatures.elements();
				while (enumer.hasMoreElements()) {
					final FeaturePainter fp = enumer.nextElement();
					if (fp.getVisible()) {
						final String nm = fp.getFeatureType();
						if (!nm.endsWith("a"))
							_currentFeatureList.add(fp.getFeatureType());
					}
				}
			}
		}

		return _currentFeatureList;
	}

	/**
	 * Get the GUI to control different aspects of the warehouse.
	 *
	 * @param lst LibrarySelectionTable to use to get information about the data, if
	 *            needed.
	 */
	@Override
	public java.awt.Component getGUI(final LibrarySelectionTable lst) {
		return null;
	}

	/**
	 * look through the list of coordinate, and see if any are contained within the
	 * currently visible area
	 */
	private boolean isVisible(final LatLonPoint tl, final LatLonPoint br, final CoordFloatString coords) {
		boolean res = false;
		final MWC.GenericData.WorldLocation _tl = new MWC.GenericData.WorldLocation(tl.getLatitude(), tl.getLongitude(),
				0);
		final MWC.GenericData.WorldLocation _br = new MWC.GenericData.WorldLocation(br.getLatitude(), br.getLongitude(),
				0);
		final MWC.GenericData.WorldArea area = new MWC.GenericData.WorldArea(_tl, _br);
		area.normalise();

		for (int i = 0; i < coords.maxIndex(); i++) {
			final float x = coords.getXasFloat(i);
			final float y = coords.getYasFloat(i);

			_workingLocation.setLat(y);
			_workingLocation.setLong(x);

			if (area.contains(_workingLocation)) {
				res = true;
				continue;
			}

		}

		return res;
	}

	private boolean overlaps(final LatLonPoint tl, final LatLonPoint br, final CoordFloatString coords) {
		boolean res = false;
		float maxLat = 0, minLat = 0, maxLong = 0, minLong = 0;
		boolean first = true;

		// create our area of interest
		_workingArea.getTopLeft().setLat(tl.getLatitude());
		_workingArea.getTopLeft().setLong(tl.getLongitude());
		_workingArea.getBottomRight().setLat(br.getLatitude());
		_workingArea.getBottomRight().setLong(br.getLongitude());

		// build up the area of the coords
		final int len = Math.abs(coords.maxIndex());

		for (int i = 0; i < len; i++) {
			// retrieve the x and y values
			final float x = coords.getXasFloat(i);
			final float y = coords.getYasFloat(i);

			// initialise our values if this is the first pass
			if (first) {
				first = false;
				maxLat = minLat = y;
				maxLong = minLong = x;
			} else {
				// else update our extreme values
				minLat = Math.min(minLat, y);
				maxLat = Math.max(maxLat, y);

				minLong = Math.min(minLong, x);
				maxLong = Math.max(maxLong, x);
			}
		}

		// put our limits into the area of this line
		_otherWorkingArea.getTopLeft().setLat(maxLat);
		_otherWorkingArea.getTopLeft().setLong(minLong);
		_otherWorkingArea.getBottomRight().setLat(minLat);
		_otherWorkingArea.getBottomRight().setLong(maxLong);

		// so, we've now got our two areas, see if they overlap
		res = _workingArea.overlaps(_otherWorkingArea);

		// well done!
		return res;

	}

	/**
	 * set the canvas which we want to plot to
	 *
	 * @param canvas the canvas
	 */
	public void setCanvas(final MWC.GUI.CanvasType canvas) {
		_myCanvas = canvas;
	}

	/**
	 * set the coastline painter characteristics
	 */
	public void setCoastlinePainting(final Boolean drawText, final Boolean drawLines) {
		_drawText = drawText;
		_drawLines = drawLines;
	}

	/**
	 * set the list of features which we are currently plotting, together with their
	 * colours
	 *
	 * @param theFeatures the set of features for this coverage
	 */
	public void setCurrentFeatures(final Hashtable<String, FeaturePainter> theFeatures) {
		_currentFeatures = theFeatures;
		_currentFeatureList = null;
	}

}
