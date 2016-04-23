/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.dblx.ui;

import heronarts.lx.LXChannel;
import heronarts.lx.LXEngine;
import heronarts.lx.color.LXPalette;
import heronarts.lx.parameter.DiscreteParameter;
import heronarts.lx.transition.LXTransition;
import heronarts.p3lx.P3LX;
import heronarts.p3lx.ui.UI;
import heronarts.p3lx.ui.UI2dContext;
import heronarts.p3lx.ui.UI3dContext;
import heronarts.p3lx.ui.control.UIChannelControl;
import org.anarres.dblx.core.Core;
import org.anarres.dblx.core.LengthUnit;
import org.anarres.dblx.core.Library;
import org.anarres.dblx.core.model.Model;
import org.anarres.dblx.core.palettes.HueCyclePalette;
import org.anarres.dblx.ui.impl.UIBlendMode;
import org.anarres.dblx.ui.impl.UICrossfader;
import org.anarres.dblx.ui.impl.UIDebugText;
import org.anarres.dblx.ui.impl.UIEffects;
import org.anarres.dblx.ui.impl.UISpeed;
import org.anarres.dblx.ui.impl.UITempo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import processing.core.PApplet;
import processing.core.PGraphics;

/**
 *
 * @author shevek
 */
public class Sketch extends PApplet {

    private static final Logger LOG = LoggerFactory.getLogger(Sketch.class);

    private static final int VIEWPORT_WIDTH = 1200;
    private static final int VIEWPORT_HEIGHT = 900;
    private static final int FPS_TARGET = 60;

    private final Core core;

    public Sketch(Core core) {
        this.core = core;
    }

    @Override
    public void settings() {
        super.settings();
        size(displayWidth, displayHeight);
    }

