
package ASSET.Util.XML.Vessels.Util;

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

import ASSET.Participants.Category;

abstract public class CategoryHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader {

	private final static String catType = "Category";

	static public void exportThis(final ASSET.Participants.Category toExport, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {
		exportThis(catType, toExport, parent, doc);
	}

	static public void exportThis(final String catName, final ASSET.Participants.Category toExport,
			final org.w3c.dom.Element parent, final org.w3c.dom.Document doc) {
		// create the element
		final org.w3c.dom.Element stat = doc.createElement(catName);

		// set the attributes
		stat.setAttribute("Environment", toExport.getEnvironment());
		stat.setAttribute("Force", toExport.getForce());
		stat.setAttribute("Type", toExport.getType());

		// add to parent
		parent.appendChild(stat);
	}

	String env;

	String force;

	String type;

	public CategoryHandler() {
		this(catType);
	}

	public CategoryHandler(final String catName) {
		super(catName);

		super.addAttributeHandler(new HandleAttribute("Environment") {
			@Override
			public void setValue(final String name, final String val) {
				env = ASSET.Participants.Category.checkEnv(val);
			}
		});
		super.addAttributeHandler(new HandleAttribute("Force") {
			@Override
			public void setValue(final String name, final String val) {
				force = ASSET.Participants.Category.checkForce(val);
			}
		});
		super.addAttributeHandler(new HandleAttribute("Type") {
			@Override
			public void setValue(final String name, final String val) {
				type = ASSET.Participants.Category.checkType(val);
			}
		});

	}

	@Override
	public void elementClosed() {
		// just check that all of the parameters are st
		if ((force == null) || (env == null) || (type == null))
			throw new RuntimeException("Not all parameters set for category");

		// create the category
		final Category cat = new Category(force, env, type);

		setCategory(cat);
	}

	abstract public void setCategory(ASSET.Participants.Category cat);

}