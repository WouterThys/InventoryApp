package com.waldo.inventory.Utils;

import java.util.Arrays;

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

    public static class UnitMultipliers {
        public static final int f = -15;
        public static final int p = -12;
        public static final int n = -9;
        public static final int µ = -6;
        public static final int m = -3;
        public static final int x = 1;
        public static final int k = 3;
        public static final int M = 6;
        public static final int G = 9;
        public static final int T = 12;

        public static final String[] ALL = {"f","p","n", "µ", "m", " ", "k", "M", "G", "T"};

        public static String get(int i) {
            return ALL[i];
        }

        public static String toMultiplier(int mul) {
            switch (mul) {
                case f:
                    return "f";
                case p:
                    return "p";
                case n:
                    return "n";
                case µ:
                    return "µ";
                case m:
                    return "m";
                case x:
                    return " ";
                case k:
                    return "k";
                case M:
                    return "M";
                case G:
                    return "G";
                case T:
                    return "T";
                default:
                    return "";
            }
        }

        public static int toMultiplier(String mul) {
            switch (mul) {
                case "f": return f;
                case "p": return p;
                case "n": return n;
                case "µ": return µ;
                case "m": return m;
                case " ": return 0;
                case "k": return k;
                case "M": return M;
                case "G": return G;
                case "T": return T;
                default:return 0;
            }
        }
    }

    public static class Units {
        public static final String R_UNIT = "\u2126"; // Ohm
        public static final String C_UNIT = "F"; // Farad
        public static final String L_UNIT = "H"; // Henry
        public static final String V_UNIT = "V"; // Volt
        public static final String I_UNIT = "A"; // Amperes

        public static final String[] ALL = {R_UNIT, C_UNIT, L_UNIT, V_UNIT, I_UNIT};
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
