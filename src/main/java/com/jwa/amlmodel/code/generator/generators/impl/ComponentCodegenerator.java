package com.jwa.amlmodel.code.generator.generators.impl;

import com.jwa.amlmodel.code.generator.generators.Codegenerator;
import com.jwa.amlmodel.code.generator.generators.amlmodel.AmlmodelConstants;
import com.jwa.amlmodel.code.generator.generators.config.CodegeneratorConfig;

import org.cdlflex.models.CAEX.InternalElement;
import org.cdlflex.models.CAEX.util.AmlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ComponentCodegenerator implements Codegenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentCodegenerator.class);

    @Override
    public void generate(final InternalElement node, final CodegeneratorConfig codeGeneratorConfig) {
        LOGGER.trace("Generating component '" + node.getName() + "' ...");

        // TODO: generates Maven component module based on the passed data

        for(InternalElement internalElement : node.getInternalElement()) {
            // TODO: for every ports (improve: only one is allowed)
            boolean isPorts = AmlUtil.hasRole(internalElement, AmlmodelConstants.NAME_ROLE_PORTS);
            if (isPorts) {
                new PortsCodegenerator().generate(internalElement, codeGeneratorConfig);
            }
        }

        LOGGER.trace("Generating component '" + node.getName() + "' finished");
    }
}
