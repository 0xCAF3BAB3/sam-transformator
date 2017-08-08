package com.jwa.amlmodel.code.generator.generators.impl;

import com.jwa.amlmodel.code.generator.generators.Codegenerator;
import com.jwa.amlmodel.code.generator.generators.CodegeneratorException;
import com.jwa.amlmodel.code.generator.generators.config.GlobalConfig;
import com.jwa.amlmodel.code.generator.generators.config.generated.impl.GeneratedPortConfig;
import com.jwa.amlmodel.code.generator.generators.config.generated.impl.GeneratedPortstyleConfig;
import com.jwa.amlmodel.code.generator.generators.constants.AmlmodelConstants;
import com.jwa.amlmodel.code.generator.generators.utils.CodefileUtils;

import org.cdlflex.models.CAEX.InternalElement;
import org.cdlflex.models.CAEX.util.AmlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public final class PortstyleCodegenerator implements Codegenerator<GeneratedPortConfig, GeneratedPortstyleConfig> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PortstyleCodegenerator.class);

    @Override
    public final GeneratedPortstyleConfig generate(final InternalElement node, final GeneratedPortConfig parentConfig) throws CodegeneratorException {
        if (!AmlmodelConstants.hasPortstyleRole(node)) {
            throw new IllegalArgumentException("Passed node has no role '" + AmlmodelConstants.NAME_ROLE_PORTSTYLE + "'");
        }

        final String portName = node.getName();

        LOGGER.trace("Generating port-style for port-node '" + portName + "' ...");

        final String portstyleStyle = AmlUtil.getAttributeValue(node, AmlmodelConstants.NAME_ATTRIBUTE_PORTSTYLE_STYLE).get();
        try {
            final String portstyleContent = "                        .setStyle(\"" + portstyleStyle + "\")";
            CodefileUtils.addToPortConfig(portstyleContent, portName, parentConfig.getPortsConfig().getComponentCommunicationserviceFile(), GlobalConfig.CHARSET);
        } catch (IOException e) {
            throw new CodegeneratorException("Failed to adapt file '" + parentConfig.getPortsConfig().getComponentCommunicationserviceFile() + "': " + e.getMessage(), e);
        }

        LOGGER.trace("Generating port-style for port-node '" + portName + "' finished");

        return new GeneratedPortstyleConfig();
    }
}
