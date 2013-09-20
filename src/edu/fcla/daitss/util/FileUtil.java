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

import java.io.IOException;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.format.markup.XMLSAXParser;

/**
 * @author vicary
 * @author Andrea Goethals, FCLA
 * 
 * This is a class consisting of static file-related methods that are likely to
 * be used by multiple classes.
 */
public class FileUtil {
    
    /**
     * Fully-qualified name for this class. To be used for 
     * Informer calls from within static methods.
     */
    private static String CLASSNAME = FileUtil.class.getName();

    /**
     * Method to append a string (str) to the end of a file (oFileName). If the
     * output file does not exist, an attempt will be made to create it.
     * 
     * @param str
     *            a string to write to the output file
     * @param oFileName
     *            the output filename
     * @throws FileNotFoundException
     *             if the file exists but is a directory rather than a regular
     *             file, does not exist but cannot be created, or cannot be
     *             opened for any other reason
     * @throws IOException
     *             if an I/O error occurs
     */
    public static void appendStringToFile(String str, String oFileName)
            throws FileNotFoundException, IOException {
        FileOutputStream ostream;
        PrintWriter pw;

        ostream = new FileOutputStream(oFileName, true);

        pw = new PrintWriter(ostream);
        pw.println(str);
        pw.close();
        ostream.close();
    }

    /**
     * Changes the root of an absolute path.
     * 
     * @param absPath
     *            the path the change.
     * @param oldRoot
     *            the old root contained in absPath.
     * @param newRoot
     *            the root to replace oldRoot with.
     * @return String object
     */
    public static String changeRoot(String absPath, String oldRoot,
            String newRoot) {
        absPath = new File(absPath).getAbsolutePath();
        oldRoot = new File(oldRoot).getAbsolutePath();
        newRoot = new File(newRoot).getAbsolutePath();        
        String relPath = stripRoot(absPath, oldRoot);
        String newPath = new File(newRoot, relPath).getAbsolutePath(); 
        return newPath;
    }

    /**
     * Method to copy a file or directory. If the source argument is a file, it
     * will be copied to the file specified by destination. If source is a
     * directory, it and all of its contents (provided that they are readable)
     * will be copied to a directory specified by destination. The directories
     * comprising the destination will be created automatically (provided the
     * process has sufficient permissions). This method works for relative and
     * absolute paths, in any combination.
     * 
     * @param source
     *            a file or directory to be copied
     * @param destination
     *            a file or directry to which source is copied
     * @throws IOException
     */
    public static void copy(String source, String destination)
            throws IOException {
        File src = new File(source);
        File dest = new File(destination);

        // Make sure the specified source exists and is readable.
        if (!src.canRead())
            throw new IOException("source is unreadable: " + src);

        if (src.isFile()) {
            copyFile(source, destination);
        } else if (src.isDirectory()) {
            // ensure that file separator is included
            String normSource = source;
            if (!normSource.endsWith(File.separator))
                normSource = normSource + File.separator;

            String normDest = destination;
            if (!normDest.endsWith(File.separator))
                normDest = normDest + File.separator;

            File fDest = new File(normDest);
            // now copy the directory
            fDest.mkdirs();

            File fSrc = new File(normSource);
            // store all files and dirs under this directory into an array
            String[] files = fSrc.list();

            for (int i = 0; i < files.length; i++) {
                // get the input file
                String input = normSource + files[i];
                // finish forming output file
                String output = normDest + input.substring(normSource.length());
                // copy the file
                copy(input, output);
            }

            // clean up
            fSrc = null;
            fDest = null;

        } else {
            // source is neither a file nor a directory (should never happen)
            throw new IOException("SubAccount is neither a file nor a directory: "
                    + source);
        }

        // clean up
        src = null;
        dest = null;
    }

