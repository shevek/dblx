/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.dblx.layout;

import com.google.common.base.Splitter;
import com.google.common.io.CharSource;
import com.google.common.io.Resources;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import java.io.BufferedReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author shevek
 */
public class BrainLayoutTest {

    private static final Logger LOG = LoggerFactory.getLogger(BrainLayoutTest.class);

    @Test
    public void testLayout() throws Exception {

        UndirectedGraph<String, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);

        URL url = Resources.getResource("layouts/playa_brain/Model_Node_Info.csv");
        CharSource source = Resources.asCharSource(url, StandardCharsets.UTF_8);
        try (BufferedReader reader = source.openBufferedStream()) {
            CSVReader csv = new CSVReaderBuilder(reader).withSkipLines(1).build();
            Splitter splitter = Splitter.on('_');
            for (String[] line : csv) {
                String node = line[0];
                List<String> neighbours = splitter.splitToList(line[5]);
                for (String neighbour : neighbours) {
                    graph.addVertex(node);
                    graph.addVertex(neighbour);
                    graph.addEdge(node, neighbour);
                }
            }
        }

        Map<Integer, Set<String>> histogram = new TreeMap<>();
        for (int i = 0; i < 10; i++) {
            histogram.put(i, new TreeSet<String>());
        }
        // A strip can only be 300px long
        // 60 LEDs per metre
        // 4 channels per controller
        // minimize controllers
        for (String node : new TreeSet<>(graph.vertexSet())) {
            histogram.get(graph.degreeOf(node)).add(node);
        }

        for (Map.Entry<Integer, Set<String>> e : histogram.entrySet()) {
            LOG.info(e.getKey() + ": " + e.getValue().size() + " - " + e.getValue());
        }

        LOG.info("Node count is " + graph.vertexSet().size());
        LOG.info("Edge count is " + graph.edgeSet().size());

        Set<String> nodes = new TreeSet<>(graph.vertexSet());
        LOG.info(String.valueOf(nodes));
    }
}
