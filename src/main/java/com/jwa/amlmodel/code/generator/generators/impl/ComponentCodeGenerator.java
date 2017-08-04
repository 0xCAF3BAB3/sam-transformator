package com.jwa.amlmodel.code.generator.generators.impl;

import com.jwa.amlmodel.code.generator.generators.AmlmodelConstants;
import com.jwa.amlmodel.code.generator.generators.CodeGenerator;

import freemarker.template.Configuration;

import org.cdlflex.models.CAEX.InternalElement;
import org.cdlflex.models.CAEX.util.AmlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ComponentCodeGenerator implements CodeGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentCodeGenerator.class);
    private final Configuration freemarkerConfig;

    public ComponentCodeGenerator(final Configuration freemarkerConfig) {
        this.freemarkerConfig = freemarkerConfig;
    }

    @Override
    public void generate(final InternalElement node) {
        LOGGER.trace("Generating component '" + node.getName() + "' ...");

        // TODO: generates Maven component module based on the passed data

        LOGGER.trace(node.getName());

        for(InternalElement internalElement : node.getInternalElement()) {
            // TODO: for every ports (improve: only one is allowed)
            boolean isPorts = AmlUtil.hasRole(internalElement, AmlmodelConstants.NAME_ROLE_PORTS);
            if (isPorts) {
                new PortsCodeGenerator(freemarkerConfig).generate(internalElement);
            }
        }

        LOGGER.trace("Generating component '" + node.getName() + "' finished");
    }
}
