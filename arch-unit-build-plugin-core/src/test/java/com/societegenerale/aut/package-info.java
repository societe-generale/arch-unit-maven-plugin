/**
 * This package contains the AUT (application under test) which the tests run against. These files get compiled via
 * Maven's {@code testCompile} and get afterwards copied to a certain directory.
 * <p>
 * {@link com.societegenerale.aut.main} gets copied to {@code target/aut-target/classes} whereas
 * {@link com.societegenerale.aut.test} gets copied to {@code target/aut-target/test-classes}.
 * <p>
 * This allows the tests to run just against these set of classes and not against themselves, too.
 */
package com.societegenerale.aut;