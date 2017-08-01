package com.waldo.inventory.Utils.parser;

import com.waldo.inventory.Utils.FileUtils;

import java.io.File;
import java.util.List;

public abstract class ProjectParser<P> {

    protected String parserName;
    protected File fileToParse;

    protected String fileExtension;
    protected String fileStartSequence;
    protected String fileStopSequence;


    protected ProjectParser(String parserName, String fileExtension, String fileStartSequence, String fileStopSequence) {
        this.parserName = parserName;
        this.fileExtension = fileExtension;
        this.fileStartSequence = fileStartSequence;
        this.fileStopSequence = fileStopSequence;
    }


    @Override
    public String toString() {
        return parserName;
    }

    public boolean isFileValid(File fileToParse) {
        return ((fileToParse.exists()) && FileUtils.getExtension(fileToParse).equals(fileExtension));
    }

    public abstract void parse(File fileToParse);

    public abstract List<P> getParsedData();

    public abstract List<P> sortList(List<P> list);

    public String getParserName() {
        return parserName;
    }

    public void setParserName(String parserName) {
        this.parserName = parserName;
    }

    public File getFileToParse() {
        return fileToParse;
    }

    public void setFileToParse(File fileToParse) {
        this.fileToParse = fileToParse;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }
}
