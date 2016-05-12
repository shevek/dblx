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
import org.anarres.dblx.core.model.Model;
import org.anarres.dblx.core.palettes.HueCyclePalette;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author shevek
 */
public class Core {

    private static final Logger LOG = LoggerFactory.getLogger(Core.class);
    /** This is the index of the LXEngine channel for the left side of the GUI. */
    public static final int LX_CHANNEL_LEFT = 0;
    /** This is the index of the LXEngine channel for the right side of the GUI. */
    public static final int LX_CHANNEL_RIGHT = 1;

    public P3LX lx;
    public Model model;
    public LXPattern[] patterns;
    public LXTransition[] transitions;
    public LXEffect[] effects;
    public DiscreteParameter selectedEffect;

    // public NerveBundle nervebundle;
    public HueCyclePalette palette;

    // global parameter to adjust output brightness
    // MJP: unclear if this affects display brightness as well
    public double global_brightness = 1.0;

    public boolean simulationOn;
    public long simulationNanos;

    public LXEffect getSelectedEffect() {
        return effects[selectedEffect.getValuei()];
    }
}
