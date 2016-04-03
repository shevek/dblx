/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.dblx.ui;

import heronarts.p3lx.ui.UI;
import heronarts.p3lx.ui.UI3dComponent;
import javax.annotation.Nonnull;
import org.anarres.dblx.core.Core;
import org.anarres.dblx.ui.impl.UICrossfader;
import processing.core.PConstants;
import processing.core.PGraphics;
import static org.anarres.dblx.core.Core.*;

/**
 * Selects colors for each point based on patterns/transitions/channels.
 * Sends that data to the pointCloud to actually draw it.
 *
 * @author shevek
 */
public class UIBrainComponent extends UI3dComponent {

    private final Core core;
    private final UICrossfader uiCrossfader;
    private final UIPointCloudVBO pointCloud;

    public UIBrainComponent(@Nonnull Core core, @Nonnull UICrossfader uiCrossfader) {
        this.core = core;
        this.uiCrossfader = uiCrossfader;
        this.pointCloud = new UIPointCloudVBO(core);
    }

    @Override
    protected void onDraw(UI ui, PGraphics pg) {
        int[] simulationColors = core.lx.getColors();

        String displayMode = uiCrossfader.getDisplayMode();
        if (displayMode.equals("A")) {
            simulationColors = core.lx.engine.getChannel(LEFT_CHANNEL).getColors();
        } else if (displayMode.equals("B")) {
            simulationColors = core.lx.engine.getChannel(RIGHT_CHANNEL).getColors();
        }
        long simulationStart = System.nanoTime();
        if (core.simulationOn) {
            core.lx.applet.hint(PConstants.ENABLE_DEPTH_TEST);
            drawSimulation(simulationColors);
            core.lx.applet.hint(PConstants.DISABLE_DEPTH_TEST);
        }
        core.simulationNanos = System.nanoTime() - simulationStart;

        // translate(0,50,-400); //remove this if we're using whole brain
        //rotateX(PI*4.1); // this doesn't seem to do anything?
        core.lx.applet.camera();
        core.lx.applet.strokeWeight(1);
    }

    void drawSimulation(int[] simulationColors) {
        core.lx.applet.noStroke();
        core.lx.applet.noFill();
        pointCloud.draw(simulationColors);
    }

}
