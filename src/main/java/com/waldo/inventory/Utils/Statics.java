package com.waldo.inventory.Utils;

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

    public static String[] Alphabet = {
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
            "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
}
