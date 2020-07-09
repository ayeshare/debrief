
package ASSET.Util.XML.Vessels;

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

abstract public class SSNHandler extends ParticipantHandler {

	static private final String myType = "SSN";

	static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {
		// create ourselves
		final org.w3c.dom.Element thisPart = doc.createElement(myType);

		ParticipantHandler.exportThis(toExport, thisPart, doc);

		parent.appendChild(thisPart);

	}

	public SSNHandler() {
		super(myType);

		addHandler(new ASSET.Util.XML.Movement.SSMovementCharsHandler() {
			@Override
			public void setMovement(final ASSET.Models.Movement.MovementCharacteristics chars) {
				_myMoveChars = chars;
			}
		});

	}

	@Override
	protected ASSET.ParticipantType getParticipant(final int index) {
		final ASSET.Models.Vessels.SSN thisVessel = new ASSET.Models.Vessels.SSN(index);
		return thisVessel;
	}
}