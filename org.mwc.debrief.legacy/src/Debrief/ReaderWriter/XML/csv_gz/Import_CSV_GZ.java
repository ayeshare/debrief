/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package Debrief.ReaderWriter.XML.csv_gz;

import java.awt.Color;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.compressors.gzip.GzipUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;
import MWC.GUI.ErrorLogger;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.LoggingService;
import MWC.GUI.Properties.DebriefColors;
import MWC.GUI.Properties.LineStylePropertyEditor;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.TacticalData.Fix;
import MWC.Utilities.TextFormatting.GMTDateFormat;
import junit.framework.TestCase;

public class Import_CSV_GZ
{

  private static TrackWrapper trackFor(Layers layers, String trackName)
  {
    final TrackWrapper track;
    Layer layer = layers.findLayer(trackName);
    if (layer != null && layer instanceof TrackWrapper)
    {
      track = (TrackWrapper) layer;
    }
    else
    {
      final boolean needsRename;
      if (layer == null)
      {
        needsRename = false;
      }
      else
      {
        needsRename = true;
      }

      final String nameToUse;
      if (needsRename)
      {
        String suffix = "-" + (int) Math.random() * 1000;
        nameToUse = trackName + suffix;
      }
      else
      {
        nameToUse = trackName;
      }

      track = new TrackWrapper();
      track.setName(nameToUse);

      // sort out a color
      // sort out a color
      final Color theCol = DebriefColors.RandomColorProvider.getRandomColor(
          colorCounter++);
      track.setColor(theCol);

      layers.addThisLayer(track);
    }
    return track;
  }

  private static final String CSV_DATE_FORMAT = "dd MMM yyyy - HH:mm:ss.SSS";

  public static class TestCSV_GZ_Import extends TestCase
  {
    static class Logger implements ErrorLogger
    {
      private final List<String> messages = new ArrayList<String>();

      private final boolean console = true;

      @Override
      public void logError(final int status, final String text,
          final Exception e)
      {
        output(text, e);
      }

      @Override
      public void logError(final int status, final String text,
          final Exception e, final boolean revealLog)
      {
        output(text, e);
      }

      @Override
      public void logStack(final int status, final String text)
      {
        output(text, null);
      }

      public void output(final String text, final Exception e)
      {
        messages.add(text);
        if (console)
        {
          System.out.println(text);
          if (e != null)
          {
            e.printStackTrace();
          }
        }
      }

      public boolean isEmpty()
      {
        return messages.isEmpty();
      }
    }

    public void test_parse_OSD_File() throws IOException
    {
      final String root =
          "../org.mwc.cmap.combined.feature/root_installs/sample_data/other_formats/csv_gz/";
      final String filename = "BARTON_xxxx_OSD_xxxx.csv.gz";

      // start off with the ownship track
      final File zipFile = new File(root + filename);
      assertTrue(zipFile.exists());
      
      assertTrue("is gzip", GzipUtils.isCompressedFilename(root + filename));
      assertEquals("name", "BARTON_xxxx_OSD_xxxx.csv", GzipUtils.getUncompressedFilename(filename));
      
      final InputStream bs = new FileInputStream(zipFile);
      
      GZIPInputStream in = new GZIPInputStream(bs);
      final ByteArrayOutputStream bos = new ByteArrayOutputStream();
      final BufferedOutputStream fout = new BufferedOutputStream(bos);
      for (int c = in.read(); c != -1; c = in.read())
      {
        fout.write(c);
      }
      in.close();
      fout.close();
      bos.close();

      // now create a byte input stream from the byte output stream
      final ByteArrayInputStream bis = new ByteArrayInputStream(bos
          .toByteArray());

      // get the file as a string
      final String contents = inputStreamAsString(bis);
      System.out.println(contents);
      
      Layers theLayers = new Layers();

      // check empty
      assertEquals("empty", 0, theLayers.size());

      doZipImport(theLayers, bs, filename);

      // check empty
      assertEquals("has track", 1, theLayers.size());

    }

