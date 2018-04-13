package com.waldo.inventory.Utils;


import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.gui.Application.imageResource;

public class Statics {

    public enum QueryType {
        CacheClear(-2),
        Unknown(-1),
        Insert(0),
        Update(1),
        Delete(2),
        Select(3),
        Custom(4);

        private final int value;
        QueryType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static QueryType fromInt(int value) {
            switch (value) {
                default:
                case -1: return Unknown;
                case -2: return CacheClear;
                case 0: return Insert;
                case 1: return Update;
                case 2: return Delete;
                case 3: return Select;
                case 4: return Custom;
            }
        }
    }

    public static final String[] Alphabet = {
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
            "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    public static final String sqlKeyWords
            =
            "ABORT|ACTION"+
                    "|ADD|AFTER"+
                    "|ALL|ALTER"+
                    "|ANALYZE|AND"+
                    "|AS|ASC"+
                    "|ATTACH|AUTOINCREMENT"+
                    "|BEFORE|BEGIN"+
                    "|BETWEEN|BY"+
                    "|CASCADE|CASE"+
                    "|CAST|CHECK"+
                    "|COLLATE|COLUMN"+
                    "|COMMIT|CONFLICT"+
                    "|CONSTRAINT|CREATE"+
                    "|CROSS|CURRENT_DATE"+
                    "|CURRENT_TIME|CURRENT_TIMESTAMP"+
                    "|DATABASE|DEFAULT"+
                    "|DEFERRABLE|DEFERRED"+
                    "|DELETE|DESC"+
                    "|DETACH|DISTINCT"+
                    "|DROP|EACH"+
                    "|ELSE|END"+
                    "|ESCAPE|EXCEPT"+
                    "|EXCLUSIVE|EXISTS"+
                    "|EXPLAIN|FAIL"+
                    "|FOR|FOREIGN"+
                    "|FROM|FULL"+
                    "|GLOB|GROUP"+
                    "|HAVING|IF"+
                    "|IGNORE|IMMEDIATE"+
                    "|IN|INDEX"+
                    "|INDEXED|INITIALLY"+
                    "|INNER|INSERT"+
                    "|INSTEAD|INTERSECT"+
                    "|INTO|IS"+
                    "|ISNULL|JOIN"+
                    "|KEY|LEFT"+
                    "|LIKE|LIMIT"+
                    "|MATCH|NATURAL"+
                    "|NO|NOT"+
                    "|NOTNULL|NULL"+
                    "|OF|OFFSET"+
                    "|ON|OR|ORDER|OUTER"+
                    "|PLAN|PRAGMA"+
                    "|PRIMARY|QUERY"+
                    "|RAISE|RECURSIVE"+
                    "|REFERENCES|REGEXP"+
                    "|REINDEX|RELEASE"+
                    "|RENAME|REPLACE"+
                    "|RESTRICT|RIGHT"+
                    "|ROLLBACK|ROW"+
                    "|SAVEPOINT|SELECT"+
                    "|SET|TABLE"+
                    "|TEMP|TEMPORARY"+
                    "|THEN|TO"+
                    "|TRANSACTION|TRIGGER"+
                    "|UNION|UNIQUE"+
                    "|UPDATE|USING"+
                    "|VACUUM|VALUES"+
                    "|VIEW|VIRTUAL"+
                    "|WHEN|WHERE"+
                    "|WITH|WITHOUT";

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

    public enum EventType {
        OneTime     ("One time", "AT"),
        Recurring   ("Recurring", "EVERY");

        private final String string;
        private final String sqlParam;
        EventType(String string, String sqlParam) {
            this.string = string;
            this.sqlParam = sqlParam;
        }

        @Override
        public String toString() {
            return string;
        }

        public String getSqlParam() {
            return sqlParam;
        }

        public static EventType fromString(String string) {
            if (string != null) {
                switch (string) {
                    default:
                    case "ONE TIME": return OneTime;
                    case "RECURRING": return Recurring;
                }
            }
            return OneTime;
        }
    }

    public enum EventIntervalField {
        Unknown,
        Year,
        Quarter,
        Month,
        Day,
        Hour,
        Minute,
        Week,
        Second;

        public static EventIntervalField fromString(String value) {
            if (value != null) {
                switch (value) {
                    default:
                    case "": return Unknown;
                    case "YEAR": return Year;
                    case "QUARTER": return Quarter;
                    case "MONTH": return Month;
                    case "DAY": return Day;
                    case "HOUR": return Hour;
                    case "MINUTE": return Minute;
                    case "WEEK": return Week;
                    case "SECOND": return Second;
                }
            }
            return Unknown;
        }
    }

    public enum GuiDetailsView {
        VerticalSplit ("Vertical split"),
        HorizontalSplit("Horizontal split");

        private final String string;
        GuiDetailsView(String string) {
            this.string = string;
        }

        @Override
        public String toString() {
            return string;
        }

        public static GuiDetailsView fromString(String string) {
            if (string != null) {
                switch (string) {
                    case "":
                    case "Vertical split": return VerticalSplit;
                    case "Horizontal split": return HorizontalSplit;
                }
            }
            return VerticalSplit;
        }
    }

    public enum ResistorBandType {
        FourBand(4, "4 Band"),
        FiveBand(5, "5 Band"),
        SixBand (6, "6 Band");

        private final int bands;
        private final String string;

        ResistorBandType(int bands, String string) {
            this.bands = bands;
            this.string = string;
        }

        @Override
        public String toString() {
            return string;
        }

        public int getBands() {
            return bands;
        }
    }

    public enum ResistorBandValue {
        Black  (0,  -1, -1, new Color(0, 0, 0)),
        Brown  (1,   1,100, new Color(127, 78, 15)),
        Red    (2,   2, 50, new Color(255, 0, 0)),
        Orange (3,  -1, 15, new Color(255, 144, 0)),
        Yellow (4,  -1, 25, new Color(255, 255, 0)),
        Green  (5, 0.5, -1, new Color(20,200,20)),
        Blue   (6,0.25, 10, new Color(0, 0, 255)),
        Violet (7, 0.1,  5, new Color(150, 0, 255)),
        Gray   (8,0.05, -1, new Color(125, 125, 125)),
        White  (9,  -1, -1, new Color(255, 255, 255)),
        Gold  (-1,   5, -1, new Color(244, 212, 48)),
        Silver(-2,  10, -1, new Color(191, 191, 191));

        private final int value;
        private final double tolerance;
        private final int ppm;
        private final Color color;

        ResistorBandValue(int value, double tolerance, int ppm, Color color) {
            this.value = value;
            this.tolerance = tolerance;
            this.ppm = ppm;
            this.color = color;
        }


        public int getValue() {
            return value;
        }

        public double getTolerance() {
            return tolerance;
        }

        public int getPpm() {
            return ppm;
        }

        public Color getColor() {
            return color;
        }

        public static ResistorBandValue valueBandFromValue(int value) {
            switch (value) {
                case 0: return Black;
                case 1: return Brown;
                case 2: return Red;
                case 3: return Orange;
                case 4: return Yellow;
                case 5: return Green;
                case 6: return Blue;
                case 7: return Violet;
                case 8: return Gray;
                case 9: return White;
                default: return null;
            }
        }

        public static ResistorBandValue multiplierBandFromValue(int multiplier) {
            switch (multiplier) {
                case  0: return Black;
                case  1: return Brown;
                case  2: return Red;
                case  3: return Orange;
                case  4: return Yellow;
                case  5: return Green;
                case  6: return Blue;
                case  7: return Violet;
                case  8: return Gray;
                case  9: return White;
                case -1: return Gold;
                case -2: return Silver;
                default: return null;
            }
        }

        public static List<ResistorBandValue> getFirstBandValues() {
            List<ResistorBandValue> values = new ArrayList<>();
            for (ResistorBandValue v : ResistorBandValue.values()) {
                if (v.getValue() > 0) {
                    values.add(v);
                }
            }
            return values;
        }

        public static List<ResistorBandValue> getSecondBandValues() {
            List<ResistorBandValue> values = new ArrayList<>();
            for (ResistorBandValue v : ResistorBandValue.values()) {
                if (v.getValue() >= 0) {
                    values.add(v);
                }
            }
            return values;
        }

        public static List<ResistorBandValue> getThirdBandValues() {
            return getSecondBandValues();
        }

        public static List<ResistorBandValue> getMultiplierBandValues() {
            return getSecondBandValues();
        }

        public static List<ResistorBandValue> getToleranceBandValues() {
            List<ResistorBandValue> values = new ArrayList<>();
            for (ResistorBandValue v : ResistorBandValue.values()) {
                if (v.getTolerance() > 0) {
                    values.add(v);
                }
            }
            return values;
        }

        public static List<ResistorBandValue> getPpmBandValues() {
            List<ResistorBandValue> values = new ArrayList<>();
            for (ResistorBandValue v : ResistorBandValue.values()) {
                if (v.getPpm() > 0) {
                    values.add(v);
                }
            }
            return values;
        }
    }

    public enum CreatedPcbLinkState {

        Ok (imageResource.readIcon("Actions.SaveOk"), "Ok", Color.BLACK),
        Warning(imageResource.readIcon("Actions.SaveWarn"), "", Color.ORANGE),
        Error(imageResource.readIcon("Actions.SaveError"), "", Color.RED),
        NotSaved(imageResource.readIcon("Actions.SaveRed"), "Not saved..", Color.GRAY)
        ;

        private final ImageIcon imageIcon;
        private final Color messageColor;
        private final List<String> messages;
        CreatedPcbLinkState(ImageIcon imageIcon, String defaultMessage, Color messageColor) {
            this.imageIcon = imageIcon;
            this.messages = new ArrayList<>();
            this.messageColor = messageColor;
            if (!defaultMessage.isEmpty()) {
                messages.add(defaultMessage);
            }
        }

        public ImageIcon getImageIcon() {
            return imageIcon;
        }

        public Color getMessageColor() {
            return messageColor;
        }

        public List<String> getMessages() {
            return messages;
        }

        public void clearMessages() {
            messages.clear();
        }

        public void addMessage(String message) {
            if (message != null && !message.isEmpty() && !messages.contains(message)) {
                messages.add(message);
            }
        }
    }

    public enum IconDisplayType {
        Icon    (0, "Icon"),
        R_SMD   (1, "Resistor - SMD"),
        R_THT   (2, "Resistor - Through hole"),
        C_Elco  (3, "Capacitor"),
        C_      (4, "Capacitor 2")
        ;

        private final int intValue;
        private final String stringValue;
        IconDisplayType(int intValue, String stringValue) {
            this.intValue = intValue;
            this.stringValue = stringValue;
        }

        public int getIntValue() {
            return intValue;
        }

        @Override
        public String toString() {
            return stringValue;
        }

        public static IconDisplayType fromInt(int intValue) {
            switch (intValue) {
                default:
                case 0: return Icon;
                case 1: return R_SMD;
                case 2: return R_THT;
                case 3: return C_Elco;
                case 4: return C_;
            }
        }
    }

    public enum DistributorType {
        Items (0, "Items"),
        Pcbs  (1, "PCB's");

        private final int intValue;
        private final String name;
        DistributorType(int intValue, String name) {
            this.intValue = intValue;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }

        public int getIntValue() {
            return intValue;
        }

        public static DistributorType fromInt(int intValue) {
            switch (intValue) {
                default:
                case 0: return Items;
                case 1: return Pcbs;
            }
        }
    }
}




























