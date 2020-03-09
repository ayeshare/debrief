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

import java.awt.Button;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MessageDialog extends WorkDialog implements ActionListener {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private final Button okButton;
	private final MessagePanel messagePanel;

	public MessageDialog(final Frame frame, final DialogClient client, final String title, final String message,
			final Image image) {
		this(frame, client, title, message, image, false);
	}

	public MessageDialog(final Frame frame, final DialogClient client, final String title, final String message,
			final Image image, final boolean modal) {

		super(frame, client, title, modal);

		messagePanel = new MessagePanel(image, message);
		okButton = addButton("Ok");
		okButton.addActionListener(this);
		setWorkPanel(messagePanel);
	}

	@Override
	public void actionPerformed(final ActionEvent event) {
		dispose();
	}

	@Override
	public void doLayout() {
		okButton.requestFocus();
		super.doLayout();
	}
}

class MessagePanel extends Postcard {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private Label label;

	public MessagePanel(final Image image, final String message) {
		super(image, new Panel());
		getPanel().add(label = new Label(message, Label.CENTER));
	}

	public MessagePanel(final String message) {
		this(null, message);
	}

	public void setMessage(final String message) {
		label.setText(message);
	}
}
