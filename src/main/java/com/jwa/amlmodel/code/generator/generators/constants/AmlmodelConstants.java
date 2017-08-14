package com.jwa.amlmodel.code.generator.generators.constants;

import com.jwa.amlmodel.code.generator.generators.utils.AmlmodelUtils;

import org.cdlflex.models.CAEX.Attribute;
import org.cdlflex.models.CAEX.InternalElement;
import org.cdlflex.models.CAEX.util.AmlUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class AmlmodelConstants {
    // ArchitectureRoleClassLib
    private static final String NAME_ROLECLASSLIB_ARCHITECTURE = "ArchitectureRoleClassLib";
    private static final String NAME_ROLE_COMPONENT = "Component";
    private static final String NAME_ATTRIBUTE_COMPONENT_ARTIFACTID = "artifactId";
    private static final String NAME_ATTRIBUTE_COMPONENT_GROUPID = "groupId";
    private static final String NAME_ROLE_SERVICE = "Service";
    private static final String NAME_ATTRIBUTE_SERVICE_ARTIFACTID = "artifactId";
    private static final String NAME_ATTRIBUTE_SERVICE_GROUPID = "groupId";

    // DomainModelRoleClassLib
    private static final String NAME_ROLECLASSLIB_DOMAINMODEL = "DomainModelRoleClassLib";
    private static final String NAME_ROLE_MESSAGEMODEL = "MessageModel";
    private static final String NAME_ATTRIBUTE_MESSAGEMODEL_NAME = "name";

    // CommunicationRoleClassLib
    private static final String NAME_ROLECLASSLIB_COMMUNICATION = "CommunicationRoleClassLib";
    private static final String NAME_ROLE_PORTS = "Ports";
    private static final String NAME_ROLE_PORT = "Port";
    private static final String NAME_ROLE_PORTSTYLE = "PortStyle";
    private static final String NAME_ATTRIBUTE_PORTSTYLE_STYLE = "style";
    private static final String NAME_ROLE_PORTPARAMETERS = "PortParameters";
    private static final String NAME_ROLE_PORTTYPE = "PortType";

    private AmlmodelConstants() {}

    public static boolean hasServiceRole(final InternalElement node) {
        if (node == null) {
            throw new IllegalArgumentException("Passed node is null");
        }
        return AmlUtil.hasRole(node, NAME_ROLECLASSLIB_ARCHITECTURE + "/" + NAME_ROLE_SERVICE);
    }

    public static boolean hasComponentRole(final InternalElement node) {
        if (node == null) {
            throw new IllegalArgumentException("Passed node is null");
        }
        return AmlUtil.hasRole(node, NAME_ROLECLASSLIB_ARCHITECTURE + "/" + NAME_ROLE_COMPONENT);
    }

    public static boolean hasPortsRole(final InternalElement node) {
        if (node == null) {
            throw new IllegalArgumentException("Passed node is null");
        }
        return AmlUtil.hasRole(node, NAME_ROLECLASSLIB_COMMUNICATION + "/" + NAME_ROLE_PORTS);
    }

    public static boolean hasPortRole(final InternalElement node) {
        if (node == null) {
            throw new IllegalArgumentException("Passed node is null");
        }
        return AmlUtil.hasRole(node, NAME_ROLECLASSLIB_COMMUNICATION + "/" + NAME_ROLE_PORT);
    }

    public static boolean hasPortstyleRole(final InternalElement node) {
        if (node == null) {
            throw new IllegalArgumentException("Passed node is null");
        }
        return AmlUtil.hasRole(node, NAME_ROLECLASSLIB_COMMUNICATION + "/" + NAME_ROLE_PORTSTYLE);
    }

    public static boolean hasPortparametersRole(final InternalElement node) {
        if (node == null) {
            throw new IllegalArgumentException("Passed node is null");
        }
        return AmlUtil.hasRole(node, NAME_ROLECLASSLIB_COMMUNICATION + "/" + NAME_ROLE_PORTPARAMETERS);
    }

    public static boolean hasPorttypeRole(final InternalElement node) {
        if (node == null) {
            throw new IllegalArgumentException("Passed node is null");
        }
        return AmlmodelUtils.hasRoleStartingWith(node, NAME_ROLECLASSLIB_COMMUNICATION + "/" + NAME_ROLE_PORTTYPE + "/");
    }

    public static boolean hasMessagemodelRole(final InternalElement node) {
        if (node == null) {
            throw new IllegalArgumentException("Passed node is null");
        }
        return AmlUtil.hasRole(node, NAME_ROLECLASSLIB_DOMAINMODEL + "/" + NAME_ROLE_MESSAGEMODEL);
    }

    public static String getServiceArtifactId(final InternalElement node) {
        if (!hasServiceRole(node)) {
            throw new IllegalArgumentException("Passed node has no service-role");
        }
        final Optional<String> value = AmlUtil.getAttributeValue(node, NAME_ROLE_SERVICE + "." + NAME_ATTRIBUTE_SERVICE_ARTIFACTID);
        return value.orElseThrow(() -> new IllegalArgumentException("Attribute not found"));
    }

    public static String getServiceGroupId(final InternalElement node) {
        if (!hasServiceRole(node)) {
            throw new IllegalArgumentException("Passed node has no service-role");
        }
        final Optional<String> value = AmlUtil.getAttributeValue(node, NAME_ROLE_SERVICE + "." + NAME_ATTRIBUTE_SERVICE_GROUPID);
        return value.orElseThrow(() -> new IllegalArgumentException("Attribute not found"));
    }

    public static String getComponentArtifactId(final InternalElement node) {
        if (!hasComponentRole(node)) {
            throw new IllegalArgumentException("Passed node has no component-role");
        }
        final Optional<String> value = AmlUtil.getAttributeValue(node, NAME_ROLE_COMPONENT + "." + NAME_ATTRIBUTE_COMPONENT_ARTIFACTID);
        return value.orElseThrow(() -> new IllegalArgumentException("Attribute not found"));
    }

    public static String getComponentGroupId(final InternalElement node) {
        if (!hasComponentRole(node)) {
            throw new IllegalArgumentException("Passed node has no component-role");
        }
        final Optional<String> value = AmlUtil.getAttributeValue(node, NAME_ROLE_COMPONENT + "." + NAME_ATTRIBUTE_COMPONENT_GROUPID);
        return value.orElseThrow(() -> new IllegalArgumentException("Attribute not found"));
    }

    public static String getPortstyleStyle(final InternalElement node) {
        if (!hasPortstyleRole(node)) {
            throw new IllegalArgumentException("Passed node has no portstyle-role");
        }
        final Optional<String> value = AmlUtil.getAttributeValue(node, NAME_ROLE_PORTSTYLE + "." + NAME_ATTRIBUTE_PORTSTYLE_STYLE);
        return value.orElseThrow(() -> new IllegalArgumentException("Attribute not found"));
    }

    public static Map<String, String> getPortparameters(final InternalElement node) {
        if (!hasPortparametersRole(node)) {
            throw new IllegalArgumentException("Passed node has no portparameters-role");
        }
        final Map<String, String> portparameters = new HashMap<>();
        for(Attribute attribute : AmlmodelUtils.getAttributesStartingWith(node, NAME_ROLE_PORTPARAMETERS)) {
            portparameters.put(attribute.getName().replace(NAME_ROLE_PORTPARAMETERS + ".", ""), attribute.getValue());
        }
        return portparameters;
    }

    public static String getPorttype(final InternalElement node) {
        if (node == null) {
            throw new IllegalArgumentException("Passed node is null");
        }
        final String leadingPart = NAME_ROLECLASSLIB_COMMUNICATION + "/" + NAME_ROLE_PORTTYPE + "/";
        final Optional<String> porttype = AmlmodelUtils.getRoleStartingWith(node, leadingPart);
        return porttype.map(ptype -> ptype.replace(leadingPart, "")).orElseThrow(() -> new IllegalArgumentException("Porttype not found"));
    }

    public static String getMessagemodelName(final InternalElement node) {
        if (!hasMessagemodelRole(node)) {
            throw new IllegalArgumentException("Passed node has no messagemodel-role");
        }
        final Optional<String> value = AmlUtil.getAttributeValue(node, NAME_ROLE_MESSAGEMODEL + "." + NAME_ATTRIBUTE_MESSAGEMODEL_NAME);
        return value.orElseThrow(() -> new IllegalArgumentException("Attribute not found"));
    }
}
