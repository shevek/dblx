/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.dblx.ui.impl;

import heronarts.p3lx.P3LX;
import heronarts.p3lx.ui.UI;
import heronarts.p3lx.ui.UI2dContext;
import javax.annotation.Nonnull;
import processing.core.PGraphics;
import static processing.core.PConstants.LEFT;
import static processing.core.PConstants.TOP;

/**
 *
 * @author shevek
 */
public class UIDebugText extends UI2dContext {

    private String line1 = "";
    private String line2 = "";

    public UIDebugText(@Nonnull P3LX lx, float x, float y, float w, float h) {
        super(lx.ui, x, y, w, h);
    }

    public UIDebugText setText(String line1) {
        return setText(line1, "");
    }

    public UIDebugText setText(String line1, String line2) {
        if (!line1.equals(this.line1) || !line2.equals(this.line2)) {
            this.line1 = line1;
            this.line2 = line2;
            setVisible(line1.length() + line2.length() > 0);
            redraw();
        }
        return this;
    }

    protected void onDraw(UI ui, PGraphics pg) {
        super.onDraw(ui, pg);
        if (line1.length() + line2.length() > 0) {
            pg.noStroke();
            pg.fill(Integer.parseInt("444444", 16));
            pg.rect(0, 0, width, height);
            pg.textFont(ui.theme.getControlFont());
            pg.textSize(10);
            pg.textAlign(LEFT, TOP);
            pg.fill(Integer.parseInt("cccccc", 16));
            pg.text(line1, 4, 4);
            pg.text(line2, 4, 24);
        }
    }

}
