package com.jwa.sam.transformator.generators.config;

public enum Files {
    COMMUNICATION("communication/");

    private final String directoryPath;

    Files(final String directoryPath) {
        this.directoryPath = directoryPath;
    }

    public final String getDirectoryPath() {
        return directoryPath;
    }
}
