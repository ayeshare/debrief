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

// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.2-147
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2012.08.30 at 10:32:43 PM EDT
//

package org.mwc.debrief.core.gpx;

import java.math.BigInteger;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for shapeType complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 *
 * <pre>
 * &lt;complexType name="shapeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="colour" type="{org.mwc.debrief.core}colourType" minOccurs="0"/>
 *         &lt;element name="fontcolour" type="{org.mwc.debrief.core}colourType" minOccurs="0"/>
 *         &lt;element name="timeRange" type="{org.mwc.debrief.core}timeRangeType" minOccurs="0"/>
 *         &lt;element name="font" type="{org.mwc.debrief.core}fontType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="Label" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="LabelLocation" type="{org.mwc.debrief.core}labelLocationType" />
 *       &lt;attribute name="LabelVisible" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="LineStyle" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *       &lt;attribute name="LineThickness" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *       &lt;attribute name="Visible" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "shapeType", propOrder = { "colour", "fontcolour", "timeRange", "font" })
@XmlSeeAlso({ RectangleType.class, LineType.class })
public abstract class ShapeType {

	protected ColourType colour;
	protected ColourType fontcolour;
	protected TimeRangeType timeRange;
	protected FontType font;
	@XmlAttribute(name = "Label")
	protected String label;
	@XmlAttribute(name = "LabelLocation")
	protected LabelLocationType labelLocation;
	@XmlAttribute(name = "LabelVisible")
	protected Boolean labelVisible;
	@XmlAttribute(name = "LineStyle")
	protected BigInteger lineStyle;
	@XmlAttribute(name = "LineThickness")
	protected BigInteger lineThickness;
	@XmlAttribute(name = "Visible")
	protected Boolean visible;

	/**
	 * Gets the value of the colour property.
	 *
	 * @return possible object is {@link ColourType }
	 *
	 */
	public ColourType getColour() {
		return colour;
	}

	/**
	 * Gets the value of the font property.
	 *
	 * @return possible object is {@link FontType }
	 *
	 */
	public FontType getFont() {
		return font;
	}

	/**
	 * Gets the value of the fontcolour property.
	 *
	 * @return possible object is {@link ColourType }
	 *
	 */
	public ColourType getFontcolour() {
		return fontcolour;
	}

	/**
	 * Gets the value of the label property.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Gets the value of the labelLocation property.
	 *
	 * @return possible object is {@link LabelLocationType }
	 *
	 */
	public LabelLocationType getLabelLocation() {
		return labelLocation;
	}

	/**
	 * Gets the value of the lineStyle property.
	 *
	 * @return possible object is {@link BigInteger }
	 *
	 */
	public BigInteger getLineStyle() {
		return lineStyle;
	}

	/**
	 * Gets the value of the lineThickness property.
	 *
	 * @return possible object is {@link BigInteger }
	 *
	 */
	public BigInteger getLineThickness() {
		return lineThickness;
	}

	/**
	 * Gets the value of the timeRange property.
	 *
	 * @return possible object is {@link TimeRangeType }
	 *
	 */
	public TimeRangeType getTimeRange() {
		return timeRange;
	}

	/**
	 * Gets the value of the labelVisible property.
	 *
	 * @return possible object is {@link Boolean }
	 *
	 */
	public Boolean isLabelVisible() {
		return labelVisible;
	}

	/**
	 * Gets the value of the visible property.
	 *
	 * @return possible object is {@link Boolean }
	 *
	 */
	public Boolean isVisible() {
		return visible;
	}

	/**
	 * Sets the value of the colour property.
	 *
	 * @param value allowed object is {@link ColourType }
	 *
	 */
	public void setColour(final ColourType value) {
		this.colour = value;
	}

	/**
	 * Sets the value of the font property.
	 *
	 * @param value allowed object is {@link FontType }
	 *
	 */
	public void setFont(final FontType value) {
		this.font = value;
	}

	/**
	 * Sets the value of the fontcolour property.
	 *
	 * @param value allowed object is {@link ColourType }
	 *
	 */
	public void setFontcolour(final ColourType value) {
		this.fontcolour = value;
	}

	/**
	 * Sets the value of the label property.
	 *
	 * @param value allowed object is {@link String }
	 *
	 */
	public void setLabel(final String value) {
		this.label = value;
	}

	/**
	 * Sets the value of the labelLocation property.
	 *
	 * @param value allowed object is {@link LabelLocationType }
	 *
	 */
	public void setLabelLocation(final LabelLocationType value) {
		this.labelLocation = value;
	}

	/**
	 * Sets the value of the labelVisible property.
	 *
	 * @param value allowed object is {@link Boolean }
	 *
	 */
	public void setLabelVisible(final Boolean value) {
		this.labelVisible = value;
	}

	/**
	 * Sets the value of the lineStyle property.
	 *
	 * @param value allowed object is {@link BigInteger }
	 *
	 */
	public void setLineStyle(final BigInteger value) {
		this.lineStyle = value;
	}

	/**
	 * Sets the value of the lineThickness property.
	 *
	 * @param value allowed object is {@link BigInteger }
	 *
	 */
	public void setLineThickness(final BigInteger value) {
		this.lineThickness = value;
	}

	/**
	 * Sets the value of the timeRange property.
	 *
	 * @param value allowed object is {@link TimeRangeType }
	 *
	 */
	public void setTimeRange(final TimeRangeType value) {
		this.timeRange = value;
	}

	/**
	 * Sets the value of the visible property.
	 *
	 * @param value allowed object is {@link Boolean }
	 *
	 */
	public void setVisible(final Boolean value) {
		this.visible = value;
	}

}
