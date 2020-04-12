package org.mwc.debrief.pepys.model.db.config;

import java.util.HashMap;

import org.mwc.debrief.pepys.model.db.DatabaseConnection;
import org.mwc.debrief.pepys.model.db.SqliteDatabaseConnection;

public class DatabaseConfiguration {

	public static class DatabaseConfigurationFactory {
		public static DatabaseConfiguration createSqliteConfiguration(final String path) {
			final DatabaseConfiguration _config = new DatabaseConfiguration();
			final HashMap<String, String> databaseTag = new HashMap<String, String>();
			databaseTag.put(SqliteDatabaseConnection.CONFIGURATION_DB_NAME, path);
			databaseTag.put(DatabaseConnection.CONFIGURATION_DATABASE_TYPE, DatabaseConnection.SQLITE);
			_config.categories.put(DatabaseConnection.CONFIGURATION_TAG, databaseTag);
			return _config;
		}
	}

	private final HashMap<String, HashMap<String, String>> categories = new HashMap<>();

	private String _sourcePath;

	public DatabaseConfiguration() {

	}

	public void clear() {
		categories.clear();
	}

	public HashMap<String, String> getCategory(final String str) {
		if (str != null) {
			if (!categories.containsKey(str)) {
				categories.put(str, new HashMap<String, String>());
			}
			return categories.get(str);
		}
		return null;
	}

	public String getSourcePath() {
		return _sourcePath;
	}

	public void setSourcePath(final String _sourcePath) {
		this._sourcePath = _sourcePath;
	}
}
