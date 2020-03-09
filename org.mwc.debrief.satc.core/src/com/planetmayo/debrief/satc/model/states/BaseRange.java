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

package com.planetmayo.debrief.satc.model.states;

public abstract class BaseRange<T extends BaseRange<?>> {

	/**
	 * exception for when constraints end up in an incompatible state. We
	 * immediately terminate processing constraints when this happens, so we're
	 * going to throw the exception and let it propagate back up the call chain
	 *
	 * @author ian
	 *
	 */
	public static class IncompatibleStateException extends Exception {

		/**
		 *
		 */
		private static final long serialVersionUID = 1L;
		private final String _message;
		private final BaseRange<?> _existingRange;
		private final BaseRange<?> _newRange;

		private volatile BoundedState _failingState;

		public IncompatibleStateException(final String message, final BaseRange<?> existingRange,
				final BaseRange<?> newRange) {
			super(message);
			_message = message;
			_existingRange = existingRange;
			_newRange = newRange;
		}

		/**
		 * get the existing (old) constraint
		 *
		 * @return
		 */
		public BaseRange<?> getExistingRange() {
			return _existingRange;
		}

		@Override
		public String getMessage() {
			String res = _message;
			if (_failingState != null)
				res += " at:" + _failingState.getTime();
			return res;
		}

		/**
		 * get the new constraint, the one we're trying to apply
		 *
		 * @return
		 */
		public BaseRange<?> getNewRange() {
			return _newRange;
		}

		public void setFailingState(final BoundedState state) {
			_failingState = state;
		}

	}

	/**
	 * apply another ranged constraint to this one
	 *
	 * @param other the other ranged constraint (of the same type as this)
	 * @throws IncompatibleStateException if the contraints are mutually exclusive
	 */
	abstract public void constrainTo(T other) throws IncompatibleStateException;
}
