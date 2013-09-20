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
package edu.fcla.daitss.service.keyserver;

import java.rmi.Remote;
import java.rmi.RemoteException;
import edu.fcla.daitss.exception.FatalException;

/**
 * @author althea Liang
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface KeyServer extends Remote{

		public abstract String getNextDfid() throws RemoteException, FatalException;
		/*FatalException is used to log failures in the local log by calling Informer.fail()*/
		public abstract String getNextIeid() throws RemoteException, FatalException;
		public abstract String getNextEntityId() throws RemoteException, FatalException;
		public abstract long getNextEventId() throws RemoteException, FatalException;
		
		// these methods need to be static in the OIDServer, but this cannot
		// be enforced in an interface, so these sigs have been removed from the 
		// interface
		 
		//public abstract boolean isValidBSID(String leadChar) throws RemoteException;
		//public abstract boolean isValidDFID(String leadChar) throws RemoteException; 
		//public abstract boolean isValidIEID(String leadChar) throws RemoteException;
		//public abstract boolean isValidEntityID(String leadChar) throws RemoteException;

}
