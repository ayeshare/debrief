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
package info.limpet.persistence;

import java.io.IOException;
import java.util.List;

import info.limpet.IStoreItem;

abstract public class FileParser {
	public static String filePrefix(final String fullPath) {
		// gets filename without extension
		return fullPath.split("\\.(?=[^\\.]+$)")[0];
	}

	abstract public List<IStoreItem> parse(String filePath) throws IOException;

}
