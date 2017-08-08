package com.jwa.amlmodel.code.generator.generators.constants;

public enum FreemarkerTemplatesConstants {
    COMMSERVICE(Constants.DIRECTORY_COMPONENT + "CommunicationServiceInitial.java.ftlh"),
    COMMSERVICE_PORT_SNIPPET(Constants.DIRECTORY_COMPONENT + "CommunicationServicePortSnippet.java.ftlh"),
    LOG4J2(Constants.DIRECTORY_COMPONENT + "log4j2.xml.ftlh"),
    MAIN(Constants.DIRECTORY_COMPONENT + "MainInitial.java.ftlh"),
    MAIN_COMMSERVICEUSAGE_SNIPPET(Constants.DIRECTORY_COMPONENT + "MainCommunicationServiceUsageSnippet.java.ftlh")
    ;

    private final String filepath;

    FreemarkerTemplatesConstants(final String filepath) {
        this.filepath = filepath;
    }

    public final String getFilepath() {
        return filepath;
    }

    private static class Constants {
        private static final String DIRECTORY_COMPONENT = "component/";
    }
}
