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
package edu.fcla.daitss.format.markup;

import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import edu.fcla.daitss.database.ArchiveDatabase;
import edu.fcla.daitss.database.DBConnection;
import edu.fcla.daitss.database.TransactionConnection;
import edu.fcla.daitss.database.SqlQuote;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.file.DataFile;
import edu.fcla.daitss.service.keyserver.OIDServer;
import edu.fcla.daitss.severe.element.SevereElement;
import edu.fcla.daitss.util.Informer;
import edu.fcla.daitss.bitstream.Bitstream;

/**
 * MarkupDocument
 */
public class MLDocument extends Bitstream {
	
	/**
	 * Maximum supported length of a character set name
	 */
	private static final int MAX_CHARSET_LENGTH = 255;

	/**
	 * The maximum supported length of the name ot the root element schema.
	 */
	private static final int MAX_ML_LENGTH = 255;

	/**
	 * HTML markup type
	 */
	public static final String MB_HTML = "HTML";

	/**
	 * Markup type is not applicable (why would this be not applicable??)
	 */
	public static final String MB_NA = "N/A";

	/**
	 * SGML markup type
	 */
	public static final String MB_SGML = "SGML";

	/**
	 * Unknown markup type
	 */
	public static final String MB_UNKNOWN = "UNKNOWN";

	/**
	 * XML markup type
	 */
	public static final String MB_XML = "XML";

	/**
	 * 
	 */
	public static final String NO_SCHEMA = "NO_SCHEMA";

	/**
	 * <p>
	 * Represents ...
	 * </p>
	 */
	public static final String NOT_VALID = "NOT_VALID";

	/**
	 * <p>
	 * Represents ...
	 * </p>
	 */
	public static final String SCHEMA_UNAVAILABLE = "UNAVAIL_SCHEMA";

	/**
	 * DTD schema type
	 */
	public static final String ST_DTD = "DTD";

	/**
	 * Schema type is not applicable (has no schema)
	 */
	public static final String ST_NA = "N/A";

	/**
	 * Unknown schema type
	 */
	public static final String ST_UNKNOWN = "UNKNOWN";

	/**
	 * W3C Schema schema type
	 */
	public static final String ST_W3C_XML_SCHEMA = "W3C_XML_SCHEMA";

	/**
	 * <p>
	 * Represents ...
	 * </p>
	 */
	//public static final String UNCHECKED = "UNCHECKED";

	/**
	 * Has a schema, is valid and well-formed
	 */
	//public static final String VALID = "VALID";
	
	/**
	 * Found to be not valid.
	 */
	public static final String VALID_FALSE = "FALSE";

	/**
	 * Validity is not applicable.
	 */
	public static final String VALID_NA = "N/A";

	/**
	 * Found to be valid.
	 */
	public static final String VALID_TRUE = "TRUE";

	/**
	 * Did not try to validate it.
	 */
	public static final String VALID_UNCHECKED = "UNCHECKED";

	/**
	 * Has no schema, is well-formed
	 */
	public static final String WELL_FORMED = "NOS_WELLFORMED";
	
	/**
	 * Character set
	 */
	private String charSet = Bitstream.UNKNOWN;
	
	/**
	 * Assume by default that the archive determined the character
	 * set/encoding of the text.
	 */
	private String charSetOrigin = Bitstream.ORIG_NA;

	/**
	 * The metalanguage used to create the markup file (ex: SGML, XML,
	 * HTML, etc.).
	 */
	private String markupBasis = MB_UNKNOWN;

	/**
	 * Name of the schema used for the root element.
	 * The value of the xmlns:prefix attribute where prefix is
	 * the actual namespace prefix used by the root element 
	 * (i.e.  <code>http://www.loc.gov/METS/</code>
	 * if the root element was METS:mets and an attribute of
	 * this element were xmlns:METS="http://www.loc.gov/METS/" .)
	 */
	private String markupLanguage = "";

	/**
	 * The DFID of the root element's schema. 
	 */
	private String schemaDfid = "";

	/**
	 * Type of schema used by this markup file. 
	 * Ex: SCHEMA, DTD, UNKNOWN, N/A.
	 */
	private String schemaType = ST_UNKNOWN;
	
	/**
	 * Whether or not the XML document was validated and if so if it is valid.
	 * The values can be <code>VALID_TRUE</code>, 
	 * <code>VALID_FALSE</code>, <code>VALID_UNCHECKED</code>, 
	 * and <code>VALID_NA</code>.
	 */
	private String validationState = VALID_UNCHECKED;

	/**
	 * 
	 * @param df
	 * @throws FatalException
	 */
	public MLDocument(DataFile df) throws FatalException {
		super(df);
	}
	
