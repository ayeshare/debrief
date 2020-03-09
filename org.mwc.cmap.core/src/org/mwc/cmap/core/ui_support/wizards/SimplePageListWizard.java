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

package org.mwc.cmap.core.ui_support.wizards;

import java.util.Iterator;
import java.util.Vector;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

/**
 * simple wizard that takes series of pages, showing them in order
 *
 * @author ianmayo
 *
 */
public class SimplePageListWizard extends Wizard {
	private Vector<IWizardPage> _myPages;

	public SimplePageListWizard() {
	}

	@Override
	public void addPages() {
		final Iterator<IWizardPage> iter = _myPages.iterator();
		while (iter.hasNext()) {
			final IWizardPage thisP = iter.next();
			addPage(thisP);
		}
	}

	/**
	 * include the specified wizard page
	 *
	 * @param page
	 */
	public void addWizard(final IWizardPage page) {
		if (_myPages == null)
			_myPages = new Vector<IWizardPage>();

		_myPages.add(page);
	}

	@Override
	public boolean performFinish() {
		return true;
	}

}
