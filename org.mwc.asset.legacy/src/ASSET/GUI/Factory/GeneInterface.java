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
import java.awt.FlowLayout;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

import ASSET.Scenario.Genetic.Gene;
import ASSET.Util.MonteCarlo.XMLVariance;
import ASSET.Util.MonteCarlo.XMLVarianceList;

public class GeneInterface extends JComponent {
	/***************************************************************
	 * list of genes
	 ***************************************************************/
	static public class GeneList extends JList {

		class GeneRenderer extends JPanel implements javax.swing.ListCellRenderer {
			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Component getListCellRendererComponent(final JList list, final Object value, final int index,
					final boolean isSelected, final boolean cellHasFocus) {
				final Gene gene = (Gene) value;
				final GeneInterface gi = new GeneInterface(gene);

				System.out.println(".");
				gi.setGene(gene);

				// setText("bing");
				// this.setLayout(new BorderLayout());
				this.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
				add(new JLabel("other"));
				add(new JLabel("otherb"));

				// add("North", gi);
				return this;
			}
		}

		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

		public GeneList() {
			this.setCellRenderer(new GeneRenderer());
		}

		public void setGenes(final Iterator<Gene> iter) {
			// remove the existing
			this.removeAll();

			final Vector<Gene> data = new Vector<Gene>(0, 1);

			// pass through, adding the genes
			while (iter.hasNext()) {
				final Gene gene = iter.next();
				data.add(gene);
			}

			this.setListData(data);
		}

	}

	/***************************************************************
	 * embedded class which shows the contents of a particular gene variable
	 ***************************************************************/
	static class VariableWrapper extends JComponent {
		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

		final XMLVariance _var;

		JLabel _curVal = null;

		public VariableWrapper(final XMLVariance var) {
			_var = var;

			initForm();

		}

		private void initForm() {
			setLayout(new BorderLayout(1, 0));

			this.setBorder(new javax.swing.border.EtchedBorder());

			// put in the variable name
			final JLabel name = new JLabel(_var.getName());

			// put in the value
			_curVal = new JLabel(_var.getValue());

			add("Center", name);
			add("East", _curVal);

		}
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/***************************************************************
	 * member methods
	 ***************************************************************/

	public static void main(final String[] args) {
		// final JFrame holder = new JFrame("holder");
		// holder.setSize(300, 300);
		// holder.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// holder.getContentPane().setLayout(new FlowLayout());
		//
		// try
		// {
		// final Gene gene = new Gene(new
		// java.io.FileInputStream(System.getProperty("TEST_ROOT") +
		// "factory_scenario.xml"),
		// new java.io.FileInputStream(System.getProperty("TEST_ROOT") +
		// "factory_variables.xml")) ;
		//
		// final Vector genes = new Vector(0,1);
		// // create the list
		// for(int i=0;i<10;i++)
		// {
		// final Gene otherGene = gene.createRandom();
		// genes.add(otherGene);
		// }
		//
		// final GeneList gl = new GeneList();
		// final Iterator iter = genes.iterator();
		// gl.setGenes(iter);
		// holder.getContentPane().add(gl);
		//
		// holder.getContentPane().add(new GeneInterface(gene));
		// }
		// catch (FileNotFoundException e)
		// {
		// e.printStackTrace();
		// }
		//
		// holder.setVisible(true);
		// todo: reinstate these tests
	}

	/***************************************************************
	 * member variables
	 ***************************************************************/
	/**
	 * the gene we are plotting
	 *
	 */
	protected Gene _myGene = null;

	/***************************************************************
	 * constructor
	 ***************************************************************/
	GeneInterface(final Gene baseGene) {
		initForm(baseGene);
	}

	/**
	 * construct the panel
	 *
	 */
	private void initForm(final Gene baseGene) {
		// setLayout(new GridLayout(1,0));
		final BoxLayout bl = new BoxLayout(this, BoxLayout.X_AXIS);
		setLayout(bl);

		// output the fitness
		this.add(new JLabel("" + (int) baseGene.getFitness()));

		// go through the gene
		final XMLVarianceList vl = baseGene.getChromosomes();

		final Iterator<XMLVariance> it = vl.getIterator();
		while (it.hasNext()) {
			final XMLVariance variable = it.next();
			this.add(new VariableWrapper(variable));
		}
	}

	/**
	 * change the gene we are plotting
	 *
	 */
	void setGene(final Gene newGene) {
		_myGene = newGene;
	}

	/**
	 * update the data we are plotting
	 *
	 */
	public void updateData() {

	}

}
