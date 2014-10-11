/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package MWC.Utilities.Timer;

/** The TimerListener interface must be implemented by
* a class that wants to be notified about time events.
*
* @version  1.00, Jul 20, 1998
*/
public interface TimerListener extends java.util.EventListener {

  /** Called when a new timer event occurs */
  public void onTime (java.awt.event.ActionEvent event);

}

