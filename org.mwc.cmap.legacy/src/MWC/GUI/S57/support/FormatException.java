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

//
// <copyright>
//
//  BBN Technologies
//  10 Moulton Street
//  Cambridge, MA 02138
//  (617) 873-8000
//
//  Copyright (C) BBNT Solutions LLC. All rights reserved.
//
// </copyright>
// **********************************************************************
//
// $Source: i:/mwc/coag/asset/cvsroot/util/MWC/GUI/S57/support/FormatException.java,v $
// $RCSfile: FormatException.java,v $
// $Revision: 1.1 $
// $Date: 2007/04/27 09:20:01 $
// $Author: ian.mayo $
//
// **********************************************************************

package MWC.GUI.S57.support;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * This class is used for exceptions that result from some format errors of the
 * data when using the BinaryFile.
 */
public class FormatException extends Exception {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	/** nested exception that caused this one */
	final private Throwable rootCause;

	/**
	 * Construct a FormatException without a detail message.
	 */
	public FormatException() {
		super();
		rootCause = null;
	}

	/**
	 * Construct a FormatException with a detail message.
	 *
	 * @param s the detail message
	 */
	public FormatException(final String s) {
		super(s);
		rootCause = null;
	}

	/**
	 * Construct a FormatException with a detail message and root cause.
	 *
	 * @param s         the detail message
	 * @param rootCause the root cause (not null)
	 */
	public FormatException(final String s, final Throwable rootCause) {
		super(s + ": " + rootCause.getLocalizedMessage());
		this.rootCause = rootCause;
	}

	/**
	 * Returns the exception that caused this one.
	 *
	 * @return the root exception, or null if there isn't one
	 */
	public Throwable getRootCause() {
		return rootCause;
	}

	/**
	 * Prints a backtrace of this exception and the rootCause (if any) to
	 * System.err.
	 */
	@Override
	public void printStackTrace() {
		super.printStackTrace();
		if (rootCause != null) {
			System.err.println("With Root Cause:");
			rootCause.printStackTrace();
		}
	}

	/**
	 * Prints a backtrace of this exception and the rootCause (if any) to a stream.
	 *
	 * @param ps the stream to print to
	 */
	@Override
	public void printStackTrace(final PrintStream ps) {
		super.printStackTrace(ps);
		if (rootCause != null) {
			ps.println("With Root Cause:");
			rootCause.printStackTrace(ps);
		}
	}

	/**
	 * Prints a backtrace of this exception and the rootCause (if any) to a writer.
	 *
	 * @param pw the writer to print to
	 */
	@Override
	public void printStackTrace(final PrintWriter pw) {
		super.printStackTrace(pw);
		if (rootCause != null) {
			pw.println("With Root Cause:");
			rootCause.printStackTrace(pw);
		}
	}
}