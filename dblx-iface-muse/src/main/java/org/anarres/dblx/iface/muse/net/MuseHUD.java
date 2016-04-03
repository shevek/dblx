/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.dblx.iface.muse.net;

import org.anarres.dblx.core.Core;
import processing.core.PConstants;
import processing.core.PGraphics;
import static processing.core.PApplet.println;

/**
 *
 * @author shevek
 */
public class MuseHUD {

    /*
     * Displays the connection quality and battery of a Muse headset.
     * Currently doesnt apppear on the screen, should eventually dynamically
     * adjust position to be in lower left corner
     * This is using Processing primitives, not the LX framework for a UI. 
     * Could definitely use a revamp
     */
    MuseConnect muse; //have reference to muse object
    PGraphics pgMuseHUD; //have basic PGraphics image to display on screen

    public final int WIDTH = 140;
    public final int HEIGHT = 240;
    public final int VOFFSET = -10;

    public final int GRAPHBASE = 100;
    public final int GRAPHHEIGHT = 80;
    public final int BARWIDTH = 8;

    // colors for muse horseshoe HUD
    //  colorMode(RGB, 255);
    private int morange = 0;
    private int mgreen = 0;
    private int mblue = 0;
    private int mred = 0;
    private int morangel = 0;
    private int mgreenl = 0;
    private int mbluel = 0;
    private int mredl = 0;

    private static int color(int r, int g, int b) {
        return 0xFF000000 | (r << 16) | (g << 8) | (b);
    }

    public MuseHUD(Core core, MuseConnect muse) {
        this.muse = muse;
        if (muse == null) {
            println("Muse object has not been instantiated yet");
        }
        pgMuseHUD = core.lx.applet.createGraphics(WIDTH, HEIGHT);

        // set up horseshoe colors
        pgMuseHUD.colorMode(PConstants.RGB, 255);
        morange = color(204, 102, 0);
        mgreen = color(102, 204, 0);
        mblue = color(0, 102, 204);
        mred = color(204, 0, 102);
        morangel = color(233, 187, 142);
        mgreenl = color(187, 233, 142);
        mbluel = color(142, 187, 233);
        mredl = color(233, 142, 187);

    }

    private int barHeight(int maxHeight, float value) {
        if (value > 1.0) {
            value = 1;
        }
        return (int) Math.floor(-(maxHeight * value));
    }

