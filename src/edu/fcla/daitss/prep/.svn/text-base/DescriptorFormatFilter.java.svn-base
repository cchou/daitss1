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
package edu.fcla.daitss.prep;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import edu.fcla.da.xml.WebCache;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.exception.PackageException;
import edu.fcla.daitss.util.ArchiveProperties;
import edu.fcla.daitss.util.Informer;
import edu.fcla.mxf2mets.MXF2METS;

/**
 * Asserts the format of a package's descriptor is in METS.
 * @author franco
 */
public class DescriptorFormatFilter extends Filter {


    private static final String MXF_DTD_URL = "http://palmm.fcla.edu/strucmeta/MXF.dtd";

        private MXF2METS converter;

        private WebCache webCache;

    /**
     * @throws FatalException
     *
     */
    public DescriptorFormatFilter() throws FatalException {

        // a webcache to access downloads
        File webCacheDir = new File(ArchiveProperties.getInstance().getArchProperty("WEBCACHE_DIR"));

        try {
                        webCache = new WebCache(webCacheDir);
                } catch (IOException e) {
                        Informer.getInstance().fail("cannot initialize webcache", "constructing FormatFilter", e);
                }

        /* make an mxf to mets converter */
        try {
            converter = new MXF2METS();
        } catch (Exception e) {
            String methodName = new Throwable().getStackTrace()[0].getMethodName();
            String message = "Cannot conrstuct an MXF to METS converter";
            String subject = "Descriptor Formatting";
            Informer.getInstance().fail(this, methodName, message, subject , e);
        }
    }


    public void process(File pkg) throws PackageException, FatalException {
        File descriptor = getDescriptor(pkg);

        /* make sure we have a descriptor to work on */
        assertDescriptorExists(pkg);

        try {
            Document document = parseXML(descriptor, true);

            Element root = document.getDocumentElement();

            /* descriptor's identification */
            String name = root.getTagName();

            boolean isMets = name.equals("mets") || name.endsWith(":mets");

            boolean isMXF = name.equals("MXF");

            if (isMets) {
                /* nothing to do, pat on the back */
                Informer.getInstance().info(
                                this,
                                new Throwable().getStackTrace()[0].getMethodName(),
                                "METS descriptor found: " + descriptor.getAbsolutePath(),
                                "Asserting that packages are mets described.",
                                false);

            } else if (isMXF) {
                Informer.getInstance().info(
                                this,
                                new Throwable().getStackTrace()[0].getMethodName(),
                                "MXF descriptor found: " + descriptor.getAbsolutePath() + "; converting to METS",
                                "Asserting that packages are mets described.",
                                false);

                /* assert MXF.dtd exists */
                assertMXFDTDExists(pkg);

                /*
                 * convert mxf to mets, converter will automatically rename
                 * the extension of the mxf file to mxf.
                 */
                try {
                    converter.convert(descriptor.getAbsolutePath());
                } catch (Exception e) {

                        /* bad MXF conversion */
                    Informer.getInstance().error(
                                "Cannot convert mxf to mets for file: " + descriptor.getAbsolutePath(),
                                "Asserting that packages are mets described.",
                                e);
                }
            } else {
                Informer.getInstance().error(
                                this,
                                new Throwable().getStackTrace()[0].getMethodName(),
                                "Unknown xml package descriptor, descriptor will not be generated.",
                                "Checking if packages have a descriptor.",
                                new PackageException());
            }
        } catch (SAXException e) {
            Informer.getInstance().error(
                        this,
                        new Throwable().getStackTrace()[0].getMethodName(),
                        "Unknown non-xml package descriptor, descriptor will not be generated.",
                        "Checking if packages have a descriptor.",
                        new PackageException());
                }
    }


        private void assertMXFDTDExists(File pkg) throws FatalException, PackageException {
                File p = findDescriptor(pkg, pkg.getName() + ".xml").getParentFile();
                File dtd = new File(p, "MXF.dtd");
                if (!dtd.exists()) {
                        // get the URL of the DTD
                        try {
                                Informer.getInstance().info(this,
                                                new Throwable().getStackTrace()[0].getMethodName(),
                                                "DTD not found: " + dtd.getAbsolutePath(),
                                                "Preprocessing",
                                                false);
                                downloadFileFromUrl(dtd, new URL(MXF_DTD_URL));
                        } catch (MalformedURLException e) {
                Informer.getInstance().fail(
                                this,
                                new Throwable().getStackTrace()[0].getMethodName(),
                                "Unknown non-xml package descriptor, descriptor will not be generated.",
                                "Checking if packages have a descriptor.",
                                new PackageException());
                        } catch (IOException e) {
                Informer.getInstance().error(
                                this,
                                new Throwable().getStackTrace()[0].getMethodName(),
                                "Cannot download mxf.dtd from: " + MXF_DTD_URL,
                                "Downloading MXF.dtd",
                                new PackageException());
                        }
                }
        }

   private void downloadFileFromUrl(File file, URL url) throws IOException, FatalException {
                Informer.getInstance().info(this,
                                new Throwable().getStackTrace()[0].getMethodName(),
                                "Attempting to download " + file.getAbsolutePath() + " from " + url.toString(),
                                "Preprocessing",
                                false);

        // Make the data input stream from the web cache.
                BufferedInputStream is = new BufferedInputStream(webCache.get(url));

        // Make the output stream.
        FileOutputStream os = new FileOutputStream(file);

        // Write the data to a file.
        int buffer;
        while ((buffer = is.read()) != -1) {
            os.write(buffer);
        }
        os.close();
        is.close();
    }
}
