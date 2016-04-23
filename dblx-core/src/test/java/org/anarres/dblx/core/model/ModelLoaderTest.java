/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.dblx.core.model;

import org.junit.Test;
import static org.junit.Assert.assertNotNull;

/**
 *
 * @author shevek
 */
public class ModelLoaderTest {

    @Test
    public void testLoader() throws Exception {
        ModelLoader loader = new ModelLoader("brainlove.complete");
        Model model = loader.load();
        assertNotNull(model);
    }

}
