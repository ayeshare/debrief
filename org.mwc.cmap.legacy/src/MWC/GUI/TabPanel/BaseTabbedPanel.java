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

package MWC.GUI.TabPanel;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Panel;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.util.Vector;

//	05/07/97	LAB	Updated to support Java 1.1
//	06/05/97	LAB	Made all private variables used in the innerclasses, protected.
//	06/05/97	LAB	Updated deprecated calls to DirectionButton's enable and disable
//					methods to the new setEnabled(boolean).
//	07/19/97	LAB	Added add/removeNotify for event listener registration. Updated deprecated
//					preferredSize and minimumSize calls.  Added a version tag
//  07/25/97    CAR marked fields transient as needed
//                  innerclasses implement java.io.Serializable
//	08/13/97	LAB	Now draws the borders with colors calculated from the backround color, and
//					draws the text with colors calculated from the foreground color.  This
//					addresses Mac Bug #7189.  Removed unneeded imports.
//	08/15/97	LAB	Reworked the way colors were calculated to avoid NullPointerExceptions,
//					and potential redraw problems.  Now colors are recalculated in paint,
//					if needed.
//	08/21/97	RKM	Added setSuppressRepaints, bottle necked repaint calls for better redraws
//					Used setSuppressRepaints in setCurrentTab
//  08/28/97    CAR can now specify an index position for addTab

/**
 * BaseTabbedPanel is a Panel extension which provides for a tabbed dialog
 * effect. It provides the visual aspect of tabs and allows the programmer to
 * doDecide what action to take when a tab is activated. It can be used directly
 * or extended. When extending from BaseTabbedPanel be sure to super() during
 * construction and to super.handleEvent(evt) from handleEvent if you override
 * it.
 * <p>
 * To add a new tab to the panel use the addTab() method. To update an existing
 * tab use the setLabel() or setTab() method. To remove tabs use the removeTab()
 * or removeAllTabs() method.
 * <p>
 * To enable or disable a tab use the setEnabled() method.
 * <p>
 * To show (activate) a tab use the showTab() method.
 *
 * @author Symantec
 * @author Scott Fauerbach
 * @version 1.1, July 19, 1997
 */
public abstract class BaseTabbedPanel extends Panel {
	/**
	 * This is the Action Event handling innerclass.
	 */
	class Action implements java.awt.event.ActionListener, java.io.Serializable {
		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(final ActionEvent e) {
			if (e.getSource() == dbLeft) {
				if (--firstVisibleTab < 0)
					firstVisibleTab = 0;
				else
					triggerRepaint();
			} else if (e.getSource() == dbRight) {
				final int sze = vLabels.size();
				if (++firstVisibleTab == sze)
					firstVisibleTab--;
				else
					triggerRepaint();
			}
		}
	}

	/**
	 * This is the Mouse Event handling innerclass.
	 */
	class Mouse extends java.awt.event.MouseAdapter implements java.io.Serializable {
		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void mousePressed(final MouseEvent e) {
			final int sizeR = vPolys.size();
			Polygon p;
			for (int x = 0; x < sizeR; x++) {
				try {
					p = vPolys.elementAt(x);
					if ((p != nullPoly) && p.contains(e.getX(), e.getY())) {
						try {
							setCurrentTab(x);
						} catch (final PropertyVetoException exc) {
						}
					}
				} catch (final ArrayIndexOutOfBoundsException exc) {
				}
			}
		}
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Position constant indicating tabs are to be put at the top of this panel.
	 */
	public static final int TOP = 0;

	/**
	 * Position constant indicating tabs are to be put at the bottom of this panel.
	 */
	public static final int BOTTOM = 1;

	/**
	 * Style constant indicating tabs are to have rounded corners.
	 */
	public static final int ROUNDED = 0;

	/**
	 * Style constant indicating tabs are to have square corners.
	 */
	public static final int SQUARE = 1;

	/**
	 * Zero-relative index of currently shown tab. -1 if no tabs exist or are shown.
	 */
	protected int curIndex = -1;

	/**
	 * A flag indicating repaints should be suppress during the setLabel() and
	 * setEnabled() methods.
	 */
	transient protected boolean suppressRepaints = false;

	/**
	 * A Vector of Polygons, one for each tab.
	 */
	protected Vector<Polygon> vPolys;

	/**
	 * The zero-relative index of the first visible tab.
	 */
	protected int firstVisibleTab = 0;

	/**
	 * A left-pointing button shown when there are too many tabs to display.
	 */
	protected DirectionButton dbLeft;

	/**
	 * A right-pointing button shown when there are too many tabs to display.
	 */
	protected DirectionButton dbRight;

	/**
	 * An empty polygon.
	 */
	protected Polygon nullPoly;

	/**
	 * A Vector of tab label Strings.
	 */
	protected Vector<String> vLabels;

	/**
	 * Color used in drawing of the border.
	 */
	protected Color borderDarkerColor = null;

	/**
	 * Color used in drawing of the border.
	 */
	protected Color borderLightColor = null;

	/**
	 * Color used in drawing of the border.
	 */
	protected Color borderDarkColor = null;

	/**
	 * Color used in drawing of the disabled text.
	 */
	protected Color disabledTextColor = null;

	/**
	 * Cached value of the foreground color. Used to determine if calculated colors
	 * need to be updated.
	 */
	protected Color cachedForeground = null;

