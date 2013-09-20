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

import java.io.File;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.exception.PackageException;
import edu.fcla.daitss.format.markup.XPaths;
import edu.fcla.daitss.util.ArchiveProperties;
import edu.fcla.daitss.util.Informer;

/**
 * @author franco
 * Asserts that a daitss:AGREEMENT_INFO element exists in the descriptor.
 * If:
 * 	AGREEMENT_INFO/ACCOUNT and AGREEMENT_INFO/PROJECT exist than process completes normally
 * else if
 * 	Archive property (boolean) SUPPLY_ACCOUNT_INFO is true insert the default account information
 * else
 * 	reject the package
 */
public class AccountInfoFilter extends Filter {

	boolean shouldCreateAinfo;
	private String account;
	private String project;
	
	/**
	 * @throws FatalException
	 */
	public AccountInfoFilter() {
		account = System.getProperty("prep.account");
		project = System.getProperty("prep.project");
	}
	
	public void process(File pkg) throws FatalException, PackageException {
		
		// get descriptor
		File descriptor = getDescriptor(pkg);
		Document doc = parseXml(descriptor);
		Element aInfo = (Element) XPaths.selectSingleNode(doc, "//daitss:AGREEMENT_INFO");
		
		if (aInfo == null) {
			if (account != null && project != null) {
				insertAgreementInfo(doc);
				saveDescriptorXml(pkg, doc);
			} else {
	            Informer.getInstance().error(
	            		this,
	            		new Throwable().getStackTrace()[0].getMethodName(),
	            		"Agreement info now found, inserting default agreement info.",
	            		"Checking if packages have account info.",
	            		new PackageException());					
			}
		} else {
			String account = aInfo.getAttribute("ACCOUNT");
			String project = aInfo.getAttribute("PROJECT");
			if (account.trim().length() == 0 || project.trim().length() == 0) {
	            Informer.getInstance().error(
	            		this,
	            		new Throwable().getStackTrace()[0].getMethodName(),
	            		"incomplete agreement info.",
	            		"Checking if packages have account info.",
	            		new PackageException());
			}
		}
	}

	private void insertAgreementInfo(Document desc) throws FatalException {
		
		ArchiveProperties p = ArchiveProperties.getInstance();

		// namespaces
		String metsNs = p.getArchProperty("NS_METS");
		String daitssNs = p.getArchProperty("NS_DAITSS");
		
		// <amdSec>
		Element amdSec = desc.createElementNS(metsNs, "amdSec");
		  
		// <digiprovMD ID="DPMD1">
		Element digiprovMD = (Element) amdSec.appendChild(desc.createElementNS(metsNs, "digiprovMD"));
		digiprovMD.setAttribute("ID", "DAITSS_AGREEMENT_INFO");
		
		// <mdWrap MDTYPE="OTHER">
		Element mdWrap = (Element) digiprovMD.appendChild(desc.createElementNS(metsNs, "mdWrap"));
		mdWrap.setAttribute("MDTYPE", "OTHER");
		
		// <xmlData>
		Element xmlData = (Element) mdWrap.appendChild(desc.createElementNS(metsNs, "xmlData"));
		
		// <daitss xmlns="http://www.fcla.edu/dls/md/daitss/">
		Element daitss = (Element) xmlData.appendChild(desc.createElementNS(daitssNs, "daitss"));
		
		// <AGREEMENT_INFO ACCOUNT="FDA" SUB_ACCOUNT="FDA" PROJECT="DART"/>
		Element agreementInfo = (Element) daitss.appendChild(desc.createElementNS(daitssNs, "AGREEMENT_INFO"));
		agreementInfo.setAttribute("ACCOUNT", account);
		agreementInfo.setAttribute("PROJECT", project);
		
		// insertion point either after the last dmdSec or after the mets header or just under mets		
		Node insertionPoint;
		if ( (insertionPoint = XPaths.selectSingleNode(desc, "/mets:mets/mets:dmdSec[last()]")) != null || 
				(insertionPoint = XPaths.selectSingleNode(desc, "/mets:mets/mets:metsHdr")) != null) {
			
			// get the next sibling so insertion takes place between this and its sibling
			Node sibling = insertionPoint.getNextSibling();
			while(!(sibling instanceof Element)) {
				sibling = sibling.getNextSibling();
			}
			insertionPoint = sibling;
		} else {
			Element mets = (Element) XPaths.selectSingleNode(desc, "/mets:mets");
			
			// get the first Element child of mets.
			Node child = mets.getFirstChild();
			while (!(child instanceof Element)) {
				child = child.getNextSibling();
			}
			insertionPoint = child;
		}
		
		// insert the amd section before the insertion point
		desc.getDocumentElement().insertBefore(amdSec, insertionPoint);
	}

}
