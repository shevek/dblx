/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.dblx.core.config;

import com.google.common.base.Charsets;
import com.google.common.collect.Iterables;
import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.LogManager;
import javax.annotation.Nonnull;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

/**
 *
 * @author shevek
 */
public abstract class AbstractSpringArguments extends AbstractArguments {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractSpringArguments.class);
    private final OptionSpec<?> componentsOption = parser.accepts("components", "Prints the DI component set and exits.").forHelp();
    private final OptionSpec<File> logdirOption = parser.accepts("logdir", "Redirects all logging to files in the given directory.").withRequiredArg().ofType(File.class).describedAs("dir");
    private final OptionSpec<String> configOption = parser.accepts("config", "Specifies a Spring-DI config file, class or directory to load.").withRequiredArg().withValuesSeparatedBy(',').describedAs("spring-config.xml");
    private final OptionSpec<String> profileOption = parser.accepts("profile", "Enables a Spring-DI @Profile.").withRequiredArg().withValuesSeparatedBy(',').describedAs("profile0,profile1,...");
    private final OptionSpec<String> defineOption = parser.acceptsAll(Arrays.asList("define", "D"), "Defines a Spring-DI @Value.").withRequiredArg().describedAs("key=value");

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public AbstractSpringArguments(@Nonnull String[] args) throws IOException {
        super(args);
        // LOG.info("CommandLine is " + cmdline);

        File logDir = getOptions().valueOf(logdirOption);
        if (logDir != null) {
            {
                File file = new File(logDir, "stdout.log");
                // LOG.info("stdout -> " + file);
                OutputStream out = Files.asByteSink(file, FileWriteMode.APPEND).openBufferedStream();
                Closeable prev = System.out;
                System.setOut(new PrintStream(out, true, Charsets.UTF_8.name()));
                prev.close();
            }
            {
                File file = new File(logDir, "stderr.log");
                // LOG.info("stderr -> " + file);
                OutputStream out = Files.asByteSink(file, FileWriteMode.APPEND).openBufferedStream();
                out = new BufferedOutputStream(out);
                Closeable prev = System.err;
                System.setErr(new PrintStream(out, true, Charsets.UTF_8.name()));
                prev.close();
            }
            // In case we are using Jdk14Logger, we need to tell it to recapture System.out
            LogManager manager = LogManager.getLogManager();
            manager.reset();
            manager.readConfiguration();
        }
    }

    private void appendAttributes(@Nonnull StringBuilder buf, @Nonnull AnnotationMetadata metadata, @Nonnull Class<? extends Annotation> type) {
        MultiValueMap<String, Object> attrs = metadata.getAllAnnotationAttributes(type.getName(), true);
        if (attrs == null)
            return;
        buf.append("\n\t").append(type.getSimpleName());
        // Garbage collect all unspecified attributes.
        Iterator<Map.Entry<String, List<Object>>> it = attrs.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, List<Object>> e = it.next();
            switch (e.getValue().size()) {
                case 1:
                    if (!"".equals(e.getValue().get(0)))
                        break;
                case 0:
                    it.remove();
                    continue;
            }
        }
        if (attrs.isEmpty())
            return;
        buf.append(": ").append(attrs);
    }

    protected void printComponentsOn(@Nonnull PrintStream out) throws IOException {
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(true);
        Iterable<BeanDefinition> beans = provider.findCandidateComponents("org.anarres.dblx");
        for (BeanDefinition bean : beans) {
            StringBuilder buf = new StringBuilder();
            buf.append(bean.getBeanClassName());
            if (bean instanceof AnnotatedBeanDefinition) {
                AnnotatedBeanDefinition annotatedBean = (AnnotatedBeanDefinition) bean;
                AnnotationMetadata metadata = annotatedBean.getMetadata();
                appendAttributes(buf, metadata, Profile.class);
                appendAttributes(buf, metadata, Component.class);
                appendAttributes(buf, metadata, Configuration.class);
            }
            out.println(buf);
        }
    }

    @Override
    @SuppressFBWarnings("DM_EXIT")
    protected OptionSet parseOptions() throws Exception {
        OptionSet o = super.parseOptions();
        if (o.has(componentsOption)) {
            printComponentsOn(System.err);
            System.exit(1);
        }
        return o;
    }

    @Nonnull
    public Iterable<? extends String> getProfiles() {
        List<String> profileArgs = getOptions().valuesOf(profileOption);
        List<String> profiles = new ArrayList<String>();
        for (String profileArg : profileArgs) {
            for (String profileArgPart : StringUtils.split(profileArg, ", ;")) {
                if (StringUtils.isBlank(profileArgPart))
                    continue;
                profiles.add(profileArgPart);
            }
        }
        return profiles;
    }

    @Nonnull
    public Iterable<? extends String> getConfigs() {
        return getOptions().valuesOf(configOption);
    }

    @Nonnull
    public Map<String, Object> getDefinitionMap() {
        List<String> definitionArgs = getOptions().valuesOf(defineOption);
        if (Iterables.isEmpty(definitionArgs))
            return Collections.emptyMap();

        Map<String, Object> definitionMap = new HashMap<String, Object>();
        for (String definitionArg : definitionArgs) {
            int idx = definitionArg.indexOf('=');
            String key;
            String value;
            if (idx == -1) {
                key = definitionArg;
                value = Boolean.TRUE.toString();
            } else {
                key = definitionArg.substring(0, idx);
                value = definitionArg.substring(idx + 1);
            }
            definitionMap.put(key, value);
        }
        return definitionMap;
    }
}
