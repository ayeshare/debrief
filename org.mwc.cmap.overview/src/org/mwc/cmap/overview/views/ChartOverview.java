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

package org.mwc.cmap.overview.views;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tracker;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.interfaces.IControllableViewport;
import org.mwc.cmap.core.operations.DebriefActionWrapper;
import org.mwc.cmap.core.ui_support.PartMonitor;
import org.mwc.cmap.core.ui_support.swt.SWTCanvasAdapter;
import org.mwc.cmap.geotools.gt2plot.GtProjection;
import org.mwc.cmap.plotViewer.editors.chart.SWTCanvas;
import org.mwc.cmap.plotViewer.editors.chart.SWTChart;

import MWC.Algorithms.PlainProjection;
import MWC.GUI.BaseLayer;
import MWC.GUI.CanvasType;
import MWC.GUI.CanvasType.PaintListener;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.PlainChart;
import MWC.GUI.Tools.Action;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */

public class ChartOverview extends ViewPart implements PropertyChangeListener {

	public class MyZoomMode extends SWTChart.PlotMouseDragger {
		org.eclipse.swt.graphics.Point _startPoint;

		SWTCanvas _myCanvas;

		@Override
		public void doMouseDrag(final org.eclipse.swt.graphics.Point pt, final int JITTER, final Layers theLayers,
				final SWTCanvas theCanvas) {
			// just do a check that we have our start point (it may have been
			// cleared
			// at the end of the move operation)
			if (_startPoint != null) {
				final int deltaX = _startPoint.x - pt.x;
				final int deltaY = _startPoint.y - pt.y;
				if (Math.abs(deltaX) < JITTER && Math.abs(deltaY) < JITTER)
					return;
				Tracker _dragTracker = new Tracker((Composite) _myCanvas.getCanvas(), SWT.RESIZE);
				final Rectangle rect = new Rectangle(_startPoint.x, _startPoint.y, deltaX, deltaY);
				_dragTracker.setRectangles(new Rectangle[] { rect });
				final boolean dragResult = _dragTracker.open();
				if (dragResult) {
					final Rectangle[] rects = _dragTracker.getRectangles();
					final Rectangle res = rects[0];
					// get world area
					final java.awt.Point tl = new java.awt.Point(res.x, res.y);
					final java.awt.Point br = new java.awt.Point(res.x + res.width, res.y + res.height);
					final WorldLocation locA = new WorldLocation(_myCanvas.getProjection().toWorld(tl));
					final WorldLocation locB = new WorldLocation(_myCanvas.getProjection().toWorld(br));
					final WorldArea area = new WorldArea(locA, locB);

					// hmm, check we have a controllable viewport
					if (_targetViewport != null) {

						try {

							// ok, we also need to get hold of the target chart
							final WorldArea oldArea = _targetViewport.getViewport();
							final Action theAction = new OverviewZoomInAction(_targetViewport, oldArea, area);

							// and wrap it
							final DebriefActionWrapper daw = new DebriefActionWrapper(theAction, null, null);

							// and add it to the clipboard
							CorePlugin.run(daw);
						} catch (final RuntimeException re) {
							re.printStackTrace();
						}
					}

					_dragTracker = null;
					_startPoint = null;
				}
			}
		}

		@Override
		public void doMouseUp(final org.eclipse.swt.graphics.Point point, final int keyState) {
			_startPoint = null;
		}

		@Override
		public void mouseDown(final org.eclipse.swt.graphics.Point point, final SWTCanvas canvas,
				final PlainChart theChart) {
			_startPoint = point;
			_myCanvas = canvas;
		}

	}

	protected class OverviewSWTCanvas extends SWTCanvas {
		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

		public OverviewSWTCanvas(final Composite parent) {
			super(parent, _myProjection);
		}

		@Override
		public void drawText(final Font theFont, final String theStr, final int x, final int y) {
			// ignore - we don't do text in overview
		}

