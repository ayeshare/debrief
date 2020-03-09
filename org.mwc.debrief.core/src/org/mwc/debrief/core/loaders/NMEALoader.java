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

package org.mwc.debrief.core.loaders;

import java.io.InputStream;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.mwc.cmap.core.wizards.ImportNMEADialog;
import org.mwc.debrief.core.DebriefPlugin;

import Debrief.ReaderWriter.NMEA.ImportNMEA;
import MWC.GUI.Layers;

/**
 */
public class NMEALoader extends CoreLoader {

	public NMEALoader() {
		super("NMEA", null);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.mwc.debrief.core.interfaces.IPlotLoader#loadFile(org.mwc.cmap.plotViewer
	 * .editors.CorePlotEditor, org.eclipse.ui.IEditorInput)
	 */
	@Override
	protected IRunnableWithProgress getImporter(final IAdaptable target, final Layers layers,
			final InputStream inputStream, final String fileName) {
		// ok, we'll need somewhere to put the data
		final Layers theLayers = target.getAdapter(Layers.class);

		return new IRunnableWithProgress() {
			@Override
			public void run(final IProgressMonitor pm) {
				// create way of passing reference back from dialog
				final AtomicReference<ImportNMEADialog> dialogO = new AtomicReference<ImportNMEADialog>();
				Display.getDefault().syncExec(new Runnable() {
					@Override
					public void run() {
						final ImportNMEADialog dialog = new ImportNMEADialog();
						if (dialog.open() != Window.CANCEL) {
							dialogO.set(dialog);
						}
					}
				});
				try {
					// did user press finish?
					if (dialogO.get() != null) {
						final ImportNMEADialog dialog = dialogO.get();
						// get the selected values
						final long osFreq = dialog.getOwnshipFreq();
						final long tgtFreq = dialog.getThirdPartyFreq();
						final boolean splitOwnshipJumps = dialog.getSplitOwnshipJumps();

						// ok - get loading going
						final ImportNMEA importer = new ImportNMEA(theLayers);
						importer.importThis(fileName, inputStream, osFreq, tgtFreq, splitOwnshipJumps);
					} else {
						DebriefPlugin.logError(IStatus.INFO, "User cancelled loading:" + fileName, null);
					}
				} catch (final Exception e) {
					DebriefPlugin.logError(IStatus.ERROR, "Problem loading AIS datafile:" + fileName, e);
				}
			}
		};
	}
}
