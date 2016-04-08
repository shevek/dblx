/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.dblx.core.model;

import heronarts.lx.model.LXModel;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;

/**
 *
 * @author shevek
 */
public class Model extends LXModel {

    private final Map<String, Node> nodes = new HashMap<>();
    private final UndirectedGraph<String, Bar> graph = new SimpleGraph<>(Bar.class);

    public void addNode(@Nonnull Node node) {
        nodes.put(node.getName(), node);
        graph.addVertex(node.getName());
    }

    public void addEdge(@Nonnull Bar edge) {
        if (!graph.addEdge(edge.node1, edge.node2, edge))
            throw new IllegalStateException("Edge already exists: " + edge);
    }
}
