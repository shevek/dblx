/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.dblx.ui;

import heronarts.lx.color.LXPalette;
import heronarts.lx.parameter.BooleanParameter;
import heronarts.lx.parameter.LXParameter;
import heronarts.lx.parameter.LXParameterListener;
import heronarts.p3lx.ui.UI;
import heronarts.p3lx.ui.UIWindow;
import heronarts.p3lx.ui.component.UIColorSwatch;
import heronarts.p3lx.ui.component.UIKnob;
import heronarts.p3lx.ui.component.UISlider;
import heronarts.p3lx.ui.component.UISwitch;
import javax.annotation.Nonnull;
import org.anarres.dblx.core.Core;
import org.anarres.dblx.core.palettes.HueCyclePalette;
import processing.core.PGraphics;

/**
 *
 * @author shevek
 */
public class UIGlobalControl extends UIWindow {

    private final Core core;

    UIGlobalControl(@Nonnull Core core, float x, float y) {
        super(core.lx.ui, "GLOBAL", x, y, 140, 246);
        this.core = core;

        float yp = TITLE_LABEL_HEIGHT;
        final UIColorSwatch swatch = new UIColorSwatch(UIGlobalControl.this.core.palette, 4, yp, width - 8, 60) {
            protected void onDraw(UI ui, PGraphics pg) {
                super.onDraw(ui, pg);
                HueCyclePalette palette = UIGlobalControl.this.core.palette;
                if (palette.hueMode.getValuei() == LXPalette.HUE_MODE_CYCLE) {
                    palette.clr.hue.setValue(UIGlobalControl.this.core.palette.getHue());
                    redraw();
                }
            }
        };
        new UIKnob(4, yp).setParameter(core.palette.spread).addToContainer(this);
        new UIKnob(40, yp).setParameter(core.palette.center).addToContainer(this);

        final BooleanParameter hueCycle = new BooleanParameter("Cycle", core.palette.hueMode.getValuei() == LXPalette.HUE_MODE_CYCLE);
        new UISwitch(76, yp).setParameter(hueCycle).addToContainer(this);
        yp += 48;

        swatch.setEnabled(core.palette.hueMode.getValuei() == LXPalette.HUE_MODE_STATIC).setPosition(4, yp).addToContainer(this);
        yp += 64;

        hueCycle.addListener(new LXParameterListener() {
            public void onParameterChanged(LXParameter p) {
                HueCyclePalette palette = UIGlobalControl.this.core.palette;
                palette.hueMode.setValue(hueCycle.isOn() ? LXPalette.HUE_MODE_CYCLE : LXPalette.HUE_MODE_STATIC);
            }
        });

        core.palette.hueMode.addListener(new LXParameterListener() {
            public void onParameterChanged(LXParameter p) {
                HueCyclePalette palette = UIGlobalControl.this.core.palette;
                swatch.setEnabled(palette.hueMode.getValuei() == LXPalette.HUE_MODE_STATIC);
                hueCycle.setValue(palette.hueMode.getValuei() == LXPalette.HUE_MODE_CYCLE);
            }
        });

        new UISlider(3, yp, width - 6, 30).setParameter(core.palette.zeriod).setLabel("Color Speed").addToContainer(this);
        yp += 58;
        new UISlider(3, yp, width - 6, 30).setParameter(core.lx.engine.speed).setLabel("Speed").addToContainer(this);
    }
}
