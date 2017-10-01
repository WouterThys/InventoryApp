package com.waldo.inventory.Utils.parser;

import com.waldo.eagleparser.EagleParser;
import com.waldo.inventory.Utils.FileUtils;
import com.waldo.inventory.classes.Item;
import com.waldo.inventory.classes.PcbItemItemLink;
import com.waldo.inventory.classes.SetItem;
import com.waldo.inventory.classes.kicad.PcbItem;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.managers.SearchManager;
import com.waldo.kicadparser.KiCadParser;
import com.waldo.kicadparser.classes.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import static com.waldo.inventory.classes.PcbItemItemLink.MATCH_FOOTPRINT;
import static com.waldo.inventory.classes.PcbItemItemLink.MATCH_NAME;
import static com.waldo.inventory.classes.PcbItemItemLink.MATCH_VALUE;

public class PcbItemParser {

    private static final String KiCadParser = "KiCadParser";
    private static final String EagleParser = "EagleParser";

    /*
     * Singleton stuff
     */

    private static PcbItemParser ourInstance = new PcbItemParser();

    public static PcbItemParser getInstance() {
        return ourInstance;
    }

    private PcbItemParser() {
        pcbParsers = new ArrayList<>();
        pcbParsers.add(new MyKiCadParser());
        pcbParsers.add(new MyEagleParser());
    }

    /*
     * The real stuff
     */
    private List<PcbParser> pcbParsers;

    public List<PcbParser> getPcbParsers() {
        return pcbParsers;
    }

    public PcbParser getParser(String name) {
        for (PcbParser parser : getPcbParsers()) {
            if (parser.getName().equals(name)) {
                return parser;
            }
        }
        return null;
    }

    public List<PcbItemItemLink> linkWithSetItem(PcbItem component, Item item) {
        List<PcbItemItemLink> itemMatches = new ArrayList<>();
        String kcName = component.getPartName().toUpperCase();
        String kcValue = component.getValue().toUpperCase();
        String kcFp = component.getFootprint().toUpperCase();
        for (SetItem setItem : SearchManager.sm().findSetItemsByItemId(item.getId())) {
            int match = 0;
            String setItemName = setItem.getName().toUpperCase();
            String setItemValue = setItem.getValue().toString().toUpperCase();
            String setItemFp = "";

            if (item.getDimensionType() != null) {
                setItemFp = item.getDimensionType().getName();
            } else if (item.getPackage() != null && item.getPackage().getPackageType() != null){
                setItemFp = item.getPackage().getPackageType().getName();
            }

            if (setItemName.equals(kcName)) {
                match |= MATCH_NAME;
            }
            if (kcValue.contains(setItemValue) || setItemValue.contains(kcValue)) {
                match |= MATCH_VALUE;
            }
            // Only check footprint match if there is already a match
            if (((match & MATCH_NAME) == MATCH_NAME) || ((match & MATCH_VALUE) == MATCH_VALUE)) {
                if (!setItemFp.isEmpty() && kcFp.contains(setItemFp)) {
                    match |= MATCH_FOOTPRINT;
                }
            }

            // Add
            PcbItemItemLink link = SearchManager.sm().findKcItemLinkWithSetItemId(setItem.getId(), component.getId());
            if (link != null) {
                itemMatches.add(link);
                component.setMatchedItem(link);
            } else {
                if (match > 0) {
                    itemMatches.add(new PcbItemItemLink(match, component, setItem));
                }
            }

        }
        return itemMatches;
    }

    public List<PcbItemItemLink> linkWithItem(PcbItem pcbItem, Item item) {
        List<PcbItemItemLink> itemMatches = new ArrayList<>();
        int match = 0;

        String itemName = item.getName().toUpperCase();
        String pcbName = pcbItem.getPartName().toUpperCase();
        String pcbValue = pcbItem.getValue().toUpperCase();
        String pcbFp = pcbItem.getFootprint().toUpperCase();

        if (itemName.contains(pcbName) || pcbName.contains(itemName)) {
            match |= MATCH_NAME; // Set bit
        }
        if (itemName.contains(pcbValue) || pcbValue.contains(itemName)) {
            match |= MATCH_VALUE; // Set bit
        }

        // Find footprint of item
        String itemFp = "";
        if (item.getDimensionType() != null) {
            itemFp = item.getDimensionType().getName();
        } else if (item.getPackage() != null && item.getPackage().getPackageType() != null){
            itemFp = item.getPackage().getPackageType().getName();
        }
        // Only check footprint match if there is already a match
        if (((match & MATCH_NAME) == MATCH_NAME) || ((match & MATCH_VALUE) == MATCH_VALUE)) {
            if (!itemFp.isEmpty() && (pcbFp.contains(itemFp) || itemFp.contains(pcbFp))) {
                match |= MATCH_FOOTPRINT;
            }
        }

        // Find or create link
        PcbItemItemLink link = SearchManager.sm().findPcbItemLinkWithItem(item.getId(), pcbItem.getId());
        if (link != null) {
            itemMatches.add(link);
            pcbItem.setMatchedItem(link);
        } else {
            if (match > 0) {
                itemMatches.add(new PcbItemItemLink(match, pcbItem, item));
            }
        }

        return itemMatches;
    }