    public void testDateParse() throws ParseException
    {
      OSD_Importer importer = new OSD_Importer();
      final String test_date = "20 Nov 2019 - 11:22:33.000";
      Date date = importer.getDate(test_date);

      DateFormat df = new GMTDateFormat(CSV_DATE_FORMAT);
      assertEquals("matching date", test_date, df.format(date));
    }

    public void testOSD_Short() throws ParseException
    {
      Logger logger = new Logger();
      List<String> tokens = new ArrayList<String>();
      tokens.add("20 Nov 2019 - 11:22:33.000");
      tokens.add("blah");
      tokens.add("rhah");
      tokens.add("attr_courseOverTheGround");
      tokens.add("" + Math.PI);
      tokens.add("attr_depth");
      tokens.add("" + 22d);
      tokens.add("attr_speedOverTheGround");
      tokens.add("1.5");
      tokens.add("bahh");

      OSD_Importer importer = new OSD_Importer();
      FixWrapper res = importer.process(tokens.iterator(), logger);
      assertNull("should not have created fix", res);
      assertFalse("should have thrown warning", logger.isEmpty());
      assertEquals("valid message",
          "Missing fields:attr_longitude, attr_latitude", logger.messages.get(
              0));
    }

    public void testSystem() throws ParseException
    {
      ErrorLogger logger = new Logger();
      List<String> tokens = new ArrayList<String>();
      tokens.add("20 Nov 2019 - 11:22:33.000");
      tokens.add("blah");
      tokens.add("rhah");
      tokens.add("blah");
      tokens.add("123456789012345");

      tokens.add("attr_bearing");
      tokens.add("" + Math.PI);
      tokens.add("attr_longitude");
      tokens.add("" + Math.PI / 2);
      tokens.add("attr_latitude");
      tokens.add("" + Math.PI / 4);
      tokens.add("attr_countryAbbreviation");
      tokens.add("GBR");
      tokens.add("attr_speedOverTheGround");
      tokens.add("1.5");
      tokens.add("bahh");
      tokens.add("attr_course");
      tokens.add("" + Math.PI / 8);
      tokens.add("attr_speed");
      tokens.add("33");
      tokens.add("attr_trackNumber");
      tokens.add("1234");

      State_Importer importer = new State_Importer();
      FixWrapper res = importer.process(tokens.iterator(), logger);
      assertNotNull("should have fix", res);
      assertEquals("lat", 45d, res.getFixLocation().getLat());
      assertEquals("long", 90d, res.getFixLocation().getLong());
      assertEquals("dep", 0d, res.getFixLocation().getDepth());
      assertEquals("crse", 22.5d, res.getCourseDegs());
      assertEquals("speed", 33d, new WorldSpeed(res.getSpeed(), WorldSpeed.Kts)
          .getValueIn(WorldSpeed.M_sec), 0.0001);
      assertEquals("country", "789012345_1234_GBR", res.getComment());
    }

    public void testSystemOwnship() throws ParseException
    {
      ErrorLogger logger = new Logger();
      List<String> tokens = new ArrayList<String>();
      tokens.add("20 Nov 2019 - 11:22:33.000");
      tokens.add("blah");
      tokens.add("rhah");
      tokens.add("blah");
      tokens.add("123456789012345");

      tokens.add("attr_bearing");
      tokens.add("" + Math.PI);
      tokens.add("attr_longitude");
      tokens.add("" + Math.PI / 2);
      tokens.add("attr_latitude");
      tokens.add("" + Math.PI / 4);
      tokens.add("attr_countryAbbreviation");
      tokens.add("GBR");
      tokens.add("attr_speedOverTheGround");
      tokens.add("1.5");
      tokens.add("bahh");
      tokens.add("attr_course");
      tokens.add("" + Math.PI / 8);
      tokens.add("attr_speed");
      tokens.add("33");
      tokens.add("attr_trackNumber");
      tokens.add("1");

      State_Importer importer = new State_Importer();
      FixWrapper res = importer.process(tokens.iterator(), logger);
      assertNotNull("should have fix", res);
      assertEquals("lat", 45d, res.getFixLocation().getLat());
      assertEquals("long", 90d, res.getFixLocation().getLong());
      assertEquals("dep", 0d, res.getFixLocation().getDepth());
      assertEquals("crse", 22.5d, res.getCourseDegs());
      assertEquals("speed", 33d, new WorldSpeed(res.getSpeed(), WorldSpeed.Kts)
          .getValueIn(WorldSpeed.M_sec), 0.0001);
      assertEquals("country", "1", res.getComment());
    }

