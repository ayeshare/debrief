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

// $RCSfile: EllipseShape.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.5 $
// $Log: EllipseShape.java,v $
// Revision 1.5  2006/04/21 07:48:36  Ian.Mayo
// Make things draggable
//
// Revision 1.4  2006/03/22 10:45:06  Ian.Mayo
// Rename properties in correct order, tidy tests
//
// Revision 1.3  2004/08/31 09:38:15  Ian.Mayo
// Rename inner static tests to match signature **Test to make automated testing more consistent
//
// Revision 1.2  2004/05/25 15:37:12  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:22  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:33  Ian.Mayo
// Initial import
//
// Revision 1.14  2003-07-04 11:00:50+01  ian_mayo
// Reflect name change of parent editor test
//
// Revision 1.13  2003-07-03 14:59:53+01  ian_mayo
// Reflect new signature of PlainShape constructor, where we don't need to set the default colour
//
// Revision 1.12  2003-06-25 15:41:35+01  ian_mayo
// Only measure rangeFrom when ellipse is visible
//
// Revision 1.11  2003-06-25 08:50:49+01  ian_mayo
// Minor tidying
//
// Revision 1.10  2003-06-23 08:30:08+01  ian_mayo
// Only calculate range from if our centre has been set
//
// Revision 1.9  2003-06-20 11:28:37+01  ian_mayo
// Only calc points on ellipse if centre has been set.
//
// Revision 1.8  2003-03-18 12:07:19+00  ian_mayo
// extended support for transparent filled shapes
//
// Revision 1.7  2003-03-03 11:54:32+00  ian_mayo
// Implement filled shape management
//
// Revision 1.6  2003-01-23 16:04:30+00  ian_mayo
// provide methods to return the shape as a series of segments
//
// Revision 1.5  2003-01-21 16:32:11+00  ian_mayo
// move getColor property management to ShapeWrapper
//
// Revision 1.4  2002-10-30 16:26:55+00  ian_mayo
// tidy (shorten) up display names for editables
//
// Revision 1.3  2002-07-18 15:23:57+01  ian_mayo
// calc ellipse points by adding Vector to origin, rather than x-y components in degrees, so that ellipses stay correctly proportioned as positioned further from equator
//
// Revision 1.2  2002-05-28 09:25:52+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:23+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:01:07+01  ian_mayo
// Initial revision
//
// Revision 1.4  2002-03-19 11:04:05+00  administrator
// Add a "type" property to indicate type of shape (label, rectangle, etc)
//
// Revision 1.3  2002-03-15 09:14:31+00  administrator
// Don't re-assign maxima to be the max of maxima & minima, just accept the maxima but use it as the orientation
//
// Revision 1.2  2002-02-19 20:30:51+00  administrator
// make clear that units for orientation are degs
//
// Revision 1.1  2002-01-17 20:40:26+00  administrator
// Reflect switch to Duration/WorldDistance
//
// Revision 1.0  2001-07-17 08:43:16+01  administrator
// Initial revision
//
// Revision 1.3  2001-01-22 19:40:36+00  novatech
// reflect optimised projection.toScreen plotting
//
// Revision 1.2  2001-01-22 12:29:27+00  novatech
// added JUnit testing code
//
// Revision 1.1  2001-01-03 13:42:22+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:49:09  ianmayo
// initial version
//
// Revision 1.12  2000-11-17 09:34:47+00  ian_mayo
// corrected processing of new location
//
// Revision 1.11  2000-09-21 09:06:47+01  ian_mayo
// make Editable.EditorType a transient parameter, to prevent it being written to file
//
// Revision 1.10  2000-08-18 13:36:01+01  ian_mayo
// implement singleton of Editable.EditorType
//
// Revision 1.9  2000-08-14 15:49:29+01  ian_mayo
// tidy up descriptions
//
// Revision 1.8  2000-08-11 08:41:58+01  ian_mayo
// tidy beaninfo
//
// Revision 1.7  2000-02-03 15:08:12+00  ian_mayo
// First issue to Devron (modified files are mostly related to WMF)
//
// Revision 1.6  1999-11-26 15:45:03+00  ian_mayo
// adding toString method
//
// Revision 1.5  1999-11-25 16:54:18+00  ian_mayo
// clarified parameter labels for beanInfo
//
// Revision 1.4  1999-11-18 11:11:19+00  ian_mayo
// minima/maxima typo correction
//
// Revision 1.3  1999-11-11 18:15:59+00  ian_mayo
// tidied up comments & fields
//
// Revision 1.2  1999-11-09 11:26:18+00  ian_mayo
// new file
//
//

