
// $RCSfile: ImportPCArgos.java,v $
// @author $Author: ian.mayo $
// @version $Revision: 1.5 $
// $Log: ImportPCArgos.java,v $
// Revision 1.5  2007/06/01 13:46:07  ian.mayo
// Improve performance of export text to clipboard
//
// Revision 1.4  2006/05/24 15:01:28  Ian.Mayo
// Reflect change in exportThis method
//
// Revision 1.3  2005/12/13 09:04:32  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.2  2004/11/25 10:24:12  Ian.Mayo
// Switch to Hi Res dates
//
// Revision 1.1.1.2  2003/07/21 14:47:39  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.4  2003-06-16 11:49:41+01  ian_mayo
// Output completed message which was failing ANT built
//
// Revision 1.3  2003-03-19 15:37:40+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.2  2002-05-28 12:28:07+01  ian_mayo
// after update
//
// Revision 1.1  2002-05-28 09:12:14+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:29:44+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:41:34+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:40:47+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 20:46:44  ianmayo
// initial import of files
//
// Revision 1.4  2000-11-17 09:10:21+00  ian_mayo
// change parent hierarchy
//
// Revision 1.3  2000-11-03 12:07:12+00  ian_mayo
// reflect change in status of TrackWrapper from plottable to Layer
//
// Revision 1.2  2000-10-09 13:37:38+01  ian_mayo
// Switch stackTrace to go to file
//
// Revision 1.1  2000-06-05 14:20:32+01  ian_mayo
// Initial revision
//
package Debrief.ReaderWriter.PCArgos;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Hashtable;
import java.util.Vector;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Layers;
import MWC.GUI.PlainWrapper;
import MWC.GUI.Plottable;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;

/** class to read in a complete PCArgos file, producing vessel fixes
 */
final class ImportPCArgos extends MWC.Utilities.ReaderWriter.PlainImporterBase
{

  /** the list of importers we know about
   */
  private static ImportArgosFix _theImporter;

  /** the 'last' points loaded for each track (used to
   * calculate course and speed from last point, and the time of
   * next valid point).
   */
  private Hashtable<String, Fix> _lastPoints = null;

  /** the indexed list of colours we are using
   */
  private Vector<java.awt.Color> colors = null;

  // keep track of how many tracks have been read in (so that we can
  // set unique colours).
  private int track_counter = 0;

  /** the origin we are offsetting our data from
   */
  private WorldLocation _origin = null;

  /** the frequency at which to record data
   */
  private long _freq = 0;

  /** the offset to add to our data
   */
  private long _dtg = 0;


  /////////////////////////////////////////////////////////////
  // constructor
  /////////////////////////////////////////////////////////////

  /** constructor, initialise Vector with the list of non-Fix items
   * which we will be reading in
   */
  public ImportPCArgos()
  {

    _myTypes = new String[]{".rao"};

    checkImporters();

    // create a list of colours
    if(colors == null){
      colors = new Vector<java.awt.Color>(0,1);
      colors.addElement(Color.white);
      colors.addElement(Color.blue);
      colors.addElement(Color.green);
      colors.addElement(Color.red);
      colors.addElement(Color.yellow);
      colors.addElement(new Color(169,1,132));
      colors.addElement(Color.orange);
      colors.addElement(new Color(188,93,6));
      colors.addElement(Color.cyan);
      colors.addElement(new Color(100,240,100));
      colors.addElement(new Color(230,200,20));
      colors.addElement(Color.pink);
    }

  }

  /**
   * function to initialise the list of importers
   */
  private static void checkImporters()
  {
    _theImporter = new ImportArgosFix();
  }

