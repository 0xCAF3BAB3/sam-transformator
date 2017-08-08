package com.jwa.amlmodel.code.generator.generators.config;

public enum FileTemplate {
    POM_COMPONENT(Constants.DIRECTORY_COMPONENT + "pom.xml"),
    POM_SERVICE(Constants.DIRECTORY_SERVICE + "pom.xml");

    private final String filepath;

    FileTemplate(final String filepath) {
        this.filepath = filepath;
    }

    public final String getFilepath() {
        return filepath;
    }

    private static final class Constants {
        private static final String DIRECTORY_COMPONENT = "component/";
        private static final String DIRECTORY_SERVICE = "service/";
    }
}
