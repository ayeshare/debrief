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
//Tested with BCEL-5.1
//http://jakarta.apache.org/builds/jakarta-bcel/release/v5.1/

package com.puppycrawl.tools.checkstyle.bcel;

/**
 * Deep visitor for classfile and generic traversals. A ClassFile traversal
 * visits all classfile and all generic nodes.
 * @author Rick Giles
 */
public interface IDeepVisitor
    extends IObjectSetVisitor
{
    /**
     * Returns the classfile visitor.
     * @return the classfile visitor.
     */
    org.apache.bcel.classfile.Visitor getClassFileVisitor();

    /**
     * Returns the generic visitor.
     * @return the generic visitor.
     */
    org.apache.bcel.generic.Visitor getGenericVisitor();
}
