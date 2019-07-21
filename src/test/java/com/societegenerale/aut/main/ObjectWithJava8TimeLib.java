package com.societegenerale.aut.main;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ObjectWithJava8TimeLib {

    private ZonedDateTime zonedDateTime;

    public static String convertDateFormat(String inputStrDate, String inputFormat, String outputFormat) {
        //some parser to do something...
        DateTimeFormatter parser = DateTimeFormatter.ofPattern("");

        return "some String";
    }
}
