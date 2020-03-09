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
package info.limpet.stackedcharts.ui.editor.parts;

import java.util.List;

import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.ChartSet;
import info.limpet.stackedcharts.model.Orientation;
import info.limpet.stackedcharts.ui.editor.StackedchartsImages;
import info.limpet.stackedcharts.ui.editor.figures.DirectionalIconLabel;
import info.limpet.stackedcharts.ui.editor.figures.DirectionalShape;
import info.limpet.stackedcharts.ui.editor.figures.ScatterSetContainerFigure;
import info.limpet.stackedcharts.ui.editor.parts.ChartEditPart.ScatterSetContainer;
import info.limpet.stackedcharts.ui.editor.policies.ScatterSetContainerEditPolicy;

public class ScatterSetContainerEditPart extends AbstractGraphicalEditPart {

	private ScatterSetContainerFigure scatterSetContainerFigure;
	private DirectionalIconLabel titleLabel;

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.CONTAINER_ROLE, new ScatterSetContainerEditPolicy());
	}

	@Override
	protected IFigure createFigure() {
		final DirectionalShape figure = new DirectionalShape();
		titleLabel = new DirectionalIconLabel(StackedchartsImages.getImage(StackedchartsImages.DESC_SCATTERSET));
		figure.add(titleLabel);
		titleLabel.getLabel().setText("Scatterset");

		scatterSetContainerFigure = new ScatterSetContainerFigure();
		figure.add(scatterSetContainerFigure);
		return figure;
	}

	@Override
	public IFigure getContentPane() {
		return scatterSetContainerFigure;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected List getModelChildren() {
		return (ScatterSetContainer) getModel();
	}

	@Override
	protected void refreshVisuals() {
		final DirectionalShape figure = (DirectionalShape) getFigure();

		final ChartSet chartSet = ((Chart) getParent().getModel()).getParent();
		final boolean vertical = chartSet.getOrientation() == Orientation.VERTICAL;

		((GraphicalEditPart) getParent()).setLayoutConstraint(this, figure,
				vertical ? BorderLayout.BOTTOM : BorderLayout.RIGHT);

		figure.setVertical(!vertical);
		scatterSetContainerFigure.setVertical(!vertical);
		titleLabel.setVertical(!vertical);
	}
}
