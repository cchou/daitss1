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
// Stub class generated by rmic, do not edit.
// Contents subject to change without notice.

package edu.fcla.daitss.service.keyserver;

public final class OIDServer_Stub
    extends java.rmi.server.RemoteStub
    implements edu.fcla.daitss.service.keyserver.KeyServer, java.rmi.Remote
{
    private static final long serialVersionUID = 2;
    
    private static java.lang.reflect.Method $method_getNextDfid_0;
    private static java.lang.reflect.Method $method_getNextEntityId_1;
    private static java.lang.reflect.Method $method_getNextEventId_2;
    private static java.lang.reflect.Method $method_getNextIeid_3;
    
    static {
	try {
	    $method_getNextDfid_0 = edu.fcla.daitss.service.keyserver.KeyServer.class.getMethod("getNextDfid", new java.lang.Class[] {});
	    $method_getNextEntityId_1 = edu.fcla.daitss.service.keyserver.KeyServer.class.getMethod("getNextEntityId", new java.lang.Class[] {});
	    $method_getNextEventId_2 = edu.fcla.daitss.service.keyserver.KeyServer.class.getMethod("getNextEventId", new java.lang.Class[] {});
	    $method_getNextIeid_3 = edu.fcla.daitss.service.keyserver.KeyServer.class.getMethod("getNextIeid", new java.lang.Class[] {});
	} catch (java.lang.NoSuchMethodException e) {
	    throw new java.lang.NoSuchMethodError(
		"stub class initialization failed");
	}
    }
    
    // constructors
    public OIDServer_Stub(java.rmi.server.RemoteRef ref) {
	super(ref);
    }
    
    // methods from remote interfaces
    
    // implementation of getNextDfid()
    public java.lang.String getNextDfid()
	throws edu.fcla.daitss.exception.FatalException, java.rmi.RemoteException
    {
	try {
	    Object $result = ref.invoke(this, $method_getNextDfid_0, null, -4989376790331703387L);
	    return ((java.lang.String) $result);
	} catch (java.lang.RuntimeException e) {
	    throw e;
	} catch (java.rmi.RemoteException e) {
	    throw e;
	} catch (edu.fcla.daitss.exception.FatalException e) {
	    throw e;
	} catch (java.lang.Exception e) {
	    throw new java.rmi.UnexpectedException("undeclared checked exception", e);
	}
    }
    
    // implementation of getNextEntityId()
    public java.lang.String getNextEntityId()
	throws edu.fcla.daitss.exception.FatalException, java.rmi.RemoteException
    {
	try {
	    Object $result = ref.invoke(this, $method_getNextEntityId_1, null, 507579235940282141L);
	    return ((java.lang.String) $result);
	} catch (java.lang.RuntimeException e) {
	    throw e;
	} catch (java.rmi.RemoteException e) {
	    throw e;
	} catch (edu.fcla.daitss.exception.FatalException e) {
	    throw e;
	} catch (java.lang.Exception e) {
	    throw new java.rmi.UnexpectedException("undeclared checked exception", e);
	}
    }
    
    // implementation of getNextEventId()
    public long getNextEventId()
	throws edu.fcla.daitss.exception.FatalException, java.rmi.RemoteException
    {
	try {
	    Object $result = ref.invoke(this, $method_getNextEventId_2, null, 7114255063978896455L);
	    return ((java.lang.Long) $result).longValue();
	} catch (java.lang.RuntimeException e) {
	    throw e;
	} catch (java.rmi.RemoteException e) {
	    throw e;
	} catch (edu.fcla.daitss.exception.FatalException e) {
	    throw e;
	} catch (java.lang.Exception e) {
	    throw new java.rmi.UnexpectedException("undeclared checked exception", e);
	}
    }
    
    // implementation of getNextIeid()
    public java.lang.String getNextIeid()
	throws edu.fcla.daitss.exception.FatalException, java.rmi.RemoteException
    {
	try {
	    Object $result = ref.invoke(this, $method_getNextIeid_3, null, -202786373724450412L);
	    return ((java.lang.String) $result);
	} catch (java.lang.RuntimeException e) {
	    throw e;
	} catch (java.rmi.RemoteException e) {
	    throw e;
	} catch (edu.fcla.daitss.exception.FatalException e) {
	    throw e;
	} catch (java.lang.Exception e) {
	    throw new java.rmi.UnexpectedException("undeclared checked exception", e);
	}
    }
}