  /** parse this line
   * @param theLine the line to parse
   */
  private String readLine(final String theLine){
    // is this line valid?
    if(theLine.length()>5){

      // now read it in.
      final Object thisObject = _theImporter.readThisLine(theLine);

      // check that a value has been returned
      if(thisObject != null)
      {

        // see if we are going to do any special processing
        if(thisObject instanceof Debrief.ReaderWriter.Replay.ReplayFix)
        {
          final Debrief.ReaderWriter.Replay.ReplayFix rf = (Debrief.ReaderWriter.Replay.ReplayFix)thisObject;



          // we've got our fix, see if we can calculate course and speed for it
          final String trkName = rf.theTrackName;
          final Object oj = _lastPoints.get(trkName);
          if(oj != null)
          {

            // calculate the course and speed
            final Fix oldFix = (Fix)oj;
            final Fix newFix = rf.theFix;
            final WorldVector separation = newFix.getLocation().subtract(oldFix.getLocation());
            final double course = separation.getBearing();
            final double rng = MWC.Algorithms.Conversions.Degs2Yds(separation.getRange());
            // sort out the time taken
            final long microsDelta = newFix.getTime().getMicros() - oldFix.getTime().getMicros();
            final double secsDelta = ((double)(microsDelta)) / 1000000.0;
            final double yardsPerSec = rng / secsDelta;
            newFix.setCourse(MWC.Algorithms.Conversions.clipRadians(course));
            newFix.setSpeed(yardsPerSec);

            // update the item in the list
            _lastPoints.remove(trkName);

          }

          // put the new fix into the list
          _lastPoints.put(trkName, rf.theFix);

          // wrap it
          final PlainWrapper thisWrapper = new FixWrapper(rf.theFix);

          // is there a layer for this track?
          TrackWrapper trkWrapper  = (TrackWrapper)getLayerFor( rf.theTrackName );

          if(trkWrapper == null){
            // now create the wrapper
            trkWrapper = new TrackWrapper();
            trkWrapper.setName(rf.theTrackName);

            // get a new colour for the track counter
            final Color thisCol = (Color)colors.elementAt(track_counter);

            // increment the counter
            track_counter ++;

            // but check it's within it's limit
            track_counter = track_counter % 6;

            // set the track colour
            trkWrapper.setColor(thisCol);

            // and the store the whole track
            addLayer(trkWrapper);

          }

          // ok, we've got the layer representing our track, add this wrapper to it
          addToLayer(thisWrapper, trkWrapper);

          // we also have to add the fix to it's track wrapper
          trkWrapper.addFix((FixWrapper)thisWrapper);

          // let's also tell the fix about it's track
          ((FixWrapper)thisWrapper).setTrackWrapper(trkWrapper);

        }   // whether it's a valid fix

      } // whether a valid fix got returned

    } // whether there are enough points on the line

    return null;
  }

  /** import data from this stream
   * @param fName the filename to use
   * @param is the input stream to use
   * @param origin the origin to offset x,y,z from
   * @param DTG the y/m/d to offset the data from
   * @param freq the minimum frequency to record data at.
   */
  public final void importThis(final Layers theData,
                         final String fName,
                         final java.io.InputStream is,
                         final WorldLocation origin,
                         final long DTG,
                         final long freq)
  {
    super.setLayers(theData);

    _origin = origin;
    _freq = freq;
    _dtg = DTG;


    // clear the list, note - we don't put stuff into it from here, we
    // insert/retrieve the points in the readLine method
    if(_lastPoints != null)
      _lastPoints.clear();
    _lastPoints = new Hashtable<String, Fix>();

    // initialise the importer
    _theImporter.setParameters(_origin, _dtg, _lastPoints, _freq);

    // and do the import
    importThis(fName, is);

  }

  /** import data from this stream
   */
  public final void importThis(final String fName,
                         final java.io.InputStream is){
    // declare linecounter
    int lineCounter = 0;

    final Reader reader = new InputStreamReader(is);
    final BufferedReader br = new BufferedReader(reader);
    String thisLine=null;

    // reset the working variables
    track_counter = 0;

    try
    {

      // check stream is valid
      if(is.available() > 0){

        thisLine = br.readLine();

        final long start = System.currentTimeMillis();

        // loop through the lines
        while(thisLine != null){

          // keep line counter
          lineCounter ++;

          // catch import problems
          readLine(thisLine);

          // read another line
          thisLine = br.readLine();
        }

        final long end = System.currentTimeMillis();
        System.out.print(" |Elapsed:" + (end - start) + " ");

      }
    }catch(final java.lang.NumberFormatException e){
       // produce the error message
       MWC.Utilities.Errors.Trace.trace(e);
       // show the message dialog
       super.readError(fName, lineCounter, "Number format error", thisLine);
    }catch(final IOException e){
       // produce the error message
       MWC.Utilities.Errors.Trace.trace(e);
       // show the message dialog
       super.readError(fName, lineCounter, "Unknown read error", thisLine);
    }catch(final java.util.NoSuchElementException e){
       // produce the error message
       MWC.Utilities.Errors.Trace.trace(e);
       // show the message dialog
       super.readError(fName, lineCounter, "Missing field error", thisLine);
    }
  }
  
  @Override
  public void importThis(String fName, InputStream is, Layers theData,
      MonitorProvider provider)
  {
    importThis(fName, is, theData);
  }
  
  @Override
  public void importThis(String fName, InputStream is, MonitorProvider provider)
  {
    importThis(fName, is);
  }

  /** produce
   */
  public final void exportThis(final Plottable item)
  {
  }


  public final boolean canImportThisFile(final String theFile)
  {
    boolean res = true;
    String theSuffix=null;
    final int pos = theFile.lastIndexOf(".");
    theSuffix = theFile.substring(pos, theFile.length());

    for(int i=0; i<_myTypes.length; i++)
    {
      if(theSuffix.equalsIgnoreCase(_myTypes[i]))
      {
        res = true;
        break;
      }
    }

    return res;
  }

  public final void exportThis(final String val)
  {
  }

}






















