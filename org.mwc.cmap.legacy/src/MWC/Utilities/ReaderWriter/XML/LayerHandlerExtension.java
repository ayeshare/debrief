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

package MWC.Utilities.ReaderWriter.XML;

import MWC.GUI.Layer;
import MWC.GUI.Layers;

public interface LayerHandlerExtension {
	/**
	 * indicate if this handler can export objects of this type
	 *
	 * @param subject
	 * @return
	 */
	public boolean canExportThis(Layer subject);

	/**
	 * actually export this object
	 *
	 * @param theLayer
	 * @param parent
	 * @param doc
	 */
	public void exportThis(Layer theLayer, org.w3c.dom.Element parent, org.w3c.dom.Document doc);

	/**
	 * store the layers object that we're working on
	 *
	 * @param theLayers
	 */
	public void setLayers(Layers theLayers);

}
