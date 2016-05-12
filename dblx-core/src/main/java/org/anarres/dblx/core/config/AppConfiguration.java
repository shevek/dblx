/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.dblx.core.config;

import javax.annotation.Nonnull;
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
}
