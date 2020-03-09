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
package info.limpet.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.measure.unit.Unit;

import org.eclipse.january.dataset.DoubleDataset;
import org.eclipse.january.dataset.IDataset;
import org.eclipse.january.metadata.AxesMetadata;

import info.limpet.IChangeListener;
import info.limpet.ICommand;
import info.limpet.IDocument;
import info.limpet.IStoreGroup;

abstract public class Document<T extends Object> implements IDocument<T> {

	private static class DoubleIterator implements Iterator<Double> {
		final private double[] _data;
		private int _ctr;
		private final int _size;
		private final int _offset;

		/**
		 * iterate through this set of January doubles
		 *
		 * @param data the dats to move through
		 * @param offset the offset to the first item
		 */
		private DoubleIterator(final double[] data, final int offset, final int size) {
			_data = data;
			_ctr = offset;
			_offset = offset;
			_size = size;
		}

		@Override
		public boolean hasNext() {
			return _ctr < _offset + _size;
		}

		@Override
		public Double next() {
			return _data[_ctr++];
		}

		@Override
		public void remove() {
			throw new IllegalArgumentException("Remove operation not provided for this iterator");
		}

	};

	// TODO: long-term, find a better place for this
	public static enum InterpMethod {
		Linear, Nearest, Before, After
	}

	/**
	 * _dataset isn't final, sincew we replace it when the document is
	 * re-calculated
	 */
	protected IDataset dataset;
	final protected ICommand predecessor;
	final private List<IChangeListener> changeListeners = new ArrayList<IChangeListener>();
	private transient List<IChangeListener> transientChangeListeners = new ArrayList<IChangeListener>();
	private IStoreGroup parent;
	final private UUID uuid;

	final private List<ICommand> dependents = new ArrayList<ICommand>();

	private Unit<?> indexUnits;

