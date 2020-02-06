

package ASSET;


import ASSET.Server.*;

/**
 * Interface representing ServerApp objects.
 * A server manages a collection of scenarios, proving external objects a list of them together with their details.
 * Also supports creation of new scenarios.
 */
public interface ServerType {


  /****************************************************
   * member constants
   ***************************************************/
  /** id of an invalid scenario
   *
   */
  public final static int INVALID_SCENARIO = -1;

   /** add object as a listener for this type of event
    *
    */
   public void addScenarioCreatedListener(ScenarioCreatedListener listener);

   /** remove listener for this type of event
    *
    */
   public void removeScenarioCreatedListener(ScenarioCreatedListener listener);

   /**
    * Return a particular scenario - so that the scenario can be controlled directly.  Listeners added/removed. Participants added/removed, etc.
    */
   ScenarioType getThisScenario(int id);

   /**
    * Provide a list of id numbers of scenarios we contain
    * @return list of ids of scenarios we contain
    */
   public Integer[] getListOfScenarios();

   /**
    * Create a new scenario.  The external client can then request the scenario itself to perform any edits
    * @param scenario_type the type of scenario the client wants
    * @return the id of the new scenario
    */
   int createNewScenario(String scenario_type);

   /** destroy a scenario (calls the close() method on the scenario, which triggers the close)
    *
    */
  public void closeScenario(int index);

}
