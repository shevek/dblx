/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.dblx.core;

import heronarts.lx.LX;
import heronarts.lx.effect.FlashEffect;
import heronarts.lx.effect.LXEffect;
import heronarts.lx.pattern.LXPattern;
import heronarts.lx.transition.AddTransition;
import heronarts.lx.transition.DissolveTransition;
import heronarts.lx.transition.FadeTransition;
import heronarts.lx.transition.LXTransition;
import heronarts.lx.transition.MultiplyTransition;
import heronarts.lx.transition.SubtractTransition;
import javax.annotation.Nonnull;
import org.anarres.dblx.core.effects.SparkleEffect;
import org.anarres.dblx.core.patterns.BlankPattern;
import org.anarres.dblx.core.patterns.PsychadelicPattern;

/**
 *
 * @author shevek
 */
public class Library {

    @Nonnull
    public static LXPattern[] patterns(@Nonnull LX lx) {
        LXPattern[] out = new LXPattern[]{
            new BlankPattern(lx),
            //new BarLengthTestPattern(lx),    
            //new PixiePattern(lx),
            new PsychadelicPattern(lx), //new SampleNodeTraversalWithFade(lx),
        //new NeuroTracePattern(lx),
        //new Scraper(lx),
        //new MuseConcMellow(lx),
        //new PixelOSCListener(lx),
        //new BrainRender(lx),
        //new VidPattern(lx),
        //new Swim(lx), // from sugarcubes
        //new WaveFrontPattern(lx),
        //new MusicResponse(lx),
        //new AVBrainPattern(lx),
        //new AHoleInMyBrain(lx),
        //had to comment out annaPattern because it wasn't working with the 
        //Playa_Brain subset - probably a specific node/bar thing.
        //She sent us a new finished version via email - 
        //TODO to add it back in and make it work! 
        //new annaPattern(lx), 
        //new RangersPattern(lx),
        //new Voronoi(lx),
        //new Serpents(lx),
        //new BrainStorm(lx),
        //new PixiePattern(lx),
        //new MoireManifoldPattern(lx),
        //new StrobePattern(lx),
        //new ColorStatic(lx),
        //new TestImagePattern(lx),
        //new HelloWorldPattern(lx),
        //new GradientPattern(lx),
        //new LXPaletteDemo(lx),
        //new TestHemispheres(lx),
        //new HeartBeatPattern(lx),
        //new RandomBarFades(lx),
        //new RainbowBarrelRoll(lx),
        //new EQTesting(lx),
        //new LayerDemoPattern(lx),
        //new CircleBounce(lx),
        //new IteratorTestPattern(lx),
        //new TestBarPattern(lx),
        };
        for (LXPattern pattern : out)
            pattern.setTransition(new DissolveTransition(lx).setDuration(1000d));
        return out;
    }

    //---------------- Transitions
    @Nonnull
    public static LXTransition[] transitions(LX lx) {
        return new LXTransition[]{
            new AddTransition(lx),
            new DissolveTransition(lx),
            new MultiplyTransition(lx),
            new SubtractTransition(lx),
            new FadeTransition(lx), // TODO(mcslee): restore these blend modes in P2LX
        // new OverlayTransition(lx),
        // new DodgeTransition(lx),
        //new SlideTransition(lx),
        //new WipeTransition(lx),
        //new IrisTransition(lx),
        };
    }

    @Nonnull
    public static LXEffect[] effects(@Nonnull LX lx) {
        return new LXEffect[]{
            new FlashEffect(lx),
            new SparkleEffect(lx)
        };
    }

    private Library() {
    }
}
