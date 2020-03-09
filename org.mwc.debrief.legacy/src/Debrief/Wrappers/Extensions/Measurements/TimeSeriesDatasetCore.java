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
package Debrief.Wrappers.Extensions.Measurements;

import java.util.Iterator;

import org.eclipse.january.dataset.Dataset;
import org.eclipse.january.dataset.DoubleDataset;
import org.eclipse.january.dataset.LongDataset;
import org.eclipse.january.metadata.AxesMetadata;

abstract public class TimeSeriesDatasetCore extends TimeSeriesCore {

	protected static class DoubleIterator implements Iterator<Double> {
		final private DoubleDataset _lData;
		int index = 0;

		public DoubleIterator(final DoubleDataset dataset) {
			_lData = dataset;
		}

		@Override
		public boolean hasNext() {
			return index != _lData.getSize();
		}

		@Override
		public Double next() {
			return _lData.get(index++);
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("remove() not supported in this iterator");
		}
	}

	private static class LongIterator implements Iterator<Long> {
		final private LongDataset _lData;
		int index = 0;

		public LongIterator(final LongDataset dataset) {
			_lData = dataset;
		}

		@Override
		public boolean hasNext() {
			return index != _lData.getSize();
		}

		@Override
		public Long next() {
			return _lData.get(index++);
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("remove() not supported in this iterator");
		}
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * where our dataset is stored
	 *
	 */
	protected Dataset _data;

	/**
	 * easy-access copy of the time indices
	 *
	 */
	private LongDataset _times;

	public TimeSeriesDatasetCore(final String units) {
		super(units);
	}

	public Dataset getDataset() {
		return _data;
	}

	/**
	 * get the index on (or after) the specified time
	 *
	 * @param time
	 * @return
	 */
	@Override
	public int getIndexNearestTo(final long time) {
		final Iterator<Long> timeIter = getIndices();
		int ctr = 0;
		while (timeIter.hasNext()) {
			final Long val = timeIter.next();
			if (val >= time) {
				if (ctr == 0 && val > time) {
					// hold on, our first value is greater than the time. don't bother
					return INVALID_INDEX;
				} else {
					return ctr;
				}
			}

			ctr++;
		}

		// ok, didn't work. return negative index
		return INVALID_INDEX;
	}

	@Override
	public Iterator<Long> getIndices() {
		// create an iterator for this data
		return new LongIterator(getTimes());
	}

	@Override
	public String getName() {
		return _data.getName();
	}

	public LongDataset getTimes() {
		if (_times == null) {
			// get the axes metadata
			final AxesMetadata axes = _data.getFirstMetadata(AxesMetadata.class);
			_times = (LongDataset) axes.getAxis(0)[0];
		}

		return _times;
	}

	@Override
	public void setName(final String name) {
		_data.setName(name);
	}

	@Override
	public int size() {
		return _data.getSize();
	}

}
