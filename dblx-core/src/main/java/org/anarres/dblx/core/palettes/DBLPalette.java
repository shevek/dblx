/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.dblx.core.palettes;

import java.awt.Color;

/**
 *
 * @author shevek
 */
public class DBLPalette {

    public static Color[] toColors(int[] ints) {
        Color[] colors = new Color[ints.length];
        for (int i = 0; i < ints.length; i++) {
            Color c = new Color(ints[i]);
            colors[i] = c;
        }
        return colors;
    }

    public static int[] toColorInts(Color[] colors) {
        int[] ints = new int[colors.length];
        for (int i = 0; i < colors.length; i++) {
            int c = colors[i].getRGB();
            ints[i] = c;
        }
        return ints;
    }

    public static Color[] interpolate(Color[] gradient, int colorCount) {
        Color[] colors = new Color[colorCount];
        float scale = (gradient.length - 1) / (colorCount - 1);

        for (int i = 0; i < colorCount; i++) {
            float value = scale * i;
            int index = (int) Math.floor(value);

            Color c1 = gradient[index];
            float remainder = 0.0f;
            Color c2 = null;
            if (index + 1 < gradient.length) {
                c2 = gradient[index + 1];
                remainder = value - index;
            } else {
                c2 = gradient[index];
            }
            //		 System.out.println("value: " + value + " index: " + index + " remainder: " + remainder);
            int red = Math.round((1 - remainder) * c1.getRed() + (remainder) * c2.getRed());
            int green = Math.round((1 - remainder) * c1.getGreen() + (remainder) * c2.getGreen());
            int blue = Math.round((1 - remainder) * c1.getBlue() + (remainder) * c2.getBlue());

            colors[i] = new Color(red, green, blue);
        }
        return colors;
    }

    public static int[] interpolate(int[] gradient, int colorCount) {
        Color[] gradientColor = toColors(gradient);
        int[] ints = toColorInts(interpolate(gradientColor, colorCount));
        return ints;
    }

}
