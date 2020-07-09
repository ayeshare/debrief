
package ASSET.Scenario.Genetic;

import java.io.InputStream;

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

import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import ASSET.Util.MonteCarlo.XMLVariance;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

public class GeneticAlgorithm {

	//////////////////////////////////////////////////////////////////////
	// comparator code
	//////////////////////////////////////////////////////////////////////
	protected class compareGenes implements Comparator<Gene> {

		@Override
		public int compare(final Gene o1, final Gene o2) {
			int res = 0;
			final double fit1 = o1.getFitness();
			final double fit2 = o2.getFitness();
			if (fit1 == fit2)
				res = 0;
			else if (fit1 > fit2)
				res = 1;
			else
				res = -1;

			return res;
		}
	}

	/***************************************************************
	 * interfaces for listeners who wish to know as we pass through stages
	 ***************************************************************/
	public interface GAProgressed {
		/**
		 * we have generated a fresh population
		 *
		 */
		public void generated();

		/**
		 * our population has mutated
		 *
		 */
		public void mutated();

		/**
		 * our star performers have been promoted
		 *
		 */
		public void promoted();

		/**
		 * we have retired the losers
		 *
		 */
		public void retired();

		/**
		 * our population has been sorted
		 *
		 */
		public void sorted();

		/**
		 * our population has grown
		 *
		 */
		public void stepCompleted();
	}

	/***************************************************************
	 * member variables
	 ***************************************************************/

	/**
	 * interfaces for listeners who wish to know as we work through each scenario
	 *
	 */
	public interface GAStepped {
		/**
		 * a gene has developed
		 *
		 */
		public void stepped();
	}

	/**
	 * basic interface of scenario runner, which just returns a random number
	 *
	 */
	static public class RandomRunner implements ScenarioRunner {
		@Override
		public ScenarioRunner.ScenarioOutcome runThis(final String scenario, final String name, final String desc) {
			final ScenarioRunner.ScenarioOutcome res = new ScenarioRunner.ScenarioOutcome();
			res.score = ASSET.Util.RandomGenerator.nextRandom() * 100;
			return res;
		}
	}

	/**
	 * basic interface of scenario runner, which just returns a random number
	 *
	 */
	static public class TotalRunner implements ScenarioRunner {
		@Override
		public ScenarioRunner.ScenarioOutcome runThis(final String scenario, final String name, final String desc) {
			// get the product of the values in the genes
			final ScenarioRunner.ScenarioOutcome res = new ScenarioRunner.ScenarioOutcome();

			final Iterator<XMLVariance> it = _currentGene._myList.getIterator();
			while (it.hasNext()) {
				final XMLVariance variable = it.next();
				final String curVal = variable.getValue();
				try {
					final double thisV = MWCXMLReader.readThisDouble(curVal);
					res.score *= thisV;
				} catch (final ParseException e) {
					MWC.Utilities.Errors.Trace.trace(e);
				}
			}

			return res;
		}
	}

	/**
	 * TEMPORARY working object which lets us get the gene currently running
	 *
	 */
	static Gene _currentGene = null;

	/**
	 * current list of genes
	 *
	 */
	private SortedSet<Gene> _theseGenes;

	/**
	 * current list of top performers
	 *
	 */
	private SortedSet<Gene> _starGenes;

	/**
	 * the size of each population
	 *
	 */
	private final int _population_size;

	/**
	 * the size of the set of stars
	 *
	 */
	private final int _stars_size;

	/**
	 * the Gene we base ourselves upon
	 *
	 */
	private Gene _baseGene;

	/**
	 * listeners for use moving through ga
	 *
	 */
	private final Vector<GAProgressed> _GAListeners = new Vector<GAProgressed>(0, 1);

	/**
	 * listeners for stepping through population
	 *
	 */
	private final Vector<GAStepped> _stepListeners = new Vector<GAStepped>(0, 1);

	/***************************************************************
	 * member variables
	 ***************************************************************/

	/**
	 * the object which performs the run for us
	 *
	 */
	private ScenarioRunner _myRunner = null;

	/**
	 * a counter to keep track of how many scenarios have been run through
	 *
	 */
	private int _counter = 0;

