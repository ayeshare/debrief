
package org.mwc.asset.comms.restlet.host;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.mwc.asset.comms.restlet.data.ListenerResource;
import org.mwc.asset.comms.restlet.host.ASSETHost.HostProvider;
import org.restlet.data.Status;

public class ParticipantListenerHandler extends ASSETResource implements
		ListenerResource
{

	public int accept(final String listenerTxt)
	{
		URI listener;
		int res = 0;
		try
		{
			listener = new URI(listenerTxt);
			final ASSETHost.HostProvider host = (HostProvider) getApplication();
			res = host.getHost().newParticipantListener(getScenarioId(),
					getParticipantId(), listener);

		}
		catch (final URISyntaxException e)
		{
			getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
		}
		return res;
	}

	public void remove()
	{
		final Map<String, Object> attrs = this.getRequestAttributes();
		final Object thisP = attrs.get("listener");
		final int theId = Integer.parseInt((String) thisP);

		final ASSETHost.HostProvider host = (HostProvider) getApplication();
		host.getHost().deleteParticipantListener(getScenarioId(),
				getParticipantId(), theId);
	}

}