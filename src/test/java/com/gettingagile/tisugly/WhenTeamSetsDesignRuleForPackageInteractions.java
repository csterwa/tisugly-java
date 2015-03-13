/*
 * Copyright 2009 Chris Sterling Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. You may obtain a copy of the 
 * License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the 
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language governing permissions and 
 * limitations under the License. 
 */
package com.gettingagile.tisugly;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.gettingagile.tisugly.analyzer.ASMAnalyzer;

public class WhenTeamSetsDesignRuleForPackageInteractions {

    private static final String DESIGN_ASSERTION_DESCRIPTION = "Design assertion for junit.textui package.";
	private DesignAssertion designAssertion = null;

    @Before
    public void setUp() {
        this.designAssertion = DesignAssertion.createDesignAssertionOnPackage("junit.textui", ASMAnalyzer.getCodeAnalyzer());
    }

    @After
    public void tearDown() {
        this.designAssertion = null;
    }

    @Test
    public void shouldNotAllowAccessFromPackageToExceptionPackage() {
        designAssertion.dontAllowAccessTo("junit.framework");
        assertFalse(designAssertion.validate());
    }

    @Test
    public void shouldAllowAccessFromPackageToPackageWithNoException() {
        assertTrue(designAssertion.validate());
    }

    @Test
    public void shouldNotAllowAccessFromPackageToExceptionModule() {
        Module module = new Module("junit core");
        module.addPackage("junit.framework");
        module.addPackage("junit.runner");
        designAssertion.dontAllowAccessTo(module);
        assertFalse(designAssertion.validate());
    }
    
    @Test
    public void shouldAllowDescriptionOnDesignAssertion() {
        this.designAssertion = DesignAssertion.createDesignAssertionOnPackageWithDescription("junit.textui", ASMAnalyzer.getCodeAnalyzer(), DESIGN_ASSERTION_DESCRIPTION);
        assertEquals(DESIGN_ASSERTION_DESCRIPTION, this.designAssertion.getDescription());
    }

}
