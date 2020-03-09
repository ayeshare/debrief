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

package com.planetmayo.debrief.satc.util;

import java.util.ArrayList;

import com.planetmayo.debrief.satc.model.GeoPoint;
import com.planetmayo.debrief.satc.model.states.LocationRange;
import com.planetmayo.debrief.satc.util.calculator.GeoCalculatorType;
import com.planetmayo.debrief.satc.util.calculator.GeodeticCalculator;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;

/**
 * utility class providing geospatial support
 *
 * @author ian
 *
 */
public class GeoSupport {
	private static volatile GeoCalculatorType _calculatorType = GeoCalculatorType.FAST;

	/**
	 * relax the precision model down from 16 sig places, since we were getting
	 * "found non-noded intersection" issues while doing a JTS intersection
	 */
	private static final GeometryFactory _factory = new GeometryFactory(new PrecisionModel(1000000));
//  private static final GeometryFactory _factory = new GeometryFactory();

	public static double convertToCompassAngle(final double angle) {
		return MathUtils.normalizeAngle(Math.PI / 2 - angle);
	}

	public static GeodeticCalculator createCalculator() {
		return _calculatorType.create();
	}

	public static Point createPoint(final double lon, final double lat) {
		return getFactory().createPoint(new Coordinate(lon, lat));
	}

	public static String decimalToDMS(double coord) {

		String output, degrees, minutes, seconds;

		double mod = coord % 1;
		int intPart = (int) coord;

		degrees = String.valueOf(intPart);

		coord = mod * 60;
		mod = coord % 1;
		intPart = (int) coord;

		minutes = String.valueOf(intPart);

		coord = mod * 60;
		intPart = (int) coord;

		seconds = String.valueOf(Math.round(coord * 100.0) / 100.0);

		output = degrees + "\u00B0 " + minutes + "' " + seconds + "\" ";
		return output;

	}

	public static double dmsToDecimal(final double degree, final double minutes, final double seconds) {
		return degree + ((seconds / 60) + minutes) / 60;
	}

	public static String formatGeoPoint(final GeoPoint geoPoint) {
		final double _lat = geoPoint.getLat();
		final double _lon = geoPoint.getLon();

		final String latitudeStr = decimalToDMS(Math.abs(_lat)) + (_lat < 0 ? "S" : "N");
		final String longitudeStr = decimalToDMS(Math.abs(_lon)) + (_lon < 0 ? "W" : "E");
		return latitudeStr + "\n" + longitudeStr;
	}

	/**
	 * creates a circle with center in specified point (lon, lat) and specified
	 * radius in meters (range)
	 * 
	 * @param center
	 * @param range
	 * @param polygon
	 * @return
	 */
	public static Polygon geoCircle(final Point center, final double range) {
		return (Polygon) geoRingOrPolygon(center, range, true);
	}

	/**
	 * creates a ring with center in specified point (lon, lat) and specified radius
	 * in meters (range)
	 * 
	 * @param center
	 * @param range
	 * @param polygon
	 * @return
	 */
	public static LinearRing geoRing(final Point center, final double range) {
		return (LinearRing) geoRingOrPolygon(center, range, false);
	}

	// /** convert a turn rate of degrees per second to radians per millisecond
	// *
	// * @param rads_milli
	// * @return
	// */
	// public static double degsSec2radsMilli(double degs_sec)
	// {
	// // first convert to millis
	// double res = degs_sec / 1000;
	//
	// // and now to rads
	// return Math.toRadians(res);
	// }
	//
	// /** convert a turn rate of radians per millisecond to degrees per second to
	// radians per millisecond
	// *
	// * @param rads_milli
	// * @return
	// */
	// public static double radsMilli2degSec(double rads_milli)
	// {
	// // first convert to seconds
	// double res = rads_milli * 1000d;
	//
	// // now to degrees
	// return Math.toDegrees(res);
	// }

	public static Geometry geoRingOrPolygon(final Point center, final double range, final boolean polygon) {
		final GeodeticCalculator calculator = createCalculator();
		calculator.setStartingGeographicPoint(center.getX(), center.getY());
		calculator.setDirection(0, range);
		final double yRadius = Math.abs(calculator.getDestinationGeographicPoint().getY() - center.getY());
		calculator.setDirection(90, range);
		final double xRadius = Math.abs(calculator.getDestinationGeographicPoint().getX() - center.getX());
		final Coordinate[] coords = new Coordinate[37];

		double current = 0;
		final double delta = Math.PI / 18.0;
		for (int i = 0; i < 36; i++, current += delta) {
			coords[i] = new Coordinate(center.getX() + Math.cos(current) * xRadius,
					center.getY() + Math.sin(current) * yRadius);
		}
		coords[36] = coords[0];
		return polygon ? _factory.createPolygon(coords) : _factory.createLinearRing(coords);
	}

	public static double[][] getCoordsFor(final LocationRange loc) {
		final Geometry geometry = loc.getGeometry();
		final Coordinate[] coords = geometry.getCoordinates();
		final double[][] res = new double[coords.length][2];
		for (int i = 0; i < coords.length; i++) {
			final Coordinate thisC = coords[i];
			res[i][0] = thisC.x;
			res[i][1] = thisC.y;
		}

		return res;
	}

	/**
	 * get our geometry factory
	 *
	 * @return
	 */
	public static GeometryFactory getFactory() {
		return _factory;
	}

	public static GeoPoint getGeoPointFromString(final String latlong) {

		final String[] _latlong = latlong.split("[NEWS]");

		final String lat = _latlong[0];
		final String lon = _latlong[1];

		double _lat = parseDMSString(lat);
		double _lon = parseDMSString(lon);

		if (latlong.indexOf("S") > 0) {
			_lat *= -1;
		}

		if (latlong.indexOf("W") > 0) {
			_lon *= -1;
		}

		return new GeoPoint(_lat, _lon);
	}

	public static double kts2MSec(final double kts) {
		return kts * 0.514444444;
	}

	public static double MSec2kts(final double m_sec) {
		return m_sec / 0.514444444;
	}

	private static double parseDMSString(final String lat) {
		final double deg = Double.parseDouble(lat.substring(0, lat.indexOf("\u00B0 ")));
		final double min = Double.parseDouble(lat.substring(lat.indexOf("\u00B0 ") + 1, lat.indexOf("' ")));
		final double sec = Double.parseDouble(lat.substring(lat.indexOf("' ") + 1, lat.indexOf("\" ")));
		return dmsToDecimal(deg, min, sec);
	}

	public static void setCalculatorType(final GeoCalculatorType type) {
		_calculatorType = type;
	}

	/**
	 * generate a grid of points across the polygon (see implementation for more
	 * detail)
	 */
	public static ArrayList<Point> ST_Tile(final Geometry p_geom, final int numPoints, final int p_precision) {
		return MakeGrid.ST_Tile(p_geom, numPoints, p_precision);
	}

	public static double yds2m(final double yds) {
		return yds * 0.91444;
	}

}