	/***************************************************************
	 * constructor
	 ***************************************************************/
	/**
	 * constructor for our genetic algorithm
	 *
	 * @param population_size the number of genes in the population
	 * @param stars_size      the number of genes we retain in high scores
	 */
	public GeneticAlgorithm(final int population_size, final int stars_size) {
		_population_size = population_size;
		_stars_size = stars_size;

		_theseGenes = new TreeSet<Gene>();
		_starGenes = new TreeSet<Gene>();

	}

	/**
	 * add/remove a progress listener
	 *
	 */
	public void addGAProgressListener(final GAProgressed listener) {
		_GAListeners.add(listener);
	}

	/**
	 * add/remove a step listener
	 *
	 */
	public void addStepListener(final GAStepped listener) {
		_stepListeners.add(listener);
	}

	/**
	 * set the data streams necessary to create the gene
	 *
	 */
	public void createGene(final InputStream inputDoc, final InputStream inputVary) {
		// _baseGene = new Gene(inputDoc, inputVary);
		// todo: reinstate this
	}

	public void generate() {
		// clear the waiting items list
		_theseGenes.clear();

		// build up list of genes in waiting list
		for (int i = 0; i < _population_size; i++) {
			final Gene newG = _baseGene.createRandom();

			_theseGenes.add(newG);
		}

		// inform the listeners
		final Iterator<GAProgressed> it = _GAListeners.iterator();
		while (it.hasNext()) {
			final GAProgressed progressed = it.next();
			progressed.generated();
		}
	}

	public Gene getGene() {
		return _baseGene;
	}

	public Collection<Gene> getGenes() {
		return _theseGenes;
	}

	/***************************************************************
	 * accessor methods
	 ***************************************************************/
	public Collection<Gene> getStarGenes() {
		return _starGenes;
	}

	private void mateWithThis(final Gene star, final double prob) {
		final Iterator<Gene> it = _theseGenes.iterator();

		while (it.hasNext()) {
			final Gene newG = it.next();
			newG.mergeWith(star, prob);

		}
	}

