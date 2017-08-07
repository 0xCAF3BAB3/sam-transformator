package com.jwa.amlmodel.code.generator.generators.impl;

import com.jwa.amlmodel.code.generator.generators.Codegenerator;
import com.jwa.amlmodel.code.generator.generators.CodegeneratorException;
import com.jwa.amlmodel.code.generator.generators.config.GlobalConfig;
import com.jwa.amlmodel.code.generator.generators.config.generated.impl.GeneratedPortConfig;
import com.jwa.amlmodel.code.generator.generators.config.generated.impl.GeneratedPortsConfig;

import org.cdlflex.models.CAEX.InternalElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PortCodegenerator implements Codegenerator<GeneratedPortsConfig, GeneratedPortConfig> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PortCodegenerator.class);

    @Override
    public final GeneratedPortConfig generate(final InternalElement node, final GeneratedPortsConfig parentConfig, final GlobalConfig globalConfig) throws CodegeneratorException {
        LOGGER.trace("Generating port for node '" + node.getName() + "' ...");

        /*
        // TODO: verify node is valid port
        if (!AmlmodelUtils.hasRoleStartingWith(node, AmlmodelConstants.NAME_ROLE_PORTTYPE + "/")) {
            throw new CodegeneratorValidationException("...");
        }

        // TODO: extract port info
        final String type = AmlmodelUtils.getRoleStartingWith(node, AmlmodelConstants.NAME_ROLE_PORTTYPE + "/");

        // TODO: write to communicationServiceFile
        final String enumValue = CodeUtils.toValidJavaIdentifier(node.getName());
        // a) to Enums
        try {
            if (type.startsWith("Receiver")) {
                CodeUtils.addValueToEnum(enumValue, "Receivers", communicationServiceFile);
            } else if (type.startsWith("Sender")) {
                CodeUtils.addValueToEnum(enumValue, "Senders", communicationServiceFile);
                if (type.endsWith("SynchronousSender")) {
                    CodeUtils.addValueToEnum(enumValue, "SynchronousSenders", communicationServiceFile);
                } else if (type.endsWith("AsynchronousSender")) {
                    CodeUtils.addValueToEnum(enumValue, "AsynchronousSenders", communicationServiceFile);
                }
            }
        } catch (IOException e) {
            throw new CodegeneratorException(e.getMessage(), e);
        }

        // TODO: b) to constructor: set factory (if nor aleady added) ... also handle no existance --> should this not be done in ports or component?
        // TODO: c) to init() method

        boolean isMessagemodelAssigned = AmlmodelUtils.hasRoleStartingWith(node, AmlmodelConstants.NAME_ROLE_MESSAGEMODEL + "/");
        if (isMessagemodelAssigned) {
            final Path messageModelOutput = Paths.get("code-output/messagemodel/src/");
            final String packageName = "com.jwa.pushlistener.code.architecture.messagemodel";
            new MessagemodelCodegenerator(messageModelOutput, packageName).generate(node, codeGeneratorConfig);
        }
        */

        LOGGER.trace("Generating port for node '" + node.getName() + "' finished");

        return new GeneratedPortConfig();
    }
}
