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

package MWC.GUI;

/**
 * interface for classes that are able to fire/throw error messages
 *
 * @author Ian Mayo
 *
 */
public interface ErrorLogger {

	// NOTE these status codes are copied from RCP

	/**
	 * Status severity constant (value 0) indicating this status represents the
	 * nominal case. This constant is also used as the status code representing the
	 * nominal case.
	 *
	 * @see #getSeverity()
	 * @see #isOK()
	 */
	public static final int OK = 0;

	/**
	 * Status type severity (bit mask, value 1) indicating this status is
	 * informational only.
	 *
	 * @see #getSeverity()
	 * @see #matches(int)
	 */
	public static final int INFO = 0x01;

	/**
	 * Status type severity (bit mask, value 2) indicating this status represents a
	 * warning.
	 *
	 * @see #getSeverity()
	 * @see #matches(int)
	 */
	public static final int WARNING = 0x02;

	/**
	 * Status type severity (bit mask, value 4) indicating this status represents an
	 * error.
	 *
	 * @see #getSeverity()
	 * @see #matches(int)
	 */
	public static final int ERROR = 0x04;

	/**
	 * log an error, somehow
	 *
	 * @param status the status code (error, warning, etc)
	 * @param text   the error message to display/record
	 * @param e      any relevant exceptin
	 */
	public void logError(int status, String text, Exception e);

	/**
	 * log an error, somehow
	 *
	 * @param status    the status code (error, warning, etc)
	 * @param text      the error message to display/record
	 * @param e         any relevant exceptin
	 * @param revealLog whether to force the error log to be visible
	 */
	public void logError(int status, String text, Exception e, boolean revealLog);

	/**
	 * show the current call stack
	 *
	 */
	public void logStack(int status, String text);
}
