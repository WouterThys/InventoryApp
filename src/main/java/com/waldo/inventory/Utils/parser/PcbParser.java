package com.waldo.inventory.Utils.parser;

import com.waldo.inventory.classes.dbclasses.PcbItem;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public abstract class PcbParser {

    public abstract HashMap<String, List<PcbItem>> parse(File fileToParse);

    public abstract String getName();
}
