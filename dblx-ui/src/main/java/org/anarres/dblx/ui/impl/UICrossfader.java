/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.dblx.ui.impl;

import heronarts.p3lx.P3LX;
import heronarts.p3lx.ui.UIWindow;
import heronarts.p3lx.ui.component.UISlider;
import heronarts.p3lx.ui.component.UIToggleSet;
import static org.anarres.dblx.core.Core.*;

/**
 *
 * @author shevek
 */
public class UICrossfader extends UIWindow {

    private final UIToggleSet displayMode;

    public UICrossfader(P3LX lx, float x, float y, float w, float h) {
        super(lx.ui, "CROSSFADER", x, y, w, h);

        new UISlider(4, UIWindow.TITLE_LABEL_HEIGHT, w - 9, 32)
                .setParameter(lx.engine.getChannel(RIGHT_CHANNEL).getFader())
                .addToContainer(this);
        (displayMode = new UIToggleSet(4, UIWindow.TITLE_LABEL_HEIGHT + 36, w - 9, 20))
                .setOptions(new String[]{"A", "COMP", "B"})
                .setValue("COMP")
                .addToContainer(this);
    }

    public UICrossfader setDisplayMode(String value) {
        displayMode.setValue(value);
        return this;
    }

    public String getDisplayMode() {
        return displayMode.getValue();
    }

}
