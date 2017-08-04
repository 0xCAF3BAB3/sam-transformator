package com.jwa.amlmodel.code.generator.generators.impl;

import com.jwa.amlmodel.code.generator.generators.Codegenerator;
import com.jwa.amlmodel.code.generator.generators.amlmodel.AmlmodelConstants;
import com.jwa.amlmodel.code.generator.generators.amlmodel.AmlmodelUtils;
import com.jwa.amlmodel.code.generator.generators.config.CodegeneratorConfig;

import org.cdlflex.models.CAEX.InternalElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MessagemodelCodegenerator implements Codegenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessagemodelCodegenerator.class);

    @Override
    public void generate(final InternalElement node, final CodegeneratorConfig codeGeneratorConfig) {
        String messagemodelName = AmlmodelUtils.getRoleStartingWith(node, AmlmodelConstants.NAME_ROLE_MESSAGEMODEL + "/");

        LOGGER.trace("Generating message-model '" + messagemodelName + "' ...");

        // TODO: if not existing already: generate Maven module 'messagemodel'

        // TODO: if not existing already: generate messagemodel subclass (= passed messagemodelName)

        LOGGER.trace("Generating message-model '" + messagemodelName + "' finished");
    }
}