    public void testSystem_no_country() throws ParseException
    {
      ErrorLogger logger = new Logger();
      List<String> tokens = new ArrayList<String>();
      tokens.add("20 Nov 2019 - 11:22:33.000");
      tokens.add("blah");
      tokens.add("rhah");
      tokens.add("blah");
      tokens.add("123456789012345");
      tokens.add("attr_bearing");
      tokens.add("" + Math.PI);
      tokens.add("attr_longitude");
      tokens.add("" + Math.PI / 2);
      tokens.add("attr_latitude");
      tokens.add("" + Math.PI / 4);
      tokens.add("attr_speedOverTheGround");
      tokens.add("1.5");
      tokens.add("bahh");
      tokens.add("attr_course");
      tokens.add("" + Math.PI / 8);
      tokens.add("attr_speed");
      tokens.add("33");
      tokens.add("attr_trackNumber");
      tokens.add("3550");

      State_Importer importer = new State_Importer();
      FixWrapper res = importer.process(tokens.iterator(), logger);
      assertNotNull("should have fix", res);
      assertEquals("lat", 45d, res.getFixLocation().getLat());
      assertEquals("long", 90d, res.getFixLocation().getLong());
      assertEquals("dep", 0d, res.getFixLocation().getDepth());
      assertEquals("crse", 22.5d, res.getCourseDegs());
      assertEquals("speed", 33d, new WorldSpeed(res.getSpeed(), WorldSpeed.Kts)
          .getValueIn(WorldSpeed.M_sec), 0.0001);
      assertEquals("country", "789012345_3550", res.getComment());
    }

    public void testSystem_short() throws ParseException
    {
      Logger logger = new Logger();
      List<String> tokens = new ArrayList<String>();
      tokens.add("20 Nov 2019 - 11:22:33.000");
      tokens.add("blah");
      tokens.add("rhah");
      tokens.add("attr_speedOverTheGround");
      tokens.add("1.5");
      tokens.add("bahh");
      tokens.add("attr_course");
      tokens.add("" + Math.PI / 8);
      tokens.add("attr_speed");
      tokens.add("33");
      tokens.add("attr_trackNumber");
      tokens.add("11000323223550");

      State_Importer importer = new State_Importer();
      FixWrapper res = importer.process(tokens.iterator(), logger);
      assertNull("should not have created fix", res);
      assertFalse("should have thrown warning", logger.isEmpty());
      assertEquals("valid message",
          "Missing fields:attr_bearing, attr_countryAbbreviation, attr_latitude, attr_longitude",
          logger.messages.get(0));
    }

    public void testSensor_Short() throws ParseException
    {
      Logger logger = new Logger();
      List<String> tokens = new ArrayList<String>();
      tokens.add("20 Nov 2019 - 11:22:33.000");
      tokens.add("blah");
      tokens.add("rhah");
      tokens.add("blah");
      tokens.add("123456789012345");
      tokens.add("attr_longitude");
      tokens.add("" + Math.PI / 2);
      tokens.add("attr_trackNumber");
      tokens.add("2000");
      tokens.add("attr_depth");
      tokens.add("" + 22d);
      tokens.add("attr_speedOverTheGround");
      tokens.add("1.5");
      tokens.add("bahh");

      Sensor_Importer importer = new Sensor_Importer();
      SensorContactWrapper res = importer.process(tokens.iterator(), logger);
      assertNull("should not have created fix", res);
      assertFalse("should have thrown warning", logger.isEmpty());
      assertEquals("valid message", "Missing fields:attr_bearing",
          logger.messages.get(0));
    }

