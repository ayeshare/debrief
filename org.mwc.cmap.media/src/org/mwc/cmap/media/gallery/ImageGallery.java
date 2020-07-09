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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.services.IDisposable;

public class ImageGallery<T, I> implements IDisposable {
	public class ImageLabel implements MouseMoveListener, MouseListener, IDisposable {
		private final Composite composite;
		private final Canvas imageLabel;
		private final CLabel textlabel;

		private T imageMeta;
		private I loadedImage;

		public ImageLabel(final Composite parent) {
			composite = new Composite(parent, SWT.NONE);
			final GridData compositeData = new GridData(thumbnailWidth + IMAGE_BORDER_MARGIN,
					thumbnailHeight + IMAGE_BORDER_MARGIN + TEXT_HEIGHT);
			composite.setLayoutData(compositeData);
			composite.setBackgroundMode(SWT.INHERIT_DEFAULT);
			composite.setBackgroundImage(backgroundImages.getTransparent());
			final GridLayout layout = new GridLayout(1, false);
			layout.marginBottom = 0;
			layout.marginHeight = 0;
			layout.marginLeft = 0;
			layout.marginRight = 0;
			layout.marginTop = 0;
			layout.marginWidth = 0;
			layout.verticalSpacing = 0;
			composite.setLayout(layout);

			imageLabel = new Canvas(composite, SWT.CENTER);
			GridData data = new GridData(thumbnailWidth + IMAGE_BORDER_MARGIN, thumbnailHeight + IMAGE_BORDER_MARGIN);
			imageLabel.setLayoutData(data);
			imageLabel.addPaintListener(new PaintListener() {

				@Override
				public void paintControl(final PaintEvent e) {
					Image drawImage = defaultImage;
					if (loadedImage != null) {
						drawImage = elementsBuilder.buildImage(loadedImage);
					}
					if (drawImage != null) {
						final ImageData data = drawImage.getImageData();
						final int widthPadding = (thumbnailWidth + IMAGE_BORDER_MARGIN - data.width) / 2;
						final int heightPadding = (thumbnailHeight + IMAGE_BORDER_MARGIN - data.height) / 2;
						e.gc.drawImage(drawImage, widthPadding, heightPadding);
					}
				}
			});

			textlabel = new CLabel(composite, SWT.CENTER);
			textlabel.setAlignment(SWT.CENTER);
			data = new GridData(thumbnailWidth + IMAGE_BORDER_MARGIN, TEXT_HEIGHT);
			data.horizontalIndent = 0;
			data.horizontalSpan = 0;
			textlabel.setLayoutData(data);
			textlabel.setMargins(0, 0, 0, 0);

			composite.addMouseMoveListener(this);
			imageLabel.addMouseMoveListener(this);
			textlabel.addMouseMoveListener(this);
			composite.addMouseListener(this);
			imageLabel.addMouseListener(this);
			textlabel.addMouseListener(this);
		}

		@Override
		public void dispose() {
			composite.dispose();
			if (loadedImage != null) {
				elementsBuilder.disposeImage(this.loadedImage);
			}
			elementsBuilder.disposeMeta(this.imageMeta);
			imageLabel.dispose();
			textlabel.dispose();
		}

		public Composite getComposite() {
			return composite;
		}

		public I getImage() {
			return loadedImage;
		}

		public Canvas getImageLabel() {
			return imageLabel;
		}

		public T getImageMeta() {
			return imageMeta;
		}

		public CLabel getTextLabel() {
			return textlabel;
		}

		@Override
		public void mouseDoubleClick(final MouseEvent event) {
			event.data = this;
			for (final MouseListener listener : elementMouseListeners) {
				listener.mouseDoubleClick(event);
			}
		}

		@Override
		public void mouseDown(final MouseEvent event) {
			event.data = this;
			for (final MouseListener listener : elementMouseListeners) {
				listener.mouseDown(event);
			}
		}

		@Override
		public void mouseMove(final MouseEvent event) {
			if (highlitedLabel != this) {
				if (highlitedLabel != null) {
					highlitedLabel.setHighlight(false);
				}
				highlitedLabel = this;
				highlitedLabel.setHighlight(true);
			}
			event.data = this;
			for (final MouseMoveListener listener : elementMouseMoveListeners) {
				event.data = this;
				listener.mouseMove(event);
			}
		}

