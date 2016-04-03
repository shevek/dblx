package org.anarres.dblx.core.model;

import heronarts.lx.model.LXModel;
import heronarts.lx.model.LXPoint;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import javax.annotation.Nonnull;
import org.anarres.dblx.core.Core;
import processing.core.PVector;
import processing.data.Table;
import static org.anarres.dblx.core.Core.logTime;

/** ******************************************************************** MODEL
 * This is the model for the whole brain. It contains four mappings, two of
 * which users should use (Bar and Node) and two which are set up to deal
 * with the physical reality of the actual brain, double bars and double
 * nodes and so on.
 *
 * SUBCLASSES
 * class Node
 * class Bar
 *
 * FIELDS
 * Nodes
 * Bars
 * ChannelMap
 * Points
 *
 *
 *
 * INITIALIZATION
 * setChannelMap()
 * initialize()
 * import_model()
 *
 *
 *
 * DATA ACCESS
 *    // many variations of these
 * getNode()
 * getBar()
 * getPoint()
 *
 *
 *
 *
 *
 * @author Alex Maki-Jokela
 * @author Alexander D. Scouras
 ************************************************************************* * */
public class LXGraphModel extends LXModel {

    //***************************************************** FIELDS AND CONSTANTS
    //-------------- Constants
    private static final String DIR_MAPS = "models";
    private static final String FILE_PARAMS = "params.csv";
    private static final String FILE_NODES = "nodes.csv";
    private static final String FILE_BARS = "bars.csv";
    private static final String FILE_PIXELS = "pixels.csv";

    private final Core core;
    //-------------- Fields
    //Note that these are stored in maps, not lists. 
    //Nodes are keyed by their three letter name ("LAB", "YAK", etc)
    //Bars are keyed by the two associated nodes in alphabetical order ("LAB-YAK", etc)
    public final TreeMap<String, Node> nodes;
    public final TreeMap<String, TreeMap<String, Bar>> bars;

    /*
     public ArrayList<Node>   nodes  = new ArrayList<Node>();
     public ArrayList<Bars>   bars   = new ArrayList<Bar>();
     public ArrayList<Pixels> pixels = new ArrayList<LXPoint>();
     */

    /* Automatic pixel layouts are based on model parameters.
     * TODO: I suspect using anything besides center is stupid
     * TODO: Brainlove has a custom mapping based on binned widths to minimize
     *       number of unique strip lengths. I don't intend to support that, it
     *       will require a custom mapping.
     * TODO: This does not necessarily give intelligent LED strip layout/maps
     *       that you would send to a microcontroller.
     *    Center: Fit as many as possible, given density and buffer, and center
     *            between the nodes
     *    Fill:   Fit as many as possible, given density and buffer, and start
     *            filling in from the start node + buffer width
     */
    public String pixel_layout = "center";
    public float pixel_density = 60.0f;
    public float pixel_buffer = 0.1f;

    //============== Physical Hardware Limitations
    // TODO: These and other parameters should probably be globals instead
    // TODO: This is only here so it compiles temporarily.
    public List<int[]> channelMap;

    public final int max_pixels_per_channel = 500;

    public int channels = 0;
    public int strips = 0;
    private int pixels_in_channel = 0;
    private int pixels_in_model = 0;

    /** ************************************************************ Constructor
     *
     *********************************************************************** * */
    public LXGraphModel(@Nonnull Core core, String model_name) {
        // Stopwatch stopwatch = Stopwatch.createStarted();
        this.core = core;

        logTime("-- loading model " + model_name);

        this.nodes = new TreeMap<String, Node>();
        this.bars = new TreeMap<String, TreeMap<String, Bar>>();
        String path_params = DIR_MAPS + "/" + model_name + "/" + FILE_PARAMS;
        String path_nodes = DIR_MAPS + "/" + model_name + "/" + FILE_NODES;
        String path_bars = DIR_MAPS + "/" + model_name + "/" + FILE_BARS;
        String path_pixels = DIR_MAPS + "/" + model_name + "/" + FILE_PIXELS;

        this.load_params(path_params);
        logTime("---- loaded model parameters");
        this.load_nodes(path_nodes);
        logTime("---- loaded nodes");
        this.load_bars(path_bars);
        logTime("---- loaded bars");
        this.load_pixels(path_pixels);
        logTime("---- loaded pixels");

        // this.initialize_pixels();
    }

    // ========================================================= Load Data Files
    // ----------- Load Parameters
    // Custom layout variables, like physical constraints for pixels per inch,
    // borders around nodes, etc.
    private void load_params(String path) {
        File f = new File(path);
        if (!f.exists()) {
            return;
        }
        Table table_params = core.lx.applet.loadTable(path, "header,tsv");
    }

