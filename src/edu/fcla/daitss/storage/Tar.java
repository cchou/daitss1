package edu.fcla.daitss.storage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.util.Informer;

public class Tar {
    
    private enum Operation {
        CREATE, EXTRACT
    }
    
    private File directory;
    private File file;    
    private File[] files; 
    private InputStream inputStream;
    private Operation operation;
    
    /**
     * mimic "tar -f file"
     * @param file
     */
    public Tar(File file) {
        this.file = file;
    }
    
    public void create(File...files) throws IOException, TarException {
        this.operation = Operation.CREATE;
        this.files = files;
        runTarCommand();
    }

    /**
     * analogous to tar --directory 
     * @param file
     */
    public void directory(File file) {
        directory = file;    
    }


    
    public void extract(File...files) throws IOException, TarException {
        this.operation = Operation.EXTRACT;
        this.files = files;
        runTarCommand();
    }


    private void runTarCommand() throws IOException, TarException {
        ProcessBuilder builder = new ProcessBuilder();
        
        // the base command
        builder.command().add("tar");
        
        // the file
        builder.command().add("--file");
        builder.command().add(file.getPath());

        // the operation
        switch (operation) {
        case CREATE:
            builder.command().add("--create");
            break;
            
        case EXTRACT:
        builder.command().add("--extract");
            break;            
        }
        
        // options
        if (directory != null) {
            builder.command().add("--directory");
            builder.command().add(directory.getPath());
        }
        
        // add files if any exist
        for (File file : files) {
            builder.command().add(file.getPath());
        }
        
        String cbuf = "";
        for (String piece : builder.command()) {
        	cbuf = cbuf + " " + piece;
        }
        
        Process process = builder.start();                
        
        // streams of the process
        BufferedInputStream pErrorStream = new BufferedInputStream(process.getErrorStream());
        BufferedInputStream pInputStream = new BufferedInputStream(process.getInputStream());
        
        int err;
        int out;
        
        StringBuffer errBuffer = new StringBuffer();
        StringBuffer outBuffer = new StringBuffer();
        
        try {
        	
        	if (process.waitFor() != 0) {
            	
                // tar's error stream
                if( (pErrorStream.available() > 0) && ((err = pErrorStream.read()) != -1)) {
                    errBuffer.append(err);
                }
                    
                // tar's output
                if( (pInputStream.available() > 0) && ((out = pInputStream.read()) != -1)) {
                	outBuffer.append(out);
                }
            	
                throw new TarException(cbuf + " exited with a code of " + process.exitValue() + "\n" + errBuffer + "\n" + outBuffer);
            }
        	
        } catch (InterruptedException e) {
            throw new TarException(e);
        } catch (Exception e) {
            throw new TarException(e);
        }
        
    }
}
