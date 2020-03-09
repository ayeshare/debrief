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

package MWC.GUI.ETOPO;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;

import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Chart.Painters.SpatialRasterPainter;
import MWC.GUI.Properties.LineWidthPropertyEditor;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

public final class ETOPO_2_Minute extends SpatialRasterPainter {
	static public final class Etopo2Test extends junit.framework.TestCase {
		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		public String _etopoPath;

		public Etopo2Test(final String val) {
			super(val);

			final String pathFromProp = System.getProperty("etopoDir");
			if (pathFromProp == null) {
				// are we in OS?
				final String sys = System.getProperty("os.name");
				if ("Mac OS X".equals(sys))
					_etopoPath = "../deploy/";
				else
					_etopoPath = "C:\\Program Files\\Debrief 2003\\etopo";
			} else
				_etopoPath = pathFromProp;

		}

		// TODO FIX-TEST
		// check data
		public void NtestFindData() {

			// String thefile = THE_PATH;
			assertTrue("Failed to find the 2 minute dataset:" + _etopoPath, ETOPO_2_Minute.dataFileExists(_etopoPath));
		}

		public void testConversions() {
			final ETOPO_2_Minute e2m = new ETOPO_2_Minute(_etopoPath);

			WorldLocation loc = new WorldLocation(54, -3, 0);
			int lat = e2m.getLatIndex(loc);
			int lon = e2m.getLongIndex(loc);
			double dLat = ETOPO_2_Minute.getLatitudeFor(lat);
			double dLong = ETOPO_2_Minute.getLongitudeFor(lon);
			WorldLocation loc2 = new WorldLocation(dLat, dLong, 0);

			System.out.println("dist:" + loc2.subtract(loc).getRange());
			assertTrue("points close enough, original: " + loc + " res:" + loc2, loc2.subtract(loc).getRange() < 1);

			loc = new WorldLocation(-54, -3, 0);
			lat = e2m.getLatIndex(loc);
			lon = e2m.getLongIndex(loc);
			dLat = ETOPO_2_Minute.getLatitudeFor(lat);
			dLong = ETOPO_2_Minute.getLongitudeFor(lon);
			loc2 = new WorldLocation(dLat, dLong, 0);
			assertTrue("points close enough, original: " + loc + " res:" + loc2, loc2.subtract(loc).getRange() < 1);

			loc = new WorldLocation(-54, 3, 0);
			lat = e2m.getLatIndex(loc);
			lon = e2m.getLongIndex(loc);
			dLat = ETOPO_2_Minute.getLatitudeFor(lat);
			dLong = ETOPO_2_Minute.getLongitudeFor(lon);
			loc2 = new WorldLocation(dLat, dLong, 0);
			assertTrue("points close enough, original: " + loc + " res:" + loc2, loc2.subtract(loc).getRange() < 1);

			loc = new WorldLocation(54, 3, 0);
			lat = e2m.getLatIndex(loc);
			lon = e2m.getLongIndex(loc);
			dLat = ETOPO_2_Minute.getLatitudeFor(lat);
			dLong = ETOPO_2_Minute.getLongitudeFor(lon);
			loc2 = new WorldLocation(dLat, dLong, 0);
			assertTrue("points close enough, original: " + loc + " res:" + loc2, loc2.subtract(loc).getRange() < 1);

			// assertEquals(loc, loc2);
		}

		public final void testMyParams() {
			ETOPO_2_Minute ed = new ETOPO_2_Minute(null);
			ed.setName("blank");
			editableTesterSupport.testParams(ed, this);
			ed = null;
		}
	}

	public final class MARTOPOInfo extends Editable.EditorType implements java.io.Serializable {

		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

		public MARTOPOInfo(final ETOPO_2_Minute data) {
			super(data, data.getName(), "");
		}

