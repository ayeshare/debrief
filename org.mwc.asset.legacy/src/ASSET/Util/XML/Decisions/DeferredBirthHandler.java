/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)

 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package ASSET.Util.XML.Decisions;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

import ASSET.Models.Decision.Tactical.DeferredBirth;
import ASSET.Util.XML.Decisions.Tactical.CoreDecisionHandler;
import MWC.GenericData.Duration;
import MWC.Utilities.ReaderWriter.XML.Util.DurationHandler;

abstract public class DeferredBirthHandler extends CoreDecisionHandler
  {

  private final static String type = "DeferredBirth";
  private final static String DURATION = "Duration";


  Duration _myDuration;

  public DeferredBirthHandler()
  {
    super(type);

    addHandler(new DurationHandler(DURATION)
    {
      public void setDuration(Duration res)
      {
        _myDuration = res;
      }
    });
  }


  public void elementClosed()
  {
    final DeferredBirth tr = new DeferredBirth(_myDuration, null);
    super.setAttributes(tr);

    setModel(tr);

    _myDuration = null;
  }


  abstract public void setModel(ASSET.Models.DecisionType dec);

  static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
                                final org.w3c.dom.Document doc)
  {
    // create ourselves
    final org.w3c.dom.Element thisPart = doc.createElement(type);

    // get data item
    final DeferredBirth bb = (DeferredBirth) toExport;

    // first output the parent bits
    CoreDecisionHandler.exportThis(bb, thisPart, doc);

    // output it's attributes
    DurationHandler.exportDuration(DURATION, bb.getDuration(), thisPart, doc);


    parent.appendChild(thisPart);

  }


}