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

package edu.fcla.daitss;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import edu.fcla.daitss.Withdrawal.Type;
import edu.fcla.daitss.database.ArchiveDatabase;
import edu.fcla.daitss.database.DBConnection;
import edu.fcla.daitss.database.SqlQuote;
import edu.fcla.daitss.database.TransactionConnection;
import edu.fcla.daitss.entity.Event;
import edu.fcla.daitss.entity.light.DataFile;
import edu.fcla.daitss.entity.light.IntEntity;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.exception.PackageException;
import edu.fcla.daitss.service.keyserver.OIDServer;
import edu.fcla.daitss.storage.Sandbox;
import edu.fcla.daitss.storage.Tar;
import edu.fcla.daitss.storage.TarException;
import edu.fcla.daitss.util.ArchiveProperties;
import edu.fcla.daitss.util.DateTimeUtil;
import edu.fcla.daitss.util.Informer;
import edu.fcla.daitss.util.FileUtil;
import edu.fcla.jmserver.Copy;
import edu.fcla.jmserver.StorageException;

/**
 * @author franco
 * 
 * Initiate a dissemination.
 * 
 */
public class Dissemination extends ArchiveWorker {
	
	private int contactId;
	private String ieid;
	private IntEntity intEntity;
	private String account;
    private Sandbox sandbox;
	/**
	 * @param ieid
	 * @param contactId
	 * @throws UnknownPackageException 
	 * @throws PermissionException 
	 * @throws FatalException 
	 * @throws StorageException 
	 */
	public Dissemination(String ieid, int contactId) throws UnknownPackageException, PermissionException, FatalException, StorageException {
		
		setTempDir();
		
		ArchiveManager.getInstance().register(ArchiveManager.ACTION_DISSEMINATE, this);
		
		Informer.getInstance().info("Preparing dissemination of: " + ieid, "Disseminating");
		
		try {
            this.sandbox = Sandbox.createSandbox();
        } catch (IOException e) {
            Informer.getInstance().fail("Cannot create sandbox","Dissemination",e);
        }
        
		this.ieid = ieid;
		this.contactId = contactId;
		this.account = getPackageAccount();
				
		if(!doesPackageExist())
		    if (isPackageWithdrawn() ) {
		        throw new UnknownPackageException("package: " + ieid + " has been withdrawn");
		    } else { 
		        throw new UnknownPackageException("package: " + ieid + " does not exist");
		}
		
		// contact must be able to disseminate on account of ie
		if (!contactCanDisseminate(account) ) {
	          throw new PermissionException(String.format("intellectual entity %s cannot be disseminatied by contact %s: permission denied", ieid, contactId));
		}
		
		intEntity = new IntEntity(ieid);
		
		if (!haveAnOnlineCopy()) {
		    throw new StorageException("No online copies available");
		}
	}
	
	private boolean haveAnOnlineCopy() throws StorageException {
	    boolean online = false;
	    for(Copy copy : intEntity.copies()) {
	        online = copy.exists() || online;
	    }
        return online;
    }
	
    private String getPackageAccount() throws FatalException, PermissionException {
		String account = null;
		try {
			DBConnection con = DBConnection.getConnection();
			String sql = String.format(
					"select ap.%s from %s a, %s ap where a.%s = %s and ap.%s = a.%s", 
					ArchiveDatabase.COL_ACCOUNT_PROJECT_ACCOUNT,
					ArchiveDatabase.TABLE_ADMIN,
					ArchiveDatabase.TABLE_ACCOUNT_PROJECT,
					ArchiveDatabase.COL_ADMIN_OID,
					SqlQuote.escapeString(ieid),
					ArchiveDatabase.COL_ACCOUNT_PROJECT_ID,
					ArchiveDatabase.COL_ADMIN_ACCOUNT_PROJECT);

			ResultSet set = con.executeQuery(sql);
			if(set.first()){
				account = set.getString(ArchiveDatabase.COL_ACCOUNT_PROJECT_ACCOUNT);
			} else {
				throw new PermissionException("No account associated with int entity " + ieid);
			}
			con.close();
		} catch (SQLException e) {
			Informer.getInstance().fail("Cannot select account for int entity: " + ieid,"Withdrawing",e);
		}
		return account;
	}
	
