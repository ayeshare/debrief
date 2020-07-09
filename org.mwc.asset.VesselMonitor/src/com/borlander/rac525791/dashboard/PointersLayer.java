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

package com.borlander.rac525791.dashboard;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Image;

import com.borlander.rac525791.dashboard.layout.BaseDashboardLayout;
import com.borlander.rac525791.dashboard.layout.ControlUISuite;
import com.borlander.rac525791.dashboard.layout.DashboardImages;
import com.borlander.rac525791.dashboard.layout.DashboardUIModel;
import com.borlander.rac525791.dashboard.layout.SelectableImageFigure;
import com.borlander.rac525791.dashboard.rotatable.AngleMapper;
import com.borlander.rac525791.dashboard.rotatable.DirectionArrow;
import com.borlander.rac525791.dashboard.rotatable.DirectionDemandedArrow;
import com.borlander.rac525791.dashboard.rotatable.FullCircleAngleMapper;
import com.borlander.rac525791.dashboard.rotatable.RedSector;
import com.borlander.rac525791.dashboard.rotatable.SpeedDepthArrow;
import com.borlander.rac525791.dashboard.rotatable.SpeedDepthDemandedValueArrow;
import com.borlander.rac525791.draw2d.ext.InvisibleRectangle;
import com.borlander.rac525791.draw2d.ext.RotatableDecorationExt;

/**
 * Intended to correct z-order of controls components. We need to draw them in
 * following order:
 *
 * <code>
 * 1. Control background
 * 2. Red Sector (if any)
 * 3. White numbers
 * 4. Demanded decoration
 * 5. Actual arrows
 * 6. Lids
 * </code>
 */
public class PointersLayer extends InvisibleRectangle {
	private class Layout extends BaseDashboardLayout {
		private final Rectangle RECT = new Rectangle();

		public Layout(final DashboardUIModel uiModel) {
			super(uiModel);
		}

		@Override
		public void layout(final IFigure container) {
			// each control pointers group can layout itself
			mySpeedArrows.layoutGroup(container);
			myDepthArrows.layoutGroup(container);
			myDirectionArrows.layoutGroup(container);

			RECT.setSize(getSuite(container).getPreferredSizeRO());
			placeAtTopLeft(container, RECT);

			myCircles.setBounds(RECT);
			myNumbers.setBounds(RECT);
		}
	}

	private static final ControlPointersLayer.Factory SPEED_DEPTH_FACTORY = new ControlPointersLayer.Factory() {
		@Override
		public RotatableDecorationExt createActualArrow() {
			return new SpeedDepthArrow();
		}

		@Override
		public AngleMapper createAngleMapper() {
			return new ArcAngleMapper(0, 1000, true);
		}

		@Override
		public RotatableDecorationExt createDemandedArrow() {
			return new SpeedDepthDemandedValueArrow();
		}

		@Override
		public RedSector createRedSector(final AngleMapper mapper) {
			return new RedSector(mapper, false);
		}
	};

	private static final ControlPointersLayer.Factory DIRECTION_FACTORY = new ControlPointersLayer.Factory() {
		@Override
		public RotatableDecorationExt createActualArrow() {
			return new DirectionArrow();
		}

		@Override
		public AngleMapper createAngleMapper() {
			return new FullCircleAngleMapper(-Math.PI / 2, 360);
		}

		@Override
		public RotatableDecorationExt createDemandedArrow() {
			return new DirectionDemandedArrow();
		}

		@Override
		public RedSector createRedSector(final AngleMapper mapper) {
			return new RedSector(mapper, true);
		}
	};

	ScaledControlPointersLayer mySpeedArrows;

	ScaledControlPointersLayer myDepthArrows;

	ControlPointersLayer myDirectionArrows;

	ImageFigure myCircles;

	ImageFigure myNumbers;

	public PointersLayer(final DashboardUIModel uiModel) {
		mySpeedArrows = new ScaledControlPointersLayer(SPEED_DEPTH_FACTORY, uiModel, ControlUISuite.SPEED);
		myDepthArrows = new ScaledControlPointersLayer(SPEED_DEPTH_FACTORY, uiModel, ControlUISuite.DEPTH);
		myDirectionArrows = new ControlPointersLayer(DIRECTION_FACTORY, uiModel, ControlUISuite.DIRECTION);

		myNumbers = new SelectableImageFigure(uiModel) {
			@Override
			protected Image selectImage(final DashboardImages images) {
				return images.getNumbers();
			}
		};
		myCircles = new SelectableImageFigure(uiModel) {
			@Override
			protected Image selectImage(final DashboardImages images) {
				return images.getCircleLids();
			}
		};

		// implicit z-order is here:
		// 1. (is managed by DashboardFigure)
		// 2. red sectors
		this.add(mySpeedArrows.getRedSector());
		this.add(myDepthArrows.getRedSector());
		this.add(myDirectionArrows.getRedSector());
		// 3. White numbers
		this.add(myNumbers);
		// 4. Demanded decorations
		this.add(mySpeedArrows.getDemandedPointer());
		this.add(myDepthArrows.getDemandedPointer());
		this.add(myDirectionArrows.getDemandedPointer());
		// 5. Actual arrows
		this.add(mySpeedArrows.getActualArrow());
		this.add(myDepthArrows.getActualArrow());
		this.add(myDirectionArrows.getActualArrow());
		// 6. Lids
		this.add(myCircles);

		setLayoutManager(new Layout(uiModel));
	}

	public ScaledControlPointersLayer getDepthArrows() {
		return myDepthArrows;
	}

	public ControlPointersLayer getDirectionArrows() {
		return myDirectionArrows;
	}

	public ScaledControlPointersLayer getSpeedArrows() {
		return mySpeedArrows;
	}

}
