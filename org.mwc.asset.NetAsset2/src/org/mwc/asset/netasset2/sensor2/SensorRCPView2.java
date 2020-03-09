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

package org.mwc.asset.netasset2.sensor2;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.mwc.asset.netasset2.time.IVTime;

import ASSET.Participants.ParticipantDetectedListener;

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

public class SensorRCPView2 extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "org.mwc.asset.NetAsset2.SensorView2";

	private IVTime _timer;
	private ParticipantDetectedListener _detector;

	private VSensor sensor;

	/**
	 * The constructor.
	 */
	public SensorRCPView2() {
	}

	private void contributeToActionBars() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize it.
	 */
	@Override
	public void createPartControl(final Composite parent) {
		sensor = new VSensor(parent, SWT.NONE);

		_timer = sensor;
		_detector = sensor;

		makeActions();
		contributeToActionBars();

	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(final Class adapter) {
		Object res = null;

		if (adapter == IVTime.class) {
			res = _timer;
		} else if (adapter == ParticipantDetectedListener.class) {
			res = _detector;
		}

		if (res == null)
			res = super.getAdapter(adapter);

		return res;
	}

	private void makeActions() {
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
		sensor.setFocus();

	}
}