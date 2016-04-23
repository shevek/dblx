/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.dblx.core.preset;

import heronarts.lx.LXChannel;
import heronarts.lx.parameter.LXParameter;
import heronarts.lx.pattern.LXPattern;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;

/**
 *
 * @author shevek
 */
public class Preset {

    private final PresetManager manager;
    private final int index;

    String className;
    private final Map<String, Float> parameters = new HashMap<>();

    Preset(PresetManager manager, int index) {
        this.manager = manager;
        this.index = index;
    }

    public void load(String serialized) {
        className = null;
        parameters.clear();
        try {
            String[] parts = serialized.split(PresetManager.DELIMITER);
            className = parts[0];
            int i = 1;
            while (i < parts.length - 1) {
                parameters.put(parts[i], Float.parseFloat(parts[i + 1]));
                i += 2;
            }
        } catch (NumberFormatException x) {
            className = null;
            parameters.clear();
        }
    }

    public String serialize() {
        if (className == null) {
            return "null";
        }
        String val = className + PresetManager.DELIMITER;
        for (String pKey : parameters.keySet()) {
            val += pKey + PresetManager.DELIMITER + parameters.get(pKey) + PresetManager.DELIMITER;
        }
        return val;
    }

    public void store(@Nonnull LXChannel channel) throws IOException {
        LXPattern pattern = channel.getActivePattern();
        className = pattern.getClass().getName();
        parameters.clear();
        for (LXParameter p : pattern.getParameters()) {
            parameters.put(p.getLabel(), p.getValuef());
        }
//    if (pattern instanceof DPat) {
//      DPat dpattern = (DPat) pattern;
//      for (DBool bool : dpattern.bools) {
//        parameters.put(bool.tag, bool.b ? 1.f : 0.f);
//      }
//      for (Pick pick : dpattern.picks) {
//        parameters.put(pick.tag, pick.CurRow + pick.CurCol/100.f);
//      }
//    }
        manager.write();
        manager.onStore(channel, this, pattern);
    }

    public void select(LXChannel channel) {
        for (LXPattern pattern : channel.getPatterns()) {
            if (pattern.getClass().getName().equals(className)) {
                for (String pLabel : parameters.keySet()) {
                    for (LXParameter p : pattern.getParameters()) {
                        if (p.getLabel().equals(pLabel)) {
                            p.setValue(parameters.get(pLabel));
                        }
                    }
//          if (pattern instanceof DPat) {
//            DPat dpattern = (DPat) pattern;
//            for (DBool bool : dpattern.bools) {
//              if (bool.tag.equals(pLabel)) {
//                bool.set(bool.row, bool.col, parameters.get(pLabel) > 0);
//              }
//            }
//            for (Pick pick : dpattern.picks) {
//              if (pick.tag.equals(pLabel)) {
//                float f = parameters.get(pLabel);
//                pick.set((int) floor(f), (int) round((f%1)*100.));
//              }
//            }
//          }
                }
                channel.goPattern(pattern);
//        if (pattern instanceof DPat) {
//          ((DPat)pattern).updateLights();
//        }
                manager.onSelect(channel, this, pattern);
                break;
            }
        }
    }
}