		@Override
		public final java.beans.PropertyDescriptor[] getPropertyDescriptors() {
			try {
				final java.beans.PropertyDescriptor[] res = {
						prop("Visible", "whether this layer is visible", VISIBILITY),
						displayLongProp("KeyLocation", "Key location", "the current location of the color-key",
								KeyLocationPropertyEditor.class, EditorType.FORMAT),
						prop("Color", "the color of the color-key", EditorType.FORMAT),
						displayProp("ShowLand", "Show land", "whether to shade land-data", EditorType.FORMAT),
						displayProp("LandColor", "Land Color", "Color to shade land data", EditorType.FORMAT),
						displayProp("NEShading", "Natural Earth Shading", "whether to use Natural Earth shading",
								EditorType.FORMAT),
						displayLongProp("LineThickness", "Line thickness", "the thickness to plot the scale border",
								LineWidthPropertyEditor.class, EditorType.FORMAT),
						displayProp("BathyRes", "Bathy resolution",
								"the size of the grid at which to plot the shaded bathy (larger blocks gives faster performance)",
								EditorType.FORMAT),
						displayProp("BathyVisible", "Bathy visible", "whether to show the gridded contours",
								VISIBILITY),
						displayProp("ContourDepths", "Contour depths", "the contour depths to plot", EditorType.FORMAT),
						displayProp("ContoursVisible", "Contours visible", "whether to show the contours", VISIBILITY),
						displayProp("ContourGridInterval", "Contour grid interval",
								"the interval at which to calculate the contours (larger interval leads to faster performance)",
								EditorType.FORMAT),
						displayProp("ContourOptimiseGrid", "Optimise grid interval",
								"whether the grid interval should be optimised", EditorType.FORMAT) };
				return res;
			} catch (final java.beans.IntrospectionException e) {
				e.printStackTrace();
				return super.getPropertyDescriptors();
			}
		}
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * the filename of our data-file
	 */
	private static final String fName = "ETOPO2.raw";

	/**
	 * the file-reader we are using
	 */
	private static java.io.RandomAccessFile ra = null;

	public static WorldArea theArea = null;

	/**
	 * static accessor to see if our data-file is there <<<<<<< HEAD
	 *
	 * =======
	 *
	 * >>>>>>> develop
	 *
	 * @param etopo_path
	 * @return
	 */
	static public boolean dataFileExists(final String etopo_path) {
		boolean res = false;

		final String thePath = etopo_path + "/" + fName;

		final File testFile = new File(thePath);

		if (testFile.exists())
			res = true;

		return res;
	}

	/**
	 * how many horizonal data points are in our data
	 */
	private static int getHorizontalNumber() {
		return 10800;
	}

	/**
	 * get the latitude for the indicated index
	 */
	static double getLatitudeFor(final int index) {
		double res;

		res = index;

		// convert to mins
		res *= 2;

		// add 1 for luck
		res += 1;

		// convert to degs
		res /= 60;

		// put in hemisphere
		if (res > 90) {
			res = -(res - 90);
		} else {
			res = 90 - res;
		}

		return res;
	}

	/**
	 * get the longitude for the indicated index
	 */
	static double getLongitudeFor(final int index) {
		double res;

		// convert to mins
		res = index * 2;

		// add 1 for luck
		res += 1;

		// convert to degs
		res /= 60;

		// put in hemisphere
		res -= 180;

		return res;
	}

	/**
	 * rescale the data-set, if necessary
	 *
	 * @param val the depth as read from file
	 * @return the rescaled depth (from feet to metres in this case)
	 */
	private static int rescaleValue(final int val) {
		// convert from feet to metres
		// return (int)MWC.Algorithms.Conversions.ft2m(val);
		return val;
	}

	/**
	 * whether to show land
	 */
	private boolean _showLand = true;

	/**
	 * color for the land
	 *
	 */
	private Color _landCol = new Color(235, 219, 188);

	/**
	 * the path to the datafile
	 */
	private final String _thePath;

	/**
	 * flag to ensure we only report missing data on the first occasion
	 */
	private boolean _reportedMissingData = false;

	/**
	 * cache the values we read from file. At some resolutions we re-read the same
	 * depth cell many times.
	 */
	private final Map<Integer, Integer> _pointCache = new HashMap<Integer, Integer>();

	public ETOPO_2_Minute(final String etopo_path) {
		super("ETOPO (2 Minute)");

		// store the path to the data file
		_thePath = etopo_path;

		// switch on NE shading
		super.setNEShading(true);

		_contourDepths = DEFAULT_CONTOUR_DEPTHS;
	}

	/**
	 * function to retrieve a data value at the indicated index
	 */
	@Override
	protected final double contour_valAt(final int i, final int j) {

		final double res;
		final int index;
		index = 2 * (j * getHorizontalNumber() + i);
		res = getValueAtIndex(index);
		return res;
	}

