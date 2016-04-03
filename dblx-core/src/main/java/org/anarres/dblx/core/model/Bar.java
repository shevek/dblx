/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.dblx.core.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jgrapht.graph.DefaultEdge;

/**
 * Edges between nodes with strips of LEDs along their length.
 * Name, nodes, direction, and other properties.
 * Adjacent bars.
 * Pixel map, reversed when traversing opposite direction.
 *
 * @author shevek
 */
public class Bar extends DefaultEdge {

    public final String name;
    public final Node node1;
    public final Node node2;
    public final int channel;
    public final List<String> tags;
    public final boolean reversed;
    // public final Bar pair;

    //private LXPoint[] points;
    final float pixel_density;
    final float pixel_buffer;
    final String pixel_layout;
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
                density, buffer, layout);
    }
}
