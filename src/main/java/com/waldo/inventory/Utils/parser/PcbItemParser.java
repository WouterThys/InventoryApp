package com.waldo.inventory.Utils.parser;

import com.waldo.eagleparser.EagleParser;
import com.waldo.inventory.Utils.FileUtils;
import com.waldo.inventory.classes.Value;
import com.waldo.inventory.classes.dbclasses.*;
import com.waldo.inventory.managers.SearchManager;
import com.waldo.kicadparser.KiCadParser;
import com.waldo.kicadparser.classes.Component;

import java.io.File;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import static com.waldo.inventory.classes.dbclasses.PcbItemItemLink.*;
import static com.waldo.inventory.managers.CacheManager.cache;

public class PcbItemParser {

    public static final String KiCadParser = "KiCadParser";
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

    private int matchName(String pcbName, String itemName) {
        if ((pcbName.length() > 2 && itemName.length() > 2) && (itemName.contains(pcbName) || pcbName.contains(itemName))) {
            return MATCH_NAME; // Set bit
        } else {
            return 0;
        }
    }

    private int matchValue(String pcbValue, Value value) {
        Value pcbVal = Value.tryFindValue(pcbValue);
        if (pcbVal != null) {
            if (pcbVal.equalsIgnoreUnits(value)) {
                return MATCH_VALUE;
            }
        }
        return 0;
    }

    private int matchValue(String pcbValue, Value value, String itemName) {
        int res = matchValue(pcbValue, value);

        if (res == 0) { // Try to match with name
            if (pcbValue.contains(itemName) || itemName.contains(pcbValue)) {
                res = MATCH_VALUE;
            }
        }

        return res;
    }

    private int matchFootprint(String pcbFp, PackageType packageType) {
        if (pcbFp != null && !pcbFp.isEmpty() && packageType != null) {
            String pkName = packageType.getPackage().getName().toUpperCase();
            String ptName = packageType.getName().toUpperCase();

            if (pcbFp.contains(pkName) || pkName.contains(pcbFp) || pcbFp.contains(ptName) || ptName.contains(pcbFp)) {
                return MATCH_FOOTPRINT;
            }
        }
        return  0;
    }

    public List<PcbItemItemLink> linkWithSetItem(PcbItem component, Item item) {
        List<PcbItemItemLink> itemMatches = new ArrayList<>();

        String pcbName = component.getPartName().toUpperCase();
        String pcbValue = component.getValue();
        String pcbFp = component.getFootprint().toUpperCase();

        ParserItemLink parserLink = SearchManager.sm().findParserItemLinkByPcbItemName(pcbName);

        if (parserLink != null) {

            if (parserLink.hasCategory()) {
                if (item.getCategoryId() != parserLink.getCategoryId()) {
                    return itemMatches;
                }
            }

            if (parserLink.hasProduct()) {
                if (item.getProductId() != parserLink.getProductId()) {
                    return itemMatches;
                }
            }

            if (parserLink.hasType()) {
                if (item.getTypeId() != parserLink.getTypeId()) {
                    return itemMatches;
                }
            }
        }

        for (SetItem setItem : SearchManager.sm().findSetItemsByItemId(item.getId())) {
            PcbItemItemLink link = SearchManager.sm().findPcbItemLinkWithSetItem(setItem.getId(), component.getId());
            if (link != null) {
                itemMatches.add(link);
                component.setMatchedItem(link);
            } else {
                int match = 0;
                String itemName = setItem.getName().toUpperCase();

                match |= matchName(pcbName, itemName); // Set bit
                match |= matchValue(pcbValue, setItem.getValue()); // Set bit

                // Only check footprint match if there is already a match
                if (match > 0 && item.getPackageTypeId() > UNKNOWN_ID) {
                    match |= matchFootprint(pcbFp, item.getPackageType());
                }

                if (match > 0) {
                    itemMatches.add(new PcbItemItemLink(match, component, setItem));
                }
            }

        }
        return itemMatches;
    }


    public List<PcbItemItemLink> linkWithItem(PcbItem pcbItem, Item item) {
        List<PcbItemItemLink> itemMatches = new ArrayList<>();


        PcbItemItemLink link = SearchManager.sm().findPcbItemLinkWithItem(item.getId(), pcbItem.getId());
        if (link != null) {
            itemMatches.add(link);
            pcbItem.setMatchedItem(link);
        } else {
            int match = 0;

            String itemName = item.getName().toUpperCase();

            String pcbName = pcbItem.getPartName().toUpperCase();
            String pcbValue = pcbItem.getValue();
            String pcbFp = pcbItem.getFootprint().toUpperCase();

            ParserItemLink parserLink = SearchManager.sm().findParserItemLinkByPcbItemName(pcbName);

            if (parserLink != null) {

                if (parserLink.hasCategory()) {
                    if (item.getCategoryId() != parserLink.getCategoryId()) {
                        return itemMatches;
                    }
                }

                if (parserLink.hasProduct()) {
                    if (item.getProductId() != parserLink.getProductId()) {
                        return itemMatches;
                    }
                }

                if (parserLink.hasType()) {
                    if (item.getTypeId() != parserLink.getTypeId()) {
                        return itemMatches;
                    }
                }
            }

            match |= matchName(pcbName, itemName); // Set bit
            match |= matchValue(pcbValue, item.getValue(), itemName);
            if (match > 0 && item.getPackageTypeId() > UNKNOWN_ID) {
                match |= matchFootprint(pcbFp, item.getPackageType());
            }

            if (match > 0) {
                itemMatches.add(new PcbItemItemLink(match, pcbItem, item));
            }
        }

        return itemMatches;
    }

