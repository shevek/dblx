/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.dblx.core.model;

import heronarts.lx.model.LXModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import processing.core.PVector;

//********************************************************************* NODE
/**
 * Connection point for bars, usually virtual except to define bars themselves.
 * Name, coordinates, and other properties.
 * Adjacent bars and nodes.
 * Pixel mapping, for effects at nodes?
 *********************************************************************** * */
public class Node extends LXModel {

    public final String name;
    public final float x;
    public final float y;
    public final float z;
    public final List<String> tags;
    public final PVector xyz;
    //public final List<String> properties = new ArrayList<String>();
    //public final List<Node> adjacent_nodes = new ArrayList<Node>();
    //public final List<Bar> adjacent_bars = new ArrayList<Bar>();

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
