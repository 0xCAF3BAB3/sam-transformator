package com.jwa.amlmodel.code.generator.generators.impl;

import com.jwa.amlmodel.code.generator.generators.Codegenerator;
import com.jwa.amlmodel.code.generator.generators.amlmodel.AmlmodelConstants;
import com.jwa.amlmodel.code.generator.generators.amlmodel.AmlmodelUtils;
import com.jwa.amlmodel.code.generator.generators.config.CodegeneratorConfig;

import org.cdlflex.models.CAEX.InternalElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public final class PortCodegenerator implements Codegenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(PortCodegenerator.class);
    private final File communicationServiceFile;

    public PortCodegenerator(final File communicationServiceFile) {
        this.communicationServiceFile = communicationServiceFile;
    }

    @Override
    public final void generate(final InternalElement node, final CodegeneratorConfig codeGeneratorConfig) {
        LOGGER.trace("Generating port '" + node.getName() + "' ...");

        // TODO: verify node is valid port

        // TODO: extract port info

        // TODO: open communicationServiceFile

        // TODO: write to communicationServiceFile
        // TODO: a) to Enums
        // TODO: b) to constructor: set factory (if nor aleady added) ... also handle no existance
        // TODO: c) to init() method

        // TODO: generate message-model (if assigned and not already created)
        // TODO: if message-model role assigned
        boolean isMessagemodelAssigned = AmlmodelUtils.hasRoleStartingWith(node, AmlmodelConstants.NAME_ROLE_MESSAGEMODEL + "/");
        if (isMessagemodelAssigned) {
            new MessagemodelCodegenerator().generate(node, codeGeneratorConfig);
        }

        LOGGER.trace("Generating port '" + node.getName() + "' finished");
    }
}