    /**
     * Method to copy a single file. Works for relative and absolute paths.
     * 
     * @param source
     *            source file
     * @param destination
     *            destination file
     * @exception IOException
     *                if operation fails
     */
    public static void copyFile(String source, String destination)
            throws IOException {
        File src = new File(source);
        File dest = new File(destination);

        // Make sure the specified source exists, is readable, and is a file.
        if (!src.isFile())
            throw new IOException("SubAccount " + src + "is not a file");
        if (!src.canRead())
            throw new IOException("SubAccount " + src + "is unreadable");

        // get directory of destination
        File newDir = new File(destination.substring(0, destination
                .lastIndexOf(File.separator)));
        // if the destination directory doesn't already exist, then try to
        // create it (and all
        // intermediate directories)
        if (!newDir.exists()) {
            if (!newDir.mkdirs()) {
                throw new IOException("Unable to create directories: " + dest);
            }
        }

        // create input and output streams for byte transfer
        FileInputStream fis = new FileInputStream(src);
        FileOutputStream fos = new FileOutputStream(dest);

        // transfer bytes in 8K chunks
        byte[] buffer = new byte[8*1024];
        while (true) {
            int bytes_read = fis.read(buffer);
            if (bytes_read == -1)
                break;
            fos.write(buffer, 0, bytes_read);
        }
        // close input/output streams
        if (fis != null)
            try {
                fis.close();
                fis = null;
            } catch (IOException e) {
                ;
            }
        if (fos != null)
            try {
                fos.close();
                fos = null;
            } catch (IOException e) {
                ;
            }

        // clean up
        src = null;
        dest = null;
        newDir = null;
    }

    /**
     * Method to delete a file or a directory and all it's contents if there are
     * any. Unlike runtime methods this method is platform independent.
     * 
     * @param path
     *            a file or directory to delete
     * @return true if all contents under and including <code>path</code> are
     *         deleted
     * @throws IOException
     */
    public static boolean delete(String path) throws IOException {
        // assume success
        boolean rc = true;

        File file = new File(path);
        if (!file.exists()) {
            // there is no such directory in the disk, throw exception
            throw new IOException("Unable to delete " + path
                    + ". Path does not exist.");
        }

        if (file.isFile()) {
            // this is a file, so delete it directly
            if (file.delete()) {
                return true;
            }

            return false;
        }

        // delete the directory recursively
        String files[];
        files = file.list();
        int i;
        for (i = 0; i < files.length; i++) {
            File f = new File(path + File.separator + files[i]);
            if (f.isFile()) {
                // this is a file, delete it directly
                if (!f.delete())
                    rc = false;
            }
            if (f.isDirectory()) {
                // this is a directory, delete it recursively
                if (!delete(f.getAbsolutePath()))
                    rc = false;
            }

            // clean up
            f = null;
        }

        // now the directory should be empty, delete it directly
        if (!file.delete())
            rc = false;

        // clean up
        file = null;

        return rc;
    }

    /**
     * Examines a String looking for single instances of the '\' character
     * replacing them with '\\'. This is used by methods that need the separator
     * used in Windows' paths escaped. The path does not need to exist or be
     * non-null.
     * 
     * @param filePath
     *            a system path
     * @return the system path with any single instances of '\' replaced with
     *         '\\'.
     */
    public static String escapePathSlashes(String filePath) {
        StringBuffer newPath = new StringBuffer("");
        char thisChar = '\u0000', nextChar = '\u0000';

        for (int i = 0, j = 1; j < filePath.length(); i++, j++) {
            thisChar = filePath.charAt(i);
            nextChar = filePath.charAt(j);
            if (thisChar == '\\' && nextChar != '\\') {
                // found a single occurrence of '\'
                newPath.append("\\\\");
            } else if (thisChar == '\t') {
                // found a tab character
                newPath.append("\\\\t");
            } else if (thisChar == '\n') {
                // found a new line character
                newPath.append("\\\\n");
            } else if (thisChar == '\r') {
                // found a return character
                newPath.append("\\\\r");
            } else if (thisChar == '\b') {
                // found a backspace character
                newPath.append("\\\\b");
            } else if (thisChar == '\f') {
                // found a formfeed character
                newPath.append("\\\\f");
            } else {
                newPath.append(thisChar);
            }
        }

        // evaluate last character (in nextChar)
        if (nextChar == '\\' && thisChar != '\\') {
            newPath.append("\\\\");
        } else if (nextChar == '\t') {
            newPath.append("\\\\t");
        } else if (nextChar == '\n') {
            newPath.append("\\\\n");
        } else if (nextChar == '\r') {
            newPath.append("\\\\r");
        } else if (nextChar == '\b') {
            newPath.append("\\\\b");
        } else if (nextChar == '\f') {
            newPath.append("\\\\f");
        } else {
            newPath.append(nextChar);
        }

        return newPath.toString();
    }
    
