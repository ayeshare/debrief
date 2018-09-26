/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2018, Deep Blue C Technology Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package org.mwc.debrief.core.ContextOperations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.CMAPOperation;
import org.mwc.cmap.core.property_support.RightClickSupport.RightClickContextItemGenerator;

import Debrief.ReaderWriter.Replay.ImportReplay;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;

/**
 * Generates a paste REP from clipboard action.
 * @author Ayesha <ayesha.ma@gmail.com>
 *
 */
public class GeneratePasteRepClipboard implements RightClickContextItemGenerator
{

  /* (non-Javadoc)
   * @see org.mwc.cmap.core.property_support.RightClickSupport.RightClickContextItemGenerator#generate(org.eclipse.jface.action.IMenuManager, MWC.GUI.Layers, MWC.GUI.Layer[], MWC.GUI.Editable[])
   */
  @Override
  public void generate(IMenuManager parent, Layers theLayers,
      Layer[] parentLayers, Editable[] subjects)
  {
    //see if there is nothing selected 
    if(subjects.length==0) {
      final Clipboard clip = CorePlugin.getDefault().getClipboard();
      final Object val = clip.getContents(TextTransfer.getInstance());
      if(val!=null) {
        final String clipBoardContent = (String)val;
        //See if there is plain text on the clipboard
        if(ImportReplay.isContentImportable(clipBoardContent)) {
          parent.add(createAction(theLayers, clipBoardContent));
        }

      }
    }
  }
  
  private Action createAction(final Layers theLayers, final String clipboardContent) {
    final Action doPasteAction = new Action("Paste REP")
    {
      @Override
      public void run()
      {
        PasteRepOperation operation = new PasteRepOperation("Paste from clipboard",theLayers, clipboardContent );
        CorePlugin.run(operation);
      }
    };
    //doPasteAction.setImageDescriptor();
    doPasteAction.setToolTipText("Paste REP from clipboard");
    return doPasteAction;
  }


  private static class PasteRepOperation extends CMAPOperation{

    private String _contentToImport;
    private Layers _tempLayers;
    private Layers _layers;
    
    
    public PasteRepOperation(String title,Layers theLayers,String contentToImport)
    {
      super(title);
      _contentToImport = contentToImport;
      _layers = theLayers;
    }
    @Override
    public IStatus execute(IProgressMonitor monitor, IAdaptable info)
        throws ExecutionException
    {
      
      ImportReplay tracker = new ImportReplay();
      _tempLayers = new Layers();
      //import to a temp layers object
      tracker.setLayers(_tempLayers);
      final int numLines = getNumLines(_contentToImport);
      if(numLines!=-1) {
        tracker.importThis(_contentToImport,numLines);
        ImportReplay.injectContent(_tempLayers, _layers, true);
      }
      
      
      return Status.OK_STATUS;
    }
    
   
    @Override
    public IStatus undo(IProgressMonitor monitor, IAdaptable info)
        throws ExecutionException
    {
      ImportReplay.injectContent(_tempLayers, _layers, false);
      return Status.OK_STATUS;
    }
    private final int getNumLines(final String text) {
      String[] lines = text.split("\\r?\\n");
      if(lines!=null) {
        return lines.length;
      }
      return -1;
      
    }
  }
}
