/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.dblx.core;

import com.google.common.base.Stopwatch;
import java.util.ServiceLoader;
import org.anarres.dblx.core.config.AppConfiguration;
import org.anarres.dblx.core.config.SpringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;

/**
 *
 * @author shevek
 */
public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        Stopwatch stopwatch = Stopwatch.createStarted();
        Arguments arguments = new Arguments(args);
        GenericApplicationContext context = new GenericApplicationContext();
        AnnotatedBeanDefinitionReader reader = new AnnotatedBeanDefinitionReader(context);
        reader.register(AppConfiguration.class);
        for (AppConfiguration.Provider configurationProvider : ServiceLoader.load(AppConfiguration.Provider.class)) {
            for (Class<?> configurationClass : configurationProvider.getConfigurationClasses()) {
                LOG.debug("Registering additional ServerConfiguration class " + configurationClass.getName());
                reader.register(configurationClass);
            }
        }
        SpringUtils.addConfigurations(context, arguments);
        context.refresh();
        context.registerShutdownHook();
        context.start();

        LOG.info("Ready; startup took " + stopwatch);
    }
}
