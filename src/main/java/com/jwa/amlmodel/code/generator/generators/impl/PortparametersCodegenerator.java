package com.jwa.amlmodel.code.generator.generators.impl;

import com.jwa.amlmodel.code.generator.generators.Codegenerator;
import com.jwa.amlmodel.code.generator.generators.CodegeneratorException;
import com.jwa.amlmodel.code.generator.generators.config.GlobalConfig;
import com.jwa.amlmodel.code.generator.generators.config.generated.impl.GeneratedPortConfig;
import com.jwa.amlmodel.code.generator.generators.config.generated.impl.GeneratedPortparametersConfig;
import com.jwa.amlmodel.code.generator.generators.constants.AmlmodelConstants;
import com.jwa.amlmodel.code.generator.generators.utils.CodefileUtils;

import org.cdlflex.models.CAEX.InternalElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

public final class PortparametersCodegenerator implements Codegenerator<GeneratedPortConfig, GeneratedPortparametersConfig> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PortparametersCodegenerator.class);

    @Override
    public final GeneratedPortparametersConfig generate(final InternalElement node, final GeneratedPortConfig parentConfig) throws CodegeneratorException {
        if (!AmlmodelConstants.hasPortparametersRole(node)) {
            throw new IllegalArgumentException("Passed node has no portparameters-role");
        }

        final String portName = node.getName();

        LOGGER.trace("Generating port-parameters for port-node '" + portName + "' ...");

        final Map<String, String> portparameters = AmlmodelConstants.getPortparameters(node);
        if (!portparameters.isEmpty()) {
            try {
                String portparametersContent = "";
                for(Map.Entry<String, String> portparameter : portparameters.entrySet()) {
                    portparametersContent += "                        .setParameter(\"" + portparameter.getKey() + "\", \"" + portparameter.getValue() + "\")" + "\n";
                }
                portparametersContent = portparametersContent.substring(0, portparametersContent.length() - 1);
                CodefileUtils.addToPortConfig(portparametersContent, portName, parentConfig.getPortsConfig().getComponentCommunicationserviceFile(), GlobalConfig.getCharset());
            } catch (IOException e) {
                throw new CodegeneratorException("Failed to adapt file '" + parentConfig.getPortsConfig().getComponentCommunicationserviceFile() + "': " + e.getMessage(), e);
            }
        } else {
            LOGGER.trace("No port-parameters set on port-node");
        }

        LOGGER.trace("Generating port-parameters for port-node '" + portName + "' finished");

        return new GeneratedPortparametersConfig();
    }
}