    /**
     * Given a file system path, it replaces the file title
     * portion with the file's DFID + "." + the extension.
     * Note that the extension parameter may not necessarily
     * be the same as the file's extension because extensions
     * are normalized in the archive.
     * 
     * @param path absolute path to a file
     * @param dfid the file's DFID (unique id within the archive)
     * @param extension file extension to use in the new path
     * @return the new path 
     * @throws FatalException
     */
    public static String fileTitleToDfidPlusExt(String path, String dfid, 
            String extension) throws FatalException {
        String methodName = "fileTitleToDfidPlusExt(String, String, String)";
        String newPath = null;
        
        if (path == null || dfid == null || extension == null ) {
            Informer.getInstance().fail(CLASSNAME,
                    methodName,
                    "Null argument",
                    "path: " + path + " dfid: " + dfid + " extension: " + extension,
                    new FatalException("args can't be null"));
        }
        
        File f = new File(path);
        String parentDir = f.getParent();
        
        newPath = parentDir + File.separator + dfid + "." + extension;
        
        return newPath;
    }

    /**
     * Method to find a specific filename under a specific directory. A Vector
     * of Strings representing the absolute path of all occurences of the file
     * under the directory. For example, the Vector may contain
     * "/darchive/packages/UF/UF00000000/1.tiff",
     * "/darchive/packages/UF/UF00000000/images/tiffs/1.tiff". Since this method
     * is expected to be used on Unix-based systems, it is case sensitive.
     * 
     * @param fileName
     *            a file to search for
     * @param dirName
     *            the directory to search under
     * @param searchRecursively
     *            if true a recursive search is performed, if false only the
     *            directory specified in dirName is searched
     * @return a Vector of Strings representing the absolute path of all
     *         occurences of the file under the directory
     * @throws IOException
     *             if the directry does not exist or is unreadable
     */
    public static Vector findFile(String fileName, String dirName,
            boolean searchRecursively) throws IOException {
        File objDir = new File(dirName);

        // input checking
        if (objDir.exists() == false)
            throw new IOException("Directory " + dirName + " does not exist.");
        if (objDir.canRead() == false)
            throw new IOException("Directory " + dirName + " is unreadable.");
        if (objDir.isDirectory() == false)
            throw new IOException(dirName + " is not a directory.");

        // Vector storage for return
        Vector files = new Vector();
        // Create an array of File objects, one for each file or directory in
        // objDir.
        File[] fileList = objDir.listFiles();

        // examine returns for files with matching name
        if (fileList != null) {
            for (int i = 0; i < fileList.length; i++) {
                File Objdir1 = new File(fileList[i].getPath());
                if (Objdir1.isDirectory() && searchRecursively) {
                    // if the current File is a Directory, Call the function
                    // Recursively
                    files.addAll(findFile(fileName, fileList[i].getPath(),
                            searchRecursively));
                } else if (fileList[i].isFile()) {
                    File f = new File(fileList[i].toString());
                    if (f.getName().equals(fileName)) {
                        files.addElement(fileList[i].getAbsolutePath());
                    }

                    // clean up
                    f = null;
                }

                // clean up
                Objdir1 = null;

            } // for loop
        }

        // clean up
        objDir = null;

        return files;
    }
    
    /**
     * Returns the absolute path of a relative file. The trick is
     * that herePath is an absolute path and therePath is a path relative
     * from herePath. In other words, therePath's relative path could appear
     * within herePath and the link would be good.
     * 
     * Note that this method does not check if the resulting absolute
     * path points to an existing path. This is left to the caller of
     * this method because the file may or may not have to exist
     * depending on the context.
     * 
     * @param herePath absolute path of parent file.
     * @param therePath relative path of child file
     * @return the absolute path of therePath
     */
    public static String getAbsPathFromRelPath(String herePath, String therePath){
       	File hFile = new File(herePath);
    	
    	// check args
    	if (!hFile.exists() || hFile.isDirectory() || !hFile.isAbsolute()){
    	    throw new IllegalArgumentException(
    	            "This file (" + herePath + ") must be an absolute path " +
    	            "to an existing file (not directory)");
    	}
    	
    	// get the parent directory of herePath
    	String hereParentDir = hFile.getParent();
    	String thereAbsPath = hereParentDir + File.separator + therePath;
    	
    	// does not check if resulting file exists

        return thereAbsPath;
    }

