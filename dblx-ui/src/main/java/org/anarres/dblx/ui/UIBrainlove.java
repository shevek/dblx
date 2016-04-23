/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.dblx.ui;

import heronarts.lx.parameter.BasicParameter;
import heronarts.lx.parameter.LXParameter;
import heronarts.lx.parameter.LXParameterListener;
import heronarts.p3lx.ui.UIWindow;
import heronarts.p3lx.ui.component.UISlider;
import org.anarres.dblx.core.Core;

/**
 *
 * @author shevek
 */
public class UIBrainlove extends UIWindow {

    final BasicParameter g_brightness;

    public UIBrainlove(final Core core, float x, float y, float w, float h) {
        super(core.lx.ui, "BRIGHTNESS", x, y, w, h);
        g_brightness = new BasicParameter("BRIGHTNESS", 1.0);
        g_brightness.addListener(new LXParameterListener() {
            public void onParameterChanged(LXParameter parameter) {
                core.global_brightness = (float) parameter.getValuef();
            }
        });
        y = UIWindow.TITLE_LABEL_HEIGHT;
        new UISlider(4, UIWindow.TITLE_LABEL_HEIGHT, w - 10, 20)
                .setParameter(g_brightness)
                .addToContainer(this);

    //y+=25 ;
    /*new UIButton(4, y, width-8, 20) {
         protected void onToggle(boolean enabled) {
         osc_send=enabled;
         if(!enabled) { global_sender=null; }
         }}
         .setLabel("Send Pixels")
         .addToContainer(this);*/
    }
    /*protected void onDraw(UI ui, PGraphics pg) {
     super.onDraw(ui, pg);
     pg.fill(#FFFFFF);
     pg.rect(0,0,width,height);
     redraw();
     }*/

}
