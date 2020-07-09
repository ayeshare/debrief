
package MWC.Utilities.ReaderWriter.XML;

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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers.NeedsToKnowAboutLayers;
import MWC.GUI.Plottable;
import MWC.GUI.Shapes.ChartBoundsWrapper;
import MWC.Utilities.ReaderWriter.XML.Features.ChartBoundsHandler;
import MWC.Utilities.ReaderWriter.XML.Features.CoastlineHandler;
import MWC.Utilities.ReaderWriter.XML.Features.Grid4WHandler;
import MWC.Utilities.ReaderWriter.XML.Features.GridHandler;
import MWC.Utilities.ReaderWriter.XML.Features.LocalGridHandler;
import MWC.Utilities.ReaderWriter.XML.Features.ScaleHandler;
import MWC.Utilities.ReaderWriter.XML.Features.TimeDisplayPainterHandler;
import MWC.Utilities.ReaderWriter.XML.Features.VPFCoastlineHandler;
import MWC.Utilities.ReaderWriter.XML.Features.VPFDatabaseHandler;

public class LayerHandler extends MWCXMLReader implements PlottableExporter {

	public static final String NAME = "Name";

	static public final String NARRATIVE_LAYER = "Narratives";

	protected static java.util.Hashtable<Class<?>, PlottableExporter> _myExporters;

	protected synchronized static void checkExporters() {
		if (_myExporters == null) {
			_myExporters = new java.util.Hashtable<Class<?>, PlottableExporter>();
			_myExporters.put(MWC.GUI.Chart.Painters.CoastPainter.class, new CoastlineHandler() {
				@Override
				public void addPlottable(final MWC.GUI.Plottable plottable) {
				}
			});
			_myExporters.put(MWC.GUI.Chart.Painters.LocalGridPainter.class, new LocalGridHandler() {
				@Override
				public void addPlottable(final MWC.GUI.Plottable plottable) {
				}
			});
			_myExporters.put(MWC.GUI.Chart.Painters.GridPainter.class, new GridHandler() {
				@Override
				public void addPlottable(final MWC.GUI.Plottable plottable) {
				}
			});
			_myExporters.put(MWC.GUI.Chart.Painters.Grid4WPainter.class, new Grid4WHandler() {
				@Override
				public void addPlottable(final MWC.GUI.Plottable plottable) {
				}
			});
			_myExporters.put(MWC.GUI.Chart.Painters.ScalePainter.class, new ScaleHandler() {
				@Override
				public void addPlottable(final MWC.GUI.Plottable plottable) {
				}
			});
			_myExporters.put(MWC.GUI.Chart.Painters.TimeDisplayPainter.class, new TimeDisplayPainterHandler() {
				@Override
				public void addPlottable(final Plottable plottable) {
				}
			});
			_myExporters.put(MWC.GUI.VPF.CoverageLayer.ReferenceCoverageLayer.class, new VPFCoastlineHandler() {
				@Override
				public void addPlottable(final MWC.GUI.Plottable plottable) {
				}
			});
			_myExporters.put(MWC.GUI.VPF.VPFDatabase.class, new VPFDatabaseHandler() {
				@Override
				public void addPlottable(final MWC.GUI.Plottable plottable) {
				}
			});
			_myExporters.put(ChartBoundsWrapper.class, new ChartBoundsHandler() {
				@Override
				public void addPlottable(final MWC.GUI.Plottable plottable) {
				}
			});

		}
	}