    public void testSensor() throws ParseException
    {
      ErrorLogger logger = new Logger();
      List<String> tokens = new ArrayList<String>();
      tokens.add("20 Nov 2019 - 11:22:33.000");
      tokens.add("blah");
      tokens.add("rhah");
      tokens.add("blah");
      tokens.add("123456789012345");
      tokens.add("attr_bearing");
      tokens.add("" + Math.PI);
      tokens.add("attr_longitude");
      tokens.add("" + Math.PI / 2);
      tokens.add("attr_trackNumber");
      tokens.add("2000");
      tokens.add("attr_depth");
      tokens.add("" + 22d);
      tokens.add("attr_speedOverTheGround");
      tokens.add("1.5");
      tokens.add("bahh");

      Sensor_Importer importer = new Sensor_Importer();
      SensorContactWrapper res = importer.process(tokens.iterator(), logger);
      assertNotNull("should have fix", res);
      assertEquals("DTG", "Wed Nov 20 11:22:33 GMT 2019", res.getDTG().getDate()
          .toString());
      assertEquals("bearing", 180d, res.getBearing());
      assertEquals("sensor", "789012345_2000", res.getSensorName());
    }

    public void testOSD() throws ParseException
    {
      ErrorLogger logger = new Logger();
      List<String> tokens = new ArrayList<String>();
      tokens.add("20 Nov 2019 - 11:22:33.000");
      tokens.add("blah");
      tokens.add("rhah");
      tokens.add("attr_courseOverTheGround");
      tokens.add("" + Math.PI);
      tokens.add("attr_longitude");
      tokens.add("" + Math.PI / 2);
      tokens.add("attr_latitude");
      tokens.add("" + Math.PI / 4);
      tokens.add("attr_depth");
      tokens.add("" + 22d);
      tokens.add("attr_speedOverTheGround");
      tokens.add("1.5");
      tokens.add("bahh");

      OSD_Importer importer = new OSD_Importer();
      FixWrapper res = importer.process(tokens.iterator(), logger);
      assertNotNull("should have fix", res);
      assertEquals("lat", 45d, res.getFixLocation().getLat());
      assertEquals("long", 90d, res.getFixLocation().getLong());
      assertEquals("dep", 22d, res.getFixLocation().getDepth());
      assertEquals("crse", 180d, res.getCourseDegs());
      assertEquals("speed", 1.5d, new WorldSpeed(res.getSpeed(), WorldSpeed.Kts)
          .getValueIn(WorldSpeed.M_sec), 0.0001);
    }
  }

  /**
   * keep track of how many tracks we've created, so we can generate unique colors
   */
  private static int colorCounter = 0;

  protected static interface CSV_Importer
  {
    void doImport(Layers theLayers, List<CSVRecord> records,
        final String hostName);
  }

  private abstract static class ImporterType implements CSV_Importer
  {

    private final GMTDateFormat _formatter = new GMTDateFormat(CSV_DATE_FORMAT);

    /**
     * map marker for the contents of the 5th column (when relevant)
     * 
     */
    protected static final String SYSTEM_ID = "SYSTEM_ID";

    protected Date getDate(final String dateStr) throws ParseException
    {
      // 20 Nov 2019 - 11:22:33.000
      Date date = _formatter.parse(dateStr);
      return date;
    }

    protected String trimmedTrackNum(String trackStr)
    {
      return trackStr.substring(6);
    }

    protected HiResDate getHiResDate(final String dateStr) throws ParseException
    {
      return new HiResDate(getDate(dateStr).getTime());
    }

    abstract public List<String> getMyFields();

    protected Map<String, String> getTokens(Iterator<String> tokens,
        List<String> myFields)
    {
      // we know each importer has already run once
      int ctr = 1;

      Map<String, String> map = new HashMap<String, String>();

      String pendingToken = null;

      while (tokens.hasNext())
      {
        final String token = tokens.next().trim();

        // is this the fifth column?
        if (++ctr == 5)
        {
          map.put(SYSTEM_ID, token);
        }

        if (pendingToken != null)
        {
          // ok, extract the next value
          map.put(pendingToken, token);
          pendingToken = null;
        }
        else
        {
          // is this one of ours?
          for (String thisT : myFields)
          {
            if (thisT.equals(token))
            {
              pendingToken = thisT;
              break;
            }
          }
        }
      }
      return map;
    }

