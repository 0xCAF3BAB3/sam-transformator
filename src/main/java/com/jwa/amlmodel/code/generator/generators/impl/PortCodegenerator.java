package com.jwa.amlmodel.code.generator.generators.impl;

import com.jwa.amlmodel.code.generator.generators.CodeUtils;
import com.jwa.amlmodel.code.generator.generators.Codegenerator;
import com.jwa.amlmodel.code.generator.generators.CodegeneratorException;
import com.jwa.amlmodel.code.generator.generators.CodegeneratorValidationException;
import com.jwa.amlmodel.code.generator.generators.amlmodel.AmlmodelConstants;
import com.jwa.amlmodel.code.generator.generators.amlmodel.AmlmodelUtils;
import com.jwa.amlmodel.code.generator.generators.config.CodegeneratorConfig;

import org.cdlflex.models.CAEX.InternalElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class PortCodegenerator implements Codegenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(PortCodegenerator.class);
    private final File communicationServiceFile;

    public PortCodegenerator(final File communicationServiceFile) {
        if (!communicationServiceFile.isFile()) {
            throw new IllegalArgumentException("...");
        }
        this.communicationServiceFile = communicationServiceFile;
    }

    @Override
    public final void generate(final InternalElement node, final CodegeneratorConfig codeGeneratorConfig) throws CodegeneratorException {
        LOGGER.trace("Generating port '" + node.getName() + "' ...");

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

        LOGGER.trace("Generating port '" + node.getName() + "' finished");
    }
}
