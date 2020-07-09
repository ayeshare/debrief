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

package org.mwc.cmap.media.xuggle.impl;

import java.util.LinkedList;
import java.util.Queue;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.IStreamCoder;

public class SoundThread extends Thread {
	private final AudioFormat audioFormat;
	private final SourceDataLine soundLine;
	private final boolean blocked;
	private final Queue<byte[]> buffers = new LinkedList<byte[]>();

	private volatile boolean reset;

	public SoundThread(final IStreamCoder aAudioCoder, final boolean blocked) throws LineUnavailableException {
		audioFormat = new AudioFormat(aAudioCoder.getSampleRate(),
				(int) IAudioSamples.findSampleBitDepth(aAudioCoder.getSampleFormat()), aAudioCoder.getChannels(), true,
				false);
		final DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
		soundLine = (SourceDataLine) AudioSystem.getLine(info);
		openSoundLine();
		this.blocked = blocked;

		setDaemon(true);
	}

	private void closeSoundLine() {
		soundLine.flush();
		soundLine.drain();
		soundLine.close();
	}

	@Override
	public void interrupt() {
		if (isAlive()) {
			super.interrupt();
		} else {
			soundLine.close();
		}
	}

	private void openSoundLine() throws LineUnavailableException {
		soundLine.open(audioFormat);
		soundLine.start();
	}

	public void play(final IAudioSamples samples, final boolean notify) {
		synchronized (buffers) {
			if (reset) {
				return;
			}
			buffers.add(samples.getData().getByteArray(0, samples.getSize()));
			if (notify) {
				buffers.notify();
			}
		}
	}

	public void reset() {
		synchronized (buffers) {
			reset = true;
			buffers.add(new byte[0]);
			buffers.notify();
		}
	}

	@Override
	public void run() {
		try {
			while (true) {
				byte[] buffer;
				if (isInterrupted()) {
					break;
				}
				try {
					synchronized (buffers) {
						if (buffers.isEmpty()) {
							buffers.wait();
						}
						buffer = buffers.poll();
						if (reset) {
							closeSoundLine();
							try {
								openSoundLine();
							} catch (final LineUnavailableException ex) {
								ex.printStackTrace();
								break;
							}
							buffers.clear();
							reset = false;
							continue;
						}
						if (blocked) {
							soundLine.write(buffer, 0, buffer.length);
						}
					}
				} catch (final InterruptedException ex) {
					break;
				}
				if (!blocked) {
					soundLine.write(buffer, 0, buffer.length);
				}
			}
		} finally {
			closeSoundLine();
		}
	}
}