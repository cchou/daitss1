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
 * Created on Jan 14, 2004
 *
 */
package edu.fcla.daitss.format.miscfile.pdf;

import java.util.Iterator;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import edu.fcla.daitss.bitstream.Bitstream;
import edu.fcla.daitss.bitstream.Compression;
import edu.fcla.daitss.database.ArchiveDatabase;
import edu.fcla.daitss.database.DBConnection;
import edu.fcla.daitss.database.TransactionConnection;
import edu.fcla.daitss.database.SqlQuote;
import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.file.DataFile;
import edu.fcla.daitss.severe.element.SevereElement;
import edu.fcla.daitss.util.Informer;

/**
 * PdfDocument represents the one and only document within a Pdf file.
 */
public class PdfDocument extends Bitstream {
	
	/**
	 * The maximum supported length of a natural language String.
	 */
	public static final int MAX_NATL_LANG_LENGTH = 128;
	
	/**
	 * The maximum supported number of pages.
	 */
	public static final int MAX_NUM_PAGES = 2147483647;
	
	/**
	 * The minimum supported number of pages.
	 * This value really means an unknown number of pages.
	 */
	public static final int MIN_NUM_PAGES = -1;
	
	/**
	 * Display the pages in once column.
	 */
	public static final String PAGE_LAYOUT_1_COL = "ONE_COLUMN";
	
	/**
	 * Display the pages in 2 columns, with odd numbers on the left.
	 */
	public static final String PAGE_LAYOUT_2_COL_L = "TWO_COLUMN_LEFT";
	
	/**
	 * Display the pages in 2 columns, with odd numbers on the right.
	 */
	public static final String PAGE_LAYOUT_2_COL_R = "TWO_COLUMN_RIGHT";

	/**
	 * Display one page at a time (the default)
	 */
	public static final String PAGE_LAYOUT_SINGLE = "SINGLE_PAGE";
	
	/**
	 * The document should be opened in full-screen mode, with no menu bar,
	 * window controls or any other window visible.
	 */
	public static final String PAGE_MODE_FULL = "FULL_SCREEN";
	
	/**
	 * Neither the document outline nor the thumbnail images should be
	 * visible when this document is opened (the default).
	 */
	public static final String PAGE_MODE_NONE = "NO_OUTLINE_NO_THUMB";
	
	/**
	 * The document outline should be visible when this document is opened.
	 */
	public static final String PAGE_MODE_OUTLINE = "SEE_OUTLINE";
	
	/**
	 * The thumbnail images should be visible when this document is opened.
	 */
	public static final String PAGE_MODE_THUMB = "SEE_THUMB";
	
	/**
	 * A list of the annotation types contained in this PDF document.
	 */
	private Vector annotations = null;
	
	/**
	 * A list of the filter (compression) types used in this PDF document.
	 * These are found by parsing each page of the Pdf document
	 * looking for stream dictionaries containing a /Filter entry.
	 * This work is done in
	 * <code>edu.fcla.daitss.format.miscfile.pdf.Pdf.parsePages(PdfManager)</code>.
	 */
	private Vector filters = null;
	
	/**
	 * Whether or not this PDF has any images.
	 * This is determined by parsing each page in the Pdf document
	 * looking for stream dictionaries with a /Subtype that is equal to /Image. That
	 * finds all the Image XObjects. Note that this does not detect any inline images!
	 * This work is done in
	 * <code>edu.fcla.daitss.format.miscfile.pdf.Pdf.parsePages(PdfManager)</code>.
	 */
	private String hasImages = Bitstream.UNKNOWN;

	/**
	 * Whether or not this PDF contains XML metadata (in XMP format)
	 * This is determined by looking up the metadata in the document 
	 * <code>edu.fcla.daitss.format.miscfile.pdf.Pdf.parseCatDict</code>.
	 */
	private String hasMetadata = Bitstream.UNKNOWN;
	
