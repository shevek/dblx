/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.dblx.ui.impl;

import heronarts.lx.LXChannel;
import heronarts.lx.transition.LXTransition;
import heronarts.p3lx.P3LX;
import heronarts.p3lx.ui.UIWindow;
import heronarts.p3lx.ui.component.UIItemList;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import org.anarres.dblx.core.Core;
import static org.anarres.dblx.core.Core.*;

/**
 *
 * @author shevek
 */
public class UIBlendMode extends UIWindow {

    public UIBlendMode(Core core, float x, float y, float w, float h) {
        super(core.lx.ui, "BLEND MODE", x, y, w, h);
        List<UIItemList.Item> items = new ArrayList<UIItemList.Item>();
        for (LXTransition t : core.transitions) {
            items.add(new TransitionItem(core.lx, t));
        }
        final UIItemList tList;
        (tList = new UIItemList(1, UIWindow.TITLE_LABEL_HEIGHT, w - 2, 60))
                .setItems(items)
                .addToContainer(this);

        core.lx.engine.getChannel(RIGHT_CHANNEL)
                .addListener(new LXChannel.AbstractListener() {
                    public void faderTransitionDidChange(LXChannel channel,
                            LXTransition transition) {
                        tList.redraw();
                    }
                });

    }

    private static class TransitionItem extends UIItemList.AbstractItem {

        private final P3LX lx;
        private final LXTransition transition;
        private final String label;

        TransitionItem(@Nonnull P3LX lx, LXTransition transition) {
            this.lx = lx;
            this.transition = transition;
            this.label = UIUtils.className(transition, "Transition");
        }

        public String getLabel() {
            return label;
        }

        public boolean isSelected() {
            return this.transition == lx.engine
                    .getChannel(RIGHT_CHANNEL)
                    .getFaderTransition();
        }

        public boolean isPending() {
            return false;
        }

        public void onMousePressed() {
            lx.engine
                    .getChannel(RIGHT_CHANNEL)
                    .setFaderTransition(this.transition);
        }
    }
}
