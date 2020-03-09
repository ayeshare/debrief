package edu.nps.moves.dis;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * Section 5.3.12.2: Removal of an entity , reliable. COMPLETE
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class RemoveEntityReliablePdu extends SimulationManagementWithReliabilityFamilyPdu implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** level of reliability service used for this transaction */
	protected short requiredReliabilityService;

	/** padding */
	protected int pad1;

	/** padding */
	protected short pad2;

	/** Request ID */
	protected long requestID;

	/** Constructor */
	public RemoveEntityReliablePdu() {
		setPduType((short) 52);
	}

	/*
	 * The equals method doesn't always work--mostly it works only on classes that
	 * consist only of primitives. Be careful.
	 */
	@Override
	public boolean equals(final Object obj) {

		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (getClass() != obj.getClass())
			return false;

		return equalsImpl(obj);
	}

	@Override
	public boolean equalsImpl(final Object obj) {
		boolean ivarsEqual = true;

		if (!(obj instanceof RemoveEntityReliablePdu))
			return false;

		final RemoveEntityReliablePdu rhs = (RemoveEntityReliablePdu) obj;

		if (!(requiredReliabilityService == rhs.requiredReliabilityService))
			ivarsEqual = false;
		if (!(pad1 == rhs.pad1))
			ivarsEqual = false;
		if (!(pad2 == rhs.pad2))
			ivarsEqual = false;
		if (!(requestID == rhs.requestID))
			ivarsEqual = false;

		return ivarsEqual && super.equalsImpl(rhs);
	}

	@Override
	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = super.getMarshalledSize();
		marshalSize = marshalSize + 1; // requiredReliabilityService
		marshalSize = marshalSize + 2; // pad1
		marshalSize = marshalSize + 1; // pad2
		marshalSize = marshalSize + 4; // requestID

		return marshalSize;
	}

	public int getPad1() {
		return pad1;
	}

	public short getPad2() {
		return pad2;
	}

	public long getRequestID() {
		return requestID;
	}

	public short getRequiredReliabilityService() {
		return requiredReliabilityService;
	}

	@Override
	public void marshal(final DataOutputStream dos) {
		super.marshal(dos);
		try {
			dos.writeByte((byte) requiredReliabilityService);
			dos.writeShort((short) pad1);
			dos.writeByte((byte) pad2);
			dos.writeInt((int) requestID);
		} // end try
		catch (final Exception e) {
			System.out.println(e);
		}
	} // end of marshal method

	/**
	 * Packs a Pdu into the ByteBuffer.
	 *
	 * @throws java.nio.BufferOverflowException if buff is too small
	 * @throws java.nio.ReadOnlyBufferException if buff is read only
	 * @see java.nio.ByteBuffer
	 * @param buff The ByteBuffer at the position to begin writing
	 * @since ??
	 */
	@Override
	public void marshal(final java.nio.ByteBuffer buff) {
		super.marshal(buff);
		buff.put((byte) requiredReliabilityService);
		buff.putShort((short) pad1);
		buff.put((byte) pad2);
		buff.putInt((int) requestID);
	} // end of marshal method

	public void setPad1(final int pPad1) {
		pad1 = pPad1;
	}

	public void setPad2(final short pPad2) {
		pad2 = pPad2;
	}

	public void setRequestID(final long pRequestID) {
		requestID = pRequestID;
	}

	public void setRequiredReliabilityService(final short pRequiredReliabilityService) {
		requiredReliabilityService = pRequiredReliabilityService;
	}

	@Override
	public void unmarshal(final DataInputStream dis) {
		super.unmarshal(dis);

		try {
			requiredReliabilityService = (short) dis.readUnsignedByte();
			pad1 = dis.readUnsignedShort();
			pad2 = (short) dis.readUnsignedByte();
			requestID = dis.readInt();
		} // end try
		catch (final Exception e) {
			System.out.println(e);
		}
	} // end of unmarshal method

	/**
	 * Unpacks a Pdu from the underlying data.
	 *
	 * @throws java.nio.BufferUnderflowException if buff is too small
	 * @see java.nio.ByteBuffer
	 * @param buff The ByteBuffer at the position to begin reading
	 * @since ??
	 */
	@Override
	public void unmarshal(final java.nio.ByteBuffer buff) {
		super.unmarshal(buff);

		requiredReliabilityService = (short) (buff.get() & 0xFF);
		pad1 = buff.getShort() & 0xFFFF;
		pad2 = (short) (buff.get() & 0xFF);
		requestID = buff.getInt();
	} // end of unmarshal method
} // end of class
