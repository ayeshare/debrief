package MWC.GUI;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

/**
 * class that represents an external datafile
 * 
 * @author ian
 * 
 */
public class ExternallyManagedDataLayer extends BaseLayer
{

	// ////////////////////////////////////////////////////
	// bean info for this class
	// ///////////////////////////////////////////////////
	public class ExternalLayerInfo extends Editable.EditorType
	{

		public ExternalLayerInfo(final ExternallyManagedDataLayer data)
		{
			super(data, data.getName(), "");
		}

		public PropertyDescriptor[] getPropertyDescriptors()
		{
			try
			{
				final PropertyDescriptor[] res =
				{ prop("Visible", "the Layer visibility", VISIBILITY),
						prop("Name", "the name of the Layer", FORMAT) };

				return res;

			}
			catch (final IntrospectionException e)
			{
				return super.getPropertyDescriptors();
			}
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * filename where data is loaded from
	 * 
	 */
	private final String _fileName;

	/**
	 * the type of this data (used to decide which decoder/manager to use)
	 * 
	 */
	private final String _dataType;

	public ExternallyManagedDataLayer(final String dataType, final String layerName,
			final String fileName)
	{
		setName(layerName);
		_dataType = dataType;
		_fileName = fileName;
	}

	/** whether this type of BaseLayer is able to have shapes added to it
	 * 
	 * @return
	 */
	@Override
	public boolean canTakeShapes()
	{
		return false;
	}
	
	public final String getDataType()
	{
		return _dataType;
	}

	public final String getFilename()
	{
		return _fileName;
	}
	

	public Editable.EditorType getInfo()
	{
		if (_myEditor == null)
			_myEditor = new ExternalLayerInfo(this);

		return _myEditor;
	}
}