		@Override
		public void drawText(final String theStr, final int x, final int y) {
			// ignore - we don't do text in overview
		}

	}

	protected static class OverviewSWTCanvasAdapter extends SWTCanvasAdapter {

		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

		public OverviewSWTCanvasAdapter(final PlainProjection proj) {
			super(proj);
		}

		@Override
		public void drawText(final Font theFont, final String theStr, final int x, final int y) {
			// ignore - we don't do text in overview
		}

		@Override
		public void drawText(final String theStr, final int x, final int y) {
			// ignore - we don't do text in overview
		}
	}

	abstract public class OverviewSWTChart extends SWTChart {

		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

		ChartOverview _parentView;

		public OverviewSWTChart(final Composite parent) {
			super(null, parent, _myProjection);

			// ok, setup double-click handler to zoom in on target location
			this.addCursorDblClickedListener(new ChartDoubleClickListener() {
				@Override
				public void cursorDblClicked(final PlainChart theChart, final WorldLocation theLocation,
						final Point thePoint) {
					// ok - got location centre plot on target loc
					final WorldArea currentArea = new WorldArea(getParentViewport().getViewport());

					currentArea.setCentre(theLocation);

					getParentViewport().setViewport(currentArea);

					// and trigger an update
					getParentViewport().update();
				}
			});
		}

		@Override
		public void chartFireSelectionChanged(final ISelection sel) {
		}

		/**
		 * over-rideable member function which allows us to over-ride the canvas which
		 * gets used.
		 *
		 * @return the Canvas to use
		 */
		public final SWTCanvas createCanvas(final Composite parent) {
			return new OverviewSWTCanvas(parent);
		}

		abstract IControllableViewport getParentViewport();

		/**
		 * over-ride the parent's version of paint, so that we can try to do it by
		 * layers.
		 */
		@Override
		public final void paintMe(final CanvasType dest) {

			// just double-check we have some layers (if we're part of an
			// overview
			// chart, we may not have...)
			if (_theLayers == null)
				return;

			// check that we have a valid canvas (that the sizes are set)
			final java.awt.Dimension sArea = dest.getProjection().getScreenArea();
			if (sArea != null) {
				if (sArea.width > 0) {

					// hey, we've plotted at least once, has the data area
					// changed?
					if (_lastDataArea != _parentView._myOverviewChart.getCanvas().getProjection().getDataArea()) {
						// remember the data area for next time
						_lastDataArea = _parentView._myOverviewChart.getCanvas().getProjection().getDataArea();

						// clear out all of the layers we are using
						_myLayers.clear();
					}

					final int canvasHeight = _parentView._myOverviewChart.getCanvas().getSize().getSize().height;
					final int canvasWidth = _parentView._myOverviewChart.getCanvas().getSize().width;

					paintBackground(dest);

					// ok, pass through the layers, repainting any which need it
					final Enumeration<Layer> numer = _theLayers.sortedElements();
					while (numer.hasMoreElements()) {
						final Layer thisLayer = numer.nextElement();

						boolean isAlreadyPlotted = false;

						// hmm, do we want to paint this layer?
						if (_parentView.doWePaintThisLayer(thisLayer)) {

							// just check if this layer is visible
							if (thisLayer.getVisible()) {
								// System.out.println("painting:" +
								// thisLayer.getName());

								if (doubleBufferPlot()) {
									// check we're plotting to a SwingCanvas,
									// because we don't
									// double-buffer anything else
									if (dest instanceof SWTCanvas) {
										// does this layer want to be
										// double-buffered?
										if (thisLayer instanceof BaseLayer) {
											// just check if there is a property
											// which over-rides the
											// double-buffering
											final BaseLayer bl = (BaseLayer) thisLayer;
											if (bl.isBuffered()) {
												isAlreadyPlotted = true;

												// do our double-buffering bit
												// do we have a layer for this
												// object
												org.eclipse.swt.graphics.Image image = _myLayers.get(thisLayer);
												if (image == null) {
													// ok - create our image
													if (_myImageTemplate == null) {

														Image tmpTemplate = new Image(Display.getCurrent(), canvasWidth,
																canvasHeight);
														_myImageTemplate = tmpTemplate.getImageData();

														tmpTemplate.dispose();
														tmpTemplate = null;
													}
													image = createSWTImage(_myImageTemplate);

													final GC newGC = new GC(image);

													// wrap the GC into
													// something we know how to
													// plot to.
													final SWTCanvasAdapter ca = new OverviewSWTCanvasAdapter(
															dest.getProjection());

													ca.setScreenSize(dest.getProjection().getScreenArea());

													// and store the GC
													ca.startDraw(newGC);

													// ok, paint the layer into
													// this canvas
													thisLayer.paint(ca);

													// done.
													ca.endDraw(null);

													// store this image in our
													// list, indexed by the
													// layer
													// object itself
													_myLayers.put(thisLayer, image);

													// and ditch the GC
													newGC.dispose();
												}

												// have we ended up with an
												// image to paint?
												if (image != null) {
													// get the graphics to paint
													// to
													final SWTCanvas canv = (SWTCanvas) dest;

													// lastly add this image to
													// our Graphics object
													canv.drawSWTImage(image, 0, 0, canvasWidth, canvasHeight, 255);

													// but, we also have to
													// ditch the image
													// image.dispose();
												}

											}
										}
									} // whether we were plotting to a
										// SwingCanvas (which may be
										// double-buffered
								} // whther we are happy to do double-buffering

								// did we manage to paint it
								if (!isAlreadyPlotted) {
									paintThisLayer(thisLayer, dest);

									isAlreadyPlotted = true;
								}
							}
						}
					}

				}
			}

		}

