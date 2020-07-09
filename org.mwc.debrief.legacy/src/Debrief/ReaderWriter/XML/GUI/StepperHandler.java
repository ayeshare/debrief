
package Debrief.ReaderWriter.XML.GUI;

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

import java.text.ParseException;

import Debrief.GUI.Frames.Session;
import Debrief.GUI.Tote.StepControl;
import Debrief.GUI.Tote.Painters.SnailPainter;
import Debrief.GUI.Tote.Painters.SnailPainter2;
import Debrief.ReaderWriter.XML.GUIHandler;
import Debrief.ReaderWriter.XML.GUIHandler.ComponentDetails;
import MWC.GUI.StepperListener;
import MWC.GenericData.HiResDate;
import MWC.Utilities.Errors.Trace;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;

public final class StepperHandler implements GUIHandler.ComponentCreator {

	public final GUIHandler.ComponentDetails exportThis(final Session session) {
		// get the stepper
		final StepControl stepper = session.getStepControl();
		if (stepper != null) {
			// collate the details for this component
			final GUIHandler.ComponentDetails details = new GUIHandler.ComponentDetails();

			final StepperListener theStepper = stepper.getCurrentPainter();

			// is this the snail painter?
			if (theStepper instanceof SnailPainter) {
				final SnailPainter sp = (SnailPainter) theStepper;
				details.addProperty("VectorStretch", MWCXMLReader.writeThis(sp.getVectorStretch()));
			}

			final StepperListener painter = stepper.getCurrentPainter();
			if (painter != null) {
				details.addProperty("Cursor", painter.toString());
			}

			// ok, we're switching to exporting the step size in microseconds
			// if we ever get the plain "StepLarge" parameter - we will assume it is millis,
			// else
			// we will always receive the units

			details.addProperty("StepLargeMicros", MWCXMLReader.writeThis(stepper.getStepLarge()));
			details.addProperty("StepSmallMicros", MWCXMLReader.writeThis(stepper.getStepSmall()));

			details.addProperty("AutoStep", MWCXMLReader.writeThis(stepper.getAutoStep()));

			// note: we trim the date format, since it may have received
			// white padding in order that all formats consume the same
			// screen width
			details.addProperty("DateFormat", stepper.getDateFormat().trim());
			// the current DTG
			final HiResDate cTime = stepper.getCurrentTime();
			if (cTime != null)
				details.addProperty("CurrentTime", MWCXMLReader.writeThis(cTime));
			// the T-zero, if set
			if (stepper.getTimeZero() != null)
				details.addProperty("TimeZero", MWCXMLReader.writeThis(stepper.getTimeZero()));
			details.addProperty("Highlighter", stepper.getCurrentHighlighter().getName());
			// what's the time?
			final HiResDate theStartTime = stepper.getToolboxStartTime();
			if (theStartTime != null)
				details.addProperty("Toolbox_Start_Time", DebriefFormatDateTime.toStringHiRes(theStartTime));
			final HiResDate theEndTime = stepper.getToolboxEndTime();
			if (theEndTime != null)
				details.addProperty("Toolbox_End_Time", DebriefFormatDateTime.toStringHiRes(theEndTime));

			return details;
		} else
			return null;
	}

