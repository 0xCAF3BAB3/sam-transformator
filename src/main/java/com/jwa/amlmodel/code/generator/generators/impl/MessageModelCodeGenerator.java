package com.jwa.amlmodel.code.generator.generators.impl;

import com.jwa.amlmodel.code.generator.generators.CodeGenerator;

import freemarker.template.Configuration;

import org.cdlflex.models.CAEX.InternalElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MessageModelCodeGenerator implements CodeGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageModelCodeGenerator.class);
    private final Configuration freemarkerConfig;

    public MessageModelCodeGenerator(final Configuration freemarkerConfig) {
        this.freemarkerConfig = freemarkerConfig;
    }

    @Override
    public void generate(final InternalElement node) {
        LOGGER.trace("Generating message-model '" + node.getName() + "' ...");

        // TODO: generates Maven module 'messagemodel' based on the passed data

        LOGGER.trace("Generating message-model '" + node.getName() + "' finished");
    }
}
