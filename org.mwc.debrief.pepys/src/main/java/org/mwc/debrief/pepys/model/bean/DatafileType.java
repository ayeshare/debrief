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

package org.mwc.debrief.pepys.model.bean;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.mwc.debrief.pepys.model.db.DatabaseConnection;
import org.mwc.debrief.pepys.model.db.SqliteDatabaseConnection;
import org.mwc.debrief.pepys.model.db.annotation.Id;
import org.mwc.debrief.pepys.model.db.annotation.TableName;
import org.mwc.debrief.pepys.model.db.config.DatabaseConfiguration;

import junit.framework.TestCase;

@TableName(name = "DatafileTypes")
public class DatafileType implements AbstractBean {

	public static class DatafileTypesTest extends TestCase {

		public void testDatafileTypesQuery() {
			try {
				final DatabaseConfiguration _config = new DatabaseConfiguration();
				DatabaseConnection.loadDatabaseConfiguration(_config,
						DatabaseConnection.DEFAULT_SQLITE_TEST_DATABASE_FILE);
				new SqliteDatabaseConnection().createInstance(_config);
				final List<DatafileType> list = DatabaseConnection.getInstance().listAll(DatafileType.class, null);

				assertTrue("Datafiletypes - database entries", list.size() == 7);

				final String[][] datafilesSomeReferences = new String[][] { { "1", "DATAFILE-TYPE-1" } };

				for (int i = 0; i < datafilesSomeReferences.length; i++) {
					boolean exist = false;
					for (final DatafileType dataFile : list) {
						exist |= datafilesSomeReferences[i][0].equals(dataFile.getDatafile_type_id())
								&& datafilesSomeReferences[i][1].equals(dataFile.getName());
					}

					assertTrue("Datafiles - Reference Name", exist);
				}

			} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException | PropertyVetoException | SQLException
					| ClassNotFoundException | IOException e) {
				e.printStackTrace();
				fail("Couldn't connect to database or query error");
			}

		}
	}

	@Id
	private String datafile_type_id;
	private String name;

	private Date created_date;

	public DatafileType() {

	}

	public Date getCreated_date() {
		return created_date;
	}

	public String getDatafile_type_id() {
		return datafile_type_id;
	}

	public String getName() {
		return name;
	}

	public void setCreated_date(final Date created_date) {
		this.created_date = created_date;
	}

	public void setDatafile_type_id(final String datafile_type_id) {
		this.datafile_type_id = datafile_type_id;
	}

	public void setName(final String name) {
		this.name = name;
	}
}
