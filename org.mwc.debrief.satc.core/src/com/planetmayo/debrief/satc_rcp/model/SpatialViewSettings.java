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

package com.planetmayo.debrief.satc_rcp.model;

import java.util.HashSet;
import java.util.Set;

public class SpatialViewSettings {
	public static interface SpatialSettingsListener {

		void onSettingsChanged();
	}

	private final Set<SpatialSettingsListener> _listeners = new HashSet<SpatialSettingsListener>();

	/**
	 * level of diagnostics for user
	 */
	private boolean _showLegEndBounds;

	/**
	 * level of diagnostics for user
	 */
	private boolean _showAllBounds;

	/**
	 * level of diagnostics for user
	 */
	private boolean _showPoints;

	/**
	 * level of diagnostics for user
	 */
	private boolean _showAchievablePoints;

	/**
	 * level of diagnostics for user
	 */
	private boolean _showRoutes;

	/**
	 * level of diagnostics for user
	 */
	private boolean _showRoutesWithScores;

	/**
	 * level of diagnostics for user
	 */
	private boolean _showRecommendedSolutions;

	/**
	 * level of diagnostics for user
	 */
	private boolean _showIntermediateGASolutions;

	private boolean _showRoutePointLabels;

	private boolean _showRoutePoints;

	private boolean _showTargetSolution;

	public void addListener(final SpatialSettingsListener listener) {
		_listeners.add(listener);
	}

	private void fireSettingsChanged() {
		for (final SpatialSettingsListener listener : _listeners) {
			listener.onSettingsChanged();
		}
	}

	public boolean isShowAchievablePoints() {
		return _showAchievablePoints;
	}

	public boolean isShowAllBounds() {
		return _showAllBounds;
	}

	public boolean isShowIntermediateGASolutions() {
		return _showIntermediateGASolutions;
	}

	public boolean isShowLegEndBounds() {
		return _showLegEndBounds;
	}

	public boolean isShowPoints() {
		return _showPoints;
	}

	public boolean isShowRecommendedSolutions() {
		return _showRecommendedSolutions;
	}

	public boolean isShowRoutePointLabels() {
		return _showRoutePointLabels;
	}

	public boolean isShowRoutePoints() {
		return _showRoutePoints;
	}

	public boolean isShowRoutes() {
		return _showRoutes;
	}

	public boolean isShowRoutesWithScores() {
		return _showRoutesWithScores;
	}

	public boolean isShowTargetSolution() {
		return _showTargetSolution;
	}

	public void removeListener(final SpatialSettingsListener listener) {
		_listeners.remove(listener);
	}

	public void setShowAchievablePoints(final boolean onOff) {
		_showAchievablePoints = onOff;
		fireSettingsChanged();
	}

	public void setShowAllBounds(final boolean onOff) {
		_showAllBounds = onOff;
		fireSettingsChanged();
	}

	public void setShowIntermediateGASolutions(final boolean onOff) {
		_showIntermediateGASolutions = onOff;
		fireSettingsChanged();
	}

	public void setShowLegEndBounds(final boolean onOff) {
		_showLegEndBounds = onOff;
		fireSettingsChanged();
	}

	public void setShowPoints(final boolean onOff) {
		_showPoints = onOff;
		fireSettingsChanged();
	}

	public void setShowRecommendedSolutions(final boolean onOff) {
		_showRecommendedSolutions = onOff;
		fireSettingsChanged();
	}

	public void setShowRoutePointLabels(final boolean onOff) {
		_showRoutePointLabels = onOff;
		fireSettingsChanged();
	}

	public void setShowRoutePoints(final boolean onOff) {
		_showRoutePoints = onOff;
		fireSettingsChanged();
	}

	public void setShowRoutes(final boolean onOff) {
		_showRoutes = onOff;
		fireSettingsChanged();
	}

	public void setShowRoutesWithScores(final boolean onOff) {
		_showRoutesWithScores = onOff;
		fireSettingsChanged();
	}

	public void setShowTargetSolution(final boolean onOff) {
		_showTargetSolution = onOff;
		fireSettingsChanged();
	}
}
