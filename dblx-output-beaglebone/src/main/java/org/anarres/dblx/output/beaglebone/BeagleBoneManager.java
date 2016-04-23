/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.dblx.output.beaglebone;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import org.anarres.dblx.core.Core;

/**
 *
 * @author shevek
 */
public class BeagleBoneManager {

    public final Core core;
    int nPixPerChannel = 512; // OPC server is set to 512 pix per channel
    int nChannelPerBoard = 24;

    public BeagleBoneManager(Core core) {
        this.core = core;
    }

    public double getGlobalBrightness() {
        return core.global_brightness;
    }

    int[] concatenateChannels(int boardNum) {
        // expects boardNum to be indexed starting at *1*
        //println("concatenating board " + boardNum);
        int[] pixIndex = new int[nPixPerChannel * nChannelPerBoard];
        int boardOffset = (boardNum - 1) * nChannelPerBoard;
        for (int i = boardOffset; i < boardOffset + nChannelPerBoard; i++) {
            // TODO
            /*
            int[] channelIx = core.model.channelMap.get(i);
            //println("adding channel " + i + ", "+ channelIx.length + " pix");
            for (int j = 0; j < channelIx.length; j++) {
                //println( i * nPixPerChannel - boardOffset*nPixPerChannel + j);
                pixIndex[i * nPixPerChannel - boardOffset * nPixPerChannel + j] = channelIx[j];
            }
            */
        }
        return pixIndex;
    }

    public List<BeagleBone> cortexList = new ArrayList<BeagleBone>();

    private void addBeagleBone(@Nonnull BeagleBone output) {
        this.core.lx.addOutput(output);
        this.cortexList.add(output);
    }

    public void addBeagleBones() {
        addBeagleBone(new BeagleBone(this, "192.168.1.80", 1, concatenateChannels(1)));
        addBeagleBone(new BeagleBone(this, "192.168.1.81", 2, concatenateChannels(2)));
    }

}
