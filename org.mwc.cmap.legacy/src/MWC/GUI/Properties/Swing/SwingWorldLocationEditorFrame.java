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

package MWC.GUI.Properties.Swing;

/*
 * SwingWorldLocationEditorFrame.java
 *
 * Created on 26 September 2000, 15:43
 */

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.DecimalFormat;

import javax.swing.ButtonGroup;

import MWC.GenericData.WorldLocation;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import MWC.Utilities.TextFormatting.BriefFormatLocation;

/**
 *
 * @author IAN MAYO
 * @version
 */
public class SwingWorldLocationEditorFrame extends javax.swing.JDialog {

	protected class FocusText extends javax.swing.JTextField implements FocusListener {
		/**
			 *
			 */
		private static final long serialVersionUID = 1L;

		public FocusText() {
			super();
			super.addFocusListener(this);
		}

		@Override
		public void focusGained(final FocusEvent e) {
			// select all of the text
			this.setSelectionStart(0);
			this.setSelectionEnd(this.getText().length());
		}

		@Override
		public void focusLost(final FocusEvent e) {
		}

	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	//////////////////////////////////////////////////////////////
	// member variables
	//////////////////////////////////////////////////////////////
	static public WorldLocation doEdit(final WorldLocation val) {
		// final JFrame parent = new JFrame("scrap");
// work with a copy of the original value
		WorldLocation res = new WorldLocation(val);
		final SwingWorldLocationEditorFrame aw = new SwingWorldLocationEditorFrame(res, null);
		aw.setVisible(true);
		res = new WorldLocation(aw.getResult());
		aw.dispose();
		// parent.dispose();
		return res;
	}

	public static void main(final String[] args) {
		System.out.println("Test started");
		final WorldLocation res = doEdit(new WorldLocation(12, 13, 14.3456, 'N', 14, 15, 11.345, 'W', 12));
		System.out.println("Res is " + res);
		System.exit(0);
	}

	private final WorldLocation _initial;

	private WorldLocation _result;
	/**
	 * the formatting objects
	 *
	 */
	private final java.text.DecimalFormat _MinDf = new DecimalFormat("0.#####");
	private final java.text.DecimalFormat _SecDf = new DecimalFormat("0.####");
	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JPanel btnPanel;
	private javax.swing.JButton AcceptBtn;
	private javax.swing.JButton CancelBtn;
	private javax.swing.JTabbedPane entryPanel;
	private javax.swing.JPanel DM;
	private javax.swing.JPanel DM_Lat;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JTextField DM_LatDeg;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JTextField DM_LatMin;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JPanel jPanel4;
	private javax.swing.JRadioButton DM_NorthBtn;
	private javax.swing.JRadioButton DM_SouthBtn;
	private javax.swing.JPanel DM_Long;
	private javax.swing.JLabel jLabel4;
	private javax.swing.JTextField DM_LongDeg;
	private javax.swing.JLabel jLabel5;
	private javax.swing.JTextField DM_LongMin;
	private javax.swing.JLabel jLabel6;
	private javax.swing.JPanel jPanel6;
	private javax.swing.JRadioButton DM_EastBtn;
	private javax.swing.JRadioButton DM_WestBtn;
	private javax.swing.JPanel DMS;
	private javax.swing.JPanel DMS_Lat;
	private javax.swing.JLabel jLabel7;
	private javax.swing.JTextField DMS_LatDeg;
	private javax.swing.JLabel jLabel8;
	private javax.swing.JTextField DMS_LatMin;
	private javax.swing.JLabel jLabel9;
	private javax.swing.JTextField DMS_LatSec;
	private javax.swing.JLabel jLabel10;
	private javax.swing.JPanel jPanel9;
	private javax.swing.JRadioButton DMS_NorthBtn;
	private javax.swing.JRadioButton DMS_SouthBtn;
	private javax.swing.JPanel DMS_Long;
	private javax.swing.JLabel jLabel11;
	private javax.swing.JTextField DMS_LongDeg;
	private javax.swing.JLabel jLabel12;
	private javax.swing.JTextField DMS_LongMin;
	private javax.swing.JLabel jLabel13;
	private javax.swing.JTextField DMS_LongSec;
	private javax.swing.JLabel jLabel14;

	private javax.swing.JPanel jPanel11;
	private javax.swing.JRadioButton DMS_EastBtn;

	// End of variables declaration//GEN-END:variables

	//////////////////////////////////////////////////////////////
	// constructor
	//////////////////////////////////////////////////////////////

	private javax.swing.JRadioButton DMS_WestBtn;

	//////////////////////////////////////////////////////////////
	// member functions
	//////////////////////////////////////////////////////////////

	private javax.swing.JLabel depthLabel;

