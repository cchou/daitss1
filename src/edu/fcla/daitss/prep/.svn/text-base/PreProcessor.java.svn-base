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
 * Created on Nov 9, 2004
 */
package edu.fcla.daitss.prep;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import edu.fcla.daitss.ArchiveManager;
import edu.fcla.daitss.ArchiveWorker;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.exception.PackageException;
import edu.fcla.daitss.prep.xmission.UnZipper;
import edu.fcla.daitss.prep.xmission.Unpacker;
import edu.fcla.daitss.util.ArchiveProperties;
import edu.fcla.daitss.util.FileUtil;
import edu.fcla.daitss.util.Informer;

/**
 * Asserts certain conditions of packages are met, otherwise a package is
 * rejected
 * 
 * @author franco
 */
public class PreProcessor extends ArchiveWorker {

	/**
	 * @param args
	 * @throws FatalException
	 */
	public static void main(String[] args) throws FatalException {
		PreProcessor p = new PreProcessor();

		/* Register with ArchiveManager (thereby initializing log) */
		ArchiveManager.getInstance().register(ArchiveManager.ACTION_PREPROCESS, p);

		// account <=> project 
		if ((System.getProperty("prep.account") != null) != (System.getProperty("prep.project") != null)) {
			StackTraceElement ste = new Throwable().getStackTrace()[0];
			Informer.getInstance().fail(ste.getClassName(), ste.getMethodName(), "if either specified account and project must exist", "Preprocessing", null);			
		}

		// descriptor => (account <=> project)
		if ((System.getProperty("prep.make.descriptor") != null) && 
				((System.getProperty("prep.account") == null) || (System.getProperty("prep.project") == null)) ) {
			StackTraceElement ste = new Throwable().getStackTrace()[0];
			Informer.getInstance().fail(ste.getClassName(), ste.getMethodName(), "if creating descriptors account and project must exist", "Preprocessing", null);

		}

		p.process();
	}

	private List<Filter> filters;

	private File inDir;

	private File outDir;

	private List<File> packages;

	private File rejectDir;

	private File workDir;

	private Map<String, Unpacker> extensionsToUnpackers;

	/**
	 * @throws FatalException
	 */
	public PreProcessor() throws FatalException {

		setTempDir();
		
		/* Archive properties */
		ArchiveProperties ap = ArchiveProperties.getInstance();

		/* Set the in directory */
		inDir = new File(ap.getArchProperty("PREPROCESSOR_INPATH"));

		/* Set the work directory */
		workDir = new File(ap.getArchProperty("PREPROCESSOR_WORKPATH"));

		/* Set the out directory */
		outDir = new File(ap.getArchProperty("PREPROCESSOR_OUTPATH"));

		/* Set the reject directory */
		rejectDir = new File(ap.getArchProperty("PREPROCESSOR_REJECTPATH"));

		/* make the filters */
		filters = new Vector<Filter>();

		/* verify a package is not empty */
		filters.add(new EmptyPackageFilter());

		/* verify a package has content */
		filters.add(new ContentExistsFilter());

		/*
		 * verify descriptors named dataset.xml get renamed to the standard
		 * descriptor convention
		 */
		filters.add(new DescriptorNameFilter("dataset.xml"));

		/* verify or assert a descriptor exists */
		filters.add(new DescriptorExistenceFilter());

		/* verify or assert the descriptor is an instance of mets */
		filters.add(new DescriptorFormatFilter());

		/* verify or assert the package only contains files it describes 
		 * We only want to prune files if we have a configuration directive to do so
		 * */
		if(System.getProperty("prep.prune.undescribed.files").compareTo("true") == 0) {
			filters.add(new FilePruner());
		}

		/* verify message digests have the proper code */
		filters.add(new MessageDigestTypeFilter());
		
		/* verify or assert the descriptor has propper account info */
		filters.add(new AccountInfoFilter());
		
		/* define the acceptable transmission format extensions */
		extensionsToUnpackers = new Hashtable<String, Unpacker>();
		extensionsToUnpackers.put("zip", new UnZipper());
	}

	/**
	 * To be called when a PackageException is reached. Used for
	 * package-specific cleanup.
	 */
	public void error() {
		// not used yet because there are no parms in the interface
	}

	/**
	 * To be called when a FatalException is reached. Used for general cleanup.
	 */
	public void fail() {
		/* exit the system */
		System.err.println("Premature exit of preprocessor");
		System.exit(1);
	}

	/**
	 * @throws FatalException
	 */
	private void makePackageList() throws FatalException {

		packages = new Vector<File>();

		// enqueue all potential packages in a Vector
		for (File file : inDir.listFiles()) {

			String ext = getExtension(file);

			if (file.isDirectory()) {
				// enqueue file in
				packages.add(file);
			} else if (extensionsToUnpackers.keySet().contains(ext)
					&& file.isFile()) {
				try {
					// the packed file
					File packedPkg = file;

					// unpack the package
					file = extensionsToUnpackers.get(ext).unpack(file);

					// delete the original zip file
					packedPkg.delete();
				} catch (IOException e) {
					// inform that current file is an unacceptable package
					// transmission
					Informer.getInstance().fail(
						this,
						new Throwable().getStackTrace()[0].getMethodName(),
						file.getAbsolutePath() + " is not an invalid method to transfer a package.",
						"selecting packages for pre processing", 
						e);
				}
				// enqueue the file
				packages.add(file);
			} else {
				// inform that current file is an unacceptable package
				Informer.getInstance().warning(
					this,
					new Throwable().getStackTrace()[0].getMethodName(),
					file.getAbsolutePath() + "  is not an acceptable package and will be ignored",
					"selecting packages for pre processing");
			}
		}
	}

