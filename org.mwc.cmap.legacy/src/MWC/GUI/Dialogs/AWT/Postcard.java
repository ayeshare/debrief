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

import java.awt.Image;
import java.awt.Insets;
import java.awt.Panel;

public class Postcard extends Panel {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private final Panel panelContainer = new Panel();
	private final ImageCanvas canvas = new ImageCanvas();

	public Postcard(final Image image, final Panel panel) {
		if (image != null)
			setImage(image);
		if (panel != null)
			setPanel(panel);

		setLayout(new RowLayout());
		add(canvas);
		add(panelContainer);
	}

	@Override
	public Insets getInsets() {
		return new Insets(10, 10, 10, 10);
	}

	public Panel getPanel() {
		if (panelContainer.getComponentCount() == 1)
			return (Panel) panelContainer.getComponent(0);
		else
			return null;
	}

	public void setImage(final Image image) {
		Util.waitForImage(this, image);
		canvas.setImage(image);
	}

	public void setPanel(final Panel panel) {
		if (panelContainer.getComponentCount() == 1) {
			panelContainer.remove(getComponent(0));
		}
		panelContainer.add(panel);
	}
}
