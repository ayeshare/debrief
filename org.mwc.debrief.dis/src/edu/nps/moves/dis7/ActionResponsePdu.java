package edu.nps.moves.dis7;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Section 7.5.8. response to an action request PDU. COMPLETE
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class ActionResponsePdu extends SimulationManagementFamilyPdu implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** Identifier for originating entity(or simulation) */
	protected EntityID originatingID = new EntityID();

	/** Identifier for the receiving entity(or simulation) */
	protected EntityID receivingID = new EntityID();

	/** Request ID that is unique */
	protected long requestID;

	/** Status of response */
	protected long requestStatus;

	/** Number of fixed datum records */
	protected long numberOfFixedDatumRecords;

	/** Number of variable datum records */
	protected long numberOfVariableDatumRecords;

	/** variable length list of fixed datums */
	protected List<FixedDatum> fixedDatums = new ArrayList<FixedDatum>();
	/** variable length list of variable length datums */
	protected List<VariableDatum> variableDatums = new ArrayList<VariableDatum>();

	/** Constructor */
	public ActionResponsePdu() {
		setPduType((short) 17);
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

		if (!(obj instanceof ActionResponsePdu))
			return false;

		final ActionResponsePdu rhs = (ActionResponsePdu) obj;

		if (!(originatingID.equals(rhs.originatingID)))
			ivarsEqual = false;
		if (!(receivingID.equals(rhs.receivingID)))
			ivarsEqual = false;
		if (!(requestID == rhs.requestID))
			ivarsEqual = false;
		if (!(requestStatus == rhs.requestStatus))
			ivarsEqual = false;
		if (!(numberOfFixedDatumRecords == rhs.numberOfFixedDatumRecords))
			ivarsEqual = false;
		if (!(numberOfVariableDatumRecords == rhs.numberOfVariableDatumRecords))
			ivarsEqual = false;

		for (int idx = 0; idx < fixedDatums.size(); idx++) {
			if (!(fixedDatums.get(idx).equals(rhs.fixedDatums.get(idx))))
				ivarsEqual = false;
		}

		for (int idx = 0; idx < variableDatums.size(); idx++) {
			if (!(variableDatums.get(idx).equals(rhs.variableDatums.get(idx))))
				ivarsEqual = false;
		}

		return ivarsEqual && super.equalsImpl(rhs);
	}

	public List<FixedDatum> getFixedDatums() {
		return fixedDatums;
	}

	@Override
	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = super.getMarshalledSize();
		marshalSize = marshalSize + originatingID.getMarshalledSize(); // originatingID
		marshalSize = marshalSize + receivingID.getMarshalledSize(); // receivingID
		marshalSize = marshalSize + 4; // requestID
		marshalSize = marshalSize + 4; // requestStatus
		marshalSize = marshalSize + 4; // numberOfFixedDatumRecords
		marshalSize = marshalSize + 4; // numberOfVariableDatumRecords
		for (int idx = 0; idx < fixedDatums.size(); idx++) {
			final FixedDatum listElement = fixedDatums.get(idx);
			marshalSize = marshalSize + listElement.getMarshalledSize();
		}
		for (int idx = 0; idx < variableDatums.size(); idx++) {
			final VariableDatum listElement = variableDatums.get(idx);
			marshalSize = marshalSize + listElement.getMarshalledSize();
		}

		return marshalSize;
	}

	public long getNumberOfFixedDatumRecords() {
		return fixedDatums.size();
	}

	public long getNumberOfVariableDatumRecords() {
		return variableDatums.size();
	}

	public EntityID getOriginatingID() {
		return originatingID;
	}

	public EntityID getReceivingID() {
		return receivingID;
	}

	public long getRequestID() {
		return requestID;
	}

	public long getRequestStatus() {
		return requestStatus;
	}

	public List<VariableDatum> getVariableDatums() {
		return variableDatums;
	}

	@Override
	public void marshal(final DataOutputStream dos) {
		super.marshal(dos);
		try {
			originatingID.marshal(dos);
			receivingID.marshal(dos);
			dos.writeInt((int) requestID);
			dos.writeInt((int) requestStatus);
			dos.writeInt(fixedDatums.size());
			dos.writeInt(variableDatums.size());

			for (int idx = 0; idx < fixedDatums.size(); idx++) {
				final FixedDatum aFixedDatum = fixedDatums.get(idx);
				aFixedDatum.marshal(dos);
			} // end of list marshalling

			for (int idx = 0; idx < variableDatums.size(); idx++) {
				final VariableDatum aVariableDatum = variableDatums.get(idx);
				aVariableDatum.marshal(dos);
			} // end of list marshalling

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
		originatingID.marshal(buff);
		receivingID.marshal(buff);
		buff.putInt((int) requestID);
		buff.putInt((int) requestStatus);
		buff.putInt(fixedDatums.size());
		buff.putInt(variableDatums.size());

		for (int idx = 0; idx < fixedDatums.size(); idx++) {
			final FixedDatum aFixedDatum = fixedDatums.get(idx);
			aFixedDatum.marshal(buff);
		} // end of list marshalling

		for (int idx = 0; idx < variableDatums.size(); idx++) {
			final VariableDatum aVariableDatum = variableDatums.get(idx);
			aVariableDatum.marshal(buff);
		} // end of list marshalling

	} // end of marshal method

	public void setFixedDatums(final List<FixedDatum> pFixedDatums) {
		fixedDatums = pFixedDatums;
	}

	/**
	 * Note that setting this value will not change the marshalled value. The list
	 * whose length this describes is used for that purpose. The
	 * getnumberOfFixedDatumRecords method will also be based on the actual list
	 * length rather than this value. The method is simply here for java bean
	 * completeness.
	 */
	public void setNumberOfFixedDatumRecords(final long pNumberOfFixedDatumRecords) {
		numberOfFixedDatumRecords = pNumberOfFixedDatumRecords;
	}

	/**
	 * Note that setting this value will not change the marshalled value. The list
	 * whose length this describes is used for that purpose. The
	 * getnumberOfVariableDatumRecords method will also be based on the actual list
	 * length rather than this value. The method is simply here for java bean
	 * completeness.
	 */
	public void setNumberOfVariableDatumRecords(final long pNumberOfVariableDatumRecords) {
		numberOfVariableDatumRecords = pNumberOfVariableDatumRecords;
	}

	public void setOriginatingID(final EntityID pOriginatingID) {
		originatingID = pOriginatingID;
	}

	public void setReceivingID(final EntityID pReceivingID) {
		receivingID = pReceivingID;
	}

	public void setRequestID(final long pRequestID) {
		requestID = pRequestID;
	}

	public void setRequestStatus(final long pRequestStatus) {
		requestStatus = pRequestStatus;
	}

	public void setVariableDatums(final List<VariableDatum> pVariableDatums) {
		variableDatums = pVariableDatums;
	}

	@Override
	public void unmarshal(final DataInputStream dis) {
		super.unmarshal(dis);

		try {
			originatingID.unmarshal(dis);
			receivingID.unmarshal(dis);
			requestID = dis.readInt();
			requestStatus = dis.readInt();
			numberOfFixedDatumRecords = dis.readInt();
			numberOfVariableDatumRecords = dis.readInt();
			for (int idx = 0; idx < numberOfFixedDatumRecords; idx++) {
				final FixedDatum anX = new FixedDatum();
				anX.unmarshal(dis);
				fixedDatums.add(anX);
			}

			for (int idx = 0; idx < numberOfVariableDatumRecords; idx++) {
				final VariableDatum anX = new VariableDatum();
				anX.unmarshal(dis);
				variableDatums.add(anX);
			}

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

		originatingID.unmarshal(buff);
		receivingID.unmarshal(buff);
		requestID = buff.getInt();
		requestStatus = buff.getInt();
		numberOfFixedDatumRecords = buff.getInt();
		numberOfVariableDatumRecords = buff.getInt();
		for (int idx = 0; idx < numberOfFixedDatumRecords; idx++) {
			final FixedDatum anX = new FixedDatum();
			anX.unmarshal(buff);
			fixedDatums.add(anX);
		}

		for (int idx = 0; idx < numberOfVariableDatumRecords; idx++) {
			final VariableDatum anX = new VariableDatum();
			anX.unmarshal(buff);
			variableDatums.add(anX);
		}

	} // end of unmarshal method
} // end of class
