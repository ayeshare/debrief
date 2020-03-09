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

package ASSET.Models.Sensor;

/**
 * Created by IntelliJ IDEA. User: Ian.Mayo Date: 14-Oct-2004 Time: 13:51:35 To
 * change this template use File | Settings | File Templates.
 */
//////////////////////////////////////////////////
// interface used for objects for whom detections can be listened to
//////////////////////////////////////////////////
public interface SensorDataProvider {

	/**
	 * somebody wants to stop listening to us
	 *
	 * @param listener
	 */
	public void addParticipantDetectedListener(ASSET.Participants.ParticipantDetectedListener listener);

	/**
	 * find the list of all detections for this participant since the start of the
	 * scenario
	 *
	 */
	ASSET.Models.Detection.DetectionList getAllDetections();

	/**
	 * somebody wants to start listening to us
	 *
	 * @param listener
	 */
	public void removeParticipantDetectedListener(ASSET.Participants.ParticipantDetectedListener listener);
}
