/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.dblx.core.palettes;

import java.awt.Color;
import org.jcolorbrewer.ColorBrewer;

/**
 *
 * @author shevek
 */
public class GradientCB {

    public static final int Sequential = 3;
    public static final int Diverging = 2;
    public static final int Qualitative = 1;

    public static Color[] getGradient(int paletteType, int paletteIndex, int colors) {
        boolean colorBlindSave = false;
        ColorBrewer[] palettes;
        if (paletteType == Sequential) {
            palettes = ColorBrewer.getSequentialColorPalettes(colorBlindSave);
        } else if (paletteType == Diverging) {
            palettes = ColorBrewer.getDivergingColorPalettes(colorBlindSave);
        } else {
            palettes = ColorBrewer.getQualitativeColorPalettes(colorBlindSave);
        }
        Color[] gradient = palettes[paletteIndex].getColorPalette(colors);
        return gradient;
    }
}
