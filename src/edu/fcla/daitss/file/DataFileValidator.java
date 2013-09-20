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
 * Created on Sep 16, 2004
 *
 */
package edu.fcla.daitss.file;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.exception.PackageException;
import edu.fcla.daitss.file.global.GlobalFile;
import edu.fcla.daitss.format.markup.METSDescriptor;
import edu.fcla.daitss.format.markup.METSDocument;
import edu.fcla.daitss.util.Informer;

/**
 * DataFileValidator makes sure that the DataFile class and
 * all its subclasses each define a certain set of required
 * methods with specific method signatures.
 * 
 * @author Andrea Goethals, FCLA
 *
 */
public class DataFileValidator {
	
	/**
	 * Fully-qualified name for this class. To be used for 
	 * Informer calls from within static methods.
	 */
	private static String CLASSNAME = "edu.fcla.daitss.file.DataFileValidator";

	/**
	 * Checks a class to make sure that it is declaring
	 * a certain set of methods that all DataFile classes
	 * are required to declare.
	 * 
	 * @param className a fully qualified Java class name
	 * @return whether or not the class is a valid DataFile class
	 * @throws FatalException
	 */
	public static boolean isValidDataFile(Class className) throws FatalException {
		String methodName = "isValidDataFile(Class)";
		
		boolean isValid = false;
		
		// exempt some special classes from having these methods
		if (className.getName().equals(METSDescriptor.class.getName()) ||
		        className.getName().equals(GlobalFile.class.getName())){
		    isValid = true;
		} else {
			// flags to set after verifying a particular method
			// signature exists in the class
			boolean sawEvalMembers = false,
					sawIsType1 = false, 
					sawIsType2 = false,
					sawParse = false;
			
			// get all the methods declared in that class (does
			// not include superclasses)
			Method[] allMethods = className.getDeclaredMethods();
			
			// store the fully qualified names of particular classes to
			// use in class comparison below
			String classFatalException = FatalException.class.getName();
			String classPackageException = PackageException.class.getName();
			String classString = String.class.getName();
			String classMETSDocument = METSDocument.class.getName();
			
			// check off particular methods as seen in the set of declared methods
			for(int i=0; i<allMethods.length; i++) {
				// collect the method signature
				String mName = allMethods[i].getName();
				Class[] exceptions = allMethods[i].getExceptionTypes();
				int modifiers = allMethods[i].getModifiers();
				Class[] parameters = allMethods[i].getParameterTypes();
				Class returnType = allMethods[i].getReturnType();
						
				// protected void evalMembers() throws FatalException
				if (!sawEvalMembers && mName.equals("evalMembers") &&
						exceptions.length == 1 &&
						exceptions[0].getName().equals(classFatalException) &&
						!Modifier.isAbstract(modifiers) &&
						!Modifier.isFinal(modifiers) &&
						Modifier.isProtected(modifiers) &&
						!Modifier.isStatic(modifiers) &&
						parameters.length == 0 &&
						returnType.getName().equals("void")) {
					sawEvalMembers = true;
					
				// public static boolean isType(String) throws FatalException
				} else if (!sawIsType1 && mName.equals("isType") &&
						exceptions.length == 1 &&
						exceptions[0].getName().equals(classFatalException) &&
						!Modifier.isAbstract(modifiers) &&
						!Modifier.isFinal(modifiers) &&
						Modifier.isPublic(modifiers) &&
						Modifier.isStatic(modifiers) &&
						parameters.length == 1 &&
						parameters[0].getName().equals(classString) &&
						returnType.getName().equals("boolean")) {
					sawIsType1 = true;
					
				// public static boolean isType(String, METSDocument) throws FatalException
				} else if (!sawIsType2 && mName.equals("isType") &&
						exceptions.length == 1 &&
						exceptions[0].getName().equals(classFatalException) &&
						!Modifier.isAbstract(modifiers) &&
						!Modifier.isFinal(modifiers) &&
						Modifier.isPublic(modifiers) &&
						Modifier.isStatic(modifiers) &&
						parameters.length == 2 &&
						parameters[0].getName().equals(classString) &&
						parameters[1].getName().equals(classMETSDocument) &&
						returnType.getName().equals("boolean")) {
					sawIsType2 = true;
					
				// protected void parse() throws FatalException
				} else if (!sawParse && mName.equals("parse") &&
						exceptions.length == 1 &&
						exceptions[0].getName().equals(classFatalException) &&
						!Modifier.isAbstract(modifiers) &&
						!Modifier.isFinal(modifiers) &&
						Modifier.isProtected(modifiers) &&
						!Modifier.isStatic(modifiers) &&
						parameters.length == 0 &&
						returnType.getName().equals("void")) {
					sawParse = true;
				}
			}
			
			// check that all required methods were seen in the class
			if (sawEvalMembers && sawIsType1 &&
				sawIsType2 && sawParse) {
				isValid = true;
			} else {
				// print out info for testing
				StringBuffer sb = new StringBuffer("");
				if (!sawEvalMembers){
					sb.append(" evalMembers()");
				}
				if (!sawIsType1){
					sb.append(" isType(String)");
				}
				if (!sawIsType2){
					sb.append(" isType(String, METSDocument)");
				}
				if (!sawParse){
					sb.append(" parse()");
				}
				
				Informer.getInstance().info(CLASSNAME,
						methodName,
						"Missing required method(s)",
						"class: " + className.getName() + 
						" (missing method(s):" + sb.toString() + ")",
						false);
			}
		}
	
		return isValid;
	}

}
