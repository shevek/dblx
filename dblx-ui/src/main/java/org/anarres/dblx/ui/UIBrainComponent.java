/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.dblx.ui;

import heronarts.p3lx.ui.UI;
import heronarts.p3lx.ui.UI3dComponent;
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

    final UIPointCloudVBO pointCloud = new UIPointCloudVBO();

    @Override
    protected void onDraw(UI ui, PGraphics pg) {
        color[] simulationColors = lx.getColors();

        String displayMode = uiCrossfader.getDisplayMode();
        if (displayMode.equals("A")) {
            simulationColors = lx.engine.getChannel(LEFT_CHANNEL).getColors();
        } else if (displayMode.equals("B")) {
            simulationColors = lx.engine.getChannel(RIGHT_CHANNEL).getColors();
        }
        long simulationStart = System.nanoTime();
        if (simulationOn) {
            hint(PConstants.ENABLE_DEPTH_TEST);
            drawSimulation(simulationColors);
            hint(PConstants.DISABLE_DEPTH_TEST);
        }
        simulationNanos = System.nanoTime() - simulationStart;

        // translate(0,50,-400); //remove this if we're using whole brain
        //rotateX(PI*4.1); // this doesn't seem to do anything?
        camera();
        strokeWeight(1);
    }

    void drawSimulation(color[] simulationColors) {
        noStroke();
        noFill();
        pointCloud.draw(simulationColors);
    }

}
