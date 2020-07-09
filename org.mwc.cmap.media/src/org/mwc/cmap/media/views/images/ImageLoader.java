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

package org.mwc.cmap.media.views.images;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.commons.io.IOUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.mwc.cmap.media.gallery.ImageGallery;
import org.mwc.cmap.media.utility.ImageUtils;
import org.mwc.cmap.media.utility.InterruptableInputStream;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ImageLoader implements Runnable {
	private static class GalleryLoaderEntry implements LoaderEntry {
		public String name;
		public Object imageMeta;
		public ImageGallery gallery;
		public boolean visible;

		public GalleryLoaderEntry(final String name, final Object imageMeta, final ImageGallery gallery) {
			this.name = name;
			this.imageMeta = imageMeta;
			this.gallery = gallery;
			visible = gallery.getMainComposite().isVisible();
		}

		@Override
		public boolean isVisible() {
			return visible;
		}

		@Override
		public void load() {
			try {
				if (!gallery.containsImage(imageMeta)) {
					return;
				}
				if (gallery.getMainComposite().isDisposed()) {
					return;
				}
				final Image image = new Image(gallery.getMainComposite().getDisplay(), name);
				final ImageData imageData = image.getImageData();
				final Point scaledSize = ImageUtils.getScaledSize(imageData.width, imageData.height,
						gallery.getThumbnailWidth(), gallery.getThumbnailHeight());
				if (gallery.getMainComposite().isDisposed()) {
					return;
				}
				final Image rescaled = new Image(gallery.getMainComposite().getDisplay(), scaledSize.x, scaledSize.y);
				final Image stretched = new Image(gallery.getMainComposite().getDisplay(), gallery.getThumbnailWidth(),
						gallery.getThumbnailHeight());
				GC gc = new GC(rescaled);
				gc.setAntialias(SWT.ON);
				gc.drawImage(image, 0, 0, imageData.width, imageData.height, 0, 0, scaledSize.x, scaledSize.y);
				gc.dispose();
				gc = new GC(stretched);
				gc.setAntialias(SWT.ON);
				gc.drawImage(image, 0, 0, imageData.width, imageData.height, 0, 0, gallery.getThumbnailWidth(),
						gallery.getThumbnailHeight());
				gc.dispose();

				image.dispose();
				Display.getDefault().asyncExec(new Runnable() {

					@Override
					public void run() {
						if (gallery.containsImage(imageMeta)) {
							gallery.addImage(imageMeta, new ThumbnailPackage(rescaled, stretched));
						}
					}
				});
			} catch (final Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private static class ImagePanelLoader implements LoaderEntry {
		ImagePanel panel;
		String name;
		boolean visible;

		public ImagePanelLoader(final String name, final ImagePanel panel) {
			this.name = name;
			this.panel = panel;
			this.visible = panel.isVisible();
		}

		@Override
		public boolean isVisible() {
			return visible;
		}

		@Override
		public void load() {
			if (name.equals(panel.getCurrentImageFile()) || name.equals(panel.getNextImageFile())) {
				InterruptableInputStream imageInput = null;
				BufferedInputStream iStream = null;
				try {
					iStream = new BufferedInputStream(new FileInputStream(name));
					imageInput = new InterruptableInputStream(iStream) {

						@Override
						protected void checkInterrupted() throws IOException {
							if (!name.equals(panel.getCurrentImageFile()) && !name.equals(panel.getNextImageFile())) {
								throw new IOException("interrupted");
							}
						}
					};
					final Image image = new Image(panel.getDisplay(), imageInput);
					Display.getDefault().asyncExec(new Runnable() {

						@Override
						public void run() {
							if (name.equals(panel.getCurrentImageFile())) {
								panel.setCurrentImage(name, image, false);
							} else if (name.equals(panel.getNextImageFile())) {
								panel.setNextImage(name, image);
							} else {
								image.dispose();
							}
						}
					});
				} catch (final Exception ex) {
					if (imageInput == null || !imageInput.wasInterrupted()) {
						ex.printStackTrace();
					}
				} finally {
					IOUtils.closeQuietly(imageInput);
					IOUtils.closeQuietly(iStream);
				}
			}
		}
	}

	private static interface LoaderEntry {
		boolean isVisible();

		void load();
	}

	private static ImageLoader instance;

	public synchronized static ImageLoader getInstance() {
		if (instance == null) {
			instance = new ImageLoader();
			final Thread thread = new Thread(instance);
			thread.setDaemon(true);
			thread.setPriority(Thread.NORM_PRIORITY - 2);
			thread.start();
		}
		return instance;
	}

	private final Object loaderMutex = new Object();

	private final LinkedList<LoaderEntry> labelsToLoad = new LinkedList<LoaderEntry>();

	private ImageLoader() {
	}

	public void load(final ImagePanel panel) {
		synchronized (loaderMutex) {
			boolean load = false;
			if (panel.shouldLoadCurrentImage()) {
				load = true;
				labelsToLoad.add(new ImagePanelLoader(panel.getCurrentImageFile(), panel));
				panel.currentImagePassedToLoad();
			}
			if (panel.shouldLoadNextImage()) {
				load = true;
				labelsToLoad.add(new ImagePanelLoader(panel.getNextImageFile(), panel));
				panel.nextImagePassedToLoad();
			}
			if (load) {
				loaderMutex.notify();
				Thread.yield();
			}
		}
	}

	public void load(final String filename, final Object imageMeta, final ImageGallery gallery) {
		synchronized (loaderMutex) {
			labelsToLoad.add(new GalleryLoaderEntry(filename, imageMeta, gallery));
			loaderMutex.notify();
		}
	}

	@Override
	public void run() {
		while (true) {
			LoaderEntry toLoad;
			synchronized (loaderMutex) {
				if (labelsToLoad.isEmpty()) {
					try {
						loaderMutex.wait();
					} catch (final InterruptedException ex) {
						// ignore
					}
					continue;
				}
				toLoad = null;
				final Iterator<LoaderEntry> iterator = labelsToLoad.iterator();
				while (iterator.hasNext()) {
					final LoaderEntry loadEntry = iterator.next();
					if (loadEntry.isVisible()) {
						toLoad = loadEntry;
						iterator.remove();
						break;
					}
				}
				if (toLoad == null) {
					toLoad = labelsToLoad.poll();
				}
			}
			toLoad.load();
		}
	}

}