    /**
     * Method to return the extension of a file name passed to it. The
     * <code>filePath</code> may be absolute or relative.
     * 
     * @param filePath
     *            absolute or relative path of a file
     * @return the file extension
     * @throws IllegalArgumentException
     */
    public static String getExtension(String filePath)
            throws IllegalArgumentException {
        if (filePath == null || filePath.trim().equals(""))
            throw new IllegalArgumentException(
                    "filePath argument is null or empty");

        File f = new File(filePath);
        String fileTitle = f.getName();
        String ext = "";

        int stopgapIndex = fileTitle.lastIndexOf(".");
        if (stopgapIndex > -1)
            ext = fileTitle.substring(stopgapIndex + 1);

        // clean up
        f = null;

        return ext;
    }
    
    /**
     * Returns the relative path of one file (there) to another (here).
     * These must be absolute paths to files!
     * 
     * @param herePath absolute path of the here file
     * @param therePath absolute path of the there file
     * @return the relative path of the there path to the 
     * here file
     */
    public static String getRelPathFrom(String herePath, String therePath){
    	String relPath = null;
    	
    	File hFile = new File(herePath);
    	File tFile = new File(therePath);
    	
    	// check args
    	if (!hFile.exists() || !tFile.exists() || hFile.isDirectory() ||
    	        tFile.isDirectory() || !hFile.isAbsolute() || !tFile.isAbsolute()){
    	    throw new IllegalArgumentException(
    	            "These file must be absolute paths " +
    	            "to existing files (not directories): herePath: " + herePath + " therePath: " + therePath);
    	}
    	
    	// calc the file name of each
    	String hBase = null;
    	String tBase = null;
    	hBase = hFile.getName();
    	tBase = tFile.getName();
    	//System.out.println("hBase: " + hBase + " tBase: " + tBase);
    	
    	// if the paths are to the same file return the name of
    	// the file
    	if (herePath.equals(therePath)){
    		return hBase;
    	}
    	
    	// calc the common part of the paths
    	int charPtr = 0;
    	char hereChar = '0';
    	char thereChar = '0';
    	char prevChar = '0';
    	int lastSepPos = -1;
    	while (hereChar == thereChar){
    	    prevChar = hereChar;
    	    if (charPtr < herePath.length())
    	    	hereChar = herePath.charAt(charPtr);
    		if (charPtr < therePath.length())
    			thereChar = therePath.charAt(charPtr);
    		if (hereChar == File.separatorChar){
    		    lastSepPos = charPtr;
    		}
    		charPtr++;
    	}
    	
    	if (prevChar != File.separatorChar){
    	    //System.out.println("need to roll back ptr");
    	    charPtr = lastSepPos+2;
    	}
    	// charPtr is now pointing to the start of the uncommon
    	// part of their paths
    	//System.out.println("hereChar: " + hereChar + " thereChar: " + thereChar);
    	
    	// calc the leftover part of the paths
    	String hLeft = null;
    	String tLeft = null;
    	hLeft = herePath.substring(charPtr - 1);
    	tLeft = therePath.substring(charPtr - 1);
    	//System.out.println("hLeft: " + hLeft + " tLeft: " + tLeft);
    	
    	// find out the paths compare to each other
    	if (hLeft.equals(hBase) && tLeft.equals(tBase)) {
    	    //System.out.println("same dir");
    		// they are in the same directory
    		relPath = tBase;
    	} else if (!hLeft.equals(hBase) && tLeft.equals(tBase)){
    		// will need to go back up the directory structure
    		//System.out.println("Need to add dots");
    		// count the number of directory separators
    		// and add a ../ for each to the their base filename
    		int numDirSeps = 0;
    		StringBuffer dotPath = new StringBuffer();
    		for (int newCharPtr = 0; newCharPtr < hLeft.length(); newCharPtr++){
    			char c = hLeft.charAt(newCharPtr);
    			if (c == File.separatorChar){
    				numDirSeps++;
    				dotPath.append(".." + File.separatorChar);
    			}
    		}
    		dotPath.append(tBase);
    		relPath = dotPath.toString();
    	} else if (hLeft.equals(hBase) && !tLeft.equals(tBase)){
    	    //System.out.println("need to include subdirs");
    		// there file is in at least one subdirectory
    		// deeper than here file
    		relPath = tLeft;
    	} else {
    	    // need to count the number of directory separators
    	    // and add a ../ for each to the their remainder of the
    	    // path leftover after removing the common part 
    	    int numDirSeps = 0;
    		StringBuffer dotPath = new StringBuffer();
    		for (int newCharPtr = 0; newCharPtr < hLeft.length(); newCharPtr++){
    			char c = hLeft.charAt(newCharPtr);
    			if (c == File.separatorChar){
    				numDirSeps++;
    				dotPath.append(".." + File.separatorChar);
    			}
    		}
    		dotPath.append(tLeft);
    		relPath = dotPath.toString();
    	}
    	
    	return relPath;
    }

