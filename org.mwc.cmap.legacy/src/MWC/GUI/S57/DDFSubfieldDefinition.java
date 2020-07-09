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

package MWC.GUI.S57;

import java.text.ParseException;

import com.bbn.openmap.MoreMath;
import com.bbn.openmap.layer.vpf.MutableInt;
import com.bbn.openmap.util.Debug;

import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

/**
 * Information from the DDR record describing one subfield of a DDFFieldDefn.
 * All subfields of a field will occur in each occurance of that field (as a
 * DDFField) in a DDFRecord. Subfield's actually contain formatted data (as
 * instances within a record).
 *
 * @author Guillaume Pelletier provided fix for Big Endian support (important
 *         for S-57)
 */
public class DDFSubfieldDefinition implements DDFConstants {

	public interface DDFBinaryFormat {
		public final static int NotBinary = 0;
		public final static int UInt = 1;
		public final static int SInt = 2;
		public final static int FPReal = 3;
		public final static int FloatReal = 4;
		public final static int FloatComplex = 5;
	}

	protected String pszName; // a.k.a. subfield mnemonic
	protected String pszFormatString;
	protected DDFDataType eType;

	protected int eBinaryFormat;

	/**
	 * bIsVariable determines whether we using the chFormatDelimeter (true), or the
	 * fixed width (false).
	 */
	protected boolean bIsVariable;
	protected char chFormatDelimeter;

	protected int nFormatWidth;

	public DDFSubfieldDefinition() {
		pszName = null;

		bIsVariable = true;
		nFormatWidth = 0;
		chFormatDelimeter = DDF_UNIT_TERMINATOR;
		eBinaryFormat = DDFBinaryFormat.NotBinary;
		eType = DDFDataType.DDFString;

		pszFormatString = new String("");
	}

	/**
	 * Dump subfield value to debugging file.
	 *
	 * @param pachData  Pointer to data for this subfield.
	 * @param nMaxBytes Maximum number of bytes available in pachData.
	 */
	public String dumpData(final byte[] pachData, final int nMaxBytes) {
		final StringBuffer sb = new StringBuffer();
		if (eType == DDFDataType.DDFFloat) {
			sb.append("      Subfield " + pszName + "=" + extractFloatData(pachData, nMaxBytes, null) + "\n");
		} else if (eType == DDFDataType.DDFInt) {
			sb.append("      Subfield " + pszName + "=" + extractIntData(pachData, nMaxBytes, null) + "\n");
		} else if (eType == DDFDataType.DDFBinaryString) {
			sb.append("      Subfield " + pszName + "=" + extractStringData(pachData, nMaxBytes, null) + "\n");
		} else {
			sb.append("      Subfield " + pszName + "=" + extractStringData(pachData, nMaxBytes, null) + "\n");
		}
		return sb.toString();
	}

