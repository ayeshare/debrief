/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

//
// <copyright>
//
//  BBN Technologies
//  10 Moulton Street
//  Cambridge, MA 02138
//  (617) 873-8000
//
//  Copyright (C) BBNT Solutions LLC. All rights reserved.
//
// </copyright>
// **********************************************************************
//
// $Source: i:/mwc/coag/asset/cvsroot/util/MWC/GUI/S57/support/BinaryBufferedFile.java,v $
// $RCSfile: BinaryBufferedFile.java,v $
// $Revision: 1.1 $
// $Date: 2007/04/27 09:20:00 $
// $Author: ian.mayo $
//
// **********************************************************************

package MWC.GUI.S57.support;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;

import com.bbn.openmap.MoreMath;

/**
 * This class extends the BinaryFile class, doing buffered reads on the
 * underlying input file. The buffer size is not modifiable after construction,
 * and the buffer management isn't the greatest.
 */
public class BinaryBufferedFile extends BinaryFile {
	/**
	 * A simple factory method that lets you try to create something without having
	 * to really deal with failure. Returns a BinaryFile if successful, null if not.
	 */
	public static BinaryBufferedFile create(final String name) {
		return create(name, 4096);
	}

	/**
	 * A simple factory method that lets you try to create something without having
	 * to really deal with failure. Returns a BinaryFile if successful, null if not.
	 */
	public static BinaryBufferedFile create(final String name, final int buffersize) {
		BinaryBufferedFile bf = null;
		try {
			bf = new BinaryBufferedFile(name, buffersize);
		} catch (final IOException ioe) {
		}
		return bf;
	}

	/** Where reads get buffered */
	private byte buffer[];
	/** Where current reads will come from */
	private int curptr = 0;

	/** how many valid bytes are in the buffer */
	private int bytesinbuffer = 0;

	/** the byte offset of the first byte in the buffer */
	private long firstbyteoffset = 0;

	/**
	 * Constructs a BinaryBufferedFile with a File
	 *
	 * @param f the input file
	 * @exception IOException pass-through errors from opening a BinaryFile with f
	 * @see com.bbn.openmap.io.BinaryFile
	 */
	public BinaryBufferedFile(final File f) throws IOException {
		this(f, 4096);
	}

	/**
	 * Constructs a BinaryBufferedFile with a File and a buffer size
	 *
	 * @param f          the input file
	 * @param buffersize the size to use for buffering reads
	 * @exception IOException pass-through errors from opening a BinaryFile with f
	 * @see com.bbn.openmap.io.BinaryFile
	 */
	public BinaryBufferedFile(final File f, final int buffersize) throws IOException {
		super(f);
		buffer = new byte[buffersize];
	}

	/**
	 * Constructs a BinaryBufferedFile with a filename
	 *
	 * @param name the name/path of the input file
	 * @exception IOException pass-through errors from opening a BinaryFile with f
	 * @see com.bbn.openmap.io.BinaryFile
	 */
	public BinaryBufferedFile(final String name) throws IOException {
		this(name, 4096);
	}

	/**
	 * Constructs a BinaryBufferedFile with a filename and a buffersize
	 *
	 * @param name       the name/path of the input file
	 * @param buffersize the size to use for buffering reads
	 * @exception IOException pass-through errors from opening a BinaryFile with f
	 * @see com.bbn.openmap.io.BinaryFile
	 */
	public BinaryBufferedFile(final String name, final int buffersize) throws IOException {
		super(name);
		buffer = new byte[buffersize];
	}

	/**
	 * Forces the buffer to have at least some minimum number of bytes in it.
	 *
	 * @param minlength the minimum number of bytes to have in the buffer
	 * @exception FormatException couldn't get enough bytes (or IO Exception)
	 * @exception EOFException    couldn't get any bytes
	 */
	private void assertSize(final int minlength) throws FormatException, EOFException {
		try {
			if (bytesinbuffer < minlength) {
				if (curptr != 0) {
					firstbyteoffset += curptr;
					System.arraycopy(buffer, curptr, buffer, 0, bytesinbuffer);
					curptr = 0;
				}
				final int err = super.read(buffer, bytesinbuffer, buffer.length - bytesinbuffer);
				if (err == -1) {

					if (available() <= 0) {
						throw new EOFException("BinaryBufferedFile, no bytes at all, trying to read " + minlength);
					} else {
						throw new FormatException("BinaryBufferedFile: failed to read " + minlength + " bytes, with "
								+ bytesinbuffer + " bytes in the buffer and " + available()
								+ " bytes available, have read " + curptr + " bytes.");
					}
				}
				bytesinbuffer += err;
				assertSize(minlength);
			}
		} catch (final EOFException e) {
			throw e;
		} catch (final IOException i) {
			throw new FormatException("assertSize IOException: " + i.getMessage());
		}
	}

