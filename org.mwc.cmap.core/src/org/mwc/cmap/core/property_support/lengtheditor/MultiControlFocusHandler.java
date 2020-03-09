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

package org.mwc.cmap.core.property_support.lengtheditor;

import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

public abstract class MultiControlFocusHandler implements FocusListener {

	private static abstract class CancellableRunnable implements Runnable {

		public boolean myIsCancelled;

		public void cancel() {
			myIsCancelled = true;
		}

		public abstract void doRun();

		@Override
		public final void run() {
			if (!myIsCancelled) {
				doRun();
			}
		}
	}

	private CancellableRunnable myDeferredDeactivate;

	public MultiControlFocusHandler(final Control... parts) {
		for (final Control next : parts) {
			next.addFocusListener(this);
		}
	}

	@Override
	public void focusGained(final FocusEvent e) {
		if (myDeferredDeactivate != null) {
			myDeferredDeactivate.cancel();
		}
		myDeferredDeactivate = null;
	}

	@Override
	public void focusLost(final FocusEvent e) {
		myDeferredDeactivate = new CancellableRunnable() {

			@Override
			public void doRun() {
				focusReallyLost(e);
			}
		};
		Display.getCurrent().asyncExec(myDeferredDeactivate);
	}

	/**
	 * In contrast to {@link FocusListener#focusLost(FocusEvent)}, this method is
	 * called only if the focus is NOT transferred between parts of multi-control
	 * figure specified on construction time.
	 */
	protected abstract void focusReallyLost(FocusEvent e);

}