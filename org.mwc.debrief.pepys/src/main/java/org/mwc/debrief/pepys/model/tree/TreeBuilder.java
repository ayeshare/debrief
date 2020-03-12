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

package org.mwc.debrief.pepys.model.tree;

import java.beans.PropertyVetoException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.mwc.debrief.pepys.model.AbstractConfiguration;
import org.mwc.debrief.pepys.model.TypeDomain;
import org.mwc.debrief.pepys.model.bean.AbstractBean;
import org.mwc.debrief.pepys.model.bean.Datafile;
import org.mwc.debrief.pepys.model.bean.Sensor;
import org.mwc.debrief.pepys.model.db.DatabaseConnection;
import org.mwc.debrief.pepys.model.tree.TreeNode.NodeType;

import junit.framework.TestCase;

public class TreeBuilder {

	/**
	 * Method that builds the tree structure from top to bottom.
	 * 
	 * @param items
	 * @param root
	 * @param sensors
	 * @param datafiles
	 * @return
	 */
	public static TreeNode buildStructure(final AbstractBean[] items, final TreeNode root,
			final TreeMap<Integer, Sensor> sensors, final TreeMap<Integer, Datafile> datafiles) {

		/*for (AbstractBean item : items) {
			final String datafileName = datafiles.get(item.getSource()).getReference();

			TreeNode datafileNode = root.getChild(datafileName);
			if (datafileNode == null) {
				datafileNode = new TreeNode(TreeNode.NodeType.DATAFILES, datafileName, null, root);
				root.addChild(datafileNode);
			}

			TreeNode measureNode = datafileNode.getChild(item.getMeasureName());
			if (measureNode == null) {
				measureNode = new TreeNode(TreeNode.NodeType.MEASURE, item.getMeasureName(), null, datafileNode);
				datafileNode.addChild(measureNode);
			}

			TreeNode leaf = new TreeNode(NodeType.VALUE, item.getMyName(), item);

			if (item.getSensor() == -1) {
				// It has an exception in the structure, we simply add the leaf.
				measureNode.addChild(leaf);
			} else {
				final String sensorName = sensors.get(item.getSensor()).getName();
				TreeNode sensorNode = measureNode.getChild(sensorName);
				if (sensorNode == null) {
					sensorNode = new TreeNode(TreeNode.NodeType.SENSOR, sensorName, null);
					measureNode.addChild(sensorNode);
				}

				sensorNode.addChild(leaf);
			}
		}*/

		return root;
	}

	public static TreeNode buildStructure(final AbstractBean[] items, final TreeNode root)
			throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, PropertyVetoException, SQLException {
		final TreeMap<Integer, Sensor> sensors = new TreeMap<Integer, Sensor>();
		final TreeMap<Integer, Datafile> datafiles = new TreeMap<Integer, Datafile>();

		for (Datafile datafile : DatabaseConnection.getInstance().listAll(Datafile.class, null)) {
			datafiles.put(datafile.getDatafile_id(), datafile);
		}

		for (Sensor sensor : DatabaseConnection.getInstance().listAll(Sensor.class, null)) {
			sensors.put(sensor.getSensor_id(), sensor);
		}

		buildStructure(items, root, sensors, datafiles);

		return root;
	}

	public static void buildStructure(final AbstractConfiguration configuration)
			throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, PropertyVetoException, SQLException {
		configuration.getTreeModel().removeAllChildren();

		final ArrayList<AbstractBean> allItems = new ArrayList<AbstractBean>();
		for (TypeDomain domain : configuration.getDatafileTypeFilters()) {
			final Class<AbstractBean> currentBeanType = domain.getDatatype();

			if (AbstractBean.class.isAssignableFrom(currentBeanType) && domain.isChecked()) {

				// TODO FILTERING HERE
				String filter = null;
				List<? extends AbstractBean> currentItems = (List<? extends AbstractBean>) DatabaseConnection
						.getInstance().listAll(currentBeanType, null);
				allItems.addAll(currentItems);
			}
		}

		buildStructure(allItems.toArray(new AbstractBean[] {}), configuration.getTreeModel());
	}
	
	public class TreeBuilderTest extends TestCase{
		
		public void testTreeBuilder() {
			
		}
	}
}
