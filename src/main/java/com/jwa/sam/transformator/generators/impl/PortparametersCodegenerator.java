package com.jwa.sam.transformator.generators.impl;

import com.jwa.sam.transformator.generators.Codegenerator;
import com.jwa.sam.transformator.generators.CodegeneratorException;
import com.jwa.sam.transformator.generators.config.FreemarkerTemplate;
import com.jwa.sam.transformator.generators.config.GlobalConfig;
import com.jwa.sam.transformator.generators.config.generated.impl.GeneratedPortConfig;
import com.jwa.sam.transformator.generators.config.generated.impl.GeneratedPortparametersConfig;
import com.jwa.sam.transformator.generators.config.generated.impl.GeneratedPortsConfig;
import com.jwa.sam.transformator.generators.constants.AmlmodelConstants;
import com.jwa.sam.transformator.generators.utils.CodefileUtils;

import freemarker.template.Template;

import org.cdlflex.models.CAEX.InternalElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public final class PortparametersCodegenerator implements Codegenerator<GeneratedPortConfig, GeneratedPortparametersConfig> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PortparametersCodegenerator.class);

    @Override
    public final GeneratedPortparametersConfig generate(final InternalElement node, final GeneratedPortConfig portConfig) throws CodegeneratorException {
        if (!AmlmodelConstants.hasPortparametersRole(node)) {
            throw new IllegalArgumentException("Passed node has no portparameters-role");
        }

        final String portName = node.getName();

        LOGGER.trace("Generating port-parameters for port-node '" + portName + "' ...");

        final Map<String, String> portParameters = AmlmodelConstants.getPortparameters(node);
        addParametersToPortInComponentCommunicationserviceClass(portParameters, portName, portConfig.getPortsConfig());

        LOGGER.trace("Generating port-parameters for port-node '" + portName + "' finished");

        return new GeneratedPortparametersConfig();
    }

    private static void addParametersToPortInComponentCommunicationserviceClass(final Map<String, String> portParameters, final String portName, final GeneratedPortsConfig portsConfig) throws CodegeneratorException {
        if (portParameters.isEmpty()) {
            LOGGER.trace("No port-parameters found and set on port-node");
            return;
        }
        final Path communicationserviceClassFile = portsConfig.getComponentCommunicationserviceClassFile();
        final Template template = GlobalConfig.getTemplate(FreemarkerTemplate.COMMSERVICE_PORTPARAMETERS_SNIPPET);
        final Map<String, Object> datamodel = new HashMap<>();
        datamodel.put("portParameters", portParameters);
        try {
            String portparametersStatements = CodefileUtils.processTemplate(template, datamodel, GlobalConfig.getCharset());
            portparametersStatements = CodefileUtils.rtrim(portparametersStatements);
            CodefileUtils.insertStatementsToMethod(
                    portparametersStatements,
                    "init",
                    "\"" + portName + "\"",
                    ".build()",
                    communicationserviceClassFile,
                    GlobalConfig.getCharset()
            );
        } catch (IOException e) {
            throw new CodegeneratorException("Adapting file '" + communicationserviceClassFile + "' failed: " + e.getMessage(), e);
        }
    }
}
