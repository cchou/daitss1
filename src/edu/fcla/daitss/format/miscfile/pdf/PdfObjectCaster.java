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
 * Created on Jan 23, 2004
 *
 */
package edu.fcla.daitss.format.miscfile.pdf;

import java.nio.CharBuffer;

/**
 * PdfObjectCaster
 * 
 * Credit for most of the methods in this class has to go to
 * com.etymon.pix.PdfName and com.etymon.pix.PdfString I just couldn't call
 * those directly because of their visibility.
 * 
 * @author Andrea Goethals, FCLA
 *  
 */
public class PdfObjectCaster {

	/**
	 * Test driver.
	 * 
	 * @param args not used
	 */
	public static void main(String[] args) {
		String testPdfName = "/A#23#42";
		System.out.println("The String of " + testPdfName + " is: "
				+ pdfNameToString(testPdfName));
	}

	/**
	 * Determines whether a character is a white-space character.
	 * 
	 * @param ch
	 *            the character to examine.
	 * @return <code>true</code> if the character is a white-space character.
	 *         Method taken from pjx code (couldn't use their's directly because
	 *         of its access modifier).
	 */
	public static boolean isWhiteSpace(char ch) {
		switch (ch) {
		case 0:
		case '\t':
		case '\n':
		case '\f':
		case '\r':
		case ' ':
			return true;
		default:
			return false;
		}
	}

	private static String decodeLiteralString(CharBuffer buf) {
		char ch, chc, che; // raw char, , escaped char
		boolean append;
		boolean escaping = false;
		int paren = 0; // tracks number of nested parentheses
		StringBuffer sb = new StringBuffer();
		char[] code = new char[3];
		int codeLen;
		boolean done = false;

		do {
			ch = buf.get();
			append = true;
			che = ch;
			if (escaping) {
				escaping = false;
				switch (ch) {
				case 'n':
					che = '\n';
					break;
				case 'r':
					che = '\r';
					break;
				case 't':
					che = '\t';
					break;
				case 'b':
					che = '\b';
					break;
				case 'f':
					che = '\f';
					break;
				case '(':
					che = '(';
					break;
				case ')':
					che = ')';
					break;
				case '\\':
					che = '\\';
					break;
				// octal numbers
				case '0':
				case '1':
				case '2':
				case '3':
				case '4':
				case '5':
				case '6':
				case '7':
				case '8':
				case '9':
					append = false;
					code[0] = ch;
					codeLen = 1;
					boolean code_done = false;
					do {
						chc = buf.get();
						switch (chc) {
						case '0':
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':
							if (codeLen < 3) {
								code[codeLen++] = chc;
								break;
							}
						default:
							code_done = true;
							buf.position(buf.position() - 1);
						}
					} while (!code_done);
					// convert octal number to base 10
					sb.append((char) Integer.parseInt(new String(code, 0,
							codeLen), 8));
					break;
				case '\r':
				case '\n':
					append = false;
					do {
						chc = buf.get();
					} while ((chc == '\r') || (chc == '\n'));
					buf.position(buf.position() - 1);
					break;
				default:
				}
			} else { // not escaping
				switch (ch) {
				case '(':
					paren++;
					break;
				case ')':
					if (paren > 0) {
						paren--;
					} else {
						append = false;
						done = true;
					}
					break;
				case '\\':
					escaping = true;
					append = false;
					break;
				case '\r':
					if (buf.get() != '\n') {
						buf.position(buf.position() - 1);
					}
					che = '\n';
					break;
				default:
				}
			}

			if (append) {
				sb.append(che);
			}
		} while (!done);

		return sb.toString();
	}

	private static String decodeHexString(CharBuffer buf) {
		char ch;
		boolean done = false;
		StringBuffer sb = new StringBuffer();
		char[] hex = new char[2];
		int hexLen = 0;

		do {
			ch = buf.get();
			switch (ch) {
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
			case 'A':
			case 'B':
			case 'C':
			case 'D':
			case 'E':
			case 'F':
			case 'a':
			case 'b':
			case 'c':
			case 'd':
			case 'e':
			case 'f':
				hex[hexLen] = ch;
				hexLen++;
				break;
			case '>':
				done = true;
			case 0:
			case '\t':
			case '\n':
			case '\f':
			case '\r':
			case ' ':
				break;
			default:
				throw new IllegalArgumentException(
						"Unrecognized character in hexadecimal string.");
			}

			if ((done) && (hexLen == 1)) {
				// odd number of digits -
				// add the final digit which is assumed to be 0
				hex[1] = '0';
				hexLen = 2;
			}

			if (hexLen >= 2) {
				sb.append((char) Integer.parseInt(new String(hex), 16));
				hexLen = 0;
			}

		} while (!done);

		return sb.toString();

	}

	/**
	 * Converts a name object in PDF format to a string value as stored by this
	 * class.
	 * 
	 * @param pdfName
	 *            the name object in PDF format.
	 * @return the name object as a String
	 * @throws IllegalArgumentException
	 */
	public static String pdfNameToString(String pdfName)
			throws IllegalArgumentException {
		String result = null;
		int len = pdfName.length();
		if (len >= 1 && pdfName.charAt(0) == '/') {
			StringBuffer sb = new StringBuffer(len);
			int x = 1;
			do {
				char ch = pdfName.charAt(x);
				if (ch == '#') {
					if ((x + 2) >= len) {
						throw new IllegalArgumentException(
								"Not a valid Pdf name object");
					}
					try {
						Integer code = Integer.valueOf(pdfName.substring(x + 1,
								x + 3), 16);
						sb.append((char) code.byteValue());
						x += 3;
					} catch (NumberFormatException e) {
						throw new IllegalArgumentException(
								"Not a valid Pdf name object");
					}
				} else {
					sb.append(ch);
					x++;
				}
			} while (x < len);
			result = sb.toString();

		} else {
			throw new IllegalArgumentException("Invalid Pdf name object");
		}

		return result;
	}

	/**
	 * Converts a string object in PDF format to a string value
	 * 
	 * Credit for most of this method has to go to
	 * com.etymon.pix.PdfName.pdfToString(String) I just couldn't call that
	 * because of its visibility.
	 * 
	 * @param pdfString the string object in PDF format.
	 * @return the string object as a String
	 * @throws IllegalArgumentException
	 */
	public static String pdfStringToString(String pdfString)
			throws IllegalArgumentException {
		CharBuffer buf = CharBuffer.allocate(pdfString.length());
		buf = buf.put(pdfString);

		// advance past any white-space
		char ch;
		do {
			ch = buf.get();
		} while (ch == 0 || ch == 9 || ch == 10 || ch == 12 || ch == 13
				|| ch == 32);
		// now ch should be either '(' or '<'
		if (ch == '(') {
			return decodeLiteralString(buf);
		} else if (ch == '<') {
			return decodeHexString(buf);
		} else {
			throw new IllegalArgumentException("Invalid Pdf String object");
		}

	}

}