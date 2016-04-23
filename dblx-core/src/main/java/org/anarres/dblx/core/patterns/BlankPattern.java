/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.dblx.core.patterns;

import heronarts.lx.LX;
import heronarts.lx.pattern.LXPattern;
import javax.annotation.Nonnull;

/**
 *
 * @author shevek
 */
public class BlankPattern extends LXPattern {

    public BlankPattern(@Nonnull LX lx) {
        super(lx);
    }

    @Override
    public void run(double deltaMs) {
        setColors(0);
    }

}