	/**
	 * function to retrieve the x-location for a specific array index
	 */
	@Override
	protected final double contour_xValAt(final int i) {
		return getLongitudeFor(i);
	}

	/**
	 * function to retrieve the x-location for a specific array index
	 */
	@Override
	protected final double contour_yValAt(final int i) {
		return getLatitudeFor(i);
	}

	/* returns a color based on slope and elevation */
	@Override
	public final int getColor(final int elevation, final double lowerLimit, final double upperLimit,
			final SpatialRasterPainter.ColorConverter converter, final boolean useNE) {
		return getETOPOColor((short) elevation, lowerLimit, upperLimit, _showLand, converter, useNE);
	}

	/* returns a color based on slope and elevation */
	public int getETOPOColor(final short elevation, final double lowerLimit, final double upperLimit,
			final boolean showLand, final SpatialRasterPainter.ColorConverter converter, final boolean useNE) {
		final int res;

		if (useNE) {
			// see if we are above or beneath water level
			if ((elevation > 0) && (showLand)) {
				// ABOVE WATER
				res = converter.convertColor(_landCol.getRed(), _landCol.getGreen(), _landCol.getBlue());
			} else {
				// BELOW WATER

				// switch to positive
				double val = elevation;
				if (val < lowerLimit)
					val = lowerLimit;

				final double x = val / lowerLimit;

				final int red = (int) (169.0577 - 157.9448 * x + 19.64085 * Math.pow(x, 2));
				final int green = (int) (197.4973 - 103.4937 * x - 2.004169 * Math.pow(x, 2));
				final int blue = (int) (227.4203 - (35.15651 * x) - (30.06253 * Math.pow(x, 2)));

				res = converter.convertColor(red, green, blue);

			}
		} else {
			// see if we are above or beneath water level
			if ((elevation > 0) && (showLand)) {
				// ABOVE WATER

				// switch to positive
				double val = elevation;
				if (val > upperLimit)
					val = upperLimit;

				final double proportion = 1 - val / upperLimit;

				final int red = (int) (_landCol.getRed() * proportion);
				final int green = (int) (_landCol.getGreen() * proportion);
				final int blue = (int) (_landCol.getBlue() * proportion);

				res = converter.convertColor(red, green, blue);

				// final double color_val = proportion * 125;
				//
				// // limit the colour val to the minimum value
				// int green_tone = 255 - (int) color_val;
				//
				// // just check we've got a valid colour
				// green_tone = Math.min(250, green_tone);
				// res = converter.convertColor(88, green_tone, 88);
			} else {
				// BELOW WATER

				// switch to positive
				double val = elevation;
				if (val < lowerLimit)
					val = lowerLimit;

				final double proportion = val / lowerLimit;

				final double color_val = proportion * ETOPOWrapper.BLUE_MULTIPLIER;

				// limit the colour val to the minimum value
				int blue_tone = 255 - (int) color_val;

				// just check we've got a valid colour
				blue_tone = Math.min(250, blue_tone);

				final int green = (int) ETOPOWrapper.GREEN_BASE_VALUE
						+ (int) (blue_tone * ETOPOWrapper.GREEN_MULTIPLIER);

				res = converter.convertColor(ETOPOWrapper.RED_COMPONENT, green, blue_tone);

			}
		}

		// so, just produce a shade of blue depending on how deep we are.

		return res;
	}

	/**
	 * provide the delta for the data (in degrees)
	 */
	@Override
	public double getGridDelta() {
		return 1d / 30d;
	}

	/**
	 * get the index for a particular point
	 *
	 * @param val the location we want the index for
	 * @return the index into the array for this position
	 */
	private int getIndex(final WorldLocation val) {
		final int res;

		// and the res
		res = 2 * ((getLatIndex(val) * getHorizontalNumber()) + getLongIndex(val));

		return res;
	}

	@Override
	public final Editable.EditorType getInfo() {
		if (_myEditor == null)
			_myEditor = new MARTOPOInfo(this);

		return _myEditor;
	}

	public Color getLandColor() {
		return _landCol;
	}