	/**
	 * Cached value of the background color. Used to determine if calculated colors
	 * need to be updated.
	 */
	protected Color cachedBackground = null;

	private final int TF_LEFT = 9;

	private final int TF_RIGHT = -9;

	private final int TF_TOP = 30;

	private final int TF_BOTTOM = -9;

	private int TF_BTN_HEIGHT = 20;

	private Vector<Boolean> vEnabled;

	private final Font fReg;

	private final Font fSel;

	private Component userPanel = null;

	private int iTabsPosition = TOP;

	private int iTabsStyle = ROUNDED;

	transient private int osAdjustment;

	private int lastWidth = -1;

	private Insets btpInsets;

	private Mouse mouse = null;
	private Action action = null;
	private final VetoableChangeSupport vetos = new VetoableChangeSupport(this);

	private final PropertyChangeSupport changes = new PropertyChangeSupport(this);

	/**
	 * Constructs a BaseTabbedPanel with tabs on top and rounded.
	 */
	public BaseTabbedPanel() {
		this(TOP, ROUNDED);
	}

	/**
	 * @deprecated
	 * @see #BaseTabbedPanel(int, int)
	 */
	@Deprecated
	public BaseTabbedPanel(final boolean bTabsOnTop) {
		this(bTabsOnTop ? TOP : BOTTOM, bTabsOnTop ? ROUNDED : SQUARE);
	}

	/**
	 * Constructs a BaseTabbedPanel with the desired tab position and corner style.
	 *
	 * @param tabsPostion a constant indicating TOP or BOTTOM tab location
	 * @param tabsStyle   a constant indicating ROUNDED or SQUARE tabs
	 */
	public BaseTabbedPanel(final int tabsPostion, final int tabsStyle) {
		vLabels = new Vector<String>();
		vEnabled = new Vector<Boolean>();
		vPolys = new Vector<Polygon>();
		btpInsets = new Insets(0, 0, 0, 0);

		setTabsInfo(tabsPostion, tabsStyle);

		fReg = new Font("Helvetica", Font.PLAIN, 10); // IM: was 12
		fSel = new Font("Helvetica", Font.BOLD, 10);

		if (System.getProperty("os.name").startsWith("S")) // SunOS, Solaris
			osAdjustment = -1;
		else
			osAdjustment = 0;

		super.setLayout(null);

		// prepare left/right arrows
		dbLeft = new DirectionButton(DirectionButton.LEFT);
		dbRight = new DirectionButton(DirectionButton.RIGHT);
		try {
			dbLeft.setShowFocus(false);
			dbRight.setShowFocus(false);
		} catch (final PropertyVetoException e) {
		}

		dbLeft.shrinkTriangle(1, 1, 0, 1);
		dbRight.shrinkTriangle(1, 1, 0, 1);
		super.add(dbLeft, -1);
		super.add(dbRight, -1);

		nullPoly = new Polygon();
		nullPoly.addPoint(0, 0);
		nullPoly.addPoint(1, 1);
		nullPoly.addPoint(0, 0);

		// Initilize the cached colors.
		cachedForeground = getForeground();
		cachedBackground = getBackground();
	}

	// ===========================================================
	// Component functions overridden so user cannot change the
	// way this container should work
	// ===========================================================
	/**
	 * Takes no action, use addTab(). This is a standard Java AWT method which gets
	 * called to add a component to a container. It is overridden here to do
	 * nothing, so the user cannot change the way this container works. Use
	 * setPanel, showPanel, addTab, and setTab instead.
	 *
	 * @param comp the component to add (IGNORED)
	 * @return the component parameter
	 * @see #setPanel
	 * @see #showPanel
	 * @see #addTab
	 * @see #setTab
	 * @see #remove
	 */
	@Override
	public Component add(final Component comp) {
		return comp;
	}

	/**
	 * Takes no action, use addTab(). This is a standard Java AWT method which gets
	 * called to add a component to a container. It is overridden here to do
	 * nothing, so the user cannot change the way this container works. Use
	 * setPanel, showPanel, addTab, and setTab instead.
	 *
	 * @param comp the component to add (IGNORED)
	 * @param pos  the zero-relative index at which to add the component or -1 for
	 *             end (IGNORED)
	 * @return the component parameter
	 * @see #setPanel
	 * @see #showPanel
	 * @see #addTab
	 * @see #setTab
	 * @see #remove
	 */
	@Override
	public synchronized Component add(final Component comp, final int pos) {
		return comp;
	}

	/**
	 * Takes no action, use addTab(). This is a standard Java AWT method which gets
	 * called to add a component to a container. It is overridden here to do
	 * nothing, so the user cannot change the way this container works. Use
	 * setPanel, showPanel, addTab, and setTab instead.
	 *
	 * @param name the positioning directive for the layout manager (IGNORED)
	 * @param comp the component to add (IGNORED)
	 * @return the component parameter
	 * @see #setPanel
	 * @see #showPanel
	 * @see #addTab
	 * @see #setTab
	 * @see #remove
	 */
	@Override
	public synchronized Component add(final String name, final Component comp) {
		return comp;
	}

	/**
	 * Adds a listener for the CurrentTab property changes.
	 *
	 * @param listener the listener to add.
	 * @see #removePropertyChangeListener
	 */
	public void addCurrentTabListener(final PropertyChangeListener listener) {
		changes.addPropertyChangeListener("CurrentTab", listener);
	}

	// ===========================================================
	// Done Component functions overridden
	// ===========================================================