    @Override
    public void setup() {
        super.setup();
        LOG.info("Setting up.");

        colorMode(HSB);                                 // nicer color mode
        size(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, OPENGL);  // startup screen size
        frame.setResizable(true);                       // but is resizable

        //not necessary, uncomment and play with it if the frame has issues
        //size((int)screenSize.getWidth(), (int)screenSize.getHeight(), OPENGL);
        //frame.setSize((int)screenSize.getWidth(), (int)screenSize.getHeight());
        //framerates
        frameRate(FPS_TARGET);
        noSmooth();
        LOG.debug("Created viewport");

        //==================================================================== Model 
        //Actually builds the model (per mappings.pde)
        final Model model = core.model;
        LOG.debug("Loaded Model");
        //model.initialize();
        //model.setChannelMap();
  /* uncomment to check pixel indexes
         for (int i =0; i<48; i++){
         println(i);
         println(model.channelMap.get(i)); 
         }*/

        LOG.debug("Initialized Model " + model.getName()
                + ". Total pixels: " + model.points.size());

        //===================================================================== P2LX
        P3LX lx = new P3LX(this, model);
        lx.enableKeyboardTempo();
        LOG.debug("Initialized LX");

        //-------------- Engine
        LXEngine engine = lx.engine;
        //engine.framesPerSecond.setValue(FPS_TARGET);
        engine.setThreaded(false);
        LOG.debug("Initialized Engine");

        //-------------- Patterns
        engine.setPatterns(Library.patterns(lx));
        engine.addChannel(Library.patterns(lx));
        LOG.debug("Initialized Patterns");

        //-------------- Transitions
        LXTransition[] transitions = Library.transitions(lx);
        engine.getChannel(Core.LX_CHANNEL_RIGHT)
                .setFaderTransition(transitions[0]);
        LOG.debug("Initialized Transitions");

        //-------------- Effects
        lx.addEffects(Library.effects(lx));
        // TODO: getEffects() may contain effects not added above for various reasons.
        core.selectedEffect = new DiscreteParameter("EFFECT", lx.getEffects().size());
        LOG.debug("Built Effects");

        //-------------- Presets
        // PresetManager presetManager = new PresetManager(lx);
        // LOG.debug("Loaded Presets");
        //-------------- MIDI
        //midiEngine = new MidiEngine();
        //LOG.debug("Setup MIDI devices");
        //-------------- Nerve Bundle
        // nervebundle = new NerveBundle(lx);
        //-------------- Global Palette
        //println("Available Capture Devices: " + Capture.list());
        LXPalette palette = new HueCyclePalette(lx);
        palette.hueMode.setValue(LXPalette.HUE_MODE_CYCLE);
        engine.getChannel(0).setPalette(palette);
        engine.addLoopTask(palette);
        LOG.debug("Created deprecated global color palette");

        final UICrossfader uiCrossfader = new UICrossfader(core.lx, width / 2 - 130, height - 90, 180, 86);

        //==================================================== Initialize sensors
        //initialize the Muse connection
        // TODO: this should gracefully handle lack of Muse OSC input
        // muse = new MuseConnect(this, MUSE_OSCPORT);
        // museHUD = new MuseHUD(muse);
        // LOG.debug("added Muse OSC parser and HUD");
        //====================================================== 3D Simulation Layer
        //adjust this if you want to play with the initial camera setting.
        // A camera layer makes an OpenGL layer that we can easily 
        // pivot around with the mouse
        UI3dContext context3d = new UI3dContext(lx.ui) {
            @Override
            protected void beforeDraw(UI ui, PGraphics pg) {
                // Let's add lighting and depth-testing to our 3-D simulation
                pointLight(0, 0, 40, model.cx, model.cy, LengthUnit.FOOT.toMillimetres(-20L));
                pointLight(0, 0, 50, model.cx, model.yMax + LengthUnit.FOOT.toMillimetres(10L), model.cz);
                pointLight(0, 0, 20, model.cx, model.yMin - LengthUnit.FOOT.toMillimetres(10L), model.cz);
                //hint(ENABLE_DEPTH_TEST);
            }

            @Override
            protected void afterDraw(UI ui, PGraphics pg) {
                // Turn off the lights and kill depth testing before the 2D layers
                noLights();
                hint(DISABLE_DEPTH_TEST);
            }
        }
                .setRadius(LengthUnit.FOOT.toMillimetres(50L)) // distance
                .setTheta(PI) // look at front of model
                //.setPhi(PI/24)                // which axis?
                //.setRotateVelocity(12*PI)     // broken?
                //.setRotateAcceleration(3*PI)  // broken?
                .setPerspective(0)
                .setCenter(model.cx, model.cy, model.cz)
                // Let's add a point cloud of our animation points
                .addComponent(new UIBrainComponent(core, uiCrossfader));
        lx.ui.addLayer(context3d);

        //=========================================================== 2D Control GUI 
        // A basic built-in 2-D control for a channel
        //lx.ui.addLayer(new UIChannelControl(lx.ui, lx.engine.getChannel(0), 4, 4));
        //lx.ui.addLayer(new UIEngineControl(lx.ui, 4, 326));
        //lx.ui.addLayer(new UIComponentsDemo(lx.ui, width-144, 4));
        //lx.ui.addLayer(new UIGlobalControl(lx.ui, width-288, 4));
        //lx.ui.addLayer(new UICameraControl(lx.ui, context, 4, 450));
        //MJP channel initialization is now global
        LXChannel L = lx.engine.getChannel(Core.LX_CHANNEL_LEFT);
        LXChannel R = lx.engine.getChannel(Core.LX_CHANNEL_RIGHT);

        UIChannelControl uiPatternL = new UIChannelControl(lx.ui, L, "PRIMARY PATTERNS", 16, 4, 4);
        UIChannelControl uiPatternR = new UIChannelControl(lx.ui, R, "MIXING PATTERNS", 16, width - 144, 4);
        UIChannelControl uiPatternA = uiPatternL;

        UI2dContext[] layers = new UI2dContext[]{
            // Left controls
            uiPatternL,
            new UIEffects(core, 4, 374, 140, 144),
            new UITempo(core.lx, 4, 522, 140, 50),
            new UISpeed(core.lx, 4, 576, 140, 50),
            new UIBrainlove(core, 4, 620, 140, 100),
            // Right controls
            uiPatternR,
            //uiMidi = new UIMidi(midiEngine, width-144, 374, 140, 158),

            // Crossfader
            uiCrossfader,
            new UIBlendMode(core, width / 2 + 54, height - 90, 140, 86),
            // Overlays
            new UIDebugText(core.lx, 148, height - 138, width - 304, 44)
        //uiMapping = new UIMapping(mappingTool, 4, 4, 140, 324)
        //add the MuseControl toggle UI & HUD
        // uiMuseControl = new UIMuseControl(lx.ui, muse, width - 150, height - 350),
        // uiMuseHUD = new UIMuseHUD(lx.ui, museHUD, width - 150, height - 300),
        };

        //layers.addLayer(uiPatternL);
        //layers.addLayer(uiPatternR);
        //uiMapping.setVisible(false);  
        for (UI2dContext layer : layers) {
            lx.ui.addLayer(layer);
        }

        LOG.debug("Built UI");

        //==================================================== Output to Controllers
        /*
         if (OUTPUT == null) {
         LOG.debug("Not setting up hardware output");
         } else {
         lx.ui.addLayer(new UIOutput(lx.ui, width - 144, 400, 140, 106));
         if (OUTPUT == "BeagleBone") {
         addBeagleBones((LX) lx);
         LOG.debug("Built output clients");
         }
         }
         */
    }

    void drawFPS() {
        fill(0x999999);
        textSize(9);
        textAlign(LEFT, BASELINE);
        text("FPS: " + ((int) (frameRate * 10)) / 10. + " / " + "60" + " (-/+)", 4, height - 4);
    }

    @Override
    public void draw() {
        background(0);

        background(40);
        int[] sendColors = core.lx.getColors();
        long gammaStart = System.nanoTime();

        drawFPS();
    }

}
