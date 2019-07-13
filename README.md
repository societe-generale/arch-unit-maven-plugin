# ArchUnit Maven plugin

[![Build Status](https://travis-ci.org/societe-generale/arch-unit-maven-plugin.svg?branch=master)](https://travis-ci.org/societe-generale/arch-unit-maven-plugin)
[![Coverage Status](https://coveralls.io/repos/github/societe-generale/arch-unit-maven-plugin/badge.svg?branch=master)](https://coveralls.io/github/societe-generale/arch-unit-maven-plugin?branch=master)

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
	<version>2.1.0</version>
	<configuration>
		<projectPath>${project.basedir}/target</projectPath>
		<rules>
			<preConfiguredRules>
				<rule>com.societegenerale.commons.plugin.rules.NoStandardStreamRuleTest</rule>
				<rule>com.societegenerale.commons.plugin.rules.NoJunitAssertRuleTest</rule>
				<rule>com.societegenerale.commons.plugin.rules.NoJodaTimeRuleTest</rule>
				<rule>com.societegenerale.commons.plugin.rules.NoJavaUtilDateRuleTest</rule>
				<rule>com.societegenerale.commons.plugin.rules.NoPowerMockRuleTest</rule>
				<rule>com.societegenerale.commons.plugin.rules.NoPrefixForInterfacesRuleTest</rule>
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
</plugin>
```

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
  <version>2.1.0</version>
  <configuration>
    <projectPath>${project.basedir}/target</projectPath>
    <rules>
       <!-- using a rule available out of the box... -->
       <preConfiguredRules>
            <rule>com.societegenerale.commons.plugin.rules.NoJunitAssertRuleTest</rule>
       </preConfiguredRules>
       <!-- ... and a custom one, coming from a dependency of the plugin-->
       <configurableRules>
       	 <configurableRule>
       	 	<rule>com.mycompany.rules.CustomArchRule</rule>
       	 	<applyOn>
       	 		<packageName>com.myproject.mypackage</packageName>
       	 		<scope>test</scope>
       	 	</applyOn>
            <!-- if the checks block is missing, all rules will be evaluated -->
       	 	<checks>
       	 	    <!-- otherwise you can specify either field or method names here -->
       	 		<check>customArchRule</check>
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

## Contribute !

If you don't want to package your rules separately and/or feel they could be useful to others, we can make your rules part of default ArchUnit Maven plugin package, so that they can be used out of the box by anyone : don't hesitate to send us a pull request ! have a look at the [code](./src/main/java/com/societegenerale/commons/plugin/rules), it's very easy to add one.

## Official maintainers

- Arpit Garg 
- Vincent Fuchs

