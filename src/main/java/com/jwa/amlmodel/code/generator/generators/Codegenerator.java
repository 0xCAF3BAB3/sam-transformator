package com.jwa.amlmodel.code.generator.generators;

import com.jwa.amlmodel.code.generator.generators.config.GlobalConfig;
import com.jwa.amlmodel.code.generator.generators.config.generated.GeneratedConfig;

import org.cdlflex.models.CAEX.InternalElement;

public interface Codegenerator <T extends GeneratedConfig, U extends GeneratedConfig> {
    T generate(final InternalElement node, final U parentConfig, final GlobalConfig globalConfig) throws CodegeneratorException;
}
