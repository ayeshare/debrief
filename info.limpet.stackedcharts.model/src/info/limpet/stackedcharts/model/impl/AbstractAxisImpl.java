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
/**
 */
package info.limpet.stackedcharts.model.impl;

import java.awt.Color;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import info.limpet.stackedcharts.model.AbstractAxis;
import info.limpet.stackedcharts.model.AxisDirection;
import info.limpet.stackedcharts.model.AxisScale;
import info.limpet.stackedcharts.model.AxisType;
import info.limpet.stackedcharts.model.StackedchartsPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model object
 * '<em><b>Abstract Axis</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link info.limpet.stackedcharts.model.impl.AbstractAxisImpl#getScale
 * <em>Scale</em>}</li>
 * <li>{@link info.limpet.stackedcharts.model.impl.AbstractAxisImpl#getName
 * <em>Name</em>}</li>
 * <li>{@link info.limpet.stackedcharts.model.impl.AbstractAxisImpl#isAutoScale
 * <em>Auto Scale</em>}</li>
 * <li>{@link info.limpet.stackedcharts.model.impl.AbstractAxisImpl#getDirection
 * <em>Direction</em>}</li>
 * <li>{@link info.limpet.stackedcharts.model.impl.AbstractAxisImpl#getFont
 * <em>Font</em>}</li>
 * <li>{@link info.limpet.stackedcharts.model.impl.AbstractAxisImpl#getColor
 * <em>Color</em>}</li>
 * <li>{@link info.limpet.stackedcharts.model.impl.AbstractAxisImpl#getAxisType
 * <em>Axis Type</em>}</li>
 * </ul>
 *
 * @generated
 */
public abstract class AbstractAxisImpl extends MinimalEObjectImpl.Container implements AbstractAxis {
	/**
	 * The default value of the '{@link #getScale() <em>Scale</em>}' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #getScale()
	 * @generated
	 * @ordered
	 */
	protected static final AxisScale SCALE_EDEFAULT = AxisScale.LINEAR;

	/**
	 * The default value of the '{@link #getName() <em>Name</em>}' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected static final String NAME_EDEFAULT = null;

	/**
	 * The default value of the '{@link #isAutoScale() <em>Auto Scale</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #isAutoScale()
	 * @generated
	 * @ordered
	 */
	protected static final boolean AUTO_SCALE_EDEFAULT = false;

	/**
	 * The default value of the '{@link #getDirection() <em>Direction</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #getDirection()
	 * @generated
	 * @ordered
	 */
	protected static final AxisDirection DIRECTION_EDEFAULT = AxisDirection.ASCENDING;

	/**
	 * The default value of the '{@link #getFont() <em>Font</em>}' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #getFont()
	 * @generated
	 * @ordered
	 */
	protected static final String FONT_EDEFAULT = null;

