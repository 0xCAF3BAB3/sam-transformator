package com.jwa.amlmodel.code.generator.generators.impl;

import com.jwa.amlmodel.code.generator.generators.Codegenerator;
import com.jwa.amlmodel.code.generator.generators.CodegeneratorException;
import com.jwa.amlmodel.code.generator.generators.config.GlobalConfig;
import com.jwa.amlmodel.code.generator.generators.config.generated.impl.GeneratedPortConfig;
import com.jwa.amlmodel.code.generator.generators.config.generated.impl.GeneratedPorttypeConfig;
import com.jwa.amlmodel.code.generator.generators.constants.AmlmodelConstants;
import com.jwa.amlmodel.code.generator.generators.utils.CodefileUtils;

import org.cdlflex.models.CAEX.InternalElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;

public final class PorttypeCodegenerator implements Codegenerator<GeneratedPortConfig, GeneratedPorttypeConfig> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PorttypeCodegenerator.class);

    @Override
    public final GeneratedPorttypeConfig generate(final InternalElement node, final GeneratedPortConfig portConfig) throws CodegeneratorException {
        if (!AmlmodelConstants.hasPorttypeRole(node)) {
            throw new IllegalArgumentException("Passed node has no porttype-role");
        }

        final String portName = node.getName();

        LOGGER.trace("Generating port-type for port-node '" + portName + "' ...");

        final String portType = AmlmodelConstants.getPorttype(node);
        addTypeToPortInComponentCommunicationserviceClass(portType, portName, portConfig);

        LOGGER.trace("Generating port-type for port-node '" + portName + "' finished");

        return new GeneratedPorttypeConfig();
    }

    private static void addTypeToPortInComponentCommunicationserviceClass(final String portType, final String portName, final GeneratedPortConfig portConfig) throws CodegeneratorException {
        final Path communicationserviceClassFile = portConfig.getPortsConfig().getComponentCommunicationserviceClassFile();
        try {
            final String porttypeContent = "                        .setType(\"" + portType + "\")";
            CodefileUtils.addToPortConfig(porttypeContent, portName, communicationserviceClassFile, GlobalConfig.getCharset());
            final String enumStatement = CodefileUtils.toValidJavaIdentifier(portName) + "(\"" + portName + "\")";
            if (portType.equals("Receiver")) {
                CodefileUtils.addValueToEnum(enumStatement, "Receivers", communicationserviceClassFile, GlobalConfig.getCharset());
            } else if (portType.startsWith("Sender/")) {
                CodefileUtils.addValueToEnum(enumStatement, "Senders", communicationserviceClassFile, GlobalConfig.getCharset());
                if (portType.equals("Sender/SynchronousSender")) {
                    CodefileUtils.addValueToEnum(enumStatement, "SynchronousSenders", communicationserviceClassFile, GlobalConfig.getCharset());
                } else if (portType.equals("Sender/AsynchronousSender")) {
                    CodefileUtils.addValueToEnum(enumStatement, "AsynchronousSenders", communicationserviceClassFile, GlobalConfig.getCharset());
                }
            }
        } catch (IOException e) {
            throw new CodegeneratorException("Failed to adapt file '" + communicationserviceClassFile + "': " + e.getMessage(), e);
        }
    }
}