	/**
	 * get the lat component of this location
	 */
	@Override
	protected final int getLatIndex(final WorldLocation val) {
		// get the components
		double lat = val.getLat();
		final int lat_index;

		// work out how far down the lat is
		if (lat > 0) {
			// convert to down
			lat = 90d - lat;
		} else
			lat = 90 + Math.abs(lat);

		// convert to mins
		lat = lat * 60;

		// convert to 2 mins intervals
		lat = lat / 2;

		lat_index = (int) lat;

		return lat_index;
	}

	/**
	 * get the long component of this location
	 */
	@Override
	protected final int getLongIndex(final WorldLocation val) {
		// get the components
		double lon = val.getLong();

		final int long_index;

		// work out how far acrss the lat is
		if (lon < 0) {
			lon = 180 + lon;
		} else
			lon = 180 + lon;

		// convert to secs
		lon = lon * 60;

		// convert to 2 sec intervals
		lon = lon / 2;

		long_index = (int) lon;

		return long_index;
	}

	/**
	 * accessor for whether to show land
	 */
	public final boolean getShowLand() {
		return _showLand;
	}

	/**
	 * @param val the location to get the depth for
	 * @return the depth (in m)
	 */
	@Override
	public final int getValueAt(final WorldLocation val) {
		final int res;

		// is it valid
		if (!val.isValid()) {
			res = 0;
		} else {

			// now get it's index
			final int index = getIndex(val);

			// and get the value itself
			res = getValueAtIndex(index);
		}

		return res;
	}

	private int getValueAtIndex(final int index) {

		// check the cache
		final Integer cached = _pointCache.get(index);
		if (cached != null) {
			return cached.intValue();
		} else {
			int res = 0;

			// just check we have the file open
			openFile();

			// just check we have a +ve (valid) index
			if (index >= 0) {
				// and retrieve the value
				try {
					ra.seek(index);
					res = ra.readShort();

					// rescale as appropriate
					res = rescaleValue(res);
				} catch (final IOException e) {
					e.printStackTrace(); // To change body of catch statement use
					// Options |
					// File Templates.
				}
			}

			// cache the value
			_pointCache.put(index, res);

			return res;
		}
	}

	/**
	 * we do want to double-buffer this layer - since it takes "so" long to create
	 *
	 */
	@Override
	public boolean isBuffered() {
		return true;
	}

	/**
	 * whether the data has been loaded yet
	 */
	@Override
	public boolean isDataLoaded() {
		// do an open, just to check
		openFile();

		return (ra != null);
	}

	/**
	 * open our datafile in random access mode
	 */
	private final void openFile() {
		if (ra == null) {
			String thePath = null;

			// just do a check to see if we have just the file or the whole path
			final File testF = new File(_thePath);
			if (testF.isFile()) {
				thePath = _thePath;
			} else if (testF.isDirectory()) {
				thePath = _thePath + "//" + fName;
			}

			if (thePath != null) {
				try {
					ra = new RandomAccessFile(thePath, "r");
				} catch (final IOException e) {
					if (_reportedMissingData) {
						MWC.GUI.Dialogs.DialogFactory.showMessage("File missing",
								"2 minute ETOPO data not found at:" + thePath);
						_reportedMissingData = true;
					}
				}
			}
		}
	}

	/**
	 * override the parent paint method, so we can open/close the datafile
	 *
	 * @param dest where we're painting to
	 */
	@Override
	public final void paint(final CanvasType dest) {
		if (getVisible()) {
			// start the paint
			openFile();

			// hey, it's only worth plotting if we've got some data
			if (!isDataLoaded())
				return;

			// remember width
			final float oldWid = dest.getLineWidth();

			// set our line width
			dest.setLineWidth(this.getLineThickness());

			super.paint(dest);

			// and restore the old one
			dest.setLineWidth(oldWid);
		}
	}

	public void setLandColor(final Color landCol) {
		this._landCol = landCol;
	}

	/**
	 * setter for whether to show land
	 */
	public final void setShowLand(final boolean val) {
		_showLand = val;
	}

	/**
	 * over-rideable method to constrain max value to zero (such as when not
	 * plotting land)
	 *
	 * @return yes/no
	 */
	@Override
	protected final boolean zeroIsMax(final boolean useNE) {
		// if we are showing land, then we don't want zero to be the top value
		return useNE || !_showLand;
	}

}
