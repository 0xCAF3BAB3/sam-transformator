package com.jwa.amlmodel.code.generator.generators.constants;

public final class AmlmodelConstants {
    // ArchitectureRoleClassLib
    private static final String NAME_ROLECLASSLIB_ARCHITECTURE = "ArchitectureRoleClassLib";
    public static final String NAME_ROLE_COMPONENT = NAME_ROLECLASSLIB_ARCHITECTURE + "/Component";
    public static final String NAME_ATTRIBUTE_COMPONENT_ARTIFACTID = "Component.artifactId";
    public static final String NAME_ATTRIBUTE_COMPONENT_GROUPID = "Component.groupId";
    public static final String NAME_ROLE_SERVICE = NAME_ROLECLASSLIB_ARCHITECTURE + "/Service";
    public static final String NAME_ATTRIBUTE_SERVICE_ARTIFACTID = "Service.artifactId";
    public static final String NAME_ATTRIBUTE_SERVICE_GROUPID = "Service.groupId";

    // DomainModelRoleClassLib
    private static final String NAME_ROLECLASSLIB_DOMAINMODEL = "DomainModelRoleClassLib";
    public static final String NAME_ROLE_MESSAGEMODEL = NAME_ROLECLASSLIB_DOMAINMODEL + "/MessageModel";

    // CommunicationRoleClassLib
    private static final String NAME_ROLECLASSLIB_COMMUNICATION = "CommunicationRoleClassLib";
    public static final String NAME_ROLE_PORTS = NAME_ROLECLASSLIB_COMMUNICATION + "/Ports";
    public static final String NAME_ROLE_PORT = NAME_ROLECLASSLIB_COMMUNICATION + "/Port";
    public static final String NAME_ROLE_PORTSTYLE = NAME_ROLECLASSLIB_COMMUNICATION + "/PortStyle";
    public static final String NAME_ATTRIBUTE_PORTSTYLE_STYLE = "PortStyle.style";
    public static final String NAME_ROLE_PORTPARAMETERS = NAME_ROLECLASSLIB_COMMUNICATION + "/PortParameters";
    public static final String NAME_ROLE_PORTTYPE = NAME_ROLECLASSLIB_COMMUNICATION + "/PortType";

    private AmlmodelConstants() {}
}
