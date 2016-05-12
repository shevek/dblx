/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.dblx.core.config;

import com.google.common.base.Throwables;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.io.PrintStream;
import javax.annotation.Nonnull;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.anarres.jdiagnostics.ProductMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author shevek
 */
public class AbstractArguments {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractArguments.class);
    protected final OptionParser parser = new OptionParser();
    private final OptionSpec<?> helpOption = parser.accepts("help", "Displays command-line help.").forHelp();
    private final OptionSpec<?> versionOption = parser.accepts("version", "Displays the product version and exits.").forHelp();
    private final String[] args;
    private OptionSet options;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public AbstractArguments(@Nonnull String[] args) {
        this.args = args;
    }

    protected void printHelpOn(@Nonnull PrintStream out) throws IOException {
        parser.printHelpOn(out);
    }

    @Nonnull
    @SuppressFBWarnings("DM_EXIT")
    protected OptionSet parseOptions() throws Exception {
        OptionSet o = parser.parse(args);
        if (o.has(helpOption)) {
            printHelpOn(System.err);
            System.exit(1);
        }
        if (o.has(versionOption)) {
            ProductMetadata metadata = new ProductMetadata();
            System.err.println(metadata);
            System.exit(1);
        }
        return o;
    }

    @Nonnull
    public OptionSet getOptions() {
        if (options == null) {
            try {
                options = parseOptions();
            } catch (Exception e) {
                throw Throwables.propagate(e);
            }
        }
        return options;
    }
}