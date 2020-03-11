package org.mwc.debrief.pepys.model.bean;

import java.beans.PropertyVetoException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.mwc.debrief.pepys.model.db.DatabaseConnection;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.Fix;
import junit.framework.TestCase;

@TableName(name = "States")
public class State implements FilterableBean, TreeStructurable {

	private int state_id;
	private Timestamp time;
	private int sensor_id;
	private double heading;
	private double course;
	private double speed;
	private int source_id;
	private int privacy_id;
	private Timestamp created_date;
	private WorldLocation location;

	public State() {

	}

	public TrackWrapper createTrackWrapper() {
		final TrackWrapper newTrackWrapper = new TrackWrapper();
		final FixWrapper fixWrapper = new FixWrapper(
				new Fix(new HiResDate(created_date.getTime()), location, course, speed));
		newTrackWrapper.add(fixWrapper);
		return newTrackWrapper;
	}

	public double getCourse() {
		return course;
	}

	public Timestamp getCreated_date() {
		return created_date;
	}

	public double getHeading() {
		return heading;
	}

	@Override
	public String getIdField() {
		return "state_id";
	}

	public WorldLocation getLocation() {
		return location;
	}

	public int getPrivacy_id() {
		return privacy_id;
	}

	public int getSensor_id() {
		return sensor_id;
	}

	public int getSource_id() {
		return source_id;
	}

	public double getSpeed() {
		return speed;
	}

	public int getState_id() {
		return state_id;
	}

	public Timestamp getTime() {
		return time;
	}

	public void setCourse(final double course) {
		this.course = course;
	}

	public void setCreated_date(final Timestamp created_date) {
		this.created_date = created_date;
	}

	public void setHeading(final double heading) {
		this.heading = heading;
	}

	public void setLocation(final WorldLocation location) {
		this.location = location;
	}

	public void setPrivacy_id(final int privacy_id) {
		this.privacy_id = privacy_id;
	}

	public void setSensor_id(final int sensor_id) {
		this.sensor_id = sensor_id;
	}

	public void setSource_id(final int source_id) {
		this.source_id = source_id;
	}

	public void setSpeed(final double speed) {
		this.speed = speed;
	}

	public void setState_id(final int state_id) {
		this.state_id = state_id;
	}

	public void setTime(final Timestamp time) {
		this.time = time;
	}

	@Override
	public String getTimeField() {
		return "time";
	}

	@Override
	public String getLocationField() {
		return "location";
	}

	@Override
	public int getSource() {
		return source_id;
	}

	@Override
	public String getMeasureName() {
		return "States";
	}

	@Override
	public int getSensor() {
		return sensor_id;
	}

	@Override
	public String getMyName() {
		// TODO 
		return state_id + "";
	}

	public static class StatesTest extends TestCase {

		public void testStatesQuery() {
			try {
				final List list = DatabaseConnection.getInstance().listAll(State.class, null);

				assertTrue("States - database entries", list.size() == 543);

				final List list2 = DatabaseConnection.getInstance().listAll(State.class, "source_id = 16");

				assertTrue("States - database entries", list2.size() == 44);

			} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException | PropertyVetoException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
}
