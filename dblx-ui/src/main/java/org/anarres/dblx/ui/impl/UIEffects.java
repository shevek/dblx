/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.dblx.ui.impl;

import heronarts.lx.effect.LXEffect;
import heronarts.lx.parameter.BasicParameter;
import heronarts.lx.parameter.LXParameter;
import heronarts.lx.parameter.LXParameterListener;
import heronarts.p3lx.ui.UIWindow;
import heronarts.p3lx.ui.component.UIItemList;
import heronarts.p3lx.ui.component.UIKnob;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import org.anarres.dblx.core.Core;

/**
 *
 * @author shevek
 */
public class UIEffects extends UIWindow {

    private final Core core;

    public UIEffects(@Nonnull Core core, float x, float y, float w, float h) {
        super(core.lx.ui, "FX", x, y, w, h);
        this.core = core;

        int yp = UIWindow.TITLE_LABEL_HEIGHT;
        List<UIItemList.Item> items = new ArrayList<>();
        int i = 0;
        for (LXEffect fx : core.effectsArr) {
            items.add(new FXScrollItem(core, fx, i++));
        }
        final UIItemList effectsList = new UIItemList(1, yp, w - 2, 60).setItems(items);
        effectsList.addToContainer(this);
        yp += effectsList.getHeight() + 10;

        final UIKnob[] parameterKnobs = new UIKnob[4];
        for (int ki = 0; ki < parameterKnobs.length; ++ki) {
            parameterKnobs[ki] = new UIKnob(5 + 34 * (ki % 4), yp + (ki / 4) * 48);
            parameterKnobs[ki].addToContainer(this);
        }

        LXParameterListener fxListener = new LXParameterListener() {
            @Override
            public void onParameterChanged(LXParameter parameter) {
                int i = 0;
                for (LXParameter p : UIEffects.this.core.getSelectedEffect().getParameters()) {
                    if (i >= parameterKnobs.length) {
                        break;
                    }
                    if (p instanceof BasicParameter) {
                        parameterKnobs[i++].setParameter((BasicParameter) p);
                    }
                }
                while (i < parameterKnobs.length) {
                    parameterKnobs[i++].setParameter(null);
                }
            }
        };

        core.selectedEffect.addListener(fxListener);
        fxListener.onParameterChanged(null);

    }

    private static class FXScrollItem extends UIItemList.AbstractItem {

        private final Core core;
        private final LXEffect effect;
        private final int index;
        private final String label;

        FXScrollItem(@Nonnull Core core, LXEffect effect, int index) {
            this.core = core;
            this.effect = effect;
            this.index = index;
            this.label = UIUtils.className(effect, "Effect");
        }

        @Override
        public String getLabel() {
            return label;
        }

        @Override
        public boolean isSelected() {
            return !effect.isEnabled() && (effect == core.getSelectedEffect());
        }

        @Override
        public boolean isPending() {
            return effect.isEnabled();
        }

        @Override
        public void onMousePressed() {
            if (effect == core.getSelectedEffect()) {
                if (effect.isMomentary()) {
                    effect.enable();
                } else {
                    effect.toggle();
                }
            } else {
                core.selectedEffect.setValue(index);
            }
        }

        @Override
        public void onMouseReleased() {
            if (effect.isMomentary()) {
                effect.disable();
            }
        }

    }

}
