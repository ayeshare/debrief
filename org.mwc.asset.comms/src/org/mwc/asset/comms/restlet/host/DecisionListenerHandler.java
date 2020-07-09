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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.mwc.asset.comms.restlet.data.ListenerResource;
import org.mwc.asset.comms.restlet.host.ASSETHost.HostProvider;
import org.restlet.data.Status;

public class DecisionListenerHandler extends ASSETResource implements ListenerResource {

	@Override
	public int accept(final String listenerTxt) {
		URI listener;
		int res = 0;
		try {
			listener = new URI(listenerTxt);
			final ASSETHost.HostProvider host = (HostProvider) getApplication();
			res = host.getHost().newParticipantDecisionListener(getScenarioId(), getParticipantId(), listener);

		} catch (final URISyntaxException e) {
			getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
		}
		return res;
	}

	@Override
	public void remove() {
		final Map<String, Object> attrs = this.getRequestAttributes();
		final Object thisP = attrs.get("listener");
		final int theId = Integer.parseInt((String) thisP);

		final ASSETHost.HostProvider host = (HostProvider) getApplication();
		host.getHost().deleteParticipantDecisionListener(getScenarioId(), getParticipantId(), theId);
	}

}