	/**
	 * Whether or not the PDF has an outline.
	 * This is set to <code>TRUE</code> when there is an 
	 * Outlines entry in the catalog dictionary.
	 */
	private String hasOutline = Bitstream.UNKNOWN;
	
	/**
	 * Whether or not this PDF has at least one instance of a thumbnail image 
	 * representation of a page.
	 */
	private String hasThumbnails = Bitstream.UNKNOWN;
	
	/**
	 * A language identifier specifying the natural language for all text in
	 * the PDF except where overridden by partial sections of the document. The
	 * syntax of the language identifier is defined in Internet RFC 1766,
	 * &quot;Tags for the Identification of Languages&quot;. See section 9.8.1
	 * of the PDF 1.4 specification for more details.
	 * 
	 * This is set to something other than <code>UNKNOWN</code>
	 * when the Lang entry is present in the Catalog dictionary.
	 */
	private String natlLang = Bitstream.UNKNOWN;

	/**
	 * Number of pages in the PDF document.
	 * <code>-1</code> means an unknown number of pages.
	 * Minimum value is <code>MIN_NUM_PAGES</code>
	 * Maximum value is <code>MAX_NUM_PAGES</code>
	 */
	private int numPages = -1;
	
	/**
	 * A list of the actions contained in this PDF document.
	 */
	private Vector actions = null;
	
	/**
	 * How the document should be formatted.
	 */
	private String pageLayout = PAGE_LAYOUT_SINGLE;
	
	/**
	 * How the document should be displayed when first opened (for
	 * example, should the outline be shown).
	 */
	private String pageMode = PAGE_MODE_NONE;
	
	/**
	 * Whether or not all of its fonts are embedded.
	 * If no fonts are used in this PDF document, then this will
	 * have a value of FALSE. 
	 */
	private String usesUnembeddedFonts = UNKNOWN;
		
	/**
	 * list of all non standard fonts used in this PDF document
	 */
	private PdfFonts pdfFonts = null;	
	
	/**
	 * Sets all default member values and builds any needed data 
	 * structures.
	 * 
	 * @param df the file containing this PDF bitstream
	 * @throws FatalException
	 */
	public PdfDocument(DataFile df) throws FatalException {
		super(df);
		
		// set default values for all PDF documents
		this.setBsTable(ArchiveDatabase.TABLE_BS_PDF);
		this.setLocationType(Bitstream.LOC_FILE_BYTE_OFFSET);
		this.getCompression().setCompression(Compression.COMP_NOT_APPLICABLE);
		
		// build needed data structures
		this.actions = new Vector();
		this.annotations = new Vector();
		this.filters = new Vector();
		this.pdfFonts = new PdfFonts();
	}
	
	/**
	 * Adds an annotation type to the list of all types of annotation used in the 
	 * PDF document, if it is a valid type and it is not already in the list. If the
	 * type is null or is already in the list, this request is
	 * silently ignored. If the annotation type is not a known one an anomaly
	 * is noted.
	 * 
	 * @param type	the annotation type
	 * @throws FatalException
	 */
	public void addAnnotation(String type) throws FatalException {
		if (type != null && !annotations.contains(type)){
			if (PdfAnnotation.isValidType(type)){
				annotations.add(type);
			} else {
				// unknown annotation type - note anomaly
				SevereElement pa = 
					this.getDf().getAnomsPossible().getSevereElement(PdfAnomalies.PDF_UNKNOWN_ANNOTATION);
				this.getDf().addAnomaly(pa);			
			}
		}
	}
	

	
	/**
	 * Adds a font to the list of all fonts used in the 
	 * PDF document
	 * @param _fontname	 	the name of the font
	 * @param embedded 	whether the font is embedded
	 * @throws FatalException
	 */
	public void addFont(String _fontname, boolean embedded) throws FatalException {
		if (_fontname != null) {
			pdfFonts.addFont(_fontname, embedded);
		}
	}

