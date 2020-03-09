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

package org.mwc.debrief.satc_interface;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.progress.WorkbenchJob;
import org.mwc.cmap.core.property_support.EditableWrapper;
import org.mwc.cmap.core.ui_support.CoreViewLabelProvider;
import org.mwc.cmap.core.ui_support.PartMonitor;
import org.mwc.debrief.satc_interface.data.SATC_Solution;
import org.osgi.framework.BundleContext;

import com.planetmayo.debrief.satc.model.generator.ISolver;
import com.planetmayo.debrief.satc.model.manager.ISolversManager;
import com.planetmayo.debrief.satc.util.DopplerCalculator;
import com.planetmayo.debrief.satc_rcp.SATC_Activator;

import MWC.Algorithms.FrequencyCalcs;
import MWC.GUI.Editable;
import MWC.GUI.Layers;

/**
 * The activator class controls the plug-in life cycle
 */
public class SATC_Interface_Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.mwc.debrief.satc.integration"; //$NON-NLS-1$

	// The shared instance
	private static SATC_Interface_Activator plugin;

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static SATC_Interface_Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in relative
	 * path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(final String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	private PartMonitor _myPartMonitor;

	private transient Layers _currentLayers = null;

	private final ISelectionChangedListener _selectionChangeListener;

	/**
	 * The constructor
	 */
	public SATC_Interface_Activator() {
		_selectionChangeListener = new ISelectionChangedListener() {
			@Override
			public void selectionChanged(final SelectionChangedEvent event) {
				// right, see what it is
				final ISelection sel = event.getSelection();
				if (sel instanceof StructuredSelection) {
					final StructuredSelection ss = (StructuredSelection) sel;
					final Object str = ss.getFirstElement();
					if (str instanceof EditableWrapper) {
						final EditableWrapper ew = (EditableWrapper) str;
						final Editable datum = ew.getEditable();
						if (datum instanceof SATC_Solution) {
							final SATC_Solution pw = (SATC_Solution) datum;
							editableSelected(ss, pw);
						}
					}
				}
			}
		};
	}

	/**
	 * tell the manager to ditch the current solver
	 *
	 */
	private void clearSolver() {
		if (_currentLayers != null) {
			SATC_Activator.getDefault().getService(ISolversManager.class, true).setActiveSolver(null);
		}
	}

	protected void editableSelected(final StructuredSelection sel, final SATC_Solution pw) {
		// ok - it's been selected - tell the SATC manager
		final ISolversManager solver = SATC_Activator.getDefault().getService(ISolversManager.class, true);
		final ISolver newSolver = pw.getSolver();

		// aah, just double-check that it's not already the solver
		if (solver.getActiveSolver() != newSolver) {
			solver.setActiveSolver(newSolver);
		}
	}

	private void initPartMonitor() {
		if (_myPartMonitor == null) {
			// register ourselves as a part monitor (We need to know about selections,
			// to tie ourselves into the SATC code
			_myPartMonitor = new PartMonitor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService());

			// also register as a selection listener
			_myPartMonitor.addPartListener(ISelectionProvider.class, PartMonitor.ACTIVATED,
					new PartMonitor.ICallback() {
						@Override
						public void eventTriggered(final String type, final Object part,
								final IWorkbenchPart parentPart) {
							final ISelectionProvider iS = (ISelectionProvider) part;
							iS.addSelectionChangedListener(_selectionChangeListener);
						}
					});
			_myPartMonitor.addPartListener(ISelectionProvider.class, PartMonitor.DEACTIVATED,
					new PartMonitor.ICallback() {
						@Override
						public void eventTriggered(final String type, final Object part,
								final IWorkbenchPart parentPart) {
							final ISelectionProvider iS = (ISelectionProvider) part;
							iS.removeSelectionChangedListener(_selectionChangeListener);
						}
					});

			// /////////////////
			// we also listen for the layers object changing. we clear the
			// contributions when that happens
			// /////////////////
			_myPartMonitor.addPartListener(Layers.class, PartMonitor.ACTIVATED, new PartMonitor.ICallback() {
				@Override
				public void eventTriggered(final String type, final Object part, final IWorkbenchPart parentPart) {
					if (part != _currentLayers) {
						clearSolver();
					}
					_currentLayers = (Layers) part;
				}
			});
			_myPartMonitor.addPartListener(Layers.class, PartMonitor.CLOSED, new PartMonitor.ICallback() {
				@Override
				public void eventTriggered(final String type, final Object part, final IWorkbenchPart parentPart) {
					if (part == _currentLayers) {
						clearSolver();
					}
					_currentLayers = null;
				}
			});
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

		final WorkbenchJob job = new WorkbenchJob("Just a UI Job") {

			@Override
			public IStatus runInUIThread(final IProgressMonitor monitor) {
				initPartMonitor();
				return Status.OK_STATUS;
			}
		};
		job.schedule();

		// register our image helper
		CoreViewLabelProvider.addImageHelper(new SATC_ImageHelper());

		// register our doppler calculator
		SATC_Activator.getDefault().setDopplerCalculator(new DopplerCalculator() {

			@Override
			public double calcPredictedFreq(final double SpeedOfSound, final double osHeadingRads,
					final double tgtHeadingRads, final double osSpeed, final double tgtSpeed, final double bearing,
					final double fNought) {
				return FrequencyCalcs.calcPredictedFreqSI(SpeedOfSound, osHeadingRads, tgtHeadingRads, osSpeed,
						tgtSpeed, bearing, fNought);
			}
		});

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void stop(final BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);

		// do some tidying
		_currentLayers = null;
		_myPartMonitor = null;
	}
}
