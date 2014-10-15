/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)

 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.2-147 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.08.30 at 10:32:43 PM EDT 
//


package org.mwc.debrief.core.gpx;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for textlabelType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="textlabelType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="font" type="{org.mwc.debrief.core}fontType" minOccurs="0"/>
 *         &lt;element name="colour" type="{org.mwc.debrief.core}colourType" minOccurs="0"/>
 *         &lt;element name="centre" type="{org.mwc.debrief.core}locationType" minOccurs="0"/>
 *         &lt;element name="timeRange" type="{org.mwc.debrief.core}timeRangeType" minOccurs="0"/>
 *       &lt;/all>
 *       &lt;attribute name="Label" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="LabelLocation" type="{org.mwc.debrief.core}labelLocationType" />
 *       &lt;attribute name="LabelVisible" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="Scale" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="Symbol" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="SymbolVisible" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="Visible" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "textlabelType", propOrder = {

})
public class TextlabelType {

    protected FontType font;
    protected ColourType colour;
    protected LocationType centre;
    protected TimeRangeType timeRange;
    @XmlAttribute(name = "Label")
    protected String label;
    @XmlAttribute(name = "LabelLocation")
    protected LabelLocationType labelLocation;
    @XmlAttribute(name = "LabelVisible")
    protected Boolean labelVisible;
    @XmlAttribute(name = "Scale")
    protected String scale;
    @XmlAttribute(name = "Symbol")
    protected String symbol;
    @XmlAttribute(name = "SymbolVisible")
    protected Boolean symbolVisible;
    @XmlAttribute(name = "Visible")
    protected Boolean visible;

    /**
     * Gets the value of the font property.
     * 
     * @return
     *     possible object is
     *     {@link FontType }
     *     
     */
    public FontType getFont() {
        return font;
    }

    /**
     * Sets the value of the font property.
     * 
     * @param value
     *     allowed object is
     *     {@link FontType }
     *     
     */
    public void setFont(final FontType value) {
        this.font = value;
    }

    /**
     * Gets the value of the colour property.
     * 
     * @return
     *     possible object is
     *     {@link ColourType }
     *     
     */
    public ColourType getColour() {
        return colour;
    }

    /**
     * Sets the value of the colour property.
     * 
     * @param value
     *     allowed object is
     *     {@link ColourType }
     *     
     */
    public void setColour(final ColourType value) {
        this.colour = value;
    }

    /**
     * Gets the value of the centre property.
     * 
     * @return
     *     possible object is
     *     {@link LocationType }
     *     
     */
    public LocationType getCentre() {
        return centre;
    }

    /**
     * Sets the value of the centre property.
     * 
     * @param value
     *     allowed object is
     *     {@link LocationType }
     *     
     */
    public void setCentre(final LocationType value) {
        this.centre = value;
    }

    /**
     * Gets the value of the timeRange property.
     * 
     * @return
     *     possible object is
     *     {@link TimeRangeType }
     *     
     */
    public TimeRangeType getTimeRange() {
        return timeRange;
    }

    /**
     * Sets the value of the timeRange property.
     * 
     * @param value
     *     allowed object is
     *     {@link TimeRangeType }
     *     
     */
    public void setTimeRange(final TimeRangeType value) {
        this.timeRange = value;
    }

    /**
     * Gets the value of the label property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the value of the label property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLabel(final String value) {
        this.label = value;
    }

    /**
     * Gets the value of the labelLocation property.
     * 
     * @return
     *     possible object is
     *     {@link LabelLocationType }
     *     
     */
    public LabelLocationType getLabelLocation() {
        return labelLocation;
    }

    /**
     * Sets the value of the labelLocation property.
     * 
     * @param value
     *     allowed object is
     *     {@link LabelLocationType }
     *     
     */
    public void setLabelLocation(final LabelLocationType value) {
        this.labelLocation = value;
    }

    /**
     * Gets the value of the labelVisible property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isLabelVisible() {
        if (labelVisible == null) {
            return false;
        } else {
            return labelVisible;
        }
    }

    /**
     * Sets the value of the labelVisible property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setLabelVisible(final Boolean value) {
        this.labelVisible = value;
    }

    /**
     * Gets the value of the scale property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getScale() {
        return scale;
    }

    /**
     * Sets the value of the scale property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setScale(final String value) {
        this.scale = value;
    }

    /**
     * Gets the value of the symbol property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * Sets the value of the symbol property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSymbol(final String value) {
        this.symbol = value;
    }

    /**
     * Gets the value of the symbolVisible property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isSymbolVisible() {
        if (symbolVisible == null) {
            return false;
        } else {
            return symbolVisible;
        }
    }

    /**
     * Sets the value of the symbolVisible property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSymbolVisible(final Boolean value) {
        this.symbolVisible = value;
    }

    /**
     * Gets the value of the visible property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isVisible() {
        if (visible == null) {
            return false;
        } else {
            return visible;
        }
    }

    /**
     * Sets the value of the visible property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setVisible(final Boolean value) {
        this.visible = value;
    }

}
