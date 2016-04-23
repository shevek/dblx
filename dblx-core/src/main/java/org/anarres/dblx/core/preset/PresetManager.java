/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.dblx.core.preset;

import com.google.common.collect.ImmutableList;
import com.google.common.io.CharSource;
import com.google.common.io.Files;
import heronarts.lx.LX;
import heronarts.lx.LXChannel;
import heronarts.lx.parameter.LXListenableParameter;
import heronarts.lx.parameter.LXParameter;
import heronarts.lx.parameter.LXParameterListener;
import heronarts.lx.pattern.LXPattern;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

public class PresetManager {

    public static final int NUM_PRESETS = 8;
    public static final String FILENAME = "data/presets.txt";
    public static final String DELIMITER = "\t";

    class ChannelState implements LXParameterListener {

        final LXChannel channel;
        LXPattern selectedPattern = null;
        Preset selectedPreset = null;
        boolean isDirty = false;

        ChannelState(LXChannel channel) {
            this.channel = channel;
            channel.addListener(new LXChannel.AbstractListener() {
                public void patternDidChange(LXChannel channel, LXPattern pattern) {
                    if (selectedPattern != pattern) {
                        onDirty();
                    }
                }
            });
        }

        private void onSelect(Preset preset, LXPattern pattern) {
            if ((selectedPattern != pattern) && (selectedPattern != null)) {
                for (LXParameter p : selectedPattern.getParameters()) {
                    ((LXListenableParameter) p).removeListener(this);
                }
            }
            selectedPreset = preset;
            selectedPattern = pattern;
            isDirty = false;
            for (LXParameter p : pattern.getParameters()) {
                ((LXListenableParameter) p).addListener(this);
            }
            for (PresetListener listener : listeners) {
                listener.onPresetSelected(channel, preset);
            }
        }

        private void onStore(Preset preset, LXPattern pattern) {
            selectedPreset = preset;
            selectedPattern = pattern;
            isDirty = false;
            for (PresetListener listener : listeners) {
                listener.onPresetStored(channel, preset);
            }
        }

        private void onDirty() {
            if (selectedPreset != null) {
                isDirty = true;
                for (PresetListener listener : listeners) {
                    listener.onPresetDirty(channel, selectedPreset);
                }
            }
        }

        public void onParameterChanged(LXParameter parameter) {
            onDirty();
        }
    }

    private final ChannelState[] channelState;
    private final Preset[] presets = new Preset[NUM_PRESETS];
    private final List<PresetListener> listeners = new ArrayList<>();

    PresetManager(@Nonnull LX lx) throws IOException {
        this.channelState = new ChannelState[lx.engine.getChannels().size()];
        for (int i = 0; i < presets.length; ++i) {
            presets[i] = new Preset(this, i);
        }

        File file = new File(FILENAME);
        if (file.exists()) {
            CharSource source = Files.asCharSource(file, StandardCharsets.UTF_8);
            ImmutableList<String> values = source.readLines();
            int i = 0;
            for (String serialized : values) {
                presets[i++].load(serialized);
                if (i >= NUM_PRESETS) {
                    break;
                }
            }
        }
        for (LXChannel channel : lx.engine.getChannels()) {
            channelState[channel.getIndex()] = new ChannelState(channel);
        }
    }

    public void addListener(PresetListener listener) {
        listeners.add(listener);
    }

    public void select(LXChannel channel, int index) {
        presets[index].select(channel);
    }

    public void store(LXChannel channel, int index) throws IOException {
        presets[index].store(channel);
    }

    public void dirty(LXChannel channel) {
        channelState[channel.getIndex()].onDirty();
    }

    public void dirty(LXPattern pattern) {
        dirty(pattern.getChannel());
    }

    public void onStore(LXChannel channel, Preset preset, LXPattern pattern) {
        channelState[channel.getIndex()].onStore(preset, pattern);
    }

    public void onSelect(LXChannel channel, Preset preset, LXPattern pattern) {
        channelState[channel.getIndex()].onSelect(preset, pattern);
    }

    public void write() throws IOException {
        List<String> lines = new ArrayList<>();
        for (Preset preset : presets)
            lines.add(preset.serialize());
        File file = new File(FILENAME);
        Files.asCharSink(file, StandardCharsets.UTF_8).writeLines(lines);
    }
}
