/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.dblx.core.config;

import com.google.common.base.Preconditions;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;

/**
 *
 * @author shevek
 */
public class SpringUtils {

    private static final Logger LOG = LoggerFactory.getLogger(SpringUtils.class);

    private static class Filter implements FileFilter {

        @Override
        public boolean accept(File file) {
            if (!file.isFile())
                return false;
            if (!file.getName().toLowerCase().endsWith(".xml"))
                return false;
            return true;
        }
    }

    public static void addConfigurations(@Nonnull GenericApplicationContext context, @Nonnull AbstractSpringArguments arguments) throws IOException {
        PROFILES:
        {
            context.getEnvironment().getActiveProfiles();   // Trigger doGetActiveProfiles before we start.
            Iterable<? extends String> profiles = arguments.getProfiles();
            if (profiles != null) {
                Set<String> active = new HashSet<String>();
                for (String profile : profiles) {
                    if (profile.equals("-all"))
                        active.clear();
                    else if (profile.startsWith("-"))
                        active.remove(profile.substring(1));
                    else if (profile.startsWith("+"))
                        active.add(profile.substring(1));
                    else
                        active.add(profile);
                }
                context.getEnvironment().setActiveProfiles(active.toArray(ArrayUtils.EMPTY_STRING_ARRAY));
            }
        }

        PROPERTIES:
        {
            // PropertySource<?> source = new CommonsCliCommandLinePropertySource("commandline args", arguments.getCommandLine());
            // context.getEnvironment().getPropertySources().addFirst(source);
            Map<String, Object> definitions = arguments.getDefinitionMap();
            context.getEnvironment().getPropertySources().addFirst(new MapPropertySource("commandline definitions", definitions));
        }

        CONFIGS:
        {
            XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(context);
            AnnotatedBeanDefinitionReader annotationReader = new AnnotatedBeanDefinitionReader(context);
            Iterable<? extends String> configurationResources = arguments.getConfigs();
            if (configurationResources != null) {
                for (String configurationResource : configurationResources) {
                    READ:
                    {
                        File file = new File(configurationResource);
                        if (file.isFile()) {
                            LOG.info("Found a file: " + file.getAbsolutePath());
                            Resource resource = new FileSystemResource(file);
                            xmlReader.loadBeanDefinitions(resource);
                            break READ;
                        }
                        LOG.debug("Not a file: " + configurationResource);

                        if (file.isDirectory()) {
                            File[] children = file.listFiles(new Filter());
                            Preconditions.checkNotNull(children, "No children of " + file);
                            for (File child : children) {
                                Resource resource = new FileSystemResource(child);
                                xmlReader.loadBeanDefinitions(resource);
                                break READ;
                            }
                        }
                        LOG.debug("Not a directory: " + configurationResource);

                        try {
                            Class<?> type = ClassUtils.forName(configurationResource, null);
                            LOG.info("Found a class: " + type.getName());
                            annotationReader.register(type);
                            break READ;
                        } catch (ClassNotFoundException e) {
                            LOG.debug("Not a class: " + configurationResource + ": " + e);
                        }

                        try {
                            // Not a class. Try a resource.
                            Resource[] resources = context.getResources(configurationResource);
                            if (!ArrayUtils.isEmpty(resources)) {
                                for (Resource resource : resources) {
                                    LOG.info("Found a resource: " + resource);
                                    xmlReader.loadBeanDefinitions(resource);
                                }
                                break READ;
                            }
                        } catch (IOException e) {
                            LOG.debug("Not a resource: " + configurationResource + ": " + e);
                        }

                        throw new IllegalArgumentException("Cannot locate resource " + configurationResource);
                    }
                }
            }
        }

    }

    @Nonnull
    public static Set<String> getProfiles() throws IOException {
        String basePackage = ClassUtils.convertClassNameToResourcePath("org.anarres.dblx");
        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + basePackage + "/**/*.class";
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
        MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resourcePatternResolver);
        Set<String> out = new TreeSet<String>();
        for (Resource resource : resources) {
            MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
            AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
            Map<String, Object> annotationAttributes = annotationMetadata.getAnnotationAttributes(Profile.class.getName());
            if (annotationAttributes == null)
                continue;
            String[] value = (String[]) annotationAttributes.get("value");
            if (value == null)
                continue;
            out.addAll(Arrays.asList(value));
        }
        return out;
    }
}
