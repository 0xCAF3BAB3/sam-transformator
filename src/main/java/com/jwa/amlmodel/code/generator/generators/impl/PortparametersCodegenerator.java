package com.jwa.amlmodel.code.generator.generators.impl;

import com.jwa.amlmodel.code.generator.generators.Codegenerator;
import com.jwa.amlmodel.code.generator.generators.CodegeneratorException;
import com.jwa.amlmodel.code.generator.generators.config.GlobalConfig;
import com.jwa.amlmodel.code.generator.generators.config.generated.impl.GeneratedPortConfig;
import com.jwa.amlmodel.code.generator.generators.config.generated.impl.GeneratedPortparametersConfig;

import org.cdlflex.models.CAEX.InternalElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PortparametersCodegenerator implements Codegenerator<GeneratedPortConfig, GeneratedPortparametersConfig> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PortparametersCodegenerator.class);

    @Override
    public final GeneratedPortparametersConfig generate(final InternalElement node, final GeneratedPortConfig parentConfig, final GlobalConfig globalConfig) throws CodegeneratorException {
        final String portName = node.getName();

        LOGGER.trace("Generating port-parameters for port-node '" + portName + "' ...");

        // TODO: implement me

        LOGGER.trace("Generating port-parameters for port-node '" + portName + "' finished");

        return new GeneratedPortparametersConfig();
    }
}
