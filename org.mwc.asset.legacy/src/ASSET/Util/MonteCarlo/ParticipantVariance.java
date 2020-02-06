
package ASSET.Util.MonteCarlo;

import java.text.DecimalFormat;
import java.text.ParseException;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ASSET.Util.MonteCarlo.XMLVariance.NamespaceContextProvider;
import ASSET.Util.XML.ParticipantsHandler;
import ASSET.Util.XML.Vessels.ParticipantHandler;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

/**
 * list of items in a particular file which may be changed
 */

public final class ParticipantVariance extends XMLVarianceList
{

	/**
	 * human=readable string used to name the part id
	 */
	private static final String NAME = "name";

	/**
	 * human=readable string used to name the part id
	 */
	private static final String NUMBER = "number";

	/**
	 * human=readable string used to name the parallel planes marker
	 */
	private static final String IN_PARALLEL_PLANES = "inParallelPlanes";

	/**
	 * human=readable string used to establish if random locations are to be used
	 */
	private static final String RANDOM_LOCATIONS = "inRandomLocations";

	/** human=readable string used to establish if participant
	 * course should be randomised
	 */
	private static final String RANDOM_COURSE = "RandomCourse";

	/**
	 * the identifier we represent
	 */
	private final String _myName;

	/**
	 * the path to find us
	 */
	private XPathExpression _myPath;

	/**
	 * whether the new participants should be handled with parrallel planes
	 * processing
	 */
	private boolean _inParallelPlanes = true;

	/**
	 * the number of permutations to create
	 */
	private final int _number;

	/**
	 * the area of coverage for participant generation
	 */
	private WorldArea _distributionArea = null;

	/**
	 * whether to randomly generate locations
	 * 
	 */
	private boolean _randomLocations = false;

	/** whether to randomise the initial courses
	 * 
	 */
	private Boolean _randomCourse = true;

	/**
	 * ************************************************************ constructor
	 * *************************************************************
	 */

	public ParticipantVariance(final Element myElement)
	{
		// read in the data from this element
		_myName = myElement.getAttribute(NAME);
		
		String randomCourseTxt = myElement.getAttribute(RANDOM_COURSE);
		if(randomCourseTxt != null)
			_randomCourse  = Boolean.valueOf(randomCourseTxt);

		// read in the data from this element
		_number = Integer.parseInt(myElement.getAttribute(NUMBER));

		// should this participant use parallel planes?
		String val = myElement.getAttribute(IN_PARALLEL_PLANES);
		_inParallelPlanes = val.equals("true");

		// now get out the inner bits
		try
		{
			String xPathExpression = getExpression(_myName);

			// tell it what schema to use for the indicated elements
			_myPath = NamespaceContextProvider.createPath(xPathExpression);

		}
		catch (Exception e)
		{
			e.printStackTrace(); // To change body of catch statement use Options |
			// File Templates.
		}

		// now continue, loading from the element
		this.loadFrom(myElement);

		// and lastly, try to load the participant locator (if there is one)
		_distributionArea = loadLocationDetails(myElement);

		// aah. lastly lastly, try for the random positions alg
		Boolean randLocs = loadLocationRandomizer(myElement);
		if (randLocs != null)
			setRandomLocations(randLocs.booleanValue());

	}

	public boolean isRandomLocations()
	{
		return _randomLocations;
	}

	public void setRandomLocations(boolean randomLocations)
	{
		_randomLocations = randomLocations;
	}

	/**
	 * ************************************************************ member methods
	 * *************************************************************
	 */

	public static String getExpression(final String name)
	{
		final String res = "//" + ParticipantsHandler.PARTICIPANTS + "/*[@"
				+ ParticipantHandler.NAME + " ='" + name + "']";

		return res;
	}

	private java.text.NumberFormat paddedInteger = new DecimalFormat("000");