		public void setChartOverview(final ChartOverview view) {
			_parentView = view;
		}

	}

	public class OverviewZoomInAction implements Action {

		private final IControllableViewport _theViewport;

		private final WorldArea _oldArea;

		private final WorldArea _newArea;

		public OverviewZoomInAction(final IControllableViewport theChart, final WorldArea oldArea,
				final WorldArea newArea) {
			_theViewport = theChart;
			_oldArea = oldArea;
			_newArea = newArea;
		}

		@Override
		public void execute() {
			_theViewport.setViewport(_newArea);

			_theViewport.update();

			_myOverviewChart.update();
		}

		@Override
		public boolean isRedoable() {
			return true;
		}

		@Override
		public boolean isUndoable() {
			return true;
		}

		@Override
		public String toString() {
			return "Zoom in operation";
		}

		@Override
		public void undo() {
			// set the data area for the chart to the specified area
			_theViewport.setViewport(_oldArea);

			_theViewport.update();

			_myOverviewChart.update();
		}
	}

	/**
	 * helper application to help track creation/activation of new plots
	 */
	private PartMonitor _myPartMonitor;

	protected Layers _targetLayers;

	OverviewSWTChart _myOverviewChart;

	IControllableViewport _targetViewport;

	private org.eclipse.jface.action.Action _fitToWindow;

	private final GtProjection _myProjection;

	/**
	 * The constructor.
	 */
	public ChartOverview() {
		_myProjection = new GtProjection();
	}

	/**
	 * disable the plot we-re no longer looking at anything...
	 */
	protected void clearPlot() {
		// ok - we're no longer looking at anything. clear the plot..
	}

