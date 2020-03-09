
package ASSET.Util.XML.Vessels.Util.Mediums;

import ASSET.Models.Environment.EnvironmentType;
import ASSET.Models.Vessels.Radiated.RadiatedCharacteristics.Medium;

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

abstract public class NBHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader {

	final static private String type = "Narrowband";
	final static private String LEVEL = "BaseNoiseLevel";
	final static private String FREQ = "Frequency";

	static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {
		final ASSET.Models.Mediums.NarrowbandRadNoise bb = (ASSET.Models.Mediums.NarrowbandRadNoise) toExport;

		final org.w3c.dom.Element ele = doc.createElement(type);

		ele.setAttribute(LEVEL, writeThis(bb.getBaseNoiseLevel()));
		ele.setAttribute(FREQ, writeThis(bb.getFrequency()));

		parent.appendChild(ele);
	}

	double _myNoise;

	double _myFreq;

	public NBHandler() {
		super(type);

		addAttributeHandler(new HandleDoubleAttribute(LEVEL) {
			@Override
			public void setValue(final String name, final double val) {
				_myNoise = val;
			}
		});
		addAttributeHandler(new HandleDoubleAttribute(FREQ) {
			@Override
			public void setValue(final String name, final double val) {
				_myFreq = val;
			}
		});
	}

	@Override
	public void elementClosed() {
		final Medium bb = new ASSET.Models.Mediums.NarrowbandRadNoise(_myNoise, _myFreq);
		setMedium(EnvironmentType.NARROWBAND, bb);
	}

	abstract public void setMedium(int index, ASSET.Models.Vessels.Radiated.RadiatedCharacteristics.Medium med);
}