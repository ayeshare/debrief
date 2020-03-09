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

package org.mwc.cmap.media.xuggle;

public interface PlayerListener {

	void onPause(XugglePlayer player);

	void onPlay(XugglePlayer player);

	void onPlaying(XugglePlayer player, long milli);

	void onSeek(XugglePlayer player, long milli);

	void onStop(XugglePlayer player);

	void onVideoOpened(XugglePlayer player, String fileName);
}
