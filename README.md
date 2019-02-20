# arch-unit-maven-plugin

[![Build Status](https://travis-ci.org/societe-generale/arch-unit-maven-plugin.svg?branch=master)](https://travis-ci.org/societe-generale/arch-unit-maven-plugin)

**arch-unit-maven-plugin** is a wrapper around [ArchUnit](https://github.com/TNG/ArchUnit). **_ArchUnit_** is a free, simple and extensible library for checking the architecture of your Java code. That is, ArchUnit can check dependencies between packages and classes, layers and slices, check for cyclic dependencies and more. It does so by analyzing given Java bytecode, importing all classes into a Java code structure. ArchUnit's main focus is to automatically test architecture and coding rules, using any plain Java unit testing framework.


## How to use ?

Add below plugin in your project to enable architecture test. Specify in **`projectPath`** property the path of the directory where the rules will be asserted 

```xml
<plugin>
  <groupId>com.societegenerale.commons</groupId>
  <artifactId>arch-unit-maven-plugin</artifactId>
  <version>1.0.0</version>
  <configuration>
    <projectPath>${project.basedir}/target</projectPath>
    <rules>
       <rule>com.societegenerale.commons.plugin.rules.NoStandardStreamRuleTest</rule>
       <rule>com.societegenerale.commons.plugin.rules.NoJunitAssertRuleTest</rule>
       <rule>com.societegenerale.commons.plugin.rules.NoJodaTimeRuleTest</rule>
       <rule>com.societegenerale.commons.plugin.rules.NoPowerMockRuleTest</rule>
       <rule>com.societegenerale.commons.plugin.rules.NoPrefixForInterfacesRuleTest</rule>
       <!-- you may want to use one of the below rules, but not both at same time -->
       <rule>com.societegenerale.commons.plugin.rules.NoTestIgnoreRuleTest</rule>
       <rule>com.societegenerale.commons.plugin.rules.NoTestIgnoreWithoutCommentRuleTest</rule>
    </rules>
  </configuration>
  <executions>
    <execution>
      <phase>test</phase>
      <goals>
        <goal>arch-test</goal>
      </goals>
    </execution>
  </executions>
</plugin>
```

## Adding custom rules

If you want to add your custom test, you have to do the following

### Add following maven dependency in your project pom

```xml
<dependency>
    <groupId>com.tngtech.archunit</groupId>
    <artifactId>archunit</artifactId>
    <version>0.5.0</version>
    <scope>test</scope>
</dependency>
```

> Make sure you use the same version as in the plugin - we may face incorrect behavior otherwise

### Create test file in **`src`** or **`test`** folder

**Example**

```java
public class NoJavaUtilLoggingRule {
  public void execute(String path) {
        noClasses().should(USE_JAVA_UTIL_LOGGING).check(new ClassFileImporter().importPath(Paths.get(path)));
  }
}
```

### Apply the rule in **`pom.xml`**

**Example**

```xml
<rule>NoJavaUtilLoggingRule</rule>
 ```
###  Build your project

```
mvn clean install
```