    // ----------- Load Nodes
    private void load_nodes(String path) {
        Table table_nodes = core.lx.applet.loadTable(path, "header,tsv");
        for (processing.data.TableRow row : table_nodes.rows()) {
            Node node = new Node(
                    row.getString("Node"),
                    row.getFloat("X"),
                    row.getFloat("Y"),
                    row.getFloat("Z"),
                    row.getString("Tags")
            );
            this.add_node(node);
        }
    }

    // ----------- Load Bars
    private void load_bars(String path) {
        PVector xyz1, xyz2;
        PVector dir;
        Table table_bars = core.lx.applet.loadTable(path, "header,tsv");
        for (processing.data.TableRow row : table_bars.rows()) {
            Node node1 = this.nodes.get(row.getString("Node1"));
            Node node2 = this.nodes.get(row.getString("Node2"));
            Bar bar = new Bar(
                    node1,
                    node2,
                    row.getInt("Channel"),
                    row.getString("Tags"),
                    pixel_density,
                    pixel_buffer,
                    pixel_layout
            );

            this.add_bar(bar);
        }
    }

    // ----------- Load Pixels
    // TODO: We'll just generate it based on parameters for now
    private void load_pixels(String path) {
        File f = new File(path);
        if (!f.exists()) {
            return;
        }
        Table table_pixels = core.lx.applet.loadTable(path, "header,tsv");
    }

    private void automap_bars() {

    }

    //******************************************************* GET NODES AND BARS
    /** Fetch a node by name */
    public Node get_node(String node) {
        return this.nodes.get(node);
    }

    /** Fetch a bar by its nodes - note that ordering is important */
    public Bar get_bar(Node node1, Node node2) {
        return this.bars.get(node1.name).get(node2.name);
    }

    /** Fetch a bar by its node names - note that ordering is important */
    public Bar get_bar(String node1, String node2) {
        return this.bars.get(node1).get(node2);
    }

    //******************************************************* ADD NODES AND BARS
    /** Add node to model. */
    private void add_node(Node node) {
        if (this.nodes.get(node.name) != null) {
            throw new RuntimeException("Node " + node.name + " already exists!");
        }
        this.nodes.put(node.name, node);
        this.bars.put(node.name, new TreeMap<String, Bar>());
    }

    /** Initialize pixels and add bar to the model. */
    private void add_bar(Bar bar) {

        List<LXPoint> points = new ArrayList<LXPoint>();

        PVector vector = new PVector(); // vector along the bar
        PVector norm = new PVector(); // normalized vector
        PVector step = new PVector(); // step vector per pixel
        PVector start = new PVector(); // starting coordinates
        PVector coords = new PVector(); // current coordinates
        int count = 0; // number of pixels

        //============ Validation
        if (this.get_node(bar.node1.name) == null) {
            throw new RuntimeException("Unknown node " + bar.node1.name);
        }
        if (this.get_node(bar.node2.name) == null) {
            throw new RuntimeException("Unknown node " + bar.node2.name);
        }
        if (this.get_bar(bar.node1, bar.node2) != null) {
            throw new RuntimeException("Bar " + bar.name + " already exists!");
        }

        float density = bar.pixel_density;
        float buffer = bar.pixel_buffer;
        String layout = bar.pixel_layout;
        float rate = 1f / density;
        if (buffer == 0.f) {
            buffer = rate;
        } // pixels never start right at a node

        //============ Vector Magic
        PVector.sub(bar.node2.xyz, bar.node1.xyz, vector);
        vector.normalize(norm);

        float len_bar = vector.mag();
        float len_zone = len_bar - (2f * buffer);
        count = (int) Math.floor(len_zone * density);
        float len_true = (float) count / density;
        float len_waste = len_zone - len_zone;

        // offset coordinates for first pixel
        if (layout == "fill") {
            PVector.mult(norm, buffer, start);
            PVector.mult(norm, rate, step);
        } else if (layout == "center") {
            PVector.mult(norm, buffer + (len_waste / 2f), start);
            PVector.mult(norm, rate, step);
        }
        // move to true coordinates
        PVector.add(start, bar.node1.xyz, coords);

        //---------- map channels 
        // See if we need to add a new channel
        // TODO: Actually build the channels
        if (pixels_in_channel + count > max_pixels_per_channel) {
            channels++;
            strips = 0;
            pixels_in_channel = 0;
        }
        pixels_in_channel += count;
        pixels_in_model += count;

        //------------ Allocate Pixels!
        for (int i = 0; i <= count; i++) {
            LXPoint point = new LXPoint(coords.x, coords.y, coords.z);
            points.add(point);
            coords.add(step);
        }

        /*
         bar.channel = channels - 1;
         bar.strip = strips;
         bar.channel_pixel = pixels_in_channel;
         bar.model_pixel = pixals_in_model;
         bar.points = points;

         rev = bar.reverse();
         */
        //------------ Add Points and Bars to Model
        this.bars.get(bar.node1.name).put(bar.node2.name, bar);
        // this.bars.get(node2.name).put(node1.name, rev);

    }

}
