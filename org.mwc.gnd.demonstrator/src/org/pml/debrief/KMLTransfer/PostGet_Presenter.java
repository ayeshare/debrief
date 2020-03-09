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

package org.pml.debrief.KMLTransfer;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.Date;

import MWC.Utilities.TextFormatting.GMTDateFormat;

public class PostGet_Presenter {

	private static final String DATABASE_ROOT = "jdbc:postgresql://127.0.0.1/ais";
	private static Connection _conn;
	private static PreparedStatement sql;

	private static void connectToDatabase() {
		// driver first
		try {
			Class.forName("org.postgresql.Driver");
		} catch (final ClassNotFoundException e) {
			throw new RuntimeException("Failed to load database driver");
		}

		try {
			final String url = DATABASE_ROOT;
			final String password = System.getenv("pg_pwd");
			if (password == null)
				throw new RuntimeException("database password missing");
			_conn = DriverManager.getConnection(url, "postgres", password);

			// also tell the connection about our new custom data types
			((org.postgresql.PGConnection) _conn).addDataType("geometry", org.postgis.PGgeometry.class);
			((org.postgresql.PGConnection) _conn).addDataType("box3d", org.postgis.PGbox3d.class);
		} catch (final SQLException e) {
			throw new RuntimeException("failed to create connection");
		}

	}

	private static String getSymFor(final String color) {
		final String res;
		if (color.equals("yellow"))
			res = "D";
		else if (color.equals("lightgreen"))
			res = "B";
		else if (color.equals("blue"))
			res = "A";
		else if (color.equals("red"))
			res = "C";
		else if (color.equals("pink"))
			res = "K";
		else if (color.equals("lightgray"))
			res = "F";
		else if (color.equals("cyan"))
			res = "H";
		else if (color.equals("magenta"))
			res = "E";
		else
			res = "";

		return res;
	}

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		try {
			// check we have a database
			connectToDatabase();

			final String query = "select dateval, nameval, mmsi, longval, latval, courseval, speedval, dataset, color from wednesday;";
			System.out.println("query will be:" + query);
			sql = _conn.prepareStatement(query);

			final FileWriter outFile = new FileWriter("portland6.rep");

			final DateFormat sdf = new GMTDateFormat("yyMMdd HHmmss");

			final ResultSet res = sql.executeQuery();
			while (res.next()) {
				int ctr = 1;
				final Timestamp date = res.getTimestamp(ctr++);
				final String name = res.getString(ctr++);
				final int mmsi = res.getInt(ctr++);
				final double longVal = res.getDouble(ctr++);
				final double latVal = res.getDouble(ctr++);
				final double course = res.getDouble(ctr++);
				final double speed = res.getDouble(ctr++);
				final int dataset = res.getInt(ctr++);
				final String color = res.getString(ctr++);

				final String symbol = getSymFor(color);

				final String vName = name + "_" + mmsi + "_" + dataset;

				// debrief format: YYMMDD HHMMSS.SSS XXXXXX SY DD MM SS.SS H DDD MM
				// SS.SS H CCC.C SS.S DDD
				final Date jDate = new Date(date.getTime());
				String line = "";
				line += sdf.format(jDate) + " ";
				line += "\"" + vName + "\" ";
				line += "@" + symbol + " ";
				line += latVal + " 0 0.0 N ";
				line += longVal + " 0 0.0 E ";
				line += course + " ";
				line += speed + " ";
				line += " 0";
				line += "\n";

				outFile.write(line);
			}

			outFile.close();

		} catch (final RuntimeException re) {
			re.printStackTrace();
		} catch (final SQLException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		} finally {

			// close the databse
			if (_conn != null) {
				try {
					System.out.println("closing database");
					_conn.close();
				} catch (final SQLException e) {
					e.printStackTrace();
				}
			}

		}
	}
}