	/**
	 * fill in the database column-value pairs for this document
	 * @param columns
	 * @param values
	 */
	private void fillDBValues(Vector<Object> columns, Vector<Object> values) {
		columns.clear();
		values.clear();
		
		columns.add(ArchiveDatabase.COL_BS_MARKUP_BSID);
		values.add(this.getBsid());
		columns.add(ArchiveDatabase.COL_BS_MARKUP_DFID);
		values.add(this.getDfid());
		columns.add(ArchiveDatabase.COL_BS_MARKUP_CHARSET);
		values.add(this.getCharSet());
		columns.add(ArchiveDatabase.COL_BS_MARKUP_CHARSET_ORIGIN);
		values.add(this.getCharSetOrigin());
		columns.add(ArchiveDatabase.COL_BS_MARKUP_MARKUP_BASIS);
		values.add(this.getMarkupBasis());
		columns.add(ArchiveDatabase.COL_BS_MARKUP_MARKUP_LANG);
		values.add(this.getMarkupLanguage());
		if (this.getSchemaDfid() != null && !this.getSchemaDfid().equals("")) {
		    String sDfid = this.getSchemaDfid();
			columns.add(ArchiveDatabase.COL_BS_MARKUP_SCHEMA_DFID);
			values.add(sDfid);
		}
		columns.add(ArchiveDatabase.COL_BS_MARKUP_SCHEMA_TYPE);
		values.add(this.getSchemaType());
		columns.add(ArchiveDatabase.COL_BS_MARKUP_VALID);
		values.add(this.getValidationState());
	}
		
	/**
	 * Insert this document into the database
	 * @return total number of affected rows
	 * @throws FatalException
	 */
	public int dbInsert() throws FatalException{
		super.dbInsert();
		
		TransactionConnection tcon = DBConnection.getSharedConnection();
		Vector<Object> columns = new Vector<Object>();
		Vector<Object> colValues = new Vector<Object>();
        
		fillDBValues(columns, colValues);
		
		return tcon.insert(ArchiveDatabase.TABLE_BS_MARKUP, columns, colValues);
	}
	
	/**
	 * update the entry in the database for this document
	 * @return total number of affected rows
	 * @throws FatalException
	 */
	public int dbUpdate() throws FatalException{
		super.dbUpdate();
		
		TransactionConnection tcon = DBConnection.getSharedConnection();
		Vector<Object> columns = new Vector<Object>();
		Vector<Object> colValues = new Vector<Object>();
        
		fillDBValues(columns, colValues);
		
		String whereClause = String.format("%s = %s",
				ArchiveDatabase.COL_BS_MARKUP_BSID, SqlQuote.escapeString(this.getBsid()));
		return tcon.update(ArchiveDatabase.TABLE_BS_MARKUP, columns, colValues, whereClause);
	}

	
	public Document toXML() throws FatalException {
	    /* Daitss Document */
	    Document doc = super.toXML();
	    
	    /* Daitss Element */
	    Element rootElement = doc.getDocumentElement();
	    
	    /* Daitss Namespace */
	    String namespace = rootElement.getNamespaceURI();
	    
	    /* Bs Markup */
	    Element bsMarkupElement = doc.createElementNS(namespace, "BS_MARKUP");	    
	    rootElement.appendChild(bsMarkupElement);

	    /* Dfid */
	    Element dfidElement = doc.createElementNS(namespace, "DFID");
	    String dfidValue = (this.getDfid() != null ? this.getDfid() : "" );
	    Text dfidText = doc.createTextNode(dfidValue);
	    dfidElement.appendChild(dfidText);
	    bsMarkupElement.appendChild(dfidElement);

	    /* Bsid */
	    Element bsidElement = doc.createElementNS(namespace, "BSID");
	    String bsidValue = (this.getBsid() != null ? this.getBsid() : "" );
	    Text bsidText = doc.createTextNode(bsidValue);
	    bsidElement.appendChild(bsidText);
	    bsMarkupElement.appendChild(bsidElement);

	    /* Markup Basis */
	    Element markupBasisElement = doc.createElementNS(namespace, "MARKUP_BASIS");
	    String markupBasisValue = (this.getMarkupBasis() != null ? this.getMarkupBasis() : "" );
	    Text markupBasisText = doc.createTextNode(markupBasisValue);
	    markupBasisElement.appendChild(markupBasisText);
	    bsMarkupElement.appendChild(markupBasisElement);

	    /* Schema Type */
	    Element schemaTypeElement = doc.createElementNS(namespace, "SCHEMA_TYPE");
	    String schemaTypeValue = (this.getSchemaType() != null ? this.getSchemaType() : "" );
	    Text schemaTypeText = doc.createTextNode(schemaTypeValue);
	    schemaTypeElement.appendChild(schemaTypeText);
	    bsMarkupElement.appendChild(schemaTypeElement);

        /* Markup Language */
        Element mlElement = doc.createElementNS(namespace, "MARKUP_LANGUAGE");
        String mlValue = (this.getMarkupLanguage() != null ? this.getMarkupLanguage() : "");
        Text mlText = doc.createTextNode(mlValue);
        mlElement.appendChild(mlText);
        bsMarkupElement.appendChild(mlElement);
	    	    
	    /* Schema Dfid */
        if (this.getSchemaDfid() != null && !this.getSchemaDfid().trim().equals("")) {
            Element schemaDfidElement = doc.createElementNS(namespace, "SCHEMA_DFID");
            String schemaDfidValue = this.getSchemaDfid();
            Text schemaDfidText = doc.createTextNode(schemaDfidValue);
            schemaDfidElement.appendChild(schemaDfidText);
            bsMarkupElement.appendChild(schemaDfidElement);
        }

	    /* Valid */
	    Element validElement = doc.createElementNS(namespace, "VALID");
	    String validValue = (this.getValidationState() != null ? this.getValidationState() : "" );
	    Text validText = doc.createTextNode(validValue);
	    validElement.appendChild(validText);
	    bsMarkupElement.appendChild(validElement);

	    /* Charset */
	    Element charsetElement = doc.createElementNS(namespace, "CHARSET");
	    String charsetValue = (this.getCharSet() != null ? this.getCharSet() : "" );
	    Text charsetText = doc.createTextNode(charsetValue);
	    charsetElement.appendChild(charsetText);
	    bsMarkupElement.appendChild(charsetElement);

	    /* Charset Origin */
	    Element charsetOriginElement = doc.createElementNS(namespace, "CHARSET_ORIGIN");
	    String charsetOriginValue = (this.getCharSetOrigin() != null ? this.getCharSetOrigin() : "" );
	    Text charsetOriginText = doc.createTextNode(charsetOriginValue);
	    charsetOriginElement.appendChild(charsetOriginText);
	    bsMarkupElement.appendChild(charsetOriginElement);
	    
	    return doc;
	}
	
