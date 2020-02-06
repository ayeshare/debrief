
package org.mwc.cmap.geotools.gt2plot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.styling.Style;
import org.geotools.swt.utils.Utils;

import Debrief.GUI.Frames.Application;
import MWC.GUI.ExternallyManagedDataLayer;
import MWC.GUI.Shapes.ChartBoundsWrapper;

public class ShapeFileLayer extends GeoToolsLayer
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ShapeFileLayer(final String layerName, final String fileName)
	{
		super(ChartBoundsWrapper.SHAPEFILE_TYPE, layerName, fileName);
	}

	protected Layer loadLayer(final File openFile)
	{
		Layer res = null;

		// nope, just load it
		FileDataStore store;
		try
		{
			if (openFile.exists())
			{
				store = FileDataStoreFinder.getDataStore(openFile);
				final SimpleFeatureSource featureSource = store.getFeatureSource();
				final Style style = Utils.createStyle(openFile, featureSource);
				res = new FeatureLayer(featureSource, style);
			}
			else
			{
				System.err.println("can't find this file");
			}
		}
		catch(final FileNotFoundException f)
		{
		  Application.logError2(-1, "Can't find the shape file", f);
			//CorePlugin.showMessage("Load ShapeFile", "Sorry, can't find the requested shapefile:\n" + openFile.getName());
		}
		catch (final IOException e)
		{
		  Application.logError2(-1, "Trouble loading shape file", e);
		}
		catch (final Exception e)
		{
			System.err.println("Surely it will get caught!!!");
		}
		return res;
	}

	public static MWC.GUI.Layer read(final String fileName)
	{
		MWC.GUI.Layer res = null;
		final File openFile = new File(fileName);
		if (openFile != null && openFile.exists())
		{
			// sort out the name of the map
			final String coverageName = ChartBoundsWrapper.getCoverageName(fileName);

			// represent it as a normal shapefile
			res = new ExternallyManagedDataLayer(ChartBoundsWrapper.SHAPEFILE_TYPE,
					coverageName, fileName);
		}
		return res;
	}

}
