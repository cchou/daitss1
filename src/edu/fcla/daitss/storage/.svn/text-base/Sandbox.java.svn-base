package edu.fcla.daitss.storage;

import java.io.File;
import java.io.IOException;

@SuppressWarnings("serial")
public class Sandbox extends File {
        
    public Sandbox(File root) {
        super(root.getPath());
    }

    public boolean cleanup() {
        boolean success = true;
        for (File f : listFiles())
            success = removeRecursive(f) && success;
        return success;
    }
    
    private static boolean removeRecursive(File f) {        
        if (f.isDirectory())
            for(File child : f.listFiles())                
                removeRecursive(child);
        return f.delete();
    }
    
    public static Sandbox createSandbox() throws IOException {
        File root = File.createTempFile("sandbox", "testdir");
        if (!root.delete() || !root.mkdir() ) {
            throw new IOException("Cannot create sandbox at " + root.getAbsolutePath());
        }
        return new Sandbox(root);
    }    
}
