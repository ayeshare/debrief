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
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class QuestionDialog extends WorkDialog implements ActionListener {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	static private int _defaultTextFieldSize = 20;
	private final Button okButton;
	private final Button cancelButton;
	private final TextField textField;
	private boolean wasCancelled;

	public QuestionDialog(final Frame frame, final DialogClient client, final String title, final String question,
			final Image image) {
		this(frame, client, title, question, null, _defaultTextFieldSize, image);
	}

	public QuestionDialog(final Frame frame, final DialogClient client, final String title, final String question,
			final int textFieldSize, final Image image) {
		this(frame, client, title, question, null, textFieldSize, image);
	}

	public QuestionDialog(final Frame frame, final DialogClient client, final String title, final String question,
			final String initialResponse, final Image image) {
		this(frame, client, title, question, initialResponse, _defaultTextFieldSize, image);
	}

	public QuestionDialog(final Frame frame, final DialogClient client, final String title, final String question,
			final String initialResponse, final int textFieldSize, final Image image) {
		this(frame, client, title, question, initialResponse, textFieldSize, image, false);
	}

	public QuestionDialog(final Frame frame, final DialogClient client, final String title, final String question,
			final String initialResponse, final int textFieldSize, final Image image, final boolean modal) {
		super(frame, client, title, modal);

		QuestionPanel questionPanel;

		okButton = addButton("Ok");
		cancelButton = addButton("Cancel");

		okButton.addActionListener(this);
		cancelButton.addActionListener(this);

		questionPanel = new QuestionPanel(this, question, initialResponse, textFieldSize, image);
		textField = questionPanel.getTextField();
		setWorkPanel(questionPanel);
	}

	@Override
	public void actionPerformed(final ActionEvent ae) {
		if (ae.getSource() == cancelButton)
			wasCancelled = true;
		else
			wasCancelled = false;

		dispose();
	}

	public String getAnswer() {
		return textField.getText();
	}

	public TextField getTextField() {
		return textField;
	}

	public void returnInTextField() {
		okButton.requestFocus();
	}

	@Override
	public void setVisible(final boolean b) {
		textField.requestFocus();
		super.setVisible(b);
	}

	public boolean wasCancelled() {
		return wasCancelled;
	}
}

class QuestionPanel extends Postcard {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private TextField field;
	QuestionDialog dialog;

	public QuestionPanel(final QuestionDialog dialog, final String question, final Image image) {
		this(dialog, question, null, 0, image);
	}

	public QuestionPanel(final QuestionDialog dialog, final String question, final int columns, final Image image) {
		this(dialog, question, null, columns, image);
	}

	public QuestionPanel(final QuestionDialog myDialog, final String question, final String initialResponse,
			final int cols, final Image image) {
		super(image, new Panel());

		final Panel panel = getPanel();
		this.dialog = myDialog;

		panel.setLayout(new RowLayout());
		panel.add(new Label(question));

		if (initialResponse != null) {
			if (cols != 0)
				panel.add(field = new TextField(initialResponse, cols));
			else
				panel.add(field = new TextField(initialResponse));
		} else {
			if (cols != 0)
				panel.add(field = new TextField(cols));
			else
				panel.add(field = new TextField());
		}

		field.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent event) {
				dialog.returnInTextField();
			}
		});
	}

	public TextField getTextField() {
		return field;
	}
}
