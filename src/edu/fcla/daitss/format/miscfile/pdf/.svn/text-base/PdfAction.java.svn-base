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
 * Created on Feb 12, 2004
 *
 */
package edu.fcla.daitss.format.miscfile.pdf;

import edu.fcla.daitss.exception.FatalException;

/**
 * PdfAction represents the action types described
 * in PDF 1.2 - 1.5 specs.
 * 
 * @author Andrea Goethals, FCLA
 *
 */
public class PdfAction {
	
	/**
	 * Go to a destination in the current document.
	 */
	public static final String A_GOTO = "GoTo";
	
	/**
	 * Go to a destination in another document.
	 */
	public static final String A_GOTOR = "GoToR";
	
	/**
	 * Go to a destination in an embedded file (PDF 1.6).
	 */
	public static final String A_GOTOE = "GoToE";
	
	/**
	 * Set an annotation's hidden flag. (PDF 1.2)
	 */
	public static final String A_HIDE = "Hide";
	
	/**
	 * Import field values from a file. (PDF 1.2)
	 */
	public static final String A_IMPORTDATA = "ImportData";
	
	/**
	 * Execute a JavaScript script. (PDF 1.3)
	 */
	public static final String A_JAVASCRIPT = "JavaScript";
	
	/**
	 * Launch an application, usually to open a file.
	 */
	public static final String A_LAUNCH = "Launch";
	
	/**
	 * Play a movie. (PDF 1.2)
	 */
	public static final String A_MOVIE = "Movie";
	
	/**
	 * Execute an action predifined by the viewer app. (PDF 1.2)
	 */
	public static final String A_NAMED = "Named";
	
	/**
	 * Set fields to their default values. (PDF 1.2)
	 */
	public static final String A_RESETFORM = "ResetForm";
	
	/**
	 * Play a sound. (PDF 1.2)
	 */
	public static final String A_SOUND = "Sound";
	
	/**
	 * Send data to a URL. (PDF 1.2)
	 */
	public static final String A_SUBMITFORM = "SubmitForm";
	
	/**
	 * Begin reading an article thread.
	 */
	public static final String A_THREAD = "Thread";
	
	/**
	 * Resolve a uniform resource identifier.
	 */
	public static final String A_URI = "URI";
	
	/**
	 * set the state of optional content group (PDF 1.5).
	 */
	public static final String A_SETOCGSTATE = "SetOCGState";
	
	/**
	 * Control the playing of multimedia content (PDF 1.5).
	 */
	public static final String A_RENDITION = "Rendition";
	
	/**
	 * Update the display of a document, using a transition dictionary (PDF 1.5).
	 */
	public static final String A_TRANS = "Trans";

	/**
	 * Set the current view of a 3D annotation (PDF 1.6).
	 */
	public static final String A_GOTO3DVIEW = "GoTo3DView";
		
	/**
	 * Annotation action on blur. (1.2)
	 */
	public static final String AN_BL = "Bl";
	
	/**
	 * Annotation action on mouse down. (1.2)
	 */
	public static final String AN_D = "D";
	/**
	 * Annotation action on cursor entry. (1.2)
	 */
	public static final String AN_E = "E";
	
	/**
	 * Annotation action on focus. (1.2)
	 */
	public static final String AN_FO = "Fo";
	
	/**
	 * Annotation action to be performed when
	 * the page containing the annotation is closed. (1.5)
	 */
	public static final String AN_PC = "PC";
	
	/**
	 * Annotation action to be performed when
	 * the page containing the annotation is no longer visible. (1.5)
	 */
	public static final String AN_PI = "PI";
	
	/**
	 * Annotation action to be performed when
	 * the page containing the annotation is opened. (1.5)
	 */
	public static final String AN_PO = "PO";
	
	/**
	 * Annotation action to be performed when
	 * the page containing the annotation becomes visible. (1.5)
	 */
	public static final String AN_PV = "PV";
	
	/**
	 * Annotation action on mouse up. (1.2)
	 */
	public static final String AN_U = "U";
	
	/**
	 * Annotation action on cursor exit. (1.2)
	 */
	public static final String AN_X = "X";
	
	/**
     * Fully-qualified name for this class. To be used for 
     * Informer calls from within static methods.
     */
    private static String CLASSNAME = "edu.fcla.daitss.bitstream.markup.pdf.PdfAction";
	
