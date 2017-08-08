package com.jwa.amlmodel.code.generator.generators.impl;

import com.jwa.amlmodel.code.generator.generators.Codegenerator;
import com.jwa.amlmodel.code.generator.generators.CodegeneratorException;
import com.jwa.amlmodel.code.generator.generators.config.generated.impl.GeneratedMessagemodelConfig;
import com.jwa.amlmodel.code.generator.generators.config.generated.impl.GeneratedPortConfig;

import org.cdlflex.models.CAEX.InternalElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MessagemodelCodegenerator implements Codegenerator<GeneratedPortConfig, GeneratedMessagemodelConfig> {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessagemodelCodegenerator.class);

    @Override
    public final GeneratedMessagemodelConfig generate(final InternalElement node, final GeneratedPortConfig parentConfig) throws CodegeneratorException {
        final String portName = node.getName();

        LOGGER.trace("Generating message-model for port-node '" + portName + "' ...");

        // TODO: implement me
        // String messagemodelName = AmlmodelUtils.getRoleStartingWith(node, AmlmodelConstants.NAME_ROLE_MESSAGEMODEL + "/");

        LOGGER.trace("Generating message-model for port-node '" + portName + "' finished");

        return new GeneratedMessagemodelConfig();
    }
}