		@Override
		public void mouseUp(final MouseEvent event) {
			if (selectedLabel != this) {
				if (selectedLabel != null) {
					selectedLabel.setSelected(false);
				}
				selectedLabel = this;
				selectedLabel.setSelected(true);
			}
			event.data = this;
			for (final MouseListener listener : elementMouseListeners) {
				listener.mouseUp(event);
			}
		}

		public void redraw() {
			// System.out.println("Redrawing with:<<"+thumbnailWidth+">>");
			// resize image label
			final GridData data = new GridData(thumbnailWidth + IMAGE_BORDER_MARGIN, TEXT_HEIGHT);
			data.horizontalIndent = 0;
			data.horizontalSpan = 0;
			System.out.println("Text " + textlabel.getText());
			textlabel.setLayoutData(data);

			final Point sizePt = imageLabel.getSize();
			imageLabel.redraw(0, 0, sizePt.x, sizePt.y, true);
			imageLabel.setLayoutData(
					new GridData(thumbnailWidth + IMAGE_BORDER_MARGIN, thumbnailHeight + IMAGE_BORDER_MARGIN));

			final GridData gd = new GridData(thumbnailWidth + IMAGE_BORDER_MARGIN,
					thumbnailHeight + IMAGE_BORDER_MARGIN + TEXT_HEIGHT);
			composite.setLayoutData(gd);
			composite.layout();

		}

		public void setHighlight(final boolean highlight) {
			if (selectedLabel == this) {
				return;
			}
			if (highlight) {
				composite.setBackgroundImage(backgroundImages.getHighlightedImage(composite.getSize()));
			} else {
				composite.setBackgroundImage(backgroundImages.getTransparent());
			}
			composite.redraw();
		}

		public void setImage(final I loadedImage) {
			if (this.loadedImage == loadedImage) {
				return;
			}
			if (this.loadedImage != null) {
				elementsBuilder.disposeImage(this.loadedImage);
			}
			this.loadedImage = loadedImage;
			if (!imageLabel.isDisposed() && imageLabel.isVisible()) {
				imageLabel.redraw();
			}
		}

		public void setImageMeta(final T imageMeta) {
			this.imageMeta = imageMeta;
			String imageText;
			imageText = elementsBuilder.buildLabel(imageMeta);
			String labelText = imageText;
			final int indexOfExtension = imageText.lastIndexOf(".");
			if (indexOfExtension != -1) {
				labelText = imageText.substring(0, indexOfExtension);
			}
			textlabel.setText(labelText);
			final Point size = textlabel.computeSize(thumbnailWidth + IMAGE_BORDER_MARGIN, SWT.DEFAULT);
			((GridData) textlabel.getLayoutData()).heightHint = size.y;
			((GridData) composite.getLayoutData()).heightHint = thumbnailHeight + IMAGE_BORDER_MARGIN + size.y + 2;
			imageLabel.layout();
			composite.layout();
		}

		public void setSelected(final boolean select) {
			if (select) {
				composite.setBackgroundImage(backgroundImages.getSelectedImage(composite.getSize()));
			} else {
				composite.setBackgroundImage(backgroundImages.getTransparent());
			}
			composite.redraw();
		}
	}

	public static final int DEFAULT_WIDTH = 90;

	public static final int DEFAULT_HEIGHT = 90;
	private static final int IMAGE_BORDER_MARGIN = 10;
	private static final int LABELS_SPACING = 0;
	private static final int TABLE_HORIZONTAL_MARGIN = 5;

	private static final int TEXT_HEIGHT = 15;

	private ImageGalleryElementsBuilder<T, I> elementsBuilder = new DefaultImageGalleryElementsBuilder<T, I>();
	private ScrolledComposite mainComposite;
	private Composite imagesTable;
	private ImageLabel highlitedLabel;
	private ImageLabel selectedLabel;
	private Image defaultImage;
	private int thumbnailWidth;
	private int thumbnailHeight;

