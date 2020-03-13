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

package org.mwc.debrief.pepys.model.db;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Properties;

import org.postgis.PGbox3d;
import org.postgis.PGgeometry;
import org.postgis.Point;
import org.postgresql.geometric.PGpoint;
import org.postgresql.util.PGobject;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.impl.NewProxyConnection;
import com.mchange.v2.c3p0.impl.NewProxyConnectionUnwrapper;

import MWC.GenericData.TimePeriod;
import MWC.GenericData.WorldLocation;

public class PostgresDatabaseConnection extends DatabaseConnection {

	private static final String DATABASE_FILE_PATH = "postgresql-pepysdb.alwaysdata.net/pepysdb_core";

	public PostgresDatabaseConnection() {
		super(); // Just formality :)
	}

	@Override
	public DatabaseConnection createInstance() throws PropertyVetoException {
		if (INSTANCE == null) {
			final PostgresDatabaseConnection newInstance = new PostgresDatabaseConnection();
			newInstance.initialize();
			INSTANCE = newInstance;
		}
		return INSTANCE;
	}

	private void initialize() throws PropertyVetoException {
		final Properties props = new Properties();
		props.setProperty("user", "pepysdb_dev");
		props.setProperty("password", "DBC_P3PYS");
		props.setProperty("ssl", "false");

		pool = new ComboPooledDataSource();
		pool.setCheckoutTimeout(TIME_OUT);
		pool.setDriverClass("org.postgresql.Driver");
		final String completePath = "jdbc:postgresql://" + DATABASE_FILE_PATH;
		pool.setJdbcUrl(completePath);
		pool.setProperties(props);
	}

	@Override
	protected String createLocationQuery(String tableName, String columnName) {
		return getAlias(tableName) + "." + columnName + " AS " + getAlias(tableName + columnName) + ", ";
	}

	@Override
	protected WorldLocation createWorldLocation(final ResultSet result, String columnName) throws SQLException {
		try {
			final PGobject object = (PGobject)result.getObject(columnName);
			if (object instanceof PGpoint) { 
				final PGpoint newPoint = (PGpoint) object;
				return new WorldLocation(newPoint.x, newPoint.y, 0);
			}else if (object instanceof PGgeometry) {
				final PGgeometry geometry = (PGgeometry) object;
				if (geometry.getGeometry().getDimension() > 0) {
					final Point point = geometry.getGeometry().getFirstPoint();
					return new WorldLocation(point.getX(), point.getY(), point.getZ());
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void loadExtention(Connection connection, Statement statement) throws SQLException, ClassNotFoundException {
		/*
		 * Add the geometry types to the connection.
		 */
		((org.postgresql.PGConnection) NewProxyConnectionUnwrapper.unWrapperInnerConnection((NewProxyConnection)connection)).addDataType("geometry", 
				PGgeometry.class);
		((org.postgresql.PGConnection)  NewProxyConnectionUnwrapper.unWrapperInnerConnection((NewProxyConnection)connection)).addDataType("box3d",
				PGbox3d.class);
	}

	@Override
	public String databasePrefix() {
		return "pepys.\"";
	}

	@Override
	public String databaseSuffix() {
		return "\"";
	}

}