	/**
	 * Adds a vetoable listener for the CurrentTab property changes.
	 *
	 * @param listener the listener to add.
	 * @see #removeVetoableChangeListener
	 */
	public void addCurrentTabListener(final VetoableChangeListener listener) {
		vetos.addVetoableChangeListener("CurrentTab", listener);
	}

	/**
	 * Tells this component that it has been added to a container. This is a
	 * standard Java AWT method which gets called by the AWT when this component is
	 * added to a container. Typically, it is used to create this component's peer.
	 *
	 * It has been overridden here to hook-up event listeners.
	 *
	 * @see #removeNotify
	 */
	@Override
	public synchronized void addNotify() {
		super.addNotify();

		// Hook up listeners
		if (mouse == null) {
			mouse = new Mouse();
			addMouseListener(mouse);
		}
		if (action == null) {
			action = new Action();
			dbLeft.addActionListener(action);
			dbRight.addActionListener(action);
		}
	}

	/**
	 * Adds a listener for all property changes.
	 *
	 * @param listener the listener to add.
	 * @see #removePropertyChangeListener
	 */
	@Override
	public void addPropertyChangeListener(final PropertyChangeListener listener) {
		changes.addPropertyChangeListener(listener);
	}

	/**
	 * Appends a new tab and sets whether it is enabled.
	 *
	 * @param sLabel   the tab label
	 * @param bEnabled enable the tab or not
	 * @return the zero-relative index of the newly added tab
	 * @see #setTab
	 * @see #setLabel
	 */
	public int addTab(final String sLabel, final boolean bEnabled) {
		return addTab(sLabel, bEnabled, -1);
	}

	/**
	 * Adds a new tab at the specified position and sets whether it is enabled.
	 *
	 * @param sLabel   the tab label
	 * @param bEnabled enable the tab or not
	 * @param pos      the zero-relative index for the new tab
	 * @return the zero-relative index of the newly added tab
	 * @see #setTab
	 * @see #setLabel
	 */
	public int addTab(final String sLabel, final boolean bEnabled, final int pos) {
		int index;

		if (pos == -1) {
			vLabels.addElement(sLabel);
			vEnabled.addElement(new Boolean(bEnabled));
			index = vLabels.size() - 1;
		} else {
			vLabels.insertElementAt(sLabel, pos);
			vEnabled.insertElementAt(new Boolean(bEnabled), pos);
			index = pos;
		}
		if (curIndex == -1 && bEnabled) {
			try {
				setCurrentTab(index);
			} catch (final PropertyVetoException e) {
				// Return an error state
				index = -1;
			}
		}

		triggerRepaint();

		return (index);
	}

	/**
	 * Adds a vetoable listener for all property changes.
	 *
	 * @param listener the listener to add.
	 * @see #removeVetoableChangeListener
	 */
	public void addVetoableChangeListener(final VetoableChangeListener listener) {
		vetos.addVetoableChangeListener(listener);
	}

	/**
	 * Used to calculate the border colors from the background color.
	 *
	 * @see #paint
	 */
	protected void calculateBorderColors(final Color c) {
		borderLightColor = ColorUtils.calculateHilightColor(c);
		borderDarkColor = ColorUtils.calculateShadowColor(c);
		borderDarkerColor = ColorUtils.darken(borderDarkColor, 0.200);
	}

	/**
	 * Used to calculate the disabled text color from the foreground color.
	 *
	 * @see #paint
	 */
	protected void calculateDisabledTextColor(final Color c) {
		try {
			disabledTextColor = ColorUtils.fade(c, Color.lightGray, 0.50);
		} catch (final IllegalArgumentException exc) {
		}
	}

	/**
	 * @deprecated
	 * @see #getCurrentTab
	 */
	@Deprecated
	public int currentTabIndex() {
		return getCurrentTab();
	}

	/**
	 * Handles the laying out of components within this component. This is a
	 * standard Java AWT method which gets called by the AWT when this component is
	 * validated with the validate() method.
	 *
	 * @see java.awt.Container#validate
	 */
	@Override
	public void doLayout() {
		final Rectangle r = this.getBounds();

		final int width = r.width - TF_LEFT + TF_RIGHT;
		if (width < 0)
			return;

		final int height = r.height - TF_TOP + TF_BOTTOM;
		if (height < 0)
			return;

		final int col = TF_LEFT;
		int row = 0;

		if (iTabsPosition == TOP)
			row = TF_TOP;
		else
			row = TF_TOP - TF_BTN_HEIGHT;

		if (userPanel != null) {
			userPanel.setBounds(col + 3, row + 3, width - 6, height - 5);
			userPanel.invalidate();
			userPanel.validate();
			if (userPanel instanceof Canvas || userPanel instanceof Panel) {
				userPanel.repaint();
			} else {
				triggerRepaint();
			}

		}
	}

	/**
	 * @deprecated
	 * @see #setEnabled(boolean, int)
	 * @exception PropertyVetoException if the specified property value is
	 *                                  unacceptable
	 */
	@Deprecated
	public void enableTab(final boolean bEnabled, final int index) throws PropertyVetoException {
		setEnabled(bEnabled, index);
	}

	/**
	 * Determine the currently shown tab.
	 *
	 * @return zero-relative index of the currently shown tab
	 * @see #setCurrentTab(int)
	 */
	public int getCurrentTab() {
		return curIndex;
	}

