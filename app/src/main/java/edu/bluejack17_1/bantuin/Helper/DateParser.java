package edu.bluejack17_1.bantuin.Helper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DateParser {
    public static String parse(Calendar temp, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        return sdf.format(temp.getTime());
    }
}