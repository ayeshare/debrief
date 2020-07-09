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

// $RCSfile: SwingEditFrame.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: SwingEditFrame.java,v $
// Revision 1.2  2004/05/25 15:29:43  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:20  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:27  Ian.Mayo
// Initial import
//
// Revision 1.2  2002-05-28 09:25:46+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:35+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:01:27+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:43:32+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:42:39+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:45:37  ianmayo
// initial version
//
// Revision 1.2  1999-11-23 11:05:02+00  ian_mayo
// further introduction of SWING components
//

import java.awt.Button;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyEditor;

import javax.swing.JDialog;
import javax.swing.JFrame;

/////////////////////////////////////////////////////
// frame to pop up, and allow editing
///////////////////////////////////////////////////

public class SwingEditFrame extends JDialog implements ActionListener {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	protected PropertyEditor _pe;

	public SwingEditFrame(final JFrame parent, final PropertyEditor pe) {
		super(parent);
		_pe = pe;
		initForm();
		setBackground(SystemColor.control);
		final Dimension di = Toolkit.getDefaultToolkit().getScreenSize();
		final Rectangle dme = getBounds();
		setLocation((di.width - dme.width) / 2, (di.height - dme.height) / 2);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				dispose();
			}
		});

	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		dispose();
	}

	public void initForm() {
		final Component cp = _pe.getCustomEditor();
		this.getContentPane().add("Center", cp);
		final Button fin = new Button("Done");
		fin.addActionListener(this);
		this.getContentPane().add("South", fin);
		final Dimension sz = cp.getPreferredSize();
		setSize(sz.width + 50, sz.height + fin.getPreferredSize().height + 50);
	}
}