	/**
	 * Returns the amount of space used by the current border. This is a standard
	 * Java AWT method which gets called to determine the size of the current
	 * border. The returned value is the width of each border side in pixels.
	 *
	 * @return the current border insets
	 */
	@Override
	public Insets getInsets() {
		btpInsets = super.getInsets();
		btpInsets.left += (TF_LEFT + 3);
		btpInsets.right += (6 - TF_RIGHT);

		if (iTabsPosition == TOP) {
			btpInsets.top += (TF_TOP + 3);
			btpInsets.bottom += (5 - TF_BOTTOM);
		} else {
			btpInsets.top += TF_TOP - TF_BTN_HEIGHT + 3;
			btpInsets.bottom += (TF_BTN_HEIGHT + 5 - TF_BOTTOM);
		}

		return btpInsets;
	}

	/**
	 * Gets the label of the tab at the specified index.
	 *
	 * @param index the zero-relative index of the tab
	 * @return the tab label
	 * @see #setLabel
	 */
	public synchronized String getLabel(final int index) {
		if ((index < 0) || (index >= vLabels.size()))
			return "";

		try {
			return vLabels.elementAt(index);
		} catch (final ArrayIndexOutOfBoundsException e) {
		}
		return "";
	}

	/**
	 * Returns the minimum dimensions to properly display this component. This is a
	 * standard Java AWT method which gets called to determine the minimum size of
	 * this component.
	 *
	 * @see #getPreferredSize
	 */
	@Override
	public Dimension getMinimumSize() {
		if (userPanel != null) {
			final Dimension s = userPanel.getMinimumSize();
			return new Dimension((s.width + btpInsets.left + btpInsets.right),
					(s.height + btpInsets.top + btpInsets.bottom));
		}
		return new Dimension(100, 100);
	}

	/**
	 * Returns the recommended dimensions to properly display this component. This
	 * is a standard Java AWT method which gets called to determine the recommended
	 * size of this component.
	 *
	 * @see #getMinimumSize
	 */
	@Override
	public Dimension getPreferredSize() {
		final Dimension s = getSize();// size();
		final Dimension m = getMinimumSize();
		return new Dimension(Math.max(s.width, m.width), Math.max(s.height, m.height));
	}

	/**
	 * Gets the current tabs position, TOP or BOTTOM.
	 *
	 * @return the position constant TOP or BOTTOM, indicating the current tabs
	 *         position
	 * @see setTabsPosition
	 * @see #TOP
	 * @see #BOTTOM
	 */
	public int getTabsPosition() {
		return iTabsPosition;
	}

	/**
	 * Gets the current tab style, ROUNDED or SQUARE.
	 *
	 * @return the style constant ROUNDED or SQUARE, indicating the current tab
	 *         style
	 * @see setTabsStyle
	 * @see #ROUNDED
	 * @see #SQUARE
	 */
	public int getTabsStyle() {
		return iTabsStyle;
	}

	/**
	 * Inserts a tab before the tab at the specified index.
	 *
	 * @param sLabel   label of the new tab to insert
	 * @param bEnabled enable the tab or not
	 * @param index    the zero-relative index at which the tab will be inserted.
	 */
	public synchronized void insertTab(final String sLabel, final boolean bEnabled, final int index) {
		if ((index < 0) || (index >= vLabels.size()))
			return;

		if (index == curIndex && !bEnabled)
			return;

		try {
			vLabels.insertElementAt(sLabel, index);
			vEnabled.insertElementAt(new Boolean(bEnabled), index);
			triggerRepaint();
		} catch (final ArrayIndexOutOfBoundsException e) {
		}
	}

	/**
	 * Determines whether or not the tab at the index is enabled.
	 *
	 * @param index the zero-relative index of the tab
	 * @return true if the tab at the index is enabled
	 */
	public boolean isEnabled(final int index) {
		if ((index < 0) || (index >= vLabels.size()))
			return false;

		try {
			final Boolean bool = vEnabled.elementAt(index);
			if (bool.booleanValue())
				return true;
		} catch (final ArrayIndexOutOfBoundsException e) {
		}

		return false;
	}

	/**
	 * Paints this component using the given graphics context. This is a standard
	 * Java AWT method which typically gets called by the AWT to handle painting
	 * this component. It paints this component using the given graphics context.
	 * The graphics context clipping region is set to the bounding rectangle of this
	 * component and its [0,0] coordinate is this component's top-left corner.
	 *
	 * @param g the graphics context used for painting
	 * @see java.awt.Component#repaint
	 * @see java.awt.Component#update
	 */
	// THIRD PARTY CODE - IGNORE CYCLOMATIC COMPLEXITY
	// CS-IGNORE:ON CYCLOMATIC_COMPLEXITY

