# TisUgly for Java

TisUgly was developed to allow teams put their module design decisions into the build process. If particular packages access other packages or modules that are off limits based on team design decisions then the build should break. TisUgly enables teams to write their design decisions about packages and modules into xUnit test cases to run with rest of your programmer test execution.

# Getting Started

The idea with TisUgly is that teams should be able to break their build based on a design decision they have made during the project. For example, if a team decided that the presentation layer should not speak to their data access layer directly then when it does the build breaks.

TisUgly has a notion of packages and modules, which are a grouping of multiple packages. A design assertion can be made on a package or module. TisUgly can be used directly in an xUnit test framework and therefore no external configuration files or tooling is needed except putting the library on your classpath.

## Example Usage

Currently, design assertions are always about what packages or modules cannot access the package or module in a design assertion. Here is some example code:

```
CodeAnalyzer analyzer = ASMAnalyzer.getCodeAnalyzer();
DesignAssertion designAssertion = DesignAssertion.createDesignAssertionOnPackage("junit.textui", analyzer);
designAssertion.dontAllowAccessTo("junit.framework");
assertFalse(designAssertion.validate());
```

## Example Usage with Modules

The same kind of design assertions can be made with modules, a group of multiple packages as a component. Here is some example code:

```
CodeAnalyzer analyzer = ASMAnalyzer.getCodeAnalyzer();
Module module = new Module("junit core");
module.addPackage("junit.textui");
module.addPackage("junit.runner");
DesignAssertion designAssertion = DesignAssertion.createDesignAssertionOnModule(module, analyzer);
designAssertion.dontAllowAccessTo("junit.framework");
assertFalse(designAssertion.validate());
```

# Upcoming Features

* Design assertions tailored for including libraries (external jars)
* Negative design assertions such as "only this package can access this module"
* Class-level design assertions
* Jar-focused design assertions
* Regular expressions in design assertions
