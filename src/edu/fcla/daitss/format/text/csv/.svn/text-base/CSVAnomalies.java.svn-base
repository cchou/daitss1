package edu.fcla.daitss.format.text.csv;

import edu.fcla.daitss.exception.FatalException;
import edu.fcla.daitss.severe.element.Anomalies;
import edu.fcla.daitss.severe.element.Severity;

/**
 * TextAnomalies
 * Note that all anomaly values must be less than 255 characters 
 * and need to start with "A_", anomaly descriptions
 * must be less than 255 characters.
 * 
 * @author Carol Chou, FCLA
 *
 */
public class CSVAnomalies extends Anomalies {
	
		/**
		 * CSV contains variable columns in each row
		 */
		public static final String CSV_VARIABLE_COLUMNS = 
			"A_CSV_VARIABLE_COLUMNS";
			
		/**
		 * use non-conformed escape character for doube quote in quoted string
		 */
		public static final String CSV_BAD_ESCAPE_CHAR = 
			"A_CSV_BAD_ESCAPE_CHAR";
			
		/**
		 * a row was ended with comma (not conformed to RFC 4180)
		 */
		public static final String CSV_ROW_ENDED_WITH_COMMA = 
			"A_CSV_ROW_ENDED_WITH_COMMA";
		
		/**
		 * Builds the list of known Pdf-specific anomalies
		 * 
		 * @throws FatalException
		 */
		public CSVAnomalies() throws FatalException {
			super();
			buildAnoms();
		}
		
		/**
		 * Builds the list of known Text file-specific anomalies.
		 * 
		 * @throws FatalException
		 */
		private void buildAnoms() throws FatalException {
			insert(CSV_VARIABLE_COLUMNS,
				Severity.SEVERITY_NOTE,
				"CSV contains variable columns in each row");

			insert(CSV_BAD_ESCAPE_CHAR,
				Severity.SEVERITY_NOTE,
				"use non-conformed escape character for doube quote in quoted string");

			insert(CSV_ROW_ENDED_WITH_COMMA,
				Severity.SEVERITY_NOTE,
				"a row was ended with comma (not conformed to RFC 4180)");

		}
}
