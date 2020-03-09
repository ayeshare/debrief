package edu.nps.moves.dis7;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * Explosion of a non-munition. Section 6.2.19.3
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class ExplosionDescriptor extends Object implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** Type of the object that exploded. See 6.2.30 */
	protected EntityType explodingObject = new EntityType();

	/**
	 * Material that exploded. Can be grain dust, tnt, gasoline, etc. Enumeration
	 */
	protected int explosiveMaterial;

	/** padding */
	protected int padding = 0;

	/** Force of explosion, in equivalent KG of TNT */
	protected float explosiveForce;

	/** Constructor */
	public ExplosionDescriptor() {
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

	/**
	 * Compare all fields that contribute to the state, ignoring transient and
	 * static fields, for <code>this</code> and the supplied object
	 *
	 * @param obj the object to compare to
	 * @return true if the objects are equal, false otherwise.
	 */
	public boolean equalsImpl(final Object obj) {
		boolean ivarsEqual = true;

		if (!(obj instanceof ExplosionDescriptor))
			return false;

		final ExplosionDescriptor rhs = (ExplosionDescriptor) obj;

		if (!(explodingObject.equals(rhs.explodingObject)))
			ivarsEqual = false;
		if (!(explosiveMaterial == rhs.explosiveMaterial))
			ivarsEqual = false;
		if (!(padding == rhs.padding))
			ivarsEqual = false;
		if (!(explosiveForce == rhs.explosiveForce))
			ivarsEqual = false;

		return ivarsEqual;
	}

	public EntityType getExplodingObject() {
		return explodingObject;
	}

	public float getExplosiveForce() {
		return explosiveForce;
	}

	public int getExplosiveMaterial() {
		return explosiveMaterial;
	}

	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = marshalSize + explodingObject.getMarshalledSize(); // explodingObject
		marshalSize = marshalSize + 2; // explosiveMaterial
		marshalSize = marshalSize + 2; // padding
		marshalSize = marshalSize + 4; // explosiveForce

		return marshalSize;
	}

	public int getPadding() {
		return padding;
	}

	public void marshal(final DataOutputStream dos) {
		try {
			explodingObject.marshal(dos);
			dos.writeShort((short) explosiveMaterial);
			dos.writeShort((short) padding);
			dos.writeFloat(explosiveForce);
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
	public void marshal(final java.nio.ByteBuffer buff) {
		explodingObject.marshal(buff);
		buff.putShort((short) explosiveMaterial);
		buff.putShort((short) padding);
		buff.putFloat(explosiveForce);
	} // end of marshal method

	public void setExplodingObject(final EntityType pExplodingObject) {
		explodingObject = pExplodingObject;
	}

	public void setExplosiveForce(final float pExplosiveForce) {
		explosiveForce = pExplosiveForce;
	}

	public void setExplosiveMaterial(final int pExplosiveMaterial) {
		explosiveMaterial = pExplosiveMaterial;
	}

	public void setPadding(final int pPadding) {
		padding = pPadding;
	}

	public void unmarshal(final DataInputStream dis) {
		try {
			explodingObject.unmarshal(dis);
			explosiveMaterial = dis.readUnsignedShort();
			padding = dis.readUnsignedShort();
			explosiveForce = dis.readFloat();
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
	public void unmarshal(final java.nio.ByteBuffer buff) {
		explodingObject.unmarshal(buff);
		explosiveMaterial = buff.getShort() & 0xFFFF;
		padding = buff.getShort() & 0xFFFF;
		explosiveForce = buff.getFloat();
	} // end of unmarshal method
} // end of class
