package com.planetmayo.debrief.satc.model.generator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.legs.AlteringLeg;
import com.planetmayo.debrief.satc.model.legs.CompositeRoute;
import com.planetmayo.debrief.satc.model.legs.CoreLeg;
import com.planetmayo.debrief.satc.model.legs.CoreRoute;
import com.planetmayo.debrief.satc.model.legs.LegType;
import com.planetmayo.debrief.satc.model.legs.StraightLeg;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;
import com.planetmayo.debrief.satc.util.GeoSupport;

public class SolutionGenerator implements  ISteppingListener,
		IBoundsManager.IShowGenerateSolutionsDiagnostics
{
	/**
	 * how many cells shall we break the polygons down into?
	 * 
	 */
	final int NUM_CELLS = 20;

	/**
	 * anybody interested in a new solution being ready?
	 * 
	 */
	private ArrayList<ISolutionsReadyListener> _readyListeners;

	/**
	 * level of diagnostics for user
	 * 
	 * @see IBoundsManager.IShowGenerateSolutionsDiagnostics
	 */
	private boolean _showPoints;

	/**
	 * level of diagnostics for user
	 * 
	 * @see IBoundsManager.IShowGenerateSolutionsDiagnostics
	 */
	private boolean _showAchievablePoints;

	/**
	 * level of diagnostics for user
	 * 
	 * @see IBoundsManager.IShowGenerateSolutionsDiagnostics
	 */
	private boolean _showRoutes;

	/**
	 * level of diagnostics for user
	 * 
	 * @see IBoundsManager.IShowGenerateSolutionsDiagnostics
	 */
	private boolean _showRoutesWithScores;

	public SolutionGenerator()
	{
		_readyListeners = new ArrayList<ISolutionsReadyListener>();
	}

	public void addReadyListener(ISolutionsReadyListener listener)
	{
		_readyListeners.add(listener);
	}

	@Override
	public void complete(final IBoundsManager boundsManager)
	{
		// ok - it's complete. now we can process it
		ProblemSpace space = boundsManager.getSpace();

		// get the legs
		ArrayList<CoreLeg> theLegs = getTheLegs(space.states());

		// get the legs to dice themselves up
		generateRoutes(theLegs);

		// get the legs to sort out what is achievable
		decideAchievable(theLegs);

		// do the fancy multiplication
		int[][] achievableRes = calculateAchievableRoutesFor(theLegs);

		// ditch the duff permutations
		cancelUnachievable(theLegs, achievableRes);

		// score the possible routes
		calculateOptimalRoutes(boundsManager.getContributions(), theLegs);

		// generate some candidate solutions
		CompositeRoute[] routes = generateCandidates(theLegs);

		// and we're done, share the good news!
		for (ISolutionsReadyListener listener : _readyListeners)
		{
			listener.solutionsReady(routes);
		}

		// and fire the diagnostics to the UI
		GeoSupport.showRoutes(routes, _showPoints, _showAchievablePoints,
				_showRoutes, _showRoutesWithScores);
	}

	/**
	 * for the set of generated routes, work out which have the highest score
	 * 
	 * @param boundsManager
	 *          the c
	 * @param theLegs
	 */
	public void calculateOptimalRoutes(
			final Collection<BaseContribution> contribs, ArrayList<CoreLeg> theLegs)
	{
		operateOn(theLegs, new LegOperation()
		{
			public void apply(CoreLeg thisLeg)
			{
				if (thisLeg.getType() == LegType.STRAIGHT)
				{
					StraightLeg leg = (StraightLeg) thisLeg;
					leg.calculateOptimum(contribs);
				}
			}
		});
	}

	public void generateRoutes(ArrayList<CoreLeg> theLegs)
	{
		operateOn(theLegs, new LegOperation()
		{
			public void apply(CoreLeg thisLeg)
			{
				thisLeg.generateRoutes(NUM_CELLS);
			}
		});
	}

	/**
	 * get the legs to decide on their achievable routes
	 * 
	 * @param theLegs
	 */
	public void decideAchievable(ArrayList<CoreLeg> theLegs)
	{
		operateOn(theLegs, new LegOperation()
		{
			public void apply(CoreLeg thisLeg)
			{
				thisLeg.decideAchievableRoutes();
			}
		});
	}

	/**
	 * using best performing routes through the straight legs, generate a set of
	 * overall solutions
	 * 
	 * @param theLegs
	 *          the set of legs we're looking at
	 * @return a set of routes through the data
	 */
	CompositeRoute[] generateCandidates(ArrayList<CoreLeg> theLegs)
	{
		// TODO generate the candidate solutions
		return null;
	}

	void cancelUnachievable(ArrayList<CoreLeg> theLegs, int[][] routes)
	{

		// get the routes for the first leg
		CoreLeg firstLeg = theLegs.get(0);

		// get the routes
		CoreRoute[][] firstRoutes = firstLeg.getRoutes();

		for (int x = 0; x < routes.length; x++)
		{
			boolean possible = false;
			for (int y = 0; y < routes[0].length; y++)
			{

				if (routes[x][y] > 0)
				{
					// this one is possible, drop out
					possible = true;
					break;
				}
			}
			if (!possible)
			{
				// mark all of that route impossible
				CoreRoute[] thisSet = firstRoutes[x];
				for (int i = 0; i < thisSet.length; i++)
				{
					CoreRoute thisRoute = thisSet[i];
					thisRoute.setImpossible();
				}
			}
		}
	}

	int[][] calculateAchievableRoutesFor(ArrayList<CoreLeg> theLegs)
	{
		// ok, loop through the legs, doing the multiplication
		int[][] res = null;
		for (Iterator<CoreLeg> iterator = theLegs.iterator(); iterator.hasNext();)
		{
			CoreLeg thisLeg = (CoreLeg) iterator.next();
			int[][] mat = thisLeg.asMatrix();

			// is this our first one?
			if (res == null)
				res = mat;
			else
			{
				res = CoreLeg.multiply(res, mat);
			}
		}

		return res;
	}

	/**
	 * apply the specified operation on all legs
	 * 
	 * @param theLegs
	 * @param theStepper
	 */
	private static void operateOn(ArrayList<CoreLeg> theLegs,
			LegOperation theStepper)
	{
		for (Iterator<CoreLeg> iterator = theLegs.iterator(); iterator.hasNext();)
		{
			CoreLeg thisLeg = (CoreLeg) iterator.next();
			theStepper.apply(thisLeg);
		}
	}

	/**
	 * extract a set of legs from the space
	 * 
	 * @param space
	 * @return
	 */
	ArrayList<CoreLeg> getTheLegs(Collection<BoundedState> theStates)
	{

		// extract the straight legs
		ArrayList<CoreLeg> theLegs = new ArrayList<CoreLeg>();

		CoreLeg currentLeg = null;

		// remember the last state, since end the first/last items in a straight leg
		// are also in the altering
		// leg before/after them
		BoundedState previousState = null;

		// increementing counter, to number turns
		int counter = 1;

		Iterator<BoundedState> iterator = theStates.iterator();
		while (iterator.hasNext())
		{
			BoundedState thisS = iterator.next();
			String thisLegName = thisS.getMemberOf();

			// is this the current leg?
			if (thisLegName != null)
			{
				// right - this is a state that is part of a straight leg

				// ok, do we have a straight leg for this name
				CoreLeg newLeg = findLeg(thisLegName, theLegs);

				// are we already in this leg?
				if (newLeg == null)
				{
					// right, we're just starting a straight leg. this state also goes on
					// the end
					// of the previous altering leg
					if (currentLeg != null)
					{
						if (currentLeg.getType() == LegType.ALTERING)
						{
							// ok, add this state to the previous altering leg
							currentLeg.add(thisS);
						}
						else
						{
							throw new RuntimeException(
									"A straight leg can only follow an altering leg - some problem here");
						}
					}

					// ok, now go for the straight leg
					currentLeg = new StraightLeg(thisLegName,
							new ArrayList<BoundedState>());
					theLegs.add(currentLeg);
				}
			}
			else
			{
				// a leg with no name = must be altering

				// were we in a straight leg?
				if (currentLeg != null)
				{
					if (currentLeg.getType() == LegType.STRAIGHT)
					{
						// ok, the straight leg is now complete. trigger a new altering leg
						currentLeg = null;
					}
				}

				// ok, are we currently in a leg?
				if (currentLeg == null)
				{
					String thisName = "Alteration " + counter++;
					currentLeg = new AlteringLeg(thisName, new ArrayList<BoundedState>());
					theLegs.add(currentLeg);

					// but, we need to start this altering leg with the previous state, if
					// there was one
					if (previousState != null)
						currentLeg.add(previousState);
				}
			}

			// ok, we've got the leg - now add the state
			currentLeg.add(thisS);

			// and remember it
			previousState = thisS;
		}
		return theLegs;
	}

	private CoreLeg findLeg(String thisLegName, ArrayList<CoreLeg> theLegs)
	{
		CoreLeg res = null;

		for (Iterator<CoreLeg> iterator = theLegs.iterator(); iterator.hasNext();)
		{
			CoreLeg coreLeg = (CoreLeg) iterator.next();
			if (coreLeg.getName().equals(thisLegName))
			{
				res = coreLeg;
				break;
			}
		}

		return res;
	}

	@Override
	public void restarted(IBoundsManager boundsManager)
	{
		// restarted, clear out any temp storage
	}

	@Override
	public void error(IBoundsManager boundsManager, IncompatibleStateException ex)
	{
		// gen contributions failed
	}

	@Override
	public void stepped(IBoundsManager boundsManager, int thisStep, int totalSteps)
	{
		// step forward in generated solutions. We should prob ignore this
	}

	/**
	 * utility interface to make it easy to operate on all legs
	 * 
	 * @author Ian
	 * 
	 */
	public static interface LegOperation
	{
		/**
		 * operate on this leg
		 * 
		 * @param thisLeg
		 */
		public void apply(CoreLeg thisLeg);
	}

	/**
	 * listener for someone who wants to know if we've managed to generate some
	 * routes
	 * 
	 * @author Ian
	 * 
	 */
	public static interface ISolutionsReadyListener
	{
		/**
		 * a set of candidate routes have been generated
		 * 
		 * @param routes
		 */
		public void solutionsReady(CompositeRoute[] routes);
	}

	@Override
	public void setShowPoints(boolean onOff)
	{
		_showPoints = onOff;
	}

	@Override
	public void setShowAchievablePoints(boolean onOff)
	{
		_showAchievablePoints = onOff;
	}

	@Override
	public void setShowRoutes(boolean onOff)
	{
		_showRoutes = onOff;
	}

	@Override
	public void setShowRoutesWithScores(boolean onOff)
	{
		_showRoutesWithScores = onOff;
	}
}
