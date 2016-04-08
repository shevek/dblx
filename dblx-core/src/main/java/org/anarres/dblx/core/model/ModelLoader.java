/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.dblx.core.model;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.io.CharSource;
import com.google.common.io.Resources;
import com.opencsv.CSVParser;
import com.opencsv.CSVReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.annotation.Nonnull;
import javax.measure.converter.UnitConverter;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;

/**
 *
 * @author shevek
 */
public class ModelLoader {

    private final String modelName;

    public ModelLoader(@Nonnull String modelName) {
        this.modelName = modelName;
    }

    @Nonnull
    private CSVReader newSVReader(@Nonnull Reader reader, char separator, int skipLines) {
        CSVParser parser = new CSVParser(separator);
        return new CSVReader(reader, skipLines, parser);
    }

    @Nonnull
    public Model load() throws IOException {
        Model model = new Model();

        UnitConverter converter = NonSI.INCH.getConverterTo(SI.MILLIMETER);
        Splitter splitter = Splitter.on(CharMatcher.BREAKING_WHITESPACE);

        NODES:
        {
            URL url = Resources.getResource("models/" + modelName + "/nodes.csv");
            CharSource source = Resources.asCharSource(url, StandardCharsets.UTF_8);
            try (Reader in = source.openBufferedStream()) {
                CSVReader reader = newSVReader(in, '\t', 1);
                for (String[] line : reader) {
                    String name = line[0];
                    long x = (long) converter.convert(Double.parseDouble(line[1]));
                    long y = (long) converter.convert(Double.parseDouble(line[2]));
                    long z = (long) converter.convert(Double.parseDouble(line[3]));
                    List<String> tags = splitter.splitToList(line[4]);
                    model.addNode(new Node(name, x, y, z, tags));
                }
            }
        }

        BARS:
        {
            URL url = Resources.getResource("models/" + modelName + "/bars.csv");
            CharSource source = Resources.asCharSource(url, StandardCharsets.UTF_8);
            try (Reader in = source.openBufferedStream()) {
                CSVReader reader = newSVReader(in, '\t', 1);
                for (String[] line : reader) {
                    List<String> tags = splitter.splitToList(line[2]);
                    Bar bar = new Bar(line[0], line[1], tags);
                    model.addEdge(bar);
                }
            }
        }

        return model;
    }
}
