/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.dblx.ui;

import heronarts.p3lx.ui.UIWindow;
import heronarts.p3lx.ui.component.UIButton;
import heronarts.p3lx.ui.component.UIKnob;
import heronarts.p3lx.ui.control.UIChannelControl;
import javax.annotation.Nonnull;
import org.anarres.dblx.core.Core;

/**
 *
 * @author shevek
 */
public class UIEngineControl extends UIWindow {

    final UIKnob fpsKnob;

    UIEngineControl(@Nonnull final Core core, float x, float y) {
        super(core.lx.ui, "ENGINE", x, y, UIChannelControl.WIDTH, 96);

        y = UIWindow.TITLE_LABEL_HEIGHT;
        new UIButton(4, y, width - 8, 20) {
            @Override
            protected void onToggle(boolean enabled) {
                core.lx.engine.setThreaded(enabled);
                fpsKnob.setEnabled(enabled);
            }
        }
                .setActiveLabel("Multi-Threaded")
                .setInactiveLabel("Single-Threaded")
                .addToContainer(this);

        y += 24;
        fpsKnob = new UIKnob(4, y);
        fpsKnob
                .setParameter(core.lx.engine.framesPerSecond)
                .setEnabled(core.lx.engine.isThreaded())
                .addToContainer(this);
    }

}
