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

// $RCSfile: CoverageLayer.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.6 $
// $Log: CoverageLayer.java,v $
// Revision 1.6  2006/05/17 08:43:07  Ian.Mayo
// Minor tidying
//
// Revision 1.5  2006/01/13 15:26:39  Ian.Mayo
// Eclipse tidying
//
// Revision 1.4  2004/10/07 14:23:15  Ian.Mayo
// Reflect fact that enum is now keyword - change our usage to enumer
//
// Revision 1.3  2004/09/03 15:13:28  Ian.Mayo
// Reflect refactored plottable getElements
//
// Revision 1.2  2004/05/25 15:37:20  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:27  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:48  Ian.Mayo
// Initial import
//
// Revision 1.6  2003-03-17 09:30:23+00  ian_mayo
// Decide whether to plot feature depending on if it's in the visible data area, not just the selected data area
//
// Revision 1.5  2002-11-01 14:44:00+00  ian_mayo
// minor tidying
//
// Revision 1.4  2002-10-30 16:27:00+00  ian_mayo
// tidy (shorten) up display names for editables
//
// Revision 1.3  2002-07-12 15:46:04+01  ian_mayo
// Insert minor error trapping
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
// Revision 1.2  2001-07-18 16:01:33+01  administrator
// still plodding along
//
// Revision 1.1  2001-07-17 12:57:34+01  administrator
// simplify, remove need to store Canvas object
//
// Revision 1.0  2001-07-17 08:42:50+01  administrator
// Initial revision
//
// Revision 1.1  2001-07-16 14:59:19+01  novatech
// Initial revision
//

import java.awt.Dimension;
import java.awt.Point;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;

import com.bbn.openmap.layer.vpf.CoverageAttributeTable;
import com.bbn.openmap.layer.vpf.CoverageTable;
import com.bbn.openmap.layer.vpf.LibrarySelectionTable;
import com.bbn.openmap.layer.vpf.VPFFeatureGraphicWarehouse;
import com.bbn.openmap.layer.vpf.VPFGraphicWarehouse;

import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GenericData.WorldLocation;

public class CoverageLayer extends MWC.GUI.BaseLayer {

	///////////////////////////////////////////////////
	// member variables
	///////////////////////////////////////////////////

	/////////////////////////////////
	// a specific instance of coverage layer which plots data which has
	// been read from a reference directory
	/////////////////////////////////
	public static class ReferenceCoverageLayer extends CoverageLayer {
		//
		public class FeaturePainterInfo extends Editable.EditorType implements Serializable {

			/**
				 *
				 */
			private static final long serialVersionUID = 1L;

			public FeaturePainterInfo(final ReferenceCoverageLayer data) {
				super(data, data.getName(), "");
			}

