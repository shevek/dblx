package org.anarres.dblx.iface.muse.net;

/**
 *
 * MuseConnect
 *
 * author: Michael J. Pesavento, Ph.D.
 * PezTek
 * mike@peztek.com
 *
 * copywrite (c) 2015 PezTek
 *
 * This software is created and distributed under the
 * GNU General Public License v3
 * http://www.gnu.org/licenses/gpl.html
 *
 * --------------------------------------
 *
 * v0.1 2015.03.21
 * v0.2 2015.08.20 updated for brainlove and DBLX
 * v0.3 2015.11.15 updated MuseHUD to display metrics
 *
 * --------------------------------------
 * Requirements:
 * requires oscP5 library package for Processing 2
 *
 * need to have muse-io installed, get SDK from here:
 * https://sites.google.com/a/interaxon.ca/muse-developer-site/download
 *
 * load muse OSC output in command line with:
 * muse-io --preset 14 --osc osc.udp://localhost:5000
 *
 */
import java.util.Arrays;
import oscP5.*;
import netP5.*;
import static processing.core.PApplet.println;

public class MuseConnect {

    Object parent; //need to have root Processing object - this - passed to constructor

    OscP5 oscP5;
    NetAddress remoteOSCLocation;

    private String host = "127.0.0.1";
    private int port;

    public float[] delta_absolute = new float[]{0, 0, 0, 0};
    public float[] theta_absolute = new float[]{0, 0, 0, 0};
    public float[] alpha_absolute = new float[]{0, 0, 0, 0};
    public float[] beta_absolute = new float[]{0, 0, 0, 0};
    public float[] gamma_absolute = new float[]{0, 0, 0, 0};

    public float[] delta_rel = new float[]{0, 0, 0, 0};
    public float[] theta_rel = new float[]{0, 0, 0, 0};
    public float[] alpha_rel = new float[]{0, 0, 0, 0};
    public float[] beta_rel = new float[]{0, 0, 0, 0};
    public float[] gamma_rel = new float[]{0, 0, 0, 0};

    public float[] delta_session = new float[]{0, 0, 0, 0};
    public float[] theta_session = new float[]{0, 0, 0, 0};
    public float[] alpha_session = new float[]{0, 0, 0, 0};
    public float[] beta_session = new float[]{0, 0, 0, 0};
    public float[] gamma_session = new float[]{0, 0, 0, 0};

    public int touching_forehead = 0; //boolean, 1 is on forehead correctly
    public float[] horseshoe = new float[]{3, 3, 3, 3}; // values: 1= good, 2=ok, 3=bad
    public float battery_level = 0; // percent battery remaining.
    public float concentration = 0;
    public float mellow = 0;

    public boolean museActive = false; // true if touching_forehead and all horseshoes are 'good'

    public MuseConnect(Object parent) {
        this.parent = parent;
        this.port = 5000;
        oscP5 = new OscP5(parent, port); // read from the muse port
        remoteOSCLocation = new NetAddress(host, port);
        println("Connected to Muse headset");
    }

    public MuseConnect(Object parent, int _port) {
        this.port = _port;
        oscP5 = new OscP5(parent, port); // read from the muse port
        remoteOSCLocation = new NetAddress(host, this.port);
        println("Opened OSC port to Muse headset on " + this.host + ":" + this.port);
    }

    public MuseConnect(Object parent, String _host, int _port) {
        this.host = _host;
        this.port = _port;
        oscP5 = new OscP5(parent, port); // read from the muse port
        remoteOSCLocation = new NetAddress(host, this.port);
        println("Opened OSC port to Muse headset on " + this.host + ":" + this.port);
    }

    public void loadFromOsc(float[] arr, OscMessage msg, int n) {
        // n is the expected number of elements in the message
        for (int i = 0; i < n; i++) {
            arr[i] = msg.get(i).floatValue();
        }
    }

    public void loadFromOsc(int[] arr, OscMessage msg, int n) {
        // n is the expected number of elements in the message
        for (int i = 0; i < n; i++) {
            arr[i] = msg.get(i).intValue();
        }
    }

