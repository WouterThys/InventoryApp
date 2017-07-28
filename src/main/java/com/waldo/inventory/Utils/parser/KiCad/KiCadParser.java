package com.waldo.inventory.Utils.parser.KiCad;


import com.waldo.inventory.Utils.FileUtils;
import com.waldo.inventory.Utils.parser.Node;
import com.waldo.inventory.Utils.parser.ProjectParser;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class KiCadParser extends ProjectParser<KcComponent> {

    private List<KcComponent> componentList;
    private boolean hasParsed;

    public KiCadParser(String parserName) {
        super(parserName, "net", "(components", "(libparts");
        componentList = new ArrayList<>();
        hasParsed = false;
    }

    @Override
    public List<KcComponent> getParsedData() {
        if (!hasParsed) {
            parse(fileToParse);
        }
        componentList.sort(new KiCadComponentComparator());
        return componentList;
    }

    @Override
    public void sortList(List<KcComponent> list) {

    }

    @Override
    public void parse(File fileToParse) {
        this.fileToParse = fileToParse;
        if (isFileValid(fileToParse)) {
            String fileData = FileUtils.getRawStringFromFile(fileToParse);
            int startNdx = fileData.indexOf(fileStartSequence);
            int stopNdx = fileData.indexOf(fileStopSequence);

            String usefulData = fileData.substring(startNdx, stopNdx);

            try {
                String block = readBlock(usefulData);
                Node head = parseBlock(block);

                componentList = parseNode(head);
                hasParsed = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private List<KcComponent> parseNode(Node head) {
        List<KcComponent> components = new ArrayList<>();
        for (Node n1 : head.getChildren()) { // n1 has the list with all the components
            if (n1.name.equals("comp")) {
                KcComponent component = new KcComponent();
                for (Node n2 : n1.getChildren()) {
                    switch (n2.name) {
                        case "ref": component.setRef(n2.value); break;
                        case "value": component.setValue(n2.value); break;
                        case "footprint": component.setFootprint(n2.value); break;
                        case "libsource":
                            KcLibSource libSource = new KcLibSource();
                            for (Node n3 : n2.getChildren()) {
                                switch (n3.name) {
                                    case "lib": libSource.setLib(n3.value); break;
                                    case "part": libSource.setPart(n3.value); break;
                                    default:break;
                                }
                            }
                            component.setLibSource(libSource);
                            break;
                        case "sheetpath":
                            KcSheetPath sheetPath = new KcSheetPath();
                            for (Node n4: n2.getChildren()) {
                                switch (n4.name) {
                                    case "names": sheetPath.parseNames(n4.value); break;
                                    case "tstamps": sheetPath.parseTimeStamps(n4.value); break;
                                    default:break;
                                }
                            }
                            component.setSheetPath(sheetPath);
                            break;
                        case "tstamp": component.parseTimeStamp(n2.value); break;
                        default:break;
                    }
                }
                components.add(component);
            }
        }
        return components;
    }

    private Node parseBlock(String block) {
        // Remove first and last bracket
        block = block.substring(1, block.length()-1);

        // Node
        Node headNode = new Node();
        StringBuilder name = new StringBuilder();
        StringBuilder value = new StringBuilder();
        char[] blockChars = block.toCharArray();

        boolean valueStart = false;
        boolean nameEnd = false;
        boolean endFound = false;
        int charCnt = 0;

        while(!endFound) {

            char c = blockChars[charCnt];
            if (c == '(') {
                // new Block
                String newData;
                newData = new String(blockChars).substring(charCnt, blockChars.length);
                String newBlock = readBlock(newData);
                Node child = parseBlock(newBlock);
                headNode.addChild(child);
                charCnt += child.parseLength;
            } else {

                if (!Character.isLetterOrDigit(c) && !nameEnd) {
                    nameEnd = true;
                }

                if ((Character.isLetterOrDigit(c) || c == '/') && nameEnd) {
                    valueStart = true;
                }

                if (!nameEnd) {
                    name.append(blockChars[charCnt]);
                }
                if (valueStart) {
                    value.append(blockChars[charCnt]);
                }
            }

            endFound = (charCnt == blockChars.length-1);
            charCnt++;
        }
        headNode.name = name.toString();
        headNode.value = value.toString();
        headNode.parseLength = charCnt;
        return headNode;
    }

    private String readBlock(String data) {
        StringBuilder block = new StringBuilder();
        char[] chars = data.toCharArray();
        boolean endFound = false;
        boolean startFound = false;
        int charCnt = 0;
        int bracketCnt = 0;

        while(!endFound) {
            if (chars[charCnt] == '(') {
                bracketCnt++;
                startFound = true;
            }
            if (chars[charCnt] == ')') {
                bracketCnt--;
            }

            if (startFound) {
                block.append(chars[charCnt]);
            }

            endFound = (bracketCnt == 0) || (charCnt == data.length());
            charCnt++;
        }
        return block.toString();
    }

    private class KiCadComponentComparator implements Comparator<KcComponent> {
        @Override
        public int compare(KcComponent o1, KcComponent o2) {
            try {
                return o1.getLibSource().getPart().compareTo(o2.getLibSource().getPart());
            } catch(Exception e) {
                e.printStackTrace();
                return 0;
            }
        }
    }

}
