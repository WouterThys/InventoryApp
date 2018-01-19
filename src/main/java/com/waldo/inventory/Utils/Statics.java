package com.waldo.inventory.Utils;

import java.util.ArrayList;
import java.util.List;

public class Statics {

    public static final String[] Alphabet = {
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
            "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    public enum ItemAmountTypes {
        Unknown (0),
        Max     (1),
        Min     (2),
        Exact   (3),
        Approximate (4);

        private final int value;
        ItemAmountTypes(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static ItemAmountTypes fromInt(int value) {
            switch (value) {
                default:
                case 0: return Unknown;
                case 1: return Max;
                case 2: return Min;
                case 3: return Exact;
                case 4: return Approximate;
            }
        }
    }

    public enum ItemOrderStates {
        NoOrder  (0, "Not ordered"),
        Ordered  (1, "Ordered"),
        Received (2, "Received"),
        Planned  (3, "Planned");

        private final int value;
        private final String string;
        ItemOrderStates(int value, String string) {
            this.value = value;
            this.string = string;
        }

        @Override
        public String toString() {
            return string;
        }

        public int getValue() {
            return value;
        }

        public static ItemOrderStates fromInt(int value) {
            switch (value) {
                default:
                case 0: return NoOrder;
                case 1: return Ordered;
                case 2: return Received;
                case 3: return Planned;
            }
        }
    }

    public enum LogTypes {
        Info    (0),
        Debug   (1),
        Warn    (2),
        Error   (3);

        private final int value;
        LogTypes(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static LogTypes fromInt(int value) {
            switch (value) {
                default:
                case 0: return Info;
                case 1: return Debug;
                case 2: return Warn;
                case 3: return Error;
            }
        }
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

    public enum CodeLanguages {
        Unknown (""),
        C       ("C"),
        Cs      ("C#"),
        Cpp     ("C++"),
        Java    ("Java"),
        Python  ("Python");

        private final String string;
        CodeLanguages(String string) {
            this.string = string;
        }

        @Override
        public String toString() {
            return string;
        }

        public static CodeLanguages fromString(String language) {
            if (language != null) {
                switch (language) {
                    default:
                    case "": return Unknown;
                    case "C": return C;
                    case "C#": return Cs;
                    case "C++": return Cpp;
                    case "Java": return Java;
                    case "Python": return Python;
                }
            } else {
                return Unknown;
            }
        }
    }

    public enum ProjectTypes {
        Unknown (""),
        Code    ("Code"),
        Pcb     ("Pcb"),
        Other   ("Other");

        private final String string;
        ProjectTypes(String string) {
            this.string = string;
        }

        @Override
        public String toString() {
            return string;
        }

        public static ProjectTypes fromString(String type) {
            if (type != null) {
                switch (type) {
                    default:
                    case "": return Unknown;
                    case "Code": return Code;
                    case "Pcb": return Pcb;
                    case "Other": return Other;
                }
            }
            return Unknown;
        }
    }

    public enum DbTypes {
        Unknown (""),
        Online  ("Online (MySQL)"),
        Local   ("Local (SqLite)");

        private final String string;
        DbTypes(String string) {
            this.string = string;
        }

        @Override
        public String toString() {
            return string;
        }

        public static DbTypes fromString(String type) {
            if (type != null) {
                switch (type) {
                    default:
                    case "": return Unknown;
                    case "Online (MySQL)": return Online;
                    case "Local (SqLite)": return Local;
                }
            }
            return Unknown;
        }
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
