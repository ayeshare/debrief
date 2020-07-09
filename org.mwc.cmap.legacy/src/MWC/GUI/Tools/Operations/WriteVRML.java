
// $RCSfile: WriteVRML.java,v $
// $Author: Ian.Mayo $
// $Log: WriteVRML.java,v $
// Revision 1.2  2004/05/25 15:44:13  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:26  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:45  Ian.Mayo
// Initial import
//
// Revision 1.2  2002-05-28 09:26:01+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:06+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 13:03:39+01  ian_mayo
// Initial revision
//
// Revision 1.1  2001-08-23 13:28:13+01  administrator
// Reflect new name, D2001
//
// Revision 1.0  2001-07-17 08:42:54+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:41:44+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:51:52  ianmayo
// initial version
//
// Revision 1.8  2000-10-09 13:35:43+01  ian_mayo
// Switched stack traces to go to log file
//
// Revision 1.7  2000-08-23 09:39:05+01  ian_mayo
// factored out Debrief-specific plotting code, and added remaining comments
//
// Revision 1.6  2000-08-07 12:21:48+01  ian_mayo
// tidy icon filename
//
// Revision 1.5  2000-05-23 13:38:22+01  ian_mayo
// more mods, currently working well
//
// Revision 1.4  2000-04-26 14:21:15+01  ian_mayo
// stretch depth, and allow more than one track per layer
//
// Revision 1.3  2000-03-14 09:54:50+00  ian_mayo
// use icons for these tools
//
// Revision 1.2  2000-02-25 09:05:08+00  ian_mayo
// first attempt at scaling
//
// Revision 1.1  2000-02-24 12:40:38+00  ian_mayo
// Initial revision
//

package MWC.GUI.Tools.Operations;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;

import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.ToolParent;
import MWC.GUI.Tools.Action;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

/**
 * Abstract class containing core functionality for creating files in VRML
 * format. The file is placed in the Debrief working directory with a filename
 * out.wrl
 */
abstract public class WriteVRML extends MWC.GUI.Tools.PlainTool {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private static java.text.NumberFormat _doubleFormat = new java.text.DecimalFormat("0.000");

/////////////////////////////////////////////////////////////
	// member variables
	////////////////////////////////////////////////////////////
	/**
	 * The data we are plotting
	 */
	protected Layers _theData;
	/**
	 * The number of segments to break the data down into each axis
	 */
	final protected long num_segs = 500;
	/**
	 * the scale applied to the xy data
	 */
	protected double _xy_scale;
	/**
	 * the scale applied to the depth data
	 */
	protected double _depth_scale;

	/*
	 * artificially compress the depth scale to be a 10th of the xy scales
	 */
	final private double _depth_stretch = 10.0;

	/////////////////////////////////////////////////////////////
	// constructor
	////////////////////////////////////////////////////////////
	/**
	 * open an existing data file
	 *
	 * @param theParent parent application, where we can show the busy cursor
	 * @param theLabel  the label to put on the button
	 * @param theData   the data to plot
	 */
	public WriteVRML(final ToolParent theParent, final String theLabel, final Layers theData) {
		super(theParent, theLabel, "images/write_vr.gif");

		_theData = theData;
	}

	private String convertDegs(final double val) {
		// scale
		final double value = val * _xy_scale;
		return _doubleFormat.format(value);
	}

	private String convertDepth(final double metres) {
		final double mtrs = -metres * _depth_scale;

		return _doubleFormat.format(mtrs); // convertDegs(metres);
	}

	/////////////////////////////////////////////////////////////
	// member functions
	////////////////////////////////////////////////////////////

