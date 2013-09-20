package edu.fcla.daitss.format.text.csv;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.util.Informer;
/**
 * @author carol
 * 
 */
public class LineReader {

	BufferedReader reader;
	
	public LineReader(String filename) throws FatalException {
		try {
			reader = new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException e) {
			Informer.getInstance().fail(this, "LineReader(File)",
				"I/O error", "Could not find the file", e);
		}
	} 
	
	/**
	 * read the next line from the file
	 * @return the next line
	 */
	protected String readNext() throws FatalException {
		String line = null;
		try{	
			line = reader.readLine();
		} catch (IOException e) {
			Informer.getInstance().fail(this, "LineReader()",
					"I/O error", "Could not read the file", e);
		}
		return line;
	}
	
}
