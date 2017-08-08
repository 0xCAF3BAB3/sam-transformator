package com.jwa.amlmodel.code.generator.generators.impl;

import com.jwa.amlmodel.code.generator.generators.Codegenerator;
import com.jwa.amlmodel.code.generator.generators.CodegeneratorException;
import com.jwa.amlmodel.code.generator.generators.config.GlobalConfig;
import com.jwa.amlmodel.code.generator.generators.config.generated.impl.GeneratedPortConfig;
import com.jwa.amlmodel.code.generator.generators.config.generated.impl.GeneratedPortsConfig;
import com.jwa.amlmodel.code.generator.generators.constants.FreemarkerTemplatesConstants;
import com.jwa.amlmodel.code.generator.generators.utils.CodefileUtils;

import freemarker.template.Template;
import freemarker.template.TemplateException;

import org.cdlflex.models.CAEX.InternalElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public final class PortCodegenerator implements Codegenerator<GeneratedPortsConfig, GeneratedPortConfig> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PortCodegenerator.class);

    @Override
    public final GeneratedPortConfig generate(final InternalElement node, final GeneratedPortsConfig parentConfig) throws CodegeneratorException {
        final String portName = node.getName();

        LOGGER.trace("Generating port for port-node '" + portName + "' ...");

        // CommunicationService.java anpassen  Port zu Methode ‚init’ hinzufügen, Parameter ‚portName’ = IE-Name
        final String portsnippet;
        final Map<String, String> portsnippetDatamodel = new HashMap<>();
        portsnippetDatamodel.put("portName", portName);
        try {
            final Template template = GlobalConfig.getTemplate(FreemarkerTemplatesConstants.COMMSERVICE_PORT_SNIPPET);
            try (final Writer writer = new StringWriter()) {
                template.process(portsnippetDatamodel, writer);
                portsnippet = writer.toString();
            }
        } catch (IOException | TemplateException e) {
            throw new CodegeneratorException("Failed to generate snippet '" + "component/CommunicationservicePortsnippet" + "': " + e.getMessage(), e);
        }
        try {
            CodefileUtils.addToMethod(portsnippet, "public final void init() throws PortsServiceException {", parentConfig.getComponentCommunicationserviceFile(), GlobalConfig.CHARSET);
        } catch (IOException e) {
            throw new CodegeneratorException("Failed to adapt file '" + parentConfig.getComponentCommunicationserviceFile() + "': " + e.getMessage(), e);
        }

        // add import-statement: port.config.PortConfigBuilder
        final String importStatement = parentConfig.getCommunicationPackageName() + ".port.config.PortConfigBuilder";
        try {
            CodefileUtils.addImport(importStatement, parentConfig.getComponentCommunicationserviceFile(), GlobalConfig.CHARSET);
        } catch (IOException e) {
            throw new CodegeneratorException("Failed to adapt file '" + parentConfig.getComponentCommunicationserviceFile() + "': " + e.getMessage(), e);
        }

        LOGGER.trace("Generating port for port-node '" + portName + "' finished");

        return new GeneratedPortConfig(parentConfig);
    }
}
