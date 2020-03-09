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
import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.Button;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;

import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.ui.editor.StackedchartsImages;

public class ChartFigure extends RectangleFigure {
	private static volatile Font boldFont;
	private final DirectionalIconLabel chartNameLabel;
	private final JFreeChartFigure chartFigure;
	private final DirectionalShape titleFigure;

	public ChartFigure(final Chart chart, final ActionListener deleteListener) {
		setPreferredSize(-1, 200);
		setBackgroundColor(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		setOutline(false);
		final BorderLayout topLayout = new BorderLayout();
		setLayoutManager(topLayout);
		titleFigure = new DirectionalShape();

		chartNameLabel = new DirectionalIconLabel(StackedchartsImages.getImage(StackedchartsImages.DESC_CHART));
		titleFigure.add(chartNameLabel);
		final Button button = new Button(StackedchartsImages.getImage(StackedchartsImages.DESC_DELETE));
		button.setToolTip(new Label("Remove this chart from the chart set"));
		button.addActionListener(deleteListener);
		titleFigure.add(button);

		add(titleFigure);

		chartFigure = new JFreeChartFigure(chart);
		add(chartFigure, BorderLayout.CENTER);

	}

	@Override
	protected void paintClientArea(final Graphics graphics) {
		super.paintClientArea(graphics);
		graphics.setForegroundColor(Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY));

		final Rectangle clientArea = getClientArea();
		graphics.drawLine(clientArea.getBottomLeft().getTranslated(0, -1),
				clientArea.getBottomRight().getTranslated(0, -1));
	}

	public void setName(final String name) {
		chartNameLabel.getLabel().setText(name);
		// cache font for AxisNameFigure
		if (boldFont == null) {
			final FontData fontData = chartNameLabel.getFont().getFontData()[0];
			boldFont = new Font(Display.getCurrent(), new FontData(fontData.getName(), fontData.getHeight(), SWT.BOLD));
		}
		chartNameLabel.setFont(boldFont);
	}

	public void setVertical(final boolean vertical) {
		titleFigure.setVertical(!vertical);
		final BorderLayout topLayout = (BorderLayout) getLayoutManager();
		if (vertical) {
			topLayout.setConstraint(titleFigure, BorderLayout.TOP);
			chartNameLabel.setVertical(false);
		} else {
			topLayout.setConstraint(titleFigure, BorderLayout.LEFT);
			chartNameLabel.setVertical(true);
		}

		topLayout.invalidate();

		repaint();

	}

	public void updateChart() {
		chartFigure.repaint();
	}
}