package MWC.GUI.Shapes;

import java.awt.Color;
import java.awt.Point;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.Vector;

import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.ExtendedCanvasType;
import MWC.GUI.Layer;
import MWC.GUI.PlainWrapper;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

public class EllipseShape extends PlainShape implements Editable, HasDraggableComponents {

	//////////////////////////////////////////////////
	// member variables
	//////////////////////////////////////////////////

	//////////////////////////////////////////////////////
	// bean info for this class
	/////////////////////////////////////////////////////
	public class EllipseInfo extends Editable.EditorType {

		public EllipseInfo(final EllipseShape data, final String theName) {
			super(data, theName, "");
		}

		@Override
		public PropertyDescriptor[] getPropertyDescriptors() {
			try {
				final PropertyDescriptor[] res = { prop("Maxima", "the Ellipse maxima"),
						prop("Minima", "the Ellipse minima"), prop("Orientation", "the Ellipse orientation (degrees)"),
						prop("Centre", "the centre of the Ellipse"), prop("Filled", "whether to fill the Ellipse"),
						displayProp("SemiTransparent", "Semi transparent",
								"whether the filled Ellipse is semi-transparent") };

				return res;

			} catch (final IntrospectionException e) {
				return super.getPropertyDescriptors();
			}
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	//////////////////////////////////////////////////////////////////////////////////////////////////
	static public class EllipseTest extends junit.framework.TestCase {
		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		public EllipseTest(final String val) {
			super(val);
		}

		public void testMyParams() {
			MWC.GUI.Editable ed = new EllipseShape(new WorldLocation(2d, 2d, 2d), 12d,
					new WorldDistance(12d, WorldDistance.DEGS), new WorldDistance(12d, WorldDistance.DEGS));
			MWC.GUI.Editable.editableTesterSupport.testParams(ed, this);
			ed = null;
		}
	}

	// keep track of versions
	static final long serialVersionUID = 1;

	/**
	 * the area covered by this Ellipse
	 */
	private WorldArea _theArea;

	/**
	 * the centre of this Ellipse
	 */
	private WorldLocation _theCentre;

	private final WorldLocation _topPoint;

	private final WorldLocation _midLeft;

	/**
	 * the maxima of this Ellipse (in degs)
	 */
	private WorldDistance _theMaxima;

	/**
	 * the minima of this Ellipse (in degs)
	 */
	private WorldDistance _theMinima;

	/**
	 * the series of world locations which represent this ellipse
	 */
	private Vector<WorldLocation> _theDataPoints;

	//////////////////////////////////////////////////
	// constructor
	//////////////////////////////////////////////////

	/**
	 * the orientation of the ellipse (in degrees)
	 */
	private double _theOrientation;

	/**
	 * our editor
	 */
	transient private Editable.EditorType _myEditor;

	/**
	 * constructor
	 *
	 * @param theCentre the WorldLocation marking the centre of the Ellipse
	 * @param theMaxima the length of the Maxima (in degs)
	 * @param theMinima the length of the minima of the ellipse (in degs)
	 * @param theOrient the orientation of the ellipse (in degs)
	 */
	public EllipseShape(final WorldLocation theCentre, final double theOrient, final WorldDistance theMaxima,
			final WorldDistance theMinima) {
		super(0, "Ellipse");

		// store the values
		_theCentre = theCentre;
		_theOrientation = theOrient;
		_theMinima = theMinima;
		_theMaxima = theMaxima;
		final double maxDist = Math.max(_theMaxima.getValueIn(WorldDistance.DEGS),
				_theMinima.getValueIn(WorldDistance.DEGS));

		// create & extend to top left
		_topPoint = _theCentre.add(new WorldVector(0, maxDist, 0));
		_topPoint.addToMe(new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(270), maxDist, 0));

		// create & extend to bottom right

		final double minDist = Math.min(_theMaxima.getValueIn(WorldDistance.DEGS),
				_theMinima.getValueIn(WorldDistance.DEGS));

		// create & extend to top left
		_midLeft = _theCentre.add(new WorldVector(0, minDist, 0));
		_midLeft.addToMe(new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(270), minDist, 0));