    //return true if all of the sensors are good (1.0)
    public boolean signalIsGood() {
        boolean is_good = true;
        if (this.touching_forehead != 1) {
            this.museActive = false;
            return false;
        }
        for (int i = 0; i < this.horseshoe.length; i++) {
            if (this.horseshoe[i] != 1.0) {
                is_good = false;
                break;
            }
        }
        this.museActive = is_good;
        return is_good;
    }

    private float average(float arr[]) {
        float out = 0;
        for (int i = 0; i < arr.length; i++)
            out += arr[i];
        return out / arr.length;
    }

    /**
     * only take the average of the middle two sensors, which should be FP1 and FP2
     */
    private float averageFront(float arr[]) {
        if (arr.length != 4)
            throw new RuntimeException("Bandwidth arrays dont have length 4, incorrect input");
        return (arr[1] + arr[2]) / 2;
    }

    /**
     * only take the average of the outer two sensors, which should be TP9 and TP10
     */
    private float averageTemporal(float arr[]) {
        if (arr.length != 4)
            throw new RuntimeException("Bandwidth arrays dont have length 4, incorrect input");
        return (arr[0] + arr[3]) / 2;
    }

    // select a single method to flatten the 4 sensor array for output in the getX() methods
    private float flattenSensor(float[] arr) {
        return arr[1]; // just use front left (FP1) for now
        // return averageFront(arr);
        // return average(arr);
    }

    public float getMellow() {
        return this.mellow;
    }

    public float getConcentration() {
        return this.concentration;
    }

    public float getDelta() {
        return flattenSensor(this.delta_session);
    }

    public float getTheta() {
        return flattenSensor(this.theta_session);
    }

    public float getAlpha() {
        return flattenSensor(this.theta_session);
    }

    public float getBeta() {
        return flattenSensor(this.alpha_session);
    }

    public float getGamma() {
        return flattenSensor(this.gamma_session);
    }

} //end MuseConnect

class MuseListener {

    // turn this on for debugging
    private static boolean verboseMuse = false;
    private final MuseConnect muse;

