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

import java.awt.image.BufferedImage;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import com.xuggle.xuggler.Global;
import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IRational;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.IVideoResampler;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;

public class PlayingThread extends Thread {
	private class PicturesBuffer {
		private final Queue<IVideoPicture> picturesToPlay = new ArrayBlockingQueue<IVideoPicture>(
				PICTURES_BUFFER_SIZE + 2);
		private final Queue<IVideoPicture> freePictures = new ArrayBlockingQueue<IVideoPicture>(
				PICTURES_BUFFER_SIZE + 2);

		public PicturesBuffer(final IStreamCoder videoCoder) {
			if (videoCoder == null) {
				return;
			}
			for (int i = 0; i < PICTURES_BUFFER_SIZE + 2; i++) {
				freePictures.add(
						IVideoPicture.make(videoCoder.getPixelType(), videoCoder.getWidth(), videoCoder.getHeight()));
			}
		}

		public void add(final IVideoPicture picture) {
			final IVideoPicture current = freePictures.poll();
			current.copy(picture);
			picturesToPlay.add(current);
		}

		public boolean isReadyToPlay() {
			return picturesToPlay.size() >= PICTURES_BUFFER_SIZE;
		}

		public boolean isReadyToPlayAudio() {
			return picturesToPlay.size() >= PICTURES_BUFFER_SIZE - 1;
		}

		public IVideoPicture poll() {
			final IVideoPicture current = picturesToPlay.poll();
			freePictures.add(current);
			return current;
		}

		public void reset() {
			freePictures.addAll(picturesToPlay);
			picturesToPlay.clear();
		}
	}

	private class PlayerClock {
		private long firstTimestampInStream = Global.NO_PTS;
		private long systemClockStartTime;

		public void reset() {
			firstTimestampInStream = Global.NO_PTS;
		}

		public void sleepToNextFrame(final long timestamp, final boolean hasVideo) {
			if (firstTimestampInStream == Global.NO_PTS) {
				firstTimestampInStream = timestamp;
				systemClockStartTime = System.currentTimeMillis();
			} else {
				final long systemClockCurrentTime = System.currentTimeMillis();
				final long millisecondsClockTimeSinceStartofVideo = systemClockCurrentTime - systemClockStartTime;
				final long millisecondsStreamTimeSinceStartOfVideo = (timestamp - firstTimestampInStream) / 1000;
				final long millisecondsTolerance = hasVideo ? 50 : 0; // and we give ourselfs 50 ms of tolerance
				final long millisecondsToSleep = (millisecondsStreamTimeSinceStartOfVideo
						- (millisecondsClockTimeSinceStartofVideo + millisecondsTolerance));
				if (millisecondsToSleep > 0) {
					try {
						Thread.sleep(millisecondsToSleep);
					} catch (final InterruptedException e) {
						return;
					}
				}
			}
		}
	}

	private static final int PICTURES_BUFFER_SIZE = 24;

	private final Object pauseMutex = new Object();
	private final Object pauseAppliedMutex = new Object();

	private final String fileName;
	private final ThreadUINotifier notifier;
	private volatile boolean stop = false;
	private volatile boolean pause = true;

	private volatile long seekMillis = -1;
	private volatile long currentPosition;

	private SoundThread soundThread;
	private long audioSeekOffset;
	private volatile IContainer container;
	private volatile IConverter converter;
	private volatile IVideoResampler resampler;
	private volatile IStream videoStream;
	private volatile IStream audioStream;

	private volatile IStreamCoder videoCoder;

	private volatile IStreamCoder audioCoder;

	public PlayingThread(final String fileName, final ThreadUINotifier notifier) {
		this.notifier = notifier;
		this.fileName = fileName;
	}

	private void closeResources() {
		if (container != null) {
			container.close();
		}
		if (videoCoder != null) {
			videoCoder.close();
		}
		if (audioCoder != null) {
			audioCoder.close();
		}
		if (soundThread != null) {
			soundThread.interrupt();
		}
	}

	private void doSeekAudio(final long seek, final IPacket packet, final IAudioSamples samples) {
		// try to use xuggler bundled algorithm for supported codecs
		final IRational rational = audioStream.getTimeBase();
		final long seekTime = seek * rational.getDenominator() / rational.getNumerator() / 1000;
		if (container.seekKeyFrame(audioStream.getIndex(), seekTime, IContainer.SEEK_FLAG_ANY) >= 0) {
			audioSeekOffset = -getStartTime() * 1000;
			return;
		}
		// xuggler doesn't have good positioning algorithm for audio. So here is my own
		// one
		audioCoder.close();
		if (audioCoder.open() < 0) {
			throw new RuntimeException("can't open codec");
		}
		while (container.readNextPacket(packet) >= 0) {
			if (packet.getStreamIndex() == audioStream.getIndex()) {
				break;
			}
		}
		final long headerOffset = packet.getPosition();
		audioSeekOffset = seek - getStartTime();
		final long bytesOffset = (audioSeekOffset * (container.getFileSize() - headerOffset)) / getDuration();
		container.seekKeyFrame(audioStream.getIndex(), bytesOffset, IContainer.SEEK_FLAG_BYTE);
		audioSeekOffset *= 1000;
	}

