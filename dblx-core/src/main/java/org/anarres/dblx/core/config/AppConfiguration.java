/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.dblx.core.config;

import java.io.IOException;
import java.util.Arrays;
import javax.annotation.Nonnull;
import org.anarres.dblx.core.model.Model;
import org.anarres.dblx.core.model.ModelLoader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author shevek
 */
@Configuration
public class AppConfiguration {

    public static interface Provider {

        @Nonnull
        public Iterable<Class<?>> getConfigurationClasses();
    }

    public static class ProviderSupport implements Provider {

        private final Class<?>[] configurationClasses;

        public ProviderSupport(@Nonnull Class<?>... configurationClasses) {
            this.configurationClasses = configurationClasses;
        }

        public ProviderSupport() {
            this.configurationClasses = new Class<?>[]{getClass()};
        }

        @Override
        public Iterable<Class<?>> getConfigurationClasses() {
            return Arrays.asList(configurationClasses);
        }
    }

    @Bean
    public Model model(@Nonnull @Value("${dblx.model:brainlove.complete}") String modelName) throws IOException {
        ModelLoader loader = new ModelLoader(modelName);
        return loader.load();
    }
}