    /**
     * Calculates the size of a file in bytes.
     * 
     * @param path
     *            to a file
     * @return the size of the file or directory
     * @throws IOException
     */
    public static long getSizeBytes(String path) throws IOException {

        // constraint
        if (path == null || path == "") {
            throw new IOException("missing filename.");
        }

        File file = new File(path);

        // constraint
        if (!file.exists()) {
            throw new IOException(file.getAbsolutePath() + " does not exist.");
        }

        // if the file is a directory then get the size of all the children
        // recursively
        long contentSize = 0;
        if (file.isDirectory()) {
            File[] contents = file.listFiles();
            for (int i = 0; i < contents.length; i++) {
                contentSize += getSizeBytes(contents[i].getPath());
            }
        }

        // clean up
        long fileSize = file.length();
        file = null;

        return (fileSize + contentSize);
    }

    /**
     * This method returns a unique path based on an absolute path 
     * to a directory or file. 
     *  
     * @param absPath
     * @return String object
     * @throws IllegalArgumentException 
     */
    public static String getUniqueName(String absPath) throws IllegalArgumentException {
        if (absPath == null || absPath.equals("")) {
            throw new IllegalArgumentException("absolute path is null or empty");
        }
        
        String thePath = absPath;
        // create a file object for the path
        File f = new File(absPath);
        // check to see if the path already exists, if not we're done        
        if (f.exists()) {
            // strip the file title if there is one
            if (f.isDirectory()) {
                if (thePath.endsWith(File.separator)) {
                    //strip the separator
                    thePath = thePath.substring(0, thePath.length() - 1);
                }
                // start finding a unique name
                int i = 0;
                String newPath = thePath;
                while (new File(newPath).exists()){
                    newPath = thePath + "_" + ++i;
                }
                
                // add the separator to the end
                thePath = newPath + File.separator;
            }
            else {
                // f is a file
                // get the path part
                String pathPart = f.getParent();
                if (pathPart == null || pathPart.equals(File.separator)) {
                    pathPart = "";
                }
                // get the title part
                String titlePart = f.getName();
                String name = titlePart;
                String ext = "";
                // strip the stopgap (if any) from the title
                if (titlePart.indexOf(".") > 0){
                    name = titlePart.substring(0, titlePart.lastIndexOf("."));
                    ext = titlePart.substring(titlePart.lastIndexOf(".") + 1);
                }
                // start finding a unique name
                int i = 0;
                String newPath = pathPart + File.separator + name + (ext.equals("")?"":("." + ext));
                while (new File(newPath).exists()){
                    newPath = pathPart + File.separator + name + "_" + ++i + (ext.equals("")?"":("." + ext));;
                }
                // return the new path
                thePath = newPath;
            }
        }
        
        return thePath;
    }
    
    /**
     * Determines whether the <code>dir</code> argument is a non-null readable
     * absolute path to a directory.
     * 
     * @param dir
     *            absolute path to a directory
     * @return whether or not <code>dir</code> is a non-null readable absolute
     *         path to a directory
     */
    public static boolean isGoodDir(String dir) {
        File theDir = null;
        boolean isAbsolutePath = true;

        theDir = new File(dir);

        if (theDir == null || !theDir.isDirectory() || !theDir.canRead()
                || !theDir.isAbsolute()) {
            isAbsolutePath = false;
        }

        // clean up
        theDir = null;

        return isAbsolutePath;
    }

