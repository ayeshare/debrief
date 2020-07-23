package Debrief.Wrappers.Track;

import java.util.Enumeration;

import Debrief.Wrappers.FixWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Plottables;
import MWC.GenericData.HiResDate;

public class FixWrapperCollisionCheck {

	/**
	 * We are creating this method because the following ticket
	 * https://github.com/debrief/debrief/issues/4894
	 *
	 * FixWrapper were initially adding with a +1 time delay to avoid collisions.
	 *
	 * Collision check modifies the time in the fixWrapper given to adapt it to
	 * avoid collision.
	 *
	 * @param _fixWrapper       FixWrapper to modify
	 * @param _currentPlotables List of all the Editables
	 */
	public static void correctTimeCollision(final FixWrapper _fixWrapper, final Plottables _currentPlotables) {
		long timeToAdd = _fixWrapper.getTime().getMicros();
		
		// First check if the exact instance is the list.
		final Enumeration<Editable> elementsEnum = _currentPlotables.elements();
		while (elementsEnum.hasMoreElements()) {
			if (elementsEnum.nextElement() == _fixWrapper) {
				// We have found it.
				return;
			}
		}
		
		while (_currentPlotables.contains(_fixWrapper)) {
			timeToAdd += 1000; // add 1 second
			_fixWrapper.getFix().setTime(new HiResDate(timeToAdd / 1000L));
		}
	}
}
