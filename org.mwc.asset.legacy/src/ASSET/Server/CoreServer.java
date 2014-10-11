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

package ASSET.Server;

import ASSET.ScenarioType;
import ASSET.ServerType;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

/**
 * The real implementation of a server
 */
public class CoreServer implements ServerType
{

  ////////////////////////////////////////////////////////////////////////
  // member objects
  ////////////////////////////////////////////////////////////////////////

  /**
   */
  private HashMap<Integer, ScenarioType> _myScenarios;

  /**
   * our list of listeners
   */
  private Vector<ScenarioCreatedListener> _myCreatedListeners;

  ////////////////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////////////////

  ////////////////////////////////////////////////////////////////////////
  // methods
  ////////////////////////////////////////////////////////////////////////

  /**
   * add object as a listener for this type of event
   */
  public void addScenarioCreatedListener(final ScenarioCreatedListener listener)
  {
    // check we have the list
    if (_myCreatedListeners == null)
      _myCreatedListeners = new Vector<ScenarioCreatedListener>(0, 1);

    // add this listener
    _myCreatedListeners.add(listener);
  }

  /**
   * remove listener for this type of event
   */
  public void removeScenarioCreatedListener(final ScenarioCreatedListener listener)
  {
    _myCreatedListeners.remove(listener);
  }

  /**
   * fire the scenario created/destroyed event
   */
  private void fireCreatedDestroyed(final boolean created, final int index)
  {
    if (_myCreatedListeners == null)
      return;

    final Iterator<ScenarioCreatedListener> it = _myCreatedListeners.iterator();
    while (it.hasNext())
    {
      final ScenarioCreatedListener list = (ScenarioCreatedListener) it.next();
      if (created)
        list.scenarioCreated(index);
      else
        list.scenarioDestroyed(index);
    }
  }

  /**
   * Return a particular scenario - so that the scenario can be controlled directly.  Listeners added/removed. Participants added/removed, etc.
   */
  public ScenarioType getThisScenario(final int id)
  {
    ScenarioType res = null;
    if (_myScenarios != null)
      res = (ScenarioType) _myScenarios.get(new Integer(id));

    return res;
  }

  /**
   * Provide a list of id numbers of scenarios we contain
   *
   * @return list of ids of scenarios we contain
   */
  public Integer[] getListOfScenarios()
  {
    Integer[] res = new Integer[0];

    if (_myScenarios != null)
    {
      final java.util.Collection<Integer> vals = _myScenarios.keySet();
      res = vals.toArray(res);
    }

    return res;
  }

  /**
   * destroy a scenario (calls the close() method on the scenario, which triggers the close)
   */
  public void closeScenario(final int index)
  {
    // check we have the scenario
    final ScenarioType sc = (ScenarioType) _myScenarios.get(new Integer(index));

    if (sc != null)
    {
      sc.close();
      _myScenarios.remove(new Integer(index));
    }

    // fire the destroyed event
    fireCreatedDestroyed(false, index);

  }

  /**
   * Create a new scenario.  The external client can then request the scenario itself to perform any edits
   *
   * @param scenario_type the type of scenario the client wants
   * @return the id of the new scenario
   */
  public int createNewScenario(final String scenario_type)
  {
    // what we should do, is to create the scenario from the scenario type name,
    // using the specified scenario_type in the classloader

    ScenarioType newS = null;

    // check out which type we want
    if (scenario_type.equals(ASSET.Scenario.MultiForceScenario.TYPE))
    {
      newS = new ASSET.Scenario.MultiForceScenario();
    }
    else
    {
      newS = new ASSET.Scenario.CoreScenario();
    }


    if (_myScenarios == null)
      _myScenarios = new java.util.HashMap<Integer, ScenarioType>();

    // get the id
    final int id = getId();

    // store it in our list
    _myScenarios.put(new Integer(id), newS);

    // fire new scenario event
    fireCreatedDestroyed(true, id);

    // return it's index
    return id;
  }

  /**
   * create new, unique id for this participant
   */
  private static int getId()
  {
    return ASSET.Util.IdNumber.generateInt();
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////
  // testing for this class
  //////////////////////////////////////////////////////////////////////////////////////////////////
  public static class ServerTest extends junit.framework.TestCase
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";
    protected int createdCounter = 0;
    protected  int destroyedCounter = 0;

    public ServerTest(final String val)
    {
      super(val);
    }

    protected class createList implements ScenarioCreatedListener
    {
      public void scenarioCreated(int index)
      {
        createdCounter++;
      }

      public void scenarioDestroyed(int index)
      {
        destroyedCounter++;
      }

    }

    public void testScenarios()
    {
      // create server
      final ServerType srv = new CoreServer();

      // add as listener
      final createList cl = new createList();
      srv.addScenarioCreatedListener(cl);

      // create new scenarios
      int s2 = srv.createNewScenario("scrap A");
      s2 = srv.createNewScenario("scrap");

      // now stop listening for these events
      srv.removeScenarioCreatedListener(cl);

      // add another, which we shouldn't hear about
      final int s3 = srv.createNewScenario("scrap");

      // check events got fired
      assertEquals("count of created events (one ignored)", createdCounter, 2);
      assertEquals("count of destroyed events", destroyedCounter, 0);

      // get list of scenarios
      final Integer[] res = srv.getListOfScenarios();

      assertEquals("Number of scenarios", res.length, 3);

      // get specific scenarios
      final ScenarioType sc2 = srv.getThisScenario(s2);
      assertTrue("scenario returned", sc2 != null);

      if(sc2 == null)
      	return;
      
      // make edits
      sc2.setName("scenario 2");
      sc2.setStepTime(1000);
      final ScenarioType sc3 = srv.getThisScenario(s3);
      sc3.setName("scenario 3");

      // re-retrieve this scenario to check we're getting the correct one
      final ScenarioType sc2a = srv.getThisScenario(s2);
      assertEquals("correct name", sc2a.getName(), "scenario 2");
      assertEquals("correct step", sc2a.getStepTime(), 1000);

      // check invalid scenario indices
      ScenarioType dd = srv.getThisScenario(1000);
      assertEquals("invalid index", dd, null);
      dd = srv.getThisScenario(0);
      assertEquals("invalid index", dd, null);

      // listen to the events again
      srv.addScenarioCreatedListener(cl);

      // try to destroy a scenario (valid)
      srv.closeScenario(s2);

      assertEquals("after destruction", destroyedCounter, 1);

      // is it still there?
      dd = srv.getThisScenario(s2);
      assertEquals("destroyed scenario", null, dd);

      dd = srv.getThisScenario(0);
      assertEquals("destroyed scenario", null, dd);

      dd = srv.getThisScenario(10000);
      assertEquals("destroyed scenario", null, dd);

    }
  }

  public static void main(String[] args)
  {
    final ServerTest ts = new ServerTest("trial");
    ts.testScenarios();
  }

}
