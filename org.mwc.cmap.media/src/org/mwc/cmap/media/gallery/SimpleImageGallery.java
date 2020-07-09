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

package org.mwc.cmap.media.gallery;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

public class SimpleImageGallery<T> extends ImageGallery<T, Image> {

	public SimpleImageGallery(final Composite parent) {
		super(parent);
	}

	public SimpleImageGallery(final Composite parent, final int thumbnailWidth, final int thumbnailHeight) {
		super(parent, thumbnailWidth, thumbnailHeight);
	}
}
