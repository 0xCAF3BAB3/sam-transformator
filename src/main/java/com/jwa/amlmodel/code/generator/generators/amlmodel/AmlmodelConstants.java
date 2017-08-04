package com.jwa.amlmodel.code.generator.generators.amlmodel;

public final class AmlmodelConstants {
    // ArchitectureRoleClassLib
    private static final String NAME_ROLECLASSLIB_ARCHITECTURE = "ArchitectureRoleClassLib";
    public static final String NAME_ROLE_COMPONENT = NAME_ROLECLASSLIB_ARCHITECTURE + "/Component";
    public static final String NAME_ROLE_SERVICE = NAME_ROLECLASSLIB_ARCHITECTURE + "/Service";

    // DomainModelRoleClassLib
    private static final String NAME_ROLECLASSLIB_DOMAINMODEL = "DomainModelRoleClassLib";
    public static final String NAME_ROLE_MESSAGEMODEL = NAME_ROLECLASSLIB_DOMAINMODEL + "/MessageModel";

    // CommunicationRoleClassLib
    private static final String NAME_ROLECLASSLIB_COMMUNICATION = "CommunicationRoleClassLib";
    public static final String NAME_ROLE_PORTS = NAME_ROLECLASSLIB_COMMUNICATION + "/Ports";
    public static final String NAME_ROLE_PORT = NAME_ROLECLASSLIB_COMMUNICATION + "/Port";
    public static final String NAME_ROLE_PORTTYPE = NAME_ROLECLASSLIB_COMMUNICATION + "/PortType";
    public static final String NAME_ROLE_PORTSTYLE = NAME_ROLECLASSLIB_COMMUNICATION + "/PortStyle";
    public static final String NAME_ROLE_PORTPARAMETERS = NAME_ROLECLASSLIB_COMMUNICATION + "/PortParameters";

    private AmlmodelConstants() {}
}
