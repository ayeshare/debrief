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

package org.mwc.debrief.core.wizards.core;

import java.awt.Color;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.mwc.cmap.core.wizards.EnterStringPage;
import org.mwc.cmap.core.wizards.MessageWizardPage;
import org.mwc.cmap.core.wizards.SelectColorPage;

import Debrief.Wrappers.SensorWrapper;

public class NewSensorWizard extends Wizard {
	private static final String PAGE_TITLE = "New sensor";
	private EnterStringPage namePage;
	private SelectColorPage colorPage;

	public NewSensorWizard() {
	}

	@Override
	public void addPages() {
		final String imagePath = "images/NewSensor.png";
		final String helpContext = "org.mwc.debrief.help.NewSensor";

		// do we know the solution wrapper?

		// ok, we need to let the user enter the solution wrapper name
		namePage = new EnterStringPage(null, "NameHere", PAGE_TITLE,
				"This wizard will lead you through creating a new Sensor.\n"
						+ "Please provide the name for this sensor",
				"a one-word title for this block of sensor contacts (e.g. S2046)", imagePath, helpContext, true, null);
		addPage(namePage);

		// ok, we need to let the user enter the solution wrapper name
		colorPage = new SelectColorPage(null, Color.RED, PAGE_TITLE, "Now format the sensor",
				"The default color for this new sensor", imagePath, helpContext, null, false);
		addPage(colorPage);

		final String message = "The sensor will now be added to the specified track, \n"
				+ "and provided with the specified default color - you\n"
				+ "can customise the sensor further in the Properties window\n"
				+ "but first select it in the Outline View";
		final MessageWizardPage messagePage = new MessageWizardPage("finalMessage", PAGE_TITLE, "Steps complete",
				message, imagePath);
		addPage(messagePage);

	}

	@Override
	public IWizardPage getPage(final String name) {
		return super.getPage(name);
	}

	/**
	 * get either the new solution wrapper, or the one passed in
	 *
	 * @return
	 */
	public SensorWrapper getSensorWrapper() {
		final SensorWrapper res = new SensorWrapper(namePage.getString());
		res.setColor(colorPage.getColor());
		return res;
	}

	@Override
	public boolean performFinish() {
		return true;
	}

}
