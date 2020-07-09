package org.mwc.debrief.dis.diagnostics.file;

import org.mwc.debrief.dis.listeners.IDISCollisionListener;

public class CollisionFileListener extends CoreFileListener implements IDISCollisionListener {

	public CollisionFileListener(final String root, final boolean toFile, final boolean toScreen,
			final LoggingFileWriter writer) {
		super(root, toFile, toScreen, "collision",
				"time, exerciseId, movingId, name, recipientId, recipientName, dLat, dLong, depthM", writer);
	}

	@Override
	public void add(final long time, final short eid, final int movingId, final String movingName,
			final int recipientId, final String recipientName, final double dLat, final double dLong,
			final double depthM) {
		// create the line
		final StringBuffer out = new StringBuffer();
		out.append(time);
		out.append(", ");
		out.append(eid);
		out.append(", ");
		out.append(movingId);
		out.append(", ");
		out.append(movingName);
		out.append(", ");
		out.append(recipientId);
		out.append(", ");
		out.append(recipientName);
		out.append(", ");
		out.append(dLat);
		out.append(", ");
		out.append(dLong);
		out.append(", ");
		out.append(depthM);
		out.append(LINE_BREAK);

		// done, write it
		write(out.toString());
	}

}