	private javax.swing.JTextField depthValue;

	/** Creates new form SwingWorldLocationEditorFrame */
	public SwingWorldLocationEditorFrame(final WorldLocation initial, final java.awt.Frame parent) {
		super(parent, true);
		initComponents();
		pack();

		// store the location
		_initial = initial;

		// put the buttons into Groups
		//
		final ButtonGroup DM_NS = new ButtonGroup();
		DM_NS.add(DM_NorthBtn);
		DM_NorthBtn.setSelected(true);
		DM_NS.add(DM_SouthBtn);

		final ButtonGroup DM_EW = new ButtonGroup();
		DM_EW.add(DM_EastBtn);
		DM_EastBtn.setSelected(true);
		DM_EW.add(DM_WestBtn);

		final ButtonGroup DMS_NS = new ButtonGroup();
		DMS_NS.add(DMS_NorthBtn);
		DMS_NorthBtn.setSelected(true);
		DMS_NS.add(DMS_SouthBtn);

		final ButtonGroup DMS_EW = new ButtonGroup();
		DMS_EW.add(DMS_EastBtn);
		DMS_EastBtn.setSelected(true);
		DMS_EW.add(DMS_WestBtn);

		initData();
		setLocationRelativeTo(parent);

	}

	void AcceptBtnActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_AcceptBtnActionPerformed

		// Add your handling code here:

		WorldLocation res = null;

		try {

			// extract the parameters to get the result
			int latDeg = 0, longDeg = 0;
			double latMin = 0d, longMin = 0d, latSec = 0d, longSec = 0d;
			double depth = 0d;
			char latHem = 'N', longHem = 'E';

			// sort out the depth, since it doesn't depend on
			// which panel is being edited
			depth = MWCXMLReader.readThisDouble(depthValue.getText());

			// determine which panel is being edited
			if (entryPanel.getSelectedComponent() == DM) {
				// just degs and minutes, collate the values
				latDeg = Integer.parseInt(DM_LatDeg.getText());
				latMin = _MinDf.parse(DM_LatMin.getText()).doubleValue();
				longDeg = Integer.parseInt(DM_LongDeg.getText());
				longMin = _MinDf.parse(DM_LongMin.getText()).doubleValue();

				if (DM_NorthBtn.getModel().isSelected())
					latHem = 'N';
				else
					latHem = 'S';

				if (DM_EastBtn.getModel().isSelected())
					longHem = 'E';
				else
					longHem = 'W';

				res = new WorldLocation(latDeg, latMin, 0, latHem, longDeg, longMin, 0, longHem, depth);

			} else {
				// just degs and minutes, collate the values
				latDeg = Integer.parseInt(DMS_LatDeg.getText());
				latMin = _MinDf.parse(DMS_LatMin.getText()).doubleValue();
				latSec = _SecDf.parse(DMS_LatSec.getText()).doubleValue();
				longDeg = Integer.parseInt(DMS_LongDeg.getText());
				longMin = _MinDf.parse(DMS_LongMin.getText()).doubleValue();
				longSec = _SecDf.parse(DMS_LongSec.getText()).doubleValue();

				if (DMS_NorthBtn.getModel().isSelected())
					latHem = 'N';
				else
					latHem = 'S';

				if (DMS_EastBtn.getModel().isSelected())
					longHem = 'E';
				else
					longHem = 'W';

				res = new WorldLocation(latDeg, (int) latMin, latSec, latHem, longDeg, (int) longMin, longSec, longHem,
						depth);

			}
		} catch (final Exception e) {
			MWC.GUI.Dialogs.DialogFactory.showMessage("Edit World Location",
					"Sorry, an invalid value has been entered");
		}

		_result = res;

		exitForm(null);

	}// GEN-LAST:event_AcceptBtnActionPerformed

	void CancelBtnActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_CancelBtnActionPerformed
		// Add your handling code here:
		// reset the results
		_result = _initial;