    /**
     * Determines whether the <code>file</code> argument is a non-null
     * readable absolute path to a file.
     * 
     * @param file absolute path to an existing file
     * @return whether or not <code>file</code> is a non-null readable
     *         absolute path to a file
     */
    public static boolean isGoodFile(String file) {
        File theFile = null;
        String os = System.getProperty("os.name");
        boolean result = true;

        theFile = new File(file);

        if (theFile == null || !theFile.isFile() || !theFile.canRead()
                || !theFile.isAbsolute()) {
            result = false;
        }

        // clean up
        theFile = null;

        return result;
    }

    /**
     * Test driver
     * 
     * @param args not used
     * @throws FatalException
     */
    public static void main(String[] args) throws FatalException {

        // test getting absolute path from relative path
/*        System.out.println("Absolute path of links/g.gif from " + 
                "/daitss/dev/data/ingest/work/UF00003061/book_2.dtd is: " +
                getAbsPathFromRelPath("/daitss/dev/data/ingest/work/UF00003061/book_2.dtd",
                        "links/g.gif"));
*/        
        
        /*
        // test unique name finding
        System.out.println(getUniqueName("/tmp"));
        System.out.println(getUniqueName("/test.txt"));
        System.out.println(getUniqueName("/tmp/name_test/no_exist"));
        System.out.println(getUniqueName("/tmp/name_test/no_exist.txt"));
        System.out.println(getUniqueName("/tmp/name_test/dir_name"));
        System.out.println(getUniqueName("/tmp/name_test/testFile"));
        System.out.println(getUniqueName("/tmp/name_test/testFile.txt"));
        */
        
        
        // test disk usage
    	/*
        String cwd = System.getProperty("user.dir");
        String fs = System.getProperty("file.separator");
        System.out.println("Current Working Directory: " + cwd + ".");
        String path = "config";
        try {
            System.out.println("Disk usage for " + cwd + fs + path + " :\t"
                    + getSizeBytes(path));
        } catch (Exception e) {
            System.out.println(e);
        }
        */
        
        /*
        System.out.println("Converted path of /home/andrea/1/2/images/1.jpg " +
        		"with dfid abc and ext jpg:\n" +
        		fileTitleToDfidPlusExt("/home/andrea/1/2/images/1.jpg",
        		        "abc", Extension.EXT_JPG));
        
        System.out.println("Converted path of images/1.jpg " +
        		"with dfid abc and ext jpg:\n" +
        		fileTitleToDfidPlusExt("images/1.jpg",
        		        "abc", Extension.EXT_JPG));
        
        System.out.println("Rel path from /home/andrea/1/2/images/1.jpg to" +
    			" /home/andrea/2.pdf's dfid path is: " + 
				getRelPathFrom("/home/andrea/1/2/images/1.jpg", 
				        fileTitleToDfidPlusExt("/home/andrea/2.pdf", "FABC_7777", Extension.EXT_PDF)));
    	*/
        
        // testing stripExtFromTitle
        /*
        System.out.println("hello.html minus the extension: " + stripExtFromTitle("hello.html"));
        System.out.println("hello minus the extension: " + stripExtFromTitle("hello"));
        System.out.println("null minus the extension: " + stripExtFromTitle(null));
        System.out.println("\"\" minus the extension: " + stripExtFromTitle(""));
        
        // testing stripRoot
        System.out.println("Rel path from /test/ to /test.x.xml: " +
                stripRoot("/test/x.xml", "/test/"));
        
        // testing getRelPathFrom
       	System.out.println("Rel path from /home/andrea/1/2/images/1.jpg to" +
    			" /home/andrea/2.pdf is: " + 
				getRelPathFrom("/home/andrea/1/2/images/1.jpg", "/home/andrea/2.pdf") + "\n");
       	
    	System.out.println("Rel path from /home/andrea/images/1.jpg to" +
    			" /home/andrea/2.pdf is: " + 
				getRelPathFrom("/home/andrea/images/1.jpg", "/home/andrea/2.pdf") + "\n");
    	
    	System.out.println("Rel path from /home/andrea/1.jpg to" +
    			" /home/andrea/pdfs/2.pdf is: " + 
				getRelPathFrom("/home/andrea/1.jpg", "/home/andrea/pdfs/2.pdf") + "\n");
    	
    	System.out.println("Rel path from /home/andrea/1.jpg to" +
    			" /home/andrea/2.pdf is: " + 
				getRelPathFrom("/home/andrea/1.jpg", "/home/andrea/2.pdf") + "\n");
    	
      	System.out.println("Rel path from /home/andrea/1.jpg to" +
    			" /home/andrea/1.jpg is: " + 
				getRelPathFrom("/home/andrea/1.jpg", "/home/andrea/1.jpg") + "\n");
      	
      	System.out.println("Rel path from /home/andrea/tea.jpg to" +
    			" /home/andrea/test.jpg is: " + 
				getRelPathFrom("/home/andrea/tea.jpg", "/home/andrea/test.jpg") + "\n");
      	
      	System.out.println("Rel path from " +
      			"/daitss/dev/data/ingest/work/UF00003061/links_20041029172743/www.fcla.edu/dls/md/techmd.xsd \nto" +
    			" /daitss/dev/data/ingest/work/UF00003061/links_20041029172743/www.w3.org/2001/F20041029_AAACIC.xsd is: " + 
				getRelPathFrom("/daitss/dev/data/ingest/work/UF00003061/links_20041029172743/www.fcla.edu/dls/md/techmd.xsd", 
				        "/daitss/dev/data/ingest/work/UF00003061/links_20041029172743/www.w3.org/2001/F20041029_AAACIC.xsd"));

		*/

    }

