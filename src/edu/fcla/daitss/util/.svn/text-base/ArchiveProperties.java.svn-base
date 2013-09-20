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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.TimeZone;

import edu.fcla.da.xml.WebCache;
import edu.fcla.daitss.exception.DaitssException;
import edu.fcla.daitss.exception.FatalException;

/**
 * A singleton class representing all the configuration properties of
 * the archive.
 * 
 * @author Andrea Goethals, FCLA
 * @author Chris Vicary, FCLA
 */
public class ArchiveProperties extends Properties {
    
    /**
     * Instance variable to store the actual value of the DAITSS_HOME
     * environment variable (in other words, $DAITSS_HOME)
     */
    private final String daitssHome;
    
    /**
     * The name of the environment variable that points to the DAITSS_HOME
     * directory - required by all installations of DAITSS  
     */
    private static final String DAITSS_HOME = "DAITSS_HOME";
    
    /**
     * The path to the configuration file. This path
     * should start with the config directory that is within
     * the <code>$DAITSS_HOME</code> directory.
     */
    private static final String CONFIG_PATH =
        "config/daitss.properties";
    
    /**
     * The only instance of ArchiveProperties.
     */
    private static ArchiveProperties instance = null;
    
    /**
     * Linux operating system
     */
    public static final String OS_LINUX = "LINUX";    
    
    /**
     * Unknown operating system
     */
    public static final String OS_UNKNOWN = "UNKNOWN";
    
    /**
     * Windows operating system
     */
    public static final String OS_WIN = "WIN";
    
    /**
     * Constructs the single ArchiveProperties object if hasn't already been constructed.
     * Fills the properties Hashtable with the configuration file properties.
     * 
     * @return a ArchiveProperties
     * @throws FatalException
     */
    public static synchronized ArchiveProperties getInstance() throws FatalException {
        if (instance == null) {
            // there is no ArchiveProperties object yet - construct it.
            instance = new ArchiveProperties();
        } 
        return instance;
    }              
     
    /**
     * Determines the operating system that this program is running
     * on. If the operating system is anything but Linux or a Windows variant 
     * the unknown operating system code will be returned. Currently
     * only Linux and Windows 2000/XP have been tested with daitss
     * so that is why only these operating systems are supported.
     * 
     * @return  the operating system
     */
    public static String getOs() {
        String theOs = OS_UNKNOWN;
        
        String os = System.getProperty("os.name");
        if (os.equalsIgnoreCase("Linux")){
            // Linux
            theOs = OS_LINUX;
        } else if  (os.toLowerCase().startsWith("windows")) {
            // Windows is the only other OS supported for now!
            theOs = OS_WIN;
        }
        return theOs;
    }    
    
    /**
     * Test driver.
     * 
     * @param args not used
     * @throws FatalException
     */
    public static void main(String[] args) throws FatalException{
        System.out.println(ArchiveProperties.getInstance().toString());
    }

    /**
     * All the properties contained in the DAITSS configuration files or 
     * overidden using the <code>setArchProperty</code> method.
     */
    private Hashtable properties = null;

    /**
     * Constructor. Private so that no other class can directly 
     * construct an ArchiveProperties object. Use <code>getInstance()</code>
     * to get an ArchiveProperties object.
     */
    private ArchiveProperties() throws FatalException{
        String methodName = "constructor: ArchiveProperties()";     
        // get the DAITSS_HOME environment variable
        String tmpDaitssHome = System.getenv(DAITSS_HOME);                      
        
        if (tmpDaitssHome == null || tmpDaitssHome.trim().equals("")) {
            fail(DAITSS_HOME + " environment variable is not set.");
        }
        
        // add a File separator to the end if needed
        this.daitssHome = tmpDaitssHome 
            + (tmpDaitssHome.endsWith(File.separator)?"":File.separator);
        
        // initialize properties storage
        this.properties = new Hashtable();
        // load all the properties from the configuration files
        this.loadArchProperties();
    }         
    
    /**
     * Fail routine for when the configuration properties can not be read
     * properly.
     * 
     * @param message   description of the reason for failure
     * @throws FatalException
     */
    private void fail(String message) throws FatalException {
        Log.getInstance("ABEND_" + this.getClass().getName());
        Informer.getSafeInstance().safeFail(this,
            "fail(String)",message, "ArchiveProperties",
            new FatalException("Unable to initialize ArchiveProperties"));
    }

    /**
     * Returns the value of a DAITSS property. If the property key given
     * to this method is not valid, the fail routines will be called.
     * 
     * @param key A DAITSS configuration file property
     * @return the value of the property
     * @throws FatalException
     */
    public String getArchProperty(String key) throws FatalException {
        
        //if (!properties.containsKey(key)) {
        if (!isArchProperty(key)) {
            
            FatalException fe = new FatalException("Not an archive property: " + key);
            
            //fe.printStackTrace();
            //System.exit(1);
            
            Informer.getInstance().fail(
                this,
                "getArchProperty(String)",
                "Invalid property name",
                "key: " + key,
                fe);                    
        } 
        
        return (String) properties.get(key);
        
    }         

    /**
     * Checks that a given property exists in its <code>properties</code> Hashtable 
     * as a key. The property must be exactly as it appears in the configuration files.
     * 
     * @param name The name of a property in a DAITSS configuration file.
     * @return <code>true</code> if <code>name</code> is a DAITSS property,
     *              otherwise <code>false</code>.
     */
    public boolean isArchProperty(String name) {
        if (name != null && properties.containsKey(name)) {
            return true;
        }
        return false;
    }


