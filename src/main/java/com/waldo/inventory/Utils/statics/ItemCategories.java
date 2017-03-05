package com.waldo.inventory.Utils.statics;

public class ItemCategories {
    public static final int UNKNOWN = -1;
    public static final int SEMICONDUCTORS = 0;
    public static final int CIRCUIT_PROTECTION= 1;
    public static final int CONNECTORS = 2;
    public static final int ELECTROMECHANICAL = 3;
    public static final int EMBEDDED_SOLUTIONS = 4;
    public static final int ENCLOSURES = 5;
    public static final int DEVELOPMENT_TOOLS = 6;
    public static final int INDUSTRIAL_AUTOMATION = 7;
    public static final int LED_LIGHTING = 8;
    public static final int OPTO_ELECTRONICS = 9;
    public static final int PASSIVE_COMPONENTS = 10;
    public static final int POWER = 11;
    public static final int SENSORS = 12;
    public static final int TEST_MEASUREMENT = 13;
    public static final int THERMAL_MANAGEMENT = 14;
    public static final int TOOLS_SUPPLIES = 15;
    public static final int WIRE_CABLE = 16;

    public static String getItemCategoryAsString(int category) {
        switch (category) {
            case UNKNOWN :
                return "Unknown";
            case SEMICONDUCTORS:
                return "Semiconductors";
            case CIRCUIT_PROTECTION:
                return "Circuit protection";
            case CONNECTORS:
                return "Connectors";
            case ELECTROMECHANICAL:
                return "Electromechanical";
            case EMBEDDED_SOLUTIONS:
                return "Embedded solutions";
            case ENCLOSURES:
                return "Enclosures";
            case DEVELOPMENT_TOOLS:
                return "Development tools";
            case INDUSTRIAL_AUTOMATION:
                return "Industrial automation";
            case LED_LIGHTING:
                return "LED lighting";
            case OPTO_ELECTRONICS:
                return "Opto-electronics";
            case PASSIVE_COMPONENTS :
                return "Passive components";
            case POWER :
                return "Power";
            case SENSORS :
                return "Sensors";
            case TEST_MEASUREMENT :
                return "Test & Measurement";
            case THERMAL_MANAGEMENT :
                return "Thermal management";
            case TOOLS_SUPPLIES :
                return "Tools & Supplies";
            case WIRE_CABLE :
                return "Wire & Cables";
            default:
                return "";
        }
    }

    public static String[] getItemCategoriesStringArray() {
        return new String[]{
                "Unknown",
                "Semiconductors",
                "Circuit protection",
                "Connectors",
                "Electromechanical",
                "Embedded solutions",
                "Enclosures",
                "Development tools",
                "Industrial automation",
                "LED lighting",
                "Opto-electronics",
                "Passive components",
                "Power",
                "Sensors",
                "Test & Measurement",
                "Thermal management",
                "Tools & Supplies",
                "Wire & Cables"
        };
    }
}