    /**
     * Replaces all occurences of sFind in sSearch with sReplace
     * 
     * @param sSearch
     *            The string to search through
     * @param sFind
     *            The string to find
     * @param sReplace
     *            The string to replace all occurences of sFind
     * @return sSearch after all occurences of sFind have been replaced by
     *         sReplace
     */
    public static String replaceString(String sSearch, String sFind,
            String sReplace) {
        String result = sSearch;
        if (result != null && result.length() > 0) {
            int a = 0;
            int b = 0;
            while (true) {
                a = result.indexOf(sFind, b);
                if (a != -1) {
                    result = result.substring(0, a) + sReplace
                            + result.substring(a + sFind.length());
                    b = a + sReplace.length();
                } else
                    break;
            }
        }
        return result;
    }

    public static Vector getSubDirectories(String dirName) throws IOException {
        File objDir = new File(dirName);

        // input checking
        if (objDir.exists() == false)
            throw new IOException("Directory " + dirName + " does not exist.");
        if (objDir.canRead() == false)
            throw new IOException("Directory " + dirName + " is unreadable.");
        if (objDir.isDirectory() == false)
            throw new IOException(dirName + " is not a directory.");

        // Vector storage for return
        Vector subDirs = new Vector();
        // Create an array of File objects, one for each file or directory in
        // objDir.
        File[] fileList = objDir.listFiles();

        // examine returns for files with matching extension
        if (fileList != null) {
            for (int i = 0; i < fileList.length; i++) {
                File Objdir1 = new File(fileList[i].getPath());
                if (fileList[i].isDirectory()) 
                	subDirs.addElement(fileList[i].getName());

                // clean up
                Objdir1 = null;

            } // for loop
        }
        return subDirs;
    }
    /**
     * <p>
     * Recursively scans the directory specified by dirName for files with
     * extension equal to the extension argument. This method is
     * case-insensitive.
     * 
     * @param dirName
     *            a directory to scan recursively
     * @param extension
     *            the type of file to scan for ("" returns all files)
     * @param searchRecursively
     *            if true a recursive search is performed, if false only the
     *            directory specified in dirName is searched
     * @return a Vector of String objects representing the absolute path to each
     *         file matching the given criteria
     * @throws IOException
     */
    public static Vector scanForFiles(String dirName, String extension,
            boolean searchRecursively) throws IOException {
        File objDir = new File(dirName);

        // input checking
        if (objDir.exists() == false)
            throw new IOException("Directory " + dirName + " does not exist.");
        if (objDir.canRead() == false)
            throw new IOException("Directory " + dirName + " is unreadable.");
        if (objDir.isDirectory() == false)
            throw new IOException(dirName + " is not a directory.");

        // Vector storage for return
        Vector files = new Vector();
        // Create an array of File objects, one for each file or directory in
        // objDir.
        File[] fileList = objDir.listFiles();

        // examine returns for files with matching extension
        if (fileList != null) {
            for (int i = 0; i < fileList.length; i++) {
                File Objdir1 = new File(fileList[i].getPath());
                if (Objdir1.isDirectory() && searchRecursively) {
                    // if the current File is a Directory, Call the function
                    // Recursively
                    files.addAll(scanForFiles(fileList[i].getPath(), extension,
                            searchRecursively));
                }
                // the check for extension.trim().equals("") is used when no
                // extension is
                // given signifiying that all files are requested.
                else if (fileList[i].isFile()
                        && (extension.trim().equals("") || (fileList[i]
                                .toString().toUpperCase().endsWith("."
                                + extension.toUpperCase())))) {
                    //System.out.println (fileList[i].getPath());
                    files.addElement(fileList[i].getAbsolutePath());
                }

                // clean up
                Objdir1 = null;

            } // for loop
        }

        // clean up
        objDir = null;

        return files;
    } // scanForFiles
    
