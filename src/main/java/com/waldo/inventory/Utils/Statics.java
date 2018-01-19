package com.waldo.inventory.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Statics {

    public static class ItemAmountTypes {
        public static final int NONE = 0;
        public static final int MAX = 1;
        public static final int MIN = 2;
        public static final int EXACT = 3;
        public static final int APPROXIMATE = 4;
    }

    public static class ItemOrderStates {
        public static final int NONE = 0;
        public static final int ORDERED = 1;
        public static final int RECEIVED = 2;
        public static final int PLANNED = 3;
    }

    public static class LogTypes {
        public static final int INFO = 0;
        public static final int DEBUG = 1;
        public static final int WARN = 2;
        public static final int ERROR = 3;
    }

    public static final String[] Alphabet = {
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
            "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    public static int indexOfAlphabet(String letter) {
        return Arrays.asList(Alphabet).indexOf(letter);
    }


    public enum ValueMultipliers {
        f("f", -15),
        p("p", -12),
        n("n", -9),
        u("µ", -6),
        m("m", -3),
        x("",  0),
        k("k", 3),
        M("M", 6),
        G("G", 9),
        T("T", 12);

        private final String string;
        private final int multiplier;
        ValueMultipliers(String string, int multiplier) {
            this.string = string;
            this.multiplier = multiplier;
        }

        @Override
        public String toString() {
            return string;
        }


        public int getMultiplier() {
            return multiplier;
        }

        public static ValueMultipliers fromInt(int multiplier) {
            switch (multiplier) {
                case -15: return f;
                case -12: return p;
                case -9 : return n;
                case -6 : return u;
                case -3 : return m;
                default :
                case 0  : return x;
                case 3  : return k;
                case 6  : return M;
                case 9  : return G;
                case 12:  return T;
            }
        }

        public static ValueMultipliers[] valuesFromTo(ValueMultipliers m1, ValueMultipliers m2) {
            List<ValueMultipliers> multipliers = new ArrayList<>();
            for (ValueMultipliers m : values()) {
                if (m.multiplier >= m1.multiplier && m.multiplier <= m2.multiplier) {
                    multipliers.add(m);
                }
            }
            ValueMultipliers[] multiplierArray = new ValueMultipliers[multipliers.size()];
            return multipliers.toArray(multiplierArray);
        }
    }

    public enum ValueUnits {
        Unknown (""),
        R ("\u2126"),
        C ("F"),
        L ("H"),
        V ("V"),
        I ("A");

        private final String unitString;
        ValueUnits(String unitString) {
            this.unitString = unitString;
        }

        @Override
        public String toString() {
            return unitString;
        }

        public static ValueUnits fromString(String unit) {
            if (unit != null) {
                switch (unit.toUpperCase()) {
                    default:
                    case "U":
                    case "UNKNOWN":
                    case "": return Unknown;
                    case "O":
                    case "OHM":
                    case "\u2126": return R;
                    case "FARAD":
                    case "F": return C;
                    case "HENRY":
                    case "H": return L;
                    case "VOLT":
                    case "VOLTAGE":
                    case "V": return V;
                    case "AMPERES":
                    case "A": return I;
                }
            }
            return Unknown;
        }
    }

    public static class CodeLanguage {
        public static final String Unknown = "";
        public static final String C = "C";
        public static final String Cs = "C#";
        public static final String Cpp = "C++";
        public static final String Java = "Java";
        public static final String Python = "Python";

        public static final String[] All = {Unknown, C, Cs, Cpp, Java, Python };
    }

    public static class ProjectTypes {
        public static final String Unknown = "";
        public static final String Code = "Code";
        public static final String Pcb = "Pcb";
        public static final String Other = "Other";

        public static final String[] All  = {Unknown, Code, Pcb, Other };
    }

    public static class DbTypes {
        public static final String Unknown = "Unknown";
        public static final String Online = "Online (MySQL)";
        public static final String Local = "Local (SqLite)";

        public static final String[] All = {Unknown, Online, Local};
    }

    public enum PriceUnits {
        Euro    (0),
        Dollar  (1),
        Pound   (2);

        private int intValue;
        PriceUnits(int intValue) {
            this.intValue = intValue;
        }

        @Override
        public String toString() {
            switch (intValue) {
                case 0: return "€";
                case 1: return "$";
                case 2: return "£";
                default: return "";
            }
        }

        public int getIntValue() {
            return intValue;
        }

        public static PriceUnits fromInt(int intValue) {
            switch (intValue) {
                default:
                case 0: return Euro;
                case 1: return Dollar;
                case 2: return Pound;
            }
        }
    }
}