	/**
	 * The default value of the '{@link #getColor() <em>Color</em>}' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #getColor()
	 * @generated
	 * @ordered
	 */
	protected static final Color COLOR_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getScale() <em>Scale</em>}' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #getScale()
	 * @generated
	 * @ordered
	 */
	protected AxisScale scale = SCALE_EDEFAULT;

	/**
	 * The cached value of the '{@link #getName() <em>Name</em>}' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected String name = NAME_EDEFAULT;

	/**
	 * The cached value of the '{@link #isAutoScale() <em>Auto Scale</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #isAutoScale()
	 * @generated
	 * @ordered
	 */
	protected boolean autoScale = AUTO_SCALE_EDEFAULT;

	/**
	 * The cached value of the '{@link #getDirection() <em>Direction</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #getDirection()
	 * @generated
	 * @ordered
	 */
	protected AxisDirection direction = DIRECTION_EDEFAULT;

	/**
	 * The cached value of the '{@link #getFont() <em>Font</em>}' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #getFont()
	 * @generated
	 * @ordered
	 */
	protected String font = FONT_EDEFAULT;

	/**
	 * The cached value of the '{@link #getColor() <em>Color</em>}' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #getColor()
	 * @generated
	 * @ordered
	 */
	protected Color color = COLOR_EDEFAULT;

	/**
	 * The cached value of the '{@link #getAxisType() <em>Axis Type</em>}'
	 * containment reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #getAxisType()
	 * @generated
	 * @ordered
	 */
	protected AxisType axisType;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	protected AbstractAxisImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public NotificationChain basicSetAxisType(final AxisType newAxisType, NotificationChain msgs) {
		final AxisType oldAxisType = axisType;
		axisType = newAxisType;
		if (eNotificationRequired()) {
			final ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
					StackedchartsPackage.ABSTRACT_AXIS__AXIS_TYPE, oldAxisType, newAxisType);
			if (msgs == null)
				msgs = notification;
			else
				msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public Object eGet(final int featureID, final boolean resolve, final boolean coreType) {
		switch (featureID) {
		case StackedchartsPackage.ABSTRACT_AXIS__SCALE:
			return getScale();
		case StackedchartsPackage.ABSTRACT_AXIS__NAME:
			return getName();
		case StackedchartsPackage.ABSTRACT_AXIS__AUTO_SCALE:
			return isAutoScale();
		case StackedchartsPackage.ABSTRACT_AXIS__DIRECTION:
			return getDirection();
		case StackedchartsPackage.ABSTRACT_AXIS__FONT:
			return getFont();
		case StackedchartsPackage.ABSTRACT_AXIS__COLOR:
			return getColor();
		case StackedchartsPackage.ABSTRACT_AXIS__AXIS_TYPE:
			return getAxisType();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(final InternalEObject otherEnd, final int featureID,
			final NotificationChain msgs) {
		switch (featureID) {
		case StackedchartsPackage.ABSTRACT_AXIS__AXIS_TYPE:
			return basicSetAxisType(null, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public boolean eIsSet(final int featureID) {
		switch (featureID) {
		case StackedchartsPackage.ABSTRACT_AXIS__SCALE:
			return scale != SCALE_EDEFAULT;
		case StackedchartsPackage.ABSTRACT_AXIS__NAME:
			return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
		case StackedchartsPackage.ABSTRACT_AXIS__AUTO_SCALE:
			return autoScale != AUTO_SCALE_EDEFAULT;
		case StackedchartsPackage.ABSTRACT_AXIS__DIRECTION:
			return direction != DIRECTION_EDEFAULT;
		case StackedchartsPackage.ABSTRACT_AXIS__FONT:
			return FONT_EDEFAULT == null ? font != null : !FONT_EDEFAULT.equals(font);
		case StackedchartsPackage.ABSTRACT_AXIS__COLOR:
			return COLOR_EDEFAULT == null ? color != null : !COLOR_EDEFAULT.equals(color);
		case StackedchartsPackage.ABSTRACT_AXIS__AXIS_TYPE:
			return axisType != null;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public void eSet(final int featureID, final Object newValue) {
		switch (featureID) {
		case StackedchartsPackage.ABSTRACT_AXIS__SCALE:
			setScale((AxisScale) newValue);
			return;
		case StackedchartsPackage.ABSTRACT_AXIS__NAME:
			setName((String) newValue);
			return;
		case StackedchartsPackage.ABSTRACT_AXIS__AUTO_SCALE:
			setAutoScale((Boolean) newValue);
			return;
		case StackedchartsPackage.ABSTRACT_AXIS__DIRECTION:
			setDirection((AxisDirection) newValue);
			return;
		case StackedchartsPackage.ABSTRACT_AXIS__FONT:
			setFont((String) newValue);
			return;
		case StackedchartsPackage.ABSTRACT_AXIS__COLOR:
			setColor((Color) newValue);
			return;
		case StackedchartsPackage.ABSTRACT_AXIS__AXIS_TYPE:
			setAxisType((AxisType) newValue);
			return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return StackedchartsPackage.Literals.ABSTRACT_AXIS;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public void eUnset(final int featureID) {
		switch (featureID) {
		case StackedchartsPackage.ABSTRACT_AXIS__SCALE:
			setScale(SCALE_EDEFAULT);
			return;
		case StackedchartsPackage.ABSTRACT_AXIS__NAME:
			setName(NAME_EDEFAULT);
			return;
		case StackedchartsPackage.ABSTRACT_AXIS__AUTO_SCALE:
			setAutoScale(AUTO_SCALE_EDEFAULT);
			return;
		case StackedchartsPackage.ABSTRACT_AXIS__DIRECTION:
			setDirection(DIRECTION_EDEFAULT);
			return;
		case StackedchartsPackage.ABSTRACT_AXIS__FONT:
			setFont(FONT_EDEFAULT);
			return;
		case StackedchartsPackage.ABSTRACT_AXIS__COLOR:
			setColor(COLOR_EDEFAULT);
			return;
		case StackedchartsPackage.ABSTRACT_AXIS__AXIS_TYPE:
			setAxisType((AxisType) null);
			return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public AxisType getAxisType() {
		return axisType;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public Color getColor() {
		return color;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public AxisDirection getDirection() {
		return direction;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public String getFont() {
		return font;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public AxisScale getScale() {
		return scale;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public boolean isAutoScale() {
		return autoScale;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public void setAutoScale(final boolean newAutoScale) {
		final boolean oldAutoScale = autoScale;
		autoScale = newAutoScale;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, StackedchartsPackage.ABSTRACT_AXIS__AUTO_SCALE,
					oldAutoScale, autoScale));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public void setAxisType(final AxisType newAxisType) {
		if (newAxisType != axisType) {
			NotificationChain msgs = null;
			if (axisType != null)
				msgs = ((InternalEObject) axisType).eInverseRemove(this,
						EOPPOSITE_FEATURE_BASE - StackedchartsPackage.ABSTRACT_AXIS__AXIS_TYPE, null, msgs);
			if (newAxisType != null)
				msgs = ((InternalEObject) newAxisType).eInverseAdd(this,
						EOPPOSITE_FEATURE_BASE - StackedchartsPackage.ABSTRACT_AXIS__AXIS_TYPE, null, msgs);
			msgs = basicSetAxisType(newAxisType, msgs);
			if (msgs != null)
				msgs.dispatch();
		} else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, StackedchartsPackage.ABSTRACT_AXIS__AXIS_TYPE,
					newAxisType, newAxisType));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public void setColor(final Color newColor) {
		final Color oldColor = color;
		color = newColor;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, StackedchartsPackage.ABSTRACT_AXIS__COLOR, oldColor,
					color));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public void setDirection(final AxisDirection newDirection) {
		final AxisDirection oldDirection = direction;
		direction = newDirection == null ? DIRECTION_EDEFAULT : newDirection;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, StackedchartsPackage.ABSTRACT_AXIS__DIRECTION,
					oldDirection, direction));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public void setFont(final String newFont) {
		final String oldFont = font;
		font = newFont;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, StackedchartsPackage.ABSTRACT_AXIS__FONT, oldFont,
					font));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public void setName(final String newName) {
		final String oldName = name;
		name = newName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, StackedchartsPackage.ABSTRACT_AXIS__NAME, oldName,
					name));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public void setScale(final AxisScale newScale) {
		final AxisScale oldScale = scale;
		scale = newScale == null ? SCALE_EDEFAULT : newScale;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, StackedchartsPackage.ABSTRACT_AXIS__SCALE, oldScale,
					scale));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy())
			return super.toString();

		final StringBuffer result = new StringBuffer(super.toString());
		result.append(" (scale: ");
		result.append(scale);
		result.append(", name: ");
		result.append(name);
		result.append(", autoScale: ");
		result.append(autoScale);
		result.append(", direction: ");
		result.append(direction);
		result.append(", font: ");
		result.append(font);
		result.append(", color: ");
		result.append(color);
		result.append(')');
		return result.toString();
	}

} // AbstractAxisImpl
