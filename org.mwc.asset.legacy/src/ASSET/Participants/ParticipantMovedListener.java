
package ASSET.Participants;

import ASSET.ScenarioType;

/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

public interface ParticipantMovedListener extends java.util.EventListener {
	public class Helper implements ParticipantMovedListener {
		/**
		 * the list of helpers we support
		 *
		 */
		private java.util.Vector<ParticipantMovedListener> _myListeners;

		/**
		 * add the new listener
		 *
		 */
		public void addListener(final ParticipantMovedListener listener) {
			if (_myListeners == null)
				_myListeners = new java.util.Vector<ParticipantMovedListener>(1, 1);

			_myListeners.add(listener);
		}

		/**
		 * handle the new event
		 *
		 */
		@Override
		public void moved(final ASSET.Participants.Status newStatus) {
			if (_myListeners != null) {
				final java.util.Iterator<ParticipantMovedListener> it = _myListeners.iterator();
				while (it.hasNext()) {
					final ParticipantMovedListener list = it.next();
					list.moved(newStatus);
				}
			}
		}

		/**
		 * remove this listener
		 *
		 */
		public void removeListener(final ParticipantMovedListener listener) {
			_myListeners.remove(listener);
		}

		/**
		 * the scenario has restarted
		 *
		 */
		@Override
		public void restart(final ScenarioType scenario) {
			//
		}
	}

	/**
	 * this participant has moved
	 *
	 */
	public void moved(ASSET.Participants.Status newStatus);

	/**
	 * the scenario has restarted
	 *
	 * @param scenario TODO
	 *
	 */
	public void restart(ScenarioType scenario);

}