    protected Double parseThis(String value)
    {
      final double res;
      if (value == null || value.length() == 0)
      {
        res = 0d;
      }
      else
      {
        res = Double.parseDouble(value);
      }
      return res;
    }
  }

  private static class OSD_Importer extends ImporterType implements CSV_Importer
  {
    private static final String SPEED = "attr_speedOverTheGround";
    private static final String DEPTH = "attr_depth";
    private static final String LAT = "attr_latitude";
    private static final String LONG = "attr_longitude";
    private static final String COURSE = "attr_courseOverTheGround";

    public FixWrapper process(Iterator<String> tokens, ErrorLogger logger)
        throws ParseException
    {
      String dateStr = tokens.next();
      HiResDate date = getHiResDate(dateStr);
      final List<String> myFields = getMyFields();

      Map<String, String> map = getTokens(tokens, myFields);

      final FixWrapper res;
      if (map.size() >= myFields.size())
      {
        // create fix
        WorldLocation loc = new WorldLocation(Math.toDegrees(parseThis(map.get(
            LAT))), Math.toDegrees(parseThis(map.get(LONG))), parseThis(map.get(
                DEPTH)));
        double speed = new WorldSpeed(parseThis(map.get(SPEED)),
            WorldSpeed.M_sec).getValueIn(WorldSpeed.ft_sec) / 3d;
        Fix fix = new Fix(date, loc, parseThis(map.get(COURSE)), speed);
        res = new FixWrapper(fix);
      }
      else
      {
        String missingFields = "";
        for (String field : myFields)
        {
          if (!map.keySet().contains(field))
          {
            if (missingFields.length() == 0)
              missingFields += field;
            else
              missingFields += ", " + field;
          }
        }

        // ok, insufficient tokens, throw wobbly?
        logger.logStack(ErrorLogger.WARNING, "Missing fields:" + missingFields);
        res = null;
      }

      return res;
    }

    public List<String> getMyFields()
    {
      final List<String> myTokens = new ArrayList<String>();
      myTokens.add(COURSE);
      myTokens.add(LONG);
      myTokens.add(LAT);
      myTokens.add(DEPTH);
      myTokens.add(SPEED);
      return myTokens;
    }

    @Override
    public void doImport(Layers theLayers, List<CSVRecord> records,
        final String hostName)
    {
      ErrorLogger logger = new LoggingService();
      TrackWrapper track = null;
      for (CSVRecord record : records)
      {
        // ok, get the date
        try
        {
          FixWrapper nextFix = process(record.iterator(), logger);

          if (nextFix != null)
          {
            if (track == null)
            {
              track = trackFor(theLayers, hostName);
            }
            track.addFix(nextFix);
          }
        }
        catch (ParseException e)
        {
          logger.logError(ErrorLogger.ERROR,
              "Failed to import OSD data from C-Log CSV", e);
        }
      }
    }
  }

