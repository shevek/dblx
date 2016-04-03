package org.anarres.dblx.core.model;

import heronarts.lx.model.LXModel;
import heronarts.lx.model.LXPoint;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;
import processing.core.PVector;
import processing.data.Table;
import static com.sun.xml.internal.ws.client.ContentNegotiation.none;
import static javax.management.Query.not;
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

    //********************************************************************* NODE
    /**
     * Connection point for bars, usually virtual except to define bars themselves.
     * Name, coordinates, and other properties.
     * Adjacent bars and nodes.
     * Pixel mapping, for effects at nodes?
     *********************************************************************** * */
    public class Node extends LXModel {

        public final String name;
        public final float x, y, z;
        public final List<String> tags;
        public final PVector xyz;
        //public final List<String> properties = new ArrayList<String>();
        //public final List<Node> adjacent_nodes = new ArrayList<Node>();
        //public final List<Bar> adjacent_bars = new ArrayList<Bar>();

        /** Constructor
         */
        public Node(String name, float x, float y, float z, String[] tags) {
            this.name = name;
            this.x = x;
            this.y = y;
            this.z = z;
            this.tags = new ArrayList<String>(Arrays.asList(tags));
            this.xyz = new PVector(x, y, z);
        }

        public Node(String name, float x, float y, float z, String tags) {
            this(name, x, y, z, tags.trim().split("\\s+"));
        }
    }

    //********************************************************************** BAR
    /**
     * Edges between nodes with strips of LEDs along their length.
     * Name, nodes, direction, and other properties.
     * Adjacent bars.
     * Pixel map, reversed when traversing opposite direction.
     *********************************************************************** * */
    public static class Bar extends LXModel {

        public final String name;
        public final Node node1;
        public final Node node2;
        public final int channel;
        public final List<String> tags;
        public final boolean reversed;
        public final Bar pair;

        //private LXPoint[] points;
        private final float pixel_density;
        private final float pixel_buffer;
        private final String pixel_layout;
        //public final List<Bar> adjacent_bars = new ArrayList<Bar>();

        /**
         * Constructor
         */
        public Bar(Node node1, Node node2, int channel, String[] tags,
                float density, float buffer, String layout) {
            this.name = node1.name + "-" + node2.name;
            this.node1 = node1;
            this.node2 = node2;
            this.channel = channel;
            this.tags = new ArrayList<String>(Arrays.asList(tags));

            this.reversed = false;
            this.pixel_buffer = buffer;
            this.pixel_density = density;
            this.pixel_layout = layout;
        }

        public Bar(Node node1, Node node2, int channel, String tags,
                float density, float buffer, String layout) {
            //String[] tag_list = tags.trim().split("\\s+");
            //this(node1, node2, tag_list, rev, density, buffer, layout);
            this(node1, node2, channel, tags.trim().split("\\s+"),
                    rev, density, buffer, layout);
        }

        public Bar(Node node1, Node node2, int channel, String tags) {
            this(node1, node2, channel, tags, rev, none, none, none);
        }

        /**
         * Create the same bar in the opposite direction for directed traversals.
         */
        private Bar reverse() {
            Bar rev = new Bar(this.node2, this.node1, this.channel, this.tags,
                    this.density, this.buffer, this.layout);
            rev.rev = !this.rev;
            rev.strip = this.strip;
            rev.points = Collections.reverse(this.points);
            rev.pair = this;
            this.pair = rev;
            return rev;
        }
    }

    //***************************************************** FIELDS AND CONSTANTS
    //-------------- Constants
    String DIR_MAPS = "models";
    String FILE_PARAMS = "params.csv";
    String FILE_NODES = "nodes.csv";
    String FILE_BARS = "bars.csv";
    String FILE_PIXELS = "pixels.csv";

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
    public ArrayList<int[]> channelMap;

    public final int max_pixels_per_channel = 500;

    public int channels = 0;
    public int strips = 0;
    private int pixels_in_channel = 0;
    private int pixels_in_model = 0;

    /** ************************************************************ Constructor
     *
     *********************************************************************** * */
    public LXGraphModel(String model_name) {

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

        this.initialize_pixels();
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
        Table table_params = loadTable(path, "header,tsv");
    }

    // ----------- Load Nodes
    private void load_nodes(String path) {
        Table table_nodes = loadTable(path, "header,tsv");
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
        Table table_bars = loadTable(path, "header,tsv");
        for (processing.data.TableRow row : table_bars.rows()) {
            Node node1 = this.nodes.get(row.getString("Node1"));
            Node node2 = this.nodes.get(row.getString("Node2"));
            Bar bar = new Bar(
                    node1,
                    node2,
                    row.getString("Channel"),
                    row.getString("Tags")
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
        Table table_pixels = loadTable(path, "header,tsv");
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
        if (this.nodes.get(node.name)) {
            throw new RuntimeException("Node " + node.name + " already exists!");
        }
        this.nodes.put(node.name, node);
        this.bars.put(node.name, new TreeMap<String, Bar>());
    }

    /** Initialize pixels and add bar to the model. */
    private void add_bar(Bar bar) {

        List<LXPoint> points = new ArrayList<LXPoint>();
        float density, rate, buffer;
        float len_bar, len_zone, len_true, len_waste;
        String layout;

        PVector vector = new PVector(); // vector along the bar
        PVector norm = new PVector(); // normalized vector
        PVector step = new PVector(); // step vector per pixel
        PVector start = new PVector(); // starting coordinates
        PVector coords = new PVector(); // current coordinates
        int count = 0; // number of pixels

        //============ Validation
        if (!this.get_node(bar.node1.name)) {
            throw new RuntimeException("Unknown node " + bar.node1.name);
        }
        if (!this.get_node(bar.node2.name)) {
            throw new RuntimeException("Unknown node " + bar.node2.name);
        }
        if (this.get_bar(bar.node1, bar.node2)) {
            throw new RuntimeException("Bar " + bar.name + " already exists!");
        }

        //============ Set defaults and initialize
        if (bar.pixel_density == null) {
            bar.pixel_density = model.pixel_density;
        }
        if (bar.pixel_buffer == null) {
            bar.pixel_buffer = model.pixel_buffer;
        }
        if (bar.pixel_layout == null) {
            bar.pixel_layout = model.pixel_layout;
        }

        density = bar.pixel_density;
        buffer = bar.pixel_buffer;
        layout = bar.pixel_layout;
        rate = 1.0 / density;
        if (buffer == 0.f) {
            buffer = rate;
        } // pixels never start right at a node

        //============ Vector Magic
        PVector.sub(node2.xyz, node1.xyz, vector);
        PVector.normalize(vector, norm);

        len_bar = vector.mag();
        len_zone = len_bar - (2.0 * buffer);
        count = (int) Math.floor(len_zone * density);
        len_true = (float) count / density;
        len_waste = len_zone - len_zone;

        // offset coordinates for first pixel
        if (layout == "fill") {
            PVector.mult(norm, buffer, start);
            PVector.mult(norm, rate, step);
        } else if (layout == "center") {
            PVector.mult(norm, buffer + (len_waste / 2.0), start);
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

        bar.channel = channels - 1;
        bar.strip = strips;
        bar.channel_pixel = pixels_in_channel;
        bar.model_pixel = pixals_in_model;
        bar.points = points;

        rev = bar.reverse();

        //------------ Add Points and Bars to Model
        this.bars.get(node1.name).put(node2.name, bar);
        this.bars.get(node2.name).put(node1.name, rev);

    }

}