    /**
     * <p>
     * Recursively scans the directory specified by dirName for files 
     * with names matching a given pattern.
     *  
     * @param dirName
     *            a directory to scan recursively
     * @param pattern
     *            a compiled, regex pattern to match against
     * @param searchRecursively
     *            if true a recursive search is performed, if false only the
     *            directory specified in dirName is searched
     * @return a Vector of String objects representing the absolute path to each
     *         file matching the given criteria
     * @throws IOException
     */
    public static Vector <String> scanForFiles(String dirName, Pattern pattern,
            boolean searchRecursively) throws IOException {
        File objDir = new File(dirName);       
        
        // input checking
        if (objDir.exists() == false)
            throw new IOException("Directory " + dirName + " does not exist.");
        if (objDir.canRead() == false)
            throw new IOException("Directory " + dirName + " is unreadable.");
        if (objDir.isDirectory() == false)
            throw new IOException(dirName + " is not a directory.");

        // Vector storage for return
        Vector <String> files = new Vector <String> () ;
        // Create an array of File objects, one for each file or directory in
        // objDir.
        File[] fileList = objDir.listFiles();

        // examine returns for files with matching extension
        if (fileList != null) {
            for (int i = 0; i < fileList.length; i++) {
                File Objdir1 = new File(fileList[i].getPath());
                if (Objdir1.isDirectory() && searchRecursively) {
                    // if the current File is a Directory, Call the function
                    // Recursively
                    files.addAll(scanForFiles(fileList[i].getPath(), pattern,
                            searchRecursively));
                }
                // the check for extension.trim().equals("") is used when no
                // extension is
                // given signifiying that all files are requested.
                else if (fileList[i].isFile() && 
                        pattern.matcher(fileList[i].getName()).matches()) {                    
                    files.addElement(fileList[i].getAbsolutePath());
                }

                // clean up
                Objdir1 = null;

            } // for loop
        }

        // clean up
        objDir = null;

        return files;
    } // scanForFiles

    
    /**
     * Returns the portion of the file title before the
     * '.' + extension.
     * 
     * @param title file title
     * @return portion of the file title before the
     * '.' + extension.
     */
    public static String stripExtFromTitle(String title){
        String prefix = null;
        
        // check args
        if (title == null || title.equals("")){
            prefix = "";
        } else {
            prefix = title;
            
	        int dotIndex = title.indexOf('.');
	        
	        if (dotIndex != -1){
	            prefix = prefix.substring(0, dotIndex);
	        } // else return title as is (no extension)
        }
        
        return prefix;
    }

    /**
     * Returns additional file extension after the standard file extension
     * for example, for a file name of WP0000127.xml.mxf, this method will
     * return .mxf given "xml" as the standard file extension
     * @param filename - file name, excluding the path
     * @param fileExt 
     * @return portion of the file name after the standard file extension.
     */
    public static String getAdditionalExtension(String filename, String fileExt){
        String additionalExt = null;
        
        // check args
        if (filename == null || filename.equals("")){
        	additionalExt = "";
        } else {
        	additionalExt = filename;
            
        	// find the beginning point of the end of optional file extension 
	        int index = filename.indexOf(fileExt);
	        
	        if (index != -1) {
	        	index = index + fileExt.length();
	        	additionalExt = additionalExt.substring(index);
	        }
        }
        
        return additionalExt;
    }
    /**
     * Removes the specified root from the absolute path. 
     * @param absPath
     * @param root
     * @return String object
     */
    public static String stripRoot(String absPath, String root) {
        absPath = new File(absPath).getAbsolutePath();
        root = new File(root).getAbsolutePath();
        int relIndex = root.length() + 1;
        String relPath = absPath.substring(relIndex);
        return relPath;
    }

}