	@Override
	public synchronized void paint(final Graphics g) {

		final Rectangle r = this.getBounds();
		// Make sure cached colors are correct.
		final Color curForeground = getForeground();
		final Color curBackground = getBackground();
		if (!GeneralUtils.objectsEqual(curForeground, cachedForeground)) {
			cachedForeground = curForeground;
			calculateDisabledTextColor(curForeground);
		}
		if (!GeneralUtils.objectsEqual(curBackground, cachedBackground)) {
			cachedBackground = curBackground;
			calculateBorderColors(curBackground);
		}

		// --------------------------------------------------------------------------------
		// paint the box
		// --------------------------------------------------------------------------------

		final int width = r.width - TF_LEFT + TF_RIGHT;
		if (width < 0)
			return;

		final int height = r.height - TF_TOP + TF_BOTTOM;
		if (height < 0)
			return;

		if (r.width > lastWidth)
			firstVisibleTab = 0;
		lastWidth = r.width;

		final int col = TF_LEFT;
		int row;

		final Color c = g.getColor();
		g.setColor(curBackground);
		g.fillRect(0, 0, r.width, r.height);

		if (iTabsPosition == TOP)
			row = TF_TOP;
		else
			row = TF_TOP - TF_BTN_HEIGHT;

		// --------------------------------------------------------------------------------
		// draw border
		// --------------------------------------------------------------------------------
		g.setColor(borderLightColor);
		g.drawLine(col, row, (col + width - 1), row);
		g.drawLine(col, row, col, (row + height - 1));

		g.setColor(borderDarkColor);
		g.drawLine((col + 2), (row + height - 2), (col + width - 2), (row + height - 2));
		g.drawLine((col + width - 2), (row + 2), (col + width - 2), (row + height - 2));

		g.setColor(borderDarkerColor);
		g.drawLine((col + 1), (row + height - 1), (col + width - 1), (row + height - 1));
		g.drawLine((col + width - 1), (row + 1), (col + width - 1), (row + height - 1));

		// --------------------------------------------------------------------------------
		// paint the tabs, and record areas
		// --------------------------------------------------------------------------------
		int x1;
		int x2 = TF_LEFT + 8;
		int y1;
		int y2;
		int x3 = 0;
		int x4 = TF_LEFT;

		final int sze = vLabels.size();
		String sLabel;
		vPolys.removeAllElements();

		final Font f = g.getFont();
//    Font f= new Font("Sans Serif", 6, Font.PLAIN);
		final FontMetrics fm = getFontMetrics(fReg);
		final FontMetrics fms = getFontMetrics(fSel);
		int labelWidth = 0;
		Polygon p;

		int w;
		// make sure there is a polygon for each tab
		for (w = 0; w < firstVisibleTab; w++) {
			vPolys.addElement(nullPoly);
		}
		if (w > 0)
			x4 += 2;
		for (; w < sze; w++) {
			p = new Polygon();
			try {
				sLabel = vLabels.elementAt(w);
				if (w == curIndex)
					labelWidth = fms.stringWidth(sLabel);
				else
					labelWidth = fm.stringWidth(sLabel);

				if (iTabsPosition == TOP) {
					y1 = TF_TOP - TF_BTN_HEIGHT;
					y2 = TF_TOP - 1;
				} else {
					y1 = r.height + TF_BOTTOM + 1;
					y2 = r.height + TF_BOTTOM - TF_BTN_HEIGHT;
				}

				if (iTabsStyle == ROUNDED) {
					x1 = x4 + 2;
					x2 = x1 + labelWidth + 13;
				} else {
					x1 = x2 - 7;
					x2 = x1 + labelWidth + 28;
				}

				// check to see if this tab would draw too far
				if ((x2 + 36 - TF_RIGHT) > r.width)
					break;

				// draw the outside edge of the tab
				if (iTabsPosition == TOP) {
					// if current tab, it extends further
					if (w == curIndex) {
						y1 -= 3;
						x1 -= 2;
					}
					g.setColor(borderLightColor);
					if (curIndex == (w + 1))
						g.drawLine(x1 + 2, y1, x2 - 2, y1);
					else
						g.drawLine(x1 + 2, y1, x2, y1);

					// draw the border between tabs if not covered by the current one
					if (curIndex != (w - 1)) {
						g.drawLine(x1, y1 + 2, x1, y2);
						x3 = x1;
					} else
						x3 = x1 + 1;

					g.drawLine(x1 + 1, y1 + 1, x1 + 1, y1 + 1);

					if (curIndex != (w + 1)) {
						g.setColor(borderDarkColor);
						g.drawLine(x2, y1, x2, y2);
						g.setColor(borderDarkerColor);
						g.drawLine(x2 + 1, y1 + 2, x2 + 1, y2);
						x4 = x2;
					} else
						x4 = x2 - 1;
				} else {
					if (iTabsStyle == SQUARE) {
						g.setColor(borderDarkColor);
						g.drawLine(x1 + 9, y1, x2 - 9, y1);

						g.setColor(borderDarkerColor);
						// left \ slanted line
						if (w == 0 || w == curIndex) {
							g.drawLine(x1, y2, x1 + 9, y1);
							p.addPoint(x1, y2);
						} else {
							g.drawLine(x1 + 4, y1 - 9, x1 + 9, y1);
							p.addPoint(x1 + 9, y2);
							p.addPoint(x1 + 4, y1 - 9);
						}
						p.addPoint(x1 + 9, y1);
						p.addPoint(x2 - 9, y1);

						if ((w + 1) == curIndex) {
							g.drawLine(x2 - 5, y1 - 9, x2 - 9, y1);
							p.addPoint(x2 - 5, y1);
							p.addPoint(x2 - 9, y2);
						} else {
							g.drawLine(x2, y2, x2 - 9, y1);
							p.addPoint(x2, y2);
						}

						if (w == 1 || w == curIndex)
							p.addPoint(x1, y2);
						else
							p.addPoint(x1 + 9, y2);
					} else {
						// if current tab, it extends further
						if (w == curIndex) {
							y1 += 3;
							x1 -= 2;
						}
						g.setColor(borderLightColor);
						if (curIndex == (w + 1))
							g.drawLine(x1 + 2, y1, x2 - 2, y1);
						else
							g.drawLine(x1 + 2, y1, x2, y1);

						// draw the border between tabs if not covered by the current one
						if (curIndex != (w - 1)) {
							g.drawLine(x1, y1 - 2, x1, y2);
							x3 = x1;
						} else
							x3 = x1 + 1;

						g.drawLine(x1 + 1, y1 - 1, x1 + 1, y1 - 1);

						if (curIndex != (w + 1)) {
							g.setColor(borderDarkColor);
							g.drawLine(x2, y1, x2, y2);
							g.setColor(borderDarkerColor);
							g.drawLine(x2 + 1, y1 - 2, x2 + 1, y2);
							x4 = x2;
						} else
							x4 = x2 - 1;
					}
				}

				// draw the inside edge of the tab
				if (w == curIndex) {
					if (iTabsPosition == TOP)
						y2++;
					else
						y2--;
					g.setColor(curBackground);
					g.drawLine(x1 + 1, y2, x2, y2);
					if (iTabsPosition == BOTTOM)
						g.drawLine(x1 + 1, y2 - 1, x2, y2 - 1);

					g.setFont(fSel);
				} else
					g.setFont(fReg);

				// if (iTabsPosition == TOP)
				if (iTabsStyle == ROUNDED) {
					p.addPoint(x3, y2);
					p.addPoint(x4, y2);
					p.addPoint(x4, y1);
					p.addPoint(x3, y1);
					p.addPoint(x3, y2);
				}
				vPolys.addElement(p);

				final Boolean bool = vEnabled.elementAt(w);
				if (bool.booleanValue())
					g.setColor(curForeground);
				else
					g.setColor(disabledTextColor);

				if (iTabsPosition == TOP)
					g.drawString(sLabel, x1 + 8, y1 + 15 + osAdjustment);
				else {
					if (iTabsStyle == ROUNDED)
						g.drawString(sLabel, x1 + 8, y1 - 6 + osAdjustment);
					else
						g.drawString(sLabel, x1 + 14, y1 - 4 + osAdjustment);
				}
			} catch (final ArrayIndexOutOfBoundsException e) {
			}
		}

		// do I need to show arrows because there are too many tabs???
		if ((firstVisibleTab > 0) || (w < sze)) {
			dbLeft.setVisible(true);
			dbRight.setVisible(true);
			if (firstVisibleTab > 0)
				dbLeft.setEnabled(true);
			else
				dbLeft.setEnabled(false);

			if (w < sze)
				dbRight.setEnabled(true);
			else
				dbRight.setEnabled(false);

			if (iTabsPosition == TOP) {
				dbLeft.setBounds(r.width - 33 + TF_RIGHT, TF_TOP - 16, 16, 15);
				dbRight.setBounds(r.width - 16 + TF_RIGHT, TF_TOP - 16, 16, 15);
			} else {
				dbLeft.setBounds(r.width - 33 + TF_RIGHT, r.height + TF_BOTTOM - TF_BTN_HEIGHT, 16, 15);
				dbRight.setBounds(r.width - 16 + TF_RIGHT, r.height + TF_BOTTOM - TF_BTN_HEIGHT, 16, 15);
			}
		} else {
			dbLeft.setVisible(false);
			dbRight.setVisible(false);
		}

		// make sure there is a polygon for each tab
		for (; w < sze; w++) {
			vPolys.addElement(nullPoly);
		}

		g.setFont(f);
		g.setColor(c);

// THIRD PARTY CODE - IGNORE CYCLOMATIC COMPLEXITY
// CS-IGNORE:OFF CYCLOMATIC_COMPLEXITY

	}

