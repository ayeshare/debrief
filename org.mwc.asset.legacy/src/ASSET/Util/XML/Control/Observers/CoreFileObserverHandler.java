
package ASSET.Util.XML.Control.Observers;

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

import ASSET.Scenario.Observers.ScenarioObserver;

abstract class CoreFileObserverHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader {

	private final static String ACTIVE = "Active";
	private final static String DIR_NAME = "directory_name";
	private final static String FILE_NAME = "file_name";
	private final static String NAME = "Name";

	/**
	 * export this core file-related parameters
	 *
	 * @param toExport
	 * @param thisPart
	 */
	static public void exportThis(final ScenarioObserver toExport, final org.w3c.dom.Element thisPart) {
		thisPart.setAttribute(NAME, toExport.getName());
		thisPart.setAttribute(ACTIVE, writeThis(toExport.isActive()));

	}

	protected String _directory;
	protected String _fileName;
	protected boolean _isActive;

	protected String _name;

	public CoreFileObserverHandler(final String type) {
		super(type);

		addAttributeHandler(new HandleBooleanAttribute(ACTIVE) {
			@Override
			public void setValue(final String name, final boolean val) {
				_isActive = val;
			}
		});

		addAttributeHandler(new HandleAttribute(DIR_NAME) {
			@Override
			public void setValue(final String name, final String val) {
				_directory = val;
			}
		});

		addAttributeHandler(new HandleAttribute(FILE_NAME) {
			@Override
			public void setValue(final String name, final String val) {
				// just check we haven't received a duff file-name
				if (val != "") {
					_fileName = val;
				}
			}
		});

		addAttributeHandler(new HandleAttribute(NAME) {
			@Override
			public void setValue(final String name, final String val) {
				_name = val;
			}
		});
	}

	@Override
	public void elementClosed() {

		// and reset
		_name = null;
		_directory = null;
		_fileName = null;
	}

	/**
	 * object is read in, somebody store it.
	 *
	 * @param obs
	 */
	abstract public void setObserver(ScenarioObserver obs);

}