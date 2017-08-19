package com.waldo.inventory.Utils;

import com.waldo.inventory.classes.Value;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

public class ValueUtils {


    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static String convertToPrettyString(double value) {
        if (value < 1/1000000) {
            value *= 1000000000;
            return String.valueOf(round(value, 6)) + "n";
        } else if (value < 1/1000) {
            value *= 1000000;
            return String.valueOf(round(value, 6)) + "µ";
        } else if (value < 1) {
            value *= 1000;
            return String.valueOf(round(value, 6)) + "m";
        } else if (value < 1000) {
            return String.valueOf(round(value, 6)) + "";
        } else if (value < 1000000) {
            value /= 1000;
            return String.valueOf(round(value, 6)) + "k";
        } else if (value < 1000000000) {
            value /= 1000000;
            return String.valueOf(round(value, 6)) + "M";
        } else {
            return String.valueOf(round(value, 6)) + "?";
        }
    }

    public static double convertUnit(double value, String unit) {
        switch (unit) {
            case "n": value /= 1000000000; break;
            case "µ": value /= 1000000; break;
            case "m": value /= 1000; break;
            case "": break;
            case "k": value *= 1000; break;
            case "M": value *= 1000000; break;
            case "G": value *= 1000000000; break;
            default: break;
        }
        return value;
    }

    public static Value findValue(String valueTxt) {
        valueTxt = valueTxt.replaceAll("\\s+","");

        // Value parts
        double value = 0.0;
        int multiplier = getMultiplier(valueTxt);
        String unit = getUnit(valueTxt);

        // Remove last characters
        int lastNdx = valueTxt.length()-1;
        while (Character.isAlphabetic(valueTxt.charAt(lastNdx)) && lastNdx >= 0) {
            valueTxt = valueTxt.substring(0, valueTxt.length()-1);
            lastNdx = valueTxt.length()-1;
        }

        // Replace any non-digit with a "."
        valueTxt = valueTxt.replaceAll("\\D+", ".");
        value = Double.valueOf(valueTxt);

        return new Value(value, multiplier, unit);
    }

    public static String getUnit(String valueTxt) {
        char last = valueTxt.charAt(valueTxt.length()-1);
        if (Character.isAlphabetic(last)) {
            if (Arrays.asList(Statics.Units.ALL).contains(String.valueOf(last))) {
                return String.valueOf(last);
            }
        }
        return "";
    }

    public static int getMultiplier(String valueTxt) {
        for (String s : Statics.UnitMultipliers.ALL) {
            if (valueTxt.contains(s)) {
                return Statics.UnitMultipliers.toMultiplier(s);
            }
        }
        return 0;
    }


}
