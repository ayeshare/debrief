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

package MWC.GUI.Coast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.text.ParseException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

/**
 * Represents a single section of coastline.
 */
public class Coastline implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	protected final Vector<CoastSegment> _data;
	protected WorldArea _myArea;

	public Coastline(final InputStream str) throws IOException, ParseException {
		_data = new Vector<CoastSegment>(0, 1);
		CoastSegment cs = null;
		String thisLine = null;
		final int count = 0;

		// DataInputStream dis = new DataInputStream(str);
		final BufferedReader dis = new BufferedReader(new InputStreamReader(str));

		boolean segment_saved = false;

		while (((thisLine = dis.readLine()) != null) & (count < 10000)) {

			if (thisLine == null)
				return;

			// so we've got a line.
			// see if it represents a new section
			if (thisLine.startsWith("# -b")) {
				// this is a new section of coast, store the previous one,
				// if there was one
				if (cs != null) {
					_data.addElement(cs);
					// indicate we've saved it
					segment_saved = true;
				}

				// create the new segment
				cs = new CoastSegment();
			} else {
				// tokenize it
				final StringTokenizer st = new StringTokenizer(thisLine);
				// note that we read in the long before the lat from the file
				final String longStr = st.nextToken();
				final String latStr = st.nextToken();
				final double _lat = MWCXMLReader.readThisDouble(latStr);
				final double _long = MWCXMLReader.readThisDouble(longStr);
				final WorldLocation pt = new WorldLocation(_lat, _long, 0);

				if (cs != null) {
					cs.addPoint(pt);

					// indicate the new segment has been changed
					segment_saved = false;
				}
			}

		}

		// we need to tidily handle the last segment (which may not have had a #-b
		// id)
		// see if there is a 'trailing' segment
		if (segment_saved == false) {
			_data.addElement(cs);
		}

		// and reset the area covererd
		resetArea();
	}

	public WorldArea getBounds() {
		if (_myArea == null)
			resetArea();

		return _myArea;
	}

	public Iterator<CoastSegment> iterator() {
		return _data.iterator();
	}

	private void resetArea() {
		final Enumeration<CoastSegment> enumer = _data.elements();
		WorldArea res = null;
		while (enumer.hasMoreElements()) {
			final CoastSegment seg = enumer.nextElement();
			res = WorldArea.extend(res, seg.getBounds());
		}
		_myArea = res;
	}

	/*
	 * public void mergeCoasts(){ Enumeration segs = this.elements(); int count=0;
	 *
	 * System.out.println("there are " + this.size() + " segments");
	 *
	 * // go through all coastal segments while(segs.hasMoreElements()){
	 *
	 * // get this seg CoastSegment sg = (CoastSegment)segs.nextElement();
	 *
	 * // go through the other segs Enumeration others = this.elements();
	 * while(others.hasMoreElements()){ CoastSegment other =
	 * (CoastSegment)others.nextElement();
	 *
	 * if(other != sg){ if(other != null){ if((other.size()>0) && (sg.size()>0)){
	 * if(other.getFirst().equals(sg.getLast())){ // this is a match, add that one
	 * to this one sg.append(other);
	 *
	 * this.removeElement(other); }else if(other.getLast().equals(sg.getFirst())){
	 * other.append(sg);
	 *
	 * this.removeElement(sg); }else if(other.getLast().equals(sg.getLast())){
	 * sg.appendBackwards(other);
	 *
	 * this.removeElement(other); }else if(other.getFirst().equals(sg.getFirst())){
	 * // sg.prependBackwards(other); // this.removeElement(other); }
	 *
	 * } } } } } }
	 */

	// public int compareTo(Plottable arg0)
	// {
	// Plottable other = (Plottable) arg0;
	// return this.getName().compareTo(other.getName());
	// }

	// public int size()
	// {
	// return _data.size();
	// }

	// public Object elementAt(int i)
	// {
	// return _data.elementAt(i);
	// }
	//
	// public void paint(CanvasType dest)
	// {
	// }

	// /**
	// * set the visibility of this item (dummy implementation)
	// */
	// public void setVisible(boolean val)
	// {
	// }
	//
	// public boolean getVisible()
	// {
	//
	// return false;
	// }

	// public double rangeFrom(WorldLocation other)
	// {
	// return INVALID_RANGE;
	// }
	//
	// /**
	// * return this item as a string
	// */
	// public String toString()
	// {
	// return getName();
	// }
	//
	// public String getName()
	// {
	// return "Coastline";
	// }
	//
	// public boolean hasEditor()
	// {
	// return false;
	// }
	//
	// public Editable.EditorType getInfo()
	// {
	// return null;
	// }

}

/*
 * private void writeObject(java.io.ObjectOutputStream out) throws IOException {
 * // write the number of elements out.writeInt(_data.size()); Enumeration
 * enumer = _data.elements(); while(enumer.hasMoreElements()) { CoastSegment cs
 * = (CoastSegment)enumer.nextElement(); writeSegment(cs, out); } }
 *
 * private void writeSegment(CoastSegment cs, java.io.ObjectOutputStream out)
 * throws IOException { // write the number of elements out.writeInt(cs.size());
 * out.writeObject(cs.getBounds()); Enumeration enumer = cs.elements();
 * while(enumer.hasMoreElements()) { WorldLocation wl =
 * (WorldLocation)enumer.nextElement(); out.writeDouble(wl.getLat());
 * out.writeDouble(wl.getLong()); out.writeDouble(wl.getDepth()); } }
 *
 * private void readObject(java.io.ObjectInputStream in) throws IOException,
 * ClassNotFoundException { _data=new Vector(0,1); long sz = in.readInt();
 * for(int i=0;i<sz; i++) { _data.addElement(readSegment(in)); } }
 *
 * private CoastSegment readSegment(java.io.ObjectInputStream in) throws
 * IOException, ClassNotFoundException { CoastSegment cs = new CoastSegment();
 * int num = in.readInt(); cs.myArea = (WorldArea)in.readObject(); for(int
 * i=0;i<num;i++) { WorldLocation wl = new WorldLocation(in.readDouble(),
 * in.readDouble(), in.readDouble()); cs.addElement(wl); } return cs; }
 */