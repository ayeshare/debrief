
package org.mwc.cmap.NarrativeViewer.app;

import org.eclipse.core.runtime.IPlatformRunnable;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * This class controls all aspects of the application's execution
 */
@SuppressWarnings("deprecation")
public class NarrativeViewerApplication implements IPlatformRunnable {

	public Object run(final Object args) throws Exception {
		final Display display = PlatformUI.createDisplay();
		try {
			final int returnCode = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
			if (returnCode == PlatformUI.RETURN_RESTART) {
				return IPlatformRunnable.EXIT_RESTART;
			}
			return IPlatformRunnable.EXIT_OK;
		} finally {
			display.dispose();
		}
	}
}
