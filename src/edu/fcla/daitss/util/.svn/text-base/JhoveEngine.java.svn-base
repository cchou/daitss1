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
package edu.fcla.daitss.util;

import java.io.File;

import edu.fcla.daitss.exception.FatalException;
import edu.harvard.hul.ois.jhove.App;
import edu.harvard.hul.ois.jhove.JhoveBase;
import edu.harvard.hul.ois.jhove.JhoveException;
import edu.harvard.hul.ois.jhove.Module;
import edu.harvard.hul.ois.jhove.RepInfo;

public class JhoveEngine extends JhoveBase {
	/* the required application object for JHOVE */
	private App jhoveApp;
	private static final int [] DATE    = {2006, 5, 15};
	/* save for now for the init() method
	private final String PDFClassName = "edu.harvard.hul.ois.jhove.module.PdfModule";
    List modParams = new ArrayList ();
    */
	/* The one and only instance of JhoveEngine */
    private static JhoveEngine instance = null;
    
	/**
	 * class constructor.
	 */
	public JhoveEngine () throws JhoveException
	{
	    super ();
	    jhoveApp = new App ("", "", DATE, "", "");
	}

	
	/**
     * Constructs the single ArchiveProperties object if hasn't already been constructed.
     * Fills the properties Hashtable with the configuration file properties.
     * 
     * @return a ArchiveProperties
     * @throws FatalException
     */
    public static synchronized JhoveEngine getInstance() throws FatalException {
        if (instance == null) {
            // there is no JhoveEngine object yet - construct it.
        	try {
        		instance = new JhoveEngine();
        	    String jhoveConfig = ArchiveProperties.getInstance().
        	    	getArchProperty("JHOVE_CONFIG");
        	    // initialize the jhove engine, this will create all the JHOVE modules
        	    JhoveEngine.getInstance().init(jhoveConfig, null);
        	} catch (JhoveException e) {
        		throw new FatalException("Problems in instantiating the JHOVE interface", e);
        	}
        } 
        return instance;
    }              
    
    /**
     * Initialize the JHOVE engine (without the use of JHOVE config file)
     */
   /* save for now, just in case we need to remove JHOVE config file later. 
    public void init () throws JhoveException
    {
        String err = null;
        
		_jhoveHome  = "/usr/local/tools/jhove/";
		_encoding = "utf-8";
		_tempDir = ".";
		_bufferSize = 131072;

        // instanciate and initialize all the needed JHOVE modules.
		try {
            Class cl = Class.forName (PDFClassName);
            Module module = (Module) cl.newInstance ();
            module.init (null);
            module.setDefaultParams (modParams);
            
			_moduleList.add (module);
			_moduleMap.put  (module.getName ().toLowerCase (), module);       
		} catch (Exception e) {
            if (err == null) {
                err = "cannot instantiate module: " + PDFClassName;
                throw new JhoveException (err);
            }
	    }
    } */
    
	/**
	 * Validate a file by invoking the protected processFile() method of the parent
	 * JhoveBase class.
	 *
	 * @param module  JHOVE module
	 * @param file    The formatted file being validated
	 * @param info    Representation information about the file
	 * @return False  if the module cannot perform validation
	 */
	public boolean validateFile (Module module, File file, RepInfo info) throws Exception
	{
	    return super.processFile (jhoveApp, module, false, file, info);
	}
}