	private Map<T, ImageLabel> labels;
	private final BackgroundImages backgroundImages;
	private final Set<MouseListener> elementMouseListeners;
	private final Set<MouseMoveListener> elementMouseMoveListeners;

	private GridLayout imagesTableLayout;

	public ImageGallery(final Composite parent) {
		this(parent, DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}

	public ImageGallery(final Composite parent, final int thumbnailWidth, final int thumbnailHeight) {
		this.thumbnailWidth = thumbnailWidth;
		this.thumbnailHeight = thumbnailHeight;
		this.labels = new HashMap<T, ImageLabel>();
		backgroundImages = new BackgroundImages(this);
		elementMouseListeners = new HashSet<MouseListener>();
		elementMouseMoveListeners = new HashSet<MouseMoveListener>();
		initUI(parent);
	}

	public void addElementMouseListener(final MouseListener listener) {
		elementMouseListeners.add(listener);
	}

	public void addElementMouseMoveListener(final MouseMoveListener listener) {
		elementMouseMoveListeners.add(listener);
	}

	public void addImage(final T imageMeta, final I image) {
		if (labels.containsKey(imageMeta)) {
			labels.get(imageMeta).setImage(image);
			return;
		}
		final ImageLabel label = new ImageLabel(imagesTable);
		label.setImageMeta(imageMeta);
		label.setImage(image);
		labels.put(imageMeta, label);
		imagesTable.layout();
		mainComposite.layout();
	}

	public boolean containsImage(final T imageMeta) {
		return labels.containsKey(imageMeta);
	}

	@Override
	public void dispose() {
		backgroundImages.dispose();
		imagesTable.dispose();
		removeAll();
		mainComposite.dispose();
		if (defaultImage != null) {
			defaultImage.dispose();
		}
		for (final ImageLabel label : labels.values()) {
			label.dispose();
		}
	}

	public Image getDefaultImage() {
		return defaultImage;
	}

	public Composite getImagesTable() {
		return imagesTable;
	}

	public ImageGalleryElementsBuilder<T, I> getLabelBuilder() {
		return elementsBuilder;
	}

	public ScrolledComposite getMainComposite() {
		return mainComposite;
	}

	public T getSelectedImage() {
		if (selectedLabel == null) {
			return null;
		}
		return selectedLabel.getImageMeta();
	}

	public int getThumbnailHeight() {
		return thumbnailHeight;
	}

	public int getThumbnailWidth() {
		return thumbnailWidth;
	}

	private void initUI(final Composite parent) {
		mainComposite = new ScrolledComposite(parent, SWT.V_SCROLL) {

			@Override
			public void layout(final boolean a) {
				super.layout(a);
				final Control[] children = imagesTable.getChildren();
				final int columns = ((GridLayout) imagesTable.getLayout()).numColumns;
				final int rows = children.length / columns + (children.length % columns != 0 ? 1 : 0);
				int minHeight = 0;
				for (int row = 0; row < rows; row++) {
					int max = 0;
					for (int col = 0; col < columns; col++) {
						final int index = row * columns + col;
						if (index >= children.length) {
							break;
						}
						max = Math.max(children[index].getSize().y, max);
					}
					minHeight += max + LABELS_SPACING;
				}
				mainComposite.setMinHeight(minHeight + 10);
			}
		};
		imagesTable = new Composite(mainComposite, SWT.NONE);
		imagesTableLayout = new GridLayout(4, false);
		imagesTableLayout.horizontalSpacing = LABELS_SPACING;
		imagesTableLayout.verticalSpacing = LABELS_SPACING;
		imagesTableLayout.marginLeft = TABLE_HORIZONTAL_MARGIN;
		imagesTableLayout.marginRight = TABLE_HORIZONTAL_MARGIN;
		imagesTable.setLayout(imagesTableLayout);
		imagesTable.setBackground(imagesTable.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		imagesTable.addMouseMoveListener(new MouseMoveListener() {

			@Override
			public void mouseMove(final MouseEvent event) {
				if (highlitedLabel != null) {
					highlitedLabel.setHighlight(false);
				}
				highlitedLabel = null;
			}
		});
		final Listener resizeListener = new Listener() {

			@Override
			public void handleEvent(final Event arg) {

				resizeLayout(imagesTableLayout);
				imagesTable.layout();
				mainComposite.layout();
			}
		};
		mainComposite.addListener(SWT.Resize, resizeListener);
		imagesTable.addListener(SWT.Resize, resizeListener);

		mainComposite.setContent(imagesTable);
		mainComposite.setMinWidth(thumbnailWidth + IMAGE_BORDER_MARGIN);

		mainComposite.setExpandHorizontal(true);
		mainComposite.setExpandVertical(true);
	}

	public void layout() {
		mainComposite.layout();
	}

	public void redrawGallery() {
		for (final ImageLabel label : labels.values()) {
			label.redraw();

		}
		resizeLayout(imagesTableLayout);
		imagesTable.layout();
		mainComposite.layout();
	}

	public void removeAll() {
		highlitedLabel = null;
		selectedLabel = null;
		for (final ImageLabel label : labels.values()) {
			label.dispose();
		}
		labels.clear();
	}

	public void removeElementMouseListener(final MouseListener listener) {
		elementMouseListeners.remove(listener);
	}

	public void removeElementMouseMoveListener(final MouseMoveListener listener) {
		elementMouseMoveListeners.remove(listener);
	}

	public void removeImage(final T imageMeta) {
		if (labels.containsKey(imageMeta)) {
			final ImageLabel label = labels.get(imageMeta);
			if (highlitedLabel == label) {
				highlitedLabel = null;
			}
			if (selectedLabel == label) {
				selectedLabel = null;
			}
			label.dispose();
			labels.remove(imageMeta);
		}
	}

	private void resizeLayout(final GridLayout layout) {
		final Point size = imagesTable.getSize();
		final int width = size.x;
		int mod = (width - TABLE_HORIZONTAL_MARGIN * 2) % (thumbnailWidth + IMAGE_BORDER_MARGIN + LABELS_SPACING);
		int count = (width - TABLE_HORIZONTAL_MARGIN * 2) / (thumbnailWidth + IMAGE_BORDER_MARGIN + LABELS_SPACING)
				+ (mod >= thumbnailWidth + IMAGE_BORDER_MARGIN + (LABELS_SPACING / 2) ? 1 : 0);
		mod = (width - count * (thumbnailWidth + IMAGE_BORDER_MARGIN) + TABLE_HORIZONTAL_MARGIN * 2);
		count = count > 0 ? count : 1;
		layout.numColumns = count;
		layout.horizontalSpacing = count > 1 ? mod / count - 3 : LABELS_SPACING;
	}

	public void selectImage(final T imageMeta, final boolean makeSelectedVisible) {
		if (labels.containsKey(imageMeta)) {
			final ImageLabel oldSelected = selectedLabel;
			selectedLabel = labels.get(imageMeta);
			if (selectedLabel != oldSelected) {
				selectedLabel.setSelected(true);
				if (oldSelected != null) {
					if (highlitedLabel == oldSelected) {
						oldSelected.setHighlight(true);
					} else {
						oldSelected.setSelected(false);
					}
				}
				if (makeSelectedVisible) {
					mainComposite.setOrigin(0, selectedLabel.getComposite().getBounds().y);
				}
			}
		}
	}

	public void setDefaultImage(final Image defaultImage) {
		if (this.defaultImage != null) {
			this.defaultImage.dispose();
		}
		this.defaultImage = defaultImage;
	}

	public void setLabelBuilder(final ImageGalleryElementsBuilder<T, I> labelBuilder) {
		this.elementsBuilder = labelBuilder;
	}

	public void setThumbnailHeight(final int thumbnailHeight) {
		this.thumbnailHeight = thumbnailHeight;
		mainComposite.layout();
	}

	public void setThumbnailSize(final int thumbnailWidth, final int thumbnailHeight) {
		this.thumbnailWidth = thumbnailWidth;
		this.thumbnailHeight = thumbnailHeight;

	}

	public void setThumbnailWidth(final int thumbnailWidth) {
		this.thumbnailWidth = thumbnailWidth;
		mainComposite.layout();
	}

	public int size() {
		return labels.size();
	}
}
