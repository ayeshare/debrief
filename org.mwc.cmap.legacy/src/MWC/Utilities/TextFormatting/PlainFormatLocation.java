
// @Author : Ian Mayo
// @Project: Debrief 3
// @File   : PlainFormatLocation.java

package MWC.Utilities.TextFormatting;

import MWC.GenericData.WorldLocation;

/** class defining how to produce a text string from
 * a location
 */
public interface PlainFormatLocation {
  /** conveert
   */
  public String convertToString(WorldLocation theLocation);
  public String getExampleString();
}