	@Override
	public final void makeThis(final ComponentDetails details, final Session session) {
		String val;
		final StepControl step = session.getStepControl();

		//////////////////////////////////////////////////////////////
		final String cursor = details.properties.get("Cursor");
		if (cursor != null) {
			// set the cursor
			step.setPainter(cursor);

			//////////////////////////////////////////////////////////////
			// is this the snail cursor?
			if (cursor.equals(SnailPainter.SNAIL_NAME)) {
				final String vector_stretch = details.properties.get("VectorStretch");
				if (vector_stretch != null) {
					// set the cursor

					try {
						if (step.getCurrentPainter() instanceof SnailPainter) {
							final SnailPainter sp = (SnailPainter) step.getCurrentPainter();
							sp.setVectorStretch(MWCXMLReader.readThisDouble(vector_stretch));
						} else if (step.getCurrentPainter() instanceof SnailPainter2) {
							final SnailPainter2 sp = (SnailPainter2) step.getCurrentPainter();
							sp.setVectorStretch(MWCXMLReader.readThisDouble(vector_stretch));
						}
					} catch (final ParseException pe) {
						MWC.Utilities.Errors.Trace.trace(pe,
								"Reader: Whilst reading in VectorStretch value of :" + vector_stretch);
					}
				}
			}
		}

		//////////////////////////////////////////////////////////////
		final String dateFormat = details.properties.get("DateFormat");
		if (dateFormat != null) {
			// set the cursor
			step.setDateFormat(dateFormat);
		}

		//////////////////////////////////////////////////////////////
		final String highlighter = details.properties.get("Highlighter");
		if (highlighter != null) {
			// set the cursor
			step.setHighlighter(highlighter);
		}

		//////////////////////////////////////////////////////////////
		final String start_time = details.properties.get("Toolbox_Start_Time");
		if (start_time != null) {
			// get a date from this
			try {
				// set the cursor
				step.setToolboxStartTime(DebriefFormatDateTime.parseThis(start_time));
			} catch (final ParseException e) {
				Trace.trace(e, "While parsing start time");
			}
		}

		//////////////////////////////////////////////////////////////
		final String end_time = details.properties.get("Toolbox_End_Time");
		if (end_time != null) {
			// get a date from this
			try {
				// set the cursor
				step.setToolboxEndTime(DebriefFormatDateTime.parseThis(end_time));
			} catch (final ParseException e) {
				Trace.trace(e, "While parsing end time");
			}
		}

		//////////////////////////////////////////////////////////////
		final String tZero = details.properties.get("TimeZero");
		if (tZero != null) {
			// get a date from this
			try {
				// set the cursor
				step.setTimeZero(DebriefFormatDateTime.parseThis(tZero));
			} catch (final ParseException e) {
				Trace.trace(e, "While parsing time zero");
			}
		}

		//////////////////////////////////////////////////////////////
		final String currentTime = details.properties.get("CurrentTime");
		if (currentTime != null) {
			// and set the time
			try {
				final HiResDate dtg = DebriefFormatDateTime.parseThis(currentTime);

				// did we find a valid dtg?
				if (dtg != null)
					step.changeTime(dtg);
			} catch (final ParseException e) {
				Trace.trace(e, "While parsing current time");
			}
		}

		//////////////////////////////////////////////////////////////
		val = details.properties.get("AutoStep");
		if (val != null) {
			// set the auto step to this number of millis
			step.setAutoStep(Integer.valueOf(val).intValue());
		}

		///////////////////////////////////////////////////////////////
		val = details.properties.get("StepLarge");
		if (val != null) {
			// set the large step to this number of millis
			try {
				final double len = MWCXMLReader.readThisDouble(val);
				step.setStepLarge((long) len * 1000);
			} catch (final java.text.ParseException pe) {
				MWC.Utilities.Errors.Trace.trace(pe, "Failed reading large step size value is:" + val);
			}
		}

		///////////////////////////////////////////////////////////////
		val = details.properties.get("StepSmall");
		if (val != null) {
			try {
				// set the small step to this number of millis
				final double len = MWCXMLReader.readThisDouble(val);
				step.setStepSmall((long) len * 1000);
			} catch (final java.text.ParseException pe) {
				MWC.Utilities.Errors.Trace.trace(pe, "Failed reading small step size value is:" + val);
			}
		}

		///////////////////////////////////////////////////////////////
		val = details.properties.get("StepLargeMicros");
		if (val != null) {
			// set the large step to this number of millis
			try {
				step.setStepLarge((long) MWCXMLReader.readThisDouble(val));
			} catch (final java.text.ParseException pe) {
				MWC.Utilities.Errors.Trace.trace(pe, "Failed reading large step size value is:" + val);
			}
		}

		///////////////////////////////////////////////////////////////
		val = details.properties.get("StepSmallMicros");
		if (val != null) {
			try {
				// set the small step to this number of millis
				step.setStepSmall((long) MWCXMLReader.readThisDouble(val));
			} catch (final java.text.ParseException pe) {
				MWC.Utilities.Errors.Trace.trace(pe, "Failed reading small step size value is:" + val);
			}
		}

		// just do some minor tidying here, to check we have start & end times for the
		// slider
		if ((step.getStartTime() == null) || (step.getEndTime() == null)) {
			step.recalcTimes();
		}
	}
}