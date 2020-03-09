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

package org.mwc.cmap.core.property_support;

import java.text.DecimalFormat;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Slider;

import MWC.GUI.Properties.BoundedInteger;
import MWC.GUI.Properties.SteppingBoundedInteger;

public class BoundedIntegerHelper extends EditorHelper {

	private static class SliderEditor extends CellEditor {

		Composite _myControl = null;
		protected Label _myLabel;
		protected Slider _theSlider;

		DecimalFormat _df = null;

		public SliderEditor(final Composite parent) {
			super(parent, SWT.NONE);
		}

		@Override
		protected Control createControl(final Composite parent) {
			final Font font = parent.getFont();
			final Color bg = parent.getBackground();

			_myControl = new Composite(parent, getStyle());
			_myControl.setFont(font);
			_myControl.setBackground(bg);

//			RowLayout rl = new RowLayout();
//			rl.wrap = false;
//			rl.type = SWT.HORIZONTAL;
//			rl.marginHeight = 0;
//			rl.marginWidth = 0;
			final GridLayout rl = new GridLayout();
			rl.marginWidth = 0;
			rl.marginHeight = 0;
			rl.numColumns = 8;

			_myControl.setLayout(rl);
//
			_myLabel = new Label(_myControl, SWT.NONE);
			_myLabel.setText("000");
			_myLabel.setBackground(bg);
			final GridData gd1 = new GridData(GridData.FILL_HORIZONTAL);
			_myLabel.setLayoutData(gd1);

			_theSlider = new Slider(_myControl, SWT.NONE);
			final GridData gd2 = new GridData(GridData.FILL_HORIZONTAL);
			gd2.horizontalSpan = 7;
			_theSlider.setLayoutData(gd2);
			_theSlider.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetDefaultSelected(final SelectionEvent e) {

				}

				@Override
				public void widgetSelected(final SelectionEvent e) {
					_myLabel.setText(formatMe(_theSlider.getSelection()));
					_myLabel.update();
				}
			});

			return _myControl;
		}

		@Override
		protected Object doGetValue() {
			Object res = null;
			if (_theSlider != null) {
				res = new BoundedInteger(_theSlider.getSelection(), _theSlider.getMinimum(), _theSlider.getMaximum());
			}
			return res;
		}

		@Override
		protected void doSetFocus() {
			_theSlider.setFocus();
		}

		@Override
		protected void doSetValue(final Object value) {
			final BoundedInteger curr = (BoundedInteger) value;
			if (_myLabel != null)
				_myLabel.setText(formatMe(curr.getCurrent()));
			if (_theSlider != null) {
				_theSlider.setMinimum(curr.getMin());
				// we have to add one to the max value, since it appears to be exclusive of the
				// max value
				_theSlider.setMaximum(curr.getMax() + 1);
				_theSlider.setSelection(curr.getCurrent());
				_theSlider.setThumb(1);
			}
		}

		protected String formatMe(final int value) {
			if (_df == null)
				_df = new DecimalFormat("000");

			return _df.format(value);
		}

		/**
		 * @return
		 */
		@Override
		public LayoutData getLayoutData() {
			final CellEditor.LayoutData res = super.getLayoutData();
			res.grabHorizontal = true;
			return res;
		}

	}

	public static class SteppingBoundedIntegerHelper extends BoundedIntegerHelper {
		public SteppingBoundedIntegerHelper() {
			super(SteppingBoundedInteger.class);
		}

		@Override
		public CellEditor getCellEditorFor(final Composite parent) {
			return new SteppingSliderEditor(parent);
		}

		@Override
		public ILabelProvider getLabelFor(final Object currentValue) {
			final ILabelProvider label1 = new LabelProvider() {
				@Override
				public String getText(final Object element) {
					final SteppingBoundedInteger rgb = (SteppingBoundedInteger) element;
					final String res = "" + rgb.getCurrent();
					return res;
				}

			};
			return label1;
		}
	}

	private static class SteppingSliderEditor extends SliderEditor {

		public SteppingSliderEditor(final Composite parent) {
			super(parent);
		}

		@Override
		protected Object doGetValue() {
			Object res = null;
			if (_theSlider != null) {
				res = new SteppingBoundedInteger(_theSlider.getSelection(), _theSlider.getMinimum(),
						_theSlider.getMaximum(), _theSlider.getIncrement());
			}
			return res;
		}

		@Override
		protected void doSetValue(final Object value) {

			final SteppingBoundedInteger curr = (SteppingBoundedInteger) value;
			if (_myLabel != null)
				_myLabel.setText(formatMe(curr.getCurrent()));
			if (_theSlider != null) {
				_theSlider.setMinimum(curr.getMin());
				// we have to add one to the max value, since it appears to be exclusive of the
				// max value
				_theSlider.setMaximum(curr.getMax() + 1);
				_theSlider.setSelection(curr.getCurrent());
				_theSlider.setIncrement(curr.getStep());
				_theSlider.setThumb(1);
			}
		}
	}

	public BoundedIntegerHelper() {
		super(BoundedInteger.class);
	}

	/**
	 * provide better constructor - so child implementations can pass target class
	 * back up the chain
	 *
	 * @param targetClass
	 */
	@SuppressWarnings("rawtypes")
	public BoundedIntegerHelper(final Class targetClass) {
		super(targetClass);
	}

	@Override
	public CellEditor getCellEditorFor(final Composite parent) {
		return new SliderEditor(parent);
	}

	@Override
	public ILabelProvider getLabelFor(final Object currentValue) {
		final ILabelProvider label1 = new LabelProvider() {
			@Override
			public String getText(final Object element) {
				final BoundedInteger rgb = (BoundedInteger) element;
				final String res = "" + rgb.getCurrent();
				return res;
			}

		};
		return label1;
	}

	@Override
	public Object translateFromSWT(final Object value) {
		return value;
	}

	////////////////////////////////////////////////////////
	// and now for stepping bounded integer support
	///////////////////////////////////////////////////////

	@Override
	public Object translateToSWT(final Object value) {
		return value;
	}

}