	/**
	 * Removes the specified component from this container. This is a standard Java
	 * AWT method which gets called to remove a component from a container. When
	 * this happens the component's removeNotify() will also get called to indicate
	 * component removal.
	 *
	 * @param comp the component to remove
	 * @see #removeAll
	 * @see #addTab
	 */
	@Override
	public synchronized void remove(final Component comp) {
		if (comp == dbLeft || comp == dbRight)
			return;
		super.remove(comp);
		if (comp == userPanel)
			userPanel = null;
	}

	/**
	 * Removes all the components from this container. This is a standard Java AWT
	 * method which gets called to remove all the components from a container. When
	 * this happens each component's removeNotify() will also get called to indicate
	 * component removal.
	 *
	 * @see #remove
	 * @see #addTab
	 */
	@Override
	public synchronized void removeAll() {
		super.removeAll();
		super.add(dbLeft, -1);
		super.add(dbRight, -1);
		userPanel = null;
	}

	/**
	 * Removes all tabs.
	 */
	public void removeAllTabs() {
		vLabels = new Vector<String>();
		vEnabled = new Vector<Boolean>();
		vPolys = new Vector<Polygon>();
		curIndex = -1;
		firstVisibleTab = 0;
		lastWidth = -1;
		removeAll();
		triggerRepaint();
	}

	/**
	 * Removes a listener for the CurrentTab property changes.
	 *
	 * @param listener the listener to remove.
	 * @see #addPropertyChangeListener
	 */
	public void removeCurrentTabListener(final PropertyChangeListener listener) {
		changes.removePropertyChangeListener("CurrentTab", listener);
	}

