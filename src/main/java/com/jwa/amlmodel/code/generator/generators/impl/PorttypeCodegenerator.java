package com.jwa.amlmodel.code.generator.generators.impl;

import com.jwa.amlmodel.code.generator.generators.Codegenerator;
import com.jwa.amlmodel.code.generator.generators.CodegeneratorException;
import com.jwa.amlmodel.code.generator.generators.config.generated.impl.GeneratedPortConfig;
import com.jwa.amlmodel.code.generator.generators.config.generated.impl.GeneratedPorttypeConfig;

import org.cdlflex.models.CAEX.InternalElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PorttypeCodegenerator implements Codegenerator<GeneratedPortConfig, GeneratedPorttypeConfig> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PorttypeCodegenerator.class);

    @Override
    public final GeneratedPorttypeConfig generate(final InternalElement node, final GeneratedPortConfig parentConfig) throws CodegeneratorException {
        final String portName = node.getName();

        LOGGER.trace("Generating port-type for port-node '" + portName + "' ...");

        // TODO: implement me

        LOGGER.trace("Generating port-type for port-node '" + portName + "' finished");

        return new GeneratedPorttypeConfig();
    }
}
