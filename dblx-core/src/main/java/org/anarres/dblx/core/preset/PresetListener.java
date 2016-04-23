/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.dblx.core.preset;

import heronarts.lx.LXChannel;
import java.util.EventListener;

/**
 *
 * @author shevek
 */
public interface PresetListener extends EventListener {

    public void onPresetSelected(LXChannel channel, Preset preset);

    public void onPresetStored(LXChannel channel, Preset preset);

    public void onPresetDirty(LXChannel channel, Preset preset);

}
