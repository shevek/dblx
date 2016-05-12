/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.dblx.ui;

import com.google.auto.service.AutoService;
import org.anarres.dblx.core.config.AppConfiguration;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author shevek
 */
@AutoService(AppConfiguration.Provider.class)
@Configuration
public class ModuleConfiguration extends AppConfiguration.ProviderSupport {

}
