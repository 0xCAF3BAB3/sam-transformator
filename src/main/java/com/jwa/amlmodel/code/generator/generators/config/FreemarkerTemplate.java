package com.jwa.amlmodel.code.generator.generators.config;

public enum FreemarkerTemplate {
    COMMSERVICE_INITIAL(Constants.DIRECTORY_COMPONENT + "CommunicationServiceInitial.java.ftlh"),
    COMMSERVICE_PORT_SNIPPET(Constants.DIRECTORY_COMPONENT + "CommunicationServicePortSnippet.java.ftlh"),
    LOG4J2("log4j2.xml.ftlh"),
    MAIN_COMMSERVICEUSAGE_SNIPPET(Constants.DIRECTORY_COMPONENT + "MainCommunicationServiceUsageSnippet.java.ftlh"),
    MAIN_INITIAL(Constants.DIRECTORY_COMPONENT + "MainInitial.java.ftlh"),
    MESSAGE(Constants.DIRECTORY_MESSAGEMODEL + "Message.java.ftlh"),
    MESSAGEMODEL(Constants.DIRECTORY_MESSAGEMODEL + "MessageModel.java.ftlh");

    private final String filepath;

    FreemarkerTemplate(final String filepath) {
        this.filepath = filepath;
    }

    public final String getFilepath() {
        return filepath;
    }

    private static final class Constants {
        private static final String DIRECTORY_COMPONENT = "component/";
        private static final String DIRECTORY_MESSAGEMODEL = "messagemodel/";
    }
}