	private void doSeekVideo(final long seek, final IPacket packet, final IVideoPicture picture) {
		final IRational rational = videoStream.getTimeBase();
		final long seekTime = seek * rational.getDenominator() / rational.getNumerator() / 1000;
		container.seekKeyFrame(videoCoder.getStream().getIndex(), 0, seekTime, seekTime, 0);
		final long pictureTime = seek * 1000;
		while (container.readNextPacket(packet) >= 0) {
			if (packet.getStreamIndex() == videoStream.getIndex()) {
				int offset = 0;
				while (offset < packet.getSize()) {
					final int bytesDecoded = videoCoder.decodeVideo(picture, packet, offset);
					if (bytesDecoded < 0) {
						throw new RuntimeException("got error decoding video in:" + fileName);
					}
					offset += bytesDecoded;

					if (picture.isComplete() && picture.getTimeStamp() >= pictureTime) {
						return;
					}
				}
			}
		}
	}

	public long getCurrentPosition() {
		return currentPosition;
	}

	public long getDuration() {
		return container.getDuration() / 1000;
	}

	private long getStartTime() {
		final long containerStartTime = container.getStartTime();
		if (containerStartTime < 0) {
			return 0;
		}
		return containerStartTime / 1000;
	}

	public boolean hasAudio() {
		return audioStream != null;
	}

	public boolean hasVideo() {
		return videoStream != null;
	}

	private boolean init() {
		container = IContainer.make();
		if (container.open(fileName, IContainer.Type.READ, null) < 0) {
			return false;
		}

		for (int i = 0; i < container.getNumStreams(); i++) {
			final IStream stream = container.getStream(i);
			if (stream.getStreamCoder().getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) {
				videoStream = stream;
				videoCoder = videoStream.getStreamCoder();
			}
			if (stream.getStreamCoder().getCodecType() == ICodec.Type.CODEC_TYPE_AUDIO) {
				audioStream = stream;
				audioCoder = audioStream.getStreamCoder();
			}
		}

		if (videoCoder != null && videoCoder.open() < 0) {
			videoCoder = null;
			videoStream = null;
		}

		if (audioCoder != null) {
			if (audioCoder.open() >= 0) {
				try {
					soundThread = new SoundThread(audioCoder, videoCoder == null);
				} catch (final Exception ex) {
					audioCoder.close();
					audioCoder = null;
					audioStream = null;
				}
			} else {
				audioCoder = null;
				audioStream = null;
			}
		}
		if (videoStream == null && audioStream == null) {
			closeResources();
			return false;
		}
		if (videoCoder != null && videoCoder.getPixelType() != IPixelFormat.Type.BGR24) {
			resampler = IVideoResampler.make(videoCoder.getWidth(), videoCoder.getHeight(), IPixelFormat.Type.BGR24,
					videoCoder.getWidth(), videoCoder.getHeight(), videoCoder.getPixelType());
			if (resampler == null) {
				closeResources();
				throw new RuntimeException("could not create color space " + "resampler for: " + fileName);
			}
		}
		return true;
	}

	public boolean isPaused() {
		return pause;
	}

	public void pause() {
		if (!this.pause) {
			synchronized (pauseAppliedMutex) {
				try {
					this.pause = true;
					if (isAlive()) {
						pauseAppliedMutex.wait();
						setPriority(MIN_PRIORITY);
					}
				} catch (final InterruptedException ex) {
					// ignore
				}
			}
		}
	}

	private void playSamples(final PlayerClock clock, final PicturesBuffer picturesBuffer,
			final IAudioSamples samples) {
		soundThread.play(samples, videoStream == null || picturesBuffer.isReadyToPlayAudio());
		if (videoStream == null) {
			final long timestamp = (samples.getTimeStamp() + audioSeekOffset) / 1000;
			currentPosition = timestamp;
			clock.sleepToNextFrame(timestamp, false);
			notifier.updateFrame(null, timestamp, -1);
		}
	}

	public void resumePlaying() {
		if (pause) {
			pause = false;
			synchronized (pauseMutex) {
				pauseMutex.notify();
			}
			if (isAlive()) {
				setPriority(MAX_PRIORITY);
			}
		}
	}

