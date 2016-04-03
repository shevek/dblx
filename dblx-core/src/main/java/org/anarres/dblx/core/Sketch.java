/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.dblx.core;

import processing.core.PApplet;

/**
 *
 * @author shevek
 */
public class Sketch extends PApplet {

    @Override
    public void settings() {
        super.settings();
        size(displayWidth, displayHeight);
    }

    @Override
    public void draw() {
        background(0);
    }

}