	/**
	 * insert the new set of participants into this document
	 * 
	 * @param newDoc
	 *          the document tree to populate
	 */
	public final void populate(final Document newDoc)
			throws XMLVariance.IllegalExpressionException,
			XMLVariance.MatchingException
	{

		try
		{
			// ok - find ourselves in the document
			// find our object
			final Element ourObj = (Element) _myPath.evaluate(newDoc,
					XPathConstants.NODE);

			// did we find it?
			if (ourObj != null)
			{
				// get the (so that we can auto-gen new ones
				final String ourName = ourObj.getAttribute(ParticipantHandler.NAME);

				// get the parent - so that we can remove ourselves
				final Node parent = ourObj.getParentNode();

				// loop through our permutations
				for (int i = 0; i < _number; i++)
				{
					// generate a new copy
					final Element newObj = (Element) ourObj.cloneNode(true);

					// generate a new name
					final String newName = ourName + "_" + paddedInteger.format(i + 1);

					// and set the new name
					newObj.setAttribute(ParticipantHandler.NAME, newName);

					// also sort out the random course
					if(_randomCourse)
					{
						applyRandomCourse(newObj);
					}
					
					// check if we have a distribution area we want to put the objects
					// into
					if (_distributionArea != null)
					{
						// ok, go and do something, man.
						applyDistributionArea(newObj, i, _number, _distributionArea, newDoc);
					}

					// just check that it know's it's monte carlo participant
					String isMonte = newObj
							.getAttribute(ParticipantHandler.MONTE_CARLO_TARGET);

					if (isMonte == "")
					{
						newObj.setAttribute(ParticipantHandler.MONTE_CARLO_TARGET, ""
								+ _inParallelPlanes);
					}

					// append the new permutation
					parent.insertBefore(newObj, ourObj);

					// generate a new permutation
					final String thisExpression = getExpression(newName);
					super.apply(thisExpression, newDoc);

				}
				// lastly, remove the existing participant instance
				parent.removeChild(ourObj);

			}
		}
		catch (XPathExpressionException e)
		{
			throw new XMLVariance.MatchingException(_myPath.toString(), e);
		}

	}

	private void applyRandomCourse(Element newObj)
	{
		// retrieve the status
		NodeList theStatuses = newObj.getElementsByTagName("Status");

		if (theStatuses.getLength() > 0)
		{
			Element theStat = (Element) theStatuses.item(0);
			if(theStat != null)
			{
				// ok, set the course attribute
				theStat.setAttribute("Course","" + Math.random() * 360);
			}
		}
	}

	/**
	 * when we want to produce a distribution of targets we have to do some fancy
	 * stuff, and this is where we do it
	 * 
	 * @param newObj
	 *          the new cloned object
	 * @param counter
	 *          the counter we've got to
	 */
	private void applyDistributionArea(Element newObj, int counter, int total,
			WorldArea theArea, Document doc)
	{
		
		// ok, now generate the new coordinate
		WorldLocation thisLoc;

		// hmm, are we doing a random dist?
		if (_randomLocations)
		{
			thisLoc = theArea.getRandomLocation();
		}
		else
		{
			// ok, go and do something, man.
			thisLoc = theArea.getDistributedLocation(counter, total);
		}

		
		// ok, now we need to put this location into the object

		// retrieve the status
		NodeList theStatuses = newObj.getElementsByTagName("Status");

		if (theStatuses.getLength() > 0)
		{
			Element theStat = (Element) theStatuses.item(0);

			// ok, now create a location to insert into it
			Element theLocation = doc.createElement("Location");
			Element theShortLoc = doc.createElement("shortLocation");
			theLocation.insertBefore(theShortLoc, null);
			theShortLoc.setAttribute("Lat", "" + thisLoc.getLat());
			theShortLoc.setAttribute("Long", "" + thisLoc.getLong());

			// ok, remove any existing location stuff
			NodeList theLocations = newObj.getElementsByTagName("Location");

			if (theLocations.getLength() > 0)
			{
				Node theOldLocation = (Node) theLocations.item(0);

				// put the new location before the existing one
				theStat.insertBefore(theLocation, theOldLocation);

				// and remove it
				theStat.removeChild(theOldLocation);
			}

		}

	}

	private static final WorldArea loadLocationDetails(final Element myElement)
	{
		// now try to build up the list of vars
		// build up our list of variances from this document

		WorldArea theArea = null;

		NodeList lis = myElement.getElementsByTagName("ParticipantLocation");

		int len = lis.getLength();

		// do we have a participant location?
		if (len > 0)
		{
			// ok. we don't know if there's going to be short or long locations in the
			// element
			// let our clever little class do it
			try 
			{
				theArea = XMLVariance.readInAreaFromXML(myElement);
			} 
			catch (final ParseException e) 
			{
				MWC.Utilities.Errors.Trace.trace(e);
			}
		}

		return theArea;
	}

	private static final Boolean loadLocationRandomizer(final Element myElement)
	{
		Boolean res = null;
		NodeList lis = myElement.getElementsByTagName("ParticipantLocation");

		int len = lis.getLength();

		// do we have a participant location?
		if (len > 0)
		{
			Element partLoc = (Element) lis.item(0);
			if (partLoc != null)
			{

				// see if there;s a boolean random location element
				String doR = partLoc.getAttribute(RANDOM_LOCATIONS);
				if (doR != null)
				{
					if (doR.length() > 0)
					{
						Boolean val = Boolean.valueOf(doR);
						res = val;
					}
				}
			}
		}
		return res;
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	public static final class PartVarianceTest extends junit.framework.TestCase
	{

		public PartVarianceTest(final String val)
		{
			super(val);
		}

		public final void testGenerate()
		{

		}

	}

}
