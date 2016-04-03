/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.dblx.core;

import heronarts.lx.effect.LXEffect;
import heronarts.lx.parameter.DiscreteParameter;
import heronarts.lx.pattern.LXPattern;
import heronarts.lx.transition.LXTransition;
import heronarts.p3lx.P3LX;
import org.anarres.dblx.core.palettes.HueCyclePalette;

/**
 *
 * @author shevek
 */
public class Core {

    public static final int LEFT_CHANNEL = 0;
    public static final int RIGHT_CHANNEL = 1;
    @Deprecated
    public static final int INCHES = 1;
    @Deprecated
    public static final int FEET = 12 * INCHES;

    public P3LX lx;
    // LXGraphModel model;
    public LXPattern[] patterns;
    public LXTransition[] transitions;
    // Effects effects;
    public LXEffect[] effectsArr;
    public DiscreteParameter selectedEffect;

    // public NerveBundle nervebundle;
    public HueCyclePalette palette;

    // global parameter to adjust output brightness
    // MJP: unclear if this affects display brightness as well
    public double global_brightness = 1.0;

    public LXEffect getSelectedEffect() {
        return effectsArr[selectedEffect.getValuei()];
    }
    private static long lastMillis = System.currentTimeMillis();

    @Deprecated
    public static void logTime(String evt) {
        long now = System.currentTimeMillis();
        System.out.format("%5d ms: %s\n", (now - lastMillis), evt);
        lastMillis = now;
    }

}
