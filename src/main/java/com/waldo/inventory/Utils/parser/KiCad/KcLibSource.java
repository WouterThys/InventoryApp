package com.waldo.inventory.Utils.parser.KiCad;

public class KcLibSource {

    private String lib;
    private String part;

    public String getLib() {
        return lib;
    }

    public void setLib(String lib) {
        this.lib = lib;
    }

    public String getPart() {
        if (part == null) {
            part = "";
        }
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }
}
