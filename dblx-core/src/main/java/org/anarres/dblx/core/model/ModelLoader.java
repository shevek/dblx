/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.dblx.core.model;

import com.google.common.io.CharSource;
import com.google.common.io.Resources;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import javax.annotation.Nonnull;

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
    public Model load() throws IOException {
        Model model = new Model();

        NODES:
        {
            URL url = Resources.getResource("/models/" + modelName + "/nodes.csv");
            CharSource source = Resources.asCharSource(url, StandardCharsets.UTF_8);
            try (Reader in = source.openBufferedStream()) {
                CSVReader reader = new CSVReaderBuilder(in).withSkipLines(1).build();
                for (String[] line : reader) {
                }
            }
        }

        BARS:
        {
            URL url = Resources.getResource("/models/" + modelName + "/bars.csv");
            CharSource source = Resources.asCharSource(url, StandardCharsets.UTF_8);
            try (Reader in = source.openBufferedStream()) {
                CSVReader reader = new CSVReaderBuilder(in).withSkipLines(1).build();
                for (String[] line : reader) {
                }
            }
        }

        return model;
    }
}
