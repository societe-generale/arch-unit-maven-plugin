package com.societegenerale.commons.plugin.rules.classesForTests;

import java.util.Date;

public class ObjectWithJavaUtilDateReferences {

	private Date date = new Date(0, 0, 0);

	public void setDate(int d, int m, int y) {
		date.setDate(d);

		date.setMonth(m);

		date.setYear(y);

	}

}