	@Override
	public void run() {
		try {
			runInternal();
		} catch (final RuntimeException ex) {
			ex.printStackTrace();
			notifier.applyStop();
		} finally {
			closeResources();
		}
	}

	public void runInternal() {
		final IPacket packet = IPacket.make();
		final IVideoPicture picture = videoStream == null ? null
				: IVideoPicture.make(videoCoder.getPixelType(), videoCoder.getWidth(), videoCoder.getHeight());
		final IVideoPicture resampledPicture = resampler == null ? null
				: IVideoPicture.make(resampler.getOutputPixelFormat(), picture.getWidth(), picture.getHeight());
		final IAudioSamples samples = audioStream == null ? null : IAudioSamples.make(1024, audioCoder.getChannels());

		final PicturesBuffer picturesBuffer = new PicturesBuffer(videoCoder);
		final PlayerClock clock = new PlayerClock();
		while (!stop) {
			if (pause && seekMillis == -1) {
				if (soundThread != null) {
					soundThread.reset();
				}
				synchronized (pauseMutex) {
					synchronized (pauseAppliedMutex) {
						pauseAppliedMutex.notify();
					}
					try {
						pauseMutex.wait();
					} catch (final InterruptedException ex) {
						break;
					}
					clock.reset();
					picturesBuffer.reset();
				}
			}
			if (seekMillis != -1) {
				if (soundThread != null) {
					soundThread.reset();
				}
				long oldSeekMillis = -1;
				while (seekMillis != oldSeekMillis) {
					oldSeekMillis = seekMillis;
					if (videoStream != null) {
						doSeekVideo(oldSeekMillis, packet, picture);
						updatePicture(null, picture, resampledPicture, oldSeekMillis);
					} else {
						doSeekAudio(oldSeekMillis, packet, samples);
						notifier.updateFrame(null, oldSeekMillis, oldSeekMillis);
					}
				}
				clock.reset();
				picturesBuffer.reset();
				seekMillis = -1;
				continue;
			}
			if (container.readNextPacket(packet) < 0) {
				seekMillis = 0;
				pause = true;
				notifier.applyPause();
				continue;
			}
			if (videoStream != null && packet.getStreamIndex() == videoStream.getIndex()) {
				int offset = 0;
				while (offset < packet.getSize()) {
					final int bytesDecoded = videoCoder.decodeVideo(picture, packet, offset);
					if (bytesDecoded < 0) {
						break;
					}
					offset += bytesDecoded;
					if (picture.isComplete()) {
						picturesBuffer.add(picture);
						if (picturesBuffer.isReadyToPlay()) {
							updatePicture(clock, picturesBuffer.poll(), resampledPicture, -1);
						}
					}
				}
			}
			if (audioStream != null && packet.getStreamIndex() == audioStream.getIndex()) {
				int offset = 0;
				while (offset < packet.getSize()) {
					final int bytesDecoded = audioCoder.decodeAudio(samples, packet, offset);
					if (bytesDecoded < 0) {
						break;
					}
					offset += bytesDecoded;
					if (samples.isComplete()) {
						playSamples(clock, picturesBuffer, samples);
					}
				}
			}
		}
	}

	public void seek(final long milli) {
		this.seekMillis = milli + getStartTime();
		if (pause) {
			synchronized (pauseMutex) {
				pauseMutex.notify();
			}
		}
	}

	public synchronized boolean startVideoThread() {
		if (!init()) {
			return false;
		}
		if (soundThread != null) {
			soundThread.start();
		}
		start();
		return true;
	}

	public void stopPlaying() {
		stop = true;
		if (!isAlive()) {
			return;
		}
		resumePlaying();
		if (Thread.currentThread() != this) {
			try {
				this.join();
			} catch (final InterruptedException ex) {
				// ignore
			}
		}
	}

	private void updatePicture(final PlayerClock clock, final IVideoPicture picture, IVideoPicture resampledPicture,
			final long seek) {
		if (resampler != null) {
			if (resampler.resample(resampledPicture, picture) < 0)
				throw new RuntimeException("could not resample video from: " + fileName);
		} else {
			resampledPicture = picture;
		}
		if (converter == null) {
			converter = ConverterFactory.createConverter(ConverterFactory.XUGGLER_BGR_24, resampledPicture);
		}
		currentPosition = (picture.getTimeStamp() / 1000) - getStartTime();
		final BufferedImage image = converter.toImage(resampledPicture);
		if (clock != null) {
			clock.sleepToNextFrame(picture.getTimeStamp(), true);
		}
		notifier.updateFrame(image, currentPosition, seek);
		if (clock != null) {
			Thread.yield();
		}
	}
}