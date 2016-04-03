/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.dblx.ui;

import heronarts.lx.parameter.BasicParameter;
import heronarts.lx.parameter.DiscreteParameter;
import heronarts.lx.parameter.LXParameter;
import heronarts.lx.parameter.LXParameterListener;
import heronarts.p3lx.ui.UI;
import heronarts.p3lx.ui.UIWindow;
import heronarts.p3lx.ui.component.UIButton;
import heronarts.p3lx.ui.component.UIIntegerBox;
import heronarts.p3lx.ui.component.UIKnob;
import heronarts.p3lx.ui.component.UILabel;
import heronarts.p3lx.ui.component.UISlider;
import heronarts.p3lx.ui.component.UIToggleSet;
import processing.core.PConstants;
import static processing.core.PApplet.println;

/**
 *
 * @author shevek
 */
public class UIComponentsDemo extends UIWindow {

    static final int NUM_KNOBS = 4;
    final BasicParameter[] knobParameters = new BasicParameter[NUM_KNOBS];

    UIComponentsDemo(UI ui, float x, float y) {
        super(ui, "UI COMPONENTS", x, y, 140, 10);

        for (int i = 0; i < knobParameters.length; ++i) {
            knobParameters[i] = new BasicParameter("Knb" + (i + 1), i + 1, 0, 4);
            knobParameters[i].addListener(new LXParameterListener() {
                public void onParameterChanged(LXParameter p) {
                    println(p.getLabel() + " value:" + p.getValue());
                }
            });
        }

        y = UIWindow.TITLE_LABEL_HEIGHT;

        new UIButton(4, y, width - 8, 20)
                .setLabel("Toggle Button")
                .addToContainer(this);
        y += 24;

        new UIButton(4, y, width - 8, 20)
                .setActiveLabel("Boop!")
                .setInactiveLabel("Momentary Button")
                .setMomentary(true)
                .addToContainer(this);
        y += 24;

        for (int i = 0; i < 4; ++i) {
            new UIKnob(4 + i * 34, y)
                    .setParameter(knobParameters[i])
                    .setEnabled(i % 2 == 0)
                    .addToContainer(this);
        }
        y += 48;

        for (int i = 0; i < 4; ++i) {
            new UISlider(UISlider.Direction.VERTICAL, 4 + i * 34, y, 30, 60)
                    .setParameter(new BasicParameter("VSl" + i, (i + 1) * .25))
                    .setEnabled(i % 2 == 1)
                    .addToContainer(this);
        }
        y += 64;

        for (int i = 0; i < 2; ++i) {
            new UISlider(4, y, width - 8, 24)
                    .setParameter(new BasicParameter("HSl" + i, (i + 1) * .25))
                    .setEnabled(i % 2 == 0)
                    .addToContainer(this);
            y += 28;
        }

        new UIToggleSet(4, y, width - 8, 24)
                .setParameter(new DiscreteParameter("Ltrs", new String[]{"A", "B", "C", "D"}))
                .addToContainer(this);
        y += 28;

        for (int i = 0; i < 4; ++i) {
            new UIIntegerBox(4 + i * 34, y, 30, 22)
                    .setParameter(new DiscreteParameter("Dcrt", 10))
                    .addToContainer(this);
        }
        y += 26;

        new UILabel(4, y, width - 8, 24)
                .setLabel("This is just a label.")
                .setAlignment(PConstants.CENTER, PConstants.CENTER)
                .setBorderColor(ui.theme.getControlDisabledColor())
                .addToContainer(this);
        y += 28;

        setSize(width, y);
    }

}
