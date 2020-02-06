
package MWC.TacticalData;

import MWC.GenericData.WatchableList;

/**
 * @author ian.mayo
 */
public interface TrackDataProvider
{
  public static interface TrackDataListener
  {
    /**
     * find out that the primary has changed
     * 
     * @param primary         the primary track
     */
    public void tracksUpdated(WatchableList primary, WatchableList[] secondaries);
  }
  
  /** people who want to find out about tracks moving
   * 
   * @author ian.mayo
   *
   */
  public static interface TrackShiftListener
  {
    /** 
     * 
     */
    public void trackShifted(WatchableList target);
  }

  /**
   * declare that we want to be informed about changes in selected tracks
   */
  public void addTrackDataListener(TrackDataListener listener);

  /**
   * forget that somebody wants to know about track changes
   */
  public void removeTrackDataListener(TrackDataListener listener);
  
  /**
   * declare that we want to be informed about changes in selected tracks
   */
  public void addTrackShiftListener(TrackShiftListener listener);

  /**
   * forget that somebody wants to know about track changes
   */
  public void removeTrackShiftListener(TrackShiftListener listener);
  
  /** ok - tell anybody that wants to know about our movement
   * 
   * @param watchableList what's being dragged
   */
  public void fireTrackShift(final WatchableList watchableList);
  
  /** ok, the tracks have changed. tell the world
   * 
   */
  public void fireTracksChanged();
  

  /**
   * find out what the primary track is
   */
  public WatchableList getPrimaryTrack();

  /**
   * find out what the secondary track is
   */
  public WatchableList[] getSecondaryTracks();

  /** add this as another secondary track
   * 
   * @param secondary additional secondary
   */
  public void addSecondary(WatchableList secondary);

  /** set the primary track
   * 
   * @param primary the primary track
   */
  public void setPrimary(WatchableList primary);

  /** set this as the sole secondary track
   * 
   * @param secondary the sole secondary track
   */
  public void setSecondary(WatchableList secondary);

}
