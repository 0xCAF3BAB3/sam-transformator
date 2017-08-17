package com.jwa.amlmodel.code.generator.generators.impl;

import com.jwa.amlmodel.code.generator.generators.Codegenerator;
import com.jwa.amlmodel.code.generator.generators.CodegeneratorException;
import com.jwa.amlmodel.code.generator.generators.config.FreemarkerTemplate;
import com.jwa.amlmodel.code.generator.generators.config.GlobalConfig;
import com.jwa.amlmodel.code.generator.generators.config.generated.impl.GeneratedPortConfig;
import com.jwa.amlmodel.code.generator.generators.config.generated.impl.GeneratedPortsConfig;
import com.jwa.amlmodel.code.generator.generators.config.generated.impl.GeneratedPorttypeConfig;
import com.jwa.amlmodel.code.generator.generators.constants.AmlmodelConstants;
import com.jwa.amlmodel.code.generator.generators.utils.CodefileUtils;

import freemarker.template.Template;

import org.cdlflex.models.CAEX.InternalElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        addTypeToPortInComponentCommunicationserviceClass(portType, portName, portConfig.getPortsConfig());

        LOGGER.trace("Generating port-type for port-node '" + portName + "' finished");

        return new GeneratedPorttypeConfig();
    }

    private static void addTypeToPortInComponentCommunicationserviceClass(final String portType, final String portName, final GeneratedPortsConfig portsConfig) throws CodegeneratorException {
        final Path communicationserviceClassFile = portsConfig.getComponentCommunicationserviceClassFile();
        try {
            final Template template = GlobalConfig.getTemplate(FreemarkerTemplate.COMMSERVICE_PORTTYPE_SNIPPET);
            final Map<String, Object> datamodel = new HashMap<>();
            datamodel.put("portType", portType);
            final String porttypeContent = CodefileUtils.processTemplate(template, datamodel, GlobalConfig.getCharset());
            CodefileUtils.addToPortConfig(porttypeContent, portName, communicationserviceClassFile, GlobalConfig.getCharset());

            final List<String> enumNames = new ArrayList<>();
            if (portType.equals("Receiver")) {
                enumNames.add("Receivers");
            } else if (portType.startsWith("Sender/")) {
                enumNames.add("Senders");
                if (portType.equals("Sender/SynchronousSender")) {
                    enumNames.add("SynchronousSenders");
                } else if (portType.equals("Sender/AsynchronousSender")) {
                    enumNames.add("AsynchronousSenders");
                }
            }
            if (!enumNames.isEmpty()) {
                final String enumStatement = CodefileUtils.toValidJavaIdentifier(portName) + "(\"" + portName + "\")";
                for(String enumName : enumNames) {
                    CodefileUtils.addValueToEnum(enumStatement, enumName, communicationserviceClassFile, GlobalConfig.getCharset());
                }
            }
        } catch (IOException e) {
            throw new CodegeneratorException("Adapting file '" + communicationserviceClassFile + "' failed: " + e.getMessage(), e);
        }
    }
}
