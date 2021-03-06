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
/*****************************************************************************
 *  Limpet - the Lightweight InforMation ProcEssing Toolkit
 *  http://limpet.info
 *
 *  (C) 2015-2016, Deep Blue C Technologies Ltd
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the Eclipse Public License v1.0
 *  (http://www.eclipse.org/legal/epl-v10.html)
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *****************************************************************************/
package info.limpet.analysis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import info.limpet.IStoreItem;

public abstract class AnalysisLibrary extends CoreAnalysis {
	private final List<IAnalysis> _library;
	private final List<String> _titles = new ArrayList<String>();
	private final List<String> _values = new ArrayList<String>();

	public AnalysisLibrary() {
		super("Library of analysis routines");

		_library = new ArrayList<IAnalysis>();

		_library.add(new GeneralDescription() {
			@Override
			protected void presentResults(final List<String> titles, final List<String> values) {
				output(getName(), titles, values);
			}
		});
		_library.add(new SimpleDescriptiveQuantity() {
			@Override
			protected void presentResults(final List<String> titles, final List<String> values) {
				output(getName(), titles, values);
			}
		});
		_library.add(new SimpleDescriptiveObject() {
			@Override
			protected void presentResults(final List<String> titles, final List<String> values) {
				output(getName(), titles, values);
			}
		});
		_library.add(new QuantityFrequencyBins() {
			@Override
			protected void presentResults(final List<String> titles, final List<String> values) {
				output(getName(), titles, values);
			}
		});
		_library.add(new ObjectFrequencyBins() {
			@Override
			protected void presentResults(final List<String> titles, final List<String> values) {
				output(getName(), titles, values);
			}
		});

	}

	@Override
	public void analyse(final List<IStoreItem> selection) {
		// clear the lists
		_titles.clear();
		_values.clear();

		final Iterator<IAnalysis> iter = _library.iterator();
		while (iter.hasNext()) {
			// get the next analysis library
			final IAnalysis iAnalysis = iter.next();

			// and run this analysis
			iAnalysis.analyse(selection);
		}

		presentResults(_titles, _values);

	}

	protected void output(final String title, final List<String> titles, final List<String> values) {
		if (titles.size() > 0) {
			_titles.add("= " + title + " =");
			_values.add("");
			_titles.addAll(titles);
			_values.addAll(values);
		}
	}

	protected abstract void presentResults(List<String> titles, List<String> values);

}