	/**
	 * Adds a filter type to the list of all types of filters used in the 
	 * PDF document, if it is a valid type and it is not already in the list. If the
	 * type is null or is already in the list, this request is
	 * silently ignored. If the filter type is not a known one an anomaly
	 * is noted.
	 * 
	 * @param type	the filter type
	 * @throws FatalException
	 */
	public void addFilter(String type) throws FatalException {
		if (type != null && !filters.contains(type)){
			if (PdfFilter.isValidType(type)){
				filters.add(type);
			} else {
				// unknown filter type - note anomaly
				SevereElement pa = 
					this.getDf().getAnomsPossible().getSevereElement(PdfAnomalies.PDF_UNKNOWN_FILTER);
				this.getDf().addAnomaly(pa);			
			}
		}
	}
	
	/**
	 * Adds a page-level action type to the list of all types of page-level actions used in the 
	 * PDF document, if it is a valid type and it is not already in the list. If the
	 * type is null or is already in the list, this request is
	 * silently ignored. If the action type is unknown an anomaly is noted.
	 * 
	 * @param type	the action type
	 * @throws FatalException
	 */
	public void addAction(String type, String action) throws FatalException {
		if (action != null && type != null) {
			String sAction = PdfAction.getUniqueActionName(action, type);
			if (sAction != null) {
				if (!actions.contains(sAction))
					actions.add(sAction);
			} else {
				// unknown action type - note anomaly
				SevereElement pa = 
					this.getDf().getAnomsPossible().getSevereElement(PdfAnomalies.PDF_UNKNOWN_ACTION);
				this.getDf().addAnomaly(pa);
			}
		}
	}
	
