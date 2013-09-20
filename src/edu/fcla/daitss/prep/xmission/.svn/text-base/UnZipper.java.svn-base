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
package edu.fcla.daitss.prep.xmission;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * Unpacks PK-Zip packages.
 * 
 * @author franco
 * 
 */
public class UnZipper implements Unpacker {

	private int bufferSize = 8 * 1024;

	public File unpack(File pkg) throws ZipException, IOException {
		ZipFile zip = new ZipFile(pkg);

		for (Enumeration entries = zip.entries(); entries.hasMoreElements();) {

			// current zip entry
			ZipEntry entry = (ZipEntry) entries.nextElement();

			// file to output this entry
			File outFile = new File(pkg.getParent(), entry.getName());

			if (entry.isDirectory()) {
				// create this directory
				outFile.mkdirs();
			} else {
				// input channel
				ReadableByteChannel ic = Channels
						.newChannel(new BufferedInputStream(zip
								.getInputStream(entry), bufferSize));

				// output channel
				FileChannel oc = new FileOutputStream(outFile).getChannel();

				// transfer from the zip file to the real file
				oc.transferFrom(ic, 0, entry.getSize());

				// close channels
				ic.close();
				oc.close();
			}
		}

		int extIndex = pkg.getName().lastIndexOf(".zip");
		String packageName = pkg.getName().substring(0, extIndex);
		File packageDir = new File(pkg.getParent(), packageName);
		return packageDir;
	}
}
