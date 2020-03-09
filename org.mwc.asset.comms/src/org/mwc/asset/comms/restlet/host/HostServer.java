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

import org.mwc.asset.comms.restlet.host.ASSETHost.HostProvider;
import org.mwc.asset.comms.restlet.test.MockHost;
import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Restlet;
import org.restlet.data.Protocol;
import org.restlet.routing.Router;

abstract public class HostServer extends Application implements HostProvider {

	public static void finish(final Component component) throws Exception {
		component.stop();
	}

	public static Component go(final Restlet host) throws Exception {
		final Component component = new Component();
		component.getClients().add(Protocol.FILE);
		component.getServers().add(Protocol.HTTP, 8080);
		component.getDefaultHost().attach(host);
		component.start();

		return component;
	}

	/**
	 * When launched as a standalone application.
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(final String[] args) throws Exception {
		final Restlet host = new HostServer() {

			ASSETHost host = new MockHost();

			@Override
			public ASSETHost getHost() {
				return host;
			}
		};
		HostServer.go(host);
	}

	@Override
	public Restlet createInboundRoot() {
		final Router router = new Router(getContext());
		getConnectorService().getClientProtocols().add(Protocol.FILE);

		router.attach("/v1/scenario", ScenariosHandler.class);
		router.attach("/v1/scenario/{scenario}/state", ScenarioStateHandler.class);
		router.attach("/v1/scenario/{scenario}/listener", ScenarioListenerHandler.class);
		router.attach("/v1/scenario/{scenario}/listener/{listener}", ScenarioListenerHandler.class);
		router.attach("/v1/scenario/{scenario}/participant", ParticipantsHandler.class);
		router.attach("/v1/scenario/{scenario}/participant/{participant}/demState", DemStatusHandler.class);
		router.attach("/v1/scenario/{scenario}/participant/{participant}/listener", ParticipantListenerHandler.class);
		router.attach("/v1/scenario/{scenario}/participant/{participant}/listener/{listener}",
				ParticipantListenerHandler.class);
		router.attach("/v1/scenario/{scenario}/participant/{participant}/decisionListener",
				DecisionListenerHandler.class);
		router.attach("/v1/scenario/{scenario}/participant/{participant}/decisionListener/{listener}",
				DecisionListenerHandler.class);
		router.attach("/v1/scenario/{scenario}/participant/{participant}/sensor", SensorsHandler.class);
		router.attach("/v1/scenario/{scenario}/participant/{participant}/detectionListener",
				DetectionListenerHandler.class);
		router.attach("/v1/scenario/{scenario}/participant/{participant}/detectionListener/{listener}",
				DetectionListenerHandler.class);
		return router;
	}

	/**
	 * provide an interface to the actual data
	 * 
	 */
	@Override
	abstract public ASSETHost getHost();

}