	/**
	 * fill in the database column-value pairs for this document
	 * @param columns
	 * @param values
	 */
	private void fillDBValues(Vector<Object> columns, Vector<Object> values) throws FatalException{
		columns.clear();
		values.clear();
		
		columns.add(ArchiveDatabase.COL_BS_PDF_BSID);
		values.add(this.getBsid());
		columns.add(ArchiveDatabase.COL_BS_PDF_DFID);
		values.add(this.getDfid());
		columns.add(ArchiveDatabase.COL_BS_PDF_HAS_IMAGES);
		values.add(this.hasImages());
		columns.add(ArchiveDatabase.COL_BS_PDF_HAS_METADATA);
		values.add(this.hasMetadata());
		columns.add(ArchiveDatabase.COL_BS_PDF_HAS_OUTLINE);
		values.add(this.hasOutline());
		columns.add(ArchiveDatabase.COL_BS_PDF_NATL_LANG);
		values.add(this.getNatlLang());
		columns.add(ArchiveDatabase.COL_BS_PDF_NUM_PAGES);
		values.add(new Long(this.getNumPages()));
		if (pdfFonts.getNonstandardEmbedded() != null) {
			columns.add(ArchiveDatabase.COL_BS_PDF_NONSTANDARD_EMBEDDED_FONTS);
			values.add(pdfFonts.getNonstandardEmbedded());
		}
		if (pdfFonts.getNonstandardUnEmbedded() != null) {
			columns.add(ArchiveDatabase.COL_BS_PDF_NONSTANDARD_UNEMBEDDED_FONTS);
			values.add(pdfFonts.getNonstandardUnEmbedded());
		}
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
		int counterAll = 0; // total number of rows affected
		int counterPB = 0; // total number of Pdf Bitstream rows affected
		int counterAC = 0; // number of Bitstream Action table rows affected
		int counterAN = 0; // number of Bitstream Annotation table rows affected
		int counterFI = 0; // number of Bitstream Filter table rows affected
		
		fillDBValues(columns, colValues);
		
		counterPB = tcon.insert(ArchiveDatabase.TABLE_BS_PDF, columns, colValues);
		
		DBConnection.NameValueVector values = new DBConnection.NameValueVector();
		
		// actions
		for (Iterator iter = this.getActions().iterator(); iter.hasNext();){								
			String action = (String) iter.next();
		    values.addElement(ArchiveDatabase.COL_BS_PDF_ACTION_PDF_ACTION, action);
		    values.addElement(ArchiveDatabase.COL_BS_PDF_ACTION_BSID, this.getBsid());		
		    
		    counterAC = counterAC + tcon.insert(ArchiveDatabase.TABLE_BS_PDF_ACTION, values);
		    values.removeAllElements(); // cleanup
		}
		

		// annotations 
		for (Iterator iter = this.getAnnotations().iterator(); iter.hasNext();){				    					
			String ann = (String) iter.next();
		    values.addElement(
		            ArchiveDatabase.COL_BS_PDF_ANNOT_PDF_ANNOT, ann);
		    values.addElement(ArchiveDatabase.COL_BS_PDF_ANNOTATION_BSID, 
		            this.getBsid());
		    counterFI = counterFI + tcon.insert(ArchiveDatabase.TABLE_BS_PDF_ANNOTATION, values);
		    values.removeAllElements();  //cleanup	
		}
		
		// filters
		for (Iterator iter = this.getFilters().iterator(); iter.hasNext();){
		    String filt = (String) iter.next();
		    values.addElement(ArchiveDatabase.COL_BS_PDF_FILTER_PDF_FILTER, filt);
		    values.addElement(ArchiveDatabase.COL_BS_PDF_FILTER_BSID, this.getBsid());
		    
		    counterFI = counterFI + tcon.insert(ArchiveDatabase.TABLE_BS_PDF_FILTER, values);
			values.removeAllElements();
		}
		
		// sum up al the affected rows
		counterAll = counterAC + counterAN + counterFI + counterPB;
		return counterAll;
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
				ArchiveDatabase.COL_BS_PDF_BSID, SqlQuote.escapeString(this.getBsid()));
		return tcon.update(ArchiveDatabase.TABLE_BS_PDF, columns, colValues, whereClause);
	}
	
	
	/** (non-Javadoc)
	 * @return Document object
	 * @throws FatalException 
	 */
	public Document toXML() throws FatalException {
	    /* Daitss Document */
	    Document doc = super.toXML();
	    
	    /* Daitss Element */
	    Element rootElement = doc.getDocumentElement();
	    
	    /* Daitss Namespace */
	    String namespace = rootElement.getNamespaceURI();
	    
	    /* Bs Pdf */
	    Element bsPdfElement = doc.createElementNS(namespace, "BS_PDF");
	    rootElement.appendChild(bsPdfElement);

	    /* Dfid */
	    Element dfidElement = doc.createElementNS(namespace, "DFID");
	    String dfidValue = (this.getDfid() != null ? this.getDfid() : "" );
	    Text dfidText = doc.createTextNode(dfidValue);
	    dfidElement.appendChild(dfidText);
	    bsPdfElement.appendChild(dfidElement);

	    /* Bsid */
	    Element bsidElement = doc.createElementNS(namespace, "BSID");
	    String bsidValue = (this.getBsid() != null ? this.getBsid() : "" );
	    Text bsidText = doc.createTextNode(bsidValue);
	    bsidElement.appendChild(bsidText);
	    bsPdfElement.appendChild(bsidElement);

	    /* Natl Lang */
	    Element natlLangElement = doc.createElementNS(namespace, "NATL_LANG");
	    String natlLangValue = (this.getNatlLang() != null ? this.getNatlLang() : "" );
	    Text natlLangText = doc.createTextNode(natlLangValue);
	    natlLangElement.appendChild(natlLangText);
	    bsPdfElement.appendChild(natlLangElement);

	    /* Num Pages */
	    Element numPagesElement = doc.createElementNS(namespace, "NUM_PAGES");
	    String numPagesValue = Long.toString(this.getNumPages());
	    Text numPagesText = doc.createTextNode(numPagesValue);
	    numPagesElement.appendChild(numPagesText);
	    bsPdfElement.appendChild(numPagesElement);

	    /* Has Outline */
	    Element hasOutlineElement = doc.createElementNS(namespace, "HAS_OUTLINE");
	    String hasOutlineValue = (this.hasOutline() != null ? this.hasOutline() : "" );
	    Text hasOutlineText = doc.createTextNode(hasOutlineValue);
	    hasOutlineElement.appendChild(hasOutlineText);
	    bsPdfElement.appendChild(hasOutlineElement);

	    /* Has Images */
	    Element hasImagesElement = doc.createElementNS(namespace, "HAS_IMAGES");
	    String hasImagesValue = this.hasImages();
	    Text hasImagesText = doc.createTextNode(hasImagesValue);
	    hasImagesElement.appendChild(hasImagesText);
	    bsPdfElement.appendChild(hasImagesElement);

	    /* Has Metadata */
	    Element hasMetadataElement = doc.createElementNS(namespace, "HAS_METADATA");
	    String hasMetadataValue = this.hasMetadata();
	    Text hasMetadataText = doc.createTextNode(hasMetadataValue);
	    hasMetadataElement.appendChild(hasMetadataText);
	    bsPdfElement.appendChild(hasMetadataElement);
	    
	    /* NON-Standard Embedded Fonts */
	    Element embeddedFontsElement = doc.createElementNS(namespace, "NONSTANDARD_EMBEDDED_FONTS");
	    String embeddedFontsValue = (pdfFonts.getNonstandardEmbedded() != null ? pdfFonts.getNonstandardEmbedded() : "" );
	    Text embeddedFontsText = doc.createTextNode(embeddedFontsValue);
	    embeddedFontsElement.appendChild(embeddedFontsText);
	    bsPdfElement.appendChild(embeddedFontsElement);
	    
	    /* NON-Standard UnEmbedded Fonts */
	    Element unEmbeddedFontsElement = doc.createElementNS(namespace, "NONSTANDARD_UNEMBEDDED_FONTS");
	    String unEmbeddedFontsValue = (pdfFonts.getNonstandardUnEmbedded() != null ? pdfFonts.getNonstandardUnEmbedded() : "" );
	    Text unEmbeddedFontsText = doc.createTextNode(unEmbeddedFontsValue);
	    unEmbeddedFontsElement.appendChild(unEmbeddedFontsText);
	    bsPdfElement.appendChild(unEmbeddedFontsElement);

	    /* Actions */
		for (Iterator iter = this.getActions().iterator(); iter.hasNext();){
		    String action = (String) iter.next();
		    
		    // Action
		    Element actionElement = doc.createElementNS(namespace, "BS_PDF_ACTION");
		    rootElement.appendChild(actionElement);
		    
		    // BSID
		    Element absidElement = doc.createElementNS(namespace, "BSID");
		    absidElement.appendChild(doc.createTextNode(this.getBsid()));
		    actionElement.appendChild(absidElement);
		    
		    // PDF_ACTION
		    Element actElement = doc.createElementNS(namespace, "PDF_ACTION");
		    actElement.appendChild(doc.createTextNode(action));
		    actionElement.appendChild(actElement);
		}

	    /* Annotations */
		for (Iterator iter = this.getAnnotations().iterator(); iter.hasNext();){
		    String ann = (String) iter.next();
		    
		    // BS_PDF_ANNOTATION
		    Element annotationElement = doc.createElementNS(namespace, "BS_PDF_ANNOTATION");
		    rootElement.appendChild(annotationElement);
		    
		    // BSID
		    Element absidElement = doc.createElementNS(namespace, "BSID");
		    absidElement.appendChild(doc.createTextNode(this.getBsid()));
		    annotationElement.appendChild(absidElement);
		    
		    // PDF_ANNOTATION
		    Element annElement = doc.createElementNS(namespace, "PDF_ANNOTATION");
		    annElement.appendChild(doc.createTextNode(ann));
		    annotationElement.appendChild(annElement);
		}
		
	    /* Filters */		
		for (Iterator iter = this.getFilters().iterator(); iter.hasNext();){
		    String filt = (String) iter.next();

		    // BS_PDF_FILTER
		    Element filterElement = doc.createElementNS(namespace, "BS_PDF_FILTER");
		    rootElement.appendChild(filterElement);
		    
		    // BSID
		    Element fbsidElement = doc.createElementNS(namespace, "BSID");
		    fbsidElement.appendChild(doc.createTextNode(this.getBsid()));
		    filterElement.appendChild(fbsidElement);
		    
		    // PDF_FILTER
		    Element filtElement = doc.createElementNS(namespace, "PDF_FILTER");
		    filtElement.appendChild(doc.createTextNode(filt));
		    filterElement.appendChild(filtElement);

		}

	    return doc;
	}
	
	/**
	 * @throws FatalException
	 */
	public void evalMembers() throws FatalException {
	}

	/**
	 * @return a list of all the unique types of annotation contained in this
	 * 	PDF document.
	 */
	public Vector getAnnotations() {
		return this.annotations;
	}
	
	/**
	 * @return a list of all the unique types of filters contained in this
	 * 	PDF document.
	 */
	public Vector getFilters() {
		return this.filters;
	}

	/**
	 * Returns the default natural language of text in this PDF
	 * 
	 * @return  the default natural language of text in this PDF
	 */
	public String getNatlLang() {
		return this.natlLang;
	}

	/**
	 * Returns the number of pages in the PDF
	 * 
	 * @return the number of pages in the PDF
	 */
	public long getNumPages() {
		return this.numPages;
	}
	
	/**
	 * @return a list of all the unique types of actions contained in this
	 * 	PDF document.
	 */
	public Vector getActions() {
		return this.actions;
	}

	/**
	 * @return the page layout
	 */
	public String getPageLayout() {
		return this.pageLayout;
	}

	/**
	 * @return the page mode
	 */
	public String getPageMode() {
		return this.pageMode;
	}

	/**
	 * @return whether or not this PDF has any images
	 */
	public String hasImages() {
		return this.hasImages;
	}
	
	/**
	 * @return whether or not this PDF has any images
	 */
	public String hasMetadata() {
		return this.hasMetadata;
	}
	
	/**
	 * @return	whether or not this PDF contains an outline
	 */
	public String hasOutline() {
		return this.hasOutline;
	}

	/**
	 * @return whether or not this PDF has at least one instance 
	 * 	of a thumbnail image represention of a page.
	 */
	public String hasThumbnails() {
		return this.hasThumbnails;
	}

	/**
	 * @param _hasImages whether or not this PDF document contains
	 * 	any images
	 * @throws FatalException
	 */
	public void setHasImages(String _hasImages)
		throws FatalException {
		if (_hasImages != null &&
			(_hasImages.equals(Bitstream.TRUE) ||
			_hasImages.equals(Bitstream.FALSE) ||
			_hasImages.equals(Bitstream.UNKNOWN))){
					this.hasImages = _hasImages;
		} else {
			Informer.getInstance().fail(
				this, "setHasImages(String)",
				"Illegal argument", "_hasImages: " + _hasImages,
				new FatalException("Not a valid _hasImages")
			);
		}
	}
	
	/**
	 * @param _hasMetadata whether or not this PDF document contains
	 * 	a XML metadata stream
	 * @throws FatalException
	 */
	public void setHasMetadata(String _hasMetadata)
		throws FatalException {
		if (_hasMetadata != null &&
			(_hasMetadata.equals(Bitstream.TRUE) ||
			_hasMetadata.equals(Bitstream.FALSE) ||
			_hasMetadata.equals(Bitstream.UNKNOWN))){
					this.hasMetadata = _hasMetadata;
		} else {
			Informer.getInstance().fail(
				this, "setHasMetadata(String)",
				"Illegal argument", "_hasMetadata: " + _hasMetadata,
				new FatalException("Not a valid _hasMetadata")
			);
		}
	}
	/**
	 * Sets whether or not this PDF contains an outline.
	 * 
	 * @param _hasOutline	whether or not this PDF contains an outline
	 * @throws FatalException
	 */
	public void setHasOutline(String _hasOutline) throws FatalException {
		if (_hasOutline == null || 
				(!_hasOutline.equals(TRUE) && !_hasOutline.equals(FALSE) &&
				!_hasOutline.equals(UNKNOWN))){
				Informer.getInstance().fail(this, "setHasOutline(String)",
					"Bad _hasOutline value", "_hasOutline: " + _hasOutline,
					new FatalException("Not a valid _hasOutline value"));
			}
		this.hasOutline = _hasOutline;
	}

	/**
	 * @param _hasThumbnails	whether or not this PDF has at least one instance 
	 * 	of a thumbnail image represention of a page.
	 * @throws FatalException
	 */
	public void setHasThumbnails(String _hasThumbnails) 
		throws FatalException {
		if (_hasThumbnails == null || 
			(!_hasThumbnails.equals(TRUE) &&
			!_hasThumbnails.equals(FALSE) && 
			!_hasThumbnails.equals(UNKNOWN))){
				Informer.getInstance().fail(this,
					"setHasThumbnails(String)",
					"Illegal argument", 
					"_hasThumbnails: " + _hasThumbnails,
					new FatalException("Not a valid _hasThumbnails value"));
		}
		this.hasThumbnails = _hasThumbnails;
	}
	
	/**
	 * Sets the default natural language of text in this PDF
	 * 
	 * @param	_language	 the default natural language of text in 
	 * 			this PDF
	 * @throws FatalException
	 */
	public void setNatlLang(String _language) 
		throws FatalException {
		if (_language != null && 
			_language.length() <= MAX_NATL_LANG_LENGTH){
			this.natlLang = _language;
		} else {
			Informer.getInstance().fail(
				this, "setNatlLang(String)",
				"Illegal argument", "_language: " + _language,
				new FatalException("Not a valid natural language")
			);
		}
		
	}

	/**
	 * Sets the number of pages in this PDF document. Note that
	 * there are minimum and maximum supported number of pages.
	 * 
	 * @param _numPages	number of pages
	 * @throws FatalException
	 */
	public void setNumPages(int _numPages) 
		throws FatalException {
		if (_numPages < MIN_NUM_PAGES) {
			Informer.getInstance().fail(
				this, "setNumPages(int)", "Illegal argument", 
				"_numPages: " + numPages,
				new FatalException("Not a valid number of pages"));
		}
		if (_numPages > MAX_NUM_PAGES){
			// has more than the supported number of pages - log this and
			// try to record the number of pages anyway
			SevereElement pa = 
				this.getDf().getAnomsPossible().getSevereElement(PdfAnomalies.PDF_OVRLMT_NUM_PAGES);
			this.getDf().addAnomaly(pa);
		}
		this.numPages = _numPages;
	}

	/**
	 * Sets how the Pdf document's page layout  should be displayed. This
	 * method ignores attempts to set the page layout to illegal values.
	 * 
	 * @param _layout	the page layout
	 */
	public void setPageLayout(String _layout) {
		if (_layout.equals("SinglePage")){
			this.pageLayout = PAGE_LAYOUT_SINGLE;
		} else if (_layout.equals("OneColumn")){
			this.pageLayout = PAGE_LAYOUT_1_COL;
		}	else if (_layout.equals("TwoColumnLeft")){
			this.pageLayout = PAGE_LAYOUT_2_COL_L;
		}	else if (_layout.equals("TwoColumnRight")){
			this.pageLayout = PAGE_LAYOUT_2_COL_R;
		} 
	}

	/**
	 * Sets how the document should be displayed when it is first
	 * opened. This method ignores any attempts to set the page
	 * mode to illegal values.
	 * 
	 * @param _mode	the page mode
	 */
	public void setPageMode(String _mode) {
		if (_mode.equals("UseNone")){
			this.pageMode = PAGE_MODE_NONE;
		} else if (_mode.equals("UseOutlines")){
			this.pageMode = PAGE_MODE_OUTLINE;
		}	else if (_mode.equals("UseThumbs")){
			this.pageMode = PAGE_MODE_THUMB;
		}	else if (_mode.equals("FullScreen")){
			this.pageMode = PAGE_MODE_FULL;
		} 
	}

	/**
	 * @param _usesUnembeddedFonts whether or not this Pdf document 
	 * 	uses unembedded fonts
	 * @throws FatalException
	 */
	public void setUsesUnembeddedFonts(String _usesUnembeddedFonts) 
		throws FatalException {
		if (_usesUnembeddedFonts == null || 
			(!_usesUnembeddedFonts.equals(TRUE) &&
			!_usesUnembeddedFonts.equals(FALSE) && 
			!_usesUnembeddedFonts.equals(UNKNOWN))){
				Informer.getInstance().fail(this,
					"setUsesUnembeddedFonts(String)",
					"Illegal argument", 
					"_usesUnembeddedFonts: " + _usesUnembeddedFonts,
					new FatalException("Not a valid _usesUnembeddedFonts value"));
		}
		usesUnembeddedFonts = _usesUnembeddedFonts;
	}
	
	/**
	 * 
	 * @return the members of this class
	 */
	public String toString() {
		String prior = super.toString();
		StringBuffer sb = new StringBuffer("");
		sb.append(prior);
		
		Iterator iter = null;
		int count = 0;

		sb.append("\tAnnotation types:");
		if (this.getAnnotations() != null && this.getAnnotations().size() > 0) {
			sb.append("\n");
			iter = this.getAnnotations().iterator();
			count = 0;
			while (iter.hasNext()){
				String p = (String)iter.next();
				sb.append("\t\tAnnotation type " + count + ": " + p + "\n");
				count++;
			}
		} else {
			sb.append(this.getAnnotations() + "\n");
		}

		sb.append("\tFilter types:");
		if (this.getFilters() != null && this.getFilters().size() > 0) {
			sb.append("\n");
			iter = this.getFilters().iterator();
			count = 0;
			while (iter.hasNext()){
				String p = (String)iter.next();
				sb.append("\t\tFilter type " + count + ": " + p + "\n");
				count++;
			}
		} else {
			sb.append(this.getFilters() + "\n");
		}
		sb.append("\tHas images: " + this.hasImages() + "\n");
		sb.append("\tHas metadata: " + this.hasMetadata() + "\n");
		sb.append("\tHas outline: " + this.hasOutline() + "\n");
		sb.append("\tHas thumbnail images: " + this.hasThumbnails() + "\n");
		sb.append("\tNatural language: " + this.getNatlLang() + "\n");
		sb.append("\tNumber of pages: " + this.getNumPages() + "\n");
		sb.append("\tAction types:");
		if (this.getActions() != null && this.getActions().size() > 0) {
			sb.append("\n");
			iter = this.getActions().iterator();
			count = 0;
			while (iter.hasNext()){
				String p = (String)iter.next();
				sb.append("\t\tAction type " + count + ": " + p + "\n");
				count++;
			}
		} else {
			sb.append(this.getActions() + "\n");
		}
		sb.append("\tUses unembedded fonts: " + this.usesUnembeddedFonts() + "\n");
		//sb.append("\tPage layout: " + this.getPageLayout() + "\n");
		//sb.append("\tPage mode: " + this.getPageMode() + "\n");
		
		return sb.toString();
	}

	/**
	 * @return whether or not this Pdf document uses unembedded fonts
	 */
	public String usesUnembeddedFonts() {
		return this.usesUnembeddedFonts;
	}

}
