/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.dblx.core;

import java.io.IOException;
import javax.annotation.Nonnull;
import org.anarres.dblx.core.config.AbstractSpringArguments;

/**
 *
 * @author shevek
 */
public class Arguments extends AbstractSpringArguments {

    public Arguments(@Nonnull String[] args) throws IOException {
        super(args);
    }
}
