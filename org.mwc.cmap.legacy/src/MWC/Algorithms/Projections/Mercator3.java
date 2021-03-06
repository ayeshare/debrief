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

package MWC.Algorithms.Projections;

import java.awt.Dimension;
import java.io.Serializable;

import MWC.Algorithms.Conversions;
import MWC.Algorithms.PlainProjection;
import MWC.GUI.Editable;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

public class Mercator3 extends PlainProjection {
	/////////////////////////////////////////////////////////////
	// nested class
	////////////////////////////////////////////////////////////
	protected class Ellipse implements Serializable {
		/**
			 *
			 */
		private static final long serialVersionUID = 1L;
		final protected double _eccent;
		final protected double _radius; // nautical metres

		public Ellipse() {
			_eccent = 0.0818191908;
			_radius = 6378137;
		}

		public double getEccentricity() {
			return _eccent;
		}

		public double getRadius() {
			return _radius;
		}
	}

	/////////////////////////////////////////////////////////////
	// member variables
	////////////////////////////////////////////////////////////

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	// the current ellipse
	protected Ellipse _myEllipse;
	//////////////////////////////////////////////////////////
	// characteristics of the current ellipsoied
	//////////////////////////////////////////////////////////
	protected double _e; // current eccentricity

	protected double _a; // current equatorial radius
	///////////////////////////////////////////////////////////
	// terms which are dependent on the current ellipsoid
	///////////////////////////////////////////////////////////
	protected double _A0;
	protected double _A2;
	protected double _A4;

	protected double _A6;
	// OR sys algorithms
	protected double _centralMeridian = 12.8;

	protected double _er = 6378137 / 1852;
	protected double _e2;
	protected double _e4;

	protected double _e6;

	protected double _t1;
	///////////////////////////////////////////////////////////
	// terms which are dependent on the current screen resolution
	///////////////////////////////////////////////////////////
	protected double _mp; // one meridional part for this // screen size & data area
	protected double _mpLowerLat;

	/**
	 * one meridional part for the lower limit of latitude
	 */
	protected double _lgTerm; // length of meridional part

	protected double _scaleVal;

	final protected double _1_over_60; // a 60th (in rads)

	/////////////////////////////////////////////////////////////
	// constructor
	////////////////////////////////////////////////////////////
	public Mercator3() {
		super("Mercator");

		// calculate our constants
		_1_over_60 = Conversions.Degs2Rads(1 / 60.0);

		setEllipse(new Ellipse());

	}