    public List<PcbItemItemLink> findLinkWithItem(PcbItem pcbItem) {
        List<PcbItemItemLink> itemLinkList = new ArrayList<>();

        for (Item item : cache().getItems()) {
            if (item.isSet()) {
                itemLinkList.addAll(linkWithSetItem(pcbItem, item));
            } else {
                itemLinkList.addAll(linkWithItem(pcbItem, item));
            }

            if (itemLinkList.size() > 15) {
                break;
            }
        }

        itemLinkList.sort(new MatchComparator());

        if (itemLinkList.size() == 1) {
            int match = itemLinkList.get(0).getMatch();
            if (getMatchCount(match) == 3) {
                pcbItem.setMatchedItem(itemLinkList.get(0));
            }
        }

        return itemLinkList;
    }

    public void updatePcbItemDb(HashMap<String, List<PcbItem>> pcbItemMap) {
        List<PcbItem> itemsToSave = new ArrayList<>();
        for (String sheet : pcbItemMap.keySet()) {
            for (PcbItem pcbItem : pcbItemMap.get(sheet)) {
                PcbItem foundItem = SearchManager.sm().findPcbItem(
                        pcbItem.getValue(),
                        pcbItem.getFootprint(),
                        pcbItem.getLibrary(),
                        pcbItem.getPartName()
                );

                if (foundItem == null) {
                    itemsToSave.add(pcbItem);
                } else {
                    PcbItem newItem = foundItem.createCopy();
                    newItem.setRef(pcbItem.getRef());
                    newItem.setReferences(pcbItem.getReferences());
                    newItem.settStamp(pcbItem.gettStamp());
                    newItem.setSheetName(pcbItem.getSheetName());

                    int ndx = pcbItemMap.get(sheet).indexOf(pcbItem);
                    pcbItemMap.get(sheet).set(ndx, newItem);
                }
            }
        }

        for (PcbItem item : itemsToSave) {
            item.save();
        }
    }

    public void updatePcbItemProjectLinksDb(HashMap<String, List<PcbItem>> pcbItems, ProjectPcb projectPcb) {
        // Create new list with links
        List<PcbItemProjectLink> toDelete = new ArrayList<>(SearchManager.sm().findPcbItemLinksWithProjectPcb(projectPcb.getId()));
        List<PcbItemProjectLink> toSave = new ArrayList<>();

        for (String sheet : pcbItems.keySet()) {
            for (PcbItem pcbItem : pcbItems.get(sheet)) {
                PcbItemProjectLink link = SearchManager.sm().findPcbItemLink(pcbItem.getId(), projectPcb.getId(), sheet);
                if (link != null) {
                    toDelete.remove(link);
                } else {
                    link = new PcbItemProjectLink(pcbItem, projectPcb);
                    toSave.add(link);
                }
            }
        }

        // Save
        for (PcbItemProjectLink link : toSave) {
            link.save();
        }

        // What remains in currentLinks can be removed
        for (PcbItemProjectLink link : toDelete) {
            link.delete();
        }
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
        public String toString() {
            return kiCadParser.getParserName();
        }

        @Override
        public HashMap<String, List<PcbItem>> parse(File fileToParse) {
            HashMap<String, List<PcbItem>> resultMap = new HashMap<>();

            // Parse from file
            if (fileToParse != null && fileToParse.isFile()) {
                if (kiCadParser.isFileValid(fileToParse)) {
                    kiCadParser.parse(fileToParse);
                    resultMap = createMap(kiCadParser.getParsedData());
                } else {
                    List<File> actualFiles = FileUtils.findFileInFolder(fileToParse.getParentFile(), kiCadParser.getFileExtension(), true);
                    if (actualFiles != null && actualFiles.size() == 1) {
                        kiCadParser.parse(actualFiles.get(0));
                        resultMap = createMap(kiCadParser.getParsedData());
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
                    PcbItem item = findItem(resultMap.get(sheet), c.getValue(), c.getFootprint(), c.getLibSource().getLib(), c.getLibSource().getPart());
                    if (item != null) {
                        item.addReference(c.getRef());
                    } else {
                        item = new PcbItem(
                                c.getRef(),
                                c.getValue(),
                                c.getFootprint(),
                                c.getLibSource().getLib(),
                                c.getLibSource().getPart(),
                                sheet,
                                new Date(c.gettStamp().getTime())
                        );
                        resultMap.get(sheet).add(item);
                    }
                }
            }
            return resultMap;
        }

        private PcbItem findItem(List<PcbItem> items, String value, String footprint, String library, String part) {
            for (PcbItem item : items) {
                if (item.getValue().equals(value) &&
                        item.getFootprint().equals(footprint) &&
                        item.getLibrary().equals(library) &&
                        item.getPartName().equals(part)) {

                    return item;
                }
            }
            return null;
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
        public String toString() {
            return eagleParser.getParserName();
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
