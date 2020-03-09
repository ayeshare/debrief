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

package MWC.GUI.Properties;

import java.beans.PropertyEditorSupport;

public class AttributeTypePropertyEditor extends PropertyEditorSupport {

	public static final int ARROW = 0;
	public static final int SYMBOL = 1;
	public static final int LABEL = 2;

	protected Integer _myCalcModel;

	private String[] _myTags;

	@Override
	public String getAsText() {
		String res = null;
		final int index = _myCalcModel.intValue();
		res = getTags()[index];
		return res;
	}

	@Override
	public String[] getTags() {
		if (_myTags == null) {
			_myTags = new String[3];
			_myTags[ARROW] = "Arrow";
			_myTags[SYMBOL] = "Symbol";
			_myTags[LABEL] = "Label";
		}
		return _myTags;
	}

	@Override
	public Object getValue() {
		return _myCalcModel;
	}

	@Override
	public void setAsText(final String inVal) {
		final String upVal = inVal.toUpperCase();
		for (int i = 0; i < getTags().length; i++) {
			final String thisStr = getTags()[i].toUpperCase();
			if (thisStr.equals(upVal))
				_myCalcModel = new Integer(i);
		}
	}

	@Override
	public void setValue(final Object p1) {
		if (p1 instanceof Integer) {
			_myCalcModel = (Integer) p1;
		} else if (p1 instanceof String) {
			final String val = (String) p1;
			setAsText(val);
		}
	}
}
