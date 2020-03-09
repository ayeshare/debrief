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

package org.mwc.cmap.gridharness.views;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.mwc.cmap.gridharness.data.UnitsSet;
import org.mwc.cmap.gridharness.data.UnitsSet.Unit;
import org.mwc.cmap.gridharness.data.ValueInUnits;

public abstract class ValueWithUnitsCellEditor2 extends CellEditor implements MultiControlCellEditor {

	/**
	 * hmm, the text bit.
	 *
	 */
	Text _myText;

	/**
	 * and the drop-down units bit
	 *
	 */
	Combo _myCombo;

	final private String _textTip;

	final private String _comboTip;

	private ValueInUnits _value;

	private UnitsSet _unitsSet;

	private String[] _unitLabels;

	public ValueWithUnitsCellEditor2(final Composite parent, final String textTip, final String comboTip) {
		super(parent);
		_textTip = textTip;
		_comboTip = comboTip;
	}

	@Override
	protected Control createControl(final Composite parent) {
		_value = initializeValue();
		_unitsSet = _value.getUnitsSet();
		_unitLabels = _unitsSet.getAllUnitLabels();
		return createControl(parent, _textTip, _comboTip);
	}

	protected Control createControl(final Composite parent, final String tipOne, final String tipTwo) {
		final Composite holder = new Composite(parent, SWT.NONE);
		final RowLayout rows = new RowLayout();
		rows.marginLeft = rows.marginRight = 0;
		rows.marginTop = rows.marginBottom = 0;
		rows.fill = false;
		rows.spacing = 0;
		rows.pack = false;
		holder.setLayout(rows);

		_myText = new Text(holder, SWT.BORDER);
		_myText.setTextLimit(7);
		_myText.setToolTipText(tipOne);
		_myCombo = new Combo(holder, SWT.DROP_DOWN);
		_myCombo.setItems(_unitLabels);
		_myCombo.setToolTipText(tipTwo);

		new MultiControlFocusHandler(_myText, _myCombo) {

			@Override
			protected void focusReallyLost(final FocusEvent e) {
				ValueWithUnitsCellEditor2.this.focusLost();
			}
		};

		return holder;
	}

	@Override
	protected Object doGetValue() {
		final String distTxt = _myText.getText();
		final double dist = new Double(distTxt).doubleValue();
		final int selectedIndex = _myCombo.getSelectionIndex();
		final String selectedLabel = _myCombo.getItem(selectedIndex);
		final UnitsSet.Unit units = _unitsSet.findUnit(selectedLabel);

		final ValueInUnits result = _value.makeCopy();
		result.setValues(dist, units);
		return result;
	}

	@Override
	protected void doSetFocus() {
		_myText.setFocus();
	}

	@Override
	protected void doSetValue(final Object value) {
		final ValueInUnits valueImpl = (ValueInUnits) value;
		final UnitsSet.Unit mainUnit = _unitsSet.getMainUnit();
		final double doubleValue = valueImpl.getValueIn(mainUnit);
		_value.setValues(doubleValue, mainUnit);
		final UnitsSet.Unit bestFitUnit = _unitsSet.selectUnitsFor(doubleValue);
		_myCombo.select(getUndexFor(bestFitUnit));
		_myText.setText(String.valueOf(_value.getValueIn(bestFitUnit)));
	}

	@Override
	public Control getLastControl() {
		return _myCombo;
	}

	private int getUndexFor(final Unit unit) {
		for (int i = 0; i < _unitLabels.length; i++) {
			if (unit.getLabel().equals(_unitLabels[i])) {
				return i;
			}
		}
		throw new IllegalStateException("Can't find unit: " + unit.getLabel() + " in units set: " + _unitsSet);
	}

	protected abstract ValueInUnits initializeValue();

}