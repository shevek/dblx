/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.dblx.ui.impl;

import heronarts.lx.parameter.BasicParameter;
import heronarts.lx.parameter.LXParameter;
import heronarts.lx.parameter.LXParameterListener;
import heronarts.p3lx.P3LX;
import heronarts.p3lx.ui.UIWindow;
import heronarts.p3lx.ui.component.UISlider;
import javax.annotation.Nonnull;

/**
 *
 * @author shevek
 */
public class UISpeed extends UIWindow {

    final BasicParameter speed;

    public UISpeed(@Nonnull final P3LX lx, float x, float y, float w, float h) {
        super(lx.ui, "SPEED", x, y, w, h);
        speed = new BasicParameter("SPEED", 0.5);
        speed.addListener(new LXParameterListener() {
            public void onParameterChanged(LXParameter parameter) {
                lx.setSpeed(parameter.getValuef() * 2);
            }
        });
        new UISlider(4, UIWindow.TITLE_LABEL_HEIGHT, w - 10, 20)
                .setParameter(speed)
                .addToContainer(this);
    }
}
