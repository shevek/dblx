/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.dblx.output.beaglebone;

import heronarts.lx.parameter.LXParameter;
import heronarts.lx.parameter.LXParameterListener;
import heronarts.p3lx.ui.UI;
import heronarts.p3lx.ui.UIWindow;
import heronarts.p3lx.ui.component.UIItemList;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

/**
 * Was: UIOutput - not sure if that's a merged class.
 *
 * @author shevek
 */
public class UIBeagleBoneOutput extends UIWindow {

    private final BeagleBoneManager manager;

    UIBeagleBoneOutput(@Nonnull BeagleBoneManager manager, UI ui, float x, float y, float w, float h) {
        super(ui, "OUTPUT", x, y, w, h);
        this.manager = manager;

        float yPos = UIWindow.TITLE_LABEL_HEIGHT - 2;
        List<UIItemList.Item> items = new ArrayList<UIItemList.Item>();
        items.add(new OutputItem());

        new UIItemList(1, yPos, width - 2, 260)
                .setItems(items)
                .addToContainer(this);
    }

    private class OutputItem extends UIItemList.AbstractItem {

        OutputItem() {
            for (BeagleBone ch : UIBeagleBoneOutput.this.manager.cortexList) {
                ch.enabled.addListener(new LXParameterListener() {
                    public void onParameterChanged(LXParameter parameter) {
                        redraw();
                    }
                });
            }
        }

        public String getLabel() {
            return "ALL CHANNELS";
        }

        public boolean isSelected() {
            // jut check the first one, since they either should all be on or all be off
            return manager.cortexList.get(0).enabled.isOn();
        }

        public void onMousePressed() {
            for (BeagleBone ch : manager.cortexList) {
                ch.enabled.toggle();
                if (ch.enabled.isOn()) {
                    ch.connect();
                } else {
//          ch.closeChannel();
                    ch.dispose();
                }
            }
        } // end onMousePressed
    }

}
