/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)

 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package Debrief.GUI;

import java.io.*;
import java.text.*;
import java.util.Date;

public class CreateVersion
{

  ///////////////////////////////////
  // member variables
  //////////////////////////////////
	private static String fName = "Debrief/GUI/VersionInfo.java";

	
  ///////////////////////////////////
  // main function
  //////////////////////////////////
  public static void main(final String[] args)
  {
    if(args.length == 1)
    {
      fName = args[0] + File.separator + fName;
      System.out.println("Creating new time stamped version file");
      System.out.println(" File will be placed in: " + fName);
      try{
        final Date now = new Date();
        final DateFormat df = new SimpleDateFormat("dd MMM yy HH:mm");
        final java.io.FileWriter fw = new FileWriter(fName);
        final java.io.BufferedWriter out = new BufferedWriter(fw);
        out.write("package Debrief.GUI;");
        out.newLine();
        out.write("// Automatically generated by CreateVersion.java (mayo.com)");
        out.newLine();
        out.write("public class VersionInfo");
        out.newLine();
        out.write("{");
        out.newLine();
        out.write("static public String getVersion()");
        out.newLine();
        out.write("{");
        out.newLine();
        out.write("return \"" + df.format(now) + "\";");
        out.newLine();
        out.write("}");
        out.newLine();
        out.write("}");
        out.flush();
        out.close();
        fw.close();
      }
      catch(final Exception e)
      {
        e.printStackTrace(System.err);
        System.exit(1);
      }
    }
    else
    {
      System.err.println("FAILED TO RECEIVE OUTPUT FILENAME");
      System.exit(1);
    }
    System.out.println("COMPLETE");
	}
}
