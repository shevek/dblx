/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.dblx.core;

import javax.annotation.Nonnull;
import javax.measure.converter.UnitConverter;
import javax.measure.quantity.Length;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

/**
 *
 * @author shevek
 */
public enum LengthUnit {

    INCH(NonSI.INCH),
    FOOT(NonSI.FOOT);

    private final UnitConverter unitConverter;


    // = NonSI.INCH.getConverterTo(SI.MILLIMETER);
    /* pp */ LengthUnit(@Nonnull Unit<Length> unit) {
        this.unitConverter = unit.getConverterTo(SI.MILLIMETRE);
    }

    public long toMillimetres(long length) {
        return (long) unitConverter.convert(length);
    }

    public double toMillimetres(double length) {
        return unitConverter.convert(length);
    }

}
