/*
 * DAITSS Copyright (C) 2007 University of Florida
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
/**
 * 
 */
package edu.fcla.daitss.util;

import java.io.File;
import java.util.StringTokenizer;

import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.exception.PackageException;
import edu.fcla.daitss.exception.SilentPackageException;

/**
 * Checks a single file of any file type for viruses. Uses the virus
 * checker and exit codes specified in the DAITSS system
 * configuration file.
 */
public class VirusChecker {	
	/**
	 * Test driver.
	 * 
	 * @param args	Not used
	 * @throws FatalException
	 * @throws PackageException
	 */
	public static void main(String[] args) 
		throws FatalException, PackageException {
				
		// virus-infected test file: linux
		//String fileToScan = "/apps/virusFiles/eicar.com";
		
		//String fileToScan = "/home/andrea/tmp/test dir/eicar.com";
		String fileToScan = "/home/vicary/Digital_Archive_Docs/test_data/UF00003061.tar";
		
		// non-virus-infected test file: linux
		//String fileToScan = "/home/andrea/work/publications/file_format_specs/jpeg/itu-1150.pdf";
		// non-virus-infected test file
		//String fileToScan = "/work/publications/file_format_specs/jpeg/itu-1150.pdf";
		
		//non-virus-infected test file: windows
		//String fileToScan = "C:\\WS_FTP.LOG";
		
		//virus-infected test file: windows
		//String fileToScan = "C:\\temp\\eicar.com";
		
		if (hasVirus(fileToScan)) {
			System.out.println("Has virus");
		} else {
			System.out.println("Does not have virus");
		}
	}

	/**
     * Specifies whether or not a file has a virus. If it finds a virus in the
     * file it will do nothing but return <code>true</code>. It will not log
     * the fact that the file had a virus so this should be done outside this
     * method.
     * 
     * @param filePath
     *            The absolute path to a file to check for viruses.
     * @return <code>true</code> if the file was found to contain a virus,
     *         otherwise <code>false</code.
     * @throws FatalException
     * @throws PackageException
     */
    public static boolean hasVirus(String filePath) throws FatalException,
            PackageException {

        //	get the class name to use in Informer calls
        String className = new VirusChecker().getClass().getName();

        String methodName = "hasVirus(String)";

        File file = null;
        boolean doesHaveVirus = false;

        // check that the filePath argument was not null
        try {
            file = new File(filePath);
        } catch (NullPointerException e) {
            Informer.getInstance().fail(className, methodName, "Null argument",
                    "null argument: filePath",
                    new FatalException(e.getMessage()));
        }

        // check that the file exists
        if (!file.exists()) {
            Informer.getInstance().fail(className, methodName,
                    "Nonexistent file", "filePath: " + filePath,
                    new FatalException("Non-existent file"));
        }

        // check that file is readable
        if (!file.canRead()) {
            Informer.getInstance().error(className, methodName,
                    "Nonreadable file", "filePath: " + filePath,
                    new PackageException("Nonreadable file"));
        }

        // virus-check the file
        ExternalProgram program = new ExternalProgram();
        String command = ArchiveProperties.getInstance()
                .getArchProperty("VIRUS_CMD");
        StringTokenizer st = new StringTokenizer(command, " ");
        String[] commands = new String[st.countTokens() + 1];
        int i = 0;
        while (st.hasMoreTokens()) {
            commands[i] = st.nextToken();
            i++;
        }

        commands[i] = filePath;
        program.executeCommand(commands);

        // cleanup referenced to file
        file = null;

        //	get any messages the executing program wrote to the output stream.
        String output = program.getOutput();
        if (output != "") {
            Informer.getInstance().info(className, methodName,
                    "Output of virus-checking file " + filePath,
                    "output: " + output, true);
        }

        // get any messages the executing program wrote to the error stream
        String error = program.getError();
        if (error != "") {
            Informer.getInstance().info(className, methodName,
                    "Error message from virus-checking file" + filePath,
                    "error: " + error, true);
        }

        // get the executing program's exit code
        int exitValue = program.getExitValue();
        
        /*
        Informer.getInstance().info(className, methodName,
                "Exit value from virus-checking file " + filePath,
                "exit value: " + exitValue, false);
                */
        
        // find out what the exit code of an infected file is
        int infected = Integer.parseInt(ArchiveProperties.getInstance()
                .getArchProperty("VIRUS_EXIT_CODE_INFECTED"));

        int clean = Integer.parseInt(ArchiveProperties.getInstance()
                .getArchProperty("VIRUS_EXIT_CODE_CLEAN"));
                
        if (exitValue == infected) {
            doesHaveVirus = true;
        } else if (exitValue != clean && exitValue != infected) {
            Informer.getInstance().error(className, methodName,
                    "Unexpected virus check exit value",
                    "exit value: " + exitValue + " file: " + filePath,
                    new SilentPackageException("Neither clean nor infected."));
        }
        
        Informer.getInstance().info(className, methodName,
                "Result of virus check of " + filePath,
                "has virus: " + doesHaveVirus, false);
        
        
        return doesHaveVirus;
    }         
} 