	/**
	 * Extract a subfield value as a float. Given a pointer to the data for this
	 * subfield (from within a DDFRecord) this method will return the floating point
	 * data for this subfield. The number of bytes consumed as part of this field
	 * can also be fetched. This method may be called for any type of subfield, and
	 * will return zero if the subfield is not numeric.
	 *
	 * @param pachSourceData  The pointer to the raw data for this field. This may
	 *                        have come from DDFRecord::GetData(), taking into
	 *                        account skip factors over previous subfields data.
	 * @param nMaxBytes       The maximum number of bytes that are accessable after
	 *                        pachSourceData.
	 * @param pnConsumedBytes Pointer to an integer into which the number of bytes
	 *                        consumed by this field should be written. May be null
	 *                        to ignore. This is used as a skip factor to increment
	 *                        pachSourceData to point to the next subfields data.
	 *
	 * @return The subfield's numeric value (or zero if it isn't numeric).
	 */
	public double extractFloatData(final byte[] pachSourceData, final int nMaxBytes, final MutableInt pnConsumedBytes) {

		switch (pszFormatString.charAt(0)) {
		case 'A':
		case 'I':
		case 'R':
		case 'S':
		case 'C':
			final String dataString = extractStringData(pachSourceData, nMaxBytes, pnConsumedBytes);

			if (dataString.equals("")) {
				return 0;
			}

			try {
				return MWCXMLReader.readThisDouble(dataString);
			} catch (final ParseException pe) {
				if (Debug.debugging("iso8211")) {
					Debug.output("DDFSubfieldDefinition.extractFloatData: number format problem: " + dataString);
				}
				return 0;
			}

		case 'B':
		case 'b':
			final byte[] abyData = new byte[8];

			if (pnConsumedBytes != null) {
				pnConsumedBytes.value = nFormatWidth;
			}

			if (nFormatWidth > nMaxBytes) {
				Debug.error("DDFSubfieldDefinition: format width is greater than max bytes for float");
				return 0.0;
			}

			// Byte swap the data if it isn't in machine native
			// format. In any event we copy it into our buffer to
			// ensure it is word aligned.
			//
			// DFD - don't think this applies to Java, since it's
			// always big endian

			// if (pszFormatString.charAt(0) == 'B') ||
			// (pszFormatString.charAt(0) == 'b') {
			// for (int i = 0; i < nFormatWidth; i++) {
			// abyData[nFormatWidth-i-1] = pachSourceData[i];
			// }
			// } else {
			// System.arraycopy(pachSourceData, 0, abyData, 8-nFormatWidth,
			// nFormatWidth);
			System.arraycopy(pachSourceData, 0, abyData, 0, nFormatWidth);
			// }

			// Interpret the bytes of data.
			switch (eBinaryFormat) {
			case DDFBinaryFormat.UInt:
			case DDFBinaryFormat.SInt:
			case DDFBinaryFormat.FloatReal:
				return pszFormatString.charAt(0) == 'B' ? MoreMath.BuildIntegerBE(abyData)
						: MoreMath.BuildIntegerLE(abyData);

			// if (nFormatWidth == 1)
			// return(abyData[0]);
			// else if (nFormatWidth == 2)
			// return(*((GUInt16 *) abyData));
			// else if (nFormatWidth == 4)
			// return(*((GUInt32 *) abyData));
			// else {
			// return 0.0;
			// }

			// case DDFBinaryFormat.SInt:
			// if (nFormatWidth == 1)
			// return(*((signed char *) abyData));
			// else if (nFormatWidth == 2)
			// return(*((GInt16 *) abyData));
			// else if (nFormatWidth == 4)
			// return(*((GInt32 *) abyData));
			// else {
			// return 0.0;
			// }

			// case DDFBinaryFormat.FloatReal:
			// if (nFormatWidth == 4)
			// return(*((float *) abyData));
			// else if (nFormatWidth == 8)
			// return(*((double *) abyData));
			// else {
			// return 0.0;
			// }

			case DDFBinaryFormat.NotBinary:
			case DDFBinaryFormat.FPReal:
			case DDFBinaryFormat.FloatComplex:
				return 0.0;
			}
			break;
		// end of 'b'/'B' case.

		default:

		}

		return 0.0;
	}

