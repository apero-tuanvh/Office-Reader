package com.apero.reader.office.fc.util;

import android.util.Log;

public class IntUtils {
    private final static String TAG = "IntUtils";

    public static int parseIntFromDouble(String doubleValue) {
        try {
            return (int) Double.parseDouble(doubleValue);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    public static int stringToInt(String number) {
        try {
            return Integer.parseInt(number);
        } catch (NumberFormatException e) {
            //not int
            Log.e(TAG, "stringToInt: " + number);
        }
        // check if double
        try {
            return (int) Double.parseDouble(number);
        } catch (NumberFormatException e) {
            //not double
            Log.e(TAG, "stringToInt double " + number);
        }
        //check if float
        try {
            return (int) Float.parseFloat(number);
        } catch (NumberFormatException e) {
            //not float
            Log.e(TAG, "stringToInt float " + number);
        }
        return 0;
    }
}
