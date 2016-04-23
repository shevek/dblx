/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.dblx.core.model;

import java.util.List;
import javax.annotation.Nonnull;
import processing.core.PVector;

//********************************************************************* NODE
/**
 * Connection point for bars, usually virtual except to define bars themselves.
 * Name, coordinates, and other properties.
 * Adjacent bars and nodes.
 * Pixel mapping, for effects at nodes?
 *********************************************************************** * */
public class Node {

    public final String name;
    // public final Vector3f position;
    /** Position in mm. */
    public final long x;
    /** Position in mm. */
    public final long y;
    /** Position in mm. */
    public final long z;
    public final List<? extends String> tags;
    // Point3f.
    public final PVector xyz;
    //public final List<String> properties = new ArrayList<String>();
    //public final List<Node> adjacent_nodes = new ArrayList<Node>();
    //public final List<Bar> adjacent_bars = new ArrayList<Bar>();

    public Node(String name, long x, long y, long z, List<? extends String> tags) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
        this.tags = tags;
        this.xyz = new PVector(x, y, z);
    }

    @Nonnull
    public String getName() {
        return name;
    }

}