	/**
	 * Extract a subfield value as an integer. Given a pointer to the data for this
	 * subfield (from within a DDFRecord) this method will return the int data for
	 * this subfield. The number of bytes consumed as part of this field can also be
	 * fetched. This method may be called for any type of subfield, and will return
	 * zero if the subfield is not numeric.
	 *
	 * @param pachSourceData  The pointer to the raw data for this field. This may
	 *                        have come from DDFRecord::GetData(), taking into
	 *                        account skip factors over previous subfields data.
	 * @param nMaxBytes       The maximum number of bytes that are accessable after
	 *                        pachSourceData.
	 * @param pnConsumedBytes Pointer to an integer into which the number of bytes
	 *                        consumed by this field should be written. May be null
	 *                        to ignore. This is used as a skip factor to increment
	 *                        pachSourceData to point to the next subfields data.
	 *
	 * @return The subfield's numeric value (or zero if it isn't numeric).
	 */
	public int extractIntData(final byte[] pachSourceData, final int nMaxBytes, final MutableInt pnConsumedBytes) {

		switch (pszFormatString.charAt(0)) {
		case 'A':
		case 'I':
		case 'R':
		case 'S':
		case 'C':
			final String dataString = extractStringData(pachSourceData, nMaxBytes, pnConsumedBytes);
			if (dataString.equals("")) {
				return 0;
			}

			try {
				return new Double(MWCXMLReader.readThisDouble(dataString)).intValue();
			} catch (final ParseException pe) {
				if (Debug.debugging("iso8211")) {
					Debug.output("DDFSubfieldDefinition.extractIntData: number format problem: " + dataString);
				}
				return 0;
			}

		case 'B':
		case 'b':
			final byte[] abyData = new byte[4];
			if (nFormatWidth > nMaxBytes) {
				Debug.error("DDFSubfieldDefinition: format width is greater than max bytes for int");
				return 0;
			}

			if (pnConsumedBytes != null) {
				pnConsumedBytes.value = nFormatWidth;
			}

			// System.arraycopy(pachSourceData, 0, abyData, 4-nFormatWidth,
			// nFormatWidth);
			System.arraycopy(pachSourceData, 0, abyData, 0, nFormatWidth);

			// Interpret the bytes of data.
			switch (eBinaryFormat) {
			case DDFBinaryFormat.UInt:
			case DDFBinaryFormat.SInt:
			case DDFBinaryFormat.FloatReal:
				return pszFormatString.charAt(0) == 'B' ? MoreMath.BuildIntegerBE(abyData)
						: MoreMath.BuildIntegerLE(abyData);

			// case DDFBinaryFormat.UInt:
			// if (nFormatWidth == 4)
			// return((int) *((GUInt32 *) abyData));
			// else if (nFormatWidth == 1)
			// return(abyData[0]);
			// else if (nFormatWidth == 2)
			// return(*((GUInt16 *) abyData));
			// else {
			// CPLAssert(false);
			// return 0;
			// }

			// case DDFBinaryFormat.SInt:
			// if (nFormatWidth == 4)
			// return(*((GInt32 *) abyData));
			// else if (nFormatWidth == 1)
			// return(*((signed char *) abyData));
			// else if (nFormatWidth == 2)
			// return(*((GInt16 *) abyData));
			// else {
			// CPLAssert(false);
			// return 0;
			// }

			// case DDFBinaryFormat.FloatReal:
			// if (nFormatWidth == 4)
			// return((int) *((float *) abyData));
			// else if (nFormatWidth == 8)
			// return((int) *((double *) abyData));
			// else {
			// CPLAssert(false);
			// return 0;
			// }

			case DDFBinaryFormat.NotBinary:
			case DDFBinaryFormat.FPReal:
			case DDFBinaryFormat.FloatComplex:
				return 0;
			}
			break;
		// end of 'b'/'B' case.

		default:
			return 0;
		}

		return 0;
	}

	/**
	 * Extract a zero terminated string containing the data for this subfield. Given
	 * a pointer to the data for this subfield (from within a DDFRecord) this method
	 * will return the data for this subfield. The number of bytes consumed as part
	 * of this field can also be fetched. This number may be one longer than the
	 * string length if there is a terminator character used.
	 * <p>
	 *
	 * This function will return the raw binary data of a subfield for types other
	 * than DDFString, including data past zero chars. This is the standard way of
	 * extracting DDFBinaryString subfields for instance.
	 * <p>
	 *
	 * @param pachSourceData  The pointer to the raw data for this field. This may
	 *                        have come from DDFRecord::GetData(), taking into
	 *                        account skip factors over previous subfields data.
	 * @param nMaxBytes       The maximum number of bytes that are accessable after
	 *                        pachSourceData.
	 * @param pnConsumedBytes Pointer to an integer into which the number of bytes
	 *                        consumed by this field should be written. May be null
	 *                        to ignore. This is used as a skip factor to increment
	 *                        pachSourceData to point to the next subfields data.
	 *
	 * @return A pointer to a buffer containing the data for this field. The
	 *         returned pointer is to an internal buffer which is invalidated on the
	 *         next ExtractStringData() call on this DDFSubfieldDefn(). It should
	 *         not be freed by the application.
	 */
	String extractStringData(final byte[] pachSourceData, final int nMaxBytes, final MutableInt pnConsumedBytes) {
		int oldConsumed = 0;
		if (pnConsumedBytes != null) {
			oldConsumed = pnConsumedBytes.value;
		}

		final int nLength = getDataLength(pachSourceData, nMaxBytes, pnConsumedBytes);

		final char[] str = new char[nLength];
		for (int i = 0; i < nLength; i++) {
			int thisI = pachSourceData[i];
			if (thisI < 0)
				thisI += 256;
			final char thisC = (char) thisI;
			str[i] = thisC;
		}

		// String ns = new String(pachSourceData, 0, nLength);
		final String ns = new String(str);

		if (Debug.debugging("iso8211detail")) {
			if (pnConsumedBytes != null) {
				Debug.output("        extracting string data from " + nLength + " bytes of " + pachSourceData.length
						+ ": " + ns + ": consumed " + pnConsumedBytes.value + " vs. " + oldConsumed + ", max = "
						+ nMaxBytes);
			}
		}

		return ns;
	}

