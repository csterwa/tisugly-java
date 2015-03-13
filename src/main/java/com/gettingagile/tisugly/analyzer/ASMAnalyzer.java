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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.depend.DependencyVisitor;

import com.gettingagile.tisugly.Module;
import com.gettingagile.tisugly.event.MessageListener;

public class ASMAnalyzer implements CodeAnalyzer {

    private String[] packageNames;
    private Map<String, Map<String, Integer>> globals;
    private static ASMAnalyzer INSTANCE = new ASMAnalyzer();
    private List<MessageListener> messageListeners = new ArrayList<MessageListener>();
    private final DependencyVisitor dependencyVisitor = new DependencyVisitor();

    public static ASMAnalyzer getCodeAnalyzer() {
        return INSTANCE;
    }

    private ASMAnalyzer() {
        URL[] urls = ((URLClassLoader) Thread.currentThread().getContextClassLoader()).getURLs();

        for (URL url : urls) {
            loadClassesFromClasspath(new File(url.getFile()));
        }

        globals = dependencyVisitor.getGlobals();
        Set<String> classPackages = dependencyVisitor.getPackages();

        packageNames = classPackages.toArray(new String[classPackages.size()]);
        Arrays.sort(packageNames);
    }

    private void loadClassesFromClasspath(File classpathElement) {
        try {
            if (classpathElement.isDirectory()) {
                loadClassesFromClasspathDirectory(classpathElement);
            } else {
                loadClassesFromClasspathArchiveFile(classpathElement);
            }
        } catch (IOException e) {
        }
    }

    private void loadClassesFromClasspathArchiveFile(File classpathElement) throws IOException {
        ZipFile f = new ZipFile(classpathElement.getAbsolutePath());
        Enumeration<? extends ZipEntry> en = f.entries();
        while (en.hasMoreElements()) {
            ZipEntry e = en.nextElement();
            String name = e.getName();
            if (name.endsWith(".class")) {
                new ClassReader(f.getInputStream(e)).accept(dependencyVisitor, 0);
            }
        }
    }

    private void loadClassesFromClasspathDirectory(File classpathElement) throws IOException, FileNotFoundException {
        Collection<File> classes = listClassesInDirectory(classpathElement);
        for (File clazz : classes) {
            new ClassReader(new FileInputStream(clazz)).accept(dependencyVisitor, 0);
        }
    }

    @SuppressWarnings("unchecked")
    private Collection<File> listClassesInDirectory(File dir) {
        return FileUtils.listFiles(dir, new String[] { "class" }, true);
    }

    public boolean isValid(Module assertionPackages, List<String> packagesNotAllowedAccessTo) {
        int numberOfDesignAssertionFailures = 0;

        for (String pkgName : assertionPackages.getPackages()) {
            String assertionPackageWithSlashes = replacePeriodInPackageNamesToSlashForASMCodeAnalyzer(pkgName);
            numberOfDesignAssertionFailures = countNumberOfDesignAssertionFailures(pkgName, packagesNotAllowedAccessTo,
                    assertionPackageWithSlashes);
        }

        return isAssertionSuccessful(numberOfDesignAssertionFailures);
    }

    private int countNumberOfDesignAssertionFailures(String assertionPackage, List<String> packagesNotAllowedAccessTo,
            String assertionPackageWithSlashes) {
        int numberOfDesignAssertionFailures = 0;

        for (String pkgName : packagesNotAllowedAccessTo) {
            int countOfPackageAccesses = countHowManyTimesAssertionPackageAccessesDisallowedPackage(
                    assertionPackageWithSlashes, pkgName);

            if (countOfPackageAccesses > 0) {
                numberOfDesignAssertionFailures += countOfPackageAccesses;
                logMessage("design assertion failure: " + assertionPackage + " accessed " + pkgName + " package "
                        + countOfPackageAccesses + " times");
            }
        }
        return numberOfDesignAssertionFailures;
    }

    private boolean isAssertionSuccessful(int numberOfDesignAssertionFailures) {
        if (numberOfDesignAssertionFailures > 0) {
            logMessage("Summary - design assertion failures: " + numberOfDesignAssertionFailures);
            return false;
        } else {
            logMessage("All design assertions passed successfully.");
            return true;
        }
    }

    private int countHowManyTimesAssertionPackageAccessesDisallowedPackage(String assertionPackage,
            String disallowedPkgName) {
        Map<String, Integer> map = globals.get(assertionPackage);
        return map == null ? 0 : map.get(replacePeriodInPackageNamesToSlashForASMCodeAnalyzer(disallowedPkgName));
    }

    private String replacePeriodInPackageNamesToSlashForASMCodeAnalyzer(String pkgName) {
        return pkgName.replaceAll("\\.", "/");
    }

    private void logMessage(String message) {
        for (MessageListener listener : this.messageListeners) {
            listener.logMessage(message);
        }
    }

    public void addMessageListener(MessageListener listener) {
        if (!this.messageListeners.contains(listener)) {
            this.messageListeners.add(listener);
        }
    }

}
