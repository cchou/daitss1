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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.file.MessageDigestCalculator;

/**
 * @author franco
 *
 * This class represents a moving of a package to the accounts' output directories.
 */
public class DisseminationDelivery {
	    	
    private File zipFile;
	
    private File digestFile;

    private List<File> files;

    private String packageName;

    private String aipDirName;



	private File finalOutputDir;




	
    /**
     * @param packageName
     * @param institutionCode
     * @param files
     * @throws FatalException 
     */
    public DisseminationDelivery(String packageName, String institutionCode, File aipDir ) throws FatalException {
        this.packageName = packageName;
        this.files = new ArrayList<File>();
        this.files.addAll(find(aipDir));
        this.aipDirName = aipDir.getAbsolutePath();
        
        File ingestOutDir = new File(ArchiveProperties.getInstance().getArchProperty("INGEST_OUTPATH"));
        this.zipFile = new File(ingestOutDir, packageName + ".zip");
        this.digestFile = new File(ingestOutDir, packageName + ".DIGESTS");
        
        File disseminationOutDir = new File(ArchiveProperties.getInstance().getArchProperty("DISSEMINATION_OUTPUT_PATH"));
        finalOutputDir = new File(disseminationOutDir, institutionCode);
    	if (!(finalOutputDir.isDirectory() || finalOutputDir.mkdir())) {
    		Informer.getInstance().fail("cannot mkdir " + finalOutputDir.getParentFile().getAbsolutePath() , "cannot make DIP output dir for account");
    	}        
    }

   
    /**
     * @throws FatalException
     */
    public void process() throws FatalException {
        zipUpPackage();
        createDigestFile();
        moveToOutput();
    }

    /**
     * @throws FatalException 
     * 
     */
    private void moveToOutput() throws FatalException {
    	
        File finalZipFile = new File(finalOutputDir, zipFile.getName());
        File finalDigestFile = new File(finalOutputDir, digestFile.getName());

        if (!zipFile.renameTo(finalZipFile) ) {
        	Informer.getInstance().fail("cannot move " + zipFile.getAbsolutePath() + " to " + finalZipFile.getAbsolutePath(), "moving the DIP to the DIP output dir");
        }
        
        if (!digestFile.renameTo(finalDigestFile)) {
        	Informer.getInstance().fail("cannot move " + digestFile.getAbsolutePath() + " to " + finalDigestFile.getAbsolutePath(), "moving the DIP to the DIP output dir");
        }
    }

    private void createDigestFile() throws FatalException {
		
        try {
        	FileInputStream stream = new FileInputStream(zipFile);    	
            String md5 = MessageDigestCalculator.md5(stream);
            stream.close();
            
            stream = new FileInputStream(zipFile);
            String sha1 = MessageDigestCalculator.sha1(stream);
    		stream.close();
        	
        	FileWriter out = new FileWriter(digestFile);
            out.write("# md5 sum\n");
            out.write(String.format("%s\t%s\n", md5, zipFile.getName()));
            out.write("# sha1 sum\n");
            out.write(String.format("%s\t%s\n", sha1, zipFile.getName()));
            out.write("\n");
            out.close();
        } catch (IOException e) {
            Informer.getInstance().fail("Cannot make digest file: " + digestFile.getPath(), "Dissemination SCP to output", e);        	
        }
    }


	
    private void zipUpPackage() throws FatalException {     
        byte[] buffer = new byte[1024];

        try {
            ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile));
			
            for(File file : files) {
				
                FileInputStream fis = new FileInputStream(file);
				
                // start a new zip entry
                int relativeIndex = aipDirName.length();
                String entryName = packageName + "/" + file.getPath().substring(relativeIndex);
                zos.putNextEntry(new ZipEntry(entryName));
				
                // transfer bytes from the input file to the zip file
                int len;
                while( (len = fis.read(buffer)) > 0 ){
                    zos.write(buffer, 0, len);
                }
				
                // close the zip entry
                zos.closeEntry();
				
                // close the file
                fis.close();
            }
			
            // close the zip file
            zos.close();
			
        } catch (IOException e) {
            StackTraceElement ste = new Throwable().getStackTrace()[0];
            String method = ste.getMethodName();
            Informer.getInstance().fail(this, method, "Cannot read or write file", "zipping up a dissemination", e);
        }
    }

    private List<File> find(File file) {
        List<File> total = new ArrayList<File>();
		
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                total.addAll(find(child));
            }
        } else {
            total.add(file);
        }
        return total;
    }	
}