	private void contributeToActionBars() {
		final IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize it.
	 */
	@Override
	public void createPartControl(final Composite parent) {

		// declare our context sensitive help
		CorePlugin.declareContextHelp(parent, "org.mwc.debrief.help.OverviewChart");

		// hey, first create the chart
		_myOverviewChart = new OverviewSWTChart(parent) {

			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void canvasResized() {
				// just check we have a plot
				if (_targetLayers != null) {
					super.canvasResized();
				}
			}

			@Override
			IControllableViewport getParentViewport() {
				return _targetViewport;
			}

		};

		// use our special dragger
		_myOverviewChart.setDragMode(new MyZoomMode());

		// and update the chart
		_myOverviewChart.setChartOverview(this);

		// and our special painter
		_myOverviewChart.getCanvas().addPainter(new PaintListener() {
			@Override
			public WorldArea getDataArea() {
				return null;
			}

			@Override
			public String getName() {
				return "Overview data area";
			}

			@Override
			public void paintMe(final CanvasType dest) {
				// ok - just paint in our rectangle
				paintDataRect(dest);
			}

			@Override
			public void resizedEvent(final PlainProjection theProj, final Dimension newScreenArea) {
			}
		});

		makeActions();
		contributeToActionBars();

		// /////////////////////////////////////////
		// ok - listen out for changes in the view
		// /////////////////////////////////////////
		watchMyParts();
	}

	@Override
	public void dispose() {
		// don't forget to stop listening for layer changes
		_myOverviewChart.setLayers(null);

		// and stop listening for projection changes
		stopListeningToViewport();

		if (_myPartMonitor != null) {
			_myPartMonitor.ditch();
		}
		super.dispose();
	}

	/**
	 * decide whether to paint this layer...
	 *
	 * @param thisLayer the layer we're looking at
	 * @return
	 */
	public boolean doWePaintThisLayer(final Layer thisLayer) {
		final boolean res = !(thisLayer instanceof Layer.NoPaintInOverview);

		// check for the special marker interface that prevents a layer being painted

		// no, don't check for ETOPO data - just paint the lot.
		// if (thisLayer instanceof SpatialRasterPainter)
		// res = false;

		return res;
	}

	private void fillLocalPullDown(final IMenuManager manager) {
	}

	private void fillLocalToolBar(final IToolBarManager manager) {
		manager.add(_fitToWindow);

		// and the help link
		manager.add(new Separator());
		manager.add(CorePlugin.createOpenHelpAction("org.mwc.debrief.help.OverviewChart", null, this));
	}

	/**
	 * do a fit-to-window of the target viewport
	 */
	protected void fitTargetToWindow() {
		_targetViewport.rescale();
		_targetViewport.update();

		// now, redraw our rectable
		_myOverviewChart.repaint();
	}

	public MWC.GUI.Rubberband getRubberband() {
		return null;
	}

	private void makeActions() {
		_fitToWindow = new org.eclipse.jface.action.Action() {
			@Override
			public void run() {
				// ok, fit the plot to the window...
				fitTargetToWindow();
			}
		};
		_fitToWindow.setText("Fit to window");
		_fitToWindow.setToolTipText("Zoom the selected plot out to show the full data");
		_fitToWindow.setImageDescriptor(CorePlugin.getImageDescriptor("icons/16/fit_to_win.png"));

	}

	/**
	 * paint the data-rectangle in our overview, to show the currently visible area
	 *
	 * @param dest
	 */
	protected void paintDataRect(final CanvasType dest) {
		// check we're alive
		if (_targetViewport == null)
			return;

		// get the projection
		final PlainProjection proj = _targetViewport.getProjection();

		// get the dimensions
		final java.awt.Dimension scrArea = proj.getScreenArea();

		// did we find any data?
		if (scrArea == null)
			return;

		// now convert to data coordinates
		WorldLocation loc = proj.toWorld(new Point(0, 0));

		// did it work?
		if (loc == null) {
			// ok - didn't work. maybe the plot is collapsed
			return;
		}

		// produce the screen coordinate in the overview
		final Point thePt = _myOverviewChart.getCanvas().getProjection().toScreen(loc);

		// did it work?
		if (thePt == null)
			return;

		// and the other corner
		loc = proj.toWorld(new Point(scrArea.width, scrArea.height));

		// create the screen coordinates
		final Point tl = new Point(thePt);
		final Point br = new Point(_myOverviewChart.getCanvas().getProjection().toScreen(loc));

		//
		// // also, draw in the data-area
		// WorldArea dataRect = _currentViewport.getViewport();
		// // convert to my coords
		// java.awt.Point tl = new
		// java.awt.Point(dest.getProjection().toScreen(dataRect.getTopLeft()));
		// java.awt.Point br = new
		// java.awt.Point(dest.getProjection().toScreen(dataRect.getBottomRight()));

		dest.setColor(new java.awt.Color(200, 200, 200));
		dest.drawRect(tl.x, tl.y, br.x - tl.x, br.y - tl.y);

	}

	/**
	 * ok, a new plot is selected - better show it then
	 *
	 * @param provider   the new plot
	 * @param parentPart the part containing the plot
	 */
	protected void plotSelected(final Layers provider, final IWorkbenchPart parentPart) {
		// ok - update our chart to show the indicated plot.
		_myOverviewChart.setLayers(provider);
		if (provider != null) {
			_myOverviewChart.rescale();
		}
		_myOverviewChart.repaint();
		// this.setPartName(parentPart.getTitle());
	}

	@Override
	public void propertyChange(final PropertyChangeEvent evt) {
		// ok, we've had a range change. better update
		_myOverviewChart.repaint();
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {

	}

	/**
	 *
	 */
	void startListeningToViewport() {
		_targetViewport.getProjection().addListener(this);
	}

	/**
	 *
	 */
	void stopListeningToViewport() {
		if (_targetViewport != null)
			if (_targetViewport.getProjection() != null) {
				_targetViewport.getProjection().removeListener(this);
			}
	}

	/**
	 * sort out what we're listening to...
	 */
	private void watchMyParts() {
		_myPartMonitor = new PartMonitor(getSite().getWorkbenchWindow().getPartService());
		_myPartMonitor.addPartListener(Layers.class, PartMonitor.ACTIVATED, new PartMonitor.ICallback() {
			@Override
			public void eventTriggered(final String type, final Object part, final IWorkbenchPart parentPart) {
				final Layers provider = (Layers) part;

				// is this different to our current one?
				if (provider != _targetLayers) {
					// ok, start listening to the new one
					_targetLayers = provider;
					plotSelected(provider, parentPart);
				}
			}
		});
		_myPartMonitor.addPartListener(Layers.class, PartMonitor.CLOSED, new PartMonitor.ICallback() {
			@Override
			public void eventTriggered(final String type, final Object part, final IWorkbenchPart parentPart) {
				if (part == _targetLayers) {
					// cancel the listeners
					plotSelected(null, null);

					_targetLayers = null;
					clearPlot();
				}
			}
		});
		_myPartMonitor.addPartListener(IControllableViewport.class, PartMonitor.ACTIVATED, new PartMonitor.ICallback() {
			@Override
			public void eventTriggered(final String type, final Object part, final IWorkbenchPart parentPart) {
				final IControllableViewport provider = (IControllableViewport) part;

				// is this different to our current one?
				if (provider != _targetViewport) {
					// ok, stop listening to the current viewport (if we
					// have one)
					if (_targetViewport != null)
						stopListeningToViewport();

					// and start listening to the new one
					_targetViewport = provider;

					startListeningToViewport();

				}
			}
		});
		_myPartMonitor.addPartListener(IControllableViewport.class, PartMonitor.CLOSED, new PartMonitor.ICallback() {
			@Override
			public void eventTriggered(final String type, final Object part, final IWorkbenchPart parentPart) {
				if (part == _targetViewport) {
					if (_targetViewport != null)
						stopListeningToViewport();

					_targetViewport = null;
				}
			}
		});

		// ok we're all ready now. just try and see if the current part is valid
		_myPartMonitor.fireActivePart(getSite().getWorkbenchWindow().getActivePage());
	}
}