	private boolean contactCanDisseminate(String account) throws FatalException {
		boolean canDisseminate = false;
		try {
			DBConnection con = DBConnection.getConnection();
			String sql = String.format("select %s from %s where %s = %s and %s = %s", 
					ArchiveDatabase.COL_OUTPUT_REQUEST_CAN_REQUEST_DISSEMINATION,
					ArchiveDatabase.TABLE_OUTPUT_REQUEST,
					ArchiveDatabase.COL_OUTPUT_REQUEST_CONTACT,
					contactId,
					ArchiveDatabase.COL_OUTPUT_REQUEST_ACCOUNT,
					SqlQuote.escapeString(account));
			ResultSet set = con.executeQuery(sql);
			canDisseminate = set.first();
			set.close();
			con.close();
		} catch (SQLException e) {
			Informer.getInstance().fail("Cannot select permissions for contact id: " + contactId, "Disseminating",	e);
		}
		return canDisseminate;
	}

	
	private boolean doesPackageExist() throws FatalException {
        boolean hasArecord = false;
        try {			
			DBConnection conn = DBConnection.getConnection();
			String sql = String.format(
					"SELECT * FROM %s WHERE %s = %s",
					ArchiveDatabase.TABLE_INT_ENTITY,
					ArchiveDatabase.COL_INT_ENTITY_IEID, 
					SqlQuote.escapeString(ieid));
			ResultSet r = conn.executeQuery(sql);
            hasArecord = r.first();
			r.close();
			conn.close();
		} catch (SQLException e) {
			Informer.getInstance().fail("Cannot select package existance for " + ieid, "Dissemination", e);
		}
		return hasArecord;
	}

    private boolean isPackageWithdrawn() throws FatalException {
        boolean hasARecord = false;
        try {
            
            DBConnection conn = DBConnection.getConnection();
            String sql = String.format(
                            "SELECT * FROM %s WHERE %s = %s AND (%s = %s OR %s = %s)",
                            ArchiveDatabase.TABLE_EVENT,
                            ArchiveDatabase.COL_EVENT_OID, SqlQuote.escapeString(ieid),
                            ArchiveDatabase.COL_EVENT_EVENT_TYPE, SqlQuote.escapeString(Type.archive.getCode()),
                            ArchiveDatabase.COL_EVENT_EVENT_TYPE, SqlQuote.escapeString(Type.owner.getCode()));
            ResultSet r = conn.executeQuery(sql);
            hasARecord = r.first();
            r.close();            
            conn.close();            
        } catch (SQLException e) {
            Informer.getInstance().fail("Cannot select package existance for " + ieid, "Dissemination", e);
        }
        return hasARecord;
    }
	
	
	/**
	 * @param args
	 * @throws FatalException 
	 */
	public static void main(String[] args) throws FatalException {
		
		// process parameters
		String ieid = System.getProperty("daitss.disseminate.ieid");
		int contactId = Integer.parseInt(System.getProperty("daitss.disseminate.contactId"));

		try {
			
			// create the dissemination
			Dissemination d = null;
			try {
			    d = new Dissemination(ieid, contactId);
			} catch (UnknownPackageException e) {
				Informer.getInstance().error("Package does not exist: " + ieid, "Disseminating", e);
			} catch (PermissionException e) {
				Informer.getInstance().error("Contact id does not have permission to disseminate:" + contactId, "Disseminating", e);
			} catch (StorageException e) {
				e.printStackTrace();
	             Informer.getInstance().error("No online Silos", "Disseminating", e);
            }
			
			// disseminate
			d.process();
			
		} catch (FatalException e) {
			 //exit with system error
			System.err.println("Fatal Error:" + e.getMessage());
			e.printStackTrace();
			System.exit(2);
		} catch (PackageException e) {
			 // exit with package error
			System.err.println("Package Error:" + e.getMessage());
			System.exit(1);
		}		

		// log successfull ingest
		Informer.getInstance().info("Dissemination successfull: " + ieid,"Disseminating");
		
		// exit gracefully
		System.exit(0);
	}

