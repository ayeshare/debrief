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

package com.planetmayo.debrief.satc_rcp.ui.contributions;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.widgets.Composite;

import com.planetmayo.debrief.satc.model.contributions.StraightLegForecastContribution;
import com.planetmayo.debrief.satc.model.generator.IContributions;

public class StraightLegForecastContributionView extends BaseContributionView<StraightLegForecastContribution> {

	public StraightLegForecastContributionView(final Composite parent,
			final StraightLegForecastContribution contribution, final IContributions contributions) {
		super(parent, contribution, contributions);
		initUI();
	}

	@Override
	protected void bindValues(final DataBindingContext context) {
		bindCommonHeaderWidgets(context, null, null, null);
		bindCommonDates(context);
	}

	@Override
	protected void createLimitAndEstimateSliders() {
	}

	@Override
	protected String getTitlePrefix() {
		return "Straight Leg Forecast - ";
	}

	@Override
	protected void initializeWidgets() {
		hardConstraintLabel.setText("n/a");
		estimateLabel.setText("n/a");
	}
}
