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

package org.mwc.debrief.core.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.mwc.debrief.core.wizards.NewProjectWizard;

public class CreateProjectHandler extends AbstractHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final Shell shell = PlatformUI.getWorkbench().getModalDialogShellProvider().getShell();

		final NewProjectWizard wizard = new NewProjectWizard(false);
		wizard.init(PlatformUI.getWorkbench(), null);

		final WizardDialog dialog = new WizardDialog(shell, wizard) {
			@Override
			public void setShellStyle(final int newShellStyle) {
				super.setShellStyle(newShellStyle | SWT.SHEET);
			}
		};
		dialog.create();
		dialog.open();
		return null;
	}

}
