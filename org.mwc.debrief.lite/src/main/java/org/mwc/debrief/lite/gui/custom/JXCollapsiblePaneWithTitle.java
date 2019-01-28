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
package org.mwc.debrief.lite.gui.custom;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.SwingConstants;

import org.jdesktop.swingx.JXLabel;

public class JXCollapsiblePaneWithTitle extends JXCollapsiblePane
{
  /**
   * 
   */
  private static final long serialVersionUID = -2723902372006197600L;

  private final JXLabel titleLabel;

  // Variables used to implement the resize behavior.
  private boolean dragging = false;
  private Point dragLocation = new Point();

  public JXCollapsiblePaneWithTitle(final Direction direction,
      final String title, final int defaultSize)
  {
    super(direction, 20, false);

    final JXCollapsiblePane collapsiblePaneInstance = this;
    collapsiblePaneInstance.setPreferredSize(new Dimension(defaultSize, defaultSize));

    titleLabel = new JXLabel(title, SwingConstants.CENTER);
    final Dimension titleDimension;
    
    if ( direction.isVertical() )
    {
      titleDimension = new Dimension(defaultSize, 20);
    }else
    {
      titleDimension = new Dimension(20, defaultSize);
    }
    titleLabel.setPreferredSize(titleDimension);
    setLayout(new BorderLayout());

    if (direction == Direction.LEFT)
    {
      add(titleLabel, BorderLayout.EAST);
      setMinimumSize(new Dimension(30, titleLabel.getHeight()));
    }
    else if (direction == Direction.RIGHT)
    {
      add(titleLabel, BorderLayout.WEST);
    }
    else if (direction == Direction.DOWN)
    {
      add(titleLabel, BorderLayout.NORTH);
    }
    else if (direction == Direction.UP)
    {
      add(titleLabel, BorderLayout.SOUTH);
    }

    if (!direction.isVertical())
    {
      titleLabel.setTextRotation(3 * Math.PI / 2);
    }

    titleLabel.setBackground(Color.black);

    titleLabel.addMouseListener(new MouseAdapter()
    {
      @Override
      public void mouseClicked(MouseEvent e)
      {
        if (e.getClickCount() == 2)
        {
          collapsiblePaneInstance.setCollapsed(!collapsiblePaneInstance
              .isCollapsed());
        }
      }

      @Override
      public void mousePressed(MouseEvent e)
      {
        dragging = true;
        dragLocation = e.getPoint();
      }

      @Override
      public void mouseReleased(MouseEvent e)
      {
        dragging = false;
      }
    });

    titleLabel.addMouseMotionListener(new MouseMotionListener()
    {

      @Override
      public void mouseMoved(MouseEvent event)
      {
        System.out.println("Mouse is moving over the title");
      }

      @Override
      public void mouseDragged(MouseEvent event)
      {
        if (dragging)
        {
          Rectangle bounds = collapsiblePaneInstance.getWrapper().getBounds();
          int newDimension = (int) (collapsiblePaneInstance.getContentPane()
              .getWidth() + event.getPoint().getX() - dragLocation.getX());
          
          collapsiblePaneInstance.getWrapper().setViewPosition(new Point(newDimension, 0));
          
          bounds.width = newDimension;
          collapsiblePaneInstance.getWrapper().setBounds(bounds);

          System.out.println(bounds);
          // collapsiblePaneInstance.doLayout();
          // collapsiblePaneInstance.validate();
          // collapsiblePaneInstance.repaint();
          collapsiblePaneInstance.getContentPane().validate();
          System.out.println("Tamano actual: = " + getContentPane().getSize());
        }
      }
    });
  }
}
