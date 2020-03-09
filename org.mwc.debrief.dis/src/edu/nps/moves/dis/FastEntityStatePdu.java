package edu.nps.moves.dis;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Section 5.3.3.1. Represents the postion and state of one entity in the world.
 * This is identical in function to entity state pdu, but generates less garbage
 * to collect in the Java world. COMPLETE
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class FastEntityStatePdu extends EntityInformationFamilyPdu implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** The site ID */
	protected int site;

	/** The application ID */
	protected int application;

	/** the entity ID */
	protected int entity;

	/** what force this entity is affiliated with, eg red, blue, neutral, etc */
	protected short forceId;

	/** How many articulation parameters are in the variable length list */
	protected byte numberOfArticulationParameters;

	/** Kind of entity */
	protected short entityKind;

	/** Domain of entity (air, surface, subsurface, space, etc) */
	protected short domain;

	/** country to which the design of the entity is attributed */
	protected int country;

	/** category of entity */
	protected short category;

	/** subcategory of entity */
	protected short subcategory;

	/**
	 * specific info based on subcategory field. Name changed from specific because
	 * that is a reserved word in SQL.
	 */
	protected short specif;

	protected short extra;

	/** Kind of entity */
	protected short altEntityKind;

	/** Domain of entity (air, surface, subsurface, space, etc) */
	protected short altDomain;

	/** country to which the design of the entity is attributed */
	protected int altCountry;

	/** category of entity */
	protected short altCategory;

	/** subcategory of entity */
	protected short altSubcategory;

	/** specific info based on subcategory field */
	protected short altSpecific;

	protected short altExtra;

	/** X velo */
	protected float xVelocity;

	/** y Value */
	protected float yVelocity;

	/** Z value */
	protected float zVelocity;

	/** X value */
	protected double xLocation;

	/** y Value */
	protected double yLocation;

	/** Z value */
	protected double zLocation;

	protected float psi;

	protected float theta;

	protected float phi;

	/**
	 * a series of bit flags that are used to help draw the entity, such as smoking,
	 * on fire, etc.
	 */
	protected int entityAppearance;

	/** enumeration of what dead reckoning algorighm to use */
	protected short deadReckoningAlgorithm;

	/** other parameters to use in the dead reckoning algorithm */
	protected byte[] otherParameters = new byte[15];

	/** X value */
	protected float xAcceleration;

	/** y Value */
	protected float yAcceleration;

	/** Z value */
	protected float zAcceleration;

	/** X value */
	protected float xAngularVelocity;

	/** y Value */
	protected float yAngularVelocity;

	/** Z value */
	protected float zAngularVelocity;

	/**
	 * characters that can be used for debugging, or to draw unique strings on the
	 * side of entities in the world
	 */
	protected byte[] marking = new byte[12];

	/** a series of bit flags */
	protected int capabilities;

	/** variable length list of articulation parameters */
	protected List<ArticulationParameter> articulationParameters = new ArrayList<ArticulationParameter>();

	/** Constructor */
	public FastEntityStatePdu() {
		setPduType((short) 1);
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

		if (!(obj instanceof FastEntityStatePdu))
			return false;

		final FastEntityStatePdu rhs = (FastEntityStatePdu) obj;

		if (!(site == rhs.site))
			ivarsEqual = false;
		if (!(application == rhs.application))
			ivarsEqual = false;
		if (!(entity == rhs.entity))
			ivarsEqual = false;
		if (!(forceId == rhs.forceId))
			ivarsEqual = false;
		if (!(numberOfArticulationParameters == rhs.numberOfArticulationParameters))
			ivarsEqual = false;
		if (!(entityKind == rhs.entityKind))
			ivarsEqual = false;
		if (!(domain == rhs.domain))
			ivarsEqual = false;
		if (!(country == rhs.country))
			ivarsEqual = false;
		if (!(category == rhs.category))
			ivarsEqual = false;
		if (!(subcategory == rhs.subcategory))
			ivarsEqual = false;
		if (!(specif == rhs.specif))
			ivarsEqual = false;
		if (!(extra == rhs.extra))
			ivarsEqual = false;
		if (!(altEntityKind == rhs.altEntityKind))
			ivarsEqual = false;
		if (!(altDomain == rhs.altDomain))
			ivarsEqual = false;
		if (!(altCountry == rhs.altCountry))
			ivarsEqual = false;
		if (!(altCategory == rhs.altCategory))
			ivarsEqual = false;
		if (!(altSubcategory == rhs.altSubcategory))
			ivarsEqual = false;
		if (!(altSpecific == rhs.altSpecific))
			ivarsEqual = false;
		if (!(altExtra == rhs.altExtra))
			ivarsEqual = false;
		if (!(xVelocity == rhs.xVelocity))
			ivarsEqual = false;
		if (!(yVelocity == rhs.yVelocity))
			ivarsEqual = false;
		if (!(zVelocity == rhs.zVelocity))
			ivarsEqual = false;
		if (!(xLocation == rhs.xLocation))
			ivarsEqual = false;
		if (!(yLocation == rhs.yLocation))
			ivarsEqual = false;
		if (!(zLocation == rhs.zLocation))
			ivarsEqual = false;
		if (!(psi == rhs.psi))
			ivarsEqual = false;
		if (!(theta == rhs.theta))
			ivarsEqual = false;
		if (!(phi == rhs.phi))
			ivarsEqual = false;
		if (!(entityAppearance == rhs.entityAppearance))
			ivarsEqual = false;
		if (!(deadReckoningAlgorithm == rhs.deadReckoningAlgorithm))
			ivarsEqual = false;

		for (int idx = 0; idx < 15; idx++) {
			if (!(otherParameters[idx] == rhs.otherParameters[idx]))
				ivarsEqual = false;
		}

		if (!(xAcceleration == rhs.xAcceleration))
			ivarsEqual = false;
		if (!(yAcceleration == rhs.yAcceleration))
			ivarsEqual = false;
		if (!(zAcceleration == rhs.zAcceleration))
			ivarsEqual = false;
		if (!(xAngularVelocity == rhs.xAngularVelocity))
			ivarsEqual = false;
		if (!(yAngularVelocity == rhs.yAngularVelocity))
			ivarsEqual = false;
		if (!(zAngularVelocity == rhs.zAngularVelocity))
			ivarsEqual = false;

		for (int idx = 0; idx < 12; idx++) {
			if (!(marking[idx] == rhs.marking[idx]))
				ivarsEqual = false;
		}

		if (!(capabilities == rhs.capabilities))
			ivarsEqual = false;

		for (int idx = 0; idx < articulationParameters.size(); idx++) {
			if (!(articulationParameters.get(idx).equals(rhs.articulationParameters.get(idx))))
				ivarsEqual = false;
		}

		return ivarsEqual && super.equalsImpl(rhs);
	}

	public short getAltCategory() {
		return altCategory;
	}

	public int getAltCountry() {
		return altCountry;
	}

	public short getAltDomain() {
		return altDomain;
	}

	public short getAltEntityKind() {
		return altEntityKind;
	}

	public short getAltExtra() {
		return altExtra;
	}

	public short getAltSpecific() {
		return altSpecific;
	}

	public short getAltSubcategory() {
		return altSubcategory;
	}

	public int getApplication() {
		return application;
	}

	public List<ArticulationParameter> getArticulationParameters() {
		return articulationParameters;
	}

	public int getCapabilities() {
		return capabilities;
	}

	public short getCategory() {
		return category;
	}

	public int getCountry() {
		return country;
	}

	public short getDeadReckoningAlgorithm() {
		return deadReckoningAlgorithm;
	}

	public short getDomain() {
		return domain;
	}

	public int getEntity() {
		return entity;
	}

	public int getEntityAppearance() {
		return entityAppearance;
	}

	/**
	 * 0 off 1 on
	 */
	public int getEntityAppearance_brakeLights() {
		final int val = this.entityAppearance & 0x4000;
		return val >> 14;
	}

	/**
	 * 0 desert 1 winter 2 forest 3 unused
	 */
	public int getEntityAppearance_camouflageType() {
		final int val = this.entityAppearance & 0x60000;
		return val >> 17;
	}

	/**
	 * 0 no damage, 1 slight damage, 2 moderate, 3 destroyed
	 */
	public int getEntityAppearance_damage() {
		final int val = this.entityAppearance & 0x18;
		return val >> 3;
	}

	/**
	 * 0 no firepower iill, 1 firepower kill
	 */
	public int getEntityAppearance_firepower() {
		final int val = this.entityAppearance & 0x4;
		return val >> 2;
	}

	/**
	 * 0 off 1 on
	 */
	public int getEntityAppearance_flaming() {
		final int val = this.entityAppearance & 0x8000;
		return val >> 15;
	}

	/**
	 * 0 NA 1 closed popped 3 popped and person visible 4 open 5 open and person
	 * visible
	 */
	public int getEntityAppearance_hatch() {
		final int val = this.entityAppearance & 0xe00;
		return val >> 9;
	}

	/**
	 * 0 off 1 on
	 */
	public int getEntityAppearance_headlights() {
		final int val = this.entityAppearance & 0x1000;
		return val >> 12;
	}

	/**
	 * 0 not raised 1 raised
	 */
	public int getEntityAppearance_launcher() {
		final int val = this.entityAppearance & 0x10000;
		return val >> 16;
	}

	/**
	 * 0 no mobility kill, 1 mobility kill
	 */
	public int getEntityAppearance_mobility() {
		final int val = this.entityAppearance & 0x2;
		return val >> 1;
	}

	/**
	 * 0 uniform color, 1 camouflage
	 */
	public int getEntityAppearance_paintScheme() {
		final int val = this.entityAppearance & 0x1;
		return val >> 0;
	}

	/**
	 * 0 no smoke, 1 smoke plume, 2 engine smoke, 3 engine smoke and plume
	 */
	public int getEntityAppearance_smoke() {
		final int val = this.entityAppearance & 0x60;
		return val >> 5;
	}

	/**
	 * 0 off 1 on
	 */
	public int getEntityAppearance_tailLights() {
		final int val = this.entityAppearance & 0x2000;
		return val >> 13;
	}

	/**
	 * dust cloud, 0 none 1 small 2 medium 3 large
	 */
	public int getEntityAppearance_trailingEffects() {
		final int val = this.entityAppearance & 0x180;
		return val >> 7;
	}

	public short getEntityKind() {
		return entityKind;
	}

	public short getExtra() {
		return extra;
	}

	public short getForceId() {
		return forceId;
	}

	public byte[] getMarking() {
		return marking;
	}

	@Override
	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = super.getMarshalledSize();
		marshalSize = marshalSize + 2; // site
		marshalSize = marshalSize + 2; // application
		marshalSize = marshalSize + 2; // entity
		marshalSize = marshalSize + 1; // forceId
		marshalSize = marshalSize + 1; // numberOfArticulationParameters
		marshalSize = marshalSize + 1; // entityKind
		marshalSize = marshalSize + 1; // domain
		marshalSize = marshalSize + 2; // country
		marshalSize = marshalSize + 1; // category
		marshalSize = marshalSize + 1; // subcategory
		marshalSize = marshalSize + 1; // specif
		marshalSize = marshalSize + 1; // extra
		marshalSize = marshalSize + 1; // altEntityKind
		marshalSize = marshalSize + 1; // altDomain
		marshalSize = marshalSize + 2; // altCountry
		marshalSize = marshalSize + 1; // altCategory
		marshalSize = marshalSize + 1; // altSubcategory
		marshalSize = marshalSize + 1; // altSpecific
		marshalSize = marshalSize + 1; // altExtra
		marshalSize = marshalSize + 4; // xVelocity
		marshalSize = marshalSize + 4; // yVelocity
		marshalSize = marshalSize + 4; // zVelocity
		marshalSize = marshalSize + 8; // xLocation
		marshalSize = marshalSize + 8; // yLocation
		marshalSize = marshalSize + 8; // zLocation
		marshalSize = marshalSize + 4; // psi
		marshalSize = marshalSize + 4; // theta
		marshalSize = marshalSize + 4; // phi
		marshalSize = marshalSize + 4; // entityAppearance
		marshalSize = marshalSize + 1; // deadReckoningAlgorithm
		marshalSize = marshalSize + 15 * 1; // otherParameters
		marshalSize = marshalSize + 4; // xAcceleration
		marshalSize = marshalSize + 4; // yAcceleration
		marshalSize = marshalSize + 4; // zAcceleration
		marshalSize = marshalSize + 4; // xAngularVelocity
		marshalSize = marshalSize + 4; // yAngularVelocity
		marshalSize = marshalSize + 4; // zAngularVelocity
		marshalSize = marshalSize + 12 * 1; // marking
		marshalSize = marshalSize + 4; // capabilities
		for (int idx = 0; idx < articulationParameters.size(); idx++) {
			final ArticulationParameter listElement = articulationParameters.get(idx);
			marshalSize = marshalSize + listElement.getMarshalledSize();
		}

		return marshalSize;
	}

	public byte getNumberOfArticulationParameters() {
		return (byte) articulationParameters.size();
	}

	public byte[] getOtherParameters() {
		return otherParameters;
	}

	public float getPhi() {
		return phi;
	}

	public float getPsi() {
		return psi;
	}

	public int getSite() {
		return site;
	}

	public short getSpecif() {
		return specif;
	}

	public short getSubcategory() {
		return subcategory;
	}

	public float getTheta() {
		return theta;
	}

	public float getXAcceleration() {
		return xAcceleration;
	}

	public float getXAngularVelocity() {
		return xAngularVelocity;
	}

	public double getXLocation() {
		return xLocation;
	}

	public float getXVelocity() {
		return xVelocity;
	}

	public float getYAcceleration() {
		return yAcceleration;
	}

	public float getYAngularVelocity() {
		return yAngularVelocity;
	}

	public double getYLocation() {
		return yLocation;
	}

	public float getYVelocity() {
		return yVelocity;
	}

	public float getZAcceleration() {
		return zAcceleration;
	}

	public float getZAngularVelocity() {
		return zAngularVelocity;
	}

	public double getZLocation() {
		return zLocation;
	}

	public float getZVelocity() {
		return zVelocity;
	}

	@Override
	public void marshal(final DataOutputStream dos) {
		super.marshal(dos);
		try {
			dos.writeShort((short) site);
			dos.writeShort((short) application);
			dos.writeShort((short) entity);
			dos.writeByte((byte) forceId);
			dos.writeByte((byte) articulationParameters.size());
			dos.writeByte((byte) entityKind);
			dos.writeByte((byte) domain);
			dos.writeShort((short) country);
			dos.writeByte((byte) category);
			dos.writeByte((byte) subcategory);
			dos.writeByte((byte) specif);
			dos.writeByte((byte) extra);
			dos.writeByte((byte) altEntityKind);
			dos.writeByte((byte) altDomain);
			dos.writeShort((short) altCountry);
			dos.writeByte((byte) altCategory);
			dos.writeByte((byte) altSubcategory);
			dos.writeByte((byte) altSpecific);
			dos.writeByte((byte) altExtra);
			dos.writeFloat(xVelocity);
			dos.writeFloat(yVelocity);
			dos.writeFloat(zVelocity);
			dos.writeDouble(xLocation);
			dos.writeDouble(yLocation);
			dos.writeDouble(zLocation);
			dos.writeFloat(psi);
			dos.writeFloat(theta);
			dos.writeFloat(phi);
			dos.writeInt(entityAppearance);
			dos.writeByte((byte) deadReckoningAlgorithm);

			for (int idx = 0; idx < otherParameters.length; idx++) {
				dos.writeByte(otherParameters[idx]);
			} // end of array marshaling

			dos.writeFloat(xAcceleration);
			dos.writeFloat(yAcceleration);
			dos.writeFloat(zAcceleration);
			dos.writeFloat(xAngularVelocity);
			dos.writeFloat(yAngularVelocity);
			dos.writeFloat(zAngularVelocity);

			for (int idx = 0; idx < marking.length; idx++) {
				dos.writeByte(marking[idx]);
			} // end of array marshaling

			dos.writeInt(capabilities);

			for (int idx = 0; idx < articulationParameters.size(); idx++) {
				final ArticulationParameter aArticulationParameter = articulationParameters.get(idx);
				aArticulationParameter.marshal(dos);
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
		buff.putShort((short) site);
		buff.putShort((short) application);
		buff.putShort((short) entity);
		buff.put((byte) forceId);
		buff.put((byte) articulationParameters.size());
		buff.put((byte) entityKind);
		buff.put((byte) domain);
		buff.putShort((short) country);
		buff.put((byte) category);
		buff.put((byte) subcategory);
		buff.put((byte) specif);
		buff.put((byte) extra);
		buff.put((byte) altEntityKind);
		buff.put((byte) altDomain);
		buff.putShort((short) altCountry);
		buff.put((byte) altCategory);
		buff.put((byte) altSubcategory);
		buff.put((byte) altSpecific);
		buff.put((byte) altExtra);
		buff.putFloat(xVelocity);
		buff.putFloat(yVelocity);
		buff.putFloat(zVelocity);
		buff.putDouble(xLocation);
		buff.putDouble(yLocation);
		buff.putDouble(zLocation);
		buff.putFloat(psi);
		buff.putFloat(theta);
		buff.putFloat(phi);
		buff.putInt(entityAppearance);
		buff.put((byte) deadReckoningAlgorithm);

		for (int idx = 0; idx < otherParameters.length; idx++) {
			buff.put(otherParameters[idx]);
		} // end of array marshaling

		buff.putFloat(xAcceleration);
		buff.putFloat(yAcceleration);
		buff.putFloat(zAcceleration);
		buff.putFloat(xAngularVelocity);
		buff.putFloat(yAngularVelocity);
		buff.putFloat(zAngularVelocity);

		for (int idx = 0; idx < marking.length; idx++) {
			buff.put(marking[idx]);
		} // end of array marshaling

		buff.putInt(capabilities);

		for (int idx = 0; idx < articulationParameters.size(); idx++) {
			final ArticulationParameter aArticulationParameter = articulationParameters.get(idx);
			aArticulationParameter.marshal(buff);
		} // end of list marshalling

	} // end of marshal method

	public void setAltCategory(final short pAltCategory) {
		altCategory = pAltCategory;
	}

	public void setAltCountry(final int pAltCountry) {
		altCountry = pAltCountry;
	}

	public void setAltDomain(final short pAltDomain) {
		altDomain = pAltDomain;
	}

	public void setAltEntityKind(final short pAltEntityKind) {
		altEntityKind = pAltEntityKind;
	}

	public void setAltExtra(final short pAltExtra) {
		altExtra = pAltExtra;
	}

	public void setAltSpecific(final short pAltSpecific) {
		altSpecific = pAltSpecific;
	}

	public void setAltSubcategory(final short pAltSubcategory) {
		altSubcategory = pAltSubcategory;
	}

	public void setApplication(final int pApplication) {
		application = pApplication;
	}

	public void setArticulationParameters(final List<ArticulationParameter> pArticulationParameters) {
		articulationParameters = pArticulationParameters;
	}

	public void setCapabilities(final int pCapabilities) {
		capabilities = pCapabilities;
	}

	public void setCategory(final short pCategory) {
		category = pCategory;
	}

	public void setCountry(final int pCountry) {
		country = pCountry;
	}

	public void setDeadReckoningAlgorithm(final short pDeadReckoningAlgorithm) {
		deadReckoningAlgorithm = pDeadReckoningAlgorithm;
	}

	public void setDomain(final short pDomain) {
		domain = pDomain;
	}

	public void setEntity(final int pEntity) {
		entity = pEntity;
	}

	public void setEntityAppearance(final int pEntityAppearance) {
		entityAppearance = pEntityAppearance;
	}

	/**
	 * 0 off 1 on
	 */
	public void setEntityAppearance_brakeLights(final int val) {
		int aVal = 0;
		this.entityAppearance &= (~0x4000); // clear bits
		aVal = val << 14;
		this.entityAppearance = this.entityAppearance | aVal;
	}

	/**
	 * 0 desert 1 winter 2 forest 3 unused
	 */
	public void setEntityAppearance_camouflageType(final int val) {
		int aVal = 0;
		this.entityAppearance &= (~0x60000); // clear bits
		aVal = val << 17;
		this.entityAppearance = this.entityAppearance | aVal;
	}

	/**
	 * 0 no damage, 1 slight damage, 2 moderate, 3 destroyed
	 */
	public void setEntityAppearance_damage(final int val) {
		int aVal = 0;
		this.entityAppearance &= (~0x18); // clear bits
		aVal = val << 3;
		this.entityAppearance = this.entityAppearance | aVal;
	}

	/**
	 * 0 no firepower iill, 1 firepower kill
	 */
	public void setEntityAppearance_firepower(final int val) {
		int aVal = 0;
		this.entityAppearance &= (~0x4); // clear bits
		aVal = val << 2;
		this.entityAppearance = this.entityAppearance | aVal;
	}

	/**
	 * 0 off 1 on
	 */
	public void setEntityAppearance_flaming(final int val) {
		int aVal = 0;
		this.entityAppearance &= (~0x8000); // clear bits
		aVal = val << 15;
		this.entityAppearance = this.entityAppearance | aVal;
	}

	/**
	 * 0 NA 1 closed popped 3 popped and person visible 4 open 5 open and person
	 * visible
	 */
	public void setEntityAppearance_hatch(final int val) {
		int aVal = 0;
		this.entityAppearance &= (~0xe00); // clear bits
		aVal = val << 9;
		this.entityAppearance = this.entityAppearance | aVal;
	}

	/**
	 * 0 off 1 on
	 */
	public void setEntityAppearance_headlights(final int val) {
		int aVal = 0;
		this.entityAppearance &= (~0x1000); // clear bits
		aVal = val << 12;
		this.entityAppearance = this.entityAppearance | aVal;
	}

	/**
	 * 0 not raised 1 raised
	 */
	public void setEntityAppearance_launcher(final int val) {
		int aVal = 0;
		this.entityAppearance &= (~0x10000); // clear bits
		aVal = val << 16;
		this.entityAppearance = this.entityAppearance | aVal;
	}

	/**
	 * 0 no mobility kill, 1 mobility kill
	 */
	public void setEntityAppearance_mobility(final int val) {
		int aVal = 0;
		this.entityAppearance &= (~0x2); // clear bits
		aVal = val << 1;
		this.entityAppearance = this.entityAppearance | aVal;
	}

	/**
	 * 0 uniform color, 1 camouflage
	 */
	public void setEntityAppearance_paintScheme(final int val) {
		int aVal = 0;
		this.entityAppearance &= (~0x1); // clear bits
		aVal = val << 0;
		this.entityAppearance = this.entityAppearance | aVal;
	}

	/**
	 * 0 no smoke, 1 smoke plume, 2 engine smoke, 3 engine smoke and plume
	 */
	public void setEntityAppearance_smoke(final int val) {
		int aVal = 0;
		this.entityAppearance &= (~0x60); // clear bits
		aVal = val << 5;
		this.entityAppearance = this.entityAppearance | aVal;
	}

	/**
	 * 0 off 1 on
	 */
	public void setEntityAppearance_tailLights(final int val) {
		int aVal = 0;
		this.entityAppearance &= (~0x2000); // clear bits
		aVal = val << 13;
		this.entityAppearance = this.entityAppearance | aVal;
	}

	/**
	 * dust cloud, 0 none 1 small 2 medium 3 large
	 */
	public void setEntityAppearance_trailingEffects(final int val) {
		int aVal = 0;
		this.entityAppearance &= (~0x180); // clear bits
		aVal = val << 7;
		this.entityAppearance = this.entityAppearance | aVal;
	}

	public void setEntityKind(final short pEntityKind) {
		entityKind = pEntityKind;
	}

	public void setExtra(final short pExtra) {
		extra = pExtra;
	}

	public void setForceId(final short pForceId) {
		forceId = pForceId;
	}

	public void setMarking(final byte[] pMarking) {
		marking = pMarking;
	}

	/**
	 * Note that setting this value will not change the marshalled value. The list
	 * whose length this describes is used for that purpose. The
	 * getnumberOfArticulationParameters method will also be based on the actual
	 * list length rather than this value. The method is simply here for java bean
	 * completeness.
	 */
	public void setNumberOfArticulationParameters(final byte pNumberOfArticulationParameters) {
		numberOfArticulationParameters = pNumberOfArticulationParameters;
	}

	public void setOtherParameters(final byte[] pOtherParameters) {
		otherParameters = pOtherParameters;
	}

	public void setPhi(final float pPhi) {
		phi = pPhi;
	}

	public void setPsi(final float pPsi) {
		psi = pPsi;
	}

	public void setSite(final int pSite) {
		site = pSite;
	}

	public void setSpecif(final short pSpecif) {
		specif = pSpecif;
	}

	public void setSubcategory(final short pSubcategory) {
		subcategory = pSubcategory;
	}

	public void setTheta(final float pTheta) {
		theta = pTheta;
	}

	public void setXAcceleration(final float pXAcceleration) {
		xAcceleration = pXAcceleration;
	}

	public void setXAngularVelocity(final float pXAngularVelocity) {
		xAngularVelocity = pXAngularVelocity;
	}

	public void setXLocation(final double pXLocation) {
		xLocation = pXLocation;
	}

	public void setXVelocity(final float pXVelocity) {
		xVelocity = pXVelocity;
	}

	public void setYAcceleration(final float pYAcceleration) {
		yAcceleration = pYAcceleration;
	}

	public void setYAngularVelocity(final float pYAngularVelocity) {
		yAngularVelocity = pYAngularVelocity;
	}

	public void setYLocation(final double pYLocation) {
		yLocation = pYLocation;
	}

	public void setYVelocity(final float pYVelocity) {
		yVelocity = pYVelocity;
	}

	public void setZAcceleration(final float pZAcceleration) {
		zAcceleration = pZAcceleration;
	}

	public void setZAngularVelocity(final float pZAngularVelocity) {
		zAngularVelocity = pZAngularVelocity;
	}

	public void setZLocation(final double pZLocation) {
		zLocation = pZLocation;
	}

	public void setZVelocity(final float pZVelocity) {
		zVelocity = pZVelocity;
	}

	@Override
	public void unmarshal(final DataInputStream dis) {
		super.unmarshal(dis);

		try {
			site = dis.readUnsignedShort();
			application = dis.readUnsignedShort();
			entity = dis.readUnsignedShort();
			forceId = (short) dis.readUnsignedByte();
			numberOfArticulationParameters = dis.readByte();
			entityKind = (short) dis.readUnsignedByte();
			domain = (short) dis.readUnsignedByte();
			country = dis.readUnsignedShort();
			category = (short) dis.readUnsignedByte();
			subcategory = (short) dis.readUnsignedByte();
			specif = (short) dis.readUnsignedByte();
			extra = (short) dis.readUnsignedByte();
			altEntityKind = (short) dis.readUnsignedByte();
			altDomain = (short) dis.readUnsignedByte();
			altCountry = dis.readUnsignedShort();
			altCategory = (short) dis.readUnsignedByte();
			altSubcategory = (short) dis.readUnsignedByte();
			altSpecific = (short) dis.readUnsignedByte();
			altExtra = (short) dis.readUnsignedByte();
			xVelocity = dis.readFloat();
			yVelocity = dis.readFloat();
			zVelocity = dis.readFloat();
			xLocation = dis.readDouble();
			yLocation = dis.readDouble();
			zLocation = dis.readDouble();
			psi = dis.readFloat();
			theta = dis.readFloat();
			phi = dis.readFloat();
			entityAppearance = dis.readInt();
			deadReckoningAlgorithm = (short) dis.readUnsignedByte();
			for (int idx = 0; idx < otherParameters.length; idx++) {
				otherParameters[idx] = dis.readByte();
			} // end of array unmarshaling
			xAcceleration = dis.readFloat();
			yAcceleration = dis.readFloat();
			zAcceleration = dis.readFloat();
			xAngularVelocity = dis.readFloat();
			yAngularVelocity = dis.readFloat();
			zAngularVelocity = dis.readFloat();
			for (int idx = 0; idx < marking.length; idx++) {
				marking[idx] = dis.readByte();
			} // end of array unmarshaling
			capabilities = dis.readInt();
			for (int idx = 0; idx < numberOfArticulationParameters; idx++) {
				final ArticulationParameter anX = new ArticulationParameter();
				anX.unmarshal(dis);
				articulationParameters.add(anX);
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

		site = buff.getShort() & 0xFFFF;
		application = buff.getShort() & 0xFFFF;
		entity = buff.getShort() & 0xFFFF;
		forceId = (short) (buff.get() & 0xFF);
		numberOfArticulationParameters = buff.get();
		entityKind = (short) (buff.get() & 0xFF);
		domain = (short) (buff.get() & 0xFF);
		country = buff.getShort() & 0xFFFF;
		category = (short) (buff.get() & 0xFF);
		subcategory = (short) (buff.get() & 0xFF);
		specif = (short) (buff.get() & 0xFF);
		extra = (short) (buff.get() & 0xFF);
		altEntityKind = (short) (buff.get() & 0xFF);
		altDomain = (short) (buff.get() & 0xFF);
		altCountry = buff.getShort() & 0xFFFF;
		altCategory = (short) (buff.get() & 0xFF);
		altSubcategory = (short) (buff.get() & 0xFF);
		altSpecific = (short) (buff.get() & 0xFF);
		altExtra = (short) (buff.get() & 0xFF);
		xVelocity = buff.getFloat();
		yVelocity = buff.getFloat();
		zVelocity = buff.getFloat();
		xLocation = buff.getDouble();
		yLocation = buff.getDouble();
		zLocation = buff.getDouble();
		psi = buff.getFloat();
		theta = buff.getFloat();
		phi = buff.getFloat();
		entityAppearance = buff.getInt();
		deadReckoningAlgorithm = (short) (buff.get() & 0xFF);
		for (int idx = 0; idx < otherParameters.length; idx++) {
			otherParameters[idx] = buff.get();
		} // end of array unmarshaling
		xAcceleration = buff.getFloat();
		yAcceleration = buff.getFloat();
		zAcceleration = buff.getFloat();
		xAngularVelocity = buff.getFloat();
		yAngularVelocity = buff.getFloat();
		zAngularVelocity = buff.getFloat();
		for (int idx = 0; idx < marking.length; idx++) {
			marking[idx] = buff.get();
		} // end of array unmarshaling
		capabilities = buff.getInt();
		for (int idx = 0; idx < numberOfArticulationParameters; idx++) {
			final ArticulationParameter anX = new ArticulationParameter();
			anX.unmarshal(buff);
			articulationParameters.add(anX);
		}

	} // end of unmarshal method
} // end of class