	/**
	 * A JavaScript action to be performed before closing a 
	 * document. (1.4)
	 */
	public static final String D_DC = "DC";
	
	/**
	 * A JavaScript action to be performed after printing a document. (1.4)
	 */
	public static final String D_DP = "DP";
	
	/**
	 * A JavaScript action to be performed after saving a document. (1.4)
	 */
	public static final String D_DS = "DS";
	
	/**
	 * A JavaScript action to be performed before printing a document. (1.4)
	 */
	public static final String D_WP = "WP";
	
	/**
	 * A JavaScript action to be performed before saving a document. (1.4)
	 */
	public static final String D_WS = "WS";
	
	/**
	 * Form field action on recalculation of this field because 
	 * another field changed. (1.3)
	 */
	public static final String F_C = "C";
	
	/**
	 * Form field action before formatting field display. (1.3)
	 */
	public static final String F_F = "F";
	
	/**
	 * Form field action on key type or scollable list selection 
	 * modification. (1.3)
	 */
	public static final String F_K = "K";
	
	/**
	 * Form field action on field value change. (1.3)
	 */
	public static final String F_V = "V";
	
	/**
	 * Page action on close. (1.2)
	 */
	public static final String P_C = "C";
	
	/**
	 * Page action on open. (1.2)
	 */
	public static final String P_O = "O";
	
	/**
	 * Action type: Annotation
	 */
	public static final String TYPE_ANNOTATION = "ANNOTATION";
	
	/**
	 * Action type: Document
	 */
	public static final String TYPE_DOCUMENT = "DOCUMENT";
	
	/**
	 * Action type: Form Field
	 */
	public static final String TYPE_FORM_FIELD = "FORM_FIELD";
	
	/**
	 * Action type: Page
	 */
	public static final String TYPE_PAGE = "PAGE";
	
	/**
	 * Action type: Activation
	 */
	public static final String TYPE_ACTIVATION = "ACTIVATION";
	/**
	 * 
	 * @param action the action constant
	 * @param type the action type
	 * @return the name of <code>action</code> which is unique among the
	 * 	other action constants
	 * @throws FatalException
	 */
	public static String getUniqueActionName(String action, String type) 
		throws FatalException {
	    
	    StringBuffer uqName = new StringBuffer();
	    
	    // make sure that the action name is a constant
	    // in this class.
	    if (action == null ||
	    		( !isValidAnnotAa(action) && !isValidDocCatAa(action) &&
	            !isValidPageAa(action) && !isValidFormFieldtAa(action) &&
				!isValidActionType(action))) {
	        return null;
	    }
	    
	    // make sure the action type is valid
	    if (type == null || !isValidArchivePdfActionType(type)){
	        return null;
	    }
	    
	    if (type.equals(TYPE_ANNOTATION)){
	    	uqName.append("AN_");
	    } else if (type.equals(TYPE_DOCUMENT)){
	    	uqName.append("D_");
	    } else if (type.equals(TYPE_FORM_FIELD)){
	    	uqName.append("F_");
	    } else if (type.equals(TYPE_PAGE)){
	    	uqName.append("P_");
	    } else if (type.equals(TYPE_ACTIVATION)){
	    	uqName.append("A_");
	    }
	    
	    // convert to upper case before appending it
	    uqName.append(action.toUpperCase()); 
	    
	    
	    return uqName.toString();
	}
	
	/**
	 * Determines whether or not an action type is considered to be
	 * a valid one (specified on page 518 in the PDF 1.4 spec.).
	 * 
	 * @param type	the action type 
	 * @return	<code>true</code> if the type is valid, else
	 * 	<code>false</code>.
	 */
	public static boolean isValidActionType (String type){
		boolean isValid = false;
		
		if (type != null && 
			(type.equals(A_GOTO)) || type.equals(A_GOTOR) 
			|| type.equals(A_GOTOE) || type.equals(A_GOTO3DVIEW)
			|| type.equals(A_HIDE) || type.equals(A_IMPORTDATA)
			|| type.equals(A_JAVASCRIPT) || type.equals(A_LAUNCH)
			|| type.equals(A_MOVIE) || type.equals(A_NAMED)
			|| type.equals(A_RENDITION) || type.equals(A_SETOCGSTATE)
			|| type.equals(A_RESETFORM) || type.equals(A_SOUND)
			|| type.equals(A_SUBMITFORM) || type.equals(A_THREAD)
			|| type.equals(A_TRANS) || type.equals(A_URI)){
					isValid = true;
			}
		
		return isValid;
	}
	
