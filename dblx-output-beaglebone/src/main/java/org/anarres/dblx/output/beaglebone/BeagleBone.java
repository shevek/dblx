package org.anarres.dblx.output.beaglebone;

/*
 * Motor cortex:
 * Create output to the LEDs via OSC packets to BeagleBone Black
 * Each BBB pushes up to 24 channels of <=512 pixels per channel
 *
 * @author mjp 2015.08.08
 */
import heronarts.lx.LX;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;

import heronarts.lx.color.LXColor;
import heronarts.lx.output.LXOutput;
import static processing.core.PApplet.println;

public class BeagleBone extends LXOutput {

    // constants for creating OPC header
    private static final int HEADER_LEN = 4;
    private static final int BYTES_PER_PIXEL = 3;
    private static final int INDEX_CHANNEL = 0;
    private static final int INDEX_COMMAND = 1;
    private static final int INDEX_DATA_LEN_MSB = 2;
    private static final int INDEX_DATA_LEN_LSB = 3;
    private static final int INDEX_DATA = 4;
    private static final int OFFSET_R = 0;
    private static final int OFFSET_G = 1;
    private static final int OFFSET_B = 2;

    private static final int COMMAND_SET_PIXEL_COLORS = 0;

    private static final int PORT = 7890; //the standard OPC port

    private final BeagleBoneManager manager;
    Socket socket;
    OutputStream output;
    String host;
    int port = 7890;

    public int boardNum;
    public int channelNum;
    public byte[] packetData;

    private final int[] pointIndices;
    /* Local array for color-conversions. */
    private final float[] hsb = new float[3];

    BeagleBone(BeagleBoneManager manager, String _host, int _boardNum, int[] _pointIndices) {
        super(manager.core.lx);
        this.manager = manager;
        this.host = _host;
        this.boardNum = _boardNum;
        this.pointIndices = _pointIndices;
        this.socket = null;
        this.output = null;
        enabled.setValue(true);

        int dataLength = BYTES_PER_PIXEL * manager.nPixPerChannel * manager.nChannelPerBoard;
        this.packetData = new byte[HEADER_LEN + dataLength];
        this.packetData[INDEX_CHANNEL] = 0;
        this.packetData[INDEX_COMMAND] = COMMAND_SET_PIXEL_COLORS;
        this.packetData[INDEX_DATA_LEN_MSB] = (byte) (dataLength >>> 8);
        this.packetData[INDEX_DATA_LEN_LSB] = (byte) (dataLength & 0xFF);

        this.connect();

    }

    public boolean isConnected() {
        return (this.output != null);
    }

    protected void connect() {
        // if (this.socket == null) {
        if (this.output == null) {
            try {
                this.socket = new Socket();
                this.socket.connect(new InetSocketAddress(this.host, this.port), 100);
                // this.socket.setTcpNoDelay(true); // disable on SugarCubes
                this.output = this.socket.getOutputStream();
                didConnect();
            } catch (ConnectException cx) {
                dispose(cx);
            } catch (IOException iox) {
                dispose(iox);
            }
        }
    }

    protected void didConnect() {
//    println("Connected to OPC server: " + host + " for channel " + channelNum);
    }

    protected void closeChannel() {
        try {
            this.output.close();
            this.socket.close();
        } catch (IOException e) {
            println("tried closing a channel and fucked up");
        }
    }

    protected void dispose() {
        if (output != null) {
            closeChannel();
        }
        this.socket = null;
        this.output = null;
    }

    protected void dispose(Exception x) {
        if (output != null)
            println("Disconnected from OPC server");
        this.socket = null;
        this.output = null;
        didDispose(x);
    }

    protected void didDispose(Exception x) {
//    println("Failed to connect to OPC server " + host);
//    println("disposed");
    }

    // @Override
    protected void onSend(int[] colors) {
        if (packetData == null || packetData.length == 0)
            return;

        for (int i = 0; i < colors.length; i++) {
            // TODO MJP: this might not work as expected, if we are dimming the global color array for each datagram that is sent
            LXColor.RGBtoHSB(colors[i], hsb);
            float b = hsb[2];
            colors[i] = LX.hsb(360f * hsb[0], 100f * hsb[1], (float) (100f * b * manager.getGlobalBrightness()));
        }

        //connect();
        if (isConnected()) {
            try {
                this.output.write(getPacketData(colors));
            } catch (IOException iox) {
                dispose(iox);
            }
        }

    }

    // @Override
    protected byte[] getPacketData(int[] colors) {
        for (int i = 0; i < this.pointIndices.length; ++i) {
            int dataOffset = INDEX_DATA + i * BYTES_PER_PIXEL;
            int c = colors[this.pointIndices[i]];
            this.packetData[dataOffset + OFFSET_R] = (byte) (0xFF & (c >> 16));
            this.packetData[dataOffset + OFFSET_G] = (byte) (0xFF & (c >> 8));
            this.packetData[dataOffset + OFFSET_B] = (byte) (0xFF & c);
        }
        // all other values in packetData should be 0 by default
        return this.packetData;
    }

}