    /**
     * Loads the general, system, policy and database property files located at
     * the paths stored as constants in this class. 
     * This is called by <code>ArchiveProperties.getInstance()</code>
     * the first time its called.
     * 
     * @throws FatalException
     */
    private void loadArchProperties() throws FatalException {
        
        String configPath = this.daitssHome + CONFIG_PATH;
                
        try {
            // load all the configuration files
            FileInputStream stream = new FileInputStream(configPath);
            load(stream);
            stream.close();
            Enumeration e = propertyNames();
            
            // get the system path prefixes
            String codeHome = null, dataHome = null, logsHome = null;

            codeHome = this.daitssHome;
            dataHome = getProperty("DAITSS_DATA_PATH");
            logsHome = getProperty("DAITSS_LOGS_PATH");
                                    
            // load system configuration properties to hashtable
            while (e.hasMoreElements()) {
                String element = (String) e.nextElement();
                String value = getProperty(element);
                                                
                //  construct complete paths from path prefixes and partial paths specified
                // in the configuration files
                if (value.indexOf("$DAITSS_HOME$") > -1) {
                    value = FileUtil.replaceString(value,"$DAITSS_HOME$",codeHome );
                    value = validatePath(value);
                } else if (value.indexOf("$DAITSS_DATA_PATH$") > -1){
                    value = FileUtil.replaceString(value,"$DAITSS_DATA_PATH$",dataHome );
                    value = validatePath(value);        
                } else if (value.indexOf("$DAITSS_LOGS_PATH$") > -1){
                    value = FileUtil.replaceString(value,"$DAITSS_LOGS_PATH$",logsHome );
                    value = validatePath(value);                                            
                }
                
                if (element.equals("WEBCACHE_EXP")) {
                	System.setProperty(WebCache.EXPIRATION_PROPERTY, value);
                }
                
                properties.put(element,value);
            }
        } catch (FileNotFoundException fnfe) { 
            // log error and fail
            fail(fnfe.getMessage());
        } catch (IOException ioe) { 
            //  log error and fail
            fail(ioe.getMessage());
        }
        
        // set the timezone
        String strTZ = getArchProperty("TIME_ZONE");
        TimeZone tz = TimeZone.getTimeZone(strTZ);
        TimeZone.setDefault(tz);
    }

    /**
     * Overrides a DAITSS configuration file property. Does not change the
     * value in the configuration file but uses the value set with this method in the application
     * instead of the value set in the configuration file. If the property key given to this 
     * method is not valid it will return a <code>1</code> and not change any
     * properties. The result of calling this method should always be checked to ensure 
     * that the properties are set to known values!
     * 
     * @param key A property contained in a DAITSS configuration file.
     * @param value The value that should be assigned to <code>property</code>.
     * @return  The result of setting the property: <code>0</code> if the property
     *              was successfully set, otherwise <code>1</code>.
     * @throws FatalException
     */
    public short setArchProperty(String key, String value) throws FatalException {
        if (isArchProperty(key)) {
            //Object oldValue = properties.put(key, value);
            properties.put(key, value);
            //oldValue = null; // helps garbage collecting?
            return 0;
        } 
        
        // log error but don't fail
        Informer.getInstance().info(
            this,
            "setArchProperty(String, String)",
            "Invalid property name",
            "Name: " + key,
            true);
        return 1;
        
    }

    /**
     * Constructs a String containing all the configuration file properties.
     * This method is intended for testing purposes only - not for production because 
     * it does not throw any of the DaitssExceptions that might have been
     * thrown if it could not contruct the ArchiveProperties object or load
     * the properties from the configuration files.
     * 
     * Note: this method is intended for test purposes only! If used
     * in production it could throw a DaitssException.
     * 
     * @return all the property/value pairs in the configuration files
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        try {   
            ArchiveProperties ap = ArchiveProperties.getInstance();
                    
            // print out all the properties
            sb.append("Properties:");
            sb.append("\n____________________________________________");
            Enumeration e = ap.properties.keys();
            while (e.hasMoreElements()) {
                String element = (String) e.nextElement();
                sb.append("\n" + element + "\n\t" + ap.getArchProperty(element));
            }
            sb.append("\n____________________________________________");
            
        } catch (DaitssException e) {
            sb.append("Caught a DaitssException in ArchiveProperties.toString()");
        }
        return sb.toString();
    }
    
    /**
     * Acts on paths contained in the configuration files.
     * Makes sure that all paths specified in the conguration files exist,
     * otherwise the application fails. 
     * 
     * @param origValue a path specified in a config file
     * @return the path if the path is valid, otherwise fail
     * @throws IOException
     * @throws FatalException
     */
    private String validatePath(String origValue) throws IOException, FatalException {
        String newValue = null;
        
        File f = new File(origValue);
        newValue = f.getCanonicalPath(); // removes final slash for directories
        if (!f.exists()) {
            // all files and directories in the config files must exist
            fail("This path/dir in config file doesn't exist: " + newValue);
        }
        if (f.isDirectory()) {
            // add final slash back
            newValue = newValue + File.separator;
        }
        
        f = null;
        
        return newValue;
    }

} // end ArchiveProperties
