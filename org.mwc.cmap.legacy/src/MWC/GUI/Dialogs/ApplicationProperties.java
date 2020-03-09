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

package MWC.GUI.Dialogs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Encapsulates the java.util.Properties class to be more programmer-friendly.
 * Given a file name that resolves to a file that is
 * java.util.Properties-readable, all FileStream work is done for the client
 * programmer. This class also offers the capability to find properties based on
 * a substring to match.
 *
 * @see java.util.Properties
 *
 * @author Dave Lamy (daveandjeri@stubbydog.com)
 * @version $Revision: 1.2 $ $Date: 2004/05/25 15:23:23 $
 */
public class ApplicationProperties {
	private final File propertiesFile;
	private final Properties appProperties;
	private String introHeader;

	/**
	 * Will first verify that the given String file name is a valid file, and if so
	 * will go ahead and load the properties in that file. This constructor
	 * signature will immediately load the properties from the given file (same as
	 * calling <code>ApplicationProperties(fileName, true)
	 </code>.
	 *
	 * @param fileName - the absolute file path and name.
	 * @throws IOException - if there are problems during the loading of the file.
	 */
	public ApplicationProperties(final String fileName) throws IOException {
		this(fileName, true);
	} // constructor

	/**
	 * Will first verify that the given String file name is a valid file, and if it
	 * is valid and the loadNow parameter is set to <code>true</code>, will go ahead
	 * and load the properties in that file
	 *
	 * @param fileName - the absolute file name (path and all).
	 * @param loadNow  - whether or not to go ahead and load the properties from the
	 *                 file.
	 * @throws IOException - if there are problems during the loading of the file.
	 */
	public ApplicationProperties(final String fileName, final boolean loadNow) throws IOException {
		// set the class variables
		propertiesFile = new File(fileName);
		if (!propertiesFile.exists()) {
			propertiesFile.createNewFile();
		} // if
		if (!propertiesFile.isFile()) {
			throw new FileNotFoundException(fileName + "\nSpecified file name must resolve to an actual file.");
		} // if
		appProperties = new Properties();

		if (loadNow) {
			// go ahead and populate the properties object
			loadProperties();
		} // if

	} // constructor

	public ApplicationProperties(final String fileName, final String header) throws IOException {
		this(fileName, true);
		introHeader = header;
	}

	/**
	 * Tests for the existence of the given property name.
	 *
	 * @param propertyName - the name to look for
	 * @return boolean - the success or failure of the search
	 */
	public boolean containsPropertyName(final String propertyName) {
		return appProperties.containsKey(propertyName);
	} // containsPropertyName

	/**
	 * Tests for the existence of the given property value.
	 *
	 * @param propertyValue - the value to look for
	 * @return boolean - the success or failure of the search
	 */
	public boolean containsPropertyValue(final String propertyValue) {
		return appProperties.containsValue(propertyValue);
	} // containsPropertyValue

	/**
	 * Returns a Map of all property name/value pairs that match the passed-in
	 * String in any way. The test for matching is done via the String method
	 * indexOf, meaning that the match expression will resolve to true if the
	 * passed-in String can be found anywhere in the property name.
	 *
	 * @param propertyNameToMatch - the String to attempt to match to the property
	 *                            name.
	 * @return Map - a Map of all property name/value pairs whose names matched the
	 *         given String.
	 */
	@SuppressWarnings({ "rawtypes" })
	public Map<String, String> /* String propertyName, String value */ getPropertiesLike(
			final String propertyNameToMatch) {
		final Map<String, String> retMap = new HashMap<String, String>();
		final Enumeration allPropertyNames = appProperties.propertyNames();
		while (allPropertyNames.hasMoreElements()) {
			final String propertyName = (String) allPropertyNames.nextElement();
			if (propertyName.indexOf(propertyNameToMatch) > -1) {
				retMap.put(propertyName, appProperties.getProperty(propertyName));
			} // if
		} // while
		return retMap;
	} // getPropertiesLike

	/**
	 * Gets a property value given a property name. Will return null if no name
	 * matches.
	 *
	 * @param propertyName - the property name to retrieve
	 * @return String - the property's value
	 */
	public String getProperty(final String propertyName) {
		return appProperties.getProperty(propertyName);
	}

	/**
	 * Loads all properties from the properties file given at construction time.
	 * This method works like a reload (or a first-time load if the object was
	 * constructed with the loadNow parameter set to <code>false</code>). All
	 * in-memory changes will be lost, so if previous changes are to be persisted,
	 * call <code>storeProperties()</code> prior to performing the reload.
	 */
	public void loadProperties() throws IOException {
		final FileInputStream propertiesStream = new FileInputStream(propertiesFile);
		appProperties.load(propertiesStream);
	} // loadProperties

	/**
	 * Removes the given property. Removal will not be persisted until
	 * storeProperties() is called.
	 */
	public void removeProperty(final String propertyName) {
		appProperties.remove(propertyName);
	}

	/**
	 * Sets a property value given a property name. Will create an entry if none
	 * exists for the given property name; will overwrite if one does already exist.
	 * The set will not be persisted until storeProperties() is called.
	 *
	 * @param propertyName  - the property name to set
	 * @param propertyValue - the property's value
	 */
	public void setProperty(final String propertyName, final String propertyValue) {
		appProperties.setProperty(propertyName, propertyValue);
	} // setProperty

	/**
	 * Stores the properties back into the file. The storage process will overwrite
	 * all data that was in the properties file before the storeProperties was
	 * called.
	 */
	public void storeProperties() throws IOException {
		try {
			final FileOutputStream outStream = new FileOutputStream(propertiesFile);
			appProperties.store(outStream, propertiesFile.toString() + introHeader);
		} catch (final java.io.FileNotFoundException fe) {
			System.out.println("** Please check that properties file is not read-only");
		}
	} // storeProperties

}