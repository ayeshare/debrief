/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package com.planetmayo.debrief.satc_rcp.ui.converters.units;

public class UnitConverter
{
	public static final UnitConverter SPEED_KTS = new UnitConverter(new MSecToKts(), new KtsToMSec());
	public static final UnitConverter ANGLE_DEG = new UnitConverter(new RadsToDeg(), new DegToRads());
	
	private final AbstractUnitConverter modelToUI;
	private final AbstractUnitConverter uiToModel;
	
	UnitConverter(AbstractUnitConverter modelToUI, AbstractUnitConverter uiToModel) {
		this.modelToUI = modelToUI;
		this.uiToModel = uiToModel;
	}

	public AbstractUnitConverter getModelToUI()
	{
		return modelToUI;
	}

	public AbstractUnitConverter getUIToModel()
	{
		return uiToModel;
	}
	
	public static UnitConverter scale(UnitConverter converter, double scale)
	{
		return new UnitConverter(
				new ScaleConverter(converter.getModelToUI(), scale), 
				new ScaleConverter(converter.getUIToModel(), 1 / scale)
		);
	}
	
}
