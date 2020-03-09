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
package info.limpet.stackedcharts.ui.editor.figures;

import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.Button;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;

import info.limpet.stackedcharts.ui.editor.Activator;
import info.limpet.stackedcharts.ui.editor.StackedchartsImages;

public class AxisNameFigure extends DirectionalShape {
	private static volatile Font boldFont;
	private final DirectionalLabel nameLabel;

	public AxisNameFigure(final ActionListener deleteHandler) {

		add(new Label(StackedchartsImages.getImage(StackedchartsImages.DESC_AXIS)));
		nameLabel = new DirectionalLabel(Activator.FONT_10);
		nameLabel.setTextAlignment(PositionConstants.TOP);

		add(nameLabel);

		final Button button = new Button(StackedchartsImages.getImage(StackedchartsImages.DESC_DELETE));
		button.setToolTip(new Label("Remove this axis from the chart"));
		button.addActionListener(deleteHandler);
		add(button);

	}

	@Override
	public void setFont(final Font f) {
		nameLabel.setFont(boldFont);
	}

	public void setName(final String name) {

		nameLabel.setText(name);
		// cache font for AxisNameFigure
		if (boldFont == null) {
			final FontData fontData = nameLabel.getFont().getFontData()[0];
			boldFont = new Font(Display.getCurrent(), new FontData(fontData.getName(), fontData.getHeight(), SWT.BOLD));
		}

		nameLabel.setFont(boldFont);
	}

	@Override
	public void setVertical(final boolean vertical) {
		super.setVertical(vertical);
		nameLabel.setVertical(vertical);
	}

}