			@Override
			public PropertyDescriptor[] getPropertyDescriptors() {
				try {
					final PropertyDescriptor[] res = { prop("Color", "the Color to draw this Feature"),
							prop("Visible", "whether this feature is visible"),
							displayProp("DrawText", "Draw text", "whether to draw text"),
							displayProp("DrawLine", "Draw line", "whether to paint lines"), };

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
		 * the feature list we want to plot
		 */
		FeaturePainter _myFeature;

		transient private Editable.EditorType _myEditor1;

		/**
		 * the name of this feature
		 */
		private final String _featureType;

		/**
		 * whether to draw lines for this feature
		 */
		private boolean _drawLine = true;

		/**
		 * whether to draw text for this feature
		 */
		private boolean _drawText = true;

		public ReferenceCoverageLayer(final LibrarySelectionTable LST, final VPFGraphicWarehouse warehouse,
				final String coverageType, final String featureType, final String description,
				final FeaturePainter theFeature) {
			super(LST, warehouse, coverageType);

			_myFeature = theFeature;
			_featureType = featureType;

			setName(description);
		}

		/**
		 * paint operation, used to hide whether this class does a drawTile, or a
		 * drawFeature
		 */
		@Override
		protected void doDraw(final LibrarySelectionTable lst, final int scale, final int screenwidth,
				final int screenheight, final String covname, final VPFFeatureGraphicWarehouse warehouse,
				final com.bbn.openmap.LatLonPoint ll1, final com.bbn.openmap.LatLonPoint ll2) {
			// set the painting characteristics in the painting warehouse
			super._myWarehouse.setCoastlinePainting(new Boolean(_drawText), new Boolean(_drawLine));

			// draw tile by tile
			lst.drawTile(scale, screenwidth, screenheight, covname, warehouse, ll1, ll2);

			// clear the painting characteristics in the painting warehouse
			super._myWarehouse.setCoastlinePainting(null, null);

		}

		public java.awt.Color getColor() {
			return _myFeature.getColor();
		}

		public boolean getDrawLine() {
			return _drawLine;
		}

		public boolean getDrawText() {
			return _drawText;
		}

		/**
		 * accessor to get the list of features we want to plot
		 */
		@Override
		protected Hashtable<String, FeaturePainter> getFeatureHash() {
			final Hashtable<String, FeaturePainter> res = new Hashtable<String, FeaturePainter>();
			res.put(_featureType, _myFeature);
			return res;
		}

		@Override
		public Editable.EditorType getInfo() {
			if (_myEditor1 == null)
				_myEditor1 = new FeaturePainterInfo(this);

			return _myEditor1;
		}

		public void setColor(final java.awt.Color color) {
			_myFeature.setColor(color);
		}

		public void setDrawLine(final boolean val) {
			_drawLine = val;
		}

		public void setDrawText(final boolean val) {
			_drawText = val;
		}

	}

	/**
		 *
		 */
	private static final long serialVersionUID = 1L;

	/**
	 * the library selection table we view
	 */
	transient LibrarySelectionTable _myLST = null;

	/**
	 * the graphic warehouse, which does the plotting for us
	 */
	DebriefFeatureWarehouse _myWarehouse;

	/**
	 * the type of coverage we are plotting
	 */
	private String _myType = null;

	///////////////////////////////////////////////////
	// constructor
	///////////////////////////////////////////////////

	/**
	 * the list of features which we pass to the painter
	 */
	private Hashtable<String, FeaturePainter> _myFeatureHash;

	public CoverageLayer(final LibrarySelectionTable LST, final VPFGraphicWarehouse warehouse,
			final String coverageType) {
		_myType = coverageType;

		setVisible(false);

		// store the other data
		_myWarehouse = (DebriefFeatureWarehouse) warehouse;
		_myLST = LST;

		try {
			if (_myLST != null)
				setName(_myLST.getDescription(_myType));
		} catch (final Exception fe) {
			fe.printStackTrace();
		}
	}

	@SuppressWarnings({ "rawtypes" })
	public CoverageLayer(final LibrarySelectionTable LST, final VPFGraphicWarehouse warehouse,
			final String coverageType, final CoverageAttributeTable cat) {

		this(LST, warehouse, coverageType);

		// we now pass through the coverages, adding a feature painter for each layer
		// now get the features inside this coverage
		final CoverageTable ct = cat.getCoverageTable(_myType);

		// get the list of features in this coverage
		final Hashtable hash = ct.getFeatureTypeInfo();

		try {
			final Enumeration enumer = hash.keys();
			while (enumer.hasMoreElements()) {
				final String thisFeature = (String) enumer.nextElement();

				final String description = _myLST.getDescription(thisFeature);
				final FeaturePainter fp = new FeaturePainter(thisFeature, description);
				this.add(fp);
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
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

	/**
	 * paint operation, used to hide whether this class does a drawTile, or a
	 * drawFeature
	 */
	protected void doDraw(final LibrarySelectionTable lst, final int scale, final int screenwidth,
			final int screenheight, final String covname, final VPFFeatureGraphicWarehouse warehouse,
			final com.bbn.openmap.LatLonPoint ll1, final com.bbn.openmap.LatLonPoint ll2) {
		lst.drawFeatures(scale, screenwidth, screenheight, covname, warehouse, ll1, ll2);
	}

	/**
	 * accessor to get the list of features we want to plot
	 */
	protected Hashtable<String, FeaturePainter> getFeatureHash() {
		if (_myFeatureHash == null) {
			_myFeatureHash = new Hashtable<String, FeaturePainter>();
			final Enumeration<Editable> enumer = this.elements();
			while (enumer.hasMoreElements()) {
				final FeaturePainter fp = (FeaturePainter) enumer.nextElement();
				_myFeatureHash.put(fp.getFeatureType(), fp);
			}
		}

		return _myFeatureHash;
	}

	/**
	 * get the coverage type
	 */
	public String getType() {
		return _myType;
	}

	@Override
	public void paint(final CanvasType g) {

		// check we are visible
		if (!getVisible())
			return;

		if (_myLST == null) {
			System.err.println("No LST for:" + _myType);
		} else {
			final float oldWid = g.getLineWidth();
			g.setLineWidth(this.getLineThickness());

			final MWC.GenericData.WorldArea area = g.getProjection().getVisibleDataArea();

			// check that an area has been created
			if (area == null)
				return;

			// get the feature list
			final Hashtable<String, FeaturePainter> _myFeatures = getFeatureHash();

			// put our data into the warehouse
			_myWarehouse.setCurrentFeatures(_myFeatures);

			_myWarehouse.setCanvas(g);

			// SPECIAL PROCESSING
			// we determine the bounds of the visible area, but on occasion the perspective
			// means that areas further from the equator are shown on the plot but they're
			// outside what's described as the visible area. So, ensure the bounds rectangle
			// represents the outer corners of the area

			// top left
			final Dimension scr = g.getProjection().getScreenArea();
			final WorldLocation tl2 = new WorldLocation(g.getProjection().toWorld(new Point(0, 0)));
			final WorldLocation tl2a = new WorldLocation(g.getProjection().toWorld(new Point(0, scr.height)));
			tl2.setLong(Math.min(tl2.getLong(), tl2a.getLong()));
			tl2.setLat(tl2.getLat());

			// bottom right
			final WorldLocation br2 = new WorldLocation(g.getProjection().toWorld(new Point(scr.width, 0)));
			final WorldLocation br2a = new WorldLocation(g.getProjection().toWorld(new Point(scr.width, scr.height)));
			br2.setLong(Math.max(br2.getLong(), br2a.getLong()));
			br2.setLat(br2a.getLat());

			// put the coordinates into bbn objects
			final com.bbn.openmap.LatLonPoint tlp = new com.bbn.openmap.LatLonPoint(tl2.getLat(), tl2.getLong());
			final com.bbn.openmap.LatLonPoint brp = new com.bbn.openmap.LatLonPoint(br2.getLat(), br2.getLong());

			// how many yards wide is this?
			final int data_wid = (int) MWC.Algorithms.Conversions.Degs2Yds(area.getWidth());

			final java.awt.Dimension dim = g.getProjection().getScreenArea();

			// catch the occasional error we get when first paining a new layer
			try {
				// and start the repaint
				doDraw(_myLST, data_wid, dim.width, dim.height, _myType, _myWarehouse, tlp, brp);
			} catch (final java.lang.NullPointerException ne) {
				MWC.Utilities.Errors.Trace.trace("Error painting VPF for first time", false);
			}

			g.setLineWidth(oldWid);
		}

	}

	/**
	 * set the description of this coverage
	 */
	public void setDescription(final String desc) {
		setName(desc);
	}

	/**
	 * set the library selection table, to allow deferred creation
	 */
	public void setLST(final LibrarySelectionTable lst) {
		_myLST = lst;
	}

}
