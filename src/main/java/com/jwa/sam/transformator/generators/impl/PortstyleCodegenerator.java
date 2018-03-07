package com.jwa.sam.transformator.generators.impl;

import com.jwa.sam.transformator.generators.Codegenerator;
import com.jwa.sam.transformator.generators.CodegeneratorException;
import com.jwa.sam.transformator.generators.config.FreemarkerTemplate;
import com.jwa.sam.transformator.generators.config.GlobalConfig;
import com.jwa.sam.transformator.generators.config.generated.impl.GeneratedPortConfig;
import com.jwa.sam.transformator.generators.config.generated.impl.GeneratedPortsConfig;
import com.jwa.sam.transformator.generators.config.generated.impl.GeneratedPortstyleConfig;
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
        addStyleToPortInComponentCommunicationserviceClass(portStyle, portName, portConfig.getPortsConfig());

        LOGGER.trace("Generating port-style for port-node '" + portName + "' finished");

        return new GeneratedPortstyleConfig();
    }

    private static void addStyleToPortInComponentCommunicationserviceClass(final String portStyle, final String portName, final GeneratedPortsConfig portsConfig) throws CodegeneratorException {
        final Path communicationserviceClassFile = portsConfig.getComponentCommunicationserviceClassFile();
        final Template template = GlobalConfig.getTemplate(FreemarkerTemplate.COMMSERVICE_PORTSTYLE_SNIPPET);
        final Map<String, Object> datamodel = new HashMap<>();
        datamodel.put("portStyle", portStyle);
        try {
            final String portstyleStatement = CodefileUtils.processTemplate(template, datamodel, GlobalConfig.getCharset());
            CodefileUtils.insertStatementsToMethod(
                    portstyleStatement,
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
