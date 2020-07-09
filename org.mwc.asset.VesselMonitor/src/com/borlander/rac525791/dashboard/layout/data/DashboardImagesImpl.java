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

package com.borlander.rac525791.dashboard.layout.data;

import java.util.HashMap;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.mwc.asset.vesselmonitor.Activator;

import com.borlander.rac525791.dashboard.layout.DashboardImages;

/**
 * The very simple implementation similar to
 * org.eclipse.jface.resource.ImageRegistry class.
 *
 * (Written to avoid dependency on jface.resource)
 */
class DashboardImagesImpl implements DashboardImages {
	private static final String CONTROLS = "controls.png";

	private static final String CIRCLES = "circles.png";

	private static final String NUMBERS = "text.png";

	private final String myControlsPath;

	private final String myCirclesPath;

	private final String myNumbersPath;

	private final Display myDisplay;

	private final HashMap<String, Image> myImages = new HashMap<String, Image>();

	public DashboardImagesImpl(final Display display, final String folder, final boolean preload) {
		myControlsPath = folder + "/" + CONTROLS;
		myCirclesPath = folder + "/" + CIRCLES;
		myNumbersPath = folder + "/" + NUMBERS;

		myDisplay = display;
		myDisplay.disposeExec(new Runnable() {
			@Override
			public void run() {
				DashboardImagesImpl.this.dispose();
			}
		});

		if (preload) {
			getNumbers();
			getCircleLids();
			getControls();
		}
	}

	@Override
	public void dispose() {
		for (final Image next : myImages.values()) {
			if (next != null && !next.isDisposed()) {
				next.dispose();
			}
		}
		myImages.clear();
	}

	@Override
	public Image getCircleLids() {
		return getImage(myCirclesPath);
	}

	@Override
	public Image getControls() {
		return getImage(myControlsPath);
	}

	private Image getImage(final String filePath) {
		Image result = myImages.get(filePath);
		if (result == null || result.isDisposed()) {
			// XXX: Strange, but it does not always work for png images
			// Issues with alpha channel were found
			// result = new Image(myDisplay, filePath);

			final ImageDescriptor desc = Activator.getImageDescriptor(filePath);
//			ImageLoader loader = new ImageLoader();
//			loader.load(filePath);
//			result = new Image(myDisplay, loader.data[0]);
			result = desc.createImage();
			myImages.put(filePath, result);
		}
		return result;
	}

	@Override
	public Image getNumbers() {
		return getImage(myNumbersPath);
	}
}
