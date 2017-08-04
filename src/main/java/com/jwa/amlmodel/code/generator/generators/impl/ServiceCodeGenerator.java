package com.jwa.amlmodel.code.generator.generators.impl;

import com.jwa.amlmodel.code.generator.generators.CodeGenerator;
import com.jwa.amlmodel.code.generator.generators.amlmodel.AmlmodelConstants;
import com.jwa.amlmodel.code.generator.generators.config.CodeGeneratorConfig;

import org.cdlflex.models.CAEX.InternalElement;
import org.cdlflex.models.CAEX.util.AmlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ServiceCodeGenerator implements CodeGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceCodeGenerator.class);

    @Override
    public void generate(final InternalElement node, final CodeGeneratorConfig codeGeneratorConfig) {
        LOGGER.trace("Generating service '" + node.getName() + "' ...");

        // TODO: generates Maven project

        for(InternalElement internalElement : node.getInternalElement()) {
            // TODO: for every component
            boolean isComponent = AmlUtil.hasRole(internalElement, AmlmodelConstants.NAME_ROLE_COMPONENT);
            if (isComponent) {
                new ComponentCodeGenerator().generate(internalElement, codeGeneratorConfig);
            }
        }

        // TODO: generates Maven module 'communication' which holds communication components (no data needed, just copy)

        LOGGER.trace("Generating service '" + node.getName() + "' finished");
    }
}
