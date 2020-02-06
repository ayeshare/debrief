
package org.mwc.cmap.core.wizards;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.mwc.cmap.core.CorePlugin;

public class MessageWizardPage extends WizardPage
{
	private final String _message;

	public MessageWizardPage(final String pageName, final String pageTitle,
			final String pageDescription, final String message, final String imagePath)
	{
		super(pageName);
		setTitle(pageTitle);
		setDescription(pageDescription);
		_message = message;
		
		// ok, now try to set the image
		if (imagePath != null)
		{
			final ImageDescriptor id = AbstractUIPlugin.imageDescriptorFromPlugin(
					"org.mwc.debrief.core", imagePath);
			if (id != null)
				super.setImageDescriptor(id);
			else
				CorePlugin.logError(IStatus.WARNING, "Wizard image file not found for:"
						+ imagePath, null);
		}

	}

	public void createControl(final Composite parent)
	{
		final Composite composite = new Composite(parent, SWT.NONE);
		setControl(composite);
		final Label txt = new Label(composite, SWT.WRAP);
		txt.setText(_message);
		txt.setSize(600, 200);
	}
}