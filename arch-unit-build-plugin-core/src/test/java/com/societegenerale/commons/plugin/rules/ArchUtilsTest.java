package com.societegenerale.commons.plugin.rules;

import com.societegenerale.commons.plugin.SilentLog;
import com.societegenerale.commons.plugin.utils.ArchUtils;
import com.tngtech.archunit.core.domain.JavaClasses;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * These are not great test, as they will fail when new classes are added in the code base, but acceptable for now
 */
public class ArchUtilsTest {

	//instantiating to init the static logger in ArchUtils..
	ArchUtils archUtils=new ArchUtils(new SilentLog());

	@Test
	public void shouldLoadClassesFromGivenPackage() {
		JavaClasses classes = ArchUtils.importAllClassesInPackage("./target/classes/", "com/societegenerale/commons/plugin/model");

				

		long noOfClassesInPackage = classes.stream().count();

		assertThat(noOfClassesInPackage).isEqualTo(3);
	}

	@Test
	public void shouldLoadAllClassesWhenGivenPakageDoesntExist() {
		JavaClasses classes = ArchUtils.importAllClassesInPackage("./target/classes", "someNotExistingFolder");

		long noOfClasses = classes.stream().filter(it -> !it.isInnerClass()).count();

		assertThat(noOfClasses).isEqualTo(22);

	}

}