    private void updateHUD(PGraphics image) {
        image.colorMode(PConstants.RGB, 255);
        int backColor = 50; //dark gray
        int foreColor = 200; // not quite white
        image.fill(foreColor);
        image.stroke(0);
        image.beginDraw();
        image.smooth();
        image.background(0xff444444); // color from ui.theme.windowBackgroundColor
        image.stroke(0);
        image.fill(backColor);
        image.ellipseMode(PConstants.RADIUS);
        image.ellipse(WIDTH / 2, HEIGHT - 60 + VOFFSET, 35, 40); //head

        // println("Muse state: " + str(muse.touching_forehead) + " " + str(muse.horseshoe[0]) + " " + str(muse.horseshoe[1]) + " " + str(muse.horseshoe[2]) + " " + str(muse.horseshoe[3]));
        image.stroke(0);
        image.strokeWeight(3);
        if (muse.touching_forehead == 1)
            image.fill(0);
        else
            image.fill(backColor);
        image.ellipse(WIDTH / 2, HEIGHT - 82 + VOFFSET, 5, 4); //on_forehead

        if (muse.touching_forehead == 1) {
            // horseshoe values: 1= good, 2=ok, 3=bad
            // left temporal
            image.stroke(morange);

            if (muse.horseshoe[0] == 1) {
                image.fill(morange);
            } else if (muse.horseshoe[0] == 2) {
                image.fill(morangel);
            } else {
                image.fill(backColor);
            }
            image.ellipse(WIDTH / 2 - 17, HEIGHT - 45 + VOFFSET, 6, 8); // TP9  

            // left frontal
            image.stroke(mgreen);
            if (muse.horseshoe[1] == 1) {
                image.fill(mgreen);
            } else if (muse.horseshoe[1] == 2) {
                image.fill(mgreenl);
            } else {
                image.fill(backColor);
            }
            image.ellipse(WIDTH / 2 - 20, HEIGHT - 70 + VOFFSET, 6, 8); //FP1  

            // right frontal
            image.stroke(mblue);
            if (muse.horseshoe[2] == 1) {
                image.fill(mblue);
            } else if (muse.horseshoe[2] == 2) {
                image.fill(mbluel);
            } else {
                image.fill(backColor);
            }
            image.ellipse(WIDTH / 2 + 20, HEIGHT - 70 + VOFFSET, 6, 8); //FP2

            // right temporal
            image.stroke(mred);
            if (muse.horseshoe[3] == 1) {
                image.fill(mred);
            } else if (muse.horseshoe[3] == 2) {
                image.fill(mredl);
            } else {
                image.fill(backColor);
            }
            image.ellipse(WIDTH / 2 + 17, HEIGHT - 45 + VOFFSET, 6, 8); //TP10
        } else {
            // we probably dont have the headset on, no point in trying to color the rest
            image.stroke(0);
            image.fill(backColor);
            image.ellipse(WIDTH / 2 - 17, HEIGHT - 45 + VOFFSET, 6, 8); // TP9
            image.ellipse(WIDTH / 2 - 20, HEIGHT - 70 + VOFFSET, 6, 8); //FP1
            image.ellipse(WIDTH / 2 + 20, HEIGHT - 70 + VOFFSET, 6, 8); //FP2
            image.ellipse(WIDTH / 2 + 17, HEIGHT - 45 + VOFFSET, 6, 8); //TP10
        }

        int battery = (int) muse.battery_level;
        String battstr = "batt: " + battery + "%";
        int battfill = color(255, 255, 255); //white for default
        if (muse.touching_forehead == 0) {
            battfill = color(0, 0, 0); // disabled battery
        } else if (battery < 10) {
            battfill = color(255, 0, 0); // red battery warning
        } else if (battery < 20) {
            battfill = color(255, 230, 0); // yellow battery warning
        }
        image.stroke(battfill);
        image.fill(battfill);
        image.textSize(16);
        image.text(battstr, 3, HEIGHT - 10 + VOFFSET);

        // plot the graphs
        image.stroke(0);
        image.strokeWeight(1);
        image.line(10, GRAPHBASE, 130, GRAPHBASE);
        image.line(50, GRAPHBASE, 50, GRAPHBASE - GRAPHHEIGHT);
        image.textSize(10);
        image.fill(200);
        // image.text("M C      D T A B G", 13, GRAPHBASE);
        image.text("M C      \u03B4 \u03B8 \u03B1 \u03B2 \u03B3", 13, GRAPHBASE);

        if (muse.signalIsGood()) { // (true)
            image.stroke(0);
            image.fill(7, 145, 178); //blue
            image.rect(15, GRAPHBASE, BARWIDTH, barHeight(GRAPHHEIGHT, muse.getMellow()));
            image.fill(178, 85, 0); //orange
            image.rect(28, GRAPHBASE, BARWIDTH, barHeight(GRAPHHEIGHT, muse.getConcentration()));

            image.fill(43, 131, 186); //blue
            image.rect(60, GRAPHBASE, BARWIDTH, barHeight(GRAPHHEIGHT, muse.getDelta()));
            image.fill(171, 221, 164); //green
            image.rect(72, GRAPHBASE, BARWIDTH, barHeight(GRAPHHEIGHT, muse.getTheta()));
            image.fill(253, 174, 97); //orange
            image.rect(84, GRAPHBASE, BARWIDTH, barHeight(GRAPHHEIGHT, muse.getAlpha()));
            image.fill(215, 25, 28); //red
            image.rect(96, GRAPHBASE, BARWIDTH, barHeight(GRAPHHEIGHT, muse.getBeta()));
            image.fill(255, 255, 191); //offwhite
            image.rect(108, GRAPHBASE, BARWIDTH, barHeight(GRAPHHEIGHT, muse.getGamma()));

        }

        image.endDraw();
    }

    // use this if drawing in MuseHUD's buffer
    public void drawHUD() {
        //this.pgMuseHUD = updateHUD(this.pgMuseHUD);
        updateHUD(this.pgMuseHUD);
    }

    // use this version if drawing in someone else's buffer
    public void drawHUD(PGraphics buffer) {
        //buffer = updateHUD(buffer);
        updateHUD(buffer);
    }
}