	/**
	 * @return character set
	 */
	public String getCharSet() {
		return this.charSet;
	}

	/**
	 * @return the origin of the character set information
	 */
	public String getCharSetOrigin() {
		return this.charSetOrigin;
	}
	
	/**
	 * Returns the metalanguage used to create this markup file
	 * 
	 * @return the metalanguage used to create this markup file
	 */
	public String getMarkupBasis() {
		return this.markupBasis;
	}

	/**
	 * Returns the name of the schema used for the root element 
	 * 
	 * @return the name of the schema used for the root element 
	 */
	public String getMarkupLanguage() {
		return this.markupLanguage;
	}

	/**
	 * Returns the DFID of the root element's schema
	 * 
	 * @return the DFID of the root element's schema
	 */
	public String getSchemaDfid() {
		return this.schemaDfid;
	}

	/**
	 * Returns the type of schema used by this markup file
	 * 
	 * @return the type of schema used by this markup file
	 */
	public String getSchemaType() {
		return this.schemaType;
	}
	
	/**
	 * Returns whether or not this file was validated and if
	 * so what the result was.
	 * 
	 * @return the validation state
	 */
	public String getValidationState() {
		return this.validationState;
	} 
	
	/**
	 * @param _charSet character set
	 * @throws FatalException
	 */
	public void setCharSet(String _charSet) 
		throws FatalException {
		if (_charSet == null ){
			Informer.getInstance().fail(
				this, "setCharSet(String)",
				"Illegal argument", "_charSet: " + _charSet,
				new FatalException("Character set name can't be null")
			);
		} else if (_charSet.length() > MAX_CHARSET_LENGTH){
			// too long of a string to store - log this and try
			// to store the value anyway
			SevereElement ta = 
				this.getDf().getAnomsPossible().getSevereElement(MLAnomalies.ML_OVRLMT_CHARSET);
			this.getDf().addAnomaly(ta);
		}
		this.charSet = _charSet;
	}

	/**
	 * @param _charSetOrigin the origin of the character set information 
	 * @throws FatalException
	 */
	public void setCharSetOrigin(String _charSetOrigin) throws FatalException {
		if (_charSetOrigin == null || 
			(!_charSetOrigin.equals(Bitstream.ORIG_ARCHIVE) &&
			 !_charSetOrigin.equals(Bitstream.ORIG_DEPOSITOR) &&
			 !_charSetOrigin.equals(Bitstream.ORIG_NA))) {
				Informer.getInstance().fail(
					this,
					"setCharSetOrigin(String)",
					"Invalid argument",
					"_charSetOrigin: " + _charSetOrigin,
					new FatalException("Not a valid character set origin"));
			}
		this.charSetOrigin = _charSetOrigin;
	}

