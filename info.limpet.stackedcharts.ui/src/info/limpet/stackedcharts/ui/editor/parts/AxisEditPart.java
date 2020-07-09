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

import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.SimpleLoweredBorder;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.gef.requests.GroupRequest;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.views.properties.IPropertySource;

import info.limpet.stackedcharts.model.AxisType;
import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.ChartSet;
import info.limpet.stackedcharts.model.DependentAxis;
import info.limpet.stackedcharts.model.Orientation;
import info.limpet.stackedcharts.model.StackedchartsPackage;
import info.limpet.stackedcharts.ui.editor.commands.DeleteAxisFromChartCommand;
import info.limpet.stackedcharts.ui.editor.figures.ArrowFigure;
import info.limpet.stackedcharts.ui.editor.figures.AxisNameFigure;
import info.limpet.stackedcharts.ui.editor.policies.AxisContainerEditPolicy;

public class AxisEditPart extends AbstractGraphicalEditPart implements ActionListener, IPropertySourceProvider {

	public class AxisAdapter implements Adapter {

		@Override
		public Notifier getTarget() {
			return getAxis();
		}

		@Override
		public boolean isAdapterForType(final Object type) {
			return type.equals(DependentAxis.class);
		}

		@Override
		public void notifyChanged(final Notification notification) {
			final int featureId = notification.getFeatureID(StackedchartsPackage.class);
			switch (featureId) {
			case StackedchartsPackage.DEPENDENT_AXIS__DATASETS:
				refreshChildren();
			case StackedchartsPackage.ABSTRACT_AXIS__NAME:
				refreshVisuals();
			}
		}

		@Override
		public void setTarget(final Notifier newTarget) {
		}
	}

	public static final Color BACKGROUND_COLOR = Display.getDefault().getSystemColor(SWT.COLOR_WHITE);

	private RectangleFigure datasetsPane;

	private AxisNameFigure axisNameLabel;

	private final AxisAdapter adapter = new AxisAdapter();

	private ArrowFigure arrowFigure;

	@Override
	public void actionPerformed(final org.eclipse.draw2d.ActionEvent event) {
		final Command deleteCommand = getCommand(new GroupRequest(REQ_DELETE));
		if (deleteCommand != null) {
			final CommandStack commandStack = getViewer().getEditDomain().getCommandStack();
			commandStack.execute(deleteCommand);
		}
	}

	@Override
	public void activate() {
		super.activate();
		getAxis().eAdapters().add(adapter);
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.PRIMARY_DRAG_ROLE, new NonResizableEditPolicy());

		installEditPolicy(EditPolicy.CONTAINER_ROLE, new AxisContainerEditPolicy());

		installEditPolicy(EditPolicy.COMPONENT_ROLE, new ComponentEditPolicy() {
			@Override
			protected Command createDeleteCommand(final GroupRequest deleteRequest) {
				final DependentAxis dataset = (DependentAxis) getHost().getModel();
				final Chart parent = (Chart) dataset.eContainer();
				final DeleteAxisFromChartCommand cmd = new DeleteAxisFromChartCommand(parent, dataset);
				return cmd;
			}
		});
	}

	@Override
	protected IFigure createFigure() {
		final RectangleFigure figure = new RectangleFigure();
		figure.setBackgroundColor(BACKGROUND_COLOR);
		final Color borderCol = Display.getCurrent().getSystemColor(SWT.COLOR_GRAY);
		final Border figureBorder = new LineBorder(borderCol, 2);
		figure.setBorder(figureBorder);

		figure.setOutline(false);
		final GridLayout layoutManager = new GridLayout();
		// zero margin, in order to connect the dependent axes to the shared one
		layoutManager.marginHeight = 0;
		layoutManager.marginWidth = 0;
		figure.setLayoutManager(layoutManager);

		datasetsPane = new RectangleFigure();
		datasetsPane.setOutline(false);
		final SimpleLoweredBorder datasetBorder = new SimpleLoweredBorder(3);
		datasetsPane.setBorder(datasetBorder);
		final GridLayout datasetsPaneLayout = new GridLayout();
		datasetsPane.setLayoutManager(datasetsPaneLayout);
		figure.add(datasetsPane);

		arrowFigure = new ArrowFigure(false);
		figure.add(arrowFigure);

		axisNameLabel = new AxisNameFigure(this);
		figure.add(axisNameLabel);

		return figure;
	}

	@Override
	public void deactivate() {
		getAxis().eAdapters().remove(adapter);
		super.deactivate();
	}

	protected DependentAxis getAxis() {
		return (DependentAxis) getModel();
	}

	@Override
	public IFigure getContentPane() {
		return datasetsPane;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected List getModelChildren() {
		return getAxis().getDatasets();
	}

	@Override
	public IPropertySource getPropertySource() {
		final DependentAxis axis = getAxis();
		final AxisType axisType = getAxis().getAxisType();

		// Proxy two objects in to one
		return new CombinedProperty(axis, axisType, "Axis type");
	}

	@Override
	protected void refreshVisuals() {
		axisNameLabel.setName(getAxis().getName());

		final GraphicalEditPart parent = (GraphicalEditPart) getParent();

		final boolean horizontal = ((ChartSet) parent.getParent().getParent().getParent().getModel())
				.getOrientation() == Orientation.HORIZONTAL;

		final GridLayout layout = (GridLayout) getFigure().getLayoutManager();
		if (horizontal) {
			layout.numColumns = 1;
			parent.setLayoutConstraint(this, figure, new GridData(GridData.FILL, GridData.CENTER, true, false));

			layout.setConstraint(datasetsPane, new GridData(GridData.FILL, GridData.CENTER, true, false));

			layout.setConstraint(arrowFigure, new GridData(GridData.FILL, GridData.CENTER, true, false));
			layout.setConstraint(axisNameLabel, new GridData(GridData.FILL, GridData.CENTER, true, false));

			axisNameLabel.setVertical(false);
			arrowFigure.setHorizontal(true);
		} else {
			layout.numColumns = figure.getChildren().size();
			parent.setLayoutConstraint(this, figure, new GridData(GridData.CENTER, GridData.FILL, false, true));

			layout.setConstraint(datasetsPane, new GridData(GridData.CENTER, GridData.FILL, false, true));

			layout.setConstraint(arrowFigure, new GridData(GridData.CENTER, GridData.FILL, false, true));
			layout.setConstraint(axisNameLabel, new GridData(GridData.CENTER, GridData.FILL, false, true));

			axisNameLabel.setVertical(true);
			arrowFigure.setHorizontal(false);
		}
		layout.invalidate();
		parent.refresh();

		final GridLayout layoutManager = (GridLayout) datasetsPane.getLayoutManager();
		layoutManager.numColumns = horizontal ? 1 : getModelChildren().size();
		layoutManager.invalidate();

	}

}
