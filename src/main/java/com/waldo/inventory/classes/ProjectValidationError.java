package com.waldo.inventory.classes;

import java.io.File;

public class ProjectValidationError {

    private ProjectDirectory invalidDirectory;
    private ProjectIDE invalidType;
    private File invalidFile;
    private String error;

    public ProjectValidationError(ProjectDirectory directory, String error) {
        this.invalidDirectory = directory;
        this.error = error;
    }

    public ProjectValidationError(ProjectDirectory directory, ProjectIDE type, File file, String error) {
        this(directory, error);
        this.invalidType = type;
        this.invalidFile = file;
    }

    @Override
    public String toString() {
        String result;
        if (invalidType == null) {
            result = " - Directory: " + invalidDirectory.getDirectory() + ": " + error;
        } else {
            result = " - Type: " + invalidType.getName() + ": " + error;
        }
        return result;
    }

    public ProjectDirectory getInvalidDirectory() {
        return invalidDirectory;
    }

    public ProjectIDE getInvalidType() {
        return invalidType;
    }

    public File getInvalidFile() {
        return invalidFile;
    }

    public String getError() {
        return error;
    }
}
