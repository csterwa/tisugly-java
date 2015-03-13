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
package com.gettingagile.tisugly.analyzer;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.gettingagile.tisugly.Module;

import static junit.framework.Assert.*;

public class ASMAnalyzerIntegrationTest {

    @Test
    public void shouldAllowWhenNoDependenciesFoundForOmmissions() {
        ASMAnalyzer analyzer = ASMAnalyzer.getCodeAnalyzer();
        List<String> omit = new ArrayList<String>();
        omit.add("junit/framework");
        Module module = new Module("java/util");
        module.addPackage("java/util");
        assertTrue(analyzer.isValid(module, omit));
    }

    @Test
    public void shouldFailWhenDependenciesBetweenPackagesFound() {
        ASMAnalyzer analyzer = ASMAnalyzer.getCodeAnalyzer();
        List<String> omit = new ArrayList<String>();
        omit.add("junit/framework");
        Module module = new Module("junit/textui");
        module.addPackage("junit/textui");
        assertFalse(analyzer.isValid(module, omit));
    }

}
