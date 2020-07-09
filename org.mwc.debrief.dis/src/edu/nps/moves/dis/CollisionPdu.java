package edu.nps.moves.dis;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * Section 5.3.3.2. Information about a collision. COMPLETE
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class CollisionPdu extends EntityInformationFamilyPdu implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** ID of the entity that issued the collision PDU */
	protected EntityID issuingEntityID = new EntityID();

	/** ID of entity that has collided with the issuing entity ID */
	protected EntityID collidingEntityID = new EntityID();

	/** ID of event */
	protected EventID eventID = new EventID();

	/** ID of event */
	protected short collisionType;

	/** some padding */
	protected byte pad = (byte) 0;

	/** velocity at collision */
	protected Vector3Float velocity = new Vector3Float();

	/** mass of issuing entity */
	protected float mass;

	/** Location with respect to entity the issuing entity collided with */
	protected Vector3Float location = new Vector3Float();

	/** Constructor */
	public CollisionPdu() {
		setPduType((short) 4);
		setProtocolFamily((short) 1);
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

		if (!(obj instanceof CollisionPdu))
			return false;

		final CollisionPdu rhs = (CollisionPdu) obj;

		if (!(issuingEntityID.equals(rhs.issuingEntityID)))
			ivarsEqual = false;
		if (!(collidingEntityID.equals(rhs.collidingEntityID)))
			ivarsEqual = false;
		if (!(eventID.equals(rhs.eventID)))
			ivarsEqual = false;
		if (!(collisionType == rhs.collisionType))
			ivarsEqual = false;
		if (!(pad == rhs.pad))
			ivarsEqual = false;
		if (!(velocity.equals(rhs.velocity)))
			ivarsEqual = false;
		if (!(mass == rhs.mass))
			ivarsEqual = false;
		if (!(location.equals(rhs.location)))
			ivarsEqual = false;

		return ivarsEqual && super.equalsImpl(rhs);
	}

	public EntityID getCollidingEntityID() {
		return collidingEntityID;
	}

	public short getCollisionType() {
		return collisionType;
	}

	public EventID getEventID() {
		return eventID;
	}

	public EntityID getIssuingEntityID() {
		return issuingEntityID;
	}

	public Vector3Float getLocation() {
		return location;
	}

	@Override
	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = super.getMarshalledSize();
		marshalSize = marshalSize + issuingEntityID.getMarshalledSize(); // issuingEntityID
		marshalSize = marshalSize + collidingEntityID.getMarshalledSize(); // collidingEntityID
		marshalSize = marshalSize + eventID.getMarshalledSize(); // eventID
		marshalSize = marshalSize + 1; // collisionType
		marshalSize = marshalSize + 1; // pad
		marshalSize = marshalSize + velocity.getMarshalledSize(); // velocity
		marshalSize = marshalSize + 4; // mass
		marshalSize = marshalSize + location.getMarshalledSize(); // location

		return marshalSize;
	}

	public float getMass() {
		return mass;
	}

	public byte getPad() {
		return pad;
	}

	public Vector3Float getVelocity() {
		return velocity;
	}

	@Override
	public void marshal(final DataOutputStream dos) {
		super.marshal(dos);
		try {
			issuingEntityID.marshal(dos);
			collidingEntityID.marshal(dos);
			eventID.marshal(dos);
			dos.writeByte((byte) collisionType);
			dos.writeByte(pad);
			velocity.marshal(dos);
			dos.writeFloat(mass);
			location.marshal(dos);
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
		issuingEntityID.marshal(buff);
		collidingEntityID.marshal(buff);
		eventID.marshal(buff);
		buff.put((byte) collisionType);
		buff.put(pad);
		velocity.marshal(buff);
		buff.putFloat(mass);
		location.marshal(buff);
	} // end of marshal method

	public void setCollidingEntityID(final EntityID pCollidingEntityID) {
		collidingEntityID = pCollidingEntityID;
	}

	public void setCollisionType(final short pCollisionType) {
		collisionType = pCollisionType;
	}

	public void setEventID(final EventID pEventID) {
		eventID = pEventID;
	}

	public void setIssuingEntityID(final EntityID pIssuingEntityID) {
		issuingEntityID = pIssuingEntityID;
	}

	public void setLocation(final Vector3Float pLocation) {
		location = pLocation;
	}

	public void setMass(final float pMass) {
		mass = pMass;
	}

	public void setPad(final byte pPad) {
		pad = pPad;
	}

	public void setVelocity(final Vector3Float pVelocity) {
		velocity = pVelocity;
	}

	@Override
	public void unmarshal(final DataInputStream dis) {
		super.unmarshal(dis);

		try {
			issuingEntityID.unmarshal(dis);
			collidingEntityID.unmarshal(dis);
			eventID.unmarshal(dis);
			collisionType = (short) dis.readUnsignedByte();
			pad = dis.readByte();
			velocity.unmarshal(dis);
			mass = dis.readFloat();
			location.unmarshal(dis);
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

		issuingEntityID.unmarshal(buff);
		collidingEntityID.unmarshal(buff);
		eventID.unmarshal(buff);
		collisionType = (short) (buff.get() & 0xFF);
		pad = buff.get();
		velocity.unmarshal(buff);
		mass = buff.getFloat();
		location.unmarshal(buff);
	} // end of unmarshal method
} // end of class
