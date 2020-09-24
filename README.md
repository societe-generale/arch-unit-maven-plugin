# ArchUnit Maven plugin

[![Build Status](https://travis-ci.org/societe-generale/arch-unit-maven-plugin.svg?branch=master)](https://travis-ci.org/societe-generale/arch-unit-maven-plugin)
[![Coverage Status](https://coveralls.io/repos/github/societe-generale/arch-unit-maven-plugin/badge.svg?branch=master)](https://coveralls.io/github/societe-generale/arch-unit-maven-plugin?branch=master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.societegenerale.commons/arch-unit-maven-plugin/badge.svg?style=plastic)](https://maven-badges.herokuapp.com/maven-central/com.societegenerale.commons/arch-unit-maven-plugin)


**ArchUnit Maven plugin** is a simple Maven wrapper around [ArchUnit](https://github.com/TNG/ArchUnit) that enables you to easily make sure that all your projects follow the same architecture rules.

ArchUnit is an awesome tool to implement some tricky checks that other static analysis tools can't implement at all : these checks run as unit tests, and guarantee that build will break if any violation is introduced, giving developers very fast feedback on their work. 

Now, imagine you have 3 architecture rules, and you want 10 repositories to apply them... By default, you'll have no choice but copy/paste the rules (ie unit tests) in the 10 repositories. And if somebody removes one of these tests later, it may take months to realize it. 

ArchUnit Maven plugin comes with a couple of rules out of the box (but you can add your own) : all you need is a bit of Maven config in your root pom.xml to make sure the rules you want to enforce are actually run at every build ! it then becomes very easy to have a proper governance on dozens of repositories.
 
And if you want to have regular reporting on which projects are using ArchUnit Maven plugin, with which rules, you can use our [GitHub crawler](https://github.com/societe-generale/github-crawler) 

## How to use ArchUnit Maven plugin ? 

Add below plugin in your root pom.xml : all available ```<rule>``` are mentioned, so remove the ones you don't need. If needed, specify in ```<projectPath>``` property the path of the directory where the rules will be asserted 

```xml
<plugin>
	<groupId>com.societegenerale.commons</groupId>
	<artifactId>arch-unit-maven-plugin</artifactId>
	<version>2.6.1</version>
	<configuration>
		
		<rules>
			<preConfiguredRules>
				<rule>com.societegenerale.commons.plugin.rules.NoStandardStreamRuleTest</rule>
				<rule>com.societegenerale.commons.plugin.rules.NoJunitAssertRuleTest</rule>
				<rule>com.societegenerale.commons.plugin.rules.NoJodaTimeRuleTest</rule>
				<rule>com.societegenerale.commons.plugin.rules.NoJavaUtilDateRuleTest</rule>
				<rule>com.societegenerale.commons.plugin.rules.NoPowerMockRuleTest</rule>
				<rule>com.societegenerale.commons.plugin.rules.NoPrefixForInterfacesRuleTest</rule>
				<rule>com.societegenerale.commons.plugin.rules.NoPublicFieldRuleTest</rule>
				
				<!-- you may want to use one of the below rules, but not both at same time -->
				<rule>com.societegenerale.commons.plugin.rules.NoTestIgnoreRuleTest</rule>
				<rule>com.societegenerale.commons.plugin.rules.NoTestIgnoreWithoutCommentRuleTest</rule>

				<rule>com.societegenerale.commons.plugin.rules.NoInjectedFieldTest</rule>
				<rule>com.societegenerale.commons.plugin.rules.NoAutowiredFieldTest</rule>
			</preConfiguredRules>
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
    <dependencies>
       <dependency>
           <!-- 
                A version of the core jar is included by default, but don't hesitate 
                to upgrade to a later one if you need :
                we will be able to add rules and behavior in arch-unit-build-plugin-core
                without releasing a new version of arch-unit-maven-plugin
            -->
            <groupId>com.societegenerale.commons</groupId>
            <artifactId>arch-unit-build-plugin-core</artifactId>
            <version>SOME_CORE_VERSION_GREATER_THAN_THE_PLUGIN</version>
       </dependency>
    </dependencies>
</plugin>
```

## Dependency on arch-unit-build-plugin-core

Since v2.3.0, a lot of the original code from this repository has been moved to [https://github.com/societe-generale/arch-unit-build-plugin-core](https://github.com/societe-generale/arch-unit-build-plugin-core) , so that we can build a Maven or a Gradle plugin on top of a (common) [core logic](https://github.com/societe-generale/arch-unit-build-plugin-core/tree/arch_unit_build_plugin_core_2.3.0). 

Therefore, since then, this repository is greatly simplified as it contains only Maven specific code and the adapters between Maven world and arch-unit-build-plugin-core. 

This Maven plugin ships with a default version of arch-unit-build-plugin-core, but if new rules are added in arch-unit-build-plugin-core, you'll need to declare it as a dependency (as in the example above) to benefit from them. As long as there's no major change in the core API that would force us to update the Maven plugin, we won't have to release a new version of the plugin.  


## Adding custom rules

### Add a single rule, for a given project

If you need to add a rule that is specific to a project, just add a regular ArchUnit test, as described on ArchUnit's homepage. You'll need to import yourself archUnit dependency, so please make sure to use the same version as in the plugin, otherwise there may be strange behaviors. ArchUnit Maven plugin will not be involved. 

### Add a rule, and share it across projects

You can share custom rules by packaging the respective classes containing the rules in a jar.
Such classes can either contain fields of type `ArchRule` or methods taking a single parameter of type
`JavaClasses` (compare JUnit support at https://www.archunit.org/userguide/html/000_Index.html#_junit_4_5_support).
The classes those rules will be checked against are configured within the plugin.
Add the jar containing your classes to your classpath (by mentioning it as a plugin's dependency for instance), 
and then mention the ```<rule>``` with its fully qualified name the ```<rules>``` block, 
so that ArchUnit Maven plugin can instantiate it and run it. 

So your config would become something like :

```xml
<plugin>
  <groupId>com.societegenerale.commons</groupId>
  <artifactId>arch-unit-maven-plugin</artifactId>
  <version>2.6.1</version>
  <configuration>
    
    <!-- optional - you can exclude classes that have a path containing any of the mentioned paths -->
    <excludedPaths>
        <excludedPath>my/package/to/exclude</excludedPath>
    </excludedPaths>

    <rules>
       <!-- using a rule available out of the box... -->
       <preConfiguredRules>
            <rule>com.societegenerale.commons.plugin.rules.NoJunitAssertRuleTest</rule>
       </preConfiguredRules>
       <!-- ... and a custom one, coming from a dependency of the plugin -->
       <configurableRules>
            <configurableRule>
                <rule>com.tngtech.archunit.library.GeneralCodingRules</rule>
                <applyOn>
                    <packageName>com.myproject.mypackage</packageName>
                    <!-- scope can be "main" or "test" -->
                    <scope>main</scope>
                </applyOn>

                <checks>
       	 	        <!-- otherwise you can specify either field or method names here. If no checks block is defined, all are executed -->
       	 		    <check>NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS</check>
       	 	    </checks>
            </configurableRule>
       </configurableRules>

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
  <dependencies>
    <dependency>
        <!-- dependency contains com.mycompany.rules.CustomArchRule -->
        <groupId>com.myCompany</groupId>
        <artifactId>custom-quality-rules</artifactId>
        <version>1.0.0</version>
    </dependency>
  </dependencies>
</plugin>
```

## Skipping rules

In case of adding **ArchUnit Maven plugin** to a legacy code base you might not be able to enforce all rules immediately.
You may add **ArchUnit Maven plugin** to a parent POM of your Maven artifacts and still be able to skip execution in child
projects by using the skip-configuration.

### Skipping just one rule

Most of the time you add a new rule to your **ArchUnit Maven plugin** (which might immediately be available in all of
your artifacts) but the issues **ArchUnit** reveals using your rule may not be fixed as soon as you need to release the
next version of your artifacts.

To skip a rule temporarily, configure it like

```xml
<properties>
  <archunit.customrule.skip>false</archunit.customrule.skip>
</properties>
<!-- and inside your plugin's configuration -->
<configurableRule>
  <rule>com.mycompany.rules.CustomArchRule</rule>
  <skip>${archunit.customrule.skip}</skip>
  <!-- detailed configuration omitted -->
</configurableRule>
```

and then your slow-to-be-fixed-artifacts my override the property `<archunit.customrule.skip>true</archunit.customrule.skip>`

### Skipping the whole plugin

If even skipping certain rules doesn't fit your needs, configure to skip the whole plugin execution:

```xml
<properties>
  <archunit.skip>false</archunit.skip>
</properties>
<!-- and then inside the ArchUnit Maven plugin -->
  <configuration>
    <skip>${archunit.skip}</skip>
  </configuration>
```

and then you can switch the parameter `archunit.skip` either on runtime (via `-Darchunit.skip=true`) or statically in child modules.

### ArchUnit advanced configuration

Since v2.2.0, you can benefit from ArchUnit advanced configuration, as the plugin can find `archunit.properties` file. More infos in [ArchUnit's user guide](https://www.archunit.org/userguide/html/000_Index.html#_advanced_configuration)

## Excluding paths

Since v2.4.0, configuration can take an optional `excludedPaths` element. All classes that have a location that contains one the mentioned Strings will be excluded from the ArchUnit checks : can be useful in case some classes are generated (Lombok, Mapstruct, ..) and you have little or no control on what gets generated.

Remember that ArchUnit operates on the **compiled code** : so we can't exclude something like `generated-sources`. However, if these generated classes are part of a specific package, we can exclude that package.

See [ExclusionImportOption.java](https://github.com/societe-generale/arch-unit-build-plugin-core/blob/2a6f5d009b96a7921bf2de65fcc0aad85edc006a/src/main/java/com/societegenerale/commons/plugin/utils/ExclusionImportOption.java) for details on the (very simple) logic. 



## Contribute !

If you want to make changes in the Maven specific behavior, don't hesitate to open on issue on this repository and/or create a pull request.

If you don't want to package your rules separately and/or feel they could be useful to others, we can make your rules part of arch-unit-build-plugin-core, so that they can be used out of the box by anyone : don't hesitate to send us a pull request ! have a look at the [code](https://github.com/societe-generale/arch-unit-build-plugin-core/tree/arch_unit_build_plugin_core_2.3.0/src/main/java/com/societegenerale/commons/plugin/rules), it's very easy to add one.

## Official maintainers

- Arpit Garg 
- Vincent Fuchs
