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

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.gettingagile.tisugly.analyzer.ASMAnalyzer;
import com.gettingagile.tisugly.analyzer.CodeAnalyzer;

public class WhenTeamSetsDesignRuleForModuleInteractions {

    private DesignAssertion designAssertion = null;

    @Before
    public void setUp() {
        CodeAnalyzer analyzer = ASMAnalyzer.getCodeAnalyzer();
        Module module = new Module("junit core");
        module.addPackage("junit.textui");
        module.addPackage("junit.runner");
        this.designAssertion = DesignAssertion.createDesignAssertionOnModule(module, analyzer);
    }

    @After
    public void tearDown() {
        this.designAssertion = null;
    }

    @Test
    public void shouldNotAllowAccessFromModuleToExceptionPackage() {
        designAssertion.dontAllowAccessTo("junit.framework");
        assertFalse(designAssertion.validate());
    }

    @Test
    public void shouldAllowAccessFromModuleToPackageWithNoException() {
        assertTrue(designAssertion.validate());
    }

}