	@Override
	public long available() throws IOException {
		return (length() - firstbyteoffset - curptr);
	}

	/**
	 * closes the underlying file input, and releases some resources of the class.
	 * Calling any other members after this one will return bogus results.
	 *
	 * @exception IOException IO errors envountered in closing the file
	 */
	@Override
	public void close() throws IOException {
		buffer = null;
		super.close();
	}

	@Override
	public long getFilePointer() throws IOException {
		return (firstbyteoffset + curptr);
	}

	@Override
	public long length() throws IOException {
		return super.length();
	}

	@Override
	public int read() throws IOException {
		try {
			if (bytesinbuffer == 0)
				refillBuffer();
		} catch (final EOFException e) {
			return -1;
		}
		bytesinbuffer--;
		return MoreMath.signedToInt(buffer[curptr++]);
	}

	@Override
	public int read(final byte b[]) throws IOException {
		return read(b, 0, b.length);
	}

	/**
	 * Read from the file
	 *
	 * @param b      The byte array to read into
	 * @param theOff the first array position to read into
	 * @param theLen the number of bytes to read
	 * @return the number of bytes read
	 * @exception IOException Any IO errors encountered in reading from the file
	 */
	@Override
	public int read(final byte b[], final int off, final int len) throws IOException {
		int numread = 0;
		int copy;
		int theOff = off;
		int theLen = len;
		if (theLen < bytesinbuffer)
			copy = theLen;
		else
			copy = bytesinbuffer;
		numread += copy;
		bytesinbuffer -= copy;
		System.arraycopy(buffer, curptr, b, theOff, copy);
		curptr += copy;
		theOff += copy;

		if (theLen == copy)
			return numread;

		theLen -= copy;
		// was not enough stuff in buffer, do some reads...

		if (theLen > 512) {// threshold exceeded, read straight into user
							// buffer

			final int bcnt = super.read(b, theOff, theLen);
			firstbyteoffset += (curptr + bcnt);
			curptr = 0;
			return (numread + bcnt);

		} else { // refull buffer and recurse

			try {
				refillBuffer();
			} catch (final EOFException e) {
				return numread;
			}
			return (numread + read(b, theOff, theLen));

		}
	}

	/**
	 * Read from the file.
	 *
	 * @param howmany   the number of bytes to read
	 * @param allowless if we can return fewer bytes than requested
	 * @return the array of bytes read.
	 * @exception FormatException Any IO Exceptions, plus an end-of-file encountered
	 *                            after reading some, but now enough, bytes when
	 *                            allowless was <code>false</code>
	 * @exception EOFException    Encountered an end-of-file while allowless was
	 *                            <code>false</code>, but NO bytes had been read.
	 */
	@Override
	public byte[] readBytes(final int howmany, final boolean allowless) throws EOFException, FormatException {

		final byte foo[] = new byte[howmany];
		int gotsofar = 0;
		try {
			while (gotsofar < howmany) {
				final int err = read(foo, gotsofar, howmany - gotsofar);
				if (err == -1) {
					if (allowless) {
						/*
						 * return a smaller array, so the caller can tell how much they really got
						 */
						final byte retval[] = new byte[gotsofar];
						System.arraycopy(foo, 0, retval, 0, gotsofar);
						return retval;
					} else { // some kind of failure...
						if (gotsofar > 0) {
							throw new FormatException("EOF while reading");
						} else {
							throw new EOFException();
						}
					}
				}
				gotsofar += err;
			}
		} catch (final IOException i) {
			throw new FormatException("IOException reading file: " + i.getMessage());
		}
		return foo;
	}

