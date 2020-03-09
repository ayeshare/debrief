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

package MWC.GUI.Dialogs.AWT;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Dialog;
import java.awt.Image;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class MessageDialogLauncher extends Panel implements DialogClient, ActionListener {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private final Applet applet;
	private Button messageDialogButton;
	private MessageDialog messageDialog;
	private final Checkbox modal = new Checkbox("modal");

	public MessageDialogLauncher(final Applet applet) {
		this.applet = applet;

		add(modal);

		add(messageDialogButton = new Button("Launch Message Dialog"));

		messageDialogButton.addActionListener(this);
	}

	@Override
	public void actionPerformed(final ActionEvent event) {
		final Image image = applet.getImage(applet.getCodeBase(), "gifs/information.gif");
		if (messageDialog == null) {
			messageDialog = new MessageDialog(Util.getFrame(this), this, "Example Message Dialog",
					"This is an example of a message dialog.", image, modal.getState());
		} else {
			if (modal.getState())
				messageDialog.setModal(true);
			else
				messageDialog.setModal(false);
		}
		messageDialog.setVisible(true);
	}

	@Override
	public void dialogCancelled(final Dialog d) {
		applet.showStatus("Message Dialog Cancelled");
	}

	@Override
	public void dialogDismissed(final Dialog d) {
		applet.showStatus("MessageDialog Dismissed");
	}
}

public class MessageDialogTester extends Applet {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void init() {
		setLayout(new BorderLayout());
		add(new MessageDialogLauncher(this), "Center");
	}
}
