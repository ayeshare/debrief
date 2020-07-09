
package ASSET.Util.XML.Decisions;

import ASSET.Models.Decision.TargetType;

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

import ASSET.Models.Decision.Tactical.LaunchWeapon;
import ASSET.Util.XML.Decisions.Tactical.CoreDecisionHandler;
import MWC.GenericData.Duration;
import MWC.GenericData.WorldDistance;
import MWC.Utilities.ReaderWriter.XML.Util.DurationHandler;
import MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler;

abstract class LaunchWeaponHandler extends CoreDecisionHandler {

	private final static String type = "LaunchWeapon";
	private final static String TYPE = "LaunchType";
	private final static String TYPE_FILENAME = "LaunchTypeFileName";

	static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {
		// create ourselves
		final org.w3c.dom.Element thisPart = doc.createElement(type);

		// get data item
		final LaunchWeapon bb = (LaunchWeapon) toExport;

		// first the parent export
		CoreDecisionHandler.exportThis(bb, thisPart, doc);

		// output it's attributes
		// is there a filename?
		final String fName = bb.getLaunchFilename();
		if (fName != null) {
			try {

				// write the details to file
				final java.io.FileWriter fw = new java.io.FileWriter(fName);
				final java.io.BufferedWriter bw = new java.io.BufferedWriter(fw);
				bw.write(bb.getLaunchType());

				bw.close();

				// insert the filename
				thisPart.setAttribute(TYPE_FILENAME, fName);

			} catch (final java.io.IOException ee) {
				ee.printStackTrace();
			}

		} else {
			// insert the launch details
			// DON't INSERT THE DETAILS
		}
		ASSET.Util.XML.Decisions.Util.TargetTypeHandler.exportThis(bb.getTargetType(), thisPart, doc);
		WorldDistanceHandler.exportDistance(bb.getLaunchRange(), thisPart, doc);
		DurationHandler.exportDuration(bb.getCoolOffTime(), thisPart, doc);

		parent.appendChild(thisPart);

	}

	WorldDistance _launchRange;
	Duration _launchTime;
	String _launchType;
	String _fileName;

	TargetType _myTargetType;

	public LaunchWeaponHandler() {
		super(type);
		addAttributeHandler(new HandleAttribute(TYPE_FILENAME) {
			@Override
			public void setValue(final String name, final String val) {
				// store the data
				_fileName = val;
				_launchType = LaunchWeapon.readWeaponFromThisFile(val);
			}
		});
		addAttributeHandler(new HandleAttribute(TYPE) {
			@Override
			public void setValue(final String name, final String val) {
				// so, before we learn to read in the actual type,
				// set it to the hard-coded value used for testing launch

				_launchType = val;
			}
		});

		addHandler(new WorldDistanceHandler() {
			@Override
			public void setWorldDistance(final WorldDistance res) {
				_launchRange = res;
			}
		});

		addHandler(new ASSET.Util.XML.Decisions.Util.TargetTypeHandler() {
			@Override
			public void setTargetType(final TargetType type) {
				_myTargetType = type;
			}
		});
		addHandler(new DurationHandler() {
			@Override
			public void setDuration(final Duration res) {
				_launchTime = res;
			}
		});

	}

	@Override
	public void elementClosed() {
		final LaunchWeapon lnch = new LaunchWeapon();

		super.setAttributes(lnch);

		lnch.setCoolOffTime(_launchTime);
		lnch.setLaunchRange(_launchRange);
		lnch.setTargetType(_myTargetType);
		lnch.setLaunchType(_launchType);
		if (_fileName != null)
			lnch.setLaunchFilename(_fileName);

		// finally output it
		setModel(lnch);

		// clear the data
		_myTargetType = null;
		_fileName = null;
		_launchType = null;
		_launchTime = null;
	}

	abstract public void setModel(ASSET.Models.DecisionType dec);

}