package com.jwa.amlmodel.code.generator.generators.impl;

import com.jwa.amlmodel.code.generator.generators.Codegenerator;
import com.jwa.amlmodel.code.generator.generators.CodegeneratorException;
import com.jwa.amlmodel.code.generator.generators.amlmodel.AmlmodelConstants;
import com.jwa.amlmodel.code.generator.generators.config.CodegeneratorConfig;

import org.cdlflex.models.CAEX.InternalElement;
import org.cdlflex.models.CAEX.util.AmlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public final class ServiceCodegenerator implements Codegenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceCodegenerator.class);

    @Override
    public void generate(final InternalElement node, final CodegeneratorConfig codeGeneratorConfig) throws CodegeneratorException {
        LOGGER.trace("Generating service '" + node.getName() + "' ...");

        // TODO: generates Maven project

        // TODO: generate Maven module 'messagemodel'
        final File file = new File("code-output/messagemodel/src/");
        file.mkdirs();

        for(InternalElement internalElement : node.getInternalElement()) {
            // TODO: for every component
            boolean isComponent = AmlUtil.hasRole(internalElement, AmlmodelConstants.NAME_ROLE_COMPONENT);
            if (isComponent) {
                new ComponentCodegenerator().generate(internalElement, codeGeneratorConfig);
            }
        }

        // TODO: generates Maven module 'communication' which holds communication components (no data needed, just copy)

        LOGGER.trace("Generating service '" + node.getName() + "' finished");
    }
}