	/**
	 * Sets the metalanguage used to create the markup language
	 * 
	 * @param _markupBasis the metalanguage used to create the markup language
	 * @throws FatalException
	 */
	public void setMarkupBasis(String _markupBasis) 
		throws FatalException {
		if (_markupBasis != null &&
			(_markupBasis.equals(MB_HTML) ||
			_markupBasis.equals(MB_NA) ||
			_markupBasis.equals(MB_SGML) ||
			_markupBasis.equals(MB_UNKNOWN) ||
			_markupBasis.equals(MB_XML))){
				this.markupBasis = _markupBasis;
		} else {
			Informer.getInstance().fail(
				this, "setMarkupBasis(String)",
				"Illegal argument", "_markupBasis: " + _markupBasis,
				new FatalException("Not a valid markup basis"));
		}
	}

	/**
	 * Sets the name of the schema used for the root element.
	 * Note that there is a maximum length for the name: 
	 * <code>MAX_ML_LENGTH</code>
	 * 
	 * @param _markupLanguage	the name of the schema used for the root element
	 * @throws FatalException
	 */
	public void setMarkupLanguage(String _markupLanguage) 
		throws FatalException {
		if (_markupLanguage == null ) {
			Informer.getInstance().fail(
				this, "setMarkupLanguage(String)",
				"Illegal argument", "_markupLanguage: " + _markupLanguage,
				new FatalException("Not a valid schema name")
			);
		}
		if (_markupLanguage.length() >= MAX_ML_LENGTH){
			// exceeds the database limit for the string length - note this.
			SevereElement xa = 
				this.getDf().getAnomsPossible().getSevereElement(MLAnomalies.ML_OVRLMT_MARKUP_LANGUAGE);
			this.getDf().addAnomaly(xa);
		}
		this.markupLanguage = _markupLanguage;
	} 

	/**
	 * Sets the DFID of the root element's schema
	 * 
	 * @param _schemaDFID	the DFID of the root element's schema
	 * @throws FatalException
	 */
	public void setSchemaDfid(String _schemaDFID) 
		throws FatalException {
		//	check that the argument is not null
		this.checkForNullObjectArg("setSchemaDFID(String _schemaDFID)",
			"_schemaDFID", _schemaDFID, this.getClass().getName());
		
		if (!OIDServer.isValidDfid(_schemaDFID)){
			Informer.getInstance().fail(this,
				"setSchemaDFID(String)", "Illegal argument",
				"_schemaDFID: " + _schemaDFID, 
				new FatalException("Not a valid DFID."));
		}
		this.schemaDfid = _schemaDFID;
	} 

	/**
	 * Sets the type of schema used by this markup file
	 * 
	 * @param _schemaType	the type of schema used by this markup file
	 * @throws FatalException
	 */
	public void setSchemaType(String _schemaType) 
		throws FatalException {
		if (_schemaType != null &&
			(_schemaType.equals(ST_DTD) ||
			_schemaType.equals(ST_NA) ||
			_schemaType.equals(ST_UNKNOWN) ||
			_schemaType.equals(ST_W3C_XML_SCHEMA))){
				this.schemaType = _schemaType;
		} else {
			Informer.getInstance().fail(
				this, "setSchemaLanguage(String)",
				"Illegal argument", "_schemaType: " + _schemaType,
				new FatalException("Not a valid schema type")
			);
		}
	} 
	
	/**
	 * Whether or not the XML document has been validated
	 * and what the result was.
	 * 
	 * @param _validationState the validation state
	 * @throws FatalException
	 */
	public void setValidationState(String _validationState) 
		throws FatalException {
		if (_validationState != VALID_FALSE &&
			_validationState != VALID_NA &&
			_validationState != VALID_TRUE &&
			_validationState != VALID_UNCHECKED){
				Informer.getInstance().fail(
					this, "setValidationState(String)",
					"Illegal argument", "_validationState: " + validationState,
					new FatalException("Not a valid validation state.")
				);
		}
		this.validationState = _validationState;
	} 
	
	/**
	 * Returns the values of all its members.
	 * 
	 * @return the members of this class as a String
	 */
	public String toString() {
		String prior = super.toString();
		StringBuffer sb = new StringBuffer("");
		sb.append(prior);
		
		sb.append("\tCharacter set: " + this.getCharSet() + "\n");
		sb.append("\tCharacter set origin: " + this.getCharSetOrigin() + "\n");
		sb.append("\tMarkup basis: " + this.getMarkupBasis() + "\n");
		sb.append("\tMarkup language: " + this.getMarkupLanguage() + "\n");
		sb.append("\tSchema DFID: " + this.getSchemaDfid() + "\n");
		sb.append("\tSchema type: " + this.getSchemaType() + "\n");
		sb.append("\tValidation state: " + this.getValidationState() + "\n");
		
		return sb.toString();
	}
	
}