    public MuseListener(MuseConnect muse) {
        this.muse = muse;
    }

//*********************************************************************************************
// global function to catch all OSC messages
// NOTE: this will conflict with any other use of oscP5 and definitions of this method!
// future versions will be in pure java to avoid this problem
    public void oscEvent(OscMessage msg) {

        if (msg.checkAddrPattern("/muse/elements/horseshoe") == true) {
            muse.loadFromOsc(muse.horseshoe, msg, 4);
            if (verboseMuse)
                println("reading signal quality: " + Arrays.toString(muse.horseshoe));
        } else if (msg.checkAddrPattern("/muse/batt") == true) {
            muse.battery_level = msg.get(0).intValue() / 100;
            if (verboseMuse)
                println("******* received battery level: " + muse.battery_level);
        } else if (msg.checkAddrPattern("/muse/elements/touching_forehead") == true) {
            muse.touching_forehead = msg.get(0).intValue();
        } //*************************
        // catch and report absolute bandwidth values
        else if (msg.checkAddrPattern("/muse/elements/delta_absolute") == true) {
            muse.loadFromOsc(muse.delta_absolute, msg, 4);
            if (verboseMuse)
                println("received /muse/elements/delta_absolute: " + Arrays.toString(muse.delta_absolute));
        } else if (msg.checkAddrPattern("/muse/elements/theta_absolute") == true) {
            muse.loadFromOsc(muse.theta_absolute, msg, 4);
            if (verboseMuse)
                println("received /muse/elements/theta_absolute: " + Arrays.toString(muse.theta_absolute));
        } else if (msg.checkAddrPattern("/muse/elements/alpha_absolute") == true) {
            muse.loadFromOsc(muse.alpha_absolute, msg, 4);
            if (verboseMuse)
                println("received /muse/elements/alpha_absolute: " + Arrays.toString(muse.alpha_absolute));
        } else if (msg.checkAddrPattern("/muse/elements/beta_absolute") == true) {
            muse.loadFromOsc(muse.beta_absolute, msg, 4);
            if (verboseMuse)
                println("received /muse/elements/beta_absolute: " + Arrays.toString(muse.beta_absolute));
        } else if (msg.checkAddrPattern("/muse/elements/gamma_absolute") == true) {
            muse.loadFromOsc(muse.gamma_absolute, msg, 4);
            if (verboseMuse)
                println("received /muse/elements/gamma_absolute: " + Arrays.toString(muse.gamma_absolute));
        } //*************************
        // catch and report session scores
        else if (msg.checkAddrPattern("/muse/elements/delta_session_score") == true) {
            muse.loadFromOsc(muse.delta_session, msg, 4);
            if (verboseMuse)
                println("received /muse/elements/delta_session_score: " + Arrays.toString(muse.delta_session));
        } else if (msg.checkAddrPattern("/muse/elements/theta_session_score") == true) {
            muse.loadFromOsc(muse.theta_session, msg, 4);
            if (verboseMuse)
                println("received /muse/elements/theta_session_score: " + Arrays.toString(muse.theta_session));
        } else if (msg.checkAddrPattern("/muse/elements/alpha_session_score") == true) {
            muse.loadFromOsc(muse.alpha_session, msg, 4);
            if (verboseMuse)
                println("received /muse/elements/alpha_session_score: " + Arrays.toString(muse.alpha_session));
        } else if (msg.checkAddrPattern("/muse/elements/beta_session_score") == true) {
            muse.loadFromOsc(muse.beta_session, msg, 4);
            if (verboseMuse)
                println("received /muse/elements/beta_session_score: " + Arrays.toString(muse.beta_session));
        } else if (msg.checkAddrPattern("/muse/elements/gamma_session_score") == true) {
            muse.loadFromOsc(muse.gamma_session, msg, 4);
            if (verboseMuse)
                println("received /muse/elements/gamma_session_score: " + Arrays.toString(muse.gamma_session));
        } //*************************
        // catch and report relative bandwidth values
        else if (msg.checkAddrPattern("/muse/elements/delta_relative") == true) {
            muse.loadFromOsc(muse.delta_rel, msg, 4);
            if (verboseMuse)
                println("received /muse/elements/delta_relative: " + Arrays.toString(muse.delta_rel));
        } else if (msg.checkAddrPattern("/muse/elements/theta_relative") == true) {
            muse.loadFromOsc(muse.theta_rel, msg, 4);
            if (verboseMuse)
                println("received /muse/elements/theta_relative: " + Arrays.toString(muse.theta_rel));
        } else if (msg.checkAddrPattern("/muse/elements/alpha_relative") == true) {
            muse.loadFromOsc(muse.alpha_rel, msg, 4);
            if (verboseMuse)
                println("received /muse/elements/alpha_relative: " + Arrays.toString(muse.alpha_rel));
        } else if (msg.checkAddrPattern("/muse/elements/beta_relative") == true) {
            muse.loadFromOsc(muse.beta_rel, msg, 4);
            if (verboseMuse)
                println("received /muse/elements/beta_relative: " + Arrays.toString(muse.beta_rel));
        } else if (msg.checkAddrPattern("/muse/elements/gamma_relative") == true) {
            muse.loadFromOsc(muse.gamma_rel, msg, 4);
            if (verboseMuse)
                println("received /muse/elements/gamma_relative: " + Arrays.toString(muse.gamma_rel));
        } // concentration and mellow metrics
        else if (msg.checkAddrPattern("/muse/elements/experimental/concentration") == true) {
            muse.concentration = msg.get(0).floatValue();
            if (verboseMuse)
                println("received /muse/elements/experimental/concentration: " + muse.concentration);
        } else if (msg.checkAddrPattern("/muse/elements/experimental/mellow") == true) {
            muse.mellow = msg.get(0).floatValue();
            if (verboseMuse)
                println("received /muse/elements/experimental/mellow: " + muse.mellow);

        }

    } // end oscEvent()

}