	public static void exportLayer(final MWC.GUI.BaseLayer layer, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {
		exportLayer("layer", layer, parent, doc);
	}

	public static void exportLayer(final String elementName, final MWC.GUI.BaseLayer layer,
			final org.w3c.dom.Element parent, final org.w3c.dom.Document doc) {
		// check our exporters
		checkExporters();

		final org.w3c.dom.Element eLayer = doc.createElement(elementName);

		eLayer.setAttribute(NAME, layer.getName());
		eLayer.setAttribute("Visible", writeThis(layer.getVisible()));
		eLayer.setAttribute("LineThickness", writeThis(layer.getLineThickness()));

		// step through the components of the layer
		final java.util.Enumeration<Editable> enumer = layer.elements();
		while (enumer.hasMoreElements()) {
			final MWC.GUI.Plottable nextPlottable = (MWC.GUI.Plottable) enumer.nextElement();

			exportThisItem(nextPlottable, eLayer, doc);
		}

		parent.appendChild(eLayer);

	}

	public static void exportThisItem(final MWC.GUI.Plottable nextPlottable, final org.w3c.dom.Element eLayer,
			final org.w3c.dom.Document doc) {

		// definition of what parameter we are going to search for (since
		// shapeWrappers are indexed by shape not actual class)
		Object classId = null;

		// get the shape
		classId = nextPlottable.getClass();

		// try to get this one
		final Object exporterType = _myExporters.get(classId);
		if (exporterType != null) {
			final PlottableExporter cl = (PlottableExporter) exporterType;
			cl.exportThisPlottable(nextPlottable, eLayer, doc);
		} else
			MWC.Utilities.Errors.Trace.trace("Exporter not found for " + nextPlottable.getName());

	}

	private final MWC.GUI.Layers _theLayers;

	protected MWC.GUI.BaseLayer _myLayer;

	/**
	 * use the default layer name
	 *
	 * @param theLayers
	 */
	public LayerHandler(final MWC.GUI.Layers theLayers) {
		this(theLayers, "layer");
	}

	public LayerHandler(final MWC.GUI.Layers theLayers, final String layerName) {
		// inform our parent what type of class we are
		super(layerName);

		// store the layers object, so that we can add ourselves to it
		_theLayers = theLayers;

		addHandler(new CoastlineHandler() {
			@Override
			public void addPlottable(final MWC.GUI.Plottable plottable) {
				addThis(plottable);
			}
		});

		addHandler(new ChartBoundsHandler() {
			@Override
			public void addPlottable(final MWC.GUI.Plottable plottable) {
				addThis(plottable);
			}
		});

		addHandler(new VPFCoastlineHandler() {
			@Override
			public void addPlottable(final MWC.GUI.Plottable plottable) {
				addThis(plottable);
			}
		});

		addHandler(new VPFDatabaseHandler() {
			@Override
			public void addPlottable(final MWC.GUI.Plottable plottable) {
				addThis(plottable);
			}
		});

		addHandler(new ScaleHandler() {
			@Override
			public void addPlottable(final MWC.GUI.Plottable plottable) {
				addThis(plottable);
			}
		});

		addHandler(new TimeDisplayPainterHandler() {
			@Override
			public void addPlottable(final MWC.GUI.Plottable plottable) {
				addThis(plottable);
			}
		});

		addHandler(new LocalGridHandler() {
			@Override
			public void addPlottable(final MWC.GUI.Plottable plottable) {
				addThis(plottable);
			}
		});
		addHandler(new GridHandler() {
			@Override
			public void addPlottable(final MWC.GUI.Plottable plottable) {
				addThis(plottable);
			}
		});
		addHandler(new Grid4WHandler() {
			@Override
			public void addPlottable(final MWC.GUI.Plottable plottable) {
				addThis(plottable);
			}
		});

		addAttributeHandler(new HandleAttribute(NAME) {
			@Override
			public void setValue(final String name, final String val) {
				_myLayer.setName(val);
			}
		});

		addAttributeHandler(new HandleBooleanAttribute("Visible") {
			@Override
			public void setValue(final String name, final boolean val) {
				_myLayer.setVisible(val);
			}
		});

		addAttributeHandler(new HandleIntegerAttribute("LineThickness") {
			@Override
			public void setValue(final String name, final int val) {
				_myLayer.setLineThickness(val);
			}
		});

	}

	public void addThis(final Editable editable) {
		_myLayer.add(editable);

		// is this an item that wants to know about the layers object?
		if (editable instanceof NeedsToKnowAboutLayers) {
			final NeedsToKnowAboutLayers theL = (NeedsToKnowAboutLayers) editable;
			theL.setLayers(_theLayers);
		}

	}

	@Override
	public void elementClosed() {

		// our layer is complete, add it to the parent!
		// note, we no longer allow duplicates, we only want
		// one Chart Features, for example
		_theLayers.addThisLayer(_myLayer);

		// now, we may well have already had a layer of this name - particularly
		// the "Chart Features" one.
		// if this has happened, we need to update it's visibility with our one
		final Layer theLayer = _theLayers.findLayer(_myLayer.getName());
		theLayer.setVisible(_myLayer.getVisible());

		_myLayer = null;

	}

	@Override
	public void exportThisPlottable(final Plottable plottable, final Element parent, final Document doc) {
		exportLayer((BaseLayer) plottable, parent, doc);
	}

	protected BaseLayer getLayer() {
		return new MWC.GUI.BaseLayer();
	}

	// this is one of ours, so get on with it!
	@Override
	protected void handleOurselves(final String name, final Attributes attributes) {
		// we are starting a new layer, so create it!
		_myLayer = getLayer();

		super.handleOurselves(name, attributes);
	}

}