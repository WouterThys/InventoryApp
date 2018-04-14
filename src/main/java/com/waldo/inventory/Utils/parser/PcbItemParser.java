package com.waldo.inventory.Utils.parser;

import com.waldo.eagleparser.EagleParser;
import com.waldo.inventory.classes.dbclasses.*;
import com.waldo.inventory.managers.SearchManager;
import com.waldo.kicadparser.KiCadParser;
import com.waldo.kicadparser.classes.Component;
import com.waldo.utils.FileUtils;

import java.io.File;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PcbItemParser {

    public static final String KiCadParser = "KiCadParser";
    private static final String EagleParser = "EagleParser";

    /*
     * Singleton stuff
     */

    private static final PcbItemParser ourInstance = new PcbItemParser();

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
    private final List<PcbParser> pcbParsers;

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

    public List<PcbItemProjectLink> updatePcbItemDb(ProjectPcb pcbProject, HashMap<String, List<PcbItem>> pcbItemMap) {

        List<PcbItem> itemsToSave = new ArrayList<>();
        List<PcbItemProjectLink> projectLinks = new ArrayList<>();

        for (String sheet : pcbItemMap.keySet()) {
            for (PcbItem pcbItem : pcbItemMap.get(sheet)) {
                PcbItem foundItem;
                PcbItemProjectLink link;

                foundItem = SearchManager.sm().findPcbItem(
                        pcbItem.getFootprint(),
                        pcbItem.getPartName()
                );

                if (foundItem == null) {
                    int ndx  = itemsToSave.indexOf(pcbItem);
                    if (ndx < 0) {
                        itemsToSave.add(pcbItem);
                        link = new PcbItemProjectLink(pcbItem, pcbProject);
                    } else {
                        PcbItem knownItem = itemsToSave.get(ndx);
                        link = new PcbItemProjectLink(knownItem, pcbProject);
                        link.setReferences(pcbItem.getReferences());
                        link.setValue(pcbItem.getValue());
                        link.setPcbSheetName(pcbItem.getSheetName());
                    }
                } else {
                    PcbItem newItem = foundItem.createCopy();
                    newItem.setRef(pcbItem.getRef());
                    newItem.setValue(pcbItem.getValue());
                    newItem.setReferences(pcbItem.getReferences());
                    newItem.settStamp(pcbItem.gettStamp());
                    newItem.setSheetName(pcbItem.getSheetName());

                    int ndx = pcbItemMap.get(sheet).indexOf(pcbItem);
                    pcbItemMap.get(sheet).set(ndx, newItem);

                    link = new PcbItemProjectLink(newItem, pcbProject);
                }

                projectLinks.add(link);
            }
        }

        for (PcbItem item : itemsToSave) {
            item.save();
        }

        return projectLinks;
    }

    public void updatePcbItemProjectLinksDb(ProjectPcb projectPcb, List<PcbItemProjectLink> projectLinks) {
        // Create new list with links
        List<PcbItemProjectLink> toDelete = new ArrayList<>(SearchManager.sm().findPcbItemLinksWithProjectPcb(projectPcb.getId()));
        List<PcbItemProjectLink> toSave = new ArrayList<>();

        for (PcbItemProjectLink link : projectLinks) {
            if (toDelete.contains(link)) {
                toDelete.remove(link);
            } else {
                toSave.add(link);
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

    public void updatePcbItemItemLinks(List<PcbItemProjectLink> projectLinks) {
        for (PcbItemProjectLink projectLink : projectLinks) {
            if (projectLink.getPcbItemId() > DbObject.UNKNOWN_ID) {
                PcbItem pcbItem = projectLink.getPcbItem();
                List<PcbItemItemLink> linkList = pcbItem.getKnownItemLinks();

                if (linkList.size() > 0) {
                    // Only one exact link
                    if ((linkList.size()) == 1 && (pcbItem.getPartName().equals(pcbItem.getValue()))) {
                        projectLink.setPcbItemItemLinkId(linkList.get(0).getId());
                        continue;
                    }
                    if (!pcbItem.getPartName().equals(pcbItem.getValue())) {
                        for (PcbItemItemLink link : linkList) {
                            Item item = link.getItem();
                            if (item.getValue().hasValue()) {
                                if (PcbItem.matchesValue(pcbItem.getValue(), item.getValue())) {
                                    projectLink.setPcbItemItemLinkId(link.getId());
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /*
     * Helper classes
     */

    private static class MyKiCadParser extends PcbParser {

        private final KiCadParser kiCadParser;

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
            if (fileToParse != null) {
                File parseFile = null;
                if (fileToParse.isFile()) {
                    if (kiCadParser.isFileValid(fileToParse)) {
                        parseFile = fileToParse;
                    } else {
                        List<File> actualFiles = FileUtils.findFileInFolder(fileToParse.getParentFile(), kiCadParser.getFileExtension(), true);
                        if (actualFiles != null && actualFiles.size() == 1) {
                            parseFile = actualFiles.get(0);
                        }
                    }
                } else {
                    // Search for file
                    List<File> actualFiles = FileUtils.findFileInFolder(fileToParse, kiCadParser.getFileExtension(), true);
                    if (actualFiles != null && actualFiles.size() == 1) {
                        parseFile = actualFiles.get(0);
                    }
                }

                if (parseFile != null) {
                    try {
                        resultMap = createMap(kiCadParser.parse(parseFile));
                    } catch (com.waldo.kicadparser.KiCadParser.KiCadParserException e) {
                        e.printStackTrace();
                    }
                }
            }


            return resultMap;
        }

        private HashMap<String, List<PcbItem>> createMap(HashMap<String, List<Component>> kiCadMap) {
            HashMap<String, List<PcbItem>> resultMap = new HashMap<>();
            for (String sheet : kiCadMap.keySet()) {
                resultMap.put(sheet, new ArrayList<>());
                for (Component c : kiCadMap.get(sheet)) {
                    PcbItem item = findItem(resultMap.get(sheet), c.getValue(), c.getFootprint(), c.getLibSource().getPart());
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

        private PcbItem findItem(List<PcbItem> items, String value, String footprint, String part) {
            for (PcbItem item : items) {
                if (item.getValue().equals(value) &&
                        item.getFootprint().equals(footprint) &&
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

        private final EagleParser eagleParser;

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
}