	/**
	 * Execute the dissemination
	 * 
	 * @throws FatalException
	 */
	@Override
    public void process() throws FatalException {		
	    getFilesFromStorage();
	    getGlobalFilesFromFileSystem();
	    moveFilesToReingestDir();
	    recordEvent();
	    
	    sandbox.cleanup();
	    sandbox.delete();
	}	
	
	private void recordEvent() throws FatalException {
        TransactionConnection scon = DBConnection.getSharedConnection();
                
        Map<String, String> m = new Hashtable<String, String>();        
        m.put(ArchiveDatabase.COL_EVENT_ID, Long.toString(OIDServer.getNewEventId()));
        m.put(ArchiveDatabase.COL_EVENT_OID, ieid);
        m.put(ArchiveDatabase.COL_EVENT_EVENT_TYPE, Event.EVENT_DISSEMINATED);
        m.put(ArchiveDatabase.COL_EVENT_DATE_TIME, DateTimeUtil.now());
        m.put(ArchiveDatabase.COL_EVENT_EVENT_PROCEDURE, "AIP dissemination");
        m.put(ArchiveDatabase.COL_EVENT_OUTCOME, Event.OUTCOME_SUCCESS);
        m.put(ArchiveDatabase.COL_EVENT_NOTE, String.format("Dissemination of %s in account %s requested by %s", ieid, account, getContactEmail()));

        Vector<String> cols = new Vector<String>(m.size());
        Vector<String> vals = new Vector<String>(m.size());
        for (String col : m.keySet()) {
            cols.add(col);
            vals.add(m.get(col));
        }
        scon.insert(ArchiveDatabase.TABLE_EVENT, cols, vals);
                  
        scon.commitTransaction();
        try {
            scon.close();
        } catch (SQLException e) {
            Informer.getInstance().fail("Database error, closing connection", "Disseminating", e);
        }
    }

    private void moveFilesToReingestDir() throws FatalException {
        Informer.getInstance().info("Queueing for re-ingest: " + ieid, "Disseminating");
        File reingestDir = new File(ArchiveProperties.getInstance().getArchProperty("REINGEST_INPATH"), intEntity.getPackageName());
        if (!reingestDir.mkdir()) {
            Informer.getInstance().fail(String.format("cannot mkdir %s", reingestDir.getAbsolutePath()), "Dissemination");
        }
        for(File src : sandbox.listFiles()) {
            File dest = new File(reingestDir, src.getName());
            try {            		
            		FileUtil.copy(src.getPath(), dest.getPath());
            		FileUtil.delete(src.getPath());
                }
            catch(IOException e) {
            	Informer.getInstance().fail(String.format("cannot move %s to %s", src, dest), "Dissemination");
            }            
        }
    }

    private Collection<? extends File> getGlobalFilesFromFileSystem() throws FatalException {
        Vector<File> files = new Vector<File>();
        File globalsDir = new File(ArchiveProperties.getInstance().getArchProperty("LOCAL_GLOBAL_DIR"));
        for (DataFile globalFile : intEntity.getGlobalFiles()) {
            String gfpid = globalFile.getIeid();
            String path  = globalFile.getPath();            
            File src = new File(new File(globalsDir, gfpid), path);            
            File dest = new File(new File(sandbox, gfpid), path);
            
            try {
                copy(src, dest);
            } catch (IOException e) {
                Informer.getInstance().fail(String.format("cannot copy %s to %s", src, dest), "Dissemination", e);
            }
            
            files.add(dest);
        }                
        
        return files;
    }

    private void copy(File src, File dest) throws IOException {
        if(dest.getParentFile().isDirectory() || dest.getParentFile().mkdirs()) {            
            FileChannel srcChannel = new FileInputStream(src).getChannel();
            FileChannel destChannel = new FileOutputStream(dest).getChannel();
            destChannel.transferFrom(srcChannel, 0, srcChannel.size());
        } else {
            throw new IOException("cannot make directory " + dest.getParent());
        }
    }

