/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.dblx.core.palettes;

import heronarts.lx.LX;
import heronarts.lx.color.LXPalette;
import heronarts.lx.model.LXPoint;
import heronarts.lx.parameter.BasicParameter;
import heronarts.lx.parameter.LXParameter;
import heronarts.lx.parameter.LXParameterListener;
import org.anarres.dblx.core.Core;
import static processing.core.PApplet.abs;

/**
 *
 * @author shevek
 */
public class HueCyclePalette extends LXPalette {

    public final BasicParameter zeriod = new BasicParameter("Period", 5000, 0, 30000);
    public final BasicParameter spread = new BasicParameter("Spread", 2, 0, 8);
    public final BasicParameter center = new BasicParameter("Center", model.cx - 10 * Core.INCHES, model.xMin, model.xMax);

    HueCyclePalette(LX lx) {
        super(lx);
        addParameter(zeriod);
        addParameter(spread);
        addParameter(center);

        zeriod.addListener(new LXParameterListener() {
            public void onParameterChanged(LXParameter p) {
                period.setValue(zeriod.getValue());
            }
        });

    }

    public double getHue(LXPoint p) {
        return super.getHue() + spread.getValue() * (abs(p.x - center.getValuef()) + abs(p.y - model.cy));
    }

}