	/**
	 * Removes a vetoable listener for the CurrentTab property changes.
	 *
	 * @param listener the listener to remove.
	 * @see #addVetoableChangeListener
	 */
	public void removeCurrentTabListener(final VetoableChangeListener listener) {
		vetos.removeVetoableChangeListener("CurrentTab", listener);
	}

	/**
	 * Tells this component that it is being removed from a container. This is a
	 * standard Java AWT method which gets called by the AWT when this component is
	 * removed from a container. Typically, it is used to destroy the peers of this
	 * component and all its subcomponents.
	 *
	 * It has been overridden here to unhook event listeners.
	 *
	 * @see #addNotify
	 */
	@Override
	public synchronized void removeNotify() {
		// Unhook listeners
		if (mouse != null) {
			removeMouseListener(mouse);
			mouse = null;
		}
		if (action != null) {
			dbLeft.removeActionListener(action);
			dbRight.removeActionListener(action);
			action = null;
		}

		super.removeNotify();
	}

	/**
	 * Removes a listener for all property changes.
	 *
	 * @param listener the listener to remove.
	 * @see #addPropertyChangeListener
	 */
	@Override
	public void removePropertyChangeListener(final PropertyChangeListener listener) {
		changes.removePropertyChangeListener(listener);
	}

	/**
	 * Removes the tab at the specified index. The currently shown tab cannot be
	 * removed.
	 *
	 * @param index the zero-relative index of the tab to remove
	 */
	public void removeTab(final int index) {
		if ((index < 0) || (index >= vEnabled.size()) || index == curIndex)
			return;

		try {
			vLabels.removeElementAt(index);
			vEnabled.removeElementAt(index);
			triggerRepaint();
		} catch (final ArrayIndexOutOfBoundsException e) {
		}
	}

	/**
	 * Removes a vetoable listener for all property changes.
	 *
	 * @param listener the listener to remove.
	 * @see #addVetoableChangeListener
	 */
	public void removeVetoableChangeListener(final VetoableChangeListener listener) {
		vetos.removeVetoableChangeListener(listener);
	}

	/**
	 * Shows the tab at the specified index. The tab must be enabled for it to be
	 * shown.
	 *
	 * @param index the zero-relative index of the tab to show
	 * @exception PropertyVetoException if the specified property value is
	 *                                  unacceptable
	 * @see #getCurrentTab
	 */
	public void setCurrentTab(final int index) throws PropertyVetoException {
		if ((index < 0) || (index >= vLabels.size()) || index == curIndex)
			return;

		if (isEnabled(index)) {
			final boolean wasSuppressingRepaints = setSuppressRepaints(true);

			try {
				final Integer oldValue = new Integer(curIndex);
				final Integer newValue = new Integer(index);

				vetos.fireVetoableChange("CurrentTab", oldValue, newValue);

				curIndex = index;
				invalidate();
				validate();

				changes.firePropertyChange("CurrentTab", oldValue, newValue);
			} finally {
				setSuppressRepaints(wasSuppressingRepaints);
			}

			triggerRepaint();
		}
	}

	/**
	 * Conditionally enables the tab at the specified index. The currently active
	 * tab cannot be disabled. This performs the same action as enableTab().
	 *
	 * @param bEnable true to enable, false to disable
	 * @param index   the zero-relative index of the tab
	 * @exception PropertyVetoException if the specified property value is
	 *                                  unacceptable
	 */
	public synchronized void setEnabled(final boolean bEnabled, final int index) throws PropertyVetoException {
		if ((index < 0) || (index >= vLabels.size()))
			return;

		if (index == curIndex && !bEnabled)
			return;

		try {
			final Boolean oldValue = vEnabled.elementAt(index);
			final Boolean newValue = new Boolean(bEnabled);

			vetos.fireVetoableChange("Enabled", oldValue, newValue);

			vEnabled.setElementAt(newValue, index);
			triggerRepaint();

			changes.firePropertyChange("Enabled", oldValue, newValue);
		} catch (final ArrayIndexOutOfBoundsException e) {
		}
	}

	/**
	 * Changes the label of the tab at the specified index.
	 *
	 * @param sLabel the new label for the specified tab
	 * @param index  the zero-relative index of the tab
	 * @exception PropertyVetoException if the specified property value is
	 *                                  unacceptable
	 * @see #getLabel
	 * @see #addTab
	 * @see #setTab
	 */
	public synchronized void setLabel(final String sLabel, final int index) throws PropertyVetoException {
		if ((index < 0) || (index >= vLabels.size()))
			return;

		try {
			final String oldValue = vLabels.elementAt(index);
			vetos.fireVetoableChange("Label", oldValue, sLabel);

			vLabels.setElementAt(sLabel, index);
			triggerRepaint();

			changes.firePropertyChange("Label", oldValue, sLabel);
		} catch (final ArrayIndexOutOfBoundsException e) {
		}
	}

	/**
	 * Takes no action. This is a standard Java AWT method which gets called to
	 * specify which layout manager should be used to layout the components in
	 * standard containers.
	 *
	 * Since layout managers CANNOT BE USED with this container the standard
	 * setLayout has been OVERRIDDEN for this container and does nothing.
	 *
	 * @param mgr the layout manager to use to layout this container's components
	 *            (IGNORED)
	 * @see java.awt.Container#getLayout
	 **/
	@Override
	public void setLayout(final LayoutManager mgr) {
	}

