package org.mwc.cmap.core.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.*;
import org.eclipse.core.runtime.*;
import org.mwc.cmap.core.CorePlugin;

import MWC.GUI.Layers;
import MWC.GUI.Tools.Action;

public class DebriefActionWrapper extends AbstractOperation
{

	final private Action _myAction;
	
	final private Layers _myLayers;

	/** constructor, so that we can wrap our action
	 * 
	 * @param theAction
	 */
	public DebriefActionWrapper(Action theAction, final Layers theLayers)
	{
		super(theAction.toString());
		
		_myLayers = theLayers;
		
		// put in the global context, for some reason
		super.addContext(CorePlugin.CMAP_CONTEXT);
		
		_myAction = theAction;
	}
	
	/** constructor, so that we can wrap our action
	 * 
	 * @param theAction
	 */
	public DebriefActionWrapper(Action theAction)
	{
		super(theAction.toString());
		
		_myLayers = null;
		
		// put in the global context, for some reason
		super.addContext(CorePlugin.CMAP_CONTEXT);
		
		_myAction = theAction;
	}	
	//////////////////////////////////////////////////////////////
	// eclipse action bits
	//////////////////////////////////////////////////////////////
	
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException
	{
		_myAction.execute();

		// hey, fire update
		if(_myLayers != null)
			_myLayers.fireModified(null);
		
		return Status.OK_STATUS;
	}

	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException
	{
		if(_myAction.isRedoable())
		{
			_myAction.execute();
			
			// and fire update
			if(_myLayers != null)
				_myLayers.fireModified(null);			
			
		}
		
		return Status.OK_STATUS;
	}

	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException
	{
		if(_myAction.isUndoable())
		{
			_myAction.undo();
			
			// and fire update
			if(_myLayers != null)
				_myLayers.fireModified(null);			
		}

		return Status.OK_STATUS;
	}

	/**
	 * @return
	 */
	public boolean canExecute()
	{
		return true;
	}

	/**
	 * @return
	 */
	public boolean canRedo()
	{
		return _myAction.isRedoable();
	}

	/**
	 * @return
	 */
	public boolean canUndo()
	{
		return _myAction.isUndoable();
	}


	/**
	 * @return
	 */
	public String toString()
	{
		return _myAction.toString();
	}
	
	


}