	/**
	 * Reads and returns a single byte, cast to a char.
	 *
	 * @return the byte read from the file, cast to a char
	 * @exception EOFException    the end-of-file has been reached, so no chars
	 *                            where available
	 * @exception FormatException a rethrown IOException
	 */
	@Override
	public char readChar() throws EOFException, FormatException {
		try {
			final int retv = read();
			if (retv == -1) {
				throw new EOFException("Error in ReadChar, EOF reached");
			}
			return (char) retv;
		} catch (final IOException i) {
			throw new FormatException("IOException in ReadChar: " + i.getMessage());
		}
	}

	/**
	 * Reads <code>length</code> bytes and returns a string composed of the bytes
	 * cast to chars.
	 *
	 * @param length the number of bytes to read into the string
	 * @return the composed string
	 * @exception EOFException    there were less than <code>length</code> bytes
	 *                            left in the file
	 * @exception FormatException rethrow of IOExceptions encountered while reading
	 *                            the bytes for the short
	 */
	@Override
	public String readFixedLengthString(final int length) throws EOFException, FormatException {
		String retstring;
		if (length < buffer.length) {
			assertSize(length);
			retstring = new String(buffer, curptr, length);
			curptr += length;
			bytesinbuffer -= length;
		} else {
			final byte foo[] = readBytes(length, false);
			retstring = new String(foo, 0, length);
		}
		return retstring;
	}

	/**
	 * Reads an array of floats from the input.
	 *
	 * @param vec       the vector to read into
	 * @param theOffset the first float read goes into vec[offset]
	 * @param theLen    the number of floats to read from the stream
	 * @exception EOFException    not enough bytes were left in the file
	 * @exception FormatException rethrow of IOExceptions encountered while reading
	 *                            the bytes for the integer
	 */
	@Override
	public void readFloatArray(final float vec[], final int offset, final int len)
			throws EOFException, FormatException {
		int theOffset = offset;
		int theLen = len;
		while (theLen > 0) {
			final int floatsleft = bytesinbuffer / 4;
			if (floatsleft == 0) {
				assertSize(4); // force a buffer refill
				continue;
			}
			final int reallyread = (theLen < floatsleft) ? theLen : floatsleft;
			int cursor = curptr;
			if (MSBFirst) {
				for (int i = 0; i < reallyread; i++) {
					final int floatasint = MoreMath.BuildIntegerBE(buffer, cursor);
					vec[theOffset++] = Float.intBitsToFloat(floatasint);
					cursor += 4;
				}
			} else {
				for (int i = 0; i < reallyread; i++) {
					final int floatasint = MoreMath.BuildIntegerLE(buffer, cursor);
					vec[theOffset++] = Float.intBitsToFloat(floatasint);
					cursor += 4;
				}
			}
			theLen -= reallyread;
			bytesinbuffer -= (4 * reallyread);
			curptr = cursor;
		}
	}

	/**
	 * Reads and returns a long.
	 *
	 * @return the 4 bytes merged into a long, according to the current byte
	 *         ordering
	 * @exception EOFException    there were less than 4 bytes left in the file
	 * @exception FormatException rethrow of IOExceptions encountered while reading
	 *                            the bytes for the integer
	 */
	@Override
	public int readInteger() throws EOFException, FormatException {
		// MSBFirst must be set when we are called
		assertSize(4);
		curptr += 4;
		bytesinbuffer -= 4;
		return MoreMath.BuildInteger(buffer, curptr - 4, MSBFirst);
	}

	/**
	 * Reads an array of integers.
	 *
	 * @exception EOFException    there were fewer bytes than needed in the file
	 * @exception FormatException rethrow of IOExceptions encountered while reading
	 *                            the bytes for the array
	 */
	@Override
	public void readIntegerArray(final int vec[], final int offset, final int len)
			throws EOFException, FormatException {
		int theOffset = offset;
		int theLen = len;

		while (theLen > 0) {
			final int intsleft = bytesinbuffer / 4;
			if (intsleft == 0) {
				assertSize(4); // force a buffer refill
				continue;
			}
			final int reallyread = (theLen < intsleft) ? theLen : intsleft;
			int cursor = curptr;
			if (MSBFirst) {
				for (int i = 0; i < reallyread; i++) {
					vec[theOffset++] = MoreMath.BuildIntegerBE(buffer, cursor);
					cursor += 4;
				}
			} else {
				for (int i = 0; i < reallyread; i++) {
					vec[theOffset++] = MoreMath.BuildIntegerLE(buffer, cursor);
					cursor += 4;
				}
			}
			theLen -= reallyread;
			bytesinbuffer -= (4 * reallyread);
			curptr = cursor;
		}
	}