  private static class Sensor_Importer extends ImporterType implements
      CSV_Importer
  {

    private static final String BEARING = "attr_bearing";
    private static final String TRACK_ID = "attr_trackNumber";

    public SensorContactWrapper process(Iterator<String> tokens,
        ErrorLogger logger) throws ParseException
    {
      String dateStr = tokens.next();
      HiResDate date = getHiResDate(dateStr);
      final List<String> myFields = getMyFields();

      Map<String, String> map = getTokens(tokens, myFields);

      final SensorContactWrapper res;
      if (map.size() >= myFields.size() + 1)
      {
        Double bearingDegs = Math.toDegrees(parseThis(map.get(BEARING)));

        final String TRACK_NAME = trimmedTrackNum(map.get(SYSTEM_ID)) + "_"
            + map.get(TRACK_ID);

        // create sensor
        res = new SensorContactWrapper("PENDING", date, null, bearingDegs, null,
            null, null, LineStylePropertyEditor.SOLID, TRACK_NAME);
      }
      else
      {
        String missingFields = "";
        for (String field : myFields)
        {
          if (!map.keySet().contains(field))
          {
            if (missingFields.length() == 0)
              missingFields += field;
            else
              missingFields += ", " + field;
          }
        }

        // ok, insufficient tokens, throw wobbly?
        logger.logStack(ErrorLogger.WARNING, "Missing fields:" + missingFields);
        res = null;
      }

      return res;
    }

    public List<String> getMyFields()
    {
      final List<String> myTokens = new ArrayList<String>();
      myTokens.add(BEARING);
      myTokens.add(TRACK_ID);
      return myTokens;
    }

    private Map<String, SensorWrapper> getSensors(TrackWrapper parent)
    {
      Enumeration<Editable> sensors = parent.getSensors().elements();
      Map<String, SensorWrapper> res = new HashMap<String, SensorWrapper>();
      while (sensors.hasMoreElements())
      {
        SensorWrapper next = (SensorWrapper) sensors.nextElement();
        res.put(next.getName(), next);
      }
      return res;
    }

    @Override
    public void doImport(Layers theLayers, List<CSVRecord> records,
        final String hostName)
    {
      ErrorLogger logger = new LoggingService();

      // get the host track
      Layer host = theLayers.findLayer(hostName);
      if (host == null || !(host instanceof TrackWrapper))
      {
        LoggingService.INSTANCE().logStack(LoggingService.ERROR,
            "Can't find host track:" + hostName);
      }
      else
      {

        TrackWrapper track = (TrackWrapper) host;
        Map<String, SensorWrapper> map = getSensors(track);
        for (CSVRecord record : records)
        {
          // ok, get the date
          try
          {
            SensorContactWrapper nextFix = process(record.iterator(), logger);

            String sensorId = nextFix.getSensorName();

            SensorWrapper sensor = map.get(sensorId);
            if (sensor == null)
            {
              sensor = new SensorWrapper(sensorId);
              track.add(sensor);
              map.put(sensorId, sensor);
            }

            sensor.add(nextFix);
          }
          catch (ParseException e)
          {
            logger.logError(ErrorLogger.ERROR,
                "Failed to import OSD data from C-Log CSV", e);
          }
        }
      }
    }
  }

