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

package MWC.GUI.Video;

import java.io.IOException;

import javax.media.Time;
import javax.media.protocol.PushBufferDataSource;
import javax.media.protocol.PushBufferStream;

/**
 * This DataSource captures live frames from the screen. You can specify the
 * location, size and frame rate in the URL string as follows:
 * screen://x,y,width,height/framespersecond Eg: screen://20,40,160,120/12.5
 * Note: Requires JDK 1.3+ to compile and run
 */

public class JDataSource extends PushBufferDataSource {

	protected Object[] controls = new Object[0];
	protected boolean started = false;
	protected String contentType = "raw";
	protected boolean connected = false;
	protected Time duration = DURATION_UNBOUNDED;
	protected LiveStream[] streams = null;
	protected LiveStream stream = null;

	public JDataSource() {
	}

	@Override
	public void connect() throws IOException {
		if (connected)
			return;
		connected = true;
	}

	@Override
	public void disconnect() {
		try {
			if (started)
				stop();
		} catch (final IOException e) {
		}
		connected = false;
	}

	@Override
	public String getContentType() {
		if (!connected) {
			System.err.println("Error: DataSource not connected");
			return null;
		}
		return contentType;
	}

	@Override
	public Object getControl(final String controlType) {
		return null;
	}

	@Override
	public Object[] getControls() {
		return controls;
	}

	@Override
	public Time getDuration() {
		return duration;
	}

	@Override
	public PushBufferStream[] getStreams() {
		if (streams == null) {
			streams = new LiveStream[1];
			stream = streams[0] = new LiveStream(getLocator());
		}
		return streams;
	}

	@Override
	public void start() throws IOException {
		// we need to throw error if connect() has not been called
		if (!connected)
			throw new java.lang.Error("DataSource must be connected before it can be started");
		if (started)
			return;
		started = true;
		stream.start(true);
	}

	@Override
	public void stop() throws IOException {
		if ((!connected) || (!started))
			return;
		started = false;
		stream.start(false);
	}
}