	/**
	 * collate the data ready to perform the operations
	 *
	 * @return the collected data - stored as an Action
	 */
	@Override
	public Action getData() {
		// the result object
		final Action res = null;

		try {
			// create the output file
			final File of = new File("out.wrl");
			final BufferedWriter out = new BufferedWriter(new FileWriter(of));

			// put the header
			writeHeader(out);

			// find the data limits
			final int num = _theData.size();
			WorldArea bounds = null;
			for (int i = 0; i < num; i++) {
				final Layer l = _theData.elementAt(i);

				bounds = WorldArea.extend(bounds, l.getBounds());

			}

			if (bounds == null)
				return res;

			// set the scaling limits
			final double max_dim = Math.max(bounds.getHeight(), bounds.getWidth());

			// sort out the scale to be applied (to convert degs to scale)
			_xy_scale = num_segs / max_dim;

			final double depth_range = bounds.getDepthRange();
			if (depth_range == 0) {
				// there is no depth data in track, don't progress any further
				MWC.GUI.Dialogs.DialogFactory.showMessage("Write VR File",
						"Sorry the data does not contain depth data.  A 3-D file will not be produced");
				return null;
			}

			// scale the depth
			_depth_scale = num_segs / depth_range;

			// shrink the z component, to give a flatter plot
			_depth_scale = _depth_scale / _depth_stretch;

			// put in the axes
			writeAxes(out, bounds);

			// get our child classes to plot out the data
			plotData(_theData, out);

			// tidy up the output file
			out.flush();

			// and close it
			out.close();

		} catch (final Exception e) {
			MWC.Utilities.Errors.Trace.trace(e);
		}

		// return the product
		return res;
	}

	/**
	 * method overridden by child classes to plot the data
	 *
	 * @param theData the data to plot
	 * @param out     the stream to write the data to
	 */
	abstract protected void plotData(MWC.GUI.Layers theData, java.io.BufferedWriter out) throws java.io.IOException;

	/**
	 * Produce a set of axes
	 *
	 * @param out    the stream we are writing to
	 * @param bounds the outer limits
	 * @throws IOException any problem writing to the file
	 */
	protected void writeAxes(final java.io.BufferedWriter out, final WorldArea bounds) throws IOException {
		// calculate the scales to use
		final String wid = convertDegs(bounds.getWidth());
		final String ht = convertDegs(bounds.getHeight());
		/**
		 * note that we apply the depth_stretch to the max depth
		 */
		String depth = convertDepth(Math.abs(bounds.getDepthRange() / _depth_stretch));

		try {
			// check depth is +ve
			double dv = MWCXMLReader.readThisDouble(depth);
			dv = Math.abs(dv);
			depth = _doubleFormat.format(dv);

			// determine the centre point
			final WorldLocation centre = bounds.getCentre();
			final String x = convertDegs(centre.getLong());
			final String y = convertDegs(centre.getLat());
			String z = convertDepth(centre.getDepth());
			dv = MWCXMLReader.readThisDouble(z);
			dv = Math.abs(dv);
			z = _doubleFormat.format(dv);

			// first the surface
			out.write("Transform {");
			out.newLine();
			out.write("translation " + x + " " + y + " 0");
			out.newLine();
			out.write("children [");
			out.newLine();
			out.write("Shape{ appearance Appearance {");
			out.newLine();
			// out.write(" material Material { diffuseColor 0.8 0.8 1.0 transparency 0.44
			// }}"); out.newLine();
			out.write("		material Material { diffuseColor 0.8 0.8 1.0 }}");
			out.newLine();
			out.write("			geometry Box{size " + wid + " " + ht + " 0.1}}");
			out.newLine();
			out.write("]}");
			out.newLine();

			// now the depth axis
			out.write("Transform {");
			out.newLine();
			out.write("translation " + x + " " + y + " " + z);
			out.newLine();
			out.write("children [");
			out.newLine();
			out.write("Shape{ appearance Appearance {");
			out.newLine();
			out.write("		material Material { emissiveColor   0.7 0.7 0.7 }}");
			out.newLine();
			out.write("			geometry Box{size 1.0 1.0 " + depth + " }}");
			out.newLine();
			out.write("]}");
			out.newLine();

		} catch (final ParseException pe) {
			MWC.Utilities.Errors.Trace.trace(pe);
		}

		/*
		 *
		 * Transform {translation -1383.895 17250.154 -250.000 children [ Shape{
		 * appearance Appearance { material Material { diffuseColor 1.0 1.0 1.0 }}
		 * geometry Box{size 1.0 1.0 -500.000 }} ]}
		 *
		 *
		 * Transform { translation -1383.895 17250.154 0 children [ Shape{ appearance
		 * Appearance { material Material { diffuseColor 0.8 0.8 1.0 transparency 0.44
		 * }} geometry Box{size 500.000 190.032 0.1}}
		 *
		 * ]}
		 *
		 */

	}

