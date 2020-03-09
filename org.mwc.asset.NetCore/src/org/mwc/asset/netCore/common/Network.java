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

package org.mwc.asset.netCore.common;

import java.util.Vector;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.serialize.CollectionSerializer;
import com.esotericsoftware.kryonet.EndPoint;

import ASSET.ParticipantType;
import ASSET.ScenarioType;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Detection.DetectionList;
import ASSET.Participants.Category;
import ASSET.Participants.Status;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;

// This class is a convenient place to keep things common to both the client and server.
public class Network {
	/**
	 * and our event handler
	 *
	 */
	public abstract static class AHandler<T> {
		public void onFailure(final Throwable t) {
			t.printStackTrace();
		}

		abstract public void onSuccess(T result);
	}

	/**
	 * specify a demanded status for this part
	 *
	 * @author ianmayo
	 *
	 */
	public static class DemStatus {
		public String scenario;
		public int partId;
		public double courseDegs;
		public double speedKts;
		public double depthM;
	}

	public static class GetScenarios {
	}

	public static class LightParticipant {
		public int id;;

		public String name;

		public Category category;

		public String activity;

		public LightParticipant() {
		}

		public LightParticipant(final int Id, final String string) {
			this.id = Id;
			name = string;
			category = new Category(Category.Force.RED, Category.Environment.SURFACE, Category.Type.SONAR_BUOY);
			activity = "some activity";
		}

		public LightParticipant(final ParticipantType pt) {
			id = pt.getId();
			name = pt.getName();
			category = pt.getCategory();
			activity = pt.getActivity();
		}
	}

	public static class LightScenario {
		public String name;;

		public Vector<LightParticipant> listOfParticipants;;

		public LightScenario() {
		};

		public LightScenario(final ScenarioType scenario) {
			listOfParticipants = new Vector<LightParticipant>();
			name = scenario.getName();
			final Integer[] list = scenario.getListOfParticipants();
			for (int i = 0; i < list.length; i++) {
				final Integer integer = list[i];
				final ParticipantType pt = scenario.getThisParticipant(integer);
				listOfParticipants.add(new LightParticipant(pt));
			}
		}

		@Override
		public String toString() {
			return name + " (" + listOfParticipants.size() + ")";
		}
	}

	/**
	 * we wish to start receiving updates for this participant
	 *
	 * @author ianmayo
	 *
	 */
	public static class ListenPart {
		public String scenarioName;
		public int partId;
	}

	public static class ListenScen {
		public String name;
	}

	public static class PartDetection {
		public int id;
		public String scenario;
		public DetectionList detections;

		public PartDetection() {
		};

		public PartDetection(final int id, final String scenario, final DetectionList detections) {
			this.id = id;
			this.scenario = scenario;
			this.detections = detections;
		}
	}

	public static class PartMovement {
		public int id;
		public Status lStatus;
		public String scenario;

		public PartMovement() {
		};

		public PartMovement(final int id, final String scenario, final Status status) {
			this.id = id;
			this.scenario = scenario;
			lStatus = status;
		}
	}

	/**
	 * we no longer wish to control this part, restore it's original decision model
	 *
	 * @author ianmayo
	 *
	 */
	public static class ReleasePart {
		public String scenarioName;
		public int partId;
	}

	public static class ScenarioList {
		public Vector<LightScenario> list;
	}

	public static class ScenControl {
		public static final String PLAY = "Start";
		public static final String STEP = "Step";
		public static final String PAUSE = "Pause";
		public static final String TERMINATE = "Finish";
		public static final String FASTER = "FASTER";
		public static final String SLOWER = "SLOWER";

		public String instruction;
		public String scenarioName;

		public ScenControl() {
		};

		public ScenControl(final String scenarioName, final String step2) {
			this.scenarioName = scenarioName;
			this.instruction = step2;
		}

	}

	public static class ScenUpdate {
		public static final String PLAYING = "Started";;

		public static final String STEPPED = "Stepped";

		public static final String PAUSED = "Paused";
		public static final String TERMINATED = "Finished";
		public String scenarioName;
		public long newTime;
		public String event;

		public ScenUpdate() {
		}

		public ScenUpdate(final String scenName, final String stepped2, final long newTime2) {
			scenarioName = scenName;
			event = STEPPED;
			newTime = newTime2;
		}
	}

	public static class SomeRequest {
		public String text;
	}

	public static class SomeResponse {
		public String text;
	}

	/**
	 * we no longer wish to receive updates for this participant
	 *
	 * @author ianmayo
	 *
	 */
	public static class StopListenPart {
		public String scenarioName;
		public int partId;
	}

	public static class StopListenScen {
		public String name;
	}

	public static final int UDP_PORT = 54778;
	public static final int TCP_PORT = 54555;

	public static final int DUFF_INDEX = -1;

	// This registers objects that are going to be sent over the network.
	static public void register(final EndPoint endPoint) {
		final Kryo kryo = endPoint.getKryo();
		// sample ones
		kryo.register(SomeRequest.class);
		kryo.register(SomeResponse.class);
		// real ones
		kryo.register(GetScenarios.class);
		kryo.register(ScenarioList.class);
		kryo.register(LightScenario.class);
		kryo.register(ListenScen.class);
		kryo.register(StopListenScen.class);
		kryo.register(ScenUpdate.class);
		kryo.register(ScenControl.class);
		kryo.register(LightParticipant.class);
		kryo.register(Vector.class, new CollectionSerializer(kryo));
		kryo.register(Category.class);
		kryo.register(ListenPart.class);
		kryo.register(StopListenPart.class);
		kryo.register(PartMovement.class);
		kryo.register(PartDetection.class);
		kryo.register(DetectionList.class);
		kryo.register(TimePeriod.class);
		kryo.register(DetectionEvent.class);
		kryo.register(DemStatus.class);
		kryo.register(ReleasePart.class);
		kryo.register(WorldSpeed.class);
		kryo.register(WorldLocation.class);
		kryo.register(WorldDistance.class);
		kryo.register(Status.class);
	}

}