		// now represented our Ellipse as an area
		calcPoints();
	}
	//////////////////////////////////////////////////
	// member functions
	//////////////////////////////////////////////////

	/**
	 * calculate the shape as a series of WorldLocation points. Joined up, these
	 * form a representation of the shape
	 */
	private Vector<WorldLocation> calcDataPoints() {
		// get ready to store the list
		final Vector<WorldLocation> res = new Vector<WorldLocation>(0, 1);

		// produce the orientation in radians
		final double orient = MWC.Algorithms.Conversions.Degs2Rads(_theOrientation);

		for (int i = 0; i <= CircleShape.NUM_SEGMENTS; i++) {

			// produce the current bearing
			final double this_brg = (360.0 / CircleShape.NUM_SEGMENTS * i) / 180.0 * Math.PI;

			// first produce a standard ellipse of the correct size
			final double x1 = Math.sin(this_brg) * _theMaxima.getValueIn(WorldDistance.DEGS);
			double y1 = Math.cos(this_brg) * _theMinima.getValueIn(WorldDistance.DEGS);

			// now produce the range out to the edge of the ellipse at
			// this point
			final double r = Math.sqrt(Math.pow(x1, 2) + Math.pow(y1, 2));

			// to prevent div/0 error in atan, make y1 small if zero
			if (y1 == 0)
				y1 = 0.0000001;

			// and the new bearing to the correct point on the ellipse
			final double tr = Math.atan2(y1, x1) + orient;

			// use our "add" function to add a vector, rather than the
			// x-y components as we did, so that the ellipse stays correctly
			// shaped as it travels further from the equator.
			final WorldLocation wl = _theCentre.add(new WorldVector(tr, r, 0));

			res.add(wl);
		}

		return res;

	}

	/**
	 * calculate some convenience values based on the radius and centre of the
	 * Ellipse
	 */
	protected void calcPoints() {
		// check we have a centre point
		if (_theCentre == null)
			return;

		// create our area
		_theArea = new WorldArea(_theCentre, _theCentre);

		final double dist = Math.max(_theMaxima.getValueIn(WorldDistance.DEGS),
				_theMinima.getValueIn(WorldDistance.DEGS));

		// create & extend to top left
		WorldLocation other = _theCentre.add(new WorldVector(0, dist, 0));
		other.addToMe(new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(270), dist, 0));
		_theArea.extend(other);

