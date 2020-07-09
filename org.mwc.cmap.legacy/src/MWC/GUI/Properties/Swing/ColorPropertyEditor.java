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

package MWC.GUI.Properties.Swing;

// Copyright MWC 1999, Debrief 3 Project

// $RCSfile: ColorPropertyEditor.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: ColorPropertyEditor.java,v $
// Revision 1.2  2004/05/25 15:29:30  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:20  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:26  Ian.Mayo
// Initial import
//
// Revision 1.4  2003-05-08 13:49:26+01  ian_mayo
// Override parent hasCustomEditor method
//
// Revision 1.3  2003-03-03 14:06:15+00  ian_mayo
// Implement custom color editors
//
// Revision 1.2  2002-05-28 09:25:48+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:32+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:01:24+01  ian_mayo
// Initial revision
//
// Revision 1.1  2001-08-10 13:22:51+01  administrator
// handle instance where we don't have an existing value
//
// Revision 1.0  2001-07-17 08:43:29+01  administrator
// Initial revision
//
// Revision 1.2  2001-01-24 11:38:47+00  novatech
// highlight current value in drop-down combo lists
//
// Revision 1.1  2001-01-03 13:42:37+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:45:35  ianmayo
// initial version
//
// Revision 1.2  2000-08-15 15:43:11+01  ian_mayo
// try to make control smaller
//
// Revision 1.1  2000-08-15 11:43:26+01  ian_mayo
// Initial revision
//

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

/**
 * Swing implementation of colour property editor
 */
public class ColorPropertyEditor extends MWC.GUI.Properties.ColorPropertyEditor implements ActionListener {
	/**
	 * class which will draw a rectangle in the correct colour
	 */
	static class ColorIcon implements Icon {
		/**
		 * the colour of this icon
		 */
		private Color color;
		/**
		 * width and height of this icon
		 */
		private final int w, h;

		/**
		 * default (gray) constructor
		 */
		public ColorIcon() {
			this(Color.gray, 50, 12);
		}

		/**
		 * normal constructor
		 *
		 * @param color the colour to use
		 * @param w     the width of this icon
		 * @param h     the height of this icon
		 */
		public ColorIcon(final Color color, final int w, final int h) {
			this.color = color;
			this.w = w;
			this.h = h;
		}

		/**
		 * get the current colour
		 *
		 * @return the current colour
		 */
		public Color getColor() {
			return color;
		}

		/**
		 * height of this icon
		 *
		 * @return height
		 */
		@Override
		public int getIconHeight() {
			return h;
		}

		/**
		 * get the current width of this icon
		 *
		 * @return width
		 */
		@Override
		public int getIconWidth() {
			return w;
		}

		/**
		 * paint method
		 *
		 * @param c component to paint
		 * @param g graphics destination
		 * @param x x coordinate
		 * @param y y coordinate
		 */
		@Override
		public void paintIcon(final Component c, final Graphics g, final int x, final int y) {
			g.setColor(Color.black);
			g.drawRect(x, y, w - 1, h - 1);
			g.setColor(color);
			g.fillRect(x + 1, y + 1, w - 2, h - 2);
		}

		/**
		 * set the colour of this icon
		 *
		 * @param color the colour
		 */
		public void setColor(final Color color) {
			this.color = color;
		}
	}

	//////////////////////////////////////
	// internal class which keeps track of the current value
	//////////////////////////////////////
	@SuppressWarnings("rawtypes")
	private class TickableComboBox extends JComboBox {
		///////////////////////////////////////////////
		// our new, custom renderer for showing colours
		////////////////////////////////////////////////
		/**
		 * class which provides a "Label" rendition of a colour together with a
		 * rectangle in that colour
		 */
		class ColorRenderer extends JLabel implements ListCellRenderer {
			/**
				 *
				 */
			private static final long serialVersionUID = 1L;

			/**
			 * rectangular object which gets shaded in a particular colour
			 */
			private final ColorIcon icon = new ColorIcon();

			/**
			 * red border, to show the currently selected item
			 */
			private final javax.swing.border.Border redBorder = BorderFactory.createLineBorder(Color.red, 1),
					emptyBorder = BorderFactory.createEmptyBorder(1, 1, 1, 1);

			/**
			 * get a rendered component for this data item
			 *
			 * @param list         the list being edited
			 * @param value        the value of this item
			 * @param index        the index of this item
			 * @param isSelected   whether this item is selected
			 * @param cellHasFocus whether this item has focus
			 * @return a component to use
			 */
			@Override
			public Component getListCellRendererComponent(final JList list, final Object value, final int index,
					final boolean isSelected, final boolean cellHasFocus) {
				final NamedColor nm = (NamedColor) value;
				if (isCurrentValue(index)) {
					setForeground(Color.red);
				} else {
					setForeground(Color.black);
				}
				if (nm != null) {
					icon.setColor(nm.getColor());
					setIcon(icon);
					setText(nm.name);
				}

				if (isSelected) {
					setBorder(redBorder);
				} else {
					setBorder(emptyBorder);
				}

				return this;
			}
		}

