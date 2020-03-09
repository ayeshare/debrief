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

package org.mwc.debrief.core.actions.drag;

import org.eclipse.swt.graphics.Cursor;
import org.mwc.cmap.core.CursorRegistry;
import org.mwc.debrief.core.actions.DragSegment.IconProvider;

import Debrief.Wrappers.Track.TrackSegment;
import MWC.GUI.Shapes.DraggableItem;
import MWC.GenericData.WorldVector;

public class TranslateOperation extends CoreDragOperation implements DraggableItem, IconProvider {
	public TranslateOperation(final TrackSegment segment) {
		super(segment, "centre point");
	}

	@Override
	public Cursor getHotspotCursor() {
		return CursorRegistry.getCursor(CursorRegistry.SELECT_FEATURE_HIT_DRAG);
	}

	@Override
	public void shift(final WorldVector vector) {
		//
		_segment.shift(vector);
	}
}