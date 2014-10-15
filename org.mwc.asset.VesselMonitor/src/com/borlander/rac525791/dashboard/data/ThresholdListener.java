/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)

 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package com.borlander.rac525791.dashboard.data;

public interface ThresholdListener {
	public void depthOnThresholdChanged(boolean isOkNow);
	public void speedOnThresholdChanged(boolean isOkNow);
	public void directionOnThresholdChanged(boolean isOkNow);
	
	public static final ThresholdListener NULL = new ThresholdListener(){
	
		public void speedOnThresholdChanged(boolean isOkNow) {
			// 
		}
	
		public void directionOnThresholdChanged(boolean isOkNow) {
			// 
		}
	
		public void depthOnThresholdChanged(boolean isOkNow) {
			// 	
		}
	
	};
}
