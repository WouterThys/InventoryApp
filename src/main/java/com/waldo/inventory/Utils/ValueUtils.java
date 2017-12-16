package com.waldo.inventory.Utils;

import com.waldo.inventory.classes.Value;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ValueUtils {

    private final static char[] PREFIX_TEST_ARRAY = {'f', 'p', 'n', 'u', 'µ', 'm', 'k', 'K', 'M', 'G', 'T'};
    private final static int[] PREFIX_EXP_ARRAY = {-15, -12, -9, -6, -6, -3, 3, 3, 6, 9, 12};
    private final static int PREFIX_OFFSET = 5;
    private final static String[] PREFIX_ARRAY = {"f", "p", "n", "µ", "m", "", "k", "M", "G", "T"};

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static String convert(BigDecimal value, int dp) {

        double val = value.doubleValue();

        // If the value is zero, then simply return 0 with the correct number of dp
        if (val == 0) return String.format("%." + dp + "f", 0.0);

        // If the value is negative, make it positive so the log10 works
        double posVal = (val<0) ? -val : val;
        double log10 = Math.log10(posVal);

        // Determine how many orders of 3 magnitudes the value is
        int count = (int) Math.floor(log10/3);

        // Calculate the index of the prefix symbol
        int index = count + PREFIX_OFFSET;

        // Scale the value into the range 1<=val<1000
        val /= Math.pow(10, count * 3);

        if (index >= 0 && index < PREFIX_ARRAY.length) {
            // If a prefix exists use it to create the correct string
            return String.format("%." + dp + "f%s", val, PREFIX_ARRAY[index]);
        } else {
            // If no prefix exists just make a string of the form 000e000
            return String.format("%." + dp + "fe%d", val, count * 3);
        }
    }

    public static BigDecimal parse(String str)
    {
        return parse(str.toCharArray());
    }

    public static BigDecimal parse(char[] chars) throws NumberFormatException
    {
        int exponent = 0;
        BigDecimal value = BigDecimal.ZERO;

        boolean gotChar = false;      // Set to true once any non-whitespace, or minus character has been found
        boolean gotMinus = false;     // Set to true once a minus character has been found
        boolean gotDP = false;        // Set to true once a decimal place character has been found
        boolean gotPrefix = false;    // Set to true once a prefix character has been found
        boolean gotDigit = false;     // Set to true once a digit character has been found

        // Search for start of string
        int start = 0;
        while (start < chars.length) {
            if (chars[start] != ' ' && chars[start] != '\t') break;
            start ++;
        }

        if (start == chars.length) throw new NumberFormatException("Empty string");

        // Search for end of string
        int end = chars.length - 1;
        while (end >= 0) {
            if (chars[end] != ' ' && chars[end] != '\t') break;
            end --;
        }

        // Iterate through characters
        CharLoop: for (int c=start ; c<=end ; c++)
        {
            // Check for a minus symbol
            if (chars[c] == '-')
            {
                if (gotChar) throw new NumberFormatException("Can only have minus symbol at the start");
                if (gotMinus) throw new NumberFormatException("Too many minus symbols");
                gotMinus = true;
                continue CharLoop;
            }

            gotChar = true;

            // Check for a numerical digit
            if (chars[c] >= '0' && chars[c] <= '9')
            {
                if (gotPrefix || gotDP) exponent --;
                if (gotPrefix && gotDP) throw new NumberFormatException("Cannot have digits after prefix when number includes decimal point");
                value = value.multiply(BigDecimal.TEN);
                value = value.add(BigDecimal.valueOf(chars[c] - '0'));
                gotDigit = true;
                continue CharLoop;
            }

            // Check for a decimal place
            if (chars[c] == '.')
            {
                if (gotDP) throw new NumberFormatException("Too many decimal points");
                if (gotPrefix) throw new NumberFormatException("Cannot have decimal point after prefix");
                gotDP = true;
                continue CharLoop;
            }

            // Check for a match with a prefix character
            for (int p=0 ; p<PREFIX_TEST_ARRAY.length ; p++)
            {
                if (PREFIX_TEST_ARRAY[p] == chars[c])
                {
                    if (gotPrefix) throw new NumberFormatException("Too many prefixes");
                    exponent += PREFIX_EXP_ARRAY[p];
                    gotPrefix = true;
                    continue CharLoop;
                }
            }

            // All other characters are invalid
            throw new NumberFormatException("Invalid character '" + chars[c] + "'");
        }

        // Check if any digits were found
        if (!gotDigit) throw new NumberFormatException("No digits");

        // Apply negation if required
        if (gotMinus) value = value.negate();

        return value.multiply(BigDecimal.valueOf(Math.pow(10, exponent)));
    }


    public static Value findValue(String valueTxt) {
        valueTxt = valueTxt.replaceAll("\\s+","");

        // Value parts
        double value = 0.0;
        int multiplier = getMultiplier(valueTxt);
        String unit = getUnit(valueTxt);

        // Remove last characters
        int lastNdx = valueTxt.length()-1;
        while (Character.isAlphabetic(valueTxt.charAt(lastNdx))) {
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

    public static List<Integer> exponents(BigDecimal minValue, BigDecimal maxValue) {
        List<Integer> exponents = new ArrayList<>();

        int maxPrecision = maxValue.precision();
        int maxScale = maxValue.scale();
        int maxExponent = maxPrecision - maxScale - 1;

        int minPrecision = minValue.precision();
        int minScale = minValue.scale();
        int minExponent = minPrecision - minScale - 1;

        if (maxExponent > minExponent) {
            for (int i = minExponent; i <= maxExponent; i++) {
                exponents.add(i);
            }
        }

        return exponents;
    }
}