  private static class State_Importer extends ImporterType implements
      CSV_Importer
  {
    private static final String bearing = "attr_bearing";
    private static final String country = "attr_countryAbbreviation";
    private static final String course = "attr_course";
    private static final String latitude = "attr_latitude";
    private static final String longitude = "attr_longitude";
    private static final String speed = "attr_speed";
    private static final String trackNum = "attr_trackNumber";

    private static final String MY_TRACK_ID = "1";

    public List<String> getMyFields()
    {
      final List<String> myTokens = new ArrayList<String>();
      myTokens.add(bearing);
      myTokens.add(country);
      myTokens.add(course);
      myTokens.add(latitude);
      myTokens.add(longitude);
      myTokens.add(speed);
      myTokens.add(trackNum);
      return myTokens;
    }

    public FixWrapper process(Iterator<String> tokens, ErrorLogger logger)
        throws ParseException
    {

      String dateStr = tokens.next();
      HiResDate date = getHiResDate(dateStr);
      final List<String> myFields = getMyFields();

      Map<String, String> map = getTokens(tokens, myFields);

      final FixWrapper res;
      // note: we allow for missing country field
      if (map.size() >= myFields.size() - 1)
      {
        // create fix
        WorldLocation loc = new WorldLocation(Math.toDegrees(parseThis(map.get(
            latitude))), Math.toDegrees(parseThis(map.get(longitude))), 0d);
        double speedVal = new WorldSpeed(parseThis(map.get(speed)),
            WorldSpeed.M_sec).getValueIn(WorldSpeed.ft_sec) / 3d;
        Fix fix = new Fix(date, loc, parseThis(map.get(course)), speedVal);
        res = new FixWrapper(fix);

        // store the track name
        final String trackName;
        final String trackStr = map.get(trackNum);
        if (trackStr != null && trackStr == MY_TRACK_ID)
        {
          trackName = MY_TRACK_ID;
        }
        else
        {
          final String systemID = map.get(SYSTEM_ID);
          final String countryStr = map.get(country);
          final String trimmedTrackStr = trimmedTrackNum(systemID);
          String workingName = trimmedTrackStr + "_" + trackStr;
          if (countryStr != null)
          {
            workingName += "_" + countryStr;
          }
          trackName = workingName;
        }

        res.setComment(trackName);
      }
      else
      {
        String missingFields = "";
        for (String field : myFields)
        {
          if (!map.keySet().contains(field))
          {
            if (missingFields.length() == 0)
              missingFields += field;
            else
              missingFields += ", " + field;
          }
        }

        // ok, insufficient tokens, throw wobbly?
        logger.logStack(ErrorLogger.WARNING, "Missing fields:" + missingFields);
        res = null;
      }

      return res;
    }

    @Override
    public void doImport(Layers theLayers, List<CSVRecord> records,
        final String hostName)
    {
      ErrorLogger logger = new LoggingService();
      for (CSVRecord record : records)
      {
        // ok, get the date
        try
        {
          FixWrapper nextFix = process(record.iterator(), logger);

          if (nextFix != null)
          {
            String trackId = nextFix.getComment();
            final String trackName;
            if (trackId.equals(MY_TRACK_ID))
            {
              trackName = hostName;
            }
            else
            {
              trackName = trackId;
            }
            TrackWrapper track = trackFor(theLayers, trackName);
            track.addFix(nextFix);
          }
        }
        catch (ParseException e)
        {
          logger.logError(ErrorLogger.ERROR,
              "Failed to import OSD data from C-Log CSV", e);
        }
      }
    }
  }

  private static CSV_Importer importerFor(final String filename)
  {
    return new OSD_Importer();
  }

  private static String trackFor(final String fileName)
  {
    int index = fileName.indexOf("_");
    return fileName.substring(0, index);
  }

  private static void doImport(final Layers theLayers,
      final InputStream inputStream, final String fileName)
  {
    final String trackName = trackFor(fileName);

    // find out which type it is
    CSV_Importer importer = importerFor(fileName);

    try
    {
      // get the file as a string
      final String contents = inputStreamAsString(inputStream);

      // pass it through the parser
      final List<CSVRecord> records = CSVParser.parse(contents, CSVFormat.EXCEL)
          .getRecords();

      // go for it
      importer.doImport(theLayers, records, trackName);
    }
    catch (IOException e)
    {
      LoggingService.INSTANCE().logError(LoggingService.ERROR,
          "Failed while importing CSV file:" + fileName, e);
    }

  }

  public static void doZipImport(final Layers theLayers,
      final InputStream inputStream, final String fileName)
  {
    try (GZIPInputStream zis = new GZIPInputStream(inputStream))
    {

      final ByteArrayOutputStream bos = new ByteArrayOutputStream();
      final BufferedOutputStream fout = new BufferedOutputStream(bos);
      for (int c = zis.read(); c != -1; c = zis.read())
      {
        fout.write(c);
      }
      zis.close();
      fout.close();
      bos.close();

      // now create a byte input stream from the byte output stream
      final ByteArrayInputStream bis = new ByteArrayInputStream(bos
          .toByteArray());

      // and create it
      doImport(theLayers, bis, fileName);
    }
    catch (final IOException e)
    {
      e.printStackTrace();
    }

  }

  private static String inputStreamAsString(final InputStream stream)
      throws IOException
  {
    final InputStreamReader isr = new InputStreamReader(stream);
    final BufferedReader br = new BufferedReader(isr);
    final StringBuilder sb = new StringBuilder();
    String line = null;

    while ((line = br.readLine()) != null)
    {
      sb.append(line + "\n");
    }

    br.close();
    return sb.toString();
  }
}
