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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.containsString;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.gettingagile.tisugly.analyzer.ASMAnalyzer;
import com.gettingagile.tisugly.analyzer.CodeAnalyzer;

public class WhenTeamExecutesDesignAssertionsInBuild {

    private static final String JUNIT_TEXTUI_PACKAGE_NAME = "junit.textui";
    private static final String JUNIT_FRAMEWORK_PACKAGE_NAME = "junit.framework";
    private MessageConsole console = null;
    private DesignAssertion assertion = null;
    private CodeAnalyzer analyzer = ASMAnalyzer.getCodeAnalyzer();

    @Before
    public void setUp() {
        this.console = new MessageConsole();
        this.assertion = DesignAssertion.createDesignAssertionOnPackage(JUNIT_TEXTUI_PACKAGE_NAME, analyzer);
        this.analyzer.addMessageListener(this.console);
    }

    @After
    public void tearDown() {
        this.assertion = null;
        this.console = null;
    }

    @Test
    public void shouldPrintSuccessfulDesignAssertionMessage() {
        this.assertion.validate();
        assertThat(this.console.printResult(), equalTo("All design assertions passed successfully.\n"));
    }

    @Test
    public void shouldSummarizeDesignAssertionIssues() {
        this.assertion.dontAllowAccessTo(JUNIT_FRAMEWORK_PACKAGE_NAME);
        this.assertion.validate();
        assertThat(this.console.printResult(), containsString("Summary - design assertion failures: "));
    }

    @Test
    public void shouldShowHowManyClassesInPackageFailedAssertionIssues() {
        this.assertion.dontAllowAccessTo(JUNIT_FRAMEWORK_PACKAGE_NAME);
        this.assertion.validate();
        assertThat(this.console.printResult(), containsString("design assertion failure: " + JUNIT_TEXTUI_PACKAGE_NAME
                + " accessed " + JUNIT_FRAMEWORK_PACKAGE_NAME + " package "));
    }

    @Test
    public void shouldShowHowManyClassesInModuleFailedAssertionIssues() {
        Module module = new Module("module assertion");
        module.addPackage(JUNIT_TEXTUI_PACKAGE_NAME);
        module.addPackage("junit.runner");
        DesignAssertion moduleAssertion = DesignAssertion.createDesignAssertionOnModule(module, this.analyzer);
        moduleAssertion.dontAllowAccessTo(JUNIT_FRAMEWORK_PACKAGE_NAME);
        moduleAssertion.validate();
        assertThat(this.console.printResult(), containsString("design assertion failure: " + JUNIT_TEXTUI_PACKAGE_NAME
                + " accessed " + JUNIT_FRAMEWORK_PACKAGE_NAME + " package "));
        assertThat(this.console.printResult(), containsString("design assertion failure: junit.runner accessed "
                + JUNIT_FRAMEWORK_PACKAGE_NAME + " package "));
        System.out.println(this.console.printResult());
    }

}
