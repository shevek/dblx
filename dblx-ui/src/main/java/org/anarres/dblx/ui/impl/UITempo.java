/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.dblx.ui.impl;

import heronarts.lx.Tempo;
import heronarts.lx.parameter.BasicParameter;
import heronarts.lx.parameter.LXParameter;
import heronarts.lx.parameter.LXParameterListener;
import heronarts.p3lx.P3LX;
import heronarts.p3lx.ui.UI;
import heronarts.p3lx.ui.UI2dComponent;
import heronarts.p3lx.ui.UIWindow;
import heronarts.p3lx.ui.component.UIButton;
import heronarts.p3lx.ui.component.UIKnob;
import javax.annotation.Nonnull;
import processing.core.PApplet;
import processing.core.PGraphics;

/**
 *
 * @author shevek
 */
public class UITempo extends UIWindow {

    private final UIButton tempoButton;
    UIKnob tempoKnobFine;
    UIKnob tempoKnobCoarse;
    BasicParameter tempoAdjustFine;
    BasicParameter tempoAdjustCoarse;

    UITempo(@Nonnull final P3LX lx, float x, float y, float w, float h) {
        super(lx.ui, "TEMPO", x, y, w, h);

        tempoButton = new UIButton(4, UIWindow.TITLE_LABEL_HEIGHT, w - 75, 20) {
            protected void onToggle(boolean active) {
                if (active) {
                    lx.tempo.tap();
                }
            }
        }.setMomentary(true);

        LXParameterListener tempoListener = new LXParameterListener() {
            public void onParameterChanged(LXParameter parameter) {
                if (parameter == tempoAdjustFine) {
                    tempoKnobFine.setParameter(tempoAdjustFine);
                } else if (parameter == tempoAdjustCoarse) {
                    tempoKnobCoarse.setParameter(tempoAdjustCoarse);
                }
            }
        };

        tempoKnobFine = new UIKnob(w - 65, UIWindow.TITLE_LABEL_HEIGHT - 20);
        tempoKnobCoarse = new UIKnob(w - 35, UIWindow.TITLE_LABEL_HEIGHT - 20);

        tempoAdjustFine = new BasicParameter("temF", 0, -3, 3);
        tempoAdjustCoarse = new BasicParameter("temC", 0, 0, 300);
        tempoAdjustFine.addListener(new LXParameterListener() {
            public void onParameterChanged(LXParameter tempoAdjustFine) {
                lx.tempo.adjustBpm(tempoAdjustFine.getValue());
            }
        });
        tempoAdjustCoarse.addListener(new LXParameterListener() {
            public void onParameterChanged(LXParameter tempoAdjustCoarse) {
                lx.tempo.setBpm(tempoAdjustCoarse.getValuef());
            }
        });
        tempoKnobFine.setParameter(tempoAdjustFine);
        tempoKnobCoarse.setParameter(tempoAdjustCoarse);
        tempoKnobFine.addToContainer(this);
        tempoKnobCoarse.addToContainer(this);
        tempoButton.addToContainer(this);

        new UITempoBlipper(lx, 8, UIWindow.TITLE_LABEL_HEIGHT + 4, 12, 12).addToContainer(this);
    }

    /** Not static because tempoButton. */
    private class UITempoBlipper extends UI2dComponent {

        private final P3LX lx;

        /** Needs P3LX for lx.applet.color. */
        UITempoBlipper(@Nonnull P3LX lx, float x, float y, float w, float h) {
            super(x, y, w, h);
            this.lx = lx;
        }

        protected void onDraw(UI ui, PGraphics pg) {
            tempoButton.setLabel("" + ((int) (lx.tempo.bpm() * 10)) / 10.);

            // Overlay tempo thing with openGL, redraw faster than button UI
            pg.fill(lx.applet.color(0, 0, 24 - 8 * lx.tempo.rampf()));
            pg.noStroke();
            pg.rect(0, 0, width, height);
            redraw();
        }
    }

}
