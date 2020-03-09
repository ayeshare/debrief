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

package org.mwc.cmap.media.perspective;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.mwc.cmap.media.views.ImagesView;
import org.mwc.cmap.media.views.TestHarnessView;
import org.mwc.cmap.media.views.VideoPlayerView;

public class PlanetmayoPerspective implements IPerspectiveFactory {

	public final static String ID = "org.mwc.cmap.media.perspective.PlanetmayoPerspective";

	@Override
	public void createInitialLayout(final IPageLayout layout) {

		final String editorArea = layout.getEditorArea();

		final IFolderLayout topLeft = layout.createFolder("topLeft", IPageLayout.LEFT, 0.25f, editorArea);
		topLeft.addView(VideoPlayerView.ID + ":1");
		topLeft.addPlaceholder(VideoPlayerView.ID + ":*");

		final IFolderLayout bottomLeft = layout.createFolder("bottomLeft", IPageLayout.BOTTOM, 0.50f, "topLeft");
		bottomLeft.addView(TestHarnessView.ID);

		final IFolderLayout right = layout.createFolder("right", IPageLayout.RIGHT, 0.75f, editorArea);
		right.addView(ImagesView.ID + ":1");
		right.addPlaceholder(ImagesView.ID + ":*");
		layout.setEditorAreaVisible(false);

		layout.addShowViewShortcut(VideoPlayerView.ID);
		layout.addShowViewShortcut(TestHarnessView.ID);
		layout.addShowViewShortcut(ImagesView.ID);
		layout.addPerspectiveShortcut(ID);
	}

}