	/**
	 * Adds the panel to the base panel and shows it. Removes all other previous
	 * panels from base panel.
	 *
	 * @param p the panel to add and show
	 * @exception PropertyVetoException if the specified property value is
	 *                                  unacceptable
	 */
	public void setPanel(final Component p) throws PropertyVetoException {
		final Component oldValue = userPanel;

		vetos.fireVetoableChange("Panel", oldValue, p);

		removeAll();
		userPanel = p;
		if (userPanel != null) {
			super.add(userPanel, -1);
			userPanel.requestFocus();
		}

		changes.firePropertyChange("Panel", oldValue, p);
	}

	public boolean setSuppressRepaints(final boolean b) {
		final boolean wasSuppressingRepaints = suppressRepaints;
		suppressRepaints = b;
		return wasSuppressingRepaints;
	}

	/**
	 * Labels and conditionally enables the tab at the specified index.
	 *
	 * @param sLabel   the tab label
	 * @param bEnabled enable the tab or not
	 * @param index    the zero-relative index of the tab
	 * @exception PropertyVetoException if the specified property value is
	 *                                  unacceptable
	 * @see #addTab
	 * @see #setLabel
	 */
	public synchronized void setTab(final String sLabel, final boolean bEnabled, final int index)
			throws PropertyVetoException {
		final boolean wasSuppressingRepaints = setSuppressRepaints(true);

		try {
			setLabel(sLabel, index);
			setEnabled(bEnabled, index);
		} finally {
			setSuppressRepaints(wasSuppressingRepaints);
		}

		triggerRepaint();
	}

	/**
	 * Sets the position and style of all the tabs. Note that if the tabs are on top
	 * they are always rounded.
	 *
	 * @param tabsPosition a constant indicating TOP or BOTTOM
	 * @param tabsStyle    a constant indicating ROUNDED or SQUARE
	 * @see getTabsPosition
	 * @see getTabsStyle
	 * @see #TOP
	 * @see #BOTTOM
	 * @see #ROUNDED
	 * @see #SQUARE
	 */
	public void setTabsInfo(final int tabsPosition, final int tabsStyle) {
		iTabsPosition = tabsPosition;
		if (iTabsPosition == TOP)
			iTabsStyle = ROUNDED;
		else
			iTabsStyle = tabsStyle;

		if (iTabsStyle == ROUNDED)
			TF_BTN_HEIGHT = 20;
		else
			TF_BTN_HEIGHT = 17;

		triggerRepaint();
	}

	/**
	 * Sets the position of the all tabs to the top or bottom of this panel. Note
	 * that if the tabs are on top they are always rounded.
	 *
	 * @param tabsPosition constant indicating TOP or BOTTOM
	 * @exception PropertyVetoException if the specified property value is
	 *                                  unacceptable
	 * @see getTabsPosition
	 * @see #TOP
	 * @see #BOTTOM
	 */
	public void setTabsPosition(final int tabsPosition) throws PropertyVetoException {
		if (iTabsPosition != tabsPosition) {
			final Integer oldValue = new Integer(iTabsPosition);
			final Integer newValue = new Integer(tabsPosition);

			vetos.fireVetoableChange("TabsPosition", oldValue, newValue);

			setTabsInfo(tabsPosition, iTabsStyle);

			changes.firePropertyChange("TabsPosition", oldValue, newValue);
		}
	}

	/**
	 * Sets the style of the tabs to ROUNDED or SQUARE. Note that if the tabs are on
	 * top they are always rounded.
	 *
	 * @param tabsStyle a constant indicating ROUNDED or SQUARE
	 * @exception PropertyVetoException if the specified property value is
	 *                                  unacceptable
	 * @see getTabsStyle
	 * @see #ROUNDED
	 * @see #SQUARE
	 */
	public void setTabsStyle(final int tabsStyle) throws PropertyVetoException {
		if (iTabsStyle != tabsStyle) {
			final Integer oldValue = new Integer(iTabsStyle);
			final Integer newValue = new Integer(tabsStyle);

			vetos.fireVetoableChange("TabsStyle", oldValue, newValue);

			setTabsInfo(iTabsPosition, tabsStyle);

			changes.firePropertyChange("TabsStyle", oldValue, newValue);
		}
	}

	/**
	 * Adds the panel to the base panel and shows it. Hides all other (previous)
	 * panels instead of removing them.
	 *
	 * @param p the Panel to add and show
	 */
	public void showPanel(final Component p) {
		if (userPanel != null)
			userPanel.setVisible(false);

		userPanel = p;
		if (userPanel != null) {
			final Component[] comps = getComponents();
			final int l = comps.length;
			int x;
			for (x = 0; x < l; x++) {
				if (comps[x] == userPanel)
					break;
			}
			if (x == l)
				super.add(userPanel, -1);

			userPanel.setVisible(true);
			userPanel.requestFocus();
			validate();
			triggerRepaint();
		}
	}

	/**
	 * @deprecated
	 * @see #setCurrentTab(int)
	 * @exception PropertyVetoException if the specified property value is
	 *                                  unacceptable
	 */
	@Deprecated
	public void showTab(final int index) throws PropertyVetoException {
		setCurrentTab(index);
	}

	/**
	 * @deprecated
	 * @see #isEnabled(int)
	 */
	@Deprecated
	public boolean tabIsEnabled(final int index) {
		return (isEnabled(index));
	}

	protected void triggerRepaint() {
		if (!suppressRepaints)
			repaint();
	}

}
