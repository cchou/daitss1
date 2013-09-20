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

import java.io.BufferedInputStream;
import java.io.InputStreamReader;
import java.io.IOException;

import edu.fcla.daitss.exception.FatalException;

/**
 * Represents a program external to DAITSS. Used for executing processes 
 * and commands outside of the DAITSS code. Only tested with a small
 * set of known external programs and commands. A singleton object.
 * 
 * @author Andrea Goethals, FCLA
 * 
 */
public class ExternalProgram {

	/**
	 * Test driver.
	 * 
	 * @param args command to execute
	 * @throws FatalException
	 */
	public static void main(String[] args) throws FatalException {
		// form the command to execute
		//String[] command = {"C:\\clamav-devel\\bin\\clamscan.exe","--quiet","C:\\temp\\eicar.com"};
		String[] command = {"clamscan", "/apps/virusFiles/eicar.com"
		};

		ExternalProgram program = new ExternalProgram();
		program.executeCommand(command);

		// get any messages the executing program wrote to the output stream.
		String output = program.getOutput();
		if (output != "") {
			System.out.println("Output: " + output);
		} else {
			System.out.println("No output message.");
		}

		// get any messages the executing program wrote to the error stream
		String error = program.getError();
		if (error != "") {
			System.out.println("Error: " + error);
		} else {
			System.out.println("No error message.");
		}

		// get the executing program's exit code
		int exitValue = program.getExitValue();
		System.out.println("Exit value: " + exitValue);
	
	}

	/**
	* Any message written to STDERR resulting from the external program's
	* execution.
	*/
	private ReaderConsumer commandErr = null;

	/**
	 * Input to the external program, Output to the program
	 * calling this class. NOT SUPPORTED YET IF EVER.
	 */
	//private OutputStream commandIn = null;

	/**
	 * Any message written to STDOUT as a result of the program execution.
	 */
	private ReaderConsumer commandOut = null;

	/**
	 * The executing program's exit value.
	 */
	private int exitValue;

	/**
	 * Only constructor that is private so that this class can be a singleton.
	 */
	public ExternalProgram() {
	}

	/**
	 * Executes a command. Will not support pipes ('|') or redirects.
	 * 
	 * @param command Command to execute
	 * @throws FatalException
	 */
	public void executeCommand(String[] command) throws FatalException {
		// check that there is a command to execute
		if (!hasGoodArgs(command)) {
			Informer.getInstance().fail(
				this, "executeCommand(String)",
				"Null argument",
				"null argument: args",
				new FatalException("Null argument"));
		}
		//setArgs(args);
		
		// create a nice print-out ready String of the command
		StringBuffer theCommand = new StringBuffer();
		for (int i = 0; i < command.length; i++) {
			theCommand.append(" " + command[i]);
		}
		
		Informer.getInstance().info(
			this, "executeCommand(String)",
			"Beginning execution at " + DateTimeUtil.now(),
			"command: " + theCommand.toString(),
			false);

		Runtime rt = Runtime.getRuntime();
		try {
			Process p = null;
			try {
				// execute
				p = rt.exec(command);
			} catch (IOException e) {
				// log and then throw a fatal error
				Informer.getInstance().fail(
					this, "executeCommand(String)",
					"Illegal argument",
					"command: " + theCommand.toString(),
					new FatalException(e.getMessage()));
			}

			// read the program execution's output
			this.commandOut =
				new ReaderConsumer(
					new InputStreamReader(
						new BufferedInputStream(p.getInputStream())));
			new Thread(commandOut).start();

			// simultaneously read the program execution's error output
			this.commandErr =
				new ReaderConsumer(
					new InputStreamReader(
						new BufferedInputStream(p.getErrorStream())));
			new Thread(commandErr).start();

			// AFTER the program stops get the exit value
			this.exitValue = p.waitFor();
			
			Informer.getInstance().info(
				this, "executeCommand(String)",
				"Finished execution at " + DateTimeUtil.now(),
				"command: " + theCommand.toString(),
				false);
				
		} catch (Exception e) {
			// log the fatal error then throw it
			Informer.getInstance().fail(
				this, "executeCommand(String)",
				"Unexpected exception",
				"command: " + theCommand.toString(),
				new FatalException(e.getMessage()));
		}

	}

