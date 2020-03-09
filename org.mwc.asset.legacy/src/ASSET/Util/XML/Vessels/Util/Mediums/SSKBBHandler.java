
package ASSET.Util.XML.Vessels.Util.Mediums;

import ASSET.Models.Environment.EnvironmentType;

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

abstract public class SSKBBHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader {

	final static private String type = "SSKBroadband";
	final static private String LEVEL = "BaseNoiseLevel";
	final static private String SNORT_LEVEL = "SnortNoiseLevel";

	static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {
		final ASSET.Models.Mediums.SSKBroadband bb = (ASSET.Models.Mediums.SSKBroadband) toExport;

		final org.w3c.dom.Element ele = doc.createElement(type);

		ele.setAttribute("BaseNoiseLevel", writeThis(bb.getBaseNoiseLevelFor(null)));
		ele.setAttribute("SnortNoiseLevel", writeThis(bb.getSnortNoiseLevel()));

		parent.appendChild(ele);
	}

	double _myNoise;

	double _mySnortNoise;

	public SSKBBHandler() {
		super(type);

		addAttributeHandler(new HandleDoubleAttribute(LEVEL) {
			@Override
			public void setValue(final String name, final double val) {
				_myNoise = val;
			}
		});
		addAttributeHandler(new HandleDoubleAttribute(SNORT_LEVEL) {
			@Override
			public void setValue(final String name, final double val) {
				_mySnortNoise = val;
			}
		});
	}

	@Override
	public void elementClosed() {
		final ASSET.Models.Mediums.SSKBroadband bb = new ASSET.Models.Mediums.SSKBroadband(_myNoise, _mySnortNoise);
		setMedium(EnvironmentType.BROADBAND_PASSIVE, bb);
	}

	abstract public void setMedium(int index, ASSET.Models.Vessels.Radiated.RadiatedCharacteristics.Medium med);
}