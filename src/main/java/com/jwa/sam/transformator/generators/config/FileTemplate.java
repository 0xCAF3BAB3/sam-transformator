package com.jwa.sam.transformator.generators.config;

public enum FileTemplate {
    POM_SERVICE(Constants.DIRECTORY_SERVICE + "pom.xml"),
    POM_COMPONENT(Constants.DIRECTORY_COMPONENT + "pom.xml"),
    POM_MESSAGEMODEL(Constants.DIRECTORY_MESSAGEMODEL + "pom.xml"),
    POM_COMMUNICATION(Constants.DIRECTORY_COMMUNICATION + "pom.xml");

    private final String filepath;

    FileTemplate(final String filepath) {
        this.filepath = filepath;
    }

    public final String getFilepath() {
        return filepath;
    }

    private static final class Constants {
        private static final String DIRECTORY_SERVICE = "service/";
        private static final String DIRECTORY_COMPONENT = "component/";
        private static final String DIRECTORY_MESSAGEMODEL = "messagemodel/";
        private static final String DIRECTORY_COMMUNICATION = "communication/";
    }
}
