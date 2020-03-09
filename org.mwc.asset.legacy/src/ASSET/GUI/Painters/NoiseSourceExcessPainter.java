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

package ASSET.GUI.Painters;

import java.awt.Color;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditorSupport;
import java.util.Vector;

import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Chart.Painters.SpatialRasterPainter;
import MWC.GUI.Chart.Painters.SpatialRasterPainter.ColorConverter.SwingColorConverter;
import MWC.GenericData.WorldLocation;

/**
 * class to show the excess between the in-water noise and the residual
 * noise-level from a point source
 */
public class NoiseSourceExcessPainter extends SpatialRasterPainter implements NoiseSource {
	///////////////////////////////////////////////////
	// member variables
	///////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////
	// info class
	////////////////////////////////////////////////////////////
	public class NoiseInfo extends Editable.EditorType implements java.io.Serializable {

		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

		public NoiseInfo(final NoiseSourceExcessPainter data) {
			super(data, data.getName(), "Edit");
		}

		@Override
		public PropertyDescriptor[] getPropertyDescriptors() {
			try {
				final PropertyDescriptor[] res = { prop("Visible", "whether this layer is visible"),
						prop("SimplePlotting", "whether to use simple colors"),
						prop("SourceAlpha", "the background noise level"),
						prop("SourceBravo", "the noise source to examine"),
						prop("KeyLocation", "the location of the scale"), prop("Name", "name of this painter"), };
				res[2].setPropertyEditorClass(NoiseSourcePropertyEditor.class);
				res[3].setPropertyEditorClass(NoiseSourcePropertyEditor.class);
				res[5].setPropertyEditorClass(SpatialRasterPainter.KeyLocationPropertyEditor.class);
				return res;
			} catch (final IntrospectionException e) {
				return super.getPropertyDescriptors();
			}
		}
	}

	/**
	 * ************************************************* custom editor allowing
	 * noise source editors to be selected
	 * *************************************************
	 */
	public static class NoiseSourcePropertyEditor extends PropertyEditorSupport {
		public static Layers _myLayers;

		String _theSource;

		@Override
		public String getAsText() {
			return _theSource;
		}

		@Override
		public String[] getTags() {
			String[] res = new String[] { " ", " " };

			final Vector<String> list = new Vector<String>(0, 1);

			// pass through the layers to find any noise sources
			for (int i = 0; i < _myLayers.size(); i++) {
				final Layer thisL = _myLayers.elementAt(i);
				if (thisL instanceof NoiseSource) {
					list.add(thisL.getName());
				}
			}

			// did we find any?
			if (list.size() > 0) {
				res = new String[list.size()];
				res = list.toArray(res);
			}

			return res;
		}

		@Override
		public Object getValue() {
			return _theSource;
		}

		@Override
		public void setAsText(final String val) {
			_theSource = val;
		}

		@Override
		public void setValue(final Object p1) {
			if (p1 instanceof String) {
				final String val = (String) p1;
				setAsText(val);
			}
		}
	}

	//////////////////////////////////////////////////
	// add testing code
	//////////////////////////////////////////////////
	public static class PainterTest extends SupportTesting.EditableTesting {
		/**
		 * get an object which we can test
		 *
		 * @return Editable object which we can check the properties for
		 */
		@Override
		public Editable getEditable() {
			return new NoiseSourceExcessPainter("the layer", new Layers());
		}
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * the calculated noise levels across the environment
	 */
	private SpatialRasterPainter _wholeScenario = null;

	/**
	 * the source of interest
	 */
	private SpatialRasterPainter _source = null;

	/**
	 * whether to produce detailed or vague plots
	 */
	private boolean _simplePlotting = true;

	/**
	 * the layers object to take the layers from
	 */
	private Layers _theLayers = null;

	/**
	 * temporarily create a color converter, use the swing one
	 *
	 */
	private SwingColorConverter _myConverter = null;

	///////////////////////////////////////////////////
	// constructor
	///////////////////////////////////////////////////
	public NoiseSourceExcessPainter(final NoiseSourcePainter point, final ScenarioNoiseLevelPainter scenario,
			final Layers theData) {
		this("Noise Source Excess Painter", theData);
		_wholeScenario = scenario;
		_source = point;

		_myConverter = new SwingColorConverter();
	}

	public NoiseSourceExcessPainter(final String layerName, final Layers theData) {
		super(layerName);

		// put the layers into the data
		_theLayers = theData;
	}
	///////////////////////////////////////////////////
	// member methods
	///////////////////////////////////////////////////

	/* returns a color based on slope and elevation */
	public int getColor(final int elevation, final double lowerLimit, final double upperLimit) {

		int res = 0;

		if (_simplePlotting) {
			if (elevation > 0)
				res = Color.green.getRGB();
			else
				res = Color.red.getRGB();
		} else
			res = super.getColor(elevation, lowerLimit, upperLimit, _myConverter, false);

		return res;

	}

	/**
	 * provide the delta for the data (in degrees)
	 */
	@Override
	public double getGridDelta() {
		return MWC.Algorithms.Conversions.Nm2Degs(5);
	}

	@Override
	public Editable.EditorType getInfo() {
		// update the layers in the editor
		NoiseSourcePropertyEditor._myLayers = _theLayers;

		if (_myEditor == null) {
			_myEditor = new NoiseInfo(this);
		}
		return _myEditor;
	}

	public String getSourceAlpha() {
		String res = null;
		if (_source != null)
			res = _source.getName();

		return res;
	}

	public String getSourceBravo() {
		String res = null;
		if (_wholeScenario != null)
			res = _wholeScenario.getName();

		return res;
	}

	@Override
	public int getValueAt(final WorldLocation location) {
		// work out the noise dissipation from this origin
		int res = 0;

		final int source = _source.getValueAt(location);
		final int scenario = _wholeScenario.getValueAt(location);
		res = source - scenario;

		return res;
	}

	///////////////////////////////////////////////////
	// editor support
	///////////////////////////////////////////////////
	@Override
	public boolean hasEditor() {
		return true;
	}

	/**
	 * whether the data has been loaded yet
	 */
	@Override
	public boolean isDataLoaded() {
		return true;
	}

	public boolean isSimplePlotting() {
		return _simplePlotting;
	}

	public void setSimplePlotting(final boolean simplePlotting) {
		this._simplePlotting = simplePlotting;
	}

	public void setSourceAlpha(final String source) {
		// set this source
		_source = (SpatialRasterPainter) _theLayers.findLayer(source);
	}

	public void setSourceBravo(final String source) {
		// set this source
		_wholeScenario = (SpatialRasterPainter) _theLayers.findLayer(source);
	}
}