		// create & extend to bottom right
		other = _theCentre.add(new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(180), dist, 0));
		other.addToMe(new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(90), dist, 0));
		_theArea.extend(other);

		// and produce the list of points
		_theDataPoints = calcDataPoints();
	}

	@Override
	public void findNearestHotSpotIn(final Point cursorPos, final WorldLocation cursorLoc,
			final ComponentConstruct currentNearest, final Layer parentLayer) {
		checkThisOne(_topPoint, cursorLoc, currentNearest, this, parentLayer);
		checkThisOne(_midLeft, cursorLoc, currentNearest, this, parentLayer);

		// now for the more difficult one. See if it is on the radius.
		// - how far is it from the centre
		final WorldVector vec = cursorLoc.subtract(_theCentre);
		final WorldDistance sep = new WorldDistance(vec);

		// ahh, now subtract the maxima from this separation
		final WorldDistance newSep1 = new WorldDistance(
				Math.abs(sep.getValueIn(WorldDistance.YARDS) - this._theMaxima.getValueIn(WorldDistance.YARDS)),
				WorldDistance.YARDS);

		final WorldDistance newSep2 = new WorldDistance(
				Math.abs(sep.getValueIn(WorldDistance.YARDS) - this._theMinima.getValueIn(WorldDistance.YARDS)),
				WorldDistance.YARDS);

		// now we have to wrap this operation in a made-up location
		final WorldLocation dragMaxima = new WorldLocation(cursorLoc) {
			private static final long serialVersionUID = 100L;

			@Override
			public void addToMe(final WorldVector delta) {
				// ok - process the drag
				super.addToMe(delta);
				// ok, what's this distance from the origin?
				final WorldVector newSep1 = subtract(_theCentre);
				final WorldDistance dist = new WorldDistance(newSep1);
				setMaxima(dist);

				// WorldDistance newDist = new
				// WorldDistance(dist.getValueIn(WorldDistance.YARDS) + _theRadius,
				// WorldDistance.YARDS);
				// hmm, are we going in or out?
				// now, change the radius to this
			}
		};
		final WorldLocation dragMinima = new WorldLocation(cursorLoc) {
			private static final long serialVersionUID = 100L;

			@Override
			public void addToMe(final WorldVector delta) {
				// ok - process the drag
				super.addToMe(delta);
				// ok, what's this distance from the origin?
				final WorldVector newSep1 = subtract(_theCentre);
				final WorldDistance dist = new WorldDistance(newSep1);
				setMinima(dist);

				// WorldDistance newDist = new
				// WorldDistance(dist.getValueIn(WorldDistance.YARDS) + _theRadius,
				// WorldDistance.YARDS);
				// hmm, are we going in or out?
				// now, change the radius to this
			}
		};

		// try range
		currentNearest.checkMe(this, newSep1, null, parentLayer, dragMaxima);
		currentNearest.checkMe(this, newSep2, null, parentLayer, dragMinima);

	}

	/**
	 * get the 'anchor point' for any labels attached to this shape
	 */
	public MWC.GenericData.WorldLocation getAnchor() {
		return _theCentre;
	}

	@Override
	public MWC.GenericData.WorldArea getBounds() {
		return _theArea;
	}

	/**
	 * return the centre of the Ellipse
	 *
	 * @return the centre of the Ellipse
	 */
	public WorldLocation getCentre() {
		return _theCentre;
	}

	public Color getEllipseColor() {
		return super.getColor();
	}

	@Override
	public Editable.EditorType getInfo() {
		if (_myEditor == null)
			_myEditor = new EllipseInfo(this, this.getName());

		return _myEditor;
	}

	/**
	 * return the maxima of the ellipse
	 *
	 * @return the maxima of the ellipse
	 */
	public WorldDistance getMaxima() {
		// convert to yards
		return _theMaxima;
	}

	/**
	 * return the minima of the ellipse
	 *
	 * @return the minima of the ellipse
	 */
	public WorldDistance getMinima() {
		return _theMinima;
	}

	/**
	 * get the orientation (in degs)
	 *
	 * @return the Orientation of the ellipse (in degrees)
	 */
	public double getOrientation() {
		return _theOrientation;
	}

	@Override
	public boolean hasEditor() {
		return true;
	}

	@Override
	public void paint(final CanvasType dest) {
		// are we visible?
		if (!getVisible())
			return;

		// set the colour, if we know it
		if (this.getColor() != null) {
			// create a transparent colour
			final Color newcol = getColor();

			dest.setColor(new Color(newcol.getRed(), newcol.getGreen(), newcol.getBlue(), TRANSPARENCY_SHADE));
		}

		// create a polygon to represent the ellipse (so that we can fill or draw it)
		final int len = _theDataPoints.size();
		final int[] xPoints = new int[len];
		final int[] yPoints = new int[len];

		// work through the list to create the list of screen coordinates
		for (int i = 0; i < _theDataPoints.size(); i++) {
			final WorldLocation location = _theDataPoints.elementAt(i);

			final Point p2 = dest.toScreen(location);

			// handle unable to gen screen coords (if off visible area)
			if (p2 == null)
				return;

			xPoints[i] = p2.x;
			yPoints[i] = p2.y;
		}

		if (getFilled()) {
			if (getSemiTransparent() && dest instanceof ExtendedCanvasType) {
				final ExtendedCanvasType ext = (ExtendedCanvasType) dest;
				ext.semiFillPolygon(xPoints, yPoints, len);
			} else
				dest.fillPolygon(xPoints, yPoints, len);
		} else
			dest.drawPolygon(xPoints, yPoints, len);
	}

	/**
	 * get the range from the indicated world location. We always measure the range
	 * from the centre of the ellipse, but only from the edge if the edge itself is
	 * visible.
	 */
	@Override
	public double rangeFrom(final WorldLocation point) {

		double res = -1;

		// check we have a centre point
		if (_theCentre != null) {
			// start off with the range from the centre
			res = _theCentre.rangeFrom(point);

			// right, we only measure the range from the edge of the ellipse if the shape
			// itself is visible
			if (getVisible()) {

				/**
				 * also pass around the points on the ellipse (albeit at a lower fidelity
				 */
				// number of line segments to compare against
				final int NUM_SEGMENTS_TEST = 50;

				// produce the orientation in radians
				final double orient = MWC.Algorithms.Conversions.Degs2Rads(_theOrientation);

				for (int i = 0; i <= NUM_SEGMENTS_TEST; i++) {
					// produce the current bearing
					final double this_brg = (360.0 / NUM_SEGMENTS_TEST * i) / 180.0 * Math.PI;

					// first produce a standard ellipse of the correct size
					final double x1 = Math.sin(this_brg) * _theMaxima.getValueIn(WorldDistance.DEGS);
					double y1 = Math.cos(this_brg) * _theMinima.getValueIn(WorldDistance.DEGS);

					// now produce the range out to the edge of the ellipse at
					// this point
					final double r = Math.sqrt(Math.pow(x1, 2) + Math.pow(y1, 2));

					// to prevent div/0 error in atan, make y1 small if zero
					if (y1 == 0)
						y1 = 0.0000001;

					// and the new bearing to the correct point on the ellipse
					final double tr = Math.atan2(y1, x1) + orient;

					// use our "add" function to add a vector, rather than the
					// x-y components as we did, so that the ellipse stays correctly
					// shaped as it travels further from the equator.
					final WorldLocation wl = _theCentre.add(new WorldVector(tr, r, 0));
					final double res2 = wl.rangeFrom(point);

					res = Math.min(res, res2);
				}
			} // whether the ellipse was visible
		}

		return res;
	}

	/**
	 * set the centre location of the Ellipse
	 */
	public void setCentre(final WorldLocation centre) {
		// get an old location
		final WorldLocation oldLocation = _theCentre;

		// make the change
		_theCentre = centre;

		// and calc the new summary data & shape
		calcPoints();

		// inform our listeners
		firePropertyChange(PlainWrapper.LOCATION_CHANGED, oldLocation, _theCentre);
	}

	//////////////////////////////////////////
	// convenience functions which pass calls back to parent
	//////////////////////////////////////////
	public void setEllipseColor(final Color val) {
		super.setColor(val);
	}

	/**
	 * @param val the maxima of the ellipse
	 */
	public void setMaxima(final WorldDistance val) {
		// check we received data
		if (val == null)
			return;

		// convert to yards
		_theMaxima = val;

		// and calc the new summary data
		calcPoints();

		// and inform the parent (so it can move the label)
		firePropertyChange(PlainWrapper.LOCATION_CHANGED, null, null);

	}

	/**
	 * @param val the minima of the ellipse
	 */
	public void setMinima(final WorldDistance val) {
		// check we received data
		if (val == null)
			return;

		// convert to yarda
		_theMinima = val;

		// and calc the new summary data
		calcPoints();

		// and inform the parent (so it can move the label)
		firePropertyChange(PlainWrapper.LOCATION_CHANGED, null, null);

	}

	/**
	 * @param degs the orientation of the ellipse (in degs)
	 */
	public void setOrientation(final double degs) {
		_theOrientation = degs;

		// and calc the new summary data
		calcPoints();

		// and inform the parent (so it can move the label)
		firePropertyChange(PlainWrapper.LOCATION_CHANGED, null, null);

	}

	@Override
	public void shift(final WorldLocation feature, final WorldVector vector) {
		// ok, just shift it...
		feature.addToMe(vector);

		// and calc the new summary data
		calcPoints();

		// and inform the parent (so it can move the label)
		firePropertyChange(PlainWrapper.LOCATION_CHANGED, null, null);

	}

	@Override
	public void shift(final WorldVector vector) {
		final WorldLocation oldCentre = getCentre();
		final WorldLocation newCentre = oldCentre.add(vector);
		setCentre(newCentre);

		// and calc the new summary data
		calcPoints();

		// and inform the parent (so it can move the label)
		firePropertyChange(PlainWrapper.LOCATION_CHANGED, null, null);
	}

}