	/**
	 * put a box into the world
	 *
	 * @param out      the stream we are writing to
	 * @param xVal     the x-coord of the centre
	 * @param yVal     the y-coord of the centre
	 * @param zVal     the z-coord of the centre
	 * @param theColor the colour of the box
	 * @param theLbl   the label attached to the box
	 * @throws IOException    file troubles
	 * @throws ParseException
	 */
	protected void writeBox(final BufferedWriter out, final double xVal, final double yVal, final double zVal,
			final java.awt.Color theColor, final String theLbl) throws IOException, ParseException {
		final double red = theColor.getRed() / 255.0;
		final double green = theColor.getGreen() / 255.0;
		final double blue = theColor.getBlue() / 255.0;

		final String dt = convertDepth(zVal);
		final double dtText = MWCXMLReader.readThisDouble(dt) + 10;

		out.write("Transform {");
		out.newLine();
		out.write("translation " + convertDegs(xVal) + " " + convertDegs(yVal) + " " + dt);
		out.newLine();
		out.write("children");
		out.newLine();
		out.write("Shape{ appearance Appearance {");
		out.newLine();
		out.write("		material Material { diffuseColor " + red + " " + green + " " + blue + " }}");
		out.newLine();
		out.write("			geometry Box{size 0.8 0.8 0.8}}}");
		out.newLine();
//		out.write("			geometry Sphere{ radius 1.0}}}"); 		  out.newLine();

		out.write("	Viewpoint { ");
		out.newLine();
//		out.write("	 position " + convertDegs(xVal) + " " + convertDegs(yVal) + " " + convertDepth(zVal)); 		  out.newLine();
		out.write("	 position " + convertDegs(xVal) + " " + convertDegs(yVal) + " " + dtText);
		out.newLine();
		out.write("	 orientation 1 0 0 0 ");
		out.newLine();
		out.write("	fieldOfView 0.99 ");
		out.newLine();
		out.write("	description \"" + theLbl + "\" ");
		out.newLine();
		out.write("	} ");
		out.newLine();

		/*
		 *
		 * Viewpoint { position -624.319 6587.077 31 orientation 1 0 0 0 fieldOfView
		 * 0.99 description "Camera 1" }
		 *
		 *
		 */
	}

	/**
	 * put the header information into the file
	 *
	 * @param out the stream we are writing to
	 * @throws IOException file-related troubles
	 */
	protected void writeHeader(final java.io.BufferedWriter out) throws IOException {
		out.write("#VRML V2.0 utf8");
		out.newLine();
		out.write("WorldInfo {");
		out.newLine();
		out.write("title \"Debrief 2001 Virtual Reality Plot\"");
		out.newLine();
		out.write("   info [\"(C) Copyright 2001 MWC\"");
		out.newLine();
		out.write("\"mwc.td@gtnet.gov.uk\"]");
		out.newLine();
		out.write("}");
		out.newLine();
		out.newLine();

	}

	/**
	 * place a new coordinate in our line
	 *
	 * @param out  the stream we are writing to
	 * @param xVal x coordinate
	 * @param yVal y coordinate
	 * @param zVal z coordinate
	 * @throws IOException if there is a file access problem
	 */
	protected void writeLineEntry(final BufferedWriter out, final double xVal, final double yVal, final double zVal)
			throws IOException {
		out.write(" " + convertDegs(xVal) + " " + convertDegs(yVal) + " " + convertDepth(zVal) + ",");
		out.newLine();
	}

