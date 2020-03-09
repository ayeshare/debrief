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
package info.limpet.stackedcharts.ui.editor.commands;

import org.eclipse.emf.common.util.EList;
import org.eclipse.gef.commands.Command;

import info.limpet.stackedcharts.model.DependentAxis;
import info.limpet.stackedcharts.model.impl.ChartImpl;

public class MoveAxisCommand extends Command {
	/**
	 * convenience class to find the relevant list (min/max axes) for the supplied
	 * axis
	 *
	 * @param axis
	 * @return
	 */
	public static EList<DependentAxis> getHostListFor(final DependentAxis axis) {
		final ChartImpl chart = (ChartImpl) axis.eContainer();
		// ok, find out which item this is in
		final EList<DependentAxis> minAxes = chart.getMinAxes();
		final EList<DependentAxis> maxAxes = chart.getMaxAxes();

		// check if max has it
		if (maxAxes.contains(axis)) {
			return maxAxes;
		}

		return minAxes;
	}

	private final DependentAxis axis;
	private final EList<DependentAxis> destination;
	private final EList<DependentAxis> source;
	private final int index;

	private final int redoIndex;

	public MoveAxisCommand(final EList<DependentAxis> destination, final DependentAxis axis) {
		this(destination, axis, -1);
	}

	public MoveAxisCommand(final EList<DependentAxis> destination, final DependentAxis axis, final int index) {
		this.axis = axis;
		this.destination = destination;
		this.index = index;
		source = getHostListFor(axis);
		redoIndex = source.indexOf(axis);
	}

	@Override
	public void execute() {
		source.remove(axis);

		if (index > -1) {
			destination.add(index, axis);
		} else {
			destination.add(axis);
		}
	}

	@Override
	public void undo() {
		destination.remove(axis);

		if (redoIndex != -1) {
			source.add(redoIndex, axis);
		} else {
			source.add(axis);
		}
	}

}
