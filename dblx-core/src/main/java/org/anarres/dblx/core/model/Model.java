/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.dblx.core.model;

import heronarts.lx.model.LXModel;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;

/**
 *
 * @author shevek
 */
public class Model extends LXModel {

    private final UndirectedGraph<Node, Bar> graph = new SimpleGraph<>(Bar.class);

}
