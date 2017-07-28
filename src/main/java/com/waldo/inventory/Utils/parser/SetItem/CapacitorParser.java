package com.waldo.inventory.Utils.parser.SetItem;

import com.waldo.inventory.classes.SetItem;

import java.util.List;

public class CapacitorParser extends SetItemParser{

    @Override
    public List<SetItem> parse(String series) {
        String fileName = "/setvalues/" + C + series + ".txt";
        return null;
    }

    @Override
    public List<SetItem> crop(int value) {
        return null;
    }

    CapacitorParser() {}
}