	/**
	 * Reads and returns a long.
	 *
	 * @return the 8 bytes merged into a long, according to the current byte
	 *         ordering
	 * @exception EOFException    there were less than 8 bytes left in the file
	 * @exception FormatException rethrow of IOExceptions encountered while reading
	 *                            the bytes for the long
	 * @see #read(byte[])
	 */
	@Override
	public long readLong() throws EOFException, FormatException {
		assertSize(8);
		curptr += 8;
		bytesinbuffer -= 8;
		return MoreMath.BuildLong(buffer, curptr - 8, MSBFirst);
	}

	/**
	 * Reads and returns a short.
	 *
	 * @return the 2 bytes merged into a short, according to the current byte
	 *         ordering
	 * @exception EOFException    there were less than 2 bytes left in the file
	 * @exception FormatException rethrow of IOExceptions encountered while reading
	 *                            the bytes for the short
	 * @see #read(byte[])
	 */
	@Override
	public short readShort() throws EOFException, FormatException {
		// MSBFirst must be set when we are called
		assertSize(2);
		curptr += 2;
		bytesinbuffer -= 2;
		return MoreMath.BuildShort(buffer, curptr - 2, MSBFirst);
	}

	/**
	 * Reads an array of shorts.
	 *
	 * @param vec       the array to write the shorts into
	 * @param theOffset the first array index to write to
	 * @param theLen    the number of shorts to read
	 * @exception EOFException    there were fewer bytes than needed in the file
	 * @exception FormatException rethrow of IOExceptions encountered while reading
	 *                            the bytes for the array
	 */
	public void readShortArray(final short vec[], final int offset, final int len)
			throws EOFException, FormatException {
		int theOffset = offset;
		int theLen = len;

		while (theLen > 0) {
			final int shortsleft = bytesinbuffer / 2;
			if (shortsleft == 0) {
				assertSize(2); // force a buffer refill - throws
								// exception if it can't
				continue;
			}
			final int reallyread = (theLen < shortsleft) ? theLen : shortsleft;
			if (MSBFirst) {
				for (int i = 0; i < reallyread; i++) {
					vec[theOffset++] = MoreMath.BuildShortBE(buffer, curptr);
					curptr += 2;
				}
			} else {
				for (int i = 0; i < reallyread; i++) {
					vec[theOffset++] = MoreMath.BuildShortLE(buffer, curptr);
					curptr += 2;
				}
			}
			theLen -= reallyread;
			bytesinbuffer -= (2 * reallyread);
		}
	}

	/**
	 * Throws away whatever data is in the buffer, and refills it.
	 *
	 * @exception IOException  IO errors encountered while refilling the buffer
	 * @exception EOFException no data was left in the file
	 */
	private void refillBuffer() throws IOException, EOFException {
		firstbyteoffset += (curptr + bytesinbuffer);
		final int err = super.read(buffer, 0, buffer.length);
		curptr = 0;
		if (err == -1)
			throw new EOFException();
		bytesinbuffer = err;
	}

	@Override
	public void seek(final long pos) throws IOException {
		final long relpos = pos - firstbyteoffset;
		if ((relpos >= 0) && (relpos < (curptr + bytesinbuffer))) {
			final int relcur = (int) relpos - curptr;
			if (relcur != 0) {
				bytesinbuffer -= relcur;
				curptr = (int) relpos;
			} // else we're already at the right place
		} else {
			super.seek(pos);
			firstbyteoffset = pos;
			bytesinbuffer = 0;
			curptr = 0;
		}
	}

	/**
	 * Set the input reader used by the BinaryFile. Make sure it's intialized
	 * properly. Assumes that the pointer is at the beginning of the file.
	 */
	@Override
	public void setInputReader(final InputReader reader) {
		super.setInputReader(reader);
		firstbyteoffset = 0;
		bytesinbuffer = 0;
		curptr = 0;
	}

	@Override
	public long skipBytes(final long n) throws IOException {
		if (n < bytesinbuffer) {
			bytesinbuffer -= n;
			curptr += n;
			return n;
		}
		final long oldbinb = bytesinbuffer;
		bytesinbuffer = 0;
		curptr = 0;
		final int skipcnt = (int) super.skipBytes(n - oldbinb);
		firstbyteoffset += skipcnt;
		return (oldbinb + skipcnt);
	}
}