		////////////////////////////////////////////////////////////
		// end of renderer
		////////////////////////////////////////////////////////////
		/**
			 *
			 */
		private static final long serialVersionUID = 1L;

		/**
		 * the current value - either set from code, or following and ActionEvent (user
		 * selecting the value)
		 */
		private int _currentValue;

		@SuppressWarnings("unchecked")
		public TickableComboBox(final Vector<?> list) {
			super(list);
			this.setRenderer(new ColorRenderer());
			this.addActionListener(this);
		}

		/**
		 * event handler so that we are informed when the user sets a new value
		 *
		 */
		@Override
		public void actionPerformed(final java.awt.event.ActionEvent event) {
			setCurrentValue(this.getSelectedIndex());

		}

		/**
		 * accessor method to determine if this index is the current value
		 */
		public boolean isCurrentValue(final int index) {
			return (_currentValue == index);
		}

		/**
		 * modifier (used near construction) to set the current value of the list
		 *
		 */
		private void setCurrentValue(final int index) {
			_currentValue = index;
		}

		////////////////////////////////////////////////////////////
		// custom renderer which can call the parent object to
		// determine the current value of the list, and which does
		// not depend on whether the value is selected
		////////////////////////////////////////////////////////////

		/**
		 * over-ride this method so that we can update our index of the current value
		 */
		@Override
		public void setSelectedItem(final Object oj) {
			super.setSelectedItem(oj);
			setCurrentValue(getSelectedIndex());
		}

		////////////////////////////////////////////////////////////
		// end of custom component
		////////////////////////////////////////////////////////////

	}

	/////////////////////////////////////////////////////////////
	// constructor
	////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////
	// member functions
	////////////////////////////////////////////////////////////

//  /** initialise the list of colours
//   */
//  public Vector<NamedColor> createColors()
//  {
//    final Vector<NamedColor> res = super.createColors();
//
//    // and append our custom item
//    res.add(new NamedColor(CUSTOM_LABEL, Color.white));
//
//    return res;
//
//  }

	/////////////////////////////////////////////////////////////
	// member variables
	////////////////////////////////////////////////////////////
	/**
	 * combobox containing our choices
	 */
	@SuppressWarnings("rawtypes")
	JComboBox _theList;

	/**
	 * the label indicating a custom colour
	 *
	 */
	private final String CUSTOM_LABEL = "Custom ...";

	/**
	 * user has selected something from the combo box
	 *
	 * @param p1 data for action performed
	 */
	@Override
	public void actionPerformed(final ActionEvent p1) {
		// retrieve the value from the list
		final String selectedColor = _theList.getSelectedItem().toString();

		// is it the custom item?
		if (selectedColor.equals(CUSTOM_LABEL)) {
			// see if the colour is our "custom" colour. If so,
			// popup the custom dialog
			final Color theCol = JColorChooser.showDialog(null, "Select Color", (Color) this.getValue());

			if (!(theCol == null)) {
				setValue(theCol);
			}

		} else {
			setAsText(selectedColor);
		}

	}

	/**
	 * effectively the constructor which creates the panel
	 *
	 * @return the component containing the editor
	 */
	@Override
	public java.awt.Component getCustomEditor() {
		final JPanel _theHolder = new JPanel();
		_theHolder.setLayout(new java.awt.BorderLayout());

		_theList = new TickableComboBox(_theColors);
		_theList.addActionListener(this);

		_theHolder.add("Center", _theList);

		// now initialise our values
		setData();

		return _theHolder;
	}

	/**
	 * don't really bother, not if you don't want to anyway
	 */
	protected void resetData() {

	}

	/**
	 * update the GUI to reflect the new value
	 */
	protected void setData() {
		// remove us from the action listener list, so we don't get recursive
		_theList.removeActionListener(this);

		// get the current colour
		_theList.setSelectedItem(getNamedColor());

		//
		_theList.addActionListener(this);
	}

	/**
	 * reflect the new color value
	 *
	 * @param p1 new colour
	 */
	@Override
	public void setValue(final Object p1) {
		super.setValue(p1);

		if (p1 instanceof java.awt.Color) {
			if (_theList != null) {
				setData();
			}
		}
	}

	/**
	 * whether we can provide a custom editor component
	 *
	 * @return yes, of course
	 */
	@Override
	public boolean supportsCustomEditor() {
		return true;
	}

}
