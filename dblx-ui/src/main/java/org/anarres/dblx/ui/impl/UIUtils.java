/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.dblx.ui.impl;

/**
 *
 * @author shevek
 */
public class UIUtils {

    public static String className(Object p, String suffix) {
        String s = p.getClass().getName();
        int li;
        if ((li = s.lastIndexOf('.')) > 0) {
            s = s.substring(li + 1);
        }
        if (s.indexOf("SugarCubes$") == 0) {
            s = s.substring("SugarCubes$".length());
        }
        if ((suffix != null) && ((li = s.indexOf(suffix)) != -1)) {
            s = s.substring(0, li);
        }
        return s;
    }

    private UIUtils() {
    }

}