	/**
	 * Executes a command. Will not support pipes ('|') or redirects.
	 * 
	 * @param command Command to execute
	 * @throws FatalException
	 */
	public void executeCommand(String command) throws FatalException {
		// check that there is a command to execute
		if ((command == null) || command.length() < 1 ) {
			Informer.getInstance().fail(
				this, "executeCommand(String)",
				"Null argument",
				"null argument: args",
				new FatalException("Null argument"));
		}

		
		// create a nice print-out ready String of the command
		StringBuffer theCommand = new StringBuffer(command);
	
		Informer.getInstance().info(
			this, "executeCommand(String)",
			"Beginning execution at " + DateTimeUtil.now(),
			"command: " + theCommand.toString(),
			false);

		Runtime rt = Runtime.getRuntime();
		try {
			Process p = null;
			try {
				// execute
				p = rt.exec(command);
			} catch (IOException e) {
				// log and then throw a fatal error
				Informer.getInstance().fail(
					this, "executeCommand(String)",
					"Illegal argument",
					"command: " + theCommand.toString(),
					new FatalException(e.getMessage()));
			}

			// read the program execution's output
			this.commandOut =
				new ReaderConsumer(
					new InputStreamReader(
						new BufferedInputStream(p.getInputStream())));
			new Thread(commandOut).start();

			// simultaneously read the program execution's error output
			this.commandErr =
				new ReaderConsumer(
					new InputStreamReader(
						new BufferedInputStream(p.getErrorStream())));
			new Thread(commandErr).start();

			// AFTER the program stops get the exit value
			this.exitValue = p.waitFor();
			
			Informer.getInstance().info(
				this, "executeCommand(String)",
				"Finished execution at " + DateTimeUtil.now(),
				"command: " + theCommand.toString(),
				false);
				
		} catch (Exception e) {
			// log the fatal error then throw it
			Informer.getInstance().fail(
				this, "executeCommand(String)",
				"Unexpected exception",
				"command: " + theCommand.toString(),
				new FatalException(e.getMessage()));
		}

	}
	/**
	 * Returns any output written to STDERR by the executed program.
	 * 
	 * @return Any output that the executed program wrote to STDERR.
	 */
	public String getError() {
		return commandErr.getData();
	} // end getError        

	/**
	 * Returns the exit value of the executed program.
	 * 
	 * @return The exit value. The valid values depend on the program.
	 */
	public int getExitValue() {
		return exitValue;
	}

	/**
	 * Returns any output written to STDOUT by the executed program.
	 * 
	 * @return Any output the executed program wrote to STDOUT.
	 */
	public String getOutput() {
		return commandOut.getData();
	} // end getOutput        

	/**
	 * Checks that a command to execute exists.
	 * 
	 * @param args A non-null command to execute.
	 * @return 	<code>true</code> if the <code>args</code> argument in not null,
	 * 				otherwise <code>false</code>.
	 */
	private boolean hasGoodArgs(String[] args) {
		if (args == null || args.length < 1) {
			return false;
		}
		return true;
	}

	/**
	 * Parses the args parameter to set the commands Vector. Will throw
	 * an IllegalArgumentException if a pipe or redirect is included in the command.
	 * 
	 * @param args Command to execute.
	 */
	/*private void censorArgs(String[] args) throws FatalException {
		
		StringTokenizer tokenizer = new StringTokenizer(args);
		String currentToken = null;
		int numTokens = 0;
		this.command = new String[tokenizer.countTokens()];
		while (tokenizer.hasMoreTokens()) {
			currentToken = tokenizer.nextToken();
			if (currentToken.charAt(0) == '|'
				|| currentToken.charAt(0) == '>') {
				//log and then throw a fatal error
				Informer.getInstance().fail(
					this, "setArgs(String)",
					"Illegal argument",
					"command: " + args + "\tillegal char: " + currentToken,
					new FatalException("Illegal character in command"));
			}
			this.command[numTokens++] = currentToken;
		}
		
	}*/

} // end ExternalProgram
