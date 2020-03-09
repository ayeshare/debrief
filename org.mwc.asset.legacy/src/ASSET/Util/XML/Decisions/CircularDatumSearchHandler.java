
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

import ASSET.Models.Decision.Tactical.CircularDatumSearch;
import ASSET.Util.XML.Decisions.Tactical.CoreDecisionHandler;
import MWC.GenericData.WorldDistance;
import MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler;

abstract class CircularDatumSearchHandler extends CoreDecisionHandler {

	private final static String type = "CircularDatumSearch";
	private final static String WEAPON_TYPE = "LaunchWeaponType";
	private final static String WEAPON_TYPE_FILENAME = "LaunchWeaponFileName";
	private final static String BUOY_TYPE = "LaunchBuoyType";
	private final static String BUOY_TYPE_FILENAME = "LaunchBuoyFileName";
	private final static String NUM_BUOYS = "NumBuoys";

	static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {

		// todo: implement export for this behaviour
		MWC.Utilities.Errors.Trace.trace("Problem saving Circular Datum Search");

		// // create ourselves
		// final org.w3c.dom.Element thisPart = doc.createElement(type);
		//
		// // get data item
		// final LaunchWeapon bb = (LaunchWeapon)toExport;
		//
		// // output it's attributes
		// thisPart.setAttribute("Name", bb.getName());
		// // is there a filename?
		// final String fName = bb.getLaunchFilename();
		// if(fName != null)
		// {
		// try{
		//
		// // write the details to file
		// final java.io.FileWriter fw = new java.io.FileWriter(fName);
		// final java.io.BufferedWriter bw = new java.io.BufferedWriter(fw);
		// bw.write(bb.getLaunchType());
		//
		// // insert the filename
		// thisPart.setAttribute(TYPE_FILENAME, fName);
		//
		// }
		// catch(java.io.IOException ee)
		// {
		// ee.printStackTrace();
		// }
		//
		// }
		// else
		// {
		// // insert the launch details
		// // DON't INSERT THE DETAILS
		// }
		// ASSET.Util.XML.Decisions.Util.TargetTypeHandler.exportSequence(bb.getTargetType(),
		// thisPart,
		// doc);
		// WorldDistanceHandler.exportDistance(bb.getLaunchRange(), thisPart, doc);
		// DurationHandler.exportDuration(bb.getCoolOffTime(), thisPart, doc);
		//
		// parent.appendChild(thisPart);

	}

	WorldDistance _radius;
	TargetType _targetType;
	String _weaponType;
	String _weaponFileName;
	String _buoyType;
	String _buoyFileName;

	int _numBuoys;

	public CircularDatumSearchHandler() {
		super(type);

		addAttributeHandler(new HandleIntegerAttribute(NUM_BUOYS) {
			@Override
			public void setValue(final String name, final int val) {
				_numBuoys = val;
			}
		});

		//////////////////////////////////////////////////
		// FIRST THE BUOY
		//////////////////////////////////////////////////

		addAttributeHandler(new HandleAttribute(BUOY_TYPE_FILENAME) {
			@Override
			public void setValue(final String name, final String val) {
				final StringBuffer res = new StringBuffer();
				// we've received the weapon description as a filename, load it
				try {

					final java.io.FileReader fi = new java.io.FileReader(val);
					final java.io.BufferedReader bg = new java.io.BufferedReader(fi);
					String next = bg.readLine();
					while (next != null) {
						res.append(next);
						res.append("\n");
						next = bg.readLine();
					}

					bg.close();
					// store the data
					_buoyFileName = val;
					_buoyType = res.toString();
				} catch (final java.io.IOException ee) {
					ee.printStackTrace();
				}
			}
		});
		addAttributeHandler(new HandleAttribute(BUOY_TYPE) {
			@Override
			public void setValue(final String name, final String val) {
				// so, before we learn to read in the actual type,
				// set it to the hard-coded value used for testing launch
				_buoyType = val;
			}
		});

		//////////////////////////////////////////////////
		// AND THE WEAPON
		//////////////////////////////////////////////////

		addAttributeHandler(new HandleAttribute(WEAPON_TYPE_FILENAME) {
			@Override
			public void setValue(final String name, final String val) {
				final StringBuffer res = new StringBuffer();
				// we've received the weapon description as a filename, load it
				try {

					final java.io.FileReader fi = new java.io.FileReader(val);
					final java.io.BufferedReader bg = new java.io.BufferedReader(fi);
					String next = bg.readLine();
					while (next != null) {
						res.append(next);
						res.append("\n");
						next = bg.readLine();
					}
					bg.close();

					// store the data
					_weaponFileName = val;
					_weaponType = res.toString();
				} catch (final java.io.IOException ee) {
					ee.printStackTrace();
				}
			}
		});
		addAttributeHandler(new HandleAttribute(WEAPON_TYPE) {
			@Override
			public void setValue(final String name, final String val) {
				// so, before we learn to read in the actual type,
				// set it to the hard-coded value used for testing launch
				_weaponType = val;
			}
		});

		addHandler(new WorldDistanceHandler() {
			@Override
			public void setWorldDistance(final WorldDistance res) {
				_radius = res;
			}
		});

		addHandler(new ASSET.Util.XML.Decisions.Util.TargetTypeHandler() {
			@Override
			public void setTargetType(final TargetType type) {
				_targetType = type;
			}
		});

	}

	@Override
	public void elementClosed() {
		final CircularDatumSearch search = new CircularDatumSearch(_targetType, _radius, _numBuoys, _weaponType,
				_weaponFileName, _buoyType, _buoyFileName);

		// set the parent attributes
		super.setAttributes(search);

		// finally output it
		setModel(search);

		// clear the data
		_radius = null;
		_targetType = null;
		_weaponFileName = null;
		_weaponType = null;
		_buoyFileName = null;
		_buoyType = null;
	}

	abstract public void setModel(ASSET.Models.DecisionType dec);

}