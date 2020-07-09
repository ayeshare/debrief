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

package org.mwc.cmap.media.views;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.nebula.widgets.formattedtext.FormattedText;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.mwc.cmap.media.Activator;
import org.mwc.cmap.media.PlanetmayoFormats;
import org.mwc.cmap.media.time.ITimeListener;
import org.mwc.cmap.media.utility.DateUtils;

public class TestHarnessView extends ViewPart {
	private static final String STATE_FIRED_TIME = "firedTime";
	private static final String STATE_TIME_TO_FIRE = "timeToFire";

	public static final String ID = "org.mwc.cmap.media.views.TestHarnessView";

	private IMemento memento;

	private ITimeListener timeListener;

	private Label firedTime;
	private FormattedText timeToFire;
	private Button button;

	@Override
	public void createPartControl(final Composite parent) {
		final Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));

		Label label = new Label(main, SWT.LEFT | SWT.TOP);
		label.setText("Fired Time: ");
		GridData data = new GridData();
		data.heightHint = 50;
		label.setLayoutData(data);

		firedTime = new Label(main, SWT.LEFT);
		firedTime.setText("           -");
		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = SWT.FILL;
		data.heightHint = 50;
		firedTime.setLayoutData(data);

		label = new Label(main, SWT.LEFT);
		label.setText("Time to fire: ");

		final Composite fireComposite = new Composite(main, SWT.NONE);
		fireComposite.setLayout(new RowLayout(SWT.HORIZONTAL));

		timeToFire = new FormattedText(fireComposite);
		timeToFire.setFormatter(PlanetmayoFormats.getInstance().getDateTimeFormatter());
		timeToFire.setValue(new Date());

		button = new Button(fireComposite, SWT.PUSH);
		button.setText("Fire");
		button.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(final SelectionEvent event) {
			}

			@Override
			public void widgetSelected(final SelectionEvent event) {
				final Date fireTime = (Date) timeToFire.getValue();
				DateUtils.removeMilliSeconds(fireTime);
				Activator.getDefault().getTimeProvider().fireNewTime(TestHarnessView.this, fireTime.getTime());
			}
		});
		restoreSavedState();
	}

	@Override
	public void dispose() {
		super.dispose();
		Activator.getDefault().getTimeProvider().removeListener(timeListener);
	}

	@Override
	public void init(final IViewSite site, final IMemento memento) throws PartInitException {
		super.init(site, memento);
		this.memento = memento;
		timeListener = new ITimeListener() {

			SimpleDateFormat dateFormat = PlanetmayoFormats.getInstance().getDateFormat();

			@Override
			public void newTime(final Object src, final long millis) {
				firedTime.setText(dateFormat.format(new Date(millis)));
			}
		};
		Activator.getDefault().getTimeProvider().addListener(timeListener);
	}

	private void restoreSavedState() {
		if (memento == null) {
			return;
		}
		if (memento.getString(STATE_FIRED_TIME) != null) {
			firedTime.setText(memento.getString(STATE_FIRED_TIME));
		}
		try {
			if (memento.getString(STATE_TIME_TO_FIRE) != null) {
				timeToFire.setValue(new Date(Long.parseLong(memento.getString(STATE_TIME_TO_FIRE))));
			}
		} catch (final NumberFormatException ex) {

		}
	}

	@Override
	public void saveState(final IMemento memento) {
		super.saveState(memento);
		final Date date = (Date) timeToFire.getValue();
		memento.putString(STATE_FIRED_TIME, firedTime.getText());
		memento.putString(STATE_TIME_TO_FIRE, Long.toString(date.getTime()));
	}

	@Override
	public void setFocus() {
		firedTime.setFocus();
	}
}
