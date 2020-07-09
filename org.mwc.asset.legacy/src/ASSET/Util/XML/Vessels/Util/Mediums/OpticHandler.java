
package ASSET.Util.XML.Vessels.Util.Mediums;

import ASSET.Models.Environment.EnvironmentType;
import ASSET.Models.Mediums.Optic;
import MWC.GenericData.WorldDistance;
import MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler;

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

abstract public class OpticHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader {

	final static private String type = "Optic";

	final static private String NOISE = "BaseNoiseLevel";
	final static private String AREA = "XSectArea";
	final static private String HEIGHT = "Height";

	static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {
		final ASSET.Models.Mediums.Optic bb = (ASSET.Models.Mediums.Optic) toExport;

		final org.w3c.dom.Element ele = doc.createElement(type);

		ele.setAttribute(AREA, writeThis(bb.getXSectArea()));

		WorldDistanceHandler.exportDistance(HEIGHT, bb.getHeight(), ele, doc);

		parent.appendChild(ele);
	}

	double _myNoise = Optic.INVALID_HEIGHT;
	double _myArea = Optic.INVALID_HEIGHT;

	WorldDistance _myHeight = new WorldDistance(Optic.INVALID_HEIGHT, WorldDistance.METRES);

	public OpticHandler() {
		super(type);

		addAttributeHandler(new HandleDoubleAttribute(NOISE) {
			@Override
			public void setValue(final String name, final double val) {
				_myNoise = val;
			}
		});
		addAttributeHandler(new HandleDoubleAttribute(AREA) {
			@Override
			public void setValue(final String name, final double val) {
				_myArea = val;
			}
		});

		addHandler(new WorldDistanceHandler(HEIGHT) {
			@Override
			public void setWorldDistance(final WorldDistance res) {
				_myHeight = res;
			}
		});
	}

	@Override
	public void elementClosed() {

		Optic res = null;

		// do we have height?
		if (_myHeight == null) {
			res = new ASSET.Models.Mediums.Optic(_myNoise,
					new WorldDistance(Optic.INVALID_HEIGHT, WorldDistance.METRES));

		} else {
			res = new ASSET.Models.Mediums.Optic(_myArea, _myHeight);
		}

		res.setXSectArea(_myArea);
		setMedium(EnvironmentType.VISUAL, res);

		// reset vars
		_myNoise = Optic.INVALID_HEIGHT;
		_myArea = Optic.INVALID_HEIGHT;
		_myHeight = null;

	}

	abstract public void setMedium(int index, ASSET.Models.Vessels.Radiated.RadiatedCharacteristics.Medium med);
}