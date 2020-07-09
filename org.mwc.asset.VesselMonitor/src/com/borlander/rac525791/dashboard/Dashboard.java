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

package com.borlander.rac525791.dashboard;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.swt.widgets.Composite;

import com.borlander.rac525791.dashboard.data.DashboardDataModel;

public class Dashboard extends FigureCanvas {
	private final DashboardFigure myRootFigure;
	private final DashboardDataModel myDataModel;

	public Dashboard(final Composite parent) {
		super(parent);
		setScrollBarVisibility(NEVER);
		myDataModel = new DashboardDataModel();
		myRootFigure = new DashboardFigure();
		new DashboardUpdater(myDataModel, myRootFigure, this);

		getViewport().setContentsTracksWidth(true);
		getViewport().setContentsTracksHeight(true);

		getViewport().setContents(myRootFigure);
	}

	@Override
	public void dispose() {
		myRootFigure.dispose();
	}

	public DashboardDataModel getDataModel() {
		return myDataModel;
	}

}
