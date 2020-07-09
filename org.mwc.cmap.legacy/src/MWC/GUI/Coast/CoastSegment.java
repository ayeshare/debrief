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

import java.util.Vector;

import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

/** Represents a single section of coastline. */
public final class CoastSegment extends Vector<WorldLocation> {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	WorldArea myArea;

//  public CoastSegment(){
//    super(0,1);
//  }

	public void addPoint(final WorldLocation pt) {
		this.addElement(pt);

		if (myArea == null) {
			myArea = new WorldArea(pt, pt);
		} else
			myArea.extend(pt);

	}

//	public int compareTo(Plottable arg0)
//	{
//		Plottable other = (Plottable) arg0;
//		return this.getName().compareTo(other.getName());
//	}

//	protected WorldLocation getFirst(){
//    WorldLocation res = null;
//    if(this.size()>0){
//      res = (WorldLocation) this.elementAt(0);
//    }
//    return res;
//  }
//
//  protected WorldLocation getLast(){
//    WorldLocation res = null;
//    if(this.size()>0){
//      res = (WorldLocation) this.elementAt(this.size()-1);
//    }
//    return res;
//  }
//
//  private void append(CoastSegment other){
//    for(int i=0; i<other.size();i++){
//      this.addPoint((WorldLocation)other.elementAt(i));
//    }
//    other.removeAllElements();
//  }
//
//  private void appendBackwards(CoastSegment other){
//    for(int i=other.size()-1; i>=0;i--){
//      this.addPoint((WorldLocation)other.elementAt(i));
//    }
//    other.removeAllElements();
//  }
//
//  private void prependBackwards(CoastSegment other){
//    for(int i=0; i<other.size();i++){
//      this.insertElementAt(other.elementAt(i),0);
//    }
//    other.removeAllElements();
//  }

//  public void paint(CanvasType dest)
//  {
//  }

	public WorldArea getBounds() {
		return myArea;
	}

//  public boolean getVisible()
//  {
//    return true;
//  }

}
