package com.jwa.amlmodel.code.generator.generators.impl;

import com.jwa.amlmodel.code.generator.generators.CodeGenerator;
import com.jwa.amlmodel.code.generator.generators.amlmodel.AmlmodelConstants;
import com.jwa.amlmodel.code.generator.generators.amlmodel.AmlmodelUtils;
import com.jwa.amlmodel.code.generator.generators.config.CodeGeneratorConfig;

import org.cdlflex.models.CAEX.InternalElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MessagemodelCodeGenerator implements CodeGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessagemodelCodeGenerator.class);

    @Override
    public void generate(final InternalElement node, final CodeGeneratorConfig codeGeneratorConfig) {
        String messagemodelName = AmlmodelUtils.getRoleStartingWith(node, AmlmodelConstants.NAME_ROLE_MESSAGEMODEL + "/");

        LOGGER.trace("Generating message-model '" + messagemodelName + "' ...");

        // TODO: if not existing already: generate Maven module 'messagemodel'

        // TODO: if not existing already: generate messagemodel subclass (= passed messagemodelName)

        LOGGER.trace("Generating message-model '" + messagemodelName + "' finished");
    }
}
