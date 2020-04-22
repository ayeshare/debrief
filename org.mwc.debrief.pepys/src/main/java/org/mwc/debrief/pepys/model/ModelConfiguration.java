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

package org.mwc.debrief.pepys.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import org.mwc.debrief.pepys.model.bean.Comment;
import org.mwc.debrief.pepys.model.bean.Contact;
import org.mwc.debrief.pepys.model.bean.State;
import org.mwc.debrief.pepys.model.db.DatabaseConnection;
import org.mwc.debrief.pepys.model.tree.TreeBuilder;
import org.mwc.debrief.pepys.model.tree.TreeNode;
import org.mwc.debrief.pepys.model.tree.TreeStructurable;

import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.TimePeriod.BaseTimePeriod;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

public class ModelConfiguration implements AbstractConfiguration {

	interface InternTreeItemFiltering {
		boolean isAcceptable(final TreeStructurable _item);
	}

	@Override
	public String getSearch() {
		return searchText;
	}

	@Override
	public void setSearch(final String _newSearch) {
		final String oldSearch = searchText;
		searchText = _newSearch;

		if (_pSupport != null) {
			final java.beans.PropertyChangeEvent pce = new PropertyChangeEvent(this, SEARCH_PROPERTY, oldSearch,
					searchText);
			_pSupport.firePropertyChange(pce);
		}
	}

	private PropertyChangeSupport _pSupport = null;

	private final ArrayList<TypeDomain> currentDatatype = new ArrayList<TypeDomain>();

	private WorldArea currentArea = null;

	private TimePeriod currentPeriod = new BaseTimePeriod();

	private final TreeNode treeModel = new TreeNode(TreeNode.NodeType.ROOT, "", null);

	private PepysConnectorBridge _bridge;

	private Collection<TreeStructurable> currentItems;

	private String filterText = "";
	
	private String searchText = "";

	public ModelConfiguration() {
		final Calendar twentyYearsAgoCal = Calendar.getInstance();
		twentyYearsAgoCal.add(Calendar.YEAR, -20);

		currentPeriod.setStartDTG(new HiResDate(twentyYearsAgoCal.getTime()));
	}

	@Override
	public void addDatafileTypeFilter(final TypeDomain newType) {
		currentDatatype.add(newType);
	}

	@Override
	public void addPropertyChangeListener(final PropertyChangeListener l) {
		if (_pSupport == null) {
			_pSupport = new PropertyChangeSupport(this);
		}
		_pSupport.addPropertyChangeListener(l);
	}

	@Override
	public void apply() throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, PropertyVetoException, SQLException,
			ClassNotFoundException, IOException {

		currentItems = TreeBuilder.buildStructure(this);
		updateTree();

		if (_pSupport != null) {
			final java.beans.PropertyChangeEvent pce = new PropertyChangeEvent(this, TREE_MODEL, null, treeModel);
			_pSupport.firePropertyChange(pce);
		}
	}

	@Override
	public void doImport() {
		if (_bridge == null) {
			/**
			 * In case we don't have a bridge to Full Debrief, it means
			 * we are probably running an unit test (or the deattached version,
			 * then simply do a mockup import process (print to sout) :) 
			 */
			doImportProcessMockup(treeModel);
		} else {
			/**
			 * Import process receives a filter method which is used to confirm if the 
			 * node is going to be imported to Debrief.
			 * 
			 * I am using it to import first all the NON Contacts nodes, and 
			 * after that I import only the Contact nodes. It will ensure
			 * that we will have already all the related tracks.
			 */
			doImport(treeModel, new InternTreeItemFiltering() {

				@Override
				public boolean isAcceptable(final TreeStructurable _item) {
					return !(_item instanceof Contact);
				}
			});
			doImport(treeModel, new InternTreeItemFiltering() {

				@Override
				public boolean isAcceptable(final TreeStructurable _item) {
					return _item instanceof Contact;
				}
			});
		}
	}

	private void doImport(final TreeNode treeModel, final InternTreeItemFiltering filter) {
		if (treeModel.isChecked()) {
			for (final TreeStructurable item : treeModel.getItems()) {
				if (filter.isAcceptable(item)) {
					item.doImport(_bridge.getLayers());
				}
			}
		}
		for (final TreeNode child : treeModel.getChildren()) {
			doImport(child, filter);
		}
	}

	private void doImportProcessMockup(final TreeNode treeModel) {
		if (treeModel.isChecked()) {
			for (final TreeStructurable item : treeModel.getItems()) {
				System.out.println("Importing " + treeModel.getName() + " -> " + item);
			}
		}
		for (final TreeNode child : treeModel.getChildren()) {
			doImportProcessMockup(child);
		}
	}

	@Override
	public boolean doTestQuery() throws SQLException {
		return DatabaseConnection.getInstance().doTestQuery(new Class[] { Contact.class, State.class, Comment.class });
	}

	@Override
	public WorldArea getCurrentArea() {
		return currentArea;
	}

	@Override
	public Collection<TypeDomain> getDatafileTypeFilters() {
		return currentDatatype;
	}

	@Override
	public WorldLocation getDefaultBottomRight() {
		return new WorldLocation(-45, 80, 0);
	}

	@Override
	public WorldLocation getDefaultTopLeft() {
		return new WorldLocation(65, -125, 0);
	}

	@Override
	public String getFilter() {
		return filterText;
	}

	@Override
	public TimePeriod getTimePeriod() {
		return currentPeriod;
	}

	@Override
	public TreeNode getTreeModel() {
		return treeModel;
	}

	@Override
	public void removeDatafileTypeFilter(final TypeDomain typeToRemove) {
		currentDatatype.remove(typeToRemove);
	}

	@Override
	public void removePropertyChangeListener(final PropertyChangeListener l) {
		if (_pSupport != null) {
			_pSupport.removePropertyChangeListener(l);
		}
	}

	@Override
	public void setArea(final WorldArea newArea) {
		final WorldArea oldArea = currentArea;
		currentArea = newArea;

		if (_pSupport != null) {
			final java.beans.PropertyChangeEvent pce = new PropertyChangeEvent(this, AREA_PROPERTY, oldArea,
					currentArea);
			_pSupport.firePropertyChange(pce);
		}
	}

	@Override
	public void setCurrentViewport() {
		if (_bridge != null) {
			setArea(_bridge.getCurrentArea());
		} else {
			final WorldArea demoArea = new WorldArea(getDefaultTopLeft(), getDefaultBottomRight());
			setArea(demoArea);
		}
	}

	@Override
	public void setFilter(final String _newFilter) {
		filterText = _newFilter;
	}

	@Override
	public void setPepysConnectorBridge(final PepysConnectorBridge _bridge) {
		this._bridge = _bridge;
	}

	@Override
	public void setTimePeriod(final TimePeriod newPeriod) {
		final TimePeriod oldPeriod = currentPeriod;
		currentPeriod = newPeriod;

		if (_pSupport != null) {
			final java.beans.PropertyChangeEvent pce = new PropertyChangeEvent(this, PERIOD_PROPERTY, oldPeriod,
					currentPeriod);
			_pSupport.firePropertyChange(pce);
		}
	}

	@Override
	public void updateTree() {
		if (currentItems != null) {
			TreeBuilder.buildStructure(currentItems.toArray(new TreeStructurable[] {}), getTreeModel());

			if (_pSupport != null) {
				final java.beans.PropertyChangeEvent pce = new PropertyChangeEvent(this, TREE_MODEL, null, treeModel);
				_pSupport.firePropertyChange(pce);
			}
		}
	}

}