    private void getFilesFromStorage() throws FatalException {
        Informer.getInstance().info(String.format("Getting package %s from storage", intEntity.getIeid()), "Disseminating");
        for(Copy copy : intEntity.copies()) {
            try {
            	// write it to a temp file
            	BufferedInputStream inputStream = new BufferedInputStream(copy.get());
            	File tempFile = File.createTempFile("dissemination.", ".tar");
            	FileOutputStream outputStream = new FileOutputStream(tempFile);
            	
            	int bytesRead;
            	byte[] buffer = new byte[1024];
            	while((bytesRead = inputStream.read(buffer)) != -1) {
            		outputStream.write(buffer, 0, bytesRead);
            	}
            	outputStream.close();
            	inputStream.close();
            	
            	
                Tar tarball = new Tar(tempFile);
                tarball.directory(sandbox);            

                tarball.extract();
                sanityCheckFiles();
                tempFile.delete();
            } catch (FileNotFoundException e) {
                if (intEntity.copies().lastIndexOf(copy) < (intEntity.copies().size() - 1)) {
                    Informer.getInstance().info(String.format("Datafile %s is not present in copy %s, trying next copy", e.getMessage(), copy), "Disseminating");                
                    continue;
                } else {
                    Informer.getInstance().fail(String.format("All copies of %s do not pass sanity check", intEntity.getIeid()), "Disseminating", new FatalException());        
                }                
            } catch (IOException e) {
                Informer.getInstance().fail(String.format("Problem untarring copy %s", copy), "Disseminating", e);           
            } catch (TarException e) {
                Informer.getInstance().fail(String.format("Problem untarring copy %s", copy), "Disseminating", e);
            } catch (StorageException e) {
                Informer.getInstance().fail(String.format("Problem untarring copy %s", copy), "Disseminating", e);
            }            
        }
        Informer.getInstance().info(String.format("Package %s retrieved from storage", intEntity.getIeid()), "Disseminating");
    }

    private void sanityCheckFiles() throws FileNotFoundException {
        for(DataFile f : intEntity.getDataFiles()) {
            File parent = new File(sandbox, intEntity.getIeid());
            File file = new File(parent, f.getPath());
            if (!file.exists())
                throw new FileNotFoundException(f.getPath());
        }
    }

    /**
	 * Method to handle Dissemination error (when PackageException is encountered)
	 */
	@Override
    public void error() {
	    // Intelligent Design
	}
	
	/** 
	 * Method to handle Dissemination program failure (when a 
	 * FatalException is encountered).
	 */	
	@Override
    public void fail() {
		// stop everything
		System.exit(1);		
	}	

	/**
	 * @return
	 * @throws FatalException 
	 * @throws SQLException 
	 */
	private String getContactEmail() throws FatalException {
		String sql = String.format("select %s from %s where %s=%s", 
				ArchiveDatabase.COL_CONTACT_EMAIL, 
				ArchiveDatabase.TABLE_CONTACT,
				ArchiveDatabase.COL_CONTACT_ID,
				SqlQuote.escapeInt(contactId));
		
		DBConnection con = DBConnection.getConnection();
		
		String email = null;
		StackTraceElement element = new Throwable().getStackTrace()[0];
		try {
			ResultSet set = con.executeQuery(sql);
			if (set.first()) {
				email = set.getString(ArchiveDatabase.COL_CONTACT_EMAIL);
			} else {
				Informer.getInstance().fail(this, 
						element.getMethodName(), 
						"Email of contact with ID " + contactId + " does not exist", 
						"Selecting contact's email", 
						null);
			}
			set.close();
			con.close();
		} catch (SQLException e) {
			Informer.getInstance().fail(this, 
					element.getMethodName(), 
					"Cannot select the email of a contact", 
					"Selecting contact's email", 
					null);
		}
		return email;
	}

}
