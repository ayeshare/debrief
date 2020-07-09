
package ASSET.Scenario.SuperSearch;

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

import ASSET.NetworkParticipant;
import ASSET.ParticipantType;
import ASSET.Util.MonteCarlo.XMLVarianceList;

public class SuperSearch {

	/**
	 * the counter for the current id number
	 *
	 */
	protected static int _currentId;

	//////////////////////////////////////////////
	// clone items, using "Serializable" interface
	/////////////////////////////////////////////////
	static public NetworkParticipant cloneThis(final NetworkParticipant item) {
		NetworkParticipant res = null;
		try {
			final java.io.ByteArrayOutputStream bas = new java.io.ByteArrayOutputStream();
			final java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(bas);
			oos.writeObject(item);
			// get closure
			oos.close();
			bas.close();

			// now get the item
			final byte[] bt = bas.toByteArray();

			// and read it back in as a new item
			final java.io.ByteArrayInputStream bis = new java.io.ByteArrayInputStream(bt);

			// create the reader
			final java.io.ObjectInputStream iis = new java.io.ObjectInputStream(bis);

			// and read it in
			final Object oj = iis.readObject();

			// get more closure
			bis.close();
			iis.close();

			if (oj instanceof ParticipantType) {
				res = (NetworkParticipant) oj;
			}
		} catch (final Exception e) {
			MWC.Utilities.Errors.Trace.trace(e);
		}
		return res;
	}

	/***************************************************************
	 * member variables
	 ***************************************************************/
	/**
	 * number of targets to create along the x axis
	 *
	 */
	private int _xNum;

	/**
	 * number of targets to create along the y axis
	 *
	 */
	private int _yNum;

	/**
	 * the time step for this scenario
	 *
	 */
	private final int _timeStep = 60000;

	/**
	 * area to fill with targets
	 *
	 */
	private MWC.GenericData.WorldArea _area;

	/**
	 * the template of target to work from
	 *
	 */
	protected String _templateFile = null;

	/**
	 * the variance file used to create new vessel instances
	 *
	 */
	protected String _varianceFile = null;

	/**
	 * the id number to start from
	 *
	 */
	protected int _startId;

	/**
	 * build the list of targets
	 *
	 */
	public ParticipantType[] build() {
		final ParticipantType[] results = null;

		// try
		// {
		//
		//
		// // check we have our data
		// if((_templateFile != null) &&
		// (_varianceFile != null))
		// {
		//
		// // read in our SuperSearch control data from the variables file
		// configure(new java.io.FileInputStream(_varianceFile));
		//
		// // prepare the results
		// results = new ParticipantType[1];
		// final java.util.Vector lst = new java.util.Vector(1,10);
		//
		// // set the counters
		// _currentId = _startId;
		//
		// // sort out the TL & offsets
		// // final MWC.GenericData.WorldLocation origin = _area.getTopLeft();
		//
		// // calculate the height and width in degrees (not calculated distance in
		// using
		// // earch model).
		// final double ht = _area.getTopLeft().getLat() -
		// _area.getBottomRight().getLat();
		// final double wid = _area.getBottomRight().getLong() -
		// _area.getTopLeft().getLong();
		//
		// // final double area = ht * wid;
		//
		// final double xSep = wid / (_xNum -1);
		// final double ySep = ht / (_yNum -1);
		//
		// // produce the XML Merging files we need for the creation
		// final XMLVarianceList xl = new XMLVarianceList();
		// xl.setVariables(new java.io.FileInputStream(_varianceFile));
		// xl.setDocument(new java.io.FileInputStream(_templateFile));
		//
		// for(int y=0;y<_yNum;y++)
		// {
		// final double this_lat = ySep * y;
		//
		// for(int x=0;x<_xNum;x++)
		// {
		// final double this_long = xSep * x;
		//
		// final MWC.GenericData.WorldLocation thisP =
		// new MWC.GenericData.WorldLocation(_area.getBottomRight().getLat() + this_lat,
		// _area.getTopLeft().getLong() + this_long,
		// _area.getTopLeft().getDepth());
		//
		// lst.add(buildTarget(thisP, xl));
		// }
		// }
		//
		// results = (ParticipantType[]) lst.toArray(results);
		// }
		//
		// }
		// catch(java.io.FileNotFoundException fe)
		// {
		// fe.printStackTrace();
		// }
		// todo: reinstate this
		return results;
	}

	/**
	 * set the area to fill with targets
	 *
	 */
	// private void setArea(final MWC.GenericData.WorldArea area)
	// {
	// _area = area;
	// }

	/**
	 * build a single target, at the indicated location
	 *
	 */
	protected NetworkParticipant buildTarget(final MWC.GenericData.WorldLocation origin, final XMLVarianceList xl) {

		final ParticipantType result = null;

		// // create a new instance of the file
		// final String file = xl.getNewRandomisedPermutation();
		//
		//// System.out.println("file:" + file);
		//// System.out.println("===========================================");
		//
		// // read in this file
		// final java.io.ByteArrayInputStream bis = new
		// java.io.ByteArrayInputStream(file.getBytes());
		// result = (ASSET.Participants.CoreParticipant)
		// ASSETReaderWriter.importParticipant(_templateFile, bis);
		//
		// if(result != null)
		// {
		//
		// // update the id
		// result.setId(_currentId++);
		//
		// // update the location
		// result.getStatus().setLocation(origin);
		// }
		// todo: reinstate this
		return result;
	}

