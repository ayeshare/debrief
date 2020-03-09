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

package org.mwc.asset.core;

import org.eclipse.jface.resource.ImageDescriptor;
import org.mwc.cmap.core.ui_support.CoreViewLabelProvider.ViewLabelImageHelper;

import ASSET.NetworkParticipant;
import ASSET.GUI.Workbench.Plotters.BehavioursPlottable;
import ASSET.GUI.Workbench.Plotters.MoveCharsPlottable;
import ASSET.GUI.Workbench.Plotters.ScenarioParticipantWrapper;
import ASSET.GUI.Workbench.Plotters.SensorsPlottable;
import ASSET.Models.DecisionType;
import ASSET.Models.MovementType;
import ASSET.Models.SensorType;
import ASSET.Participants.Category.Force;
import MWC.GUI.Editable;

public class ASSETImageHelper implements ViewLabelImageHelper {

	@Override
	public ImageDescriptor getImageFor(final Editable subject) {
		final ImageDescriptor res;
		if ((subject instanceof SensorType) || (subject instanceof SensorsPlottable)) {
			res = ASSETPlugin.getImageDescriptor("icons/satellite_dish.png");
		} else if ((subject instanceof MovementType) || (subject instanceof MoveCharsPlottable)) {
			res = ASSETPlugin.getImageDescriptor("icons/gear.png");
		} else if ((subject instanceof DecisionType) || (subject instanceof BehavioursPlottable)) {
			res = ASSETPlugin.getImageDescriptor("icons/user_comment.png");
		} else if (subject instanceof ScenarioParticipantWrapper) {
			final ScenarioParticipantWrapper sw = (ScenarioParticipantWrapper) subject;
			final NetworkParticipant part = sw.getParticipant();
			final String force = part.getCategory().getForce();
			if (force == Force.RED)
				res = ASSETPlugin.getImageDescriptor("icons/flag_red.png");
			else if (force == Force.GREEN)
				res = ASSETPlugin.getImageDescriptor("icons/flag_green.png");
			else if (force == Force.BLUE)
				res = ASSETPlugin.getImageDescriptor("icons/flag_blue.png");
			else
				res = null;
		} else {
			res = null;
		}

		return res;
	}

}
