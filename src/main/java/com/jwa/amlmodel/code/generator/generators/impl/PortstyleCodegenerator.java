package com.jwa.amlmodel.code.generator.generators.impl;

import com.jwa.amlmodel.code.generator.generators.Codegenerator;
import com.jwa.amlmodel.code.generator.generators.CodegeneratorException;
import com.jwa.amlmodel.code.generator.generators.config.GlobalConfig;
import com.jwa.amlmodel.code.generator.generators.config.generated.impl.GeneratedPortConfig;
import com.jwa.amlmodel.code.generator.generators.config.generated.impl.GeneratedPortstyleConfig;
import com.jwa.amlmodel.code.generator.generators.constants.AmlmodelConstants;
import com.jwa.amlmodel.code.generator.generators.utils.CodefileUtils;

import org.cdlflex.models.CAEX.InternalElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;

public final class PortstyleCodegenerator implements Codegenerator<GeneratedPortConfig, GeneratedPortstyleConfig> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PortstyleCodegenerator.class);

    @Override
    public final GeneratedPortstyleConfig generate(final InternalElement node, final GeneratedPortConfig portConfig) throws CodegeneratorException {
        if (!AmlmodelConstants.hasPortstyleRole(node)) {
            throw new IllegalArgumentException("Passed node has no portstyle-role");
        }

        final String portName = node.getName();

        LOGGER.trace("Generating port-style for port-node '" + portName + "' ...");

        final String portStyle = AmlmodelConstants.getPortstyleStyle(node);
        addStyleToPortInComponentCommunicationserviceClass(portStyle, portName, portConfig);

        LOGGER.trace("Generating port-style for port-node '" + portName + "' finished");

        return new GeneratedPortstyleConfig();
    }

    private static void addStyleToPortInComponentCommunicationserviceClass(final String portStyle, final String portName, final GeneratedPortConfig portConfig) throws CodegeneratorException {
        final Path communicationserviceClassFile = portConfig.getPortsConfig().getComponentCommunicationserviceClassFile();
        try {
            final String portstyleContent = "                        .setStyle(\"" + portStyle + "\")";
            CodefileUtils.addToPortConfig(portstyleContent, portName, communicationserviceClassFile, GlobalConfig.getCharset());
        } catch (IOException e) {
            throw new CodegeneratorException("Failed to adapt file '" + communicationserviceClassFile + "': " + e.getMessage(), e);
        }
    }
}
