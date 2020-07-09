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

package org.mwc.debrief.gndmanager.views.io;

import java.io.IOException;
import java.util.ArrayList;

import org.mwc.debrief.gndmanager.views.ManagerView;

public interface SearchModel {
	public static interface Facet {
		public int getCount(int index);

		public String getName(int index);

		public int size();

		public ArrayList<String> toList();
	}

	public static interface Match {
		public String getId();

		public String getName();

		public String getPlatform();

		public String getPlatformType();

		public String getTrial();
	}

	public static interface MatchList {
		public Facet getFacet(String name);

		public Match getMatch(int index);

		public int getNumMatches();
	}

	MatchList getAll(String indexURL, String dbURL) throws IOException;

	MatchList getMatches(String indexURL, String dbURL, ManagerView view) throws IOException;
}
