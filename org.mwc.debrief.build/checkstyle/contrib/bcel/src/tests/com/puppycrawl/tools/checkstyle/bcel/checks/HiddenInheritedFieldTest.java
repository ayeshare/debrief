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
package com.puppycrawl.tools.checkstyle.bcel.checks;

import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.bcel.BcelCheckTestCase;

public class HiddenInheritedFieldTest
    extends BcelCheckTestCase
{
    public void testIt()
            throws Exception
    {
        final DefaultConfiguration checkConfig =
            createCheckConfig(HiddenInheritedFieldCheck.class);

        final String[] expected = {
            "0: Field 'private int subClassPrivate' hides field in class 'com.puppycrawl.tools.checkstyle.bcel.checks.SuperClass'.",
            "0: Field 'protected int differentType' hides field in class 'com.puppycrawl.tools.checkstyle.bcel.checks.SuperClass'.",
            "0: Field 'protected int reusedName' hides field in class 'com.puppycrawl.tools.checkstyle.bcel.checks.SuperClass'.",
        };
        verify(checkConfig, getPath("com\\puppycrawl\\tools\\checkstyle\\bcel\\checks\\SubClass.class"), expected);
    }
}
