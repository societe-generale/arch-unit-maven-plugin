package com.societegenerale.commons.plugin.maven;

import java.io.File;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableSet;
import org.assertj.core.api.AbstractThrowableAssert;
import org.assertj.core.api.Condition;
import org.codehaus.plexus.configuration.DefaultPlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfiguration;

import static java.util.Arrays.stream;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

abstract class AbstractArchUnitMojoTest
{
    protected static ExpectedRuleFailure.Creator expectRuleFailure(String ruleDescription) {
        return new ExpectedRuleFailure.Creator(ruleDescription);
    }

    protected String getBasedir() {
        String basedir = System.getProperty("basedir");

        if (basedir == null) {
            basedir = new File("").getAbsolutePath();
        }

        return basedir;
    }

    protected void executeAndExpectViolations(ArchUnitMojo mojo, ExpectedRuleFailure... expectedRuleFailures) {
        AbstractThrowableAssert<?, ? extends Throwable> throwableAssert = assertThatThrownBy(mojo::execute);
        stream(expectedRuleFailures).forEach(expectedFailure -> {
            throwableAssert.hasMessageContaining(String.format("Rule '%s' was violated", expectedFailure.ruleDescription));
            expectedFailure.details.forEach(throwableAssert::hasMessageContaining);
        });
        throwableAssert.has(exactNumberOfViolatedRules(expectedRuleFailures.length));
    }

    private Condition<Throwable> exactNumberOfViolatedRules(final int number) {
        return new Condition<Throwable>("exactly " + number + " violated rules") {
            @Override
            public boolean matches(Throwable throwable) {
                Matcher matcher = Pattern.compile("Rule '.*' was violated").matcher(throwable.getMessage());
                int numberOfOccurrences = 0;
                while (matcher.find()) {
                    numberOfOccurrences++;
                }
                return numberOfOccurrences == number;
            }
        };
    }

    protected PlexusConfiguration buildApplyOnBlock(String packageName, String scope) {

        return buildApplyOnBlock(packageName, scope, false);
    }

    protected PlexusConfiguration buildApplyOnBlock(String packageName, String scope, Boolean aggregator) {

        PlexusConfiguration packageNameElement = new DefaultPlexusConfiguration("packageName", packageName);
        PlexusConfiguration scopeElement = new DefaultPlexusConfiguration("scope", scope);
        PlexusConfiguration aggregatorElement = new DefaultPlexusConfiguration("aggregator", aggregator.toString());
        PlexusConfiguration applyOnElement = new DefaultPlexusConfiguration("applyOn");
        applyOnElement.addChild(packageNameElement);
        applyOnElement.addChild(scopeElement);
        applyOnElement.addChild(aggregatorElement);

        return applyOnElement;
    }


    protected PlexusConfiguration buildChecksBlock(String... checks) {
        PlexusConfiguration checksElement = new DefaultPlexusConfiguration("checks");
        stream(checks).map(c -> new DefaultPlexusConfiguration("check", c)).forEach(checksElement::addChild);
        return checksElement;
    }

    public static class ExpectedRuleFailure {
        private final String ruleDescription;
        private final Set<String> details;

        private ExpectedRuleFailure(String ruleDescription, Set<String> details) {
            this.ruleDescription = ruleDescription;
            this.details = details;
        }

        public static class Creator {
            private final String ruleDescription;

            Creator(String ruleDescription) {
                this.ruleDescription = ruleDescription;
            }

            ExpectedRuleFailure ofAnyKind() {
                return withDetails();
            }

            ExpectedRuleFailure withDetails(String... detals) {
                return new ExpectedRuleFailure(ruleDescription, ImmutableSet.copyOf(detals));
            }
        }
    }
}
