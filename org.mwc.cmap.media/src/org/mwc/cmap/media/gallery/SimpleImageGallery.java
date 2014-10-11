/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.cmap.media.gallery;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

public class SimpleImageGallery<T> extends ImageGallery<T, Image> {

	public SimpleImageGallery(Composite parent, int thumbnailWidth,	int thumbnailHeight) {
		super(parent, thumbnailWidth, thumbnailHeight);
	}

	public SimpleImageGallery(Composite parent) {
		super(parent);
	}
}
