/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.dblx.iface.muse.ui;

import heronarts.lx.parameter.BooleanParameter;
import heronarts.lx.parameter.LXParameter;
import heronarts.lx.parameter.LXParameterListener;
import heronarts.p3lx.ui.UI;
import heronarts.p3lx.ui.UIWindow;
import heronarts.p3lx.ui.component.UIButton;
import org.anarres.dblx.iface.muse.net.MuseConnect;

/**
 *
 * @author shevek
 */
public class UIMuseControl extends UIWindow {

    // requires the MuseConnect and MuseHUD objects to be created on the global space
    private MuseConnect muse;
    private final static int WIDTH = 140;
    private final static int HEIGHT = 50;

    public UIMuseControl(UI ui, MuseConnect muse, float x, float y) {
        super(ui, "MUSE CONTROL", x, y, WIDTH, HEIGHT);
        this.muse = muse;
        float yp = UIWindow.TITLE_LABEL_HEIGHT;

        final BooleanParameter bMuseActivated = new BooleanParameter("bMuseActivated");

        new UIButton(4, yp, WIDTH - 8, 20)
                .setActiveLabel("Muse Activated")
                .setParameter(bMuseActivated)
                .setInactiveLabel("Muse Deactivated")
                .addToContainer(this);
        bMuseActivated.addListener(new LXParameterListener() {
            public void onParameterChanged(LXParameter parameter) {
                // TODO museActivated = parameter.getValue() > 0f;
            }
        });
        yp += 24;

    }

}
