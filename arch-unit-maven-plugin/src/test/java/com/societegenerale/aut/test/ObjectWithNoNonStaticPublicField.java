package com.societegenerale.aut.test;

public class ObjectWithNoNonStaticPublicField {

	private int x = 10;

	protected String name = "name";

	double a = 5.3;

	static final int NUMBER = 4;

	private static final int A = 10;

	protected static final String SD = "AAAAA";

	static double Z = 10.2;

	//this field is public, but since it's final, it's OK
	public static final int ABC = 10;

}
