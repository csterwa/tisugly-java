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

import java.util.ArrayList;
import java.util.List;

import com.gettingagile.tisugly.analyzer.ASMAnalyzer;
import com.gettingagile.tisugly.analyzer.CodeAnalyzer;

public class DesignAssertion {

    private Module assertionPackages = null;
    private CodeAnalyzer analyzer = null;
    private List<String> packagesNotAllowedAccessTo = new ArrayList<String>();
	private String description = null;

	private DesignAssertion(Module module, CodeAnalyzer analyzer, String desc) {
		this.assertionPackages = module;
		this.analyzer = analyzer;
		this.description  = desc;
	}
	
    public static DesignAssertion createDesignAssertionOnPackage(String pkgName, CodeAnalyzer analyzer) {
        return new DesignAssertion(createModuleFromPackageName(pkgName), analyzer, null);
    }

    public static DesignAssertion createDesignAssertionOnPackage(String pkgName) {
    	return new DesignAssertion(createModuleFromPackageName(pkgName), ASMAnalyzer.getCodeAnalyzer(), null);
    }
    
    public static DesignAssertion createDesignAssertionOnModule(Module module, CodeAnalyzer analyzer) {
    	return new DesignAssertion(module, analyzer, null);
    }
    
    public static DesignAssertion createDesignAssertionOnModule(Module module) {
    	return new DesignAssertion(module, ASMAnalyzer.getCodeAnalyzer(), null);
    }
    
    public static DesignAssertion createDesignAssertionOnPackageWithDescription(
    		String pkgName, ASMAnalyzer codeAnalyzer, String desc) {
    	return new DesignAssertion(createModuleFromPackageName(pkgName), codeAnalyzer, desc);
    }
    
    private static Module createModuleFromPackageName(String pkgName) {
    	Module module = new Module(pkgName);
    	module.addPackage(pkgName);
    	return module;
    }
    
    public void dontAllowAccessTo(String pkgName) {
        this.packagesNotAllowedAccessTo.add(pkgName);
    }

    public boolean validate() {
        return this.analyzer.isValid(assertionPackages, this.packagesNotAllowedAccessTo);
    }

    public void dontAllowAccessTo(Module module) {
        for (String pkgName : module.getPackages()) {
            this.packagesNotAllowedAccessTo.add(pkgName);
        }
    }

	public String getDescription() {
		return this.description;
	}

}
