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

// $RCSfile: SwingCompositeTimeEditor.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.7 $
// $Log: SwingCompositeTimeEditor.java,v

/*
* SwingCompositeTimeEditor.java
*
* Created on 29 March 2000, 10:37
*/

package Debrief.GUI.Tote.Swing.TimeFilter;

import java.awt.FlowLayout;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import MWC.GenericData.HiResDate;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;
import MWC.Utilities.TextFormatting.GMTDateFormat;

/**
 * Class defining panel which will combines a time slider with a text box to
 * provide a constrained time period NOTE that we perform all date manipulation
 * in the GMT time zone, to ensure that track data is exchangeable between time
 * zones.
 *
 * @author Ian.Mayo
 */
public final class SwingCompositeTimeEditor extends javax.swing.JPanel
		implements ChangeListener, java.awt.event.ActionListener {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * the choices for what step size to use
	 */
	static final public int SECOND_STEPS = 0;
	static final public int MINUTE_STEPS = 1;
	static final public int HOUR_STEPS = 2;
	// and of course, microseconds
	static final public int MICRO_STEPS = 4;

	/**
	 * Holds value of property DTG.
	 */
	private HiResDate _DTG = null;
	/**
	 * Holds value of property startDTG.
	 */
	private HiResDate _startDTG;
	/**
	 * Holds value of property endDTG.
	 */
	private HiResDate _endDTG;

	/**
	 * keep an internal scale factor for converting time (integer slider position -
	 * micros) to slider data values (which must be INTs
	 */
	private long SCALE_FACTOR = 60 * 1000 * 1000;
	/**
	 * hardcoded minimum value to use
	 */
	private final int MIN_VALUE = 0;

	/**
	 * format the date (yy/mm/dd) part of the date
	 */
	private final SimpleDateFormat _dateF = new GMTDateFormat("dd/MM/yy");

	/**
	 * format the time (hh:mm:ss) part of the DTG
	 */
	private final SimpleDateFormat _timeF = new GMTDateFormat("HH:mm:ss");

	/**
	 * UI components
	 */
	private javax.swing.JSlider _theSlider;
	private javax.swing.JTextField _dateField;
	private javax.swing.JTextField _timeField;
	private javax.swing.JTextField _microField;

	/**
	 * Creates new form SwingCompositeTimeEditor
	 */

	public SwingCompositeTimeEditor(final String title) {
		initComponents(title);

		// set the min value to
		_theSlider.setMinimum(MIN_VALUE);

		_theSlider.revalidate();

		// register as a listener
		_theSlider.addChangeListener(this);

		_timeField.addActionListener(this);
		_dateField.addActionListener(this);

		// do the micros, if we have to
		if (_microField != null)
			_microField.addActionListener(this);
	}

	/**
	 * event handler for text box
	 *
	 * @param e some auto stuff, we don't use it
	 */
	@Override
	public final void actionPerformed(final java.awt.event.ActionEvent e) {
		// we may not actually have time periods set, so check before we go
		// of half-cocked
		if (_startDTG == null)
			return;

		// produce the date from the values
		final HiResDate tmp = getDateFromTextFields();

		// check the value is in range (just to be sure)
		if ((tmp.greaterThanOrEqualTo(_startDTG)) && (tmp.lessThanOrEqualTo(_endDTG))) {
			_DTG = tmp;
		}

		resetData();

	}

	/**
	 * convert from slider (int) to DTG (long) units
	 *
	 * @param sliderUnit the sliderUnit we are converting
	 * @return DTG
	 */
	private HiResDate convertToDateUnits(final int sliderUnit) {
		// find offset from min value
		long val = sliderUnit - MIN_VALUE;

		// scale it
		val *= SCALE_FACTOR;

		// calculate offset from min date
		final HiResDate res = new HiResDate(0, _startDTG.getMicros() + val);

		// done.
		return res;
	}

	/**
	 * scale from DTG (long) to slider (int) units
	 *
	 * @param dtg the DTG we are converting
	 * @return the slider position to use
	 */
	private int convertToSliderUnits(final HiResDate dtg) {
		int res = 0;

		// just check we have a start time.
		// sometimes it gets mixed up when reloading from XML
		if (_startDTG != null) {
			// calculate offset from start
			long val = dtg.getMicros() - _startDTG.getMicros();

			// scale it
			val /= SCALE_FACTOR;

			// and find offset from min value
			val = MIN_VALUE + val;

			res = (int) val;
		}

		return res;

	}

	/**
	 * Getter for property DTG.
	 *
	 * @return Value of property DTG.
	 */
	public final HiResDate get_DTG() {
		// this is the place where we have to ensure that the slider is up to date for
		// the text
		// pretend the user has pressed RETURN in the text box
		actionPerformed(null);

		// and now return the value
		return _DTG;
	}

	/**
	 * Getter for property endDTG.
	 *
	 * @return Value of property endDTG.
	 */
	public final HiResDate get_endDTG() {
		return _endDTG;
	}

	/**
	 * Getter for property startDTG.
	 *
	 * @return Value of property startDTG.
	 */
	public final HiResDate get_startDTG() {
		return _startDTG;
	}

	/**
	 * extract a date value from the text fields
	 *
	 * @return the DTG
	 */
	private synchronized HiResDate getDateFromTextFields() {
		HiResDate val = null;

		try {
			final String dateTxt = _dateField.getText();
			final String timeTxt = _timeField.getText();
			final long theDate = _dateF.parse(dateTxt).getTime() * 1000;
			final long theTime = _timeF.parse(timeTxt).getTime() * 1000;
			int theMicros = 0;

			// do we have micros?
			if (_microField != null)
				theMicros = Integer.parseInt(_microField.getText());

			/**
			 * note, we did have to determine the timezone offset for the current timezone,
			 * and add it to the date calculated below. Now that we have switched to GMT
			 * this problem has gone away.
			 */
			val = new HiResDate(0, theDate + theTime + theMicros);

		} catch (final ParseException e) {
			MWC.Utilities.Errors.Trace.trace(e);
		}

		return val;
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the FormEditor.
	 */
	private synchronized void initComponents(final String title) {// GEN-BEGIN:initComponents

		final JPanel topPart = new JPanel();
		topPart.setLayout(new java.awt.BorderLayout());

		_theSlider = new javax.swing.JSlider();
		final JPanel _theTextContainer = new JPanel();
		final JPanel jPanel1 = new JPanel();
		jPanel1.setLayout(new FlowLayout());
		final JLabel _dateLabel = new JLabel();
		_dateField = new javax.swing.JTextField(7);
		_dateField.setText("00/00/00");
		_dateField.setToolTipText("Format: " + _dateF.toPattern());
		final JLabel _timeLabel = new JLabel();
		_timeField = new javax.swing.JTextField(6);
		_timeField.setText("00:00:00");
		_timeField.setToolTipText("Format:" + _timeF.toPattern());
		setLayout(new java.awt.BorderLayout());

		topPart.add(_theSlider, java.awt.BorderLayout.SOUTH);
		add("North", topPart);

		_dateLabel.setText("Date:");
		final JLabel theTitle = new JLabel(title);
		theTitle.setHorizontalAlignment(SwingConstants.LEFT);
		jPanel1.add(theTitle);
		jPanel1.add(_dateField);
		_timeLabel.setText("Time:");
		jPanel1.add(_timeField);

		// right, are we doing micros?
		if (HiResDate.inHiResProcessingMode()) {
			_microField = new JTextField(7);
			jPanel1.add(_microField);
		}

		_theTextContainer.add(jPanel1);

		add("Center", _theTextContainer);

	}// GEN-END:initComponents

	/**
	 * update the values in the text
	 */
	private synchronized void resetData() {
		// remove us from the text fields for when they get updated
		_dateField.removeActionListener(this);
		_timeField.removeActionListener(this);
		_theSlider.removeChangeListener(this);

		// and remove th emicros, if we have to
		if (_microField != null)
			_microField.removeActionListener(this);

		// HI-RES NOT DONE - PROVIDE TEXTUIAL OUTPUT OF MILLIS

		// do we have a date yet?
		if (_DTG != null) {
			final Date dateVal = _DTG.getDate();
			_dateField.setText(_dateF.format(dateVal));
			_timeField.setText(_timeF.format(dateVal));

			// are we in hi-res?
			if (_microField != null)
				_microField.setText(DebriefFormatDateTime.formatMicros(_DTG));

			// done.
			setSliderValue(_DTG);
		}

		// and put us back in the listeners
		_dateField.addActionListener(this);
		_timeField.addActionListener(this);
		_theSlider.addChangeListener(this);
		// and remove th emicros, if we have to
		if (_microField != null)
			_microField.addActionListener(this);
	}

	/**
	 * Setter for property DTG.
	 *
	 * @param DTG New value of property DTG.
	 */
	public final void set_DTG(final HiResDate DTG) {

		// don't bother with duff data
		if (DTG == null)
			return;

		this._DTG = DTG;

		// also initialise the data
		resetData();

	}

	/**
	 * Setter for property endDTG.
	 *
	 * @param endDTG New value of property endDTG.
	 */
	public final void set_endDTG(final HiResDate endDTG) {
		// don't bother with duff data
		if (endDTG == null)
			return;

		this._endDTG = endDTG;

		// do we know our DTG?
		if (_DTG != null) {
			// trim it to the time period
			if (_DTG.greaterThan(_endDTG))
				set_DTG(_endDTG);
		}

		// now sort out the end slider value
		_theSlider.setMaximum(convertToSliderUnits(endDTG));
		_theSlider.revalidate();
	}

	/**
	 * Setter for property startDTG.
	 *
	 * @param startDTG New value of property startDTG.
	 */
	public final void set_startDTG(final HiResDate startDTG) {
		// don't bother with duff data
		if (startDTG == null)
			return;

		this._startDTG = startDTG;

		// note, we use min int value as the start value for the slider

		// do we know our DTG?
		if (_DTG != null) {
			// yes - trim it to the start time
			if (_DTG.lessThan(_startDTG))
				set_DTG(_startDTG);
		}

	}

	/**
	 * update the position of the slider to this DTG value
	 *
	 * @param dtg the current DTG
	 */
	private void setSliderValue(final HiResDate dtg) {
		// and set it
		final int newVal = convertToSliderUnits(dtg);

		_theSlider.setValue(newVal);
	}

	/**
	 * set the units for the steps on the slider
	 *
	 * @param stepSize the size of step to use (see public variables for this class)
	 */
	public final void setStepSize(final int stepSize) {
		switch (stepSize) {
		case MICRO_STEPS: {
			SCALE_FACTOR = 1;
			break;
		}
		case SECOND_STEPS: {
			SCALE_FACTOR = 1000 * 1000;
			break;
		}
		case MINUTE_STEPS: {
			SCALE_FACTOR = 60 * 1000 * 1000;
			break;
		}
		case HOUR_STEPS: {
			SCALE_FACTOR = 60l * 60 * 1000 * 1000;
			break;
		}

		}
	}

	/**
	 * event handler
	 *
	 * @param e some auto stuff, we don't use it
	 */
	@Override
	public final void stateChanged(final ChangeEvent e) {
		// have our time ranges been initialised?
		// There's a chance that we have sliders open, but no data with a time
		// representation
		if (_startDTG == null)
			return;

		// retrieve the value
		final HiResDate tmp = convertToDateUnits(_theSlider.getValue());

		// check the value is in range (just to be sure)
		if ((tmp.greaterThanOrEqualTo(_startDTG)) && (tmp.lessThanOrEqualTo(_endDTG))) {
			_DTG = tmp;
		}

		resetData();

	}

}