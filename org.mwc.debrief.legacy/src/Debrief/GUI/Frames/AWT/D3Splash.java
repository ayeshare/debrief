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

package Debrief.GUI.Frames.AWT;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;

public final class D3Splash extends Window {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private final Image imageD;

	Image imageE;
	Image imageB;
	Image imageR;
	Image imageI;
	private final Image imageK;
	Image imageF;
	private final Image image2;
	private final Image crest;
	private Image off;

	private Graphics offG;

	private String theStr = "Loading editors....";

	// Required for Visual Designer support
	public D3Splash(final Frame parent) {

		super(parent);

		final MediaTracker mt = new MediaTracker(this);
		final String srv = "";
		imageD = Toolkit.getDefaultToolkit().getImage(srv + "d.gif");
		mt.addImage(imageD, 0);
		imageK = Toolkit.getDefaultToolkit().getImage(srv + "k.gif");
		mt.addImage(imageK, 1);
		image2 = Toolkit.getDefaultToolkit().getImage(srv + "two.gif");
		mt.addImage(image2, 1);
		crest = Toolkit.getDefaultToolkit().getImage(srv + "d3logo.gif");
		mt.addImage(crest, 1);
		try {
			mt.waitForAll();
		} catch (final Exception e) {
			MWC.Utilities.Errors.Trace.trace(e);
		}
		initForm();

	}

	private void initForm() {

		this.setLayout(new java.awt.FlowLayout());

		this.setSize(590, 230);
		this.setBackground(Color.lightGray);

		final Rectangle sz = this.getBounds();
		final Dimension ssz = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((ssz.width - sz.width) / 2, (ssz.height - sz.height) / 2);
	}

	@Override
	public final void paint(final Graphics p2) {
		final Dimension sz = this.getSize();
		int x = 5;
		int y = 60;
		// if(off == null)
		{
			off = this.createImage(sz.width, sz.height);
			offG = off.getGraphics();
			offG.setFont(new Font("Sans Serif", Font.PLAIN, 14));
			offG.drawString("Debrief 3 - another great MWC product from MAL", 30, 20);
			offG.drawString(theStr, 30, 40);

			offG.drawImage(crest, x, y, this);
			offG.draw3DRect(3, 3, sz.width - 7, sz.height - 7, false);
		}
		x += crest.getWidth(this);
		if (image2 != null) {
			final int ht = crest.getHeight(this);
			final int ht2 = imageD.getHeight(this);
			y = y + (ht - ht2) / 2 + 5;

			offG.drawImage(imageD, x, y, this);
			x += imageD.getWidth(this);
			offG.drawImage(image2, x, y, this);
			x += image2.getWidth(this);
			offG.drawImage(imageK, x, y, this);
		}
		p2.drawImage(off, 0, 0, this);
	}

	@Override
	public final void setVisible(final boolean p1) {
		super.setVisible(p1);

		try {
			Thread.sleep(2000);

			theStr = "Loading Coastline";

		} catch (final Exception e) {
		}

	}

	@Override
	public final void update(final Graphics p1) {
		paint(p1);
	}

}
