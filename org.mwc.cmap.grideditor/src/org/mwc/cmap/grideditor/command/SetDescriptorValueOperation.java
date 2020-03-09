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

package org.mwc.cmap.grideditor.command;

import java.lang.reflect.Method;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osgi.util.NLS;
import org.mwc.cmap.gridharness.data.GriddableItemDescriptor;
import org.mwc.cmap.gridharness.data.UnitsSet;
import org.mwc.cmap.gridharness.data.ValueInUnits;

import MWC.GUI.FireReformatted;
import MWC.GUI.TimeStampedDataItem;

public class SetDescriptorValueOperation extends AbstractGridEditorOperation {

	protected static class ElementHasValueState implements EnvironmentState {

		private final Object myValue;

		private final TimeStampedDataItem myItem;

		public ElementHasValueState(final OperationEnvironment context) {
			this(context.getSubject(), context.getDescriptor());
		}

		public ElementHasValueState(final TimeStampedDataItem item, final GriddableItemDescriptor descriptor) {
			myItem = item;
			myValue = BeanUtil.getItemValue(item, descriptor);
		}

		public TimeStampedDataItem getItem() {
			return myItem;
		}

		public Object getValue() {
			return myValue;
		}

		@Override
		public boolean isCompatible(final OperationEnvironment environment) {
			return safeEquals(myValue, BeanUtil.getItemValue(environment.getSubject(), environment.getDescriptor()));
		}

		private boolean safeEquals(final Object o1, final Object o2) {
			return o1 == null ? o2 == null : o1.equals(o2);
		}

	}

	private static Object adjustValue(final OperationEnvironment environment, final Object newValue) {
		if (ValueInUnits.class.isAssignableFrom(environment.getDescriptor().getType()) && newValue instanceof Number) {
			final ValueInUnits currentValue = BeanUtil.getItemValue(environment.getSubject(),
					environment.getDescriptor(), ValueInUnits.class);
			if (currentValue != null) {
				final ValueInUnits copy = currentValue.makeCopy();
				final UnitsSet.Unit chartUnit = copy.getUnitsSet().getMainUnit();
				copy.setValues(((Number) newValue).doubleValue(), chartUnit);
				return copy;
			}
		}
		return newValue;
	}

	private static String formatLabel(final GriddableItemDescriptor descriptor) {
		return NLS.bind("Setting {0} value", descriptor == null ? "" : descriptor.getTitle());
	}

	private final boolean myFireRefresh;

	private final Object myNewValue;

	public SetDescriptorValueOperation(final OperationEnvironment environment, final Object newValue) {
		this(environment, newValue, true);
	}

	public SetDescriptorValueOperation(final OperationEnvironment environment, final Object newValue,
			final boolean fireRefresh) {
		super(formatLabel(environment.getDescriptor()), environment);
		myNewValue = adjustValue(environment, newValue);
		myFireRefresh = fireRefresh;
		if (environment.getSubject() == null) {
			throw new IllegalArgumentException("I need a subject item");
		}
		if (environment.getDescriptor() == null) {
			throw new IllegalArgumentException("I need a descriptor to set value");
		}
	}

	@Override
	protected EnvironmentState computeBeforeExecutionState() {
		return new ElementHasValueState(getOperationEnvironment());
	}

	@Override
	protected EnvironmentState doExecute(final IProgressMonitor monitor, final IAdaptable info)
			throws ExecutionException {
		final TimeStampedDataItem subject = getOperationEnvironment().getSubject();
		BeanUtil.setItemValue(subject, getOperationEnvironment().getDescriptor(), myNewValue);
		final EnvironmentState resultState = new ElementHasValueState(subject,
				getOperationEnvironment().getDescriptor());
		if (myFireRefresh) {

			final String setterName = BeanUtil.getSetterName(getOperationEnvironment().getDescriptor());
			final Method setter = BeanUtil.getSetter(subject, getOperationEnvironment().getDescriptor(), setterName);

			if (setter.isAnnotationPresent(FireReformatted.class)) {
				getOperationEnvironment().getSeries().fireReformatted(subject);
			}

			getOperationEnvironment().getSeries().fireModified(subject);
		}
		return resultState;
	}

	@Override
	protected void doUndo(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
		final TimeStampedDataItem subject = getOperationEnvironment().getSubject();
		final Object oldValue = getStateBeforeFirstRun().getValue();
		BeanUtil.setItemValue(subject, getOperationEnvironment().getDescriptor(), oldValue);
		if (myFireRefresh) {
			getOperationEnvironment().getSeries().fireModified(subject);
		}
	}

	@Override
	protected ElementHasValueState getStateBeforeFirstRun() {
		return (ElementHasValueState) super.getStateBeforeFirstRun();
	}

}
