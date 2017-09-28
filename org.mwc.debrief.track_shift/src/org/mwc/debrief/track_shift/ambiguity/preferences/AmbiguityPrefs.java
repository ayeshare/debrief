/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2016, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.debrief.track_shift.ambiguity.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.debrief.track_shift.TrackShiftActivator;

public class AmbiguityPrefs extends FieldEditorPreferencePage implements
    IWorkbenchPreferencePage
{

  public AmbiguityPrefs()
  {
    super("Ambiguity Resolution", CorePlugin
        .getImageDescriptor("icons/24/debrief_icon.png"), GRID);
    setPreferenceStore(TrackShiftActivator.getDefault().getPreferenceStore());
    setDescription("Customise resolving bearing ambiguity (including debugging)");
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
   */
  public void init(IWorkbench workbench)
  {
    // empty body.
  }
  
  /**
   * Creates the field editors. Field editors are abstractions of the common GUI blocks needed to
   * manipulate various types of preferences. Each field editor knows how to save and restore
   * itself.
   */
  public void createFieldEditors()
  {
    addField(new StringFieldEditor(PreferenceConstants.MIN_ZIG,
        "Minimum ambiguity rate delta to treat as manoeuvering, such as 0.6 (degs/sec)", getFieldEditorParent()));
    addField(new StringFieldEditor(PreferenceConstants.MAX_STEADY,
        "Maximum ambiguity rate delta to treat as steady, such as 0.2 (degs/sec)", getFieldEditorParent()));
    addField(new BooleanFieldEditor(PreferenceConstants.DIAGNOSTICS,
        "Output slicing diagnostics to Error Log", getFieldEditorParent()));
    addField(new BooleanFieldEditor(PreferenceConstants.DISPLAY,
        "Display controls for TA data slicing", getFieldEditorParent()));
  }

}