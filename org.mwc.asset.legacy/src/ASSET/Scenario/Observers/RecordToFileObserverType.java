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

package ASSET.Scenario.Observers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import ASSET.ScenarioType;
import MWC.GUI.Editable;
import MWC.Utilities.TextFormatting.GMTDateFormat;

/**
 * Created by IntelliJ IDEA. User: Ian Date: 24-Jul-2003 Time: 12:47:01 To
 * change this template use Options | File Templates.
 */
public abstract class RecordToFileObserverType extends CoreObserver {
	static public class RecordToFileObserverInfo extends MWC.GUI.Editable.EditorType {

		/**
		 * constructor for editable details of a set of Layers
		 *
		 * @param data the Layers themselves
		 */
		public RecordToFileObserverInfo(final RecordToFileObserverType data) {
			super(data, data.getName(), "Edit");
		}

		/**
		 * editable GUI properties for our participant
		 *
		 * @return property descriptions
		 */
		@Override
		public java.beans.PropertyDescriptor[] getPropertyDescriptors() {
			try {
				final java.beans.PropertyDescriptor[] res = {
						prop("Directory", "The directory to place Debrief data-files"),
						prop("Active", "Whether this observer is active"),
						prop("FileName", "The filename template to use"), prop("Name", "The name of this observer"), };

				return res;
			} catch (final java.beans.IntrospectionException e) {
				return super.getPropertyDescriptors();
			}
		}

	}

	/**
	 * the data format we use
	 */
	protected static java.text.DecimalFormat df = new java.text.DecimalFormat("0.00 ");

	/**
	 * the directory we output to
	 */
	private File _directoryName;

	/***************************************************************
	 * constructor
	 ***************************************************************/

	/**
	 * the filename to use (if supplied)
	 */
	private String _fileName;

	/***************************************************************
	 * member methods
	 ***************************************************************/

	/**
	 * create a new monitor
	 *
	 * @param directoryName the directory to output the plots to
	 * @param directoryName the file to output the plots to
	 */
	public RecordToFileObserverType(final String directoryName, final String fileName, final String observerName,
			final boolean isActive) {
		super(observerName, isActive);
		_fileName = fileName;

		// put the dir name into a file
		setDirectory(directoryName);
	}

	/**
	 * hey, we haven't got an editor, better create one
	 *
	 * @return a new property editor for this object
	 */
	protected Editable.EditorType createEditor() {
		return new RecordToFileObserverInfo(this);
	}

	/**
	 * ok, do the actual legwork of creating the file
	 *
	 * @param scenario the new scenario we're processing
	 * @return the writer stream we created
	 * @throws java.io.IOException
	 */
	protected FileWriter createOutputFileWriter(final ScenarioType scenario) throws IOException {

		String theName = null;
		if (getFileName() != null) {
			theName = new String(getFileName());

			// ok. does the name contain any of our special characters?
			// start off with the scenario name
			theName = theName.replaceAll("%s", scenario.getName());

			// now the current DTG
			final DateFormat sdf = new GMTDateFormat("yyMMdd_HHmmss");
			theName = theName.replaceAll("%d", sdf.format(new Date()));

		} else
			theName = newName(scenario.getName());

		// check if we need to append our special suffix
		if (!theName.toUpperCase().endsWith(getMySuffix().toUpperCase())) {
			theName += "." + getMySuffix();
		}

		// check we have the output directory
		getDirectory().mkdirs();

		final File theFile = new File(getDirectory(), theName);

		final FileWriter os = new FileWriter(theFile);
		return os;
	}

	/**
	 * retrieve the build date for this software
	 *
	 * @return auto-generated build date
	 */
	protected String getBuildDate() {
		return ASSET.GUI.VersionInfo.getVersion();
	}

	/**
	 * get the directory name
	 */
	public File getDirectory() {
		return _directoryName;
	}

	public String getFileName() {
		return _fileName;
	}

	/**
	 * get the editor for this item
	 *
	 * @return the BeanInfo data for this editable object
	 */
	@Override
	public MWC.GUI.Editable.EditorType getInfo() {
		if (_myEditor == null)
			_myEditor = createEditor();

		return _myEditor;
	}

	/**
	 * determine the normal suffix for this file type
	 */
	abstract protected String getMySuffix();

	//////////////////////////////////////////////////
	// editor support
	//////////////////////////////////////////////////

	/**
	 * whether there is any edit information for this item this is a convenience
	 * function to save creating the EditorType data first
	 *
	 * @return yes/no
	 */
	@Override
	public boolean hasEditor() {
		return true;
	}

	/**
	 * create a unique name for this run
	 */
	protected String newName(final String name) {
		return getName() + "_" + name;
	}

	/**
	 * set the directory name
	 */
	public void setDirectory(final File dirName) {
		if (dirName != null)
			_directoryName = dirName;
		else
			_directoryName = new File("C:\\temp");
	}

	/**
	 * set the directory name
	 */
	public void setDirectory(String dirName) {
		if (dirName == null)
			dirName = "c:\\temp";

		setDirectory(new File(dirName));
	}

	//////////////////////////////////////////////////////////////////////
	// editable properties
	//////////////////////////////////////////////////////////////////////

	public void setFileName(final String fileName) {
		this._fileName = fileName;
	}
}