	/**
	 * mutate using our star performers
	 *
	 */
	public void mutate() {
		// do we have any stars?
		if (_starGenes.size() > 0) {
			// we now merge the random genes with our stars
			final double percentage_step = 0.3d / _starGenes.size();
			double this_prob = percentage_step;
			final Iterator<Gene> it = _starGenes.iterator();
			while (it.hasNext()) {
				final Gene nextStar = it.next();
				// pass through the new genes, and mate with this one according to the %age
				mateWithThis(nextStar, this_prob);
				this_prob += percentage_step;
			}
		}

		// inform the listeners
		final Iterator<GAProgressed> it = _GAListeners.iterator();
		while (it.hasNext()) {
			final GAProgressed progressed = it.next();
			progressed.mutated();
		}

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void promote() {
		// compare the top performers with the one at the bottom of the stars list

		// do we have a list of stars?
		if (_starGenes.size() == 0) {
			// just create it afresh!
			_starGenes.addAll(_theseGenes);
		} else {
			// get the poorest of our stars
			final Gene lowest = _starGenes.first();

			// get the list of the finished genes which are equal to or greater than
			// the lowest of our start
			final SortedSet<Gene> newStars = _theseGenes.tailSet(lowest);

			// add this list to our stars
			_starGenes.addAll(newStars);

		}

		// sort the star genes
		final Object[] list = _starGenes.toArray();
		Arrays.sort(list, new Comparator() {
			@Override
			public int compare(final Object o1, final Object o2) {
				final Gene na = (Gene) o1;
				return 1 - na.compareTo((Gene) o2);
			}
		});

		// finally trim it down to the desired size
		final SortedSet<Gene> ss = new TreeSet<Gene>();
		for (int ii = 0; ii < _stars_size; ii++) {
			ss.add((Gene) list[ii]);
		}

		_starGenes = ss;

		// inform the listeners
		final Iterator<GAProgressed> it = _GAListeners.iterator();
		while (it.hasNext()) {
			final GAProgressed progressed = it.next();
			progressed.promoted();
		}

	}

	/**
	 * add/remove a progress listener
	 *
	 */
	public void removeGAProgressListener(final GAProgressed listener) {
		_GAListeners.remove(listener);
	}

	/**
	 * add/remove a step listener
	 *
	 */
	public void removeStepListener(final GAStepped listener) {
		_stepListeners.remove(listener);
	}

	public void retire() {
		// ditch all of the genes from the completed list
		_theseGenes.clear();

		// inform the listeners
		final Iterator<GAProgressed> it = _GAListeners.iterator();
		while (it.hasNext()) {
			final GAProgressed progressed = it.next();
			progressed.retired();
		}

	}

	public void setGene(final Gene myGene) {
		_baseGene = myGene;
	}

	/**
	 * set the object which performs the run for us
	 *
	 */
	public void setRunner(final ScenarioRunner runner) {
		_myRunner = runner;
	}

	/***************************************************************
	 * embedded class to run a scenario
	 ***************************************************************/

	public void sort() {

		// create a blank list
		final TreeSet<Gene> ts = new TreeSet<Gene>();

		// add our exiting genes to it, to sort it
		final Iterator<Gene> it = _theseGenes.iterator();
		while (it.hasNext()) {
			final Gene gene = it.next();

			// did this succeed?
			if (gene.getFitness() != ScenarioRunner.ScenarioOutcome.INVALID_SCORE)
				ts.add(gene);
		}

		// and store as our own generation
		_theseGenes = ts;

		// inform the listeners
		final Iterator<GAProgressed> it2 = _GAListeners.iterator();
		while (it2.hasNext()) {
			final GAProgressed progressed = it2.next();
			progressed.sorted();
		}
	}

	/**
	 * move all of the scenarios forward, calculate the fitnesses
	 *
	 */
	public void step(final boolean lowScoresHigh) {
		/***************************************************************
		 * step through the genes
		 ***************************************************************/
		final Iterator<Gene> it = _theseGenes.iterator();
		while (it.hasNext()) {
			final Gene thisG = it.next();

			_currentGene = thisG;

			/***************************************************************
			 * get the scenario
			 ***************************************************************/

			// do a simulation using this combination
			final String thisScenario = thisG.getDocument();

			// create a new name
			final String name = "" + _counter++;

			// name the gene
			thisG.setName(name);

			/***************************************************************
			 * get the fitness
			 ***************************************************************/

			final ScenarioRunner.ScenarioOutcome fitness = stepThis(thisScenario, name, thisG.toString());

			// reverse the score, if we running in reverse
			if (lowScoresHigh && (fitness.score != ScenarioRunner.ScenarioOutcome.INVALID_SCORE))
				fitness.score = -fitness.score;

			thisG.setFitness(fitness.score, fitness.summary);

			/***************************************************************
			 * inform the listeners
			 ***************************************************************/
			// inform the listeners
			final Iterator<GAStepped> it2 = _stepListeners.iterator();
			while (it2.hasNext()) {
				final GAStepped progressed = it2.next();
				progressed.stepped();
			}

		}

		/***************************************************************
		 * inform the listeners that the whole cycle is complete
		 ***************************************************************/

		// inform the listeners
		final Iterator<GAProgressed> it2 = _GAListeners.iterator();
		while (it2.hasNext()) {
			final GAProgressed progressed = it2.next();
			progressed.stepCompleted();
		}

	}

	/**
	 * Move the supplied scenario forward one step, returning it's fitness figure
	 *
	 * @return the fitness of this scenario
	 */
	private ScenarioRunner.ScenarioOutcome stepThis(final String scenario, final String name, final String desc) {
		final ScenarioRunner.ScenarioOutcome res = _myRunner.runThis(scenario, name, desc);
		return res;
	}

	//////////////////////////////////////////////////////////////////////
	// testing code
	//////////////////////////////////////////////////////////////////////

	//////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	//////////////////////////////////////////////////////////////////////////////////////////////////
	// public static class dtestGA extends junit.framework.TestCase
	// {
	// // todo: reinstate this test, rename it as testGA
	// static public final String TEST_ALL_TEST_TYPE = "UNIT";
	//
	// public dtestGA(final String val)
	// {
	// super(val);
	// }
	//
	// int gen = 0;
	// int mut = 0;
	// int step = 0;
	// int prom = 0;
	// int ret = 0;
	// int sort = 0;
	//
	// private class listener implements GAProgressed, GAStepped
	// {
	// /** our population has been sorted
	// *
	// */
	// public void sorted()
	// {
	// sort++;
	// }
	//
	// /** we have generated a fresh population
	// *
	// */
	// public void generated()
	// {
	// gen++;
	// }
	//
	// /** our population has mutated
	// *
	// */
	// public void mutated()
	// {
	// mut++;
	// }
	//
	// /** our population has grown
	// *
	// */
	// public void stepped()
	// {
	// step++;
	// }
	//
	// /** our population has grown
	// *
	// */
	// public void stepCompleted()
	// {
	// step++;
	// }
	//
	// /** our star performers have been promoted
	// *
	// */
	// public void promoted()
	// {
	// prom++;
	// }
	//
	// /** we have retired the losers
	// *
	// */
	// public void retired()
	// {
	// ret++;
	// }
	// }
	//
	// public void testScoreSort()
	// {
	// Gene ga = Gene.getGeneForTesting();
	// ga.setFitness(12, "12");
	//
	// Gene gb = Gene.getGeneForTesting();
	// gb.setFitness(16, "16");
	//
	// Gene gc = Gene.getGeneForTesting();
	// gc.setFitness(14, "14");
	//
	// Gene gd = Gene.getGeneForTesting();
	// gd.setFitness(14, "14");
	//
	// Gene ge = Gene.getGeneForTesting();
	// ge.setFitness(15, "15");
	//
	// Gene gf = Gene.getGeneForTesting();
	// gf.setFitness(13, "13");
	//
	// GeneticAlgorithm gen = new GeneticAlgorithm(6, 2);
	// gen._theseGenes.add(ga);
	// gen._theseGenes.add(gb);
	// gen._theseGenes.add(gc);
	// gen._theseGenes.add(gd);
	// gen._theseGenes.add(ge);
	// gen._theseGenes.add(gf);
	//
	// // check the initial order
	// assertEquals("first item is corect", gen._theseGenes.first(), ga);
	// assertEquals("last item is corect", gen._theseGenes.last(), gb);
	//
	// // what happens if we promote?
	// gen.promote();
	//
	// // check the initial order
	// assertEquals("first start is corect", gen._starGenes.first(), ge);
	// assertEquals("second start is corect", gen._starGenes.last(), gb);
	//
	// list("first promotion", gen._starGenes);
	//
	// // do a forward sort
	// gen.sort();
	//
	// // check the order
	// assertEquals("first item is corect", gen._theseGenes.first(), ga);
	// assertEquals("last item is corect", gen._theseGenes.last(), gb);
	//
	// list("forward", gen._theseGenes);
	//
	// // do a reverse sort
	// gen.sort();
	//
	// list("reverse", gen._theseGenes);
	//
	// // check the order
	// assertEquals("first item is corect", gb, gen._theseGenes.first());
	// assertEquals("last item is corect", ga, gen._theseGenes.last());
	//
	// // reset the list of stars
	// gen._starGenes.clear();
	//
	// gen.promote();
	//
	// // promoted list?
	// list("promoted:",gen._starGenes);
	//
	// // check the initial order
	// assertEquals("first star is corect", ga, gen._starGenes.first());
	// assertEquals("second star is corect", gf, gen._starGenes.last());
	//
	//
	// }
	//
	// public void testIt()
	// {
	//
	//
	// String test_root = System.getProperty("TEST_ROOT");
	// if(test_root == "" || test_root == null)
	// {
	// test_root = "d:\\dev\\Asset\\src\\test_data";
	// }
	//
	// final String docPath = test_root + "\\factory_scenario.xml";
	// final String varyPath = test_root + "\\factory_variables.xml";
	//
	//
	// final int POP_SIZE = 10;
	// final int STAR_SIZE = 5;
	//
	// /***************************************************************
	// * read in the data
	// ***************************************************************/
	// // now put it into our ga
	// GeneticAlgorithm ga = null;
	// try
	// {
	// ga = new GeneticAlgorithm(POP_SIZE, STAR_SIZE);
	// ga.createGene(new java.io.FileInputStream(docPath), new
	////////////////////////////////////////////////////////////////////////////////////////////////// java.io.FileInputStream(varyPath));
	// ga.setRunner(new RandomRunner());
	// }
	// catch (java.io.FileNotFoundException fe)
	// {
	// fe.printStackTrace();
	// assertTrue(fe.getMessage(), false);
	// }
	//
	// /***************************************************************
	// * add listeners
	// ***************************************************************/
	// final listener l = new listener();
	// ga.addGAProgressListener(l);
	// ga.addStepListener(l);
	//
	//
	// /***************************************************************
	// * create a generation
	// ***************************************************************/
	//
	// // do pre-checks
	// assertEquals("number before", 0, ga._theseGenes.size());
	// assertEquals("number before", 0, ga._starGenes.size());
	// assertEquals("correct pop size", POP_SIZE, ga._population_size);
	// assertEquals("correct pop size", STAR_SIZE, ga._stars_size);
	//
	// ga.generate();
	//
	// // do checks
	// // do pre-checks
	// assertEquals("pop number after", POP_SIZE, ga._theseGenes.size());
	// assertEquals("stars number after", 0, ga._starGenes.size());
	//
	// // check counter
	// assertEquals("gen message sent", 1, gen);
	//
	//
	// /***************************************************************
	// * mutate (not first time)
	// ***************************************************************/
	//
	// // store generation
	// TreeSet safeGen = new TreeSet(ga._theseGenes);
	// ga.mutate();
	//
	// // check gen still alive
	// assertTrue("we have kept same elements", checkSame(safeGen, ga._theseGenes));
	//
	// assertEquals("mutate message sent", 1, mut);
	//
	// /***************************************************************
	// * step forward
	// ***************************************************************/
	//
	// safeGen = new TreeSet(ga._theseGenes);
	//
	// Gene first = (Gene) ga._theseGenes.first();
	// Gene last = (Gene) ga._theseGenes.last();
	//
	// double beforeFit = first.getFitness();
	//
	// // check gene is capable of producing document
	// String thisDoc = first.getDocument();
	// assertTrue("document was returned", (thisDoc != null));
	// assertTrue("document was returned", (thisDoc.length() > 0));
	//
	// ga.step(false);
	//
	// // check genes have changed
	// double afterFit = first.getFitness();
	//
	// assertTrue("fitnesses have changed", (beforeFit != afterFit));
	//
	// assertEquals("step message sent", POP_SIZE + 1, step);
	//
	//
	// /***************************************************************
	// * sort
	// ***************************************************************/
	//
	// // remember the order
	// safeGen = new TreeSet(ga._theseGenes);
	//
	// // do the sort
	// ga.sort();
	//
	// // check they are in roughly the correct order
	// first = (Gene) ga._theseGenes.first();
	// last = (Gene) ga._theseGenes.last();
	// assertEquals("correct order", -1, first.compareTo(last));
	//
	// // check the order has changed
	// assertTrue("order is not same as before", (!checkSame(safeGen,
	////////////////////////////////////////////////////////////////////////////////////////////////// ga._theseGenes)));
	//
	// assertEquals("sort message sent", 1, sort);
	//
	//
	// /***************************************************************
	// * promote
	// ***************************************************************/
	//
	// assertEquals("before first promotion", 0, ga._starGenes.size());
	//
	// ga.promote();
	//
	// assertEquals("first promotion", STAR_SIZE, ga._starGenes.size());
	// // check they are in roughly the correct order
	// first = (Gene) ga._starGenes.first();
	// last = (Gene) ga._starGenes.last();
	// assertEquals("stars sorted", -1, first.compareTo(last));
	//
	// Gene otherLast = (Gene) ga._theseGenes.first();
	// assertEquals("stars greater than normal sorted", -1,
	////////////////////////////////////////////////////////////////////////////////////////////////// otherLast.compareTo(last));
	// assertEquals("promote message sent", 1, prom);
	//
	//
	//
	// /***************************************************************
	// * retire stragglers
	// ***************************************************************/
	// assertEquals("before retirement", POP_SIZE, ga._theseGenes.size());
	// assertEquals("before retirement", STAR_SIZE, ga._starGenes.size());
	//
	// ga.retire();
	//
	// assertEquals("after retirement", 0, ga._theseGenes.size());
	// assertEquals("after retirement", STAR_SIZE, ga._starGenes.size());
	// assertEquals("retire message sent", 1, ret);
	//
	// /***************************************************************
	// * create another
	// ***************************************************************/
	// ga.generate();
	//
	// // do checks
	// // do pre-checks
	// assertEquals("pop number after", POP_SIZE, ga._theseGenes.size());
	// assertEquals("stars number after", STAR_SIZE, ga._starGenes.size());
	//
	// // check counter
	// assertEquals("2nd gen message sent", 2, gen);
	//
	//
	// /***************************************************************
	// * step
	// ***************************************************************/
	//
	// first = (Gene) ga._theseGenes.first();
	// beforeFit = first.getFitness();
	//
	// // check gene is capable of producing document
	// thisDoc = first.getDocument();
	// assertTrue("document was returned", (thisDoc != null));
	// assertTrue("document was returned", (thisDoc.length() > 0));
	//
	// ga.step(false);
	//
	// // check genes have changed
	// afterFit = first.getFitness();
	//
	// assertTrue("fitnesses have changed", (beforeFit != afterFit));
	//
	// assertEquals("2nd step message sent", 2 * POP_SIZE + 2, step);
	//
	// /***************************************************************
	// * sort
	// ***************************************************************/
	// ga.sort();
	//
	// // check they are in roughly the correct order
	// first = (Gene) ga._theseGenes.first();
	// last = (Gene) ga._theseGenes.last();
	// assertEquals("correct order", -1, first.compareTo(last));
	//
	// assertEquals("sort message sent", 2, sort);
	//
	//
	// /***************************************************************
	// * promote
	// ***************************************************************/
	//
	// // take copy of existing stars
	// final TreeSet ts = new TreeSet(ga._starGenes);
	//
	// // check size
	// assertEquals("before second promotion", STAR_SIZE, ga._starGenes.size());
	//
	// ga.promote();
	//
	// assertEquals("first promotion", STAR_SIZE, ga._starGenes.size());
	// // check they are in roughly the correct order
	// first = (Gene) ga._starGenes.first();
	// last = (Gene) ga._starGenes.last();
	// assertEquals("stars sorted", -1, first.compareTo(last));
	//
	// otherLast = (Gene) ga._theseGenes.first();
	// assertEquals("stars greater than normal sorted", -1,
	////////////////////////////////////////////////////////////////////////////////////////////////// otherLast.compareTo(last));
	//
	// // ok, now do retire
	// ga.retire();
	//
	// // check that new list is different to old stars list
	// assertTrue("lists contain different items", (!checkSame(ts, ga._starGenes)));
	// assertEquals("promote message sent", 2, prom);
	//
	// }
	//
	//
	// private static boolean checkSame(final SortedSet a, final SortedSet b)
	// {
	// boolean same = true;
	// final Iterator newIt = a.iterator();
	// final Iterator oldIt = b.iterator();
	//
	// while (newIt.hasNext())
	// {
	// final Gene newOb = (Gene) newIt.next();
	// final Gene oldOb = (Gene) oldIt.next();
	//
	// if (newOb != oldOb)
	// {
	// same = false;
	// break;
	// }
	// }
	// return same;
	// }
	//
	// private static void list(final String msg, final SortedSet set)
	// {
	// if (msg != null)
	// System.out.println(msg);
	// final Iterator it = set.iterator();
	// while (it.hasNext())
	// {
	// final Gene variable = (Gene) it.next();
	// System.out.println(":" + variable.toString());
	// }
	// }
	// }

	// public static void main(String[] args)
	// {
	// final dtestGA ga = new dtestGA("test");
	// ga.testScoreSort();
	// ga.testIt();
	// }

}