	/**
	 * Scan for the end of variable length data. Given a pointer to the data for
	 * this subfield (from within a DDFRecord) this method will return the number of
	 * bytes which are data for this subfield. The number of bytes consumed as part
	 * of this field can also be fetched. This number may be one longer than the
	 * length if there is a terminator character used.
	 * <p>
	 *
	 * This method is mainly for internal use, or for applications which want the
	 * raw binary data to interpret themselves. Otherwise use one of
	 * ExtractStringData(), ExtractIntData() or ExtractFloatData().
	 *
	 * @param pachSourceData  The pointer to the raw data for this field. This may
	 *                        have come from DDFRecord::GetData(), taking into
	 *                        account skip factors over previous subfields data.
	 * @param nMaxBytes       The maximum number of bytes that are accessable after
	 *                        pachSourceData.
	 * @param pnConsumedBytes the number of bytes used.
	 *
	 * @return The number of bytes at pachSourceData which are actual data for this
	 *         record (not including unit, or field terminator).
	 */
	public int getDataLength(final byte[] pachSourceData, final int nMaxBytes, final MutableInt pnConsumedBytes) {
		if (!bIsVariable) {
			if (nFormatWidth > nMaxBytes) {
				Debug.error("DDFSubfieldDefinition: Only " + nMaxBytes + " bytes available for subfield " + pszName
						+ " with format string " + pszFormatString + " ... returning shortened data.");

				if (pnConsumedBytes != null) {
					pnConsumedBytes.value = nMaxBytes;
				}

				return nMaxBytes;
			} else {

				if (pnConsumedBytes != null) {
					pnConsumedBytes.value = nFormatWidth;
				}

				return nFormatWidth;
			}

		} else {

			int nLength = 0;
			boolean bCheckFieldTerminator = true;

			/*
			 * We only check for the field terminator because of some buggy datasets with
			 * missing format terminators. However, we have found the field terminator is a
			 * legal character within the fields of some extended datasets (such as
			 * JP34NC94.000). So we don't check for the field terminator if the field
			 * appears to be multi-byte which we established by the first character being
			 * out of the ASCII printable range (32-127).
			 */

			if (pachSourceData[0] < 32 || pachSourceData[0] >= 127) {
				bCheckFieldTerminator = false;
			}

			while (nLength < nMaxBytes && pachSourceData[nLength] != chFormatDelimeter) {

				if (bCheckFieldTerminator && pachSourceData[nLength] == DDF_FIELD_TERMINATOR)
					break;

				nLength++;
			}

			if (pnConsumedBytes != null) {
				if (nMaxBytes == 0) {
					pnConsumedBytes.value = nLength;
				} else {
					pnConsumedBytes.value = nLength + 1;
				}
			}

			return nLength;
		}
	}

	/** Get pointer to subfield format string */
	public String getFormat() {
		return pszFormatString;
	}

	/** Get pointer to subfield name. */
	public String getName() {
		return pszName;
	}

	/**
	 * Get the general type of the subfield. This can be used to determine which of
	 * ExtractFloatData(), ExtractIntData() or ExtractStringData() should be used.
	 *
	 * @return The subfield type. One of DDFInt, DDFFloat, DDFString or
	 *         DDFBinaryString.
	 */
	public DDFDataType getType() {
		return eType;
	}

	public int getWidth() {
		return nFormatWidth;
	}