	/**
	 * get the area to be filled with targets
	 *
	 */
	public MWC.GenericData.WorldArea getArea() {
		return _area;
	}

	/**
	 * get the time step for this scenario (millis)
	 *
	 */
	public int getTimeStep() {
		return _timeStep;
	}

	/**
	 * get the number of targets to create
	 *
	 */
	public int getXNumber() {
		return _xNum;
	}

	/**
	 * get the number of targets to create
	 *
	 */
	public int getYNumber() {
		return _yNum;
	}

	/**
	 * set the id number to start from
	 *
	 */
	public void setIdStart(final int number) {
		_startId = number;
	}

	/***************************************************************
	 * member methods
	 ***************************************************************/
	/**
	 * set the template for the targets to be created
	 *
	 */
	public void setTemplate(final String templateFile) {
		_templateFile = templateFile;
	}

	/**
	 * set the file which tells us how to vary the targets
	 *
	 */
	public void setVariance(final String varianceFile) {
		_varianceFile = varianceFile;
	}

	/**
	 * set the number of targets to create along each axis
	 *
	 */
	protected void setXNumber(final int number) {
		_xNum = number;
	}

	/**
	 * set the number of targets to create along each axis
	 *
	 */
	protected void setYNumber(final int number) {
		_yNum = number;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	//////////////////////////////////////////////////////////////////////////////////////////////////
	// public static class UNtestGenerate extends junit.framework.TestCase
	// {
	// // todo: reinstate this test - change name when we're ready to test Monte
	////////////////////////////////////////////////////////////////////////////////////////////////// Carlo
	////////////////////////////////////////////////////////////////////////////////////////////////// bits
	// static public final String TEST_ALL_TEST_TYPE = "UNTIE";
	// public UNtestGenerate(final String val)
	// {
	// super(val);
	// }
	// public void testCreate()
	// {
	// final int total = 10;
	// final SuperSearch gen = new SuperSearch();
	// gen.setXNumber(total);
	// gen.setYNumber(total);
	// gen.setIdStart(100);
	// final MWC.GenericData.WorldArea coverage = new MWC.GenericData.WorldArea(new
	// MWC.GenericData.WorldLocation(36.5,-4.18,0),
	// new MWC.GenericData.WorldLocation(35.4,-2.72,0));
	// gen.setArea(coverage);
	//
	// // create the red participants
	// final ASSET.Participants.Status stat = new ASSET.Participants.Status(2, 12);
	// final MWC.GenericData.WorldLocation origin = new
	////////////////////////////////////////////////////////////////////////////////////////////////// MWC.GenericData.WorldLocation(1,1,1);
	// stat.setLocation(origin);
	// stat.setCourse(0);
	// stat.setSpeed(new WorldSpeed(3, WorldSpeed.M_sec));
	//
	// // build the ssk
	// final ASSET.ParticipantType ssk1 = new ASSET.Models.Vessels.SSK(11, stat,
	////////////////////////////////////////////////////////////////////////////////////////////////// null,
	////////////////////////////////////////////////////////////////////////////////////////////////// "SSK1");
	//
	// // give the ssk a sensor
	// final ASSET.Models.Sensor.CoreSensor sensor = new
	// ASSET.Models.Sensor.Initial.BroadbandSensor(22);
	// ssk1.addSensor(sensor);
	//
	// String TEST_ROOT = System.getProperty("TEST_ROOT");
	// if(TEST_ROOT == null)
	// {
	// TEST_ROOT = "..\\src\\test_data\\";
	// }
	//
	//
	// String f1 = TEST_ROOT + "Supersearch\\SS2_ControlSimple.xml";
	// String f2 = TEST_ROOT + "Supersearch\\SS2_SSK.xml";
	// assertTrue("couldn't find control file", new File(f1).exists());
	// assertTrue("couldn't find ssk file", new File(f2).exists());
	//
	// // set the template
	// gen.setVariance(f1);
	// gen.setTemplate(f2);
	//
	// final ParticipantType[] lst = gen.build();
	// assertEquals("correct number created", total * total, lst.length);
	//
	// // check the coordinates of the first one
	// final ParticipantType p1 = lst[0];
	// final MWC.GenericData.WorldLocation loc1 = p1.getStatus().getLocation();
	// assertEquals("coordinates of first point", new
	// MWC.GenericData.WorldLocation(coverage.getBottomRight().getLat(),
	// coverage.getTopLeft().getLong(),
	// coverage.getTopLeft().getDepth()), loc1);
	//
	// // check the coordinates of the last one
	// final ParticipantType p2 = lst[lst.length - 1];
	// final MWC.GenericData.WorldLocation loc2 = p2.getStatus().getLocation();
	// assertEquals("coordinates of last point", new
	// MWC.GenericData.WorldLocation(coverage.getTopLeft().getLat(),
	// coverage.getBottomRight().getLong(),
	// coverage.getTopLeft().getDepth()), loc2);
	//
	// // check the sensors
	// assertEquals("first Sensor fit", 2, p1.getNumSensors());
	// assertEquals("last Sensor fit", 2, p2.getNumSensors());
	// assertEquals("first sensor 1 name", "BB", p1.getSensorAt(0).getName());
	// assertEquals("first sensor 2 name", "Optic", p1.getSensorAt(1).getName());
	// assertEquals("last sensor name", "BB", p2.getSensorAt(0).getName());
	//
	//
	// }
	// }
	//
	// public static void main(String[] args) {
	// final UNtestGenerate tt = new UNtestGenerate("blank");
	// tt.testCreate();
	// }

}