		exitForm(null);
	}// GEN-LAST:event_CancelBtnActionPerformed

	/** Exit the Application */
	void exitForm(final java.awt.event.WindowEvent evt) {// GEN-FIRST:event_exitForm
		setVisible(false);
	}// GEN-LAST:event_exitForm

	/**
	 * return the current value of the field
	 */
	protected WorldLocation getResult() {
		return _result;
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the FormEditor.
	 */
	private void initComponents()// GEN-BEGIN:initComponents
	{
		btnPanel = new javax.swing.JPanel();
		AcceptBtn = new javax.swing.JButton();
		CancelBtn = new javax.swing.JButton();
		entryPanel = new javax.swing.JTabbedPane();
		DM = new javax.swing.JPanel();
		DM_Lat = new javax.swing.JPanel();
		jLabel1 = new javax.swing.JLabel();
		DM_LatDeg = new FocusText();
		jLabel2 = new javax.swing.JLabel();
		DM_LatMin = new FocusText();
		jLabel3 = new javax.swing.JLabel();
		jPanel4 = new javax.swing.JPanel();
		DM_NorthBtn = new javax.swing.JRadioButton();
		DM_SouthBtn = new javax.swing.JRadioButton();
		DM_Long = new javax.swing.JPanel();
		jLabel4 = new javax.swing.JLabel();
		DM_LongDeg = new FocusText();
		jLabel5 = new javax.swing.JLabel();
		DM_LongMin = new FocusText();
		jLabel6 = new javax.swing.JLabel();
		jPanel6 = new javax.swing.JPanel();
		DM_EastBtn = new javax.swing.JRadioButton();
		DM_WestBtn = new javax.swing.JRadioButton();
		DMS = new javax.swing.JPanel();
		DMS_Lat = new javax.swing.JPanel();
		jLabel7 = new javax.swing.JLabel();
		DMS_LatDeg = new FocusText();
		jLabel8 = new javax.swing.JLabel();
		DMS_LatMin = new FocusText();
		jLabel9 = new javax.swing.JLabel();
		DMS_LatSec = new FocusText();
		jLabel10 = new javax.swing.JLabel();
		jPanel9 = new javax.swing.JPanel();
		DMS_NorthBtn = new javax.swing.JRadioButton();
		DMS_SouthBtn = new javax.swing.JRadioButton();
		DMS_Long = new javax.swing.JPanel();
		jLabel11 = new javax.swing.JLabel();
		DMS_LongDeg = new FocusText();
		jLabel12 = new javax.swing.JLabel();
		DMS_LongMin = new FocusText();
		jLabel13 = new javax.swing.JLabel();
		DMS_LongSec = new FocusText();
		jLabel14 = new javax.swing.JLabel();
		jPanel11 = new javax.swing.JPanel();
		DMS_EastBtn = new javax.swing.JRadioButton();
		DMS_WestBtn = new javax.swing.JRadioButton();
		addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(final java.awt.event.WindowEvent evt) {
				exitForm(evt);
			}
		});

		btnPanel.setLayout(new java.awt.GridLayout(2, 2));

		///////////////////////////////////////////////////////
		// create and insert a panel to hold the depth data
		//////////////////////////////////////////////////////
		final javax.swing.JPanel depthPanel = new javax.swing.JPanel();
		depthPanel.setLayout(new java.awt.GridLayout(1, 2));
		depthLabel = new javax.swing.JLabel("Depth (m)");
		depthValue = new FocusText();
		depthValue.setText("0.0");
		depthPanel.add(depthLabel);
		depthPanel.add(depthValue);
		btnPanel.add(depthPanel);
		btnPanel.add(new javax.swing.JLabel(" "));

		///////////////////////////////////////////////////////
		// continue with the accept/cancel buttons
		//////////////////////////////////////////////////////

		AcceptBtn.setText("Accept");
		AcceptBtn.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				AcceptBtnActionPerformed(evt);
			}
		});
		btnPanel.add(AcceptBtn);

		CancelBtn.setText("Cancel");
		CancelBtn.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				CancelBtnActionPerformed(evt);
			}
		});
		btnPanel.add(CancelBtn);

		getContentPane().add(btnPanel, java.awt.BorderLayout.SOUTH);

		DM.setLayout(new java.awt.GridLayout(1, 2));
		DM.setName("");

		DM_Lat.setLayout(new java.awt.GridLayout(3, 2));

		jLabel1.setText("Deg");
		DM_Lat.add(jLabel1);

		DM_LatDeg.setText("0");
		DM_Lat.add(DM_LatDeg);

		jLabel2.setText("Min");
		DM_Lat.add(jLabel2);

		DM_LatMin.setText("0");
		DM_Lat.add(DM_LatMin);

		jLabel3.setText("Hemi");
		DM_Lat.add(jLabel3);

		jPanel4.setLayout(new java.awt.GridLayout(1, 2));

		DM_NorthBtn.setText("N");
		jPanel4.add(DM_NorthBtn);

		DM_SouthBtn.setText("S");
		jPanel4.add(DM_SouthBtn);

		DM_Lat.add(jPanel4);

		DM.add(DM_Lat);

		DM_Long.setLayout(new java.awt.GridLayout(3, 2));

		jLabel4.setText("Deg");
		DM_Long.add(jLabel4);

		DM_LongDeg.setText("0");
		DM_Long.add(DM_LongDeg);

		jLabel5.setText("Min");
		DM_Long.add(jLabel5);

		DM_LongMin.setText("0");
		DM_Long.add(DM_LongMin);

		jLabel6.setText("Hemi");
		DM_Long.add(jLabel6);

		jPanel6.setLayout(new java.awt.GridLayout(1, 2));

		DM_EastBtn.setText("N");
		DM_EastBtn.setText("E");
		jPanel6.add(DM_EastBtn);

		DM_WestBtn.setText("W");
		jPanel6.add(DM_WestBtn);

		DM_Long.add(jPanel6);

		DM.add(DM_Long);

		entryPanel.addTab("Deg Min", DM);

		DMS.setLayout(new java.awt.GridLayout(1, 2));

		DMS_Lat.setLayout(new java.awt.GridLayout(4, 2));

		jLabel7.setText("Deg");
		DMS_Lat.add(jLabel7);

		DMS_LatDeg.setText("0");
		DMS_Lat.add(DMS_LatDeg);

		jLabel8.setText("Min");
		DMS_Lat.add(jLabel8);

		DMS_LatMin.setText("0");
		DMS_Lat.add(DMS_LatMin);

		jLabel9.setText("Sec");
		DMS_Lat.add(jLabel9);

		DMS_LatSec.setText("0");
		DMS_Lat.add(DMS_LatSec);

		jLabel10.setText("Hemi");
		DMS_Lat.add(jLabel10);

		jPanel9.setLayout(new java.awt.GridLayout(1, 2));

		DMS_NorthBtn.setText("N");
		jPanel9.add(DMS_NorthBtn);

		DMS_SouthBtn.setText("S");
		jPanel9.add(DMS_SouthBtn);

		DMS_Lat.add(jPanel9);

		DMS.add(DMS_Lat);

		DMS_Long.setLayout(new java.awt.GridLayout(4, 2));

		jLabel11.setText("Deg");
		DMS_Long.add(jLabel11);

		DMS_LongDeg.setText("0");
		DMS_Long.add(DMS_LongDeg);

		jLabel12.setText("Min");
		DMS_Long.add(jLabel12);

		DMS_LongMin.setText("0");
		DMS_Long.add(DMS_LongMin);

		jLabel13.setText("Sec");
		DMS_Long.add(jLabel13);

		DMS_LongSec.setText("0");
		DMS_Long.add(DMS_LongSec);

		jLabel14.setText("Hemi");
		DMS_Long.add(jLabel14);

		jPanel11.setLayout(new java.awt.GridLayout(1, 2));

		DMS_EastBtn.setText("E");
		jPanel11.add(DMS_EastBtn);

		DMS_WestBtn.setText("W");
		jPanel11.add(DMS_WestBtn);

		DMS_Long.add(jPanel11);

		DMS.add(DMS_Long);

		entryPanel.addTab("Deg Min Sec", DMS);

		getContentPane().add(entryPanel, java.awt.BorderLayout.CENTER);

	}// GEN-END:initComponents

	/**
	 * initialise the text boxes
	 */
	protected void initData() {
		// initialise the results parameter
		_result = _initial;

		final MWC.Utilities.TextFormatting.BriefFormatLocation.brokenDown _lat = new BriefFormatLocation.brokenDown(
				_result.getLat(), true);
		final MWC.Utilities.TextFormatting.BriefFormatLocation.brokenDown _long = new BriefFormatLocation.brokenDown(
				_result.getLong(), false);

// start with the depth, since it doesn't depend on which
// panel is being used
		depthValue.setText("" + _result.getDepth());

		// first the DM
		DM_LatDeg.setText("" + _lat.deg);
		DM_LatMin.setText("" + _MinDf.format((_lat.min + _lat.sec / (60.0))));
		DM_LongDeg.setText("" + _long.deg);
		DM_LongMin.setText("" + _MinDf.format((_long.min + _long.sec / (60.0))));
		if (_lat.hem == 'N')
			DM_NorthBtn.setSelected(true);
		else
			DM_SouthBtn.setSelected(true);

		if (_long.hem == 'E')
			DM_EastBtn.setSelected(true);
		else
			DM_WestBtn.setSelected(true);

		// now the DMS
		DMS_LatDeg.setText("" + _lat.deg);
		DMS_LatMin.setText("" + _lat.min);
		DMS_LatSec.setText("" + _SecDf.format(_lat.sec));
		DMS_LongDeg.setText("" + _long.deg);
		DMS_LongMin.setText("" + _long.min);
		DMS_LongSec.setText("" + _SecDf.format(_long.sec));

		if (_lat.hem == 'N')
			DMS_NorthBtn.setSelected(true);
		else
			DMS_SouthBtn.setSelected(true);

		if (_long.hem == 'E')
			DMS_EastBtn.setSelected(true);
		else
			DMS_WestBtn.setSelected(true);

	}

}
