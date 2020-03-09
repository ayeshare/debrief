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

package org.mwc.debrief.multipath2;

import java.io.IOException;
import java.text.ParseException;

import org.eclipse.core.runtime.IStatus;
import org.jfree.data.time.TimeSeries;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.debrief.multipath2.MultiPathPresenter.Display.FileHandler;
import org.mwc.debrief.multipath2.model.MultiPathModel;
import org.mwc.debrief.multipath2.model.MultiPathModel.DataFormatException;
import org.mwc.debrief.multipath2.model.RangeValues;

import flanagan.math.MinimisationFunction;

public class MultiPathPresenterTest extends MultiPathPresenter {
	private static final int OWNSHIP_DEPTH = 30;
	private RangeValues _ranges = null;
	protected String _rangePath;

	/**
	 * initialise presenter
	 *
	 * @param display
	 * @param model
	 */
	public MultiPathPresenterTest(final Display display) {
		super(display);

	}

	@Override
	public void bind() {
		// do the parent bits
		super.bind();

		// now listen out for ranges being dropped on the slider
		_display.addRangesListener(new FileHandler() {
			@Override
			public void newFile(final String path) {
				loadRanges(path);

				checkEnablement();
			}
		});
	}

	@Override
	protected void checkEnablement() {
		// do the parent bit first
		super.checkEnablement();

		// check for the range stuff
		if (_rangePath == null) {
			// disable it
			_display.setEnabled(false);

			// show error
			_display.setSliderText("Waiting for ranges");
		} else {
			// just check the init is complete
			if ((_svp != null) && (_times != null)) {

				// enable it
				_display.setEnabled(true);

				// initialise the slider
				_display.setSliderVal(_curDepth);

				// and give it a sensible start value
				_dragHandler.newValue(_curDepth);
			}

		}

	}

	@Override
	protected MinimisationFunction createMiracle() {
		return new MultiPathModel.RangedMiracleFunction(_ranges, _svp, _times, OWNSHIP_DEPTH);
	}

	@Override
	protected TimeSeries getCalculatedProfile(final int val) {
		TimeSeries calculated = null;

		if (_ranges != null)
			calculated = _model.getCalculatedProfileFor(_ranges, _svp, _times, val, OWNSHIP_DEPTH);

		return calculated;
	}

	protected void loadRanges(final String path) {
		try {
			_rangePath = path;
			_ranges = new RangeValues();

			_ranges.load(path);
		} catch (final ParseException e) {
			CorePlugin.logError(IStatus.ERROR, "ranges file number formatting problem", e);
			_ranges = null;
		} catch (final IOException e) {
			CorePlugin.logError(IStatus.ERROR, "ranges file loading problem", e);
			_ranges = null;
		} catch (final DataFormatException e) {
			CorePlugin.logError(IStatus.ERROR, "ranges file data formatting problem", e);
			_ranges = null;
		}

		if (_ranges != null)
			updateCalc(100);
	}

}
