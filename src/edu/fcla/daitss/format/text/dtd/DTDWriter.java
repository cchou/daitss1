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
/*
 * Created on Nov 5, 2004
 *
 */
package edu.fcla.daitss.format.text.dtd;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.util.Informer;

/**
 * DTDWriter
 * 
 * @author Andrea Goethals, FCLA
 *
 */
public class DTDWriter {
    
    /**
     * DTD output.
     */
    private Writer output = null;
    
    /**
     * 
     */
    private DTD dFile = null;
    
    /**
     * 
     * @throws FatalException
     */
    public void cleanUp() throws FatalException {
        String methodName = "cleanUp()";
        
        try {
            dFile = null;
            output.close();
        } catch (IOException e) {
            Informer.getInstance().fail(this,
                    methodName,
                    "Can't close writer",
                    "Localizer writer for " + dFile.getPath(),
                    new FatalException(e));
        }
    }

    /**
     * Instantiate the writer.
     * 
     * @param filePath absolute path of a file to create.
     * @param _dFile DTD representation of file being parsed
     * @throws FatalException
     */
    public DTDWriter(String filePath, DTD _dFile) throws FatalException {
        String methodName = "DTDWriter(String)";
        
        // check args
        if (filePath == null || _dFile == null) {
            Informer.getInstance().fail(this,
                    methodName,
                    "Null argument(s)",
                    "filePath: " + filePath + "_dFile: " + _dFile,
                    new FatalException("Arguments can't be null"));
        }
        
        dFile = _dFile;
        
        // instantiate the writer
        String encoding = "UTF-8";
        try {
            output = new OutputStreamWriter(
                    new FileOutputStream(filePath), encoding);
        } catch (UnsupportedEncodingException e) {
            // won't happen with UTF-8 but anyway -
            Informer.getInstance().fail(this,
                    methodName,
                    "Unsupported encoding",
                    "Encoding: " + encoding,
                    new FatalException(e));
            
        } catch (FileNotFoundException e) {
            // ? the file didn't exist even though it
            // doesn't exist, we are creating it
            Informer.getInstance().fail(this,
                    methodName,
                    "File doesn't exist",
                    "File: " + dFile.getPath(),
                    new FatalException(e));
        }
    }
    
    /**
     * Write a raw character.
     * 
     * @param c The character to write.
     * @exception DTDWriterException
     *        If there is an error writing the character, this method
     *        will throw an IOException wrapped in a DTDWriterException.
     */
    public void write(char c) throws DTDWriterException {
        try {
            output.write(c);
        } catch (IOException e) {
            throw new DTDWriterException(e);
        }
    }
    
    /**
     * Write a raw string.
     * 
     * @param s
     * @exception DTDWriterException
     *        If there is an error writing the string, this method will
     *        throw an IOException wrapped in a DTDWriterException
     */
    public void write(String s) throws DTDWriterException {
        try {
            output.write(s);
        } catch (IOException e) {
            throw new DTDWriterException(e);
        }
    }

}
