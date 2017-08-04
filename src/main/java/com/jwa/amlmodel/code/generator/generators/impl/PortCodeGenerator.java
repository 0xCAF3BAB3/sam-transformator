package com.jwa.amlmodel.code.generator.generators.impl;

import com.jwa.amlmodel.code.generator.generators.CodeGenerator;

import freemarker.template.Configuration;

import org.cdlflex.models.CAEX.InternalElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public final class PortCodeGenerator implements CodeGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(PortCodeGenerator.class);
    private final File communicationServiceFile;
    private final Configuration freemarkerConfig;

    public PortCodeGenerator(final File communicationServiceFile, final Configuration freemarkerConfig) {
        this.communicationServiceFile = communicationServiceFile;
        this.freemarkerConfig = freemarkerConfig;
    }

    @Override
    public final void generate(final InternalElement node) {
        LOGGER.trace("Generating port '" + node.getName() + "' ...");

        // TODO: verify node is valid port

        // TODO: extract port info

        // TODO: open communicationServiceFile

        // TODO: write to communicationServiceFile
        // TODO: a) to Enums
        // TODO: b) to constructor: set factory (if nor aleady added) ... also handle no existance
        // TODO: c) to init() method

        LOGGER.trace("Generating port '" + node.getName() + "' finished");
    }
}
