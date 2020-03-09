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
/*****************************************************************************
 *  Limpet - the Lightweight InforMation ProcEssing Toolkit
 *  http://limpet.info
 *
 *  (C) 2015-2016, Deep Blue C Technologies Ltd
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the Eclipse Public License v1.0
 *  (http://www.eclipse.org/legal/epl-v10.html)
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *****************************************************************************/
package info.limpet.data.persistence.xml;

import java.awt.geom.Point2D;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import info.limpet.operations.spatial.GeoSupport;

public class PointConverter implements Converter {

	public PointConverter() {
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean canConvert(final Class type) {
		return Point2D.class.isAssignableFrom(type);
	}

	@Override
	public void marshal(final Object source, final HierarchicalStreamWriter writer, final MarshallingContext context) {
		final Point2D point = (Point2D) source;
		writer.startNode("lat");
		String value = Double.toString(point.getY());
		writer.setValue(value);
		writer.endNode();
		writer.startNode("lon");
		value = Double.toString(point.getX());
		writer.setValue(value);
		writer.endNode();
	}

	@Override
	public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
		double lat = 0, lon = 0;
		while (reader.hasMoreChildren()) {
			reader.moveDown();
			if ("lat".equals(reader.getNodeName())) {
				lat = new Double(reader.getValue()).doubleValue();
			} else if ("lon".equals(reader.getNodeName())) {
				lon = new Double(reader.getValue()).doubleValue();
			}
			reader.moveUp();
		}
		final Point2D point = GeoSupport.getCalculatorWGS84().createPoint(lon, lat);
		return point;
	}

}
