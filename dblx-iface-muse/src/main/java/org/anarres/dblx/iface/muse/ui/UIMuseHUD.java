/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.dblx.iface.muse.ui;

import heronarts.p3lx.ui.UI;
import heronarts.p3lx.ui.UIWindow;
import org.anarres.dblx.iface.muse.net.MuseHUD;
import processing.core.PGraphics;

/**
 *
 * @author shevek
 */
public class UIMuseHUD extends UIWindow {

    private final static int WIDTH = 120;
    private final static int HEIGHT = 120;
    private final MuseHUD museHUD;

    public UIMuseHUD(UI ui, MuseHUD museHUD, float x, float y) {
        super(ui, "MUSE HUD", x, y, museHUD.WIDTH, museHUD.HEIGHT);
        this.museHUD = museHUD;
    }

    protected void onDraw(UI ui, PGraphics pg) {
        super.onDraw(ui, pg);
        museHUD.drawHUD(pg);
    // image(pg, mouseX-pg.width/2-VIEWPORT_WIDTH, mouseY-pg.height/2-VIEWPORT_HEIGHT);
        // pg.fill(#FFFFFF);
        // pg.rect(0,24,width,height);
        redraw();
    }

}