	/**
	 * While interpreting the format string we don't support:
	 * <UL>
	 * <LI>Passing an explicit terminator for variable length field.
	 * <LI>'X' for unused data ... this should really be filtered
	 * <LI>out by DDFFieldDefinition.applyFormats(), but isn't.
	 * <LI>'B' bitstrings that aren't a multiple of eight.
	 * </UL>
	 */
	public boolean setFormat(final String pszFormat) {
		pszFormatString = pszFormat;

		if (Debug.debugging("iso8211")) {
			Debug.output("DDFSubfieldDefinition.setFormat(" + pszFormat + ")");
		}

		/* -------------------------------------------------------------------- */
		/* These values will likely be used. */
		/* -------------------------------------------------------------------- */
		if (pszFormatString.length() > 1 && pszFormatString.charAt(1) == '(') {

			// Need to loop through characters to grab digits, and
			// then get integer version. If we look a the atoi code,
			// it checks for non-digit characters and then stops.
			int i = 3;
			for (; i < pszFormat.length() && Character.isDigit(pszFormat.charAt(i)); i++) {
			}

			nFormatWidth = Integer.parseInt(pszFormat.substring(2, i));
			bIsVariable = (nFormatWidth == 0);
		} else {
			bIsVariable = true;
		}

		/* -------------------------------------------------------------------- */
		/* Interpret the format string. */
		/* -------------------------------------------------------------------- */
		switch (pszFormatString.charAt(0)) {

		case 'A':
		case 'C': // It isn't clear to me how this is different than
			// 'A'
			eType = DDFDataType.DDFString;
			break;

		case 'R':
			eType = DDFDataType.DDFFloat;
			break;

		case 'I':
		case 'S':
			eType = DDFDataType.DDFInt;
			break;

		case 'B':
		case 'b':
			// Is the width expressed in bits? (is it a bitstring)
			bIsVariable = false;
			if (pszFormatString.charAt(1) == '(') {

				int numEndIndex = 2;
				for (; numEndIndex < pszFormatString.length()
						&& Character.isDigit(pszFormatString.charAt(numEndIndex)); numEndIndex++) {
				}

				final String numberString = pszFormatString.substring(2, numEndIndex);
				nFormatWidth = Integer.valueOf(numberString).intValue();

				if (nFormatWidth % 8 != 0) {
					Debug.error("DDFSubfieldDefinition.setFormat() problem with " + pszFormatString.charAt(0)
							+ " not being modded with 8 evenly");
					return false;
				}

				nFormatWidth = Integer.parseInt(numberString) / 8;

				eBinaryFormat = DDFBinaryFormat.SInt; // good
				// default,
				// works for
				// SDTS.

				if (nFormatWidth < 5) {
					eType = DDFDataType.DDFInt;
				} else {
					eType = DDFDataType.DDFBinaryString;
				}

			} else { // or do we have a binary type indicator? (is it binary)

				eBinaryFormat = pszFormatString.charAt(1) - '0';

				int numEndIndex = 2;
				for (; numEndIndex < pszFormatString.length()
						&& Character.isDigit(pszFormatString.charAt(numEndIndex)); numEndIndex++) {
				}

				nFormatWidth = Integer.valueOf(pszFormatString.substring(2, numEndIndex)).intValue();

				if (eBinaryFormat == DDFBinaryFormat.SInt || eBinaryFormat == DDFBinaryFormat.UInt) {

					eType = DDFDataType.DDFInt;
				} else {
					eType = DDFDataType.DDFFloat;
				}
			}
			break;

		case 'X':
			// 'X' is extra space, and shouldn't be directly assigned
			// to a
			// subfield ... I haven't encountered it in use yet
			// though.
			Debug.error("DDFSubfieldDefinition: Format type of " + pszFormatString.charAt(0) + " not supported.");

			return false;
		default:
			Debug.error("DDFSubfieldDefinition: Format type of " + pszFormatString.charAt(0) + " not recognised.");
			return false;
		}

		return true;
	}

	/**
	 * Set the name of the subfield.
	 */
	public void setName(final String pszNewName) {
		pszName = pszNewName.trim();
	}

	/**
	 * Write out subfield definition info. A variety of information about this field
	 * definition is written to the give debugging file handle.
	 */
	@Override
	public String toString() {
		final StringBuffer buf = new StringBuffer("    DDFSubfieldDefn:\n");
		buf.append("        Label = " + pszName + "\n");
		buf.append("        FormatString = " + pszFormatString + "\n");
		return buf.toString();
	}

}