	public Document(final IDataset dataset, final ICommand predecessor) {
		this.dataset = dataset;
		this.predecessor = predecessor;
		uuid = UUID.randomUUID();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see info.limpet.IDocument#addChangeListener(info.limpet.IChangeListener)
	 */
	@Override
	public void addChangeListener(final IChangeListener listener) {
		changeListeners.add(listener);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see info.limpet.IDocument#addDependent(info.limpet.ICommand)
	 */
	@Override
	public void addDependent(final ICommand command) {
		dependents.add(command);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see info.limpet.IDocument#addChangeListener(info.limpet.IChangeListener)
	 */
	@Override
	public void addTransientChangeListener(final IChangeListener listener) {
		// we may need to re-create it, if we've been restored from file
		if (transientChangeListeners == null) {
			transientChangeListeners = new ArrayList<IChangeListener>();
		}
		transientChangeListeners.add(listener);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see info.limpet.IDocument#beingDeleted()
	 */
	@Override
	public void beingDeleted() {
		// ok, detach ourselves from our parent
		final IStoreGroup parent = this.getParent();
		if (parent != null) {
			parent.remove(this);
		}

		final List<IChangeListener> listeners = new ArrayList<IChangeListener>();
		listeners.addAll(getListeners());

		for (final IChangeListener s : listeners) {
			s.collectionDeleted(this);
		}
	}

	/**
	 * temporarily use this - until we're confident about replacing child
	 * Dataset objects
	 *
	 */
	@Override
	public void clearQuiet() {
		dataset = null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see info.limpet.IDocument#fireDataChanged()
	 */
	@Override
	public void fireDataChanged() {
		final List<IChangeListener> listeners = getListeners();
		for (final IChangeListener s : listeners) {
			s.dataChanged(this);
		}
	}

	@Override
	public void fireMetadataChanged() {
		final List<IChangeListener> listeners = getListeners();
		for (final IChangeListener s : listeners) {
			s.metadataChanged(this);
		}
	}

	public IDataset getDataset() {
		return dataset;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see info.limpet.IDocument#getDependents()
	 */
	@Override
	public List<ICommand> getDependents() {
		return dependents;
	}

	@Override
	public double getIndexAt(final int i) {
		// hmm, if the dataset has been sliced, we may not be starting at
		// element one.
		final DoubleDataset ds = getIndexValues();
		return ds.getData()[i + ds.getOffset()];
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see info.limpet.IDocument#getIndices()
	 */
	@Override
	public Iterator<Double> getIndexIterator() {
		final DoubleDataset values = getIndexValues();
		final double[] items = values.getData();
		return new DoubleIterator(items, values.getOffset(), values.getSize());
	}

	@UIProperty(name = "IndexUnits", category = "Label", visibleWhen = "indexed == true")
	@Override
	public Unit<?> getIndexUnits() {
		return indexUnits;
	}

	@Override
	public DoubleDataset getIndexValues() {
		DoubleDataset res;

		if (isIndexed()) {
			final AxesMetadata am = dataset.getFirstMetadata(AxesMetadata.class);

			final DoubleDataset dd = (DoubleDataset) am.getAxes()[0];
			res = dd;
		} else {
			throw new IllegalArgumentException("Dataset isn't indexed");
		}

		return res;
	}

	/**
	 * collate a list of the listeners for this document
	 *
	 * @return
	 */
	private List<IChangeListener> getListeners() {
		final List<IChangeListener> listeners = new ArrayList<IChangeListener>();
		listeners.addAll(changeListeners);
		listeners.addAll(dependents);
		// since the TCLs are (by definition) transient, we may not have any
		// (such as after file restore). So, first check that they're present
		if (transientChangeListeners != null) {
			listeners.addAll(transientChangeListeners);
		}
		return listeners;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see info.limpet.IDocument#getName()
	 */
	@Override
	@UIProperty(name = "Name", category = UIProperty.CATEGORY_LABEL)
	public String getName() {
		final String res;
		if (dataset != null) {
			res = dataset.getName();
		} else {
			res = null;
		}
		return res;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see info.limpet.IDocument#getParent()
	 */
	@Override
	public IStoreGroup getParent() {
		return parent;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see info.limpet.IDocument#getPrecedent()
	 */
	@Override
	public ICommand getPrecedent() {
		return predecessor;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see info.limpet.IDocument#getUUID()
	 */
	@Override
	public UUID getUUID() {
		return uuid;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see info.limpet.IDocument#isIndexed()
	 */
	@Override
	@UIProperty(name = "Indexed", category = UIProperty.CATEGORY_LABEL)
	public boolean isIndexed() {
		final boolean res;
		if (dataset != null) {
			// is there an axis?
			final AxesMetadata am = dataset.getFirstMetadata(AxesMetadata.class);

			// is it a time axis?
			res = am != null;
		} else {
			res = false;
		}

		return res;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see info.limpet.IDocument#isQuantity()
	 */
	@Override
	@UIProperty(name = "Quantity", category = UIProperty.CATEGORY_LABEL)
	public boolean isQuantity() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * info.limpet.IDocument#removeChangeListener(info.limpet.IChangeListener)
	 */
	@Override
	public void removeChangeListener(final IChangeListener listener) {
		changeListeners.remove(listener);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see info.limpet.IDocument#removeDependent(info.limpet.ICommand)
	 */
	@Override
	public void removeDependent(final ICommand command) {
		dependents.remove(command);
	}

	@Override
	public void removeTransientChangeListener(final IChangeListener collectionChangeListener) {
		transientChangeListeners.remove(collectionChangeListener);
	}

	public void setDataset(final IDataset dataset) {
		this.dataset = dataset;
	}

	/**
	 * set the units for the index data
	 *
	 * @param units
	 */
	@Override
	public void setIndexUnits(final Unit<?> units) {
		if (!isIndexed()) {
			throw new IllegalArgumentException("Index not present, cannot set index units");
		}
		indexUnits = units;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see info.limpet.IDocument#setName(java.lang.String)
	 */
	@Override
	public void setName(final String name) {
		dataset.setName(name);

		fireDataChanged();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see info.limpet.IDocument#setParent(info.limpet.IStoreGroup)
	 */
	@Override
	public void setParent(final IStoreGroup parent) {
		this.parent = parent;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see info.limpet.IDocument#size()
	 */
	@Override
	@UIProperty(name = "Size", category = UIProperty.CATEGORY_LABEL)
	public int size() {
		return dataset.getSize();
	}

	/**
	 * produce this document as a listing
	 *
	 * @return
	 */
	abstract public String toListing();

	@Override
	final public String toString() {
		return getName();
	}
}