	private String getExtension(File file) {
		String name = file.getName();
		int dotIndex = name.lastIndexOf(".");
		int length = name.length();
		return name.substring(dotIndex + 1, length);
	}

	/**
	 * Filter all packages in the input directory. If a package passes all
	 * filters then move it to the output directory, else move it to the reject
	 * directory.
	 * 
	 * @throws FatalException
	 */
	public void process() throws FatalException {
		String methodName = new Throwable().getStackTrace()[0].getMethodName();

		// a list of packages
		makePackageList();

		
		// filter all packages and move them to the appropiate place.
		for (File inPkg : packages) {

			/* Informing */
			Informer.getInstance().info(
					this,
					new Throwable().getStackTrace()[0].getMethodName(),
					"Starting preprocessing of package: " + inPkg.getAbsolutePath(), "Preprocessing", false);

			// Copy from the in directory to the work directory
			File workPkg = transactionCopyFile(inPkg, workDir);

			try {

				// process this package through all the filters
				for (Filter filter : filters) {
					filter.process(workPkg);
				}

				// destination is the output directory
				File outPkg = transactionMoveFile(workPkg, outDir);

				// delete package from in dir				
				deleteFile(inPkg);
				
				// report success
				Informer.getInstance().info(
						this,
						methodName,
						outPkg.getName() + " successfully pre-processed",
						"Preprocessing.",
						false);

			} catch (PackageException e) {
				
				// move package reject dir
				File outPkg = transactionMoveFile(workPkg, rejectDir);

				// delete package from in dir				
				deleteFile(inPkg);
				
				// report failure
				Informer.getInstance().warning(
						this,
						new Throwable().getStackTrace()[0].getMethodName(),
						"Rejecting package: " + outPkg.getName(),
						"Preprocessing");

				error();
			} catch (FatalException e) {

				// delete package from work dir				
				deleteFile(workPkg);
				
				// roll back the package
				Informer.getInstance().info(
						this, 
						methodName,
						workPkg.getName() + " Rolling back pre processing. " + e.getMessage(),
						"Preprocessing.", 
						false);

				
				fail();
			}
		}
	}

	private File transactionCopyFile(File source, File destDir)
			throws FatalException {
		String methodName = "transactionMoveFile(File, File)";

		/* New file after the move. */
		File destFile = new File(destDir, source.getName());

		/* Path of the new file. */
		String destPath = destFile.getAbsolutePath();

		/* Path of the file to move. */
		String sourcePath = source.getAbsolutePath();

		/* Transact. */
		try {
			/* Copy the file. */
			FileUtil.copy(sourcePath, destPath);
		} catch (IOException e) {
			/* rollback the transaction */
			try {
				FileUtil.delete(destFile.getAbsolutePath());
			} catch (IOException e1) {
				Informer.getInstance().fail(this, methodName,
						"Cannot delete file", destFile.getAbsolutePath(), e);
			}
			Informer.getInstance().fail(this, methodName,
					"Transactionally copying file: " + destPath,
					"Rolling back transaction, Cannot copy file.", e);
		}
		return destFile;
	}

	/**
	 * @param source
	 * @param destDir
	 * @return the file that was moved
	 * @throws FatalException
	 */
	private File transactionMoveFile(File source, File destDir)
			throws FatalException {
		String methodName = "transactionMoveFile(File, File)";

		/* New file after the move. */
		File destFile = new File(destDir, source.getName());

		/* Path of the new file. */
		String destPath = destFile.getAbsolutePath();

		/* Path of the file to move. */
		String sourcePath = source.getAbsolutePath();

		/* Transact. */
		try {
			/* Copy the file. */
			FileUtil.copy(sourcePath, destPath);
		} catch (IOException e) {
			/* rollback the transaction */
			try {
				FileUtil.delete(destFile.getAbsolutePath());
			} catch (IOException e1) {
				Informer.getInstance().fail(this, methodName,
						"Transactionally moving file: " + destPath,
						"Rolling back transaction, Cannot delete file.", e1);
			}
			Informer.getInstance().fail(this, methodName,
					"Transactionally moving file: " + destPath,
					"Rolling back transaction, Cannot move file.", e);
		} finally {
			/* complete the transaction */
			try {
				FileUtil.delete(source.getAbsolutePath());
			} catch (IOException e) {
				Informer.getInstance().fail(this, methodName,
						"Transactionally moving file: " + sourcePath,
						"Rolling back transaction, Cannot delete file.", e);
			}
		}

		return destFile;
	}
	
	private void deleteFile(File f) throws FatalException {
		try {
			FileUtil.delete(f.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
			Informer.getInstance().fail(
					this,
					new Throwable().getStackTrace()[0].getMethodName(),
					"Cannot delete package: " + f.getName() + " " + e.getMessage(),
					"Preprocessing",
					e);

		}
	}
}