    public List<PcbItemItemLink> findLinkWithItem(PcbItem pcbItem) {
        List <PcbItemItemLink> itemLinkList = new ArrayList<>();

        for (Item item : DbManager.db().getItems()) {
            if (pcbItem.getLibrary().toUpperCase().equals("DEVICE")) {
                if (item.isSet()) {
                    itemLinkList.addAll(linkWithSetItem(pcbItem, item));
                } else {
                    itemLinkList.addAll(linkWithItem(pcbItem, item));
                }
            } else {
                itemLinkList.addAll(linkWithItem(pcbItem, item));
            }
        }

        itemLinkList.sort(new MatchComparator());

        return itemLinkList;
    }

    public int getMatchCount(int i) {
        i = i - ((i >>> 1) & 0x55555555);
        i = (i & 0x33333333) + ((i >>> 2) & 0x33333333);
        return (((i + (i >>> 4)) & 0x0F0F0F0F) * 0x01010101) >>> 24;
    }

    /*
     * Helper classes
     */

    private static class MyKiCadParser extends PcbParser {

        private KiCadParser kiCadParser;

        private MyKiCadParser() {
            kiCadParser = new KiCadParser(KiCadParser);
        }

        @Override
        public HashMap<String, List<PcbItem>> parse(File fileToParse) {
            HashMap<String, List<PcbItem>> resultMap = new HashMap<>();

            if (fileToParse != null && fileToParse.isFile()) {
                if (kiCadParser.isFileValid(fileToParse)) {
                    kiCadParser.parse(fileToParse);
                    resultMap = createMap(kiCadParser.getParsedData());
                } else {
                    if (FileUtils.getExtension(fileToParse).equals(kiCadParser.getFileExtension())) { // getFileExtension should be "pro"
                        resultMap = parse(fileToParse.getParentFile());
                    }
                }
            } else {
                // Search for file
                List<File> actualFiles = FileUtils.findFileInFolder(fileToParse, kiCadParser.getFileExtension(), true);
                if (actualFiles != null && actualFiles.size() == 1) {
                    kiCadParser.parse(actualFiles.get(0));
                    resultMap = createMap(kiCadParser.getParsedData());
                }
            }

            return resultMap;
        }

        private HashMap<String, List<PcbItem>> createMap(HashMap<String, List<Component>> kiCadMap) {
            HashMap<String, List<PcbItem>> resultMap = new HashMap<>();
            for (String sheet : kiCadMap.keySet()) {
                resultMap.put(sheet, new ArrayList<>());
                for (Component c : kiCadMap.get(sheet)) {
                    PcbItem item = new PcbItem(
                            c.getRef(),
                            c.getValue(),
                            c.getFootprint(),
                            c.getLibSource().getLib(),
                            c.getLibSource().getPart(),
                            sheet,
                            c.gettStamp()
                    );
                    resultMap.get(sheet).add(item);
                }
            }
            return resultMap;
        }

        @Override
        public String getName() {
            return kiCadParser.getParserName();
        }
    }

    private static class MyEagleParser extends PcbParser {

        private EagleParser eagleParser;

        private MyEagleParser() {
            eagleParser = new EagleParser(EagleParser);
        }

        @Override
        public HashMap<String, List<PcbItem>> parse(File fileToParse) {
            return null;
        }

        @Override
        public String getName() {
            return eagleParser.getParserName();
        }
    }

    private class MatchComparator implements Comparator<PcbItemItemLink> {
        @Override
        public int compare(PcbItemItemLink o1, PcbItemItemLink o2) {
            int mc1 = getMatchCount(o1.getMatch());
            int mc2 = getMatchCount(o2.getMatch());
            if (mc1 < mc2) {
                return 1;
            } else if (mc1 > mc2) {
                return -1;
            } else {
                return 0;
            }
        }
    }
}
