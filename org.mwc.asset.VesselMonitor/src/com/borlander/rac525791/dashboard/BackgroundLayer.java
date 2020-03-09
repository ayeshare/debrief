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

package com.borlander.rac525791.dashboard;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import com.borlander.rac525791.dashboard.layout.BaseDashboardLayout;
import com.borlander.rac525791.dashboard.layout.DashboardImages;
import com.borlander.rac525791.dashboard.layout.DashboardUIModel;
import com.borlander.rac525791.dashboard.layout.SelectableImageFigure;
import com.borlander.rac525791.draw2d.ext.InvisibleRectangle;

public class BackgroundLayer extends InvisibleRectangle {
	protected static class DoubleGradientRectangle extends InvisibleRectangle {
		private static final Color TOP = new Color(null, 129, 129, 129);
		private static final Color BOTTOM = new Color(null, 88, 88, 88);
		private static final Color MIDDLE = new Color(null, 200, 200, 200);

		@Override
		protected void fillShape(final Graphics g) {
			final Rectangle b = getBounds();
			final int smallHeight = b.height / 15;

			g.pushState();
			g.setForegroundColor(TOP);
			g.setBackgroundColor(MIDDLE);
			g.fillGradient(b.x, b.y, b.width, smallHeight, true);

			g.setForegroundColor(MIDDLE);
			g.setBackgroundColor(BOTTOM);

			g.fillGradient(b.x, b.y + smallHeight, b.width, b.height - smallHeight, true);

			g.restoreState();
		}
	}

	private class Layout extends BaseDashboardLayout {
		private final Rectangle RECT = new Rectangle();

		public Layout(final DashboardUIModel uiModel) {
			super(uiModel);
		}

		@Override
		public void layout(final IFigure container) {
			placeAtTopLeft(container, RECT);
			RECT.setSize(getSuite(container).getPreferredSize());

			myControls.setBounds(RECT);
			myTopText.setBounds(RECT);

			myBack.setBounds(container.getBounds());
		}
	}

	IFigure myBack;

	ImageFigure myControls;

	ImageFigure myTopText;

	public BackgroundLayer(final DashboardUIModel uiModel) {
		setLayoutManager(new Layout(uiModel));

		myBack = new DoubleGradientRectangle();
		myControls = new SelectableImageFigure(uiModel) {

			@Override
			protected Image selectImage(final DashboardImages images) {
				return images.getControls();
			}
		};
		// XXX
		myTopText = new SelectableImageFigure(uiModel) {
			@Override
			protected Image selectImage(final DashboardImages images) {
				return images.getControls();
			}
		};

		this.add(myBack);
		this.add(myControls);
//		this.add(myTopText);
	}
}
