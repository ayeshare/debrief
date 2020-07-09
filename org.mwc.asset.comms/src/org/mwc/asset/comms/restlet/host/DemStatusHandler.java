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

package org.mwc.asset.comms.restlet.host;

import org.mwc.asset.comms.restlet.data.DemandedStatusResource;
import org.mwc.asset.comms.restlet.host.ASSETHost.HostProvider;
import org.restlet.resource.Put;

import ASSET.Participants.DemandedStatus;

public class DemStatusHandler extends ASSETResource implements DemandedStatusResource {

	@Override
	@Put
	public void store(final DemandedStatus demState) {
		final ASSETHost.HostProvider hostP = (HostProvider) getApplication();
		final ASSETHost host = hostP.getHost();
		host.setDemandedStatus(getScenarioId(), getParticipantId(), demState);
	}
}