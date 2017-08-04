package com.jwa.amlmodel.code.generator.generators.impl;

import com.jwa.amlmodel.code.generator.generators.CodeGenerator;
import com.jwa.amlmodel.code.generator.generators.amlmodel.AmlmodelConstants;
import com.jwa.amlmodel.code.generator.generators.config.CodeGeneratorConfig;

import org.cdlflex.models.CAEX.InternalElement;
import org.cdlflex.models.CAEX.util.AmlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public final class PortsCodeGenerator implements CodeGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(PortsCodeGenerator.class);

    @Override
    public void generate(final InternalElement node, final CodeGeneratorConfig codeGeneratorConfig) {
        LOGGER.trace("Generating ports '" + node.getName() + "' ...");

        // TODO: ...

        // TODO: generate communicationService and store path to it
        final File communicationServiceFile = null;

        for(InternalElement internalElement : node.getInternalElement()) {
            // TODO: for every port
            boolean isPort = AmlUtil.hasRole(internalElement, AmlmodelConstants.NAME_ROLE_PORT);
            if (isPort) {
                new PortCodeGenerator(communicationServiceFile).generate(internalElement, codeGeneratorConfig);
            }
        }

        LOGGER.trace("Generating ports '" + node.getName() + "' finished");
    }
}
