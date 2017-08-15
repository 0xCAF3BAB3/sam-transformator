package com.jwa.amlmodel.code.generator.generators.impl;

import com.jwa.amlmodel.code.generator.generators.Codegenerator;
import com.jwa.amlmodel.code.generator.generators.CodegeneratorException;
import com.jwa.amlmodel.code.generator.generators.config.FreemarkerTemplate;
import com.jwa.amlmodel.code.generator.generators.config.GlobalConfig;
import com.jwa.amlmodel.code.generator.generators.config.generated.impl.GeneratedPortConfig;
import com.jwa.amlmodel.code.generator.generators.config.generated.impl.GeneratedPortsConfig;
import com.jwa.amlmodel.code.generator.generators.constants.AmlmodelConstants;
import com.jwa.amlmodel.code.generator.generators.utils.CodefileUtils;

import freemarker.template.Template;

import org.cdlflex.models.CAEX.InternalElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public final class PortCodegenerator implements Codegenerator<GeneratedPortsConfig, GeneratedPortConfig> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PortCodegenerator.class);

    @Override
    public final GeneratedPortConfig generate(final InternalElement node, final GeneratedPortsConfig portsConfig) throws CodegeneratorException {
        if (!AmlmodelConstants.hasPortRole(node)) {
            throw new IllegalArgumentException("Passed node has no port-role");
        }

        final String portName = node.getName();

        LOGGER.trace("Generating port for port-node '" + portName + "' ...");

        addPortToComponentCommunicationserviceClass(portName, portsConfig);

        LOGGER.trace("Generating port for port-node '" + portName + "' finished");

        return new GeneratedPortConfig(portsConfig);
    }

    private static void addPortToComponentCommunicationserviceClass(final String portName, final GeneratedPortsConfig portsConfig) throws CodegeneratorException {
        final String portContent;
        final Template template = GlobalConfig.getTemplate(FreemarkerTemplate.COMMSERVICE_PORT_SNIPPET);
        final Map<String, String> datamodel = new HashMap<>();
        datamodel.put("portName", portName);
        try {
            portContent = CodefileUtils.processTemplate(template, datamodel, GlobalConfig.getCharset());
        } catch (IOException e) {
            throw new CodegeneratorException("Failed to generate port-content: " + e.getMessage(), e);
        }

        final Path communicationserviceClassFile = portsConfig.getComponentCommunicationserviceClassFile();
        try {
            CodefileUtils.addToMethod(portContent, "init", communicationserviceClassFile, GlobalConfig.getCharset());
            final String communicationMavenModulePackageName = portsConfig.getComponentConfig().getServiceConfig().getCommunicationMavenModuleInfo().getGroupAndArtifactId();
            final String importStatement = communicationMavenModulePackageName + ".port.config.PortConfigBuilder";
            CodefileUtils.addImport(importStatement, communicationserviceClassFile, GlobalConfig.getCharset());
        } catch (IOException e) {
            throw new CodegeneratorException("Failed to adapt file '" + communicationserviceClassFile + "': " + e.getMessage(), e);
        }
    }
}