	/**
	 * finish off the line
	 *
	 * @param out    the stream we are writing to
	 * @param length how many points were in the line (to produce the indexes)
	 * @throws IOException file-related troubles
	 */
	protected void writeLineFooter(final BufferedWriter out, final long length) throws IOException {
		out.write("  ]  ");
		out.newLine();
		out.write("  } ");
		out.newLine();
		out.write("  coordIndex [ ");
		out.newLine();

		// put in the indexes
		for (int i = 0; i < length - 1; i++) {
			out.write(" " + i + " " + (i + 1) + " -1");
			out.newLine();
		}

		out.write("   ]  ");
		out.newLine();
		out.write("    } } ");
		out.newLine();

		/*
		 * ] } coordIndex [ 0 1 -1 1 2 -1 2 3 -1 3 4 -1 4 5 -1 5 6 -1 6 7 -1 7 8 -1
		 *
		 * ] } }
		 */
	}

	/**
	 * write the header text we produce before writing a line to the file
	 *
	 * @param out    the stream we are writing to
	 * @param theCol the colour to use
	 * @throws IOException if there is a file access problem
	 */
	protected void writeLineHeader(final java.io.BufferedWriter out, final java.awt.Color theCol) throws IOException {

		final double red = theCol.getRed() / 255.0;
		final double green = theCol.getGreen() / 255.0;
		final double blue = theCol.getBlue() / 255.0;

		out.write(" Shape { ");
		out.newLine();
		out.write(" appearance Appearance { ");
		out.newLine();
		out.write(" material Material { ");
		out.newLine();
		out.write("   emissiveColor " + red + " " + green + " " + blue + " ");
		out.newLine();
		out.write("  } ");
		out.newLine();
		out.write(" } ");
		out.newLine();
		out.write("   geometry IndexedLineSet { ");
		out.newLine();
		out.write("  coord Coordinate { ");
		out.newLine();
		out.write("   point [ ");
		out.newLine();

	}

	/**
	 * place a line of text into the world
	 *
	 * @param out      the stream we are writing to
	 * @param xVal     x coordinate of the text
	 * @param yVal     y coordinate of the text
	 * @param zVal     z coordinate of the text
	 * @param theStr   the string to write
	 * @param theColor the colour to write
	 * @throws IOException file related troubles
	 */
	protected void writeText(final BufferedWriter out, final double xVal, final double yVal, final double zVal,
			final String theStr, final java.awt.Color theColor) throws IOException {
		final double red = theColor.getRed() / 255.0;
		final double green = theColor.getGreen() / 255.0;
		final double blue = theColor.getBlue() / 255.0;

		out.write("Transform {");
		out.newLine();
		out.write("translation " + convertDegs(xVal) + " " + convertDegs(yVal) + " " + convertDepth(zVal));
		out.newLine();
		out.write("children");
		out.newLine();
		out.write("Shape{ appearance Appearance {");
		out.newLine();
		out.write("		material Material { diffuseColor " + red + " " + green + " " + blue + " }}");
		out.newLine();
		out.write("			geometry Text{string[\".  " + theStr + "\"]}}}");
		out.newLine();
	}

	/*******************************************************************************
	 * Debrief - the Open Source Maritime Analysis Application http://debrief.info
	 *
	 * (C) 2000-2020, Deep Blue C Technology Ltd
	 *
	 * This library is free software; you can redistribute it and/or modify it under
	 * the terms of the Eclipse Public License v1.0
	 * (http://www.eclipse.org/legal/epl-v10.html)
	 *
	 * This library is distributed in the hope that it will be useful, but WITHOUT
	 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
	 * FOR A PARTICULAR PURPOSE.
	 *******************************************************************************/
}