	@Override
	public Editable.EditorType getInfo() {
		return null;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public boolean hasEditor() {
		return false;
	}

	protected double length(final double val) {
		final double lat = Conversions.Degs2Rads(val);

		double res;
		res = _a * (_A0 * lat - _A2 * Math.sin(2 * lat) + _A4 * Math.sin(4 * lat) - _A6 * Math.sin(6 * lat));
		return res;
	}

	protected double lg(final double val1, final double val2) {
		final double lat1 = Conversions.Degs2Rads(val1);
		final double lat2 = Conversions.Degs2Rads(val2);

		double res;

		final double t2 = 1 - (_e2 / 4) * (1 + 3 * Math.cos(lat1 + lat2));

		res = _t1 * t2;

		return res;
	}

	protected double mParts(final double val) {

		final double lat = Conversions.Degs2Rads(val);

		double res;

		res = 10800 / Math.PI * (Math.log(Math.tan(Math.PI / 4 + lat / 2)) - _e2 * Math.sin(lat)
				- 1 / 3 * _e4 * Math.sin(Math.pow(lat, 3)) - 1 / 5 * _e6 * Math.sin(Math.pow(lat, 5)));

		return res;
	}

	protected void rescale() {
		final WorldArea wa = super.getDataArea();
		final Dimension sz = super.getScreenArea();

		if ((wa == null) || (sz == null))
			return;

		// find the x scale factor
		final double dx = getScreenArea().width / (getDataArea().getWidth());// * _dataBorder);
		// find the y scale factor
		final double dy = getScreenArea().height / (getDataArea().getHeight());// * _dataBorder);

		// find the maximum of these
		_scaleVal = Math.min(dx, dy);

		// calculate the length of one meridional part at this lat

		_mp = sz.height / (mParts(wa.getTopLeft().getLat()) - mParts(wa.getBottomRight().getLat()));

		// calculate the length of a meridional part at this lat
		_mpLowerLat = mParts(wa.getBottomRight().getLat());

		// calculate the length of the meridional part
		_lgTerm = lg(wa.getBottomRight().getLat(), wa.getTopLeft().getLat()) / 60;

		_centralMeridian = wa.getBottomRight().getLat();
	}

	@Override
	public void setDataArea(final WorldArea theArea) {
		super.setDataArea(theArea);

		rescale();
	}

	/////////////////////////////////////////////////////////////
	// member functions
	////////////////////////////////////////////////////////////
	public void setEllipse(final Ellipse val) {
		// store it
		_myEllipse = val;

		_e = val.getEccentricity();
		_a = val.getRadius();

		// fill out the rest of our terms (powers of eccentricity)
		_e2 = Math.pow(_e, 2);
		_e4 = Math.pow(_e, 4);
		_e6 = Math.pow(_e, 6);

		// and now the big A numbers
		_A0 = 1 - 1 / 4 * _e2 - 3 / 64 * _e4 - 5 / 256 * _e6;

		_A2 = 3 / 8 * (_e2 + 1 / 2 * _e4 + 15 / 128 * _e6);

		_A4 = 15 / 256 * (_e4 + 3 / 4 * _e6);
		_A6 = 35 / 3072 * _e6;

		_t1 = _a * Math.sin(_1_over_60);
	}

	@Override
	public void setScreenArea(final java.awt.Dimension theArea) {
		super.setScreenArea(theArea);

		rescale();
	}

	@Override
	public java.awt.Point toScreen(final WorldLocation val) {

		final double _easting = 0;
		final double _northing = 0;

		final double d1 = val.getLong();

		double d2 = val.getLat();

		for (; d2 - _centralMeridian < -180.0; d2 += 360.0) {
			/* null body */
		}

		for (; d2 - _centralMeridian > 180.0; d2 -= 360.0) {
			/* null body */
		}

		final double d3 = (d2 - _centralMeridian) * 0.01745329251994 * _er;

		final double d4 = d1 * 0.01745329251994;

		final double d5 = Math.pow((1.0 - _e * Math.sin(d4)) / (1.0 + _e * Math.sin(d4)), _e / 2);

		final double d6 = Math.log(Math.tan((45.0 + 0.5 * d1) * 0.01745329251994) * d5) * _er;

		return new java.awt.Point((int) (d3 + _easting), (int) (d6 + _northing));

		/*
		 * java.awt.Point res = null;
		 *
		 * if(_scaleVal == 0) return res;
		 *
		 * WorldArea wa = getDataArea(); Dimension sz = getScreenArea();
		 *
		 * double x = (val.getLong() - wa.getTopLeft().getLong()) * _scaleVal;
		 *
		 * double y = (mParts(val.getLat()) - _mpLowerLat) * _mp;
		 *
		 * // invert the y axis y = sz.height - y;
		 *
		 * res = new Point((int)x, (int)y);
		 *
		 * return res;
		 */
	}

	@Override
	public WorldLocation toWorld(final java.awt.Point val) {

		int i;

		double d9;

		double d10;

		final double d1 = val.x - 0;

		final double d2 = val.y - 0;

		double d3 = 0.0;

		final double d4 = d1 / _er / 0.01745329251994 + _centralMeridian;

		final double d5 = Math.exp(-d2 / _er);

		double d6 = 90.0 - 2.0 * Math.atan(d5) / 0.01745329251994;

		double d7 = 1.0;

		final double d8 = 0.00001;

		for (i = 0; d7 > d8 && i < 100; i++)

		{

			d9 = Math.sin(d6 * 0.01745329251994);

			d10 = (1.0 - _e * d9) / (1.0 + _e * d9);

			d3 = 90.0 - 2.0 * Math.atan(d5 * Math.pow(d10, _e / 2)) / 0.01745329251994;

			d7 = Math.abs(Math.abs(d3) - Math.abs(d6));

			d6 = d3;

		}

		return new WorldLocation(d3, d4, 0);

		/*
		 * WorldLocation res = null;
		 *
		 * if(_scaleVal == 0) return res;
		 *
		 * WorldArea wa = getDataArea(); Dimension sz = getScreenArea();
		 *
		 * // invert y axis double y = sz.height - val.y;
		 *
		 * double lng = val.x / _scaleVal + wa.getTopLeft().getLong();
		 *
		 * double mLat = y / _mp + _mpLowerLat;
		 *
		 * // now steve's new bit double lat2 = -80; double latOld = lat2; boolean
		 * foundLat = false; while((lat2 < 80) && (foundLat == false)) { double mp =
		 * mParts(lat2); if(mp > mLat) { foundLat = true; } else { latOld = lat2; lat2 =
		 * lat2 + 1; } }
		 *
		 * double co = 0; double dLat = (lat2 - latOld) / 10.0; while(dLat > 0.00000001)
		 * { foundLat = false; if((co < 10) && (foundLat == false)) { lat2 = latOld +
		 * dLat; co = co + 1; if(((long)mParts(lat2) * 1000000) >= ((long)mLat *
		 * 1000000)) { foundLat = true; dLat = (lat2 - latOld) / 10; co = 0; } else {
		 * latOld = lat2; } } }
		 *
		 *
		 * //double lat = mLat * _lgTerm;
		 *
		 * res = new WorldLocation(lat2, lng, 0);
		 *
		 * return res;
		 */
	}

	@Override
	public void zoom(final double value) {
		if (value != 0.0) {
			_scaleVal /= value;
		} else {
			rescale();
		}
	}

}
