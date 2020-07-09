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

package ASSET.GUI.Factory;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import ASSET.Scenario.Genetic.Gene;

public class FactoryGUI extends JPanel implements ASSET.Scenario.Genetic.GeneticAlgorithm.GAProgressed,
		ASSET.Scenario.Genetic.GeneticAlgorithm.GAStepped {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/***************************************************************
	 * run this class
	 ***************************************************************/
	public static void main(final String[] args) {
		final FactoryGUI scf = new FactoryGUI();
		final JFrame fr = new JFrame("Factory");
		fr.setSize(800, 600);
		fr.getContentPane().setLayout(new BorderLayout());
		fr.getContentPane().add("Center", scf);
		fr.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		fr.setVisible(true);

		// scf._myFactory.loadObserver("d:\\dev\\asset\\asset2_out\\datum_factory_control.xml");
		// scf._myFactory.setScenarioFile("d:\\dev\\asset\\asset2_out\\datum_factory_scenario.xml");
		// scf._myFactory.setVariablesFile("d:\\dev\\asset\\asset2_out\\datum_factory_variables.xml");

	}

	/***************************************************************
	 * member variables
	 ***************************************************************/
	/**
	 * our factory
	 *
	 */
	CoreFactory _myFactory;

	/**
	 * our stars list
	 *
	 */
	private JList _starList;

	/**
	 * the build button (which gets enabled after all necessary data is loaded
	 *
	 */
	JButton _buildBtn;

	/**
	 * our genes list
	 *
	 */
	private JList _geneList;

	/**
	 * our cycle button
	 *
	 */
	private JButton _cycleBtn;

	/**
	 * our step button
	 *
	 */
	JButton _stepBtn;

	/**
	 * our list of discrete steps
	 *
	 */
	private JToolBar _discreteList;

	/***************************************************************
	 * constructor
	 ***************************************************************/
	private FactoryGUI() {
		/**
		 * create the factory
		 *
		 */
		_myFactory = new CoreFactory() {
			@Override
			public void signalAllDataLoaded() {
				super.signalAllDataLoaded();
				_buildBtn.setEnabled(true);
			}
		};

		/**
		 * and build the form
		 *
		 */
		initForm();
	}

	/**
	 * do the build operation
	 *
	 */
	void doBuild() {
		// _myFactory.setDocument("C:\\Asset\\ASSET2_OUT\\tstFactory.xml");
		// _myFactory.setVariance("C:\\Asset\\ASSET2_OUT\\vary_factory.xml");

		_myFactory.build();

		// check it worked
		if (_myFactory._myGA == null)
			return;

		/**
		 * listen to the GA
		 *
		 */
		_myFactory._myGA.addGAProgressListener(this);

		_myFactory._myGA.addStepListener(this);

		// and enable the cycle btn
		_cycleBtn.setEnabled(true);

		//
		_discreteList.setEnabled(true);

		final Component[] comps = _discreteList.getComponents();
		for (int i = 0; i < comps.length; i++) {
			final Component comp = comps[i];
			comp.setEnabled(true);
		}

	}

	/**
	 * do a step operation
	 *
	 */
	void doStep() {
		_stepBtn.setEnabled(false);

		final Runnable doIt = new Runnable() {
			@Override
			public void run() {
				_myFactory._myGA.step(_myFactory.getLowScoresHigh());
				_stepBtn.setEnabled(true);
			}
		};

		final Thread tt = new Thread(doIt);
		tt.setPriority(Thread.MIN_PRIORITY);
		tt.start();
	}

	/**
	 * we have generated a fresh population
	 *
	 */
	@Override
	public void generated() {
		final Iterator<Gene> it = _myFactory._myGA.getGenes().iterator();
		updateGenes(it);
	}

	/***************************************************************
	 * member methods
	 ***************************************************************/
	/**
	 * build the form
	 *
	 */
	private void initForm() {
		setLayout(new BorderLayout());

		final JToolBar tools = new JToolBar("Tools", SwingConstants.HORIZONTAL);

		final JPanel lists = new JPanel();
		lists.setName("lists");
		lists.setLayout(new GridLayout(2, 0));
		_geneList = new JList();
		_geneList.setBorder(new TitledBorder("Genes"));
		_starList = new JList();
		_starList.setBorder(new TitledBorder("Stars"));
		_starList.add(new JLabel("blank"));
		lists.add(new JScrollPane(_starList));
		lists.add(new JScrollPane(_geneList));

		_buildBtn = new JButton("build");
		_buildBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				doBuild();
			}
		});
		_buildBtn.setEnabled(false);

		_discreteList = new JToolBar(SwingConstants.HORIZONTAL);
		_discreteList.setEnabled(false);

		final JButton generate = new JButton("generate");
		generate.setEnabled(false);
		generate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				_myFactory._myGA.generate();
			}
		});
		final JButton mutate = new JButton("mutate");
		mutate.setEnabled(false);
		mutate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				_myFactory._myGA.mutate();
			}
		});
		_stepBtn = new JButton("step");
		_stepBtn.setEnabled(false);
		_stepBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				doStep();
			}
		});
		final JButton sort = new JButton("sort");
		sort.setEnabled(false);
		sort.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				_myFactory._myGA.sort();
			}
		});
		final JButton promote = new JButton("promote");
		promote.setEnabled(false);
		promote.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				_myFactory._myGA.promote();
			}
		});
		final JButton retire = new JButton("retire");
		retire.setEnabled(false);
		retire.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				_myFactory._myGA.retire();
			}
		});

		_cycleBtn = new JButton("cycle");
		_cycleBtn.setEnabled(false);
		_cycleBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				_myFactory.cycle();
			}
		});
		final JButton exit = new JButton("exit");
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				System.exit(0);
			}
		});

		// put in the tools
		tools.add(_buildBtn);
		tools.add(_cycleBtn);

		tools.add(new javax.swing.JSeparator(SwingConstants.VERTICAL));
		_discreteList.add(generate);
		_discreteList.add(mutate);
		_discreteList.add(_stepBtn);
		_discreteList.add(sort);
		_discreteList.add(promote);
		_discreteList.add(retire);

		tools.add(_discreteList);

		tools.add(new javax.swing.JSeparator(SwingConstants.VERTICAL));

		final JLabel observers = new JLabel("Control");
		_myFactory._observerDropper.addComponent(observers);
		observers.setBorder(new javax.swing.border.EtchedBorder(EtchedBorder.RAISED));
		tools.add(observers);

		final JLabel Variables = new JLabel("Variables");
		_myFactory._varianceDropper.addComponent(Variables);
		Variables.setBorder(new javax.swing.border.EtchedBorder(EtchedBorder.RAISED));
		tools.add(Variables);

		final JLabel Scenario = new JLabel("Scenario");
		_myFactory._scenarioDropper.addComponent(Scenario);
		Scenario.setBorder(new javax.swing.border.EtchedBorder(EtchedBorder.RAISED));
		tools.add(Scenario);

		tools.add(new javax.swing.JSeparator(SwingConstants.VERTICAL));

		tools.add(exit);

		this.add("South", tools);
		this.add("Center", lists);

	}

	/**
	 * our population has mutated
	 *
	 */
	@Override
	public void mutated() {
		final Iterator<Gene> it = _myFactory._myGA.getGenes().iterator();
		updateGenes(it);
	}

	/**
	 * our star performers have been promoted
	 *
	 */
	@Override
	public void promoted() {
		final Iterator<Gene> it = _myFactory._myGA.getStarGenes().iterator();
		updateStars(it);
	}

	/**
	 * we have retired the losers
	 *
	 */
	@Override
	public void retired() {
		final Iterator<Gene> it = _myFactory._myGA.getGenes().iterator();

		// we have to clear out our list of genes
		_geneList.setListData(new java.util.Vector<Gene>(0, 1));
		updateGenes(it);
	}

	/**
	 * our population has been sorted
	 *
	 */
	@Override
	public void sorted() {
		final Iterator<Gene> it = _myFactory._myGA.getGenes().iterator();
		updateGenes(it);
	}

	/**
	 * our population has grown
	 *
	 */
	@Override
	public void stepCompleted() {
		final Iterator<Gene> it = _myFactory._myGA.getGenes().iterator();
		updateGenes(it);
	}

	/**
	 * a gene has developed
	 *
	 */
	@Override
	public void stepped() {
		final Iterator<Gene> it = _myFactory._myGA.getGenes().iterator();
		updateGenes(it);
		_geneList.repaint();
	}

	private void updateGenes(final Iterator<Gene> iter) {

		// empty the list
		_geneList.removeAll();

		// re-populate it
		final Vector<String> newL = new Vector<String>(0, 1);
		while (iter.hasNext()) {
			final Gene gene = iter.next();
			final String newStr = gene.toString();
			newL.add(newStr);
		}
		_geneList.setListData(newL);

		// trigger refresh
		_geneList.invalidate();
	}

	/***************************************************************
	 * class which wraps a gene in a Label, to place it into a List
	 ***************************************************************/
	// private class GeneWrapper extends JLabel
	// {
	// Gene _myGene = null;
	//
	//
	// public GeneWrapper()
	// {
	// }
	//
	// private void pressed()
	// {
	// MWC.GUI.Dialogs.DialogFactory.showMessage("Title", "view pressed");
	// }
	//
	// public void setGene(Gene gene)
	// {
	// if(_myGene != null)
	// {
	// // clear out the old data
	// }
	// _myGene = gene;
	// setText("val:" + _myGene.toString());
	// }
	//
	// public void updateMe()
	// {
	// setText(_myGene.toString());
	// }
	//
	// public String toString()
	// {
	// return _myGene.toString();
	// }
	// }

	/**
	 * usign the supplied iterator put a list of genes into the panel
	 */
	private void updateStars(final Iterator<Gene> iter) {
		// empty the list
		_starList.removeAll();

		// re-populate it
		final Vector<String> newL = new Vector<String>(0, 1);
		while (iter.hasNext()) {
			final Gene gene = iter.next();
			final String newStr = gene.toString();
			newL.add(newStr);
		}
		_starList.setListData(newL);

		// trigger refresh
		_starList.invalidate();
	}
}
