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

public final class PortsCodegenerator implements Codegenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(PortsCodegenerator.class);

    @Override
    public void generate(final InternalElement node, final CodegeneratorConfig codeGeneratorConfig) throws CodegeneratorException {
        LOGGER.trace("Generating ports '" + node.getName() + "' ...");

        // TODO: ...

        // TODO: generate communicationService and store path to it
        final File communicationServiceFile = new File("code-output/ports/CommunicationService.java");
        communicationServiceFile.getParentFile().mkdirs();

        for(InternalElement internalElement : node.getInternalElement()) {
            // TODO: for every port
            boolean isPort = AmlUtil.hasRole(internalElement, AmlmodelConstants.NAME_ROLE_PORT);
            if (isPort) {
                new PortCodegenerator(communicationServiceFile).generate(internalElement, codeGeneratorConfig);
            }
        }

        LOGGER.trace("Generating ports '" + node.getName() + "' finished");
    }
}
