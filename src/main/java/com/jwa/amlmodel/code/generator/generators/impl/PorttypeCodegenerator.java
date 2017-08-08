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
    public final GeneratedPorttypeConfig generate(final InternalElement node, final GeneratedPortConfig parentConfig) throws CodegeneratorException {
        if (!AmlmodelConstants.hasPorttypeRole(node)) {
            throw new IllegalArgumentException("Passed node has no porttype-role");
        }

        final String portName = node.getName();

        LOGGER.trace("Generating port-type for port-node '" + portName + "' ...");

        final Path commServiceFile = parentConfig.getPortsConfig().getComponentCommunicationserviceFile();

        final String porttype = AmlmodelConstants.getPorttype(node);
        try {
            final String porttypeContent = "                        .setType(\"" + porttype + "\")";
            CodefileUtils.addToPortConfig(porttypeContent, portName, commServiceFile, GlobalConfig.CHARSET);
        } catch (IOException e) {
            throw new CodegeneratorException("Failed to adapt file '" + commServiceFile + "': " + e.getMessage(), e);
        }

        try {
            final String enumValue = CodefileUtils.toValidJavaIdentifier(portName);
            if (porttype.equals("Receiver")) {
                CodefileUtils.addValueToEnum(enumValue + "(\"" + portName + "\")", "Receivers", commServiceFile);
            } else if (porttype.startsWith("Sender/")) {
                CodefileUtils.addValueToEnum(enumValue + "(\"" + portName + "\")", "Senders", commServiceFile);
                if (porttype.equals("Sender/SynchronousSender")) {
                    CodefileUtils.addValueToEnum(enumValue + "(\"" + portName + "\")", "SynchronousSenders", commServiceFile);
                } else if (porttype.equals("Sender/AsynchronousSender")) {
                    CodefileUtils.addValueToEnum(enumValue + "(\"" + portName + "\")", "AsynchronousSenders", commServiceFile);
                }
            }
        } catch (IOException e) {
            throw new CodegeneratorException("Failed to adapt file '" + commServiceFile + "': " + e.getMessage(), e);
        }

        LOGGER.trace("Generating port-type for port-node '" + portName + "' finished");

        return new GeneratedPorttypeConfig();
    }
}
