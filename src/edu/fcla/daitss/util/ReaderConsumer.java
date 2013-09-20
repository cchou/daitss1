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
/**
 *
 */
package edu.fcla.daitss.util;

import java.io.Reader;

import edu.fcla.daitss.exception.DaitssException;
import edu.fcla.daitss.exception.FatalException;

/**
 *  A multi-threaded reader of character streams. Doesn't stop reading
 * a character stream until the stream is completely read (or its interrupted).
 *
 * @author Andrea Goethals, FCLA
 */
public class ReaderConsumer implements Runnable {
	/**
	 * Will contain the data that has already been read. Starts with
	 * an initial capacity of 512 characters but will 'grow' if it needs to.
	 */
	private StringBuffer data = new StringBuffer(512);

	/**
	 * The reader of the data stream.
	 */
	private Reader reader;

	/**
	 * The thread running the reader.
	 */
	private Thread runningThread;

	/**
	 * Whether or not the reader should continue reading the data stream.
	 */
	private boolean shouldContinue = true;

	/**
	 * Must be called with a non-null Reader parameter.
	 * 
	 * @param reader A non-null java.io.Reader.
	 * @see java.io.Reader
	 * @throws FatalException
	 */
	public ReaderConsumer(Reader reader) throws FatalException {
		if (reader == null) {
			Informer.getInstance().fail(
				this, "ReaderConsumer(Reader)",
				"Null argument",
				"null argument: reader",
				new FatalException("Null Reader argument"));
		}
		this.reader = reader;
	}

	/**
	 * Returns the data stream that was read.
	 * 
	 * @return The data stream, either "" or contains text.
	 */
	public String getData() {
		// return the read data to the correct thread
		synchronized (data) {
			final String readData;
			if (hasData()) {
				// there was data to read
				readData = data.toString();
			} else {
				readData = "";
			}
			data.setLength(0);

			return readData;
		}
	}

	/**
	 * Determines if there was any text read in the data stream.
	 * 
	 * @return True if the data stream had any text, otherwise false.
	 */
	private boolean hasData() {
		return data.length() > 0;
	}

	/**
	 * Reads the data stream until its completely read. Throws and catches an 
	 * InterruptedException when it has read all of the data stream.
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		this.runningThread = Thread.currentThread();
		try {
			if (false) {
				// will never get here but makes compiler happy
				throw new InterruptedException("Exception in ReaderConsumer.");
			}
			while (shouldContinue) {
				// still data to read - read one character
				final int intCh = this.reader.read(); // might block
				if (intCh == -1) {
					// the end of the stream has been reached - stop reading
					shouldContinue = false;
					continue;
				}
				// add data to correct thread's data
				synchronized (data) {
					data.append((char) intCh);
				}
			}
		} catch (InterruptedException e) {
			// this is expected when the stream has been read - ignore
		} catch (Exception e) {
			// log the fatal error
			try {
				Informer.getInstance().fail(
					this, "run()",
					"Unexpected Exception",
					"unknown exception",
					e);
			} catch (DaitssException e1) {
				// TODO UNCAUGHT DAITSSEXCEPTION
			}
		}
	}

}