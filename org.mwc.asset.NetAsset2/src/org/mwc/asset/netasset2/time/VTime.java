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

package org.mwc.asset.netasset2.time;

import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

public class VTime extends Composite implements IVTime, IVTimeControl {
	private final Text _time;
	private final Button btnStep;
	private final Button btnPlay;
	private final Button btnStop;
	private final Button btnFaster;
	private final Button btnSlower;
	private final Composite composite;

	/**
	 * Create the composite.
	 *
	 * @param parent
	 * @param style
	 */
	public VTime(final Composite parent, final int style) {
		super(parent, style);
		setLayout(new RowLayout(SWT.HORIZONTAL));

		_time = new Text(this, SWT.BORDER);
		_time.setLayoutData(new RowData(137, SWT.DEFAULT));
		_time.setText("00/00/00 00:00:00");

		composite = new Composite(this, SWT.NONE);
		final RowLayout rl_composite = new RowLayout(SWT.HORIZONTAL);
		rl_composite.justify = true;
		composite.setLayout(rl_composite);
		composite.setLayoutData(new RowData(116, 55));

		btnStep = new Button(composite, SWT.FLAT);
		btnStep.setEnabled(false);
		btnStep.setText("Step");

		btnStop = new Button(composite, SWT.FLAT);
		btnStop.setText("Stop");
		btnStop.setEnabled(false);

		btnPlay = new Button(composite, SWT.FLAT);
		btnPlay.setText(IVTimeControl.PLAY);
		btnPlay.setEnabled(false);

		btnFaster = new Button(composite, SWT.FLAT);
		btnFaster.setText("Faster");
		btnFaster.setEnabled(false);

		btnSlower = new Button(composite, SWT.FLAT);
		btnSlower.setText("Slower");
		btnSlower.setEnabled(false);

	}

	@Override
	public void addFasterListener(final SelectionListener listener) {
		btnFaster.addSelectionListener(listener);
	}

	@Override
	public void addPlayListener(final SelectionListener listener) {
		btnPlay.addSelectionListener(listener);
	}

	@Override
	public void addSlowerListener(final SelectionListener listener) {
		btnSlower.addSelectionListener(listener);
	}

	@Override
	public void addStepListener(final SelectionListener listener) {
		btnStep.addSelectionListener(listener);
	}

	@Override
	public void addStopListener(final SelectionListener listener) {
		btnStop.addSelectionListener(listener);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	@Override
	public void newTime(final long newTime) {
		final Date dt = new Date(newTime);
		final String date = dt.toString();

		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				if (!_time.isDisposed())
					_time.setText(date);
			}
		});
	}

	@Override
	public void setEnabled(final boolean val) {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				if (!_time.isDisposed()) {
					_time.setEnabled(val);
					btnStep.setEnabled(val);
					btnPlay.setEnabled(val);
					btnStop.setEnabled(val);
					btnFaster.setEnabled(val);
					btnSlower.setEnabled(val);
				}
			}
		});
	}

	@Override
	public void setPlayLabel(final String text) {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				if (btnPlay.isDisposed())
					btnPlay.setText(text);
			}
		});
	}

}