	/**
	 * Determines whether or not an action code is a valid one
	 * in the Additional Actions dictionary of an annotation.
	 * See p. 515 of the pdf 1.4 spec and
	 * p. 594 of the pdf 1.5 spec.
	 * 
	 * @param action an action code
	 * @return whether or not its valid
	 */
	public static boolean isValidAnnotAa(String action){
		boolean isValid = false;
		
		if (action != null &&
			(action.equals(AN_BL) || action.equals(AN_D) ||
			action.equals(AN_E) || action.equals(AN_FO) ||
			action.equals(AN_U) || action.equals(AN_X) ||
			action.equals(AN_PO) || action.equals(AN_PC) ||
			action.equals(AN_PI) || action.equals(AN_PV))){
			isValid = true;
		}
		
		return isValid;
	}
	
	/**
	 * Determines if a Pdf action type is one recognized by
	 * the archive.
	 * 
	 * @param type action type
	 * @return whether or not the action type is valid in the archive
	 */
	public static boolean isValidArchivePdfActionType(String type) {
		boolean isType = false;
		
		if (type != null && (type.equals(TYPE_ANNOTATION) ||
			type.equals(TYPE_DOCUMENT) || 
			type.equals(TYPE_FORM_FIELD) ||
			type.equals(TYPE_PAGE) ||
			type.equals(TYPE_ACTIVATION))) {
			isType = true;
		}
		return isType;	
	}
	
	/**
	 * Determines whether or not an action code is a valid one
	 * in the Additional Actions dictionary of the document
	 * catalog dictionary.
	 * See p. 516 of the pdf 1.4 spec.
	 * 
	 * @param action an action code
	 * @return whether or not its valid
	 */
	public static boolean isValidDocCatAa(String action){
		boolean isValid = false;
		
		if (action != null &&
			(action.equals(D_DC) || action.equals(D_DP) ||
			action.equals(D_DS) || action.equals(D_WP) ||
			action.equals(D_WS))){
			isValid = true;
		}
		
		return isValid;
	}
	
	/**
	 * Determines whether or not an action code is a valid one
	 * in a form field's Additional Actions dictionary.
	 * See p. 516 of the pdf 1.4 spec.
	 * 
	 * @param action an action code
	 * @return whether or not its valid
	 */
	public static boolean isValidFormFieldtAa(String action){
		boolean isValid = false;
		
		if (action != null &&
			(action.equals(F_C) || action.equals(F_F) ||
			action.equals(F_K) || action.equals(F_V))){
			isValid = true;
		}
		
		return isValid;
	}
	
	/**
	 * Determines whether or not an action code is a valid one
	 * in the Additional Actions dictionary of a page object.
	 * See p. 515 of the pdf 1.4 spec.
	 * 
	 * @param action an action code
	 * @return whether or not its valid
	 */
	public static boolean isValidPageAa(String action){
		boolean isValid = false;
		
		if (action != null &&
			(action.equals(P_C) || action.equals(P_O))) {
				isValid = true;
		}
		return isValid;
	}
	
	/**
	 * Test driver.
	 * 
	 * @param args
	 * @throws FatalException
	 */
	public static void main(String[] args) throws FatalException {
		System.out.println("Not unique name for AN_X: " + AN_X);
		System.out.println("Unique name for AN_X: " + 
				getUniqueActionName(AN_X, TYPE_ANNOTATION));
		
		System.out.println("Not unique name for F_C: " + F_C);
		System.out.println("Unique name for F_C: " + 
				getUniqueActionName(F_C, TYPE_FORM_FIELD));
		
		System.out.println("Not unique name for P_C: " + P_C);
		System.out.println("Unique name for P_C: " + 
				getUniqueActionName(P_C, TYPE_PAGE));
		
		System.out.println("Not unique name for D_DC: " + D_DC);
		System.out.println("Unique name for D_DC: " + 
				getUniqueActionName(D_DC, TYPE_DOCUMENT));
	}
}
